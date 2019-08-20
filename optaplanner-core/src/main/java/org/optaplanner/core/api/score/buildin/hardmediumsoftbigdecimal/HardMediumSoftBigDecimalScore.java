/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.AbstractScore;
import org.optaplanner.core.api.score.FeasibilityScore;
import org.optaplanner.core.api.score.Score;

/**
 * This {@link Score} is based on 3 levels of {@link BigDecimal} constraints: hard, medium and soft.
 * Hard constraints have priority over medium constraints.
 * Medium constraints have priority over soft constraints.
 * Hard constraints determine feasibility.
 * <p>
 * This class is immutable.
 * @see Score
 */
public final class HardMediumSoftBigDecimalScore extends AbstractScore<HardMediumSoftBigDecimalScore>
        implements FeasibilityScore<HardMediumSoftBigDecimalScore> {

    public static final HardMediumSoftBigDecimalScore ZERO = new HardMediumSoftBigDecimalScore(0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    public static final HardMediumSoftBigDecimalScore ONE_HARD = new HardMediumSoftBigDecimalScore(0, BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ZERO);
    public static final HardMediumSoftBigDecimalScore ONE_MEDIUM = new HardMediumSoftBigDecimalScore(0, BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ZERO);
    public static final HardMediumSoftBigDecimalScore ONE_SOFT = new HardMediumSoftBigDecimalScore(0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ONE);
    private static final String HARD_LABEL = "hard";
    private static final String MEDIUM_LABEL = "medium";
    private static final String SOFT_LABEL = "soft";

    public static HardMediumSoftBigDecimalScore parseScore(String scoreString) {
        String[] scoreTokens = parseScoreTokens(HardMediumSoftBigDecimalScore.class, scoreString,
                HARD_LABEL, MEDIUM_LABEL, SOFT_LABEL);
        int initScore = parseInitScore(HardMediumSoftBigDecimalScore.class, scoreString, scoreTokens[0]);
        BigDecimal hardScore = parseLevelAsBigDecimal(HardMediumSoftBigDecimalScore.class, scoreString, scoreTokens[1]);
        BigDecimal mediumScore = parseLevelAsBigDecimal(HardMediumSoftBigDecimalScore.class, scoreString, scoreTokens[2]);
        BigDecimal softScore = parseLevelAsBigDecimal(HardMediumSoftBigDecimalScore.class, scoreString, scoreTokens[3]);
        return ofUninitialized(initScore, hardScore, mediumScore, softScore);
    }

    public static HardMediumSoftBigDecimalScore ofUninitialized(int initScore, BigDecimal hardScore, BigDecimal mediumScore, BigDecimal softScore) {
        return new HardMediumSoftBigDecimalScore(initScore, hardScore, mediumScore, softScore);
    }

    /**
     * @deprecated in favor of {@link #ofUninitialized(int, BigDecimal, BigDecimal, BigDecimal)}
     */
    @Deprecated
    public static HardMediumSoftBigDecimalScore valueOfUninitialized(int initScore, BigDecimal hardScore, BigDecimal mediumScore, BigDecimal softScore) {
        return new HardMediumSoftBigDecimalScore(initScore, hardScore, mediumScore, softScore);
    }

    public static HardMediumSoftBigDecimalScore of(BigDecimal hardScore, BigDecimal mediumScore, BigDecimal softScore) {
        return new HardMediumSoftBigDecimalScore(0, hardScore, mediumScore, softScore);
    }

    /**
     * @deprecated in favor of {@link #of(BigDecimal, BigDecimal, BigDecimal)}
     */
    @Deprecated
    public static HardMediumSoftBigDecimalScore valueOf(BigDecimal hardScore, BigDecimal mediumScore, BigDecimal softScore) {
        return new HardMediumSoftBigDecimalScore(0, hardScore, mediumScore, softScore);
    }

    public static HardMediumSoftBigDecimalScore ofHard(BigDecimal hardScore) {
        return new HardMediumSoftBigDecimalScore(0, hardScore, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public static HardMediumSoftBigDecimalScore ofMedium(BigDecimal mediumScore) {
        return new HardMediumSoftBigDecimalScore(0, BigDecimal.ZERO, mediumScore, BigDecimal.ZERO);
    }

    public static HardMediumSoftBigDecimalScore ofSoft(BigDecimal softScore) {
        return new HardMediumSoftBigDecimalScore(0, BigDecimal.ZERO, BigDecimal.ZERO, softScore);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final BigDecimal hardScore;
    private final BigDecimal mediumScore;
    private final BigDecimal softScore;

    /**
     * Private default constructor for default marshalling/unmarshalling of unknown frameworks that use reflection.
     * Such integration is always inferior to the specialized integration modules, such as
     * optaplanner-persistence-jpa, optaplanner-persistence-xstream, optaplanner-persistence-jaxb, ...
     */
    @SuppressWarnings("unused")
    private HardMediumSoftBigDecimalScore() {
        super(Integer.MIN_VALUE);
        hardScore = null;
        mediumScore = null;
        softScore = null;
    }

    private HardMediumSoftBigDecimalScore(int initScore, BigDecimal hardScore, BigDecimal mediumScore, BigDecimal softScore) {
        super(initScore);
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
    public BigDecimal getHardScore() {
        return hardScore;
    }

    /**
     * The total of the broken negative medium constraints and fulfilled positive medium constraints.
     * Their weight is included in the total.
     * The medium score is usually a negative number because most use cases only have negative constraints.
     * <p>
     * In a normal score comparison, the medium score is irrelevant if the 2 scores don't have the same hard score.
     * @return higher is better, usually negative, 0 if no medium constraints are broken/fulfilled
     */
    public BigDecimal getMediumScore() {
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
    public BigDecimal getSoftScore() {
        return softScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public HardMediumSoftBigDecimalScore toInitializedScore() {
        return initScore == 0 ? this : new HardMediumSoftBigDecimalScore(0, hardScore, mediumScore, softScore);
    }

    @Override
    public HardMediumSoftBigDecimalScore withInitScore(int newInitScore) {
        assertNoInitScore();
        return new HardMediumSoftBigDecimalScore(newInitScore, hardScore, mediumScore, softScore);
    }

    /**
     * A {@link PlanningSolution} is feasible if it has no broken hard constraints.
     * @return true if the {@link #getHardScore()} is 0 or higher
     */
    @Override
    public boolean isFeasible() {
        return initScore >= 0 && hardScore.compareTo(BigDecimal.ZERO) >= 0;
    }

    @Override
    public HardMediumSoftBigDecimalScore add(HardMediumSoftBigDecimalScore addend) {
        return new HardMediumSoftBigDecimalScore(
                initScore + addend.getInitScore(),
                hardScore.add(addend.getHardScore()),
                mediumScore.add(addend.getMediumScore()),
                softScore.add(addend.getSoftScore()));
    }

    @Override
    public HardMediumSoftBigDecimalScore subtract(HardMediumSoftBigDecimalScore subtrahend) {
        return new HardMediumSoftBigDecimalScore(
                initScore - subtrahend.getInitScore(),
                hardScore.subtract(subtrahend.getHardScore()),
                mediumScore.subtract(subtrahend.getMediumScore()),
                softScore.subtract(subtrahend.getSoftScore()));
    }

    @Override
    public HardMediumSoftBigDecimalScore multiply(double multiplicand) {
        // Intentionally not taken "new BigDecimal(multiplicand, MathContext.UNLIMITED)"
        // because together with the floor rounding it gives unwanted behaviour
        BigDecimal multiplicandBigDecimal = BigDecimal.valueOf(multiplicand);
        // The (unspecified) scale/precision of the multiplicand should have no impact on the returned scale/precision
        return new HardMediumSoftBigDecimalScore(
                (int) Math.floor(initScore * multiplicand),
                hardScore.multiply(multiplicandBigDecimal).setScale(hardScore.scale(), RoundingMode.FLOOR),
                mediumScore.multiply(multiplicandBigDecimal).setScale(mediumScore.scale(), RoundingMode.FLOOR),
                softScore.multiply(multiplicandBigDecimal).setScale(softScore.scale(), RoundingMode.FLOOR));
    }

    @Override
    public HardMediumSoftBigDecimalScore divide(double divisor) {
        BigDecimal divisorBigDecimal = BigDecimal.valueOf(divisor);
        // The (unspecified) scale/precision of the divisor should have no impact on the returned scale/precision
        return new HardMediumSoftBigDecimalScore(
                (int) Math.floor(initScore / divisor),
                hardScore.divide(divisorBigDecimal, hardScore.scale(), RoundingMode.FLOOR),
                mediumScore.divide(divisorBigDecimal, mediumScore.scale(), RoundingMode.FLOOR),
                softScore.divide(divisorBigDecimal, softScore.scale(), RoundingMode.FLOOR));
    }

    @Override
    public HardMediumSoftBigDecimalScore power(double exponent) {
        BigDecimal exponentBigDecimal = BigDecimal.valueOf(exponent);
        // The (unspecified) scale/precision of the exponent should have no impact on the returned scale/precision
        // TODO FIXME remove .intValue() so non-integer exponents produce correct results
        // None of the normal Java libraries support BigDecimal.pow(BigDecimal)
        return new HardMediumSoftBigDecimalScore(
                (int) Math.floor(Math.pow(initScore, exponent)),
                hardScore.pow(exponentBigDecimal.intValue()).setScale(hardScore.scale(), RoundingMode.FLOOR),
                mediumScore.pow(exponentBigDecimal.intValue()).setScale(mediumScore.scale(), RoundingMode.FLOOR),
                softScore.pow(exponentBigDecimal.intValue()).setScale(softScore.scale(), RoundingMode.FLOOR));
    }

    @Override
    public HardMediumSoftBigDecimalScore negate() {
        return new HardMediumSoftBigDecimalScore(-initScore, hardScore.negate(), mediumScore.negate(), softScore.negate());
    }

    @Override
    public Number[] toLevelNumbers() {
        return new Number[]{hardScore, mediumScore, softScore};
    }

    @Override
    public boolean equals(Object o) {
        // A direct implementation (instead of EqualsBuilder) to avoid dependencies
        if (this == o) {
            return true;
        } else if (o instanceof HardMediumSoftBigDecimalScore) {
            HardMediumSoftBigDecimalScore other = (HardMediumSoftBigDecimalScore) o;
            return initScore == other.getInitScore()
                    && hardScore.stripTrailingZeros().equals(other.getHardScore().stripTrailingZeros())
                    && mediumScore.stripTrailingZeros().equals(other.getMediumScore().stripTrailingZeros())
                    && softScore.stripTrailingZeros().equals(other.getSoftScore().stripTrailingZeros());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        // A direct implementation (instead of HashCodeBuilder) to avoid dependencies
        return (((((17 * 37)
                + initScore) * 37)
                + hardScore.stripTrailingZeros().hashCode()) * 37
                + mediumScore.stripTrailingZeros().hashCode()) * 37
                + softScore.stripTrailingZeros().hashCode();
    }

    @Override
    public int compareTo(HardMediumSoftBigDecimalScore other) {
        if (initScore != other.getInitScore()) {
            return initScore < other.getInitScore() ? -1 : 1;
        }
        int hardScoreComparison = hardScore.compareTo(other.getHardScore());
        int mediumScoreComparison = mediumScore.compareTo(other.getMediumScore());
        if (hardScoreComparison != 0) {
            return hardScoreComparison;
        } else if (mediumScoreComparison != 0) {
            return mediumScoreComparison;
        } else {
            return softScore.compareTo(other.getSoftScore());
        }
    }

    @Override
    public String toShortString() {
        return buildShortString((n) -> ((BigDecimal) n).compareTo(BigDecimal.ZERO) != 0,
                HARD_LABEL, MEDIUM_LABEL, SOFT_LABEL);
    }

    @Override
    public String toString() {
        return getInitPrefix() + hardScore + HARD_LABEL + "/" + mediumScore + MEDIUM_LABEL + "/" + softScore + SOFT_LABEL;
    }

    @Override
    public boolean isCompatibleArithmeticArgument(Score otherScore) {
        return otherScore instanceof HardMediumSoftBigDecimalScore;
    }

}
