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

import static org.optaplanner.constraint.streams.bavet.bi.Group0Mapping2CollectorBiNode.mergeCollectors;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.optaplanner.constraint.streams.bavet.common.Group;
import org.optaplanner.constraint.streams.bavet.quad.QuadTuple;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.impl.util.Pair;

final class Group2Mapping2CollectorBiNode<OldA, OldB, A, B, C, D, ResultContainerC_, ResultContainerD_>
        extends AbstractGroupBiNode<OldA, OldB, QuadTuple<A, B, C, D>, Pair<A, B>, Object, Pair<C, D>> {

    private final BiFunction<OldA, OldB, A> groupKeyMappingA;
    private final BiFunction<OldA, OldB, B> groupKeyMappingB;
    private final int outputStoreSize;

    public Group2Mapping2CollectorBiNode(BiFunction<OldA, OldB, A> groupKeyMappingA, BiFunction<OldA, OldB, B> groupKeyMappingB,
                                         int groupStoreIndex,
                                         BiConstraintCollector<OldA, OldB, ResultContainerC_, C> collectorC,
                                         BiConstraintCollector<OldA, OldB, ResultContainerD_, D> collectorD,
                                         Consumer<QuadTuple<A, B, C, D>> nextNodesInsert, Consumer<QuadTuple<A, B, C, D>> nextNodesRetract,
                                         int outputStoreSize) {
        super(groupStoreIndex, mergeCollectors(collectorC, collectorD), nextNodesInsert, nextNodesRetract);
        this.groupKeyMappingA = groupKeyMappingA;
        this.groupKeyMappingB = groupKeyMappingB;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected Pair<A, B> createGroupKey(BiTuple<OldA, OldB> tuple) {
        OldA oldA = tuple.factA;
        OldB oldB = tuple.factB;
        A a = groupKeyMappingA.apply(oldA, oldB);
        B b = groupKeyMappingB.apply(oldA, oldB);
        return Pair.of(a, b);
    }

    @Override
    protected QuadTuple<A, B, C, D> createOutTuple(Group<QuadTuple<A, B, C, D>, Pair<A, B>, Object> group) {
        Pair<A, B> groupKey = group.groupKey;
        Pair<C, D> result = finisher.apply(group.resultContainer);
        return new QuadTuple<>(groupKey.getKey(), groupKey.getValue(), result.getKey(), result.getValue(), outputStoreSize);
    }

    @Override
    public String toString() {
        return "GroupBiNode 2+2";
    }

}
