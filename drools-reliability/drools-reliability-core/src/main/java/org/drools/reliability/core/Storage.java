/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.reliability.core;

import java.util.Collection;
import java.util.Map;

public interface Storage<K, V> {

    V get(K key);

    V getOrDefault(K key, V value);

    V put(K key, V value);

    boolean containsKey(K key);

    V remove(K key);

    void clear();

    Collection<V> values();

    int size();

    boolean isEmpty();

    default Map<K,V> asMap() {
        return new MapWrappedStorage<>(this);
    }

}
