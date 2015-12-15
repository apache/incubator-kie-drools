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

package org.optaplanner.core.api.score.buildin.bendablebigdecimal;

import java.math.BigDecimal;
import java.util.Arrays;

import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;

/**
 * @see BendableBigDecimalScore
 */
public class BendableBigDecimalScoreHolder extends AbstractScoreHolder {

    private BigDecimal[] hardScores;
    private BigDecimal[] softScores;

    public BendableBigDecimalScoreHolder(boolean constraintMatchEnabled, int hardLevelsSize, int softLevelsSize) {
        super(constraintMatchEnabled);
        hardScores = new BigDecimal[hardLevelsSize];
        Arrays.fill(hardScores, BigDecimal.ZERO);
        softScores = new BigDecimal[softLevelsSize];
        Arrays.fill(softScores, BigDecimal.ZERO);
    }

    public int getHardLevelsSize() {
        return hardScores.length;
    }

    public BigDecimal getHardScore(int hardLevel) {
        return hardScores[hardLevel];
    }

    @Deprecated
    public void setHardScore(int hardLevel, BigDecimal hardScore) {
        hardScores[hardLevel] = hardScore;
    }

    public int getSoftLevelsSize() {
        return softScores.length;
    }

    public BigDecimal getSoftScore(int softLevel) {
        return softScores[softLevel];
    }

    @Deprecated
    public void setSoftScore(int softLevel, BigDecimal softScore) {
        softScores[softLevel] = softScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public void addHardConstraintMatch(RuleContext kcontext, final int hardLevel, final BigDecimal weight) {
        hardScores[hardLevel] = hardScores[hardLevel].add(weight);
        registerBigDecimalConstraintMatch(kcontext, hardLevel, weight, new BigDecimalConstraintUndoListener() {
            public void undo() {
                hardScores[hardLevel] = hardScores[hardLevel].subtract(weight);
            }
        });
    }

    public void addSoftConstraintMatch(RuleContext kcontext, final int softLevel, final BigDecimal weight) {
        softScores[softLevel] = softScores[softLevel].add(weight);
        registerBigDecimalConstraintMatch(kcontext, getHardLevelsSize() + softLevel, weight, new BigDecimalConstraintUndoListener() {
            public void undo() {
                softScores[softLevel] = softScores[softLevel].subtract(weight);
            }
        });
    }

    public Score extractScore() {
        return new BendableBigDecimalScore(Arrays.copyOf(hardScores, hardScores.length),
                Arrays.copyOf(softScores, softScores.length));
    }

}
