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
import org.optaplanner.constraint.streams.bavet.tri.TriTuple;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.impl.util.Triple;

final class Group0Mapping3CollectorBiNode<OldA, OldB, A, B, C, ResultContainerA_, ResultContainerB_, ResultContainerC_>
        extends AbstractGroupBiNode<OldA, OldB, TriTuple<A, B, C>, String, Object, Triple<A, B, C>> {

    private static final String NO_GROUP_KEY = "NO_GROUP";

    private final int outputStoreSize;

    public Group0Mapping3CollectorBiNode(int groupStoreIndex,
                                         BiConstraintCollector<OldA, OldB, ResultContainerA_, A> collectorA,
                                         BiConstraintCollector<OldA, OldB, ResultContainerB_, B> collectorB,
                                         BiConstraintCollector<OldA, OldB, ResultContainerC_, C> collectorC,
                                         Consumer<TriTuple<A, B, C>> nextNodesInsert, Consumer<TriTuple<A, B, C>> nextNodesRetract,
                                         int outputStoreSize) {
        super(groupStoreIndex, mergeCollectors(collectorA, collectorB, collectorC), nextNodesInsert, nextNodesRetract);
        this.outputStoreSize = outputStoreSize;
    }

    static <OldA, OldB, A, B, C, ResultContainerA_, ResultContainerB_, ResultContainerC_>
            BiConstraintCollector<OldA, OldB, Object, Triple<A, B, C>> mergeCollectors(
                    BiConstraintCollector<OldA, OldB, ResultContainerA_, A> collectorA,
                    BiConstraintCollector<OldA, OldB, ResultContainerB_, B> collectorB,
                    BiConstraintCollector<OldA, OldB, ResultContainerC_, C> collectorC) {
        return (BiConstraintCollector<OldA, OldB, Object, Triple<A, B, C>>) ConstraintCollectors.compose(collectorA, collectorB,
                collectorC, Triple::of);
    }

    @Override
    protected String createGroupKey(BiTuple<OldA, OldB> tuple) {
        return NO_GROUP_KEY;
    }

    @Override
    protected TriTuple<A, B, C> createOutTuple(Group<TriTuple<A, B, C>, String, Object> group) {
        Object resultContainer = group.resultContainer;
        Triple<A, B, C> result = finisher.apply(resultContainer);
        return new TriTuple<>(result.getA(), result.getB(), result.getC(), outputStoreSize);
    }

    @Override
    public String toString() {
        return "GroupBiNode 0+3";
    }

}
