package io.advantageous.reakt.promise.impl;

import io.advantageous.reakt.Ref;
import io.advantageous.reakt.Result;
import io.advantageous.reakt.promise.Promise;
import io.advantageous.reakt.promise.Promises;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * This is very much like a Java Future. It is blocking.
 * This is useful for testing and for legacy integration.
 *
 * @param <T> value of result.
 */
public class BlockingPromise<T> extends BasePromise<T> {

    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private final Ref<Duration> duration;

    public BlockingPromise() {
        this.duration = Ref.empty();
    }


    public BlockingPromise(final Duration duration) {
        this.duration = Ref.of(duration);
    }

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


    private void doAwait(final Callable<Void> runnable) {
        try {
            runnable.call();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    protected boolean _success() {
        return super.success();
    }


    private void await() {
        duration.ifPresent(duration1 -> {
            doAwait(() -> {
                countDownLatch.await(duration1.toMillis(), MILLISECONDS);
                return null;
            });
        }).ifEmpty(() -> {
            doAwait(() -> {
                countDownLatch.await();
                return null;
            });
        });
    }

    @Override
    public <U> Promise<U> thenMap(Function<? super T, ? extends U> mapper) {
        final Promise<U> mappedPromise = Promises.promise();
        this.whenComplete(p -> {
            final BlockingPromise<T> promise = (BlockingPromise) p;
            if (promise._success()) {
                final T t = promise.result.get().get();
                final U mapped = mapper.apply(t);
                mappedPromise.reply(mapped);
            } else {
                mappedPromise.fail(promise.cause());
            }
        });
        return mappedPromise;
    }

}
