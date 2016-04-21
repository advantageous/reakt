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
    public void testErrorConsumer() throws Exception {

        TestService testService = new TestService();
        Result<Employee>[] results = new Result[1];
        testService.errorConsumer(result -> {
            results[0] = result;

        });
        assertTrue(results[0].complete());
        assertTrue(results[0].failure());
        assertFalse(results[0].success());
    }


    @Test
    public void testConsumer() throws Exception {

        TestService testService = new TestService();
        Result<Employee>[] results = new Result[1];
        Employee[] employee = new Employee[1];
        testService.simpleConsumer(result -> {
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
    public void testNoReturn() throws Exception {

        TestService testService = new TestService();
        Result<Employee>[] results = new Result[1];
        Employee[] employee = new Employee[1];
        testService.simpleNoReturn(result -> {
            results[0] = result;
            result.then(e -> employee[0] = e).catchError(error -> {
                System.err.println(error.getMessage());
            });
        });

        assertTrue(results[0].complete());
        assertFalse(results[0].failure());
        assertTrue(results[0].success());
        assertNull(employee[0]);
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

            callback.resolve(new Employee("Rick"));
        }

        public void simpleNoReturn(Callback<Employee> callback) {

            callback.resolve();
        }

        public void simpleConsumer(Callback<Employee> callback) {
            callback.consumer().accept(new Employee("Rick"));
        }


        public void error(Callback<Employee> callback) {
            callback.reject("Error");
        }


        public void errorConsumer(Callback<Employee> callback) {
            callback.errorConsumer().accept(new IllegalStateException("Error"));
        }

        public void exception(Callback<Employee> callback) {
            callback.reject("force exception", new IllegalStateException("Error"));
        }
    }
}