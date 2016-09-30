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

package io.advantageous.reakt.promise;

import io.advantageous.reakt.CallbackHandler;
import io.advantageous.reakt.Expected;
import io.advantageous.reakt.Invokable;
import io.advantageous.reakt.Result;
import io.advantageous.reakt.promise.impl.BasePromise;
import io.advantageous.reakt.reactor.Reactor;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This combines a Result and callback handler to simplify implementation of a Promise.
 * A promise is like a non-blocking {@code Future}({@link java.util.concurrent.Future}).
 * You get notified of changes instead of having to call {@code get}.
 * <p>
 * A promise is both a {@code CallbackHandler} ({@link CallbackHandler}),
 * and a {@code Result} {@link io.advantageous.reakt.Result}.
 * </p>
 * <p>
 * A promise is a sort of deferred value.
 *
 * @param <T> value of result.
 * @author Rick Hightower
 * @author Geoff Chandler
 */
public interface PromiseHandler<T> extends CallbackHandler<T>, Result<T>, Promise<T> {

    /**
     * Creates an immutable promise.
     *
     * @return final promise
     */
    default PromiseHandler<T> freeze() {
        return BasePromise.provideFinalPromise(this);
    }

    /**
     * If a result is sent, and there was no error, then handle the result.
     * <p>
     * There is only one {@code then} Handler.
     * </p>
     * Unlike ES6, {@code then(..)} cannot be chained per se, but {@code whenComplete(..)}, and
     * {@code }thenMap(...)} can be nested.
     *
     * @param consumer executed if result has no error.
     * @return this, fluent API
     * @throws NullPointerException if result is present and {@code consumer} is null
     */
    PromiseHandler<T> then(Consumer<T> consumer);


    /**
     * Notified of completeness.
     * <p>
     * If you want N handlers for when the promise gets called back use whenComplete instead of
     * {@code then} or {@code thenExpect}.
     * <p>
     * There can be many {@code whenComplete} handlers.
     * <p>
     * This does not create a new promise.
     *
     * @param doneListener doneListener
     * @return this, fluent API
     */
    PromiseHandler<T> whenComplete(Consumer<PromiseHandler<T>> doneListener);

    /**
     * If a result is sent, and there was no error, then handle the result as a value which could be null.
     * <p>
     * There is only one thenExpect handler per promise.
     * <p>
     * Unlike ES6, {@code thenExpect(..)} cannot be chained per se as it does not create a new promise,
     * but {@code whenComplete(..)}, and {@code }thenMap(...)} can be chained.
     * <p>
     * This does not create a new promise.
     *
     * @param consumer executed if result has no error.
     * @return this, fluent API
     * @throws NullPointerException if result is present and {@code consumer} is
     *                              null
     */
    PromiseHandler<T> thenExpect(Consumer<Expected<T>> consumer);


    /**
     * This method can be chained, and it creates a new promise, which can be a different type.
     *
     * @param mapper mapper function
     * @param <U>    new type for new promise
     * @return a promise that uses mapper function to map old promise result to new result.
     */
    <U> PromiseHandler<U> thenMap(Function<? super T, ? extends U> mapper);

    /**
     * If a result is sent, and there is an error, then handle handle the error.
     *
     * @param consumer executed if result has error.
     * @return this, fluent API
     * @throws NullPointerException if result is present and {@code consumer} is null
     */
    PromiseHandler<T> catchError(Consumer<Throwable> consumer);

    /**
     * Returns true if this PromiseHandler is an Invokable PromiseHandler.
     * <p>
     * Hint: if you have to ask, the answer is no.
     *
     * @return true if it is invokable
     */
    default boolean isInvokable() {
        return this instanceof Invokable;
    }

    /**
     * Allows promises returned from, for example, proxy stubs for services methods to invoke the operation.
     * <p>
     * This allows use to set up the catchError and then before the method is async invoked.
     * <p>
     * Example Remote Proxy Gen to support returning Reakt invokeable promise
     * <pre>
     * <code>
     *     employeeService.lookupEmployee("123")
     *           .then((employee)-&gt; {...})
     *           .catchError(...)
     *           .invoke();
     * </code>
     * </pre>
     *
     * @return this, fluent
     */
    default PromiseHandler<T> invoke() {
        throw new UnsupportedOperationException("This is not an invokable promise.");
    }


    /**
     * Allows you to pass an existing promise as a handler.
     *
     * @param promise promise
     * @return this, fluent
     */
    default PromiseHandler<T> thenPromise(Promise<T> promise) {
        thenCallback((CallbackHandler)promise);
        return this;
    }


    /**
     * Allows you to pass an existing callback as a handler.
     *
     * @param callback callback
     * @return this, fluent
     */
    default PromiseHandler<T> thenCallback(CallbackHandler<T> callback) {
        this.catchError(callback::reject).then(callback::resolve);
        return this;
    }

