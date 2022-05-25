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

package org.optaplanner.constraint.streams.bavet.uni;

import java.util.function.Consumer;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.Group;
import org.optaplanner.constraint.streams.bavet.tri.TriTuple;
import org.optaplanner.core.impl.util.Triple;

final class Group3Mapping0CollectorUniNode<OldA, A, B, C>
        extends AbstractGroupUniNode<OldA, TriTuple<A, B, C>, Triple<A, B, C>, Void, Void> {

    private final Function<OldA, A> groupKeyMappingA;
    private final Function<OldA, B> groupKeyMappingB;
    private final Function<OldA, C> groupKeyMappingC;
    private final int outputStoreSize;

    public Group3Mapping0CollectorUniNode(Function<OldA, A> groupKeyMappingA, Function<OldA, B> groupKeyMappingB,
            Function<OldA, C> groupKeyMappingC, int groupStoreIndex,
            Consumer<TriTuple<A, B, C>> nextNodesInsert, Consumer<TriTuple<A, B, C>> nextNodesRetract, int outputStoreSize) {
        super(groupStoreIndex, null, nextNodesInsert, nextNodesRetract);
        this.groupKeyMappingA = groupKeyMappingA;
        this.groupKeyMappingB = groupKeyMappingB;
        this.groupKeyMappingC = groupKeyMappingC;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected Triple<A, B, C> createGroupKey(UniTuple<OldA> tuple) {
        OldA oldA = tuple.factA;
        A a = groupKeyMappingA.apply(oldA);
        B b = groupKeyMappingB.apply(oldA);
        C c = groupKeyMappingC.apply(oldA);
        return Triple.of(a, b, c);
    }

    @Override
    protected TriTuple<A, B, C> createOutTuple(Group<TriTuple<A, B, C>, Triple<A, B, C>, Void> group) {
        Triple<A, B, C> key = group.groupKey;
        return new TriTuple<>(key.getA(), key.getB(), key.getC(), outputStoreSize);
    }

    @Override
    public String toString() {
        return "GroupUniNode 3+0";
    }

}
