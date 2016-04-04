package io.advantageous.reakt.promise;

import io.advantageous.reakt.Callback;
import io.advantageous.reakt.Result;
import io.advantageous.reakt.promise.impl.BlockingPromise;
import io.advantageous.reakt.promise.impl.PromiseImpl;
import io.advantageous.reakt.promise.impl.ReplayPromiseImpl;

import java.time.Duration;

/**
 * A promise is like a non-blocking {@code Future}({@link java.util.concurrent.Future}).
 * You get notified of changes instead of having to call {@code get}.
 * <p>
 * A promise is both a {@code Callback} ({@link io.advantageous.reakt.Callback}),
 * and a {@code Result} {@link io.advantageous.reakt.Result}.
 * <p>
 * <p>
 * A promise is a sort of deferred value.
 *
 * @param <T> value of result.
 */
public interface Promise<T> extends Callback<T>, Result<T> {

    /**
     * Create a promise.
     * After you create a promise you register its then and catchError and then you use it to
     * handle a callback.
     *
     * @param <T> type of result
     * @return new promise
     */
    static <T> Promise<T> promise() {
        return new PromiseImpl<>();
    }


    /**
     * Allows the results of a promise to be replayed on the callers thread.
     *
     * @param timeout timeout
     * @param time    time
     * @param <T>     type of result
     * @return new replay promise
     */
    static <T> ReplayPromise<T> replayPromise(final Duration timeout, long time) {
        return new ReplayPromiseImpl<>(timeout, time);
    }


    /**
     * Allows the results of a promise to be replayed on the callers thread.
     *
     * @param timeout timeout
     * @param <T>     type of result
     * @return new replay promise
     */
    static <T> ReplayPromise<T> replayPromise(final Duration timeout) {
        return new ReplayPromiseImpl<>(timeout, System.currentTimeMillis());
    }


    /**
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     * After you create a promise you register its then and catchError and then you use it to
     * handle a callback.
     *
     * @param <T> type of result
     * @return new promise
     */
    static <T> Promise<T> blockingPromise() {
        return new BlockingPromise<>();
    }
}
