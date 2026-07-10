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

package org.optaplanner.core.api.score.buildin.bendable;

import java.util.Arrays;
import java.util.Objects;

import org.optaplanner.core.api.score.IBendableScore;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.ScoreUtil;
import org.optaplanner.core.impl.score.buildin.BendableScoreDefinition;

/**
 * This {@link Score} is based on n levels of int constraints.
 * The number of levels is bendable at configuration time.
 * <p>
 * This class is immutable.
 * <p>
 * The {@link #hardLevelsSize()} and {@link #softLevelsSize()} must be the same as in the
 * {@link BendableScoreDefinition} used.
 *
 * @see Score
 */
public final class BendableScore implements IBendableScore<BendableScore> {

    /**
     * @param scoreString never null
     * @return never null
     */
    public static BendableScore parseScore(String scoreString) {
        String[][] scoreTokens = ScoreUtil.parseBendableScoreTokens(BendableScore.class, scoreString);
        int initScore = ScoreUtil.parseInitScore(BendableScore.class, scoreString, scoreTokens[0][0]);
        int[] hardScores = new int[scoreTokens[1].length];
        for (int i = 0; i < hardScores.length; i++) {
            hardScores[i] = ScoreUtil.parseLevelAsInt(BendableScore.class, scoreString, scoreTokens[1][i]);
        }
        int[] softScores = new int[scoreTokens[2].length];
        for (int i = 0; i < softScores.length; i++) {
            softScores[i] = ScoreUtil.parseLevelAsInt(BendableScore.class, scoreString, scoreTokens[2][i]);
        }
        return ofUninitialized(initScore, hardScores, softScores);
    }

    /**
     * Creates a new {@link BendableScore}.
     *
     * @param initScore see {@link Score#initScore()}
     * @param hardScores never null, never change that array afterwards: it must be immutable
     * @param softScores never null, never change that array afterwards: it must be immutable
     * @return never null
     */
    public static BendableScore ofUninitialized(int initScore, int[] hardScores, int[] softScores) {
        return new BendableScore(initScore, hardScores, softScores);
    }

    /**
     * Creates a new {@link BendableScore}.
     *
     * @param hardScores never null, never change that array afterwards: it must be immutable
     * @param softScores never null, never change that array afterwards: it must be immutable
     * @return never null
     */
    public static BendableScore of(int[] hardScores, int[] softScores) {
        return new BendableScore(0, hardScores, softScores);
    }

    /**
     * Creates a new {@link BendableScore}.
     *
     * @param hardLevelsSize at least 0
     * @param softLevelsSize at least 0
     * @return never null
     */
    public static BendableScore zero(int hardLevelsSize, int softLevelsSize) {
        return new BendableScore(0, new int[hardLevelsSize], new int[softLevelsSize]);
    }

    /**
     * Creates a new {@link BendableScore}.
     *
     * @param hardLevelsSize at least 0
     * @param softLevelsSize at least 0
     * @param hardLevel at least 0, less than hardLevelsSize
     * @param hardScore any
     * @return never null
     */
    public static BendableScore ofHard(int hardLevelsSize, int softLevelsSize, int hardLevel, int hardScore) {
        int[] hardScores = new int[hardLevelsSize];
        hardScores[hardLevel] = hardScore;
        return new BendableScore(0, hardScores, new int[softLevelsSize]);
    }

