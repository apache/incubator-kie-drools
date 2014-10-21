package org.optaplanner.core.impl.score.buildin.simplelong;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.buildin.simpledouble.SimpleDoubleScoreDefinition;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

import static org.junit.Assert.assertEquals;

public class SimpleLongScoreDefinitionTest {

    @Test
    public void getLevelSize() { assertEquals(1, new SimpleLongScoreDefinition().getLevelsSize()); }

    @Test
    public void buildOptimisticBound() {
        int scoreSize = new SimpleLongScoreDefinition().getLevelsSize();
        InitializingScoreTrendLevel[] levels = new InitializingScoreTrendLevel[scoreSize];
        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_UP;
        }
        SimpleLongScore score = SimpleLongScore.parseScore("-999");
        InitializingScoreTrend scoreTrend = new InitializingScoreTrend(levels);
        SimpleLongScoreDefinition hardSoftLongScoreDefinition = new SimpleLongScoreDefinition();
        SimpleLongScore score2 = hardSoftLongScoreDefinition.buildOptimisticBound(scoreTrend, score);

        assertEquals(Long.MAX_VALUE, score2.getScore());

        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_DOWN;
        }
        score2 = hardSoftLongScoreDefinition.buildOptimisticBound(scoreTrend, score);
        assertEquals(-999, score2.getScore());
    }

    @Test
    public void buildPessimisticBound() {
        int scoreSize = new SimpleLongScoreDefinition().getLevelsSize();
        InitializingScoreTrendLevel[] levels = new InitializingScoreTrendLevel[scoreSize];
        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_UP;
        }
        SimpleLongScore score = SimpleLongScore.parseScore("-999");
        InitializingScoreTrend scoreTrend = new InitializingScoreTrend(levels);
        SimpleLongScoreDefinition hardSoftScoreDefinition = new SimpleLongScoreDefinition();
        SimpleLongScore score2 = hardSoftScoreDefinition.buildPessimisticBound(scoreTrend, score);

        assertEquals(-999, score2.getScore());

        for (int i = 0; i < scoreSize; i++) {
            levels[i] = InitializingScoreTrendLevel.ONLY_DOWN;
        }
        score2 = hardSoftScoreDefinition.buildPessimisticBound(scoreTrend, score);

        assertEquals(Long.MIN_VALUE, score2.getScore());
    }

}
