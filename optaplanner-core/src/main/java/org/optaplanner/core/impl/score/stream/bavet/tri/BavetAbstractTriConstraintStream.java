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

package org.optaplanner.core.impl.score.stream.bavet.tri;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.api.function.ToIntTriFunction;
import org.optaplanner.core.api.function.ToLongTriFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraint;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintFactory;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetNodeBuildPolicy;
import org.optaplanner.core.impl.score.stream.tri.InnerTriConstraintStream;

public abstract class BavetAbstractTriConstraintStream<Solution_, A, B, C> extends BavetAbstractConstraintStream<Solution_>
        implements InnerTriConstraintStream<A, B, C> {

    protected final List<BavetAbstractTriConstraintStream<Solution_, A, B, C>> childStreamList = new ArrayList<>(2);

    public BavetAbstractTriConstraintStream(BavetConstraintFactory<Solution_> constraintFactory) {
        super(constraintFactory);
    }

    // ************************************************************************
    // Stream builder methods
    // ************************************************************************

    protected void addChildStream(BavetAbstractTriConstraintStream<Solution_, A, B, C> childStream) {
        childStreamList.add(childStream);
    }

    // ************************************************************************
    // Filter
    // ************************************************************************

    @Override
    public BavetAbstractTriConstraintStream<Solution_, A, B, C> filter(TriPredicate<A, B, C> predicate) {
        BavetFilterTriConstraintStream<Solution_, A, B, C> stream = new BavetFilterTriConstraintStream<>(constraintFactory, this, predicate);
        addChildStream(stream);
        return stream;
    }

    // ************************************************************************
    // Penalize/reward
    // ************************************************************************

    @Override
    public final Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            boolean positive) {
        BavetConstraint<Solution_> constraint = buildConstraint(constraintPackage, constraintName, constraintWeight,
                positive);
        BavetScoringTriConstraintStream<Solution_, A, B, C> stream =
                new BavetScoringTriConstraintStream<>(constraintFactory, this, constraint);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public final Constraint impactScore(String constraintPackage, String constraintName, Score<?> constraintWeight,
            ToIntTriFunction<A, B, C> matchWeigher, boolean positive) {
        BavetConstraint<Solution_> constraint = buildConstraint(constraintPackage, constraintName, constraintWeight,
                positive);
        BavetScoringTriConstraintStream<Solution_, A, B, C> stream =
                new BavetScoringTriConstraintStream<>(constraintFactory, this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public final Constraint impactScoreLong(String constraintPackage, String constraintName,
            Score<?> constraintWeight, ToLongTriFunction<A, B, C> matchWeigher, boolean positive) {
        BavetConstraint<Solution_> constraint = buildConstraint(constraintPackage, constraintName, constraintWeight,
                positive);
        BavetScoringTriConstraintStream<Solution_, A, B, C> stream =
                new BavetScoringTriConstraintStream<>(constraintFactory, this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public final Constraint impactScoreBigDecimal(String constraintPackage, String constraintName,
            Score<?> constraintWeight, TriFunction<A, B, C, BigDecimal> matchWeigher, boolean positive) {
        BavetConstraint<Solution_> constraint = buildConstraint(constraintPackage, constraintName, constraintWeight,
                positive);
        BavetScoringTriConstraintStream<Solution_, A, B, C> stream =
                new BavetScoringTriConstraintStream<>(constraintFactory, this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public final Constraint impactScoreConfigurable(String constraintPackage, String constraintName, boolean positive) {
        BavetConstraint<Solution_> constraint = buildConstraintConfigurable(constraintPackage, constraintName,
                positive);
        BavetScoringTriConstraintStream<Solution_, A, B, C> stream =
                new BavetScoringTriConstraintStream<>(constraintFactory, this, constraint);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public final Constraint impactScoreConfigurable(String constraintPackage, String constraintName,
            ToIntTriFunction<A, B, C> matchWeigher, boolean positive) {
        BavetConstraint<Solution_> constraint = buildConstraintConfigurable(constraintPackage, constraintName,
                positive);
        BavetScoringTriConstraintStream<Solution_, A, B, C> stream =
                new BavetScoringTriConstraintStream<>(constraintFactory, this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public final Constraint impactScoreConfigurableLong(String constraintPackage, String constraintName,
            ToLongTriFunction<A, B, C> matchWeigher, boolean positive) {
        BavetConstraint<Solution_> constraint = buildConstraintConfigurable(constraintPackage, constraintName,
                positive);
        BavetScoringTriConstraintStream<Solution_, A, B, C> stream =
                new BavetScoringTriConstraintStream<>(constraintFactory, this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    @Override
    public final Constraint impactScoreConfigurableBigDecimal(String constraintPackage, String constraintName,
            TriFunction<A, B, C, BigDecimal> matchWeigher, boolean positive) {
        BavetConstraint<Solution_> constraint = buildConstraintConfigurable(constraintPackage, constraintName,
                positive);
        BavetScoringTriConstraintStream<Solution_, A, B, C> stream =
                new BavetScoringTriConstraintStream<>(constraintFactory, this, constraint, matchWeigher);
        childStreamList.add(stream);
        return constraint;
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    public BavetAbstractTriNode<A, B, C> createNodeChain(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder, BavetAbstractTriNode<A, B, C> parentNode) {
        BavetAbstractTriNode<A, B, C> node = createNode(buildPolicy, constraintWeight, nodeOrder, parentNode);
        node = processNode(buildPolicy, nodeOrder, parentNode, node);
        createChildNodeChains(buildPolicy, constraintWeight, nodeOrder, node);
        return node;
    }

    protected BavetAbstractTriNode<A, B, C> processNode(BavetNodeBuildPolicy<Solution_> buildPolicy, int nodeOrder, BavetAbstractTriNode<A, B, C> parentNode, BavetAbstractTriNode<A, B, C> node) {
        buildPolicy.updateNodeOrderMaximum(nodeOrder);
        BavetAbstractTriNode<A, B, C> sharedNode = buildPolicy.retrieveSharedNode(node);
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

    protected void createChildNodeChains(BavetNodeBuildPolicy<Solution_> buildPolicy, Score<?> constraintWeight, int nodeOrder, BavetAbstractTriNode<A, B, C> node) {
        if (childStreamList.isEmpty()) {
            throw new IllegalStateException("The stream (" + this + ") leads to nowhere.\n"
                    + "Maybe don't create it.");
        }
        for (BavetAbstractTriConstraintStream<Solution_, A, B, C> childStream : childStreamList) {
            childStream.createNodeChain(buildPolicy, constraintWeight, nodeOrder + 1, node);
        }
    }

    protected abstract BavetAbstractTriNode<A, B, C> createNode(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder, BavetAbstractTriNode<A, B, C> parentNode);

}
