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

package org.optaplanner.core.impl.score.buildin.bendablebigdecimal;

import java.math.BigDecimal;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;

import static org.junit.Assert.*;

public class BendableBigDecimalScoreDefinitionTest {

    @Test
    public void getLevelsSize() {
        assertEquals(2, new BendableBigDecimalScoreDefinition(1, 1).getLevelsSize());
        assertEquals(7, new BendableBigDecimalScoreDefinition(3, 4).getLevelsSize());
        assertEquals(7, new BendableBigDecimalScoreDefinition(4, 3).getLevelsSize());
        assertEquals(5, new BendableBigDecimalScoreDefinition(0, 5).getLevelsSize());
        assertEquals(5, new BendableBigDecimalScoreDefinition(5, 0).getLevelsSize());
    }

    @Test
    public void getLevelLabels() {
        assertArrayEquals(new String[]{"hard 0 score", "soft 0 score"}, new BendableBigDecimalScoreDefinition(1, 1).getLevelLabels());
        assertArrayEquals(new String[]{"hard 0 score", "hard 1 score", "hard 2 score", "soft 0 score", "soft 1 score", "soft 2 score", "soft 3 score"}, new BendableBigDecimalScoreDefinition(3, 4).getLevelLabels());
        assertArrayEquals(new String[]{"hard 0 score", "hard 1 score", "hard 2 score", "hard 3 score", "soft 0 score", "soft 1 score", "soft 2 score"}, new BendableBigDecimalScoreDefinition(4, 3).getLevelLabels());
        assertArrayEquals(new String[]{"soft 0 score", "soft 1 score", "soft 2 score", "soft 3 score", "soft 4 score"}, new BendableBigDecimalScoreDefinition(0, 5).getLevelLabels());
        assertArrayEquals(new String[]{"hard 0 score", "hard 1 score", "hard 2 score", "hard 3 score", "hard 4 score"}, new BendableBigDecimalScoreDefinition(5, 0).getLevelLabels());
    }

    @Test
    public void getFeasibleLevelsSize() {
        assertEquals(1, new BendableBigDecimalScoreDefinition(1, 1).getFeasibleLevelsSize());
        assertEquals(3, new BendableBigDecimalScoreDefinition(3, 4).getFeasibleLevelsSize());
        assertEquals(4, new BendableBigDecimalScoreDefinition(4, 3).getFeasibleLevelsSize());
        assertEquals(0, new BendableBigDecimalScoreDefinition(0, 5).getFeasibleLevelsSize());
        assertEquals(5, new BendableBigDecimalScoreDefinition(5, 0).getFeasibleLevelsSize());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createScoreWithIllegalArgument() {
        BendableBigDecimalScoreDefinition bendableScoreDefinition = new BendableBigDecimalScoreDefinition(2, 3);
        bendableScoreDefinition.createScore(new BigDecimal(1), new BigDecimal(2), new BigDecimal(3));
    }

    @Test
    public void createScore() {
        for (int hardLevelSize = 1; hardLevelSize < 5; hardLevelSize++) {
            for (int softLevelSize = 1; softLevelSize < 5; softLevelSize++) {
                int levelSize = hardLevelSize + softLevelSize;
                BigDecimal[] scores = new BigDecimal[levelSize];
                for (int i = 0; i < levelSize; i++) {
                    scores[i] = new BigDecimal(i);
                }
                BendableBigDecimalScoreDefinition bendableScoreDefinition = new BendableBigDecimalScoreDefinition(hardLevelSize, softLevelSize);
                BendableBigDecimalScore bendableScore = bendableScoreDefinition.createScore(scores);
                assertEquals(hardLevelSize, bendableScore.getHardLevelsSize());
                assertEquals(softLevelSize, bendableScore.getSoftLevelsSize());
                for (int i = 0; i < levelSize; i++) {
                    if (i < hardLevelSize) {
                        assertEquals(scores[i], bendableScore.getHardScore(i));
                    } else {
                        assertEquals(scores[i], bendableScore.getSoftScore(i - hardLevelSize));
                    }
                }
            }
        }
    }

    // Optimistic and pessimistic bounds are currently not supported for this score definition

}
