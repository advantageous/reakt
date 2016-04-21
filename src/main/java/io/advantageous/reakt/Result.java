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

import io.advantageous.reakt.impl.ResultImpl;

import java.util.function.Consumer;

/**
 * The result of an async operation.
 * <p>
 * This was modeled after Vert.x AsyncResult and after the types of results one would deal with in JavaScript.
 *
 * @param <T> type of value expected in the result.
 * @author Rick Hightower
 * @author Geoff Chandler
 */
public interface Result<T> {

    /**
     * DONE Result for Callback Void.
     */
    Result<Void> DONE = new ResultImpl<>(null);

    /**
     * Create a result.
     *
     * @param value value
     * @param <T>   T
     * @return result
     */
    static <T> Result<T> result(T value) {
        return new ResultImpl<>(value);
    }

    /**
     * Create a result
     *
     * @param error error
     * @param <T>   T
     * @return result
     */
    static <T> Result<T> error(Throwable error) {
        return new ResultImpl<>(error);
    }

    /**
     * Done results
     *
     * @return returns constant which is a result that is done.
     */
    static Result<Void> doneResult() {
        return DONE;
    }

    /**
     * If a result is sent, and there was no error, then handle the result.
     *
     * @param consumer executed if result has no error.
     * @return this, fluent API
     * @throws NullPointerException if result is present and {@code consumer} is
     *                              null
     */
    Result<T> then(Consumer<T> consumer);

    /**
     * If a result is sent, and there was no error, then handle the result as a value which could be null.
     *
     * @param consumer executed if result has no error.
     * @return this, fluent API
     * @throws NullPointerException if result is present and {@code consumer} is
     *                              null
     */
    Result<T> thenExpect(Consumer<Expected<T>> consumer);

    /**
     * If a result is sent, and there is an error, then handle handle the error.
     *
     * @param consumer executed if result has error.
     * @return this, fluent API
     * @throws NullPointerException if result is present and {@code consumer} is
     *                              null
     */
    Result<T> catchError(Consumer<Throwable> consumer);

    /**
     * @return true if result is sent successfully.
     */
    boolean success();

    /**
     * @return true if result is sent and this is the last result.
     */
    boolean complete();

    /**
     * If failure is true then cause will not be null.
     *
     * @return true if result is sent and result outcome is a failure.
     */
    boolean failure();

    /**
     * If failure is true, the cause will not be null.
     *
     * @return cause of error associated with the result
     */
    Throwable cause();

    /**
     * If the value of the result can be null, it is better to use Expected which is like Optional.
     *
     * @return value associated with a successful result.
     */
    Expected<T> expect();

    /**
     * Raw value of the result.
     * You should not use this if the result could be null, use expect instead.
     *
     * @return raw value associated with the result.
     */
    T get();

    /**
     * Return the value if no error.  If there was ane error return {@code other}.
     *
     * @param other value which is returned if no there was an error.
     * @return the value, if no error, or if error return {@code other}
     */
    T orElse(T other);

}
