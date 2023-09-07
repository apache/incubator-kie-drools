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

package org.optaplanner.constraint.streams.bavet.common.index;

import java.util.Comparator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.optaplanner.constraint.streams.bavet.common.collection.TupleListEntry;
import org.optaplanner.core.impl.score.stream.JoinerType;

final class ComparisonIndexer<T, Key_ extends Comparable<Key_>> implements Indexer<T> {

    private final int indexKeyPosition;
    private final Supplier<Indexer<T>> downstreamIndexerSupplier;
    private final Comparator<Key_> keyComparator;
    private final boolean hasOrEquals;
    private final NavigableMap<Key_, Indexer<T>> comparisonMap;

    public ComparisonIndexer(JoinerType comparisonJoinerType, Supplier<Indexer<T>> downstreamIndexerSupplier) {
        this(comparisonJoinerType, 0, downstreamIndexerSupplier);
    }

    public ComparisonIndexer(JoinerType comparisonJoinerType, int indexKeyPosition,
            Supplier<Indexer<T>> downstreamIndexerSupplier) {
        this.indexKeyPosition = indexKeyPosition;
        this.downstreamIndexerSupplier = Objects.requireNonNull(downstreamIndexerSupplier);
        /*
         * For GT/GTE, the iteration order is reversed.
         * This allows us to iterate over the entire map, stopping when the threshold is reached.
         * This is done so that we can avoid using head/tail sub maps, which are expensive.
         */
        this.keyComparator =
                (comparisonJoinerType == JoinerType.GREATER_THAN || comparisonJoinerType == JoinerType.GREATER_THAN_OR_EQUAL)
                        ? KeyComparator.INSTANCE.reversed()
                        : KeyComparator.INSTANCE;
        this.hasOrEquals = comparisonJoinerType == JoinerType.GREATER_THAN_OR_EQUAL
                || comparisonJoinerType == JoinerType.LESS_THAN_OR_EQUAL;
        this.comparisonMap = new TreeMap<>(keyComparator);
    }

    @Override
    public TupleListEntry<T> put(IndexProperties indexProperties, T tuple) {
        Key_ indexKey = indexProperties.toKey(indexKeyPosition);
        // Avoids computeIfAbsent in order to not create lambdas on the hot path.
        Indexer<T> downstreamIndexer = comparisonMap.get(indexKey);
        if (downstreamIndexer == null) {
            downstreamIndexer = downstreamIndexerSupplier.get();
            comparisonMap.put(indexKey, downstreamIndexer);
        }
        return downstreamIndexer.put(indexProperties, tuple);
    }

    @Override
    public void remove(IndexProperties indexProperties, TupleListEntry<T> entry) {
        Key_ indexKey = indexProperties.toKey(indexKeyPosition);
        Indexer<T> downstreamIndexer = getDownstreamIndexer(indexProperties, indexKey, entry);
        downstreamIndexer.remove(indexProperties, entry);
        if (downstreamIndexer.isEmpty()) {
            comparisonMap.remove(indexKey);
        }
    }

    private Indexer<T> getDownstreamIndexer(IndexProperties indexProperties, Key_ indexerKey,
            TupleListEntry<T> entry) {
        Indexer<T> downstreamIndexer = comparisonMap.get(indexerKey);
        if (downstreamIndexer == null) {
            throw new IllegalStateException("Impossible state: the tuple (" + entry.getElement()
                    + ") with indexProperties (" + indexProperties
                    + ") doesn't exist in the indexer " + this + ".");
        }
        return downstreamIndexer;
    }

    // TODO clean up DRY
    @Override
    public int size(IndexProperties indexProperties) {
        int mapSize = comparisonMap.size();
        if (mapSize == 0) {
            return 0;
        }
        Key_ indexKey = indexProperties.toKey(indexKeyPosition);
        if (mapSize == 1) { // Avoid creation of the entry set and iterator.
            Map.Entry<Key_, Indexer<T>> entry = comparisonMap.firstEntry();
            int comparison = keyComparator.compare(entry.getKey(), indexKey);
            if (comparison >= 0) { // Possibility of reaching the boundary condition.
                if (comparison > 0 || !hasOrEquals) {
                    // Boundary condition reached when we're out of bounds entirely, or when GTE/LTE is not allowed.
                    return 0;
                }
            }
            return entry.getValue().size(indexProperties);
        } else {
            int size = 0;
            for (Map.Entry<Key_, Indexer<T>> entry : comparisonMap.entrySet()) {
                int comparison = keyComparator.compare(entry.getKey(), indexKey);
                if (comparison >= 0) { // Possibility of reaching the boundary condition.
                    if (comparison > 0 || !hasOrEquals) {
                        // Boundary condition reached when we're out of bounds entirely, or when GTE/LTE is not allowed.
                        break;
                    }
                }
                // Boundary condition not yet reached; include the indexer in the range.
                size += entry.getValue().size(indexProperties);
            }
            return size;
        }
    }

    @Override
    public void forEach(IndexProperties indexProperties, Consumer<T> tupleConsumer) {
        int size = comparisonMap.size();
        if (size == 0) {
            return;
        }
        Key_ indexKey = indexProperties.toKey(indexKeyPosition);
        if (size == 1) { // Avoid creation of the entry set and iterator.
            Map.Entry<Key_, Indexer<T>> entry = comparisonMap.firstEntry();
            visitEntry(indexProperties, tupleConsumer, indexKey, entry);
        } else {
            for (Map.Entry<Key_, Indexer<T>> entry : comparisonMap.entrySet()) {
                boolean boundaryReached = visitEntry(indexProperties, tupleConsumer, indexKey, entry);
                if (boundaryReached) {
                    return;
                }
            }
        }
    }

    private boolean visitEntry(IndexProperties indexProperties, Consumer<T> tupleConsumer,
            Key_ indexKey, Map.Entry<Key_, Indexer<T>> entry) {
        // Comparator matches the order of iteration of the map, so the boundary is always found from the bottom up.
        int comparison = keyComparator.compare(entry.getKey(), indexKey);
        if (comparison >= 0) { // Possibility of reaching the boundary condition.
            if (comparison > 0 || !hasOrEquals) {
                // Boundary condition reached when we're out of bounds entirely, or when GTE/LTE is not allowed.
                return true;
            }
        }
        // Boundary condition not yet reached; include the indexer in the range.
        entry.getValue().forEach(indexProperties, tupleConsumer);
        return false;
    }

    @Override
    public boolean isEmpty() {
        return comparisonMap.isEmpty();
    }

    @Override
    public String toString() {
        return "size = " + comparisonMap.size();
    }

    private static final class KeyComparator<Key_ extends Comparable<Key_>> implements Comparator<Key_> {

        private static final Comparator INSTANCE = new KeyComparator<>();

        @Override
        public int compare(Key_ o1, Key_ o2) {
            if (o1 == o2) {
                return 0;
            }
            return o1.compareTo(o2);
        }

    }

}
