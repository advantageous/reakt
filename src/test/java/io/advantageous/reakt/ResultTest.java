package io.advantageous.reakt;

import org.junit.Test;

import static org.junit.Assert.*;

public class ResultTest {


    @Test
    public void testSuccess() {
        final Result<Employee> rick = Result.result(new Employee("Rick"));
        Employee[] employee = new Employee[1];
        rick.then(e -> employee[0] = e);
        assertNotNull(employee[0]);
        Ref<Employee>[] employeeValue = new Ref[1];

        rick.thenRef(ev -> employeeValue[0] = ev);
        assertNotNull(employeeValue[0]);
        assertTrue(employeeValue[0].isPresent());

        assertTrue(rick.complete());
        assertFalse(rick.failure());
        assertTrue(rick.success());

    }


    @Test
    public void testFail() {
        final Result<Employee> rick = Result.error(new IllegalStateException("Rick"));
        Employee[] employee = new Employee[1];
        rick.then(e -> employee[0] = e);
        assertNull(employee[0]);
        Ref<Employee>[] employeeValue = new Ref[1];

        rick.thenRef(ev -> employeeValue[0] = ev);
        assertNull(employeeValue[0]);

        assertTrue(rick.complete());
        assertTrue(rick.failure());
        assertFalse(rick.success());

        boolean[] flag = new boolean[1];

        rick.catchError(throwable -> flag[0] = true);

        try {
            rick.get();
            fail();
        } catch (Exception e) {

        }


        try {
            rick.getRef();
            fail();
        } catch (Exception e) {

        }
        assertTrue(flag[0]);


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

}