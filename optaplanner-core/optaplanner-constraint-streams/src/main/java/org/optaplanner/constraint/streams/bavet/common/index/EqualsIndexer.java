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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.optaplanner.constraint.streams.bavet.common.Tuple;

final class EqualsIndexer<Tuple_ extends Tuple, Value_> implements Indexer<Tuple_, Value_> {

    private final Function<IndexProperties, Object> indexerKeyFunction;
    private final Supplier<Indexer<Tuple_, Value_>> downstreamIndexerSupplier;
    private final Map<Object, Indexer<Tuple_, Value_>> downstreamIndexerMap = new HashMap<>();

    public EqualsIndexer(Function<IndexProperties, Object> indexerKeyFunction,
            Supplier<Indexer<Tuple_, Value_>> downstreamIndexerSupplier) {
        this.downstreamIndexerSupplier = Objects.requireNonNull(downstreamIndexerSupplier);
        this.indexerKeyFunction = Objects.requireNonNull(indexerKeyFunction);
    }

    @Override
    public void put(IndexProperties indexProperties, Tuple_ tuple, Value_ value) {
        Objects.requireNonNull(value);
        Indexer<Tuple_, Value_> downstreamIndexer =
                downstreamIndexerMap.computeIfAbsent(indexerKeyFunction.apply(indexProperties),
                        k -> downstreamIndexerSupplier.get());
        downstreamIndexer.put(indexProperties, tuple, value);
    }

    @Override
    public Value_ remove(IndexProperties indexProperties, Tuple_ tuple) {
        Object oldIndexKey = indexerKeyFunction.apply(indexProperties);
        Indexer<Tuple_, Value_> downstreamIndexer = downstreamIndexerMap.get(oldIndexKey);
        if (downstreamIndexer == null) {
            throw new IllegalStateException("Impossible state: the tuple (" + tuple
                    + ") with indexProperties (" + indexProperties
                    + ") doesn't exist in the indexer" + this + ".");
        }
        Value_ value = downstreamIndexer.remove(indexProperties, tuple);
        if (downstreamIndexer.isEmpty()) {
            downstreamIndexerMap.remove(oldIndexKey);
        }
        return value;
    }

    @Override
    public void visit(IndexProperties indexProperties, Consumer<Map<Tuple_, Value_>> tupleValueMapVisitor) {
        Indexer<Tuple_, Value_> downstreamIndexer = downstreamIndexerMap.get(indexerKeyFunction.apply(indexProperties));
        if (downstreamIndexer == null) {
            return;
        }
        downstreamIndexer.visit(indexProperties, tupleValueMapVisitor);
    }

    @Override
    public boolean isEmpty() {
        return downstreamIndexerMap.isEmpty();
    }

}
