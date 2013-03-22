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

package org.optaplanner.core.api.score.buildin.hardsoftbigdecimal;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.optaplanner.core.api.score.AbstractScore;
import org.optaplanner.core.api.score.FeasibilityScore;
import org.optaplanner.core.api.score.Score;

/**
 * This {@link Score} is based on 2 levels of {@link BigDecimal} constraints: hard and soft.
 * Hard constraints have priority over soft constraints.
 * <p/>
 * This class is immutable.
 * @see Score
 */
public final class HardSoftBigDecimalScore extends AbstractScore<HardSoftBigDecimalScore>
        implements FeasibilityScore<HardSoftBigDecimalScore> {

    private static final String HARD_LABEL = "hard";
    private static final String SOFT_LABEL = "soft";

    public static HardSoftBigDecimalScore parseScore(String scoreString) {
        String[] levelStrings = parseLevelStrings(scoreString, HARD_LABEL, SOFT_LABEL);
        BigDecimal hardScore = new BigDecimal(levelStrings[0]);
        BigDecimal softScore = new BigDecimal(levelStrings[1]);
        return valueOf(hardScore, softScore);
    }

    public static HardSoftBigDecimalScore valueOf(BigDecimal hardScore, BigDecimal softScore) {
        return new HardSoftBigDecimalScore(hardScore, softScore);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final BigDecimal hardScore;
    private final BigDecimal softScore;

    private HardSoftBigDecimalScore(BigDecimal hardScore, BigDecimal softScore) {
        this.hardScore = hardScore;
        this.softScore = softScore;
    }

    /**
     * The total of the broken negative hard constraints and fulfilled positive hard constraints.
     * Their weight is included in the total.
     * The hard score is usually a negative number because most use cases only have negative constraints.
     * @return higher is better, usually negative, 0 if no hard constraints are broken/fulfilled
     */
    public BigDecimal getHardScore() {
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
    public BigDecimal getSoftScore() {
        return softScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public boolean isFeasible() {
        return getHardScore().compareTo(BigDecimal.ZERO) >= 0;
    }

    public HardSoftBigDecimalScore add(HardSoftBigDecimalScore augment) {
        return new HardSoftBigDecimalScore(hardScore.add(augment.getHardScore()),
                softScore.add(augment.getSoftScore()));
    }

    public HardSoftBigDecimalScore subtract(HardSoftBigDecimalScore subtrahend) {
        return new HardSoftBigDecimalScore(hardScore.subtract(subtrahend.getHardScore()),
                softScore.subtract(subtrahend.getSoftScore()));
    }

    public HardSoftBigDecimalScore multiply(double multiplicand) {
        // Intentionally not taken "new BigDecimal(multiplicand, MathContext.UNLIMITED)"
        // because together with the floor rounding it gives unwanted behaviour
        BigDecimal multiplicandBigDecimal = BigDecimal.valueOf(multiplicand);
        // The (unspecified) scale/precision of the multiplicand should have no impact on the returned scale/precision
        return new HardSoftBigDecimalScore(
                hardScore.multiply(multiplicandBigDecimal).setScale(hardScore.scale(), RoundingMode.FLOOR),
                softScore.multiply(multiplicandBigDecimal).setScale(softScore.scale(), RoundingMode.FLOOR));
    }

    public HardSoftBigDecimalScore divide(double divisor) {
        // Intentionally not taken "new BigDecimal(multiplicand, MathContext.UNLIMITED)"
        // because together with the floor rounding it gives unwanted behaviour
        BigDecimal divisorBigDecimal = BigDecimal.valueOf(divisor);
        // The (unspecified) scale/precision of the divisor should have no impact on the returned scale/precision
        return new HardSoftBigDecimalScore(
                hardScore.divide(divisorBigDecimal, hardScore.scale(), RoundingMode.FLOOR),
                softScore.divide(divisorBigDecimal, softScore.scale(), RoundingMode.FLOOR));
    }

    public double[] toDoubleLevels() {
        return new double[]{hardScore.doubleValue(), softScore.doubleValue()};
    }

    public boolean equals(Object o) {
        // A direct implementation (instead of EqualsBuilder) to avoid dependencies
        if (this == o) {
            return true;
        } else if (o instanceof HardSoftBigDecimalScore) {
            HardSoftBigDecimalScore other = (HardSoftBigDecimalScore) o;
            return hardScore.equals(other.getHardScore())
                    && softScore.equals(other.getSoftScore());
        } else {
            return false;
        }
    }

    public int hashCode() {
        // A direct implementation (instead of HashCodeBuilder) to avoid dependencies
        return (((17 * 37) + hardScore.hashCode())) * 37 + softScore.hashCode();
    }

    public int compareTo(HardSoftBigDecimalScore other) {
        // A direct implementation (instead of CompareToBuilder) to avoid dependencies
        if (hardScore.compareTo(other.getHardScore()) != 0) {
            if (hardScore.compareTo(other.getHardScore()) < 0) {
                return -1;
            } else {
                return 1;
            }
        } else {
            if (softScore.compareTo(other.getSoftScore()) < 0) {
                return -1;
            } else if (softScore.compareTo(other.getSoftScore()) > 0) {
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
