/*
 * Copyright 2019 Red Hat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.remote;

public interface RemoteEntryPoint extends RemoteWorkingMemory {

    /**
     * Inserts a new fact into this entry point
     *
     * @param object
     *        the fact to be inserted
     *
     * @return the fact handle created for the given fact
     */
    <T> RemoteFactHandle<T> insert(T object);

    /**
     * Retracts the fact for which the given FactHandle was assigned
     * regardless if it has been explicitly or logically inserted.
     *
     * @param handle the handle whose fact is to be retracted.
     */
    <T> void delete(RemoteFactHandle<T> handle);

    /**
     * Updates the fact for which the given FactHandle was assigned with the new
     * fact set as the second parameter in this method.
     *
     * @param handle the FactHandle for the fact to be updated.
     * @param object the new value for the fact being updated.
     */
    <T> void update(RemoteFactHandle<T> handle, T object);
}
