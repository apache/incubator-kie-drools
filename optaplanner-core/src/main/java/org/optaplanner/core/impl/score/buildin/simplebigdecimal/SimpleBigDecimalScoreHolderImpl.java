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

package org.optaplanner.core.impl.score.buildin.simplebigdecimal;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.domain.constraintweight.ConstraintConfiguration;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScoreHolder;
import org.optaplanner.core.impl.score.holder.AbstractScoreHolder;

/**
 * @see SimpleBigDecimalScore
 */
public final class SimpleBigDecimalScoreHolderImpl extends AbstractScoreHolder<SimpleBigDecimalScore>
        implements SimpleBigDecimalScoreHolder {

    protected final Map<Rule, BigDecimalMatchExecutor> matchExecutorByNumberMap = new LinkedHashMap<>();

    protected BigDecimal score = BigDecimal.ZERO;

    public SimpleBigDecimalScoreHolderImpl(boolean constraintMatchEnabled) {
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
        BigDecimalMatchExecutor matchExecutor;
        if (constraintWeight.equals(SimpleBigDecimalScore.ZERO)) {
            matchExecutor = (RuleContext kcontext, BigDecimal matchWeight, Object... justifications) -> {
            };
        } else {
            matchExecutor =
                    (RuleContext kcontext, BigDecimal matchWeight, Object... justifications) -> addConstraintMatch(kcontext,
                            constraintWeight.getScore().multiply(matchWeight), justifications);
        }
        matchExecutorByNumberMap.put(rule, matchExecutor);
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
    public void reward(RuleContext kcontext) {
        impactScore(kcontext, BigDecimal.ONE);
    }

    @Override
    public void reward(RuleContext kcontext, BigDecimal weightMultiplier) {
        impactScore(kcontext, weightMultiplier);
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

    // ************************************************************************
    // Other match methods
    // ************************************************************************

    @Override
    public void addConstraintMatch(RuleContext kcontext, BigDecimal weight) {
        addConstraintMatch(kcontext, weight, EMPTY_OBJECT_ARRAY);
    }

    private void addConstraintMatch(RuleContext kcontext, BigDecimal weight, Object... justifications) {
        score = score.add(weight);
        registerConstraintMatch(kcontext, () -> score = score.subtract(weight), () -> SimpleBigDecimalScore.of(weight),
                justifications);
    }

    @Override
    public SimpleBigDecimalScore extractScore(int initScore) {
        return SimpleBigDecimalScore.ofUninitialized(initScore, score);
    }

}
