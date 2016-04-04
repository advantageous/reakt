package io.advantageous.reakt.promise;

import io.advantageous.reakt.Callback;
import io.advantageous.reakt.Ref;
import org.junit.Test;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class PromiseTest {

    @Test
    public void test() throws Exception {

        TestService testService = new TestService();
        Employee[] employee = new Employee[1];
        Ref[] value = new Ref[1];

        Promise<Employee> promise = Promise.promise();

        promise.then(e -> employee[0] = e);
        promise.thenValue(employeeValue -> value[0] = employeeValue);


        testService.simple(promise);

        assertNotNull(promise.get());
        assertNotNull(promise.getRef());
        assertNotNull(value[0]);
        assertTrue(promise.complete());
        assertFalse(promise.failure());
        assertTrue(promise.success());
        assertNotNull(employee[0]);
    }


    @Test
    public void testAsyncWithBlockingPromise() throws Exception {

        TestService testService = new TestService();
        Employee[] employee = new Employee[1];
        Ref[] value = new Ref[1];

        /* Note this is only for legacy integration and testing. */
        Promise<Employee> promise = Promise.blockingPromise();

        promise.then(e -> employee[0] = e);
        promise.thenValue(employeeValue -> value[0] = employeeValue);


        testService.async(promise);

        assertNotNull(promise.get());
        assertNotNull(promise.getRef());
        assertTrue(promise.complete());
        assertFalse(promise.failure());
        assertTrue(promise.success());
        assertNull(promise.cause());
        assertNotNull(employee[0]);

        assertNotNull(value[0]);

    }


    @Test
    public void testAsyncWithReplayPromise() throws Exception {


        ReplayPromise<Employee> promise = Promise.replayPromise(Duration.ofMinutes(10));

        validateReplay(promise);

    }


    @Test
    public void testAsyncWithReplayPromise2() throws Exception {


        ReplayPromise<Employee> promise = Promise.replayPromise(Duration.ofMinutes(10), System.currentTimeMillis());

        validateReplay(promise);

    }

    private void validateReplay(ReplayPromise<Employee> promise) throws InterruptedException {
        TestService testService = new TestService();
        AtomicReference<Employee> employee = new AtomicReference<>();
        AtomicReference<Ref> ref = new AtomicReference<>();
        AtomicBoolean afterCalled = new AtomicBoolean();


        promise.then(employee::set);
        promise.thenValue(ref::set);
        promise.afterResultProcessed(replayPromise -> afterCalled.set(true));


        testService.async(promise);

        for (int index=0; index < 100; index++) {
            if (promise.check(System.currentTimeMillis())) {
                break;
            }
            Thread.sleep(1);

        }

        assertNotNull(promise.get());
        assertNotNull(promise.getRef());
        assertTrue(promise.complete());
        assertFalse(promise.failure());
        assertTrue(promise.success());
        assertNull(promise.cause());
        assertNotNull(employee.get());
        assertNotNull(ref.get());
        assertTrue(afterCalled.get());
    }


    @Test
    public void testAsyncHandleTimeout() throws Exception {

        TestService testService = new TestService();
        AtomicReference<Employee> employee = new AtomicReference<>();
        AtomicReference<Ref> ref = new AtomicReference<>();
        AtomicBoolean afterCalled = new AtomicBoolean();
        AtomicBoolean timeoutCalled = new AtomicBoolean();

        ReplayPromise<Employee> promise = Promise.replayPromise(Duration.ofMillis(1));

        promise.then(employee::set);
        promise.thenValue(ref::set);
        promise.afterResultProcessed(replayPromise -> afterCalled.set(true));
        promise.onTimeout(() -> timeoutCalled.set(true));


        testService.async(promise);

        for (int index=0; index < 100; index++) {
            if (promise.check(System.currentTimeMillis())) {
                break;
            }
            Thread.sleep(1);

        }

        try {
            assertNotNull(promise.get());
            fail();
        } catch (Exception ex) {

        }

        try {
            assertNotNull(promise.getRef());
            fail();
        } catch (Exception ex) {

        }

        assertTrue(promise.complete());

        assertTrue(promise.failure());
        assertFalse(promise.success());
        assertNotNull(promise.cause());
        assertNull(employee.get());
        assertNull(ref.get());
        assertTrue(afterCalled.get());
        assertTrue(timeoutCalled.get());
    }

    @Test
    public void testError() throws Exception {

        TestService testService = new TestService();
        Employee[] employee = new Employee[1];
        boolean[] error = new boolean[1];

        Promise<Employee> promise = Promise.promise();
        promise
                .then(e -> employee[0] = e)
                .catchError(throwable -> error[0] = true);

        testService.error(promise);


        try {
            assertNull(promise.get());
            fail();
        } catch (Exception ex) {

        }

        try {
            assertNull(promise.getRef());
            fail();
        } catch (Exception ex) {

        }


        //assertNotNull(promise.getRef());

        assertNull(employee[0]);
        assertTrue(error[0]);
        assertTrue(promise.complete());
        assertTrue(promise.failure());
        assertFalse(promise.success());
    }

    @Test
    public void testPrematureAccess() throws Exception {

        Employee[] employee = new Employee[1];
        boolean[] error = new boolean[1];

        Promise<Employee> promise = Promise.promise();
        promise
                .then(e -> employee[0] = e)
                .catchError(throwable -> error[0] = true);

        try {
            promise.get();
            fail();
        } catch (NoSuchElementException ex) {

        }

        try {
            promise.getRef();
            fail();
        } catch (NoSuchElementException ex) {

        }



        try {
            promise.cause();
            fail();
        } catch (NoSuchElementException ex) {

        }


        try {
            promise.failure();
            fail();
        } catch (NoSuchElementException ex) {

        }


        try {
            promise.success();
            fail();
        } catch (NoSuchElementException ex) {

        }

    }

    static class Employee {
        private final String id;

        Employee(String id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Employee employee = (Employee) o;

            return id != null ? id.equals(employee.id) : employee.id == null;

        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }
    }


    public static class TestService {

        public void simple(Callback<Employee> callback) {
            callback.reply(new Employee("Rick"));
        }


        public void async(final Callback<Employee> callback) {

            new Thread(() -> {
                callback.reply(new Employee("Rick"));
            }).start();
        }


        public void asyncError(final Callback<Employee> callback) {
            new Thread(() -> {
                callback.fail("Rick");
            }).start();
        }



        public void error(Callback<Employee> callback) {
            callback.fail("Error");
        }

        public void exception(Callback<Employee> callback) {
            callback.fail(new IllegalStateException("Error"));
        }
    }
}