package io.advantageous.reakt.promise;

import java.util.function.Consumer;

/**
 * Replay promise ensures that the event handler callbacks (then, catchError) happen on the calling thread.
 *
 * @param <T> T value of the result.
 */
public interface ReplayPromise<T> extends Promise<T> {

    /**
     * Return true if done.
     * Gets called periodically to move data from foreign thread to this one.
     *
     * @param time current time
     * @return true if done
     */
    boolean check(long time);

    /**
     * @param handler handle timeout.
     * @return this fluent
     * @throws NullPointerException if result is present and {@code handler} is null
     */
    ReplayPromise<T> onTimeout(Runnable handler);

    /**
     * Handler after the async result has been processed and data copied to this thread.
     *
     * @param handler handler
     * @return this fluent
     */
    ReplayPromise<T> afterResultProcessed(Consumer<ReplayPromise> handler);

}
