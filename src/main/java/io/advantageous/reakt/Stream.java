package io.advantageous.reakt;

import io.advantageous.reakt.impl.StreamResultImpl;

import java.util.function.Consumer;


/**
 * A generic event handler for N results, i.e., a stream of results.
 * <p>
 * This is a like a type of {@link Callback} for streaming results.
 * While {@code Callback} can be considered for scalar results, a
 * {@code Stream} is more appropriate for non-scalar results, i.e., {@code Stream#onNext}
 * will get called many times.
 * <p>
 * which can be thought of as a callback handler.
 * <p>
 * This is like an async future or promise.
 *
 * @param <T> type of result returned from callback
 */
public interface Stream<T> {


    /**
     * (Client view)
     * A result was returned so handle it.
     * <p>
     * This is registered from the callers (or event receivers perspective).
     * A client of a service would override {@code onResult}.
     *
     * @param result to handle
     */
    void onNext(StreamResult<T> result);


    /**
     * (Service view)
     * This allows services to send back a last result easily to the client/handler.
     * <p>
     * This is a helper methods for producers (services that produce results) to send a result.
     *
     * @param result result value to send.
     */
    default void complete(final T result) {
        onNext(new StreamResultImpl<>(result, true, Ref.empty(), Ref.empty()));
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
        onNext(new StreamResultImpl<>(result, false,
                Ref.empty(), Ref.empty()));
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
        onNext(new StreamResultImpl<>(result, done, Ref.empty(),
                Ref.empty()));
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
     * @param cancelHandler cancel handler if you support canceling.
     */
    default void reply(final T result, final boolean done, final Runnable cancelHandler) {
        onNext(new StreamResultImpl<>(result, done, Ref.of(cancelHandler),
                Ref.empty()));
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
     * @param cancelHandler cancel handler if you support canceling the stream
     * @param wantsMore handler so client can request more items if this is supported.
     */
    default void reply(final T result, final boolean done, final Runnable cancelHandler, final Consumer<Long> wantsMore) {
        onNext(new StreamResultImpl<>(result, done, Ref.of(cancelHandler),
                Ref.of(wantsMore)));
    }

    /**
     * (Service view)
     * This allows services to send back a failed result easily to the client/handler.
     * <p>
     * This is a helper methods for producers (services that produce results) to send a failed result.
     *
     * @param error error
     */
    default void fail(final Throwable error) {

        onNext(new StreamResultImpl<>(error, true, Ref.empty(), Ref.empty()));
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
        onNext(new StreamResultImpl<>(new IllegalStateException(errorMessage),
                true, Ref.empty(), Ref.empty()));

    }

}
