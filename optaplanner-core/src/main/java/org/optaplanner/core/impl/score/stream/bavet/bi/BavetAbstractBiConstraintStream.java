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
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraint;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintFactory;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetNodeBuildPolicy;
import org.optaplanner.core.impl.score.stream.bavet.common.index.BavetIndexFactory;
import org.optaplanner.core.impl.score.stream.bavet.tri.BavetJoinTriConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetAbstractUniConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetJoinBridgeUniConstraintStream;
import org.optaplanner.core.impl.score.stream.bi.InnerBiConstraintStream;
import org.optaplanner.core.impl.score.stream.tri.AbstractTriJoiner;

public abstract class BavetAbstractBiConstraintStream<Solution_, A, B> extends BavetAbstractConstraintStream<Solution_>
        implements InnerBiConstraintStream<A, B> {

    protected final List<BavetAbstractBiConstraintStream<Solution_, A, B>> childStreamList = new ArrayList<>(2);

    public BavetAbstractBiConstraintStream(BavetConstraintFactory<Solution_> constraintFactory) {
        super(constraintFactory);
    }

    // ************************************************************************
    // Stream builder methods
    // ************************************************************************

    protected void addChildStream(BavetAbstractBiConstraintStream<Solution_, A, B> childStream) {
        childStreamList.add(childStream);
    }

    // ************************************************************************
    // Filter
    // ************************************************************************

    @Override
    public BavetAbstractBiConstraintStream<Solution_, A, B> filter(BiPredicate<A, B> predicate) {
        BavetFilterBiConstraintStream<Solution_, A, B> stream = new BavetFilterBiConstraintStream<>(constraintFactory, this, predicate);
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
        if (constraintFactory != other.getConstraintFactory()) {
            throw new IllegalStateException("The streams (" + this + ", " + other
                    + ") are build from different constraintFactories (" + constraintFactory + ", " + other.getConstraintFactory()
                    + ").");
        }
        if (!(joiner instanceof AbstractTriJoiner)) {
            throw new IllegalArgumentException("The joiner class (" + joiner.getClass() + ") is not supported.");
        }
        AbstractTriJoiner<A, B, C> castedJoiner = (AbstractTriJoiner<A, B, C>) joiner;
        BavetIndexFactory indexFactory = new BavetIndexFactory(castedJoiner);
        BavetJoinBridgeBiConstraintStream<Solution_, A, B> leftBridge = new BavetJoinBridgeBiConstraintStream<>(
                constraintFactory, this, true, castedJoiner.getLeftCombinedMapping(), indexFactory);
        addChildStream(leftBridge);
        BavetJoinBridgeUniConstraintStream<Solution_, C> rightBridge = new BavetJoinBridgeUniConstraintStream<>(
                constraintFactory, other, false, castedJoiner.getRightCombinedMapping(), indexFactory);
        other.addChildStream(rightBridge);
        BavetJoinTriConstraintStream<Solution_, A, B, C> joinStream = new BavetJoinTriConstraintStream<>(constraintFactory, leftBridge, rightBridge);
        leftBridge.setJoinStream(joinStream);
        rightBridge.setJoinStream(joinStream);
        return joinStream;
    }

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    @Override
    public final Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            boolean positive) {
        BavetConstraint<Solution_> constraint = buildConstraint(constraintPackage, constraintName, constraintWeight,
                positive);
        BavetScoringBiConstraintStream<Solution_, A, B> stream = new BavetScoringBiConstraintStream<>(constraintFactory,
                this, constraint);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public final Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntBiFunction<A, B> matchWeigher, boolean positive) {
        BavetConstraint<Solution_> constraint = buildConstraint(constraintPackage, constraintName, constraintWeight,
                positive);
        BavetScoringBiConstraintStream<Solution_, A, B> stream = new BavetScoringBiConstraintStream<>(constraintFactory,
                this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public final Constraint impactScoreLong(String constraintPackage, String constraintName,
            Score<?> constraintWeight, ToLongBiFunction<A, B> matchWeigher, boolean positive) {
        BavetConstraint<Solution_> constraint = buildConstraint(constraintPackage, constraintName, constraintWeight,
                positive);
        BavetScoringBiConstraintStream<Solution_, A, B> stream = new BavetScoringBiConstraintStream<>(constraintFactory,
                this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public final Constraint impactScoreBigDecimal(String constraintPackage, String constraintName,
            Score<?> constraintWeight, BiFunction<A, B, BigDecimal> matchWeigher, boolean positive) {
        BavetConstraint<Solution_> constraint = buildConstraint(constraintPackage, constraintName, constraintWeight,
                positive);
        BavetScoringBiConstraintStream<Solution_, A, B> stream = new BavetScoringBiConstraintStream<>(constraintFactory,
                this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public final Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            boolean positive) {
        BavetConstraint<Solution_> constraint = buildConstraintConfigurable(constraintPackage, constraintName,
                positive);
        BavetScoringBiConstraintStream<Solution_, A, B> stream = new BavetScoringBiConstraintStream<>(constraintFactory,
                this, constraint);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public final Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ToIntBiFunction<A, B> matchWeigher, boolean positive) {
        BavetConstraint<Solution_> constraint = buildConstraintConfigurable(constraintPackage, constraintName,
                positive);
        BavetScoringBiConstraintStream<Solution_, A, B> stream = new BavetScoringBiConstraintStream<>(constraintFactory,
                this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public final Constraint impactScoreConfigurableLong(String constraintPackage, String constraintName,
            ToLongBiFunction<A, B> matchWeigher, boolean positive) {
        BavetConstraint<Solution_> constraint = buildConstraintConfigurable(constraintPackage, constraintName,
                positive);
        BavetScoringBiConstraintStream<Solution_, A, B> stream = new BavetScoringBiConstraintStream<>(constraintFactory,
                this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public final Constraint impactScoreConfigurableBigDecimal(String constraintPackage, String constraintName,
            BiFunction<A, B, BigDecimal> matchWeigher, boolean positive) {
        BavetConstraint<Solution_> constraint = buildConstraintConfigurable(constraintPackage, constraintName,
                positive);
        BavetScoringBiConstraintStream<Solution_, A, B> stream = new BavetScoringBiConstraintStream<>(constraintFactory,
                this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
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

    protected BavetAbstractBiNode<A, B> processNode(BavetNodeBuildPolicy<Solution_> buildPolicy, int nodeOrder,
            BavetAbstractBiNode<A, B> parentNode, BavetAbstractBiNode<A, B> node) {
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

    protected void createChildNodeChains(BavetNodeBuildPolicy<Solution_> buildPolicy, Score<?> constraintWeight,
            int nodeOrder, BavetAbstractBiNode<A, B> node) {
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
