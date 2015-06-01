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

package org.optaplanner.core.api.score.buildin.hardmediumsoft;

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.score.AbstractScore;
import org.optaplanner.core.api.score.FeasibilityScore;
import org.optaplanner.core.api.score.Score;

/**
 * This {@link Score} is based on 3 levels of int constraints: hard, medium and soft.
 * Hard constraints have priority over medium constraints.
 * Medium constraints have priority over soft constraints.
 * <p/>
 * This class is immutable.
 * @see Score
 */
public final class HardMediumSoftScore extends AbstractScore<HardMediumSoftScore>
        implements FeasibilityScore<HardMediumSoftScore> {

    private static final String HARD_LABEL = "hard";
    private static final String MEDIUM_LABEL = "medium";
    private static final String SOFT_LABEL = "soft";

    public static HardMediumSoftScore parseScore(String scoreString) {
        String[] levelStrings = parseLevelStrings(HardMediumSoftScore.class, scoreString,
                HARD_LABEL, MEDIUM_LABEL, SOFT_LABEL);
        int hardScore = parseLevelAsInt(HardMediumSoftScore.class, scoreString, levelStrings[0]);
        int mediumScore = parseLevelAsInt(HardMediumSoftScore.class, scoreString, levelStrings[1]);
        int softScore = parseLevelAsInt(HardMediumSoftScore.class, scoreString, levelStrings[2]);
        return valueOf(hardScore, mediumScore, softScore);
    }

    public static HardMediumSoftScore valueOf(int hardScore, int mediumScore, int softScore) {
        return new HardMediumSoftScore(hardScore, mediumScore, softScore);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final int hardScore;
    private final int mediumScore;
    private final int softScore;

    private HardMediumSoftScore(int hardScore, int mediumScore, int softScore) {
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
    public int getHardScore() {
        return hardScore;
    }

    /**
     * The total of the broken negative medium constraints and fulfilled positive medium constraints.
     * Their weight is included in the total.
     * The medium score is usually a negative number because most use cases only have negative constraints.
     * <p/>
     * In a normal score comparison, the medium score is irrelevant if the 2 scores don't have the same hard score.
     * @return higher is better, usually negative, 0 if no hard constraints are broken/fulfilled
     */
    public int getMediumScore() {
        return mediumScore;
    }

    /**
     * The total of the broken negative soft constraints and fulfilled positive soft constraints.
     * Their weight is included in the total.
     * The soft score is usually a negative number because most use cases only have negative constraints.
     * <p/>
     * In a normal score comparison, the soft score is irrelevant if the 2 scores don't have the same hard and medium score.
     * @return higher is better, usually negative, 0 if no soft constraints are broken/fulfilled
     */
    public int getSoftScore() {
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
        return getHardScore() >= 0;
    }

    public HardMediumSoftScore add(HardMediumSoftScore augment) {
        return new HardMediumSoftScore(
                hardScore + augment.getHardScore(),
                mediumScore + augment.getMediumScore(),
                softScore + augment.getSoftScore());
    }

    public HardMediumSoftScore subtract(HardMediumSoftScore subtrahend) {
        return new HardMediumSoftScore(
                hardScore - subtrahend.getHardScore(),
                mediumScore - subtrahend.getMediumScore(),
                softScore - subtrahend.getSoftScore());
    }

    public HardMediumSoftScore multiply(double multiplicand) {
        return new HardMediumSoftScore(
                (int) Math.floor(hardScore * multiplicand),
                (int) Math.floor(mediumScore * multiplicand),
                (int) Math.floor(softScore * multiplicand));
    }

    public HardMediumSoftScore divide(double divisor) {
        return new HardMediumSoftScore(
                (int) Math.floor(hardScore / divisor),
                (int) Math.floor(mediumScore / divisor),
                (int) Math.floor(softScore / divisor));
    }

    public HardMediumSoftScore power(double exponent) {
        return new HardMediumSoftScore(
                (int) Math.floor(Math.pow(hardScore, exponent)),
                (int) Math.floor(Math.pow(mediumScore, exponent)),
                (int) Math.floor(Math.pow(softScore, exponent)));
    }

    public HardMediumSoftScore negate() {
        return new HardMediumSoftScore(-hardScore, -mediumScore, -softScore);
    }

    public Number[] toLevelNumbers() {
        return new Number[]{hardScore, mediumScore, softScore};
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
        return ((((17 * 37)
                + hardScore)) * 37
                + mediumScore) * 37
                + softScore;
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
