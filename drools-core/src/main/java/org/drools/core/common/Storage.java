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

package org.drools.core.common;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This interface represents the minimal abstraction to store data. It might be merged into ObjectStore in the long run.
 * See drools-reliability module for alternative implementations
 * @param <K>
 * @param <V>
 */
public interface Storage<K, V> {

    V get(K key);

    V put(K key, V value);

    boolean containsKey(K key);

    V remove(K key);

    Set<K> keySet();

    default Collection<V> values() {
        return keySet().stream().map(this::get).collect(Collectors.toList());
    }

    default void clear() {
        keySet().forEach(this::remove);
    }

    default int size() {
        return keySet().size();
    }

    default boolean isEmpty() {
        return values().isEmpty();
    }

    default V getOrDefault(K key, V value) {
        return containsKey(key) ? get(key): value;
    }

    static <K, V> Storage<K,V> fromMap(Map<K, V> input) {
        return new MapStorage<>(input);
    }
}
