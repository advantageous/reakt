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
    public void testAnyBlocking() throws Exception {

        TestService testService = new TestService();

        Promise<Employee> promise1 = Promises.promise();
        Promise<Employee> promise2 = Promises.promise();

        final Promise<Void> promise = Promises.anyBlocking(promise1, promise2);

        assertFalse(promise.complete());

        testService.async(promise2);

        assertTrue(promise.success());

    }

    @Test
    public void testAllBlocking() throws Exception {

        TestService testService = new TestService();

        Promise<Employee> promise1 = Promises.promise();
        Promise<Employee> promise2 = Promises.promise();

        final Promise<Void> promise = Promises.allBlocking(promise1, promise2);

        assertFalse(promise.complete());

        testService.async(promise1);

        assertFalse(promise.complete());

        testService.async(promise2);

        assertTrue(promise.success());

    }


    @Test
    public void testAll() throws Exception {

        /** Test service. */
        TestService testService = new TestService();

        /* Promise that expects an employee. */
        Promise<Employee> promise1 = Promises.promise();
        Promise<Employee> promise2 = Promises.promise();


        /* Promise that returns when all employees are returned. */
        final Promise<Void> promise = Promises.all(promise1, promise2);


        promise.then(nil -> System.out.println("DONE!"));

        assertFalse("Not done yet", promise.complete());

        /** Call service. */
        testService.simple(promise1);

        /** Still not done because only one service has been called. */
        assertFalse(promise.complete());

        /** Ok now second service is called. */
        testService.simple(promise2);

        /** Wait some time. */
        //...

        assertTrue(promise.complete());
        assertTrue(promise.success());

    }

    @Test
    public void testAny() throws Exception {

        /** Test service. */
        TestService testService = new TestService();

        /* Promise that expects an employee. */
        Promise<Employee> promise1 = Promises.promise();
        Promise<Employee> promise2 = Promises.promise();


        /* Promise that returns when all employees are returned. */
        final Promise<Void> promise = Promises.any(promise1, promise2);


        promise.then(nil -> System.out.println("DONE!"));

        assertFalse("Not done yet", promise.complete());

        /** Call service. */
        testService.simple(promise2);


        /** Wait some time. */
        //...

        assertTrue(promise.complete());
        assertTrue(promise.success());

    }


    @Test
    public void testAllReplay() throws Exception {

        TestService testService = new TestService();

        Promise<Employee> promise1 = Promises.promise();
        Promise<Employee> promise2 = Promises.promise();

        final ReplayPromise<Void> promise = Promises.allReplay(Duration.ofMillis(1000),
                promise1, promise2);

        assertFalse(promise.complete());

        testService.async(promise1);

        assertFalse(promise.complete());

        testService.async(promise2);


        for (int index = 0; index < 10; index++) {
            promise.check(System.currentTimeMillis());
            if (promise.complete()) break;
            Thread.sleep(10);

        }


        assertTrue(promise.complete());
        assertTrue(promise.success());

    }


    @Test
    public void testAllReplayFailFast() throws Exception {

        TestService testService = new TestService();

        Promise<Employee> promise1 = Promises.promise();
        Promise<Employee> promise2 = Promises.promise();

        final ReplayPromise<Void> promise = Promises.allReplay(Duration.ofMillis(1000),
                promise1, promise2);

        assertFalse(promise.complete());

        testService.async(promise1);

        assertFalse(promise.complete());

        testService.asyncError(promise2);


        for (int index = 0; index < 10; index++) {
            promise.check(System.currentTimeMillis());
            if (promise.complete()) break;
            Thread.sleep(10);

        }


        assertTrue(promise.complete());
        assertTrue(promise.failure());

    }

    @Test
    public void testAnyReplay() throws Exception {

        TestService testService = new TestService();

        Promise<Employee> promise1 = Promises.promise();
        Promise<Employee> promise2 = Promises.promise();

        final ReplayPromise<Void> promise = Promises.anyReplay(Duration.ofMillis(1000),
                promise1, promise2);

        assertFalse(promise.complete());

        testService.async(promise2);


        for (int index = 0; index < 10; index++) {
            promise.check(System.currentTimeMillis());
            if (promise.complete()) break;
            Thread.sleep(10);

        }


        assertTrue(promise.complete());
        assertTrue(promise.success());

    }


    @Test
    public void testAnyReplayFailFast() throws Exception {

        TestService testService = new TestService();

        Promise<Employee> promise1 = Promises.promise();
        Promise<Employee> promise2 = Promises.promise();

        final ReplayPromise<Void> promise = Promises.anyReplay(Duration.ofMillis(1000),
                promise1, promise2);

        assertFalse(promise.complete());

        testService.asyncError(promise2);


        for (int index = 0; index < 10; index++) {
            promise.check(System.currentTimeMillis());
            if (promise.complete()) break;
            Thread.sleep(10);

        }


        assertTrue(promise.complete());
        assertTrue(promise.failure());

    }

    @Test
    public void test() throws Exception {

        TestService testService = new TestService();
        Employee[] employee = new Employee[1];
        Ref[] value = new Ref[1];

        Promise<Employee> promise = Promises.<Employee>promise().then(e -> employee[0] = e)
                .thenRef(employeeValue -> value[0] = employeeValue);


        testSuccessWithPromise(testService, employee, value, promise);
    }


    @Test
    public void testFreeze() throws Exception {

        TestService testService = new TestService();
        Employee[] employee = new Employee[1];
        Ref[] value = new Ref[1];
        Promise<Employee> promise = Promises.<Employee>promise().then(e -> employee[0] = e)
                .thenRef(employeeValue -> value[0] = employeeValue).freeze();


        testSuccessWithPromise(testService, employee, value, promise);
    }

    private void testSuccessWithPromise(TestService testService, Employee[] employee, Ref[] value, Promise<Employee> promise) {


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
        Promise<Employee> promise = Promises.blockingPromise();

        promise.then(e -> employee[0] = e);
        promise.thenRef(employeeValue -> value[0] = employeeValue);


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
    public void testAsyncWithBlockingPromiseWithDuration() throws Exception {

        TestService testService = new TestService();
        Employee[] employee = new Employee[1];
        Ref[] value = new Ref[1];

        AtomicBoolean completedCalled = new AtomicBoolean();

        /* Note this is only for legacy integration and testing. */
        Promise<Employee> promise = Promises.blockingPromise(Duration.ofMillis(1000));

        promise.then(e -> employee[0] = e);
        promise.thenRef(employeeValue -> value[0] = employeeValue)
                .whenComplete((p) -> completedCalled.set(true));


        testService.async(promise);

        assertNotNull(promise.get());

        assertTrue(completedCalled.get());
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


        ReplayPromise<Employee> promise = Promises.replayPromise(Duration.ofMinutes(10));

        validateReplay(promise);

    }


    @Test
    public void testAsyncWithReplayPromise2() throws Exception {


        ReplayPromise<Employee> promise = Promises.replayPromise(Duration.ofMinutes(10), System.currentTimeMillis());

        validateReplay(promise);

    }

    private void validateReplay(ReplayPromise<Employee> promise) throws InterruptedException {
        TestService testService = new TestService();
        AtomicReference<Employee> employee = new AtomicReference<>();
        AtomicReference<Ref> ref = new AtomicReference<>();
        AtomicBoolean afterCalled = new AtomicBoolean();


        promise.then(employee::set);
        promise.thenRef(ref::set);
        promise.afterResultProcessed(replayPromise -> afterCalled.set(true));


        testService.async(promise);

        for (int index = 0; index < 100; index++) {
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

        ReplayPromise<Employee> promise = Promises.replayPromise(Duration.ofMillis(1));

        promise.then(employee::set);
        promise.thenRef(ref::set);
        promise.afterResultProcessed(replayPromise -> afterCalled.set(true));
        promise.onTimeout(() -> timeoutCalled.set(true));


        testService.asyncTimeout(promise);

        for (int index = 0; index < 100; index++) {
            if (promise.check(System.currentTimeMillis())) {
                break;
            }
            Thread.sleep(10);

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
        assertTrue(timeoutCalled.get());

        promise.check(System.currentTimeMillis());

    }

    @Test
    public void testErrorFreeze() throws Exception {

        TestService testService = new TestService();
        Employee[] employee = new Employee[1];
        boolean[] error = new boolean[1];

        final Promise<Employee> promise = Promises.<Employee>promise()
                .then(e -> employee[0] = e)
                .catchError(throwable -> error[0] = true).freeze();

        testErrorWithPromise(testService, employee, error, promise);
    }


    @Test
    public void testError() throws Exception {

        TestService testService = new TestService();
        Employee[] employee = new Employee[1];
        boolean[] error = new boolean[1];

        final Promise<Employee> promise = Promises.<Employee>promise()
                .then(e -> employee[0] = e)
                .catchError(throwable -> error[0] = true);

        testErrorWithPromise(testService, employee, error, promise);
    }

    private void testErrorWithPromise(TestService testService, Employee[] employee, boolean[] error, Promise<Employee> promise) {
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

        Promise<Employee> promise = Promises.promise();
        promise
                .then(e -> employee[0] = e)
                .catchError(throwable -> error[0] = true);

        testPrematureAccessWithPromise(promise);

    }


    @Test
    public void testFreezeImmutability() throws Exception {

        Employee[] employee = new Employee[1];
        boolean[] error = new boolean[1];

        Promise<Employee> promise = Promises.<Employee>promise().freeze();

        try {
            promise.then(e -> employee[0] = e);
            fail();
        } catch (UnsupportedOperationException oe) {

        }


        try {
            promise.thenRef(e -> {
            });
            fail();
        } catch (UnsupportedOperationException oe) {

        }

        try {
            promise.catchError(throwable -> error[0] = true);
            fail();
        } catch (UnsupportedOperationException oe) {

        }

    }

    @Test
    public void testPrematureAccessWithFreeze() throws Exception {

        Employee[] employee = new Employee[1];
        boolean[] error = new boolean[1];

        Promise<Employee> promise = Promises.<Employee>promise()
                .then(e -> employee[0] = e)
                .catchError(throwable -> error[0] = true).freeze();

        testPrematureAccessWithPromise(promise);

    }

    private void testPrematureAccessWithPromise(Promise<Employee> promise) {
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


        public void asyncTimeout(final Callback<Employee> callback) {

            new Thread(() -> {
                try {
                    Thread.sleep(10_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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