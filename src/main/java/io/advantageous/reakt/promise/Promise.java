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

import io.advantageous.reakt.Callback;
import io.advantageous.reakt.Expected;
import io.advantageous.reakt.Invokable;
import io.advantageous.reakt.Result;
import io.advantageous.reakt.promise.impl.BasePromise;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A promise is like a non-blocking {@code Future}({@link java.util.concurrent.Future}).
 * You get notified of changes instead of having to call {@code get}.
 * <p>
 * A promise is both a {@code Callback} ({@link io.advantageous.reakt.Callback}),
 * and a {@code Result} {@link io.advantageous.reakt.Result}.
 * </p>
 * <p>
 * A promise is a sort of deferred value.
 *
 * @param <T> value of result.
 * @author Rick Hightower
 * @author Geoff Chandler
 */
public interface Promise<T> extends Callback<T>, Result<T> {

    /**
     * Creates an immutable promise.
     *
     * @return final promise
     */
    default Promise<T> freeze() {
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
    Promise<T> then(Consumer<T> consumer);

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
    Promise<T> whenComplete(Consumer<Promise<T>> doneListener);

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
    Promise<T> thenExpect(Consumer<Expected<T>> consumer);


    /**
     * This method can be chained, and it creates a new promise, which can be a different type.
     *
     * @param mapper mapper function
     * @param <U>    new type for new promise
     * @return a promise that uses mapper function to map old promise result to new result.
     */
    <U> Promise<U> thenMap(Function<? super T, ? extends U> mapper);

    /**
     * If a result is sent, and there is an error, then handle handle the error.
     *
     * @param consumer executed if result has error.
     * @return this, fluent API
     * @throws NullPointerException if result is present and {@code consumer} is null
     */
    Promise<T> catchError(Consumer<Throwable> consumer);

    /**
     * Returns true if this Promise is an Invokable Promise.
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
     */
    default void invoke() {
        throw new UnsupportedOperationException("This is not an invokable promise.");
    }

}
