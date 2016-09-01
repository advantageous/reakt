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

package io.advantageous.reakt.promise.impl;

import io.advantageous.reakt.Expected;
import io.advantageous.reakt.Result;
import io.advantageous.reakt.impl.ResultImpl;
import io.advantageous.reakt.promise.Promise;
import io.advantageous.reakt.promise.ReplayPromise;

import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ReplayPromiseImpl<T> extends BasePromise<T> implements ReplayPromise<T> {

    private final Duration timeoutDuration;
    private final long startTime;
    private Expected<Runnable> timeoutHandler = Expected.empty();
    private Expected<Consumer<ReplayPromise>> afterResultProcessedHandler = Expected.empty();
    private final AtomicBoolean replayed = new AtomicBoolean();



    public ReplayPromiseImpl(final Duration timeout, final long startTime) {

        this.timeoutDuration = timeout;
        this.startTime = startTime;
    }

    @Override
    public void onResult(final Result<T> result) {

        //Ensure this is only handled one time.
        if (this.result.compareAndSet(null, result)) {
            afterResultProcessedHandler.ifPresent(replayPromiseConsumer -> replayPromiseConsumer.accept(this));
        }

    }

    @Override
    public boolean checkTimeout(final long time) {

        if (!complete()) {
            if ((time - startTime) > timeoutDuration.toMillis()) {
                handleTimeout(time);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

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
        replay();
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


    @Override
    public void replay() {
        if (replayed.compareAndSet(false, true)) {
            handleResultPresent(result.get());
        }
    }

    @Override
    public Promise<T> freeze() {
        throw new IllegalStateException("Freeze (freeze()) only makes sense for callback " +
                "and blocking promises because replay promises are only accessed from one " +
                "thread so mutability is ok.");
    }
}
