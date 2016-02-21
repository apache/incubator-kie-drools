/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.api.score.buildin.bendablelong;

import org.optaplanner.core.api.score.AbstractScore;
import org.optaplanner.core.api.score.FeasibilityScore;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.buildin.bendablelong.BendableLongScoreDefinition;

import java.util.Arrays;

/**
 * This {@link Score} is based on n levels of long constraints.
 * The number of levels is bendable at configuration time.
 * <p>
 * This class is immutable.
 * <p>
 * The {@link #getHardLevelsSize()} and {@link #getSoftLevelsSize()} must be the same as in the
 * {@link BendableLongScoreDefinition} used.
 * @see Score
 */
public final class BendableLongScore extends AbstractScore<BendableLongScore>
        implements FeasibilityScore<BendableLongScore> {

    public static BendableLongScore parseScore(int hardLevelsSize, int softLevelsSize, String scoreString) {
        int levelsSize = hardLevelsSize + softLevelsSize;
        String[] levelStrings = parseLevelStrings(BendableLongScore.class, scoreString, levelsSize);
        long[] hardScores = new long[hardLevelsSize];
        long[] softScores = new long[softLevelsSize];
        for (int i = 0; i < hardScores.length; i++) {
            hardScores[i] = parseLevelAsLong(BendableLongScore.class, scoreString, levelStrings[i]);
        }
        for (int i = 0; i < softScores.length; i++) {
            softScores[i] = parseLevelAsLong(BendableLongScore.class, scoreString, levelStrings[hardScores.length + i]);
        }
        return valueOf(hardScores, softScores);
    }

    /**
     * Creates a new {@link BendableLongScore}.
     * @param hardScores never null, never change that array afterwards: it must be immutable
     * @param softScores never null, never change that array afterwards: it must be immutable
     * @return never null
     */
    public static BendableLongScore valueOf(long[] hardScores, long[] softScores) {
        return new BendableLongScore(hardScores, softScores);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final long[] hardScores;
    private final long[] softScores;

    /**
     * Private default constructor for default marshalling/unmarshalling of unknown frameworks that use reflection.
     * Such integration is always inferior to the specialized integration modules, such as
     * optaplanner-persistence-jpa, optaplanner-persistence-xstream, optaplanner-persistence-jaxb, ...
     */
    @SuppressWarnings("unused")
    private BendableLongScore() {
        hardScores = null;
        softScores = null;
    }

    protected BendableLongScore(long[] hardScores, long[] softScores) {
        this.hardScores = hardScores;
        this.softScores = softScores;
    }

    // Intentionally no getters for the hardScores or softScores int arrays to guarantee that this class is immutable

    public int getHardLevelsSize() {
        return hardScores.length;
    }

    /**
     * @param index {@code 0 <= index <} {@link #getHardLevelsSize()}
     * @return higher is better
     */
    public long getHardScore(int index) {
        return hardScores[index];
    }

    public int getSoftLevelsSize() {
        return softScores.length;
    }

    /**
     * @param index {@code 0 <= index <} {@link #getSoftLevelsSize()}
     * @return higher is better
     */
    public long getSoftScore(int index) {
        return softScores[index];
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    /**
     * @return {@link #getHardLevelsSize()} + {@link #getSoftLevelsSize()}
     */
    public int getLevelsSize() {
        return hardScores.length + softScores.length;
    }

    /**
     * @param index {@code 0 <= index <} {@link #getLevelsSize()}
     * @return higher is better
     */
    public long getHardOrSoftScore(int index) {
        if (index < hardScores.length) {
            return hardScores[index];
        } else {
            return softScores[index - hardScores.length];
        }
    }

    public boolean isFeasible() {
        for (long hardScore : hardScores) {
            if (hardScore > 0) {
                return true;
            } else if (hardScore < 0) {
                return false;
            }
        }
        return true;
    }

    public BendableLongScore add(BendableLongScore augment) {
        validateCompatible(augment);
        long[] newHardScores = new long[hardScores.length];
        long[] newSoftScores = new long[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = hardScores[i] + augment.getHardScore(i);
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = softScores[i] + augment.getSoftScore(i);
        }
        return new BendableLongScore(newHardScores, newSoftScores);
    }

    public BendableLongScore subtract(BendableLongScore subtrahend) {
        validateCompatible(subtrahend);
        long[] newHardScores = new long[hardScores.length];
        long[] newSoftScores = new long[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = hardScores[i] - subtrahend.getHardScore(i);
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = softScores[i] - subtrahend.getSoftScore(i);
        }
        return new BendableLongScore(newHardScores, newSoftScores);
    }

    public BendableLongScore multiply(double multiplicand) {
        long[] newHardScores = new long[hardScores.length];
        long[] newSoftScores = new long[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = (long) Math.floor(hardScores[i] * multiplicand);
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = (long) Math.floor(softScores[i] * multiplicand);
        }
        return new BendableLongScore(newHardScores, newSoftScores);
    }

    public BendableLongScore divide(double divisor) {
        long[] newHardScores = new long[hardScores.length];
        long[] newSoftScores = new long[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = (long) Math.floor(hardScores[i] / divisor);
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = (long) Math.floor(softScores[i] / divisor);
        }
        return new BendableLongScore(newHardScores, newSoftScores);
    }

    public BendableLongScore power(double exponent) {
        long[] newHardScores = new long[hardScores.length];
        long[] newSoftScores = new long[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = (long) Math.floor(Math.pow(hardScores[i], exponent));
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = (long) Math.floor(Math.pow(softScores[i], exponent));
        }
        return new BendableLongScore(newHardScores, newSoftScores);
    }

    public BendableLongScore negate() {
        long[] newHardScores = new long[hardScores.length];
        long[] newSoftScores = new long[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = - hardScores[i];
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = - softScores[i];
        }
        return new BendableLongScore(newHardScores, newSoftScores);
    }

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

    public boolean equals(Object o) {
        // A direct implementation (instead of EqualsBuilder) to avoid dependencies
        if (this == o) {
            return true;
        } else if (o instanceof BendableLongScore) {
            BendableLongScore other = (BendableLongScore) o;
            if (getHardLevelsSize() != other.getHardLevelsSize()
                    || getSoftLevelsSize() != other.getSoftLevelsSize()) {
                return false;
            }
            for (int i = 0; i < hardScores.length; i++) {
                if (hardScores[i] != other.getHardScore(i)) {
                    return false;
                }
            }
            for (int i = 0; i < softScores.length; i++) {
                if (softScores[i] != other.getSoftScore(i)) {
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
        // A direct implementation (instead of HashCodeBuilder) to avoid dependencies
        int result = Arrays.hashCode(hardScores);
        result = 31 * result + Arrays.hashCode(softScores);
        return result;
    }

    public int compareTo(BendableLongScore other) {
        // A direct implementation (instead of CompareToBuilder) to avoid dependencies
        validateCompatible(other);
        for (int i = 0; i < hardScores.length; i++) {
            if (hardScores[i] != other.getHardScore(i)) {
                if (hardScores[i] < other.getHardScore(i)) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
        for (int i = 0; i < softScores.length; i++) {
            if (softScores[i] != other.getSoftScore(i)) {
                if (softScores[i] < other.getSoftScore(i)) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
        return 0;
    }

    public String toString() {
        StringBuilder s = new StringBuilder(((hardScores.length + softScores.length) * 4) + 1);
        boolean first = true;
        for (long hardScore : hardScores) {
            if (first) {
                first = false;
            } else {
                s.append("/");
            }
            s.append(hardScore);
        }
        for (long softScore : softScores) {
            if (first) {
                first = false;
            } else {
                s.append("/");
            }
            s.append(softScore);
        }
        return s.toString();
    }

    public void validateCompatible(BendableLongScore other) {
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

    @Override
    public boolean isCompatibleArithmeticArgument(Score otherScore) {
        if (!(otherScore instanceof BendableLongScore)) {
            return false;
        }
        BendableLongScore otherBendableScore = (BendableLongScore) otherScore;
        return hardScores.length == otherBendableScore.hardScores.length
                && softScores.length == otherBendableScore.softScores.length;
    }

}
