/*
 * Copyright 2013 JBoss Inc
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
 * <p/>
 * This class is immutable.
 * <p/>
 * The {@link #getHardLevelCount()} and {@link #getSoftLevelCount()} must be the same as in the
 * {@link BendableScoreDefinition} used.
 * @see Score
 */
public final class BendableScore extends AbstractScore<BendableScore>
        implements FeasibilityScore<BendableScore> {

    public static BendableScore parseScore(int hardLevelCount, int softLevelCount, String scoreString) {
        int levelCount = hardLevelCount + softLevelCount;
        String[] levelStrings = parseLevelStrings(scoreString, levelCount);
        int[] hardScores = new int[hardLevelCount];
        int[] softScores = new int[softLevelCount];
        for (int i = 0; i < hardScores.length; i++) {
            hardScores[i] = Integer.parseInt(levelStrings[i]);
        }
        for (int i = 0; i < softScores.length; i++) {
            softScores[i] = Integer.parseInt(levelStrings[hardScores.length + i]);
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

    protected BendableScore(int[] hardScores, int[] softScores) {
        this.hardScores = hardScores;
        this.softScores = softScores;
    }

    // Intentionally no getters for the hardScores or softScores int arrays to guarantee that this class is immutable

    public int getHardLevelCount() {
        return hardScores.length;
    }

    /**
     * @param index 0 <= index < hardLevelCount
     * @return higher is better
     */
    public int getHardScore(int index) {
        return hardScores[index];
    }

    public int getSoftLevelCount() {
        return softScores.length;
    }

    /**
     * @param index 0 <= index < softLevelCount
     * @return higher is better
     */
    public int getSoftScore(int index) {
        return softScores[index];
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

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

    public double[] toDoubleLevels() {
        double[] doubleLevels = new double[hardScores.length + softScores.length];
        for (int i = 0; i < hardScores.length; i++) {
            doubleLevels[i] = hardScores[i];
        }
        for (int i = 0; i < softScores.length; i++) {
            doubleLevels[hardScores.length + i] = softScores[i];
        }
        return doubleLevels;
    }

    public boolean equals(Object o) {
        // A direct implementation (instead of EqualsBuilder) to avoid dependencies
        if (this == o) {
            return true;
        } else if (o instanceof BendableScore) {
            BendableScore other = (BendableScore) o;
            if (getHardLevelCount() != other.getHardLevelCount()
                    || getSoftLevelCount() != other.getSoftLevelCount()) {
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
        if (getHardLevelCount() != other.getHardLevelCount()) {
            throw new IllegalArgumentException("The score (" + this
                    + ") with hardScoreSize (" + getHardLevelCount()
                    + ") is not compatible with the other score (" + other
                    + ") with hardScoreSize (" + other.getHardLevelCount() + ").");
        }
        if (getSoftLevelCount() != other.getSoftLevelCount()) {
            throw new IllegalArgumentException("The score (" + this
                    + ") with softScoreSize (" + getSoftLevelCount()
                    + ") is not compatible with the other score (" + other
                    + ") with softScoreSize (" + other.getSoftLevelCount() + ").");
        }
    }

}
