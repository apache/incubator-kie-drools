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

package org.drools.planner.core.score.buildin.hardsoft;

import org.drools.planner.core.score.AbstractScore;

/**
 * Default implementation of {@link HardSoftScore}.
 * <p/>
 * This class is immutable.
 * @see HardSoftScore
 */
public final class DefaultHardSoftScore extends AbstractScore<HardSoftScore>
        implements HardSoftScore {

    private static final String HARD_LABEL = "hard";
    private static final String SOFT_LABEL = "soft";

    public static DefaultHardSoftScore parseScore(String scoreString) {
        String[] levelStrings = parseLevelStrings(scoreString, HARD_LABEL, SOFT_LABEL);
        int hardScore = Integer.parseInt(levelStrings[0]);
        int softScore = Integer.parseInt(levelStrings[1]);
        return valueOf(hardScore, softScore);
    }

    public static DefaultHardSoftScore valueOf(int hardScore, int softScore) {
        return new DefaultHardSoftScore(hardScore, softScore);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final int hardScore;
    private final int softScore;

    protected DefaultHardSoftScore(int hardScore, int softScore) {
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

    public HardSoftScore add(HardSoftScore augment) {
        return new DefaultHardSoftScore(hardScore + augment.getHardScore(),
                this.softScore + augment.getSoftScore());
    }

    public HardSoftScore subtract(HardSoftScore subtrahend) {
        return new DefaultHardSoftScore(hardScore - subtrahend.getHardScore(),
                this.softScore - subtrahend.getSoftScore());
    }

    public HardSoftScore multiply(double multiplicand) {
        return new DefaultHardSoftScore((int) Math.floor(hardScore * multiplicand),
                (int) Math.floor(this.softScore * multiplicand));
    }

    public HardSoftScore divide(double divisor) {
        return new DefaultHardSoftScore((int) Math.floor(hardScore / divisor),
                (int) Math.floor(this.softScore / divisor));
    }

    public double[] toDoubleLevels() {
        return new double[]{hardScore, softScore};
    }

    public boolean equals(Object o) {
        // A direct implementation (instead of EqualsBuilder) to avoid dependencies
        if (this == o) {
            return true;
        } else if (o instanceof HardSoftScore) {
            HardSoftScore other = (HardSoftScore) o;
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

    public int compareTo(HardSoftScore other) {
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
