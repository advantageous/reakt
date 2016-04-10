package io.advantageous.reakt.promise.impl;

import io.advantageous.reakt.promise.Promise;

public class AllBlockingPromise extends BlockingPromise<Void> implements Promise<Void> {
    public AllBlockingPromise(Promise<?>... promises) {
        PromiseUtil.all(this, (Promise[]) promises);
    }

}