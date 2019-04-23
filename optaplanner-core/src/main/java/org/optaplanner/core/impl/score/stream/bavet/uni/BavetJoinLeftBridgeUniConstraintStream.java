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
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraint;
import org.optaplanner.core.impl.score.stream.bavet.bi.BavetJoinBiConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.bi.BavetJoinBiNode;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetNodeBuildPolicy;
import org.optaplanner.core.impl.score.stream.bavet.common.index.BavetIndexFactory;

public final class BavetJoinLeftBridgeUniConstraintStream<Solution_, A, B>
        extends BavetAbstractUniConstraintStream<Solution_, A> {

    private final BavetJoinBiConstraintStream<Solution_, A, B> biStream;
    private final Function<A, Object[]> mapping;
    private final BavetIndexFactory indexFactory;

    public BavetJoinLeftBridgeUniConstraintStream(BavetConstraint<Solution_> bavetConstraint,
            BavetJoinBiConstraintStream<Solution_, A, B> biStream,
            Function<A, Object[]> mapping, BavetIndexFactory indexFactory) {
        super(bavetConstraint);
        this.biStream = biStream;
        this.mapping = mapping;
        this.indexFactory = indexFactory;
    }

    // ************************************************************************
    // Node creation methods
    // ************************************************************************

    @Override
    protected BavetJoinLeftBridgeUniNode<A, B> createNode(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder, List<BavetAbstractUniNode<A>> childNodeList) {
        if (!childNodeList.isEmpty()) {
            throw new IllegalStateException("Impossible state: the stream (" + this
                    + ") has an non-empty childNodeList (" + childNodeList + ") but it's a join bridge.");
        }
        BavetJoinBiNode<A, B> biNode = (BavetJoinBiNode<A, B>) buildPolicy.getStreamToNodeMap().get(biStream);
        if (biNode == null) {
            biNode = biStream.createNodeChain(buildPolicy, constraintWeight, nodeOrder + 1); // TODO BUG needs max(left node order, right node order)
            buildPolicy.getStreamToNodeMap().put(biStream, biNode);
        }
        BavetJoinLeftBridgeUniNode<A, B> node = new BavetJoinLeftBridgeUniNode<>(buildPolicy.getSession(),
                nodeOrder, mapping, indexFactory.buildIndex(true), biNode);
        biNode.setLeftParentNode(node);
        return node;
    }

    @Override
    public String toString() {
        return "JoinLeftBridge()";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
