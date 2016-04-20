package io.advantageous.reakt.promise;

import io.advantageous.reakt.promise.impl.*;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Utility methods for creating promises.
 */
public interface Promises {

    /**
     * Create a promise.
     * After you create a promise you register its then(...) and catchError(...) and then you use it to
     * handle a callback.
     *
     * @param <T> type of result
     * @return new promise
     */
    static <T> Promise<T> promise() {
        return new BasePromise<>();
    }

    /**
     * All promises must complete.
     *
     * @param promises promises
     * @return return containing promise
     */
    static Promise<Void> all(final Promise<?>... promises) {
        return new AllPromise(promises);
    }

    /**
     * All promises must complete.
     *
     * @param promises promises
     * @param <T>      types of promise
     * @return return containing promise
     */
    static <T> Promise<Void> all(final List<Promise<T>> promises) {
        return all(promises.toArray(new Promise[promises.size()]));
    }

    /**
     * Any promises must complete.
     *
     * @param promises promises
     * @return return containing promise
     */
    static Promise<Void> any(final Promise<?>... promises) {
        return new AnyPromise(promises);
    }

    /**
     * All promises must complete.
     *
     * @param promises promises
     * @param <T>      types of promise
     * @return return containing promise
     */
    static <T> Promise<Void> any(final List<Promise<T>> promises) {
        return any(promises.toArray(new Promise[promises.size()]));
    }


    /**
     * All promises must complete.
     *
     * @param promises promises
     * @return return containing promise that is blocking.
     */
    static Promise<Void> allBlocking(final Promise<?>... promises) {
        return new AllBlockingPromise(promises);
    }


    /**
     * All promises must complete.
     *
     * @param promises promises
     * @param <T>      types of promise
     * @return return containing promise that is blocking.
     */
    static <T> Promise<Void> allBlocking(final List<Promise<T>> promises) {
        return allBlocking(promises.toArray(new Promise[promises.size()]));
    }


    /**
     * Any promises must complete.
     *
     * @param promises promises
     * @return return containing promise that is blocking.
     */
    static Promise<Void> anyBlocking(final Promise<?>... promises) {
        return new AnyBlockingPromise(promises);
    }


    /**
     * Any promises must complete.
     *
     * @param promises promises
     * @param <T>      types of promise
     * @return return containing promise that is blocking.
     */
    static <T> Promise<Void> anyBlocking(final List<Promise<T>> promises) {
        return anyBlocking(promises.toArray(new Promise[promises.size()]));
    }

    /**
     * All promises must complete.
     *
     * @param timeout  timeout
     * @param time     time
     * @param promises promises
     * @return returns replay promise so promise can be replayed in caller's thread.
     */
    static ReplayPromise<Void> allReplay(final Duration timeout,
                                         final long time,
                                         final Promise<?>... promises) {
        return new AllReplayPromise(timeout, time, promises);
    }

    /**
     * All promises must complete.
     *
     * @param timeout  timeout
     * @param time     time
     * @param promises promises
     * @param <T>      types of promise
     * @return returns replay promise so promise can be replayed in caller's thread.
     */
    static <T> ReplayPromise<Void> allReplay(final Duration timeout,
                                             final long time,
                                             final List<Promise<T>> promises) {
        return allReplay(timeout, time, promises.toArray(new Promise[promises.size()]));
    }

    /**
     * All promises must complete.
     *
     * @param timeout  timeout
     * @param promises promises
     * @return returns replay promise so promise can be replayed in caller's thread.
     */
    static ReplayPromise<Void> allReplay(final Duration timeout,
                                         final Promise<?>... promises) {
        return allReplay(timeout, System.currentTimeMillis(), promises);
    }


    /**
     * All promises must complete.
     *
     * @param timeout  timeout
     * @param promises promises
     * @param <T>      types of promise
     * @return returns replay promise so promise can be replayed in caller's thread.
     */
    static <T> ReplayPromise<Void> allReplay(final Duration timeout,
                                             final List<Promise<T>> promises) {
        return allReplay(timeout, System.currentTimeMillis(), promises.toArray(new Promise[promises.size()]));
    }

