/*
 *
 *  Copyright (c) 2016. Rick Hightower, Geoff Chandler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    		http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.advantageous.reakt;

import io.advantageous.reakt.exception.RejectedPromiseException;
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
 * A {@code CallbackHandler} is a {@code Consumer} and can be used anywhere a consumer is used.
 * This is for easy integration with non-Reakt libs and code bases.
 * <p>
 *
 * @param <T> type of result returned from callback
 * @author Rick Hightower
 * @author Geoff Chandler
 */
public interface CallbackHandler<T> extends Consumer<T>, Callback<T> {

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
        reject(new RejectedPromiseException(errorMessage));
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
        reject(new RejectedPromiseException(errorMessage, error));
    }




    /**
     * Reply that you are done.
     * You can only use this for CallbackHandler of type Void only.
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
        onResult(new ResultImpl<>(result));
    }

    /**
     * Bridge between Consumer world and CallbackHandler world
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    @Override
    default void accept(T t) {
        resolve(t);
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
