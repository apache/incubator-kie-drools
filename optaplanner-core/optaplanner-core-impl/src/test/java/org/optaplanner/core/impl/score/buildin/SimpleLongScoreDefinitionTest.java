package org.optaplanner.core.impl.score.buildin;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

class SimpleLongScoreDefinitionTest {

    @Test
    void getZeroScore() {
        SimpleLongScore score = new SimpleLongScoreDefinition().getZeroScore();
        assertThat(score).isEqualTo(SimpleLongScore.ZERO);
    }

    @Test
    void getSoftestOneScore() {
        SimpleLongScore score = new SimpleLongScoreDefinition().getOneSoftestScore();
        assertThat(score).isEqualTo(SimpleLongScore.ONE);
    }

    @Test
    void getLevelSize() {
        assertThat(new SimpleLongScoreDefinition().getLevelsSize()).isEqualTo(1);
    }

    @Test
    void getLevelLabels() {
        assertThat(new SimpleLongScoreDefinition().getLevelLabels()).isEqualTo(new String[] { "score" });
    }

    @Test
    void buildOptimisticBoundOnlyUp() {
        SimpleLongScoreDefinition scoreDefinition = new SimpleLongScoreDefinition();
        SimpleLongScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 1),
                SimpleLongScore.of(-1L));
        assertThat(optimisticBound.getInitScore()).isEqualTo(0);
        assertThat(optimisticBound.getScore()).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    void buildOptimisticBoundOnlyDown() {
        SimpleLongScoreDefinition scoreDefinition = new SimpleLongScoreDefinition();
        SimpleLongScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 1),
                SimpleLongScore.of(-1L));
        assertThat(optimisticBound.getInitScore()).isEqualTo(0);
        assertThat(optimisticBound.getScore()).isEqualTo(-1L);
    }

    @Test
    void buildPessimisticBoundOnlyUp() {
        SimpleLongScoreDefinition scoreDefinition = new SimpleLongScoreDefinition();
        SimpleLongScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 1),
                SimpleLongScore.of(-1L));
        assertThat(pessimisticBound.getInitScore()).isEqualTo(0);
        assertThat(pessimisticBound.getScore()).isEqualTo(-1L);
    }

    @Test
    void buildPessimisticBoundOnlyDown() {
        SimpleLongScoreDefinition scoreDefinition = new SimpleLongScoreDefinition();
        SimpleLongScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 1),
                SimpleLongScore.of(-1L));
        assertThat(pessimisticBound.getInitScore()).isEqualTo(0);
        assertThat(pessimisticBound.getScore()).isEqualTo(Long.MIN_VALUE);
    }

    @Test
    void divideBySanitizedDivisor() {
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
