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

package org.optaplanner.constraint.streams.bavet.bi;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.tri.TriTuple;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;

final class BavetGroupBridge1Mapping2CollectorBiConstraintStream<Solution_, A, B, NewA, ResultContainerB_, NewB, ResultContainerC_, NewC>
        extends BavetAbstractTriGroupBridgeBiConstraintStream<Solution_, A, B, NewA, NewB, NewC> {

    private final BiFunction<A, B, NewA> groupKeyMapping;
    private final BiConstraintCollector<A, B, ResultContainerB_, NewB> collectorB;
    private final BiConstraintCollector<A, B, ResultContainerC_, NewC> collectorC;

    public BavetGroupBridge1Mapping2CollectorBiConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractBiConstraintStream<Solution_, A, B> parent, BiFunction<A, B, NewA> groupKeyMapping,
            BiConstraintCollector<A, B, ResultContainerB_, NewB> collectorB,
            BiConstraintCollector<A, B, ResultContainerC_, NewC> collectorC) {
        super(constraintFactory, parent);
        this.groupKeyMapping = groupKeyMapping;
        this.collectorB = collectorB;
        this.collectorC = collectorC;
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    protected AbstractGroupBiNode<A, B, TriTuple<NewA, NewB, NewC>, ?, ?, ?> createNode(int inputStoreIndex,
            Consumer<TriTuple<NewA, NewB, NewC>> insert, Consumer<TriTuple<NewA, NewB, NewC>> retract, int outputStoreSize) {
        return new Group1Mapping2CollectorBiNode<>(
                groupKeyMapping, inputStoreIndex, collectorB, collectorC,
                insert, retract, outputStoreSize);
    }
}
