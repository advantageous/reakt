package io.advantageous.reakt;

import io.advantageous.reakt.impl.ResultImpl;

/**
 *
 * A generic event handler which can be thought of as a callback handler.
 *
 * This is like an async future or promise.
 *
 * @param <T> type of result returned from callback
 */
public interface Callback<T> {



    /**
     * A result was returned so so handle it.
     *
     * This is registered from the callers (or event receivers perspective).
     * @param result to handle
     */
    void onResult(Result<T> result);


    default void failWith(Throwable error) {
        onResult(new ResultImpl<>(error));
    }


    default void failWith(String errorMessage) {
        onResult(new ResultImpl<>(new IllegalStateException(errorMessage)));
    }


    default void reply(T result) {
        onResult(new ResultImpl<>(result));
    }


}
