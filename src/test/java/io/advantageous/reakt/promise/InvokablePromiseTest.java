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

package io.advantageous.reakt.promise;

import io.advantageous.reakt.Expected;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static io.advantageous.reakt.promise.Promises.invokablePromise;
import static org.junit.Assert.*;

public class InvokablePromiseTest {


    final URI successResult = URI.create("http://localhost:8080/employeeService/");
    ServiceDiscovery serviceDiscovery;
    ServiceDiscovery asyncServiceDiscovery;
    URI empURI;
    CountDownLatch latch;
    AtomicReference<URI> returnValue;
    AtomicReference<Throwable> errorRef;

    @Before
    public void before() {
        latch = new CountDownLatch(1);
        returnValue = new AtomicReference<>();
        errorRef = new AtomicReference<>();
        serviceDiscovery = new ServiceDiscoveryImpl();
        asyncServiceDiscovery = new ServiceDiscoveryAsyncImpl();
        asyncServiceDiscovery.start();
        empURI = URI.create("marathon://default/employeeService?env=staging");
    }

    @After
    public void after() {
        asyncServiceDiscovery.shutdown();
    }

    public void await() {
        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    public void testServiceWithReturnPromiseSuccess() {
        serviceDiscovery.lookupService(empURI).then(this::handleSuccess)
                .catchError(this::handleError).invoke();
        await();
        assertNotNull("We have a return", returnValue.get());
        assertNull("There were no errors", errorRef.get());
        assertEquals("The result is the expected result", successResult, returnValue.get());
    }


    @Test
    public void testServiceWithReturnPromiseFail() {


        serviceDiscovery.lookupService(null).then(this::handleSuccess)
                .catchError(this::handleError).invoke();

        await();
        assertNull("We do not have a return", returnValue.get());
        assertNotNull("There were  errors", errorRef.get());
    }


    @Test
    public void testAsyncServiceWithReturnPromiseSuccess() {
        asyncServiceDiscovery.lookupService(empURI).then(this::handleSuccess)
                .catchError(this::handleError).invoke();
        await();
        assertNotNull("We have a return from async", returnValue.get());
        assertNull("There were no errors form async", errorRef.get());
        assertEquals("The result is the expected result form async", successResult, returnValue.get());
    }

    @Test
    public void testAsyncServiceWithInvokeWithPromise() {

        Promise<URI> promise = Promises.blockingPromise();
        promise.then(this::handleSuccess)
                .catchError(this::handleError);

        asyncServiceDiscovery.lookupService(empURI).invokeWithPromise(promise);

        final Expected<URI> expect = promise.expect();

        assertFalse(expect.isEmpty());

        assertNotNull("We have a return from async", returnValue.get());
        assertNull("There were no errors form async", errorRef.get());
        assertEquals("The result is the expected result form async", successResult, returnValue.get());
    }


    @Test
    public void testAsyncServiceWithInvokePromiseFail() {


        Promise<URI> promise = Promises.blockingPromise();
        promise.then(this::handleSuccess)
                .catchError(this::handleError);

        asyncServiceDiscovery.lookupService(null).invokeWithPromise(promise);


        try {
            promise.get();
            fail();
        } catch (Exception ex) {

        }

        assertNull("We do not have a return from async", returnValue.get());
        assertNotNull("There were  errors from async", errorRef.get());
    }

    @Test
    public void testAsyncServiceWithInvokePromiseFail2() {


        Promise<URI> promise = Promises.blockingPromise();
        promise.then(this::handleSuccess)
                .catchError(this::handleError);

        asyncServiceDiscovery.lookupService(null).thenCallback(promise).invoke();


        try {
            promise.get();
            fail();
        } catch (Exception ex) {

        }

        assertNull("We do not have a return from async", returnValue.get());
        assertNotNull("There were  errors from async", errorRef.get());
    }

    @Test
    public void testAsyncServiceWithReturnPromiseFail() {


        asyncServiceDiscovery.lookupService(null).then(this::handleSuccess)
                .catchError(this::handleError).invoke();

        await();
        assertNull("We do not have a return from async", returnValue.get());
        assertNotNull("There were  errors from async", errorRef.get());
    }

    @Test(expected = IllegalStateException.class)
    public void testServiceWithReturnPromiseSuccessInvokeTwice() {
        final Promise<URI> promise = serviceDiscovery.lookupService(empURI).then(this::handleSuccess)
                .catchError(this::handleError);
        promise.invoke();
        promise.invoke();
    }

    @Test
    public void testIsInvokable() {
        final Promise<URI> promise = serviceDiscovery.lookupService(empURI).then(this::handleSuccess)
                .catchError(this::handleError);

        assertTrue("Is this an invokable promise", promise.isInvokable());
    }


    private void handleError(Throwable error) {
        errorRef.set(error);
        latch.countDown();
    }

    private void handleSuccess(URI uri) {
        returnValue.set(uri);
        latch.countDown();
    }


    interface ServiceDiscovery {
        Promise<URI> lookupService(URI uri);

        default void shutdown() {
        }

        default void start() {
        }
    }

    class ServiceDiscoveryImpl implements ServiceDiscovery {

        @Override
        public Promise<URI> lookupService(URI uri) {
            return invokablePromise(promise -> {

                if (uri == null) {
                    promise.reject("URI was null");
                } else {
                    promise.resolve(successResult);
                }
            });
        }
    }


    class ServiceDiscoveryAsyncImpl implements ServiceDiscovery {

        final ExecutorService executorService;

        final Queue<Runnable> runnables;

        final AtomicBoolean stop;

        public ServiceDiscoveryAsyncImpl() {
            executorService = Executors.newSingleThreadExecutor();
            runnables = new LinkedTransferQueue<>();
            stop = new AtomicBoolean();
        }

        @Override
        public Promise<URI> lookupService(URI uri) {
            return invokablePromise(promise -> {
                runnables.offer(() -> {
                    if (uri == null) {
                        promise.reject("URI was null");
                    } else {
                        promise.resolve(URI.create("http://localhost:8080/employeeService/"));
                    }
                });
            });
        }

        @Override
        public void shutdown() {
            stop.set(true);
            executorService.shutdown();
        }

        @Override
        public void start() {
            executorService.submit((Runnable) () -> {

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (true) {
                    if (stop.get()) break;
                    Runnable runnable = runnables.poll();
                    while (runnable != null) {
                        runnable.run();
                        runnable = runnables.poll();
                    }
                }

            });
        }
    }
}
