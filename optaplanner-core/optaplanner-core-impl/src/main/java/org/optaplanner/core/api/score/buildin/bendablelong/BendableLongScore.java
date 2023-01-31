package org.optaplanner.core.api.score.buildin.bendablelong;

import java.util.Arrays;
import java.util.Objects;

import org.optaplanner.core.api.score.IBendableScore;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.ScoreUtil;
import org.optaplanner.core.impl.score.buildin.BendableLongScoreDefinition;

/**
 * This {@link Score} is based on n levels of long constraints.
 * The number of levels is bendable at configuration time.
 * <p>
 * This class is immutable.
 * <p>
 * The {@link #hardLevelsSize()} and {@link #softLevelsSize()} must be the same as in the
 * {@link BendableLongScoreDefinition} used.
 *
 * @see Score
 */
public final class BendableLongScore implements IBendableScore<BendableLongScore> {

    /**
     * @param scoreString never null
     * @return never null
     */
    public static BendableLongScore parseScore(String scoreString) {
        String[][] scoreTokens = ScoreUtil.parseBendableScoreTokens(BendableLongScore.class, scoreString);
        int initScore = ScoreUtil.parseInitScore(BendableLongScore.class, scoreString, scoreTokens[0][0]);
        long[] hardScores = new long[scoreTokens[1].length];
        for (int i = 0; i < hardScores.length; i++) {
            hardScores[i] = ScoreUtil.parseLevelAsLong(BendableLongScore.class, scoreString, scoreTokens[1][i]);
        }
        long[] softScores = new long[scoreTokens[2].length];
        for (int i = 0; i < softScores.length; i++) {
            softScores[i] = ScoreUtil.parseLevelAsLong(BendableLongScore.class, scoreString, scoreTokens[2][i]);
        }
        return ofUninitialized(initScore, hardScores, softScores);
    }

    /**
     * Creates a new {@link BendableLongScore}.
     *
     * @param initScore see {@link Score#initScore()}
     * @param hardScores never null, never change that array afterwards: it must be immutable
     * @param softScores never null, never change that array afterwards: it must be immutable
     * @return never null
     */
    public static BendableLongScore ofUninitialized(int initScore, long[] hardScores, long[] softScores) {
        return new BendableLongScore(initScore, hardScores, softScores);
    }

    /**
     * Creates a new {@link BendableLongScore}.
     *
     * @param hardScores never null, never change that array afterwards: it must be immutable
     * @param softScores never null, never change that array afterwards: it must be immutable
     * @return never null
     */
    public static BendableLongScore of(long[] hardScores, long[] softScores) {
        return new BendableLongScore(0, hardScores, softScores);
    }

    /**
     * Creates a new {@link BendableLongScore}.
     *
     * @param hardLevelsSize at least 0
     * @param softLevelsSize at least 0
     * @return never null
     */
    public static BendableLongScore zero(int hardLevelsSize, int softLevelsSize) {
        return new BendableLongScore(0, new long[hardLevelsSize], new long[softLevelsSize]);
    }

    /**
     * Creates a new {@link BendableLongScore}.
     *
     * @param hardLevelsSize at least 0
     * @param softLevelsSize at least 0
     * @param hardLevel at least 0, less than hardLevelsSize
     * @param hardScore any
     * @return never null
     */
    public static BendableLongScore ofHard(int hardLevelsSize, int softLevelsSize, int hardLevel, long hardScore) {
        long[] hardScores = new long[hardLevelsSize];
        hardScores[hardLevel] = hardScore;
        return new BendableLongScore(0, hardScores, new long[softLevelsSize]);
    }

