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
        assertThat(new HardSoftLongScoreDefinition().getLevelLabels()).isEqualTo(new String[] { "hard score", "soft score" });
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
        assertThat(optimisticBound.getInitScore()).isEqualTo(0);
        assertThat(optimisticBound.getHardScore()).isEqualTo(Long.MAX_VALUE);
        assertThat(optimisticBound.getSoftScore()).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    void buildOptimisticBoundOnlyDown() {
        HardSoftLongScoreDefinition scoreDefinition = new HardSoftLongScoreDefinition();
        HardSoftLongScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 2),
                HardSoftLongScore.of(-1L, -2L));
        assertThat(optimisticBound.getInitScore()).isEqualTo(0);
        assertThat(optimisticBound.getHardScore()).isEqualTo(-1L);
        assertThat(optimisticBound.getSoftScore()).isEqualTo(-2L);
    }

    @Test
    void buildPessimisticBoundOnlyUp() {
        HardSoftLongScoreDefinition scoreDefinition = new HardSoftLongScoreDefinition();
        HardSoftLongScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 2),
                HardSoftLongScore.of(-1L, -2L));
        assertThat(pessimisticBound.getInitScore()).isEqualTo(0);
        assertThat(pessimisticBound.getHardScore()).isEqualTo(-1L);
        assertThat(pessimisticBound.getSoftScore()).isEqualTo(-2L);
    }

    @Test
    void buildPessimisticBoundOnlyDown() {
        HardSoftLongScoreDefinition scoreDefinition = new HardSoftLongScoreDefinition();
        HardSoftLongScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 2),
                HardSoftLongScore.of(-1L, -2L));
        assertThat(pessimisticBound.getInitScore()).isEqualTo(0);
        assertThat(pessimisticBound.getHardScore()).isEqualTo(Long.MIN_VALUE);
        assertThat(pessimisticBound.getSoftScore()).isEqualTo(Long.MIN_VALUE);
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
