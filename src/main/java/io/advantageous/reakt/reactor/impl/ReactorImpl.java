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

package io.advantageous.reakt.reactor.impl;

import io.advantageous.reakt.Callback;
import io.advantageous.reakt.Expected;
import io.advantageous.reakt.Result;
import io.advantageous.reakt.promise.Promise;
import io.advantageous.reakt.promise.ReplayPromise;
import io.advantageous.reakt.reactor.Reactor;
import io.advantageous.reakt.reactor.TimeSource;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.advantageous.reakt.promise.Promises.*;

public class ReactorImpl implements Reactor {

    private final Duration defaultTimeout;
    private final TimeSource timeSource;
    private final BlockingQueue<ReplayPromise> inputPromiseQueue = new LinkedTransferQueue<>();
    private final BlockingQueue<ReplayPromise> replyPromiseQueue = new LinkedTransferQueue<>();
    private final BlockingQueue<Runnable> deferRuns = new LinkedTransferQueue<>();
    private final List<ReplayPromise> notCompletedPromises = new ArrayList<>();


    private BlockingQueue<FireOnceTask> fireOnceAfterTaskQueue = new LinkedTransferQueue<>();
    private BlockingQueue<RepeatingTask> repeatingTaskQueue = new LinkedTransferQueue<>();


    private List<FireOnceTask> fireOnceAfterTaskList = new ArrayList<>(1);
    private List<RepeatingTask> repeatingTaskList = new ArrayList<>(1);

    private long currentTime;


    public ReactorImpl(final Duration defaultTimeout,
                       final TimeSource timeSource) {
        this.defaultTimeout = defaultTimeout;
        final Duration checkPromiseTimeoutInterval = defaultTimeout.dividedBy(10);
        this.timeSource = timeSource;
        this.addRepeatingTask(checkPromiseTimeoutInterval, this::processPromiseTimeouts);
    }

    @Override
    public <T> Promise<T> promise() {
        final ReplayPromise<T> promise = replayPromise(defaultTimeout, timeSource.getTime());
        return addPromiseToProcessingQueue(promise);
    }


    @Override
    public <T> Promise<T> promise(final Duration timeout) {
        final ReplayPromise<T> promise = replayPromise(timeout, timeSource.getTime());
        return addPromiseToProcessingQueue(promise);
    }


    @Override
    public Promise<Void> all(Promise<?>... promises) {
        return all(defaultTimeout, promises);
    }

    @Override
    public Promise<Void> all(final Duration timeout,
                             final Promise<?>... promises) {
        return addPromiseToProcessingQueue(
                wrapAllOrAnyAndMakeInvokeable(allReplay(timeout, timeSource.getTime(), promises))
        );
    }


    @Override
    public <T> Promise<Void> all(List<Promise<T>> promises) {
        return all(defaultTimeout, promises);
    }

    @Override
    public <T> Promise<Void> all(final Duration timeout,
                                 final List<Promise<T>> promises) {
        return addPromiseToProcessingQueue(
                wrapAllOrAnyAndMakeInvokeable(allReplay(timeout, timeSource.getTime(), promises))
        );
    }


    @Override
    public Promise<Void> any(Promise<?>... promises) {
        return any(defaultTimeout, promises);
    }

    @Override
    public Promise<Void> any(final Duration timeout,
                             final Promise<?>... promises) {
        return addPromiseToProcessingQueue(
                wrapAllOrAnyAndMakeInvokeable(anyReplay(timeout, timeSource.getTime(), promises))
        );
    }

    @Override
    public <T> Promise<Void> any(List<Promise<T>> promises) {
        return any(defaultTimeout, promises);
    }

    @Override
    public <T> Promise<Void> any(final Duration timeout,
                                 final List<Promise<T>> promises) {
        return addPromiseToProcessingQueue(
                wrapAllOrAnyAndMakeInvokeable(anyReplay(timeout, timeSource.getTime(), promises))
        );
    }

    @Override
    public void addRepeatingTask(final Duration interval, final Runnable runnable) {
        repeatingTaskQueue.add(new RepeatingTask(runnable, interval.toMillis()));
    }

    @Override
    public void runTaskAfter(Duration afterInterval, Runnable runnable) {
        fireOnceAfterTaskQueue.add(new FireOnceTask(runnable, afterInterval.toMillis()));
    }

    @Override
    public void deferRun(Runnable runnable) {
        deferRuns.add(runnable);
    }

    @Override
    public void process() {
        copyTaskQueues();
        currentTime = timeSource.getTime();
        processDeferRuns();
        processRepeatingTasks();
        processFireOnceTasks();
        processAsyncPromisesReturns();
    }

    private void copyTaskQueues() {
        copyQueueToList(fireOnceAfterTaskList, fireOnceAfterTaskQueue);
        copyQueueToList(repeatingTaskList, repeatingTaskQueue);
    }

