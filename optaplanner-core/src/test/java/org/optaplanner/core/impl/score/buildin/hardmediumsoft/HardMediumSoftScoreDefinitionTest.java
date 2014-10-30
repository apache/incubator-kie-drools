package org.optaplanner.core.impl.score.buildin.hardmediumsoft;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.buildin.InitializingScoreTrendLevelFactory;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

import static org.junit.Assert.assertEquals;

public class HardMediumSoftScoreDefinitionTest {

    @Test
    public void getLevelsSize() {
        assertEquals(3, new HardMediumSoftScoreDefinition().getLevelsSize());
    }

    @Test
    public void getFeasibleLevelsSize() {
        assertEquals(1, new HardMediumSoftScoreDefinition().getFeasibleLevelsSize());
    }

    @Test
    public void buildOptimisticBoundUp() {
        int scoreSize = new HardMediumSoftScoreDefinition().getLevelsSize();
        HardMediumSoftScore score = HardMediumSoftScore.parseScore("-1hard/-2medium/-3soft");
        InitializingScoreTrend scoreTrend = InitializingScoreTrendLevelFactory
                .createInitializingScoreTrendLevelArray(scoreSize, InitializingScoreTrendLevel.ONLY_UP);
        HardMediumSoftScoreDefinition hardMediumSoftScoreDefinition = new HardMediumSoftScoreDefinition();
        HardMediumSoftScore score2 = hardMediumSoftScoreDefinition.buildOptimisticBound(scoreTrend, score);
        assertEquals(Integer.MAX_VALUE, score2.getHardScore());
        assertEquals(Integer.MAX_VALUE, score2.getMediumScore());
        assertEquals(Integer.MAX_VALUE, score2.getSoftScore());
    }

    @Test
    public void buildOptimisticBoundDown() {int scoreSize = new HardMediumSoftScoreDefinition().getLevelsSize();
        HardMediumSoftScore score = HardMediumSoftScore.parseScore("-1hard/-2medium/-3soft");
        InitializingScoreTrend scoreTrend = InitializingScoreTrendLevelFactory
                .createInitializingScoreTrendLevelArray(scoreSize, InitializingScoreTrendLevel.ONLY_DOWN);
        HardMediumSoftScoreDefinition hardMediumSoftScoreDefinition = new HardMediumSoftScoreDefinition();
        HardMediumSoftScore score2 = hardMediumSoftScoreDefinition.buildOptimisticBound(scoreTrend, score);
        assertEquals(-1, score2.getHardScore());
        assertEquals(-2, score2.getMediumScore());
        assertEquals(-3, score2.getSoftScore());
    }

    @Test
    public void buildPessimisticBoundUp() {
        int scoreSize = new HardMediumSoftScoreDefinition().getLevelsSize();
        HardMediumSoftScore score = HardMediumSoftScore.parseScore("-1hard/-2medium/-3soft");
        InitializingScoreTrend scoreTrend = InitializingScoreTrendLevelFactory
                .createInitializingScoreTrendLevelArray(scoreSize, InitializingScoreTrendLevel.ONLY_UP);
        HardMediumSoftScoreDefinition hardMediumSoftScoreDefinition = new HardMediumSoftScoreDefinition();
        HardMediumSoftScore score2 = hardMediumSoftScoreDefinition.buildPessimisticBound(scoreTrend, score);
        assertEquals(-1, score2.getHardScore());
        assertEquals(-2, score2.getMediumScore());
        assertEquals(-3, score2.getSoftScore());
    }

    @Test
    public void buildPessimisticBoundDown() {
        int scoreSize = new HardMediumSoftScoreDefinition().getLevelsSize();
        HardMediumSoftScore score = HardMediumSoftScore.parseScore("-1hard/-2medium/-3soft");
        InitializingScoreTrend scoreTrend = InitializingScoreTrendLevelFactory
                .createInitializingScoreTrendLevelArray(scoreSize, InitializingScoreTrendLevel.ONLY_DOWN);
        HardMediumSoftScoreDefinition hardMediumSoftScoreDefinition = new HardMediumSoftScoreDefinition();
        HardMediumSoftScore score2 = hardMediumSoftScoreDefinition.buildPessimisticBound(scoreTrend, score);

        assertEquals(Integer.MIN_VALUE, score2.getHardScore());
        assertEquals(Integer.MIN_VALUE, score2.getMediumScore());
        assertEquals(Integer.MIN_VALUE, score2.getSoftScore());
    }

}
