package org.optaplanner.core.impl.score.buildin.hardmediumsoft;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

import static org.junit.Assert.assertEquals;

public class HardMediumSoftScoreDefinitionTest {

    @Test
    public void getLevelsSize() { assertEquals(3, new HardMediumSoftScoreDefinition().getLevelsSize()); }

    @Test
    public void getFeasibleLevelsSize() { assertEquals(1, new HardMediumSoftScoreDefinition().getFeasibleLevelsSize()); }

    @Test
    public void buildOptimisticBound() {
        int scoreSize = new HardMediumSoftScoreDefinition().getLevelsSize();
        InitializingScoreTrendLevel[] levels = new InitializingScoreTrendLevel[scoreSize];
        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_UP;
        }
        HardMediumSoftScore score = HardMediumSoftScore.parseScore("-999hard/-999medium/-999soft");
        InitializingScoreTrend scoreTrend = new InitializingScoreTrend(levels);
        HardMediumSoftScoreDefinition hardMediumSoftScoreDefinition = new HardMediumSoftScoreDefinition();
        HardMediumSoftScore score2 = hardMediumSoftScoreDefinition.buildOptimisticBound(scoreTrend, score);

        assertEquals(Integer.MAX_VALUE, score2.getHardScore());
        assertEquals(Integer.MAX_VALUE, score2.getMediumScore());
        assertEquals(Integer.MAX_VALUE, score2.getSoftScore());

        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_DOWN;
        }
        score2 = hardMediumSoftScoreDefinition.buildOptimisticBound(scoreTrend, score);
        assertEquals(-999, score2.getHardScore());
        assertEquals(-999, score2.getMediumScore());
        assertEquals(-999, score2.getSoftScore());
    }

    @Test
    public void buildPessimisticBound() {
        int scoreSize = new HardMediumSoftScoreDefinition().getLevelsSize();
        InitializingScoreTrendLevel[] levels = new InitializingScoreTrendLevel[scoreSize];
        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_UP;
        }
        HardMediumSoftScore score = HardMediumSoftScore.parseScore("-999hard/-999medium/-999soft");
        InitializingScoreTrend scoreTrend = new InitializingScoreTrend(levels);
        HardMediumSoftScoreDefinition hardMediumSoftScoreDefinition = new HardMediumSoftScoreDefinition();
        HardMediumSoftScore score2 = hardMediumSoftScoreDefinition.buildPessimisticBound(scoreTrend, score);

        assertEquals(-999, score2.getHardScore());
        assertEquals(-999, score2.getMediumScore());
        assertEquals(-999, score2.getSoftScore());

        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_DOWN;
        }
        score2 = hardMediumSoftScoreDefinition.buildPessimisticBound(scoreTrend, score);

        assertEquals(Integer.MIN_VALUE, score2.getHardScore());
        assertEquals(Integer.MIN_VALUE, score2.getMediumScore());
        assertEquals(Integer.MIN_VALUE, score2.getSoftScore());
    }
}
