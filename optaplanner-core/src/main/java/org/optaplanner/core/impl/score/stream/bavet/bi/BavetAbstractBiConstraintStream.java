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
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraint;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetNodeBuildPolicy;

public abstract class BavetAbstractBiConstraintStream<Solution_, A, B> extends BavetAbstractConstraintStream<Solution_>
        implements BiConstraintStream<A, B> {

    protected final List<BavetAbstractBiConstraintStream<Solution_, A, B>> childStreamList = new ArrayList<>(2);

    public BavetAbstractBiConstraintStream(BavetConstraint<Solution_> bavetConstraint) {
        super(bavetConstraint);
    }

    // ************************************************************************
    // Stream builder methods
    // ************************************************************************

    @Override
    public BavetAbstractBiConstraintStream<Solution_, A, B> filter(BiPredicate<A, B> predicate) {
        BavetFilterBiConstraintStream<Solution_, A, B> stream = new BavetFilterBiConstraintStream<>(constraint, predicate);
        childStreamList.add(stream);
        return stream;
    }

    // ************************************************************************
    // Join
    // ************************************************************************

    @Override
    public <C> TriConstraintStream<A, B, C> join(UniConstraintStream<C> otherStream) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public <C> TriConstraintStream<A, B, C> join(UniConstraintStream<C> otherStream, TriJoiner<A, B, C> joiner) {
        throw new UnsupportedOperationException(); // TODO
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
        // TODO FIXME depends on Score type
        addScoringBiConstraintStream(new BavetScoringBiConstraintStream<>(constraint, false));
    }

    @Override
    public void penalizeInt(ToIntBiFunction<A, B> matchWeigher) {
        addScoringBiConstraintStream(new BavetScoringBiConstraintStream<>(constraint, false, matchWeigher));
    }

    @Override
    public void penalizeLong(ToLongBiFunction<A, B> matchWeigher) {
        addScoringBiConstraintStream(new BavetScoringBiConstraintStream<>(constraint, false, matchWeigher));
    }

    @Override
    public void penalizeBigDecimal(BiFunction<A, B, BigDecimal> matchWeigher) {
        addScoringBiConstraintStream(new BavetScoringBiConstraintStream<>(constraint, false, matchWeigher));
    }

    @Override
    public void reward() {
        // TODO FIXME depends on Score type
        addScoringBiConstraintStream(new BavetScoringBiConstraintStream<>(constraint, true));
    }

    @Override
    public void rewardInt(ToIntBiFunction<A, B> matchWeigher) {
        addScoringBiConstraintStream(new BavetScoringBiConstraintStream<>(constraint, true, matchWeigher));
    }

    @Override
    public void rewardLong(ToLongBiFunction<A, B> matchWeigher) {
        addScoringBiConstraintStream(new BavetScoringBiConstraintStream<>(constraint, true, matchWeigher));
    }

    @Override
    public void rewardBigDecimal(BiFunction<A, B, BigDecimal> matchWeigher) {
        addScoringBiConstraintStream(new BavetScoringBiConstraintStream<>(constraint, true, matchWeigher));
    }

    private void addScoringBiConstraintStream(BavetScoringBiConstraintStream<Solution_, A, B> stream) {
        childStreamList.add(stream);
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    public BavetAbstractBiNode<A, B> createNodeChain(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder, BavetAbstractBiNode<A, B> parentNode) {
        buildPolicy.updateNodeOrderMaximum(nodeOrder);

        BavetAbstractBiNode<A, B> node = createNode(buildPolicy, constraintWeight, nodeOrder, parentNode);
        BavetAbstractBiNode<A, B> sharedNode = buildPolicy.retrieveSharedNode(node);
        if (sharedNode != node) {
            // Share node
            node = sharedNode;
        } else {
            if (parentNode != null) {
                parentNode.addChildNode(node);
            }
        }

        assertChildStreamListSize();
        for (BavetAbstractBiConstraintStream<Solution_, A, B> childStream : childStreamList) {
            childStream.createNodeChain(buildPolicy, constraintWeight, nodeOrder + 1, node);
        }
        return node;
    }

    protected abstract void assertChildStreamListSize();

    protected abstract BavetAbstractBiNode<A, B> createNode(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder, BavetAbstractBiNode<A, B> parentNode);

}
