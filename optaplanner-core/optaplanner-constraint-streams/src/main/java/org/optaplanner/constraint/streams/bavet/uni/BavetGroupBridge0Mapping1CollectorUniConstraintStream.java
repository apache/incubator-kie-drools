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

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;

final class BavetGroupBridge0Mapping1CollectorUniConstraintStream<Solution_, A, ResultContainer_, NewA>
        extends BavetAbstractUniGroupBridgeUniConstraintStream<Solution_, A, NewA> {

    private final UniConstraintCollector<A, ResultContainer_, NewA> collector;

    public BavetGroupBridge0Mapping1CollectorUniConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractUniConstraintStream<Solution_, A> parent,
            UniConstraintCollector<A, ResultContainer_, NewA> collector) {
        super(constraintFactory, parent);
        this.collector = collector;
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    protected AbstractGroupUniNode<A, UniTuple<NewA>, ?, ?, ?> createNode(int inputStoreIndex,
            Consumer<UniTuple<NewA>> insert, Consumer<UniTuple<NewA>> retract, int outputStoreSize) {
        return new Group0Mapping1CollectorUniNode<>(inputStoreIndex, collector, insert, retract, outputStoreSize);
    }
}
