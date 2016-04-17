package io.advantageous.reakt.reactor.impl;

import io.advantageous.reakt.promise.Promise;
import io.advantageous.reakt.promise.ReplayPromise;
import io.advantageous.reakt.reactor.Reactor;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static io.advantageous.reakt.reactor.Reactor.reactor;
import static org.junit.Assert.*;

public class ReactorImplTest {

    private Reactor reactor;
    private TestTimer testTimer;

    @Before
    public void before() {
        testTimer = new TestTimer();
        testTimer.setTime(System.currentTimeMillis());

        reactor = reactor();
        reactor = reactor(Duration.ofSeconds(30), testTimer);
        reactor.process();
    }

    @Test
    public void testRepeatingTask() {
        AtomicLong count = new AtomicLong();
        reactor.addRepeatingTask(Duration.ofSeconds(1), count::incrementAndGet);
        assertEquals(0, count.get());
        reactor.process();
        assertEquals(1, count.get());

        testTimer.setTime(System.currentTimeMillis() + Duration.ofMillis(1001).toMillis());
        reactor.process();
        assertEquals(2, count.get());

    }


    @Test
    public void testOneShotTask() {
        AtomicLong count = new AtomicLong();
        reactor.runTaskAfter(Duration.ofSeconds(1), count::incrementAndGet);
        assertEquals(0, count.get());
        reactor.process();
        assertEquals(0, count.get());

        testTimer.setTime(System.currentTimeMillis() + Duration.ofMillis(1001).toMillis());
        reactor.process();
        assertEquals(1, count.get());


        testTimer.setTime(System.currentTimeMillis() + Duration.ofMillis(2002).toMillis());
        reactor.process();

        assertEquals(1, count.get());
    }


    @Test
    public void testDeferTask() {
        AtomicLong count = new AtomicLong();
        reactor.deferRun(count::incrementAndGet);
        assertEquals(0, count.get());

        reactor.process();
        assertEquals(1, count.get());

        testTimer.setTime(System.currentTimeMillis() + Duration.ofMillis(1001).toMillis());
        reactor.process();
        assertEquals(1, count.get());


        testTimer.setTime(System.currentTimeMillis() + Duration.ofMillis(2002).toMillis());
        reactor.process();
        assertEquals(1, count.get());
    }


    @Test
    public void testOnePromise() {
        AtomicBoolean thenCalled = new AtomicBoolean();
        Promise<String> promise = reactor.promise();
        promise.then(s -> thenCalled.set(true));

        assertFalse(promise.complete());
        reactor.process();
        assertFalse(promise.complete());

        /** Invoke the callback. */
        promise.reply("DONE");

        /** You can see the results but the events wont fire until reactor plays the replay promises. */
        assertTrue(promise.complete());
        assertTrue(promise.success());
        assertEquals("DONE", promise.get());
        /* We don't see the then(..) event yet because it won't fire until we play it. */
        assertFalse(thenCalled.get());

        reactor.process(); //play it


        /** Now we see the event. */
        assertTrue(thenCalled.get());


    }


    @Test
    public void testAll() {
        AtomicBoolean thenCalled = new AtomicBoolean();
        Promise<String> promise1 = reactor.promise();
        Promise<String> promise2 = reactor.promise();

        final Promise<Void> promise = reactor.all(promise1, promise2);

        promise.then(s -> thenCalled.set(true));

        assertFalse(promise.complete());
        reactor.process();
        assertFalse(promise.complete());

        /** Invoke the callback. */
        promise1.reply("DONE");
        promise2.reply("DONE");


        reactor.process(); //play it

        /** You can see the results but the events wont fire until reactor plays the replay promises. */
        assertTrue(promise.complete());
        assertTrue(promise.success());

        /** Now we see the event. */
        assertTrue(thenCalled.get());

    }


    @Test
    public void testAllList() {
        AtomicBoolean thenCalled = new AtomicBoolean();
        Promise<String> promise1 = reactor.promise();
        Promise<String> promise2 = reactor.promise();

        final Promise<Void> promise = reactor.all(Arrays.asList(promise1, promise2));

        promise.then(s -> thenCalled.set(true));

        assertFalse(promise.complete());
        reactor.process();
        assertFalse(promise.complete());

        /** Invoke the callback. */
        promise1.reply("DONE");
        promise2.reply("DONE");


        reactor.process(); //play it

        /** You can see the results but the events wont fire until reactor plays the replay promises. */
        assertTrue(promise.complete());
        assertTrue(promise.success());

        /** Now we see the event. */
        assertTrue(thenCalled.get());

    }


    @Test
    public void testAnyList() {
        AtomicBoolean thenCalled = new AtomicBoolean();
        Promise<String> promise1 = reactor.promise();
        Promise<String> promise2 = reactor.promise();

        final Promise<Void> promise = reactor.any(Arrays.asList(promise1, promise2));

        promise.then(s -> thenCalled.set(true));

        assertFalse(promise.complete());
        reactor.process();
        assertFalse(promise.complete());

        /** Invoke the callback. */
        promise1.reply("DONE");
        promise2.reply("DONE");


        reactor.process(); //play it

        /** You can see the results but the events wont fire until reactor plays the replay promises. */
        assertTrue(promise.complete());
        assertTrue(promise.success());

        /** Now we see the event. */
        assertTrue(thenCalled.get());

    }


    @Test
    public void testAny() {
        AtomicBoolean thenCalled = new AtomicBoolean();
        Promise<String> promise1 = reactor.promise();
        Promise<String> promise2 = reactor.promise();

        final Promise<Void> promise = reactor.any(promise1, promise2);

        promise.then(s -> thenCalled.set(true));

        assertFalse(promise.complete());
        reactor.process();
        assertFalse(promise.complete());

        /** Invoke the callback. */
        promise1.reply("DONE");
        promise2.reply("DONE");


        reactor.process(); //play it

        /** You can see the results but the events wont fire until reactor plays the replay promises. */
        assertTrue(promise.complete());
        assertTrue(promise.success());

        /** Now we see the event. */
        assertTrue(thenCalled.get());

    }

    @Test
    public void utilityMethod() {
        Promise promise;

        promise = reactor.promiseNotify();
        assertTrue(promise instanceof ReplayPromise);
        promise = reactor.promise(Employee.class);
        assertTrue(promise instanceof ReplayPromise);
        promise = reactor.promiseString();
        assertTrue(promise instanceof ReplayPromise);
        promise = reactor.promiseBoolean();
        assertTrue(promise instanceof ReplayPromise);
        promise = reactor.promiseLong();
        assertTrue(promise instanceof ReplayPromise);
        promise = reactor.promiseInt();
        assertTrue(promise instanceof ReplayPromise);
        promise = reactor.promiseFloat();
        assertTrue(promise instanceof ReplayPromise);
        promise = reactor.promiseDouble();
        assertTrue(promise instanceof ReplayPromise);
        promise = reactor.promiseList(Employee.class);
        assertTrue(promise instanceof ReplayPromise);
        promise = reactor.promiseSet(Employee.class);
        assertTrue(promise instanceof ReplayPromise);
        promise = reactor.promiseCollection(Employee.class);
        assertTrue(promise instanceof ReplayPromise);
        promise = reactor.promiseMap(String.class, Employee.class);
        assertTrue(promise instanceof ReplayPromise);
    }

    public static class Employee {
        private String id;
    }
}