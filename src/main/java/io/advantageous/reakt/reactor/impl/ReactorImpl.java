package io.advantageous.reakt.reactor.impl;

import io.advantageous.reakt.promise.Promise;
import io.advantageous.reakt.promise.Promises;
import io.advantageous.reakt.promise.ReplayPromise;
import io.advantageous.reakt.reactor.Reactor;
import io.advantageous.reakt.reactor.TimeSource;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

public class ReactorImpl implements Reactor {

    private final Duration defaultTimeout;
    private final TimeSource timeSource;
    private final BlockingQueue<ReplayPromise> promisesQueue = new LinkedTransferQueue<>();
    private final List<ReplayPromise> notCompletedPromises = new ArrayList<>();

    public ReactorImpl(final Duration defaultTimeout,
                       final TimeSource timeSource) {
        this.defaultTimeout = defaultTimeout;
        this.timeSource = timeSource;
    }

    @Override
    public <T> Promise<T> promise() {
        final ReplayPromise<T> promise = Promises.<T>replayPromise(defaultTimeout, timeSource.getTime());
        return addPromiseToProcessingQueue(promise);
    }

    @Override
    public Promise<Void> all(Promise<?>... promises) {
        return addPromiseToProcessingQueue(
                Promises.allReplay(defaultTimeout, timeSource.getTime(), promises)
        );
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
        return addPromiseToProcessingQueue(
                Promises.allReplay(defaultTimeout, timeSource.getTime(), promises)
        );
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
        return addPromiseToProcessingQueue(
                Promises.anyReplay(defaultTimeout, timeSource.getTime(), promises)
        );
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
        return addPromiseToProcessingQueue(
                Promises.anyReplay(defaultTimeout, timeSource.getTime(), promises)
        );
    }

    @Override
    public <T> Promise<Void> any(final Duration timeout,
                                 final List<Promise<T>> promises) {
        return addPromiseToProcessingQueue(
                Promises.anyReplay(timeout, timeSource.getTime(), promises)
        );
    }

    @Override
    public void addRepeatingTask(Duration interval, Runnable runnable) {

    }

    @Override
    public void runTaskAfter(Duration afterInterval, Runnable runnable) {

    }

    @Override
    public void deferRun(Runnable runnable) {

    }

    @Override
    public void process() {

        processReplayPromises();

    }

    private void processReplayPromises() {
        notCompletedPromises.clear();
        final ReplayPromise poll = promisesQueue.poll();

        while (poll != null) {
            poll.check(timeSource.getTime());
            if (!poll.complete()) {
                notCompletedPromises.add(poll);
            }
        }
        promisesQueue.addAll(notCompletedPromises);
        notCompletedPromises.clear();
    }

    private <T> Promise<T> addPromiseToProcessingQueue(ReplayPromise<T> promise) {
        promise.afterResultProcessed(promisesQueue::add);
        return promise;
    }

}
