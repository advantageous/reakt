package io.advantageous.reakt.impl;

import io.advantageous.reakt.Ref;
import io.advantageous.reakt.Result;

import java.util.function.Consumer;

public class ResultImpl<T> implements Result<T> {

    private final Object object;

    public ResultImpl(Object object) {
        this.object = object;
    }

    @Override
    public Result<T> thenRef(Consumer<Ref<T>> consumer) {
        if (success()) {
            consumer.accept(getRef());
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
    public Ref<T> getRef() {
        if (failure()) {
            throw new IllegalStateException(cause());
        }
        return Ref.ofNullable((T) object);
    }


    @SuppressWarnings("unchecked")
    public T get() {
        if (failure()) {
            throw new IllegalStateException(cause());
        }
        return (T) object;
    }
}
