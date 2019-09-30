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

package org.optaplanner.core.api.score.buildin.simpledouble;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;

/**
 * WARNING: NOT RECOMMENDED TO USE DUE TO ROUNDING ERRORS THAT CAUSE SCORE CORRUPTION.
 * Use {@link SimpleDoubleScoreHolder} instead.
 * @see SimpleDoubleScore
 */
public class SimpleDoubleScoreHolder extends AbstractScoreHolder<SimpleDoubleScore> {

    protected final Map<Rule, BiConsumer<RuleContext, Double>> matchExecutorByNumberMap = new LinkedHashMap<>();

    protected double score;

    public SimpleDoubleScoreHolder(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled, SimpleDoubleScore.ZERO);
    }

    public double getScore() {
        return score;
    }

    // ************************************************************************
    // Setup methods
    // ************************************************************************

    @Override
    public void configureConstraintWeight(Rule rule, SimpleDoubleScore constraintWeight) {
        super.configureConstraintWeight(rule, constraintWeight);
        BiConsumer<RuleContext, Double> matchExecutor;
        if (constraintWeight.equals(SimpleDoubleScore.ZERO)) {
            matchExecutor = (RuleContext kcontext, Double matchWeight) -> {};
        } else {
            matchExecutor = (RuleContext kcontext, Double matchWeight)
                    -> addConstraintMatch(kcontext, constraintWeight.getScore() * matchWeight);
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

    @Override
    public void impactScore(RuleContext kcontext) {
        impactScore(kcontext, 1.0);
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

    // ************************************************************************
    // Other match methods
    // ************************************************************************

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param weight higher is better, negative for a penalty, positive for a reward
     */
    public void addConstraintMatch(RuleContext kcontext, double weight) {
        score += weight;
        registerConstraintMatch(kcontext,
                () -> score -= weight,
                () -> SimpleDoubleScore.of(weight));
    }

    @Override
    public SimpleDoubleScore extractScore(int initScore) {
        return SimpleDoubleScore.ofUninitialized(initScore, score);
    }

}
