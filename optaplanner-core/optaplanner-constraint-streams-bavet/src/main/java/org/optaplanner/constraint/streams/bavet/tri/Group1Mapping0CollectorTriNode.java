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
import org.optaplanner.core.api.function.TriFunction;

import org.optaplanner.constraint.streams.bavet.uni.UniTuple;

final class Group1Mapping0CollectorTriNode<OldA, OldB, OldC, A>
        extends AbstractGroupTriNode<OldA, OldB, OldC, UniTuple<A>, A, Void, Void> {

    private final TriFunction<OldA, OldB, OldC, A> groupKeyMapping;
    private final int outputStoreSize;

    public Group1Mapping0CollectorTriNode(TriFunction<OldA, OldB, OldC, A> groupKeyMapping, int groupStoreIndex,
                                          Consumer<UniTuple<A>> nextNodesInsert, Consumer<UniTuple<A>> nextNodesRetract,
                                          int outputStoreSize) {
        super(groupStoreIndex, null, nextNodesInsert, nextNodesRetract);
        this.groupKeyMapping = groupKeyMapping;
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected A createGroupKey(TriTuple<OldA, OldB, OldC> tuple) {
        return groupKeyMapping.apply(tuple.factA, tuple.factB, tuple.factC);
    }

    @Override
    protected UniTuple<A> createOutTuple(Group<UniTuple<A>, A, Void> group) {
        A a = group.groupKey;
        return new UniTuple<>(a, outputStoreSize);
    }

    @Override
    public String toString() {
        return "GroupTriNode 1+0";
    }

}
