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
    public void buildOptimisticBoundUp() {
        int scoreSize = new HardMediumSoftLongScoreDefinition().getLevelsSize();
        HardMediumSoftLongScore score = HardMediumSoftLongScore.parseScore("-1hard/-2medium/-3soft");
        InitializingScoreTrend scoreTrend = InitializingScoreTrend.buildUniformTrend(
                InitializingScoreTrendLevel.ONLY_UP, scoreSize);
        HardMediumSoftLongScoreDefinition hardMediumSoftLongScoreDefinition = new HardMediumSoftLongScoreDefinition();
        HardMediumSoftLongScore score2 = hardMediumSoftLongScoreDefinition.buildOptimisticBound(scoreTrend, score);
        assertEquals(Long.MAX_VALUE, score2.getHardScore());
        assertEquals(Long.MAX_VALUE, score2.getMediumScore());
        assertEquals(Long.MAX_VALUE, score2.getSoftScore());


    }

    @Test
    public void buildOptimisticBoundDown() {
        int scoreSize = new HardMediumSoftLongScoreDefinition().getLevelsSize();
        HardMediumSoftLongScore score = HardMediumSoftLongScore.parseScore("-1hard/-2medium/-3soft");
        InitializingScoreTrend scoreTrend = InitializingScoreTrend.buildUniformTrend(
                InitializingScoreTrendLevel.ONLY_DOWN, scoreSize);
        HardMediumSoftLongScoreDefinition hardMediumSoftLongScoreDefinition = new HardMediumSoftLongScoreDefinition();
        HardMediumSoftLongScore score2 = hardMediumSoftLongScoreDefinition.buildOptimisticBound(scoreTrend, score);
        assertEquals(-1, score2.getHardScore());
        assertEquals(-2, score2.getMediumScore());
        assertEquals(-3, score2.getSoftScore());
    }

    @Test
    public void buildPessimisticBoundUp() {
        int scoreSize = new HardMediumSoftLongScoreDefinition().getLevelsSize();
        HardMediumSoftLongScore score = HardMediumSoftLongScore.parseScore("-1hard/-2medium/-3soft");
        InitializingScoreTrend scoreTrend = InitializingScoreTrend.buildUniformTrend(
                InitializingScoreTrendLevel.ONLY_UP, scoreSize);
        HardMediumSoftLongScoreDefinition hardMediumSoftLongScoreDefinition = new HardMediumSoftLongScoreDefinition();
        HardMediumSoftLongScore score2 = hardMediumSoftLongScoreDefinition.buildPessimisticBound(scoreTrend, score);
        assertEquals(-1, score2.getHardScore());
        assertEquals(-2, score2.getMediumScore());
        assertEquals(-3, score2.getSoftScore());
    }

    @Test
    public void buildPessimisticBoundDown() {
        int scoreSize = new HardMediumSoftLongScoreDefinition().getLevelsSize();
        HardMediumSoftLongScore score = HardMediumSoftLongScore.parseScore("-1hard/-2medium/-3soft");
        InitializingScoreTrend scoreTrend = InitializingScoreTrend.buildUniformTrend(
                InitializingScoreTrendLevel.ONLY_DOWN, scoreSize);
        HardMediumSoftLongScoreDefinition hardMediumSoftLongScoreDefinition = new HardMediumSoftLongScoreDefinition();
        HardMediumSoftLongScore score2 = hardMediumSoftLongScoreDefinition.buildPessimisticBound(scoreTrend, score);
        assertEquals(Long.MIN_VALUE, score2.getHardScore());
        assertEquals(Long.MIN_VALUE, score2.getMediumScore());
        assertEquals(Long.MIN_VALUE, score2.getSoftScore());
    }
}
