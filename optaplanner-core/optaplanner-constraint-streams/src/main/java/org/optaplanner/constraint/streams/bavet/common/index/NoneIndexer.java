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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import org.optaplanner.constraint.streams.bavet.common.Tuple;

public final class NoneIndexer<Tuple_ extends Tuple, Value_> implements Indexer<Tuple_, Value_> {

    private final Map<Tuple_, Value_> tupleMap = new LinkedHashMap<>();

    @Override
    public void put(IndexProperties indexProperties, Tuple_ tuple, Value_ value) {
        Objects.requireNonNull(value);
        Value_ old = tupleMap.put(tuple, value);
        if (old != null) {
            throw new IllegalStateException("Impossible state: the tuple (" + tuple
                    + ") with indexProperties (" + indexProperties
                    + ") was already added in the indexer.");
        }
    }

    @Override
    public Value_ remove(IndexProperties indexProperties, Tuple_ tuple) {
        Value_ value = tupleMap.remove(tuple);
        if (value == null) {
            throw new IllegalStateException("Impossible state: the tuple (" + tuple
                    + ") with indexProperties (" + indexProperties
                    + ") doesn't exist in the indexer.");
        }
        return value;
    }

    @Override
    public void visit(IndexProperties indexProperties, Consumer<Map<Tuple_, Value_>> tupleValueMapVisitor) {
        tupleValueMapVisitor.accept(tupleMap);
    }

    @Override
    public boolean isEmpty() {
        return tupleMap.isEmpty();
    }

}
