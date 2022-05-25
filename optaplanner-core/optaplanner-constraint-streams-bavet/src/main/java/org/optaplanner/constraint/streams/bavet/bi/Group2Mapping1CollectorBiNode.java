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

import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.optaplanner.constraint.streams.bavet.common.Group;
import org.optaplanner.constraint.streams.bavet.tri.TriTuple;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.impl.util.Pair;

final class Group2Mapping1CollectorBiNode<OldA, OldB, A, B, C, ResultContainer_>
        extends AbstractGroupBiNode<OldA, OldB, TriTuple<A, B, C>, Pair<A, B>, ResultContainer_, C> {

    private final BiFunction<OldA, OldB, A> groupKeyMappingA;
    private final BiFunction<OldA, OldB, B> groupKeyMappingB;
    private final int outputStoreSize;

    public Group2Mapping1CollectorBiNode(BiFunction<OldA, OldB, A> groupKeyMappingA, BiFunction<OldA, OldB, B> groupKeyMappingB,
                                         int groupStoreIndex,
                                         BiConstraintCollector<OldA, OldB, ResultContainer_, C> collector,
                                         Consumer<TriTuple<A, B, C>> nextNodesInsert, Consumer<TriTuple<A, B, C>> nextNodesRetract,
                                         int outputStoreSize) {
        super(groupStoreIndex, collector, nextNodesInsert, nextNodesRetract);
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
    protected TriTuple<A, B, C> createOutTuple(Group<TriTuple<A, B, C>, Pair<A, B>, ResultContainer_> group) {
        Pair<A, B> groupKey = group.groupKey;
        C c = finisher.apply(group.resultContainer);
        return new TriTuple<>(groupKey.getKey(), groupKey.getValue(), c, outputStoreSize);
    }

    @Override
    public String toString() {
        return "GroupBiNode 2+1";
    }

}
