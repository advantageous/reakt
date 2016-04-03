package io.advantageous.reakt;


/**
 * Represents an async supplier of results.
 *
 * @param <T> the type of results supplied by this supplier
 */
public interface AsyncSupplier<T> {

    void get(Callback<T> callback);

}
