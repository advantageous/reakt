package io.advantageous.reakt;

import io.advantageous.reakt.impl.ResultImpl;

/**
 * A generic event handler which can be thought of as a callback handler.
 * <p>
 * This is like an async future or promise.
 *
 * @param <T> type of result returned from callback
 */
public interface Callback<T> {

    /**
     * (Client view)
     * A result was returned so so handle it.
     * <p>
     * This is registered from the callers (or event receivers perspective).
     * A client of a service would override onResult.
     *
     * @param result to handle
     */
    void onResult(Result<T> result);


    /**
     * (Service view)
     * This allows services to send back a failed result easily to the client/handler.
     * <p>
     * This is a helper methods for producers (services that produce results) to send a failed result.
     *
     * @param error error
     */
    default void fail(final Throwable error) {
        onResult(new ResultImpl<>(error));
    }


    /**
     * (Service view)
     * This allows services to send back a failed result easily to the client/handler.
     * <p>
     * This is a helper methods for producers (services that produce results) to send a failed result.
     *
     * @param errorMessage error message
     */
    default void fail(final String errorMessage) {
        onResult(new ResultImpl<>(new IllegalStateException(errorMessage)));
    }


    /**
     * (Service view)
     * This allows services to send back a result easily to the client/handler.
     * <p>
     * This is a helper methods for producers (services that produce results) to send a result.
     *
     * @param result result value to send.
     */
    default void reply(final T result) {
        onResult(new ResultImpl<>(result));
    }

}
