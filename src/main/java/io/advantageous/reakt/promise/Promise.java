package io.advantageous.reakt.promise;

import io.advantageous.reakt.Callback;
import io.advantageous.reakt.Ref;
import io.advantageous.reakt.Result;
import io.advantageous.reakt.promise.impl.*;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * A promise is like a non-blocking {@code Future}({@link java.util.concurrent.Future}).
 * You get notified of changes instead of having to call {@code get}.
 * <p>
 * A promise is both a {@code Callback} ({@link io.advantageous.reakt.Callback}),
 * and a {@code Result} {@link io.advantageous.reakt.Result}.
 * </p>
 * <p>
 * A promise is a sort of deferred value.
 *
 * @param <T> value of result.
 */
public interface Promise<T> extends Callback<T>, Result<T> {

    /**
     * Create a promise.
     * After you create a promise you register its then(...) and catchError(...) and then you use it to
     * handle a callback.
     *
     * @param <T> type of result
     * @return new promise
     */
    static <T> Promise<T> promise() {
        return new BasePromise<>();
    }

    /**
     * All promises must complete.
     *
     * @param promises promises
     * @return return containing promise
     */
    static Promise<Void> all(Promise<?>... promises) {
        return new AllPromise(promises);
    }

    /**
     * All promises must complete.
     *
     * @param promises promises
     * @return return containing promise that is blocking.
     */
    static Promise<Void> allBlocking(Promise<?>... promises) {
        return new AllBlockingPromise(promises);
    }

    /**
     * All promises must complete.
     *
     * @param timeout  timeout
     * @param time     time
     * @param promises promises
     * @return returns replay promise so promise can be replayed in caller's thread.
     */
    static ReplayPromise<Void> allReplay(final Duration timeout, long time, Promise<?>... promises) {
        return new AllReplayPromise(timeout, time, promises);
    }

    /**
     * All promises must complete.
     *
     * @param timeout  timeout
     * @param promises promises
     * @return returns replay promise so promise can be replayed in caller's thread.
     */
    static ReplayPromise<Void> allReplay(final Duration timeout, Promise<?>... promises) {
        return Promise.allReplay(timeout, System.currentTimeMillis(), promises);
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

    /**
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     * After you create a promise you register its then and catchError and then you use it to
     * handle a callback.
     *
     * @param duration duration of timeout
     * @param <T>      type of result
     * @return new promise
     */
    static <T> Promise<T> blockingPromise(final Duration duration) {
        return new BlockingPromise<>(duration);
    }

    /**
     * Creates an immutable promise.
     *
     * @return final promise
     */
    default Promise<T> freeze() {
        return BasePromise.provideFinalPromise(this);
    }

    /**
     * If a result is sent, and there was no error, then handle the result.
     *
     * @param consumer executed if result has no error.
     * @return this, fluent API
     * @throws NullPointerException if result is present and {@code consumer} is
     *                              null
     */
    Promise<T> then(Consumer<T> consumer);

    /**
     * Notified of completeness
     *
     * @param doneListener doneListener
     * @return this, fluent API
     */
    Promise<T> whenComplete(Runnable doneListener);

    /**
     * If a result is sent, and there was no error, then handle the result as a value which could be null.
     *
     * @param consumer executed if result has no error.
     * @return this, fluent API
     * @throws NullPointerException if result is present and {@code consumer} is
     *                              null
     */
    Promise<T> thenRef(Consumer<Ref<T>> consumer);

    /**
     * If a result is sent, and there is an error, then handle handle the error.
     *
     * @param consumer executed if result has error.
     * @return this, fluent API
     * @throws NullPointerException if result is present and {@code consumer} is
     *                              null
     */
    Promise<T> catchError(Consumer<Throwable> consumer);
}
