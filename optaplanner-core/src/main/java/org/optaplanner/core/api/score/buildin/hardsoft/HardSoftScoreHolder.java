/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.buildin.hardsoft;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;

/**
 * @see HardSoftScore
 */
public class HardSoftScoreHolder extends AbstractScoreHolder<HardSoftScore> {

    protected final Map<Rule, BiConsumer<RuleContext, Integer>> matchExecutorByNumberMap = new LinkedHashMap<>();
    /** Slower than {@link #matchExecutorByNumberMap} */
    protected final Map<Rule, BiConsumer<RuleContext, HardSoftScore>> matchExecutorByScoreMap = new LinkedHashMap<>();

    protected int hardScore;
    protected int softScore;

    public HardSoftScoreHolder(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled, HardSoftScore.ZERO);
    }

    public int getHardScore() {
        return hardScore;
    }

    public int getSoftScore() {
        return softScore;
    }

    // ************************************************************************
    // Setup methods
    // ************************************************************************

    @Override
    public void configureConstraintWeight(Rule rule, HardSoftScore constraintWeight) {
        super.configureConstraintWeight(rule, constraintWeight);
        BiConsumer<RuleContext, Integer> matchExecutor;
        if (constraintWeight.equals(HardSoftScore.ZERO)) {
            matchExecutor = (RuleContext kcontext, Integer matchWeight) -> {};
        } else if (constraintWeight.getSoftScore() == 0) {
            matchExecutor = (RuleContext kcontext, Integer matchWeight)
                    -> addHardConstraintMatch(kcontext, constraintWeight.getHardScore() * matchWeight);
        } else if (constraintWeight.getHardScore() == 0) {
            matchExecutor = (RuleContext kcontext, Integer matchWeight)
                    -> addSoftConstraintMatch(kcontext, constraintWeight.getSoftScore() * matchWeight);
        } else {
            matchExecutor = (RuleContext kcontext, Integer matchWeight)
                    -> addMultiConstraintMatch(kcontext,
                    constraintWeight.getHardScore() * matchWeight,
                    constraintWeight.getSoftScore() * matchWeight);
        }
        matchExecutorByNumberMap.put(rule, matchExecutor);
        matchExecutorByScoreMap.put(rule, (RuleContext kcontext,
                HardSoftScore weightMultiplier) -> addMultiConstraintMatch(kcontext,
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
        impactScore(kcontext, -1);
    }

    /**
     * Penalize a match by the {@link ConstraintWeight} negated and multiplied with the weightMultiplier for all score levels.
     * @param kcontext never null, the magic variable in DRL
     * @param weightMultiplier at least 0
     */
    public void penalize(RuleContext kcontext, int weightMultiplier) {
        impactScore(kcontext, -weightMultiplier);
    }

    /**
     * Penalize a match by the {@link ConstraintWeight} negated and multiplied with the specific weightMultiplier per score level.
     * Slower than {@link #penalize(RuleContext, int)}.
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeightMultiplier at least 0
     * @param softWeightMultiplier at least 0
     */
    public void penalize(RuleContext kcontext, int hardWeightMultiplier, int softWeightMultiplier) {
        impactScore(kcontext, -hardWeightMultiplier, -softWeightMultiplier);
    }

    /**
     * Reward a match by the {@link ConstraintWeight}.
     * @param kcontext never null, the magic variable in DRL
     */
    public void reward(RuleContext kcontext) {
        impactScore(kcontext, 1);
    }

    /**
     * Reward a match by the {@link ConstraintWeight} multiplied with the weightMultiplier for all score levels.
     * @param kcontext never null, the magic variable in DRL
     * @param weightMultiplier at least 0
     */
    public void reward(RuleContext kcontext, int weightMultiplier) {
        impactScore(kcontext, weightMultiplier);
    }

    /**
     * Reward a match by the {@link ConstraintWeight} multiplied with the specific weightMultiplier per score level.
     * Slower than {@link #reward(RuleContext, int)}.
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeightMultiplier at least 0
     * @param softWeightMultiplier at least 0
     */
    public void reward(RuleContext kcontext, int hardWeightMultiplier, int softWeightMultiplier) {
        impactScore(kcontext, hardWeightMultiplier, softWeightMultiplier);
    }

    @Override
    public void impactScore(RuleContext kcontext) {
        impactScore(kcontext, 1);
    }

    @Override
    public void impactScore(RuleContext kcontext, int weightMultiplier) {
        Rule rule = kcontext.getRule();
        BiConsumer<RuleContext, Integer> matchExecutor = matchExecutorByNumberMap.get(rule);
        if (matchExecutor == null) {
            throw new IllegalStateException("The DRL rule (" + rule.getPackageName() + ":" + rule.getName()
                    + ") does not match a @" + ConstraintWeight.class.getSimpleName() + " on the @"
                    + ConstraintConfiguration.class.getSimpleName() + " annotated class.");
        }
        matchExecutor.accept(kcontext, weightMultiplier);
    }

    private void impactScore(RuleContext kcontext, int hardWeightMultiplier, int softWeightMultiplier) {
        Rule rule = kcontext.getRule();
        BiConsumer<RuleContext, HardSoftScore> matchExecutor = matchExecutorByScoreMap.get(rule);
        if (matchExecutor == null) {
            throw new IllegalStateException("The DRL rule (" + rule.getPackageName() + ":" + rule.getName()
                    + ") does not match a @" + ConstraintWeight.class.getSimpleName() + " on the @"
                    + ConstraintConfiguration.class.getSimpleName() + " annotated class.");
        }
        matchExecutor.accept(kcontext, HardSoftScore.of(hardWeightMultiplier, softWeightMultiplier));
    }

    // ************************************************************************
    // Other match methods
    // ************************************************************************

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeight higher is better, negative for a penalty, positive for a reward
     */
    public void addHardConstraintMatch(RuleContext kcontext, int hardWeight) {
        hardScore += hardWeight;
        registerConstraintMatch(kcontext,
                () -> hardScore -= hardWeight,
                () -> HardSoftScore.of(hardWeight, 0));
    }

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param softWeight higher is better, negative for a penalty, positive for a reward
     */
    public void addSoftConstraintMatch(RuleContext kcontext, int softWeight) {
        softScore += softWeight;
        registerConstraintMatch(kcontext,
                () -> softScore -= softWeight,
                () -> HardSoftScore.of(0, softWeight));
    }

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeight higher is better, negative for a penalty, positive for a reward
     * @param softWeight higher is better, negative for a penalty, positive for a reward
     */
    public void addMultiConstraintMatch(RuleContext kcontext, int hardWeight, int softWeight) {
        hardScore += hardWeight;
        softScore += softWeight;
        registerConstraintMatch(kcontext,
                () -> {
                    hardScore -= hardWeight;
                    softScore -= softWeight;
                },
                () -> HardSoftScore.of(hardWeight, softWeight));
    }

    @Override
    public HardSoftScore extractScore(int initScore) {
        return HardSoftScore.ofUninitialized(initScore, hardScore, softScore);
    }

}
