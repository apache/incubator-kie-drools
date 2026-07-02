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

package org.optaplanner.core.impl.domain.solution.cloner;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * A thread-safe memoization that caches a calculation.
 *
 * @param <K> the parameter of the calculation
 * @param <V> the result of the calculation
 */
final class ConcurrentMemoization<K, V> extends ConcurrentHashMap<K, V> {

    /**
     * An overridden implementation that heavily favors read access over write access speed.
     * This is thread-safe.
     *
     * {@inheritDoc}
     */
    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        V value = get(key);
        if (value != null) {
            return value;
        }
        return super.computeIfAbsent(key, mappingFunction);
    }

}
