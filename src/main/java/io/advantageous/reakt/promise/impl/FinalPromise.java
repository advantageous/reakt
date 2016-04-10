package io.advantageous.reakt.promise.impl;


import io.advantageous.reakt.Ref;
import io.advantageous.reakt.Result;
import io.advantageous.reakt.promise.Promise;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class FinalPromise<T> implements Promise<T> {

    protected final Ref<List<Consumer<Promise<T>>>> completeListeners;
    protected final AtomicReference<Result<T>> result = new AtomicReference<>();
    protected final Ref<Consumer<T>> thenConsumer;
    protected final Ref<Consumer<Throwable>> catchConsumer;
    protected final Ref<Consumer<Ref<T>>> thenValueConsumer;

    public FinalPromise(Ref<Consumer<T>> thenConsumer,
                        Ref<Consumer<Throwable>> catchConsumer,
                        Ref<Consumer<Ref<T>>> thenValueConsumer,
                        Ref<List<Consumer<Promise<T>>>> completeListeners) {
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
    public synchronized Promise<T> thenRef(Consumer<Ref<T>> consumer) {
        throw new UnsupportedOperationException("thenRef(..) not supported for final promise");
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
     * If the value of the promise can be null, it is better to use Ref which is like Optional.
     *
     * @return value associated with a successful result.
     */
    public Ref<T> getRef() {
        if (result.get() == null) {
            throw new NoSuchElementException("No value present, result not returned.");
        }
        if (failure()) {
            throw new IllegalStateException(cause());
        }
        return result.get().getRef();
    }

    /**
     * Raw value of the result.
     * You should not use this if the result could be null, use getRef instead.
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
        this.thenValueConsumer.ifPresent(valueConsumer -> valueConsumer.accept(result.getRef()));
    }
}
