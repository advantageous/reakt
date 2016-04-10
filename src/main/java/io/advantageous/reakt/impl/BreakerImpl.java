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
    private final int maxErrorCount;

    public BreakerImpl() {
        maxErrorCount = 0;
        this.service = null;
    }

    public BreakerImpl(T value) {
        maxErrorCount = 0;
        this.service = Objects.requireNonNull(value);
    }


    public BreakerImpl(T value, int maxErrorCount) {
        this.service = Objects.requireNonNull(value);
        this.maxErrorCount = maxErrorCount;
    }


    @Override
    public boolean isOperational() {
        return service != null && maxErrorCount == 0 || errorCount() < maxErrorCount;
    }

    @Override
    public boolean isBroken() {
        return !isOperational();
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
