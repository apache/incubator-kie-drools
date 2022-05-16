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

final class BavetGroupBridge1Mapping0CollectorUniConstraintStream<Solution_, A, NewA>
        extends BavetAbstractUniGroupBridgeUniConstraintStream<Solution_, A, NewA> {

    private final Function<A, NewA> groupKeyMapping;

    public BavetGroupBridge1Mapping0CollectorUniConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractUniConstraintStream<Solution_, A> parent,
            Function<A, NewA> groupKeyMapping) {
        super(constraintFactory, parent);
        this.groupKeyMapping = groupKeyMapping;
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    protected AbstractGroupUniNode<A, UniTuple<NewA>, ?, ?, ?> createNode(int inputStoreIndex,
            Consumer<UniTuple<NewA>> insert, Consumer<UniTuple<NewA>> retract, int outputStoreSize) {
        return new Group1Mapping0CollectorUniNode<>(groupKeyMapping, inputStoreIndex, insert, retract, outputStoreSize);
    }
}