    /**
     * Creates a new {@link BendableScore}.
     *
     * @param hardLevelsSize at least 0
     * @param softLevelsSize at least 0
     * @param softLevel at least 0, less than softLevelsSize
     * @param softScore any
     * @return never null
     */
    public static BendableScore ofSoft(int hardLevelsSize, int softLevelsSize, int softLevel, int softScore) {
        int[] softScores = new int[softLevelsSize];
        softScores[softLevel] = softScore;
        return new BendableScore(0, new int[hardLevelsSize], softScores);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final int initScore;
    private final int[] hardScores;
    private final int[] softScores;

    /**
     * Private default constructor for default marshalling/unmarshalling of unknown frameworks that use reflection.
     * Such integration is always inferior to the specialized integration modules, such as
     * optaplanner-persistence-jpa, optaplanner-persistence-jackson, optaplanner-persistence-jaxb, ...
     */
    @SuppressWarnings("unused")
    private BendableScore() {
        this(Integer.MIN_VALUE, null, null);
    }

    /**
     * @param initScore see {@link Score#initScore()}
     * @param hardScores never null
     * @param softScores never null
     */
    private BendableScore(int initScore, int[] hardScores, int[] softScores) {
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
    public int[] hardScores() {
        return Arrays.copyOf(hardScores, hardScores.length);
    }

    /**
     * As defined by {@link #hardScores()}.
     *
     * @deprecated Use {@link #hardScores()} instead.
     */
    @Deprecated(forRemoval = true)
    public int[] getHardScores() {
        return hardScores();
    }

    /**
     * @return not null, array copy because this class is immutable
     */
    public int[] softScores() {
        return Arrays.copyOf(softScores, softScores.length);
    }

    /**
     * As defined by {@link #softScores()}.
     *
     * @deprecated Use {@link #softScores()} instead.
     */
    @Deprecated(forRemoval = true)
    public int[] getSoftScores() {
        return softScores();
    }

    @Override
    public int hardLevelsSize() {
        return hardScores.length;
    }

    /**
     * @param hardLevel {@code 0 <= hardLevel <} {@link #hardLevelsSize()}.
     *        The {@code scoreLevel} is {@code hardLevel} for hard levels and {@code softLevel + hardLevelSize} for soft levels.
     * @return higher is better
     */
    public int hardScore(int hardLevel) {
        return hardScores[hardLevel];
    }

    /**
     * As defined by {@link #hardScore(int)}.
     *
     * @deprecated Use {@link #hardScore(int)} instead.
     */
    @Deprecated(forRemoval = true)
    public int getHardScore(int hardLevel) {
        return hardScore(hardLevel);
    }

    @Override
    public int softLevelsSize() {
        return softScores.length;
    }

    /**
     * @param softLevel {@code 0 <= softLevel <} {@link #softLevelsSize()}.
     *        The {@code scoreLevel} is {@code hardLevel} for hard levels and {@code softLevel + hardLevelSize} for soft levels.
     * @return higher is better
     */
    public int softScore(int softLevel) {
        return softScores[softLevel];
    }

    /**
     * As defined by {@link #softScore(int)}.
     *
     * @deprecated Use {@link #softScore(int)} instead.
     */
    @Deprecated(forRemoval = true)
    public int getSoftScore(int hardLevel) {
        return softScore(hardLevel);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public BendableScore withInitScore(int newInitScore) {
        return new BendableScore(newInitScore, hardScores, softScores);
    }

    /**
     * @param level {@code 0 <= level <} {@link #levelsSize()}
     * @return higher is better
     */
    public int hardOrSoftScore(int level) {
        if (level < hardScores.length) {
            return hardScores[level];
        } else {
            return softScores[level - hardScores.length];
        }
    }

    /**
     * As defined by {@link #hardOrSoftScore(int)}.
     *
     * @deprecated Use {@link #hardOrSoftScore(int)} instead.
     */
    @Deprecated(forRemoval = true)
    public int getHardOrSoftScore(int level) {
        return hardOrSoftScore(level);
    }

    @Override
    public boolean isFeasible() {
        if (initScore < 0) {
            return false;
        }
        for (int hardScore : hardScores) {
            if (hardScore < 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public BendableScore add(BendableScore addend) {
        validateCompatible(addend);
        int[] newHardScores = new int[hardScores.length];
        int[] newSoftScores = new int[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = hardScores[i] + addend.hardScore(i);
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = softScores[i] + addend.softScore(i);
        }
        return new BendableScore(
                initScore + addend.initScore(),
                newHardScores, newSoftScores);
    }

    @Override
    public BendableScore subtract(BendableScore subtrahend) {
        validateCompatible(subtrahend);
        int[] newHardScores = new int[hardScores.length];
        int[] newSoftScores = new int[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = hardScores[i] - subtrahend.hardScore(i);
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = softScores[i] - subtrahend.softScore(i);
        }
        return new BendableScore(
                initScore - subtrahend.initScore(),
                newHardScores, newSoftScores);
    }

    @Override
    public BendableScore multiply(double multiplicand) {
        int[] newHardScores = new int[hardScores.length];
        int[] newSoftScores = new int[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = (int) Math.floor(hardScores[i] * multiplicand);
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = (int) Math.floor(softScores[i] * multiplicand);
        }
        return new BendableScore(
                (int) Math.floor(initScore * multiplicand),
                newHardScores, newSoftScores);
    }

    @Override
    public BendableScore divide(double divisor) {
        int[] newHardScores = new int[hardScores.length];
        int[] newSoftScores = new int[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = (int) Math.floor(hardScores[i] / divisor);
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = (int) Math.floor(softScores[i] / divisor);
        }
        return new BendableScore(
                (int) Math.floor(initScore / divisor),
                newHardScores, newSoftScores);
    }

    @Override
    public BendableScore power(double exponent) {
        int[] newHardScores = new int[hardScores.length];
        int[] newSoftScores = new int[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = (int) Math.floor(Math.pow(hardScores[i], exponent));
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = (int) Math.floor(Math.pow(softScores[i], exponent));
        }
        return new BendableScore(
                (int) Math.floor(Math.pow(initScore, exponent)),
                newHardScores, newSoftScores);
    }

    @Override
    public BendableScore negate() { // Overridden as the default impl would create zero() all the time.
        int[] newHardScores = new int[hardScores.length];
        int[] newSoftScores = new int[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = -hardScores[i];
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = -softScores[i];
        }
        return new BendableScore(-initScore, newHardScores, newSoftScores);
    }

    @Override
    public BendableScore abs() {
        int[] newHardScores = new int[hardScores.length];
        int[] newSoftScores = new int[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = Math.abs(hardScores[i]);
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = Math.abs(softScores[i]);
        }
        return new BendableScore(Math.abs(initScore), newHardScores, newSoftScores);
    }

    @Override
    public BendableScore zero() {
        return BendableScore.zero(hardLevelsSize(), softLevelsSize());
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
        } else if (o instanceof BendableScore) {
            BendableScore other = (BendableScore) o;
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
    public int compareTo(BendableScore other) {
        validateCompatible(other);
        if (initScore != other.initScore()) {
            return Integer.compare(initScore, other.initScore());
        }
        for (int i = 0; i < hardScores.length; i++) {
            if (hardScores[i] != other.hardScore(i)) {
                return Integer.compare(hardScores[i], other.hardScore(i));
            }
        }
        for (int i = 0; i < softScores.length; i++) {
            if (softScores[i] != other.softScore(i)) {
                return Integer.compare(softScores[i], other.softScore(i));
            }
        }
        return 0;
    }

    @Override
    public String toShortString() {
        return ScoreUtil.buildBendableShortString(this, n -> n.intValue() != 0);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(((hardScores.length + softScores.length) * 4) + 13);
        s.append(ScoreUtil.getInitPrefix(initScore));
        s.append("[");
        boolean first = true;
        for (int hardScore : hardScores) {
            if (first) {
                first = false;
            } else {
                s.append("/");
            }
            s.append(hardScore);
        }
        s.append("]hard/[");
        first = true;
        for (int softScore : softScores) {
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

    public void validateCompatible(BendableScore other) {
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
