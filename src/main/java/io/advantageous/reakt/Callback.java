package io.advantageous.reakt;

import io.advantageous.reakt.impl.ResultImpl;

import static io.advantageous.reakt.Result.doneResult;

/**
 * A generic event handler which can be thought of as a callback handler.
 * <p>
 * This is like an async future or promise.
 * <p>
 * This was modeled after QBit's callback, and JavaScripts callbacks.
 * The {@link Result} result represents the result or error from an async operation.
 *
 * @param <T> type of result returned from callback
 */
public interface Callback<T> {

    /**
     * (Client view)
     * A result was returned so handle it.
     * <p>
     * This is registered from the callers (or event receivers perspective).
     * A client of a service would override {@code onResult}.
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
    default void reject(final Throwable error) {
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
    default void reject(final String errorMessage) {
        onResult(new ResultImpl<>(new IllegalStateException(errorMessage)));
    }


    /**
     * (Service view)
     * This allows services to send back a failed result easily to the client/handler.
     * <p>
     * This is a helper methods for producers (services that produce results) to send a failed result.
     *
     * @param errorMessage error message
     * @param error        exception
     */
    default void reject(final String errorMessage, final Throwable error) {
        reject(new IllegalStateException(errorMessage, error));
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


    /**
     * Reply that you are done.
     * You can only use this for Callback of type Void only.
     */
    @SuppressWarnings("all")
    default void replyDone() {
        onResult((Result<T>) doneResult());
    }

    /**
     * Resolve resolves a promise.
     *
     * @param result makes it more compatible with ES6 style promises
     */
    default void resolve(final T result) {
        reply(result);
    }

}
