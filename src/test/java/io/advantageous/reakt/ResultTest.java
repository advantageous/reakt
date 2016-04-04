package io.advantageous.reakt;

import org.junit.Test;

import java.util.function.Consumer;

import static org.junit.Assert.*;

public class ResultTest {


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

    @Test
    public void testSuccess() {
        final Result<Employee> rick = Result.result(new Employee("Rick"));
        Employee[] employee = new Employee[1];
        rick.then(e -> employee[0]=e);
        assertNotNull(employee[0]);
        Value<Employee>[] employeeValue = new Value[1];

        rick.thenValue(ev -> employeeValue[0]=ev);
        assertNotNull(employeeValue[0]);
        assertTrue(employeeValue[0].isPresent());

        assertTrue(rick.complete());
        assertFalse(rick.failure());
        assertTrue(rick.success());
        rick.cancel();

    }


    @Test
    public void testFail() {
        final Result<Employee> rick = Result.error(new IllegalStateException("Rick"));
        Employee[] employee = new Employee[1];
        rick.then(e -> employee[0]=e);
        assertNull(employee[0]);
        Value<Employee>[] employeeValue = new Value[1];

        rick.thenValue(ev -> employeeValue[0]=ev);
        assertNull(employeeValue[0]);

        assertTrue(rick.complete());
        assertTrue(rick.failure());
        assertFalse(rick.success());
        rick.cancel();

        boolean [] flag = new boolean[1];

        rick.catchError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {

                flag[0] = true;
            }
        });

        assertTrue(flag[0]);


    }
}