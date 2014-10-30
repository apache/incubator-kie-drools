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
    public void buildOptimisticBoundUp() {
        int scoreSize = new HardSoftLongScoreDefinition().getLevelsSize();
        HardSoftLongScore score = HardSoftLongScore.parseScore("-1hard/-2soft");
        InitializingScoreTrend scoreTrend = InitializingScoreTrend.buildUniformTrend(
                InitializingScoreTrendLevel.ONLY_UP, scoreSize);
        HardSoftLongScoreDefinition hardSoftLongScoreDefinition = new HardSoftLongScoreDefinition();
        HardSoftLongScore score2 = hardSoftLongScoreDefinition.buildOptimisticBound(scoreTrend, score);
        assertEquals(Long.MAX_VALUE, score2.getHardScore());
        assertEquals(Long.MAX_VALUE, score2.getSoftScore());
    }

    @Test
    public void buildOptimisticBoundDown() {
        int scoreSize = new HardSoftLongScoreDefinition().getLevelsSize();
        HardSoftLongScore score = HardSoftLongScore.parseScore("-1hard/-2soft");
        InitializingScoreTrend scoreTrend = InitializingScoreTrend.buildUniformTrend(
                InitializingScoreTrendLevel.ONLY_DOWN, scoreSize);
        HardSoftLongScoreDefinition hardSoftLongScoreDefinition = new HardSoftLongScoreDefinition();
        HardSoftLongScore score2 = hardSoftLongScoreDefinition.buildOptimisticBound(scoreTrend, score);
        assertEquals(-1, score2.getHardScore());
        assertEquals(-2, score2.getSoftScore());
    }

    @Test
    public void buildPessimisticBoundUp() {
        int scoreSize = new HardSoftLongScoreDefinition().getLevelsSize();
        HardSoftLongScore score = HardSoftLongScore.parseScore("-1hard/-2soft");
        InitializingScoreTrend scoreTrend = InitializingScoreTrend.buildUniformTrend(
                InitializingScoreTrendLevel.ONLY_UP, scoreSize);
        HardSoftLongScoreDefinition hardSoftScoreDefinition = new HardSoftLongScoreDefinition();
        HardSoftLongScore score2 = hardSoftScoreDefinition.buildPessimisticBound(scoreTrend, score);
        assertEquals(-1, score2.getHardScore());
        assertEquals(-2, score2.getSoftScore());
    }

    @Test
    public void buildPessimisticBoundDown() {
        int scoreSize = new HardSoftLongScoreDefinition().getLevelsSize();
        HardSoftLongScore score = HardSoftLongScore.parseScore("-1hard/-2soft");
        InitializingScoreTrend scoreTrend = InitializingScoreTrend.buildUniformTrend(
                InitializingScoreTrendLevel.ONLY_DOWN, scoreSize);
        HardSoftLongScoreDefinition hardSoftScoreDefinition = new HardSoftLongScoreDefinition();
        HardSoftLongScore score2 = hardSoftScoreDefinition.buildPessimisticBound(scoreTrend, score);
        assertEquals(Long.MIN_VALUE, score2.getHardScore());
        assertEquals(Long.MIN_VALUE, score2.getSoftScore());
    }
}
