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
import org.optaplanner.core.api.score.stream.uni.UniCollector;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraint;
import org.optaplanner.core.impl.score.stream.bavet.bi.BavetGroupedBiConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.bi.BavetGroupedBiNode;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetNodeBuildPolicy;

public final class BavetGroupByBridgeUniConstraintStream<Solution_, A, GroupKey_, ResultContainer_, Result_>
        extends BavetAbstractUniConstraintStream<Solution_, A> {

    private final BavetGroupedBiConstraintStream<Solution_, GroupKey_, ResultContainer_, Result_> biStream;
    private final Function<A, GroupKey_> groupKeyMapping;
    private final UniCollector<A, ResultContainer_, Result_> collector;

    public BavetGroupByBridgeUniConstraintStream(BavetConstraint<Solution_> bavetConstraint,
            BavetGroupedBiConstraintStream<Solution_, GroupKey_, ResultContainer_, Result_> biStream,
            Function<A, GroupKey_> groupKeyMapping, UniCollector<A, ResultContainer_, Result_> collector) {
        super(bavetConstraint);
        this.biStream = biStream;
        this.groupKeyMapping = groupKeyMapping;
        this.collector = collector;
    }

    // ************************************************************************
    // Node creation methods
    // ************************************************************************

    @Override
    protected BavetGroupByBridgeUniNode<A, GroupKey_, ResultContainer_, Result_> createNode(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder, List<BavetAbstractUniNode<A>> childNodeList) {
        if (!childNodeList.isEmpty()) {
            throw new IllegalStateException("Impossible state: the stream (" + this
                    + ") has an non-empty childNodeList (" + childNodeList + ") but it's a groupBy bridge.");
        }
        BavetGroupedBiNode<GroupKey_, ResultContainer_, Result_> biNode = biStream.createNodeChain(buildPolicy, constraintWeight, nodeOrder + 1);
        BavetGroupByBridgeUniNode<A, GroupKey_, ResultContainer_, Result_> node = new BavetGroupByBridgeUniNode<>(
                buildPolicy.getSession(), nodeOrder, groupKeyMapping, collector, biNode);
        return node;
    }

    @Override
    public String toString() {
        return "GroupByBridge()";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
