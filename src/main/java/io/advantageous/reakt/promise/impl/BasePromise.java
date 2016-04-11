package io.advantageous.reakt.promise.impl;


import io.advantageous.reakt.Expected;
import io.advantageous.reakt.Result;
import io.advantageous.reakt.promise.Promise;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

public class BasePromise<T> implements Promise<T> {

    protected final AtomicReference<Result<T>> result = new AtomicReference<>();
    protected Expected<Consumer<T>> thenConsumer = Expected.empty();
    protected Expected<Consumer<Throwable>> catchConsumer = Expected.empty();
    protected Expected<Consumer<Expected<T>>> thenValueConsumer = Expected.empty();
    protected Expected<List<Consumer<Promise<T>>>> completeListeners = Expected.empty();

    public static <T> Promise<T> provideFinalPromise(Promise<T> promise) {
        if (promise instanceof BasePromise) {
            BasePromise<T> basePromise = ((BasePromise<T>) promise);
            return new FinalPromise<>(basePromise.thenConsumer,
                    basePromise.catchConsumer,
                    basePromise.thenValueConsumer,
                    basePromise.completeListeners);
        } else {
            throw new IllegalStateException("Operation not supported use FinalPromise directly");
        }
    }

    public synchronized Promise<T> then(final Consumer<T> consumer) {
        thenConsumer = Expected.of(consumer);
        return this;
    }

    @Override
    public Promise<T> whenComplete(final Consumer<Promise<T>> doneListener) {
        if (completeListeners.isEmpty()) {
            completeListeners = Expected.of(new CopyOnWriteArrayList<>());
        }
        completeListeners.get().add(doneListener);
        return this;
    }

    @Override
    public synchronized Promise<T> thenExpect(Consumer<Expected<T>> consumer) {
        thenValueConsumer = Expected.of(consumer);
        return this;
    }

    @Override
    public Promise<T> catchError(Consumer<Throwable> consumer) {
        catchConsumer = Expected.of(consumer);
        return this;
    }

    @Override
    public boolean success() {
        if (result.get() == null) {
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

        if (result.get() == null) {
            throw new NoSuchElementException("No value present, result not returned.");
        }
        return result.get().failure();
    }

    @Override
    public Throwable cause() {

        if (result.get() == null) {
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
        if (result.get() == null) {
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

        if (result.get() == null) {
            throw new NoSuchElementException("No value present, result not returned.");
        }
        if (failure()) {
            throw new IllegalStateException(cause());
        }
        return result.get().get();
    }

    @Override
    public T orElse(T other) {
        if (!complete()) {
            throw new NoSuchElementException("No value present, result not returned.");
        }
        return success() ? result.get().get() : other;
    }

    @Override
    public void onResult(Result<T> result) {
        doOnResult(result);
    }

    protected void doOnResult(Result<T> result) {
        this.result.set(result);
        if (result.success()) {
            thenConsumer.ifPresent(consumer -> consumer.accept(result.get()));
            thenValueConsumer.ifPresent(valueConsumer -> valueConsumer.accept(result.expect()));
        } else {
            catchConsumer.ifPresent(catchConsumer -> catchConsumer.accept(result.cause()));
        }
        this.completeListeners.ifPresent(runnables ->
                runnables.forEach((Consumer<Consumer<Promise<T>>>) promiseConsumer -> promiseConsumer.accept(this)));
    }


    @Override
    public <U> Promise<U> thenMap(Function<? super T, ? extends U> mapper) {
        return PromiseUtil.mapPromise(this, mapper);
    }

}
