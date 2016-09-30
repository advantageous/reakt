package io.advantageous.reakt;



/**
 * Simplified interface to a callback, hiding a callbacks hierarchy and dual roles.
 * This is the service view of a callback.
 * This focuses on just resolution methods of a callback.
 */
public interface Callback<T> {

    /**
     * (Service view)
     * This allows services to send back a failed result easily to the client/handler.
     * <p>
     * This is a helper methods for producers (services that produce results) to send a failed result.
     *
     * @param error error
     */
    void reject(final Throwable error) ;


    /**
     * (Service view)
     * This allows services to send back a failed result easily to the client/handler.
     * <p>
     * This is a helper methods for producers (services that produce results) to send a failed result.
     *
     * @param errorMessage error message
     */
    void reject(final String errorMessage);


    /**
     * (Service view)
     * This allows services to send back a failed result easily to the client/handler.
     * <p>
     * This is a helper methods for producers (services that produce results) to send a failed result.
     *
     * @param errorMessage error message
     * @param error        exception
     */
    void reject(final String errorMessage, final Throwable error) ;



    /**
     * Calls replayDone, for VOID callback only. ES6 promise style.
     */
    void resolve();

    /**
     * Resolve resolves a promise.
     *
     * @param result makes it more compatible with ES6 style promises
     */
    void resolve(final T result);


}
