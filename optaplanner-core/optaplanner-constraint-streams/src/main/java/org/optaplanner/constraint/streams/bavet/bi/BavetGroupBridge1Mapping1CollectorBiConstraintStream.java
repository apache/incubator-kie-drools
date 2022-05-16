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
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;

final class BavetGroupBridge1Mapping1CollectorBiConstraintStream<Solution_, A, B, NewA, ResultContainer_, NewB>
        extends BavetAbstractBiGroupBridgeBiConstraintStream<Solution_, A, B, NewA, NewB> {

    private final BiFunction<A, B, NewA> groupKeyMapping;
    private final BiConstraintCollector<A, B, ResultContainer_, NewB> collector;

    public BavetGroupBridge1Mapping1CollectorBiConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractBiConstraintStream<Solution_, A, B> parent, BiFunction<A, B, NewA> groupKeyMapping,
            BiConstraintCollector<A, B, ResultContainer_, NewB> collector) {
        super(constraintFactory, parent);
        this.groupKeyMapping = groupKeyMapping;
        this.collector = collector;
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    protected AbstractGroupBiNode<A, B, BiTuple<NewA, NewB>, ?, ?, ?> createNode(int inputStoreIndex,
            Consumer<BiTuple<NewA, NewB>> insert, Consumer<BiTuple<NewA, NewB>> retract, int outputStoreSize) {
        return new Group1Mapping1CollectorBiNode<>(
                groupKeyMapping, inputStoreIndex, collector,
                insert, retract, outputStoreSize);
    }

}
