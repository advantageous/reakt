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
import io.advantageous.reakt.StreamResult;

import java.util.function.Consumer;

/**
 * The result of an async operations with optional callbacks for cancel and request more.
 *
 * @param <T> Type of result.
 * @author Rick Hightower
 */
public class StreamResultImpl<T> extends ResultImpl<T> implements StreamResult<T> {
    private final boolean done;
    private final Expected<Runnable> cancelCallback;
    private final Expected<Consumer<Long>> requestMore;

    public StreamResultImpl(final Object object,
                            final boolean done,
                            final Expected<Runnable> cancelCallback,
                            final Expected<Consumer<Long>> requestMore) {
        super(object);
        this.done = done;
        this.cancelCallback = cancelCallback;
        this.requestMore = requestMore;
    }

    @Override
    public boolean complete() {
        return done;
    }

    @Override
    public void cancel() {
        cancelCallback.ifPresent(Runnable::run);
    }

    @Override
    public void request(long n) {
        requestMore.ifPresent(longConsumer -> longConsumer.accept(n));
    }
}

