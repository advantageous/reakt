/*
 *
 *  Copyright (c) 2016. Rick Hightower, Geoff Chandler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    		http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.advantageous.reakt.impl;

import io.advantageous.reakt.Breaker;
import io.advantageous.reakt.Expected;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Circuit breaker.
 *
 * @author Rick Hightower
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
    private final Expected<Predicate<T>> brokenPredicate;

    public BreakerImpl() {
        this.maxErrorCount = 0;
        this.service = null;
        this.brokenPredicate = Expected.empty();
    }

    public BreakerImpl(final T value) {
        this.maxErrorCount = 0;
        this.service = Objects.requireNonNull(value);
        this.brokenPredicate = Expected.empty();
    }

    public BreakerImpl(final T value, final int maxErrorCount) {
        this.service = Objects.requireNonNull(value);
        this.maxErrorCount = maxErrorCount;
        this.brokenPredicate = Expected.empty();
    }

    public BreakerImpl(final T value, final int maxErrorCount, final Predicate<T> brokenPredicate) {
        this.service = Objects.requireNonNull(value);
        this.maxErrorCount = maxErrorCount;
        this.brokenPredicate = Expected.of(brokenPredicate);
    }


    @Override
    public boolean isOperational() {
        return (this.service != null) &&
                (this.maxErrorCount == 0 || errorCount() < this.maxErrorCount) &&
                (this.brokenPredicate.isEmpty() || !this.brokenPredicate.get().test(service));
    }

    @Override
    public boolean isBroken() {
        return !isOperational();
    }

    @Override
    public Breaker<T> ifOperational(final Consumer<? super T> consumer) {
        try {
            if (isOperational()) consumer.accept(this.service);
            return this;
        } catch (Exception ex) {
            this.errors.incrementAndGet();
            throw new IllegalStateException("Operation failed", ex);
        }
    }

    @Override
    public Breaker<T> ifBroken(final Runnable runnable) {
        if (isBroken()) runnable.run();
        return this;
    }


    @Override
    public Breaker<T> cleanup(final Consumer<? super T> consumer) {
        if (this.service != null) consumer.accept(this.service);
        return this;
    }

    @Override
    public long errorCount() {
        return this.errors.get();
    }
}
