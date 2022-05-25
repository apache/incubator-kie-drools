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

package org.optaplanner.constraint.streams.bavet.quad;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.AbstractIfExistsNode;
import org.optaplanner.constraint.streams.bavet.common.index.IndexProperties;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.core.api.function.PentaPredicate;
import org.optaplanner.core.api.function.QuadFunction;

import org.optaplanner.constraint.streams.bavet.common.index.Indexer;

final class IfExistsQuadWithUniNode<A, B, C, D, E> extends AbstractIfExistsNode<QuadTuple<A, B, C, D>, E> {

    private final QuadFunction<A, B, C, D, IndexProperties> mappingABCD;
    private final PentaPredicate<A, B, C, D, E> filtering;

    public IfExistsQuadWithUniNode(boolean shouldExist,
                                   QuadFunction<A, B, C, D, IndexProperties> mappingABCD, Function<E, IndexProperties> mappingD,
                                   int inputStoreIndexABC, int inputStoreIndexD,
                                   Consumer<QuadTuple<A, B, C, D>> nextNodesInsert, Consumer<QuadTuple<A, B, C, D>> nextNodesRetract,
                                   Indexer<QuadTuple<A, B, C, D>, Counter<QuadTuple<A, B, C, D>>> indexerABCD,
                                   Indexer<UniTuple<E>, Set<Counter<QuadTuple<A, B, C, D>>>> indexerE,
                                   PentaPredicate<A, B, C, D, E> filtering) {
        super(shouldExist, mappingD, inputStoreIndexABC, inputStoreIndexD, nextNodesInsert, nextNodesRetract, indexerABCD,
                indexerE);
        this.mappingABCD = mappingABCD;
        this.filtering = filtering;
    }

    @Override
    protected IndexProperties createIndexProperties(QuadTuple<A, B, C, D> tuple) {
        return mappingABCD.apply(tuple.factA, tuple.factB, tuple.factC, tuple.factD);
    }

    @Override
    protected boolean isFiltering() {
        return filtering != null;
    }

    @Override
    protected boolean isFiltered(QuadTuple<A, B, C, D> leftTuple, UniTuple<E> rightTuple) {
        return filtering.test(leftTuple.factA, leftTuple.factB, leftTuple.factC, leftTuple.factD, rightTuple.factA);
    }

    @Override
    public String toString() {
        return "IfExistsQuadWithUniNode";
    }

}
