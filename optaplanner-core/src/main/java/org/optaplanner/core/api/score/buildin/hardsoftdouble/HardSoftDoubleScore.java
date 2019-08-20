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
 * Hard constraints determine feasibility.
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

    public static final HardSoftDoubleScore ZERO = new HardSoftDoubleScore(0, 0.0, 0.0);
    public static final HardSoftDoubleScore ONE_HARD = new HardSoftDoubleScore(0, 1.0, 0.0);
    public static final HardSoftDoubleScore ONE_SOFT = new HardSoftDoubleScore(0, 0.0, 1.0);
    private static final String HARD_LABEL = "hard";
    private static final String SOFT_LABEL = "soft";

    public static HardSoftDoubleScore parseScore(String scoreString) {
        String[] scoreTokens = parseScoreTokens(HardSoftDoubleScore.class, scoreString, HARD_LABEL, SOFT_LABEL);
        int initScore = parseInitScore(HardSoftDoubleScore.class, scoreString, scoreTokens[0]);
        double hardScore = parseLevelAsDouble(HardSoftDoubleScore.class, scoreString, scoreTokens[1]);
        double softScore = parseLevelAsDouble(HardSoftDoubleScore.class, scoreString, scoreTokens[2]);
        return ofUninitialized(initScore, hardScore, softScore);
    }

    public static HardSoftDoubleScore ofUninitialized(int initScore, double hardScore, double softScore) {
        return new HardSoftDoubleScore(initScore, hardScore, softScore);
    }

    /**
     * @deprecated in favor of {@link #ofUninitialized(int, double, double)}
     */
    @Deprecated
    public static HardSoftDoubleScore valueOfUninitialized(int initScore, double hardScore, double softScore) {
        return new HardSoftDoubleScore(initScore, hardScore, softScore);
    }

    public static HardSoftDoubleScore of(double hardScore, double softScore) {
        return new HardSoftDoubleScore(0, hardScore, softScore);
    }

    /**
     * @deprecated in favor of {@link #of(double, double)}
     */
    @Deprecated
    public static HardSoftDoubleScore valueOf(double hardScore, double softScore) {
        return new HardSoftDoubleScore(0, hardScore, softScore);
    }

    public static HardSoftDoubleScore ofHard(double hardScore) {
        return new HardSoftDoubleScore(0, hardScore, 0.0);
    }

    public static HardSoftDoubleScore ofSoft(double softScore) {
        return new HardSoftDoubleScore(0, 0.0, softScore);
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
        super(Integer.MIN_VALUE);
        hardScore = Double.NaN;
        softScore = Double.NaN;
    }

    private HardSoftDoubleScore(int initScore, double hardScore, double softScore) {
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

    @Override
    public HardSoftDoubleScore toInitializedScore() {
        return initScore == 0 ? this : new HardSoftDoubleScore(0, hardScore, softScore);
    }

    @Override
    public HardSoftDoubleScore withInitScore(int newInitScore) {
        assertNoInitScore();
        return new HardSoftDoubleScore(newInitScore, hardScore, softScore);
    }

    @Override
    public boolean isFeasible() {
        return initScore >= 0 && hardScore >= 0.0;
    }

    @Override
    public HardSoftDoubleScore add(HardSoftDoubleScore addend) {
        return new HardSoftDoubleScore(
                initScore + addend.getInitScore(),
                hardScore + addend.getHardScore(),
                softScore + addend.getSoftScore());
    }

    @Override
    public HardSoftDoubleScore subtract(HardSoftDoubleScore subtrahend) {
        return new HardSoftDoubleScore(
                initScore - subtrahend.getInitScore(),
                hardScore - subtrahend.getHardScore(),
                softScore - subtrahend.getSoftScore());
    }

    @Override
    public HardSoftDoubleScore multiply(double multiplicand) {
        return new HardSoftDoubleScore(
                (int) Math.floor(initScore * multiplicand),
                hardScore * multiplicand,
                softScore * multiplicand);
    }

    @Override
    public HardSoftDoubleScore divide(double divisor) {
        return new HardSoftDoubleScore(
                (int) Math.floor(initScore / divisor),
                hardScore / divisor,
                softScore / divisor);
    }

    @Override
    public HardSoftDoubleScore power(double exponent) {
        return new HardSoftDoubleScore(
                (int) Math.floor(Math.pow(initScore, exponent)),
                Math.pow(hardScore, exponent),
                Math.pow(softScore, exponent));
    }

    @Override
    public HardSoftDoubleScore negate() {
        return new HardSoftDoubleScore(-initScore, -hardScore, -softScore);
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
        } else if (o instanceof HardSoftDoubleScore) {
            HardSoftDoubleScore other = (HardSoftDoubleScore) o;
            return initScore == other.getInitScore()
                    && hardScore == other.getHardScore()
                    && softScore == other.getSoftScore();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        // A direct implementation (instead of HashCodeBuilder) to avoid dependencies
        return (((17 * 37)
                + initScore) * 37
                + Double.valueOf(hardScore).hashCode()) * 37
                + Double.valueOf(softScore).hashCode();
    }

    @Override
    public int compareTo(HardSoftDoubleScore other) {
        // A direct implementation (instead of CompareToBuilder) to avoid dependencies
        if (initScore != other.getInitScore()) {
            return initScore < other.getInitScore() ? -1 : 1;
        } else if (hardScore != other.getHardScore()) {
            return hardScore < other.getHardScore() ? -1 : 1;
        } else {
            return Double.compare(softScore, other.getSoftScore());
        }
    }

    @Override
    public String toShortString() {
        return buildShortString((n) -> ((Double) n).doubleValue() != 0.0, HARD_LABEL, SOFT_LABEL);
    }

    @Override
    public String toString() {
        return getInitPrefix() + hardScore + HARD_LABEL + "/" + softScore + SOFT_LABEL;
    }

    @Override
    public boolean isCompatibleArithmeticArgument(Score otherScore) {
        return otherScore instanceof HardSoftDoubleScore;
    }

}
