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

package org.optaplanner.constraint.streams.bavet.tri;

import static org.optaplanner.constraint.streams.bavet.tri.Group0Mapping2CollectorTriNode.mergeCollectors;
import static org.optaplanner.constraint.streams.bavet.tri.Group2Mapping0CollectorTriNode.createGroupKey;

import org.optaplanner.constraint.streams.bavet.common.TupleLifecycle;
import org.optaplanner.constraint.streams.bavet.quad.QuadTuple;
import org.optaplanner.constraint.streams.bavet.quad.QuadTupleImpl;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.util.Pair;

final class Group2Mapping2CollectorTriNode<OldA, OldB, OldC, A, B, C, D, ResultContainerC_, ResultContainerD_>
        extends
        AbstractGroupTriNode<OldA, OldB, OldC, QuadTuple<A, B, C, D>, QuadTupleImpl<A, B, C, D>, Pair<A, B>, Object, Pair<C, D>> {

    private final int outputStoreSize;

    public Group2Mapping2CollectorTriNode(TriFunction<OldA, OldB, OldC, A> groupKeyMappingA,
            TriFunction<OldA, OldB, OldC, B> groupKeyMappingB, int groupStoreIndex, int undoStoreIndex,
            TriConstraintCollector<OldA, OldB, OldC, ResultContainerC_, C> collectorC,
            TriConstraintCollector<OldA, OldB, OldC, ResultContainerD_, D> collectorD,
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
