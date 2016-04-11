package io.advantageous.reakt.impl;

import io.advantageous.reakt.Expected;
import io.advantageous.reakt.Result;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class ResultImpl<T> implements Result<T> {

    private final Object object;

    public ResultImpl(Object object) {
        this.object = object;
    }

    @Override
    public Result<T> thenExpect(Consumer<Expected<T>> consumer) {
        if (success()) {
            consumer.accept(expect());
        }
        return this;
    }


    @Override
    public Result<T> then(Consumer<T> consumer) {
        if (success()) {
            consumer.accept(get());
        }
        return this;
    }


    @Override
    public Result<T> catchError(Consumer<Throwable> handler) {
        if (failure()) {
            handler.accept(cause());
        }
        return this;
    }

    public boolean success() {
        return !(object instanceof Throwable);
    }


    @Override
    public boolean complete() {
        return true;
    }


    public boolean failure() {
        return object instanceof Throwable;
    }

    @Override
    public Throwable cause() {
        return object instanceof Throwable ? (Throwable) object : null;
    }


    @SuppressWarnings("unchecked")
    public Expected<T> expect() {
        if (failure()) {
            throw new IllegalStateException(cause());
        }
        return Expected.ofNullable((T) object);
    }


    @SuppressWarnings("unchecked")
    public T get() {
        if (failure()) {
            throw new IllegalStateException(cause());
        }
        return (T) object;
    }

    @Override
    public T orElse(T other) {
        return success() ? (T)object : other;
    }

}
