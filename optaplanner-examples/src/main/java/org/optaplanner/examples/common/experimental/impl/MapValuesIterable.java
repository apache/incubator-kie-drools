/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.common.experimental.impl;

import java.util.Iterator;
import java.util.Map;

public class MapValuesIterable<Key_, Value_> implements Iterable<Value_> {
    private final Map<Key_, ? extends Value_> sourceMap;

    public MapValuesIterable(Map<Key_, ? extends Value_> sourceMap) {
        this.sourceMap = sourceMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<Value_> iterator() {
        return (Iterator<Value_>) sourceMap.values().iterator();
    }

    @Override
    public String toString() {
        return "MapValuesIterable{" +
                "sourceMap=" + sourceMap +
                '}';
    }
}
