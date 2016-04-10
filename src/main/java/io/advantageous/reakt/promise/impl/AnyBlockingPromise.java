package io.advantageous.reakt.promise.impl;

import io.advantageous.reakt.promise.Promise;

public class AnyBlockingPromise extends BlockingPromise<Void> implements Promise<Void> {
    public AnyBlockingPromise(Promise<?>... promises) {
        PromiseUtil.any(this, (Promise[]) promises);
    }
}
