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
        super(constraintMatchEnabled, BendableBigDecimalScore.zero(hardLevelsSize, softLevelsSize));
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

    public int getSoftLevelsSize() {
        return softScores.length;
    }

    public BigDecimal getSoftScore(int softLevel) {
        return softScores[softLevel];
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param hardLevel {@code 0 <= hardLevel <} {@link #getHardLevelsSize()}.
     * The {@code scoreLevel} is {@code hardLevel} for hard levels and {@code softLevel + hardLevelSize} for soft levels.
     * @param weight never null, higher is better, negative for a penalty, positive for a reward
     */
    public void addHardConstraintMatch(RuleContext kcontext, int hardLevel, BigDecimal weight) {
        hardScores[hardLevel] = hardScores[hardLevel].add(weight);
        registerConstraintMatch(kcontext,
                () -> hardScores[hardLevel] = hardScores[hardLevel].subtract(weight),
                () -> {
                    BigDecimal[] newHardScores = new BigDecimal[hardScores.length];
                    Arrays.fill(newHardScores, BigDecimal.ZERO);
                    BigDecimal[] newSoftScores = new BigDecimal[softScores.length];
                    Arrays.fill(newSoftScores, BigDecimal.ZERO);
                    newHardScores[hardLevel] = weight;
                    return BendableBigDecimalScore.valueOf(newHardScores, newSoftScores);
                });
    }

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param softLevel {@code 0 <= softLevel <} {@link #getSoftLevelsSize()}.
     * The {@code scoreLevel} is {@code hardLevel} for hard levels and {@code softLevel + hardLevelSize} for soft levels.
     * @param weight never null, higher is better, negative for a penalty, positive for a reward
     */
    public void addSoftConstraintMatch(RuleContext kcontext, int softLevel, BigDecimal weight) {
        softScores[softLevel] = softScores[softLevel].add(weight);
        registerConstraintMatch(kcontext,
                () -> softScores[softLevel] = softScores[softLevel].subtract(weight),
                () -> {
                    BigDecimal[] newHardScores = new BigDecimal[hardScores.length];
                    Arrays.fill(newHardScores, BigDecimal.ZERO);
                    BigDecimal[] newSoftScores = new BigDecimal[softScores.length];
                    Arrays.fill(newSoftScores, BigDecimal.ZERO);
                    newSoftScores[softLevel] = weight;
                    return BendableBigDecimalScore.valueOf(newHardScores, newSoftScores);
                });
    }

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param hardWeights never null, array of length {@link #getHardLevelsSize()}, does not contain any nulls
     * @param softWeights never null, array of length {@link #getSoftLevelsSize()}, does not contain any nulls
     */
    public void addMultiConstraintMatch(RuleContext kcontext, BigDecimal[] hardWeights, BigDecimal[] softWeights) {
        if (hardScores.length != hardWeights.length) {
            throw new IllegalArgumentException("The hardScores length (" + hardScores.length
                    + ") is different than the hardWeights length (" + hardWeights.length + ").");
        }
        for (int i = 0; i < hardScores.length; i++) {
            hardScores[i] = hardScores[i].add(hardWeights[i]);
        }
        if (softScores.length != softWeights.length) {
            throw new IllegalArgumentException("The softScores length (" + softScores.length
                    + ") is different than the softWeights length (" + softWeights.length + ").");
        }
        for (int i = 0; i < softScores.length; i++) {
            softScores[i] = softScores[i].add(softWeights[i]);
        }
        registerConstraintMatch(kcontext,
                () -> {
                    for (int i = 0; i < hardScores.length; i++) {
                        hardScores[i] = hardScores[i].subtract(hardWeights[i]);
                    }
                    for (int i = 0; i < softScores.length; i++) {
                        softScores[i] = softScores[i].subtract(softWeights[i]);
                    }
                },
                () -> BendableBigDecimalScore.valueOf(hardWeights, softWeights));
    }

    @Override
    public Score extractScore(int initScore) {
        return new BendableBigDecimalScore(initScore,
                Arrays.copyOf(hardScores, hardScores.length),
                Arrays.copyOf(softScores, softScores.length));
    }

}
