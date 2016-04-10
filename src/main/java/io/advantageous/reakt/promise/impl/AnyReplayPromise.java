package io.advantageous.reakt.promise.impl;

import io.advantageous.reakt.promise.Promise;

import java.time.Duration;

public class AnyReplayPromise extends ReplayPromiseImpl<Void> implements Promise<Void> {
    public AnyReplayPromise(final Duration timeout, final long startTime, Promise<?>... promises) {
        super(timeout, startTime);
        PromiseUtil.any(this, (Promise[])promises);
    }
}
