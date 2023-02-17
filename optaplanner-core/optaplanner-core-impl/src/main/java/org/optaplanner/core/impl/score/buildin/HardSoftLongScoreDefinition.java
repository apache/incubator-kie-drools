package org.optaplanner.core.impl.score.buildin;

import java.util.Arrays;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.definition.AbstractScoreDefinition;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

public class HardSoftLongScoreDefinition extends AbstractScoreDefinition<HardSoftLongScore> {

    public HardSoftLongScoreDefinition() {
        super(new String[] { "hard score", "soft score" });
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public int getLevelsSize() {
        return 2;
    }

    @Override
    public int getFeasibleLevelsSize() {
        return 1;
    }

    @Override
    public Class<HardSoftLongScore> getScoreClass() {
        return HardSoftLongScore.class;
    }

    @Override
    public HardSoftLongScore getZeroScore() {
        return HardSoftLongScore.ZERO;
    }

    @Override
    public HardSoftLongScore getOneSoftestScore() {
        return HardSoftLongScore.ONE_SOFT;
    }

    @Override
    public HardSoftLongScore parseScore(String scoreString) {
        return HardSoftLongScore.parseScore(scoreString);
    }

    @Override
    public HardSoftLongScore fromLevelNumbers(int initScore, Number[] levelNumbers) {
        if (levelNumbers.length != getLevelsSize()) {
            throw new IllegalStateException("The levelNumbers (" + Arrays.toString(levelNumbers)
                    + ")'s length (" + levelNumbers.length + ") must equal the levelSize (" + getLevelsSize() + ").");
        }
        return HardSoftLongScore.ofUninitialized(initScore, (Long) levelNumbers[0], (Long) levelNumbers[1]);
    }

    @Override
    public HardSoftLongScore buildOptimisticBound(InitializingScoreTrend initializingScoreTrend,
            HardSoftLongScore score) {
        InitializingScoreTrendLevel[] trendLevels = initializingScoreTrend.getTrendLevels();
        return HardSoftLongScore.ofUninitialized(0,
                trendLevels[0] == InitializingScoreTrendLevel.ONLY_DOWN ? score.hardScore() : Long.MAX_VALUE,
                trendLevels[1] == InitializingScoreTrendLevel.ONLY_DOWN ? score.softScore() : Long.MAX_VALUE);
    }

    @Override
    public HardSoftLongScore buildPessimisticBound(InitializingScoreTrend initializingScoreTrend,
            HardSoftLongScore score) {
        InitializingScoreTrendLevel[] trendLevels = initializingScoreTrend.getTrendLevels();
        return HardSoftLongScore.ofUninitialized(0,
                trendLevels[0] == InitializingScoreTrendLevel.ONLY_UP ? score.hardScore() : Long.MIN_VALUE,
                trendLevels[1] == InitializingScoreTrendLevel.ONLY_UP ? score.softScore() : Long.MIN_VALUE);
    }

    @Override
    public HardSoftLongScore divideBySanitizedDivisor(HardSoftLongScore dividend, HardSoftLongScore divisor) {
        int dividendInitScore = dividend.initScore();
        int divisorInitScore = sanitize(divisor.initScore());
        long dividendHardScore = dividend.hardScore();
        long divisorHardScore = sanitize(divisor.hardScore());
        long dividendSoftScore = dividend.softScore();
        long divisorSoftScore = sanitize(divisor.softScore());
        return fromLevelNumbers(
                divide(dividendInitScore, divisorInitScore),
                new Number[] {
                        divide(dividendHardScore, divisorHardScore),
                        divide(dividendSoftScore, divisorSoftScore)
                });
    }

    @Override
    public Class<?> getNumericType() {
        return long.class;
    }
}
