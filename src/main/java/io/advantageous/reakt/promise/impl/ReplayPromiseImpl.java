package io.advantageous.reakt.promise.impl;

import io.advantageous.reakt.Expected;
import io.advantageous.reakt.Result;
import io.advantageous.reakt.impl.ResultImpl;
import io.advantageous.reakt.promise.ReplayPromise;

import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class ReplayPromiseImpl<T> extends BasePromise<T> implements ReplayPromise<T> {

    private final Duration timeoutDuration;
    private final long startTime;
    private Expected<Runnable> timeoutHandler;
    private Expected<Consumer<ReplayPromise>> afterResultProcessedHandler = Expected.empty();


    public ReplayPromiseImpl(final Duration timeout, final long startTime) {

        this.timeoutDuration = timeout;
        this.startTime = startTime;
    }

    @Override
    public void onResult(final Result<T> result) {

        this.result.compareAndSet(null, result);
        afterResultProcessedHandler.ifPresent(replayPromiseConsumer -> replayPromiseConsumer.accept(this));

    }

    @Override
    public boolean check(final long time) {

        if (result.get() == null) {
            if ((time - startTime) > timeoutDuration.toMillis()) {
                handleTimeout(time);
            }
        }

        final Result<T> theResult = result.get();
        if (theResult != null) {
            handleResultPresent(theResult);
            return true;
        }

        return false;
    }

    private void handleResultPresent(Result<T> theResult) {
        doOnResult(theResult);
    }

    private void handleTimeout(long time) {
        timeoutHandler.ifPresent(Runnable::run);
        result.set(new ResultImpl<>(
                new TimeoutException(String.format("Operation timed out start time %d timeout " +
                                "duration ms %d time %d elapsed time %d",
                        startTime, timeoutDuration.toMillis(), time, time - startTime))));
    }

    @Override
    public synchronized ReplayPromise<T> onTimeout(final Runnable handler) {
        timeoutHandler = Expected.of(handler);
        return this;
    }

    @Override
    public synchronized ReplayPromise<T> afterResultProcessed(Consumer<ReplayPromise> handler) {
        afterResultProcessedHandler = Expected.of(handler);
        return this;
    }
}