    /**
     * Any promises must complete.
     *
     * @param timeout  timeout
     * @param time     time
     * @param promises promises
     * @return returns replay promise so promise can be replayed in caller's thread.
     */
    static ReplayPromise<Void> anyReplay(final Duration timeout, long time,
                                         final Promise<?>... promises) {
        return new AnyReplayPromise(timeout, time, promises);
    }

    /**
     * Any promises must complete.
     *
     * @param timeout  timeout
     * @param time     time
     * @param promises promises
     * @param <T>      types of promise
     * @return returns replay promise so promise can be replayed in caller's thread.
     */
    static <T> ReplayPromise<Void> anyReplay(final Duration timeout, long time,
                                             final List<Promise<T>> promises) {
        return new AnyReplayPromise(timeout, time, promises.toArray(new Promise[promises.size()]));
    }

    /**
     * Any promises must complete.
     *
     * @param timeout  timeout
     * @param promises promises
     * @return returns replay promise so promise can be replayed in caller's thread.
     */
    static ReplayPromise<Void> anyReplay(final Duration timeout, final Promise<?>... promises) {
        return anyReplay(timeout, System.currentTimeMillis(), promises);
    }


    /**
     * Any promises must complete.
     *
     * @param timeout  timeout
     * @param promises promises
     * @param <T>      types of promise
     * @return returns replay promise so promise can be replayed in caller's thread.
     */
    static <T> ReplayPromise<Void> anyReplay(final Duration timeout, final List<Promise<T>> promises) {
        return anyReplay(timeout, System.currentTimeMillis(), promises.toArray(new Promise[promises.size()]));
    }

    /**
     * Allows the results of a promise to be replayed on the callers thread.
     *
     * @param timeout timeout
     * @param time    time
     * @param <T>     type of result
     * @return new replay promise
     */
    static <T> ReplayPromise<T> replayPromise(final Duration timeout,
                                              final long time) {
        return new ReplayPromiseImpl<>(timeout, time);
    }

    /**
     * Allows the results of a promise to be replayed on the callers thread.
     *
     * @param timeout timeout
     * @param <T>     type of result
     * @return new replay promise
     */
    static <T> ReplayPromise<T> replayPromise(final Duration timeout) {
        return new ReplayPromiseImpl<>(timeout, System.currentTimeMillis());
    }

    /**
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     * After you create a promise you register its then and catchError and then you use it to
     * handle a callback.
     *
     * @param <T> type of result
     * @return new promise
     */
    static <T> Promise<T> blockingPromise() {
        return new BlockingPromise<>();
    }

    /**
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     * After you create a promise you register its then and catchError and then you use it to
     * handle a callback.
     *
     * @param duration duration of timeout
     * @param <T>      type of result
     * @return new promise
     */
    static <T> Promise<T> blockingPromise(final Duration duration) {
        return new BlockingPromise<>(duration);
    }


    /**
     * Returns a String promise
     * Added to make static imports possible.
     *
     * @return returns a string promise
     */
    static Promise<String> promiseString() {
        return new BasePromise<>();
    }

    /**
     * Returns a Integer promise
     * Added to make static imports possible.
     *
     * @return returns an int promise
     */
    static Promise<Integer> promiseInt() {
        return new BasePromise<>();
    }


    /**
     * Returns a Long promise
     * Added to make static imports possible.
     *
     * @return returns an long promise
     */
    static Promise<Long> promiseLong() {
        return new BasePromise<>();
    }

    /**
     * Returns a Double promise
     * Added to make static imports possible.
     *
     * @return returns an double promise
     */
    static Promise<Double> promiseDouble() {
        return new BasePromise<>();
    }


