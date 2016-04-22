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

package io.advantageous.reakt.impl;

import io.advantageous.reakt.Expected;
import io.advantageous.reakt.Result;
import io.advantageous.reakt.exception.ResultFailedException;

import java.util.function.Consumer;

/**
 * The result of an async operation.
 * <p>
 * This was modeled after Vert.x AsyncResult and after the types of results one would deal with in JavaScript.
 *
 * @param <T> type of value expected in the result.
 * @author Rick Hightower
 */
public class ResultImpl<T> implements Result<T> {

    private final Object object;

    public ResultImpl(final Object object) {
        this.object = object;
    }

    @Override
    public Result<T> thenExpect(final Consumer<Expected<T>> consumer) {
        if (success()) consumer.accept(expect());
        return this;
    }

    @Override
    public Result<T> then(final Consumer<T> consumer) {
        if (success()) consumer.accept(get());
        return this;
    }

    @Override
    public Result<T> catchError(final Consumer<Throwable> handler) {
        if (failure()) handler.accept(cause());
        return this;
    }

    public boolean success() {
        return !(this.object instanceof Throwable);
    }

    @Override
    public boolean complete() {
        return true;
    }

    public boolean failure() {
        return this.object instanceof Throwable;
    }

    @Override
    public Throwable cause() {
        return this.object instanceof Throwable ? (Throwable) this.object : null;
    }

    @SuppressWarnings("unchecked")
    public Expected<T> expect() {
        if (failure()) throw new IllegalStateException(cause());
        return Expected.ofNullable((T) this.object);
    }

    @SuppressWarnings("unchecked")
    public T get() {
        if (failure()) {
            if (cause() instanceof RuntimeException) {
                throw (RuntimeException) cause();
            } else {
                throw new ResultFailedException(cause());
            }
        }
        return (T) this.object;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T orElse(final T other) {
        return success() ? (T) this.object : other;
    }

}
