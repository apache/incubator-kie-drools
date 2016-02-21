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

package org.optaplanner.core.api.score.buildin.bendablebigdecimal;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.optaplanner.core.api.score.AbstractScore;
import org.optaplanner.core.api.score.FeasibilityScore;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.buildin.bendable.BendableScoreDefinition;

/**
 * This {@link Score} is based on n levels of {@link BigDecimal} constraints.
 * The number of levels is bendable at configuration time.
 * <p>
 * This class is immutable.
 * <p>
 * The {@link #getHardLevelsSize()} and {@link #getSoftLevelsSize()} must be the same as in the
 * {@link BendableScoreDefinition} used.
 * @see Score
 */
public final class BendableBigDecimalScore extends AbstractScore<BendableBigDecimalScore>
        implements FeasibilityScore<BendableBigDecimalScore> {

    public static BendableBigDecimalScore parseScore(int hardLevelsSize, int softLevelsSize, String scoreString) {
        int levelsSize = hardLevelsSize + softLevelsSize;
        String[] levelStrings = parseLevelStrings(BendableBigDecimalScore.class, scoreString, levelsSize);
        BigDecimal[] hardScores = new BigDecimal[hardLevelsSize];
        BigDecimal[] softScores = new BigDecimal[softLevelsSize];
        for (int i = 0; i < hardScores.length; i++) {
            hardScores[i] = parseLevelAsBigDecimal(BendableBigDecimalScore.class, scoreString, levelStrings[i]);
        }
        for (int i = 0; i < softScores.length; i++) {
            softScores[i] = parseLevelAsBigDecimal(BendableBigDecimalScore.class, scoreString, levelStrings[hardScores.length + i]);
        }
        return valueOf(hardScores, softScores);
    }

    /**
     * Creates a new {@link BendableBigDecimalScore}.
     * @param hardScores never null, never change that array afterwards: it must be immutable
     * @param softScores never null, never change that array afterwards: it must be immutable
     * @return never null
     */
    public static BendableBigDecimalScore valueOf(BigDecimal[] hardScores, BigDecimal[] softScores) {
        return new BendableBigDecimalScore(hardScores, softScores);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final BigDecimal[] hardScores;
    private final BigDecimal[] softScores;

    /**
     * Private default constructor for default marshalling/unmarshalling of unknown frameworks that use reflection.
     * Such integration is always inferior to the specialized integration modules, such as
     * optaplanner-persistence-jpa, optaplanner-persistence-xstream, optaplanner-persistence-jaxb, ...
     */
    @SuppressWarnings("unused")
    private BendableBigDecimalScore() {
        hardScores = null;
        softScores = null;
    }

    protected BendableBigDecimalScore(BigDecimal[] hardScores, BigDecimal[] softScores) {
        this.hardScores = hardScores;
        this.softScores = softScores;
    }

    // Intentionally no getters for the hardScores or softScores int arrays to guarantee that this class is immutable

    public int getHardLevelsSize() {
        return hardScores.length;
    }

    /**
     * @param index {@code 0 <= index <} {@link #getHardLevelsSize()}
     * @return higher is better
     */
    public BigDecimal getHardScore(int index) {
        return hardScores[index];
    }

    public int getSoftLevelsSize() {
        return softScores.length;
    }

    /**
     * @param index {@code 0 <= index <} {@link #getSoftLevelsSize()}
     * @return higher is better
     */
    public BigDecimal getSoftScore(int index) {
        return softScores[index];
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    /**
     * @return {@link #getHardLevelsSize()} + {@link #getSoftLevelsSize()}
     */
    public int getLevelsSize() {
        return hardScores.length + softScores.length;
    }

    /**
     * @param index {@code 0 <= index <} {@link #getLevelsSize()}
     * @return higher is better
     */
    public BigDecimal getHardOrSoftScore(int index) {
        if (index < hardScores.length) {
            return hardScores[index];
        } else {
            return softScores[index - hardScores.length];
        }
    }

    public boolean isFeasible() {
        for (BigDecimal hardScore : hardScores) {
            int comparison = hardScore.compareTo(BigDecimal.ZERO);
            if (comparison > 0) {
                return true;
            } else if (comparison < 0) {
                return false;
            }
        }
        return true;
    }

    public BendableBigDecimalScore add(BendableBigDecimalScore augment) {
        validateCompatible(augment);
        BigDecimal[] newHardScores = new BigDecimal[hardScores.length];
        BigDecimal[] newSoftScores = new BigDecimal[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = hardScores[i].add(augment.getHardScore(i));
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = softScores[i].add(augment.getSoftScore(i));
        }
        return new BendableBigDecimalScore(newHardScores, newSoftScores);
    }

    public BendableBigDecimalScore subtract(BendableBigDecimalScore subtrahend) {
        validateCompatible(subtrahend);
        BigDecimal[] newHardScores = new BigDecimal[hardScores.length];
        BigDecimal[] newSoftScores = new BigDecimal[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = hardScores[i].subtract(subtrahend.getHardScore(i));
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = softScores[i].subtract(subtrahend.getSoftScore(i));
        }
        return new BendableBigDecimalScore(newHardScores, newSoftScores);
    }

    public BendableBigDecimalScore multiply(double multiplicand) {
        BigDecimal[] newHardScores = new BigDecimal[hardScores.length];
        BigDecimal[] newSoftScores = new BigDecimal[softScores.length];
        BigDecimal bigDecimalMultiplicand = BigDecimal.valueOf(multiplicand);
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = hardScores[i].multiply(bigDecimalMultiplicand);
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = softScores[i].multiply(bigDecimalMultiplicand);
        }
        return new BendableBigDecimalScore(newHardScores, newSoftScores);
    }

    public BendableBigDecimalScore divide(double divisor) {
        BigDecimal[] newHardScores = new BigDecimal[hardScores.length];
        BigDecimal[] newSoftScores = new BigDecimal[softScores.length];
        BigDecimal bigDecimalDivisor = BigDecimal.valueOf(divisor);
        for (int i = 0; i < newHardScores.length; i++) {
            BigDecimal hardScore = hardScores[i];
            newHardScores[i] = hardScore.divide(bigDecimalDivisor, hardScore.scale(), RoundingMode.FLOOR);
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            BigDecimal softScore = softScores[i];
            newSoftScores[i] = softScore.divide(bigDecimalDivisor, softScore.scale(), RoundingMode.FLOOR);
        }
        return new BendableBigDecimalScore(newHardScores, newSoftScores);
    }

    public BendableBigDecimalScore power(double exponent) {
        BigDecimal[] newHardScores = new BigDecimal[hardScores.length];
        BigDecimal[] newSoftScores = new BigDecimal[softScores.length];
        BigDecimal actualExponent = BigDecimal.valueOf(exponent);
        // The (unspecified) scale/precision of the exponent should have no impact on the returned scale/precision
        // TODO FIXME remove .intValue() so non-integer exponents produce correct results
        // None of the normal Java libraries support BigDecimal.pow(BigDecimal)
        for (int i = 0; i < newHardScores.length; i++) {
            BigDecimal hardScore = hardScores[i];
            newHardScores[i] = hardScore.pow(actualExponent.intValue()).setScale(hardScore.scale());
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            BigDecimal softScore = softScores[i];
            newSoftScores[i] = softScore.pow(actualExponent.intValue()).setScale(softScore.scale());
        }
        return new BendableBigDecimalScore(newHardScores, newSoftScores);
    }

    public BendableBigDecimalScore negate() {
        BigDecimal[] newHardScores = new BigDecimal[hardScores.length];
        BigDecimal[] newSoftScores = new BigDecimal[softScores.length];
        for (int i = 0; i < newHardScores.length; i++) {
            newHardScores[i] = hardScores[i].negate();
        }
        for (int i = 0; i < newSoftScores.length; i++) {
            newSoftScores[i] = softScores[i].negate();
        }
        return new BendableBigDecimalScore(newHardScores, newSoftScores);
    }

    public Number[] toLevelNumbers() {
        Number[] levelNumbers = new Number[hardScores.length + softScores.length];
        for (int i = 0; i < hardScores.length; i++) {
            levelNumbers[i] = hardScores[i];
        }
        for (int i = 0; i < softScores.length; i++) {
            levelNumbers[hardScores.length + i] = softScores[i];
        }
        return levelNumbers;
    }

    public boolean equals(Object o) {
        // A direct implementation (instead of EqualsBuilder) to avoid dependencies
        if (this == o) {
            return true;
        } else if (o instanceof BendableBigDecimalScore) {
            BendableBigDecimalScore other = (BendableBigDecimalScore) o;
            if (getHardLevelsSize() != other.getHardLevelsSize()
                    || getSoftLevelsSize() != other.getSoftLevelsSize()) {
                return false;
            }
            for (int i = 0; i < hardScores.length; i++) {
                if (!hardScores[i].equals(other.getHardScore(i))) {
                    return false;
                }
            }
            for (int i = 0; i < softScores.length; i++) {
                if (!softScores[i].equals(other.getSoftScore(i))) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public int hashCode() {
        // A direct implementation (instead of HashCodeBuilder) to avoid dependencies
        int hashCode = 17;
        for (BigDecimal hardScore : hardScores) {
            hashCode = hashCode * 37 + hardScore.hashCode();
        }
        for (BigDecimal softScore : softScores) {
            hashCode = hashCode * 37 + softScore.hashCode();
        }
        return hashCode;
    }

    public int compareTo(BendableBigDecimalScore other) {
        // A direct implementation (instead of CompareToBuilder) to avoid dependencies
        validateCompatible(other);
        for (int i = 0; i < hardScores.length; i++) {
            if (!hardScores[i].equals(other.getHardScore(i))) {
                if (hardScores[i].compareTo(other.getHardScore(i)) < 0) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
        for (int i = 0; i < softScores.length; i++) {
            if (!softScores[i].equals(other.getSoftScore(i))) {
                if (softScores[i].compareTo(other.getSoftScore(i)) < 0) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }
        return 0;
    }

    public String toString() {
        StringBuilder s = new StringBuilder(((hardScores.length + softScores.length) * 4) + 1);
        boolean first = true;
        for (BigDecimal hardScore : hardScores) {
            if (first) {
                first = false;
            } else {
                s.append("/");
            }
            s.append(hardScore);
        }
        for (BigDecimal softScore : softScores) {
            if (first) {
                first = false;
            } else {
                s.append("/");
            }
            s.append(softScore);
        }
        return s.toString();
    }

    public void validateCompatible(BendableBigDecimalScore other) {
        if (getHardLevelsSize() != other.getHardLevelsSize()) {
            throw new IllegalArgumentException("The score (" + this
                    + ") with hardScoreSize (" + getHardLevelsSize()
                    + ") is not compatible with the other score (" + other
                    + ") with hardScoreSize (" + other.getHardLevelsSize() + ").");
        }
        if (getSoftLevelsSize() != other.getSoftLevelsSize()) {
            throw new IllegalArgumentException("The score (" + this
                    + ") with softScoreSize (" + getSoftLevelsSize()
                    + ") is not compatible with the other score (" + other
                    + ") with softScoreSize (" + other.getSoftLevelsSize() + ").");
        }
    }

    @Override
    public boolean isCompatibleArithmeticArgument(Score otherScore) {
        if (!(otherScore instanceof BendableBigDecimalScore)) {
            return false;
        }
        BendableBigDecimalScore otherBendableScore = (BendableBigDecimalScore) otherScore;
        return hardScores.length == otherBendableScore.hardScores.length
                && softScores.length == otherBendableScore.softScores.length;
    }

}
