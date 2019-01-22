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

package org.optaplanner.core.impl.score.stream.bavet.bi;

import java.util.List;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraint;
import org.optaplanner.core.impl.score.stream.bavet.session.BavetNodeBuildPolicy;

public final class BavetJoinBiConstraintStream<Solution_, A, B, Property_> extends BavetAbstractBiConstraintStream<Solution_, A, B> {

    public BavetJoinBiConstraintStream(BavetConstraint<Solution_> bavetConstraint) {
        super(bavetConstraint);
    }

    // ************************************************************************
    // Node creation methods
    // ************************************************************************

    @Override
    public BavetJoinBiNode<A, B, Property_> createNodeChain(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder) {
        return (BavetJoinBiNode<A, B, Property_>) super.createNodeChain(buildPolicy, constraintWeight, nodeOrder);
    }

    @Override
    protected BavetAbstractBiNode<A, B> createNode(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder, List<BavetAbstractBiNode<A, B>> childNodeList) {
        if (childNodeList.isEmpty()) {
            throw new IllegalStateException("The stream (" + this + ") leads to nowhere.\n"
                    + "Maybe don't create it.");
        }
        return new BavetJoinBiNode<>(buildPolicy.getSession(), nodeOrder, childNodeList);
    }

    @Override
    public String toString() {
        return "Join() with " + childStreamList.size()  + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
