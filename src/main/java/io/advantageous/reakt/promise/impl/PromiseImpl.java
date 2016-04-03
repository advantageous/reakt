package io.advantageous.reakt.promise.impl;


import io.advantageous.reakt.promise.Promise;
import io.advantageous.reakt.Result;
import io.advantageous.reakt.Value;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class PromiseImpl<T> implements Promise<T> {

    protected final AtomicReference<Result<T>> result = new AtomicReference<>();
    protected Value<Consumer<T>> thenConsumer = Value.empty();
    protected Value<Consumer<Throwable>> catchConsumer = Value.empty();
    protected Value<Consumer<Value<T>>> thenValueConsumer;


    protected void doFail(Throwable cause) {
        catchConsumer.ifPresent(catchConsumer -> catchConsumer.accept(cause));

    }

    protected void doThen(T value) {
        thenConsumer.ifPresent(consumer -> consumer.accept(value));
    }


    public synchronized Promise then(final Consumer<T> consumer) {
        thenConsumer = Value.of(consumer);
        return this;
    }

    @Override
    public synchronized Promise thenValue(Consumer<Value<T>> consumer) {
        thenValueConsumer = Value.of(consumer);
        return this;
    }

    @Override
    public Result<T> catchError(Consumer<Throwable> consumer) {
        catchConsumer = Value.of(consumer);
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

    @Override
    public void cancel() {

        if (result.get() == null) {
            throw new NoSuchElementException("No value present, result not returned.");
        }
        result.get().cancel();

    }


    /**
     * If the value of the promise can be null, it is better to use Value which is like Optional.
     *
     * @return value associated with a successful result.
     */
    public Value<T> getValue() {
        if (result.get() == null) {
            throw new NoSuchElementException("No value present, result not returned.");
        }

        return result.get().getValue();
    }

    /**
     * Raw value of the result.
     * You should not use this if the result coulb be null, use getValue instead.
     *
     * @return raw value associated with the result.
     */
    public T get() {

        if (result.get() == null) {
            throw new NoSuchElementException("No value present, result not returned.");
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
    }

    protected void doThenValue(final Result<T> result) {
        this.thenValueConsumer.ifPresent(valueConsumer -> valueConsumer.accept(result.getValue()));
    }
}
