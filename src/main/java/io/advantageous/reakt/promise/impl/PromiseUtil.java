/*
 *
 *  Copyright (c) 2016. Rick Hightower, Geoff Chandler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    		http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.advantageous.reakt.promise.impl;

import io.advantageous.reakt.Result;
import io.advantageous.reakt.exception.RejectedPromiseException;
import io.advantageous.reakt.promise.Promise;
import io.advantageous.reakt.promise.PromiseHandler;
import io.advantageous.reakt.promise.Promises;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

public interface PromiseUtil {

    /**
     * Does the all logic for All*PromiseHandler.
     * This promise only fires (comes back) if all of the child promises come back.
     *
     * @param parent        parent
     * @param childPromises promises that all have to come back before this promise comes back
     * @param <T>           type of result
     */
    static <T> void all(Promise<T> parent, Promise<T>[] childPromises) {
        final AtomicInteger count = new AtomicInteger(childPromises.length);
        final AtomicBoolean done = new AtomicBoolean();

        final Consumer<PromiseHandler<T>> consumer = (childPromise) -> {

            if (done.get()) {
                return;
            }

            /** If any promise fails then stop processing. */
            if (childPromise.failure()) {
                if (done.compareAndSet(false, true)) {
                    parent.asHandler().reject(childPromise.cause());
                    count.set(-1);
                }
            } else {
                /** If the count is 0, then we are done. */
                int currentCount = count.decrementAndGet();
                if (currentCount == 0 && !parent.asHandler().complete()) {
                    if (done.compareAndSet(false, true)) {
                        parent.asHandler().onResult(Result.result(null));
                    }
                }
            }
        };
        /** Register the listener. */
        for (Promise<T> childPromise : childPromises) {
            childPromise.asHandler().whenComplete(consumer);
        }
    }

    /**
     * Does the any logic for Any*PromiseHandler.
     * If any child comes back, then the parent comes back.
     *
     * @param parent        parent promise
     * @param childPromises list of promises
     * @param <T>           type of result
     */
    static <T> void any(Promise<T> parent, Promise<T>[] childPromises) {


        final AtomicBoolean done = new AtomicBoolean();
        final Consumer<PromiseHandler<T>> runnable = (childPromise) -> {
            /** If any promise fails then stop processing. */
            if (childPromise.failure()) {
                if (done.compareAndSet(false, true)) {
                    parent.asHandler().reject(childPromise.cause());
                }
            } else {
                /** Only fire if the child promise is the first promise
                 * so the parent does not fire multiple times. */
                if (done.compareAndSet(false, true)) {
                    parent.asHandler().reject(childPromise.cause());
                }
            }

        };
        for (Promise<T> childPromise : childPromises) {
            childPromise.asHandler().whenComplete(runnable);
        }
    }

    static <T, U> PromiseHandler<U> mapPromise(PromiseHandler<T> thisPromise, Function<? super T, ? extends U> mapper) {
        final PromiseHandler<U> mappedPromise = (PromiseHandler<U>) Promises.promise();
        thisPromise.whenComplete(promise -> {
            if (promise.success()) {
                final U mapped = mapper.apply(promise.get());
                mappedPromise.resolve(mapped);
            } else {
                mappedPromise.reject(promise.cause());
            }
        });
        return mappedPromise;
    }


    static <T> T doGet(AtomicReference<Result<T>> result, PromiseHandler<?> promise) {

        if (!promise.complete()) {
            throw new NoSuchElementException("No value present, result not returned.");
        }
        if (promise.failure()) {
            if (promise.cause() instanceof RuntimeException) {
                throw (RuntimeException) promise.cause();
            } else {
                throw new RejectedPromiseException(promise.cause());
            }
        }
        return result.get().get();
    }
}
