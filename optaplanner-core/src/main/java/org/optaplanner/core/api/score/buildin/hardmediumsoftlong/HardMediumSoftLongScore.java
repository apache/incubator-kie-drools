/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.buildin.hardmediumsoftlong;

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.score.AbstractScore;
import org.optaplanner.core.api.score.FeasibilityScore;
import org.optaplanner.core.api.score.Score;

/**
 * This {@link Score} is based on 3 levels of long constraints: hard, medium and soft.
 * Hard constraints have priority over medium constraints.
 * Medium constraints have priority over soft constraints.
 * <p>
 * This class is immutable.
 * @see Score
 */
public final class HardMediumSoftLongScore extends AbstractScore<HardMediumSoftLongScore>
        implements FeasibilityScore<HardMediumSoftLongScore> {

    private static final String HARD_LABEL = "hard";
    private static final String MEDIUM_LABEL = "medium";
    private static final String SOFT_LABEL = "soft";

    public static HardMediumSoftLongScore parseScore(String scoreString) {
        String[] levelStrings = parseLevelStrings(HardMediumSoftLongScore.class, scoreString,
                HARD_LABEL, MEDIUM_LABEL, SOFT_LABEL);
        long hardScore = parseLevelAsLong(HardMediumSoftLongScore.class, scoreString, levelStrings[0]);
        long mediumScore = parseLevelAsLong(HardMediumSoftLongScore.class, scoreString, levelStrings[1]);
        long softScore = parseLevelAsLong(HardMediumSoftLongScore.class, scoreString, levelStrings[2]);
        return valueOf(hardScore, mediumScore, softScore);
    }

    public static HardMediumSoftLongScore valueOf(long hardScore, long mediumScore, long softScore) {
        return new HardMediumSoftLongScore(hardScore, mediumScore, softScore);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final long hardScore;
    private final long mediumScore;
    private final long softScore;

    /**
     * Private default constructor for default marshalling/unmarshalling of unknown frameworks that use reflection.
     * Such integration is always inferior to the specialized integration modules, such as
     * optaplanner-persistence-jpa, optaplanner-persistence-xstream, optaplanner-persistence-jaxb, ...
     */
    @SuppressWarnings("unused")
    private HardMediumSoftLongScore() {
        hardScore = Long.MIN_VALUE;
        mediumScore = Long.MIN_VALUE;
        softScore = Long.MIN_VALUE;
    }

    private HardMediumSoftLongScore(long hardScore, long mediumScore, long softScore) {
        this.hardScore = hardScore;
        this.mediumScore = mediumScore;
        this.softScore = softScore;
    }

    /**
     * The total of the broken negative hard constraints and fulfilled positive hard constraints.
     * Their weight is included in the total.
     * The hard score is usually a negative number because most use cases only have negative constraints.
     * @return higher is better, usually negative, 0 if no hard constraints are broken/fulfilled
     */
    public long getHardScore() {
        return hardScore;
    }

    /**
     * The total of the broken negative medium constraints and fulfilled positive medium constraints.
     * Their weight is included in the total.
     * The medium score is usually a negative number because most use cases only have negative constraints.
     * <p>
     * In a normal score comparison, the medium score is irrelevant if the 2 scores don't have the same hard score.
     * @return higher is better, usually negative, 0 if no hard constraints are broken/fulfilled
     */
    public long getMediumScore() {
        return mediumScore;
    }

    /**
     * The total of the broken negative soft constraints and fulfilled positive soft constraints.
     * Their weight is included in the total.
     * The soft score is usually a negative number because most use cases only have negative constraints.
     * <p>
     * In a normal score comparison, the soft score is irrelevant if the 2 scores don't have the same hard and medium score.
     * @return higher is better, usually negative, 0 if no soft constraints are broken/fulfilled
     */
    public long getSoftScore() {
        return softScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    /**
     * A {@link Solution} is feasible if it has no broken hard constraints.
     * @return true if the {@link #getHardScore()} is 0 or higher
     */
    public boolean isFeasible() {
        return getHardScore() >= 0L;
    }

    public HardMediumSoftLongScore add(HardMediumSoftLongScore augment) {
        return new HardMediumSoftLongScore(
                hardScore + augment.getHardScore(),
                mediumScore + augment.getMediumScore(),
                softScore + augment.getSoftScore());
    }

    public HardMediumSoftLongScore subtract(HardMediumSoftLongScore subtrahend) {
        return new HardMediumSoftLongScore(
                hardScore - subtrahend.getHardScore(),
                mediumScore - subtrahend.getMediumScore(),
                softScore - subtrahend.getSoftScore());
    }

    public HardMediumSoftLongScore multiply(double multiplicand) {
        return new HardMediumSoftLongScore(
                (long) Math.floor(hardScore * multiplicand),
                (long) Math.floor(mediumScore * multiplicand),
                (long) Math.floor(softScore * multiplicand));
    }

    public HardMediumSoftLongScore divide(double divisor) {
        return new HardMediumSoftLongScore(
                (long) Math.floor(hardScore / divisor),
                (long) Math.floor(mediumScore / divisor),
                (long) Math.floor(softScore / divisor));
    }

    public HardMediumSoftLongScore power(double exponent) {
        return new HardMediumSoftLongScore(
                (long) Math.floor(Math.pow(hardScore, exponent)),
                (long) Math.floor(Math.pow(mediumScore, exponent)),
                (long) Math.floor(Math.pow(softScore, exponent)));
    }

    public HardMediumSoftLongScore negate() {
        return new HardMediumSoftLongScore(-hardScore, -mediumScore, -softScore);
    }

    public Number[] toLevelNumbers() {
        return new Number[]{hardScore, mediumScore, softScore};
    }

    public boolean equals(Object o) {
        // A direct implementation (instead of EqualsBuilder) to avoid dependencies
        if (this == o) {
            return true;
        } else if (o instanceof HardMediumSoftLongScore) {
            HardMediumSoftLongScore other = (HardMediumSoftLongScore) o;
            return hardScore == other.getHardScore()
                    && mediumScore == other.getMediumScore()
                    && softScore == other.getSoftScore();
        } else {
            return false;
        }
    }

    public int hashCode() {
        // A direct implementation (instead of HashCodeBuilder) to avoid dependencies
        return ((((17 * 37)
                + Long.valueOf(hardScore).hashCode())) * 37
                + Long.valueOf(mediumScore).hashCode()) * 37
                + Long.valueOf(softScore).hashCode();
    }

    public int compareTo(HardMediumSoftLongScore other) {
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
