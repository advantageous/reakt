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

import java.util.function.Consumer;

/**
 * Replay promise ensures that the event handler callbacks (then, catchError) happen on the calling thread.
 *
 * @param <T> T value of the result.
 * @author Rick Hightower
 */
public interface ReplayPromise<T> extends PromiseHandler<T> {

    /**
     * Return true if timed out.
     * If this has timed out, it will be marked completed.
     * @param time current time
     * @return true if done
     */
    boolean checkTimeout(long time);

    /**
     * @param handler handle timeout.
     * @return this fluent
     * @throws NullPointerException if result is present and {@code handler} is null
     */
    ReplayPromise<T> onTimeout(Runnable handler);

    /**
     * Handler after the async result has been processed and data copied to this thread.
     *
     * @param handler handler
     * @return this fluent
     */
    ReplayPromise<T> afterResultProcessed(Consumer<ReplayPromise> handler);

    /**
     * Replay the promise on another thread.
     */
    void replay();
}
