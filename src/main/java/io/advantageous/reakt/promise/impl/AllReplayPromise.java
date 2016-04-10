package io.advantageous.reakt.promise.impl;

import io.advantageous.reakt.promise.Promise;

import java.time.Duration;

public class AllReplayPromise extends ReplayPromiseImpl<Void> implements Promise<Void> {
    public AllReplayPromise(final Duration timeout, final long startTime, Promise<?>... promises) {
        super(timeout, startTime);
        PromiseUtil.all(this, (Promise[])promises);
    }
}