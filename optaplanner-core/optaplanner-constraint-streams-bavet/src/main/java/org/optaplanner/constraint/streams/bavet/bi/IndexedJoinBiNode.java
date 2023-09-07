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

package org.optaplanner.constraint.streams.bavet.bi;

import java.util.function.BiPredicate;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.AbstractIndexedJoinNode;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.common.index.IndexProperties;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;

final class IndexedJoinBiNode<A, B> extends AbstractIndexedJoinNode<UniTuple<A>, B, BiTuple<A, B>, BiTupleImpl<A, B>> {

    private final Function<A, IndexProperties> mappingA;
    private final BiPredicate<A, B> filtering;
    private final int outputStoreSize;

    public IndexedJoinBiNode(Function<A, IndexProperties> mappingA, Function<B, IndexProperties> mappingB,
            int inputStoreIndexA, int inputStoreIndexEntryA, int inputStoreIndexOutTupleListA,
            int inputStoreIndexB, int inputStoreIndexEntryB, int inputStoreIndexOutTupleListB,
            TupleLifecycle<BiTuple<A, B>> nextNodesTupleLifecycle, BiPredicate<A, B> filtering,
            int outputStoreSize,
            int outputStoreIndexOutEntryA, int outputStoreIndexOutEntryB,
            Indexer<UniTuple<A>> indexerA,
            Indexer<UniTuple<B>> indexerB) {
        super(mappingB,
                inputStoreIndexA, inputStoreIndexEntryA, inputStoreIndexOutTupleListA,
                inputStoreIndexB, inputStoreIndexEntryB, inputStoreIndexOutTupleListB,
                nextNodesTupleLifecycle, filtering != null,
                outputStoreIndexOutEntryA, outputStoreIndexOutEntryB,
                indexerA, indexerB);
        this.mappingA = mappingA;
        this.filtering = filtering;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected IndexProperties createIndexPropertiesLeft(UniTuple<A> leftTuple) {
        return mappingA.apply(leftTuple.getFactA());
    }

    @Override
    protected BiTupleImpl<A, B> createOutTuple(UniTuple<A> leftTuple, UniTuple<B> rightTuple) {
        return new BiTupleImpl<>(leftTuple.getFactA(), rightTuple.getFactA(), outputStoreSize);
    }

    @Override
    protected void setOutTupleLeftFacts(BiTupleImpl<A, B> outTuple, UniTuple<A> leftTuple) {
        outTuple.factA = leftTuple.getFactA();
    }

    @Override
    protected void setOutTupleRightFact(BiTupleImpl<A, B> outTuple, UniTuple<B> rightTuple) {
        outTuple.factB = rightTuple.getFactA();
    }

    @Override
    protected boolean testFiltering(UniTuple<A> leftTuple, UniTuple<B> rightTuple) {
        return filtering.test(leftTuple.getFactA(), rightTuple.getFactA());
    }

}
