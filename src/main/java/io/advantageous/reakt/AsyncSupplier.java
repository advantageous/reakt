package io.advantageous.reakt;


/**
 * Represents an async supplier of results.
 *
 * @param <T> the type of results supplied by this supplier
 */
public interface AsyncSupplier<T> {

    /**
     * Supply an item when you can.
     *
     * @param callback callback with supplied item as a result.
     */
    void get(Callback<T> callback);

}
