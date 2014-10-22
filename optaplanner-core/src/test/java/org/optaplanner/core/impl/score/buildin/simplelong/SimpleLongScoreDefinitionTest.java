package org.optaplanner.core.impl.score.buildin.simplelong;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.buildin.InitializingScoreTrendLevelFactory;
import org.optaplanner.core.impl.score.buildin.simpledouble.SimpleDoubleScoreDefinition;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

import static org.junit.Assert.assertEquals;

public class SimpleLongScoreDefinitionTest {

    @Test
    public void getLevelSize() { assertEquals(1, new SimpleLongScoreDefinition().getLevelsSize()); }

    @Test
    public void buildOptimisticBoundUp() {
        int scoreSize = new SimpleLongScoreDefinition().getLevelsSize();
        SimpleLongScore score = SimpleLongScore.parseScore("-1");
        InitializingScoreTrend scoreTrend = InitializingScoreTrendLevelFactory
                .createInitializingScoreTrendLevelArray(scoreSize, InitializingScoreTrendLevel.ONLY_UP);
        SimpleLongScoreDefinition hardSoftLongScoreDefinition = new SimpleLongScoreDefinition();
        SimpleLongScore score2 = hardSoftLongScoreDefinition.buildOptimisticBound(scoreTrend, score);
        assertEquals(Long.MAX_VALUE, score2.getScore());
    }

    @Test
    public void buildOptimisticBoundDown() {
        int scoreSize = new SimpleLongScoreDefinition().getLevelsSize();
        SimpleLongScore score = SimpleLongScore.parseScore("-1");
        InitializingScoreTrend scoreTrend = InitializingScoreTrendLevelFactory
                .createInitializingScoreTrendLevelArray(scoreSize, InitializingScoreTrendLevel.ONLY_DOWN);
        SimpleLongScoreDefinition hardSoftLongScoreDefinition = new SimpleLongScoreDefinition();
        SimpleLongScore score2 = hardSoftLongScoreDefinition.buildOptimisticBound(scoreTrend, score);
        assertEquals(-1, score2.getScore());
    }

    @Test
    public void buildPessimisticBoundUp() {
        int scoreSize = new SimpleLongScoreDefinition().getLevelsSize();
        SimpleLongScore score = SimpleLongScore.parseScore("-1");
        InitializingScoreTrend scoreTrend = InitializingScoreTrendLevelFactory
                .createInitializingScoreTrendLevelArray(scoreSize, InitializingScoreTrendLevel.ONLY_UP);
        SimpleLongScoreDefinition hardSoftScoreDefinition = new SimpleLongScoreDefinition();
        SimpleLongScore score2 = hardSoftScoreDefinition.buildPessimisticBound(scoreTrend, score);
        assertEquals(-1, score2.getScore());
    }

    @Test
    public void buildPessimisticBoundDown() {
        int scoreSize = new SimpleLongScoreDefinition().getLevelsSize();
        SimpleLongScore score = SimpleLongScore.parseScore("-1");
        InitializingScoreTrend scoreTrend = InitializingScoreTrendLevelFactory
                .createInitializingScoreTrendLevelArray(scoreSize, InitializingScoreTrendLevel.ONLY_DOWN);
        SimpleLongScoreDefinition hardSoftScoreDefinition = new SimpleLongScoreDefinition();
        SimpleLongScore score2 = hardSoftScoreDefinition.buildPessimisticBound(scoreTrend, score);
        assertEquals(Long.MIN_VALUE, score2.getScore());
    }

}
