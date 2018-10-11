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

package org.optaplanner.core.api.score.buildin.hardsoftdouble;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScoreHolder;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;

/**
 * WARNING: NOT RECOMMENDED TO USE DUE TO ROUNDING ERRORS THAT CAUSE SCORE CORRUPTION.
 * Use {@link HardSoftBigDecimalScoreHolder} instead.
 * @see HardSoftDoubleScore
 */
public class HardSoftDoubleScoreHolder extends AbstractScoreHolder<HardSoftDoubleScore> {

    protected final Map<Rule, BiConsumer<RuleContext, Double>> matchExecutorMap = new LinkedHashMap<>();

    protected double hardScore;
    protected double softScore;

    public HardSoftDoubleScoreHolder(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled, HardSoftDoubleScore.ZERO);
    }

    public double getHardScore() {
        return hardScore;
    }

    public double getSoftScore() {
        return softScore;
    }

    // ************************************************************************
    // Setup methods
    // ************************************************************************

    @Override
    public void putConstraintWeight(Rule rule, HardSoftDoubleScore constraintWeight) {
        BiConsumer<RuleContext, Double> matchExecutor;
        if (constraintWeight.equals(HardSoftDoubleScore.ZERO)) {
            matchExecutor = (RuleContext kcontext, Double matchWeight) -> {};
        } else if (constraintWeight.getInitScore() != 0) {
            throw new IllegalStateException("The initScore (" + constraintWeight.getInitScore() + ") must be 0.");
        } else if (constraintWeight.getSoftScore() == 0.0) {
            matchExecutor = (RuleContext kcontext, Double matchWeight)
                    -> addHardConstraintMatch(kcontext, constraintWeight.getHardScore() * matchWeight);
        } else if (constraintWeight.getHardScore() == 0.0) {
            matchExecutor = (RuleContext kcontext, Double matchWeight)
                    -> addSoftConstraintMatch(kcontext, constraintWeight.getSoftScore() * matchWeight);
        } else {
            matchExecutor = (RuleContext kcontext, Double matchWeight)
                    -> addMultiConstraintMatch(kcontext,
                    constraintWeight.getHardScore() * matchWeight,
                    constraintWeight.getSoftScore() * matchWeight);
        }
        matchExecutorMap.put(rule, matchExecutor);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeight higher is better, negative for a penalty, positive for a reward
     */
    public void addHardConstraintMatch(RuleContext kcontext, double hardWeight) {
        hardScore += hardWeight;
        registerConstraintMatch(kcontext,
                () -> hardScore -= hardWeight,
                () -> HardSoftDoubleScore.of(hardWeight, 0.0));
    }

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param softWeight higher is better, negative for a penalty, positive for a reward
     */
    public void addSoftConstraintMatch(RuleContext kcontext, double softWeight) {
        softScore += softWeight;
        registerConstraintMatch(kcontext,
                () -> softScore -= softWeight,
                () -> HardSoftDoubleScore.of(0.0, softWeight));
    }

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeight higher is better, negative for a penalty, positive for a reward
     * @param softWeight higher is better, negative for a penalty, positive for a reward
     */
    public void addMultiConstraintMatch(RuleContext kcontext, double hardWeight, double softWeight) {
        hardScore += hardWeight;
        softScore += softWeight;
        registerConstraintMatch(kcontext,
                () -> {
                    hardScore -= hardWeight;
                    softScore -= softWeight;
                },
                () -> HardSoftDoubleScore.of(hardWeight, softWeight));
    }

    @Override
    public HardSoftDoubleScore extractScore(int initScore) {
        return HardSoftDoubleScore.ofUninitialized(initScore, hardScore, softScore);
    }

}
