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

import java.util.function.Consumer;

import org.optaplanner.constraint.streams.bavet.common.Group;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;

final class Group0Mapping1CollectorBiNode<OldA, OldB, A, ResultContainer_>
        extends AbstractGroupBiNode<OldA, OldB, UniTuple<A>, String, ResultContainer_, A> {

    private static final String NO_GROUP_KEY = "NO_GROUP";

    private final int outputStoreSize;

    public Group0Mapping1CollectorBiNode(int groupStoreIndex,
                                         BiConstraintCollector<OldA, OldB, ResultContainer_, A> collector,
                                         Consumer<UniTuple<A>> nextNodesInsert, Consumer<UniTuple<A>> nextNodesRetract,
                                         int outputStoreSize) {
        super(groupStoreIndex, collector, nextNodesInsert, nextNodesRetract);
        this.outputStoreSize = outputStoreSize;
    }

    @Override
    protected String createGroupKey(BiTuple<OldA, OldB> tuple) {
        return NO_GROUP_KEY;
    }

    @Override
    protected UniTuple<A> createOutTuple(Group<UniTuple<A>, String, ResultContainer_> group) {
        ResultContainer_ resultContainer = group.resultContainer;
        A a = finisher.apply(resultContainer);
        return new UniTuple<>(a, outputStoreSize);
    }

    @Override
    public String toString() {
        return "GroupBiNode 0+1";
    }
}
