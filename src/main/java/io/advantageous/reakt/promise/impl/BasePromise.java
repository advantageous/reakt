package io.advantageous.reakt.promise.impl;


import io.advantageous.reakt.Ref;
import io.advantageous.reakt.Result;
import io.advantageous.reakt.promise.Promise;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class BasePromise<T> implements Promise<T> {

    protected final AtomicReference<Result<T>> result = new AtomicReference<>();
    protected Ref<Consumer<T>> thenConsumer = Ref.empty();
    protected Ref<Consumer<Throwable>> catchConsumer = Ref.empty();
    protected Ref<Consumer<Ref<T>>> thenValueConsumer = Ref.empty();
    protected Ref<List<Runnable>> completeListeners = Ref.empty();

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
        thenConsumer = Ref.of(consumer);
        return this;
    }

    @Override
    public Promise<T> whenComplete(final Runnable doneListener) {
        if (completeListeners.isEmpty()) {
            completeListeners = Ref.of(new CopyOnWriteArrayList<>());
        }
        completeListeners.get().add(doneListener);
        return this;
    }

    @Override
    public synchronized Promise<T> thenRef(Consumer<Ref<T>> consumer) {
        thenValueConsumer = Ref.of(consumer);
        return this;
    }

    @Override
    public Promise<T> catchError(Consumer<Throwable> consumer) {
        catchConsumer = Ref.of(consumer);
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
        doOnResult(result);
    }

    protected void doOnResult(Result<T> result) {
        this.result.set(result);
        if (result.success()) {
            thenConsumer.ifPresent(consumer -> consumer.accept(result.get()));
            thenValueConsumer.ifPresent(valueConsumer -> valueConsumer.accept(result.getRef()));
        } else {
            catchConsumer.ifPresent(catchConsumer -> catchConsumer.accept(result.cause()));
        }
        this.completeListeners.ifPresent(runnables -> runnables.forEach((Consumer<Runnable>) Runnable::run));
    }
}
