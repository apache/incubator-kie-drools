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

import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.optaplanner.constraint.streams.bavet.common.Tuple;
import org.optaplanner.core.impl.score.stream.JoinerType;

final class ComparisonIndexer<Tuple_ extends Tuple, Value_> implements Indexer<Tuple_, Value_> {

    private final BiFunction<NavigableMap<Object, Indexer<Tuple_, Value_>>, Comparable, Map<Object, Indexer<Tuple_, Value_>>> submapFunction;
    private final Function<IndexProperties, Comparable> comparisonIndexPropertyFunction;
    private final Supplier<Indexer<Tuple_, Value_>> downstreamIndexerSupplier;
    private final NavigableMap<Object, Indexer<Tuple_, Value_>> comparisonMap = new TreeMap<>();

    public ComparisonIndexer(JoinerType comparisonJoinerType,
            Function<IndexProperties, Comparable> comparisonIndexPropertyFunction,
            Supplier<Indexer<Tuple_, Value_>> downstreamIndexerSupplier) {
        this.submapFunction = getSubmapFunction(comparisonJoinerType);
        this.comparisonIndexPropertyFunction = Objects.requireNonNull(comparisonIndexPropertyFunction);
        this.downstreamIndexerSupplier = Objects.requireNonNull(downstreamIndexerSupplier);
    }

    @Override
    public void put(IndexProperties indexProperties, Tuple_ tuple, Value_ value) {
        Objects.requireNonNull(value);
        Comparable comparisonIndexProperty = comparisonIndexPropertyFunction.apply(indexProperties);
        Indexer<Tuple_, Value_> downstreamIndexer =
                comparisonMap.computeIfAbsent(comparisonIndexProperty, k -> downstreamIndexerSupplier.get());
        downstreamIndexer.put(indexProperties, tuple, value);
    }

    @Override
    public Value_ remove(IndexProperties indexProperties, Tuple_ tuple) {
        Comparable comparisonIndexProperty = comparisonIndexPropertyFunction.apply(indexProperties);
        Indexer<Tuple_, Value_> downstreamIndexer = comparisonMap.get(comparisonIndexProperty);
        if (downstreamIndexer == null) {
            throw new IllegalStateException("Impossible state: the tuple (" + tuple
                    + ") with indexProperties (" + indexProperties
                    + ") doesn't exist in the indexer.");
        }
        Value_ value = downstreamIndexer.remove(indexProperties, tuple);
        if (downstreamIndexer.isEmpty()) {
            comparisonMap.remove(comparisonIndexProperty);
        }
        return value;
    }

    private BiFunction<NavigableMap<Object, Indexer<Tuple_, Value_>>, Comparable, Map<Object, Indexer<Tuple_, Value_>>>
            getSubmapFunction(JoinerType comparisonJoinerType) {
        switch (comparisonJoinerType) {
            case LESS_THAN:
                return (comparisonMap, comparisonIndexProperty) -> comparisonMap.headMap(comparisonIndexProperty, false);
            case LESS_THAN_OR_EQUAL:
                return (comparisonMap, comparisonIndexProperty) -> comparisonMap.headMap(comparisonIndexProperty, true);
            case GREATER_THAN:
                return (comparisonMap, comparisonIndexProperty) -> comparisonMap.tailMap(comparisonIndexProperty, false);
            case GREATER_THAN_OR_EQUAL:
                return (comparisonMap, comparisonIndexProperty) -> comparisonMap.tailMap(comparisonIndexProperty, true);
            default:
                throw new IllegalStateException("Impossible state: the comparisonJoinerType (" + comparisonJoinerType
                        + ") is not one of the 4 comparison types.");
        }
    }

    @Override
    public void visit(IndexProperties indexProperties, Consumer<Map<Tuple_, Value_>> tupleValueMapVisitor) {
        Comparable comparisonIndexProperty = comparisonIndexPropertyFunction.apply(indexProperties);
        Map<Object, Indexer<Tuple_, Value_>> selectedComparisonMap =
                submapFunction.apply(comparisonMap, comparisonIndexProperty);
        if (selectedComparisonMap.isEmpty()) {
            return;
        }
        for (Indexer<Tuple_, Value_> indexer : selectedComparisonMap.values()) {
            indexer.visit(indexProperties, tupleValueMapVisitor);
        }
    }

    @Override
    public boolean isEmpty() {
        return comparisonMap.isEmpty();
    }

}
