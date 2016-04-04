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
     */
    Result<T> then(Consumer<T> consumer);


    /**
     * If a result is sent, and there was no error, then handle the result as a value which could be null.
     *
     * @param consumer executed if result has no error.
     * @throws NullPointerException if result is present and {@code consumer} is
     *                              null
     */
    Result<T> thenValue(Consumer<Value<T>> consumer);


    /**
     * If a result is sent, and there is an error, then handle handle the error.
     *
     * @param consumer executed if result has error.
     * @throws NullPointerException if result is present and {@code consumer} is
     *                              null
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
     * Cancel any more results being sent, this is used with streams not a scalar result.
     * A stream is a source that can return more than one result.
     */
    void cancel();

    /**
     * If the value of the result can be null, it is better to use Value which is like Optional.
     *
     * @return value associated with a successful result.
     */
    Value<T> getValue();

    /**
     * Raw value of the result.
     * You should not use this if the result could be null, use getValue instead.
     *
     * @return raw value associated with the result.
     */
    T get();
}
