package org.optaplanner.core.impl.score.buildin;

import java.util.Arrays;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.definition.AbstractScoreDefinition;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

public class SimpleScoreDefinition extends AbstractScoreDefinition<SimpleScore> {

    public SimpleScoreDefinition() {
        super(new String[] { "score" });
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public int getLevelsSize() {
        return 1;
    }

    @Override
    public int getFeasibleLevelsSize() {
        return 0;
    }

    @Override
    public Class<SimpleScore> getScoreClass() {
        return SimpleScore.class;
    }

    @Override
    public SimpleScore getZeroScore() {
        return SimpleScore.ZERO;
    }

    @Override
    public SimpleScore getOneSoftestScore() {
        return SimpleScore.ONE;
    }

    @Override
    public SimpleScore parseScore(String scoreString) {
        return SimpleScore.parseScore(scoreString);
    }

    @Override
    public SimpleScore fromLevelNumbers(int initScore, Number[] levelNumbers) {
        if (levelNumbers.length != getLevelsSize()) {
            throw new IllegalStateException("The levelNumbers (" + Arrays.toString(levelNumbers)
                    + ")'s length (" + levelNumbers.length + ") must equal the levelSize (" + getLevelsSize() + ").");
        }
        return SimpleScore.ofUninitialized(initScore, (Integer) levelNumbers[0]);
    }

    @Override
    public SimpleScore buildOptimisticBound(InitializingScoreTrend initializingScoreTrend, SimpleScore score) {
        InitializingScoreTrendLevel[] trendLevels = initializingScoreTrend.getTrendLevels();
        return SimpleScore.ofUninitialized(0,
                trendLevels[0] == InitializingScoreTrendLevel.ONLY_DOWN ? score.getScore() : Integer.MAX_VALUE);
    }

    @Override
    public SimpleScore buildPessimisticBound(InitializingScoreTrend initializingScoreTrend, SimpleScore score) {
        InitializingScoreTrendLevel[] trendLevels = initializingScoreTrend.getTrendLevels();
        return SimpleScore.ofUninitialized(0,
                trendLevels[0] == InitializingScoreTrendLevel.ONLY_UP ? score.getScore() : Integer.MIN_VALUE);
    }

    @Override
    public SimpleScore divideBySanitizedDivisor(SimpleScore dividend, SimpleScore divisor) {
        int dividendInitScore = dividend.getInitScore();
        int divisorInitScore = sanitize(divisor.getInitScore());
        int dividendScore = dividend.getScore();
        int divisorScore = sanitize(divisor.getScore());
        return fromLevelNumbers(
                divide(dividendInitScore, divisorInitScore),
                new Number[] {
                        divide(dividendScore, divisorScore)
                });
    }
}
