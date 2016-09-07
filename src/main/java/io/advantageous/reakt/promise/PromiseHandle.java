package io.advantageous.reakt.promise;

import io.advantageous.reakt.Expected;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * Simplified interface to promise that hides the complexity and hierarchy of a Promise.
 */
public interface PromiseHandle<T> {


    /**
     * If a result is sent, and there was no error, then handle the result.
     * <p>
     * There is only one {@code then} Handler.
     * </p>
     * Unlike ES6, {@code then(..)} cannot be chained per se, but {@code whenComplete(..)}, and
     * {@code }thenMap(...)} can be nested.
     *
     * @param consumer executed if result has no error.
     * @return this, fluent API
     * @throws NullPointerException if result is present and {@code consumer} is null
     */
    PromiseHandle<T> then(Consumer<T> consumer);

    /**
     * If a result is sent, and there was no error, then handle the result as a value which could be null.
     * <p>
     * There is only one thenExpect handler per promise.
     * <p>
     * Unlike ES6, {@code thenExpect(..)} cannot be chained per se as it does not create a new promise,
     * but {@code whenComplete(..)}, and {@code }thenMap(...)} can be chained.
     * <p>
     * This does not create a new promise.
     *
     * @param consumer executed if result has no error.
     * @return this, fluent API
     * @throws NullPointerException if result is present and {@code consumer} is
     *                              null
     */
    PromiseHandle<T> thenExpect(Consumer<Expected<T>> consumer);

    /**
     * If a result is sent, and there is an error, then handle handle the error.
     *
     * @param consumer executed if result has error.
     * @return this, fluent API
     * @throws NullPointerException if result is present and {@code consumer} is null
     */
    PromiseHandle<T> catchError(Consumer<Throwable> consumer);


    /**
     * Allows promises returned from, for example, proxy stubs for services methods to invoke the operation.
     * <p>
     * This allows use to set up the catchError and then before the method is async invoked.
     * <p>
     * Example Remote Proxy Gen to support returning Reakt invokeable promise
     * <pre>
     * <code>
     *     employeeService.lookupEmployee("123")
     *           .then((employee)-&gt; {...})
     *           .catchError(...)
     *           .invoke();
     * </code>
     * </pre>
     *
     * @return this, fluent
     */
    PromiseHandle<T> invoke();

    /**
     * If the thenSafeExpect handler throws an exception, this will report it as if it it was caught by catchError.
     * <p>
     * This is convenient if you are running your handler with an async lib that is not reporting or catching
     * exceptions as your code is running on their threads.
     * <p>
     * If a result is sent, and there was no error, then handle the result as a value which could be null.
     * <p>
     * There is only one thenSafeExpect or thenExpect handler per promise.
     * Once then is called all other then* handlers are safe.
     * <p>
     * Unlike ES6, {@code thenExpect(..)} cannot be chained per se as it does not create a new promise,
     * but {@code whenComplete(..)}, and {@code }thenMap(...)} can be chained.
     * <p>
     * This does not create a new promise.
     *
     * @param consumer executed if result has no error.
     * @return this, fluent API
     * @throws NullPointerException if result is present and {@code consumer} is
     *                              null
     */
    PromiseHandle<T> thenSafeExpect(Consumer<Expected<T>> consumer);


    /**
     * If the {@code then} handler throws an exception, this will report the exception as if it were caught
     * by {@code catchError}.
     * <p>
     * This is convenient if you are running your handler with an async lib that is not reporting or catching
     * exceptions as your code is running on their threads.
     * <p>
     * If a result is sent, and there was no error, then handle the result as a value which could be null.
     * <p>
     * There is only one thenSafe or then handler per promise.
     * Once then is called all other then* handlers are safe.
     * <p>
     * Unlike ES6, {@code thenExpect(..)} cannot be chained per se as it does not create a new promise,
     * but {@code whenComplete(..)}, and {@code }thenMap(...)} can be chained.
     * <p>
     * This does not create a new promise.
     *
     * @param consumer executed if result has no error.
     * @return this, fluent API
     * @throws NullPointerException if result is present and {@code consumer} is
     *                              null
     */
    PromiseHandle<T> thenSafe(Consumer<T> consumer);


    /**
     * Used for testing and legacy integration.
     * This turns an async promise into a blocking promise and then does a get operations.
     * @param duration duration to wait for call
     * @return result of call, blocks until return comes back.
     */
    T blockingGet(Duration duration);

    /**
     * Used for testing and legacy integration.
     * This turns an async promise into a blocking promise and then does a get operations.
     * @return result of call, blocks until return comes back.
     */
    T blockingGet();

    /**
     * If backed by a Promise then this will return that promise, otherwise throws an IllegalStateException.
     * @return promise that backs this handle
     */
    default Promise<T> asPromise() {
        return (Promise<T>) this;
    }

}
