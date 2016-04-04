package io.advantageous.reakt.promise;

import io.advantageous.reakt.Callback;
import io.advantageous.reakt.Value;
import org.junit.Test;

import static org.junit.Assert.*;

public class PromiseTest {

    @Test
    public void test() throws Exception {

        TestService testService = new TestService();
        Employee[] employee = new Employee[1];
        Value[] value = new Value[1];

        Promise<Employee> promise = Promise.promise();

        promise.then(e -> employee[0] = e);
        promise.thenValue(employeeValue -> value[0] = employeeValue);


        testService.simple(promise);

        promise.cancel();
        assertNotNull(promise.get());
        assertNotNull(promise.getValue());
        assertNotNull(value[0]);
        assertTrue(promise.complete());
        assertFalse(promise.failure());
        assertTrue(promise.success());
        assertNotNull(employee[0]);
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
            assertNull(promise.getValue());
            fail();
        } catch (Exception ex) {

        }


        //assertNotNull(promise.getValue());

        assertNull(employee[0]);
        assertTrue(error[0]);
        assertTrue(promise.complete());
        assertTrue(promise.failure());
        assertFalse(promise.success());
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
            callback.fail("Error");
        }

        public void exception(Callback<Employee> callback) {
            callback.fail(new IllegalStateException("Error"));
        }
    }
}