/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.persistence.api;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.kie.kogito.persistence.api.query.Query;

public interface Storage<K, V> {

    /**
     * Adds a listener on the create events.
     *
     * @param consumer The listener.
     */
    void addObjectCreatedListener(Consumer<V> consumer);

    /**
     * Adds a listener on the update events.
     *
     * @param consumer The listener.
     */
    void addObjectUpdatedListener(Consumer<V> consumer);

    /**
     * Adds a listener on the remove events.
     *
     * @param consumer The listener.
     */
    void addObjectRemovedListener(Consumer<K> consumer);

    /**
     * Gets the `Query` object to query the storage.
     *
     * @return The `Query` instance.
     */
    Query<V> query();

    /**
     * Gets an element by key. If the element is not present in the storage, then `null` is returned.
     *
     * @param key The key.
     * @return The element.
     */
    V get(K key);

    /**
     * Puts an element with a key. If an element with the same key is already present in the storage, then it is replaced.
     *
     * @param key   The key.
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
    Set<Map.Entry<K, V>> entrySet();

    /**
     * Erase all the elements in the storage.
     */
    void clear();

    /**
     * Gets the root type for the storage.
     *
     * @return The root type for the storage.
     */
    String getRootType();
}
