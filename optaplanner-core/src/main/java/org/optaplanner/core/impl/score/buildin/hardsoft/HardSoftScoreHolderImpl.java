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

package org.optaplanner.core.impl.score.buildin.hardsoft;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScoreHolder;
import org.optaplanner.core.impl.score.holder.AbstractScoreHolder;

/**
 * @see HardSoftScore
 */
public final class HardSoftScoreHolderImpl extends AbstractScoreHolder<HardSoftScore> implements HardSoftScoreHolder {

    protected final Map<Rule, IntMatchExecutor> matchExecutorByNumberMap = new LinkedHashMap<>();
    /** Slower than {@link #matchExecutorByNumberMap} */
    protected final Map<Rule, ScoreMatchExecutor<HardSoftScore>> matchExecutorByScoreMap = new LinkedHashMap<>();

    protected int hardScore;
    protected int softScore;

    public HardSoftScoreHolderImpl(boolean constraintMatchEnabled) {
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
        IntMatchExecutor matchExecutor;
        if (constraintWeight.equals(HardSoftScore.ZERO)) {
            matchExecutor = (RuleContext kcontext, int matchWeight, Object... justifications) -> {
            };
        } else if (constraintWeight.getSoftScore() == 0) {
            matchExecutor =
                    (RuleContext kcontext, int matchWeight, Object... justifications) -> addHardConstraintMatch(kcontext,
                            constraintWeight.getHardScore() * matchWeight, justifications);
        } else if (constraintWeight.getHardScore() == 0) {
            matchExecutor =
                    (RuleContext kcontext, int matchWeight, Object... justifications) -> addSoftConstraintMatch(kcontext,
                            constraintWeight.getSoftScore() * matchWeight, justifications);
        } else {
            matchExecutor =
                    (RuleContext kcontext, int matchWeight, Object... justifications) -> addMultiConstraintMatch(kcontext,
                            constraintWeight.getHardScore() * matchWeight, constraintWeight.getSoftScore() * matchWeight,
                            justifications);
        }
        matchExecutorByNumberMap.put(rule, matchExecutor);
        matchExecutorByScoreMap.put(rule,
                (RuleContext kcontext, HardSoftScore weightMultiplier) -> addMultiConstraintMatch(
                        kcontext, constraintWeight.getHardScore() * weightMultiplier.getHardScore(),
                        constraintWeight.getSoftScore() * weightMultiplier.getSoftScore()));
    }

    // ************************************************************************
    // Penalize and reward methods
    // ************************************************************************

    @Override
    public void penalize(RuleContext kcontext) {
        impactScore(kcontext, -1);
    }

    @Override
    public void penalize(RuleContext kcontext, int weightMultiplier) {
        impactScore(kcontext, -weightMultiplier);
    }

    @Override
    public void penalize(RuleContext kcontext, int hardWeightMultiplier, int softWeightMultiplier) {
        impactScore(kcontext, -hardWeightMultiplier, -softWeightMultiplier);
    }

    @Override
    public void reward(RuleContext kcontext) {
        impactScore(kcontext, 1);
    }

    @Override
    public void reward(RuleContext kcontext, int weightMultiplier) {
        impactScore(kcontext, weightMultiplier);
    }

    @Override
    public void reward(RuleContext kcontext, int hardWeightMultiplier, int softWeightMultiplier) {
        impactScore(kcontext, hardWeightMultiplier, softWeightMultiplier);
    }

    @Override
    public void impactScore(RuleContext kcontext, Object... justifications) {
        impactScore(kcontext, 1, justifications);
    }

    @Override
    public void impactScore(RuleContext kcontext, int weightMultiplier, Object... justifications) {
        Rule rule = kcontext.getRule();
        IntMatchExecutor matchExecutor = matchExecutorByNumberMap.get(rule);
        if (matchExecutor == null) {
            throw new IllegalStateException("The DRL rule (" + rule.getPackageName() + ":" + rule.getName()
                    + ") does not match a @" + ConstraintWeight.class.getSimpleName() + " on the @"
                    + ConstraintConfiguration.class.getSimpleName() + " annotated class.");
        }
        matchExecutor.accept(kcontext, weightMultiplier, justifications);
    }

    @Override
    public void impactScore(RuleContext kcontext, long weightMultiplier, Object... justifications) {
        throw new UnsupportedOperationException("In the rule (" + kcontext.getRule().getName()
                + "), the scoreHolder class (" + getClass()
                + ") does not support a long weightMultiplier (" + weightMultiplier + ").\n"
                + "If you're using constraint streams, maybe switch from penalizeLong() to penalize().");
    }

    @Override
    public void impactScore(RuleContext kcontext, BigDecimal weightMultiplier, Object... justifications) {
        throw new UnsupportedOperationException("In the rule (" + kcontext.getRule().getName()
                + "), the scoreHolder class (" + getClass()
                + ") does not support a BigDecimal weightMultiplier (" + weightMultiplier + ").\n"
                + "If you're using constraint streams, maybe switch from penalizeBigDecimal() to penalize().");
    }

    private void impactScore(RuleContext kcontext, int hardWeightMultiplier, int softWeightMultiplier) {
        Rule rule = kcontext.getRule();
        ScoreMatchExecutor<HardSoftScore> matchExecutor = matchExecutorByScoreMap.get(rule);
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

    @Override
    public void addHardConstraintMatch(RuleContext kcontext, int hardWeight) {
        addHardConstraintMatch(kcontext, hardWeight, EMPTY_OBJECT_ARRAY);
    }

    private void addHardConstraintMatch(RuleContext kcontext, int hardWeight, Object... justifications) {
        hardScore += hardWeight;
        registerConstraintMatch(kcontext, () -> hardScore -= hardWeight, () -> HardSoftScore.ofHard(hardWeight),
                justifications);
    }

    @Override
    public void addSoftConstraintMatch(RuleContext kcontext, int softWeight) {
        addSoftConstraintMatch(kcontext, softWeight, EMPTY_OBJECT_ARRAY);
    }

    private void addSoftConstraintMatch(RuleContext kcontext, int softWeight, Object... justifications) {
        softScore += softWeight;
        registerConstraintMatch(kcontext, () -> softScore -= softWeight, () -> HardSoftScore.ofSoft(softWeight),
                justifications);
    }

    @Override
    public void addMultiConstraintMatch(RuleContext kcontext, int hardWeight, int softWeight) {
        addMultiConstraintMatch(kcontext, hardWeight, softWeight, EMPTY_OBJECT_ARRAY);
    }

    private void addMultiConstraintMatch(RuleContext kcontext, int hardWeight, int softWeight, Object... justifications) {
        hardScore += hardWeight;
        softScore += softWeight;
        registerConstraintMatch(kcontext,
                () -> {
                    hardScore -= hardWeight;
                    softScore -= softWeight;
                },
                () -> HardSoftScore.of(hardWeight, softWeight),
                justifications);
    }

    @Override
    public HardSoftScore extractScore(int initScore) {
        return HardSoftScore.ofUninitialized(initScore, hardScore, softScore);
    }

}
