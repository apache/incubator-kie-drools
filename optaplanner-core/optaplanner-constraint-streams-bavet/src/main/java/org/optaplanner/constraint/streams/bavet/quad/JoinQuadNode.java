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

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.AbstractJoinNode;
import org.optaplanner.constraint.streams.bavet.common.index.IndexProperties;
import org.optaplanner.constraint.streams.bavet.tri.TriTuple;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.core.api.function.TriFunction;

import org.optaplanner.constraint.streams.bavet.common.index.Indexer;

final class JoinQuadNode<A, B, C, D> extends AbstractJoinNode<TriTuple<A, B, C>, D, QuadTuple<A, B, C, D>> {

    private final TriFunction<A, B, C, IndexProperties> mappingABC;
    private final int outputStoreSize;

    public JoinQuadNode(TriFunction<A, B, C, IndexProperties> mappingABC, Function<D, IndexProperties> mappingD,
                        int inputStoreIndexAB, int inputStoreIndexC,
                        Consumer<QuadTuple<A, B, C, D>> nextNodesInsert, Consumer<QuadTuple<A, B, C, D>> nextNodesRetract,
                        int outputStoreSize,
                        Indexer<TriTuple<A, B, C>, Map<UniTuple<D>, QuadTuple<A, B, C, D>>> indexerABC,
                        Indexer<UniTuple<D>, Map<TriTuple<A, B, C>, QuadTuple<A, B, C, D>>> indexerD) {
        super(mappingD, inputStoreIndexAB, inputStoreIndexC, nextNodesInsert, nextNodesRetract, indexerABC, indexerD);
        this.mappingABC = mappingABC;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected IndexProperties createIndexProperties(TriTuple<A, B, C> tuple) {
        return mappingABC.apply(tuple.factA, tuple.factB, tuple.factC);
    }

    @Override
    protected QuadTuple<A, B, C, D> createOutTuple(TriTuple<A, B, C> leftTuple, UniTuple<D> rightTuple) {
        return new QuadTuple<>(leftTuple.factA, leftTuple.factB, leftTuple.factC, rightTuple.factA, outputStoreSize);
    }

    @Override
    public String toString() {
        return "JoinQuadNode";
    }

}
