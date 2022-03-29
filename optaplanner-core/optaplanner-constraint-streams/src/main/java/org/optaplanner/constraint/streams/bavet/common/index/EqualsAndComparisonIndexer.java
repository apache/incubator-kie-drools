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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

import org.optaplanner.constraint.streams.bavet.common.Tuple;
import org.optaplanner.core.impl.score.stream.JoinerType;

public final class EqualsAndComparisonIndexer<Tuple_ extends Tuple, Value_> implements Indexer<Tuple_, Value_> {

    private final JoinerType comparisonJoinerType;
    private final Map<IndexerKey, NavigableMap<Object, Map<Tuple_, Value_>>> equalsMap = new HashMap<>();

    public EqualsAndComparisonIndexer(JoinerType comparisonJoinerType) {
        this.comparisonJoinerType = comparisonJoinerType;
    }

    @Override
    public void put(Object[] indexProperties, Tuple_ tuple, Value_ value) {
        Objects.requireNonNull(value);
        int indexPropertyCount = indexProperties.length;
        IndexerKey equalsIndexKey = new IndexerKey(indexProperties, indexPropertyCount - 1);
        Object comparisonIndexProperty = indexProperties[indexPropertyCount - 1];
        NavigableMap<Object, Map<Tuple_, Value_>> comparisonMap =
                equalsMap.computeIfAbsent(equalsIndexKey, k -> new TreeMap<>());
        Map<Tuple_, Value_> tupleMap = comparisonMap.computeIfAbsent(comparisonIndexProperty, k -> new LinkedHashMap<>());
        Value_ old = tupleMap.put(tuple, value);
        if (old != null) {
            throw new IllegalStateException("Impossible state: the tuple (" + tuple
                    + ") with indexProperties (" + Arrays.toString(indexProperties)
                    + ") was already added in the indexer.");
        }
    }

    @Override
    public Value_ remove(Object[] indexProperties, Tuple_ tuple) {
        int indexPropertyCount = indexProperties.length;
        IndexerKey equalsIndexKey = new IndexerKey(indexProperties, indexPropertyCount - 1);
        NavigableMap<Object, Map<Tuple_, Value_>> comparisonMap = equalsMap.get(equalsIndexKey);
        if (comparisonMap == null) {
            throw new IllegalStateException("Impossible state: the tuple (" + tuple
                    + ") with indexProperties (" + Arrays.toString(indexProperties)
                    + ") doesn't exist in the indexer.");
        }
        Object comparisonIndexProperty = indexProperties[indexPropertyCount - 1];
        Map<Tuple_, Value_> tupleMap = comparisonMap.get(comparisonIndexProperty);
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
            comparisonMap.remove(comparisonIndexProperty);
            if (comparisonMap.isEmpty()) {
                equalsMap.remove(equalsIndexKey);
            }
        }
        return value;
    }

    @Override
    public Map<Tuple_, Value_> get(Object[] indexProperties) {
        int indexPropertyCount = indexProperties.length;
        IndexerKey equalsIndexKey = new IndexerKey(indexProperties, indexPropertyCount - 1);
        NavigableMap<Object, Map<Tuple_, Value_>> comparisonMap = equalsMap.get(equalsIndexKey);
        if (comparisonMap == null) {
            return Collections.emptyMap();
        }
        Object comparisonIndexProperty = indexProperties[indexPropertyCount - 1];
        NavigableMap<Object, Map<Tuple_, Value_>> selectedComparisonMap;
        switch (comparisonJoinerType) {
            case LESS_THAN:
                selectedComparisonMap = comparisonMap.headMap(comparisonIndexProperty, false);
                break;
            case LESS_THAN_OR_EQUAL:
                selectedComparisonMap = comparisonMap.headMap(comparisonIndexProperty, true);
                break;
            case GREATER_THAN:
                selectedComparisonMap = comparisonMap.tailMap(comparisonIndexProperty, false);
                break;
            case GREATER_THAN_OR_EQUAL:
                selectedComparisonMap = comparisonMap.tailMap(comparisonIndexProperty, true);
                break;
            default:
                throw new IllegalStateException("Impossible state: the comparisonJoinerType (" + comparisonJoinerType
                        + ") is not one of the 4 comparison types.");
        }
        if (selectedComparisonMap.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Tuple_, Value_> result = new LinkedHashMap<>();
        for (Map<Tuple_, Value_> map : selectedComparisonMap.values()) {
            result.putAll(map);
        }
        return result;
    }

}
