package io.advantageous.reakt;

import io.advantageous.reakt.impl.StreamResult;


/**
 * A generic event handler for N results.
 * This is a type of Callback for streaming results.
 * While Callback can be considered for scalar results.
 * {@code Stream} is more appropriate for non-scalar results, i.e., {@code Result#onResult}
 * will get called many times.
 * <p>
 * which can be thought of as a callback handler.
 * <p>
 * This is like an async future or promise.
 *
 * @param <T> type of result returned from callback
 */
public interface Stream<T> extends Callback<T> {


    /**
     * (Service view)
     * This allows services to send back a last result easily to the client/handler.
     * <p>
     * This is a helper methods for producers (services that produce results) to send a result.
     *
     * @param result result value to send.
     */
    default void complete(final T result) {
        onResult(new StreamResult<>(result, true));
    }

    /**
     * (Service view)
     * This allows services to send back a next result easily to the client/handler.
     * <p>
     * This is a helper methods for producers (services that produce results) to send a result.
     *
     * @param result result value to send.
     */
    default void reply(final T result) {
        onResult(new StreamResult<>(result, false));
    }


    /**
     * (Service view)
     * This allows services to send back a next result easily to the client/handler
     * and pass done flag to denote completeness.
     * <p>
     * This is a helper methods for producers (services that produce results) to send a result.
     *
     * @param result result value to send.
     * @param done   if true signifies that that this is the last result.
     */
    default void reply(final T result, final boolean done) {
        onResult(new StreamResult<>(result, done));
    }
}
