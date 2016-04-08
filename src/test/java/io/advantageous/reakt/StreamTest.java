package io.advantageous.reakt;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.*;

public class StreamTest {

    private static void sleep() {
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test() throws Exception {
        TestStreamService testService = new TestStreamService();
        Result<Employee>[] results = new Result[1];
        Employee[] employee = new Employee[1];
        testService.simple(result -> {
            results[0] = result;
            result.then(e -> employee[0] = e);
        });
        assertTrue(results[0].complete());
        assertFalse(results[0].failure());
        assertTrue(results[0].success());
        assertNotNull(employee[0]);
    }

    @Test
    public void testError() throws Exception {

        TestStreamService testService = new TestStreamService();
        Result<Employee>[] results = new Result[1];
        testService.error(result -> {
            results[0] = result;

        });
        assertTrue(results[0].complete());
        assertTrue(results[0].failure());
        assertFalse(results[0].success());
    }

    @Test
    public void testException() throws Exception {

        TestStreamService testService = new TestStreamService();
        Result<Employee>[] results = new Result[1];
        testService.exception(result -> {
            results[0] = result;

        });
        assertTrue(results[0].complete());
        assertTrue(results[0].failure());
        assertFalse(results[0].success());
    }

    @Test
    public void testStream() throws Exception {
        TestStreamService testService = new TestStreamService();

        CountDownLatch countDownLatch = new CountDownLatch(3);
        AtomicLong counter = new AtomicLong();
        testService.streaming(result -> {
            counter.incrementAndGet();
            countDownLatch.countDown();

        });

        countDownLatch.await();
        assertEquals(3L, counter.get());
    }

    @Test
    public void testStreamWithCancel() throws Exception {
        TestStreamService testService = new TestStreamService();

        AtomicLong counter = new AtomicLong();
        testService.streamingWithCancel(result -> {
            counter.incrementAndGet();
            result.request(5);
            if (counter.get() == 3) {
                result.cancel();
            }

        });

        sleep();
        sleep();
        sleep();
        sleep();
        sleep();
        sleep();
        assertEquals(3L, counter.get());
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

    public static class TestStreamService {

        public void simple(Stream<Employee> stream) {
            stream.complete(new Employee("Rick"));
        }


        public void streaming(final Stream<Employee> stream) {


            new Thread(() -> {
                stream.reply(new Employee("Rick"));
                sleep();
                stream.reply(new Employee("Geoff"));
                sleep();
                stream.reply(new Employee("Paul"), true);
                sleep();
            }).start();
        }


        public void streamingWithCancel(final Stream<Employee> stream) {

            AtomicBoolean cancelled = new AtomicBoolean();

            new Thread(() -> {
                if (!cancelled.get()) stream.reply(new Employee("Rick"));
                sleep();

                if (!cancelled.get())
                    stream.reply(new Employee("Geoff"), false, () -> cancelled.set(true), sendMore -> {

                    });
                sleep();
                if (!cancelled.get()) stream.reply(new Employee("Paul"), false, () -> cancelled.set(true));
                sleep();
                sleep();
                if (!cancelled.get()) stream.reply(new Employee("Alex"), true, () -> cancelled.set(true));
                sleep();
                sleep();
            }).start();
        }

        public void error(Stream<Employee> callback) {
            callback.fail("Error");
        }

        public void exception(Stream<Employee> callback) {
            callback.fail(new IllegalStateException("Error"));
        }
    }
}