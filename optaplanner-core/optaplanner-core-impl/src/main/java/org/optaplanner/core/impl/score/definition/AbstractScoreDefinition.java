package org.optaplanner.core.impl.score.definition;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.buildin.HardSoftScoreDefinition;

/**
 * Abstract superclass for {@link ScoreDefinition}.
 *
 * @see ScoreDefinition
 * @see HardSoftScoreDefinition
 */
public abstract class AbstractScoreDefinition<Score_ extends Score<Score_>> implements ScoreDefinition<Score_> {

    private final String[] levelLabels;

    protected static int sanitize(int number) {
        return number == 0 ? 1 : number;
    }

    protected static long sanitize(long number) {
        return number == 0L ? 1L : number;
    }

    protected static BigDecimal sanitize(BigDecimal number) {
        return number.signum() == 0 ? BigDecimal.ONE : number;
    }

    protected static int divide(int dividend, int divisor) {
        return (int) Math.floor(divide(dividend, (double) divisor));
    }

    protected static long divide(long dividend, long divisor) {
        return (long) Math.floor(divide(dividend, (double) divisor));
    }

    protected static double divide(double dividend, double divisor) {
        return dividend / divisor;
    }

    protected static BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {
        return dividend.divide(divisor, dividend.scale() - divisor.scale(), RoundingMode.FLOOR);
    }

    /**
     * @param levelLabels never null, as defined by {@link ScoreDefinition#getLevelLabels()}
     */
    public AbstractScoreDefinition(String[] levelLabels) {
        this.levelLabels = levelLabels;
    }

    @Override
    public String getInitLabel() {
        return "init score";
    }

    @Override
    public int getLevelsSize() {
        return levelLabels.length;
    }

    @Override
    public String[] getLevelLabels() {
        return levelLabels;
    }

    @Override
    public String formatScore(Score_ score) {
        return score.toString();
    }

    @Override
    public boolean isCompatibleArithmeticArgument(Score score) {
        return Objects.equals(score.getClass(), getScoreClass());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