    /**
     * Allows you to pass an existing promise as a handler.
     *
     * @param promise promise
     * @return this, fluent
     */
    default PromiseHandler<T> invokeWithPromise(PromiseHandler<T> promise) {
        thenPromise(promise).invoke();
        return this;
    }

    /**
     * Use this to run the promise in replay mode on a reactor.
     * Allows a promise to be invoked with a reactor
     * @param reactor reactor to use
     * @return new PromiseHandler that wraps the promise. New promise is associated with the reactor.
     */
    PromiseHandler<T> invokeWithReactor(Reactor reactor);


    /**
     * Use this to run the promise in replay mode on a reactor.
     * Allows a promise to be invoked with a reactor
     * @param reactor reactor to use
     * @param timeout if the promise does not return in the allotted time, the reactor will time it out.
     * @return new PromiseHandler that wraps the promise. New promise is associated with the reactor.
     */
    PromiseHandler<T> invokeWithReactor(Reactor reactor, Duration timeout);

    /**
     * If the thenSafeExpect handler throws an exception, this will report it as if it it was caught by catchError.
     * <p>
     * This is convenient if you are running your handler with an async lib that is not reporting or catching
     * exceptions as your code is running on their threads.
     * <p>
     * If a result is sent, and there was no error, then handle the result as a value which could be null.
     * <p>
     * There is only one thenSafeExpect or thenExpect handler per promise.
     * Once then is called all other then* handlers are safe.
     * <p>
     * Unlike ES6, {@code thenExpect(..)} cannot be chained per se as it does not create a new promise,
     * but {@code whenComplete(..)}, and {@code }thenMap(...)} can be chained.
     * <p>
     * This does not create a new promise.
     *
     * @param consumer executed if result has no error.
     * @return this, fluent API
     * @throws NullPointerException if result is present and {@code consumer} is
     *                              null
     */
    default PromiseHandler<T> thenSafeExpect(Consumer<Expected<T>> consumer) {
        throw new UnsupportedOperationException("PromiseHandler provider does not support thenSafeExpect");
    }


    /**
     * If the {@code then} handler throws an exception, this will report the exception as if it were caught
     * by {@code catchError}.
     * <p>
     * This is convenient if you are running your handler with an async lib that is not reporting or catching
     * exceptions as your code is running on their threads.
     * <p>
     * If a result is sent, and there was no error, then handle the result as a value which could be null.
     * <p>
     * There is only one thenSafe or then handler per promise.
     * Once then is called all other then* handlers are safe.
     * <p>
     * Unlike ES6, {@code thenExpect(..)} cannot be chained per se as it does not create a new promise,
     * but {@code whenComplete(..)}, and {@code }thenMap(...)} can be chained.
     * <p>
     * This does not create a new promise.
     *
     * @param consumer executed if result has no error.
     * @return this, fluent API
     * @throws NullPointerException if result is present and {@code consumer} is
     *                              null
     */
    default PromiseHandler<T> thenSafe(Consumer<T> consumer) {
        throw new UnsupportedOperationException("PromiseHandler provider does not support thenSafeExpect");
    }

    /**
     * Denotes if the promise provider supports thenSafe and thenSafeExpect.
     *
     * @return true if safe operations are supported.
     */
    default boolean supportsSafe() {
        return false;
    }

    /**
     * Used for testing and legacy integration.
     * This turns an async promise into a blocking promise.
     * @return blocking promise
     */
    default PromiseHandler<T> invokeAsBlockingPromise() {
        PromiseHandler<T> blockingPromise = (PromiseHandler<T>)Promises.blockingPromise();
        this.invokeWithPromise(blockingPromise);
        return blockingPromise;
    }


    /**
     * Used for testing and legacy integration.
     * This turns an async promise into a blocking promise.
     * @param duration duration to wait for call
     * @return blocking promise
     */
    default PromiseHandler<T> invokeAsBlockingPromise(Duration duration) {
        PromiseHandler<T> blockingPromise = (PromiseHandler<T>) Promises.blockingPromise(duration);
        this.invokeWithPromise(blockingPromise);
        return blockingPromise;
    }


    /**
     * Used for testing and legacy integration.
     * This turns an async promise into a blocking promise and then does a get operations.
     * @param duration duration to wait for call
     * @return result of call, blocks until return comes back.
     */
    default T blockingGet(Duration duration) {
        return invokeAsBlockingPromise(duration).get();
    }

    /**
     * Used for testing and legacy integration.
     * This turns an async promise into a blocking promise and then does a get operations.
     * @return result of call, blocks until return comes back.
     */
    default T blockingGet() {
        return invokeAsBlockingPromise().get();
    }


}
