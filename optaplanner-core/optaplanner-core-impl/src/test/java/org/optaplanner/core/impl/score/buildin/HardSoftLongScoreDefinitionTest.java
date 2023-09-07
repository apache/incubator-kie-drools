/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.impl.score.buildin;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

class HardSoftLongScoreDefinitionTest {

    @Test
    void getZeroScore() {
        HardSoftLongScore score = new HardSoftLongScoreDefinition().getZeroScore();
        assertThat(score).isEqualTo(HardSoftLongScore.ZERO);
    }

    @Test
    void getSoftestOneScore() {
        HardSoftLongScore score = new HardSoftLongScoreDefinition().getOneSoftestScore();
        assertThat(score).isEqualTo(HardSoftLongScore.ONE_SOFT);
    }

    @Test
    void getLevelSize() {
        assertThat(new HardSoftLongScoreDefinition().getLevelsSize()).isEqualTo(2);
    }

    @Test
    void getLevelLabels() {
        assertThat(new HardSoftLongScoreDefinition().getLevelLabels()).containsExactly("hard score", "soft score");
    }

    @Test
    void getFeasibleLevelsSize() {
        assertThat(new HardSoftLongScoreDefinition().getFeasibleLevelsSize()).isEqualTo(1);
    }

    @Test
    void buildOptimisticBoundOnlyUp() {
        HardSoftLongScoreDefinition scoreDefinition = new HardSoftLongScoreDefinition();
        HardSoftLongScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 2),
                HardSoftLongScore.of(-1L, -2L));
        assertThat(optimisticBound.initScore()).isEqualTo(0);
        assertThat(optimisticBound.hardScore()).isEqualTo(Long.MAX_VALUE);
        assertThat(optimisticBound.softScore()).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    void buildOptimisticBoundOnlyDown() {
        HardSoftLongScoreDefinition scoreDefinition = new HardSoftLongScoreDefinition();
        HardSoftLongScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 2),
                HardSoftLongScore.of(-1L, -2L));
        assertThat(optimisticBound.initScore()).isEqualTo(0);
        assertThat(optimisticBound.hardScore()).isEqualTo(-1L);
        assertThat(optimisticBound.softScore()).isEqualTo(-2L);
    }

    @Test
    void buildPessimisticBoundOnlyUp() {
        HardSoftLongScoreDefinition scoreDefinition = new HardSoftLongScoreDefinition();
        HardSoftLongScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 2),
                HardSoftLongScore.of(-1L, -2L));
        assertThat(pessimisticBound.initScore()).isEqualTo(0);
        assertThat(pessimisticBound.hardScore()).isEqualTo(-1L);
        assertThat(pessimisticBound.softScore()).isEqualTo(-2L);
    }

    @Test
    void buildPessimisticBoundOnlyDown() {
        HardSoftLongScoreDefinition scoreDefinition = new HardSoftLongScoreDefinition();
        HardSoftLongScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 2),
                HardSoftLongScore.of(-1L, -2L));
        assertThat(pessimisticBound.initScore()).isEqualTo(0);
        assertThat(pessimisticBound.hardScore()).isEqualTo(Long.MIN_VALUE);
        assertThat(pessimisticBound.softScore()).isEqualTo(Long.MIN_VALUE);
    }

    @Test
    void divideBySanitizedDivisor() {
        HardSoftLongScoreDefinition scoreDefinition = new HardSoftLongScoreDefinition();
        HardSoftLongScore dividend = scoreDefinition.fromLevelNumbers(2, new Number[] { 0L, 10L });
        HardSoftLongScore zeroDivisor = scoreDefinition.getZeroScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, zeroDivisor))
                .isEqualTo(dividend);
        HardSoftLongScore oneDivisor = scoreDefinition.getOneSoftestScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, oneDivisor))
                .isEqualTo(dividend);
        HardSoftLongScore tenDivisor = scoreDefinition.fromLevelNumbers(10, new Number[] { 10L, 10L });
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, tenDivisor))
                .isEqualTo(scoreDefinition.fromLevelNumbers(0, new Number[] { 0L, 1L }));
    }

}
