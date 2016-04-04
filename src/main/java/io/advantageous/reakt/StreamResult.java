package io.advantageous.reakt;

/**
 * The result of an async operations with optional callbacks for cancel and request more.
 *
 * @param <T> Type of result.
 */
public interface StreamResult<T> extends Result<T> {

    /**
     * Request more results
     *
     * @param n number of results that you are requesting.
     */
    void request(long n);

    /**
     * Stop sending results.
     */
    void cancel();
}
