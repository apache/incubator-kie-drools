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

package org.optaplanner.core.api.score.buildin.hardsoftbigdecimal;

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
 * @see HardSoftBigDecimalScore
 */
public class HardSoftBigDecimalScoreHolder extends AbstractScoreHolder<HardSoftBigDecimalScore> {

    protected final Map<Rule, BiConsumer<RuleContext, BigDecimal>> matchExecutorByNumberMap = new LinkedHashMap<>();
    /** Slower than {@link #matchExecutorByNumberMap} */
    protected final Map<Rule, BiConsumer<RuleContext, HardSoftBigDecimalScore>> matchExecutorByScoreMap = new LinkedHashMap<>();

    protected BigDecimal hardScore = null;
    protected BigDecimal softScore = null;

    public HardSoftBigDecimalScoreHolder(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled, HardSoftBigDecimalScore.ZERO);
    }

    public BigDecimal getHardScore() {
        return hardScore;
    }

    public BigDecimal getSoftScore() {
        return softScore;
    }

    // ************************************************************************
    // Setup methods
    // ************************************************************************

    @Override
    public void putConstraintWeight(Rule rule, HardSoftBigDecimalScore constraintWeight) {
        BiConsumer<RuleContext, BigDecimal> matchExecutor;
        if (constraintWeight.equals(HardSoftBigDecimalScore.ZERO)) {
            matchExecutor = (RuleContext kcontext, BigDecimal matchWeight) -> {};
        } else if (constraintWeight.getInitScore() != 0) {
            throw new IllegalStateException("The initScore (" + constraintWeight.getInitScore() + ") must be 0.");
        } else if (constraintWeight.getSoftScore().equals(BigDecimal.ZERO)) {
            matchExecutor = (RuleContext kcontext, BigDecimal matchWeight)
                    -> addHardConstraintMatch(kcontext, constraintWeight.getHardScore().multiply(matchWeight));
        } else if (constraintWeight.getHardScore().equals(BigDecimal.ZERO)) {
            matchExecutor = (RuleContext kcontext, BigDecimal matchWeight)
                    -> addSoftConstraintMatch(kcontext, constraintWeight.getSoftScore().multiply(matchWeight));
        } else {
            matchExecutor = (RuleContext kcontext, BigDecimal matchWeight)
                    -> addMultiConstraintMatch(kcontext,
                    constraintWeight.getHardScore().multiply(matchWeight),
                    constraintWeight.getSoftScore().multiply(matchWeight));
        }
        matchExecutorByNumberMap.put(rule, matchExecutor);
        matchExecutorByScoreMap.put(rule, (RuleContext kcontext,
                HardSoftBigDecimalScore weightMultiplier) -> addMultiConstraintMatch(kcontext,
                constraintWeight.getHardScore().multiply(weightMultiplier.getHardScore()),
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
        reward(kcontext, BigDecimal.ONE.negate());
    }

    /**
     * Penalize a match by the {@link ConstraintWeight} negated and multiplied with the weightMultiplier for all score levels.
     * @param kcontext never null, the magic variable in DRL
     * @param weightMultiplier at least 0
     */
    public void penalize(RuleContext kcontext, BigDecimal weightMultiplier) {
        reward(kcontext, weightMultiplier.negate());
    }

    /**
     * Penalize a match by the {@link ConstraintWeight} negated and multiplied with the specific weightMultiplier per score level.
     * Slower than {@link #penalize(RuleContext, BigDecimal)}.
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeightMultiplier at least 0
     * @param softWeightMultiplier at least 0
     */
    public void penalize(RuleContext kcontext, BigDecimal hardWeightMultiplier, BigDecimal softWeightMultiplier) {
        reward(kcontext, hardWeightMultiplier.negate(), softWeightMultiplier.negate());
    }

    /**
     * Reward a match by the {@link ConstraintWeight}.
     * @param kcontext never null, the magic variable in DRL
     */
    public void reward(RuleContext kcontext) {
        reward(kcontext, BigDecimal.ONE);
    }

    /**
     * Reward a match by the {@link ConstraintWeight} multiplied with the weightMultiplier for all score levels.
     * @param kcontext never null, the magic variable in DRL
     * @param weightMultiplier at least 0
     */
    public void reward(RuleContext kcontext, BigDecimal weightMultiplier) {
        Rule rule = kcontext.getRule();
        BiConsumer<RuleContext, BigDecimal> matchExecutor = matchExecutorByNumberMap.get(rule);
        if (matchExecutor == null) {
            throw new IllegalStateException("The DRL rule (" + rule.getPackageName() + ":" + rule.getName()
                    + ") does not match a @" + ConstraintWeight.class.getSimpleName() + " on the @"
                    + ConstraintConfiguration.class.getSimpleName() + " annotated class.");
        }
        matchExecutor.accept(kcontext, weightMultiplier);
    }

    /**
     * Reward a match by the {@link ConstraintWeight} multiplied with the specific weightMultiplier per score level.
     * Slower than {@link #reward(RuleContext, BigDecimal)}.
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeightMultiplier at least 0
     * @param softWeightMultiplier at least 0
     */
    public void reward(RuleContext kcontext, BigDecimal hardWeightMultiplier, BigDecimal softWeightMultiplier) {
        Rule rule = kcontext.getRule();
        BiConsumer<RuleContext, HardSoftBigDecimalScore> matchExecutor = matchExecutorByScoreMap.get(rule);
        if (matchExecutor == null) {
            throw new IllegalStateException("The DRL rule (" + rule.getPackageName() + ":" + rule.getName()
                    + ") does not match a @" + ConstraintWeight.class.getSimpleName() + " on the @"
                    + ConstraintConfiguration.class.getSimpleName() + " annotated class.");
        }
        matchExecutor.accept(kcontext, HardSoftBigDecimalScore.of(hardWeightMultiplier, softWeightMultiplier));
    }

    // ************************************************************************
    // Other match methods
    // ************************************************************************

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeight never null, higher is better, negative for a penalty, positive for a reward
     */
    public void addHardConstraintMatch(RuleContext kcontext, BigDecimal hardWeight) {
        hardScore = (hardScore == null) ? hardWeight : hardScore.add(hardWeight);
        registerConstraintMatch(kcontext,
                () -> hardScore = hardScore.subtract(hardWeight),
                () -> HardSoftBigDecimalScore.of(hardWeight, BigDecimal.ZERO));
    }

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param softWeight never null, higher is better, negative for a penalty, positive for a reward
     */
    public void addSoftConstraintMatch(RuleContext kcontext, BigDecimal softWeight) {
        softScore = (softScore == null) ? softWeight : softScore.add(softWeight);
        registerConstraintMatch(kcontext,
                () -> softScore = softScore.subtract(softWeight),
                () -> HardSoftBigDecimalScore.of(BigDecimal.ZERO, softWeight));
    }

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeight never null, higher is better, negative for a penalty, positive for a reward
     * @param softWeight never null, higher is better, negative for a penalty, positive for a reward
     */
    public void addMultiConstraintMatch(RuleContext kcontext, BigDecimal hardWeight, BigDecimal softWeight) {
        hardScore = (hardScore == null) ? hardWeight : hardScore.add(hardWeight);
        softScore = (softScore == null) ? softWeight : softScore.add(softWeight);
        registerConstraintMatch(kcontext,
                () -> {
                    hardScore = hardScore.subtract(hardWeight);
                    softScore = softScore.subtract(softWeight);
                },
                () -> HardSoftBigDecimalScore.of(hardWeight, softWeight));
    }

    @Override
    public HardSoftBigDecimalScore extractScore(int initScore) {
        return HardSoftBigDecimalScore.ofUninitialized(initScore,
                hardScore == null ? BigDecimal.ZERO : hardScore,
                softScore == null ? BigDecimal.ZERO : softScore);
    }

}
