package io.advantageous.reakt.impl;


import io.advantageous.reakt.Expected;
import io.advantageous.reakt.StreamResult;

import java.util.function.Consumer;

public class StreamResultImpl<T> extends ResultImpl<T> implements StreamResult<T> {
    private final boolean done;
    private final Expected<Runnable> cancelCallback;
    private final Expected<Consumer<Long>> requestMore;

    public StreamResultImpl(final Object object,
                            final boolean done,
                            final Expected<Runnable> cancelCallback,
                            final Expected<Consumer<Long>> requestMore) {
        super(object);
        this.done = done;
        this.cancelCallback = cancelCallback;
        this.requestMore = requestMore;
    }

    @Override
    public boolean complete() {
        return done;
    }

    @Override
    public void cancel() {
        cancelCallback.ifPresent(Runnable::run);
    }

    @Override
    public void request(long n) {
        requestMore.ifPresent(longConsumer -> longConsumer.accept(n));
    }
}

