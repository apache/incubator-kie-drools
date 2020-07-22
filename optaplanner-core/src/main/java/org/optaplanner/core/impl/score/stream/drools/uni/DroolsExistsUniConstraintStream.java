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

package org.optaplanner.core.impl.score.stream.drools.uni;

import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.UniConstraintGraphChildNode;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.UniConstraintGraphNode;

public final class DroolsExistsUniConstraintStream<Solution_, A> extends DroolsAbstractUniConstraintStream<Solution_, A> {

    private final UniConstraintGraphChildNode node;
    private final String streamName;

    public <B> DroolsExistsUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractUniConstraintStream<Solution_, A> parent, boolean shouldExist, Class<B> otherClass,
            BiJoiner<A, B>... joiners) {
        super(constraintFactory);
        this.node = shouldExist
                ? constraintFactory.getConstraintGraph().ifExists(parent.getConstraintGraphNode(), otherClass, joiners)
                : constraintFactory.getConstraintGraph().ifNotExists(parent.getConstraintGraphNode(), otherClass, joiners);
        this.streamName = shouldExist ? "IfExists()" : "IfNotExists()";
    }

    // ************************************************************************
    // Pattern creation
    // ************************************************************************

    @Override
    public UniConstraintGraphNode getConstraintGraphNode() {
        return node;
    }

    @Override
    public String toString() {
        return streamName + " with " + getChildStreams().size() + " children";
    }

}
