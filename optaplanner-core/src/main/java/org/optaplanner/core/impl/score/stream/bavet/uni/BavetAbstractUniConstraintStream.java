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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraint;
import org.optaplanner.core.impl.score.stream.bavet.bi.BavetGroupedBiConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.bi.BavetJoinBiConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetNodeBuildPolicy;
import org.optaplanner.core.impl.score.stream.bavet.common.index.BavetIndexFactory;
import org.optaplanner.core.impl.score.stream.bi.AbstractBiJoiner;

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

    // ************************************************************************
    // Join
    // ************************************************************************

    @Override
    public <B> BiConstraintStream<A, B> join(UniConstraintStream<B> otherStream) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public <B> BiConstraintStream<A, B> join(
                UniConstraintStream<B> otherStream, BiJoiner<A, B> joiner) {
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
        if (!(joiner instanceof AbstractBiJoiner)) {
            throw new IllegalArgumentException("The joiner class (" + joiner.getClass() + ") is not supported.");
        }
        AbstractBiJoiner<A, B> castedJoiner = (AbstractBiJoiner<A, B>) joiner;
        BavetIndexFactory indexFactory = new BavetIndexFactory(castedJoiner);
        BavetJoinBiConstraintStream<Solution_, A, B> biStream = new BavetJoinBiConstraintStream<>(constraint);
        BavetJoinBridgeUniConstraintStream<Solution_, A> leftBridge = new BavetJoinBridgeUniConstraintStream<>(
                constraint, biStream, true, castedJoiner.getLeftCombinedMapping(), indexFactory);
        childStreamList.add(leftBridge);
        BavetJoinBridgeUniConstraintStream<Solution_, B> rightBridge = new BavetJoinBridgeUniConstraintStream<>(
                constraint, biStream, false, castedJoiner.getRightCombinedMapping(), indexFactory);
        other.childStreamList.add(rightBridge);
        return biStream;
    }

    // ************************************************************************
    // Group by
    // ************************************************************************

    @Override
    public <GroupKey_> UniConstraintStream<GroupKey_> groupBy(Function<A, GroupKey_> groupKeyMapping) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public <GroupKeyA_, GroupKeyB_> BiConstraintStream<GroupKeyA_, GroupKeyB_> groupBy(Function<A, GroupKeyA_> groupKeyAMapping, Function<A, GroupKeyB_> groupKeyBMapping) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public <GroupKey_, ResultContainer_, Result_> BiConstraintStream<GroupKey_, Result_> groupBy(
            Function<A, GroupKey_> groupKeyMapping, UniConstraintCollector<A, ResultContainer_, Result_> collector) {
        BavetGroupedBiConstraintStream<Solution_, GroupKey_, ResultContainer_, Result_> biStream
                = new BavetGroupedBiConstraintStream<>(constraint, collector.finisher());
        BavetGroupByBridgeUniConstraintStream<Solution_, A, GroupKey_, ResultContainer_, Result_> bridge
                = new BavetGroupByBridgeUniConstraintStream<>(constraint, biStream, groupKeyMapping, collector);
        childStreamList.add(bridge);
        return biStream;
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainer_, Result_> TriConstraintStream<GroupKeyA_, GroupKeyB_, Result_> groupBy(
            Function<A, GroupKeyA_> groupKeyAMapping, Function<A, GroupKeyB_> groupKeyBMapping,
            UniConstraintCollector<A, ResultContainer_, Result_> collector) {
        throw new UnsupportedOperationException(); // TODO
    }

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    @Override
    public void penalize() {
        addScoringUniConstraintStream(new BavetScoringUniConstraintStream<>(constraint, false));
    }

    @Override
    public void penalize(ToIntFunction<A> matchWeigher) {
        addScoringUniConstraintStream(new BavetScoringUniConstraintStream<>(constraint, false, matchWeigher));
    }

    @Override
    public void penalizeLong(ToLongFunction<A> matchWeigher) {
        addScoringUniConstraintStream(new BavetScoringUniConstraintStream<>(constraint, false, matchWeigher));
    }

    @Override
    public void penalizeBigDecimal(Function<A, BigDecimal> matchWeigher) {
        addScoringUniConstraintStream(new BavetScoringUniConstraintStream<>(constraint, false, matchWeigher));
    }

    @Override
    public void reward() {
        addScoringUniConstraintStream(new BavetScoringUniConstraintStream<>(constraint, true));
    }

    @Override
    public void reward(ToIntFunction<A> matchWeigher) {
        addScoringUniConstraintStream(new BavetScoringUniConstraintStream<>(constraint, true, matchWeigher));
    }

    @Override
    public void rewardLong(ToLongFunction<A> matchWeigher) {
        addScoringUniConstraintStream(new BavetScoringUniConstraintStream<>(constraint, true, matchWeigher));
    }

    @Override
    public void rewardBigDecimal(Function<A, BigDecimal> matchWeigher) {
        addScoringUniConstraintStream(new BavetScoringUniConstraintStream<>(constraint, true, matchWeigher));
    }

    private void addScoringUniConstraintStream(BavetScoringUniConstraintStream<Solution_, A> stream) {
        childStreamList.add(stream);
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    public BavetAbstractUniNode<A> createNodeChain(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder, BavetAbstractUniNode<A> parentNode) {
        BavetAbstractUniNode<A> node = createNode(buildPolicy, constraintWeight, nodeOrder, parentNode);
        node = processNode(buildPolicy, nodeOrder, parentNode, node);
        createChildNodeChains(buildPolicy, constraintWeight, nodeOrder, node);
        return node;
    }

    protected BavetAbstractUniNode<A> processNode(BavetNodeBuildPolicy<Solution_> buildPolicy, int nodeOrder, BavetAbstractUniNode<A> parentNode, BavetAbstractUniNode<A> node) {
        buildPolicy.updateNodeOrderMaximum(nodeOrder);
        BavetAbstractUniNode<A> sharedNode = buildPolicy.retrieveSharedNode(node);
        if (sharedNode != node) {
            // Share node
            node = sharedNode;
        } else {
            if (parentNode != null) {
                parentNode.addChildNode(node);
            }
        }
        return node;
    }

    protected void createChildNodeChains(BavetNodeBuildPolicy<Solution_> buildPolicy, Score<?> constraintWeight, int nodeOrder, BavetAbstractUniNode<A> node) {
        if (childStreamList.isEmpty()) {
            throw new IllegalStateException("The stream (" + this + ") leads to nowhere.\n"
                    + "Maybe don't create it.");
        }
        for (BavetAbstractUniConstraintStream<Solution_, A> childStream : childStreamList) {
            childStream.createNodeChain(buildPolicy, constraintWeight, nodeOrder + 1, node);
        }
    }

    protected abstract BavetAbstractUniNode<A> createNode(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder, BavetAbstractUniNode<A> parentNode);

}
