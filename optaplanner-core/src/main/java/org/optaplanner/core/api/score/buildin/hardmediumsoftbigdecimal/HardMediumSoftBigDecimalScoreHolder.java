/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;

/**
 * @see HardMediumSoftBigDecimalScore
 */
public class HardMediumSoftBigDecimalScoreHolder extends AbstractScoreHolder<HardMediumSoftBigDecimalScore> {

    protected final Map<Rule, BiConsumer<RuleContext, BigDecimal>> matchExecutorByNumberMap = new LinkedHashMap<>();
    /** Slower than {@link #matchExecutorByNumberMap} */
    protected final Map<Rule, BiConsumer<RuleContext, HardMediumSoftBigDecimalScore>> matchExecutorByScoreMap = new LinkedHashMap<>();

    protected BigDecimal hardScore = BigDecimal.ZERO;
    protected BigDecimal mediumScore = BigDecimal.ZERO;
    protected BigDecimal softScore = BigDecimal.ZERO;

    public HardMediumSoftBigDecimalScoreHolder(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled, HardMediumSoftBigDecimalScore.ZERO);
    }

    public BigDecimal getHardScore() {
        return hardScore;
    }

    public BigDecimal getMediumScore() {
        return mediumScore;
    }

    public BigDecimal getSoftScore() {
        return softScore;
    }

    // ************************************************************************
    // Setup methods
    // ************************************************************************

    @Override
    public void configureConstraintWeight(Rule rule, HardMediumSoftBigDecimalScore constraintWeight) {
        super.configureConstraintWeight(rule, constraintWeight);
        BiConsumer<RuleContext, BigDecimal> matchExecutor;
        if (constraintWeight.equals(HardMediumSoftBigDecimalScore.ZERO)) {
            matchExecutor = (RuleContext kcontext, BigDecimal matchWeight) -> {};
        } else if (constraintWeight.getMediumScore().equals(BigDecimal.ZERO) && constraintWeight.getSoftScore().equals(BigDecimal.ZERO)) {
            matchExecutor = (RuleContext kcontext, BigDecimal matchWeight)
                    -> addHardConstraintMatch(kcontext, constraintWeight.getHardScore().multiply(matchWeight));
        } else if (constraintWeight.getHardScore().equals(BigDecimal.ZERO) && constraintWeight.getSoftScore().equals(BigDecimal.ZERO)) {
            matchExecutor = (RuleContext kcontext, BigDecimal matchWeight)
                    -> addMediumConstraintMatch(kcontext, constraintWeight.getMediumScore().multiply(matchWeight));
        } else if (constraintWeight.getHardScore().equals(BigDecimal.ZERO) && constraintWeight.getMediumScore().equals(BigDecimal.ZERO)) {
            matchExecutor = (RuleContext kcontext, BigDecimal matchWeight)
                    -> addSoftConstraintMatch(kcontext, constraintWeight.getSoftScore().multiply(matchWeight));
        } else {
            matchExecutor = (RuleContext kcontext, BigDecimal matchWeight)
                    -> addMultiConstraintMatch(kcontext,
                    constraintWeight.getHardScore().multiply(matchWeight),
                    constraintWeight.getMediumScore().multiply(matchWeight),
                    constraintWeight.getSoftScore().multiply(matchWeight));
        }
        matchExecutorByNumberMap.put(rule, matchExecutor);
        matchExecutorByScoreMap.put(rule, (RuleContext kcontext,
                HardMediumSoftBigDecimalScore weightMultiplier) -> addMultiConstraintMatch(kcontext,
                constraintWeight.getHardScore().multiply(weightMultiplier.getHardScore()),
                constraintWeight.getMediumScore().multiply(weightMultiplier.getMediumScore()),
                constraintWeight.getSoftScore().multiply(weightMultiplier.getSoftScore())));
    }

    // ************************************************************************
    // Penalize and reward methods
    // ************************************************************************

    /**
     * Penalize a match by the {@link ConstraintWeight} negated.
     * @param kcontext never null, the magic variable in DRL
     */
    public void penalize(RuleContext kcontext) {
        impactScore(kcontext, BigDecimal.ONE.negate());
    }

    /**
     * Penalize a match by the {@link ConstraintWeight} negated and multiplied with the weightMultiplier for all score levels.
     * @param kcontext never null, the magic variable in DRL
     * @param weightMultiplier at least 0
     */
    public void penalize(RuleContext kcontext, BigDecimal weightMultiplier) {
        impactScore(kcontext, weightMultiplier.negate());
    }

    /**
     * Penalize a match by the {@link ConstraintWeight} negated and multiplied with the specific weightMultiplier per score level.
     * Slower than {@link #penalize(RuleContext, BigDecimal)}.
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeightMultiplier at least 0
     * @param mediumWeightMultiplier at least 0
     * @param softWeightMultiplier at least 0
     */
    public void penalize(RuleContext kcontext, BigDecimal hardWeightMultiplier, BigDecimal mediumWeightMultiplier, BigDecimal softWeightMultiplier) {
        impactScore(kcontext, hardWeightMultiplier.negate(), mediumWeightMultiplier.negate(), softWeightMultiplier.negate());
    }

    /**
     * Reward a match by the {@link ConstraintWeight}.
     * @param kcontext never null, the magic variable in DRL
     */
    public void reward(RuleContext kcontext) {
        impactScore(kcontext, BigDecimal.ONE);
    }

    /**
     * Reward a match by the {@link ConstraintWeight} multiplied with the weightMultiplier for all score levels.
     * @param kcontext never null, the magic variable in DRL
     * @param weightMultiplier at least 0
     */
    public void reward(RuleContext kcontext, BigDecimal weightMultiplier) {
        impactScore(kcontext, weightMultiplier);
    }

    /**
     * Reward a match by the {@link ConstraintWeight} multiplied with the specific weightMultiplier per score level.
     * Slower than {@link #reward(RuleContext, BigDecimal)}.
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeightMultiplier at least 0
     * @param mediumWeightMultiplier at least 0
     * @param softWeightMultiplier at least 0
     */
    public void reward(RuleContext kcontext, BigDecimal hardWeightMultiplier, BigDecimal mediumWeightMultiplier, BigDecimal softWeightMultiplier) {
        impactScore(kcontext, hardWeightMultiplier, mediumWeightMultiplier, softWeightMultiplier);
    }

    @Override
    public void impactScore(RuleContext kcontext) {
        impactScore(kcontext, BigDecimal.ONE);
    }

    @Override
    public void impactScore(RuleContext kcontext, BigDecimal weightMultiplier) {
        Rule rule = kcontext.getRule();
        BiConsumer<RuleContext, BigDecimal> matchExecutor = matchExecutorByNumberMap.get(rule);
        if (matchExecutor == null) {
            throw new IllegalStateException("The DRL rule (" + rule.getPackageName() + ":" + rule.getName()
                    + ") does not match a @" + ConstraintWeight.class.getSimpleName() + " on the @"
                    + ConstraintConfiguration.class.getSimpleName() + " annotated class.");
        }
        matchExecutor.accept(kcontext, weightMultiplier);
    }

    private void impactScore(RuleContext kcontext, BigDecimal hardWeightMultiplier, BigDecimal mediumWeightMultiplier, BigDecimal softWeightMultiplier) {
        Rule rule = kcontext.getRule();
        BiConsumer<RuleContext, HardMediumSoftBigDecimalScore> matchExecutor = matchExecutorByScoreMap.get(rule);
        if (matchExecutor == null) {
            throw new IllegalStateException("The DRL rule (" + rule.getPackageName() + ":" + rule.getName()
                    + ") does not match a @" + ConstraintWeight.class.getSimpleName() + " on the @"
                    + ConstraintConfiguration.class.getSimpleName() + " annotated class.");
        }
        matchExecutor.accept(kcontext, HardMediumSoftBigDecimalScore.of(hardWeightMultiplier, mediumWeightMultiplier, softWeightMultiplier));
    }

    // ************************************************************************
    // Other match methods
    // ************************************************************************

    /**
     * Add a hard constraint of specified weighting.
     *
     * This is typically used in Drools scoring to add a hard constraint match (negative value to indicate an infeasible
     * solution).
     *
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeight never null, higher is better, negative for a penalty, positive for a reward
     */
    public void addHardConstraintMatch(RuleContext kcontext, BigDecimal hardWeight) {
        hardScore = hardScore.add(hardWeight);
        registerConstraintMatch(kcontext,
                () -> hardScore = hardScore.subtract(hardWeight),
                () -> HardMediumSoftBigDecimalScore.of(hardWeight, BigDecimal.ZERO, BigDecimal.ZERO));
    }

    /**
     * Add a medium level constraint of specified weighting.
     *
     * This is typically used in Drools scoring to add a medium priority constraint match.
     *
     * @param kcontext never null, the magic variable in DRL
     * @param mediumWeight never null, higher is better, negative for a penalty, positive for a reward
     */
    public void addMediumConstraintMatch(RuleContext kcontext, BigDecimal mediumWeight) {
        mediumScore = mediumScore.add(mediumWeight);
        registerConstraintMatch(kcontext,
                () -> mediumScore = mediumScore.subtract(mediumWeight),
                () -> HardMediumSoftBigDecimalScore.of(BigDecimal.ZERO, mediumWeight, BigDecimal.ZERO));
    }

    /**
     * Add a soft constraint match of specified weighting.
     *
     * This is typically used in Drools scoring to add a low priority constraint match.
     *
     * @param kcontext never null, the magic variable in DRL
     * @param softWeight never null, higher is better, negative for a penalty, positive for a reward
     */
    public void addSoftConstraintMatch(RuleContext kcontext, BigDecimal softWeight) {
        softScore = softScore.add(softWeight);
        registerConstraintMatch(kcontext,
                () -> softScore = softScore.subtract(softWeight),
                () -> HardMediumSoftBigDecimalScore.of(BigDecimal.ZERO, BigDecimal.ZERO, softWeight));
    }

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeight never null, higher is better, negative for a penalty, positive for a reward
     * @param mediumWeight never null, higher is better, negative for a penalty, positive for a reward
     * @param softWeight never null, higher is better, negative for a penalty, positive for a reward
     */
    public void addMultiConstraintMatch(RuleContext kcontext, BigDecimal hardWeight, BigDecimal mediumWeight, BigDecimal softWeight) {
        hardScore = hardScore.add(hardWeight);
        mediumScore = mediumScore.add(mediumWeight);
        softScore = softScore.add(softWeight);
        registerConstraintMatch(kcontext,
                () -> {
                    hardScore = hardScore.subtract(hardWeight);
                    mediumScore = mediumScore.subtract(mediumWeight);
                    softScore = softScore.subtract(softWeight);
                },
                () -> HardMediumSoftBigDecimalScore.of(hardWeight, mediumWeight, softWeight));
    }

    @Override
    public HardMediumSoftBigDecimalScore extractScore(int initScore) {
        return HardMediumSoftBigDecimalScore.ofUninitialized(initScore, hardScore, mediumScore, softScore);
    }

}
