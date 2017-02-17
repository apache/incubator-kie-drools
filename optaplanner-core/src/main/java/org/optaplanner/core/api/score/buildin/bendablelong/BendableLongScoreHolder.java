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

package org.optaplanner.core.api.score.buildin.bendablelong;

import java.util.Arrays;

import org.kie.api.runtime.rule.RuleContext;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.holder.AbstractScoreHolder;

/**
 * @see BendableLongScore
 */
public class BendableLongScoreHolder extends AbstractScoreHolder {

    private long[] hardScores;
    private long[] softScores;

    public BendableLongScoreHolder(boolean constraintMatchEnabled, int hardLevelsSize, int softLevelsSize) {
        super(constraintMatchEnabled, BendableLongScore.zero(hardLevelsSize, softLevelsSize));
        hardScores = new long[hardLevelsSize];
        softScores = new long[softLevelsSize];
    }

    public int getHardLevelsSize() {
        return hardScores.length;
    }

    public long getHardScore(int hardLevel) {
        return hardScores[hardLevel];
    }

    public int getSoftLevelsSize() {
        return softScores.length;
    }

    public long getSoftScore(int softLevel) {
        return softScores[softLevel];
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param hardLevel {@code 0 <= hardLevel <} {@link #getHardLevelsSize()}.
     * The {@code scoreLevel} is {@code hardLevel} for hard levels and {@code softLevel + hardLevelSize} for soft levels.
     * @param weight higher is better, negative for a penalty, positive for a reward
     */
    public void addHardConstraintMatch(RuleContext kcontext, final int hardLevel, final long weight) {
        hardScores[hardLevel] += weight;
        registerConstraintMatch(kcontext,
                () -> hardScores[hardLevel] -= weight,
                () -> {
                    long[] newHardScores = new long[hardScores.length];
                    long[] newSoftScores = new long[softScores.length];
                    newHardScores[hardLevel] = weight;
                    return BendableLongScore.valueOf(newHardScores, newSoftScores);
                });
    }

    /**
     * @param kcontext never null, the magic variable in DRL
     * @param softLevel {@code 0 <= softLevel <} {@link #getSoftLevelsSize()}.
     * The {@code scoreLevel} is {@code hardLevel} for hard levels and {@code softLevel + hardLevelSize} for soft levels.
     * @param weight higher is better, negative for a penalty, positive for a reward
     */
    public void addSoftConstraintMatch(RuleContext kcontext, final int softLevel, final long weight) {
        softScores[softLevel] += weight;
        registerConstraintMatch(kcontext,
                () -> softScores[softLevel] -= weight,
                () -> {
                    long[] newHardScores = new long[hardScores.length];
                    long[] newSoftScores = new long[softScores.length];
                    newSoftScores[softLevel] = weight;
                    return BendableLongScore.valueOf(newHardScores, newSoftScores);
                });
    }

    @Override
    public Score extractScore(int initScore) {
        return new BendableLongScore(initScore,
                Arrays.copyOf(hardScores, hardScores.length),
                Arrays.copyOf(softScores, softScores.length));
    }

}
