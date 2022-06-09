package org.optaplanner.core.api.score.buildin.hardmediumsoftlong;

import java.util.Objects;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.AbstractScore;
import org.optaplanner.core.api.score.Score;

/**
 * This {@link Score} is based on 3 levels of long constraints: hard, medium and soft.
 * Hard constraints have priority over medium constraints.
 * Medium constraints have priority over soft constraints.
 * Hard constraints determine feasibility.
 * <p>
 * This class is immutable.
 *
 * @see Score
 */
public final class HardMediumSoftLongScore extends AbstractScore<HardMediumSoftLongScore> {

    public static final HardMediumSoftLongScore ZERO = new HardMediumSoftLongScore(0, 0L, 0L, 0L);
    public static final HardMediumSoftLongScore ONE_HARD = new HardMediumSoftLongScore(0, 1L, 0L, 0L);
    public static final HardMediumSoftLongScore ONE_MEDIUM = new HardMediumSoftLongScore(0, 0L, 1L, 0L);
    public static final HardMediumSoftLongScore ONE_SOFT = new HardMediumSoftLongScore(0, 0L, 0L, 1L);
    private static final String HARD_LABEL = "hard";
    private static final String MEDIUM_LABEL = "medium";
    private static final String SOFT_LABEL = "soft";

    public static HardMediumSoftLongScore parseScore(String scoreString) {
        String[] scoreTokens = parseScoreTokens(HardMediumSoftLongScore.class, scoreString,
                HARD_LABEL, MEDIUM_LABEL, SOFT_LABEL);
        int initScore = parseInitScore(HardMediumSoftLongScore.class, scoreString, scoreTokens[0]);
        long hardScore = parseLevelAsLong(HardMediumSoftLongScore.class, scoreString, scoreTokens[1]);
        long mediumScore = parseLevelAsLong(HardMediumSoftLongScore.class, scoreString, scoreTokens[2]);
        long softScore = parseLevelAsLong(HardMediumSoftLongScore.class, scoreString, scoreTokens[3]);
        return ofUninitialized(initScore, hardScore, mediumScore, softScore);
    }

    public static HardMediumSoftLongScore ofUninitialized(int initScore, long hardScore, long mediumScore, long softScore) {
        return new HardMediumSoftLongScore(initScore, hardScore, mediumScore, softScore);
    }

    public static HardMediumSoftLongScore of(long hardScore, long mediumScore, long softScore) {
        return new HardMediumSoftLongScore(0, hardScore, mediumScore, softScore);
    }

    public static HardMediumSoftLongScore ofHard(long hardScore) {
        return of(hardScore, 0, 0);
    }

    public static HardMediumSoftLongScore ofMedium(long mediumScore) {
        return of(0, mediumScore, 0);
    }