    /**
     * Returns a Float promise
     * Added to make static imports possible.
     *
     * @return returns an float promise
     */
    static Promise<Float> promiseFloat() {
        return new BasePromise<>();
    }

    /**
     * Returns a void promise for notify of outcome but no value returned.
     * <p>
     * Callback replyDone can be used instead of replay on service side.
     *
     * @return void promise
     */
    static Promise<Void> promiseNotify() {
        return new BasePromise<>();
    }

    /**
     * Boolean promise
     * Added to make static imports possible.
     *
     * @return promises a boolean
     */
    static Promise<Boolean> promiseBoolean() {
        return new BasePromise<>();
    }


    /**
     * Generic promise.
     * Added to make static imports possible.
     *
     * @param cls type
     * @param <T> promise of a result of T
     * @return new Promise of type T
     */
    @SuppressWarnings("unused")
    static <T> Promise<T> promise(Class<T> cls) {
        return new BasePromise<>();
    }


    /**
     * Generic list promise.
     * Added to make static imports possible.
     *
     * @param componentType component type of list
     * @param <T>           promise a list of type T
     * @return new Promise for a list of type T
     */
    @SuppressWarnings("unused")
    static <T> Promise<List<T>> promiseList(Class<T> componentType) {
        return new BasePromise<>();
    }

    /**
     * Generic collection promise.
     * Added to make static imports possible.
     *
     * @param componentType component type of collection
     * @param <T>           promise a collection of type T
     * @return new Promise for a collection of type T
     */
    @SuppressWarnings("unused")
    static <T> Promise<Collection<T>> promiseCollection(Class<T> componentType) {
        return new BasePromise<>();
    }

    /**
     * Generic map promise.
     * Added to make static imports possible.
     *
     * @param keyType   type of map key
     * @param valueType type of map value
     * @param <K>       promise a map of  key type K
     * @param <V>       promise a map of  value type V
     * @return new Promise for a collection of type T
     */
    @SuppressWarnings("unused")
    static <K, V> Promise<Map<K, V>> promiseMap(Class<K> keyType, Class<V> valueType) {
        return new BasePromise<>();
    }

    /**
     * Generic set promise.
     * Added to make static imports possible.
     *
     * @param componentType component type of set
     * @param <T>           promise a set of type T
     * @return new Promise for a set of type T
     */
    @SuppressWarnings("unused")
    static <T> Promise<Set<T>> promiseSet(Class<T> componentType) {
        return new BasePromise<>();
    }

    /*
    Replay
     */


    /**
     * Returns a String promise
     * Added to make static imports possible.
     *
     * @param timeout timeout
     * @param time    time
     * @return returns a string promise
     */
    static ReplayPromise<String> replayPromiseString(final Duration timeout,
                                                     final long time) {
        return new ReplayPromiseImpl<>(timeout, time);
    }

    /**
     * Returns a Integer promise
     * Added to make static imports possible.
     *
     * @param timeout timeout
     * @param time    time
     * @return returns an int promise
     */
    static ReplayPromise<Integer> replayPromiseInt(final Duration timeout,
                                                   final long time) {
        return new ReplayPromiseImpl<>(timeout, time);
    }


    /**
     * Returns a Long promise
     * Added to make static imports possible.
     *
     * @param timeout timeout
     * @param time    time
     * @return returns an long promise
     */
    static ReplayPromise<Long> replayPromiseLong(final Duration timeout,
                                                 final long time) {
        return new ReplayPromiseImpl<>(timeout, time);
    }

    /**
     * Returns a Double promise
     * Added to make static imports possible.
     *
     * @param timeout timeout
     * @param time    time
     * @return returns an double promise
     */
    static ReplayPromise<Double> replayPromiseDouble(final Duration timeout,
                                                     final long time) {
        return new ReplayPromiseImpl<>(timeout, time);
    }


