package io.advantageous.reakt;


/**
 * Provides a mechanism for receiving push-based notifications.
 * This class represents traditional Subscriber/Observer and how to integrate with them.
 *
 * @param <T> the type of item the Observer expects to observe
 * @see <a href="http://reactivex.io/documentation/observable.html">ReactiveX documentation: Observable</a>
 */
public interface Observer<T> {

    /**
     * Adapts an observer to callback.
     *
     * @param observer observer that you want to turn into a callback.
     * @param <T>      value of results, which will be a singleton.
     * @return new Callback
     */
    static <T> Callback<T> callback(final Observer<T> observer) {
        return result -> {
            if (result.success()) {
                observer.onNext(result.get());
            } else {
                observer.onError(result.cause());
            }
            observer.onCompleted();
        };
    }

    /**
     * Adapts an observer to a stream.
     *
     * @param observer observer
     * @param <T>      value of results which will be a singleton.
     * @return a new Reakt stream which represents the results returned.
     */
    static <T> Stream<T> stream(final Observer<T> observer) {
        return result -> {
            if (result.success()) {
                observer.onNext(result.get());
            } else {
                observer.onError(result.cause());
            }
            if (result.complete()) {
                observer.onCompleted();
            }
        };
    }

    /**
     * Notifies the Observer that the an Observable has finished sending push-based notifications.
     * <p>
     * The Observable will not call this method if it calls {@link #onError}.
     */
    void onCompleted();

    /**
     * Notifies the Observer that the Observable has hit an error condition.
     * <p>
     * If the Observable calls this method, it will not thereafter call {@link #onNext} or
     * {@link #onCompleted}.
     *
     * @param e the exception encountered by the Observable
     */
    void onError(Throwable e);

    /**
     * Provides the Observer with a new item to observe.
     * <p>
     * The Observable may call this method 0 or more times. In the case of a scalar call {@link Callback#onResult(Result)}
     * it will get called once. In the case of {@link Stream#onNext(StreamResult)} the stream will get called many times.
     * <p>
     * The {@code Observable} will not call this method again after it calls either {@link #onCompleted} or
     * {@link #onError}.
     *
     * @param t the item emitted by the Observable
     */
    void onNext(T t);

}

