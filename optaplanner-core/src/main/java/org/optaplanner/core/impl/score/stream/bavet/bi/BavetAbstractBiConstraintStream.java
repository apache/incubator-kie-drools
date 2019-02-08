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
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
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
    // Penalize/reward
    // ************************************************************************

    @Override
    public void penalize() {
        // TODO FIXME depends on Score type
        addScoringBiConstraintStream(new BavetScoringBiConstraintStream<>(constraint, false, (A a, B b) -> 1));
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
        addScoringBiConstraintStream(new BavetScoringBiConstraintStream<>(constraint, true, (A a, B b) -> 1));
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
    // Node creation methods
    // ************************************************************************

    public BavetAbstractBiNode<A, B> createNodeChain(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder) {
        buildPolicy.updateNodeOrderMaximum(nodeOrder);
        List<BavetAbstractBiNode<A, B>> childNodeList = new ArrayList<>(childStreamList.size());
        for (BavetAbstractBiConstraintStream<Solution_, A, B> childStream : childStreamList) {
            BavetAbstractBiNode<A, B> childNode = childStream.createNodeChain(
                    buildPolicy, constraintWeight, nodeOrder + 1);
            childNodeList.add(childNode);
        }
        return createNode(buildPolicy, constraintWeight, nodeOrder, childNodeList);
    }

    protected abstract BavetAbstractBiNode<A, B> createNode(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder, List<BavetAbstractBiNode<A, B>> childNodeList);

}
