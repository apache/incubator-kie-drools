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

package org.optaplanner.core.impl.score.buildin.hardsoftdouble;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardsoftdouble.HardSoftDoubleScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

public class HardSoftDoubleScoreDefinitionTest {

    @Test
    public void getZeroScore() {
        HardSoftDoubleScore score = new HardSoftDoubleScoreDefinition().getZeroScore();
        assertThat(score).isEqualTo(HardSoftDoubleScore.ZERO);
    }

    @Test
    public void getSoftestOneScore() {
        HardSoftDoubleScore score = new HardSoftDoubleScoreDefinition().getOneSoftestScore();
        assertThat(score).isEqualTo(HardSoftDoubleScore.ONE_SOFT);
    }

    @Test
    public void getLevelSize() {
        assertThat(new HardSoftDoubleScoreDefinition().getLevelsSize()).isEqualTo(2);
    }

    @Test
    public void getLevelLabels() {
        assertThat(new HardSoftDoubleScoreDefinition().getLevelLabels()).isEqualTo(new String[] { "hard score", "soft score" });
    }

    @Test
    public void getFeasibleLevelsSize() {
        assertThat(new HardSoftDoubleScoreDefinition().getFeasibleLevelsSize()).isEqualTo(1);
    }

    @Test
    public void buildOptimisticBoundOnlyUp() {
        HardSoftDoubleScoreDefinition scoreDefinition = new HardSoftDoubleScoreDefinition();
        HardSoftDoubleScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 2),
                HardSoftDoubleScore.of(-1.7, -2.2));
        assertThat(optimisticBound.getInitScore()).isEqualTo(0);
        assertThat(optimisticBound.getHardScore()).isEqualTo(Double.POSITIVE_INFINITY, offset(0.0));
        assertThat(optimisticBound.getSoftScore()).isEqualTo(Double.POSITIVE_INFINITY, offset(0.0));
    }

    @Test
    public void buildOptimisticBoundOnlyDown() {
        HardSoftDoubleScoreDefinition scoreDefinition = new HardSoftDoubleScoreDefinition();
        HardSoftDoubleScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 2),
                HardSoftDoubleScore.of(-1.7, -2.2));
        assertThat(optimisticBound.getInitScore()).isEqualTo(0);
        assertThat(optimisticBound.getHardScore()).isEqualTo(-1.7, offset(0.0));
        assertThat(optimisticBound.getSoftScore()).isEqualTo(-2.2, offset(0.0));
    }

    @Test
    public void buildPessimisticBoundOnlyUp() {
        HardSoftDoubleScoreDefinition scoreDefinition = new HardSoftDoubleScoreDefinition();
        HardSoftDoubleScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 2),
                HardSoftDoubleScore.of(-1.7, -2.2));
        assertThat(pessimisticBound.getInitScore()).isEqualTo(0);
        assertThat(pessimisticBound.getHardScore()).isEqualTo(-1.7, offset(0.0));
        assertThat(pessimisticBound.getSoftScore()).isEqualTo(-2.2, offset(0.0));
    }

    @Test
    public void buildPessimisticBoundOnlyDown() {
        HardSoftDoubleScoreDefinition scoreDefinition = new HardSoftDoubleScoreDefinition();
        HardSoftDoubleScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 2),
                HardSoftDoubleScore.of(-1, -2));
        assertThat(pessimisticBound.getInitScore()).isEqualTo(0);
        assertThat(pessimisticBound.getHardScore()).isEqualTo(Double.NEGATIVE_INFINITY, offset(0.0));
        assertThat(pessimisticBound.getSoftScore()).isEqualTo(Double.NEGATIVE_INFINITY, offset(0.0));
    }

    @Test
    public void divideBySanitizedDivisor() {
        HardSoftDoubleScoreDefinition scoreDefinition = new HardSoftDoubleScoreDefinition();
        HardSoftDoubleScore dividend = scoreDefinition.fromLevelNumbers(2, new Number[] { 0d, 10d });
        HardSoftDoubleScore zeroDivisor = scoreDefinition.getZeroScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, zeroDivisor))
                .isEqualTo(dividend);
        HardSoftDoubleScore oneDivisor = scoreDefinition.getOneSoftestScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, oneDivisor))
                .isEqualTo(dividend);
        HardSoftDoubleScore tenDivisor = scoreDefinition.fromLevelNumbers(10, new Number[] { 10d, 10d });
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, tenDivisor))
                .isEqualTo(scoreDefinition.fromLevelNumbers(0, new Number[] { 0d, 1d }));
    }

}
