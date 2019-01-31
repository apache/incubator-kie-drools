/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.api.score.stream.uni.UniCollector;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraint;
import org.optaplanner.core.impl.score.stream.bavet.bi.BavetGroupedBiConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.bi.BavetJoinBiConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetNodeBuildPolicy;

public abstract class BavetAbstractUniConstraintStream<Solution_, A> extends BavetAbstractConstraintStream<Solution_>
        implements UniConstraintStream<A> {

    protected final List<BavetAbstractUniConstraintStream<Solution_, A>> childStreamList = new ArrayList<>(2);

    public BavetAbstractUniConstraintStream(BavetConstraint<Solution_> bavetConstraint) {
        super(bavetConstraint);
    }

    // ************************************************************************
    // Stream builder methods
    // ************************************************************************

    @Override
    public BavetAbstractUniConstraintStream<Solution_, A> filter(Predicate<A> predicate) {
        BavetFilterUniConstraintStream<Solution_, A> stream = new BavetFilterUniConstraintStream<>(constraint, predicate);
        childStreamList.add(stream);
        return stream;
    }

    @Override
    public <B, Property_> BiConstraintStream<A, B> join(
                UniConstraintStream<B> otherStream, BiJoiner<A, B, Property_> joiner) {
        if (!(otherStream instanceof BavetAbstractUniConstraintStream)) {
            throw new IllegalStateException("The streams (" + this + ", " + otherStream
                    + ") are not build from the same " + ConstraintFactory.class.getSimpleName() + ".");
        }
        BavetAbstractUniConstraintStream<Solution_, B> other = (BavetAbstractUniConstraintStream<Solution_, B>) otherStream;
        if (constraint != other.constraint) {
            throw new IllegalStateException("The streams (" + this + ", " + other
                    + ") are build from different constraints (" + constraint + ", " + other.constraint
                    + ").");
        }
        BavetJoinBiConstraintStream<Solution_, A, B, Property_> biStream = new BavetJoinBiConstraintStream<>(constraint);
        BavetJoinLeftBridgeUniConstraintStream<Solution_, A, B, Property_> leftBridge = new BavetJoinLeftBridgeUniConstraintStream<>(
                constraint, biStream, joiner.getLeftMapping());
        childStreamList.add(leftBridge);
        BavetJoinRightBridgeUniConstraintStream<Solution_, A, B, Property_> rightBridge = new BavetJoinRightBridgeUniConstraintStream<>(
                constraint, biStream, joiner.getRightMapping());
        other.childStreamList.add(rightBridge);
        return biStream;
    }

    @Override
    public <GroupKey_, ResultContainer_, Result_> BiConstraintStream<GroupKey_, Result_> groupBy(
            Function<A, GroupKey_> groupKeyMapping, UniCollector<A, ResultContainer_, Result_> collector) {
        BavetGroupedBiConstraintStream<Solution_, GroupKey_, ResultContainer_, Result_> biStream
                = new BavetGroupedBiConstraintStream<>(constraint, collector.finisher());
        BavetGroupByBridgeUniConstraintStream<Solution_, A, GroupKey_, ResultContainer_, Result_> bridge
                = new BavetGroupByBridgeUniConstraintStream<>(constraint, biStream, groupKeyMapping, collector);
        childStreamList.add(bridge);
        return biStream;
    }

    @Override
    public void penalize() {
        ToIntFunction<A> matchWeigher = (A a) -> -1;
        addIntScoring(matchWeigher);
    }

    @Override
    public void reward() {
        ToIntFunction<A> matchWeigher = (A a) -> 1;
        addIntScoring(matchWeigher);
    }

    private void addIntScoring(ToIntFunction<A> matchWeigher) {
        BavetIntScoringUniConstraintStream<Solution_, A> stream = new BavetIntScoringUniConstraintStream<>(constraint, matchWeigher);
        childStreamList.add(stream);
    }

    // ************************************************************************
    // Node creation methods
    // ************************************************************************

    public BavetAbstractUniNode<A> createNodeChain(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder) {
        buildPolicy.updateNodeOrderMaximum(nodeOrder);
        List<BavetAbstractUniNode<A>> childNodeList = new ArrayList<>(childStreamList.size());
        for (BavetAbstractUniConstraintStream<Solution_, A> childStream : childStreamList) {
            BavetAbstractUniNode<A> childNode = childStream.createNodeChain(
                    buildPolicy, constraintWeight, nodeOrder + 1);
            childNodeList.add(childNode);
        }
        return createNode(buildPolicy, constraintWeight, nodeOrder, childNodeList);
    }

    protected abstract BavetAbstractUniNode<A> createNode(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder, List<BavetAbstractUniNode<A>> childNodeList);

}
