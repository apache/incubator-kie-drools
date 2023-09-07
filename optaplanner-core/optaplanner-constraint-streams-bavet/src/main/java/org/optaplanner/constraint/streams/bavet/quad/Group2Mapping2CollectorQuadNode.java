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

import static org.optaplanner.constraint.streams.bavet.quad.Group0Mapping2CollectorQuadNode.mergeCollectors;
import static org.optaplanner.constraint.streams.bavet.quad.Group2Mapping0CollectorQuadNode.createGroupKey;

import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.util.Pair;

final class Group2Mapping2CollectorQuadNode<OldA, OldB, OldC, OldD, A, B, C, D, ResultContainerC_, ResultContainerD_>
        extends
        AbstractGroupQuadNode<OldA, OldB, OldC, OldD, QuadTuple<A, B, C, D>, QuadTupleImpl<A, B, C, D>, Pair<A, B>, Object, Pair<C, D>> {

    private final int outputStoreSize;

    public Group2Mapping2CollectorQuadNode(QuadFunction<OldA, OldB, OldC, OldD, A> groupKeyMappingA,
            QuadFunction<OldA, OldB, OldC, OldD, B> groupKeyMappingB, int groupStoreIndex, int undoStoreIndex,
            QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerC_, C> collectorC,
            QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerD_, D> collectorD,
            TupleLifecycle<QuadTuple<A, B, C, D>> nextNodesTupleLifecycle, int outputStoreSize,
            EnvironmentMode environmentMode) {
        super(groupStoreIndex, undoStoreIndex, tuple -> createGroupKey(groupKeyMappingA, groupKeyMappingB, tuple),
                mergeCollectors(collectorC, collectorD), nextNodesTupleLifecycle, environmentMode);
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected QuadTupleImpl<A, B, C, D> createOutTuple(Pair<A, B> groupKey) {
        return new QuadTupleImpl<>(groupKey.getKey(), groupKey.getValue(), null, null, outputStoreSize);
    }

    @Override
    protected void updateOutTupleToResult(QuadTupleImpl<A, B, C, D> outTuple, Pair<C, D> result) {
        outTuple.factC = result.getKey();
        outTuple.factD = result.getValue();
    }

}
