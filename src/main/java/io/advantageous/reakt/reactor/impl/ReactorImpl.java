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

import io.advantageous.reakt.promise.Promise;
import io.advantageous.reakt.promise.Promises;
import io.advantageous.reakt.promise.ReplayPromise;
import io.advantageous.reakt.reactor.Reactor;
import io.advantageous.reakt.reactor.TimeSource;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.stream.Collectors;

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
        final ReplayPromise<T> promise = Promises.replayPromise(defaultTimeout, timeSource.getTime());
        return addPromiseToProcessingQueue(promise);
    }


    @Override
    public <T> Promise<T> promise(final Duration timeout) {
        final ReplayPromise<T> promise = Promises.replayPromise(timeout, timeSource.getTime());
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
                Promises.allReplay(timeout, timeSource.getTime(), promises)
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
                Promises.allReplay(timeout, timeSource.getTime(), promises)
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
                Promises.anyReplay(timeout, timeSource.getTime(), promises)
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
                Promises.anyReplay(timeout, timeSource.getTime(), promises)
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
        return addPromiseToProcessingQueue(Promises.replayPromiseString(defaultTimeout, currentTime));
    }

    @Override
    public Promise<Integer> promiseInt() {
        return addPromiseToProcessingQueue(Promises.replayPromiseInt(defaultTimeout, currentTime));
    }

    @Override
    public Promise<Long> promiseLong() {
        return addPromiseToProcessingQueue(Promises.replayPromiseLong(defaultTimeout, currentTime));
    }

    @Override
    public Promise<Double> promiseDouble() {
        return addPromiseToProcessingQueue(Promises.replayPromiseDouble(defaultTimeout, currentTime));
    }

    @Override
    public Promise<Float> promiseFloat() {
        return addPromiseToProcessingQueue(Promises.replayPromiseFloat(defaultTimeout, currentTime));
    }

    @Override
    public Promise<Void> promiseNotify() {
        return addPromiseToProcessingQueue(Promises.replayPromiseNotify(defaultTimeout, currentTime));
    }

    @Override
    public Promise<Boolean> promiseBoolean() {
        return addPromiseToProcessingQueue(Promises.replayPromiseBoolean(defaultTimeout, currentTime));
    }

    @Override
    public <T> Promise<T> promise(Class<T> cls) {
        return addPromiseToProcessingQueue(Promises.replayPromise(cls, defaultTimeout, currentTime));
    }

    @Override
    public <T> Promise<List<T>> promiseList(Class<T> componentType) {
        return addPromiseToProcessingQueue(Promises.replayPromiseList(componentType,
                defaultTimeout, currentTime));
    }

    @Override
    public <T> Promise<Collection<T>> promiseCollection(Class<T> componentType) {
        return addPromiseToProcessingQueue(Promises.replayPromiseCollection(componentType,
                defaultTimeout, currentTime));
    }

    @Override
    public <K, V> Promise<Map<K, V>> promiseMap(Class<K> keyType, Class<V> valueType) {

        return addPromiseToProcessingQueue(Promises.replayPromiseMap(keyType, valueType,
                defaultTimeout, currentTime));
    }

    @Override
    public <T> Promise<Set<T>> promiseSet(Class<T> componentType) {
        return addPromiseToProcessingQueue(Promises.replayPromiseSet(componentType,
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
        replyPromiseQueue.addAll(notCompletedPromises);

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
