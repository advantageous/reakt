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

package io.advantageous.reakt.reactor.impl;

import io.advantageous.reakt.promise.Promise;
import io.advantageous.reakt.promise.Promises;
import io.advantageous.reakt.promise.ReplayPromise;
import io.advantageous.reakt.reactor.Reactor;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

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
        AtomicInteger thenCalled = new AtomicInteger();
        Promise<String> promise1 = reactor.promise();
        Promise<String> promise2 = reactor.promise();

        final Promise<Void> promise = reactor.all(promise1, promise2);

        promise.then(s -> thenCalled.incrementAndGet());

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
        assertEquals(1, thenCalled.get());

    }


    @Test
    public void testAll2() throws InterruptedException {
        final AtomicInteger thenCalled = new AtomicInteger();
        final Promise<String> promise1 = Promises.promise();
        final Promise<String> promise2 = Promises.promise();

        final Promise<Void> promise = reactor.all(promise1, promise2).catchError(Throwable::printStackTrace);

        promise.then(s -> thenCalled.incrementAndGet());

        assertFalse(promise.complete());
        reactor.process();
        assertFalse(promise.complete());

        process();
        new Thread(() -> {
            promise1.resolve("DONE");

        }).start();

        new Thread(() -> {
            promise2.resolve("DONE");

        }).start();


        for (int index =0; index < 100; index++) {
            Thread.sleep(50);
            if (promise.complete()) break;
        }
        /** You can see the results but the events wont fire until reactor plays the replay promises. */
        assertTrue(promise.complete());
        assertTrue(promise.success());

        /** Now we see the event. */
        assertEquals(1, thenCalled.get());

    }


    @Test
    public void testAllList2() throws InterruptedException {
        final AtomicInteger thenCalled = new AtomicInteger();
        final List<Promise<String>> promiseList = new ArrayList<>();

        for (int index =0; index < 100; index++) {
            promiseList.add(Promises.promiseString());
        }

        final Promise<Void> promise = reactor.all(promiseList).catchError(Throwable::printStackTrace);

        promise.then(s -> thenCalled.incrementAndGet());

        assertFalse(promise.complete());
        reactor.process();
        assertFalse(promise.complete());

        process();

        for (Promise<String> stringPromise : promiseList) {
            new Thread(() -> {
                stringPromise.resolve("DONE");
            }).start();
        }



        for (int index =0; index < 100; index++) {
            Thread.sleep(50);
            if (promise.complete()) break;
        }
        /** You can see the results but the events wont fire until reactor plays the replay promises. */
        assertTrue(promise.complete());
        assertTrue(promise.success());

        /** Now we see the event. */
        assertEquals(1, thenCalled.get());

    }


    public Promise<String> invokeMe() {
        return Promises.invokablePromise(stringPromise ->
                new Thread(() -> {
                    stringPromise.reply("DONE");
                }).start());
    }

    @Test
    public void testAllListInvokeable() throws InterruptedException {
        final AtomicInteger thenCalled = new AtomicInteger();
        final List<Promise<String>> promiseList = new ArrayList<>();

        for (int index =0; index < 100; index++) {
            promiseList.add(invokeMe());
        }

        final Promise<Void> promise = reactor.all(promiseList);

        promise.then(s -> {
            thenCalled.incrementAndGet();
        }).catchError(Throwable::printStackTrace);

        assertFalse(promise.complete());
        reactor.process();
        assertFalse(promise.complete());

        process();

        promiseList.stream().forEach(Promise::invoke);

        for (int index =0; index < 1000; index++) {
            Thread.sleep(50);
            if (promise.complete()) break;
        }
        /** You can see the results but the events wont fire until reactor plays the replay promises. */
        assertTrue(promise.complete());
        assertTrue(promise.success());

        Thread.sleep(50);

        /** Now we see the event. */
        assertEquals(1, thenCalled.get());

    }

    private void process() {
        new Thread(() -> {
            for (int index = 0; index < 1000000; index++) {
                reactor.process(); //play it
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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