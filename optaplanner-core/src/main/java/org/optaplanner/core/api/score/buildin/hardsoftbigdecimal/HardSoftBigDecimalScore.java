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

package org.optaplanner.core.api.score.buildin.hardsoftbigdecimal;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.optaplanner.core.api.score.AbstractScore;
import org.optaplanner.core.api.score.FeasibilityScore;
import org.optaplanner.core.api.score.Score;

/**
 * This {@link Score} is based on 2 levels of {@link BigDecimal} constraints: hard and soft.
 * Hard constraints have priority over soft constraints.
 * Hard constraints determine feasibility.
 * <p>
 * This class is immutable.
 * @see Score
 */
public final class HardSoftBigDecimalScore extends AbstractScore<HardSoftBigDecimalScore>
        implements FeasibilityScore<HardSoftBigDecimalScore> {

    public static final HardSoftBigDecimalScore ZERO = new HardSoftBigDecimalScore(0, BigDecimal.ZERO, BigDecimal.ZERO);
    public static final HardSoftBigDecimalScore ONE_HARD = new HardSoftBigDecimalScore(0, BigDecimal.ONE, BigDecimal.ZERO);
    public static final HardSoftBigDecimalScore ONE_SOFT = new HardSoftBigDecimalScore(0, BigDecimal.ZERO, BigDecimal.ONE);
    private static final String HARD_LABEL = "hard";
    private static final String SOFT_LABEL = "soft";

    public static HardSoftBigDecimalScore parseScore(String scoreString) {
        String[] scoreTokens = parseScoreTokens(HardSoftBigDecimalScore.class, scoreString, HARD_LABEL, SOFT_LABEL);
        int initScore = parseInitScore(HardSoftBigDecimalScore.class, scoreString, scoreTokens[0]);
        BigDecimal hardScore = parseLevelAsBigDecimal(HardSoftBigDecimalScore.class, scoreString, scoreTokens[1]);
        BigDecimal softScore = parseLevelAsBigDecimal(HardSoftBigDecimalScore.class, scoreString, scoreTokens[2]);
        return ofUninitialized(initScore, hardScore, softScore);
    }

    public static HardSoftBigDecimalScore ofUninitialized(int initScore, BigDecimal hardScore, BigDecimal softScore) {
        return new HardSoftBigDecimalScore(initScore, hardScore, softScore);
    }

    /**
     * @deprecated in favor of {@link #ofUninitialized(int, BigDecimal, BigDecimal)}
     */
    @Deprecated
    public static HardSoftBigDecimalScore valueOfUninitialized(int initScore, BigDecimal hardScore, BigDecimal softScore) {
        return new HardSoftBigDecimalScore(initScore, hardScore, softScore);
    }

    public static HardSoftBigDecimalScore of(BigDecimal hardScore, BigDecimal softScore) {
        return new HardSoftBigDecimalScore(0, hardScore, softScore);
    }

    /**
     * @deprecated in favor of {@link #of(BigDecimal, BigDecimal)}
     */
    @Deprecated
    public static HardSoftBigDecimalScore valueOf(BigDecimal hardScore, BigDecimal softScore) {
        return new HardSoftBigDecimalScore(0, hardScore, softScore);
    }

    public static HardSoftBigDecimalScore ofHard(BigDecimal hardScore) {
        return new HardSoftBigDecimalScore(0, hardScore, BigDecimal.ZERO);
    }

    public static HardSoftBigDecimalScore ofSoft(BigDecimal softScore) {
        return new HardSoftBigDecimalScore(0, BigDecimal.ZERO, softScore);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final BigDecimal hardScore;
    private final BigDecimal softScore;

    /**
     * Private default constructor for default marshalling/unmarshalling of unknown frameworks that use reflection.
     * Such integration is always inferior to the specialized integration modules, such as
     * optaplanner-persistence-jpa, optaplanner-persistence-xstream, optaplanner-persistence-jaxb, ...
     */
    @SuppressWarnings("unused")
    private HardSoftBigDecimalScore() {
        super(Integer.MIN_VALUE);
        hardScore = null;
        softScore = null;
    }

    private HardSoftBigDecimalScore(int initScore, BigDecimal hardScore, BigDecimal softScore) {
        super(initScore);
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
     * <p>
     * In a normal score comparison, the soft score is irrelevant if the 2 scores don't have the same hard score.
     * @return higher is better, usually negative, 0 if no soft constraints are broken/fulfilled
     */
    public BigDecimal getSoftScore() {
        return softScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public HardSoftBigDecimalScore toInitializedScore() {
        return initScore == 0 ? this : new HardSoftBigDecimalScore(0, hardScore, softScore);
    }

    @Override
    public HardSoftBigDecimalScore withInitScore(int newInitScore) {
        assertNoInitScore();
        return new HardSoftBigDecimalScore(newInitScore, hardScore, softScore);
    }

    @Override
    public boolean isFeasible() {
        return initScore >= 0 && hardScore.compareTo(BigDecimal.ZERO) >= 0;
    }

    @Override
    public HardSoftBigDecimalScore add(HardSoftBigDecimalScore addend) {
        return new HardSoftBigDecimalScore(
                initScore + addend.getInitScore(),
                hardScore.add(addend.getHardScore()),
                softScore.add(addend.getSoftScore()));
    }

    @Override
    public HardSoftBigDecimalScore subtract(HardSoftBigDecimalScore subtrahend) {
        return new HardSoftBigDecimalScore(
                initScore - subtrahend.getInitScore(),
                hardScore.subtract(subtrahend.getHardScore()),
                softScore.subtract(subtrahend.getSoftScore()));
    }

    @Override
    public HardSoftBigDecimalScore multiply(double multiplicand) {
        // Intentionally not taken "new BigDecimal(multiplicand, MathContext.UNLIMITED)"
        // because together with the floor rounding it gives unwanted behaviour
        BigDecimal multiplicandBigDecimal = BigDecimal.valueOf(multiplicand);
        // The (unspecified) scale/precision of the multiplicand should have no impact on the returned scale/precision
        return new HardSoftBigDecimalScore(
                (int) Math.floor(initScore * multiplicand),
                hardScore.multiply(multiplicandBigDecimal).setScale(hardScore.scale(), RoundingMode.FLOOR),
                softScore.multiply(multiplicandBigDecimal).setScale(softScore.scale(), RoundingMode.FLOOR));
    }

    @Override
    public HardSoftBigDecimalScore divide(double divisor) {
        // Intentionally not taken "new BigDecimal(multiplicand, MathContext.UNLIMITED)"
        // because together with the floor rounding it gives unwanted behaviour
        BigDecimal divisorBigDecimal = BigDecimal.valueOf(divisor);
        // The (unspecified) scale/precision of the divisor should have no impact on the returned scale/precision
        return new HardSoftBigDecimalScore(
                (int) Math.floor(initScore / divisor),
                hardScore.divide(divisorBigDecimal, hardScore.scale(), RoundingMode.FLOOR),
                softScore.divide(divisorBigDecimal, softScore.scale(), RoundingMode.FLOOR));
    }

    @Override
    public HardSoftBigDecimalScore power(double exponent) {
        // Intentionally not taken "new BigDecimal(multiplicand, MathContext.UNLIMITED)"
        // because together with the floor rounding it gives unwanted behaviour
        BigDecimal exponentBigDecimal = BigDecimal.valueOf(exponent);
        // The (unspecified) scale/precision of the exponent should have no impact on the returned scale/precision
        // TODO FIXME remove .intValue() so non-integer exponents produce correct results
        // None of the normal Java libraries support BigDecimal.pow(BigDecimal)
        return new HardSoftBigDecimalScore(
                (int) Math.floor(Math.pow(initScore, exponent)),
                hardScore.pow(exponentBigDecimal.intValue()).setScale(hardScore.scale(), RoundingMode.FLOOR),
                softScore.pow(exponentBigDecimal.intValue()).setScale(softScore.scale(), RoundingMode.FLOOR));
    }

    @Override
    public HardSoftBigDecimalScore negate() {
        return new HardSoftBigDecimalScore(-initScore, hardScore.negate(), softScore.negate());
    }

    @Override
    public Number[] toLevelNumbers() {
        return new Number[]{hardScore, softScore};
    }

    @Override
    public boolean equals(Object o) {
        // A direct implementation (instead of EqualsBuilder) to avoid dependencies
        if (this == o) {
            return true;
        } else if (o instanceof HardSoftBigDecimalScore) {
            HardSoftBigDecimalScore other = (HardSoftBigDecimalScore) o;
            return initScore == other.getInitScore()
                    && hardScore.stripTrailingZeros().equals(other.getHardScore().stripTrailingZeros())
                    && softScore.stripTrailingZeros().equals(other.getSoftScore().stripTrailingZeros());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        // A direct implementation (instead of HashCodeBuilder) to avoid dependencies
        return (((17 * 37)
                + initScore) * 37
                + hardScore.stripTrailingZeros().hashCode()) * 37
                + softScore.stripTrailingZeros().hashCode();
    }

    @Override
    public int compareTo(HardSoftBigDecimalScore other) {
        // A direct implementation (instead of CompareToBuilder) to avoid dependencies
        if (initScore != other.getInitScore()) {
            return initScore < other.getInitScore() ? -1 : 1;
        }
        int hardScoreComparison = hardScore.compareTo(other.getHardScore());
        if (hardScoreComparison != 0) {
            return hardScoreComparison;
        } else {
            return softScore.compareTo(other.getSoftScore());
        }
    }

    @Override
    public String toShortString() {
        return buildShortString((n) -> ((BigDecimal) n).compareTo(BigDecimal.ZERO) != 0, HARD_LABEL, SOFT_LABEL);
    }

    @Override
    public String toString() {
        return getInitPrefix() + hardScore + HARD_LABEL + "/" + softScore + SOFT_LABEL;
    }

    @Override
    public boolean isCompatibleArithmeticArgument(Score otherScore) {
        return otherScore instanceof HardSoftBigDecimalScore;
    }

}
