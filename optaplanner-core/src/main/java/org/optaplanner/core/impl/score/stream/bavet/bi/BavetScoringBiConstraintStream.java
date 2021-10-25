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

package org.optaplanner.core.impl.score.stream.bavet.bi;

import static java.util.Arrays.asList;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.inliner.ScoreInliner;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;
import org.optaplanner.core.impl.score.inliner.WeightedScoreImpacter;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraint;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintFactory;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetNodeBuildPolicy;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetFromUniConstraintStream;

public final class BavetScoringBiConstraintStream<Solution_, A, B>
        extends BavetAbstractBiConstraintStream<Solution_, A, B> {

    private final BavetAbstractBiConstraintStream<Solution_, A, B> parent;
    private final BavetConstraint<Solution_> constraint;
    private final boolean noMatchWeigher;
    private final ToIntBiFunction<A, B> intMatchWeigher;
    private final ToLongBiFunction<A, B> longMatchWeigher;
    private final BiFunction<A, B, BigDecimal> bigDecimalMatchWeigher;

    public BavetScoringBiConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractBiConstraintStream<Solution_, A, B> parent,
            BavetConstraint<Solution_> constraint) {
        this(constraintFactory, parent, constraint, true, null, null, null);
    }

    public BavetScoringBiConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractBiConstraintStream<Solution_, A, B> parent,
            BavetConstraint<Solution_> constraint, ToIntBiFunction<A, B> intMatchWeigher) {
        this(constraintFactory, parent, constraint, false, intMatchWeigher, null, null);
        if (intMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    public BavetScoringBiConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractBiConstraintStream<Solution_, A, B> parent,
            BavetConstraint<Solution_> constraint, ToLongBiFunction<A, B> longMatchWeigher) {
        this(constraintFactory, parent, constraint, false, null, longMatchWeigher, null);
        if (longMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    public BavetScoringBiConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractBiConstraintStream<Solution_, A, B> parent,
            BavetConstraint<Solution_> constraint, BiFunction<A, B, BigDecimal> bigDecimalMatchWeigher) {
        this(constraintFactory, parent, constraint, false, null, null, bigDecimalMatchWeigher);
        if (bigDecimalMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    private BavetScoringBiConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractBiConstraintStream<Solution_, A, B> parent,
            BavetConstraint<Solution_> constraint, boolean noMatchWeigher,
            ToIntBiFunction<A, B> intMatchWeigher, ToLongBiFunction<A, B> longMatchWeigher,
            BiFunction<A, B, BigDecimal> bigDecimalMatchWeigher) {
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
    protected BavetScoringBiNode<A, B> createNode(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, BavetAbstractBiNode<A, B> parentNode) {
        ScoreInliner<?> scoreInliner = buildPolicy.getSession().getScoreInliner();
        WeightedScoreImpacter weightedScoreImpacter = scoreInliner.buildWeightedScoreImpacter(constraint);
        BiFunction<A, B, UndoScoreImpacter> scoreImpacter;
        if (intMatchWeigher != null) {
            scoreImpacter = (a, b) -> {
                int matchWeight = intMatchWeigher.applyAsInt(a, b);
                constraint.assertCorrectImpact(matchWeight);
                return weightedScoreImpacter.impactScore(matchWeight, () -> asList(a, b));
            };
        } else if (longMatchWeigher != null) {
            scoreImpacter = (a, b) -> {
                long matchWeight = longMatchWeigher.applyAsLong(a, b);
                constraint.assertCorrectImpact(matchWeight);
                return weightedScoreImpacter.impactScore(matchWeight, () -> asList(a, b));
            };
        } else if (bigDecimalMatchWeigher != null) {
            scoreImpacter = (a, b) -> {
                BigDecimal matchWeight = bigDecimalMatchWeigher.apply(a, b);
                constraint.assertCorrectImpact(matchWeight);
                return weightedScoreImpacter.impactScore(matchWeight, () -> asList(a, b));
            };
        } else if (noMatchWeigher) {
            scoreImpacter = (a, b) -> weightedScoreImpacter.impactScore(1, () -> asList(a, b));
        } else {
            throw new IllegalStateException("Impossible state: neither of the supported match weighers provided.");
        }
        return new BavetScoringBiNode<>(buildPolicy.getSession(), buildPolicy.nextNodeIndex(), constraintWeight,
                scoreImpacter);
    }

    @Override
    protected void createChildNodeChains(BavetNodeBuildPolicy<Solution_> buildPolicy, Score<?> constraintWeight,
            BavetAbstractBiNode<A, B> node) {
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
