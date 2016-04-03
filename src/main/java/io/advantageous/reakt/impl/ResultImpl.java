package io.advantageous.reakt.impl;

import io.advantageous.reakt.Result;
import io.advantageous.reakt.Value;

import java.util.function.Consumer;

public class ResultImpl<T> implements Result<T> {

    final Object object;

    public ResultImpl(Object object) {
        this.object = object;
    }

    @Override
    public Result<T> thenValue(Consumer<Value<T>> consumer) {
        if (success()) {
            consumer.accept(getValue());
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
    public Result<T> rejected(Consumer<Throwable> handler) {
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

    @Override
    public void cancel() {

    }

    public Value<T> getValue() {
        return Value.ofNullable((T) object);
    }


    public T get() {
        return (T) object;
    }
}
