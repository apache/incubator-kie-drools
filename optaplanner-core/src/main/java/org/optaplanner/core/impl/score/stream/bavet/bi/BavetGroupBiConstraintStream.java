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
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintFactory;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetNodeBuildPolicy;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetFromUniConstraintStream;

public final class BavetGroupBiConstraintStream<Solution_, GroupKey_, ResultContainer_, Result_>
        extends BavetAbstractBiConstraintStream<Solution_, GroupKey_, Result_> {

    private final BavetAbstractConstraintStream<Solution_> parent;
    private final Function<ResultContainer_, Result_> finisher;

    public BavetGroupBiConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractConstraintStream<Solution_> parent,
            Function<ResultContainer_, Result_> finisher) {
        super(constraintFactory);
        this.parent = parent;
        this.finisher = finisher;
    }

    @Override
    public List<BavetFromUniConstraintStream<Solution_, Object>> getFromStreamList() {
        return parent.getFromStreamList();
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    public BavetGroupBiNode<GroupKey_, ResultContainer_, Result_> createNodeChain(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder, BavetAbstractBiNode<GroupKey_, Result_> parentNode) {
        return (BavetGroupBiNode<GroupKey_, ResultContainer_, Result_>) super.createNodeChain(buildPolicy, constraintWeight, nodeOrder, parentNode);
    }

    @Override
    protected BavetGroupBiNode<GroupKey_, ResultContainer_, Result_> createNode(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder, BavetAbstractBiNode<GroupKey_, Result_> parentNode) {
        if (parentNode != null) {
            throw new IllegalStateException("Impossible state: the stream (" + this
                    + ") cannot have a parentNode (" + parentNode + ").");
        }
        return new BavetGroupBiNode<>(buildPolicy.getSession(), nodeOrder, finisher);
    }

    @Override
    public String toString() {
        return "Group() with " + childStreamList.size()  + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
