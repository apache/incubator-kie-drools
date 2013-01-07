/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.core.score.buildin.hardmediumsoft;

import org.drools.planner.core.score.AbstractScore;
import org.drools.planner.core.score.buildin.hardandsoft.HardAndSoftScore;

/**
 * Default implementation of {@link HardAndSoftScore}.
 * <p/>
 * This class is immutable.
 * @see HardAndSoftScore
 */
public final class DefaultHardMediumSoftScore extends AbstractScore<HardMediumSoftScore>
        implements HardMediumSoftScore {

    private static final String HARD_LABEL = "hard";
    private static final String MEDIUM_LABEL = "medium";
    private static final String SOFT_LABEL = "soft";

    public static DefaultHardMediumSoftScore parseScore(String scoreString) {
        String[] scoreTokens = scoreString.split("\\/");
        if (scoreTokens.length != 3
                || !scoreTokens[0].endsWith(HARD_LABEL)
                || !scoreTokens[1].endsWith(MEDIUM_LABEL)
                || !scoreTokens[2].endsWith(SOFT_LABEL)) {
            throw new IllegalArgumentException("The scoreString (" + scoreString
                    + ") doesn't follow the 999hard/999medium/999soft pattern.");
        }
        int hardScore = Integer.parseInt(scoreTokens[0].substring(0, scoreTokens[0].length() - HARD_LABEL.length()));
        int mediumScore = Integer.parseInt(scoreTokens[1].substring(0, scoreTokens[1].length() - MEDIUM_LABEL.length()));
        int softScore = Integer.parseInt(scoreTokens[2].substring(0, scoreTokens[2].length() - SOFT_LABEL.length()));
        return valueOf(hardScore, mediumScore, softScore);
    }

    public static DefaultHardMediumSoftScore valueOf(int hardScore, int mediumScore, int softScore) {
        return new DefaultHardMediumSoftScore(hardScore, mediumScore, softScore);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final int hardScore;
    private final int mediumScore;
    private final int softScore;

    protected DefaultHardMediumSoftScore(int hardScore, int mediumScore, int softScore) {
        this.hardScore = hardScore;
        this.mediumScore = mediumScore;
        this.softScore = softScore;
    }

    public int getHardScore() {
        return hardScore;
    }

    public int getMediumScore() {
        return mediumScore;
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

    public HardMediumSoftScore add(HardMediumSoftScore augment) {
        return new DefaultHardMediumSoftScore(
                hardScore + augment.getHardScore(),
                mediumScore + augment.getMediumScore(),
                softScore + augment.getSoftScore());
    }

    public HardMediumSoftScore subtract(HardMediumSoftScore subtrahend) {
        return new DefaultHardMediumSoftScore(
                hardScore - subtrahend.getHardScore(),
                mediumScore - subtrahend.getMediumScore(),
                softScore - subtrahend.getSoftScore());
    }

    public HardMediumSoftScore multiply(double multiplicand) {
        return new DefaultHardMediumSoftScore(
                (int) Math.floor(hardScore * multiplicand),
                (int) Math.floor(mediumScore * multiplicand),
                (int) Math.floor(softScore * multiplicand));
    }

    public HardMediumSoftScore divide(double divisor) {
        return new DefaultHardMediumSoftScore(
                (int) Math.floor(hardScore / divisor),
                (int) Math.floor(mediumScore / divisor),
                (int) Math.floor(softScore / divisor));
    }

    public double[] toDoubleLevels() {
        return new double[]{hardScore, mediumScore, softScore};
    }

    public boolean equals(Object o) {
        // A direct implementation (instead of EqualsBuilder) to avoid dependencies
        if (this == o) {
            return true;
        } else if (o instanceof HardMediumSoftScore) {
            HardMediumSoftScore other = (HardMediumSoftScore) o;
            return hardScore == other.getHardScore()
                    && mediumScore == other.getMediumScore()
                    && softScore == other.getSoftScore();
        } else {
            return false;
        }
    }

    public int hashCode() {
        // A direct implementation (instead of HashCodeBuilder) to avoid dependencies
        return ((((17 * 37) + hardScore)) * 37 + mediumScore) * 37 + softScore;
    }

    public int compareTo(HardMediumSoftScore other) {
        // A direct implementation (instead of CompareToBuilder) to avoid dependencies
        if (hardScore != other.getHardScore()) {
            if (hardScore < other.getHardScore()) {
                return -1;
            } else {
                return 1;
            }
        } else {
            if (mediumScore != other.getMediumScore()) {
                if (mediumScore < other.getMediumScore()) {
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
    }

    public String toString() {
        return hardScore + HARD_LABEL + "/" + mediumScore + MEDIUM_LABEL + "/" + softScore + SOFT_LABEL;
    }

}
