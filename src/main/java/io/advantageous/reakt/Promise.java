package io.advantageous.reakt;

import io.advantageous.reakt.impl.ResultImpl;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

// IN PROGRESS...
public class Promise<T> implements Consumer<Result<T>> {

    private final AtomicReference<Result<T>> result = new AtomicReference<>();

    private Value<Consumer<T>> thenConsumer = Value.empty();

    private Value<Consumer<Throwable>> catchConsumer = Value.empty();

    public Callback<T> callback() {
        return new Callback<T>() {
            @Override
            public void onResult(Result<T> result) {
                accept(result);
            }
        };
    }

    protected void doFail(Throwable cause) {
        catchConsumer.ifPresent(catchConsumer -> catchConsumer.accept(cause));

    }

    protected void doThen(T value) {
        thenConsumer.ifPresent(consumer -> consumer.accept(value));
    }

    protected void resolve(T value) {
        result.set(new ResultImpl<>(value));
    }


    protected void reject(Throwable error) {
        result.set(new ResultImpl<>(error));
    }


    public synchronized Promise<T> then(final Consumer<T> consumer) {
        thenConsumer = Value.of(consumer);
        return this;
    }


    public synchronized Promise<T> catchError(final Consumer<Throwable> consumer) {
        catchConsumer = Value.of(consumer);
        return this;
    }

    public Promise<T> andThen(Promise<T> after) {
        Objects.requireNonNull(after);
        return new Promise<T>() {
            @Override
            protected void doFail(Throwable cause) {
                super.doFail(cause);
                after.doFail(cause);
            }

            @Override
            protected void doThen(T value) {
                super.doThen(value);
                after.doThen(value);
            }
        };
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
    public void accept(Result<T> result) {

        this.result.set(result);
        if (result.success()) {
            doThen(result.get());
        } else {
            doFail(result.cause());
        }
    }
}
