package org.optaplanner.core.api.score.buildin.bendablebigdecimal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import org.optaplanner.core.api.score.AbstractBendableScore;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.buildin.BendableScoreDefinition;

/**
 * This {@link Score} is based on n levels of {@link BigDecimal} constraints.
 * The number of levels is bendable at configuration time.
 * <p>
 * This class is immutable.
 * <p>
 * The {@link #getHardLevelsSize()} and {@link #getSoftLevelsSize()} must be the same as in the
 * {@link BendableScoreDefinition} used.
 *
 * @see Score
 */
public final class BendableBigDecimalScore extends AbstractBendableScore<BendableBigDecimalScore> {

    /**
     * @param scoreString never null
     * @return never null
     */
    public static BendableBigDecimalScore parseScore(String scoreString) {
        String[][] scoreTokens = parseBendableScoreTokens(BendableBigDecimalScore.class, scoreString);
        int initScore = parseInitScore(BendableBigDecimalScore.class, scoreString, scoreTokens[0][0]);
        BigDecimal[] hardScores = new BigDecimal[scoreTokens[1].length];
        for (int i = 0; i < hardScores.length; i++) {
            hardScores[i] = parseLevelAsBigDecimal(BendableBigDecimalScore.class, scoreString, scoreTokens[1][i]);
        }
        BigDecimal[] softScores = new BigDecimal[scoreTokens[2].length];
        for (int i = 0; i < softScores.length; i++) {
            softScores[i] = parseLevelAsBigDecimal(BendableBigDecimalScore.class, scoreString, scoreTokens[2][i]);
        }
        return ofUninitialized(initScore, hardScores, softScores);
    }

    /**
     * Creates a new {@link BendableBigDecimalScore}.
     *
     * @param initScore see {@link Score#getInitScore()}
     * @param hardScores never null, never change that array afterwards: it must be immutable
     * @param softScores never null, never change that array afterwards: it must be immutable
     * @return never null
     */
    public static BendableBigDecimalScore ofUninitialized(int initScore, BigDecimal[] hardScores, BigDecimal[] softScores) {
        return new BendableBigDecimalScore(initScore, hardScores, softScores);
    }

    /**
     * Creates a new {@link BendableBigDecimalScore}.
     *
     * @param hardScores never null, never change that array afterwards: it must be immutable
     * @param softScores never null, never change that array afterwards: it must be immutable
     * @return never null
     */
    public static BendableBigDecimalScore of(BigDecimal[] hardScores, BigDecimal[] softScores) {
        return new BendableBigDecimalScore(0, hardScores, softScores);
    }

    /**
     * Creates a new {@link BendableBigDecimalScore}.
     *
     * @param hardLevelsSize at least 0
     * @param softLevelsSize at least 0
     * @return never null
     */
    public static BendableBigDecimalScore zero(int hardLevelsSize, int softLevelsSize) {
        BigDecimal[] hardScores = new BigDecimal[hardLevelsSize];
        Arrays.fill(hardScores, BigDecimal.ZERO);
        BigDecimal[] softScores = new BigDecimal[softLevelsSize];
        Arrays.fill(softScores, BigDecimal.ZERO);
        return new BendableBigDecimalScore(0, hardScores, softScores);
    }

    /**
     * Creates a new {@link BendableBigDecimalScore}.
     *
     * @param hardLevelsSize at least 0
     * @param softLevelsSize at least 0
     * @param hardLevel at least 0, less than hardLevelsSize
     * @param hardScore never null
     * @return never null
     */
    public static BendableBigDecimalScore ofHard(int hardLevelsSize, int softLevelsSize, int hardLevel, BigDecimal hardScore) {
        BigDecimal[] hardScores = new BigDecimal[hardLevelsSize];
        Arrays.fill(hardScores, BigDecimal.ZERO);
        BigDecimal[] softScores = new BigDecimal[softLevelsSize];
        Arrays.fill(softScores, BigDecimal.ZERO);
        hardScores[hardLevel] = hardScore;
        return new BendableBigDecimalScore(0, hardScores, softScores);
    }

