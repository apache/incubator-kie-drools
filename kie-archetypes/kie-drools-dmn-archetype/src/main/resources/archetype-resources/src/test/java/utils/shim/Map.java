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
#*
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
*#package utils.shim;

import java.util.Collections;
import java.util.HashMap;

public class Map {
    public static <K, V> java.util.Map<K, V> of(K key, V value) {
        java.util.Map<K, V> result = new HashMap<>();
        result.put(key, value);
        return Collections.unmodifiableMap(result);
    }
    public static <K, V> java.util.Map<K, V> of(K key1, V value1, K key2, V value2) {
        java.util.Map<K, V> result = new HashMap<>();
        result.put(key1, value1);
        result.put(key2, value2);
        return Collections.unmodifiableMap(result);
    }
    public static <K, V> java.util.Map<K, V> of(K key1, V value1, K key2, V value2, K key3, V value3) {
        java.util.Map<K, V> result = new HashMap<>();
        result.put(key1, value1);
        result.put(key2, value2);
        result.put(key3, value3);
        return Collections.unmodifiableMap(result);
    }
}