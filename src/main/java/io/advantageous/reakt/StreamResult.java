package io.advantageous.reakt;

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
