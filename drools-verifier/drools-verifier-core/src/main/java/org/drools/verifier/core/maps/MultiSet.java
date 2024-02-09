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
package org.drools.verifier.core.maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiSet<K, V> {

    private Map<K, HashSet<V>> map = new HashMap<>();

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public boolean put(K key,
                       V value) {
        if (map.containsKey(key)) {
            return map.get(key).add(value);
        } else {
            HashSet<V> list = new HashSet<>();
            list.add(value);
            map.put(key, list);
            return true;
        }
    }

    public void putAllValues(K key,
                             HashSet<V> values) {
        map.put(key, values);
    }

    public boolean addAllValues(K key,
                                Collection<V> values) {
        if (map.containsKey(key)) {
            return map.get(key).addAll(values);
        } else {
            HashSet<V> set = new HashSet<>();
            set.addAll(values);
            map.put(key, set);
            return true;
        }
    }

    public Collection<V> remove(K key) {
        return map.remove(key);
    }

    public int size() {
        return map.size();
    }

    public Set<K> keys() {
        return map.keySet();
    }

    public Collection<V> get(K key) {
        return map.get(key);
    }

    public void clear() {
        map.clear();
    }

    public List<V> allValues() {
        ArrayList<V> allValues = new ArrayList<>();

        for (K k : keys()) {
            allValues.addAll(get(k));
        }

        return allValues;
    }

    public void removeValue(K k,
                            V v) {
        get(k).remove(v);
    }
}
