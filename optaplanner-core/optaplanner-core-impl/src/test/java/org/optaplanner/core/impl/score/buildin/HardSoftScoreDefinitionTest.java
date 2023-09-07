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
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

class HardSoftScoreDefinitionTest {

    @Test
    void getZeroScore() {
        HardSoftScore score = new HardSoftScoreDefinition().getZeroScore();
        assertThat(score).isEqualTo(HardSoftScore.ZERO);
    }

    @Test
    void getSoftestOneScore() {
        HardSoftScore score = new HardSoftScoreDefinition().getOneSoftestScore();
        assertThat(score).isEqualTo(HardSoftScore.ONE_SOFT);
    }

    @Test
    void getLevelsSize() {
        assertThat(new HardSoftScoreDefinition().getLevelsSize()).isEqualTo(2);
    }

    @Test
    void getLevelLabels() {
        assertThat(new HardSoftScoreDefinition().getLevelLabels()).containsExactly("hard score", "soft score");
    }

    @Test
    void getFeasibleLevelsSize() {
        assertThat(new HardSoftScoreDefinition().getFeasibleLevelsSize()).isEqualTo(1);
    }

    @Test
    void buildOptimisticBoundOnlyUp() {
        HardSoftScoreDefinition scoreDefinition = new HardSoftScoreDefinition();
        HardSoftScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 2),
                HardSoftScore.of(-1, -2));
        assertThat(optimisticBound.initScore()).isEqualTo(0);
        assertThat(optimisticBound.hardScore()).isEqualTo(Integer.MAX_VALUE);
        assertThat(optimisticBound.softScore()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    void buildOptimisticBoundOnlyDown() {
        HardSoftScoreDefinition scoreDefinition = new HardSoftScoreDefinition();
        HardSoftScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 2),
                HardSoftScore.of(-1, -2));
        assertThat(optimisticBound.initScore()).isEqualTo(0);
        assertThat(optimisticBound.hardScore()).isEqualTo(-1);
        assertThat(optimisticBound.softScore()).isEqualTo(-2);
    }

    @Test
    void buildPessimisticBoundOnlyUp() {
        HardSoftScoreDefinition scoreDefinition = new HardSoftScoreDefinition();
        HardSoftScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 2),
                HardSoftScore.of(-1, -2));
        assertThat(pessimisticBound.initScore()).isEqualTo(0);
        assertThat(pessimisticBound.hardScore()).isEqualTo(-1);
        assertThat(pessimisticBound.softScore()).isEqualTo(-2);
    }

    @Test
    void buildPessimisticBoundOnlyDown() {
        HardSoftScoreDefinition scoreDefinition = new HardSoftScoreDefinition();
        HardSoftScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 2),
                HardSoftScore.of(-1, -2));
        assertThat(pessimisticBound.initScore()).isEqualTo(0);
        assertThat(pessimisticBound.hardScore()).isEqualTo(Integer.MIN_VALUE);
        assertThat(pessimisticBound.softScore()).isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    void divideBySanitizedDivisor() {
        HardSoftScoreDefinition scoreDefinition = new HardSoftScoreDefinition();
        HardSoftScore dividend = scoreDefinition.fromLevelNumbers(2, new Number[] { 0, 10 });
        HardSoftScore zeroDivisor = scoreDefinition.getZeroScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, zeroDivisor))
                .isEqualTo(dividend);
        HardSoftScore oneDivisor = scoreDefinition.getOneSoftestScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, oneDivisor))
                .isEqualTo(dividend);
        HardSoftScore tenDivisor = scoreDefinition.fromLevelNumbers(10, new Number[] { 10, 10 });
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, tenDivisor))
                .isEqualTo(scoreDefinition.fromLevelNumbers(0, new Number[] { 0, 1 }));
    }

}
