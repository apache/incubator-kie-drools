package org.optaplanner.core.impl.score.buildin.hardmediumsoftlong;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

import static org.junit.Assert.assertEquals;

public class HardMediumSoftLongScoreDefinitionTest {

    @Test
    public void getLevelsSize() { assertEquals(3, new HardMediumSoftLongScoreDefinition().getLevelsSize()); }

    @Test
    public void getFeasibleLevelsSize() { assertEquals(1, new HardMediumSoftLongScoreDefinition().getFeasibleLevelsSize()); }

    @Test
    public void buildOptimisticBound() {
        int scoreSize = new HardMediumSoftLongScoreDefinition().getLevelsSize();
        InitializingScoreTrendLevel[] levels = new InitializingScoreTrendLevel[scoreSize];
        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_UP;
        }
        HardMediumSoftLongScore score = HardMediumSoftLongScore.parseScore("-999hard/-999medium/-999soft");
        InitializingScoreTrend scoreTrend = new InitializingScoreTrend(levels);
        HardMediumSoftLongScoreDefinition hardMediumSoftLongScoreDefinition = new HardMediumSoftLongScoreDefinition();
        HardMediumSoftLongScore score2 = hardMediumSoftLongScoreDefinition.buildOptimisticBound(scoreTrend, score);

        assertEquals(Long.MAX_VALUE, score2.getHardScore());
        assertEquals(Long.MAX_VALUE, score2.getMediumScore());
        assertEquals(Long.MAX_VALUE, score2.getSoftScore());

        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_DOWN;
        }
        score2 = hardMediumSoftLongScoreDefinition.buildOptimisticBound(scoreTrend, score);
        assertEquals(-999, score2.getHardScore());
        assertEquals(-999, score2.getMediumScore());
        assertEquals(-999, score2.getSoftScore());
    }

    @Test
    public void buildPessimisticBound() {
        int scoreSize = new HardMediumSoftLongScoreDefinition().getLevelsSize();
        InitializingScoreTrendLevel[] levels = new InitializingScoreTrendLevel[scoreSize];
        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_UP;
        }
        HardMediumSoftLongScore score = HardMediumSoftLongScore.parseScore("-999hard/-999medium/-999soft");
        InitializingScoreTrend scoreTrend = new InitializingScoreTrend(levels);
        HardMediumSoftLongScoreDefinition hardMediumSoftLongScoreDefinition = new HardMediumSoftLongScoreDefinition();
        HardMediumSoftLongScore score2 = hardMediumSoftLongScoreDefinition.buildPessimisticBound(scoreTrend, score);

        assertEquals(-999, score2.getHardScore());
        assertEquals(-999, score2.getMediumScore());
        assertEquals(-999, score2.getSoftScore());

        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_DOWN;
        }
        score2 = hardMediumSoftLongScoreDefinition.buildPessimisticBound(scoreTrend, score);

        assertEquals(Long.MIN_VALUE, score2.getHardScore());
        assertEquals(Long.MIN_VALUE, score2.getMediumScore());
        assertEquals(Long.MIN_VALUE, score2.getSoftScore());
    }
}
