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

package org.optaplanner.constraint.streams.bavet.uni;

import java.util.Set;

import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.NodeBuildHelper;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;

public final class BavetGroupUniConstraintStream<Solution_, A>
        extends BavetAbstractUniConstraintStream<Solution_, A> {

    private final BavetAbstractConstraintStream<Solution_> parent;

    public BavetGroupUniConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractConstraintStream<Solution_> parent) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.parent = parent;
    }

    @Override
    public boolean guaranteesDistinct() {
        return true;
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
    public ConstraintStream getTupleSource() {
        return this;
    }

    @Override
    public <Score_ extends Score<Score_>> void buildNode(NodeBuildHelper<Score_> buildHelper) {
        // Do nothing. BavetGroupBridgeUniConstraintStream, etc build everything.
    }

    // ************************************************************************
    // Equality for node sharing
    // ************************************************************************

    // TODO

    @Override
    public String toString() {
        return "Group() with " + childStreamList.size() + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