    public static HardMediumSoftLongScore ofSoft(long softScore) {
        return of(0, 0, softScore);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final long hardScore;
    private final long mediumScore;
    private final long softScore;

    /**
     * Private default constructor for default marshalling/unmarshalling of unknown frameworks that use reflection.
     * Such integration is always inferior to the specialized integration modules, such as
     * optaplanner-persistence-jpa, optaplanner-persistence-xstream, optaplanner-persistence-jaxb, ...
     */
    @SuppressWarnings("unused")
    private HardMediumSoftLongScore() {
        this(Integer.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE);
    }

    private HardMediumSoftLongScore(int initScore, long hardScore, long mediumScore, long softScore) {
        super(initScore);
        this.hardScore = hardScore;
        this.mediumScore = mediumScore;
        this.softScore = softScore;
    }

    /**
     * The total of the broken negative hard constraints and fulfilled positive hard constraints.
     * Their weight is included in the total.
     * The hard score is usually a negative number because most use cases only have negative constraints.
     *
     * @return higher is better, usually negative, 0 if no hard constraints are broken/fulfilled
     */
    public long getHardScore() {
        return hardScore;
    }

    /**
     * The total of the broken negative medium constraints and fulfilled positive medium constraints.
     * Their weight is included in the total.
     * The medium score is usually a negative number because most use cases only have negative constraints.
     * <p>
     * In a normal score comparison, the medium score is irrelevant if the 2 scores don't have the same hard score.
     *
     * @return higher is better, usually negative, 0 if no medium constraints are broken/fulfilled
     */
    public long getMediumScore() {
        return mediumScore;
    }

    /**
     * The total of the broken negative soft constraints and fulfilled positive soft constraints.
     * Their weight is included in the total.
     * The soft score is usually a negative number because most use cases only have negative constraints.
     * <p>
     * In a normal score comparison, the soft score is irrelevant if the 2 scores don't have the same hard and medium score.
     *
     * @return higher is better, usually negative, 0 if no soft constraints are broken/fulfilled
     */
    public long getSoftScore() {
        return softScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public HardMediumSoftLongScore withInitScore(int newInitScore) {
        return new HardMediumSoftLongScore(newInitScore, hardScore, mediumScore, softScore);
    }

    /**
     * A {@link PlanningSolution} is feasible if it has no broken hard constraints.
     *
     * @return true if the {@link #getHardScore()} is 0 or higher
     */
    @Override
    public boolean isFeasible() {
        return initScore >= 0 && hardScore >= 0L;
    }

    @Override
    public HardMediumSoftLongScore add(HardMediumSoftLongScore addend) {
        return new HardMediumSoftLongScore(
                initScore + addend.getInitScore(),
                hardScore + addend.getHardScore(),
                mediumScore + addend.getMediumScore(),
                softScore + addend.getSoftScore());
    }

    @Override
    public HardMediumSoftLongScore subtract(HardMediumSoftLongScore subtrahend) {
        return new HardMediumSoftLongScore(
                initScore - subtrahend.getInitScore(),
                hardScore - subtrahend.getHardScore(),
                mediumScore - subtrahend.getMediumScore(),
                softScore - subtrahend.getSoftScore());
    }

    @Override
    public HardMediumSoftLongScore multiply(double multiplicand) {
        return new HardMediumSoftLongScore(
                (int) Math.floor(initScore * multiplicand),
                (long) Math.floor(hardScore * multiplicand),
                (long) Math.floor(mediumScore * multiplicand),
                (long) Math.floor(softScore * multiplicand));
    }

    @Override
    public HardMediumSoftLongScore divide(double divisor) {
        return new HardMediumSoftLongScore(
                (int) Math.floor(initScore / divisor),
                (long) Math.floor(hardScore / divisor),
                (long) Math.floor(mediumScore / divisor),
                (long) Math.floor(softScore / divisor));
    }

    @Override
    public HardMediumSoftLongScore power(double exponent) {
        return new HardMediumSoftLongScore(
                (int) Math.floor(Math.pow(initScore, exponent)),
                (long) Math.floor(Math.pow(hardScore, exponent)),
                (long) Math.floor(Math.pow(mediumScore, exponent)),
                (long) Math.floor(Math.pow(softScore, exponent)));
    }

    @Override
    public HardMediumSoftLongScore negate() {
        return new HardMediumSoftLongScore(-initScore, -hardScore, -mediumScore, -softScore);
    }

    @Override
    public HardMediumSoftLongScore zero() {
        return HardMediumSoftLongScore.ZERO;
    }

    @Override
    public Number[] toLevelNumbers() {
        return new Number[] { hardScore, mediumScore, softScore };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof HardMediumSoftLongScore) {
            HardMediumSoftLongScore other = (HardMediumSoftLongScore) o;
            return initScore == other.getInitScore()
                    && hardScore == other.getHardScore()
                    && mediumScore == other.getMediumScore()
                    && softScore == other.getSoftScore();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(initScore, hardScore, mediumScore, softScore);
    }

    @Override
    public int compareTo(HardMediumSoftLongScore other) {
        if (initScore != other.getInitScore()) {
            return Integer.compare(initScore, other.getInitScore());
        } else if (hardScore != other.getHardScore()) {
            return Long.compare(hardScore, other.getHardScore());
        } else if (mediumScore != other.getMediumScore()) {
            return Long.compare(mediumScore, other.getMediumScore());
        } else {
            return Long.compare(softScore, other.getSoftScore());
        }
    }

    @Override
    public String toShortString() {
        return buildShortString((n) -> n.longValue() != 0L, HARD_LABEL, MEDIUM_LABEL, SOFT_LABEL);
    }

    @Override
    public String toString() {
        return getInitPrefix() + hardScore + HARD_LABEL + "/" + mediumScore + MEDIUM_LABEL + "/" + softScore + SOFT_LABEL;
    }

}
