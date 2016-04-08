package io.advantageous.reakt.promise.impl;

import io.advantageous.reakt.promise.Promise;

public class AllPromise extends BasePromise<Void> implements Promise<Void> {
    public AllPromise(Promise<?>... promises) {
        PromiseUtil.all(this, promises);
    }
}
