package org.optaplanner.core.impl.score.buildin.simpledouble;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simpledouble.SimpleDoubleScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

import static org.junit.Assert.*;

public class SimpleDoubleScoreDefinitionTest {

    private static final double TOLERANCE = 0.00001;

    @Test
    public void getLevelSize() { assertEquals(1, new SimpleDoubleScoreDefinition().getLevelsSize()); }

    @Test
    public void buildOptimisticBound() {
        int scoreSize = new SimpleDoubleScoreDefinition().getLevelsSize();
        InitializingScoreTrendLevel[] levels = new InitializingScoreTrendLevel[scoreSize];
        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_UP;
        }
        SimpleDoubleScore score = SimpleDoubleScore.parseScore("-999");
        InitializingScoreTrend scoreTrend = new InitializingScoreTrend(levels);
        SimpleDoubleScoreDefinition hardSoftScoreDefinition = new SimpleDoubleScoreDefinition();
        SimpleDoubleScore score2 = hardSoftScoreDefinition.buildOptimisticBound(scoreTrend, score);

        assertEquals(Double.POSITIVE_INFINITY, score2.getScore(), TOLERANCE);

        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_DOWN;
        }
        score2 = hardSoftScoreDefinition.buildOptimisticBound(scoreTrend, score);
        assertEquals(-999, score2.getScore(), TOLERANCE);
    }

    @Test
    public void buildPessimisticBound() {
        int scoreSize = new SimpleDoubleScoreDefinition().getLevelsSize();
        InitializingScoreTrendLevel[] levels = new InitializingScoreTrendLevel[scoreSize];
        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_UP;
        }
        SimpleDoubleScore score = SimpleDoubleScore.parseScore("-999");
        InitializingScoreTrend scoreTrend = new InitializingScoreTrend(levels);
        SimpleDoubleScoreDefinition hardSoftScoreDefinition = new SimpleDoubleScoreDefinition();
        SimpleDoubleScore score2 = hardSoftScoreDefinition.buildPessimisticBound(scoreTrend, score);

        assertEquals(-999, score2.getScore(), TOLERANCE);

        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_DOWN;
        }
        score2 = hardSoftScoreDefinition.buildPessimisticBound(scoreTrend, score);

        assertEquals(Double.NEGATIVE_INFINITY, score2.getScore(), TOLERANCE);
    }

}
