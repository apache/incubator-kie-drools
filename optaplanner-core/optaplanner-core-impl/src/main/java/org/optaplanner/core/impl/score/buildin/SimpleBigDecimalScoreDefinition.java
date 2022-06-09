package org.optaplanner.core.impl.score.buildin;

import java.math.BigDecimal;
import java.util.Arrays;

import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.impl.score.definition.AbstractScoreDefinition;
import org.optaplanner.core.impl.score.trend.InitializingScoreTrend;

public class SimpleBigDecimalScoreDefinition extends AbstractScoreDefinition<SimpleBigDecimalScore> {

    public SimpleBigDecimalScoreDefinition() {
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
    public Class<SimpleBigDecimalScore> getScoreClass() {
        return SimpleBigDecimalScore.class;
    }

    @Override
    public SimpleBigDecimalScore getZeroScore() {
        return SimpleBigDecimalScore.ZERO;
    }

    @Override
    public SimpleBigDecimalScore getOneSoftestScore() {
        return SimpleBigDecimalScore.ONE;
    }

    @Override
    public SimpleBigDecimalScore parseScore(String scoreString) {
        return SimpleBigDecimalScore.parseScore(scoreString);
    }

    @Override
    public SimpleBigDecimalScore fromLevelNumbers(int initScore, Number[] levelNumbers) {
        if (levelNumbers.length != getLevelsSize()) {
            throw new IllegalStateException("The levelNumbers (" + Arrays.toString(levelNumbers)
                    + ")'s length (" + levelNumbers.length + ") must equal the levelSize (" + getLevelsSize() + ").");
        }
        return SimpleBigDecimalScore.ofUninitialized(initScore, (BigDecimal) levelNumbers[0]);
    }

    @Override
    public SimpleBigDecimalScore buildOptimisticBound(InitializingScoreTrend initializingScoreTrend,
            SimpleBigDecimalScore score) {
        // TODO https://issues.redhat.com/browse/PLANNER-232
        throw new UnsupportedOperationException("PLANNER-232: BigDecimalScore does not support bounds" +
                " because a BigDecimal cannot represent infinity.");
    }

    @Override
    public SimpleBigDecimalScore buildPessimisticBound(InitializingScoreTrend initializingScoreTrend,
            SimpleBigDecimalScore score) {
        // TODO https://issues.redhat.com/browse/PLANNER-232
        throw new UnsupportedOperationException("PLANNER-232: BigDecimalScore does not support bounds" +
                " because a BigDecimal cannot represent infinity.");
    }

    @Override
    public SimpleBigDecimalScore divideBySanitizedDivisor(SimpleBigDecimalScore dividend,
            SimpleBigDecimalScore divisor) {
        int dividendInitScore = dividend.getInitScore();
        int divisorInitScore = sanitize(divisor.getInitScore());
        BigDecimal dividendScore = dividend.getScore();
        BigDecimal divisorScore = sanitize(divisor.getScore());
        return fromLevelNumbers(
                divide(dividendInitScore, divisorInitScore),
                new Number[] {
                        divide(dividendScore, divisorScore)
                });
    }
}
