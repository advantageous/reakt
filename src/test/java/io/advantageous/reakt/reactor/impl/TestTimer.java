package io.advantageous.reakt.reactor.impl;

import io.advantageous.reakt.reactor.TimeSource;

public class TestTimer implements TimeSource {


    private long time;

    @Override
    public long getTime() {
        return time;
    }

    public TestTimer setTime(long time) {
        this.time = time;
        return this;
    }
}
