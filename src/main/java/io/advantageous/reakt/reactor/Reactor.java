/*
 *
 *  Copyright (c) 2016. Rick Hightower, Geoff Chandler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    		http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package io.advantageous.reakt.reactor;

import io.advantageous.reakt.promise.Promise;
import io.advantageous.reakt.reactor.impl.ReactorImpl;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Ensures that tasks, repeating tasks and callbacks run in the callers thread.
 * Used with actor service models like QBit, Vertx, etc.
 *
 * @author Rick Hightower
 */
public interface Reactor {

    /**
     * Creates a default reactor.
     *
     * @return a reactor
     */
    static Reactor reactor() {
        return reactor(Duration.ofSeconds(30));
    }

    /**
     * Creates a default reactor with timeout.
     *
     * @param timeout timeout
     * @return a reactor
     */
    static Reactor reactor(final Duration timeout) {
        return reactor(timeout, System::currentTimeMillis);
    }

    /**
     * Creates a default reactor with timeout and timesource.
     *
     * @param timeout    timeout
     * @param timeSource time source
     * @return a reactor
     */
    static Reactor reactor(final Duration timeout, final TimeSource timeSource) {
        return new ReactorImpl(timeout, timeSource);
    }

    /**
     * Create a promise.
     * After you create a promise you register its then(...) and catchError(...) and then you use it to
     * handle a callback.
     * <p>
     * Creates a replay promise that is managed by this Reactor.
     *
     * @param <T> type of result
     * @return new promise
     */
    <T> Promise<T> promise();


    <T> Promise<T> promise(Duration timeout);


    /**
     * All promises must complete.
     *
     * @param promises promises
     * @return return containing promise
     */
    Promise<Void> all(final Promise<?>... promises);


    /**
     * All promises must complete.
     *
     * @param timeout  timeout
     * @param promises promises
     * @return return containing promise
     */
    Promise<Void> all(final Duration timeout, final Promise<?>... promises);

    /**
     * All promises must complete.
     *
     * @param promises promises
     * @param <T>      types of promise
     * @return return containing promise
     */
    <T> Promise<Void> all(final List<Promise<T>> promises);

    /**
     * All promises must complete.
     *
     * @param timeout  timeout
     * @param promises promises
     * @param <T>      types of promise
     * @return return containing promise
     */
    <T> Promise<Void> all(final Duration timeout, final List<Promise<T>> promises);

    /**
     * Any promises must complete.
     *
     * @param promises promises
     * @return return containing promise
     */
    Promise<Void> any(final Promise<?>... promises);

    /**
     * Any promises must complete.
     *
     * @param timeout  timeout
     * @param promises promises
     * @return return containing promise
     */
    Promise<Void> any(final Duration timeout, final Promise<?>... promises);

    /**
     * All promises must complete.
     *
     * @param promises promises
     * @param <T>      types of promise
     * @return return containing promise
     */
    <T> Promise<Void> any(final List<Promise<T>> promises);

    /**
     * All promises must complete.
     *
     * @param timeout  timeout
     * @param promises promises
     * @param <T>      types of promise
     * @return return containing promise
     */
    <T> Promise<Void> any(final Duration timeout, final List<Promise<T>> promises);

    /**
     * Add a repeating task that will run every interval
     *
     * @param interval duration of interval
     * @param runnable runnable to run.
     */
    void addRepeatingTask(final Duration interval, final Runnable runnable);

    /**
     * Add a task that will run once after the interval.
     *
     * @param afterInterval duration of interval
     * @param runnable      runnable to run.
     */
    void runTaskAfter(final Duration afterInterval, final Runnable runnable);

    /**
     * Run on this Reactor's thread as soon as you can.
     *
     * @param runnable runnable
     */
    void deferRun(final Runnable runnable);

    /**
     * Allows the reactor to process its tasks, and promises (callbacks).
     */
    void process();

    /**
     * Returns a String promise
     *
     * @return returns a string promise
     */
    Promise<String> promiseString();

    /**
     * Returns a Integer promise
     *
     * @return returns an int promise
     */
    Promise<Integer> promiseInt();

    /**
     * Returns a Long promise
     *
     * @return returns an long promise
     */
    Promise<Long> promiseLong();

    /**
     * Returns a Double promise
     *
     * @return returns an double promise
     */
    Promise<Double> promiseDouble();

    /**
     * Returns a Float promise
     *
     * @return returns an float promise
     */
    Promise<Float> promiseFloat();

    /**
     * Returns a void promise for notify of outcome but no value returned.
     * <p>
     * CallbackHandler replyDone can be used instead of replay on service side.
     *
     * @return void promise
     */
    Promise<Void> promiseNotify();

    /**
     * Boolean promise
     *
     * @return promises a boolean
     */
    Promise<Boolean> promiseBoolean();


    /**
     * Generic promise.
     *
     * @param cls type
     * @param <T> promise of a result of T
     * @return new PromiseHandler of type T
     */
    @SuppressWarnings("unused")
    <T> Promise<T> promise(final Class<T> cls);


    /**
     * Generic list promise.
     *
     * @param componentType component type of list
     * @param <T>           promise a list of type T
     * @return new PromiseHandler for a list of type T
     */
    @SuppressWarnings("unused")
    <T> Promise<List<T>> promiseList(final Class<T> componentType);

    /**
     * Generic collection promise.
     *
     * @param componentType component type of collection
     * @param <T>           promise a collection of type T
     * @return new PromiseHandler for a collection of type T
     */
    @SuppressWarnings("unused")
    <T> Promise<Collection<T>> promiseCollection(final Class<T> componentType);

    /**
     * Generic map promise.
     *
     * @param keyType   type of map key
     * @param valueType type of map value
     * @param <K>       promise a map of  key type K
     * @param <V>       promise a map of  value type V
     * @return new PromiseHandler for a collection of type T
     */
    @SuppressWarnings("unused")
    <K, V> Promise<Map<K, V>> promiseMap(final Class<K> keyType, final Class<V> valueType);

    /**
     * Generic set promise.
     *
     * @param componentType component type of set
     * @param <T>           promise a set of type T
     * @return new PromiseHandler for a set of type T
     */
    @SuppressWarnings("unused")
    <T> Promise<Set<T>> promiseSet(final Class<T> componentType);


}
