package io.advantageous.reakt;

import io.advantageous.reakt.impl.ValueImpl;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


/**
 *
 * Contains an value object which may not be set. This is like {@code Optional} but could be the value from an async operation
 * which sent a null.
 *
 * If a value is present, {@code isPresent()} will return {@code true} and
 * {@code get()} will return the value.
 *
 * This is heavily modeled after {@link java.util.Optional} optional.
 */
public interface Value<T> {
    /**
     * Common instance for {@code empty()}.
     */
    Value EMPTY = new ValueImpl<>();

    /**
     * Returns an empty {@code ValueImpl} instance.  No value is present for this
     * value.
     *
     * @param <T> Type of the non-existent value
     * @return an empty {@code ValueImpl}
     */
    static <T> Value<T> empty() {
        @SuppressWarnings("unchecked")
        Value<T> t = EMPTY;
        return t;
    }

    /**
     * Returns an {@code ValueImpl} using the specified present value, which must not be null.
     *
     * @param <T>   the class of the value
     * @param value the value to be present. Must be non-null
     * @return an {@code ValueImpl} with the value present
     * @throws NullPointerException if value is null
     */
    static <T> Value<T> of(T value) {
        return new ValueImpl<>(value);
    }

    /**
     * Returns an {@code ValueImpl} describing the specified value, if non-null,
     * otherwise returns an empty {@code ValueImpl}.
     *
     * @param <T>   the class of the value
     * @param value the possibly non-existent value
     * @return an {@code ValueImpl} with a present value if the specified value
     * is non-null, otherwise an empty {@code Optional}
     */
    static <T> Value<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    /**
     * Returns an {@code ValueImpl} describing the specified value, if non-null,
     * otherwise returns an empty {@code ValueImpl}.
     *
     * @param <T>   the class of the value
     * @param value the possibly non-existent value
     * @return an {@code ValueImpl} with a present value if the specified value
     * is not empty, otherwise an empty {@code Optional}
     */
    static <T> Value<T> ofOptional(Optional<T> value) {
        return !value.isPresent() ? empty() : of(value.get());
    }


    /**
     * If a value is present in this {@code Value}, returns the value,
     * otherwise throws {@code NoSuchElementException}.
     *
     * @return the value held by this {@code Value}
     * @throws NoSuchElementException if there is no value present
     * @see Value#isPresent()
     */
    T get();


    /**
     * Return {@code true} if there is a value present, otherwise {@code false}.
     *
     * @return {@code true} if there is a value present, otherwise {@code false}
     */
    boolean isPresent();


    /**
     * Return {@code true} if there is not a value present, otherwise {@code false}.
     *
     * @return {@code true} if there is not a value present, otherwise {@code false}
     */
    boolean isEmpty();


    /**
     * If a value is present, invoke the consumer with the value.
     *
     * @param consumer executed if a value is present
     * @throws NullPointerException if value is present and {@code consumer} is
     *                              null
     */
    Value<T> ifPresent(Consumer<? super T> consumer);


    /**
     * If a value is not present, invoke the runnable.
     *
     * @param runnable executed if a value is not present
     */
    Value<T> ifEmpty(Runnable runnable);


    /**
     * If a value is present, and the value matches the given predicate,
     * return an {@code ValueImpl} describing the value, otherwise return an
     * empty {@code ValueImpl}.
     *
     * @param predicate a predicate to apply to the value, if present
     * @return an {@code ValueImpl} the value {@code Value}
     * if present and the value matches the predicate,
     * otherwise an empty {@code Value}
     * @throws NullPointerException if the predicate is null
     */
    Value<T> filter(Predicate<? super T> predicate);


    /**
     * If a value present, use the mapping function to it,
     * and if the result is present, return an {@code ValueImpl} with the result.
     * Otherwise return an empty value {@code Value}.
     *
     * @param <U>    The type of the result of the mapping function
     * @param mapper a mapper to apply to the value, if present
     * @return a value {@code Value} which is the result of the mapper
     * function applied to {@code Value} value if present or an empty value.
     * @throws NullPointerException if the mapper is null
     */
    <U> Value<U> map(Function<? super T, ? extends U> mapper);


    /**
     * Return the value if present.  If not present return {@code other}.
     *
     * @param other value which is returned if no value present.
     * @return the value, if present, or if not present return {@code other}
     */
    T orElse(T other);


    /**
     * Indicates whether some other object is "equal to" the value.
     * The other value is equal if Object.equals(value, other) returns true.
     */
    boolean equalsValue(Object value);


}
