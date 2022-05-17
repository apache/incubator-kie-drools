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
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.impl.util.Triple;

final class Group0Mapping3CollectorTriNode<OldA, OldB, OldC, A, B, C, ResultContainerA_, ResultContainerB_, ResultContainerC_>
        extends AbstractGroupTriNode<OldA, OldB, OldC, TriTuple<A, B, C>, String, Object, Triple<A, B, C>> {

    private static final String NO_GROUP_KEY = "NO_GROUP";

    private final int outputStoreSize;

    public Group0Mapping3CollectorTriNode(int groupStoreIndex,
            TriConstraintCollector<OldA, OldB, OldC, ResultContainerA_, A> collectorA,
            TriConstraintCollector<OldA, OldB, OldC, ResultContainerB_, B> collectorB,
            TriConstraintCollector<OldA, OldB, OldC, ResultContainerC_, C> collectorC,
            Consumer<TriTuple<A, B, C>> nextNodesInsert, Consumer<TriTuple<A, B, C>> nextNodesRetract,
            int outputStoreSize) {
        super(groupStoreIndex, mergeCollectors(collectorA, collectorB, collectorC), nextNodesInsert, nextNodesRetract);
        this.outputStoreSize = outputStoreSize;
    }

    static <OldA, OldB, OldC, A, B, C, ResultContainerA_, ResultContainerB_, ResultContainerC_>
            TriConstraintCollector<OldA, OldB, OldC, Object, Triple<A, B, C>> mergeCollectors(
                    TriConstraintCollector<OldA, OldB, OldC, ResultContainerA_, A> collectorA,
                    TriConstraintCollector<OldA, OldB, OldC, ResultContainerB_, B> collectorB,
                    TriConstraintCollector<OldA, OldB, OldC, ResultContainerC_, C> collectorC) {
        return (TriConstraintCollector<OldA, OldB, OldC, Object, Triple<A, B, C>>) ConstraintCollectors.compose(collectorA,
                collectorB, collectorC, Triple::of);
    }

    @Override
    protected String createGroupKey(TriTuple<OldA, OldB, OldC> tuple) {
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
        return "GroupTriNode 0+3";
    }

}
