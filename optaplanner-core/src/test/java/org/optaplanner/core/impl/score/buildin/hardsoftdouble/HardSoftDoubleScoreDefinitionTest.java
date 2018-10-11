/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.buildin.hardsoftdouble;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.hardsoftdouble.HardSoftDoubleScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

import static org.junit.Assert.*;

public class HardSoftDoubleScoreDefinitionTest {

    @Test
    public void getLevelSize() {
        assertEquals(2, new HardSoftDoubleScoreDefinition().getLevelsSize());
    }

    @Test
    public void getLevelLabels() {
        assertArrayEquals(new String[]{"hard score", "soft score"}, new HardSoftDoubleScoreDefinition().getLevelLabels());
    }

    @Test
    public void getFeasibleLevelsSize() {
        assertEquals(1, new HardSoftDoubleScoreDefinition().getFeasibleLevelsSize());
    }

    @Test
    public void buildOptimisticBoundOnlyUp() {
        HardSoftDoubleScoreDefinition scoreDefinition = new HardSoftDoubleScoreDefinition();
        HardSoftDoubleScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 2),
                HardSoftDoubleScore.of(-1.7, -2.2));
        assertEquals(0, optimisticBound.getInitScore());
        assertEquals(Double.POSITIVE_INFINITY, optimisticBound.getHardScore(), 0.0);
        assertEquals(Double.POSITIVE_INFINITY, optimisticBound.getSoftScore(), 0.0);
    }

    @Test
    public void buildOptimisticBoundOnlyDown() {
        HardSoftDoubleScoreDefinition scoreDefinition = new HardSoftDoubleScoreDefinition();
        HardSoftDoubleScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 2),
                HardSoftDoubleScore.of(-1.7, -2.2));
        assertEquals(0, optimisticBound.getInitScore());
        assertEquals(-1.7, optimisticBound.getHardScore(), 0.0);
        assertEquals(-2.2, optimisticBound.getSoftScore(), 0.0);
    }

    @Test
    public void buildPessimisticBoundOnlyUp() {
        HardSoftDoubleScoreDefinition scoreDefinition = new HardSoftDoubleScoreDefinition();
        HardSoftDoubleScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 2),
                HardSoftDoubleScore.of(-1.7, -2.2));
        assertEquals(0, pessimisticBound.getInitScore());
        assertEquals(-1.7, pessimisticBound.getHardScore(), 0.0);
        assertEquals(-2.2, pessimisticBound.getSoftScore(), 0.0);
    }

    @Test
    public void buildPessimisticBoundOnlyDown() {
        HardSoftDoubleScoreDefinition scoreDefinition = new HardSoftDoubleScoreDefinition();
        HardSoftDoubleScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 2),
                HardSoftDoubleScore.of(-1, -2));
        assertEquals(0, pessimisticBound.getInitScore());
        assertEquals(Double.NEGATIVE_INFINITY, pessimisticBound.getHardScore(), 0.0);
        assertEquals(Double.NEGATIVE_INFINITY, pessimisticBound.getSoftScore(), 0.0);
    }

}
