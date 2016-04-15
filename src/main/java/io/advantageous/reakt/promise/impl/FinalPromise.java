package io.advantageous.reakt.promise.impl;


import io.advantageous.reakt.Expected;
import io.advantageous.reakt.Result;
import io.advantageous.reakt.promise.Promise;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

public class FinalPromise<T> implements Promise<T> {

    protected final Expected<List<Consumer<Promise<T>>>> completeListeners;
    protected final AtomicReference<Result<T>> result = new AtomicReference<>();
    protected final Expected<Consumer<T>> thenConsumer;
    protected final Expected<Consumer<Throwable>> catchConsumer;
    protected final Expected<Consumer<Expected<T>>> thenValueConsumer;

    public FinalPromise(Expected<Consumer<T>> thenConsumer,
                        Expected<Consumer<Throwable>> catchConsumer,
                        Expected<Consumer<Expected<T>>> thenValueConsumer,
                        Expected<List<Consumer<Promise<T>>>> completeListeners) {
        this.thenConsumer = thenConsumer;
        this.catchConsumer = catchConsumer;
        this.thenValueConsumer = thenValueConsumer;
        this.completeListeners = completeListeners;
    }


    protected void doFail(Throwable cause) {
        catchConsumer.ifPresent(catchConsumer -> catchConsumer.accept(cause));
    }

    protected void doThen(T value) {
        thenConsumer.ifPresent(consumer -> consumer.accept(value));
    }


    public Promise<T> then(final Consumer<T> consumer) {
        throw new UnsupportedOperationException("then(..) not supported for final promise");

    }

    @Override
    public Promise<T> whenComplete(Consumer<Promise<T>> doneListener) {
        throw new UnsupportedOperationException("whenComplete(..) not supported for final promise");
    }


    @Override
    public synchronized Promise<T> thenExpect(Consumer<Expected<T>> consumer) {
        throw new UnsupportedOperationException("thenExpect(..) not supported for final promise");
    }

    @Override
    public Promise<T> catchError(Consumer<Throwable> consumer) {
        throw new UnsupportedOperationException("catchError(..) not supported for final promise");
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
    public void onResult(Result<T> result) {

        this.result.set(result);
        if (result.success()) {
            doThen(result.get());
            doThenValue(result);
        } else {
            doFail(result.cause());
        }
        this.completeListeners.ifPresent(runnables ->
                runnables.forEach((Consumer<Consumer<Promise<T>>>) promiseConsumer -> promiseConsumer.accept(this)));
    }

    protected void doThenValue(final Result<T> result) {
        this.thenValueConsumer.ifPresent(valueConsumer -> valueConsumer.accept(result.expect()));
    }

    @Override
    public <U> Promise<U> thenMap(Function<? super T, ? extends U> mapper) {
        throw new UnsupportedOperationException("then(..) not supported for final promise");
    }

    @Override
    public T orElse(T other) {
        if (!complete()) {
            throw new NoSuchElementException("No value present, result not returned.");
        }
        return success() ? result.get().get() : other;
    }

}
