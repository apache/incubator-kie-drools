/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.buildin.hardsoftlong;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;

/**
 * @see HardSoftLongScore
 */
public class HardSoftLongScoreHolder extends AbstractScoreHolder<HardSoftLongScore> {

    protected final Map<Rule, BiConsumer<RuleContext, Long>> matchExecutorByNumberMap = new LinkedHashMap<>();
    /** Slower than {@link #matchExecutorByNumberMap} */
    protected final Map<Rule, BiConsumer<RuleContext, HardSoftLongScore>> matchExecutorByScoreMap = new LinkedHashMap<>();

    protected long hardScore;
    protected long softScore;

    public HardSoftLongScoreHolder(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled, HardSoftLongScore.ZERO);
    }

    public long getHardScore() {
        return hardScore;
    }

    public long getSoftScore() {
        return softScore;
    }

    // ************************************************************************
    // Setup methods
    // ************************************************************************

    @Override
    public void configureConstraintWeight(Rule rule, HardSoftLongScore constraintWeight) {
        super.configureConstraintWeight(rule, constraintWeight);
        BiConsumer<RuleContext, Long> matchExecutor;
        if (constraintWeight.equals(HardSoftLongScore.ZERO)) {
            matchExecutor = (RuleContext kcontext, Long matchWeight) -> {};
        } else if (constraintWeight.getSoftScore() == 0L) {
            matchExecutor = (RuleContext kcontext, Long matchWeight)
                    -> addHardConstraintMatch(kcontext, constraintWeight.getHardScore() * matchWeight);
        } else if (constraintWeight.getHardScore() == 0L) {
            matchExecutor = (RuleContext kcontext, Long matchWeight)
                    -> addSoftConstraintMatch(kcontext, constraintWeight.getSoftScore() * matchWeight);
        } else {
            matchExecutor = (RuleContext kcontext, Long matchWeight)
                    -> addMultiConstraintMatch(kcontext,
                    constraintWeight.getHardScore() * matchWeight,
                    constraintWeight.getSoftScore() * matchWeight);
        }
        matchExecutorByNumberMap.put(rule, matchExecutor);
        matchExecutorByScoreMap.put(rule, (RuleContext kcontext,
                HardSoftLongScore weightMultiplier) -> addMultiConstraintMatch(kcontext,
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
        impactScore(kcontext, -1L);
    }

    /**
     * Penalize a match by the {@link ConstraintWeight} negated and multiplied with the weightMultiplier for all score levels.
     * @param kcontext never null, the magic variable in DRL
     * @param weightMultiplier at least 0
     */
    public void penalize(RuleContext kcontext, long weightMultiplier) {
        impactScore(kcontext, -weightMultiplier);
    }

    /**
     * Penalize a match by the {@link ConstraintWeight} negated and multiplied with the specific weightMultiplier per score level.
     * Slower than {@link #penalize(RuleContext, long)}.
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeightMultiplier at least 0
     * @param softWeightMultiplier at least 0
     */
    public void penalize(RuleContext kcontext, long hardWeightMultiplier, long softWeightMultiplier) {
        impactScore(kcontext, -hardWeightMultiplier, -softWeightMultiplier);
    }

    /**
     * Reward a match by the {@link ConstraintWeight}.
     * @param kcontext never null, the magic variable in DRL
     */
    public void reward(RuleContext kcontext) {
        impactScore(kcontext, 1L);
    }

    /**
     * Reward a match by the {@link ConstraintWeight} multiplied with the weightMultiplier for all score levels.
     * @param kcontext never null, the magic variable in DRL
     * @param weightMultiplier at least 0
     */
    public void reward(RuleContext kcontext, long weightMultiplier) {
        impactScore(kcontext, weightMultiplier);
    }

    /**
     * Reward a match by the {@link ConstraintWeight} multiplied with the specific weightMultiplier per score level.
     * Slower than {@link #reward(RuleContext, long)}.
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeightMultiplier at least 0
     * @param softWeightMultiplier at least 0
     */
    public void reward(RuleContext kcontext, long hardWeightMultiplier, long softWeightMultiplier) {
        impactScore(kcontext, hardWeightMultiplier, softWeightMultiplier);
    }

    @Override
    public void impactScore(RuleContext kcontext) {
        impactScore(kcontext, 1L);
    }

    @Override
    public void impactScore(RuleContext kcontext, long weightMultiplier) {
        Rule rule = kcontext.getRule();
        BiConsumer<RuleContext, Long> matchExecutor = matchExecutorByNumberMap.get(rule);
        if (matchExecutor == null) {
            throw new IllegalStateException("The DRL rule (" + rule.getPackageName() + ":" + rule.getName()
                    + ") does not match a @" + ConstraintWeight.class.getSimpleName() + " on the @"
                    + ConstraintConfiguration.class.getSimpleName() + " annotated class.");
        }
        matchExecutor.accept(kcontext, weightMultiplier);
    }

    private void impactScore(RuleContext kcontext, long hardWeightMultiplier, long softWeightMultiplier) {
        Rule rule = kcontext.getRule();
        BiConsumer<RuleContext, HardSoftLongScore> matchExecutor = matchExecutorByScoreMap.get(rule);
        if (matchExecutor == null) {
            throw new IllegalStateException("The DRL rule (" + rule.getPackageName() + ":" + rule.getName()
                    + ") does not match a @" + ConstraintWeight.class.getSimpleName() + " on the @"
                    + ConstraintConfiguration.class.getSimpleName() + " annotated class.");
        }
        matchExecutor.accept(kcontext, HardSoftLongScore.of(hardWeightMultiplier, softWeightMultiplier));
    }

    // ************************************************************************
    // Other match methods
    // ************************************************************************

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeight higher is better, negative for a penalty, positive for a reward
     */
    public void addHardConstraintMatch(RuleContext kcontext, long hardWeight) {
        hardScore += hardWeight;
        registerConstraintMatch(kcontext,
                () -> hardScore -= hardWeight,
                () -> HardSoftLongScore.of(hardWeight, 0L));
    }

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param softWeight higher is better, negative for a penalty, positive for a reward
     */
    public void addSoftConstraintMatch(RuleContext kcontext, long softWeight) {
        softScore += softWeight;
        registerConstraintMatch(kcontext,
                () -> softScore -= softWeight,
                () -> HardSoftLongScore.of(0L, softWeight));
    }

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param softWeight higher is better, negative for a penalty, positive for a reward
     */
    public void addMultiConstraintMatch(RuleContext kcontext, long hardWeight, long softWeight) {
        hardScore += hardWeight;
        softScore += softWeight;
        registerConstraintMatch(kcontext,
                () -> {
                    hardScore -= hardWeight;
                    softScore -= softWeight;
                },
                () -> HardSoftLongScore.of(hardWeight, softWeight));
    }

    @Override
    public HardSoftLongScore extractScore(int initScore) {
        return HardSoftLongScore.ofUninitialized(initScore, hardScore, softScore);
    }

}
