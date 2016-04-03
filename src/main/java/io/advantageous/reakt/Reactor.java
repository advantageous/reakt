package io.advantageous.reakt;

import io.advantageous.reakt.promise.ReplayPromise;

import java.time.Duration;

public interface Reactor {

    <T> void addReplayPromise(ReplayPromise<T> replayPromise);
    void addRepeatingTask(Duration interval, Runnable runnable);
    void runTaskAfter(Duration interval, Runnable runnable);
    void deferRun(Runnable runnable);
    void process();
}
