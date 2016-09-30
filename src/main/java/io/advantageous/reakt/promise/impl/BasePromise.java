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


import io.advantageous.reakt.Expected;
import io.advantageous.reakt.Result;
import io.advantageous.reakt.exception.ThenHandlerException;
import io.advantageous.reakt.promise.PromiseHandler;
import io.advantageous.reakt.reactor.Reactor;

import java.time.Duration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

public class BasePromise<T> implements PromiseHandler<T> {


    protected final AtomicReference<Result<T>> result = new AtomicReference<>();
    protected Expected<Consumer<T>> thenConsumer = Expected.empty();
    protected Expected<Consumer<Expected<T>>> thenExpectedConsumer = Expected.empty();
    protected Expected<Consumer<Throwable>> catchConsumer = Expected.empty();
    protected Expected<List<Consumer<PromiseHandler<T>>>> completeListeners = Expected.empty();
    private boolean safe;

    public static <T> PromiseHandler<T> provideFinalPromise(PromiseHandler<T> promise) {
        if (promise instanceof BasePromise) {
            BasePromise<T> basePromise = ((BasePromise<T>) promise);
            return new FinalPromise<>(basePromise.thenConsumer,
                    basePromise.catchConsumer,
                    basePromise.thenExpectedConsumer,
                    basePromise.completeListeners, true);
        } else {
            throw new IllegalStateException("Operation not supported use FinalPromise directly");
        }
    }

    @Override
    public PromiseHandler<T> thenSafeExpect(Consumer<Expected<T>> consumer) {
        safe = true;
        thenExpectedConsumer = Expected.of(consumer);
        return this;
    }

    @Override
    public PromiseHandler<T> thenSafe(Consumer<T> consumer) {
        safe = true;
        thenConsumer = Expected.of(consumer);
        return this;
    }

    @Override
    public boolean supportsSafe() {
        return true;
    }

    public synchronized PromiseHandler<T> then(final Consumer<T> consumer) {
        thenConsumer = Expected.of(consumer);
        return this;
    }

    @Override
    public PromiseHandler<T> whenComplete(final Consumer<PromiseHandler<T>> doneListener) {
        if (completeListeners.isEmpty()) {
            completeListeners = Expected.of(new CopyOnWriteArrayList<>());
        }
        completeListeners.get().add(doneListener);
        return this;
    }

    @Override
    public synchronized PromiseHandler<T> thenExpect(Consumer<Expected<T>> consumer) {
        thenExpectedConsumer = Expected.of(consumer);
        return this;
    }

    @Override
    public PromiseHandler<T> catchError(Consumer<Throwable> consumer) {
        catchConsumer = Expected.of(consumer);
        return this;
    }

    @Override
    public PromiseHandler<T> invokeWithReactor(final Reactor reactor) {
        final BasePromise<T> reactorPromise = (BasePromise<T>) reactor.promise();
        copyPromiseFieldsToReactorPromise(reactorPromise);
        return this;
    }


    @Override
    public PromiseHandler<T> invokeWithReactor(final Reactor reactor, Duration timeout) {
        final BasePromise<T> reactorPromise = (BasePromise<T>) reactor.promise(timeout);
        copyPromiseFieldsToReactorPromise(reactorPromise);
        return this;
    }


    @Override
    public boolean success() {
        if (!complete()) {
            throw new NoSuchElementException("No value present, result not returned.");
        }
        return result.get().success();
    }

    @Override
    public boolean complete() {
        return result.get() != null;
    }

    @Override
    public boolean failure() {

        if (!complete()) {
            throw new NoSuchElementException("No value present, result not returned.");
        }
        return result.get().failure();
    }

    @Override
    public Throwable cause() {

        if (!complete()) {
            throw new NoSuchElementException("No value present, result not returned.");
        }
        return result.get().cause();
    }

    /**
     * If the value of the promise can be null, it is better to use Expected which is like Optional.
     *
     * @return value associated with a successful result.
     */
    public Expected<T> expect() {
        if (!complete()) {
            throw new NoSuchElementException("No value present, result not returned.");
        }
        if (failure()) {
            throw new IllegalStateException(cause());
        }
        return result.get().expect();
    }

    /**
     * Raw value of the result.
     * You should not use this if the result could be null, use expect instead.
     *
     * @return raw value associated with the result.
     */
    public T get() {
        return PromiseUtil.doGet(result, this);
    }

    @Override
    public T orElse(T other) {
        if (!complete()) {
            throw new NoSuchElementException("No value present, result not returned.");
        }
        return success() ? result.get().get() : other;
    }

    @Override
    public void onResult(final Result<T> result) {
        if (this.result.compareAndSet(null, result)) {
            doOnResult(result);
        }
    }

    protected void doOnResult(final Result<T> result) {


        if (result.success()) {
            if (!safe) {
                thenConsumer.ifPresent(consumer -> consumer.accept(result.get()));
                thenExpectedConsumer.ifPresent(valueConsumer -> valueConsumer.accept(result.expect()));
            } else {
                try {
                    thenConsumer.ifPresent(consumer -> consumer.accept(result.get()));
                    thenExpectedConsumer.ifPresent(valueConsumer -> valueConsumer.accept(result.expect()));
                } catch (Exception ex) {
                    catchConsumer.ifPresent(catchConsumer -> catchConsumer.accept(
                            new ThenHandlerException(ex)));
                }
            }
        } else {
            catchConsumer.ifPresent(catchConsumer -> catchConsumer.accept(result.cause()));
        }

        this.completeListeners.ifPresent(runnables ->
                runnables.forEach(promiseConsumer -> promiseConsumer.accept(this)));
    }


    @Override
    public <U> PromiseHandler<U> thenMap(Function<? super T, ? extends U> mapper) {
        return PromiseUtil.mapPromise(this, mapper);
    }

    private void copyPromiseFieldsToReactorPromise(BasePromise<T> reactorPromise) {
        reactorPromise.catchConsumer = this.catchConsumer;
        reactorPromise.thenConsumer = this.thenConsumer;
        reactorPromise.thenExpectedConsumer = this.thenExpectedConsumer;
        reactorPromise.safe = this.safe;
        this.thenExpectedConsumer = Expected.empty();
        this.thenConsumer = Expected.empty();
        this.catchConsumer = Expected.empty();


        completeListeners.ifPresent(consumers ->
                consumers.forEach(reactorPromise::whenComplete));

        this.thenPromise(reactorPromise);
        this.invoke();
    }
}
