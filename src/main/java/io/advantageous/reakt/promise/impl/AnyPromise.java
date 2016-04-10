package io.advantageous.reakt.promise.impl;

import io.advantageous.reakt.promise.Promise;

public class AnyPromise extends BasePromise<Void> implements Promise<Void> {
    public AnyPromise(Promise<?>... promises) {
        PromiseUtil.any(this, (Promise[]) promises);
    }
}
