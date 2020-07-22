/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.drools.quad;

import org.optaplanner.core.api.score.stream.quad.QuadJoiner;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.QuadConstraintGraphNode;
import org.optaplanner.core.impl.score.stream.drools.tri.DroolsAbstractTriConstraintStream;
import org.optaplanner.core.impl.score.stream.drools.uni.DroolsAbstractUniConstraintStream;

public final class DroolsJoinQuadConstraintStream<Solution_, A, B, C, D>
        extends DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> {

    private final QuadConstraintGraphNode node;

    public DroolsJoinQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractTriConstraintStream<Solution_, A, B, C> parent,
            DroolsAbstractUniConstraintStream<Solution_, D> otherStream, QuadJoiner<A, B, C, D> joiner) {
        super(constraintFactory);
        this.node = constraintFactory.getConstraintGraph().join(parent.getConstraintGraphNode(),
                otherStream.getConstraintGraphNode(), joiner);
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public QuadConstraintGraphNode getConstraintGraphNode() {
        return node;
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public String toString() {
        return "QuadJoin() with " + getChildStreams().size() + " children";
    }

}
