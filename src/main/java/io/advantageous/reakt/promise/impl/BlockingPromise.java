package io.advantageous.reakt.promise.impl;

import io.advantageous.reakt.Ref;
import io.advantageous.reakt.Result;

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
        super.onResult(result);
        countDownLatch.countDown();
    }

    @Override
    public Ref<T> getRef() {
        await();
        return super.getRef();
    }

    @Override
    public T get() {
        await();
        return super.get();
    }

    @Override
    public Throwable cause() {
        await();
        return super.cause();
    }

    @Override
    public boolean failure() {
        await();
        return super.failure();
    }

    @Override
    public boolean success() {
        await();
        return super.success();
    }

    private void await() {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

}
