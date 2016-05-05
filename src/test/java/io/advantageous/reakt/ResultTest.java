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

import java.io.IOException;

import static org.junit.Assert.*;

public class ResultTest {


    @Test
    public void testSuccess() {
        final Result<Employee> rick = Result.result(new Employee("Rick"));
        Employee[] employee = new Employee[1];
        rick.then(e -> employee[0] = e);
        assertNotNull(employee[0]);
        Expected<Employee>[] employeeValue = new Expected[1];

        rick.thenExpect(ev -> employeeValue[0] = ev);
        assertNotNull(employeeValue[0]);
        assertTrue(employeeValue[0].isPresent());

        assertTrue(rick.complete());
        assertFalse(rick.failure());
        assertTrue(rick.success());

    }


    @Test
    public void testFail() {
        final Result<Employee> rick = Result.error(new IOException("Rick"));
        Employee[] employee = new Employee[1];
        rick.then(e -> employee[0] = e);
        assertNull(employee[0]);
        Expected<Employee>[] employeeValue = new Expected[1];

        rick.thenExpect(ev -> employeeValue[0] = ev);
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
            rick.expect();
            fail();
        } catch (Exception e) {

        }


        assertTrue(flag[0]);

        final Employee richard = rick.orElse(new Employee("richard"));

        assertNotNull(richard);

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