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

import java.util.function.Predicate;

import org.optaplanner.core.impl.score.stream.drools.DroolsConstraintFactory;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.UniConstraintGraphChildNode;
import org.optaplanner.core.impl.score.stream.drools.common.nodes.UniConstraintGraphNode;

public final class DroolsFilterUniConstraintStream<Solution_, A> extends DroolsAbstractUniConstraintStream<Solution_, A> {

    private final UniConstraintGraphChildNode node;

    public DroolsFilterUniConstraintStream(DroolsConstraintFactory<Solution_> constraintFactory,
            DroolsAbstractUniConstraintStream<Solution_, A> parent, Predicate<A> predicate) {
        super(constraintFactory);
        this.node = constraintFactory.getConstraintGraph().filter(parent.getConstraintGraphNode(), predicate);
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
        return "Filter() with " + getChildStreams().size() + " children";
    }

}
