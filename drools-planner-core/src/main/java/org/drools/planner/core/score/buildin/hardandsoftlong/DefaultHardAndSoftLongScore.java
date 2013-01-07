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

package org.drools.planner.core.score.buildin.hardandsoftlong;

import org.drools.planner.core.score.AbstractScore;

/**
 * Default implementation of {@link HardAndSoftLongScore}.
 * <p/>
 * This class is immutable.
 * @see HardAndSoftLongScore
 */
public final class DefaultHardAndSoftLongScore extends AbstractScore<HardAndSoftLongScore>
        implements HardAndSoftLongScore {

    private static final String HARD_LABEL = "hard";
    private static final String SOFT_LABEL = "soft";

    public static DefaultHardAndSoftLongScore parseScore(String scoreString) {
        String[] scoreTokens = scoreString.split(HARD_LABEL + "\\/");
        if (scoreTokens.length != 2 || !scoreTokens[1].endsWith(SOFT_LABEL)) {
            throw new IllegalArgumentException("The scoreString (" + scoreString
                    + ") doesn't follow the 999hard/999soft pattern.");
        }
        long hardScore = Long.parseLong(scoreTokens[0]);
        long softScore = Long.parseLong(scoreTokens[1].substring(0, scoreTokens[1].length() - SOFT_LABEL.length()));
        return valueOf(hardScore, softScore);
    }

    public static DefaultHardAndSoftLongScore valueOf(long hardScore) {
        return new DefaultHardAndSoftLongScore(hardScore);
    }

    public static DefaultHardAndSoftLongScore valueOf(long hardScore, long softScore) {
        return new DefaultHardAndSoftLongScore(hardScore, softScore);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final long hardScore;
    private final long softScore;

    public DefaultHardAndSoftLongScore(long hardScore) {
        // Any other softScore is better
        this(hardScore, Long.MIN_VALUE);
    }

    public DefaultHardAndSoftLongScore(long hardScore, long softScore) {
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

    public HardAndSoftLongScore add(HardAndSoftLongScore augment) {
        return new DefaultHardAndSoftLongScore(hardScore + augment.getHardScore(),
                this.softScore + augment.getSoftScore());
    }

    public HardAndSoftLongScore subtract(HardAndSoftLongScore subtrahend) {
        return new DefaultHardAndSoftLongScore(hardScore - subtrahend.getHardScore(),
                this.softScore - subtrahend.getSoftScore());
    }

    public HardAndSoftLongScore multiply(double multiplicand) {
        return new DefaultHardAndSoftLongScore((long) Math.floor(hardScore * multiplicand),
                (long) Math.floor(this.softScore * multiplicand));
    }

    public HardAndSoftLongScore divide(double divisor) {
        return new DefaultHardAndSoftLongScore((long) Math.floor(hardScore / divisor),
                (long) Math.floor(this.softScore / divisor));
    }

    public double[] toDoubleLevels() {
        return new double[]{hardScore, softScore};
    }

    public boolean equals(Object o) {
        // A direct implementation (instead of EqualsBuilder) to avoid dependencies
        if (this == o) {
            return true;
        } else if (o instanceof HardAndSoftLongScore) {
            HardAndSoftLongScore other = (HardAndSoftLongScore) o;
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

    public int compareTo(HardAndSoftLongScore other) {
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
