/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.commons.functionals;

import java.util.HashMap;
import java.util.Map;

public class FunctionUtils {

    /**
     * Returns an <code>HashMap</code> with the given key/value pairs
     * @param k1
     * @param v1
     * @return
     */
    public static <K, V> Map<K, V> mapOf(K k1, V v1) {
        return new HashMap<K, V>() {{
            put(k1, v1);
        }};
    }

    /**
     * @param k1
     * @param v1
     * @param k2
     * @param v2
     * @return
     */
    public static <K, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2) {
        return new HashMap<K, V>() {{
            put(k1, v1);
            put(k2, v2);
        }};
    }

    /**
     * @param k1
     * @param v1
     * @param k2
     * @param v2
     * @param k3
     * @param v3
     * @return
     */
    public static <K, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2, K k3, V v3) {
        return new HashMap<K, V>() {{
            put(k1, v1);
            put(k2, v2);
            put(k3, v3);
        }};
    }

    /**
     * @param k1
     * @param v1
     * @param k2
     * @param v2
     * @param k3
     * @param v3
     * @param k4
     * @param v4
     * @return
     */
    public static <K, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return new HashMap<K, V>() {{
            put(k1, v1);
            put(k2, v2);
            put(k3, v3);
            put(k4, v4);
        }};
    }
}
