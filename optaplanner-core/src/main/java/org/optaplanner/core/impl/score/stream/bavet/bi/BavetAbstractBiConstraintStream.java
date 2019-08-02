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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraint;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetNodeBuildPolicy;
import org.optaplanner.core.impl.score.stream.bavet.common.index.BavetIndexFactory;
import org.optaplanner.core.impl.score.stream.bavet.tri.BavetJoinTriConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetAbstractUniConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetJoinBridgeUniConstraintStream;
import org.optaplanner.core.impl.score.stream.tri.AbstractTriJoiner;

public abstract class BavetAbstractBiConstraintStream<Solution_, A, B> extends BavetAbstractConstraintStream<Solution_>
        implements BiConstraintStream<A, B> {

    protected final List<BavetAbstractBiConstraintStream<Solution_, A, B>> childStreamList = new ArrayList<>(2);

    public BavetAbstractBiConstraintStream(BavetConstraint<Solution_> bavetConstraint) {
        super(bavetConstraint);
    }

    // ************************************************************************
    // Stream builder methods
    // ************************************************************************

    public void addChildStream(BavetAbstractBiConstraintStream<Solution_, A, B> childStream) {
        childStreamList.add(childStream);
    }

    // ************************************************************************
    // Filter
    // ************************************************************************

    @Override
    public BavetAbstractBiConstraintStream<Solution_, A, B> filter(BiPredicate<A, B> predicate) {
        BavetFilterBiConstraintStream<Solution_, A, B> stream = new BavetFilterBiConstraintStream<>(constraint, predicate);
        addChildStream(stream);
        return stream;
    }

    // ************************************************************************
    // Join
    // ************************************************************************

    @Override
    public <C> TriConstraintStream<A, B, C> join(UniConstraintStream<C> otherStream, TriJoiner<A, B, C> joiner) {
        if (!(otherStream instanceof BavetAbstractUniConstraintStream)) {
            throw new IllegalStateException("The streams (" + this + ", " + otherStream
                    + ") are not build from the same " + ConstraintFactory.class.getSimpleName() + ".");
        }
        BavetAbstractUniConstraintStream<Solution_, C> other = (BavetAbstractUniConstraintStream<Solution_, C>) otherStream;
        if (constraint != other.getConstraint()) {
            throw new IllegalStateException("The streams (" + this + ", " + other
                    + ") are build from different constraints (" + constraint + ", " + other.getConstraint()
                    + ").");
        }
        if (!(joiner instanceof AbstractTriJoiner)) {
            throw new IllegalArgumentException("The joiner class (" + joiner.getClass() + ") is not supported.");
        }
        AbstractTriJoiner<A, B, C> castedJoiner = (AbstractTriJoiner<A, B, C>) joiner;
        BavetIndexFactory indexFactory = new BavetIndexFactory(castedJoiner);
        BavetJoinTriConstraintStream<Solution_, A, B, C> triStream = new BavetJoinTriConstraintStream<>(constraint);
        BavetJoinBridgeBiConstraintStream<Solution_, A, B> leftBridge = new BavetJoinBridgeBiConstraintStream<>(
                constraint, triStream, true, castedJoiner.getLeftCombinedMapping(), indexFactory);
        addChildStream(leftBridge);
        BavetJoinBridgeUniConstraintStream<Solution_, C> rightBridge = new BavetJoinBridgeUniConstraintStream<>(
                constraint, triStream, false, castedJoiner.getRightCombinedMapping(), indexFactory);
        other.addChildStream(rightBridge);
        return triStream;
    }

    // ************************************************************************
    // Group by
    // ************************************************************************

    @Override
    public <GroupKey_> UniConstraintStream<GroupKey_> groupBy(BiFunction<A, B, GroupKey_> groupKeyMapping) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public <GroupKey_, ResultContainer_, Result_> BiConstraintStream<GroupKey_, Result_> groupBy(BiFunction<A, B, GroupKey_> groupKeyMapping, BiConstraintCollector<A, B, ResultContainer_, Result_> collector) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public <GroupKeyA_, GroupKeyB_> BiConstraintStream<GroupKeyA_, GroupKeyB_> groupBy(BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public <GroupKeyA_, GroupKeyB_, ResultContainer_, Result_> TriConstraintStream<GroupKeyA_, GroupKeyB_, Result_> groupBy(BiFunction<A, B, GroupKeyA_> groupKeyAMapping, BiFunction<A, B, GroupKeyB_> groupKeyBMapping, BiConstraintCollector<A, B, ResultContainer_, Result_> collector) {
        throw new UnsupportedOperationException(); // TODO
    }

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    @Override
    public void penalize() {
        addChildStream(new BavetScoringBiConstraintStream<>(constraint, false));
    }

    @Override
    public void penalize(ToIntBiFunction<A, B> matchWeigher) {
        addChildStream(new BavetScoringBiConstraintStream<>(constraint, false, matchWeigher));
    }

    @Override
    public void penalizeLong(ToLongBiFunction<A, B> matchWeigher) {
        addChildStream(new BavetScoringBiConstraintStream<>(constraint, false, matchWeigher));
    }

    @Override
    public void penalizeBigDecimal(BiFunction<A, B, BigDecimal> matchWeigher) {
        addChildStream(new BavetScoringBiConstraintStream<>(constraint, false, matchWeigher));
    }

    @Override
    public void reward() {
        addChildStream(new BavetScoringBiConstraintStream<>(constraint, true));
    }

    @Override
    public void reward(ToIntBiFunction<A, B> matchWeigher) {
        addChildStream(new BavetScoringBiConstraintStream<>(constraint, true, matchWeigher));
    }

    @Override
    public void rewardLong(ToLongBiFunction<A, B> matchWeigher) {
        addChildStream(new BavetScoringBiConstraintStream<>(constraint, true, matchWeigher));
    }

    @Override
    public void rewardBigDecimal(BiFunction<A, B, BigDecimal> matchWeigher) {
        addChildStream(new BavetScoringBiConstraintStream<>(constraint, true, matchWeigher));
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    public BavetAbstractBiNode<A, B> createNodeChain(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder, BavetAbstractBiNode<A, B> parentNode) {
        BavetAbstractBiNode<A, B> node = createNode(buildPolicy, constraintWeight, nodeOrder, parentNode);
        node = processNode(buildPolicy, nodeOrder, parentNode, node);
        createChildNodeChains(buildPolicy, constraintWeight, nodeOrder, node);
        return node;
    }

    protected BavetAbstractBiNode<A, B> processNode(BavetNodeBuildPolicy<Solution_> buildPolicy, int nodeOrder, BavetAbstractBiNode<A, B> parentNode, BavetAbstractBiNode<A, B> node) {
        buildPolicy.updateNodeOrderMaximum(nodeOrder);
        BavetAbstractBiNode<A, B> sharedNode = buildPolicy.retrieveSharedNode(node);
        if (sharedNode != node) {
            // Share node
            node = sharedNode;
        } else {
            if (parentNode != null) { // TODO remove null check and don't go through this code like this for from and joins
                parentNode.addChildNode(node);
            }
        }
        return node;
    }

    protected void createChildNodeChains(BavetNodeBuildPolicy<Solution_> buildPolicy, Score<?> constraintWeight, int nodeOrder, BavetAbstractBiNode<A, B> node) {
        if (childStreamList.isEmpty()) {
            throw new IllegalStateException("The stream (" + this + ") leads to nowhere.\n"
                    + "Maybe don't create it.");
        }
        for (BavetAbstractBiConstraintStream<Solution_, A, B> childStream : childStreamList) {
            childStream.createNodeChain(buildPolicy, constraintWeight, nodeOrder + 1, node);
        }
    }

    protected abstract BavetAbstractBiNode<A, B> createNode(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder, BavetAbstractBiNode<A, B> parentNode);

}
