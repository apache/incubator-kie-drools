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

package org.optaplanner.core.impl.score.buildin.simplelong;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

public class SimpleLongScoreDefinitionTest {

    @Test
    public void getZeroScore() {
        SimpleLongScore score = new SimpleLongScoreDefinition().getZeroScore();
        assertThat(score).isEqualTo(SimpleLongScore.ZERO);
    }

    @Test
    public void getSoftestOneScore() {
        SimpleLongScore score = new SimpleLongScoreDefinition().getOneSoftestScore();
        assertThat(score).isEqualTo(SimpleLongScore.ONE);
    }

    @Test
    public void getLevelSize() {
        assertThat(new SimpleLongScoreDefinition().getLevelsSize()).isEqualTo(1);
    }

    @Test
    public void getLevelLabels() {
        assertThat(new SimpleLongScoreDefinition().getLevelLabels()).isEqualTo(new String[] { "score" });
    }

    @Test
    public void buildOptimisticBoundOnlyUp() {
        SimpleLongScoreDefinition scoreDefinition = new SimpleLongScoreDefinition();
        SimpleLongScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 1),
                SimpleLongScore.of(-1L));
        assertThat(optimisticBound.getInitScore()).isEqualTo(0);
        assertThat(optimisticBound.getScore()).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    public void buildOptimisticBoundOnlyDown() {
        SimpleLongScoreDefinition scoreDefinition = new SimpleLongScoreDefinition();
        SimpleLongScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 1),
                SimpleLongScore.of(-1L));
        assertThat(optimisticBound.getInitScore()).isEqualTo(0);
        assertThat(optimisticBound.getScore()).isEqualTo(-1L);
    }

    @Test
    public void buildPessimisticBoundOnlyUp() {
        SimpleLongScoreDefinition scoreDefinition = new SimpleLongScoreDefinition();
        SimpleLongScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 1),
                SimpleLongScore.of(-1L));
        assertThat(pessimisticBound.getInitScore()).isEqualTo(0);
        assertThat(pessimisticBound.getScore()).isEqualTo(-1L);
    }

    @Test
    public void buildPessimisticBoundOnlyDown() {
        SimpleLongScoreDefinition scoreDefinition = new SimpleLongScoreDefinition();
        SimpleLongScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 1),
                SimpleLongScore.of(-1L));
        assertThat(pessimisticBound.getInitScore()).isEqualTo(0);
        assertThat(pessimisticBound.getScore()).isEqualTo(Long.MIN_VALUE);
    }

    @Test
    public void divideBySanitizedDivisor() {
        SimpleLongScoreDefinition scoreDefinition = new SimpleLongScoreDefinition();
        SimpleLongScore dividend = scoreDefinition.fromLevelNumbers(2, new Number[] { 10L });
        SimpleLongScore zeroDivisor = scoreDefinition.getZeroScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, zeroDivisor))
                .isEqualTo(dividend);
        SimpleLongScore oneDivisor = scoreDefinition.getOneSoftestScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, oneDivisor))
                .isEqualTo(dividend);
        SimpleLongScore tenDivisor = scoreDefinition.fromLevelNumbers(10, new Number[] { 10L });
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, tenDivisor))
                .isEqualTo(scoreDefinition.fromLevelNumbers(0, new Number[] { 1L }));
    }

}
