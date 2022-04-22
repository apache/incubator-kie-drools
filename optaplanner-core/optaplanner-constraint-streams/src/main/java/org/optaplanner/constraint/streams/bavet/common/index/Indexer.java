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
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.optaplanner.constraint.streams.bavet.bi.JoinBiNode;
import org.optaplanner.constraint.streams.bavet.common.BavetTupleState;
import org.optaplanner.constraint.streams.bavet.common.Tuple;

/**
 * An indexer for entity or fact {@code X},
 * maps a property or a combination of properties of {@code X}, denoted by {@code indexProperties},
 * to all instances of {@code X} that match those properties,
 * depending on the the indexer type (equal, lower than, ...).
 * For example for {@code {Lesson(id=1, room=A), Lesson(id=2, room=B), Lesson(id=3, room=A)}},
 * calling {@code get(room=A)} would return a map with a keySet of lesson 1 and 3.
 * <p>
 * It returns a {@code Map<X, V>} instead of {@code Set<X>} for performance,
 * to avoid doing the same hash lookup twice in the client.
 * For example {@link JoinBiNode} uses the value to store a set of child tuples justified by the X instance.
 * <p>
 * The fact X is wrapped in a Tuple, because the {@link BavetTupleState} is needed by clients of
 * {@link #visit(IndexProperties, Consumer)}.
 *
 * @param <Tuple_> For example for {@code from(A).join(B)}, the tuple is {@code UniTuple<A>} xor {@code UniTuple<B>}.
 *        For example for {@code Bi<A, B>.join(C)}, the tuple is {@code BiTuple<A, B>} xor {@code UniTuple<C>}.
 * @param <Value_> For example for {@code from(A).join(B)}, the value is {@code Set<BiTuple<A, B>>}.
 *        For example for {@code Bi<A, B>.join(C)}, the value is {@code Set<TriTuple<A, B, C>>}.
 */
public interface Indexer<Tuple_ extends Tuple, Value_> {

    /**
     * Differs from {@link Map#put(Object, Object)} because it fails if the key already exists.
     * 
     * @param indexProperties never null
     * @param tuple never null
     * @param value never null
     * @throws IllegalStateException if the indexProperties-tuple key already exists
     */
    void put(IndexProperties indexProperties, Tuple_ tuple, Value_ value);

    /**
     * Differs from {@link Map#remove(Object)} because it fails if the key does not exist.
     * 
     * @param indexProperties never null
     * @param tuple never null
     * @return never null
     * @throws IllegalStateException if the indexProperties-tuple key didn't exist
     */
    Value_ remove(IndexProperties indexProperties, Tuple_ tuple);

    /**
     * @param indexProperties never null
     * @param tupleValueMapEntryVisitor never null
     */
    void visit(IndexProperties indexProperties, BiConsumer<Tuple_, Value_> tupleValueMapEntryVisitor);

    boolean isEmpty();

}
