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

import java.util.Map;

public interface Storage<K, V> extends StorageFetcher<K, V> {
    /**
     * Puts an element with a key. If an element with the same key is already present in the storage, then it is replaced.
     *
     * @param key The key.
     * @param value The value.
     * @return The value.
     */
    V put(K key, V value);

    /**
     * Removes an element by key. If the element is not present in the storage, then `null` is returned.
     *
     * @param key The key.
     * @return The removed object.
     */
    V remove(K key);

    /**
     * Checks whether the storage contains a key.
     *
     * @param key The key.
     * @return `true` if the key is present in the storage, `false` otherwise.
     */
    boolean containsKey(K key);

    /**
     * Gets the pair key-value entry set of the elements in the storage.
     *
     * @return The key-value pair set of the elements in the storage.
     */
    Map<K, V> entries();

    /**
     * Gets the root type for the storage.
     *
     * @return The root type for the storage.
     */
    String getRootType();
}
