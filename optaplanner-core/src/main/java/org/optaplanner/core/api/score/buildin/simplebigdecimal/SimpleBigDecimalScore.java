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

package org.optaplanner.core.api.score.buildin.simplebigdecimal;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.optaplanner.core.api.score.AbstractScore;
import org.optaplanner.core.api.score.Score;

/**
 * This {@link Score} is based on 1 level of {@link BigDecimal} constraints.
 * <p/>
 * This class is immutable.
 * @see Score
 */
public final class SimpleBigDecimalScore extends AbstractScore<SimpleBigDecimalScore> {

    public static SimpleBigDecimalScore parseScore(String scoreString) {
        return valueOf(new BigDecimal(scoreString));
    }

    public static SimpleBigDecimalScore valueOf(BigDecimal score) {
        return new SimpleBigDecimalScore(score);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final BigDecimal score;

    private SimpleBigDecimalScore(BigDecimal score) {
        this.score = score;
    }

    /**
     * The total of the broken negative constraints and fulfilled positive hard constraints.
     * Their weight is included in the total.
     * The score is usually a negative number because most use cases only have negative constraints.
     * @return higher is better, usually negative, 0 if no constraints are broken/fulfilled
     */
    public BigDecimal getScore() {
        return score;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public SimpleBigDecimalScore add(SimpleBigDecimalScore augment) {
        return new SimpleBigDecimalScore(score.add(augment.getScore()));
    }

    public SimpleBigDecimalScore subtract(SimpleBigDecimalScore subtrahend) {
        return new SimpleBigDecimalScore(score.subtract(subtrahend.getScore()));
    }

    public SimpleBigDecimalScore multiply(double multiplicand) {
        // Intentionally not taken "new BigDecimal(multiplicand, MathContext.UNLIMITED)"
        // because together with the floor rounding it gives unwanted behaviour
        BigDecimal multiplicandBigDecimal = BigDecimal.valueOf(multiplicand);
        // The (unspecified) scale/precision of the multiplicand should have no impact on the returned scale/precision
        return new SimpleBigDecimalScore(
                score.multiply(multiplicandBigDecimal).setScale(score.scale(), RoundingMode.FLOOR));
    }

    public SimpleBigDecimalScore divide(double divisor) {
        // Intentionally not taken "new BigDecimal(multiplicand, MathContext.UNLIMITED)"
        // because together with the floor rounding it gives unwanted behaviour
        BigDecimal divisorBigDecimal = BigDecimal.valueOf(divisor);
        // The (unspecified) scale/precision of the divisor should have no impact on the returned scale/precision
        return new SimpleBigDecimalScore(
                score.divide(divisorBigDecimal, score.scale(), RoundingMode.FLOOR));
    }

    public double[] toDoubleLevels() {
        return new double[]{score.doubleValue()};
    }

    public boolean equals(Object o) {
        // A direct implementation (instead of EqualsBuilder) to avoid dependencies
        if (this == o) {
            return true;
        } else if (o instanceof SimpleBigDecimalScore) {
            SimpleBigDecimalScore other = (SimpleBigDecimalScore) o;
            return score.equals(other.getScore());
        } else {
            return false;
        }
    }

    public int hashCode() {
        // A direct implementation (instead of HashCodeBuilder) to avoid dependencies
        return (17 * 37) + score.hashCode();
    }

    public int compareTo(SimpleBigDecimalScore other) {
        // A direct implementation (instead of CompareToBuilder) to avoid dependencies
        if (score.compareTo(other.getScore()) < 0) {
            return -1;
        } else if (score.compareTo(other.getScore()) > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    public String toString() {
        return score.toString();
    }

}
