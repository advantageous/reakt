package io.advantageous.reakt.promise;

/**
 * Replay promise ensures that the event handler callbacks (then, catchError) happen on the calling thread.
 *
 * @param <T> T value of the result.
 */
public interface ReplayPromise<T> extends Promise<T> {

    /**
     * Return true if done.
     *
     * @param time current time
     * @return true if done
     */
    boolean check(long time);

    /**
     * @param handler handle timeout.
     * @throws NullPointerException if result is present and {@code handler} is null
     */
    ReplayPromise<T> onTimeout(Runnable handler);
}
