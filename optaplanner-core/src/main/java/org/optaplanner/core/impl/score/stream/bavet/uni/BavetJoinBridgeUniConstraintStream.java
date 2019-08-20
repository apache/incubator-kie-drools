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
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintFactory;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetJoinBridgeConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetJoinBridgeNode;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetJoinConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetNodeBuildPolicy;
import org.optaplanner.core.impl.score.stream.bavet.common.index.BavetIndexFactory;

public final class BavetJoinBridgeUniConstraintStream<Solution_, A>
        extends BavetAbstractUniConstraintStream<Solution_, A>
        implements BavetJoinBridgeConstraintStream<Solution_> {

    private final BavetAbstractUniConstraintStream<Solution_, A> parent;
    private BavetJoinConstraintStream<Solution_> joinStream;
    private final boolean isLeftBridge;
    private final Function<A, Object[]> mapping;
    private final BavetIndexFactory indexFactory;

    public BavetJoinBridgeUniConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractUniConstraintStream<Solution_, A> parent,
            boolean isLeftBridge,
            Function<A, Object[]> mapping, BavetIndexFactory indexFactory) {
        super(constraintFactory);
        this.parent = parent;
        this.isLeftBridge = isLeftBridge;
        this.mapping = mapping;
        this.indexFactory = indexFactory;
    }

    public void setJoinStream(BavetJoinConstraintStream<Solution_> joinStream) {
        this.joinStream = joinStream;
    }

    @Override
    public List<BavetFromUniConstraintStream<Solution_, Object>> getFromStreamList() {
        return parent.getFromStreamList();
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    protected BavetJoinBridgeUniNode<A> createNode(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder, BavetAbstractUniNode<A> parentNode) {
        BavetJoinBridgeUniNode<A> node = new BavetJoinBridgeUniNode<>(buildPolicy.getSession(),
                nodeOrder, parentNode, mapping, indexFactory.buildIndex(isLeftBridge));
        return node;
    }

    @Override
    protected void createChildNodeChains(BavetNodeBuildPolicy<Solution_> buildPolicy, Score<?> constraintWeight, int nodeOrder, BavetAbstractUniNode<A> uncastedNode) {
        if (!childStreamList.isEmpty()) {
            throw new IllegalStateException("Impossible state: the stream (" + this
                    + ") has an non-empty childStreamList (" + childStreamList + ") but it's a join bridge.");
        }
        BavetJoinBridgeUniNode<A> node = (BavetJoinBridgeUniNode<A>) uncastedNode;
        BavetJoinBridgeNode otherBridgeNode = buildPolicy.getJoinConstraintStreamToJoinBridgeNodeMap().get(joinStream);
        if (otherBridgeNode == null) {
            buildPolicy.getJoinConstraintStreamToJoinBridgeNodeMap().put(joinStream, node);
        } else {
            BavetJoinBridgeNode leftNode = isLeftBridge ? node : otherBridgeNode;
            BavetJoinBridgeNode rightNode = isLeftBridge ? otherBridgeNode : node;
            int maxNodeOrder = Math.max(leftNode.getNodeOrder(), rightNode.getNodeOrder());
            joinStream.createNodeChain(buildPolicy, constraintWeight, maxNodeOrder + 1, leftNode, rightNode);
        }
    }

    @Override
    public String toString() {
        return "JoinBridge()";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
