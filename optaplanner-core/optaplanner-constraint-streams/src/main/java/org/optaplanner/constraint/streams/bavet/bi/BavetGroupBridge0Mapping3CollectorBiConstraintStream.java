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

import java.util.function.Consumer;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.tri.TriTuple;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;

final class BavetGroupBridge0Mapping3CollectorBiConstraintStream<Solution_, A, B, ResultContainerA_, NewA, ResultContainerB_, NewB, ResultContainerC_, NewC>
        extends BavetAbstractTriGroupBridgeBiConstraintStream<Solution_, A, B, NewA, NewB, NewC> {

    private final BiConstraintCollector<A, B, ResultContainerA_, NewA> collectorA;
    private final BiConstraintCollector<A, B, ResultContainerB_, NewB> collectorB;
    private final BiConstraintCollector<A, B, ResultContainerC_, NewC> collectorC;

    public BavetGroupBridge0Mapping3CollectorBiConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractBiConstraintStream<Solution_, A, B> parent,
            BiConstraintCollector<A, B, ResultContainerA_, NewA> collectorA,
            BiConstraintCollector<A, B, ResultContainerB_, NewB> collectorB,
            BiConstraintCollector<A, B, ResultContainerC_, NewC> collectorC) {
        super(constraintFactory, parent);
        this.collectorA = collectorA;
        this.collectorB = collectorB;
        this.collectorC = collectorC;
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    protected AbstractGroupBiNode<A, B, TriTuple<NewA, NewB, NewC>, ?, ?, ?> createNode(int inputStoreIndex,
            Consumer<TriTuple<NewA, NewB, NewC>> insert, Consumer<TriTuple<NewA, NewB, NewC>> retract, int outputStoreSize) {
        return new Group0Mapping3CollectorBiNode<>(inputStoreIndex,
                collectorA, collectorB, collectorC,
                insert, retract, outputStoreSize);
    }
}
