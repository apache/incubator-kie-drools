package org.optaplanner.core.api.score.buildin.hardsoftbigdecimal;

import static org.optaplanner.core.impl.score.ScoreUtil.HARD_LABEL;
import static org.optaplanner.core.impl.score.ScoreUtil.SOFT_LABEL;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.ScoreUtil;

/**
 * This {@link Score} is based on 2 levels of {@link BigDecimal} constraints: hard and soft.
 * Hard constraints have priority over soft constraints.
 * Hard constraints determine feasibility.
 * <p>
 * This class is immutable.
 *
 * @see Score
 */
public final class HardSoftBigDecimalScore implements Score<HardSoftBigDecimalScore> {

    public static final HardSoftBigDecimalScore ZERO = new HardSoftBigDecimalScore(0, BigDecimal.ZERO, BigDecimal.ZERO);
    public static final HardSoftBigDecimalScore ONE_HARD = new HardSoftBigDecimalScore(0, BigDecimal.ONE, BigDecimal.ZERO);
    public static final HardSoftBigDecimalScore ONE_SOFT = new HardSoftBigDecimalScore(0, BigDecimal.ZERO, BigDecimal.ONE);

    public static HardSoftBigDecimalScore parseScore(String scoreString) {
        String[] scoreTokens = ScoreUtil.parseScoreTokens(HardSoftBigDecimalScore.class, scoreString, HARD_LABEL, SOFT_LABEL);
        int initScore = ScoreUtil.parseInitScore(HardSoftBigDecimalScore.class, scoreString, scoreTokens[0]);
        BigDecimal hardScore = ScoreUtil.parseLevelAsBigDecimal(HardSoftBigDecimalScore.class, scoreString, scoreTokens[1]);
        BigDecimal softScore = ScoreUtil.parseLevelAsBigDecimal(HardSoftBigDecimalScore.class, scoreString, scoreTokens[2]);
        return ofUninitialized(initScore, hardScore, softScore);
    }

    public static HardSoftBigDecimalScore ofUninitialized(int initScore, BigDecimal hardScore, BigDecimal softScore) {
        return new HardSoftBigDecimalScore(initScore, hardScore, softScore);
    }

    public static HardSoftBigDecimalScore of(BigDecimal hardScore, BigDecimal softScore) {
        return new HardSoftBigDecimalScore(0, hardScore, softScore);
    }

    public static HardSoftBigDecimalScore ofHard(BigDecimal hardScore) {
        return of(hardScore, BigDecimal.ZERO);
    }

