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
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.impl.util.Quadruple;

final class Group0Mapping4CollectorTriNode<OldA, OldB, OldC, A, B, C, D, ResultContainerA_, ResultContainerB_, ResultContainerC_, ResultContainerD_>
        extends AbstractGroupTriNode<OldA, OldB, OldC, QuadTuple<A, B, C, D>, String, Object, Quadruple<A, B, C, D>> {

    private static final String NO_GROUP_KEY = "NO_GROUP";

    private final int outputStoreSize;

    public Group0Mapping4CollectorTriNode(int groupStoreIndex,
                                          TriConstraintCollector<OldA, OldB, OldC, ResultContainerA_, A> collectorA,
                                          TriConstraintCollector<OldA, OldB, OldC, ResultContainerB_, B> collectorB,
                                          TriConstraintCollector<OldA, OldB, OldC, ResultContainerC_, C> collectorC,
                                          TriConstraintCollector<OldA, OldB, OldC, ResultContainerD_, D> collectorD,
                                          Consumer<QuadTuple<A, B, C, D>> nextNodesInsert, Consumer<QuadTuple<A, B, C, D>> nextNodesRetract,
                                          int outputStoreSize) {
        super(groupStoreIndex, mergeCollectors(collectorA, collectorB, collectorC, collectorD), nextNodesInsert,
                nextNodesRetract);
        this.outputStoreSize = outputStoreSize;
    }

    static <OldA, OldB, OldC, A, B, C, D, ResultContainerA_, ResultContainerB_, ResultContainerC_, ResultContainerD_>
            TriConstraintCollector<OldA, OldB, OldC, Object, Quadruple<A, B, C, D>> mergeCollectors(
                    TriConstraintCollector<OldA, OldB, OldC, ResultContainerA_, A> collectorA,
                    TriConstraintCollector<OldA, OldB, OldC, ResultContainerB_, B> collectorB,
                    TriConstraintCollector<OldA, OldB, OldC, ResultContainerC_, C> collectorC,
                    TriConstraintCollector<OldA, OldB, OldC, ResultContainerD_, D> collectorD) {
        return (TriConstraintCollector<OldA, OldB, OldC, Object, Quadruple<A, B, C, D>>) ConstraintCollectors.compose(
                collectorA, collectorB, collectorC, collectorD, Quadruple::of);
    }

    @Override
    protected String createGroupKey(TriTuple<OldA, OldB, OldC> tuple) {
        return NO_GROUP_KEY;
    }

    @Override
    protected QuadTuple<A, B, C, D> createOutTuple(Group<QuadTuple<A, B, C, D>, String, Object> group) {
        Object resultContainer = group.resultContainer;
        Quadruple<A, B, C, D> result = finisher.apply(resultContainer);
        return new QuadTuple<>(result.getA(), result.getB(), result.getC(), result.getD(), outputStoreSize);
    }

    @Override
    public String toString() {
        return "GroupTriNode 0+4";
    }

}
