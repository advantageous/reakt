package io.advantageous.reakt;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BreakerTest {

    @Test
    public void testBroken() {

        AtomicBoolean okCalled = new AtomicBoolean();
        final Breaker<Object> broken = Breaker.broken();
        broken.ifOk(o -> okCalled.set(true));

        assertFalse(okCalled.get());
        assertTrue(broken.isBroken());
        assertFalse(broken.isOk());
    }


    @Test
    public void testOK() {

        AtomicBoolean okCalled = new AtomicBoolean();
        final Breaker<Object> ok = Breaker.operational(new Object());
        ok.ifOk(o -> okCalled.set(true));

        assertTrue(okCalled.get());
        assertFalse(ok.isBroken());
        assertTrue(ok.isOk());
    }

}