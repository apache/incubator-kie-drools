/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import java.util.Set;
import java.util.function.Consumer;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.common.AbstractGroupNode;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.NodeBuildHelper;
import org.optaplanner.constraint.streams.bavet.quad.BavetGroupQuadConstraintStream;
import org.optaplanner.constraint.streams.bavet.quad.QuadTuple;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;

final class BavetQuadGroupBridgeBiConstraintStream<Solution_, A, B, NewA, NewB, NewC, NewD>
        extends BavetAbstractBiConstraintStream<Solution_, A, B> {

    private final BavetAbstractBiConstraintStream<Solution_, A, B> parent;
    private BavetGroupQuadConstraintStream<Solution_, NewA, NewB, NewC, NewD> groupStream;
    private final BiGroupNodeConstructor<A, B, QuadTuple<NewA, NewB, NewC, NewD>> nodeConstructor;

    public BavetQuadGroupBridgeBiConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractBiConstraintStream<Solution_, A, B> parent,
            BiGroupNodeConstructor<A, B, QuadTuple<NewA, NewB, NewC, NewD>> nodeConstructor) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.parent = parent;
        this.nodeConstructor = nodeConstructor;
    }

    @Override
    public boolean guaranteesDistinct() {
        return true;
    }

    public void setGroupStream(BavetGroupQuadConstraintStream<Solution_, NewA, NewB, NewC, NewD> groupStream) {
        this.groupStream = groupStream;
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    public void collectActiveConstraintStreams(Set<BavetAbstractConstraintStream<Solution_>> constraintStreamSet) {
        parent.collectActiveConstraintStreams(constraintStreamSet);
        constraintStreamSet.add(this);
    }

    @Override
    public <Score_ extends Score<Score_>> void buildNode(NodeBuildHelper<Score_> buildHelper) {
        if (!childStreamList.isEmpty()) {
            throw new IllegalStateException("Impossible state: the stream (" + this
                    + ") has an non-empty childStreamList (" + childStreamList + ") but it's a groupBy bridge.");
        }
        int inputStoreIndex = buildHelper.reserveTupleStoreIndex(parent.getTupleSource());
        Consumer<QuadTuple<NewA, NewB, NewC, NewD>> insert = buildHelper.getAggregatedInsert(groupStream.getChildStreamList());
        Consumer<QuadTuple<NewA, NewB, NewC, NewD>> retract =
                buildHelper.getAggregatedRetract(groupStream.getChildStreamList());
        int outputStoreSize = buildHelper.extractTupleStoreSize(groupStream);
        AbstractGroupNode<BiTuple<A, B>, QuadTuple<NewA, NewB, NewC, NewD>, ?, ?> node =
                nodeConstructor.apply(inputStoreIndex, insert, retract, outputStoreSize);
        buildHelper.addNode(node);
        buildHelper.putInsertUpdateRetract(this, node::insert, node::update, node::retract);
    }

    @Override
    public ConstraintStream getTupleSource() {
        return parent.getTupleSource();
    }

}
