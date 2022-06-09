package org.optaplanner.core.impl.score.buildin;

import java.util.Arrays;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.config.score.trend.InitializingScoreTrendLevel;
import org.optaplanner.core.impl.score.definition.AbstractScoreDefinition;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

public class HardSoftScoreDefinition extends AbstractScoreDefinition<HardSoftScore> {

    public HardSoftScoreDefinition() {
        super(new String[] { "hard score", "soft score" });
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public int getFeasibleLevelsSize() {
        return 1;
    }

    @Override
    public Class<HardSoftScore> getScoreClass() {
        return HardSoftScore.class;
    }

    @Override
    public HardSoftScore getZeroScore() {
        return HardSoftScore.ZERO;
    }

    @Override
    public HardSoftScore getOneSoftestScore() {
        return HardSoftScore.ONE_SOFT;
    }

    @Override
    public HardSoftScore parseScore(String scoreString) {
        return HardSoftScore.parseScore(scoreString);
    }

    @Override
    public HardSoftScore fromLevelNumbers(int initScore, Number[] levelNumbers) {
        if (levelNumbers.length != getLevelsSize()) {
            throw new IllegalStateException("The levelNumbers (" + Arrays.toString(levelNumbers)
                    + ")'s length (" + levelNumbers.length + ") must equal the levelSize (" + getLevelsSize() + ").");
        }
        return HardSoftScore.ofUninitialized(initScore, (Integer) levelNumbers[0], (Integer) levelNumbers[1]);
    }

    @Override
    public HardSoftScore buildOptimisticBound(InitializingScoreTrend initializingScoreTrend, HardSoftScore score) {
        InitializingScoreTrendLevel[] trendLevels = initializingScoreTrend.getTrendLevels();
        return HardSoftScore.ofUninitialized(0,
                trendLevels[0] == InitializingScoreTrendLevel.ONLY_DOWN ? score.getHardScore() : Integer.MAX_VALUE,
                trendLevels[1] == InitializingScoreTrendLevel.ONLY_DOWN ? score.getSoftScore() : Integer.MAX_VALUE);
    }

    @Override
    public HardSoftScore buildPessimisticBound(InitializingScoreTrend initializingScoreTrend, HardSoftScore score) {
        InitializingScoreTrendLevel[] trendLevels = initializingScoreTrend.getTrendLevels();
        return HardSoftScore.ofUninitialized(0,
                trendLevels[0] == InitializingScoreTrendLevel.ONLY_UP ? score.getHardScore() : Integer.MIN_VALUE,
                trendLevels[1] == InitializingScoreTrendLevel.ONLY_UP ? score.getSoftScore() : Integer.MIN_VALUE);
    }

    @Override
    public HardSoftScore divideBySanitizedDivisor(HardSoftScore dividend, HardSoftScore divisor) {
        int dividendInitScore = dividend.getInitScore();
        int divisorInitScore = sanitize(divisor.getInitScore());
        int dividendHardScore = dividend.getHardScore();
        int divisorHardScore = sanitize(divisor.getHardScore());
        int dividendSoftScore = dividend.getSoftScore();
        int divisorSoftScore = sanitize(divisor.getSoftScore());
        return fromLevelNumbers(
                divide(dividendInitScore, divisorInitScore),
                new Number[] {
                        divide(dividendHardScore, divisorHardScore),
                        divide(dividendSoftScore, divisorSoftScore)
                });
    }
}
