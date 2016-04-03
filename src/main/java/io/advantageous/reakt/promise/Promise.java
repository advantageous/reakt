package io.advantageous.reakt.promise;

import io.advantageous.reakt.Callback;
import io.advantageous.reakt.Result;
import io.advantageous.reakt.promise.impl.BlockingPromise;
import io.advantageous.reakt.promise.impl.ReplayPromiseImpl;
import io.advantageous.reakt.promise.impl.PromiseImpl;

import java.time.Duration;

/**
 * A promise is like a non-blocking {@code Future}({@link java.util.concurrent.Future}).
 * You get notified of changes instead of having to call {@code get}.
 *
 * A promise is both a {@code Callback} ({@link io.advantageous.reakt.Callback}),
 * and a {@code Result} {@link io.advantageous.reakt.Result}.
 *
 *
 * A promise is a sort of deferred value.
 * @param <T> value of result.
 */
public interface Promise<T> extends Callback<T>, Result<T> {

    static <T> Promise<T> promise() {
        return new PromiseImpl<>();
    }


    static <T> ReplayPromise<T> replayPromise(final Duration timeout, long time) {
        return new ReplayPromiseImpl<>(timeout, time);
    }


    static <T> ReplayPromise<T> replayPromise(final Duration timeout) {
        return new ReplayPromiseImpl<>(timeout, System.currentTimeMillis());
    }


    static <T> Promise<T> blockingPromise() {
        return new BlockingPromise<>();
    }
}
