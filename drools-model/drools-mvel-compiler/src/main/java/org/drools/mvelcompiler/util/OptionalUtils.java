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
package org.drools.mvelcompiler.util;

import java.util.Optional;
import java.util.function.BiFunction;

public class OptionalUtils {

    private OptionalUtils() {

    }

    public static <T, K, V> Optional<V> map2(Optional<T> opt1, Optional<K> opt2, BiFunction<T, K, V> f) {
        return opt1.flatMap(t1 -> opt2.map(t2 -> f.apply(t1, t2)));
    }
}
