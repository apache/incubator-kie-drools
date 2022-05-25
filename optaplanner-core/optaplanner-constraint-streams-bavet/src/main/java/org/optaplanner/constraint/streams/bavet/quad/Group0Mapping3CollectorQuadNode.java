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

package org.optaplanner.constraint.streams.bavet.quad;

import java.util.function.Consumer;

import org.optaplanner.constraint.streams.bavet.common.Group;
import org.optaplanner.constraint.streams.bavet.tri.TriTuple;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.impl.util.Triple;

final class Group0Mapping3CollectorQuadNode<OldA, OldB, OldC, OldD, A, B, C, ResultContainerA_, ResultContainerB_, ResultContainerC_>
        extends AbstractGroupQuadNode<OldA, OldB, OldC, OldD, TriTuple<A, B, C>, String, Object, Triple<A, B, C>> {

    private static final String NO_GROUP_KEY = "NO_GROUP";

    private final int outputStoreSize;

    public Group0Mapping3CollectorQuadNode(int groupStoreIndex,
            QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerA_, A> collectorA,
            QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerB_, B> collectorB,
            QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerC_, C> collectorC,
            Consumer<TriTuple<A, B, C>> nextNodesInsert, Consumer<TriTuple<A, B, C>> nextNodesRetract,
            int outputStoreSize) {
        super(groupStoreIndex, mergeCollectors(collectorA, collectorB, collectorC), nextNodesInsert, nextNodesRetract);
        this.outputStoreSize = outputStoreSize;
    }

    static <OldA, OldB, OldC, OldD, A, B, C, ResultContainerA_, ResultContainerB_, ResultContainerC_>
            QuadConstraintCollector<OldA, OldB, OldC, OldD, Object, Triple<A, B, C>> mergeCollectors(
                    QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerA_, A> collectorA,
                    QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerB_, B> collectorB,
                    QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainerC_, C> collectorC) {
        return (QuadConstraintCollector<OldA, OldB, OldC, OldD, Object, Triple<A, B, C>>) ConstraintCollectors.compose(
                collectorA,
                collectorB, collectorC, Triple::of);
    }

    @Override
    protected String createGroupKey(QuadTuple<OldA, OldB, OldC, OldD> tuple) {
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
        return "GroupQuadNode 0+3";
    }

}
