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

final class BavetGroupBridge3Mapping0CollectorTriConstraintStream<Solution_, A, B, C, NewA, NewB, NewC>
        extends BavetAbstractTriGroupBridgeTriConstraintStream<Solution_, A, B, C, NewA, NewB, NewC> {

    private final TriFunction<A, B, C, NewA> groupKeyMappingA;
    private final TriFunction<A, B, C, NewB> groupKeyMappingB;
    private final TriFunction<A, B, C, NewC> groupKeyMappingC;

    public BavetGroupBridge3Mapping0CollectorTriConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractTriConstraintStream<Solution_, A, B, C> parent,
            TriFunction<A, B, C, NewA> groupKeyMappingA, TriFunction<A, B, C, NewB> groupKeyMappingB,
            TriFunction<A, B, C, NewC> groupKeyMappingC) {
        super(constraintFactory, parent);
        this.groupKeyMappingA = groupKeyMappingA;
        this.groupKeyMappingB = groupKeyMappingB;
        this.groupKeyMappingC = groupKeyMappingC;
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    protected AbstractGroupTriNode<A, B, C, TriTuple<NewA, NewB, NewC>, ?, ?, ?> createNode(int inputStoreIndex,
            Consumer<TriTuple<NewA, NewB, NewC>> insert, Consumer<TriTuple<NewA, NewB, NewC>> retract, int outputStoreSize) {
        return new Group3Mapping0CollectorTriNode<>(
                groupKeyMappingA, groupKeyMappingB, groupKeyMappingC, inputStoreIndex,
                insert, retract, outputStoreSize);
    }
}
