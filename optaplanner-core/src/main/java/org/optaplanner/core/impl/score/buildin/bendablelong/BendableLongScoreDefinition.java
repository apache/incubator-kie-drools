/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.core.impl.score.buildin.bendablelong;

import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScoreHolder;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.definition.AbstractFeasibilityScoreDefinition;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

import java.util.Arrays;

public class BendableLongScoreDefinition extends AbstractFeasibilityScoreDefinition<BendableLongScore> {

    private final int hardLevelsSize;
    private final int softLevelsSize;

    public BendableLongScoreDefinition(int hardLevelsSize, int softLevelsSize) {
        this.hardLevelsSize = hardLevelsSize;
        this.softLevelsSize = softLevelsSize;
    }

    public int getHardLevelsSize() {
        return hardLevelsSize;
    }

    public int getSoftLevelsSize() {
        return softLevelsSize;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public int getLevelsSize() {
        return hardLevelsSize + softLevelsSize;
    }

    @Override
    public int getFeasibleLevelsSize() {
        return hardLevelsSize;
    }

    public Class<BendableLongScore> getScoreClass() {
        return BendableLongScore.class;
    }

    public BendableLongScore parseScore(String scoreString) {
        return BendableLongScore.parseScore(hardLevelsSize, softLevelsSize, scoreString);
    }

    public BendableLongScore createScore(long... scores) {
        int levelsSize = hardLevelsSize + softLevelsSize;
        if (scores.length != levelsSize) {
            throw new IllegalArgumentException("The scores (" + Arrays.toString(scores)
                    + ")'s length (" + scores.length
                    + ") is not levelsSize (" + levelsSize + ").");
        }
        return BendableLongScore.valueOf(Arrays.copyOfRange(scores, 0, hardLevelsSize),
                Arrays.copyOfRange(scores, hardLevelsSize, levelsSize));
    }

    public BendableLongScoreHolder buildScoreHolder(boolean constraintMatchEnabled) {
        return new BendableLongScoreHolder(constraintMatchEnabled, hardLevelsSize, softLevelsSize);
    }

    public BendableLongScore buildOptimisticBound(InitializingScoreTrend initializingScoreTrend, BendableLongScore score) {
        InitializingScoreTrendLevel[] trendLevels = initializingScoreTrend.getTrendLevels();
        long[] hardScores = new long[hardLevelsSize];
        for (int i = 0; i < hardLevelsSize; i++) {
            hardScores[i] = (trendLevels[i] == InitializingScoreTrendLevel.ONLY_DOWN)
                    ? score.getHardScore(i) : Long.MAX_VALUE;
        }
        long[] softScores = new long[softLevelsSize];
        for (int i = 0; i < softLevelsSize; i++) {
            softScores[i] = (trendLevels[hardLevelsSize + i] == InitializingScoreTrendLevel.ONLY_DOWN)
                    ? score.getSoftScore(i) : Long.MAX_VALUE;
        }
        return BendableLongScore.valueOf(hardScores, softScores);
    }

    public BendableLongScore buildPessimisticBound(InitializingScoreTrend initializingScoreTrend, BendableLongScore score) {
        InitializingScoreTrendLevel[] trendLevels = initializingScoreTrend.getTrendLevels();
        long[] hardScores = new long[hardLevelsSize];
        for (int i = 0; i < hardLevelsSize; i++) {
            hardScores[i] = (trendLevels[i] == InitializingScoreTrendLevel.ONLY_UP)
                    ? score.getHardScore(i) : Long.MIN_VALUE;
        }
        long[] softScores = new long[softLevelsSize];
        for (int i = 0; i < softLevelsSize; i++) {
            softScores[i] = (trendLevels[hardLevelsSize + i] == InitializingScoreTrendLevel.ONLY_UP)
                    ? score.getSoftScore(i) : Long.MIN_VALUE;
        }
        return BendableLongScore.valueOf(hardScores, softScores);
    }

}
