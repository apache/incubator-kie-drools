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

package org.optaplanner.core.api.score.buildin.hardsoftdouble;

import org.optaplanner.core.api.score.AbstractScore;
import org.optaplanner.core.api.score.FeasibilityScore;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;

/**
 * This {@link Score} is based on 2 levels of double constraints: hard and soft.
 * Hard constraints have priority over soft constraints.
 * <p>
 * WARNING: NOT RECOMMENDED TO USE DUE TO ROUNDING ERRORS THAT CAUSE SCORE CORRUPTION.
 * For example, this prints false: <code>System.out.println((0.01 + 0.05) == (0.01 + 0.02 + 0.03));</code>
 * Use {@link HardSoftBigDecimalScore} instead.
 * <p>
 * This class is immutable.
 * @see Score
 */
public final class HardSoftDoubleScore extends AbstractScore<HardSoftDoubleScore>
        implements FeasibilityScore<HardSoftDoubleScore> {

    private static final String HARD_LABEL = "hard";
    private static final String SOFT_LABEL = "soft";

    public static HardSoftDoubleScore parseScore(String scoreString) {
        String[] levelStrings = parseLevelStrings(HardSoftDoubleScore.class, scoreString, HARD_LABEL, SOFT_LABEL);
        double hardScore = parseLevelAsDouble(HardSoftDoubleScore.class, scoreString, levelStrings[0]);
        double softScore = parseLevelAsDouble(HardSoftDoubleScore.class, scoreString, levelStrings[1]);
        return valueOf(hardScore, softScore);
    }

    public static HardSoftDoubleScore valueOf(double hardScore, double softScore) {
        return new HardSoftDoubleScore(hardScore, softScore);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final double hardScore;
    private final double softScore;

    /**
     * Private default constructor for default marshalling/unmarshalling of unknown frameworks that use reflection.
     * Such integration is always inferior to the specialized integration modules, such as
     * optaplanner-persistence-jpa, optaplanner-persistence-xstream, optaplanner-persistence-jaxb, ...
     */
    @SuppressWarnings("unused")
    private HardSoftDoubleScore() {
        hardScore = Double.NaN;
        softScore = Double.NaN;
    }

    private HardSoftDoubleScore(double hardScore, double softScore) {
        this.hardScore = hardScore;
        this.softScore = softScore;
    }

    /**
     * The total of the broken negative hard constraints and fulfilled positive hard constraints.
     * Their weight is included in the total.
     * The hard score is usually a negative number because most use cases only have negative constraints.
     * @return higher is better, usually negative, 0 if no hard constraints are broken/fulfilled
     */
    public double getHardScore() {
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
    public double getSoftScore() {
        return softScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isFeasible() {
        return getHardScore() >= 0.0;
    }

    public HardSoftDoubleScore add(HardSoftDoubleScore augment) {
        return new HardSoftDoubleScore(hardScore + augment.getHardScore(),
                softScore + augment.getSoftScore());
    }

    public HardSoftDoubleScore subtract(HardSoftDoubleScore subtrahend) {
        return new HardSoftDoubleScore(hardScore - subtrahend.getHardScore(),
                softScore - subtrahend.getSoftScore());
    }

    public HardSoftDoubleScore multiply(double multiplicand) {
        return new HardSoftDoubleScore(hardScore * multiplicand,
                softScore * multiplicand);
    }

    public HardSoftDoubleScore divide(double divisor) {
        return new HardSoftDoubleScore(hardScore / divisor,
                softScore / divisor);
    }

    public HardSoftDoubleScore power(double exponent) {
        return new HardSoftDoubleScore(Math.pow(hardScore, exponent),
                Math.pow(softScore, exponent));
    }

    public HardSoftDoubleScore negate() {
        return new HardSoftDoubleScore(-hardScore, -softScore);
    }

    public Number[] toLevelNumbers() {
        return new Number[]{hardScore, softScore};
    }

    public boolean equals(Object o) {
        // A direct implementation (instead of EqualsBuilder) to avoid dependencies
        if (this == o) {
            return true;
        } else if (o instanceof HardSoftDoubleScore) {
            HardSoftDoubleScore other = (HardSoftDoubleScore) o;
            return hardScore == other.getHardScore()
                    && softScore == other.getSoftScore();
        } else {
            return false;
        }
    }

    public int hashCode() {
        // A direct implementation (instead of HashCodeBuilder) to avoid dependencies
        return (((17 * 37) + Double.valueOf(hardScore).hashCode())) * 37 + Double.valueOf(softScore).hashCode();
    }

    public int compareTo(HardSoftDoubleScore other) {
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
