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

package org.optaplanner.core.impl.score.buildin.hardmediumsoftlong;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreHolder;
import org.optaplanner.core.impl.score.holder.AbstractScoreHolder;

/**
 * @see HardMediumSoftScore
 */
public final class HardMediumSoftLongScoreHolderImpl extends AbstractScoreHolder<HardMediumSoftLongScore>
        implements HardMediumSoftLongScoreHolder {

    protected final Map<Rule, LongMatchExecutor> matchExecutorByNumberMap = new LinkedHashMap<>();
    /** Slower than {@link #matchExecutorByNumberMap} */
    protected final Map<Rule, ScoreMatchExecutor<HardMediumSoftLongScore>> matchExecutorByScoreMap = new LinkedHashMap<>();

    protected long hardScore;
    protected long mediumScore;
    protected long softScore;

    public HardMediumSoftLongScoreHolderImpl(boolean constraintMatchEnabled) {
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
        LongMatchExecutor matchExecutor;
        if (constraintWeight.equals(HardMediumSoftLongScore.ZERO)) {
            matchExecutor = (RuleContext kcontext, long matchWeight, Object... justifications) -> {
            };
        } else if (constraintWeight.getMediumScore() == 0 && constraintWeight.getSoftScore() == 0) {
            matchExecutor =
                    (RuleContext kcontext, long matchWeight, Object... justifications) -> addHardConstraintMatch(kcontext,
                            constraintWeight.getHardScore() * matchWeight, justifications);
        } else if (constraintWeight.getHardScore() == 0 && constraintWeight.getSoftScore() == 0) {
            matchExecutor =
                    (RuleContext kcontext, long matchWeight, Object... justifications) -> addMediumConstraintMatch(kcontext,
                            constraintWeight.getMediumScore() * matchWeight, justifications);
        } else if (constraintWeight.getHardScore() == 0 && constraintWeight.getMediumScore() == 0) {
            matchExecutor =
                    (RuleContext kcontext, long matchWeight, Object... justifications) -> addSoftConstraintMatch(kcontext,
                            constraintWeight.getSoftScore() * matchWeight, justifications);
        } else {
            matchExecutor =
                    (RuleContext kcontext, long matchWeight, Object... justifications) -> addMultiConstraintMatch(kcontext,
                            constraintWeight.getHardScore() * matchWeight, constraintWeight.getMediumScore() * matchWeight,
                            constraintWeight.getSoftScore() * matchWeight, justifications);
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

    @Override
    public void penalize(RuleContext kcontext) {
        impactScore(kcontext, -1L);
    }

    @Override
    public void penalize(RuleContext kcontext, long weightMultiplier) {
        impactScore(kcontext, -weightMultiplier);
    }

    @Override
    public void penalize(RuleContext kcontext, long hardWeightMultiplier, long mediumWeightMultiplier,
            long softWeightMultiplier) {
        impactScore(kcontext, -hardWeightMultiplier, -mediumWeightMultiplier, -softWeightMultiplier);
    }

    @Override
    public void reward(RuleContext kcontext) {
        impactScore(kcontext, 1L);
    }

    @Override
    public void reward(RuleContext kcontext, long weightMultiplier) {
        impactScore(kcontext, weightMultiplier);
    }

    @Override
    public void reward(RuleContext kcontext, long hardWeightMultiplier, long mediumWeightMultiplier,
            long softWeightMultiplier) {
        impactScore(kcontext, hardWeightMultiplier, mediumWeightMultiplier, softWeightMultiplier);
    }

    @Override
    public void impactScore(RuleContext kcontext, Object... justifications) {
        impactScore(kcontext, 1L, justifications);
    }

    @Override
    public void impactScore(RuleContext kcontext, int weightMultiplier, Object... justifications) {
        impactScore(kcontext, (long) weightMultiplier, justifications);
    }

    @Override
    public void impactScore(RuleContext kcontext, long weightMultiplier, Object... justifications) {
        Rule rule = kcontext.getRule();
        LongMatchExecutor matchExecutor = matchExecutorByNumberMap.get(rule);
        if (matchExecutor == null) {
            throw new IllegalStateException("The DRL rule (" + rule.getPackageName() + ":" + rule.getName()
                    + ") does not match a @" + ConstraintWeight.class.getSimpleName() + " on the @"
                    + ConstraintConfiguration.class.getSimpleName() + " annotated class.");
        }
        matchExecutor.accept(kcontext, weightMultiplier, justifications);
    }

    @Override
    public void impactScore(RuleContext kcontext, BigDecimal weightMultiplier, Object... justifications) {
        throw new UnsupportedOperationException("In the rule (" + kcontext.getRule().getName()
                + "), the scoreHolder class (" + getClass()
                + ") does not support a BigDecimal weightMultiplier (" + weightMultiplier + ").\n"
                + "If you're using constraint streams, maybe switch from penalizeBigDecimal() to penalizeLong().");
    }

    private void impactScore(RuleContext kcontext, long hardWeightMultiplier, long mediumWeightMultiplier,
            long softWeightMultiplier) {
        Rule rule = kcontext.getRule();
        ScoreMatchExecutor<HardMediumSoftLongScore> matchExecutor = matchExecutorByScoreMap.get(rule);
        if (matchExecutor == null) {
            throw new IllegalStateException("The DRL rule (" + rule.getPackageName() + ":" + rule.getName()
                    + ") does not match a @" + ConstraintWeight.class.getSimpleName() + " on the @"
                    + ConstraintConfiguration.class.getSimpleName() + " annotated class.");
        }
        matchExecutor.accept(kcontext,
                HardMediumSoftLongScore.of(hardWeightMultiplier, mediumWeightMultiplier, softWeightMultiplier));
    }

    // ************************************************************************
    // Other match methods
    // ************************************************************************

    @Override
    public void addHardConstraintMatch(RuleContext kcontext, long hardWeight) {
        addHardConstraintMatch(kcontext, hardWeight, EMPTY_OBJECT_ARRAY);
    }

    private void addHardConstraintMatch(RuleContext kcontext, long hardWeight, Object... justifications) {
        hardScore += hardWeight;
        registerConstraintMatch(kcontext, () -> hardScore -= hardWeight, () -> HardMediumSoftLongScore.ofHard(hardWeight),
                justifications);
    }

    @Override
    public void addMediumConstraintMatch(RuleContext kcontext, long mediumWeight) {
        addMediumConstraintMatch(kcontext, mediumWeight, EMPTY_OBJECT_ARRAY);
    }

    private void addMediumConstraintMatch(RuleContext kcontext, long mediumWeight, Object... justifications) {
        mediumScore += mediumWeight;
        registerConstraintMatch(kcontext, () -> mediumScore -= mediumWeight,
                () -> HardMediumSoftLongScore.ofMedium(mediumWeight), justifications);
    }

    @Override
    public void addSoftConstraintMatch(RuleContext kcontext, long softWeight) {
        addSoftConstraintMatch(kcontext, softWeight, EMPTY_OBJECT_ARRAY);
    }

    private void addSoftConstraintMatch(RuleContext kcontext, long softWeight, Object... justifications) {
        softScore += softWeight;
        registerConstraintMatch(kcontext, () -> softScore -= softWeight, () -> HardMediumSoftLongScore.ofSoft(softWeight),
                justifications);
    }

    @Override
    public void addMultiConstraintMatch(RuleContext kcontext, long hardWeight, long mediumWeight, long softWeight) {
        addMultiConstraintMatch(kcontext, hardWeight, mediumWeight, softWeight, EMPTY_OBJECT_ARRAY);
    }

    private void addMultiConstraintMatch(RuleContext kcontext, long hardWeight, long mediumWeight, long softWeight,
            Object... justifications) {
        hardScore += hardWeight;
        mediumScore += mediumWeight;
        softScore += softWeight;
        registerConstraintMatch(kcontext,
                () -> {
                    hardScore -= hardWeight;
                    mediumScore -= mediumWeight;
                    softScore -= softWeight;
                },
                () -> HardMediumSoftLongScore.of(hardWeight, mediumWeight, softWeight),
                justifications);
    }

    @Override
    public HardMediumSoftLongScore extractScore(int initScore) {
        return HardMediumSoftLongScore.ofUninitialized(initScore, hardScore, mediumScore, softScore);
    }

}
