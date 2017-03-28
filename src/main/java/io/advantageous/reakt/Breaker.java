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

package io.advantageous.reakt;

import io.advantageous.reakt.impl.BreakerImpl;

import java.util.function.Consumer;
import java.util.function.Predicate;


/**
 * Represents a Circuit Breaker.
 * The contained service can be broken (open circuit) or operational (closed circuit).
 * <p>
 * This represents a service which may or may not be available.
 * </p>
 * We were using Expected a lot where we really wanted something like a Breaker.
 * <p>
 * This could be extended to blow the circuit with different conditions by providing
 * your own Breaker.
 * </p>
 * <p>
 * Also we want to use interfaces for all core concepts.
 * </p>
 * In addition we wanted callback for ifBroken and ifOperational.
 * <p>
 * If a service is active and healthy, {@code isOperational()} will return {@code true}.
 * If a service is not healthy or not working, {code isBroken()} will return {@code true}.
 * </p>
 * This is heavily modeled after {@code Expected} optional.
 *
 * @author Rick Hightower
 * @author Geoff Chandler
 */
public interface Breaker<T> {
    /**
     * Common instance for {@code broken()}.
     */
    Breaker OPENED = new BreakerImpl<>();

    /**
     * Returns an open {@code Breaker} instance.  No service is present for this
     * value.
     *
     * @param <T> Type of the non-existent value
     * @return an empty {@code ExpectedImpl}
     */
    static <T> Breaker<T> broken() {
        return opened();
    }

    /**
     * Returns an open/broken {@code Breaker} instance.  No service is present for this
     * value.
     *
     * @param <T> Type of the non-existent value
     * @return an empty {@code ExpectedImpl}
     */
    static <T> Breaker<T> opened() {
        @SuppressWarnings("unchecked")
        Breaker<T> t = OPENED;
        return t;
    }

    /**
     * Returns an {@code Breaker} using the specified present value, which must not be null.
     *
     * @param <T>   the class of the value
     * @param value the value to be present. Must be non-null
     * @return an {@code ExpectedImpl} with the value present
     * @throws NullPointerException if value is null
     */
    static <T> Breaker<T> operational(T value) {
        return new BreakerImpl<>(value);
    }

    /**
     * Returns an {@code Breaker} using the specified present value, which must not be null.
     *
     * @param <T>            the class of the value
     * @param value          the value to be present. Must be non-null
     * @param maxErrorsCount max error count
     * @return an {@code ExpectedImpl} with the value present
     * @throws NullPointerException if value is null
     */
    static <T> Breaker<T> operational(T value, final int maxErrorsCount) {
        return new BreakerImpl<>(value, maxErrorsCount);
    }


    /**
     * Returns an {@code Breaker} using the specified present value, which must not be null.
     *
     * @param <T>             the class of the value
     * @param value           the value to be present. Must be non-null
     * @param maxErrorsCount  max error count
     * @param brokenPredicate supplier to determine if connection to service is broken
     * @return an {@code ExpectedImpl} with the value present
     * @throws NullPointerException if value is null
     */
    static <T> Breaker<T> operational(T value, final int maxErrorsCount, final Predicate<T> brokenPredicate) {
        return new BreakerImpl<>(value, maxErrorsCount, brokenPredicate);
    }


    /**
     * Returns an {@code Breaker} using the specified present value, which must not be null.
     *
     * @param <T>             the class of the value
     * @param value           the value to be present. Must be non-null
     * @param brokenPredicate predicate to determine if connection to service is broken
     * @return an {@code ExpectedImpl} with the value present
     * @throws NullPointerException if value is null
     */
    static <T> Breaker<T> operational(T value, final Predicate<T> brokenPredicate) {
        return new BreakerImpl<>(value, 0, brokenPredicate);
    }

    /**
     * x
     * Return {@code true} if the service is broken, otherwise {@code false}.
     *
     * @return {@code true} if the service is broken, otherwise {@code false}
     */
    boolean isBroken();

    /**
     * Return {@code true} if the service is working, otherwise {@code false}.
     *
     * @return {@code true} if the service is working, otherwise {@code false}.
     */
    boolean isOperational();

    /**
     * Short version of isOperational.
     *
     * @return ok
     */
    default boolean isOk() {
        return isOperational();
    }

    /**
     * If a service is beleived to be working, invoke the consumer with the value.
     * <p>
     * This tracks errors thrown by the consumer.
     *
     * @param consumer executed if a value is present
     * @return this, fluent API
     * @throws NullPointerException if value is present and {@code consumer} is
     *                              null
     */
    Breaker<T> ifOperational(Consumer<? super T> consumer);

    /**
     * Short version of ifOperational.
     * If a service is beleived to be working, invoke the consumer with the value.
     *
     * @param consumer executed if a value is present
     * @return this, fluent API
     * @throws NullPointerException if value is present and {@code consumer} is
     *                              null
     */
    default Breaker<T> ifOk(Consumer<? super T> consumer) {
        return ifOperational(consumer);
    }

    /**
     * If a service is broken, invoke the runnable.
     *
     * @param runnable executed if a value is not present
     * @return this, fluent API
     */
    Breaker<T> ifBroken(Runnable runnable);

    /**
     * If a service is broken but present, invoke the consumer.
     * This is used to do clean up, like closing a connection.
     *
     * @param consumer executed if a value is not present
     * @return this, fluent API
     */
    Breaker<T> cleanup(Consumer<? super T> consumer);

    /**
     * @return number of errors detected.
     */
    long errorCount();

}