    /**
     * Returns a Float promise
     * Added to make static imports possible.
     *
     * @param timeout timeout
     * @param time    time
     * @return returns an float promise
     */
    static ReplayPromise<Float> replayPromiseFloat(final Duration timeout,
                                                   final long time) {
        return new ReplayPromiseImpl<>(timeout, time);
    }

    /**
     * Returns a void promise for notify of outcome but no value returned.
     * <p>
     * Callback replyDone can be used instead of replay on service side.
     *
     * @param timeout timeout
     * @param time    time
     * @return void promise
     */
    static ReplayPromise<Void> replayPromiseNotify(final Duration timeout,
                                                   final long time) {
        return new ReplayPromiseImpl<>(timeout, time);
    }

    /**
     * Boolean promise
     * Added to make static imports possible.
     *
     * @param timeout timeout
     * @param time    time
     * @return promises a boolean
     */
    static ReplayPromise<Boolean> replayPromiseBoolean(final Duration timeout,
                                                       final long time) {
        return new ReplayPromiseImpl<>(timeout, time);

    }


    /**
     * Generic promise.
     * Added to make static imports possible.
     *
     * @param cls     type
     * @param timeout timeout
     * @param time    time
     * @param <T>     promise of a result of T
     * @return new Promise of type T
     */
    @SuppressWarnings("unused")
    static <T> ReplayPromise<T> replayPromise(Class<T> cls,
                                              final Duration timeout,
                                              final long time) {
        return new ReplayPromiseImpl<>(timeout, time);
    }


    /**
     * Generic list promise.
     * Added to make static imports possible.
     *
     * @param componentType component type of list
     * @param timeout       timeout
     * @param time          time
     * @param <T>           promise a list of type T
     * @return new Promise for a list of type T
     */
    @SuppressWarnings("unused")
    static <T> ReplayPromise<List<T>> replayPromiseList(Class<T> componentType,
                                                        final Duration timeout,
                                                        final long time) {
        return new ReplayPromiseImpl<>(timeout, time);
    }

    /**
     * Generic collection promise.
     * Added to make static imports possible.
     *
     * @param componentType component type of collection
     * @param timeout       timeout
     * @param time          time
     * @param <T>           promise a collection of type T
     * @return new Promise for a collection of type T
     */
    @SuppressWarnings("unused")
    static <T> ReplayPromise<Collection<T>> replayPromiseCollection(Class<T> componentType,
                                                                    final Duration timeout,
                                                                    final long time) {
        return new ReplayPromiseImpl<>(timeout, time);
    }

    /**
     * Generic map promise.
     * Added to make static imports possible.
     *
     * @param keyType   type of map key
     * @param valueType type of map value
     * @param timeout   timeout
     * @param time      time
     * @param <K>       promise a map of  key type K
     * @param <V>       promise a map of  value type V
     * @return new Promise for a collection of type T
     */
    @SuppressWarnings("unused")
    static <K, V> ReplayPromise<Map<K, V>> replayPromiseMap(Class<K> keyType, Class<V> valueType,
                                                            final Duration timeout,
                                                            final long time) {
        return new ReplayPromiseImpl<>(timeout, time);
    }

    /**
     * Generic set promise.
     * Added to make static imports possible.
     *
     * @param componentType component type of set
     * @param timeout       timeout
     * @param time          time
     * @param <T>           promise a set of type T
     * @return new Promise for a set of type T
     */
    @SuppressWarnings("unused")
    static <T> ReplayPromise<Set<T>> replayPromiseSet(Class<T> componentType,
                                                      final Duration timeout,
                                                      final long time) {
        return new ReplayPromiseImpl<>(timeout, time);
    }

    /* Blocking. */


    /**
     * Returns a String promise
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     * Added to make static imports possible.
     *
     * @param duration duration of block
     * @return returns a string promise
     */
    static Promise<String> blockingPromiseString(final Duration duration) {
        return new BlockingPromise<>(duration);
    }

