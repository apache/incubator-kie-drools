package org.optaplanner.core.impl.score.buildin.hardsoftlong;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

import static org.junit.Assert.assertEquals;

public class HardSoftLongScoreDefinitionTest {

    @Test
    public void getLevelSize() { assertEquals(2, new HardSoftLongScoreDefinition().getLevelsSize()); }

    @Test
    public void getFeasibleLevelsSize() { assertEquals(1, new HardSoftLongScoreDefinition().getFeasibleLevelsSize()); }

    @Test
    public void buildOptimisticBound() {
        int scoreSize = new HardSoftLongScoreDefinition().getLevelsSize();
        InitializingScoreTrendLevel[] levels = new InitializingScoreTrendLevel[scoreSize];
        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_UP;
        }
        HardSoftLongScore score = HardSoftLongScore.parseScore("-999hard/-999soft");
        InitializingScoreTrend scoreTrend = new InitializingScoreTrend(levels);
        HardSoftLongScoreDefinition hardSoftLongScoreDefinition = new HardSoftLongScoreDefinition();
        HardSoftLongScore score2 = hardSoftLongScoreDefinition.buildOptimisticBound(scoreTrend, score);

        assertEquals(Long.MAX_VALUE, score2.getHardScore());
        assertEquals(Long.MAX_VALUE, score2.getSoftScore());

        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_DOWN;
        }
        score2 = hardSoftLongScoreDefinition.buildOptimisticBound(scoreTrend, score);
        assertEquals(-999, score2.getHardScore());
        assertEquals(-999, score2.getSoftScore());
    }

    @Test
    public void buildPessimisticBound() {
        int scoreSize = new HardSoftLongScoreDefinition().getLevelsSize();
        InitializingScoreTrendLevel[] levels = new InitializingScoreTrendLevel[scoreSize];
        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_UP;
        }
        HardSoftLongScore score = HardSoftLongScore.parseScore("-999hard/-999soft");
        InitializingScoreTrend scoreTrend = new InitializingScoreTrend(levels);
        HardSoftLongScoreDefinition hardSoftScoreDefinition = new HardSoftLongScoreDefinition();
        HardSoftLongScore score2 = hardSoftScoreDefinition.buildPessimisticBound(scoreTrend, score);

        assertEquals(-999, score2.getHardScore());
        assertEquals(-999, score2.getSoftScore());

        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_DOWN;
        }
        score2 = hardSoftScoreDefinition.buildPessimisticBound(scoreTrend, score);

        assertEquals(Long.MIN_VALUE, score2.getHardScore());
        assertEquals(Long.MIN_VALUE, score2.getSoftScore());
    }
}
