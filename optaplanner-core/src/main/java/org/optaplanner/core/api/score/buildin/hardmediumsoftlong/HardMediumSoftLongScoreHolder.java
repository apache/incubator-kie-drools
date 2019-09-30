/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.buildin.hardmediumsoftlong;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;

/**
 * @see HardMediumSoftScore
 */
public class HardMediumSoftLongScoreHolder extends AbstractScoreHolder<HardMediumSoftLongScore> {

    protected final Map<Rule, BiConsumer<RuleContext, Long>> matchExecutorByNumberMap = new LinkedHashMap<>();
    /** Slower than {@link #matchExecutorByNumberMap} */
    protected final Map<Rule, BiConsumer<RuleContext, HardMediumSoftLongScore>> matchExecutorByScoreMap = new LinkedHashMap<>();

    protected long hardScore;
    protected long mediumScore;
    protected long softScore;

    public HardMediumSoftLongScoreHolder(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled, HardMediumSoftLongScore.ZERO);
    }

    public long getHardScore() {
        return hardScore;
    }

    public long getMediumScore() {
        return mediumScore;
    }

    public long getSoftScore() {
        return softScore;
    }

    // ************************************************************************
    // Setup methods
    // ************************************************************************

    @Override
    public void configureConstraintWeight(Rule rule, HardMediumSoftLongScore constraintWeight) {
        super.configureConstraintWeight(rule, constraintWeight);
        BiConsumer<RuleContext, Long> matchExecutor;
        if (constraintWeight.equals(HardMediumSoftLongScore.ZERO)) {
            matchExecutor = (RuleContext kcontext, Long matchWeight) -> {};
        } else if (constraintWeight.getMediumScore() == 0 && constraintWeight.getSoftScore() == 0) {
            matchExecutor = (RuleContext kcontext, Long matchWeight)
                    -> addHardConstraintMatch(kcontext, constraintWeight.getHardScore() * matchWeight);
        } else if (constraintWeight.getHardScore() == 0 && constraintWeight.getSoftScore() == 0) {
            matchExecutor = (RuleContext kcontext, Long matchWeight)
                    -> addMediumConstraintMatch(kcontext, constraintWeight.getMediumScore() * matchWeight);
        } else if (constraintWeight.getHardScore() == 0 && constraintWeight.getMediumScore() == 0) {
            matchExecutor = (RuleContext kcontext, Long matchWeight)
                    -> addSoftConstraintMatch(kcontext, constraintWeight.getSoftScore() * matchWeight);
        } else {
            matchExecutor = (RuleContext kcontext, Long matchWeight)
                    -> addMultiConstraintMatch(kcontext,
                    constraintWeight.getHardScore() * matchWeight,
                    constraintWeight.getMediumScore() * matchWeight,
                    constraintWeight.getSoftScore() * matchWeight);
        }
        matchExecutorByNumberMap.put(rule, matchExecutor);
        matchExecutorByScoreMap.put(rule, (RuleContext kcontext,
                HardMediumSoftLongScore weightMultiplier) -> addMultiConstraintMatch(kcontext,
                constraintWeight.getHardScore() * weightMultiplier.getHardScore(),
                constraintWeight.getMediumScore() * weightMultiplier.getMediumScore(),
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
     * @param mediumWeightMultiplier at least 0
     * @param softWeightMultiplier at least 0
     */
    public void penalize(RuleContext kcontext, long hardWeightMultiplier, long mediumWeightMultiplier, long softWeightMultiplier) {
        impactScore(kcontext, -hardWeightMultiplier, -mediumWeightMultiplier, -softWeightMultiplier);
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
     * @param mediumWeightMultiplier at least 0
     * @param softWeightMultiplier at least 0
     */
    public void reward(RuleContext kcontext, long hardWeightMultiplier, long mediumWeightMultiplier, long softWeightMultiplier) {
        impactScore(kcontext, hardWeightMultiplier, mediumWeightMultiplier, softWeightMultiplier);
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

    private void impactScore(RuleContext kcontext, long hardWeightMultiplier, long mediumWeightMultiplier, long softWeightMultiplier) {
        Rule rule = kcontext.getRule();
        BiConsumer<RuleContext, HardMediumSoftLongScore> matchExecutor = matchExecutorByScoreMap.get(rule);
        if (matchExecutor == null) {
            throw new IllegalStateException("The DRL rule (" + rule.getPackageName() + ":" + rule.getName()
                    + ") does not match a @" + ConstraintWeight.class.getSimpleName() + " on the @"
                    + ConstraintConfiguration.class.getSimpleName() + " annotated class.");
        }
        matchExecutor.accept(kcontext, HardMediumSoftLongScore.of(hardWeightMultiplier, mediumWeightMultiplier, softWeightMultiplier));
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
                () -> HardMediumSoftLongScore.of(hardWeight, 0L, 0L));
    }

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param mediumWeight higher is better, negative for a penalty, positive for a reward
     */
    public void addMediumConstraintMatch(RuleContext kcontext, long mediumWeight) {
        mediumScore += mediumWeight;
        registerConstraintMatch(kcontext,
                () -> mediumScore -= mediumWeight,
                () -> HardMediumSoftLongScore.of(0L, mediumWeight, 0L));
    }

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param softWeight higher is better, negative for a penalty, positive for a reward
     */
    public void addSoftConstraintMatch(RuleContext kcontext, long softWeight) {
        softScore += softWeight;
        registerConstraintMatch(kcontext,
                () -> softScore -= softWeight,
                () -> HardMediumSoftLongScore.of(0L, 0L, softWeight));
    }

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeight higher is better, negative for a penalty, positive for a reward
     * @param mediumWeight higher is better, negative for a penalty, positive for a reward
     * @param softWeight higher is better, negative for a penalty, positive for a reward
     */
    public void addMultiConstraintMatch(RuleContext kcontext, long hardWeight, long mediumWeight, long softWeight) {
        hardScore += hardWeight;
        mediumScore += mediumWeight;
        softScore += softWeight;
        registerConstraintMatch(kcontext,
                () -> {
                    hardScore -= hardWeight;
                    mediumScore -= mediumWeight;
                    softScore -= softWeight;
                },
                () -> HardMediumSoftLongScore.of(hardWeight, mediumWeight, softWeight));
    }

    @Override
    public HardMediumSoftLongScore extractScore(int initScore) {
        return HardMediumSoftLongScore.ofUninitialized(initScore, hardScore, mediumScore, softScore);
    }

}
