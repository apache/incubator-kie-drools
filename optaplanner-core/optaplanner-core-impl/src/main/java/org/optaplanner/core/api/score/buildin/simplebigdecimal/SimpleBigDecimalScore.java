package org.optaplanner.core.api.score.buildin.simplebigdecimal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import org.optaplanner.core.api.score.AbstractScore;
import org.optaplanner.core.api.score.Score;

/**
 * This {@link Score} is based on 1 level of {@link BigDecimal} constraints.
 * <p>
 * This class is immutable.
 *
 * @see Score
 */
public final class SimpleBigDecimalScore extends AbstractScore<SimpleBigDecimalScore> {

    public static final SimpleBigDecimalScore ZERO = new SimpleBigDecimalScore(0, BigDecimal.ZERO);
    public static final SimpleBigDecimalScore ONE = new SimpleBigDecimalScore(0, BigDecimal.ONE);

    public static SimpleBigDecimalScore parseScore(String scoreString) {
        String[] scoreTokens = parseScoreTokens(SimpleBigDecimalScore.class, scoreString, "");
        int initScore = parseInitScore(SimpleBigDecimalScore.class, scoreString, scoreTokens[0]);
        BigDecimal score = parseLevelAsBigDecimal(SimpleBigDecimalScore.class, scoreString, scoreTokens[1]);
        return ofUninitialized(initScore, score);
    }

    public static SimpleBigDecimalScore ofUninitialized(int initScore, BigDecimal score) {
        return new SimpleBigDecimalScore(initScore, score);
    }

    public static SimpleBigDecimalScore of(BigDecimal score) {
        return new SimpleBigDecimalScore(0, score);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final BigDecimal score;

    /**
     * Private default constructor for default marshalling/unmarshalling of unknown frameworks that use reflection.
     * Such integration is always inferior to the specialized integration modules, such as
     * optaplanner-persistence-jpa, optaplanner-persistence-xstream, optaplanner-persistence-jaxb, ...
     */
    @SuppressWarnings("unused")
    private SimpleBigDecimalScore() {
        this(Integer.MIN_VALUE, null);
    }

    private SimpleBigDecimalScore(int initScore, BigDecimal score) {
        super(initScore);
        this.score = score;
    }

    /**
     * The total of the broken negative constraints and fulfilled positive constraints.
     * Their weight is included in the total.
     * The score is usually a negative number because most use cases only have negative constraints.
     *
     * @return higher is better, usually negative, 0 if no constraints are broken/fulfilled
     */
    public BigDecimal getScore() {
        return score;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public SimpleBigDecimalScore withInitScore(int newInitScore) {
        return new SimpleBigDecimalScore(newInitScore, score);
    }

    @Override
    public SimpleBigDecimalScore add(SimpleBigDecimalScore addend) {
        return new SimpleBigDecimalScore(
                initScore + addend.getInitScore(),
                score.add(addend.getScore()));
    }

    @Override
    public SimpleBigDecimalScore subtract(SimpleBigDecimalScore subtrahend) {
        return new SimpleBigDecimalScore(
                initScore - subtrahend.getInitScore(),
                score.subtract(subtrahend.getScore()));
    }

    @Override
    public SimpleBigDecimalScore multiply(double multiplicand) {
        // Intentionally not taken "new BigDecimal(multiplicand, MathContext.UNLIMITED)"
        // because together with the floor rounding it gives unwanted behaviour
        BigDecimal multiplicandBigDecimal = BigDecimal.valueOf(multiplicand);
        // The (unspecified) scale/precision of the multiplicand should have no impact on the returned scale/precision
        return new SimpleBigDecimalScore(
                (int) Math.floor(initScore * multiplicand),
                score.multiply(multiplicandBigDecimal).setScale(score.scale(), RoundingMode.FLOOR));
    }

    @Override
    public SimpleBigDecimalScore divide(double divisor) {
        // Intentionally not taken "new BigDecimal(multiplicand, MathContext.UNLIMITED)"
        // because together with the floor rounding it gives unwanted behaviour
        BigDecimal divisorBigDecimal = BigDecimal.valueOf(divisor);
        // The (unspecified) scale/precision of the divisor should have no impact on the returned scale/precision
        return new SimpleBigDecimalScore(
                (int) Math.floor(initScore / divisor),
                score.divide(divisorBigDecimal, score.scale(), RoundingMode.FLOOR));
    }

    @Override
    public SimpleBigDecimalScore power(double exponent) {
        // Intentionally not taken "new BigDecimal(multiplicand, MathContext.UNLIMITED)"
        // because together with the floor rounding it gives unwanted behaviour
        BigDecimal exponentBigDecimal = BigDecimal.valueOf(exponent);
        // The (unspecified) scale/precision of the exponent should have no impact on the returned scale/precision
        // TODO FIXME remove .intValue() so non-integer exponents produce correct results
        // None of the normal Java libraries support BigDecimal.pow(BigDecimal)
        return new SimpleBigDecimalScore(
                (int) Math.floor(Math.pow(initScore, exponent)),
                score.pow(exponentBigDecimal.intValue()).setScale(score.scale(), RoundingMode.FLOOR));
    }

    @Override
    public SimpleBigDecimalScore negate() {
        return new SimpleBigDecimalScore(-initScore, score.negate());
    }

    @Override
    public SimpleBigDecimalScore abs() {
        return new SimpleBigDecimalScore(Math.abs(initScore), score.abs());
    }

    @Override
    public SimpleBigDecimalScore zero() {
        return SimpleBigDecimalScore.ZERO;
    }

    @Override
    public boolean isFeasible() {
        return initScore >= 0;
    }

    @Override
    public Number[] toLevelNumbers() {
        return new Number[] { score };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof SimpleBigDecimalScore) {
            SimpleBigDecimalScore other = (SimpleBigDecimalScore) o;
            return initScore == other.getInitScore()
                    && score.stripTrailingZeros().equals(other.getScore().stripTrailingZeros());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(initScore, score.stripTrailingZeros());
    }

    @Override
    public int compareTo(SimpleBigDecimalScore other) {
        if (initScore != other.getInitScore()) {
            return Integer.compare(initScore, other.getInitScore());
        } else {
            return score.compareTo(other.getScore());
        }
    }

    @Override
    public String toShortString() {
        return buildShortString((n) -> ((BigDecimal) n).compareTo(BigDecimal.ZERO) != 0, "");
    }

    @Override
    public String toString() {
        return getInitPrefix() + score;
    }

}
