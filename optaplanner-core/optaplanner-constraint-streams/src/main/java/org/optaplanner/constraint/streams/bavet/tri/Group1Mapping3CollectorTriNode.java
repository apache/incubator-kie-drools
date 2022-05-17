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

import static org.optaplanner.constraint.streams.bavet.tri.Group0Mapping3CollectorTriNode.mergeCollectors;

import java.util.function.Consumer;

import org.optaplanner.constraint.streams.bavet.common.Group;
import org.optaplanner.constraint.streams.bavet.quad.QuadTuple;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.impl.util.Triple;

final class Group1Mapping3CollectorTriNode<OldA, OldB, OldC, A, B, C, D, ResultContainerB_, ResultContainerC_, ResultContainerD_>
        extends AbstractGroupTriNode<OldA, OldB, OldC, QuadTuple<A, B, C, D>, A, Object, Triple<B, C, D>> {

    private final TriFunction<OldA, OldB, OldC, A> groupKeyMapping;
    private final int outputStoreSize;

    public Group1Mapping3CollectorTriNode(TriFunction<OldA, OldB, OldC, A> groupKeyMapping, int groupStoreIndex,
            TriConstraintCollector<OldA, OldB, OldC, ResultContainerB_, B> collectorB,
            TriConstraintCollector<OldA, OldB, OldC, ResultContainerC_, C> collectorC,
            TriConstraintCollector<OldA, OldB, OldC, ResultContainerD_, D> collectorD,
            Consumer<QuadTuple<A, B, C, D>> nextNodesInsert, Consumer<QuadTuple<A, B, C, D>> nextNodesRetract,
            int outputStoreSize) {
        super(groupStoreIndex, mergeCollectors(collectorB, collectorC, collectorD),
                nextNodesInsert, nextNodesRetract);
        this.groupKeyMapping = groupKeyMapping;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected A createGroupKey(TriTuple<OldA, OldB, OldC> tuple) {
        return groupKeyMapping.apply(tuple.factA, tuple.factB, tuple.factC);
    }

    @Override
    protected QuadTuple<A, B, C, D> createOutTuple(Group<QuadTuple<A, B, C, D>, A, Object> group) {
        A a = group.groupKey;
        Triple<B, C, D> result = finisher.apply(group.resultContainer);
        return new QuadTuple<>(a, result.getA(), result.getB(), result.getC(), outputStoreSize);
    }

    @Override
    public String toString() {
        return "GroupTriNode 1+3";
    }

}
