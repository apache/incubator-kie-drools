/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.bavet.bi;

import static java.util.Arrays.asList;

import java.math.BigDecimal;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.ToIntBiFunction;
import java.util.function.ToLongBiFunction;

import org.optaplanner.constraint.streams.bavet.BavetConstraint;
import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.BavetScoringConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.NodeBuildHelper;
import org.optaplanner.constraint.streams.common.inliner.AbstractScoreInliner;
import org.optaplanner.constraint.streams.common.inliner.UndoScoreImpacter;
import org.optaplanner.constraint.streams.common.inliner.WeightedScoreImpacter;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;

public final class BavetScoringBiConstraintStream<Solution_, A, B>
        extends BavetAbstractBiConstraintStream<Solution_, A, B>
        implements BavetScoringConstraintStream<Solution_> {

    private final BavetAbstractBiConstraintStream<Solution_, A, B> parent;
    private final boolean noMatchWeigher;
    private final ToIntBiFunction<A, B> intMatchWeigher;
    private final ToLongBiFunction<A, B> longMatchWeigher;
    private final BiFunction<A, B, BigDecimal> bigDecimalMatchWeigher;
    private BavetConstraint<Solution_> constraint;

    public BavetScoringBiConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractBiConstraintStream<Solution_, A, B> parent) {
        this(constraintFactory, parent, true, null, null, null);
    }

    public BavetScoringBiConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractBiConstraintStream<Solution_, A, B> parent,
            ToIntBiFunction<A, B> intMatchWeigher) {
        this(constraintFactory, parent, false, intMatchWeigher, null, null);
        if (intMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    public BavetScoringBiConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractBiConstraintStream<Solution_, A, B> parent,
            ToLongBiFunction<A, B> longMatchWeigher) {
        this(constraintFactory, parent, false, null, longMatchWeigher, null);
        if (longMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    public BavetScoringBiConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractBiConstraintStream<Solution_, A, B> parent,
            BiFunction<A, B, BigDecimal> bigDecimalMatchWeigher) {
        this(constraintFactory, parent, false, null, null, bigDecimalMatchWeigher);
        if (bigDecimalMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    private BavetScoringBiConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractBiConstraintStream<Solution_, A, B> parent,
            boolean noMatchWeigher,
            ToIntBiFunction<A, B> intMatchWeigher, ToLongBiFunction<A, B> longMatchWeigher,
            BiFunction<A, B, BigDecimal> bigDecimalMatchWeigher) {
        super(constraintFactory, parent.getRetrievalSemantics());
        this.parent = parent;
        this.noMatchWeigher = noMatchWeigher;
        this.intMatchWeigher = intMatchWeigher;
        this.longMatchWeigher = longMatchWeigher;
        this.bigDecimalMatchWeigher = bigDecimalMatchWeigher;
    }

    @Override
    public void setConstraint(BavetConstraint<Solution_> constraint) {
        this.constraint = constraint;
    }

    @Override
    public boolean guaranteesDistinct() {
        return parent.guaranteesDistinct();
    }

    // ************************************************************************
    // Node creation
    // ************************************************************************

    @Override
    public void collectActiveConstraintStreams(Set<BavetAbstractConstraintStream<Solution_>> constraintStreamSet) {
        parent.collectActiveConstraintStreams(constraintStreamSet);
        constraintStreamSet.add(this);
    }

    @Override
    public ConstraintStream getTupleSource() {
        return parent.getTupleSource();
    }

    @Override
    public <Score_ extends Score<Score_>> void buildNode(NodeBuildHelper<Score_> buildHelper) {
        Score_ constraintWeight = buildHelper.getConstraintWeight(constraint);
        AbstractScoreInliner<Score_> scoreInliner = buildHelper.getScoreInliner();
        WeightedScoreImpacter weightedScoreImpacter = scoreInliner.buildWeightedScoreImpacter(constraint, constraintWeight);
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
        BiScorer<A, B> scorer = new BiScorer<>(constraint.getConstraintPackage(), constraint.getConstraintName(),
                constraintWeight, scoreImpacter, buildHelper.reserveTupleStoreIndex(parent.getTupleSource()));
        buildHelper.putInsertRetract(this, scorer::insert, scorer::retract);
    }

    // ************************************************************************
    // Equality for node sharing
    // ************************************************************************

    // No node sharing

    @Override
    public String toString() {
        return "Scoring(" + constraint.getConstraintName() + ")";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
