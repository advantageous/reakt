package io.advantageous.reakt.impl;

import io.advantageous.reakt.Breaker;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Circuit breaker.
 */
public class BreakerImpl<T> implements Breaker<T> {

    /**
     * hold service
     */
    private final T service;

    /**
     * Hold blow circuit breaker flag.
     */
    private final AtomicLong errors = new AtomicLong();

    public BreakerImpl() {
        this.service = null;
    }

    public BreakerImpl(T value) {
        this.service = Objects.requireNonNull(value);
    }

    @Override
    public boolean isOperational() {
        return service != null;
    }

    @Override
    public boolean isBroken() {
        return service == null;
    }

    @Override
    public Breaker<T> ifOperational(Consumer<? super T> consumer) {
        try {
            if (isOperational())
                consumer.accept(service);
            return this;
        } catch (Exception ex) {
            errors.incrementAndGet();
            throw new IllegalStateException("Operation failed", ex);
        }
    }

    @Override
    public Breaker<T> ifBroken(Runnable runnable) {
        if (isBroken())
            runnable.run();
        return this;
    }


    @Override
    public Breaker<T> cleanup(Consumer<? super T> consumer) {
        if (isBroken() && service != null)
            consumer.accept(service);
        return this;
    }

    @Override
    public long errorCount() {
        return errors.get();
    }
}
