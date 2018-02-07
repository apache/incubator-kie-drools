/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.domain.common;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * A thread-safe memoization that caches a calculation.
 *
 * @param <K> the parameter of the calculation
 * @param <V> the result of the calculation
 * */
public final class ConcurrentMemoization<K, V> extends ConcurrentHashMap<K, V> {

    /**
     * An overridden implementation that heavily favors read access over write access speed.
     * This is thread-safe.
     *
     * {@inheritDoc}
     */
    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        // This might look like a Double Checked Idiom (which is broken), but it is not
        // because value is not a global variable
        V value = get(key);
        if (value != null) {
            return value;
        }
        return super.computeIfAbsent(key, mappingFunction);
    }

}
