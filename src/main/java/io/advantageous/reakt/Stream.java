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
 * @author Rick Hightower
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
    void onNext(final StreamResult<T> result);

    /**
     * (Service view)
     * This allows services to send back a last result easily to the client/handler.
     * <p>
     * This is a helper methods for producers (services that produce results) to send a result.
     *
     * @param result result value to send.
     */
    default void complete(final T result) {
        this.onNext(new StreamResultImpl<>(result, true, Expected.empty(), Expected.empty()));
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
        this.onNext(new StreamResultImpl<>(result, false, Expected.empty(), Expected.empty()));
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
        this.onNext(new StreamResultImpl<>(result, done, Expected.empty(), Expected.empty()));
    }

    /**
     * (Service view)
     * This allows services to send back a next result easily to the client/handler
     * and pass done flag to denote completeness.
     * <p>
     * This is a helper methods for producers (services that produce results) to send a result.
     *
     * @param result        result value to send.
     * @param done          if true signifies that that this is the last result.
     * @param cancelHandler cancel handler if you support canceling.
     */
    default void reply(final T result, final boolean done, final Runnable cancelHandler) {
        this.onNext(new StreamResultImpl<>(result, done, Expected.of(cancelHandler), Expected.empty()));
    }

    /**
     * (Service view)
     * This allows services to send back a next result easily to the client/handler
     * and pass done flag to denote completeness.
     * <p>
     * This is a helper methods for producers (services that produce results) to send a result.
     *
     * @param result        result value to send.
     * @param done          if true signifies that that this is the last result.
     * @param cancelHandler cancel handler if you support canceling the stream
     * @param wantsMore     handler so client can request more items if this is supported.
     */
    default void reply(final T result, final boolean done,
                       final Runnable cancelHandler,
                       final Consumer<Long> wantsMore) {

        this.onNext(new StreamResultImpl<>(result, done, Expected.of(cancelHandler), Expected.of(wantsMore)));
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
        this.onNext(new StreamResultImpl<>(error, true, Expected.empty(), Expected.empty()));
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
        this.onNext(new StreamResultImpl<>(
                new IllegalStateException(errorMessage), true, Expected.empty(), Expected.empty()));
    }
}
