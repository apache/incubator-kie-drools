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

package org.optaplanner.constraint.streams.bavet.bi;

import java.util.Map;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.AbstractJoinNode;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.common.index.IndexProperties;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;

final class JoinBiNode<A, B> extends AbstractJoinNode<UniTuple<A>, B, BiTuple<A, B>> {

    private final Function<A, IndexProperties> mappingA;
    private final int outputStoreSize;

    public JoinBiNode(Function<A, IndexProperties> mappingA, Function<B, IndexProperties> mappingB,
            int inputStoreIndexA, int inputStoreIndexB,
            TupleLifecycle<BiTuple<A, B>> nextNodesTupleLifecycle,
            int outputStoreSize,
            Indexer<UniTuple<A>, Map<UniTuple<B>, BiTuple<A, B>>> indexerA,
            Indexer<UniTuple<B>, Map<UniTuple<A>, BiTuple<A, B>>> indexerB) {
        super(mappingB, inputStoreIndexA, inputStoreIndexB, nextNodesTupleLifecycle, indexerA, indexerB);
        this.mappingA = mappingA;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected IndexProperties createIndexProperties(UniTuple<A> aUniTuple) {
        return mappingA.apply(aUniTuple.factA);
    }

    @Override
    protected BiTuple<A, B> createOutTuple(UniTuple<A> aUniTuple, UniTuple<B> rightTuple) {
        return new BiTuple<>(aUniTuple.factA, rightTuple.factA, outputStoreSize);
    }

    @Override
    public String toString() {
        return "JoinBiNode";
    }

}
