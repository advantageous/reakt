package io.advantageous.reakt.reactor;

import io.advantageous.reakt.promise.Promise;
import io.advantageous.reakt.reactor.impl.ReactorImpl;

import java.time.Duration;
import java.util.List;

/**
 * Ensures that tasks, repeating tasks and callbacks run in the callers thread.
 * Used with actor service models like QBit, Vertx, etc.
 */
public interface Reactor {

    /**
     * Creates a default reactor.
     *
     * @return a reactor
     */
    static Reactor reactor() {
        return new ReactorImpl(Duration.ofSeconds(30), System::currentTimeMillis);
    }

    /**
     * Creates a default reactor with timeout.
     *
     * @param timeout timeout
     * @return a reactor
     */
    static Reactor reactor(final Duration timeout) {
        return new ReactorImpl(timeout, System::currentTimeMillis);
    }

    /**
     * Creates a default reactor with timeout and timesource.
     *
     * @param timeout    timeout
     * @param timeSource time source
     * @return a reactor
     */
    static Reactor reactor(final Duration timeout, final TimeSource timeSource) {
        return new ReactorImpl(timeout, timeSource);
    }

    /**
     * Create a promise.
     * After you create a promise you register its then(...) and catchError(...) and then you use it to
     * handle a callback.
     * <p>
     * Creates a replay promise that is managed by this Reactor.
     *
     * @param <T> type of result
     * @return new promise
     */
    <T> Promise<T> promise();

    /**
     * All promises must complete.
     *
     * @param promises promises
     * @return return containing promise
     */
    Promise<Void> all(final Promise<?>... promises);


    /**
     * All promises must complete.
     *
     * @param timeout  timeout
     * @param promises promises
     * @return return containing promise
     */
    Promise<Void> all(final Duration timeout,
                      final Promise<?>... promises);

    /**
     * All promises must complete.
     *
     * @param promises promises
     * @param <T>      types of promise
     * @return return containing promise
     */
    <T> Promise<Void> all(final List<Promise<T>> promises);

    /**
     * All promises must complete.
     *
     * @param timeout  timeout
     * @param promises promises
     * @param <T>      types of promise
     * @return return containing promise
     */
    <T> Promise<Void> all(final Duration timeout,
                          final List<Promise<T>> promises);

    /**
     * Any promises must complete.
     *
     * @param promises promises
     * @return return containing promise
     */
    Promise<Void> any(final Promise<?>... promises);


    /**
     * Any promises must complete.
     *
     * @param timeout  timeout
     * @param promises promises
     * @return return containing promise
     */
    Promise<Void> any(final Duration timeout,
                      final Promise<?>... promises);

    /**
     * All promises must complete.
     *
     * @param promises promises
     * @param <T>      types of promise
     * @return return containing promise
     */
    <T> Promise<Void> any(final List<Promise<T>> promises);

    /**
     * All promises must complete.
     *
     * @param timeout  timeout
     * @param promises promises
     * @param <T>      types of promise
     * @return return containing promise
     */
    <T> Promise<Void> any(final Duration timeout,
                          final List<Promise<T>> promises);


    /**
     * Add a repeating task that will run every interval
     *
     * @param interval duration of interval
     * @param runnable runnable to run.
     */
    void addRepeatingTask(final Duration interval, final Runnable runnable);

    /**
     * Add a task that will run once after the interval.
     *
     * @param afterInterval duration of interval
     * @param runnable      runnable to run.
     */
    void runTaskAfter(final Duration afterInterval, final Runnable runnable);

    /**
     * Run on this Reactor's thread as soon as you can.
     *
     * @param runnable runnable
     */
    void deferRun(Runnable runnable);

    /**
     * Allows the reactor to process its tasks, and promises (callbacks).
     */
    void process();
}
