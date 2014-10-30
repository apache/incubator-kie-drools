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
    public void buildOptimisticBoundUp() {
        int scoreSize = new HardSoftScoreDefinition().getLevelsSize();
        HardSoftScore score = HardSoftScore.parseScore("-1hard/-2soft");
        InitializingScoreTrend scoreTrend = InitializingScoreTrend.buildUniformTrend(
                InitializingScoreTrendLevel.ONLY_UP, scoreSize);
        HardSoftScoreDefinition hardSoftScoreDefinition = new HardSoftScoreDefinition();
        HardSoftScore score2 = hardSoftScoreDefinition.buildOptimisticBound(scoreTrend, score);
        assertEquals(Integer.MAX_VALUE, score2.getHardScore());
        assertEquals(Integer.MAX_VALUE, score2.getSoftScore());
    }

    @Test
    public void buildOptimisticBoundDown() {
        int scoreSize = new HardSoftScoreDefinition().getLevelsSize();
        HardSoftScore score = HardSoftScore.parseScore("-1hard/-2soft");
        InitializingScoreTrend scoreTrend = InitializingScoreTrend.buildUniformTrend(
                InitializingScoreTrendLevel.ONLY_DOWN, scoreSize);
        HardSoftScoreDefinition hardSoftScoreDefinition = new HardSoftScoreDefinition();
        HardSoftScore score2 = hardSoftScoreDefinition.buildOptimisticBound(scoreTrend, score);
        assertEquals(-1, score2.getHardScore());
        assertEquals(-2, score2.getSoftScore());
    }

    @Test
    public void buildPessimisticBoundUp() {
        int scoreSize = new HardSoftScoreDefinition().getLevelsSize();
        HardSoftScore score = HardSoftScore.parseScore("-1hard/-2soft");
        InitializingScoreTrend scoreTrend = InitializingScoreTrend.buildUniformTrend(
                InitializingScoreTrendLevel.ONLY_UP, scoreSize);
        HardSoftScoreDefinition hardSoftScoreDefinition = new HardSoftScoreDefinition();
        HardSoftScore score2 = hardSoftScoreDefinition.buildPessimisticBound(scoreTrend, score);
        assertEquals(-1, score2.getHardScore());
        assertEquals(-2, score2.getSoftScore());
    }

    @Test
    public void buildPessimisticBoundDown() {
        int scoreSize = new HardSoftScoreDefinition().getLevelsSize();
        HardSoftScore score = HardSoftScore.parseScore("-1hard/-2soft");
        InitializingScoreTrend scoreTrend = InitializingScoreTrend.buildUniformTrend(
                InitializingScoreTrendLevel.ONLY_DOWN, scoreSize);
        HardSoftScoreDefinition hardSoftScoreDefinition = new HardSoftScoreDefinition();
        HardSoftScore score2 = hardSoftScoreDefinition.buildPessimisticBound(scoreTrend, score);
        assertEquals(Integer.MIN_VALUE, score2.getHardScore());
        assertEquals(Integer.MIN_VALUE, score2.getSoftScore());
    }

}
