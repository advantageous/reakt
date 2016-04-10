package io.advantageous.reakt.promise.impl;

import io.advantageous.reakt.Result;
import io.advantageous.reakt.promise.Promise;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public interface PromiseUtil {

    /**
     * Does the all logic for All*Promise.
     * This promise only fires (comes back) if all of the child promises come back.
     *
     * @param parent        parent
     * @param childPromises promises that all have to come back before this promise comes back
     */
    static <T> void all(Promise<T> parent, Promise<T>[] childPromises) {
        final AtomicInteger count = new AtomicInteger(childPromises.length);
        final Consumer<Promise<T>> consumer = (childPromise) -> {
            /** If any promise fails then stop processing. */
            if (childPromise.failure()) {
                parent.fail(childPromise.cause());
                count.set(0);
            } else {
                /** If the count is 0, then we are done. */
                int currentCount = count.decrementAndGet();
                if (currentCount <= 0) {
                    parent.onResult(Result.result(null));
                }
            }
        };
        /** Register the listener. */
        for (Promise<T> childPromise : childPromises) {
            childPromise.whenComplete(consumer);
        }
    }

    /**
     * Does the any logic for Any*Promise.
     * If any child comes back, then the parent comes back.
     *
     * @param parent        parent promise
     * @param childPromises list of promises
     */
    static <T> void any(Promise<T> parent, Promise<T>[] childPromises) {

        final AtomicBoolean done = new AtomicBoolean();
        final Consumer<Promise<T>> runnable = (childPromise) -> {
            /** If any promise fails then stop processing. */
            if (childPromise.failure()) {
                if (done.compareAndSet(false, true)) {
                    parent.fail(childPromise.cause());
                }
            } else {
                /** Only fire if the child promise is the first promise
                 * so the parent does not fire multiple times. */
                if (done.compareAndSet(false, true)) {
                    parent.fail(childPromise.cause());
                }
            }

        };
        for (Promise<T> childPromise : childPromises) {
            childPromise.whenComplete(runnable);
        }
    }
}
