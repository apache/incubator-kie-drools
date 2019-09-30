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
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;
import org.optaplanner.core.impl.score.inliner.BigDecimalWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.IntWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.LongWeightedScoreImpacter;
import org.optaplanner.core.impl.score.inliner.ScoreInliner;
import org.optaplanner.core.impl.score.inliner.UndoScoreImpacter;
import org.optaplanner.core.impl.score.inliner.WeightedScoreImpacter;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraint;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintFactory;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetNodeBuildPolicy;

public final class BavetScoringUniConstraintStream<Solution_, A> extends BavetAbstractUniConstraintStream<Solution_, A> {

    private final BavetAbstractUniConstraintStream<Solution_, A> parent;
    private final BavetConstraint<Solution_> constraint;
    private final boolean noMatchWeigher;
    private final ToIntFunction<A> intMatchWeigher;
    private final ToLongFunction<A> longMatchWeigher;
    private final Function<A, BigDecimal> bigDecimalMatchWeigher;

    public BavetScoringUniConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractUniConstraintStream<Solution_, A> parent,
            BavetConstraint<Solution_> constraint) {
        this(constraintFactory, parent, constraint, true, null, null, null);
    }

    public BavetScoringUniConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractUniConstraintStream<Solution_, A> parent,
            BavetConstraint<Solution_> constraint, ToIntFunction<A> intMatchWeigher) {
        this(constraintFactory, parent, constraint, false, intMatchWeigher, null, null);
        if (intMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    public BavetScoringUniConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractUniConstraintStream<Solution_, A> parent,
            BavetConstraint<Solution_> constraint, ToLongFunction<A> longMatchWeigher) {
        this(constraintFactory, parent, constraint, false, null, longMatchWeigher, null);
        if (longMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    public BavetScoringUniConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractUniConstraintStream<Solution_, A> parent,
            BavetConstraint<Solution_> constraint, Function<A, BigDecimal> bigDecimalMatchWeigher) {
        this(constraintFactory, parent, constraint, false, null, null, bigDecimalMatchWeigher);
        if (bigDecimalMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    private BavetScoringUniConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractUniConstraintStream<Solution_, A> parent,
            BavetConstraint<Solution_> constraint, boolean noMatchWeigher,
            ToIntFunction<A> intMatchWeigher, ToLongFunction<A> longMatchWeigher, Function<A, BigDecimal> bigDecimalMatchWeigher) {
        super(constraintFactory);
        this.parent = parent;
        this.constraint = constraint;
        this.noMatchWeigher = noMatchWeigher;
        this.intMatchWeigher = intMatchWeigher;
        this.longMatchWeigher = longMatchWeigher;
        this.bigDecimalMatchWeigher = bigDecimalMatchWeigher;
    }

    @Override
    public List<BavetFromUniConstraintStream<Solution_, Object>> getFromStreamList() {
        return parent.getFromStreamList();
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    protected BavetScoringUniNode<A> createNode(BavetNodeBuildPolicy<Solution_> buildPolicy,
            Score<?> constraintWeight, int nodeOrder, BavetAbstractUniNode<A> parentNode) {
        ScoreInliner scoreInliner = buildPolicy.getSession().getScoreInliner();
        WeightedScoreImpacter weightedScoreImpacter = scoreInliner.buildWeightedScoreImpacter(constraintWeight);
        BiFunction<A, Consumer<Score<?>>, UndoScoreImpacter> scoreImpacter;
        if (weightedScoreImpacter instanceof IntWeightedScoreImpacter) {
            IntWeightedScoreImpacter castedWeightedScoreImpacter = (IntWeightedScoreImpacter) weightedScoreImpacter;
            if (intMatchWeigher != null) {
                scoreImpacter = (A a, Consumer<Score<?>> matchScoreConsumer) -> {
                    int matchWeight = intMatchWeigher.applyAsInt(a);
                    return castedWeightedScoreImpacter.impactScore(matchWeight, matchScoreConsumer);
                };
            } else if (noMatchWeigher) {
                scoreImpacter = (A a, Consumer<Score<?>> matchScoreConsumer) ->
                        castedWeightedScoreImpacter.impactScore(1, matchScoreConsumer);
            } else {
                throw new IllegalStateException("The matchWeigher of " + UniConstraintStream.class.getSimpleName()
                        + ".penalize(matchWeigher) of the constraint (" + constraint.getConstraintId()
                        + ") must return an int.");
            }
        } else if (weightedScoreImpacter instanceof LongWeightedScoreImpacter) {
            LongWeightedScoreImpacter castedWeightedScoreImpacter = (LongWeightedScoreImpacter) weightedScoreImpacter;
            if (longMatchWeigher != null) {
                scoreImpacter = (A a, Consumer<Score<?>> matchScoreConsumer) -> {
                    long matchWeight = longMatchWeigher.applyAsLong(a);
                    return castedWeightedScoreImpacter.impactScore(matchWeight, matchScoreConsumer);
                };
            } else if (noMatchWeigher) {
                scoreImpacter = (A a, Consumer<Score<?>> matchScoreConsumer) ->
                        castedWeightedScoreImpacter.impactScore(1L, matchScoreConsumer);
            } else {
                throw new IllegalStateException("The matchWeigher of " + UniConstraintStream.class.getSimpleName()
                        + ".penalize(matchWeigher) of the constraint (" + constraint.getConstraintId()
                        + ") must return a long.");
            }
        } else if (weightedScoreImpacter instanceof BigDecimalWeightedScoreImpacter) {
            BigDecimalWeightedScoreImpacter castedWeightedScoreImpacter = (BigDecimalWeightedScoreImpacter) weightedScoreImpacter;
            if (bigDecimalMatchWeigher != null) {
                scoreImpacter = (A a, Consumer<Score<?>> matchScoreConsumer) -> {
                    BigDecimal matchWeight = bigDecimalMatchWeigher.apply(a);
                    return castedWeightedScoreImpacter.impactScore(matchWeight, matchScoreConsumer);
                };
            } else if (noMatchWeigher) {
                scoreImpacter = (A a, Consumer<Score<?>> matchScoreConsumer) ->
                        castedWeightedScoreImpacter.impactScore(BigDecimal.ONE, matchScoreConsumer);
            } else {
                throw new IllegalStateException("The matchWeigher of " + UniConstraintStream.class.getSimpleName()
                        + ".penalize(matchWeigher) of the constraint (" + constraint.getConstraintId()
                        + ") must return a " + BigDecimal.class.getSimpleName() + ".");
            }
        } else {
            throw new IllegalStateException("Unsupported weightedScoreImpacter (" + weightedScoreImpacter + ").");
        }
        BavetScoringUniNode<A> node = new BavetScoringUniNode<>(buildPolicy.getSession(), nodeOrder, parentNode,
                constraint.getConstraintPackage(), constraint.getConstraintName(),
                constraintWeight, scoreImpacter);
        buildPolicy.addScoringNode(node);
        return node;
    }

    @Override
    protected void createChildNodeChains(BavetNodeBuildPolicy<Solution_> buildPolicy, Score<?> constraintWeight, int nodeOrder, BavetAbstractUniNode<A> node) {
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
