/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.buildin.hardsoftbigdecimal;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScoreHolder;
import org.optaplanner.core.impl.score.holder.AbstractScoreHolder;

/**
 * @see HardSoftBigDecimalScore
 */
public final class HardSoftBigDecimalScoreHolderImpl extends AbstractScoreHolder<HardSoftBigDecimalScore>
        implements HardSoftBigDecimalScoreHolder {

    protected final Map<Rule, BigDecimalMatchExecutor> matchExecutorByNumberMap = new LinkedHashMap<>();
    /** Slower than {@link #matchExecutorByNumberMap} */
    protected final Map<Rule, ScoreMatchExecutor<HardSoftBigDecimalScore>> matchExecutorByScoreMap = new LinkedHashMap<>();

    protected BigDecimal hardScore = BigDecimal.ZERO;
    protected BigDecimal softScore = BigDecimal.ZERO;

    public HardSoftBigDecimalScoreHolderImpl(boolean constraintMatchEnabled) {
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
    public void configureConstraintWeight(Rule rule, HardSoftBigDecimalScore constraintWeight) {
        super.configureConstraintWeight(rule, constraintWeight);
        BigDecimalMatchExecutor matchExecutor;
        if (constraintWeight.equals(HardSoftBigDecimalScore.ZERO)) {
            matchExecutor = (RuleContext kcontext, BigDecimal matchWeight, Object... justifications) -> {
            };
        } else if (constraintWeight.getSoftScore().equals(BigDecimal.ZERO)) {
            matchExecutor =
                    (RuleContext kcontext, BigDecimal matchWeight, Object... justifications) -> addHardConstraintMatch(kcontext,
                            constraintWeight.getHardScore().multiply(matchWeight), justifications);
        } else if (constraintWeight.getHardScore().equals(BigDecimal.ZERO)) {
            matchExecutor =
                    (RuleContext kcontext, BigDecimal matchWeight, Object... justifications) -> addSoftConstraintMatch(kcontext,
                            constraintWeight.getSoftScore().multiply(matchWeight), justifications);
        } else {
            matchExecutor = (RuleContext kcontext, BigDecimal matchWeight, Object... justifications) -> addMultiConstraintMatch(
                    kcontext, constraintWeight.getHardScore().multiply(matchWeight),
                    constraintWeight.getSoftScore().multiply(matchWeight), justifications);
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

    @Override
    public void penalize(RuleContext kcontext) {
        impactScore(kcontext, BigDecimal.ONE.negate());
    }

    @Override
    public void penalize(RuleContext kcontext, BigDecimal weightMultiplier) {
        impactScore(kcontext, weightMultiplier.negate());
    }

    @Override
    public void penalize(RuleContext kcontext, BigDecimal hardWeightMultiplier, BigDecimal softWeightMultiplier) {
        impactScore(kcontext, hardWeightMultiplier.negate(), softWeightMultiplier.negate());
    }

    @Override
    public void reward(RuleContext kcontext) {
        impactScore(kcontext, BigDecimal.ONE);
    }

    @Override
    public void reward(RuleContext kcontext, BigDecimal weightMultiplier) {
        impactScore(kcontext, weightMultiplier);
    }

    @Override
    public void reward(RuleContext kcontext, BigDecimal hardWeightMultiplier, BigDecimal softWeightMultiplier) {
        impactScore(kcontext, hardWeightMultiplier, softWeightMultiplier);
    }

    @Override
    public void impactScore(RuleContext kcontext, Object... justifications) {
        impactScore(kcontext, BigDecimal.ONE, justifications);
    }

    @Override
    public void impactScore(RuleContext kcontext, int weightMultiplier, Object... justifications) {
        impactScore(kcontext, BigDecimal.valueOf(weightMultiplier), justifications);
    }

    @Override
    public void impactScore(RuleContext kcontext, long weightMultiplier, Object... justifications) {
        impactScore(kcontext, BigDecimal.valueOf(weightMultiplier), justifications);
    }

    @Override
    public void impactScore(RuleContext kcontext, BigDecimal weightMultiplier, Object... justifications) {
        Rule rule = kcontext.getRule();
        BigDecimalMatchExecutor matchExecutor = matchExecutorByNumberMap.get(rule);
        if (matchExecutor == null) {
            throw new IllegalStateException("The DRL rule (" + rule.getPackageName() + ":" + rule.getName()
                    + ") does not match a @" + ConstraintWeight.class.getSimpleName() + " on the @"
                    + ConstraintConfiguration.class.getSimpleName() + " annotated class.");
        }
        matchExecutor.accept(kcontext, weightMultiplier, justifications);
    }

    private void impactScore(RuleContext kcontext, BigDecimal hardWeightMultiplier, BigDecimal softWeightMultiplier) {
        Rule rule = kcontext.getRule();
        ScoreMatchExecutor<HardSoftBigDecimalScore> matchExecutor = matchExecutorByScoreMap.get(rule);
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

    @Override
    public void addHardConstraintMatch(RuleContext kcontext, BigDecimal hardWeight) {
        addHardConstraintMatch(kcontext, hardWeight, EMPTY_OBJECT_ARRAY);
    }

    private void addHardConstraintMatch(RuleContext kcontext, BigDecimal hardWeight, Object... justifications) {
        hardScore = hardScore.add(hardWeight);
        registerConstraintMatch(kcontext, () -> hardScore = hardScore.subtract(hardWeight),
                () -> HardSoftBigDecimalScore.ofHard(hardWeight), justifications);
    }

    @Override
    public void addSoftConstraintMatch(RuleContext kcontext, BigDecimal softWeight) {
        addSoftConstraintMatch(kcontext, softWeight, EMPTY_OBJECT_ARRAY);
    }

    private void addSoftConstraintMatch(RuleContext kcontext, BigDecimal softWeight, Object... justifications) {
        softScore = softScore.add(softWeight);
        registerConstraintMatch(kcontext, () -> softScore = softScore.subtract(softWeight),
                () -> HardSoftBigDecimalScore.ofSoft(softWeight), justifications);
    }

    @Override
    public void addMultiConstraintMatch(RuleContext kcontext, BigDecimal hardWeight, BigDecimal softWeight) {
        addMultiConstraintMatch(kcontext, hardWeight, softWeight, EMPTY_OBJECT_ARRAY);
    }

    private void addMultiConstraintMatch(RuleContext kcontext, BigDecimal hardWeight, BigDecimal softWeight,
            Object... justifications) {
        hardScore = hardScore.add(hardWeight);
        softScore = softScore.add(softWeight);
        registerConstraintMatch(kcontext,
                () -> {
                    hardScore = hardScore.subtract(hardWeight);
                    softScore = softScore.subtract(softWeight);
                },
                () -> HardSoftBigDecimalScore.of(hardWeight, softWeight),
                justifications);
    }

    @Override
    public HardSoftBigDecimalScore extractScore(int initScore) {
        return HardSoftBigDecimalScore.ofUninitialized(initScore, hardScore, softScore);
    }

}
