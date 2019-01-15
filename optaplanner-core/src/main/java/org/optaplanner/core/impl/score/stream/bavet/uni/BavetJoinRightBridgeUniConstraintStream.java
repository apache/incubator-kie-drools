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

import java.util.function.Function;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraint;
import org.optaplanner.core.impl.score.stream.bavet.bi.BavetJoinBiConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.bi.BavetJoinBiNode;
import org.optaplanner.core.impl.score.stream.bavet.session.BavetNodeBuildPolicy;

public final class BavetJoinRightBridgeUniConstraintStream<Solution_, A, B, Property_>
        extends BavetAbstractUniConstraintStream<Solution_, B> {

    private final BavetJoinBiConstraintStream<Solution_, A, B, Property_> biStream;
    private final Function<B, Property_> mapping;

    public BavetJoinRightBridgeUniConstraintStream(BavetConstraint<Solution_> bavetConstraint,
            BavetJoinBiConstraintStream<Solution_, A, B, Property_> biStream, Function<B, Property_> mapping) {
        super(bavetConstraint);
        this.biStream = biStream;
        this.mapping = mapping;
    }

    // ************************************************************************
    // Node creation methods
    // ************************************************************************

    @Override
    protected BavetJoinRightBridgeUniNode<A, B, Property_> createNode(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder, BavetAbstractUniNode<B> nextNode) {
        if (nextNode != null) {
            throw new IllegalStateException("Impossible state: the stream (" + this + ") has one or more nextStreams ("
                    + nextStreamList + ") but it's a join bridge.");
        }
        BavetJoinBiNode<A, B, Property_> biNode = (BavetJoinBiNode<A, B, Property_>) buildPolicy.getStreamToNodeMap().get(biStream);
        if (biNode == null) {
            biNode = biStream.createNodeChain(buildPolicy, constraintWeight, nodeOrder + 1);
            buildPolicy.getStreamToNodeMap().put(biStream, biNode);
        }
        BavetJoinRightBridgeUniNode<A, B, Property_> node = new BavetJoinRightBridgeUniNode<>(buildPolicy.getSession(),
                nodeOrder, mapping, biNode);
        biNode.setRightParentNode(node);
        return node;
    }

}
