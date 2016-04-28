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

import io.advantageous.reakt.Expected;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Contains an value object which may not be set.
 * If a value is present, {@code isPresent()} will return {@code true} and
 * {@code get()} will return the value.
 *
 * @author Rick Hightower
 */
public class ExpectedImpl<T> implements Expected<T> {

    /**
     * If non-null, the value; if null, indicates no value is present
     */
    private final T value;

    /**
     * Constructs an empty instance of value.
     */
    public ExpectedImpl() {
        this.value = null;
    }

    /**
     * Constructs an instance with the value present.
     *
     * @param value the non-null value to be present
     * @throws NullPointerException if value is null
     */
    public ExpectedImpl(T value) {
        this.value = Objects.requireNonNull(value);
    }

    /**
     * If a value is present in this {@code Expected}, returns the value,
     * otherwise throws {@code NoSuchElementException}.
     *
     * @return the value held by this {@code Expected}
     * @throws NoSuchElementException if there is no value present
     * @see ExpectedImpl#isPresent()
     */
    @Override
    public T get() {
        if (this.value == null) throw new NoSuchElementException("No value present");
        return this.value;
    }

    /**
     * Return {@code true} if there is a value present, otherwise {@code false}.
     *
     * @return {@code true} if there is a value present, otherwise {@code false}
     */
    @Override
    public boolean isPresent() {
        return this.value != null;
    }


    /**
     * Return {@code true} if there is not a value present, otherwise {@code false}.
     *
     * @return {@code true} if there is not a value present, otherwise {@code false}
     */
    @Override
    public boolean isAbsent() {
        return this.value == null;
    }

    /**
     * Return {@code true} if there is not a value present, otherwise {@code false}.
     *
     * @return {@code true} if there is not a value present, otherwise {@code false}
     */
    @Override
    public boolean isEmpty() {
        if (this.value == null)  {
            return true;
        } else if (value instanceof Collection) {
            final Collection c = ((Collection) value);
            if (c.size() == 0) {
                return true;
            }
        } else if (value instanceof CharSequence) {
            final CharSequence cs = ((CharSequence) value);
            if (cs.length() == 0) {
                return true;
            }
        } else if (value.getClass().isArray()) {
            if (Array.getLength(value) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * If a value is present, invoke the consumer with the value.
     *
     * @param consumer executed if a value is present
     * @throws NullPointerException if value is present and {@code consumer} is
     *                              null
     */
    @Override
    public Expected<T> ifPresent(final Consumer<? super T> consumer) {
        if (this.value != null) consumer.accept(this.value);
        return this;
    }

    /**
     * If a value is not present or present but empty, invoke the runnable.
     *
     * @param runnable executed if a value is not present
     */
    @Override
    public Expected<T> ifEmpty(final Runnable runnable) {
        if (isEmpty()) {
            runnable.run();
        }
        return this;
    }

    /**
     * If a value is not present (null), invoke the runnable.
     *
     * @param runnable executed if a value is not present
     */
    @Override
    public Expected<T> ifAbsent(final Runnable runnable) {
        if (isAbsent()) {
            runnable.run();
        }
        return this;
    }


    /**
     * If a value is present, and the value matches the given predicate,
     * return an {@code ExpectedImpl} describing the value, otherwise return an
     * empty {@code ExpectedImpl}.
     *
     * @param predicate a predicate to apply to the value, if present
     * @return an {@code ExpectedImpl} the value {@code ExpectedImpl}
     * if present and the value matches the predicate,
     * otherwise an empty {@code ExpectedImpl}
     * @throws NullPointerException if the predicate is null
     */
    @Override
    public Expected<T> filter(final Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return !isPresent() ? this : predicate.test(value) ? this : Expected.empty();
    }

    /**
     * If a value present, use the mapping function to it,
     * and if the result is present, return an {@code ExpectedImpl} with the result.
     * Otherwise return an empty value {@code ExpectedImpl}.
     *
     * @param <U>    The type of the result of the mapping function
     * @param mapper a mapper to apply to the value, if present
     * @return a value {@code ExpectedImpl} which is the result of the mapper
     * function applied to {@code ExpectedImpl} value if present or an empty value.
     * @throws NullPointerException if the mapper is null
     */
    @Override
    public <U> Expected<U> map(final Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return !isPresent() ? Expected.empty() : Expected.ofNullable(mapper.apply(this.value));
    }

    /**
     * Return the value if present.  If not present return {@code other}.
     *
     * @param other value which is returned if no value present.
     * @return the value, if present, or if not present return {@code other}
     */
    @Override
    public T orElse(T other) {
        return this.value != null ? this.value : other;
    }

    /**
     * Indicates whether some other object is "equal to" the value.
     * The other value is equal if:
     * <ul>
     * <li>it is also an {@code ExpectedImpl} and;
     * <li>both instances have no value present or;
     * <li>the present values are "equal to" to the other value.
     * </ul>
     *
     * @param other an object to be tested for equality
     * @return {code true} if the other object is "equal to" this object
     * otherwise {@code false}
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Expected)) {
            return false;
        }
        final ExpectedImpl<?> otherValue = (ExpectedImpl<?>) other;
        return Objects.equals(this.value, otherValue.value);
    }

    /**
     * Indicates whether some other object is "equal to" the value.
     * The other value is equal if Object.equals(value, other) returns true.
     */
    @Override
    public boolean equalsValue(final Object other) {
        return Objects.equals(this.value, other);
    }

    /**
     * Returns the hash code value of the present value, if any, or 0 (zero) if
     * no value is present.
     *
     * @return hash code value of the present value or 0 if no value is present
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(this.value);
    }

    /**
     * Returns a non-empty string representation of this Expected suitable for
     * debugging. The exact presentation format is unspecified and may vary
     * between implementations and versions.
     *
     * @return the string representation of this instance
     */
    @Override
    public String toString() {
        return "Expected{" + "value=" + this.value + '}';
    }
}
