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
    public void createScoreFail() {
        BendableScoreDefinition bendableScoreDefinition = new BendableScoreDefinition(2, 3);
        bendableScoreDefinition.createScore(1, 2, 3);
    }

    @Test
    public void createScore() {
        for (int hardLevelSize = 1; hardLevelSize < 5; hardLevelSize++) {
            for (int softLevelSize = 1; softLevelSize < 5; softLevelSize++) {
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
        }
    }

    @Test
    public void buildOptimisticBound() {
        int softScoreSize = 3;
        int hardScoreSize = 2;
        int scoreSize = softScoreSize + hardScoreSize;
        InitializingScoreTrendLevel[] levels = new InitializingScoreTrendLevel[scoreSize];

        int startingScore = -999;
        String stringScore = String.valueOf(startingScore);
        for (int i = 0; i < scoreSize - 1; i++) {
            stringScore += "/" + String.valueOf(startingScore);
        }
        BendableScore score = BendableScore.parseScore(hardScoreSize, softScoreSize, stringScore);
        InitializingScoreTrend scoreTrend = new InitializingScoreTrend(levels);

        BendableScoreDefinition bendableScoreDefinition = new BendableScoreDefinition(hardScoreSize, softScoreSize);
        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_UP;
        }
        BendableScore score2 = bendableScoreDefinition.buildOptimisticBound(scoreTrend, score);

        for (int i = 0; i < score2.getHardLevelsSize() + score2.getSoftLevelsSize(); i++) {
            if(i < score2.getHardLevelsSize()) {
                assertEquals(Integer.MAX_VALUE, score2.getHardScore(i));
            } else {
                assertEquals(Integer.MAX_VALUE, score2.getSoftScore(i - score2.getHardLevelsSize()));
            }
        }

        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_DOWN;
        }
        score2 = bendableScoreDefinition.buildOptimisticBound(scoreTrend, score);
        for (int i = 0; i < score2.getHardLevelsSize() + score2.getSoftLevelsSize(); i++) {
            if(i < score2.getHardLevelsSize()) {
                assertEquals(startingScore, score2.getHardScore(i));
            } else {
                assertEquals(startingScore, score2.getSoftScore(i - score2.getHardLevelsSize()));
            }
        }
    }

    @Test
    public void buildPessimisticBound() {
        int softScoreSize = 3;
        int hardScoreSize = 2;
        int scoreSize = softScoreSize + hardScoreSize;
        InitializingScoreTrendLevel[] levels = new InitializingScoreTrendLevel[scoreSize];
        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_UP;
        }
        int startingScore = -999;
        String stringScore = String.valueOf(startingScore);
        for (int i = 0; i < scoreSize - 1; i++) {
            stringScore += "/" + String.valueOf(startingScore);
        }
        BendableScore score = BendableScore.parseScore(hardScoreSize, softScoreSize, stringScore);
        InitializingScoreTrend scoreTrend = new InitializingScoreTrend(levels);
        BendableScoreDefinition bendableScoreDefinition = new BendableScoreDefinition(hardScoreSize, softScoreSize);
        BendableScore score2 = bendableScoreDefinition.buildPessimisticBound(scoreTrend, score);

        for (int i = 0; i < score2.getHardLevelsSize() + score2.getSoftLevelsSize(); i++) {
            if(i < score2.getHardLevelsSize()) {
                assertEquals(startingScore, score2.getHardScore(i));
            } else {
                assertEquals(startingScore, score2.getSoftScore(i - score2.getHardLevelsSize()));
            }
        }

        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_DOWN;
        }
        score2 = bendableScoreDefinition.buildPessimisticBound(scoreTrend, score);
        for (int i = 0; i < score2.getHardLevelsSize() + score2.getSoftLevelsSize(); i++) {
            if(i < score2.getHardLevelsSize()) {
                assertEquals(Integer.MIN_VALUE, score2.getHardScore(i));
            } else {
                assertEquals(Integer.MIN_VALUE, score2.getSoftScore(i - score2.getHardLevelsSize()));
            }
        }
    }

}
