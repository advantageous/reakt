package io.advantageous.reakt.impl;

import io.advantageous.reakt.Value;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Contains an value object which may not be set.
 * If a value is present, {@code isPresent()} will return {@code true} and
 * {@code get()} will return the value.
 */
public class ValueImpl<T> implements Value<T> {

    /**
     * If non-null, the value; if null, indicates no value is present
     */
    private final T value;

    /**
     * Constructs an empty instance of value.
     */
    public ValueImpl() {
        this.value = null;
    }

    /**
     * Constructs an instance with the value present.
     *
     * @param value the non-null value to be present
     * @throws NullPointerException if value is null
     */
    public ValueImpl(T value) {
        this.value = Objects.requireNonNull(value);
    }


    /**
     * If a value is present in this {@code Value}, returns the value,
     * otherwise throws {@code NoSuchElementException}.
     *
     * @return the value held by this {@code Value}
     * @throws NoSuchElementException if there is no value present
     * @see ValueImpl#isPresent()
     */
    @Override
    public T get() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    /**
     * Return {@code true} if there is a value present, otherwise {@code false}.
     *
     * @return {@code true} if there is a value present, otherwise {@code false}
     */
    @Override
    public boolean isPresent() {
        return value != null;
    }


    /**
     * Return {@code true} if there is not a value present, otherwise {@code false}.
     *
     * @return {@code true} if there is not a value present, otherwise {@code false}
     */
    @Override
    public boolean isEmpty() {
        return value == null;
    }

    /**
     * If a value is present, invoke the consumer with the value.
     *
     * @param consumer executed if a value is present
     * @throws NullPointerException if value is present and {@code consumer} is
     *                              null
     */
    @Override
    public Value<T> ifPresent(Consumer<? super T> consumer) {
        if (value != null)
            consumer.accept(value);

        return this;
    }

    /**
     * If a value is not present, invoke the runnable.
     *
     * @param runnable executed if a value is not present
     */
    @Override
    public Value<T> ifEmpty(Runnable runnable) {
        if (value == null)
            runnable.run();
        return this;
    }


    /**
     * If a value is present, and the value matches the given predicate,
     * return an {@code ValueImpl} describing the value, otherwise return an
     * empty {@code ValueImpl}.
     *
     * @param predicate a predicate to apply to the value, if present
     * @return an {@code ValueImpl} the value {@code ValueImpl}
     * if present and the value matches the predicate,
     * otherwise an empty {@code ValueImpl}
     * @throws NullPointerException if the predicate is null
     */
    @Override
    public Value<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (!isPresent())
            return this;
        else
            return predicate.test(value) ? this : Value.empty();
    }

    /**
     * If a value present, use the mapping function to it,
     * and if the result is present, return an {@code ValueImpl} with the result.
     * Otherwise return an empty value {@code ValueImpl}.
     *
     * @param <U>    The type of the result of the mapping function
     * @param mapper a mapper to apply to the value, if present
     * @return a value {@code ValueImpl} which is the result of the mapper
     * function applied to {@code ValueImpl} value if present or an empty value.
     * @throws NullPointerException if the mapper is null
     */
    @Override
    public <U> Value<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent())
            return Value.empty();
        else {
            return Value.ofNullable(mapper.apply(value));
        }
    }


    /**
     * Return the value if present.  If not present return {@code other}.
     *
     * @param other value which is returned if no value present.
     * @return the value, if present, or if not present return {@code other}
     */
    @Override
    public T orElse(T other) {
        return value != null ? value : other;
    }

    /**
     * Indicates whether some other object is "equal to" the value.
     * The other value is equal if:
     * <ul>
     * <li>it is also an {@code ValueImpl} and;
     * <li>both instances have no value present or;
     * <li>the present values are "equal to" to the other value.
     * </ul>
     *
     * @param other an object to be tested for equality
     * @return {code true} if the other object is "equal to" this object
     * otherwise {@code false}
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Value)) {
            return false;
        }

        ValueImpl<?> otherValue = (ValueImpl<?>) other;
        return Objects.equals(value, otherValue.value);
    }

    /**
     * Indicates whether some other object is "equal to" the value.
     * The other value is equal if Object.equals(value, other) returns true.
     */
    @Override
    public boolean equalsValue(Object other) {
        return Objects.equals(value, other);
    }

    /**
     * Returns the hash code value of the present value, if any, or 0 (zero) if
     * no value is present.
     *
     * @return hash code value of the present value or 0 if no value is present
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    /**
     * Returns a non-empty string representation of this Value suitable for
     * debugging. The exact presentation format is unspecified and may vary
     * between implementations and versions.
     *
     * @return the string representation of this instance
     */
    @Override
    public String toString() {
        return "Value{" + "value=" + value + '}';
    }
}
