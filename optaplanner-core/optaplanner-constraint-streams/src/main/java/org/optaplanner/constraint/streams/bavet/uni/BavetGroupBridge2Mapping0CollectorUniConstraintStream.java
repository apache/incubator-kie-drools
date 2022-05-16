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

package org.optaplanner.constraint.streams.bavet.uni;

import java.util.function.Consumer;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.bi.BiTuple;

final class BavetGroupBridge2Mapping0CollectorUniConstraintStream<Solution_, A, NewA, NewB>
        extends BavetAbstractBiGroupBridgeUniConstraintStream<Solution_, A, NewA, NewB> {

    private final Function<A, NewA> groupKeyMappingA;
    private final Function<A, NewB> groupKeyMappingB;

    public BavetGroupBridge2Mapping0CollectorUniConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractUniConstraintStream<Solution_, A> parent,
            Function<A, NewA> groupKeyMappingA, Function<A, NewB> groupKeyMappingB) {
        super(constraintFactory, parent);
        this.groupKeyMappingA = groupKeyMappingA;
        this.groupKeyMappingB = groupKeyMappingB;
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    protected AbstractGroupUniNode<A, BiTuple<NewA, NewB>, ?, ?, ?> createNode(int inputStoreIndex,
            Consumer<BiTuple<NewA, NewB>> insert, Consumer<BiTuple<NewA, NewB>> retract, int outputStoreSize) {
        return new Group2Mapping0CollectorUniNode<>(
                groupKeyMappingA, groupKeyMappingB, inputStoreIndex,
                insert, retract, outputStoreSize);
    }

}
