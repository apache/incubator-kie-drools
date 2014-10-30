package org.optaplanner.core.impl.score.buildin.simpledouble;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simpledouble.SimpleDoubleScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

import static org.junit.Assert.*;

public class SimpleDoubleScoreDefinitionTest {

    @Test
    public void getLevelSize() {
        assertEquals(1, new SimpleDoubleScoreDefinition().getLevelsSize());
    }

    @Test
    public void buildOptimisticBoundOnlyUp() {
        SimpleDoubleScoreDefinition scoreDefinition = new SimpleDoubleScoreDefinition();
        SimpleDoubleScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 1),
                SimpleDoubleScore.valueOf(-1.7));
        assertEquals(Double.POSITIVE_INFINITY, optimisticBound.getScore(), 0.0);
    }

    @Test
    public void buildOptimisticBoundOnlyDown() {
        SimpleDoubleScoreDefinition scoreDefinition = new SimpleDoubleScoreDefinition();
        SimpleDoubleScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 1),
                SimpleDoubleScore.valueOf(-1.7));
        assertEquals(-1.7, optimisticBound.getScore(), 0.0);
    }

    @Test
    public void buildPessimisticBoundOnlyUp() {
        SimpleDoubleScoreDefinition scoreDefinition = new SimpleDoubleScoreDefinition();
        SimpleDoubleScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 1),
                SimpleDoubleScore.valueOf(-1.7));
        assertEquals(-1.7, pessimisticBound.getScore(), 0.0);
    }

    @Test
    public void buildPessimisticBoundOnlyDown() {
        SimpleDoubleScoreDefinition scoreDefinition = new SimpleDoubleScoreDefinition();
        SimpleDoubleScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 1),
                SimpleDoubleScore.valueOf(-1.7));
        assertEquals(Double.NEGATIVE_INFINITY, pessimisticBound.getScore(), 0.0);
    }

}
