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

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraint;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetJoinConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetNodeBuildPolicy;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetJoinBridgeUniNode;

public final class BavetJoinBiConstraintStream<Solution_, A, B> extends BavetAbstractBiConstraintStream<Solution_, A, B>
        implements BavetJoinConstraintStream<Solution_> {

    public BavetJoinBiConstraintStream(BavetConstraint<Solution_> bavetConstraint) {
        super(bavetConstraint);
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    public BavetJoinBiNode<A, B> createNodeChain(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder, BavetJoinBridgeUniNode<?> leftNode_, BavetJoinBridgeUniNode<?> rightNode_) {
        BavetJoinBridgeUniNode<A> leftNode = (BavetJoinBridgeUniNode<A>) leftNode_;
        BavetJoinBridgeUniNode<B> rightNode = (BavetJoinBridgeUniNode<B>) rightNode_;
        BavetJoinBiNode<A, B> node = new BavetJoinBiNode<>(buildPolicy.getSession(), nodeOrder, leftNode, rightNode);
        leftNode.setChildTupleRefresher(node::refreshChildTuplesLeft); // TODO don't register if shared
        rightNode.setChildTupleRefresher(node::refreshChildTuplesRight);
        node = (BavetJoinBiNode<A, B>) processNode(buildPolicy, nodeOrder, null, node); // TODO Sharing never happens
        createChildNodeChains(buildPolicy, constraintWeight, nodeOrder, node);
        return node;
    }

    @Override
    protected BavetJoinBiNode<A, B> createNode(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder, BavetAbstractBiNode<A, B> parentNode) {
        throw new IllegalStateException("Impossible state: this code is never called.");
    }

    @Override
    public String toString() {
        return "Join() with " + childStreamList.size()  + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
