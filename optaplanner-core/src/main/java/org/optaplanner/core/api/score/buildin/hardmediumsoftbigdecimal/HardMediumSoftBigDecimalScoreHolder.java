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
        super(constraintMatchEnabled);
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
     * @param weight higher is better, negative for a penalty, positive for a reward
     */
    public void addHardConstraintMatch(RuleContext kcontext, final BigDecimal weight) {
        hardScore = (hardScore == null) ? weight : hardScore.add(weight);
        registerBigDecimalConstraintMatch(kcontext, 0, weight, new BigDecimalConstraintUndoListener() {
            @Override
            public void undo() {
                hardScore = hardScore.subtract(weight);
            }
        });
    }

    /**
     * Add a medium level constraint of specified weighting.
     *
     * This is typically used in Drools scoring to add a medium priority constraint match.
     *
     * @param kcontext never null, the magic variable in DRL
     * @param weight higher is better, negative for a penalty, positive for a reward
     */
    public void addMediumConstraintMatch(RuleContext kcontext, final BigDecimal weight) {
        mediumScore = (mediumScore == null) ? weight : mediumScore.add(weight);
        registerBigDecimalConstraintMatch(kcontext, 1, weight, new BigDecimalConstraintUndoListener() {
            @Override
            public void undo() {
                mediumScore = mediumScore.subtract(weight);
            }
        });
    }

    /**
     * Add a soft constraint match of specified weighting.
     *
     * This is typically used in Drools scoring to add a low priority constraint match.
     *
     * @param kcontext never null, the magic variable in DRL
     * @param weight higher is better, negative for a penalty, positive for a reward
     */
    public void addSoftConstraintMatch(RuleContext kcontext, final BigDecimal weight) {
        softScore = (softScore == null) ? weight : softScore.add(weight);
        registerBigDecimalConstraintMatch(kcontext, 2, weight, new BigDecimalConstraintUndoListener() {
            @Override
            public void undo() {
                softScore = softScore.subtract(weight);
            }
        });
    }

    @Override
    public Score extractScore(int initScore) {
        return HardMediumSoftBigDecimalScore.valueOf(initScore,
                hardScore == null ? BigDecimal.ZERO : hardScore,
                mediumScore == null ? BigDecimal.ZERO : mediumScore,
                softScore == null ? BigDecimal.ZERO : softScore);
    }

}
