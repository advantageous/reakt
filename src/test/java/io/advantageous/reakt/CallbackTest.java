package io.advantageous.reakt;

import org.junit.Test;

import static org.junit.Assert.*;

public class CallbackTest {

    @Test
    public void test() throws Exception {

        TestService testService = new TestService();
        Result<Employee>[] results = new Result[1];
        Employee[] employee = new Employee[1];
        testService.simple(result -> {
            results[0] = result;
            result.then(e -> employee[0] = e).catchError(error -> {
                System.err.println(error.getMessage());
            });
        });

        assertTrue(results[0].complete());
        assertFalse(results[0].failure());
        assertTrue(results[0].success());
        assertNotNull(employee[0]);
    }


    @Test
    public void testError() throws Exception {

        TestService testService = new TestService();
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

        TestService testService = new TestService();
        Result<Employee>[] results = new Result[1];
        testService.exception(result -> {
            results[0] = result;

        });
        assertTrue(results[0].complete());
        assertTrue(results[0].failure());
        assertFalse(results[0].success());
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


        public void error(Callback<Employee> callback) {
            callback.reject("Error");
        }

        public void exception(Callback<Employee> callback) {
            callback.reject("force exception", new IllegalStateException("Error"));
        }
    }
}