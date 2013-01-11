/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.core.score.buildin.hardandsoft;

import org.drools.planner.core.score.AbstractScore;

/**
 * Default implementation of {@link HardAndSoftScore}.
 * <p/>
 * This class is immutable.
 * @see HardAndSoftScore
 */
public final class DefaultHardAndSoftScore extends AbstractScore<HardAndSoftScore>
        implements HardAndSoftScore {

    private static final String HARD_LABEL = "hard";
    private static final String SOFT_LABEL = "soft";

    public static DefaultHardAndSoftScore parseScore(String scoreString) {
        String[] levelStrings = parseLevelStrings(scoreString, HARD_LABEL, SOFT_LABEL);
        int hardScore = Integer.parseInt(levelStrings[0]);
        int softScore = Integer.parseInt(levelStrings[1]);
        return valueOf(hardScore, softScore);
    }

    public static DefaultHardAndSoftScore valueOf(int hardScore, int softScore) {
        return new DefaultHardAndSoftScore(hardScore, softScore);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final int hardScore;
    private final int softScore;

    protected DefaultHardAndSoftScore(int hardScore, int softScore) {
        this.hardScore = hardScore;
        this.softScore = softScore;
    }

    public int getHardScore() {
        return hardScore;
    }

    public int getSoftScore() {
        return softScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isFeasible() {
        return getHardScore() >= 0;
    }

    public HardAndSoftScore add(HardAndSoftScore augment) {
        return new DefaultHardAndSoftScore(hardScore + augment.getHardScore(),
                this.softScore + augment.getSoftScore());
    }

    public HardAndSoftScore subtract(HardAndSoftScore subtrahend) {
        return new DefaultHardAndSoftScore(hardScore - subtrahend.getHardScore(),
                this.softScore - subtrahend.getSoftScore());
    }

    public HardAndSoftScore multiply(double multiplicand) {
        return new DefaultHardAndSoftScore((int) Math.floor(hardScore * multiplicand),
                (int) Math.floor(this.softScore * multiplicand));
    }

    public HardAndSoftScore divide(double divisor) {
        return new DefaultHardAndSoftScore((int) Math.floor(hardScore / divisor),
                (int) Math.floor(this.softScore / divisor));
    }

    public double[] toDoubleLevels() {
        return new double[]{hardScore, softScore};
    }

    public boolean equals(Object o) {
        // A direct implementation (instead of EqualsBuilder) to avoid dependencies
        if (this == o) {
            return true;
        } else if (o instanceof HardAndSoftScore) {
            HardAndSoftScore other = (HardAndSoftScore) o;
            return hardScore == other.getHardScore()
                    && softScore == other.getSoftScore();
        } else {
            return false;
        }
    }

    public int hashCode() {
        // A direct implementation (instead of HashCodeBuilder) to avoid dependencies
        return (((17 * 37) + hardScore)) * 37 + softScore;
    }

    public int compareTo(HardAndSoftScore other) {
        // A direct implementation (instead of CompareToBuilder) to avoid dependencies
        if (hardScore != other.getHardScore()) {
            if (hardScore < other.getHardScore()) {
                return -1;
            } else {
                return 1;
            }
        } else {
            if (softScore < other.getSoftScore()) {
                return -1;
            } else if (softScore > other.getSoftScore()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    public String toString() {
        return hardScore + HARD_LABEL + "/" + softScore + SOFT_LABEL;
    }

}
