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

package org.optaplanner.constraint.streams.bavet.tri;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.bi.BiTuple;
import org.optaplanner.constraint.streams.bavet.common.AbstractJoinNode;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.common.index.IndexProperties;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;

final class JoinTriNode<A, B, C> extends AbstractJoinNode<BiTuple<A, B>, C, TriTuple<A, B, C>> {

    private final BiFunction<A, B, IndexProperties> mappingAB;
    private final int outputStoreSize;

    public JoinTriNode(BiFunction<A, B, IndexProperties> mappingAB, Function<C, IndexProperties> mappingC,
            int inputStoreIndexAB, int inputStoreIndexC,
            TupleLifecycle<TriTuple<A, B, C>> nextNodesTupleLifecycle,
            int outputStoreSize,
            Indexer<BiTuple<A, B>, Map<UniTuple<C>, TriTuple<A, B, C>>> indexerAB,
            Indexer<UniTuple<C>, Map<BiTuple<A, B>, TriTuple<A, B, C>>> indexerC) {
        super(mappingC, inputStoreIndexAB, inputStoreIndexC, nextNodesTupleLifecycle, indexerAB, indexerC);
        this.mappingAB = mappingAB;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected IndexProperties createIndexProperties(BiTuple<A, B> abBiTuple) {
        return mappingAB.apply(abBiTuple.factA, abBiTuple.factB);
    }

    @Override
    protected TriTuple<A, B, C> createOutTuple(BiTuple<A, B> abBiTuple, UniTuple<C> rightTuple) {
        return new TriTuple<>(abBiTuple.factA, abBiTuple.factB, rightTuple.factA, outputStoreSize);
    }

    @Override
    public String toString() {
        return "JoinTriNode";
    }

}
