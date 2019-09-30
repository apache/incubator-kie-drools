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

package org.optaplanner.core.api.score.buildin.simplebigdecimal;

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
 * @see SimpleBigDecimalScore
 */
public class SimpleBigDecimalScoreHolder extends AbstractScoreHolder<SimpleBigDecimalScore> {

    protected final Map<Rule, BiConsumer<RuleContext, BigDecimal>> matchExecutorByNumberMap = new LinkedHashMap<>();

    protected BigDecimal score = BigDecimal.ZERO;

    public SimpleBigDecimalScoreHolder(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled, SimpleBigDecimalScore.ZERO);
    }

    public BigDecimal getScore() {
        return score;
    }

    // ************************************************************************
    // Setup methods
    // ************************************************************************

    @Override
    public void configureConstraintWeight(Rule rule, SimpleBigDecimalScore constraintWeight) {
        super.configureConstraintWeight(rule, constraintWeight);
        BiConsumer<RuleContext, BigDecimal> matchExecutor;
        if (constraintWeight.equals(SimpleBigDecimalScore.ZERO)) {
            matchExecutor = (RuleContext kcontext, BigDecimal matchWeight) -> {};
        } else {
            matchExecutor = (RuleContext kcontext, BigDecimal matchWeight)
                    -> addConstraintMatch(kcontext, constraintWeight.getScore().multiply(matchWeight));
        }
        matchExecutorByNumberMap.put(rule, matchExecutor);
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

    // ************************************************************************
    // Other match methods
    // ************************************************************************

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param weight never null, higher is better, negative for a penalty, positive for a reward
     */
    public void addConstraintMatch(RuleContext kcontext, BigDecimal weight) {
        score = score.add(weight);
        registerConstraintMatch(kcontext,
                () -> score = score.subtract(weight),
                () -> SimpleBigDecimalScore.of(weight));
    }

    @Override
    public SimpleBigDecimalScore extractScore(int initScore) {
        return SimpleBigDecimalScore.ofUninitialized(initScore, score);
    }

}
