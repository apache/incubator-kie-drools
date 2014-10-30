package org.optaplanner.core.impl.score.buildin.simplelong;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

import static org.junit.Assert.assertEquals;

public class SimpleLongScoreDefinitionTest {

    @Test
    public void getLevelSize() {
        assertEquals(1, new SimpleLongScoreDefinition().getLevelsSize());
    }

    @Test
    public void buildOptimisticBoundOnlyUp() {
        SimpleLongScoreDefinition scoreDefinition = new SimpleLongScoreDefinition();
        SimpleLongScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 1),
                SimpleLongScore.valueOf(-1L));
        assertEquals(Long.MAX_VALUE, optimisticBound.getScore());
    }

    @Test
    public void buildOptimisticBoundOnlyDown() {
        SimpleLongScoreDefinition scoreDefinition = new SimpleLongScoreDefinition();
        SimpleLongScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 1),
                SimpleLongScore.valueOf(-1L));
        assertEquals(-1L, optimisticBound.getScore());
    }

    @Test
    public void buildPessimisticBoundOnlyUp() {
        SimpleLongScoreDefinition scoreDefinition = new SimpleLongScoreDefinition();
        SimpleLongScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 1),
                SimpleLongScore.valueOf(-1L));
        assertEquals(-1L, pessimisticBound.getScore());
    }

    @Test
    public void buildPessimisticBoundOnlyDown() {
        SimpleLongScoreDefinition scoreDefinition = new SimpleLongScoreDefinition();
        SimpleLongScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 1),
                SimpleLongScore.valueOf(-1L));
        assertEquals(Long.MIN_VALUE, pessimisticBound.getScore());
    }

}
