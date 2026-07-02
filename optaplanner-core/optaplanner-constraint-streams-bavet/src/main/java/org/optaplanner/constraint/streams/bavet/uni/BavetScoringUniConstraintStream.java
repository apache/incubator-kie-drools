/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.constraint.streams.bavet.uni;

import static org.optaplanner.constraint.streams.common.inliner.JustificationsSupplier.of;

import java.math.BigDecimal;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import org.optaplanner.constraint.streams.bavet.BavetConstraint;
import org.optaplanner.constraint.streams.bavet.BavetConstraintFactory;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.BavetScoringConstraintStream;
import org.optaplanner.constraint.streams.bavet.common.NodeBuildHelper;
import org.optaplanner.constraint.streams.common.inliner.JustificationsSupplier;
import org.optaplanner.constraint.streams.common.inliner.UndoScoreImpacter;
import org.optaplanner.constraint.streams.common.inliner.WeightedScoreImpacter;
import org.optaplanner.core.api.score.Score;

public final class BavetScoringUniConstraintStream<Solution_, A>
        extends BavetAbstractUniConstraintStream<Solution_, A>
        implements BavetScoringConstraintStream<Solution_> {

    private final BavetAbstractUniConstraintStream<Solution_, A> parent;
    private final boolean noMatchWeigher;
    private final ToIntFunction<A> intMatchWeigher;
    private final ToLongFunction<A> longMatchWeigher;
    private final Function<A, BigDecimal> bigDecimalMatchWeigher;
    private BavetConstraint<Solution_> constraint;

    public BavetScoringUniConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractUniConstraintStream<Solution_, A> parent,
            ToIntFunction<A> intMatchWeigher) {
        this(constraintFactory, parent, false, intMatchWeigher, null, null);
        if (intMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    public BavetScoringUniConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractUniConstraintStream<Solution_, A> parent,
            ToLongFunction<A> longMatchWeigher) {
        this(constraintFactory, parent, false, null, longMatchWeigher, null);
        if (longMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    public BavetScoringUniConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractUniConstraintStream<Solution_, A> parent,
            Function<A, BigDecimal> bigDecimalMatchWeigher) {
        this(constraintFactory, parent, false, null, null, bigDecimalMatchWeigher);
        if (bigDecimalMatchWeigher == null) {
            throw new IllegalArgumentException("The matchWeigher (null) cannot be null.");
        }
    }

    private BavetScoringUniConstraintStream(BavetConstraintFactory<Solution_> constraintFactory,
            BavetAbstractUniConstraintStream<Solution_, A> parent,
            boolean noMatchWeigher, ToIntFunction<A> intMatchWeigher, ToLongFunction<A> longMatchWeigher,
            Function<A, BigDecimal> bigDecimalMatchWeigher) {
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
    public BavetAbstractConstraintStream<Solution_> getTupleSource() {
        return parent.getTupleSource();
    }

    @Override
    public <Score_ extends Score<Score_>> void buildNode(NodeBuildHelper<Score_> buildHelper) {
        if (!childStreamList.isEmpty()) {
            throw new IllegalStateException("Impossible state: the stream (" + this
                    + ") has an non-empty childStreamList (" + childStreamList + ") but it's an endpoint.");
        }
        Score_ constraintWeight = buildHelper.getConstraintWeight(constraint);
        WeightedScoreImpacter<Score_, ?> weightedScoreImpacter =
                buildHelper.getScoreInliner().buildWeightedScoreImpacter(constraint, constraintWeight);
        boolean constraintMatchEnabled = buildHelper.getScoreInliner().isConstraintMatchEnabled();
        Function<A, UndoScoreImpacter> scoreImpacter;
        if (intMatchWeigher != null) {
            if (constraintMatchEnabled) {
                scoreImpacter = a -> {
                    int matchWeight = intMatchWeigher.applyAsInt(a);
                    constraint.assertCorrectImpact(matchWeight);
                    JustificationsSupplier justificationsSupplier =
                            of(constraint, constraint.getJustificationMapping(), constraint.getIndictedObjectsMapping(), a);
                    return weightedScoreImpacter.impactScore(matchWeight, justificationsSupplier);
                };
            } else {
                scoreImpacter = a -> {
                    int matchWeight = intMatchWeigher.applyAsInt(a);
                    constraint.assertCorrectImpact(matchWeight);
                    return weightedScoreImpacter.impactScore(matchWeight, null);
                };
            }
        } else if (longMatchWeigher != null) {
            if (constraintMatchEnabled) {
                scoreImpacter = a -> {
                    long matchWeight = longMatchWeigher.applyAsLong(a);
                    constraint.assertCorrectImpact(matchWeight);
                    JustificationsSupplier justificationsSupplier =
                            of(constraint, constraint.getJustificationMapping(), constraint.getIndictedObjectsMapping(), a);
                    return weightedScoreImpacter.impactScore(matchWeight, justificationsSupplier);
                };
            } else {
                scoreImpacter = a -> {
                    long matchWeight = longMatchWeigher.applyAsLong(a);
                    constraint.assertCorrectImpact(matchWeight);
                    return weightedScoreImpacter.impactScore(matchWeight, null);
                };
            }
        } else if (bigDecimalMatchWeigher != null) {
            if (constraintMatchEnabled) {
                scoreImpacter = a -> {
                    BigDecimal matchWeight = bigDecimalMatchWeigher.apply(a);
                    constraint.assertCorrectImpact(matchWeight);
                    JustificationsSupplier justificationsSupplier =
                            of(constraint, constraint.getJustificationMapping(), constraint.getIndictedObjectsMapping(), a);
                    return weightedScoreImpacter.impactScore(matchWeight, justificationsSupplier);
                };
            } else {
                scoreImpacter = a -> {
                    BigDecimal matchWeight = bigDecimalMatchWeigher.apply(a);
                    constraint.assertCorrectImpact(matchWeight);
                    return weightedScoreImpacter.impactScore(matchWeight, null);
                };
            }
        } else if (noMatchWeigher) {
            if (constraintMatchEnabled) {
                scoreImpacter = a -> {
                    JustificationsSupplier justificationsSupplier =
                            of(constraint, constraint.getJustificationMapping(), constraint.getIndictedObjectsMapping(), a);
                    return weightedScoreImpacter.impactScore(1, justificationsSupplier);
                };
            } else {
                scoreImpacter = a -> weightedScoreImpacter.impactScore(1, null);
            }
        } else {
            throw new IllegalStateException("Impossible state: neither of the supported match weighers provided.");
        }
        UniScorer<A> scorer = new UniScorer<>(constraint.getConstraintPackage(), constraint.getConstraintName(),
                constraintWeight, scoreImpacter, buildHelper.reserveTupleStoreIndex(parent.getTupleSource()));
        buildHelper.putInsertUpdateRetract(this, scorer);
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