    /**
     * Creates a new {@link BendableBigDecimalScore}.
     *
     * @param hardLevelsSize at least 0
     * @param softLevelsSize at least 0
     * @param softLevel at least 0, less than softLevelsSize
     * @param softScore never null
     * @return never null
     */
    public static BendableBigDecimalScore ofSoft(int hardLevelsSize, int softLevelsSize, int softLevel, BigDecimal softScore) {
        BigDecimal[] hardScores = new BigDecimal[hardLevelsSize];
        Arrays.fill(hardScores, BigDecimal.ZERO);
        BigDecimal[] softScores = new BigDecimal[softLevelsSize];
        Arrays.fill(softScores, BigDecimal.ZERO);
        softScores[softLevel] = softScore;
        return new BendableBigDecimalScore(0, hardScores, softScores);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final BigDecimal[] hardScores;
    private final BigDecimal[] softScores;

    /**
     * Private default constructor for default marshalling/unmarshalling of unknown frameworks that use reflection.
     * Such integration is always inferior to the specialized integration modules, such as
     * optaplanner-persistence-jpa, optaplanner-persistence-xstream, optaplanner-persistence-jaxb, ...
     */
    @SuppressWarnings("unused")
    private BendableBigDecimalScore() {
        this(Integer.MIN_VALUE, null, null);
    }

    /**
     * @param initScore see {@link Score#getInitScore()}
     * @param hardScores never null
     * @param softScores never null
     */
    private BendableBigDecimalScore(int initScore, BigDecimal[] hardScores, BigDecimal[] softScores) {
        super(initScore);
        this.hardScores = hardScores;
        this.softScores = softScores;
    }

    /**
     * @return not null, array copy because this class is immutable
     */
    public BigDecimal[] getHardScores() {
        return Arrays.copyOf(hardScores, hardScores.length);
    }

    /**
     * @return not null, array copy because this class is immutable
     */
    public BigDecimal[] getSoftScores() {
        return Arrays.copyOf(softScores, softScores.length);
    }

    @Override
    public int getHardLevelsSize() {
        return hardScores.length;
    }

    /**
     * @param index {@code 0 <= index <} {@link #getHardLevelsSize()}
     * @return higher is better
     */
    public BigDecimal getHardScore(int index) {
        return hardScores[index];
    }

    @Override
    public int getSoftLevelsSize() {
        return softScores.length;
    }

    /**
     * @param index {@code 0 <= index <} {@link #getSoftLevelsSize()}
     * @return higher is better
     */
    public BigDecimal getSoftScore(int index) {
        return softScores[index];
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public BendableBigDecimalScore withInitScore(int newInitScore) {
        return new BendableBigDecimalScore(newInitScore, hardScores, softScores);
    }

    @Override
    public int getLevelsSize() {
        return hardScores.length + softScores.length;
    }

    /**
     * @param index {@code 0 <= index <} {@link #getLevelsSize()}
     * @return higher is better
     */
    public BigDecimal getHardOrSoftScore(int index) {
        if (index < hardScores.length) {
            return hardScores[index];
        } else {
            return softScores[index - hardScores.length];
        }
    }

    @Override
    public boolean isFeasible() {
        if (initScore < 0) {
            return false;
        }
        for (BigDecimal hardScore : hardScores) {
            if (hardScore.compareTo(BigDecimal.ZERO) < 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public BendableBigDecimalScore add(BendableBigDecimalScore addend) {
        validateCompatible(addend);
        BigDecimal[] newHardScores = new BigDecimal[hardScores.length];
        BigDecimal[] newSoftScores = new BigDecimal[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = hardScores[i].add(addend.getHardScore(i));
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = softScores[i].add(addend.getSoftScore(i));
        }
        return new BendableBigDecimalScore(
                initScore + addend.getInitScore(),
                newHardScores, newSoftScores);
    }

    @Override
    public BendableBigDecimalScore subtract(BendableBigDecimalScore subtrahend) {
        validateCompatible(subtrahend);
        BigDecimal[] newHardScores = new BigDecimal[hardScores.length];
        BigDecimal[] newSoftScores = new BigDecimal[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = hardScores[i].subtract(subtrahend.getHardScore(i));
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = softScores[i].subtract(subtrahend.getSoftScore(i));
        }
        return new BendableBigDecimalScore(
                initScore - subtrahend.getInitScore(),
                newHardScores, newSoftScores);
    }

    @Override
    public BendableBigDecimalScore multiply(double multiplicand) {
        BigDecimal[] newHardScores = new BigDecimal[hardScores.length];
        BigDecimal[] newSoftScores = new BigDecimal[softScores.length];
        BigDecimal bigDecimalMultiplicand = BigDecimal.valueOf(multiplicand);
        for (int i = 0; i < newHardScores.length; i++) {
            // The (unspecified) scale/precision of the multiplicand should have no impact on the returned scale/precision
            newHardScores[i] = hardScores[i].multiply(bigDecimalMultiplicand).setScale(hardScores[i].scale(),
                    RoundingMode.FLOOR);
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            // The (unspecified) scale/precision of the multiplicand should have no impact on the returned scale/precision
            newSoftScores[i] = softScores[i].multiply(bigDecimalMultiplicand).setScale(softScores[i].scale(),
                    RoundingMode.FLOOR);
        }
        return new BendableBigDecimalScore(
                (int) Math.floor(initScore * multiplicand),
                newHardScores, newSoftScores);
    }

    @Override
    public BendableBigDecimalScore divide(double divisor) {
        BigDecimal[] newHardScores = new BigDecimal[hardScores.length];
        BigDecimal[] newSoftScores = new BigDecimal[softScores.length];
        BigDecimal bigDecimalDivisor = BigDecimal.valueOf(divisor);
        for (int i = 0; i < newHardScores.length; i++) {
            BigDecimal hardScore = hardScores[i];
            newHardScores[i] = hardScore.divide(bigDecimalDivisor, hardScore.scale(), RoundingMode.FLOOR);
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            BigDecimal softScore = softScores[i];
            newSoftScores[i] = softScore.divide(bigDecimalDivisor, softScore.scale(), RoundingMode.FLOOR);
        }
        return new BendableBigDecimalScore(
                (int) Math.floor(initScore / divisor),
                newHardScores, newSoftScores);
    }

    @Override
    public BendableBigDecimalScore power(double exponent) {
        BigDecimal[] newHardScores = new BigDecimal[hardScores.length];
        BigDecimal[] newSoftScores = new BigDecimal[softScores.length];
        BigDecimal actualExponent = BigDecimal.valueOf(exponent);
        // The (unspecified) scale/precision of the exponent should have no impact on the returned scale/precision
        // TODO FIXME remove .intValue() so non-integer exponents produce correct results
        // None of the normal Java libraries support BigDecimal.pow(BigDecimal)
        for (int i = 0; i < newHardScores.length; i++) {
            BigDecimal hardScore = hardScores[i];
            newHardScores[i] = hardScore.pow(actualExponent.intValue()).setScale(hardScore.scale(), RoundingMode.FLOOR);
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            BigDecimal softScore = softScores[i];
            newSoftScores[i] = softScore.pow(actualExponent.intValue()).setScale(softScore.scale(), RoundingMode.FLOOR);
        }
        return new BendableBigDecimalScore(
                (int) Math.floor(Math.pow(initScore, exponent)),
                newHardScores, newSoftScores);
    }

    @Override
    public BendableBigDecimalScore negate() {
        BigDecimal[] newHardScores = new BigDecimal[hardScores.length];
        BigDecimal[] newSoftScores = new BigDecimal[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = hardScores[i].negate();
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = softScores[i].negate();
        }
        return new BendableBigDecimalScore(-initScore, newHardScores, newSoftScores);
    }

    @Override
    public BendableBigDecimalScore zero() {
        return BendableBigDecimalScore.zero(getHardLevelsSize(), getSoftLevelsSize());
    }

    @Override
    public Number[] toLevelNumbers() {
        Number[] levelNumbers = new Number[hardScores.length + softScores.length];
        for (int i = 0; i < hardScores.length; i++) {
            levelNumbers[i] = hardScores[i];
        }
        for (int i = 0; i < softScores.length; i++) {
            levelNumbers[hardScores.length + i] = softScores[i];
        }
        return levelNumbers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof BendableBigDecimalScore) {
            BendableBigDecimalScore other = (BendableBigDecimalScore) o;
            if (getHardLevelsSize() != other.getHardLevelsSize()
                    || getSoftLevelsSize() != other.getSoftLevelsSize()) {
                return false;
            }
            if (initScore != other.getInitScore()) {
                return false;
            }
            for (int i = 0; i < hardScores.length; i++) {
                if (!hardScores[i].stripTrailingZeros().equals(other.getHardScore(i).stripTrailingZeros())) {
                    return false;
                }
            }
            for (int i = 0; i < softScores.length; i++) {
                if (!softScores[i].stripTrailingZeros().equals(other.getSoftScore(i).stripTrailingZeros())) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int[] scoreHashCodes = Stream.concat(Arrays.stream(hardScores), Arrays.stream(softScores))
                .map(BigDecimal::stripTrailingZeros)
                .mapToInt(BigDecimal::hashCode)
                .toArray();
        return Objects.hash(initScore, Arrays.hashCode(scoreHashCodes));
    }

    @Override
    public int compareTo(BendableBigDecimalScore other) {
        validateCompatible(other);
        if (initScore != other.getInitScore()) {
            return Integer.compare(initScore, other.getInitScore());
        }
        for (int i = 0; i < hardScores.length; i++) {
            int hardScoreComparison = hardScores[i].compareTo(other.getHardScore(i));
            if (hardScoreComparison != 0) {
                return hardScoreComparison;
            }
        }
        for (int i = 0; i < softScores.length; i++) {
            int softScoreComparison = softScores[i].compareTo(other.getSoftScore(i));
            if (softScoreComparison != 0) {
                return softScoreComparison;
            }
        }
        return 0;
    }

    @Override
    public String toShortString() {
        return buildBendableShortString((n) -> ((BigDecimal) n).compareTo(BigDecimal.ZERO) != 0);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(((hardScores.length + softScores.length) * 4) + 13);
        s.append(getInitPrefix());
        s.append("[");
        boolean first = true;
        for (BigDecimal hardScore : hardScores) {
            if (first) {
                first = false;
            } else {
                s.append("/");
            }
            s.append(hardScore);
        }
        s.append("]hard/[");
        first = true;
        for (BigDecimal softScore : softScores) {
            if (first) {
                first = false;
            } else {
                s.append("/");
            }
            s.append(softScore);
        }
        s.append("]soft");
        return s.toString();
    }

    public void validateCompatible(BendableBigDecimalScore other) {
        if (getHardLevelsSize() != other.getHardLevelsSize()) {
            throw new IllegalArgumentException("The score (" + this
                    + ") with hardScoreSize (" + getHardLevelsSize()
                    + ") is not compatible with the other score (" + other
                    + ") with hardScoreSize (" + other.getHardLevelsSize() + ").");
        }
        if (getSoftLevelsSize() != other.getSoftLevelsSize()) {
            throw new IllegalArgumentException("The score (" + this
                    + ") with softScoreSize (" + getSoftLevelsSize()
                    + ") is not compatible with the other score (" + other
                    + ") with softScoreSize (" + other.getSoftLevelsSize() + ").");
        }
    }

}
