/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.buildin.hardmediumsoft;

import java.util.Map;
import java.util.function.BiConsumer;

import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.domain.parametrization.PlanningParameter;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;

/**
 * @see HardMediumSoftScore
 */
public class HardMediumSoftScoreHolder extends AbstractScoreHolder {

    /**
     * @param weightScore with {@link Score#getInitScore()} equal to 0.
     * @return never null
     */
    public BiConsumer<RuleContext, Integer> createParametrizedMatchExecutor(HardMediumSoftScore weightScore) {
        if (weightScore.equals(HardMediumSoftScore.ZERO)) {
            return (RuleContext kcontext, Integer matchWeight) -> {};
        } else if (weightScore.getInitScore() != 0) {
            throw new IllegalStateException("The initScore (" + weightScore.getInitScore() + ") must be 0.");
        } else if (weightScore.getMediumScore() == 0 && weightScore.getSoftScore() == 0) {
            return (RuleContext kcontext, Integer matchWeight)
                    -> addHardConstraintMatch(kcontext, weightScore.getHardScore() * matchWeight);
        } else if (weightScore.getHardScore() == 0 && weightScore.getSoftScore() == 0) {
            return (RuleContext kcontext, Integer matchWeight)
                    -> addMediumConstraintMatch(kcontext, weightScore.getMediumScore() * matchWeight);
        } else if (weightScore.getHardScore() == 0 && weightScore.getMediumScore() == 0) {
            return (RuleContext kcontext, Integer matchWeight)
                    -> addSoftConstraintMatch(kcontext, weightScore.getSoftScore() * matchWeight);
        } else {
            return (RuleContext kcontext, Integer matchWeight)
                    -> addMultiConstraintMatch(kcontext,
                            weightScore.getHardScore() * matchWeight,
                            weightScore.getMediumScore() * matchWeight,
                            weightScore.getSoftScore() * matchWeight);
        }
    }

    protected Map<Rule, BiConsumer<RuleContext, Integer>> parametrizedMatchMap;

    protected int hardScore;
    protected int mediumScore;
    protected int softScore;

    public HardMediumSoftScoreHolder(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled, HardMediumSoftScore.ZERO);
    }

    public int getHardScore() {
        return hardScore;
    }

    public int getMediumScore() {
        return mediumScore;
    }

    public int getSoftScore() {
        return softScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeight higher is better, negative for a penalty, positive for a reward
     */
    public void addHardConstraintMatch(RuleContext kcontext, int hardWeight) {
        hardScore += hardWeight;
        registerConstraintMatch(kcontext,
                () -> hardScore -= hardWeight,
                () -> HardMediumSoftScore.of(hardWeight, 0, 0));
    }

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param mediumWeight higher is better, negative for a penalty, positive for a reward
     */
    public void addMediumConstraintMatch(RuleContext kcontext, int mediumWeight) {
        mediumScore += mediumWeight;
        registerConstraintMatch(kcontext,
                () -> mediumScore -= mediumWeight,
                () -> HardMediumSoftScore.of(0, mediumWeight, 0));
    }

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param softWeight higher is better, negative for a penalty, positive for a reward
     */
    public void addSoftConstraintMatch(RuleContext kcontext, int softWeight) {
        softScore += softWeight;
        registerConstraintMatch(kcontext,
                () -> softScore -= softWeight,
                () -> HardMediumSoftScore.of(0, 0, softWeight));
    }

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeight higher is better, negative for a penalty, positive for a reward
     * @param mediumWeight higher is better, negative for a penalty, positive for a reward
     * @param softWeight higher is better, negative for a penalty, positive for a reward
     */
    public void addMultiConstraintMatch(RuleContext kcontext, int hardWeight, int mediumWeight, int softWeight) {
        hardScore += hardWeight;
        mediumScore += mediumWeight;
        softScore += softWeight;
        registerConstraintMatch(kcontext,
                () -> {
                    hardScore -= hardWeight;
                    mediumScore -= mediumWeight;
                    softScore -= softWeight;
                },
                () -> HardMediumSoftScore.of(hardWeight, mediumWeight, softWeight));
    }

    public void match(RuleContext kcontext) {
        match(kcontext, 1);
    }

    public void match(RuleContext kcontext, int matchWeight) {
        Rule rule = kcontext.getRule();
        BiConsumer<RuleContext, Integer> consumer = parametrizedMatchMap.get(rule);
        if (consumer == null) {
            throw new IllegalStateException("The DRL rule (" + rule.getPackageName() + ":" + rule.getName()
                    + ") does not match a @" + PlanningParameter.class.getSimpleName() + " on the @"
                    + PlanningParameter.class.getSimpleName() + " annotated class.");
        }
        consumer.accept(kcontext, matchWeight);
    }

    public void match(RuleContext kcontext, int hardMatchWeight, int mediumMatchWeight, int softMatchWeight) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public HardMediumSoftScore extractScore(int initScore) {
        return HardMediumSoftScore.ofUninitialized(initScore, hardScore, mediumScore, softScore);
    }

}
