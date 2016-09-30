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
import io.advantageous.reakt.promise.PromiseHandler;

public class AnyPromise extends BasePromise<Void> implements PromiseHandler<Void>, Invokable {

    private final Promise<?>[] promises;
    private boolean invoked;

    public AnyPromise(Promise<?>... promises) {
        this.promises = promises;
        PromiseUtil.any((Promise) this, (Promise<Void>[]) promises);
    }


    @Override
    public void invoke() {
        if (invoked) {
            throw new IllegalStateException("PromiseHandler can only be invoked once");
        }
        invoked = true;
        for (Promise<?> promise : promises) {
            if (!promise.asHandler().isInvokable()) {
                throw new IllegalStateException("AnyPromise can only be invoked if all children are invokeable");
            }
            promise.invoke();
        }
    }

    @Override
    public boolean isInvokable() {
        return true;
    }
}
