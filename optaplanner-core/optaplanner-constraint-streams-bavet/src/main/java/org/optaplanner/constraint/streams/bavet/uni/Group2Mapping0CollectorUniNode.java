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

import org.optaplanner.constraint.streams.bavet.bi.BiTuple;
import org.optaplanner.constraint.streams.bavet.common.Group;
import org.optaplanner.core.impl.util.Pair;

final class Group2Mapping0CollectorUniNode<OldA, A, B>
        extends AbstractGroupUniNode<OldA, BiTuple<A, B>, Pair<A, B>, Void, Void> {

    private final Function<OldA, A> groupKeyMappingA;
    private final Function<OldA, B> groupKeyMappingB;
    private final int outputStoreSize;

    public Group2Mapping0CollectorUniNode(Function<OldA, A> groupKeyMappingA, Function<OldA, B> groupKeyMappingB,
                                          int groupStoreIndex, Consumer<BiTuple<A, B>> nextNodesInsert, Consumer<BiTuple<A, B>> nextNodesRetract,
                                          int outputStoreSize) {
        super(groupStoreIndex, null, nextNodesInsert, nextNodesRetract);
        this.groupKeyMappingA = groupKeyMappingA;
        this.groupKeyMappingB = groupKeyMappingB;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected Pair<A, B> createGroupKey(UniTuple<OldA> tuple) {
        OldA oldA = tuple.factA;
        A a = groupKeyMappingA.apply(oldA);
        B b = groupKeyMappingB.apply(oldA);
        return Pair.of(a, b);
    }

    @Override
    protected BiTuple<A, B> createOutTuple(Group<BiTuple<A, B>, Pair<A, B>, Void> group) {
        Pair<A, B> key = group.groupKey;
        return new BiTuple<>(key.getKey(), key.getValue(), outputStoreSize);
    }

    @Override
    public String toString() {
        return "GroupUniNode 2+0";
    }

}
