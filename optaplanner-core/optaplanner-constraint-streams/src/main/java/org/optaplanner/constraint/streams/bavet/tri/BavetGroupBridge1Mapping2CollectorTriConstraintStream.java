/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;

final class BavetGroupBridge1Mapping2CollectorTriConstraintStream<Solution_, A, B, C, NewA, ResultContainerB_, NewB, ResultContainerC_, NewC>
        extends BavetAbstractTriGroupBridgeTriConstraintStream<Solution_, A, B, C, NewA, NewB, NewC> {

    private final TriFunction<A, B, C, NewA> groupKeyMapping;
    private final TriConstraintCollector<A, B, C, ResultContainerB_, NewB> collectorB;
    private final TriConstraintCollector<A, B, C, ResultContainerC_, NewC> collectorC;

    public BavetGroupBridge1Mapping2CollectorTriConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractTriConstraintStream<Solution_, A, B, C> parent, TriFunction<A, B, C, NewA> groupKeyMapping,
            TriConstraintCollector<A, B, C, ResultContainerB_, NewB> collectorB,
            TriConstraintCollector<A, B, C, ResultContainerC_, NewC> collectorC) {
        super(constraintFactory, parent);
        this.groupKeyMapping = groupKeyMapping;
        this.collectorB = collectorB;
        this.collectorC = collectorC;
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    protected AbstractGroupTriNode<A, B, C, TriTuple<NewA, NewB, NewC>, ?, ?, ?> createNode(int inputStoreIndex,
            Consumer<TriTuple<NewA, NewB, NewC>> insert, Consumer<TriTuple<NewA, NewB, NewC>> retract, int outputStoreSize) {
        return new Group1Mapping2CollectorTriNode<>(
                groupKeyMapping, inputStoreIndex, collectorB, collectorC,
                insert, retract, outputStoreSize);
    }
}
