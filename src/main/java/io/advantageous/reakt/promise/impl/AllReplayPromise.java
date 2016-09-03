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

import io.advantageous.reakt.Invokable;
import io.advantageous.reakt.promise.Promise;
import io.advantageous.reakt.reactor.Reactor;

import java.time.Duration;

public class AllReplayPromise extends ReplayPromiseImpl<Void> implements Promise<Void>, Invokable {


    private final Promise<?>[] promises;
    private boolean invoked;

    public AllReplayPromise(final Duration timeout, final long startTime, Promise<?>... promises) {
        super(timeout, startTime);
        PromiseUtil.all(this, (Promise[]) promises);
        this.promises = promises;
    }


    @Override
    public Promise<Void> invokeWithReactor(final Reactor reactor) {
        if (invoked) {
            throw new IllegalStateException("Promise can only be invoked once");
        }
        invoked = true;
        for (Promise<?> promise : promises) {
            if (!promise.isInvokable()) {
                throw new IllegalStateException("AllReplayPromise can only be invoked if all children are invokeable");
            }
            promise.invoke();
        }
        return this;
    }

    @Override
    public boolean isInvokable() {
        return true;
    }
}