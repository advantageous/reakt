package io.advantageous.reakt;

import io.advantageous.reakt.impl.ResultImpl;

import java.util.function.Consumer;

import static io.advantageous.reakt.Result.doneResult;

/**
 * A generic event handler which can be thought of as a callback handler.
 * <p>
 * This is like an async future or promise.
 * <p>
 * This was modeled after QBit's callback, and JavaScripts callbacks.
 * The {@link Result} result represents the result or error from an async operation.
 * <p>
 * A {@code Callback} is a {@code Consumer} and can be used anywhere a consumer is used.
 * This is for easy integration with non-Reakt libs and code bases.
 * <p>
 *
 * @param <T> type of result returned from callback
 */
public interface Callback<T> extends Consumer<T> {

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
     * Calls replayDone, for VOID callback only. ES6 promise style.
     */
    @SuppressWarnings("unused")
    default void resolve() {
        replyDone();
    }

    /**
     * Resolve resolves a promise.
     *
     * @param result makes it more compatible with ES6 style promises
     */
    @SuppressWarnings("unused")
    default void resolve(final T result) {
        reply(result);
    }

    /**
     * Bridge between Consumer world and Callback world
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    @Override
    default void accept(T t) {
        reply(t);
    }

    /**
     * Used to convert the error handling of the callback or promise
     * into a Consumer so you can easily integrate with non-Reakt code.
     *
     * @return Consumer version of error handling.
     */
    default Consumer<Throwable> errorConsumer() {
        return this::reject;
    }

    /**
     * Used to easily cast this callback to a consumer.
     *
     * @return Consumer version of this callback.
     */
    default Consumer<T> consumer() {
        return this;
    }


}
