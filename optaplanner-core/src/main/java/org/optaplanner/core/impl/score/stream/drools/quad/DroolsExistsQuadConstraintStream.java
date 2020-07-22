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

import org.optaplanner.core.api.score.stream.penta.PentaJoiner;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.QuadConstraintGraphNode;

public final class DroolsExistsQuadConstraintStream<Solution_, A, B, C, D>
        extends DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> {

    private final QuadConstraintGraphNode node;
    private final String streamName;

    public <E> DroolsExistsQuadConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractQuadConstraintStream<Solution_, A, B, C, D> parent, boolean shouldExist, Class<E> otherClass,
            PentaJoiner<A, B, C, D, E>... joiners) {
        super(constraintFactory);
        this.node = shouldExist
                ? constraintFactory.getConstraintGraph().ifExists(parent.getConstraintGraphNode(), otherClass, joiners)
                : constraintFactory.getConstraintGraph().ifNotExists(parent.getConstraintGraphNode(), otherClass, joiners);
        this.streamName = shouldExist ? "QuadIfExists()" : "QuadIfNotExists()";
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public QuadConstraintGraphNode getConstraintGraphNode() {
        return node;
    }

    @Override
    public String toString() {
        return streamName + " with " + getChildStreams().size() + " children";
    }

}
