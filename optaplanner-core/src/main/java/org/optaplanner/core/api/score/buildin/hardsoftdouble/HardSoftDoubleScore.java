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

package org.optaplanner.core.api.score.buildin.hardsoftdouble;

import org.optaplanner.core.api.score.AbstractScore;
import org.optaplanner.core.api.score.FeasibilityScore;
import org.optaplanner.core.api.score.Score;

/**
 * This {@link Score} is based on 2 levels of double constraints: hard and soft.
 * Hard constraints have priority over soft constraints.
 * <p/>
 * This class is immutable.
 * @see Score
 */
public final class HardSoftDoubleScore extends AbstractScore<HardSoftDoubleScore>
        implements FeasibilityScore<HardSoftDoubleScore> {

    private static final String HARD_LABEL = "hard";
    private static final String SOFT_LABEL = "soft";

    public static HardSoftDoubleScore parseScore(String scoreString) {
        String[] levelStrings = parseLevelStrings(scoreString, HARD_LABEL, SOFT_LABEL);
        double hardScore = Double.parseDouble(levelStrings[0]);
        double softScore = Double.parseDouble(levelStrings[1]);
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
     * <p/>
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

    public double[] toDoubleLevels() {
        return new double[]{hardScore, softScore};
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
