package org.optaplanner.core.impl.score.buildin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

class BendableLongScoreDefinitionTest {

    @Test
    void getZeroScore() {
        BendableLongScore score = new BendableLongScoreDefinition(1, 2).getZeroScore();
        assertThat(score).isEqualTo(BendableLongScore.zero(1, 2));
    }

    @Test
    void getSoftestOneScore() {
        BendableLongScore score = new BendableLongScoreDefinition(1, 2).getOneSoftestScore();
        assertThat(score).isEqualTo(BendableLongScore.of(new long[1], new long[] { 0L, 1L }));
    }

    @Test
    void getLevelsSize() {
        assertThat(new BendableLongScoreDefinition(1, 1).getLevelsSize()).isEqualTo(2);
        assertThat(new BendableLongScoreDefinition(3, 4).getLevelsSize()).isEqualTo(7);
        assertThat(new BendableLongScoreDefinition(4, 3).getLevelsSize()).isEqualTo(7);
        assertThat(new BendableLongScoreDefinition(0, 5).getLevelsSize()).isEqualTo(5);
        assertThat(new BendableLongScoreDefinition(5, 0).getLevelsSize()).isEqualTo(5);
    }

    @Test
    void getLevelLabels() {
        assertThat(new BendableLongScoreDefinition(1, 1).getLevelLabels())
                .isEqualTo(new String[] { "hard 0 score", "soft 0 score" });
        assertThat(new BendableLongScoreDefinition(3, 4).getLevelLabels())
                .isEqualTo(new String[] { "hard 0 score", "hard 1 score", "hard 2 score", "soft 0 score", "soft 1 score",
                        "soft 2 score", "soft 3 score" });
        assertThat(new BendableLongScoreDefinition(4, 3).getLevelLabels())
                .isEqualTo(new String[] { "hard 0 score", "hard 1 score", "hard 2 score", "hard 3 score", "soft 0 score",
                        "soft 1 score", "soft 2 score" });
        assertThat(new BendableLongScoreDefinition(0, 5).getLevelLabels())
                .isEqualTo(new String[] { "soft 0 score", "soft 1 score", "soft 2 score", "soft 3 score", "soft 4 score" });
        assertThat(new BendableLongScoreDefinition(5, 0).getLevelLabels())
                .isEqualTo(new String[] { "hard 0 score", "hard 1 score", "hard 2 score", "hard 3 score", "hard 4 score" });
    }

    @Test
    void getFeasibleLevelsSize() {
        assertThat(new BendableLongScoreDefinition(1, 1).getFeasibleLevelsSize()).isEqualTo(1);
        assertThat(new BendableLongScoreDefinition(3, 4).getFeasibleLevelsSize()).isEqualTo(3);
        assertThat(new BendableLongScoreDefinition(4, 3).getFeasibleLevelsSize()).isEqualTo(4);
        assertThat(new BendableLongScoreDefinition(0, 5).getFeasibleLevelsSize()).isEqualTo(0);
        assertThat(new BendableLongScoreDefinition(5, 0).getFeasibleLevelsSize()).isEqualTo(5);
    }

    @Test
    void createScoreWithIllegalArgument() {
        BendableLongScoreDefinition bendableLongScoreDefinition = new BendableLongScoreDefinition(2, 3);
        assertThatIllegalArgumentException().isThrownBy(() -> bendableLongScoreDefinition.createScore(1, 2, 3));
    }

    @Test
    void createScore() {
        int hardLevelSize = 3;
        int softLevelSize = 2;
        int levelSize = hardLevelSize + softLevelSize;
        long[] scores = new long[levelSize];
        for (int i = 0; i < levelSize; i++) {
            scores[i] = ((long) Integer.MAX_VALUE) + i;
        }
        BendableLongScoreDefinition bendableLongScoreDefinition = new BendableLongScoreDefinition(hardLevelSize, softLevelSize);
        BendableLongScore bendableLongScore = bendableLongScoreDefinition.createScore(scores);
        assertThat(bendableLongScore.getHardLevelsSize()).isEqualTo(hardLevelSize);
        assertThat(bendableLongScore.getSoftLevelsSize()).isEqualTo(softLevelSize);
        for (int i = 0; i < levelSize; i++) {
            if (i < hardLevelSize) {
                assertThat(bendableLongScore.getHardScore(i)).isEqualTo(scores[i]);
            } else {
                assertThat(bendableLongScore.getSoftScore(i - hardLevelSize)).isEqualTo(scores[i]);
            }
        }
    }

    @Test
    void buildOptimisticBoundOnlyUp() {
        BendableLongScoreDefinition scoreDefinition = new BendableLongScoreDefinition(2, 3);
        BendableLongScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 5),
                scoreDefinition.createScore(-1, -2, -3, -4, -5));
        assertThat(optimisticBound.getInitScore()).isEqualTo(0);
        assertThat(optimisticBound.getHardScore(0)).isEqualTo(Long.MAX_VALUE);
        assertThat(optimisticBound.getHardScore(1)).isEqualTo(Long.MAX_VALUE);
        assertThat(optimisticBound.getSoftScore(0)).isEqualTo(Long.MAX_VALUE);
        assertThat(optimisticBound.getSoftScore(1)).isEqualTo(Long.MAX_VALUE);
        assertThat(optimisticBound.getSoftScore(2)).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    void buildOptimisticBoundOnlyDown() {
        BendableLongScoreDefinition scoreDefinition = new BendableLongScoreDefinition(2, 3);
        BendableLongScore optimisticBound = scoreDefinition.buildOptimisticBound(
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
    void buildPessimisticBoundOnlyUp() {
        BendableLongScoreDefinition scoreDefinition = new BendableLongScoreDefinition(2, 3);
        BendableLongScore pessimisticBound = scoreDefinition.buildPessimisticBound(
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
    void buildPessimisticBoundOnlyDown() {
        BendableLongScoreDefinition scoreDefinition = new BendableLongScoreDefinition(2, 3);
        BendableLongScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 5),
                scoreDefinition.createScore(-1, -2, -3, -4, -5));
        assertThat(pessimisticBound.getInitScore()).isEqualTo(0);
        assertThat(pessimisticBound.getHardScore(0)).isEqualTo(Long.MIN_VALUE);
        assertThat(pessimisticBound.getHardScore(1)).isEqualTo(Long.MIN_VALUE);
        assertThat(pessimisticBound.getSoftScore(0)).isEqualTo(Long.MIN_VALUE);
        assertThat(pessimisticBound.getSoftScore(1)).isEqualTo(Long.MIN_VALUE);
        assertThat(pessimisticBound.getSoftScore(2)).isEqualTo(Long.MIN_VALUE);
    }

    @Test
    void divideBySanitizedDivisor() {
        BendableLongScoreDefinition scoreDefinition = new BendableLongScoreDefinition(1, 1);
        BendableLongScore dividend = scoreDefinition.createScoreUninitialized(2, 0, 10);
        BendableLongScore zeroDivisor = scoreDefinition.getZeroScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, zeroDivisor))
                .isEqualTo(dividend);
        BendableLongScore oneDivisor = scoreDefinition.getOneSoftestScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, oneDivisor))
                .isEqualTo(dividend);
        BendableLongScore tenDivisor = scoreDefinition.getOneSoftestScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, tenDivisor))
                .isEqualTo(scoreDefinition.createScoreUninitialized(2, 0, 10));
    }

}
