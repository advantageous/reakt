package io.advantageous.reakt;

import io.advantageous.reakt.impl.RefImpl;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;


/**
 * Same concept as Optional in Java JDK and Option in Scala.
 * We added a new concept because this one is expected to come through callbacks and
 * is used in places where Optional does not make sense.
 * <p>
 * Also we want to use interfaces for all core concepts.
 * <p>
 * In addition we wanted callback for ifPresent and ifEmpty.
 * <p>
 * Contains an value object which may not be set. This is like {@code Optional} but could be the value from an async operation
 * which sent a null.
 * <p>
 * If a value is present, {@code isPresent()} will return {@code true} and
 * {@code get()} will return the value.
 * <p>
 * This is heavily modeled after {@link java.util.Optional} optional.
 */
public interface Ref<T> {
    /**
     * Common instance for {@code empty()}.
     */
    Ref EMPTY = new RefImpl<>();

    /**
     * Returns an empty {@code RefImpl} instance.  No value is present for this
     * value.
     *
     * @param <T> Type of the non-existent value
     * @return an empty {@code RefImpl}
     */
    static <T> Ref<T> empty() {
        @SuppressWarnings("unchecked")
        Ref<T> t = EMPTY;
        return t;
    }

    /**
     * Returns an {@code RefImpl} using the specified present value, which must not be null.
     *
     * @param <T>   the class of the value
     * @param value the value to be present. Must be non-null
     * @return an {@code RefImpl} with the value present
     * @throws NullPointerException if value is null
     */
    static <T> Ref<T> of(T value) {
        return new RefImpl<>(value);
    }

    /**
     * Returns an {@code RefImpl} describing the specified value, if non-null,
     * otherwise returns an empty {@code RefImpl}.
     *
     * @param <T>   the class of the value
     * @param value the possibly non-existent value
     * @return an {@code RefImpl} with a present value if the specified value
     * is non-null, otherwise an empty {@code Optional}
     */
    static <T> Ref<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    /**
     * Returns an {@code RefImpl} describing the specified value, if non-null,
     * otherwise returns an empty {@code RefImpl}.
     *
     * @param <T>   the class of the value
     * @param value the possibly non-existent value
     * @return an {@code RefImpl} with a present value if the specified value
     * is not empty, otherwise an empty {@code Optional}
     */
    static <T> Ref<T> ofOptional(Optional<T> value) {
        return !value.isPresent() ? empty() : of(value.get());
    }


    /**
     * If a value is present in this {@code Ref}, returns the value,
     * otherwise throws {@code NoSuchElementException}.
     *
     * @return the value held by this {@code Ref}
     * @throws NoSuchElementException if there is no value present
     * @see Ref#isPresent()
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
     * @return this, fluent API
     */
    Ref<T> ifPresent(Consumer<? super T> consumer);


    /**
     * If a value is not present, invoke the runnable.
     *
     * @param runnable executed if a value is not present
     * @return this, fluent API
     */
    Ref<T> ifEmpty(Runnable runnable);


    /**
     * If a value is present, and the value matches the given predicate,
     * return an {@code RefImpl} describing the value, otherwise return an
     * empty {@code RefImpl}.
     *
     * @param predicate a predicate to apply to the value, if present
     * @return an {@code RefImpl} the value {@code Ref}
     * if present and the value matches the predicate,
     * otherwise an empty {@code Ref}
     * @throws NullPointerException if the predicate is null
     */
    Ref<T> filter(Predicate<? super T> predicate);


    /**
     * If a value present, use the mapping function to it,
     * and if the result is present, return an {@code RefImpl} with the result.
     * Otherwise return an empty value {@code Ref}.
     *
     * @param <U>    The type of the result of the mapping function
     * @param mapper a mapper to apply to the value, if present
     * @return a value {@code Ref} which is the result of the mapper
     * function applied to {@code Ref} value if present or an empty value.
     * @throws NullPointerException if the mapper is null
     */
    <U> Ref<U> map(Function<? super T, ? extends U> mapper);


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
     *
     * @param value checks for equality of inner value which is contained in reference
     * @return true if equal
     */
    boolean equalsValue(Object value);


}
