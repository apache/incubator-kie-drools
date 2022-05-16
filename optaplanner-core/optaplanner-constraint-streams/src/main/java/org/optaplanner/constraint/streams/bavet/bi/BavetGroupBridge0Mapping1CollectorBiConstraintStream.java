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
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;

final class BavetGroupBridge0Mapping1CollectorBiConstraintStream<Solution_, A, B, ResultContainer_, NewA>
        extends BavetAbstractUniGroupBridgeBiConstraintStream<Solution_, A, B, NewA> {

    private final BiConstraintCollector<A, B, ResultContainer_, NewA> collector;

    public BavetGroupBridge0Mapping1CollectorBiConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractBiConstraintStream<Solution_, A, B> parent,
            BiConstraintCollector<A, B, ResultContainer_, NewA> collector) {
        super(constraintFactory, parent);
        this.collector = collector;
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    protected AbstractGroupBiNode<A, B, UniTuple<NewA>, ?, ?, ?> createNode(int inputStoreIndex,
            Consumer<UniTuple<NewA>> insert, Consumer<UniTuple<NewA>> retract, int outputStoreSize) {
        return new Group0Mapping1CollectorBiNode<>(inputStoreIndex, collector, insert, retract, outputStoreSize);
    }
}
