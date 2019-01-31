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
import java.util.function.Function;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraint;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetNodeBuildPolicy;

public final class BavetGroupedBiConstraintStream<Solution_, GroupKey_, ResultContainer_, Result_>
        extends BavetAbstractBiConstraintStream<Solution_, GroupKey_, Result_> {

    private final Function<ResultContainer_, Result_> finisher;

    public BavetGroupedBiConstraintStream(BavetConstraint<Solution_> bavetConstraint,
            Function<ResultContainer_, Result_> finisher) {
        super(bavetConstraint);
        this.finisher = finisher;
    }

    // ************************************************************************
    // Node creation methods
    // ************************************************************************

    @Override
    public BavetGroupedBiNode<GroupKey_, ResultContainer_, Result_> createNodeChain(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder) {
        return (BavetGroupedBiNode<GroupKey_, ResultContainer_, Result_>) super.createNodeChain(buildPolicy, constraintWeight, nodeOrder);
    }

    @Override
    protected BavetAbstractBiNode<GroupKey_, Result_> createNode(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder, List<BavetAbstractBiNode<GroupKey_, Result_>> childNodeList) {
        if (childNodeList.isEmpty()) {
            throw new IllegalStateException("The stream (" + this + ") leads to nowhere.\n"
                    + "Maybe don't create it.");
        }
        return new BavetGroupedBiNode<>(buildPolicy.getSession(), nodeOrder, finisher, childNodeList);
    }

    @Override
    public String toString() {
        return "Grouped() with " + childStreamList.size()  + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
