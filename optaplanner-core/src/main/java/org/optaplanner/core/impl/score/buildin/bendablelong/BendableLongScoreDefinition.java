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

package org.optaplanner.core.impl.score.buildin.bendablelong;

import java.util.Arrays;

import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScoreHolder;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.definition.AbstractBendableScoreDefinition;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

public class BendableLongScoreDefinition extends AbstractBendableScoreDefinition<BendableLongScore> {

    public BendableLongScoreDefinition(int hardLevelsSize, int softLevelsSize) {
        super(hardLevelsSize, softLevelsSize);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public Class<BendableLongScore> getScoreClass() {
        return BendableLongScore.class;
    }

    public BendableLongScore parseScore(String scoreString) {
        return BendableLongScore.parseScore(hardLevelsSize, softLevelsSize, scoreString);
    }

    @Override
    public BendableLongScore fromLevelNumbers(Number[] levelNumbers) {
        if (levelNumbers.length != getLevelsSize()) {
            throw new IllegalStateException("The levelNumbers (" + Arrays.toString(levelNumbers)
                    + ")'s length (" + levelNumbers.length + ") must equal the levelSize (" + getLevelsSize() + ").");
        }
        long[] hardScores = new long[hardLevelsSize];
        for (int i = 0; i < hardLevelsSize; i++) {
            hardScores[i] = (Long) levelNumbers[i];
        }
        long[] softScores = new long[softLevelsSize];
        for (int i = 0; i < softLevelsSize; i++) {
            softScores[i] = (Long) levelNumbers[hardLevelsSize + i];
        }
        return BendableLongScore.valueOf(hardScores, softScores);
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
