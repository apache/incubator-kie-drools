package org.optaplanner.core.impl.score.buildin.hardsoftlong;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

import static org.junit.Assert.assertEquals;

public class HardSoftLongScoreDefinitionTest {

    @Test
    public void getLevelSize() {
        assertEquals(2, new HardSoftLongScoreDefinition().getLevelsSize());
    }

    @Test
    public void getFeasibleLevelsSize() {
        assertEquals(1, new HardSoftLongScoreDefinition().getFeasibleLevelsSize());
    }

    @Test
    public void buildOptimisticBoundOnlyUp() {
        HardSoftLongScoreDefinition scoreDefinition = new HardSoftLongScoreDefinition();
        HardSoftLongScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 2),
                HardSoftLongScore.valueOf(-1L, -2L));
        assertEquals(Long.MAX_VALUE, optimisticBound.getHardScore());
        assertEquals(Long.MAX_VALUE, optimisticBound.getSoftScore());
    }

    @Test
    public void buildOptimisticBoundOnlyDown() {
        HardSoftLongScoreDefinition scoreDefinition = new HardSoftLongScoreDefinition();
        HardSoftLongScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 2),
                HardSoftLongScore.valueOf(-1L, -2L));
        assertEquals(-1L, optimisticBound.getHardScore());
        assertEquals(-2L, optimisticBound.getSoftScore());
    }

    @Test
    public void buildPessimisticBoundOnlyUp() {
        HardSoftLongScoreDefinition scoreDefinition = new HardSoftLongScoreDefinition();
        HardSoftLongScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 2),
                HardSoftLongScore.valueOf(-1L, -2L));
        assertEquals(-1L, pessimisticBound.getHardScore());
        assertEquals(-2L, pessimisticBound.getSoftScore());
    }

    @Test
    public void buildPessimisticBoundOnlyDown() {
        HardSoftLongScoreDefinition scoreDefinition = new HardSoftLongScoreDefinition();
        HardSoftLongScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 2),
                HardSoftLongScore.valueOf(-1L, -2L));
        assertEquals(Long.MIN_VALUE, pessimisticBound.getHardScore());
        assertEquals(Long.MIN_VALUE, pessimisticBound.getSoftScore());
    }

}
