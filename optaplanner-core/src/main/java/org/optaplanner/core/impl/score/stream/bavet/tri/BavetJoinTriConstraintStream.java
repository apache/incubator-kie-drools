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

package org.optaplanner.core.impl.score.stream.bavet.tri;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraint;
import org.optaplanner.core.impl.score.stream.bavet.bi.BavetJoinBridgeBiNode;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetJoinBridgeNode;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetJoinConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetNodeBuildPolicy;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetJoinBridgeUniNode;

public final class BavetJoinTriConstraintStream<Solution_, A, B, C> extends BavetAbstractTriConstraintStream<Solution_, A, B, C>
        implements BavetJoinConstraintStream<Solution_> {

    public BavetJoinTriConstraintStream(BavetConstraint<Solution_> bavetConstraint) {
        super(bavetConstraint);
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    public BavetJoinTriNode<A, B, C> createNodeChain(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder, BavetJoinBridgeNode leftNode_, BavetJoinBridgeNode rightNode_) {
        BavetJoinBridgeBiNode<A, B> leftNode = (BavetJoinBridgeBiNode<A, B>) leftNode_;
        BavetJoinBridgeUniNode<C> rightNode = (BavetJoinBridgeUniNode<C>) rightNode_;
        BavetJoinTriNode<A, B, C> node = new BavetJoinTriNode<>(buildPolicy.getSession(), nodeOrder, leftNode, rightNode);
        leftNode.setChildTupleRefresher(node::refreshChildTuplesLeft); // TODO don't register if shared
        rightNode.setChildTupleRefresher(node::refreshChildTuplesRight);
        node = (BavetJoinTriNode<A, B, C>) processNode(buildPolicy, nodeOrder, null, node); // TODO Sharing never happens
        createChildNodeChains(buildPolicy, constraintWeight, nodeOrder, node);
        return node;
    }

    @Override
    protected BavetJoinTriNode<A, B, C> createNode(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder, BavetAbstractTriNode<A, B, C> parentNode) {
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
