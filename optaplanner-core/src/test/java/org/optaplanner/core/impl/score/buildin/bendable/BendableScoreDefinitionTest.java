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

package org.optaplanner.core.impl.score.buildin.bendable;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.buildin.InitializingScoreTrendLevelFactory;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

import static org.junit.Assert.assertEquals;

public class BendableScoreDefinitionTest {

    @Test
    public void getLevelsSize() {
        assertEquals(2, new BendableScoreDefinition(1, 1).getLevelsSize());
        assertEquals(7, new BendableScoreDefinition(3, 4).getLevelsSize());
        assertEquals(7, new BendableScoreDefinition(4, 3).getLevelsSize());
        assertEquals(5, new BendableScoreDefinition(0, 5).getLevelsSize());
        assertEquals(5, new BendableScoreDefinition(5, 0).getLevelsSize());
    }

    @Test
    public void getFeasibleLevelsSize() {
        assertEquals(1, new BendableScoreDefinition(1, 1).getFeasibleLevelsSize());
        assertEquals(3, new BendableScoreDefinition(3, 4).getFeasibleLevelsSize());
        assertEquals(4, new BendableScoreDefinition(4, 3).getFeasibleLevelsSize());
        assertEquals(0, new BendableScoreDefinition(0, 5).getFeasibleLevelsSize());
        assertEquals(5, new BendableScoreDefinition(5, 0).getFeasibleLevelsSize());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createScoreWithIllegalArgument() {
        BendableScoreDefinition bendableScoreDefinition = new BendableScoreDefinition(2, 3);
        bendableScoreDefinition.createScore(1, 2, 3);
    }

    @Test
    public void createScore() {
        int hardLevelSize = 3;
        int softLevelSize = 2;
        int sum = hardLevelSize + softLevelSize;
        int[] scores = new int [sum];
        for (int i = 0; i < sum; i++) {
            scores[i] = i;
        }
        BendableScoreDefinition bendableScoreDefinition = new BendableScoreDefinition(hardLevelSize, softLevelSize);
        BendableScore bendableScore = bendableScoreDefinition.createScore(scores);
        assertEquals(hardLevelSize, bendableScore.getHardLevelsSize());
        assertEquals(softLevelSize, bendableScore.getSoftLevelsSize());
        for (int i = 0; i < sum; i++) {
            if(i < hardLevelSize) {
                assertEquals(scores[i], bendableScore.getHardScore(i));
            } else {
                assertEquals(scores[i], bendableScore.getSoftScore(i - hardLevelSize));
            }
        }
    }

    private String createStringScore(int scoreSize, int startingScore) {
        String stringScore = String.valueOf(startingScore);
        startingScore -= 1;
        for (int i = 0; i < scoreSize - 1; i++, startingScore -= 1) {
            stringScore += "/" + String.valueOf(startingScore);
        }
        return stringScore;
    }

    @Test
    public void buildOptimisticBoundUp() {
        int softScoreSize = 3;
        int hardScoreSize = 2;
        int scoreSize = softScoreSize + hardScoreSize;
        int startingScore = -1;
        BendableScore score = BendableScore.parseScore(hardScoreSize, softScoreSize, createStringScore(scoreSize, startingScore));
        InitializingScoreTrend scoreTrend = InitializingScoreTrendLevelFactory
                .createInitializingScoreTrendLevelArray(scoreSize, InitializingScoreTrendLevel.ONLY_UP);
        BendableScoreDefinition bendableScoreDefinition = new BendableScoreDefinition(hardScoreSize, softScoreSize);
        BendableScore score2 = bendableScoreDefinition.buildOptimisticBound(scoreTrend, score);
        for (int i = 0; i < score2.getHardLevelsSize() + score2.getSoftLevelsSize(); i++) {
            if(i < score2.getHardLevelsSize()) {
                assertEquals(Integer.MAX_VALUE, score2.getHardScore(i));
            } else {
                assertEquals(Integer.MAX_VALUE, score2.getSoftScore(i - score2.getHardLevelsSize()));
            }
        }
    }

    @Test
    public void buildOptimisticBoundDown() {
        int softScoreSize = 3;
        int hardScoreSize = 2;
        int scoreSize = softScoreSize + hardScoreSize;
        int startingScore = -1;
        BendableScore score = BendableScore.parseScore(hardScoreSize, softScoreSize, createStringScore(scoreSize, startingScore));
        InitializingScoreTrend scoreTrend = InitializingScoreTrendLevelFactory
                .createInitializingScoreTrendLevelArray(scoreSize, InitializingScoreTrendLevel.ONLY_DOWN);
        BendableScoreDefinition bendableScoreDefinition = new BendableScoreDefinition(hardScoreSize, softScoreSize);
        BendableScore score2 = bendableScoreDefinition.buildOptimisticBound(scoreTrend, score);
        for (int i = 0; i < score2.getHardLevelsSize() + score2.getSoftLevelsSize(); i++, startingScore -= 1) {
            if(i < score2.getHardLevelsSize()) {
                assertEquals(startingScore, score2.getHardScore(i));
            } else {
                assertEquals(startingScore, score2.getSoftScore(i - score2.getHardLevelsSize()));
            }
        }
    }

    @Test
    public void buildPessimisticBoundUp() {
        int softScoreSize = 3;
        int hardScoreSize = 2;
        int scoreSize = softScoreSize + hardScoreSize;
        int startingScore = -1;
        BendableScore score = BendableScore.parseScore(hardScoreSize, softScoreSize, createStringScore(scoreSize, startingScore));
        InitializingScoreTrend scoreTrend = InitializingScoreTrendLevelFactory
                .createInitializingScoreTrendLevelArray(scoreSize, InitializingScoreTrendLevel.ONLY_UP);
        BendableScoreDefinition bendableScoreDefinition = new BendableScoreDefinition(hardScoreSize, softScoreSize);
        BendableScore score2 = bendableScoreDefinition.buildPessimisticBound(scoreTrend, score);
        for (int i = 0; i < score2.getHardLevelsSize() + score2.getSoftLevelsSize(); i++, startingScore -= 1) {
            if(i < score2.getHardLevelsSize()) {
                assertEquals(startingScore, score2.getHardScore(i));
            } else {
                assertEquals(startingScore, score2.getSoftScore(i - score2.getHardLevelsSize()));
            }
        }
    }

    @Test
    public void buildPessimisticBoundDown() {
        int softScoreSize = 3;
        int hardScoreSize = 2;
        int scoreSize = softScoreSize + hardScoreSize;
        int startingScore = -1;
        BendableScore score = BendableScore.parseScore(hardScoreSize, softScoreSize, createStringScore(scoreSize, startingScore));
        InitializingScoreTrend scoreTrend = InitializingScoreTrendLevelFactory
                .createInitializingScoreTrendLevelArray(scoreSize, InitializingScoreTrendLevel.ONLY_DOWN);
        BendableScoreDefinition bendableScoreDefinition = new BendableScoreDefinition(hardScoreSize, softScoreSize);
        BendableScore score2 = bendableScoreDefinition.buildPessimisticBound(scoreTrend, score);
        for (int i = 0; i < score2.getHardLevelsSize() + score2.getSoftLevelsSize(); i++) {
            if(i < score2.getHardLevelsSize()) {
                assertEquals(Integer.MIN_VALUE, score2.getHardScore(i));
            } else {
                assertEquals(Integer.MIN_VALUE, score2.getSoftScore(i - score2.getHardLevelsSize()));
            }
        }
    }

}
