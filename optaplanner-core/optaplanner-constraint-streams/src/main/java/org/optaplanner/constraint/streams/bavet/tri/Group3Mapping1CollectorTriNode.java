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

import java.util.function.Consumer;

import org.optaplanner.constraint.streams.bavet.common.Group;
import org.optaplanner.constraint.streams.bavet.quad.QuadTuple;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.impl.util.Triple;

final class Group3Mapping1CollectorTriNode<OldA, OldB, OldC, A, B, C, D, ResultContainer_>
        extends AbstractGroupTriNode<OldA, OldB, OldC, QuadTuple<A, B, C, D>, Triple<A, B, C>, ResultContainer_, D> {

    private final TriFunction<OldA, OldB, OldC, A> groupKeyMappingA;
    private final TriFunction<OldA, OldB, OldC, B> groupKeyMappingB;
    private final TriFunction<OldA, OldB, OldC, C> groupKeyMappingC;
    private final int outputStoreSize;

    public Group3Mapping1CollectorTriNode(TriFunction<OldA, OldB, OldC, A> groupKeyMappingA,
            TriFunction<OldA, OldB, OldC, B> groupKeyMappingB, TriFunction<OldA, OldB, OldC, C> groupKeyMappingC,
            int groupStoreIndex, TriConstraintCollector<OldA, OldB, OldC, ResultContainer_, D> collector,
            Consumer<QuadTuple<A, B, C, D>> nextNodesInsert, Consumer<QuadTuple<A, B, C, D>> nextNodesRetract,
            int outputStoreSize) {
        super(groupStoreIndex, collector, nextNodesInsert, nextNodesRetract);
        this.groupKeyMappingA = groupKeyMappingA;
        this.groupKeyMappingB = groupKeyMappingB;
        this.groupKeyMappingC = groupKeyMappingC;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected Triple<A, B, C> createGroupKey(TriTuple<OldA, OldB, OldC> tuple) {
        OldA oldA = tuple.factA;
        OldB oldB = tuple.factB;
        OldC oldC = tuple.factC;
        A a = groupKeyMappingA.apply(oldA, oldB, oldC);
        B b = groupKeyMappingB.apply(oldA, oldB, oldC);
        C c = groupKeyMappingC.apply(oldA, oldB, oldC);
        return Triple.of(a, b, c);
    }

    @Override
    protected QuadTuple<A, B, C, D> createOutTuple(Group<QuadTuple<A, B, C, D>, Triple<A, B, C>, ResultContainer_> group) {
        Triple<A, B, C> groupKey = group.groupKey;
        D d = finisher.apply(group.resultContainer);
        return new QuadTuple<>(groupKey.getA(), groupKey.getB(), groupKey.getC(), d, outputStoreSize);
    }

    @Override
    public String toString() {
        return "GroupTriNode 3+1";
    }

}
