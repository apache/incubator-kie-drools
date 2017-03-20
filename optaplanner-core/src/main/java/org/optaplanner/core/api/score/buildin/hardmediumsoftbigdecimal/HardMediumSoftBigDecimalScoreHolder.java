/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal;

import java.math.BigDecimal;

import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;

/**
 * @see HardMediumSoftBigDecimalScore
 */
public class HardMediumSoftBigDecimalScoreHolder extends AbstractScoreHolder {

    protected BigDecimal hardScore;
    protected BigDecimal mediumScore;
    protected BigDecimal softScore;

    public HardMediumSoftBigDecimalScoreHolder(boolean constraintMatchEnabled) {
        super(constraintMatchEnabled, HardMediumSoftBigDecimalScore.ZERO);
    }

    public BigDecimal getHardScore() {
        return hardScore;
    }

    public BigDecimal getMediumScore() {
        return mediumScore;
    }

    public BigDecimal getSoftScore() {
        return softScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************
    /**
     * Add a hard constraint of specified weighting.
     *
     * This is typically used in Drools scoring to add a hard constraint match (negative value to indicate an infeasible
     * solution).
     *
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeight never null, higher is better, negative for a penalty, positive for a reward
     */
    public void addHardConstraintMatch(RuleContext kcontext, BigDecimal hardWeight) {
        hardScore = (hardScore == null) ? hardWeight : hardScore.add(hardWeight);
        registerConstraintMatch(kcontext,
                () -> hardScore = hardScore.subtract(hardWeight),
                () -> HardMediumSoftBigDecimalScore.valueOf(hardWeight, BigDecimal.ZERO, BigDecimal.ZERO));
    }

    /**
     * Add a medium level constraint of specified weighting.
     *
     * This is typically used in Drools scoring to add a medium priority constraint match.
     *
     * @param kcontext never null, the magic variable in DRL
     * @param mediumWeight never null, higher is better, negative for a penalty, positive for a reward
     */
    public void addMediumConstraintMatch(RuleContext kcontext, BigDecimal mediumWeight) {
        mediumScore = (mediumScore == null) ? mediumWeight : mediumScore.add(mediumWeight);
        registerConstraintMatch(kcontext,
                () -> mediumScore = mediumScore.subtract(mediumWeight),
                () -> HardMediumSoftBigDecimalScore.valueOf(BigDecimal.ZERO, mediumWeight, BigDecimal.ZERO));
    }

    /**
     * Add a soft constraint match of specified weighting.
     *
     * This is typically used in Drools scoring to add a low priority constraint match.
     *
     * @param kcontext never null, the magic variable in DRL
     * @param softWeight never null, higher is better, negative for a penalty, positive for a reward
     */
    public void addSoftConstraintMatch(RuleContext kcontext, BigDecimal softWeight) {
        softScore = (softScore == null) ? softWeight : softScore.add(softWeight);
        registerConstraintMatch(kcontext,
                () -> softScore = softScore.subtract(softWeight),
                () -> HardMediumSoftBigDecimalScore.valueOf(BigDecimal.ZERO, BigDecimal.ZERO, softWeight));
    }

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeight never null, higher is better, negative for a penalty, positive for a reward
     * @param mediumWeight never null, higher is better, negative for a penalty, positive for a reward
     * @param softWeight never null, higher is better, negative for a penalty, positive for a reward
     */
    public void addMultiConstraintMatch(RuleContext kcontext, BigDecimal hardWeight, BigDecimal mediumWeight, BigDecimal softWeight) {
        hardScore = (hardScore == null) ? hardWeight : hardScore.add(hardWeight);
        mediumScore = (mediumScore == null) ? mediumWeight : mediumScore.add(mediumWeight);
        softScore = (softScore == null) ? softWeight : softScore.add(softWeight);
        registerConstraintMatch(kcontext,
                () -> {
                    hardScore = hardScore.subtract(hardWeight);
                    mediumScore = mediumScore.subtract(mediumWeight);
                    softScore = softScore.subtract(softWeight);
                },
                () -> HardMediumSoftBigDecimalScore.valueOf(hardWeight, mediumWeight, softWeight));
    }

    @Override
    public Score extractScore(int initScore) {
        return HardMediumSoftBigDecimalScore.valueOfUninitialized(initScore,
                hardScore == null ? BigDecimal.ZERO : hardScore,
                mediumScore == null ? BigDecimal.ZERO : mediumScore,
                softScore == null ? BigDecimal.ZERO : softScore);
    }

}
