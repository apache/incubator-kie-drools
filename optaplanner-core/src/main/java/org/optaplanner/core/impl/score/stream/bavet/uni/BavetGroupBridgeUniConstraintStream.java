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

package org.optaplanner.core.impl.score.stream.bavet.uni;

import java.util.List;
import java.util.function.Function;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintFactory;
import org.optaplanner.core.impl.score.stream.bavet.bi.BavetGroupBiConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.bi.BavetGroupBiNode;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetNodeBuildPolicy;

public final class BavetGroupBridgeUniConstraintStream<Solution_, A, NewA, ResultContainer_, NewB>
        extends BavetAbstractUniConstraintStream<Solution_, A> {

    private final BavetAbstractUniConstraintStream<Solution_, A> parent;
    private BavetGroupBiConstraintStream<Solution_, NewA, ResultContainer_, NewB> groupStream;
    private final Function<A, NewA> groupKeyMapping;
    private final UniConstraintCollector<A, ResultContainer_, NewB> collector;

    public BavetGroupBridgeUniConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractUniConstraintStream<Solution_, A> parent,
            Function<A, NewA> groupKeyMapping, UniConstraintCollector<A, ResultContainer_, NewB> collector) {
        super(constraintFactory);
        this.parent = parent;
        this.groupKeyMapping = groupKeyMapping;
        this.collector = collector;
    }

    public void setGroupStream(BavetGroupBiConstraintStream<Solution_, NewA, ResultContainer_, NewB> groupStream) {
        this.groupStream = groupStream;
    }

    @Override
    public List<BavetFromUniConstraintStream<Solution_, Object>> getFromStreamList() {
        return parent.getFromStreamList();
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    protected BavetGroupBridgeUniNode<A, NewA, ResultContainer_, NewB> createNode(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder, BavetAbstractUniNode<A> parentNode) {
        // TODO create bridge first. Move this to createChildNodeChains
        BavetGroupBiNode<NewA, ResultContainer_, NewB> groupNode = groupStream.createNodeChain(buildPolicy, constraintWeight, nodeOrder + 1, null);
        BavetGroupBridgeUniNode<A, NewA, ResultContainer_, NewB> node = new BavetGroupBridgeUniNode<>(
                buildPolicy.getSession(), nodeOrder, parentNode, groupKeyMapping, collector, groupNode);
        return node;
    }

    @Override
    protected void createChildNodeChains(BavetNodeBuildPolicy<Solution_> buildPolicy, Score<?> constraintWeight, int nodeOrder, BavetAbstractUniNode<A> node) {
        if (!childStreamList.isEmpty()) {
            throw new IllegalStateException("Impossible state: the stream (" + this
                    + ") has an non-empty childStreamList (" + childStreamList + ") but it's a groupBy bridge.");
        }
    }

    @Override
    public String toString() {
        return "GroupBridge()";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
