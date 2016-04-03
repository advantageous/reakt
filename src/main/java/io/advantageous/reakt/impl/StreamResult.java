package io.advantageous.reakt.impl;


public class StreamResult<T> extends ResultImpl<T> {
    private final boolean done;

    public StreamResult(Object object, boolean done) {
        super(object);
        this.done = done;
    }

    @Override
    public boolean complete() {
        return done;
    }
}