    /**
     * Creates a new {@link BendableLongScore}.
     *
     * @param hardLevelsSize at least 0
     * @param softLevelsSize at least 0
     * @param softLevel at least 0, less than softLevelsSize
     * @param softScore any
     * @return never null
     */
    public static BendableLongScore ofSoft(int hardLevelsSize, int softLevelsSize, int softLevel, long softScore) {
        long[] softScores = new long[softLevelsSize];
        softScores[softLevel] = softScore;
        return new BendableLongScore(0, new long[hardLevelsSize], softScores);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final int initScore;
    private final long[] hardScores;
    private final long[] softScores;

    /**
     * Private default constructor for default marshalling/unmarshalling of unknown frameworks that use reflection.
     * Such integration is always inferior to the specialized integration modules, such as
     * optaplanner-persistence-jpa, optaplanner-persistence-jackson, optaplanner-persistence-jaxb, ...
     */
    @SuppressWarnings("unused")
    private BendableLongScore() {
        this(Integer.MIN_VALUE, null, null);
    }

    /**
     * @param initScore see {@link Score#initScore()}
     * @param hardScores never null
     * @param softScores never null
     */
    private BendableLongScore(int initScore, long[] hardScores, long[] softScores) {
        this.initScore = initScore;
        this.hardScores = hardScores;
        this.softScores = softScores;
    }

    @Override
    public int initScore() {
        return initScore;
    }

    /**
     * @return not null, array copy because this class is immutable
     */
    public long[] hardScores() {
        return Arrays.copyOf(hardScores, hardScores.length);
    }

    /**
     * As defined by {@link #hardScores()}.
     *
     * @deprecated Use {@link #hardScores()} instead.
     */
    @Deprecated(forRemoval = true)
    public long[] getHardScores() {
        return hardScores();
    }

    /**
     * @return not null, array copy because this class is immutable
     */
    public long[] softScores() {
        return Arrays.copyOf(softScores, softScores.length);
    }

    /**
     * As defined by {@link #softScores()}.
     *
     * @deprecated Use {@link #softScores()} instead.
     */
    @Deprecated(forRemoval = true)
    public long[] getSoftScores() {
        return softScores();
    }

    @Override
    public int hardLevelsSize() {
        return hardScores.length;
    }

    /**
     * @param index {@code 0 <= index <} {@link #hardLevelsSize()}
     * @return higher is better
     */
    public long hardScore(int index) {
        return hardScores[index];
    }

    /**
     * As defined by {@link #hardScore(int)}.
     *
     * @deprecated Use {@link #hardScore(int)} instead.
     */
    @Deprecated(forRemoval = true)
    public long getHardScore(int index) {
        return hardScore(index);
    }

    @Override
    public int softLevelsSize() {
        return softScores.length;
    }

    /**
     * @param index {@code 0 <= index <} {@link #softLevelsSize()}
     * @return higher is better
     */
    public long softScore(int index) {
        return softScores[index];
    }

    /**
     * As defined by {@link #softScore(int)}.
     *
     * @deprecated Use {@link #softScore(int)} instead.
     */
    @Deprecated(forRemoval = true)
    public long getSoftScore(int index) {
        return softScore(index);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public BendableLongScore withInitScore(int newInitScore) {
        return new BendableLongScore(newInitScore, hardScores, softScores);
    }

    /**
     * @param index {@code 0 <= index <} {@link #levelsSize()}
     * @return higher is better
     */
    public long hardOrSoftScore(int index) {
        if (index < hardScores.length) {
            return hardScores[index];
        } else {
            return softScores[index - hardScores.length];
        }
    }

    /**
     * As defined by {@link #hardOrSoftScore(int)}.
     *
     * @deprecated Use {@link #hardOrSoftScore(int)} instead.
     */
    @Deprecated(forRemoval = true)
    public long getHardOrSoftScore(int index) {
        return hardOrSoftScore(index);
    }

    @Override
    public boolean isFeasible() {
        if (initScore < 0) {
            return false;
        }
        for (long hardScore : hardScores) {
            if (hardScore < 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public BendableLongScore add(BendableLongScore addend) {
        validateCompatible(addend);
        long[] newHardScores = new long[hardScores.length];
        long[] newSoftScores = new long[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = hardScores[i] + addend.hardScore(i);
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = softScores[i] + addend.softScore(i);
        }
        return new BendableLongScore(
                initScore + addend.initScore(),
                newHardScores, newSoftScores);
    }

    @Override
    public BendableLongScore subtract(BendableLongScore subtrahend) {
        validateCompatible(subtrahend);
        long[] newHardScores = new long[hardScores.length];
        long[] newSoftScores = new long[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = hardScores[i] - subtrahend.hardScore(i);
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = softScores[i] - subtrahend.softScore(i);
        }
        return new BendableLongScore(
                initScore - subtrahend.initScore(),
                newHardScores, newSoftScores);
    }

    @Override
    public BendableLongScore multiply(double multiplicand) {
        long[] newHardScores = new long[hardScores.length];
        long[] newSoftScores = new long[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = (long) Math.floor(hardScores[i] * multiplicand);
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = (long) Math.floor(softScores[i] * multiplicand);
        }
        return new BendableLongScore(
                (int) Math.floor(initScore * multiplicand),
                newHardScores, newSoftScores);
    }

    @Override
    public BendableLongScore divide(double divisor) {
        long[] newHardScores = new long[hardScores.length];
        long[] newSoftScores = new long[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = (long) Math.floor(hardScores[i] / divisor);
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = (long) Math.floor(softScores[i] / divisor);
        }
        return new BendableLongScore(
                (int) Math.floor(initScore / divisor),
                newHardScores, newSoftScores);
    }

    @Override
    public BendableLongScore power(double exponent) {
        long[] newHardScores = new long[hardScores.length];
        long[] newSoftScores = new long[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = (long) Math.floor(Math.pow(hardScores[i], exponent));
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = (long) Math.floor(Math.pow(softScores[i], exponent));
        }
        return new BendableLongScore(
                (int) Math.floor(Math.pow(initScore, exponent)),
                newHardScores, newSoftScores);
    }

    @Override
    public BendableLongScore negate() { // Overridden as the default impl would create zero() all the time.
        long[] newHardScores = new long[hardScores.length];
        long[] newSoftScores = new long[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = -hardScores[i];
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = -softScores[i];
        }
        return new BendableLongScore(-initScore, newHardScores, newSoftScores);
    }

    @Override
    public BendableLongScore abs() {
        long[] newHardScores = new long[hardScores.length];
        long[] newSoftScores = new long[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = Math.abs(hardScores[i]);
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = Math.abs(softScores[i]);
        }
        return new BendableLongScore(Math.abs(initScore), newHardScores, newSoftScores);
    }

    @Override
    public BendableLongScore zero() {
        return BendableLongScore.zero(hardLevelsSize(), softLevelsSize());
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
        } else if (o instanceof BendableLongScore) {
            BendableLongScore other = (BendableLongScore) o;
            if (hardLevelsSize() != other.hardLevelsSize()
                    || softLevelsSize() != other.softLevelsSize()) {
                return false;
            }
            if (initScore != other.initScore()) {
                return false;
            }
            for (int i = 0; i < hardScores.length; i++) {
                if (hardScores[i] != other.hardScore(i)) {
                    return false;
                }
            }
            for (int i = 0; i < softScores.length; i++) {
                if (softScores[i] != other.softScore(i)) {
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
        return Objects.hash(initScore, Arrays.hashCode(hardScores), Arrays.hashCode(softScores));
    }

    @Override
    public int compareTo(BendableLongScore other) {
        validateCompatible(other);
        if (initScore != other.initScore()) {
            return Integer.compare(initScore, other.initScore());
        }
        for (int i = 0; i < hardScores.length; i++) {
            if (hardScores[i] != other.hardScore(i)) {
                return Long.compare(hardScores[i], other.hardScore(i));
            }
        }
        for (int i = 0; i < softScores.length; i++) {
            if (softScores[i] != other.softScore(i)) {
                return Long.compare(softScores[i], other.softScore(i));
            }
        }
        return 0;
    }

    @Override
    public String toShortString() {
        return ScoreUtil.buildBendableShortString(this, n -> n.longValue() != 0L);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(((hardScores.length + softScores.length) * 4) + 13);
        s.append(ScoreUtil.getInitPrefix(initScore));
        s.append("[");
        boolean first = true;
        for (long hardScore : hardScores) {
            if (first) {
                first = false;
            } else {
                s.append("/");
            }
            s.append(hardScore);
        }
        s.append("]hard/[");
        first = true;
        for (long softScore : softScores) {
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

    public void validateCompatible(BendableLongScore other) {
        if (hardLevelsSize() != other.hardLevelsSize()) {
            throw new IllegalArgumentException("The score (" + this
                    + ") with hardScoreSize (" + hardLevelsSize()
                    + ") is not compatible with the other score (" + other
                    + ") with hardScoreSize (" + other.hardLevelsSize() + ").");
        }
        if (softLevelsSize() != other.softLevelsSize()) {
            throw new IllegalArgumentException("The score (" + this
                    + ") with softScoreSize (" + softLevelsSize()
                    + ") is not compatible with the other score (" + other
                    + ") with softScoreSize (" + other.softLevelsSize() + ").");
        }
    }

}