    /**
     * Returns a Integer promise
     * Added to make static imports possible.
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     *
     * @param duration duration of block
     * @return returns an int promise
     */
    static Promise<Integer> blockingPromiseInt(final Duration duration) {
        return new BlockingPromise<>(duration);
    }


    /**
     * Returns a Long promise
     * Added to make static imports possible.
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     *
     * @param duration duration of block
     * @return returns an long promise
     */
    static Promise<Long> blockingPromiseLong(final Duration duration) {
        return new BlockingPromise<>(duration);
    }

    /**
     * Returns a Double promise
     * Added to make static imports possible.
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     *
     * @param duration duration of block
     * @return returns an double promise
     */
    static Promise<Double> blockingPromiseDouble(final Duration duration) {
        return new BlockingPromise<>(duration);
    }


    /**
     * Returns a Float promise
     * Added to make static imports possible.
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     *
     * @param duration duration of block
     * @return returns an float promise
     */
    static Promise<Float> blockingPromiseFloat(final Duration duration) {
        return new BlockingPromise<>(duration);
    }

    /**
     * Returns a void promise for notify of outcome but no value returned.
     * <p>
     * Callback replyDone can be used instead of replay on service side.
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     *
     * @param duration duration of block
     * @return void promise
     */
    static Promise<Void> blockingPromiseNotify(final Duration duration) {
        return new BlockingPromise<>(duration);
    }

    /**
     * Boolean promise
     * Added to make static imports possible.
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     *
     * @param duration duration of block
     * @return promises a boolean
     */
    static Promise<Boolean> blockingPromiseBoolean(final Duration duration) {
        return new BlockingPromise<>(duration);
    }


    /**
     * Generic promise.
     * Added to make static imports possible.
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     *
     * @param duration duration of block
     * @param cls      type
     * @param <T>      promise of a result of T
     * @return new Promise of type T
     */
    @SuppressWarnings("unused")
    static <T> Promise<T> blockingPromise(Class<T> cls, final Duration duration) {
        return new BlockingPromise<>(duration);
    }


    /**
     * Generic list promise.
     * Added to make static imports possible.
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     *
     * @param duration      duration of block
     * @param componentType component type of list
     * @param <T>           promise a list of type T
     * @return new Promise for a list of type T
     */
    @SuppressWarnings("unused")
    static <T> Promise<List<T>> blockingPromiseList(Class<T> componentType, final Duration duration) {
        return new BlockingPromise<>(duration);
    }

    /**
     * Generic collection promise.
     * Added to make static imports possible.
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     *
     * @param duration      duration of block
     * @param componentType component type of collection
     * @param <T>           promise a collection of type T
     * @return new Promise for a collection of type T
     */
    @SuppressWarnings("unused")
    static <T> Promise<Collection<T>> blockingPromiseCollection(Class<T> componentType, final Duration duration) {
        return new BlockingPromise<>(duration);
    }

    /**
     * Generic map promise.
     * Added to make static imports possible.
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     *
     * @param duration  duration of block
     * @param keyType   type of map key
     * @param valueType type of map value
     * @param <K>       promise a map of  key type K
     * @param <V>       promise a map of  value type V
     * @return new Promise for a collection of type T
     */
    @SuppressWarnings("unused")
    static <K, V> Promise<Map<K, V>> blockingPromiseMap(final Class<K> keyType,
                                                        final Class<V> valueType,
                                                        final Duration duration) {
        return new BlockingPromise<>(duration);
    }

    /**
     * Generic set promise.
     * Added to make static imports possible.
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     *
     * @param duration      duration of block
     * @param componentType component type of set
     * @param <T>           promise a set of type T
     * @return new Promise for a set of type T
     */
    @SuppressWarnings("unused")
    static <T> Promise<Set<T>> blockingPromiseSet(Class<T> componentType, final Duration duration) {
        return new BlockingPromise<>(duration);
    }


    /**
     * Returns a String promise
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     * Added to make static imports possible.
     *
     * @return returns a string promise
     */
    static Promise<String> blockingPromiseString() {
        return new BlockingPromise<>();
    }

