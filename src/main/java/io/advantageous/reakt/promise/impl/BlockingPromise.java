package io.advantageous.reakt.promise.impl;

import io.advantageous.reakt.Result;
import io.advantageous.reakt.Value;

import java.util.concurrent.CountDownLatch;

/**
 * This is very much like a Java Future. It is blocking.
 * This is useful for testing and for legacy integration.
 *
 * @param <T> value of result.
 */
public class BlockingPromise<T> extends PromiseImpl<T> {

    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    @Override
    public void onResult(Result<T> result) {
        countDownLatch.countDown();
        super.onResult(result);
    }

    @Override
    public Value<T> getValue() {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        return super.getValue();
    }

    @Override
    public T get() {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
        return super.get();
    }
}
