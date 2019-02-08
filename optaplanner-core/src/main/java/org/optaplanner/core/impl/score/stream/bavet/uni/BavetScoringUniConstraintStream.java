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
import java.util.List;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.inliner.BigDecimalWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.IntWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.LongWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.ScoreInliner;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraint;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetNodeBuildPolicy;

public final class BavetScoringUniConstraintStream<Solution_, A> extends BavetAbstractUniConstraintStream<Solution_, A> {

    private final boolean positive;
    private final ToIntFunction<A> intMatchWeigher;
    private final ToLongFunction<A> longMatchWeigher;
    private final Function<A, BigDecimal> bigDecimalMatchWeigher;

    public BavetScoringUniConstraintStream(BavetConstraint<Solution_> bavetConstraint, boolean positive, ToIntFunction<A> intMatchWeigher) {
        this(bavetConstraint, positive, intMatchWeigher, null, null);
        if (intMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    public BavetScoringUniConstraintStream(BavetConstraint<Solution_> bavetConstraint, boolean positive, ToLongFunction<A> longMatchWeigher) {
        this(bavetConstraint, positive, null, longMatchWeigher, null);
        if (longMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    public BavetScoringUniConstraintStream(BavetConstraint<Solution_> bavetConstraint, boolean positive, Function<A, BigDecimal> bigDecimalMatchWeigher) {
        this(bavetConstraint, positive, null, null, bigDecimalMatchWeigher);
        if (bigDecimalMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    private BavetScoringUniConstraintStream(BavetConstraint<Solution_> bavetConstraint, boolean positive,
            ToIntFunction<A> intMatchWeigher, ToLongFunction<A> longMatchWeigher, Function<A, BigDecimal> bigDecimalMatchWeigher) {
        super(bavetConstraint);
        this.positive = positive;
        this.intMatchWeigher = intMatchWeigher;
        this.longMatchWeigher = longMatchWeigher;
        this.bigDecimalMatchWeigher = bigDecimalMatchWeigher;
    }

    @Override
    protected BavetScoringUniNode<A> createNode(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder, List<BavetAbstractUniNode<A>> childNodeList) {
        if (!childNodeList.isEmpty()) {
            throw new IllegalStateException("Impossible state: the stream (" + this
                    + ") has an non-empty childNodeList (" + childNodeList + ") but it's an endpoint.");
        }
        if (!positive) {
            constraintWeight = constraintWeight.negate();
        }
        ScoreInliner scoreInliner = buildPolicy.getSession().getScoreInliner();
        Function<A, UndoScoreImpacter> scoreImpacter;
        if (intMatchWeigher != null) {
            IntWeightedScoreImpacter weightedScoreImpacter = scoreInliner.buildIntWeightedScoreImpacter(constraintWeight);
            scoreImpacter = (A a) -> {
                int matchWeight = intMatchWeigher.applyAsInt(a);
                return weightedScoreImpacter.impactScore(matchWeight);
            };
        } else if (longMatchWeigher != null) {
            LongWeightedScoreImpacter weightedScoreImpacter = scoreInliner.buildLongWeightedScoreImpacter(constraintWeight);
            scoreImpacter = (A a) -> {
                long matchWeight = longMatchWeigher.applyAsLong(a);
                return weightedScoreImpacter.impactScore(matchWeight);
            };
        } else if (bigDecimalMatchWeigher != null) {
            BigDecimalWeightedScoreImpacter weightedScoreImpacter = scoreInliner.buildBigDecimalWeightedScoreImpacter(constraintWeight);
            scoreImpacter = (A a) -> {
                BigDecimal matchWeight = bigDecimalMatchWeigher.apply(a);
                return weightedScoreImpacter.impactScore(matchWeight);
            };
        } else {
            throw new IllegalStateException("Impossible situation: all the matchWeighers are null.");
        }
        return new BavetScoringUniNode<>(buildPolicy.getSession(), nodeOrder,
                constraint.getConstraintPackage(), constraint.getConstraintName(),
                constraintWeight, scoreImpacter);
    }

    @Override
    public String toString() {
        return "Scoring()";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
