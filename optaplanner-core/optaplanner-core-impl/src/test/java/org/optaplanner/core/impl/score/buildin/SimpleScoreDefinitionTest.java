package org.optaplanner.core.impl.score.buildin;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

class SimpleScoreDefinitionTest {

    @Test
    void getZeroScore() {
        SimpleScore score = new SimpleScoreDefinition().getZeroScore();
        assertThat(score).isEqualTo(SimpleScore.ZERO);
    }

    @Test
    void getSoftestOneScore() {
        SimpleScore score = new SimpleScoreDefinition().getOneSoftestScore();
        assertThat(score).isEqualTo(SimpleScore.ONE);
    }

    @Test
    void getLevelsSize() {
        assertThat(new SimpleScoreDefinition().getLevelsSize()).isEqualTo(1);
    }

    @Test
    void getLevelLabels() {
        assertThat(new SimpleScoreDefinition().getLevelLabels()).isEqualTo(new String[] { "score" });
    }

    @Test
    void buildOptimisticBoundOnlyUp() {
        SimpleScoreDefinition scoreDefinition = new SimpleScoreDefinition();
        SimpleScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 1),
                SimpleScore.of(-1));
        assertThat(optimisticBound.getInitScore()).isEqualTo(0);
        assertThat(optimisticBound.getScore()).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    void buildOptimisticBoundOnlyDown() {
        SimpleScoreDefinition scoreDefinition = new SimpleScoreDefinition();
        SimpleScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 1),
                SimpleScore.of(-1));
        assertThat(optimisticBound.getInitScore()).isEqualTo(0);
        assertThat(optimisticBound.getScore()).isEqualTo(-1);
    }

    @Test
    void buildPessimisticBoundOnlyUp() {
        SimpleScoreDefinition scoreDefinition = new SimpleScoreDefinition();
        SimpleScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 1),
                SimpleScore.of(-1));
        assertThat(pessimisticBound.getInitScore()).isEqualTo(0);
        assertThat(pessimisticBound.getScore()).isEqualTo(-1);
    }

    @Test
    void buildPessimisticBoundOnlyDown() {
        SimpleScoreDefinition scoreDefinition = new SimpleScoreDefinition();
        SimpleScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 1),
                SimpleScore.of(-1));
        assertThat(pessimisticBound.getInitScore()).isEqualTo(0);
        assertThat(pessimisticBound.getScore()).isEqualTo(Integer.MIN_VALUE);
    }

    @Test
    void divideBySanitizedDivisor() {
        SimpleScoreDefinition scoreDefinition = new SimpleScoreDefinition();
        SimpleScore dividend = scoreDefinition.fromLevelNumbers(2, new Number[] { 10 });
        SimpleScore zeroDivisor = scoreDefinition.getZeroScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, zeroDivisor))
                .isEqualTo(dividend);
        SimpleScore oneDivisor = scoreDefinition.getOneSoftestScore();
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, oneDivisor))
                .isEqualTo(dividend);
        SimpleScore tenDivisor = scoreDefinition.fromLevelNumbers(10, new Number[] { 10 });
        assertThat(scoreDefinition.divideBySanitizedDivisor(dividend, tenDivisor))
                .isEqualTo(scoreDefinition.fromLevelNumbers(0, new Number[] { 1 }));
    }

}
