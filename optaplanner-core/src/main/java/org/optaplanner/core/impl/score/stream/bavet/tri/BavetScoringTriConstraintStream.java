/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import static java.util.Arrays.asList;

import java.math.BigDecimal;
import java.util.List;

import org.optaplanner.core.api.function.ToIntTriFunction;
import org.optaplanner.core.api.function.ToLongTriFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.inliner.ScoreInliner;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;
import org.optaplanner.core.impl.score.inliner.WeightedScoreImpacter;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraint;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintFactory;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetNodeBuildPolicy;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetFromUniConstraintStream;

public final class BavetScoringTriConstraintStream<Solution_, A, B, C>
        extends BavetAbstractTriConstraintStream<Solution_, A, B, C> {

    private final BavetAbstractTriConstraintStream<Solution_, A, B, C> parent;
    private final BavetConstraint<Solution_> constraint;
    private final boolean noMatchWeigher;
    private final ToIntTriFunction<A, B, C> intMatchWeigher;
    private final ToLongTriFunction<A, B, C> longMatchWeigher;
    private final TriFunction<A, B, C, BigDecimal> bigDecimalMatchWeigher;

    public BavetScoringTriConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractTriConstraintStream<Solution_, A, B, C> parent,
            BavetConstraint<Solution_> constraint) {
        this(constraintFactory, parent, constraint, true, null, null, null);
    }

    public BavetScoringTriConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractTriConstraintStream<Solution_, A, B, C> parent,
            BavetConstraint<Solution_> constraint, ToIntTriFunction<A, B, C> intMatchWeigher) {
        this(constraintFactory, parent, constraint, false, intMatchWeigher, null, null);
        if (intMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    public BavetScoringTriConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractTriConstraintStream<Solution_, A, B, C> parent,
            BavetConstraint<Solution_> constraint, ToLongTriFunction<A, B, C> longMatchWeigher) {
        this(constraintFactory, parent, constraint, false, null, longMatchWeigher, null);
        if (longMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    public BavetScoringTriConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractTriConstraintStream<Solution_, A, B, C> parent,
            BavetConstraint<Solution_> constraint, TriFunction<A, B, C, BigDecimal> bigDecimalMatchWeigher) {
        this(constraintFactory, parent, constraint, false, null, null, bigDecimalMatchWeigher);
        if (bigDecimalMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    private BavetScoringTriConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractTriConstraintStream<Solution_, A, B, C> parent,
            BavetConstraint<Solution_> constraint, boolean noMatchWeigher,
            ToIntTriFunction<A, B, C> intMatchWeigher, ToLongTriFunction<A, B, C> longMatchWeigher,
            TriFunction<A, B, C, BigDecimal> bigDecimalMatchWeigher) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.parent = parent;
        this.constraint = constraint;
        this.noMatchWeigher = noMatchWeigher;
        this.intMatchWeigher = intMatchWeigher;
        this.longMatchWeigher = longMatchWeigher;
        this.bigDecimalMatchWeigher = bigDecimalMatchWeigher;
    }

    @Override
    public boolean guaranteesDistinct() {
        return parent.guaranteesDistinct();
    }

    @Override
    public List<BavetFromUniConstraintStream<Solution_, Object>> getFromStreamList() {
        return parent.getFromStreamList();
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    protected BavetScoringTriNode<A, B, C> createNode(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, BavetAbstractTriNode<A, B, C> parentNode) {
        ScoreInliner<?> scoreInliner = buildPolicy.getSession().getScoreInliner();
        WeightedScoreImpacter weightedScoreImpacter = scoreInliner.buildWeightedScoreImpacter(constraint);
        TriFunction<A, B, C, UndoScoreImpacter> scoreImpacter;
        if (intMatchWeigher != null) {
            scoreImpacter = (a, b, c) -> {
                int matchWeight = intMatchWeigher.applyAsInt(a, b, c);
                constraint.assertCorrectImpact(matchWeight);
                return weightedScoreImpacter.impactScore(matchWeight, () -> asList(a, b, c));
            };
        } else if (longMatchWeigher != null) {
            scoreImpacter = (a, b, c) -> {
                long matchWeight = longMatchWeigher.applyAsLong(a, b, c);
                constraint.assertCorrectImpact(matchWeight);
                return weightedScoreImpacter.impactScore(matchWeight, () -> asList(a, b, c));
            };
        } else if (bigDecimalMatchWeigher != null) {
            scoreImpacter = (a, b, c) -> {
                BigDecimal matchWeight = bigDecimalMatchWeigher.apply(a, b, c);
                constraint.assertCorrectImpact(matchWeight);
                return weightedScoreImpacter.impactScore(matchWeight, () -> asList(a, b, c));
            };
        } else if (noMatchWeigher) {
            scoreImpacter = (a, b, c) -> weightedScoreImpacter.impactScore(1, () -> asList(a, b, c));
        } else {
            throw new IllegalStateException("Impossible state: neither of the supported match weighers provided.");
        }
        return new BavetScoringTriNode<>(buildPolicy.getSession(), buildPolicy.nextNodeIndex(), constraintWeight,
                scoreImpacter);
    }

    @Override
    protected void createChildNodeChains(BavetNodeBuildPolicy<Solution_> buildPolicy, Score<?> constraintWeight,
            BavetAbstractTriNode<A, B, C> node) {
        if (!childStreamList.isEmpty()) {
            throw new IllegalStateException("Impossible state: the stream (" + this
                    + ") has an non-empty childStreamList (" + childStreamList + ") but it's an endpoint.");
        }
    }

    @Override
    public String toString() {
        return "Scoring()";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
