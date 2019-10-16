/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.drools.tri;

import org.optaplanner.core.api.score.stream.tri.TriJoiner;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;
import org.optaplanner.core.impl.score.stream.drools.bi.DroolsAbstractBiConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsAbstractUniConstraintStream;
import org.optaplanner.core.impl.score.stream.tri.AbstractTriJoiner;

public final class DroolsJoinTriConstraintStream<Solution_, A, B, C>
        extends DroolsAbstractTriConstraintStream<Solution_, A, B, C> {

    private final DroolsAbstractBiConstraintStream<Solution_, A, B> leftParentStream;
    private final DroolsAbstractUniConstraintStream<Solution_, C> rightParentStream;
    private final AbstractTriJoiner<A, B, C> triJoiner;

    public DroolsJoinTriConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractBiConstraintStream<Solution_, A, B> parent,
            DroolsAbstractUniConstraintStream<Solution_, C> otherStream, TriJoiner<A, B, C> triJoiner) {
        super(constraintFactory, null);
        this.leftParentStream = parent;
        this.rightParentStream = otherStream;
        this.triJoiner = (AbstractTriJoiner<A, B, C>) triJoiner;
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public DroolsTriCondition<A, B, C> createCondition() {
        return leftParentStream.createCondition().andJoin(rightParentStream.createCondition(), triJoiner);
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public DroolsAbstractBiConstraintStream<Solution_, A, B> getLeftParentStream() {
        return leftParentStream;
    }

    public DroolsAbstractUniConstraintStream<Solution_, C> getRightParentStream() {
        return rightParentStream;
    }

    @Override
    public String toString() {
        return "TriJoin() with " + getChildStreams().size()  + " children";
    }

}
