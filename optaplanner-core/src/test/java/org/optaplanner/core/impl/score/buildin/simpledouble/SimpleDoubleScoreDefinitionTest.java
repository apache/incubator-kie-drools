package org.optaplanner.core.impl.score.buildin.simpledouble;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simpledouble.SimpleDoubleScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

import static org.junit.Assert.*;

public class SimpleDoubleScoreDefinitionTest {

    private static final double TOLERANCE = 0.00001;

    @Test
    public void getLevelSize() {
        assertEquals(1, new SimpleDoubleScoreDefinition().getLevelsSize());
    }

    @Test
    public void buildOptimisticBoundUp() {
        int scoreSize = new SimpleDoubleScoreDefinition().getLevelsSize();
        SimpleDoubleScore score = SimpleDoubleScore.parseScore("-1");
        InitializingScoreTrend scoreTrend = InitializingScoreTrend.buildUniformTrend(
                InitializingScoreTrendLevel.ONLY_UP, scoreSize);
        SimpleDoubleScoreDefinition hardSoftScoreDefinition = new SimpleDoubleScoreDefinition();
        SimpleDoubleScore score2 = hardSoftScoreDefinition.buildOptimisticBound(scoreTrend, score);
        assertEquals(Double.POSITIVE_INFINITY, score2.getScore(), TOLERANCE);
    }

    @Test
    public void buildOptimisticBoundDown() {
        int scoreSize = new SimpleDoubleScoreDefinition().getLevelsSize();
        SimpleDoubleScore score = SimpleDoubleScore.parseScore("-1");
        InitializingScoreTrend scoreTrend = InitializingScoreTrend.buildUniformTrend(
                InitializingScoreTrendLevel.ONLY_DOWN, scoreSize);
        SimpleDoubleScoreDefinition hardSoftScoreDefinition = new SimpleDoubleScoreDefinition();
        SimpleDoubleScore score2 = hardSoftScoreDefinition.buildOptimisticBound(scoreTrend, score);
        assertEquals(-1, score2.getScore(), TOLERANCE);
    }

    @Test
    public void buildPessimisticBoundUp() {
        int scoreSize = new SimpleDoubleScoreDefinition().getLevelsSize();
        SimpleDoubleScore score = SimpleDoubleScore.parseScore("-1");
        InitializingScoreTrend scoreTrend = InitializingScoreTrend.buildUniformTrend(
                InitializingScoreTrendLevel.ONLY_UP, scoreSize);
        SimpleDoubleScoreDefinition hardSoftScoreDefinition = new SimpleDoubleScoreDefinition();
        SimpleDoubleScore score2 = hardSoftScoreDefinition.buildPessimisticBound(scoreTrend, score);
        assertEquals(-1, score2.getScore(), TOLERANCE);
    }

    @Test
    public void buildPessimisticBoundDown() {
        int scoreSize = new SimpleDoubleScoreDefinition().getLevelsSize();
        SimpleDoubleScore score = SimpleDoubleScore.parseScore("-1");
        InitializingScoreTrend scoreTrend = InitializingScoreTrend.buildUniformTrend(
                InitializingScoreTrendLevel.ONLY_DOWN, scoreSize);
        SimpleDoubleScoreDefinition hardSoftScoreDefinition = new SimpleDoubleScoreDefinition();
        SimpleDoubleScore score2 = hardSoftScoreDefinition.buildPessimisticBound(scoreTrend, score);
        assertEquals(Double.NEGATIVE_INFINITY, score2.getScore(), TOLERANCE);
    }
}
