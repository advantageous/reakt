package io.advantageous.reakt.promise.impl;

import io.advantageous.reakt.Result;
import io.advantageous.reakt.promise.Promise;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public interface PromiseUtil {

    /**
     * Does the all logic for All*Promise.
     * This promise only fires (comes back) if all of the child promises come back.
     *
     * @param parent        parent
     * @param childPromises promises that all have to come back before this promise comes back
     */
    static void all(Promise<?> parent, Promise<?>[] childPromises) {
        final AtomicInteger count = new AtomicInteger(childPromises.length);
        final Runnable runnable = () -> {
            int currentCount = count.decrementAndGet();
            if (currentCount <= 0) {
                parent.onResult(Result.result(null));
            }
        };
        for (Promise<?> promise : childPromises) {
            promise.whenComplete(runnable);
        }
    }

    /**
     * Does the any logic for Any*Promise.
     * If any child comes back, then the parent comes back.
     *
     * @param parent        parent promise
     * @param childPromises list of promises
     */
    static void any(Promise<?> parent, Promise<?>[] childPromises) {

        final AtomicBoolean done = new AtomicBoolean();
        final Runnable runnable = () -> {
            /** Only fire if the child promise is the first promise
             * so the parent does not fire multiple times. */
            if (done.compareAndSet(false, true)) {
                parent.onResult(Result.result(null));
            }
        };
        for (Promise<?> promise : childPromises) {
            promise.whenComplete(runnable);
        }
    }
}
