package io.advantageous.reakt;

import io.advantageous.reakt.impl.ResultImpl;

import java.util.function.Consumer;

/**
 * The result of an async operation.
 * <p>
 * This was modeled after Vert.x AsyncResult and after the types of results one would deal with in JavaScript.
 *
 * @param <T> type of value expected in the result.
 */
public interface Result<T> {

    /**
     * Create a result
     *
     * @param value value
     * @param <T>   T
     * @return result
     */
    static <T> Result<T> result(T value) {
        return new ResultImpl<>(value);
    }

    /**
     * Create a result
     *
     * @param error error
     * @param <T>   T
     * @return result
     */
    static <T> Result<T> error(Throwable error) {
        return new ResultImpl<>(error);
    }


    /**
     * If a result is sent, and there was no error, then handle the result.
     *
     * @param consumer executed if result has no error.
     * @throws NullPointerException if result is present and {@code consumer} is
     *                              null
     * @return this, fluent API
     */
    Result<T> then(Consumer<T> consumer);


    /**
     * If a result is sent, and there was no error, then handle the result as a value which could be null.
     *
     * @param consumer executed if result has no error.
     * @throws NullPointerException if result is present and {@code consumer} is
     *                              null
     * @return this, fluent API
     */
    Result<T> thenRef(Consumer<Ref<T>> consumer);


    /**
     * If a result is sent, and there is an error, then handle handle the error.
     *
     * @param consumer executed if result has error.
     * @throws NullPointerException if result is present and {@code consumer} is
     *                              null
     * @return this, fluent API
     */
    Result<T> catchError(Consumer<Throwable> consumer);

    /**
     * @return true if result is sent successfully.
     */
    boolean success();

    /**
     * @return true if result is sent and this is the last result.
     */
    boolean complete();


    /**
     * If failure is true then cause will not be null.
     *
     * @return true if result is sent and result outcome is a failure.
     */
    boolean failure();

    /**
     * If failure is true, the cause will not be null.
     *
     * @return cause of error associated with the result
     */
    Throwable cause();


    /**
     * If the value of the result can be null, it is better to use Ref which is like Optional.
     *
     * @return value associated with a successful result.
     */
    Ref<T> getRef();

    /**
     * Raw value of the result.
     * You should not use this if the result could be null, use getRef instead.
     *
     * @return raw value associated with the result.
     */
    T get();
}
