package io.advantageous.reakt;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

public class BreakerTest {

    @Test
    public void testBroken() {

        AtomicBoolean okCalled = new AtomicBoolean();

        AtomicBoolean brokenCalled = new AtomicBoolean();

        final Breaker<Object> broken = Breaker.broken();
        broken.ifOk(o -> okCalled.set(true));

        broken.ifBroken(() -> brokenCalled.set(true));

        assertFalse(okCalled.get());
        assertTrue(broken.isBroken());
        assertTrue(brokenCalled.get());
        assertFalse(broken.isOk());

        broken.cleanup(o -> {
        });
    }


    @Test
    public void testOK() {

        AtomicBoolean okCalled = new AtomicBoolean();
        AtomicBoolean brokenCalled = new AtomicBoolean();
        final Breaker<Object> ok = Breaker.operational(new Object());
        ok.ifOk(o -> okCalled.set(true));
        ok.ifBroken(() -> brokenCalled.set(true));


        assertEquals(0, ok.errorCount());
        assertTrue(okCalled.get());
        assertFalse(ok.isBroken());
        assertTrue(ok.isOk());

        try {
            ok.ifOk(o -> {
                throw new IllegalStateException("ack");
            });
            fail();
        } catch (Exception ex) {

        }

        assertEquals(1, ok.errorCount());

    }

    @Test
    public void testOKWithErrorCount() {

        AtomicBoolean okCalled = new AtomicBoolean();
        AtomicBoolean brokenCalled = new AtomicBoolean();
        final Breaker<Object> ok = Breaker.operational(new Object(), 10);
        ok.ifOk(o -> okCalled.set(true));
        ok.ifBroken(() -> brokenCalled.set(true));


        assertEquals(0, ok.errorCount());
        assertTrue(okCalled.get());
        assertFalse(ok.isBroken());
        assertTrue(ok.isOk());

        try {
            ok.ifOk(o -> {
                throw new IllegalStateException("ack");
            });
            fail();
        } catch (Exception ex) {

        }

        assertEquals(1, ok.errorCount());

        assertFalse(ok.isBroken());


        for (int index = 0; index < 20; index++) {
            try {
                ok.ifOk(o -> {
                    throw new IllegalStateException("ack");
                });
            } catch (Exception ex) {

            }
        }

        assertEquals(10, ok.errorCount());

        assertTrue(ok.isBroken());

    }


}