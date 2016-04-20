package io.advantageous.reakt.promise.impl;

import io.advantageous.reakt.promise.Promise;

import java.util.function.Consumer;

public class InvokerPromise<T> extends BasePromise<T> {

    private final Consumer<Promise<T>> consumer;
    private boolean invoked;

    public InvokerPromise(Consumer<Promise<T>> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void invoke() {
        if (invoked) {
            throw new IllegalStateException("Promise can only be invoked once");
        }
        invoked = true;
        consumer.accept(this);
    }

    @Override
    public boolean isInvokable() {
        return true;
    }
}
