/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.buildin.hardmediumsoft;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class HardMediumSoftScoreDefinitionTest {

    @Test
    public void getZeroScore() {
        HardMediumSoftScore score = new HardMediumSoftScoreDefinition().getZeroScore();
        assertThat(score).isEqualTo(HardMediumSoftScore.ZERO);
    }

    @Test
    public void getSoftestOneScore() {
        HardMediumSoftScore score = new HardMediumSoftScoreDefinition().getOneSoftestScore();
        assertThat(score).isEqualTo(HardMediumSoftScore.ONE_SOFT);
    }

    @Test
    public void getLevelsSize() {
        assertEquals(3, new HardMediumSoftScoreDefinition().getLevelsSize());
    }

    @Test
    public void getLevelLabels() {
        assertArrayEquals(new String[]{"hard score", "medium score", "soft score"}, new HardMediumSoftScoreDefinition().getLevelLabels());
    }

    @Test
    public void getFeasibleLevelsSize() {
        assertEquals(1, new HardMediumSoftScoreDefinition().getFeasibleLevelsSize());
    }

    @Test
    public void buildOptimisticBoundOnlyUp() {
        HardMediumSoftScoreDefinition scoreDefinition = new HardMediumSoftScoreDefinition();
        HardMediumSoftScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 3),
                HardMediumSoftScore.of(-1, -2, -3));
        assertEquals(0, optimisticBound.getInitScore());
        assertEquals(Integer.MAX_VALUE, optimisticBound.getHardScore());
        assertEquals(Integer.MAX_VALUE, optimisticBound.getMediumScore());
        assertEquals(Integer.MAX_VALUE, optimisticBound.getSoftScore());
    }

    @Test
    public void buildOptimisticBoundOnlyDown() {
        HardMediumSoftScoreDefinition scoreDefinition = new HardMediumSoftScoreDefinition();
        HardMediumSoftScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 3),
                HardMediumSoftScore.of(-1, -2, -3));
        assertEquals(0, optimisticBound.getInitScore());
        assertEquals(-1, optimisticBound.getHardScore());
        assertEquals(-2, optimisticBound.getMediumScore());
        assertEquals(-3, optimisticBound.getSoftScore());
    }

    @Test
    public void buildPessimisticBoundOnlyUp() {
        HardMediumSoftScoreDefinition scoreDefinition = new HardMediumSoftScoreDefinition();
        HardMediumSoftScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 3),
                HardMediumSoftScore.of(-1, -2, -3));
        assertEquals(0, pessimisticBound.getInitScore());
        assertEquals(-1, pessimisticBound.getHardScore());
        assertEquals(-2, pessimisticBound.getMediumScore());
        assertEquals(-3, pessimisticBound.getSoftScore());
    }

    @Test
    public void buildPessimisticBoundOnlyDown() {
        HardMediumSoftScoreDefinition scoreDefinition = new HardMediumSoftScoreDefinition();
        HardMediumSoftScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 3),
                HardMediumSoftScore.of(-1, -2, -3));
        assertEquals(0, pessimisticBound.getInitScore());
        assertEquals(Integer.MIN_VALUE, pessimisticBound.getHardScore());
        assertEquals(Integer.MIN_VALUE, pessimisticBound.getMediumScore());
        assertEquals(Integer.MIN_VALUE, pessimisticBound.getSoftScore());
    }

    @Test
    public void divideBySanitizedDivisor() {
        HardMediumSoftScoreDefinition scoreDefinition = new HardMediumSoftScoreDefinition();
        HardMediumSoftScore dividend = scoreDefinition.fromLevelNumbers(2, new Number[] {0, 1, 10});
        HardMediumSoftScore zeroDivisor = scoreDefinition.getZeroScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, zeroDivisor))
                .isEqualTo(dividend);
        HardMediumSoftScore oneDivisor = scoreDefinition.getOneSoftestScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, oneDivisor))
                .isEqualTo(dividend);
        HardMediumSoftScore tenDivisor = scoreDefinition.fromLevelNumbers(10, new Number[] {10, 10, 10});
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, tenDivisor))
                .isEqualTo(scoreDefinition.fromLevelNumbers(0, new Number[] {0, 0, 1}));
    }

}
