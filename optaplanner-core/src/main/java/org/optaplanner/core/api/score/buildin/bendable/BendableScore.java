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

package org.optaplanner.core.api.score.buildin.bendable;

import org.optaplanner.core.api.score.AbstractScore;
import org.optaplanner.core.api.score.FeasibilityScore;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.buildin.bendable.BendableScoreDefinition;

/**
 * This {@link Score} is based on n levels of int constraints.
 * The number of levels is bendable at configuration time.
 * <p>
 * This class is immutable.
 * <p>
 * The {@link #getHardLevelsSize()} and {@link #getSoftLevelsSize()} must be the same as in the
 * {@link BendableScoreDefinition} used.
 * @see Score
 */
public final class BendableScore extends AbstractScore<BendableScore>
        implements FeasibilityScore<BendableScore> {

    /**
     * @param hardLevelsSize {@code >= 0}
     * @param softLevelsSize {@code >= 0}
     * @param scoreString never null
     * @return never null
     */
    public static BendableScore parseScore(int hardLevelsSize, int softLevelsSize, String scoreString) {
        int levelsSize = hardLevelsSize + softLevelsSize;
        String[] levelStrings = parseLevelStrings(BendableScore.class, scoreString, levelsSize);
        int[] hardScores = new int[hardLevelsSize];
        int[] softScores = new int[softLevelsSize];
        for (int i = 0; i < hardScores.length; i++) {
            hardScores[i] = parseLevelAsInt(BendableScore.class, scoreString, levelStrings[i]);
        }
        for (int i = 0; i < softScores.length; i++) {
            softScores[i] = parseLevelAsInt(BendableScore.class, scoreString, levelStrings[hardScores.length + i]);
        }
        return valueOf(hardScores, softScores);
    }

    /**
     * Creates a new {@link BendableScore}.
     * @param hardScores never null, never change that array afterwards: it must be immutable
     * @param softScores never null, never change that array afterwards: it must be immutable
     * @return never null
     */
    public static BendableScore valueOf(int[] hardScores, int[] softScores) {
        return new BendableScore(hardScores, softScores);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final int[] hardScores;
    private final int[] softScores;

    /**
     * Private default constructor for default marshalling/unmarshalling of unknown frameworks that use reflection.
     * Such integration is always inferior to the specialized integration modules, such as
     * optaplanner-persistence-jpa, optaplanner-persistence-xstream, optaplanner-persistence-jaxb, ...
     */
    @SuppressWarnings("unused")
    private BendableScore() {
        hardScores = null;
        softScores = null;
    }

    /**
     * @param hardScores never null
     * @param softScores never null
     */
    protected BendableScore(int[] hardScores, int[] softScores) {
        this.hardScores = hardScores;
        this.softScores = softScores;
    }

    // Intentionally no getters for the hardScores or softScores int arrays to guarantee that this class is immutable

    /**
     * @return {@code >= 0}
     */
    public int getHardLevelsSize() {
        return hardScores.length;
    }

    /**
     * @param index {@code 0 <= index <} {@link #getHardLevelsSize()}
     * @return higher is better
     */
    public int getHardScore(int index) {
        return hardScores[index];
    }

    /**
     * @return {@code >= 0}
     */
    public int getSoftLevelsSize() {
        return softScores.length;
    }

    /**
     * @param index {@code 0 <= index <} {@link #getSoftLevelsSize()}
     * @return higher is better
     */
    public int getSoftScore(int index) {
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
    public int getHardOrSoftScore(int index) {
        if (index < hardScores.length) {
            return hardScores[index];
        } else {
            return softScores[index - hardScores.length];
        }
    }

    public boolean isFeasible() {
        for (int hardScore : hardScores) {
            if (hardScore > 0) {
                return true;
            } else if (hardScore < 0) {
                return false;
            }
        }
        return true;
    }

    public BendableScore add(BendableScore augment) {
        validateCompatible(augment);
        int[] newHardScores = new int[hardScores.length];
        int[] newSoftScores = new int[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = hardScores[i] + augment.getHardScore(i);
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = softScores[i] + augment.getSoftScore(i);
        }
        return new BendableScore(newHardScores, newSoftScores);
    }

    public BendableScore subtract(BendableScore subtrahend) {
        validateCompatible(subtrahend);
        int[] newHardScores = new int[hardScores.length];
        int[] newSoftScores = new int[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = hardScores[i] - subtrahend.getHardScore(i);
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = softScores[i] - subtrahend.getSoftScore(i);
        }
        return new BendableScore(newHardScores, newSoftScores);
    }

    public BendableScore multiply(double multiplicand) {
        int[] newHardScores = new int[hardScores.length];
        int[] newSoftScores = new int[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = (int) Math.floor(hardScores[i] * multiplicand);
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = (int) Math.floor(softScores[i] * multiplicand);
        }
        return new BendableScore(newHardScores, newSoftScores);
    }

    public BendableScore divide(double divisor) {
        int[] newHardScores = new int[hardScores.length];
        int[] newSoftScores = new int[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = (int) Math.floor(hardScores[i] / divisor);
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = (int) Math.floor(softScores[i] / divisor);
        }
        return new BendableScore(newHardScores, newSoftScores);
    }

    public BendableScore power(double exponent) {
        int[] newHardScores = new int[hardScores.length];
        int[] newSoftScores = new int[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = (int) Math.floor(Math.pow(hardScores[i], exponent));
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = (int) Math.floor(Math.pow(softScores[i], exponent));
        }
        return new BendableScore(newHardScores, newSoftScores);
    }

    public BendableScore negate() {
        int[] newHardScores = new int[hardScores.length];
        int[] newSoftScores = new int[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = - hardScores[i];
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = - softScores[i];
        }
        return new BendableScore(newHardScores, newSoftScores);
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
        } else if (o instanceof BendableScore) {
            BendableScore other = (BendableScore) o;
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

    public int hashCode() {
        // A direct implementation (instead of HashCodeBuilder) to avoid dependencies
        int hashCode = 17;
        for (int hardScore : hardScores) {
            hashCode = hashCode * 37 + hardScore;
        }
        for (int softScore : softScores) {
            hashCode = hashCode * 37 + softScore;
        }
        return hashCode;
    }

    public int compareTo(BendableScore other) {
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
        for (int hardScore : hardScores) {
            if (first) {
                first = false;
            } else {
                s.append("/");
            }
            s.append(hardScore);
        }
        for (int softScore : softScores) {
            if (first) {
                first = false;
            } else {
                s.append("/");
            }
            s.append(softScore);
        }
        return s.toString();
    }

    public void validateCompatible(BendableScore other) {
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
        if (!(otherScore instanceof BendableScore)) {
            return false;
        }
        BendableScore otherBendableScore = (BendableScore) otherScore;
        return hardScores.length == otherBendableScore.hardScores.length
                && softScores.length == otherBendableScore.softScores.length;
    }

}
