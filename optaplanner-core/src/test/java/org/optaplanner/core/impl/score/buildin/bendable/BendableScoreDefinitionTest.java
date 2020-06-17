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

package org.optaplanner.core.impl.score.buildin.bendable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

public class BendableScoreDefinitionTest {

    @Test
    public void getZeroScore() {
        BendableScore score = new BendableScoreDefinition(1, 2).getZeroScore();
        assertThat(score).isEqualTo(BendableScore.zero(1, 2));
    }

    @Test
    public void getSoftestOneScore() {
        BendableScore score = new BendableScoreDefinition(1, 2).getOneSoftestScore();
        assertThat(score).isEqualTo(BendableScore.of(new int[1], new int[] { 0, 1 }));
    }

    @Test
    public void getLevelsSize() {
        assertThat(new BendableScoreDefinition(1, 1).getLevelsSize()).isEqualTo(2);
        assertThat(new BendableScoreDefinition(3, 4).getLevelsSize()).isEqualTo(7);
        assertThat(new BendableScoreDefinition(4, 3).getLevelsSize()).isEqualTo(7);
        assertThat(new BendableScoreDefinition(0, 5).getLevelsSize()).isEqualTo(5);
        assertThat(new BendableScoreDefinition(5, 0).getLevelsSize()).isEqualTo(5);
    }

    @Test
    public void getLevelLabels() {
        assertThat(new BendableScoreDefinition(1, 1).getLevelLabels())
                .isEqualTo(new String[] { "hard 0 score", "soft 0 score" });
        assertThat(new BendableScoreDefinition(3, 4).getLevelLabels())
                .isEqualTo(new String[] { "hard 0 score", "hard 1 score", "hard 2 score", "soft 0 score", "soft 1 score",
                        "soft 2 score", "soft 3 score" });
        assertThat(new BendableScoreDefinition(4, 3).getLevelLabels())
                .isEqualTo(new String[] { "hard 0 score", "hard 1 score", "hard 2 score", "hard 3 score", "soft 0 score",
                        "soft 1 score", "soft 2 score" });
        assertThat(new BendableScoreDefinition(0, 5).getLevelLabels())
                .isEqualTo(new String[] { "soft 0 score", "soft 1 score", "soft 2 score", "soft 3 score", "soft 4 score" });
        assertThat(new BendableScoreDefinition(5, 0).getLevelLabels())
                .isEqualTo(new String[] { "hard 0 score", "hard 1 score", "hard 2 score", "hard 3 score", "hard 4 score" });
    }

    @Test
    public void getFeasibleLevelsSize() {
        assertThat(new BendableScoreDefinition(1, 1).getFeasibleLevelsSize()).isEqualTo(1);
        assertThat(new BendableScoreDefinition(3, 4).getFeasibleLevelsSize()).isEqualTo(3);
        assertThat(new BendableScoreDefinition(4, 3).getFeasibleLevelsSize()).isEqualTo(4);
        assertThat(new BendableScoreDefinition(0, 5).getFeasibleLevelsSize()).isEqualTo(0);
        assertThat(new BendableScoreDefinition(5, 0).getFeasibleLevelsSize()).isEqualTo(5);
    }

    @Test
    public void createScoreWithIllegalArgument() {
        BendableScoreDefinition bendableScoreDefinition = new BendableScoreDefinition(2, 3);
        assertThatIllegalArgumentException().isThrownBy(() -> bendableScoreDefinition.createScore(1, 2, 3));
    }

    @Test
    public void createScore() {
        int hardLevelSize = 3;
        int softLevelSize = 2;
        int levelSize = hardLevelSize + softLevelSize;
        int[] scores = new int[levelSize];
        for (int i = 0; i < levelSize; i++) {
            scores[i] = i;
        }
        BendableScoreDefinition bendableScoreDefinition = new BendableScoreDefinition(hardLevelSize, softLevelSize);
        BendableScore bendableScore = bendableScoreDefinition.createScore(scores);
        assertThat(bendableScore.getHardLevelsSize()).isEqualTo(hardLevelSize);
        assertThat(bendableScore.getSoftLevelsSize()).isEqualTo(softLevelSize);
        for (int i = 0; i < levelSize; i++) {
            if (i < hardLevelSize) {
                assertThat(bendableScore.getHardScore(i)).isEqualTo(scores[i]);
            } else {
                assertThat(bendableScore.getSoftScore(i - hardLevelSize)).isEqualTo(scores[i]);
            }
        }
    }

