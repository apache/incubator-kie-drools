/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.persistence.api;

import java.util.EnumSet;
import java.util.Set;

import org.kie.kogito.persistence.api.query.Query;

import io.smallrye.mutiny.Multi;

public interface StorageFetcher<K, V> {
    /**
     * Adds a listener on the create events.
     */
    Multi<V> objectCreatedListener();

    /**
     * Adds a listener on the update events.
     */
    Multi<V> objectUpdatedListener();

    /**
     * Adds a listener on the remove events.
     */
    Multi<K> objectRemovedListener();

    /**
     * Gets the `Query` object to query the storage.
     *
     * @return The `Query` instance.
     */
    Query<V> query();

    default Set<StorageServiceCapability> capabilities() {
        return EnumSet.noneOf(StorageServiceCapability.class);
    }

    /**
     * Gets an element by key. If the element is not present in the storage, then `null` is returned.
     *
     * @param key The key.
     * @return The element.
     */
    V get(K key);

    /**
     * Erase all the elements in the storage.
     */
    void clear();

}
