/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.core.score.buildin.hardsoftlong;

import org.drools.planner.core.score.AbstractScore;

/**
 * Default implementation of {@link HardSoftLongScore}.
 * <p/>
 * This class is immutable.
 * @see HardSoftLongScore
 */
public final class DefaultHardSoftLongScore extends AbstractScore<HardSoftLongScore>
        implements HardSoftLongScore {

    private static final String HARD_LABEL = "hard";
    private static final String SOFT_LABEL = "soft";

    public static DefaultHardSoftLongScore parseScore(String scoreString) {
        String[] levelStrings = parseLevelStrings(scoreString, HARD_LABEL, SOFT_LABEL);
        long hardScore = Long.parseLong(levelStrings[0]);
        long softScore = Long.parseLong(levelStrings[1]);
        return valueOf(hardScore, softScore);
    }

    public static DefaultHardSoftLongScore valueOf(long hardScore, long softScore) {
        return new DefaultHardSoftLongScore(hardScore, softScore);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final long hardScore;
    private final long softScore;

    protected DefaultHardSoftLongScore(long hardScore, long softScore) {
        this.hardScore = hardScore;
        this.softScore = softScore;
    }

    public long getHardScore() {
        return hardScore;
    }

    public long getSoftScore() {
        return softScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isFeasible() {
        return getHardScore() >= 0L;
    }

    public HardSoftLongScore add(HardSoftLongScore augment) {
        return new DefaultHardSoftLongScore(hardScore + augment.getHardScore(),
                this.softScore + augment.getSoftScore());
    }

    public HardSoftLongScore subtract(HardSoftLongScore subtrahend) {
        return new DefaultHardSoftLongScore(hardScore - subtrahend.getHardScore(),
                this.softScore - subtrahend.getSoftScore());
    }

    public HardSoftLongScore multiply(double multiplicand) {
        return new DefaultHardSoftLongScore((long) Math.floor(hardScore * multiplicand),
                (long) Math.floor(this.softScore * multiplicand));
    }

    public HardSoftLongScore divide(double divisor) {
        return new DefaultHardSoftLongScore((long) Math.floor(hardScore / divisor),
                (long) Math.floor(this.softScore / divisor));
    }

    public double[] toDoubleLevels() {
        return new double[]{hardScore, softScore};
    }

    public boolean equals(Object o) {
        // A direct implementation (instead of EqualsBuilder) to avoid dependencies
        if (this == o) {
            return true;
        } else if (o instanceof HardSoftLongScore) {
            HardSoftLongScore other = (HardSoftLongScore) o;
            return hardScore == other.getHardScore()
                    && softScore == other.getSoftScore();
        } else {
            return false;
        }
    }

    public int hashCode() {
        // A direct implementation (instead of HashCodeBuilder) to avoid dependencies
        return (((17 * 37) + Long.valueOf(hardScore).hashCode())) * 37 + Long.valueOf(softScore).hashCode();
    }

    public int compareTo(HardSoftLongScore other) {
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
