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
package org.kie.dmn.feel.util;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class DynamicTypeUtils {
    
    public static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    public static <K, U> Collector<Map.Entry<K, U>, ?, Map<K, U>> toMap() {
        return Collectors.toMap(x -> x.getKey(), x -> x.getValue());
    }
    
    @SafeVarargs
    public static <K, V> Map<K, V> mapOf(Map.Entry<K, V>... attributes) {
        return prototype(attributes);
    }
    
    @SafeVarargs
    public static <K, V> Map<K, V> prototype(Map.Entry<K, V>... attributes) {
        // as Stream.of(attributes).collect(toMap()); might fail due to some value=null, because toMap() uses java.util.HashMap.merge(HashMap.java:1224)
        // need avoid Stream API
        Map<K, V> result = new HashMap<>();
        for ( Entry<K, V> entry : attributes ) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}

