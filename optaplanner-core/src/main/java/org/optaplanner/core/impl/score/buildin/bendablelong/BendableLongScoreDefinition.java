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

    @Override
    public Class<BendableLongScore> getScoreClass() {
        return BendableLongScore.class;
    }

    @Override
    public BendableLongScore getZeroScore() {
        return BendableLongScore.zero(hardLevelsSize, softLevelsSize);
    }

    @Override
    public BendableLongScore parseScore(String scoreString) {
        BendableLongScore score = BendableLongScore.parseScore(scoreString);
        if (score.getHardLevelsSize() != hardLevelsSize) {
            throw new IllegalArgumentException("The scoreString (" + scoreString
                    + ") for the scoreClass (" + BendableLongScore.class.getSimpleName()
                    + ") doesn't follow the correct pattern:"
                    + " the hardLevelsSize (" + score.getHardLevelsSize()
                    + ") doesn't match the scoreDefinition's hardLevelsSize (" + hardLevelsSize + ").");
        }
        if (score.getSoftLevelsSize() != softLevelsSize) {
            throw new IllegalArgumentException("The scoreString (" + scoreString
                    + ") for the scoreClass (" + BendableLongScore.class.getSimpleName()
                    + ") doesn't follow the correct pattern:"
                    + " the softLevelsSize (" + score.getSoftLevelsSize()
                    + ") doesn't match the scoreDefinition's softLevelsSize (" + softLevelsSize + ").");
        }
        return score;
    }

    @Override
    public BendableLongScore fromLevelNumbers(int initScore, Number[] levelNumbers) {
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
        return BendableLongScore.ofUninitialized(initScore, hardScores, softScores);
    }

    public BendableLongScore createScore(long... scores) {
        return createScoreUninitialized(0, scores);
    }

    public BendableLongScore createScoreUninitialized(int initScore, long... scores) {
        int levelsSize = hardLevelsSize + softLevelsSize;
        if (scores.length != levelsSize) {
            throw new IllegalArgumentException("The scores (" + Arrays.toString(scores)
                    + ")'s length (" + scores.length
                    + ") is not levelsSize (" + levelsSize + ").");
        }
        return BendableLongScore.ofUninitialized(initScore,
                Arrays.copyOfRange(scores, 0, hardLevelsSize),
                Arrays.copyOfRange(scores, hardLevelsSize, levelsSize));
    }

    @Override
    public BendableLongScoreInliner buildScoreInliner(boolean constraintMatchEnabled) {
        return new BendableLongScoreInliner(constraintMatchEnabled, hardLevelsSize, softLevelsSize);
    }

    @Override
    public BendableLongScoreHolder buildScoreHolder(boolean constraintMatchEnabled) {
        return new BendableLongScoreHolder(constraintMatchEnabled, hardLevelsSize, softLevelsSize);
    }

    @Override
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
        return BendableLongScore.ofUninitialized(0, hardScores, softScores);
    }

    @Override
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
        return BendableLongScore.ofUninitialized(0, hardScores, softScores);
    }

}
