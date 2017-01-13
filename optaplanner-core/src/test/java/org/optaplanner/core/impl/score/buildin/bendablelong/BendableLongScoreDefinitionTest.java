/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.score.buildin.bendablelong;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

import static org.junit.Assert.*;

public class BendableLongScoreDefinitionTest {

    @Test
    public void getLevelsSize() {
        assertEquals(2, new BendableLongScoreDefinition(1, 1).getLevelsSize());
        assertEquals(7, new BendableLongScoreDefinition(3, 4).getLevelsSize());
        assertEquals(7, new BendableLongScoreDefinition(4, 3).getLevelsSize());
        assertEquals(5, new BendableLongScoreDefinition(0, 5).getLevelsSize());
        assertEquals(5, new BendableLongScoreDefinition(5, 0).getLevelsSize());
    }

    @Test
    public void getLevelLabels() {
        assertArrayEquals(new String[]{"hard 0 score", "soft 0 score"}, new BendableLongScoreDefinition(1, 1).getLevelLabels());
        assertArrayEquals(new String[]{"hard 0 score", "hard 1 score", "hard 2 score", "soft 0 score", "soft 1 score", "soft 2 score", "soft 3 score"}, new BendableLongScoreDefinition(3, 4).getLevelLabels());
        assertArrayEquals(new String[]{"hard 0 score", "hard 1 score", "hard 2 score", "hard 3 score", "soft 0 score", "soft 1 score", "soft 2 score"}, new BendableLongScoreDefinition(4, 3).getLevelLabels());
        assertArrayEquals(new String[]{"soft 0 score", "soft 1 score", "soft 2 score", "soft 3 score", "soft 4 score"}, new BendableLongScoreDefinition(0, 5).getLevelLabels());
        assertArrayEquals(new String[]{"hard 0 score", "hard 1 score", "hard 2 score", "hard 3 score", "hard 4 score"}, new BendableLongScoreDefinition(5, 0).getLevelLabels());
    }

    @Test
    public void getFeasibleLevelsSize() {
        assertEquals(1, new BendableLongScoreDefinition(1, 1).getFeasibleLevelsSize());
        assertEquals(3, new BendableLongScoreDefinition(3, 4).getFeasibleLevelsSize());
        assertEquals(4, new BendableLongScoreDefinition(4, 3).getFeasibleLevelsSize());
        assertEquals(0, new BendableLongScoreDefinition(0, 5).getFeasibleLevelsSize());
        assertEquals(5, new BendableLongScoreDefinition(5, 0).getFeasibleLevelsSize());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createScoreWithIllegalArgument() {
        BendableLongScoreDefinition bendableLongScoreDefinition = new BendableLongScoreDefinition(2, 3);
        bendableLongScoreDefinition.createScore(1, 2, 3);
    }

    @Test
    public void createScore() {
        int hardLevelSize = 3;
        int softLevelSize = 2;
        int levelSize = hardLevelSize + softLevelSize;
        long[] scores = new long[levelSize];
        for (int i = 0; i < levelSize; i++) {
            scores[i] = ((long) Integer.MAX_VALUE) + i;
        }
        BendableLongScoreDefinition bendableLongScoreDefinition = new BendableLongScoreDefinition(hardLevelSize, softLevelSize);
        BendableLongScore bendableLongScore = bendableLongScoreDefinition.createScore(scores);
        assertEquals(hardLevelSize, bendableLongScore.getHardLevelsSize());
        assertEquals(softLevelSize, bendableLongScore.getSoftLevelsSize());
        for (int i = 0; i < levelSize; i++) {
            if (i < hardLevelSize) {
                assertEquals(scores[i], bendableLongScore.getHardScore(i));
            } else {
                assertEquals(scores[i], bendableLongScore.getSoftScore(i - hardLevelSize));
            }
        }
    }

    @Test
    public void buildOptimisticBoundOnlyUp() {
        BendableLongScoreDefinition scoreDefinition = new BendableLongScoreDefinition(2, 3);
        BendableLongScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 5),
                scoreDefinition.createScore(-1, -2, -3, -4, -5));
        assertEquals(0, optimisticBound.getInitScore());
        assertEquals(Long.MAX_VALUE, optimisticBound.getHardScore(0));
        assertEquals(Long.MAX_VALUE, optimisticBound.getHardScore(1));
        assertEquals(Long.MAX_VALUE, optimisticBound.getSoftScore(0));
        assertEquals(Long.MAX_VALUE, optimisticBound.getSoftScore(1));
        assertEquals(Long.MAX_VALUE, optimisticBound.getSoftScore(2));
    }

    @Test
    public void buildOptimisticBoundOnlyDown() {
        BendableLongScoreDefinition scoreDefinition = new BendableLongScoreDefinition(2, 3);
        BendableLongScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 5),
                scoreDefinition.createScore(-1, -2, -3, -4, -5));
        assertEquals(0, optimisticBound.getInitScore());
        assertEquals(-1, optimisticBound.getHardScore(0));
        assertEquals(-2, optimisticBound.getHardScore(1));
        assertEquals(-3, optimisticBound.getSoftScore(0));
        assertEquals(-4, optimisticBound.getSoftScore(1));
        assertEquals(-5, optimisticBound.getSoftScore(2));
    }

    @Test
    public void buildPessimisticBoundOnlyUp() {
        BendableLongScoreDefinition scoreDefinition = new BendableLongScoreDefinition(2, 3);
        BendableLongScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 5),
                scoreDefinition.createScore(-1, -2, -3, -4, -5));
        assertEquals(0, pessimisticBound.getInitScore());
        assertEquals(-1, pessimisticBound.getHardScore(0));
        assertEquals(-2, pessimisticBound.getHardScore(1));
        assertEquals(-3, pessimisticBound.getSoftScore(0));
        assertEquals(-4, pessimisticBound.getSoftScore(1));
        assertEquals(-5, pessimisticBound.getSoftScore(2));
    }

    @Test
    public void buildPessimisticBoundOnlyDown() {
        BendableLongScoreDefinition scoreDefinition = new BendableLongScoreDefinition(2, 3);
        BendableLongScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 5),
                scoreDefinition.createScore(-1, -2, -3, -4, -5));
        assertEquals(0, pessimisticBound.getInitScore());
        assertEquals(Long.MIN_VALUE, pessimisticBound.getHardScore(0));
        assertEquals(Long.MIN_VALUE, pessimisticBound.getHardScore(1));
        assertEquals(Long.MIN_VALUE, pessimisticBound.getSoftScore(0));
        assertEquals(Long.MIN_VALUE, pessimisticBound.getSoftScore(1));
        assertEquals(Long.MIN_VALUE, pessimisticBound.getSoftScore(2));
    }

}
