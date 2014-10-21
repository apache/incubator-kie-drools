/*
 * Copyright 2010 JBoss Inc
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

package org.optaplanner.core.impl.score.buildin.hardsoft;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

import static org.junit.Assert.assertEquals;

public class HardSoftScoreDefinitionTest {

    @Test
    public void getLevelsSize() {
        assertEquals(2, new HardSoftScoreDefinition().getLevelsSize());
    }

    @Test
    public void getFeasibleLevelsSize() {
        assertEquals(1, new HardSoftScoreDefinition().getFeasibleLevelsSize());
    }

    @Test
    public void buildOptimisticBound() {
        int scoreSize = new HardSoftScoreDefinition().getLevelsSize();
        InitializingScoreTrendLevel[] levels = new InitializingScoreTrendLevel[scoreSize];
        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_UP;
        }
        HardSoftScore score = HardSoftScore.parseScore("-999hard/-999soft");
        InitializingScoreTrend scoreTrend = new InitializingScoreTrend(levels);
        HardSoftScoreDefinition hardSoftScoreDefinition = new HardSoftScoreDefinition();
        HardSoftScore score2 = hardSoftScoreDefinition.buildOptimisticBound(scoreTrend, score);

        assertEquals(Integer.MAX_VALUE, score2.getHardScore());
        assertEquals(Integer.MAX_VALUE, score2.getSoftScore());

        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_DOWN;
        }
        score2 = hardSoftScoreDefinition.buildOptimisticBound(scoreTrend, score);
        assertEquals(-999, score2.getHardScore());
        assertEquals(-999, score2.getSoftScore());
    }

    @Test
    public void buildPessimisticBound() {
        int scoreSize = new HardSoftScoreDefinition().getLevelsSize();
        InitializingScoreTrendLevel[] levels = new InitializingScoreTrendLevel[scoreSize];
        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_UP;
        }
        HardSoftScore score = HardSoftScore.parseScore("-999hard/-999soft");
        InitializingScoreTrend scoreTrend = new InitializingScoreTrend(levels);
        HardSoftScoreDefinition hardSoftScoreDefinition = new HardSoftScoreDefinition();
        HardSoftScore score2 = hardSoftScoreDefinition.buildPessimisticBound(scoreTrend, score);

        assertEquals(-999, score2.getHardScore());
        assertEquals(-999, score2.getSoftScore());

        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_DOWN;
        }
        score2 = hardSoftScoreDefinition.buildPessimisticBound(scoreTrend, score);

        assertEquals(Integer.MIN_VALUE, score2.getHardScore());
        assertEquals(Integer.MIN_VALUE, score2.getSoftScore());
    }

}
