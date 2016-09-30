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

package io.advantageous.reakt;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
    public void testSupplierNotBroken() {
        final Breaker<Object> ok = Breaker.operational(new Object(), o -> false);
        assertTrue(ok.isOk());
    }

    @Test
    public void testSupplierBroken() {
        final Breaker<Object> ok = Breaker.operational(new Object(), o -> true);
        assertTrue(!ok.isOk());
    }


    @Test
    public void testSupplierOkThenBroken() {
        final AtomicInteger service = new AtomicInteger();

        final Breaker<AtomicInteger> breaker = Breaker.operational(service, theService -> !(theService.get() == 0));
        assertTrue(breaker.isOk());

        service.incrementAndGet();

        assertTrue(breaker.isBroken());

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