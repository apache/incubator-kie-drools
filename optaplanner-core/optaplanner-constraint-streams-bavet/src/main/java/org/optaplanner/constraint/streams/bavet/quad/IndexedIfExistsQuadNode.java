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

package org.optaplanner.constraint.streams.bavet.quad;

import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.AbstractIndexedIfExistsNode;
import org.optaplanner.constraint.streams.bavet.common.ExistsCounter;
import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.common.index.IndexProperties;
import org.optaplanner.constraint.streams.bavet.common.index.Indexer;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.core.api.function.PentaPredicate;
import org.optaplanner.core.api.function.QuadFunction;

final class IndexedIfExistsQuadNode<A, B, C, D, E> extends AbstractIndexedIfExistsNode<QuadTuple<A, B, C, D>, E> {

    private final QuadFunction<A, B, C, D, IndexProperties> mappingABCD;
    private final PentaPredicate<A, B, C, D, E> filtering;

    public IndexedIfExistsQuadNode(boolean shouldExist,
            QuadFunction<A, B, C, D, IndexProperties> mappingABCD, Function<E, IndexProperties> mappingE,
            int inputStoreIndexLeftProperties, int inputStoreIndexLeftCounterEntry,
            int inputStoreIndexRightProperties, int inputStoreIndexRightEntry,
            TupleLifecycle<QuadTuple<A, B, C, D>> nextNodesTupleLifecycle,
            Indexer<ExistsCounter<QuadTuple<A, B, C, D>>> indexerABCD, Indexer<UniTuple<E>> indexerE) {
        this(shouldExist, mappingABCD, mappingE,
                inputStoreIndexLeftProperties, inputStoreIndexLeftCounterEntry, -1,
                inputStoreIndexRightProperties, inputStoreIndexRightEntry, -1,
                nextNodesTupleLifecycle, indexerABCD, indexerE,
                null);
    }

    public IndexedIfExistsQuadNode(boolean shouldExist,
            QuadFunction<A, B, C, D, IndexProperties> mappingABCD, Function<E, IndexProperties> mappingE,
            int inputStoreIndexLeftProperties, int inputStoreIndexLeftCounterEntry, int inputStoreIndexLeftTrackerList,
            int inputStoreIndexRightProperties, int inputStoreIndexRightEntry, int inputStoreIndexRightTrackerList,
            TupleLifecycle<QuadTuple<A, B, C, D>> nextNodesTupleLifecycle,
            Indexer<ExistsCounter<QuadTuple<A, B, C, D>>> indexerABCD, Indexer<UniTuple<E>> indexerE,
            PentaPredicate<A, B, C, D, E> filtering) {
        super(shouldExist, mappingE,
                inputStoreIndexLeftProperties, inputStoreIndexLeftCounterEntry, inputStoreIndexLeftTrackerList,
                inputStoreIndexRightProperties, inputStoreIndexRightEntry, inputStoreIndexRightTrackerList,
                nextNodesTupleLifecycle, indexerABCD, indexerE,
                filtering != null);
        this.mappingABCD = mappingABCD;
        this.filtering = filtering;
    }

    @Override
    protected IndexProperties createIndexProperties(QuadTuple<A, B, C, D> leftTuple) {
        return mappingABCD.apply(leftTuple.getFactA(), leftTuple.getFactB(), leftTuple.getFactC(), leftTuple.getFactD());
    }

    @Override
    protected boolean testFiltering(QuadTuple<A, B, C, D> leftTuple, UniTuple<E> rightTuple) {
        return filtering.test(leftTuple.getFactA(), leftTuple.getFactB(), leftTuple.getFactC(), leftTuple.getFactD(),
                rightTuple.getFactA());
    }

}
