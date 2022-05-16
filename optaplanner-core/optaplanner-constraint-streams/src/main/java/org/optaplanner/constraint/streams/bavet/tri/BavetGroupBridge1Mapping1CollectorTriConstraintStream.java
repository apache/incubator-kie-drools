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
import org.optaplanner.constraint.streams.bavet.bi.BiTuple;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;

final class BavetGroupBridge1Mapping1CollectorTriConstraintStream<Solution_, A, B, C, NewA, ResultContainer_, NewB>
        extends BavetAbstractBiGroupBridgeTriConstraintStream<Solution_, A, B, C, NewA, NewB> {

    private final TriFunction<A, B, C, NewA> groupKeyMapping;
    private final TriConstraintCollector<A, B, C, ResultContainer_, NewB> collector;

    public BavetGroupBridge1Mapping1CollectorTriConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractTriConstraintStream<Solution_, A, B, C> parent, TriFunction<A, B, C, NewA> groupKeyMapping,
            TriConstraintCollector<A, B, C, ResultContainer_, NewB> collector) {
        super(constraintFactory, parent);
        this.groupKeyMapping = groupKeyMapping;
        this.collector = collector;
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    protected AbstractGroupTriNode<A, B, C, BiTuple<NewA, NewB>, ?, ?, ?> createNode(int inputStoreIndex,
            Consumer<BiTuple<NewA, NewB>> insert, Consumer<BiTuple<NewA, NewB>> retract, int outputStoreSize) {
        return new Group1Mapping1CollectorTriNode<>(
                groupKeyMapping, inputStoreIndex, collector,
                insert, retract, outputStoreSize);
    }

}