    private <T> void copyQueueToList(List<T> destinaion, Queue<T> source) {
        T item = source.poll();
        while (item != null) {
            destinaion.add(item);
            item = source.poll();
        }
    }

    @Override
    public Promise<String> promiseString() {
        return addPromiseToProcessingQueue(replayPromiseString(defaultTimeout, currentTime));
    }

    @Override
    public Promise<Integer> promiseInt() {
        return addPromiseToProcessingQueue(replayPromiseInt(defaultTimeout, currentTime));
    }

    @Override
    public Promise<Long> promiseLong() {
        return addPromiseToProcessingQueue(replayPromiseLong(defaultTimeout, currentTime));
    }

    @Override
    public Promise<Double> promiseDouble() {
        return addPromiseToProcessingQueue(replayPromiseDouble(defaultTimeout, currentTime));
    }

    @Override
    public Promise<Float> promiseFloat() {
        return addPromiseToProcessingQueue(replayPromiseFloat(defaultTimeout, currentTime));
    }

    @Override
    public Promise<Void> promiseNotify() {
        return addPromiseToProcessingQueue(replayPromiseNotify(defaultTimeout, currentTime));
    }

    @Override
    public Promise<Boolean> promiseBoolean() {
        return addPromiseToProcessingQueue(replayPromiseBoolean(defaultTimeout, currentTime));
    }

    @Override
    public <T> Promise<T> promise(Class<T> cls) {
        return addPromiseToProcessingQueue(replayPromise(cls, defaultTimeout, currentTime));
    }

    @Override
    public <T> Promise<List<T>> promiseList(Class<T> componentType) {
        return addPromiseToProcessingQueue(replayPromiseList(componentType,
                defaultTimeout, currentTime));
    }

    @Override
    public <T> Promise<Collection<T>> promiseCollection(Class<T> componentType) {
        return addPromiseToProcessingQueue(replayPromiseCollection(componentType,
                defaultTimeout, currentTime));
    }

    @Override
    public <K, V> Promise<Map<K, V>> promiseMap(Class<K> keyType, Class<V> valueType) {

        return addPromiseToProcessingQueue(replayPromiseMap(keyType, valueType,
                defaultTimeout, currentTime));
    }

    @Override
    public <T> Promise<Set<T>> promiseSet(Class<T> componentType) {
        return addPromiseToProcessingQueue(replayPromiseSet(componentType,
                defaultTimeout, currentTime));
    }

    private void processDeferRuns() {
        Runnable runnable = deferRuns.poll();
        while (runnable != null) {
            runnable.run();
            runnable = deferRuns.poll();
        }
    }

    private void processPromiseTimeouts() {
        notCompletedPromises.clear();
        ReplayPromise currentPromise = inputPromiseQueue.poll();

        try {
            while (currentPromise != null) {
                currentPromise.checkTimeout(timeSource.getTime());
                if (!currentPromise.complete()) {
                    notCompletedPromises.add(currentPromise);
                }
                currentPromise = inputPromiseQueue.poll();
            }
            inputPromiseQueue.addAll(notCompletedPromises);
        } finally {
            notCompletedPromises.clear();
        }
    }

    private void processAsyncPromisesReturns() {

        ReplayPromise poll = replyPromiseQueue.poll();

        while (poll != null) {
            poll.replay();
            poll = replyPromiseQueue.poll();
        }
    }


    private <T> Promise<T> addPromiseToProcessingQueue(ReplayPromise<T> promise) {
        inputPromiseQueue.add(promise);
        promise.afterResultProcessed(replyPromiseQueue::add);
        return promise;
    }

    public void processRepeatingTasks() {

        /* Run repeating tasks if needed. */
        repeatingTaskList.forEach(repeatingTask -> {
            if (currentTime - repeatingTask.lastTimeInvoked > repeatingTask.repeatEveryMS) {
                repeatingTask.lastTimeInvoked = currentTime;
                repeatingTask.task.run();
            }
        });
    }

    public void processFireOnceTasks() {
        if (fireOnceAfterTaskList.size() == 0) {
            return;
        }
        final List<FireOnceTask> fireOnceTasks = fireOnceAfterTaskList.stream()
                .filter(fireOnceTask -> currentTime - fireOnceTask.created > fireOnceTask.fireAfterMS)
                .collect(Collectors.toList());
        fireOnceTasks.forEach(fireOnceTask -> fireOnceTask.task.run());
        fireOnceAfterTaskList.removeAll(fireOnceTasks);
    }

