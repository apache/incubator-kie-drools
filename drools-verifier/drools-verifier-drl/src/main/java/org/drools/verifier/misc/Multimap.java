/**
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
package org.drools.verifier.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Multimap<K,V> {

    private final Map<K, Collection<V>> map = new HashMap<>();
    
    public Collection<V> get(K key) {
        return map.getOrDefault(key, Collections.emptyList());
    }

    public void put(K key, V value) {
        map.computeIfAbsent(key, K -> new ArrayList<>()).add(value);
    }

    public void remove(K key, V value) {
        Collection<V> values = map.get(key);
        if (values != null) {
            values.remove(value);
        }
    }

    public Iterable<V> values() {
        return map.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public Set<K> keySet() {
        return map.keySet();
    }
}