    /**
     * Returns a Integer promise
     * Added to make static imports possible.
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     *
     * @return returns an int promise
     */
    static Promise<Integer> blockingPromiseInt() {
        return new BlockingPromise<>();
    }


    /**
     * Returns a Long promise
     * Added to make static imports possible.
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     *
     * @return returns an long promise
     */
    static Promise<Long> blockingPromiseLong() {
        return new BlockingPromise<>();
    }

    /**
     * Returns a Double promise
     * Added to make static imports possible.
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     *
     * @return returns an double promise
     */
    static Promise<Double> blockingPromiseDouble() {
        return new BlockingPromise<>();
    }


    /**
     * Returns a Float promise
     * Added to make static imports possible.
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     *
     * @return returns an float promise
     */
    static Promise<Float> blockingPromiseFloat() {
        return new BlockingPromise<>();
    }

    /**
     * Returns a void promise for notify of outcome but no value returned.
     * <p>
     * Callback replyDone can be used instead of replay on service side.
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     *
     * @return void promise
     */
    static Promise<Void> blockingPromiseNotify() {
        return new BlockingPromise<>();
    }

    /**
     * Boolean promise
     * Added to make static imports possible.
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     *
     * @return promises a boolean
     */
    static Promise<Boolean> blockingPromiseBoolean() {
        return new BlockingPromise<>();
    }


    /**
     * Generic promise.
     * Added to make static imports possible.
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     *
     * @param cls type
     * @param <T> promise of a result of T
     * @return new Promise of type T
     */
    @SuppressWarnings("unused")
    static <T> Promise<T> blockingPromise(Class<T> cls) {
        return new BlockingPromise<>();
    }


    /**
     * Generic list promise.
     * Added to make static imports possible.
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     *
     * @param componentType component type of list
     * @param <T>           promise a list of type T
     * @return new Promise for a list of type T
     */
    @SuppressWarnings("unused")
    static <T> Promise<List<T>> blockingPromiseList(Class<T> componentType) {
        return new BlockingPromise<>();
    }

    /**
     * Generic collection promise.
     * Added to make static imports possible.
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     *
     * @param componentType component type of collection
     * @param <T>           promise a collection of type T
     * @return new Promise for a collection of type T
     */
    @SuppressWarnings("unused")
    static <T> Promise<Collection<T>> blockingPromiseCollection(Class<T> componentType) {
        return new BlockingPromise<>();
    }

    /**
     * Generic map promise.
     * Added to make static imports possible.
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     *
     * @param keyType   type of map key
     * @param valueType type of map value
     * @param <K>       promise a map of  key type K
     * @param <V>       promise a map of  value type V
     * @return new Promise for a collection of type T
     */
    @SuppressWarnings("unused")
    static <K, V> Promise<Map<K, V>> blockingPromiseMap(final Class<K> keyType,
                                                        final Class<V> valueType) {
        return new BlockingPromise<>();
    }

    /**
     * Generic set promise.
     * Added to make static imports possible.
     * Create a blocking promise.
     * NOTE BLOCKING PROMISES ARE FOR LEGACY INTEGRATION AND TESTING ONLY!!!
     *
     * @param componentType component type of set
     * @param <T>           promise a set of type T
     * @return new Promise for a set of type T
     */
    @SuppressWarnings("unused")
    static <T> Promise<Set<T>> blockingPromiseSet(Class<T> componentType) {
        return new BlockingPromise<>();
    }


    /**
     * Create an invokable promise.
     * After you create a promise you register its then(...) and catchError(...) and then you use it to
     * handle a callback.
     *
     * @param <T>             type of result
     * @param promiseConsumer promise consumer so you can call reject or resolve on the service side
     * @return new promise
     */
    static <T> Promise<T> invokablePromise(Consumer<Promise<T>> promiseConsumer) {
        return new InvokerPromise<>(promiseConsumer);
    }
}
