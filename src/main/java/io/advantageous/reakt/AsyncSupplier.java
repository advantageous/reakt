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
 * Represents an async supplier of results.
 *
 * @param <T> the type of results supplied by this supplier
 * @author Rick Hightower
 * @author Geoff Chandler
 */
public interface AsyncSupplier<T> {

    /**
     * Supply an item when you can.
     *
     * @param callback callback with supplied item as a result.
     */
    void get(Callback<T> callback);

}
