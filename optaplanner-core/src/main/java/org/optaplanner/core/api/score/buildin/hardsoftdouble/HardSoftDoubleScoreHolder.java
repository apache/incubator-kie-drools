/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.buildin.hardsoftdouble;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScoreHolder;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;

/**
 * WARNING: NOT RECOMMENDED TO USE DUE TO ROUNDING ERRORS THAT CAUSE SCORE CORRUPTION.
 * Use {@link HardSoftBigDecimalScoreHolder} instead.
 * @see HardSoftDoubleScore
 */
public class HardSoftDoubleScoreHolder extends AbstractScoreHolder<HardSoftDoubleScore> {

    protected final Map<Rule, BiConsumer<RuleContext, Double>> matchExecutorByNumberMap = new LinkedHashMap<>();
    /** Slower than {@link #matchExecutorByNumberMap} */
    protected final Map<Rule, BiConsumer<RuleContext, HardSoftDoubleScore>> matchExecutorByScoreMap = new LinkedHashMap<>();

    protected double hardScore;
    protected double softScore;

    public HardSoftDoubleScoreHolder(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled, HardSoftDoubleScore.ZERO);
    }

    public double getHardScore() {
        return hardScore;
    }

    public double getSoftScore() {
        return softScore;
    }

    // ************************************************************************
    // Setup methods
    // ************************************************************************

    @Override
    public void configureConstraintWeight(Rule rule, HardSoftDoubleScore constraintWeight) {
        super.configureConstraintWeight(rule, constraintWeight);
        BiConsumer<RuleContext, Double> matchExecutor;
        if (constraintWeight.equals(HardSoftDoubleScore.ZERO)) {
            matchExecutor = (RuleContext kcontext, Double matchWeight) -> {};
        } else if (constraintWeight.getSoftScore() == 0.0) {
            matchExecutor = (RuleContext kcontext, Double matchWeight)
                    -> addHardConstraintMatch(kcontext, constraintWeight.getHardScore() * matchWeight);
        } else if (constraintWeight.getHardScore() == 0.0) {
            matchExecutor = (RuleContext kcontext, Double matchWeight)
                    -> addSoftConstraintMatch(kcontext, constraintWeight.getSoftScore() * matchWeight);
        } else {
            matchExecutor = (RuleContext kcontext, Double matchWeight)
                    -> addMultiConstraintMatch(kcontext,
                    constraintWeight.getHardScore() * matchWeight,
                    constraintWeight.getSoftScore() * matchWeight);
        }
        matchExecutorByNumberMap.put(rule, matchExecutor);
        matchExecutorByScoreMap.put(rule, (RuleContext kcontext,
                HardSoftDoubleScore weightMultiplier) -> addMultiConstraintMatch(kcontext,
                constraintWeight.getHardScore() * weightMultiplier.getHardScore(),
                constraintWeight.getSoftScore() * weightMultiplier.getSoftScore()));
    }

    // ************************************************************************
    // Penalize and reward methods
    // ************************************************************************

    /**
     * Penalize a match by the {@link ConstraintWeight} negated.
     * @param kcontext never null, the magic variable in DRL
     */
    public void penalize(RuleContext kcontext) {
        impactScore(kcontext, -1.0);
    }

    /**
     * Penalize a match by the {@link ConstraintWeight} negated and multiplied with the weightMultiplier for all score levels.
     * @param kcontext never null, the magic variable in DRL
     * @param weightMultiplier at least 0
     */
    public void penalize(RuleContext kcontext, double weightMultiplier) {
        impactScore(kcontext, -weightMultiplier);
    }

    /**
     * Penalize a match by the {@link ConstraintWeight} negated and multiplied with the specific weightMultiplier per score level.
     * Slower than {@link #penalize(RuleContext, double)}.
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeightMultiplier at least 0
     * @param softWeightMultiplier at least 0
     */
    public void penalize(RuleContext kcontext, double hardWeightMultiplier, double softWeightMultiplier) {
        impactScore(kcontext, -hardWeightMultiplier, -softWeightMultiplier);
    }

    /**
     * Reward a match by the {@link ConstraintWeight}.
     * @param kcontext never null, the magic variable in DRL
     */
    public void reward(RuleContext kcontext) {
        impactScore(kcontext, 1.0);
    }

    /**
     * Reward a match by the {@link ConstraintWeight} multiplied with the weightMultiplier for all score levels.
     * @param kcontext never null, the magic variable in DRL
     * @param weightMultiplier at least 0
     */
    public void reward(RuleContext kcontext, double weightMultiplier) {
        impactScore(kcontext, weightMultiplier);
    }

    /**
     * Reward a match by the {@link ConstraintWeight} multiplied with the specific weightMultiplier per score level.
     * Slower than {@link #reward(RuleContext, double)}.
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeightMultiplier at least 0
     * @param softWeightMultiplier at least 0
     */
    public void reward(RuleContext kcontext, double hardWeightMultiplier, double softWeightMultiplier) {
        impactScore(kcontext, hardWeightMultiplier, softWeightMultiplier);
    }

    @Override
    public void impactScore(RuleContext kcontext) {
        impactScore(kcontext, 1);
    }

    private void impactScore(RuleContext kcontext, double weightMultiplier) {
        Rule rule = kcontext.getRule();
        BiConsumer<RuleContext, Double> matchExecutor = matchExecutorByNumberMap.get(rule);
        if (matchExecutor == null) {
            throw new IllegalStateException("The DRL rule (" + rule.getPackageName() + ":" + rule.getName()
                    + ") does not match a @" + ConstraintWeight.class.getSimpleName() + " on the @"
                    + ConstraintConfiguration.class.getSimpleName() + " annotated class.");
        }
        matchExecutor.accept(kcontext, weightMultiplier);
    }

    private void impactScore(RuleContext kcontext, double hardWeightMultiplier, double softWeightMultiplier) {
        Rule rule = kcontext.getRule();
        BiConsumer<RuleContext, HardSoftDoubleScore> matchExecutor = matchExecutorByScoreMap.get(rule);
        if (matchExecutor == null) {
            throw new IllegalStateException("The DRL rule (" + rule.getPackageName() + ":" + rule.getName()
                    + ") does not match a @" + ConstraintWeight.class.getSimpleName() + " on the @"
                    + ConstraintConfiguration.class.getSimpleName() + " annotated class.");
        }
        matchExecutor.accept(kcontext, HardSoftDoubleScore.of(hardWeightMultiplier, softWeightMultiplier));
    }

    // ************************************************************************
    // Other match methods
    // ************************************************************************

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeight higher is better, negative for a penalty, positive for a reward
     */
    public void addHardConstraintMatch(RuleContext kcontext, double hardWeight) {
        hardScore += hardWeight;
        registerConstraintMatch(kcontext,
                () -> hardScore -= hardWeight,
                () -> HardSoftDoubleScore.of(hardWeight, 0.0));
    }

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param softWeight higher is better, negative for a penalty, positive for a reward
     */
    public void addSoftConstraintMatch(RuleContext kcontext, double softWeight) {
        softScore += softWeight;
        registerConstraintMatch(kcontext,
                () -> softScore -= softWeight,
                () -> HardSoftDoubleScore.of(0.0, softWeight));
    }

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeight higher is better, negative for a penalty, positive for a reward
     * @param softWeight higher is better, negative for a penalty, positive for a reward
     */
    public void addMultiConstraintMatch(RuleContext kcontext, double hardWeight, double softWeight) {
        hardScore += hardWeight;
        softScore += softWeight;
        registerConstraintMatch(kcontext,
                () -> {
                    hardScore -= hardWeight;
                    softScore -= softWeight;
                },
                () -> HardSoftDoubleScore.of(hardWeight, softWeight));
    }

    @Override
    public HardSoftDoubleScore extractScore(int initScore) {
        return HardSoftDoubleScore.ofUninitialized(initScore, hardScore, softScore);
    }

}
