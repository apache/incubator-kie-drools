/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.buildin.hardsoftlong;

import org.optaplanner.core.api.score.AbstractScore;
import org.optaplanner.core.api.score.FeasibilityScore;
import org.optaplanner.core.api.score.Score;

/**
 * This {@link Score} is based on 2 levels of long constraints: hard and soft.
 * Hard constraints have priority over soft constraints.
 * <p>
 * This class is immutable.
 * @see Score
 */
public final class HardSoftLongScore extends AbstractScore<HardSoftLongScore>
        implements FeasibilityScore<HardSoftLongScore> {

    private static final String HARD_LABEL = "hard";
    private static final String SOFT_LABEL = "soft";

    public static HardSoftLongScore parseScore(String scoreString) {
        String[] levelStrings = parseLevelStrings(HardSoftLongScore.class, scoreString, HARD_LABEL, SOFT_LABEL);
        long hardScore = parseLevelAsLong(HardSoftLongScore.class, scoreString, levelStrings[0]);
        long softScore = parseLevelAsLong(HardSoftLongScore.class, scoreString, levelStrings[1]);
        return valueOf(hardScore, softScore);
    }

    public static HardSoftLongScore valueOf(long hardScore, long softScore) {
        return new HardSoftLongScore(hardScore, softScore);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final long hardScore;
    private final long softScore;

    /**
     * Private default constructor for default marshalling/unmarshalling of unknown frameworks that use reflection.
     * Such integration is always inferior to the specialized integration modules, such as
     * optaplanner-persistence-jpa, optaplanner-persistence-xstream, optaplanner-persistence-jaxb, ...
     */
    @SuppressWarnings("unused")
    private HardSoftLongScore() {
        hardScore = Long.MIN_VALUE;
        softScore = Long.MIN_VALUE;
    }

    private HardSoftLongScore(long hardScore, long softScore) {
        this.hardScore = hardScore;
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
     * The total of the broken negative soft constraints and fulfilled positive soft constraints.
     * Their weight is included in the total.
     * The soft score is usually a negative number because most use cases only have negative constraints.
     * <p>
     * In a normal score comparison, the soft score is irrelevant if the 2 scores don't have the same hard score.
     * @return higher is better, usually negative, 0 if no soft constraints are broken/fulfilled
     */
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
        return new HardSoftLongScore(hardScore + augment.getHardScore(),
                softScore + augment.getSoftScore());
    }

    public HardSoftLongScore subtract(HardSoftLongScore subtrahend) {
        return new HardSoftLongScore(hardScore - subtrahend.getHardScore(),
                softScore - subtrahend.getSoftScore());
    }

    public HardSoftLongScore multiply(double multiplicand) {
        return new HardSoftLongScore((long) Math.floor(hardScore * multiplicand),
                (long) Math.floor(softScore * multiplicand));
    }

    public HardSoftLongScore divide(double divisor) {
        return new HardSoftLongScore((long) Math.floor(hardScore / divisor),
                (long) Math.floor(softScore / divisor));
    }

    public HardSoftLongScore power(double exponent) {
        return new HardSoftLongScore((long) Math.floor(Math.pow(hardScore, exponent)),
                (long) Math.floor(Math.pow(softScore, exponent)));
    }

    public HardSoftLongScore negate() {
        return new HardSoftLongScore(-hardScore, -softScore);
    }

    public Number[] toLevelNumbers() {
        return new Number[]{hardScore, softScore};
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
