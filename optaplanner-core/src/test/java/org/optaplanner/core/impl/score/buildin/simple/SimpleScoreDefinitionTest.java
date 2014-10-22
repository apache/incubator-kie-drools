/*
 * Copyright 2014 JBoss Inc
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

package org.optaplanner.core.impl.score.buildin.simple;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.buildin.InitializingScoreTrendLevelFactory;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

import static org.junit.Assert.*;

public class SimpleScoreDefinitionTest {

    @Test
    public void getLevelsSize() {
        assertEquals(1, new SimpleScoreDefinition().getLevelsSize());
    }

    @Test
    public void buildOptimisticBoundUp() {
        int scoreSize = new SimpleScoreDefinition().getLevelsSize();
        SimpleScore score = SimpleScore.parseScore("-1");
        InitializingScoreTrend scoreTrend = InitializingScoreTrendLevelFactory
                .createInitializingScoreTrendLevelArray(scoreSize, InitializingScoreTrendLevel.ONLY_UP);
        SimpleScoreDefinition hardSoftScoreDefinition = new SimpleScoreDefinition();
        SimpleScore score2 = hardSoftScoreDefinition.buildOptimisticBound(scoreTrend, score);
        assertEquals(Integer.MAX_VALUE, score2.getScore());
    }

    @Test
    public void buildOptimisticBoundDown() {
        int scoreSize = new SimpleScoreDefinition().getLevelsSize();
        SimpleScore score = SimpleScore.parseScore("-1");
        InitializingScoreTrend scoreTrend = InitializingScoreTrendLevelFactory
                .createInitializingScoreTrendLevelArray(scoreSize, InitializingScoreTrendLevel.ONLY_DOWN);
        SimpleScoreDefinition hardSoftScoreDefinition = new SimpleScoreDefinition();
        SimpleScore score2 = hardSoftScoreDefinition.buildOptimisticBound(scoreTrend, score);
        assertEquals(-1, score2.getScore());
    }

    @Test
    public void buildPessimisticBoundUp() {
        int scoreSize = new SimpleScoreDefinition().getLevelsSize();
        SimpleScore score = SimpleScore.parseScore("-1");
        InitializingScoreTrend scoreTrend = InitializingScoreTrendLevelFactory
                .createInitializingScoreTrendLevelArray(scoreSize, InitializingScoreTrendLevel.ONLY_UP);
        SimpleScoreDefinition hardSoftScoreDefinition = new SimpleScoreDefinition();
        SimpleScore score2 = hardSoftScoreDefinition.buildPessimisticBound(scoreTrend, score);
        assertEquals(-1, score2.getScore());
    }

    @Test
    public void buildPessimisticBoundDown() {
        int scoreSize = new SimpleScoreDefinition().getLevelsSize();
        SimpleScore score = SimpleScore.parseScore("-1");
        InitializingScoreTrend scoreTrend = InitializingScoreTrendLevelFactory
                .createInitializingScoreTrendLevelArray(scoreSize, InitializingScoreTrendLevel.ONLY_DOWN);
        SimpleScoreDefinition hardSoftScoreDefinition = new SimpleScoreDefinition();
        SimpleScore score2 = hardSoftScoreDefinition.buildPessimisticBound(scoreTrend, score);
        assertEquals(Integer.MIN_VALUE, score2.getScore());
    }

}
