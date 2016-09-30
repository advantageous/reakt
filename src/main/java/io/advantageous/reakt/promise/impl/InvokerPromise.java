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

package io.advantageous.reakt.promise.impl;

import io.advantageous.reakt.Callback;
import io.advantageous.reakt.Invokable;
import io.advantageous.reakt.promise.PromiseHandler;

import java.util.function.Consumer;

public class InvokerPromise<T> extends BasePromise<T> implements Invokable {

    private final Consumer<Callback<T>> consumer;
    private boolean invoked;

    public InvokerPromise(Consumer<Callback<T>> consumer) {
        this.consumer = consumer;
    }

    @Override
    public PromiseHandler<T> invoke() {
        if (invoked) {
            throw new IllegalStateException("PromiseHandler can only be invoked once");
        }
        invoked = true;
        consumer.accept(this);
        return this;
    }

    @Override
    public boolean isInvokable() {
        return true;
    }
}
