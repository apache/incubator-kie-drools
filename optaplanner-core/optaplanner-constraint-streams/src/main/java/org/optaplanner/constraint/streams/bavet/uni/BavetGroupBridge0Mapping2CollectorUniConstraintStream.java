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
import org.optaplanner.constraint.streams.bavet.bi.BiTuple;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;

final class BavetGroupBridge0Mapping2CollectorUniConstraintStream<Solution_, A, ResultContainerA_, NewA, ResultContainerB_, NewB>
        extends BavetAbstractBiGroupBridgeUniConstraintStream<Solution_, A, NewA, NewB> {

    private final UniConstraintCollector<A, ResultContainerA_, NewA> collectorA;
    private final UniConstraintCollector<A, ResultContainerB_, NewB> collectorB;

    public BavetGroupBridge0Mapping2CollectorUniConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractUniConstraintStream<Solution_, A> parent,
            UniConstraintCollector<A, ResultContainerA_, NewA> collectorA,
            UniConstraintCollector<A, ResultContainerB_, NewB> collectorB) {
        super(constraintFactory, parent);
        this.collectorA = collectorA;
        this.collectorB = collectorB;
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    protected AbstractGroupUniNode<A, BiTuple<NewA, NewB>, ?, ?, ?> createNode(int inputStoreIndex,
            Consumer<BiTuple<NewA, NewB>> insert, Consumer<BiTuple<NewA, NewB>> retract, int outputStoreSize) {
        return new Group0Mapping2CollectorUniNode<>(inputStoreIndex,
                collectorA, collectorB,
                insert, retract, outputStoreSize);
    }

}
