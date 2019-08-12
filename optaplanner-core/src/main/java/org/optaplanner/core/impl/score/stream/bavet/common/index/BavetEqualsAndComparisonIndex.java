/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.bavet.common.index;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.optaplanner.core.impl.score.stream.bavet.common.BavetJoinBridgeTuple;
import org.optaplanner.core.impl.score.stream.common.JoinerType;

public class BavetEqualsAndComparisonIndex<Tuple_ extends BavetJoinBridgeTuple> extends BavetIndex<Tuple_> {

    private final JoinerType comparisonJoinerType;
    private final Map<BavetIndexKey, NavigableMap<Object, Set<Tuple_>>> equalsMap = new HashMap<>();

    public BavetEqualsAndComparisonIndex(JoinerType comparisonJoinerType) {
        this.comparisonJoinerType = comparisonJoinerType;
    }

    @Override
    public void remove(Tuple_ tuple) {
        Object[] oldIndexProperties = tuple.getIndexProperties();
        BavetIndexKey oldEqualsIndexKey = new BavetIndexKey(Arrays.copyOfRange(oldIndexProperties, 0, oldIndexProperties.length - 1));
        Object oldComparisonIndexProperty = oldIndexProperties[oldIndexProperties.length - 1];
        NavigableMap<Object, Set<Tuple_>> comparisonMap = equalsMap.get(oldEqualsIndexKey);
        Set<Tuple_> tupleSet = comparisonMap.get(oldComparisonIndexProperty);
        boolean removed = tupleSet.remove(tuple);
        if (!removed) {
            throw new IllegalStateException("Impossible state: the fact (" + tuple.getFactsString()
                    + ")'s tuple cannot be removed in the index from the tupleSet (" + tupleSet + ").");
        }
        if (tupleSet.isEmpty()) {
            comparisonMap.remove(oldComparisonIndexProperty);
            if (comparisonMap.isEmpty()) {
                equalsMap.remove(oldEqualsIndexKey);
            }
        }
        tuple.setIndexProperties(null);
    }

    @Override
    public void put(Object[] indexProperties, Tuple_ tuple) {
        BavetIndexKey equalsIndexKey = new BavetIndexKey(Arrays.copyOfRange(indexProperties, 0, indexProperties.length - 1));
        Object comparisonIndexProperty = indexProperties[indexProperties.length - 1];
        NavigableMap<Object, Set<Tuple_>> comparisonMap = equalsMap.computeIfAbsent(equalsIndexKey, k -> new TreeMap<>());
        Set<Tuple_> tupleSet = comparisonMap.computeIfAbsent(comparisonIndexProperty, k -> new LinkedHashSet<>());
        boolean added = tupleSet.add(tuple);
        if (!added) {
            throw new IllegalStateException("Impossible state: the fact (" + tuple.getFactsString()
                    + ") with indexProperties (" + Arrays.toString(indexProperties)
                    + ") was already added in the index to the tupleSet (" + tupleSet + ").");
        }
        tuple.setIndexProperties(indexProperties);
    }

    @Override
    public Set<Tuple_> get(Object[] indexProperties) {
        BavetIndexKey equalsIndexKey = new BavetIndexKey(Arrays.copyOfRange(indexProperties, 0, indexProperties.length - 1));
        Object comparisonIndexProperty = indexProperties[indexProperties.length - 1];
        NavigableMap<Object, Set<Tuple_>> comparisonMap = equalsMap.get(equalsIndexKey);
        if (comparisonMap == null) {
            return Collections.emptySet();
        }
        NavigableMap<Object, Set<Tuple_>> selectedComparisonMap;
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
        return selectedComparisonMap.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

}