    public static HardSoftBigDecimalScore ofSoft(BigDecimal softScore) {
        return of(BigDecimal.ZERO, softScore);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final int initScore;
    private final BigDecimal hardScore;
    private final BigDecimal softScore;

    /**
     * Private default constructor for default marshalling/unmarshalling of unknown frameworks that use reflection.
     * Such integration is always inferior to the specialized integration modules, such as
     * optaplanner-persistence-jpa, optaplanner-persistence-jackson, optaplanner-persistence-jaxb, ...
     */
    @SuppressWarnings("unused")
    private HardSoftBigDecimalScore() {
        this(Integer.MIN_VALUE, null, null);
    }

    private HardSoftBigDecimalScore(int initScore, BigDecimal hardScore, BigDecimal softScore) {
        this.initScore = initScore;
        this.hardScore = hardScore;
        this.softScore = softScore;
    }

    @Override
    public int initScore() {
        return initScore;
    }

    /**
     * The total of the broken negative hard constraints and fulfilled positive hard constraints.
     * Their weight is included in the total.
     * The hard score is usually a negative number because most use cases only have negative constraints.
     *
     * @return higher is better, usually negative, 0 if no hard constraints are broken/fulfilled
     */
    public BigDecimal hardScore() {
        return hardScore;
    }

    /**
     * As defined by {@link #hardScore()}.
     *
     * @deprecated Use {@link #hardScore()} instead.
     */
    @Deprecated(forRemoval = true)
    public BigDecimal getHardScore() {
        return hardScore;
    }

    /**
     * The total of the broken negative soft constraints and fulfilled positive soft constraints.
     * Their weight is included in the total.
     * The soft score is usually a negative number because most use cases only have negative constraints.
     * <p>
     * In a normal score comparison, the soft score is irrelevant if the 2 scores don't have the same hard score.
     *
     * @return higher is better, usually negative, 0 if no soft constraints are broken/fulfilled
     */
    public BigDecimal softScore() {
        return softScore;
    }

    /**
     * As defined by {@link #softScore()}.
     *
     * @deprecated Use {@link #softScore()} instead.
     */
    @Deprecated(forRemoval = true)
    public BigDecimal getSoftScore() {
        return softScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public HardSoftBigDecimalScore withInitScore(int newInitScore) {
        return new HardSoftBigDecimalScore(newInitScore, hardScore, softScore);
    }

    @Override
    public boolean isFeasible() {
        return initScore >= 0 && hardScore.compareTo(BigDecimal.ZERO) >= 0;
    }

    @Override
    public HardSoftBigDecimalScore add(HardSoftBigDecimalScore addend) {
        return new HardSoftBigDecimalScore(
                initScore + addend.initScore(),
                hardScore.add(addend.hardScore()),
                softScore.add(addend.softScore()));
    }

    @Override
    public HardSoftBigDecimalScore subtract(HardSoftBigDecimalScore subtrahend) {
        return new HardSoftBigDecimalScore(
                initScore - subtrahend.initScore(),
                hardScore.subtract(subtrahend.hardScore()),
                softScore.subtract(subtrahend.softScore()));
    }

    @Override
    public HardSoftBigDecimalScore multiply(double multiplicand) {
        // Intentionally not taken "new BigDecimal(multiplicand, MathContext.UNLIMITED)"
        // because together with the floor rounding it gives unwanted behaviour
        BigDecimal multiplicandBigDecimal = BigDecimal.valueOf(multiplicand);
        // The (unspecified) scale/precision of the multiplicand should have no impact on the returned scale/precision
        return new HardSoftBigDecimalScore(
                (int) Math.floor(initScore * multiplicand),
                hardScore.multiply(multiplicandBigDecimal).setScale(hardScore.scale(), RoundingMode.FLOOR),
                softScore.multiply(multiplicandBigDecimal).setScale(softScore.scale(), RoundingMode.FLOOR));
    }

    @Override
    public HardSoftBigDecimalScore divide(double divisor) {
        // Intentionally not taken "new BigDecimal(multiplicand, MathContext.UNLIMITED)"
        // because together with the floor rounding it gives unwanted behaviour
        BigDecimal divisorBigDecimal = BigDecimal.valueOf(divisor);
        // The (unspecified) scale/precision of the divisor should have no impact on the returned scale/precision
        return new HardSoftBigDecimalScore(
                (int) Math.floor(initScore / divisor),
                hardScore.divide(divisorBigDecimal, hardScore.scale(), RoundingMode.FLOOR),
                softScore.divide(divisorBigDecimal, softScore.scale(), RoundingMode.FLOOR));
    }

    @Override
    public HardSoftBigDecimalScore power(double exponent) {
        // Intentionally not taken "new BigDecimal(multiplicand, MathContext.UNLIMITED)"
        // because together with the floor rounding it gives unwanted behaviour
        BigDecimal exponentBigDecimal = BigDecimal.valueOf(exponent);
        // The (unspecified) scale/precision of the exponent should have no impact on the returned scale/precision
        // TODO FIXME remove .intValue() so non-integer exponents produce correct results
        // None of the normal Java libraries support BigDecimal.pow(BigDecimal)
        return new HardSoftBigDecimalScore(
                (int) Math.floor(Math.pow(initScore, exponent)),
                hardScore.pow(exponentBigDecimal.intValue()).setScale(hardScore.scale(), RoundingMode.FLOOR),
                softScore.pow(exponentBigDecimal.intValue()).setScale(softScore.scale(), RoundingMode.FLOOR));
    }

    @Override
    public HardSoftBigDecimalScore abs() {
        return new HardSoftBigDecimalScore(Math.abs(initScore), hardScore.abs(), softScore.abs());
    }

    @Override
    public HardSoftBigDecimalScore zero() {
        return HardSoftBigDecimalScore.ZERO;
    }

    @Override
    public Number[] toLevelNumbers() {
        return new Number[] { hardScore, softScore };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof HardSoftBigDecimalScore) {
            HardSoftBigDecimalScore other = (HardSoftBigDecimalScore) o;
            return initScore == other.initScore()
                    && hardScore.stripTrailingZeros().equals(other.hardScore().stripTrailingZeros())
                    && softScore.stripTrailingZeros().equals(other.softScore().stripTrailingZeros());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(initScore, hardScore.stripTrailingZeros(), softScore.stripTrailingZeros());
    }

    @Override
    public int compareTo(HardSoftBigDecimalScore other) {
        if (initScore != other.initScore()) {
            return Integer.compare(initScore, other.initScore());
        }
        int hardScoreComparison = hardScore.compareTo(other.hardScore());
        if (hardScoreComparison != 0) {
            return hardScoreComparison;
        } else {
            return softScore.compareTo(other.softScore());
        }
    }

    @Override
    public String toShortString() {
        return ScoreUtil.buildShortString(this, n -> ((BigDecimal) n).compareTo(BigDecimal.ZERO) != 0, HARD_LABEL, SOFT_LABEL);
    }

    @Override
    public String toString() {
        return ScoreUtil.getInitPrefix(initScore) + hardScore + HARD_LABEL + "/" + softScore + SOFT_LABEL;
    }

}
