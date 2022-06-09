package org.optaplanner.core.impl.score.buildin;

import java.util.Arrays;

import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.definition.AbstractScoreDefinition;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

public class HardMediumSoftScoreDefinition extends AbstractScoreDefinition<HardMediumSoftScore> {

    public HardMediumSoftScoreDefinition() {
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
    public Class<HardMediumSoftScore> getScoreClass() {
        return HardMediumSoftScore.class;
    }

    @Override
    public HardMediumSoftScore getZeroScore() {
        return HardMediumSoftScore.ZERO;
    }

    @Override
    public HardMediumSoftScore getOneSoftestScore() {
        return HardMediumSoftScore.ONE_SOFT;
    }

    @Override
    public HardMediumSoftScore parseScore(String scoreString) {
        return HardMediumSoftScore.parseScore(scoreString);
    }

    @Override
    public HardMediumSoftScore fromLevelNumbers(int initScore, Number[] levelNumbers) {
        if (levelNumbers.length != getLevelsSize()) {
            throw new IllegalStateException("The levelNumbers (" + Arrays.toString(levelNumbers)
                    + ")'s length (" + levelNumbers.length + ") must equal the levelSize (" + getLevelsSize() + ").");
        }
        return HardMediumSoftScore.ofUninitialized(initScore, (Integer) levelNumbers[0], (Integer) levelNumbers[1],
                (Integer) levelNumbers[2]);
    }

    @Override
    public HardMediumSoftScore buildOptimisticBound(InitializingScoreTrend initializingScoreTrend,
            HardMediumSoftScore score) {
        InitializingScoreTrendLevel[] trendLevels = initializingScoreTrend.getTrendLevels();
        return HardMediumSoftScore.ofUninitialized(0,
                trendLevels[0] == InitializingScoreTrendLevel.ONLY_DOWN ? score.getHardScore() : Integer.MAX_VALUE,
                trendLevels[1] == InitializingScoreTrendLevel.ONLY_DOWN ? score.getMediumScore() : Integer.MAX_VALUE,
                trendLevels[2] == InitializingScoreTrendLevel.ONLY_DOWN ? score.getSoftScore() : Integer.MAX_VALUE);
    }

    @Override
    public HardMediumSoftScore buildPessimisticBound(InitializingScoreTrend initializingScoreTrend,
            HardMediumSoftScore score) {
        InitializingScoreTrendLevel[] trendLevels = initializingScoreTrend.getTrendLevels();
        return HardMediumSoftScore.ofUninitialized(0,
                trendLevels[0] == InitializingScoreTrendLevel.ONLY_UP ? score.getHardScore() : Integer.MIN_VALUE,
                trendLevels[1] == InitializingScoreTrendLevel.ONLY_UP ? score.getMediumScore() : Integer.MIN_VALUE,
                trendLevels[2] == InitializingScoreTrendLevel.ONLY_UP ? score.getSoftScore() : Integer.MIN_VALUE);
    }

    @Override
    public HardMediumSoftScore divideBySanitizedDivisor(HardMediumSoftScore dividend, HardMediumSoftScore divisor) {
        int dividendInitScore = dividend.getInitScore();
        int divisorInitScore = sanitize(divisor.getInitScore());
        int dividendHardScore = dividend.getHardScore();
        int divisorHardScore = sanitize(divisor.getHardScore());
        int dividendMediumScore = dividend.getMediumScore();
        int divisorMediumScore = sanitize(divisor.getMediumScore());
        int dividendSoftScore = dividend.getSoftScore();
        int divisorSoftScore = sanitize(divisor.getSoftScore());
        return fromLevelNumbers(
                divide(dividendInitScore, divisorInitScore),
                new Number[] {
                        divide(dividendHardScore, divisorHardScore),
                        divide(dividendMediumScore, divisorMediumScore),
                        divide(dividendSoftScore, divisorSoftScore)
                });
    }
}
