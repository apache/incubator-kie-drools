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

package org.optaplanner.core.impl.score.buildin.simpledouble;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simpledouble.SimpleDoubleScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SimpleDoubleScoreDefinitionTest {

    @Test
    public void getZeroScore() {
        SimpleDoubleScore score = new SimpleDoubleScoreDefinition().getZeroScore();
        assertThat(score).isEqualTo(SimpleDoubleScore.ZERO);
    }

    @Test
    public void getSoftestOneScore() {
        SimpleDoubleScore score = new SimpleDoubleScoreDefinition().getOneSoftestScore();
        assertThat(score).isEqualTo(SimpleDoubleScore.ONE);
    }

    @Test
    public void getLevelSize() {
        assertEquals(1, new SimpleDoubleScoreDefinition().getLevelsSize());
    }

    @Test
    public void getLevelLabels() {
        assertArrayEquals(new String[]{"score"}, new SimpleDoubleScoreDefinition().getLevelLabels());
    }

    @Test
    public void buildOptimisticBoundOnlyUp() {
        SimpleDoubleScoreDefinition scoreDefinition = new SimpleDoubleScoreDefinition();
        SimpleDoubleScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 1),
                SimpleDoubleScore.of(-1.7));
        assertEquals(0, optimisticBound.getInitScore());
        assertEquals(Double.POSITIVE_INFINITY, optimisticBound.getScore(), 0.0);
    }

    @Test
    public void buildOptimisticBoundOnlyDown() {
        SimpleDoubleScoreDefinition scoreDefinition = new SimpleDoubleScoreDefinition();
        SimpleDoubleScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 1),
                SimpleDoubleScore.of(-1.7));
        assertEquals(0, optimisticBound.getInitScore());
        assertEquals(-1.7, optimisticBound.getScore(), 0.0);
    }

    @Test
    public void buildPessimisticBoundOnlyUp() {
        SimpleDoubleScoreDefinition scoreDefinition = new SimpleDoubleScoreDefinition();
        SimpleDoubleScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 1),
                SimpleDoubleScore.of(-1.7));
        assertEquals(0, pessimisticBound.getInitScore());
        assertEquals(-1.7, pessimisticBound.getScore(), 0.0);
    }

    @Test
    public void buildPessimisticBoundOnlyDown() {
        SimpleDoubleScoreDefinition scoreDefinition = new SimpleDoubleScoreDefinition();
        SimpleDoubleScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 1),
                SimpleDoubleScore.of(-1.7));
        assertEquals(0, pessimisticBound.getInitScore());
        assertEquals(Double.NEGATIVE_INFINITY, pessimisticBound.getScore(), 0.0);
    }

    @Test
    public void divideBySanitizedDivisor() {
        SimpleDoubleScoreDefinition scoreDefinition = new SimpleDoubleScoreDefinition();
        SimpleDoubleScore dividend = scoreDefinition.fromLevelNumbers(2, new Number[] {10d});
        SimpleDoubleScore zeroDivisor = scoreDefinition.getZeroScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, zeroDivisor))
                .isEqualTo(dividend);
        SimpleDoubleScore oneDivisor = scoreDefinition.getOneSoftestScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, oneDivisor))
                .isEqualTo(dividend);
        SimpleDoubleScore tenDivisor = scoreDefinition.fromLevelNumbers(10, new Number[] {10d});
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, tenDivisor))
                .isEqualTo(scoreDefinition.fromLevelNumbers(0, new Number[] {1d}));
    }

}
