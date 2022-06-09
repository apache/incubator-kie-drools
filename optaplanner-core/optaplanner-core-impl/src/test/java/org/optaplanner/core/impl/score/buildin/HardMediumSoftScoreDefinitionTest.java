package org.optaplanner.core.impl.score.buildin;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

class HardMediumSoftScoreDefinitionTest {

    @Test
    void getZeroScore() {
        HardMediumSoftScore score = new HardMediumSoftScoreDefinition().getZeroScore();
        assertThat(score).isEqualTo(HardMediumSoftScore.ZERO);
    }

    @Test
    void getSoftestOneScore() {
        HardMediumSoftScore score = new HardMediumSoftScoreDefinition().getOneSoftestScore();
        assertThat(score).isEqualTo(HardMediumSoftScore.ONE_SOFT);
    }

    @Test
    void getLevelsSize() {
        assertThat(new HardMediumSoftScoreDefinition().getLevelsSize()).isEqualTo(3);
    }

    @Test
    void getLevelLabels() {
        assertThat(new HardMediumSoftScoreDefinition().getLevelLabels())
                .isEqualTo(new String[] { "hard score", "medium score", "soft score" });
    }

    @Test
    void getFeasibleLevelsSize() {
        assertThat(new HardMediumSoftScoreDefinition().getFeasibleLevelsSize()).isEqualTo(1);
    }

    @Test
    void buildOptimisticBoundOnlyUp() {
        HardMediumSoftScoreDefinition scoreDefinition = new HardMediumSoftScoreDefinition();
        HardMediumSoftScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 3),
                HardMediumSoftScore.of(-1, -2, -3));
        assertThat(optimisticBound.getInitScore()).isEqualTo(0);
        assertThat(optimisticBound.getHardScore()).isEqualTo(Integer.MAX_VALUE);
        assertThat(optimisticBound.getMediumScore()).isEqualTo(Integer.MAX_VALUE);
        assertThat(optimisticBound.getSoftScore()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    void buildOptimisticBoundOnlyDown() {
        HardMediumSoftScoreDefinition scoreDefinition = new HardMediumSoftScoreDefinition();
        HardMediumSoftScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 3),
                HardMediumSoftScore.of(-1, -2, -3));
        assertThat(optimisticBound.getInitScore()).isEqualTo(0);
        assertThat(optimisticBound.getHardScore()).isEqualTo(-1);
        assertThat(optimisticBound.getMediumScore()).isEqualTo(-2);
        assertThat(optimisticBound.getSoftScore()).isEqualTo(-3);
    }

    @Test
    void buildPessimisticBoundOnlyUp() {
        HardMediumSoftScoreDefinition scoreDefinition = new HardMediumSoftScoreDefinition();
        HardMediumSoftScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 3),
                HardMediumSoftScore.of(-1, -2, -3));
        assertThat(pessimisticBound.getInitScore()).isEqualTo(0);
        assertThat(pessimisticBound.getHardScore()).isEqualTo(-1);
        assertThat(pessimisticBound.getMediumScore()).isEqualTo(-2);
        assertThat(pessimisticBound.getSoftScore()).isEqualTo(-3);
    }

    @Test
    void buildPessimisticBoundOnlyDown() {
        HardMediumSoftScoreDefinition scoreDefinition = new HardMediumSoftScoreDefinition();
        HardMediumSoftScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 3),
                HardMediumSoftScore.of(-1, -2, -3));
        assertThat(pessimisticBound.getInitScore()).isEqualTo(0);
        assertThat(pessimisticBound.getHardScore()).isEqualTo(Integer.MIN_VALUE);
        assertThat(pessimisticBound.getMediumScore()).isEqualTo(Integer.MIN_VALUE);
        assertThat(pessimisticBound.getSoftScore()).isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    void divideBySanitizedDivisor() {
        HardMediumSoftScoreDefinition scoreDefinition = new HardMediumSoftScoreDefinition();
        HardMediumSoftScore dividend = scoreDefinition.fromLevelNumbers(2, new Number[] { 0, 1, 10 });
        HardMediumSoftScore zeroDivisor = scoreDefinition.getZeroScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, zeroDivisor))
                .isEqualTo(dividend);
        HardMediumSoftScore oneDivisor = scoreDefinition.getOneSoftestScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, oneDivisor))
                .isEqualTo(dividend);
        HardMediumSoftScore tenDivisor = scoreDefinition.fromLevelNumbers(10, new Number[] { 10, 10, 10 });
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, tenDivisor))
                .isEqualTo(scoreDefinition.fromLevelNumbers(0, new Number[] { 0, 0, 1 }));
    }

}
