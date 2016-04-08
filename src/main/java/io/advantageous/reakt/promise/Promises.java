package io.advantageous.reakt.promise;

import io.advantageous.reakt.promise.impl.*;

import java.time.Duration;
import java.util.List;

public interface Promises {
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
     * @return return containing promise
     */
    static Promise<Void> all(List<Promise<?>> promises) {
        return new AllPromise(promises.toArray(new Promise[promises.size()]));
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
     * @param promises promises
     * @return return containing promise that is blocking.
     */
    static Promise<Void> allBlocking(List<Promise<?>> promises) {
        return new AllBlockingPromise(promises.toArray(new Promise[promises.size()]));
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
     * @param time     time
     * @param promises promises
     * @return returns replay promise so promise can be replayed in caller's thread.
     */
    static ReplayPromise<Void> allReplay(final Duration timeout, long time, List<Promise<?>> promises) {
        return new AllReplayPromise(timeout, time, promises.toArray(new Promise[promises.size()]));
    }

    /**
     * All promises must complete.
     *
     * @param timeout  timeout
     * @param promises promises
     * @return returns replay promise so promise can be replayed in caller's thread.
     */
    static ReplayPromise<Void> allReplay(final Duration timeout, Promise<?>... promises) {
        return allReplay(timeout, System.currentTimeMillis(), promises);
    }


    /**
     * All promises must complete.
     *
     * @param timeout  timeout
     * @param promises promises
     * @return returns replay promise so promise can be replayed in caller's thread.
     */
    static ReplayPromise<Void> allReplay(final Duration timeout, List<Promise<?>> promises) {
        return allReplay(timeout, System.currentTimeMillis(), promises.toArray(new Promise[promises.size()]));
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
}
