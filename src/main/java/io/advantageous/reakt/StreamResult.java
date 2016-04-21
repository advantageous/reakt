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

/**
 * The result of an async operations with optional callbacks for cancel and request more.
 *
 * @param <T> Type of result.
 * @author Rick Hightower
 */
public interface StreamResult<T> extends Result<T> {

    /**
     * Request more results
     *
     * @param n number of results that you are requesting.
     */
    void request(long n);

    /**
     * Stop sending results.
     */
    void cancel();
}
