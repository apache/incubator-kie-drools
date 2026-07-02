/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.api.score.buildin.bendablebigdecimal;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import org.optaplanner.core.api.score.IBendableScore;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.ScoreUtil;
import org.optaplanner.core.impl.score.buildin.BendableScoreDefinition;

/**
 * This {@link Score} is based on n levels of {@link BigDecimal} constraints.
 * The number of levels is bendable at configuration time.
 * <p>
 * This class is immutable.
 * <p>
 * The {@link #hardLevelsSize()} and {@link #softLevelsSize()} must be the same as in the
 * {@link BendableScoreDefinition} used.
 *
 * @see Score
 */
public final class BendableBigDecimalScore implements IBendableScore<BendableBigDecimalScore> {

    /**
     * @param scoreString never null
     * @return never null
     */
    public static BendableBigDecimalScore parseScore(String scoreString) {
        String[][] scoreTokens = ScoreUtil.parseBendableScoreTokens(BendableBigDecimalScore.class, scoreString);
        int initScore = ScoreUtil.parseInitScore(BendableBigDecimalScore.class, scoreString, scoreTokens[0][0]);
        BigDecimal[] hardScores = new BigDecimal[scoreTokens[1].length];
        for (int i = 0; i < hardScores.length; i++) {
            hardScores[i] = ScoreUtil.parseLevelAsBigDecimal(BendableBigDecimalScore.class, scoreString, scoreTokens[1][i]);
        }
        BigDecimal[] softScores = new BigDecimal[scoreTokens[2].length];
        for (int i = 0; i < softScores.length; i++) {
            softScores[i] = ScoreUtil.parseLevelAsBigDecimal(BendableBigDecimalScore.class, scoreString, scoreTokens[2][i]);
        }
        return ofUninitialized(initScore, hardScores, softScores);
    }

    /**
     * Creates a new {@link BendableBigDecimalScore}.
     *
     * @param initScore see {@link Score#initScore()}
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

    private final int initScore;
    private final BigDecimal[] hardScores;
    private final BigDecimal[] softScores;

    /**
     * Private default constructor for default marshalling/unmarshalling of unknown frameworks that use reflection.
     * Such integration is always inferior to the specialized integration modules, such as
     * optaplanner-persistence-jpa, optaplanner-persistence-jackson, optaplanner-persistence-jaxb, ...
     */
    @SuppressWarnings("unused")
    private BendableBigDecimalScore() {
        this(Integer.MIN_VALUE, null, null);
    }

    /**
     * @param initScore see {@link Score#initScore()}
     * @param hardScores never null
     * @param softScores never null
     */
    private BendableBigDecimalScore(int initScore, BigDecimal[] hardScores, BigDecimal[] softScores) {
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
    public BigDecimal[] hardScores() {
        return Arrays.copyOf(hardScores, hardScores.length);
    }

    /**
     * As defined by {@link #hardScores()}.
     *
     * @deprecated Use {@link #hardScores()} instead.
     */
    @Deprecated(forRemoval = true)
    public BigDecimal[] getHardScores() {
        return hardScores();
    }

    /**
     * @return not null, array copy because this class is immutable
     */
    public BigDecimal[] softScores() {
        return Arrays.copyOf(softScores, softScores.length);
    }

    /**
     * As defined by {@link #softScores()}.
     *
     * @deprecated Use {@link #softScores()} instead.
     */
    @Deprecated(forRemoval = true)
    public BigDecimal[] getSoftScores() {
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
    public BigDecimal hardScore(int index) {
        return hardScores[index];
    }

    /**
     * As defined by {@link #hardScore(int)}.
     *
     * @deprecated Use {@link #hardScore(int)} instead.
     */
    @Deprecated(forRemoval = true)
    public BigDecimal getHardScore(int index) {
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
    public BigDecimal softScore(int index) {
        return softScores[index];
    }

    /**
     * As defined by {@link #softScore(int)}.
     *
     * @deprecated Use {@link #softScore(int)} instead.
     */
    @Deprecated(forRemoval = true)
    public BigDecimal getSoftScore(int index) {
        return softScore(index);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public BendableBigDecimalScore withInitScore(int newInitScore) {
        return new BendableBigDecimalScore(newInitScore, hardScores, softScores);
    }

    /**
     * @param index {@code 0 <= index <} {@link #levelsSize()}
     * @return higher is better
     */
    public BigDecimal hardOrSoftScore(int index) {
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
    public BigDecimal getHardOrSoftScore(int index) {
        return hardOrSoftScore(index);
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
            newHardScores[i] = hardScores[i].add(addend.hardScore(i));
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = softScores[i].add(addend.softScore(i));
        }
        return new BendableBigDecimalScore(
                initScore + addend.initScore(),
                newHardScores, newSoftScores);
    }

    @Override
    public BendableBigDecimalScore subtract(BendableBigDecimalScore subtrahend) {
        validateCompatible(subtrahend);
        BigDecimal[] newHardScores = new BigDecimal[hardScores.length];
        BigDecimal[] newSoftScores = new BigDecimal[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = hardScores[i].subtract(subtrahend.hardScore(i));
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = softScores[i].subtract(subtrahend.softScore(i));
        }
        return new BendableBigDecimalScore(
                initScore - subtrahend.initScore(),
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
    public BendableBigDecimalScore negate() { // Overridden as the default impl would create zero() all the time.
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
    public BendableBigDecimalScore abs() {
        BigDecimal[] newHardScores = new BigDecimal[hardScores.length];
        BigDecimal[] newSoftScores = new BigDecimal[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = hardScores[i].abs();
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = softScores[i].abs();
        }
        return new BendableBigDecimalScore(Math.abs(initScore), newHardScores, newSoftScores);
    }

    @Override
    public BendableBigDecimalScore zero() {
        return BendableBigDecimalScore.zero(hardLevelsSize(), softLevelsSize());
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
            if (hardLevelsSize() != other.hardLevelsSize()
                    || softLevelsSize() != other.softLevelsSize()) {
                return false;
            }
            if (initScore != other.initScore()) {
                return false;
            }
            for (int i = 0; i < hardScores.length; i++) {
                if (!hardScores[i].stripTrailingZeros().equals(other.hardScore(i).stripTrailingZeros())) {
                    return false;
                }
            }
            for (int i = 0; i < softScores.length; i++) {
                if (!softScores[i].stripTrailingZeros().equals(other.softScore(i).stripTrailingZeros())) {
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
        if (initScore != other.initScore()) {
            return Integer.compare(initScore, other.initScore());
        }
        for (int i = 0; i < hardScores.length; i++) {
            int hardScoreComparison = hardScores[i].compareTo(other.hardScore(i));
            if (hardScoreComparison != 0) {
                return hardScoreComparison;
            }
        }
        for (int i = 0; i < softScores.length; i++) {
            int softScoreComparison = softScores[i].compareTo(other.softScore(i));
            if (softScoreComparison != 0) {
                return softScoreComparison;
            }
        }
        return 0;
    }

    @Override
    public String toShortString() {
        return ScoreUtil.buildBendableShortString(this, n -> ((BigDecimal) n).compareTo(BigDecimal.ZERO) != 0);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(((hardScores.length + softScores.length) * 4) + 13);
        s.append(ScoreUtil.getInitPrefix(initScore));
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
