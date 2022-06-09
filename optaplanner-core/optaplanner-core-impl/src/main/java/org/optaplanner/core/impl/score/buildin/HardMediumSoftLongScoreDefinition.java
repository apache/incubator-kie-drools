package org.optaplanner.core.impl.score.buildin;

import java.util.Arrays;

import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.definition.AbstractScoreDefinition;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

public class HardMediumSoftLongScoreDefinition extends AbstractScoreDefinition<HardMediumSoftLongScore> {

    public HardMediumSoftLongScoreDefinition() {
        super(new String[] { "hard score", "medium score", "soft score" });
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public int getLevelsSize() {
        return 3;
    }

    @Override
    public int getFeasibleLevelsSize() {
        return 1;
    }

    @Override
    public Class<HardMediumSoftLongScore> getScoreClass() {
        return HardMediumSoftLongScore.class;
    }

    @Override
    public HardMediumSoftLongScore getZeroScore() {
        return HardMediumSoftLongScore.ZERO;
    }

    @Override
    public HardMediumSoftLongScore getOneSoftestScore() {
        return HardMediumSoftLongScore.ONE_SOFT;
    }

    @Override
    public HardMediumSoftLongScore parseScore(String scoreString) {
        return HardMediumSoftLongScore.parseScore(scoreString);
    }

    @Override
    public HardMediumSoftLongScore fromLevelNumbers(int initScore, Number[] levelNumbers) {
        if (levelNumbers.length != getLevelsSize()) {
            throw new IllegalStateException("The levelNumbers (" + Arrays.toString(levelNumbers)
                    + ")'s length (" + levelNumbers.length + ") must equal the levelSize (" + getLevelsSize() + ").");
        }
        return HardMediumSoftLongScore.ofUninitialized(initScore, (Long) levelNumbers[0], (Long) levelNumbers[1],
                (Long) levelNumbers[2]);
    }

    @Override
    public HardMediumSoftLongScore buildOptimisticBound(InitializingScoreTrend initializingScoreTrend,
            HardMediumSoftLongScore score) {
        InitializingScoreTrendLevel[] trendLevels = initializingScoreTrend.getTrendLevels();
        return HardMediumSoftLongScore.ofUninitialized(0,
                trendLevels[0] == InitializingScoreTrendLevel.ONLY_DOWN ? score.getHardScore() : Long.MAX_VALUE,
                trendLevels[1] == InitializingScoreTrendLevel.ONLY_DOWN ? score.getMediumScore() : Long.MAX_VALUE,
                trendLevels[2] == InitializingScoreTrendLevel.ONLY_DOWN ? score.getSoftScore() : Long.MAX_VALUE);
    }

    @Override
    public HardMediumSoftLongScore buildPessimisticBound(InitializingScoreTrend initializingScoreTrend,
            HardMediumSoftLongScore score) {
        InitializingScoreTrendLevel[] trendLevels = initializingScoreTrend.getTrendLevels();
        return HardMediumSoftLongScore.ofUninitialized(0,
                trendLevels[0] == InitializingScoreTrendLevel.ONLY_UP ? score.getHardScore() : Long.MIN_VALUE,
                trendLevels[1] == InitializingScoreTrendLevel.ONLY_UP ? score.getMediumScore() : Long.MIN_VALUE,
                trendLevels[2] == InitializingScoreTrendLevel.ONLY_UP ? score.getSoftScore() : Long.MIN_VALUE);
    }

    @Override
    public HardMediumSoftLongScore divideBySanitizedDivisor(HardMediumSoftLongScore dividend,
            HardMediumSoftLongScore divisor) {
        int dividendInitScore = dividend.getInitScore();
        int divisorInitScore = sanitize(divisor.getInitScore());
        long dividendHardScore = dividend.getHardScore();
        long divisorHardScore = sanitize(divisor.getHardScore());
        long dividendMediumScore = dividend.getMediumScore();
        long divisorMediumScore = sanitize(divisor.getMediumScore());
        long dividendSoftScore = dividend.getSoftScore();
        long divisorSoftScore = sanitize(divisor.getSoftScore());
        return fromLevelNumbers(
                divide(dividendInitScore, divisorInitScore),
                new Number[] {
                        divide(dividendHardScore, divisorHardScore),
                        divide(dividendMediumScore, divisorMediumScore),
                        divide(dividendSoftScore, divisorSoftScore)
                });
    }
}
