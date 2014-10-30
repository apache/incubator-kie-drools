package org.optaplanner.core.impl.score.buildin.hardmediumsoftlong;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

import static org.junit.Assert.assertEquals;

public class HardMediumSoftLongScoreDefinitionTest {

    @Test
    public void getLevelsSize() {
        assertEquals(3, new HardMediumSoftLongScoreDefinition().getLevelsSize());
    }

    @Test
    public void getFeasibleLevelsSize() {
        assertEquals(1, new HardMediumSoftLongScoreDefinition().getFeasibleLevelsSize());
    }

    @Test
    public void buildOptimisticBoundOnlyUp() {
        HardMediumSoftLongScoreDefinition scoreDefinition = new HardMediumSoftLongScoreDefinition();
        HardMediumSoftLongScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 3),
                HardMediumSoftLongScore.valueOf(-1L, -2L, -3L));
        assertEquals(Long.MAX_VALUE, optimisticBound.getHardScore());
        assertEquals(Long.MAX_VALUE, optimisticBound.getMediumScore());
        assertEquals(Long.MAX_VALUE, optimisticBound.getSoftScore());
    }

    @Test
    public void buildOptimisticBoundOnlyDown() {
        HardMediumSoftLongScoreDefinition scoreDefinition = new HardMediumSoftLongScoreDefinition();
        HardMediumSoftLongScore optimisticBound = scoreDefinition.buildOptimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 3),
                HardMediumSoftLongScore.valueOf(-1L, -2L, -3L));
        assertEquals(-1L, optimisticBound.getHardScore());
        assertEquals(-2L, optimisticBound.getMediumScore());
        assertEquals(-3L, optimisticBound.getSoftScore());
    }

    @Test
    public void buildPessimisticBoundOnlyUp() {
        HardMediumSoftLongScoreDefinition scoreDefinition = new HardMediumSoftLongScoreDefinition();
        HardMediumSoftLongScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_UP, 3),
                HardMediumSoftLongScore.valueOf(-1L, -2L, -3L));
        assertEquals(-1L, pessimisticBound.getHardScore());
        assertEquals(-2L, pessimisticBound.getMediumScore());
        assertEquals(-3L, pessimisticBound.getSoftScore());
    }

    @Test
    public void buildPessimisticBoundOnlyDown() {
        HardMediumSoftLongScoreDefinition scoreDefinition = new HardMediumSoftLongScoreDefinition();
        HardMediumSoftLongScore pessimisticBound = scoreDefinition.buildPessimisticBound(
                InitializingScoreTrend.buildUniformTrend(InitializingScoreTrendLevel.ONLY_DOWN, 3),
                HardMediumSoftLongScore.valueOf(-1L, -2L, -3L));
        assertEquals(Long.MIN_VALUE, pessimisticBound.getHardScore());
        assertEquals(Long.MIN_VALUE, pessimisticBound.getMediumScore());
        assertEquals(Long.MIN_VALUE, pessimisticBound.getSoftScore());
    }

}