    private <T> ReplayPromise<T> wrapAllOrAnyAndMakeInvokeable(ReplayPromise<T> complexPromise) {
        return new ReplayPromise<T>() {


            @Override
            public Promise<T> invoke() {
                complexPromise.invokeWithReactor(ReactorImpl.this);
                return this;
            }

            @Override
            public boolean checkTimeout(long time) {
                return complexPromise.checkTimeout(time);
            }

            @Override
            public ReplayPromise<T> onTimeout(Runnable handler) {
                complexPromise.onTimeout(handler);
                return this;
            }

            @Override
            public ReplayPromise<T> afterResultProcessed(Consumer<ReplayPromise> handler) {
                complexPromise.afterResultProcessed(handler);
                return this;
            }

            @Override
            public void replay() {
                complexPromise.replay();
            }

            @Override
            public Promise<T> then(Consumer<T> consumer) {
                complexPromise.then(consumer);
                return this;
            }

            @Override
            public Promise<T> whenComplete(Consumer<Promise<T>> doneListener) {
                complexPromise.whenComplete(doneListener);
                return this;
            }

            @Override
            public Promise<T> thenExpect(Consumer<Expected<T>> consumer) {
                complexPromise.thenExpect(consumer);
                return this;
            }

            @Override
            public <U> Promise<U> thenMap(Function<? super T, ? extends U> mapper) {
                return complexPromise.thenMap(mapper);
            }

            @Override
            public Promise<T> catchError(Consumer<Throwable> consumer) {
                complexPromise.catchError(consumer);
                return this;
            }

            @Override
            public Promise<T> invokeWithReactor(Reactor reactor) {
                complexPromise.invokeWithReactor(reactor);
                return this;
            }

            @Override
            public Promise<T> invokeWithReactor(Reactor reactor, Duration timeout) {
                complexPromise.invokeWithReactor(reactor, timeout);
                return this;
            }

            @Override
            public void onResult(Result<T> result) {
                complexPromise.onResult(result);
            }

            @Override
            public boolean success() {
                return complexPromise.success();
            }

            @Override
            public boolean complete() {
                return complexPromise.complete();
            }

            @Override
            public boolean failure() {
                return complexPromise.failure();
            }

            @Override
            public Throwable cause() {
                return complexPromise.cause();
            }

            @Override
            public Expected<T> expect() {
                return complexPromise.expect();
            }

            @Override
            public T get() {
                return complexPromise.get();
            }

            @Override
            public T orElse(T other) {
                return complexPromise.orElse(other);
            }

            @Override
            public Promise<T> freeze() {
                return complexPromise.freeze();
            }

            @Override
            public boolean isInvokable() {
                return complexPromise.isInvokable();
            }

            @Override
            public Promise<T> thenPromise(Promise<T> promise) {
                complexPromise.thenPromise(promise);
                return this;

            }

            @Override
            public Promise<T> thenCallback(Callback<T> callback) {
                return complexPromise.thenCallback(callback);
            }

            @Override
            public Promise<T> invokeWithPromise(Promise<T> promise) {
                complexPromise.invokeWithPromise(promise);
                return this;

            }

            @Override
            public Promise<T> thenSafeExpect(Consumer<Expected<T>> consumer) {
                complexPromise.thenSafeExpect(consumer);
                return this;

            }

            @Override
            public Promise<T> thenSafe(Consumer<T> consumer) {
                complexPromise.thenSafe(consumer);
                return this;

            }

            @Override
            public boolean supportsSafe() {
                return complexPromise.supportsSafe();
            }

            @Override
            public Promise<T> invokeAsBlockingPromise() {
                return complexPromise.invokeAsBlockingPromise();
            }

            @Override
            public Promise<T> invokeAsBlockingPromise(Duration duration) {
                return complexPromise.invokeAsBlockingPromise(duration);
            }

            @Override
            public void reject(Throwable error) {
                complexPromise.reject(error);
            }

            @Override
            public void reject(String errorMessage) {
                complexPromise.reject(errorMessage);
            }

            @Override
            public void reject(String errorMessage, Throwable error) {
                complexPromise.reject(errorMessage, error);
            }

            @Override
            public void reply(T result) {
                complexPromise.reply(result);
            }

            @Override
            public void replyDone() {
                complexPromise.replyDone();
            }

            @Override
            public void resolve() {
                complexPromise.resolve();
            }

            @Override
            public void resolve(T result) {
                complexPromise.resolve(result);
            }

            @Override
            public void accept(T t) {
                complexPromise.accept(t);
            }

            @Override
            public Consumer<Throwable> errorConsumer() {
                return complexPromise.errorConsumer();
            }

            @Override
            public Consumer<T> consumer() {
                return complexPromise.consumer();
            }
        };
    }

    /**
     * A repeating task.
     */
    class RepeatingTask {
        private final Runnable task;
        private final long repeatEveryMS;
        private long lastTimeInvoked;


        public RepeatingTask(Runnable task, long repeatEveryMS) {
            this.task = task;
            this.repeatEveryMS = repeatEveryMS;
        }
    }

    /**
     * Fire once task.
     */
    class FireOnceTask {
        private final Runnable task;
        private final long fireAfterMS;
        private final long created;

        public FireOnceTask(Runnable task, long fireAfterMS) {
            this.task = task;
            this.created = currentTime;
            this.fireAfterMS = fireAfterMS;
        }
    }
}