    @Test
    public void buildOptimisticBoundOnlyUp() {
        BendableScoreDefinition scoreDefinition = new BendableScoreDefinition(2, 3);
        BendableScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 5),
                scoreDefinition.createScore(-1, -2, -3, -4, -5));
        assertThat(optimisticBound.getInitScore()).isEqualTo(0);
        assertThat(optimisticBound.getHardScore(0)).isEqualTo(Integer.MAX_VALUE);
        assertThat(optimisticBound.getHardScore(1)).isEqualTo(Integer.MAX_VALUE);
        assertThat(optimisticBound.getSoftScore(0)).isEqualTo(Integer.MAX_VALUE);
        assertThat(optimisticBound.getSoftScore(1)).isEqualTo(Integer.MAX_VALUE);
        assertThat(optimisticBound.getSoftScore(2)).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void buildOptimisticBoundOnlyDown() {
        BendableScoreDefinition scoreDefinition = new BendableScoreDefinition(2, 3);
        BendableScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 5),
                scoreDefinition.createScore(-1, -2, -3, -4, -5));
        assertThat(optimisticBound.getInitScore()).isEqualTo(0);
        assertThat(optimisticBound.getHardScore(0)).isEqualTo(-1);
        assertThat(optimisticBound.getHardScore(1)).isEqualTo(-2);
        assertThat(optimisticBound.getSoftScore(0)).isEqualTo(-3);
        assertThat(optimisticBound.getSoftScore(1)).isEqualTo(-4);
        assertThat(optimisticBound.getSoftScore(2)).isEqualTo(-5);
    }

    @Test
    public void buildPessimisticBoundOnlyUp() {
        BendableScoreDefinition scoreDefinition = new BendableScoreDefinition(2, 3);
        BendableScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 5),
                scoreDefinition.createScore(-1, -2, -3, -4, -5));
        assertThat(pessimisticBound.getInitScore()).isEqualTo(0);
        assertThat(pessimisticBound.getHardScore(0)).isEqualTo(-1);
        assertThat(pessimisticBound.getHardScore(1)).isEqualTo(-2);
        assertThat(pessimisticBound.getSoftScore(0)).isEqualTo(-3);
        assertThat(pessimisticBound.getSoftScore(1)).isEqualTo(-4);
        assertThat(pessimisticBound.getSoftScore(2)).isEqualTo(-5);
    }

    @Test
    public void buildPessimisticBoundOnlyDown() {
        BendableScoreDefinition scoreDefinition = new BendableScoreDefinition(2, 3);
        BendableScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 5),
                scoreDefinition.createScore(-1, -2, -3, -4, -5));
        assertThat(pessimisticBound.getInitScore()).isEqualTo(0);
        assertThat(pessimisticBound.getHardScore(0)).isEqualTo(Integer.MIN_VALUE);
        assertThat(pessimisticBound.getHardScore(1)).isEqualTo(Integer.MIN_VALUE);
        assertThat(pessimisticBound.getSoftScore(0)).isEqualTo(Integer.MIN_VALUE);
        assertThat(pessimisticBound.getSoftScore(1)).isEqualTo(Integer.MIN_VALUE);
        assertThat(pessimisticBound.getSoftScore(2)).isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    public void divideBySanitizedDivisor() {
        BendableScoreDefinition scoreDefinition = new BendableScoreDefinition(1, 1);
        BendableScore dividend = scoreDefinition.createScoreUninitialized(2, 0, 10);
        BendableScore zeroDivisor = scoreDefinition.getZeroScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, zeroDivisor))
                .isEqualTo(dividend);
        BendableScore oneDivisor = scoreDefinition.getOneSoftestScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, oneDivisor))
                .isEqualTo(dividend);
        BendableScore tenDivisor = scoreDefinition.createScoreUninitialized(10, 10, 10);
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, tenDivisor))
                .isEqualTo(scoreDefinition.createScoreUninitialized(0, 0, 1));
    }

}
