/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.bavet.common.index;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import org.optaplanner.constraint.streams.bavet.common.Tuple;

public final class EqualsIndexer<Tuple_ extends Tuple, Value_> implements Indexer<Tuple_, Value_> {

    private final Map<IndexerKey, Map<Tuple_, Value_>> map = new HashMap<>();

    @Override
    public void put(Object[] indexProperties, Tuple_ tuple, Value_ value) {
        Objects.requireNonNull(value);
        Map<Tuple_, Value_> tupleMap = map.computeIfAbsent(new IndexerKey(indexProperties), k -> new LinkedHashMap<>());
        Value_ old = tupleMap.put(tuple, value);
        if (old != null) {
            throw new IllegalStateException("Impossible state: the tuple (" + tuple
                    + ") with indexProperties (" + Arrays.toString(indexProperties)
                    + ") was already added in the indexer.");
        }
    }

    @Override
    public Value_ remove(Object[] indexProperties, Tuple_ tuple) {
        IndexerKey oldIndexKey = new IndexerKey(indexProperties);
        Map<Tuple_, Value_> tupleMap = map.get(oldIndexKey);
        if (tupleMap == null) {
            throw new IllegalStateException("Impossible state: the tuple (" + tuple
                    + ") with indexProperties (" + Arrays.toString(indexProperties)
                    + ") doesn't exist in the indexer.");
        }
        Value_ value = tupleMap.remove(tuple);
        if (value == null) {
            throw new IllegalStateException("Impossible state: the tuple (" + tuple
                    + ") with indexProperties (" + Arrays.toString(indexProperties)
                    + ") doesn't exist in the indexer.");
        }
        if (tupleMap.isEmpty()) {
            map.remove(oldIndexKey);
        }
        return value;
    }

    @Override
    public void visit(Object[] indexProperties, Consumer<Map<Tuple_, Value_>> tupleValueMapVisitor) {
        Map<Tuple_, Value_> tupleMap = map.get(new IndexerKey(indexProperties));
        if (tupleMap == null) {
            return;
        }
        tupleValueMapVisitor.accept(tupleMap);
    }

    @Override
    public int countValues(Object[] indexProperties) {
        Map<Tuple_, Value_> tupleMap = map.get(new IndexerKey(indexProperties));
        if (tupleMap == null) {
            return 0;
        }
        return tupleMap.size();
    }

}
