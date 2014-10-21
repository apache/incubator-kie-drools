package org.optaplanner.core.impl.score.buildin.hardsoftdouble;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.hardsoftdouble.HardSoftDoubleScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

import static org.junit.Assert.assertEquals;

public class HardSoftDoubleScoreDefinitionTest {

    private static double TOLERANCE = 0.00001;

    @Test
    public void getLevelSize() { assertEquals(2, new HardSoftDoubleScoreDefinition().getLevelsSize()); }

    @Test
    public void getFeasibleLevelsSize() { assertEquals(1, new HardSoftDoubleScoreDefinition().getFeasibleLevelsSize()); }

    @Test
    public void buildOptimisticBound() {
        int scoreSize = new HardSoftDoubleScoreDefinition().getLevelsSize();
        InitializingScoreTrendLevel[] levels = new InitializingScoreTrendLevel[scoreSize];
        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_UP;
        }
        HardSoftDoubleScore score = HardSoftDoubleScore.parseScore("-999hard/-999soft");
        InitializingScoreTrend scoreTrend = new InitializingScoreTrend(levels);
        HardSoftDoubleScoreDefinition hardSoftScoreDefinition = new HardSoftDoubleScoreDefinition();
        HardSoftDoubleScore score2 = hardSoftScoreDefinition.buildOptimisticBound(scoreTrend, score);

        assertEquals(Double.POSITIVE_INFINITY, score2.getHardScore(), TOLERANCE);
        assertEquals(Double.POSITIVE_INFINITY, score2.getSoftScore(), TOLERANCE);

        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_DOWN;
        }
        score2 = hardSoftScoreDefinition.buildOptimisticBound(scoreTrend, score);
        assertEquals(-999, score2.getHardScore(), TOLERANCE);
        assertEquals(-999, score2.getSoftScore(), TOLERANCE);
    }

    @Test
    public void buildPessimisticBound() {
        int scoreSize = new HardSoftDoubleScoreDefinition().getLevelsSize();
        InitializingScoreTrendLevel[] levels = new InitializingScoreTrendLevel[scoreSize];
        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_UP;
        }
        HardSoftDoubleScore score = HardSoftDoubleScore.parseScore("-999hard/-999soft");
        InitializingScoreTrend scoreTrend = new InitializingScoreTrend(levels);
        HardSoftDoubleScoreDefinition hardSoftScoreDefinition = new HardSoftDoubleScoreDefinition();
        HardSoftDoubleScore score2 = hardSoftScoreDefinition.buildPessimisticBound(scoreTrend, score);

        assertEquals(-999, score2.getHardScore(), TOLERANCE);
        assertEquals(-999, score2.getSoftScore(), TOLERANCE);

        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_DOWN;
        }
        score2 = hardSoftScoreDefinition.buildPessimisticBound(scoreTrend, score);

        assertEquals(Double.NEGATIVE_INFINITY, score2.getHardScore(), TOLERANCE);
        assertEquals(Double.NEGATIVE_INFINITY, score2.getSoftScore(), TOLERANCE);
    }
}
