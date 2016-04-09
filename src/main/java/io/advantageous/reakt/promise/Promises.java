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
    static Promise<Void> all(final Promise<?>... promises) {
        return new AllPromise(promises);
    }

    /**
     * All promises must complete.
     *
     * @param promises promises
     * @param <T>      types of promise
     * @return return containing promise
     */
    static <T> Promise<Void> all(final List<Promise<T>> promises) {
        return new AllPromise(promises.toArray(new Promise[promises.size()]));
    }

    /**
     * Any promises must complete.
     *
     * @param promises promises
     * @return return containing promise
     */
    static Promise<Void> any(final Promise<?>... promises) {
        return new AnyPromise(promises);
    }

    /**
     * All promises must complete.
     *
     * @param promises promises
     * @param <T>      types of promise
     * @return return containing promise
     */
    static <T> Promise<Void> any(final List<Promise<T>> promises) {
        return new AnyPromise(promises.toArray(new Promise[promises.size()]));
    }


    /**
     * All promises must complete.
     *
     * @param promises promises
     * @return return containing promise that is blocking.
     */
    static Promise<Void> allBlocking(final Promise<?>... promises) {
        return new AllBlockingPromise(promises);
    }


    /**
     * All promises must complete.
     *
     * @param promises promises
     * @param <T>      types of promise
     * @return return containing promise that is blocking.
     */
    static <T> Promise<Void> allBlocking(final List<Promise<T>> promises) {
        return new AllBlockingPromise(promises.toArray(new Promise[promises.size()]));
    }


    /**
     * Any promises must complete.
     *
     * @param promises promises
     * @return return containing promise that is blocking.
     */
    static Promise<Void> anyBlocking(final Promise<?>... promises) {
        return new AnyBlockingPromise(promises);
    }


    /**
     * Any promises must complete.
     *
     * @param promises promises
     * @param <T>      types of promise
     * @return return containing promise that is blocking.
     */
    static <T> Promise<Void> anyBlocking(final List<Promise<T>> promises) {
        return new AnyBlockingPromise(promises.toArray(new Promise[promises.size()]));
    }

    /**
     * All promises must complete.
     *
     * @param timeout  timeout
     * @param time     time
     * @param promises promises
     * @return returns replay promise so promise can be replayed in caller's thread.
     */
    static ReplayPromise<Void> allReplay(final Duration timeout,
                                         final long time,
                                         final Promise<?>... promises) {
        return new AllReplayPromise(timeout, time, promises);
    }

    /**
     * All promises must complete.
     *
     * @param timeout  timeout
     * @param time     time
     * @param promises promises
     * @param <T>      types of promise
     * @return returns replay promise so promise can be replayed in caller's thread.
     */
    static <T> ReplayPromise<Void> allReplay(final Duration timeout,
                                             final long time,
                                             final List<Promise<T>> promises) {
        return new AllReplayPromise(timeout, time, promises.toArray(new Promise[promises.size()]));
    }

    /**
     * All promises must complete.
     *
     * @param timeout  timeout
     * @param promises promises
     * @return returns replay promise so promise can be replayed in caller's thread.
     */
    static ReplayPromise<Void> allReplay(final Duration timeout,
                                         final Promise<?>... promises) {
        return allReplay(timeout, System.currentTimeMillis(), promises);
    }


    /**
     * All promises must complete.
     *
     * @param timeout  timeout
     * @param promises promises
     * @param <T>      types of promise
     * @return returns replay promise so promise can be replayed in caller's thread.
     */
    static <T> ReplayPromise<Void> allReplay(final Duration timeout,
                                             final List<Promise<T>> promises) {
        return allReplay(timeout, System.currentTimeMillis(), promises.toArray(new Promise[promises.size()]));
    }

    /**
     * Any promises must complete.
     *
     * @param timeout  timeout
     * @param time     time
     * @param promises promises
     * @return returns replay promise so promise can be replayed in caller's thread.
     */
    static ReplayPromise<Void> anyReplay(final Duration timeout, long time,
                                         final Promise<?>... promises) {
        return new AnyReplayPromise(timeout, time, promises);
    }

    /**
     * Any promises must complete.
     *
     * @param timeout  timeout
     * @param time     time
     * @param promises promises
     * @param <T>      types of promise
     * @return returns replay promise so promise can be replayed in caller's thread.
     */
    static <T> ReplayPromise<Void> anyReplay(final Duration timeout, long time,
                                             final List<Promise<T>> promises) {
        return new AnyReplayPromise(timeout, time, promises.toArray(new Promise[promises.size()]));
    }

    /**
     * Any promises must complete.
     *
     * @param timeout  timeout
     * @param promises promises
     * @return returns replay promise so promise can be replayed in caller's thread.
     */
    static ReplayPromise<Void> anyReplay(final Duration timeout, final Promise<?>... promises) {
        return anyReplay(timeout, System.currentTimeMillis(), promises);
    }


    /**
     * Any promises must complete.
     *
     * @param timeout  timeout
     * @param promises promises
     * @param <T>      types of promise
     * @return returns replay promise so promise can be replayed in caller's thread.
     */
    static <T> ReplayPromise<Void> anyReplay(final Duration timeout, final List<Promise<T>> promises) {
        return anyReplay(timeout, System.currentTimeMillis(), promises.toArray(new Promise[promises.size()]));
    }

    /**
     * Allows the results of a promise to be replayed on the callers thread.
     *
     * @param timeout timeout
     * @param time    time
     * @param <T>     type of result
     * @return new replay promise
     */
    static <T> ReplayPromise<T> replayPromise(final Duration timeout,
                                              final long time) {
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
