package io.advantageous.reakt.promise.impl;

import io.advantageous.reakt.Result;
import io.advantageous.reakt.promise.Promise;

import java.util.concurrent.atomic.AtomicInteger;

public class PromiseUtil {

    static void all(Promise<?> parent, Promise<?>[] promises) {
        final AtomicInteger count = new AtomicInteger(promises.length);
        final Runnable runnable = () -> {
            int currentCount = count.decrementAndGet();
            if (currentCount <= 0) {
                parent.onResult(Result.result(null));
            }
        };
        for (Promise<?> promise : promises) {
            promise.whenComplete(runnable);
        }
    }
}
