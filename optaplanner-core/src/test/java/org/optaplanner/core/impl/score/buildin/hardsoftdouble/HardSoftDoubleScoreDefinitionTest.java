package org.optaplanner.core.impl.score.buildin.hardsoftdouble;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.hardsoftdouble.HardSoftDoubleScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.buildin.InitializingScoreTrendLevelFactory;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

import static org.junit.Assert.assertEquals;

public class HardSoftDoubleScoreDefinitionTest {

    private static double TOLERANCE = 0.00001;

    @Test
    public void getLevelSize() { assertEquals(2, new HardSoftDoubleScoreDefinition().getLevelsSize()); }

    @Test
    public void getFeasibleLevelsSize() { assertEquals(1, new HardSoftDoubleScoreDefinition().getFeasibleLevelsSize()); }

    @Test
    public void buildOptimisticBoundUp() {
        int scoreSize = new HardSoftDoubleScoreDefinition().getLevelsSize();
        HardSoftDoubleScore score = HardSoftDoubleScore.parseScore("-1hard/-2soft");
        InitializingScoreTrend scoreTrend = InitializingScoreTrendLevelFactory
                .createInitializingScoreTrendLevelArray(scoreSize, InitializingScoreTrendLevel.ONLY_UP);
        HardSoftDoubleScoreDefinition hardSoftScoreDefinition = new HardSoftDoubleScoreDefinition();
        HardSoftDoubleScore score2 = hardSoftScoreDefinition.buildOptimisticBound(scoreTrend, score);
        assertEquals(Double.POSITIVE_INFINITY, score2.getHardScore(), TOLERANCE);
        assertEquals(Double.POSITIVE_INFINITY, score2.getSoftScore(), TOLERANCE);
    }

    @Test
    public void buildOptimisticBoundDown() {
        int scoreSize = new HardSoftDoubleScoreDefinition().getLevelsSize();
        HardSoftDoubleScore score = HardSoftDoubleScore.parseScore("-1hard/-2soft");
        InitializingScoreTrend scoreTrend = InitializingScoreTrendLevelFactory
                .createInitializingScoreTrendLevelArray(scoreSize, InitializingScoreTrendLevel.ONLY_DOWN);
        HardSoftDoubleScoreDefinition hardSoftScoreDefinition = new HardSoftDoubleScoreDefinition();
        HardSoftDoubleScore score2 = hardSoftScoreDefinition.buildOptimisticBound(scoreTrend, score);
        assertEquals(-1, score2.getHardScore(), TOLERANCE);
        assertEquals(-2, score2.getSoftScore(), TOLERANCE);
    }

    @Test
    public void buildPessimisticBoundUp() {
        int scoreSize = new HardSoftDoubleScoreDefinition().getLevelsSize();
        HardSoftDoubleScore score = HardSoftDoubleScore.parseScore("-1hard/-2soft");
        InitializingScoreTrend scoreTrend = InitializingScoreTrendLevelFactory
                .createInitializingScoreTrendLevelArray(scoreSize, InitializingScoreTrendLevel.ONLY_UP);
        HardSoftDoubleScoreDefinition hardSoftScoreDefinition = new HardSoftDoubleScoreDefinition();
        HardSoftDoubleScore score2 = hardSoftScoreDefinition.buildPessimisticBound(scoreTrend, score);
        assertEquals(-1, score2.getHardScore(), TOLERANCE);
        assertEquals(-2, score2.getSoftScore(), TOLERANCE);
    }

    @Test
    public void buildPessimisticBoundDown() {
        int scoreSize = new HardSoftDoubleScoreDefinition().getLevelsSize();
        HardSoftDoubleScore score = HardSoftDoubleScore.parseScore("-1hard/-2soft");
        InitializingScoreTrend scoreTrend = InitializingScoreTrendLevelFactory
                .createInitializingScoreTrendLevelArray(scoreSize, InitializingScoreTrendLevel.ONLY_DOWN);
        HardSoftDoubleScoreDefinition hardSoftScoreDefinition = new HardSoftDoubleScoreDefinition();
        HardSoftDoubleScore score2 = hardSoftScoreDefinition.buildPessimisticBound(scoreTrend, score);
        assertEquals(Double.NEGATIVE_INFINITY, score2.getHardScore(), TOLERANCE);
        assertEquals(Double.NEGATIVE_INFINITY, score2.getSoftScore(), TOLERANCE);
    }
}
