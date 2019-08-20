/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.buildin.hardsoft;

import org.optaplanner.core.api.score.AbstractScore;
import org.optaplanner.core.api.score.FeasibilityScore;
import org.optaplanner.core.api.score.Score;

/**
 * This {@link Score} is based on 2 levels of int constraints: hard and soft.
 * Hard constraints have priority over soft constraints.
 * Hard constraints determine feasibility.
 * <p>
 * This class is immutable.
 * @see Score
 */
public final class HardSoftScore extends AbstractScore<HardSoftScore> implements FeasibilityScore<HardSoftScore> {

    public static final HardSoftScore ZERO = new HardSoftScore(0, 0, 0);
    public static final HardSoftScore ONE_HARD = new HardSoftScore(0, 1, 0);
    public static final HardSoftScore ONE_SOFT = new HardSoftScore(0, 0, 1);
    private static final String HARD_LABEL = "hard";
    private static final String SOFT_LABEL = "soft";

    public static HardSoftScore parseScore(String scoreString) {
        String[] scoreTokens = parseScoreTokens(HardSoftScore.class, scoreString, HARD_LABEL, SOFT_LABEL);
        int initScore = parseInitScore(HardSoftScore.class, scoreString, scoreTokens[0]);
        int hardScore = parseLevelAsInt(HardSoftScore.class, scoreString, scoreTokens[1]);
        int softScore = parseLevelAsInt(HardSoftScore.class, scoreString, scoreTokens[2]);
        return ofUninitialized(initScore, hardScore, softScore);
    }

    public static HardSoftScore ofUninitialized(int initScore, int hardScore, int softScore) {
        return new HardSoftScore(initScore, hardScore, softScore);
    }

    /**
     * @deprecated in favor of {@link #ofUninitialized(int, int, int)}
     */
    @Deprecated
    public static HardSoftScore valueOfUninitialized(int initScore, int hardScore, int softScore) {
        return new HardSoftScore(initScore, hardScore, softScore);
    }

    public static HardSoftScore of(int hardScore, int softScore) {
        return new HardSoftScore(0, hardScore, softScore);
    }

    /**
     * @deprecated in favor of {@link #of(int, int)}
     */
    @Deprecated
    public static HardSoftScore valueOf(int hardScore, int softScore) {
        return new HardSoftScore(0, hardScore, softScore);
    }

    public static HardSoftScore ofHard(int hardScore) {
        return new HardSoftScore(0, hardScore, 0);
    }

    public static HardSoftScore ofSoft(int softScore) {
        return new HardSoftScore(0, 0, softScore);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final int hardScore;
    private final int softScore;

    /**
     * Private default constructor for default marshalling/unmarshalling of unknown frameworks that use reflection.
     * Such integration is always inferior to the specialized integration modules, such as
     * optaplanner-persistence-jpa, optaplanner-persistence-xstream, optaplanner-persistence-jaxb, ...
     */
    @SuppressWarnings("unused")
    private HardSoftScore() {
        super(Integer.MIN_VALUE);
        hardScore = Integer.MIN_VALUE;
        softScore = Integer.MIN_VALUE;
    }

    private HardSoftScore(int initScore, int hardScore, int softScore) {
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
    public int getHardScore() {
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
    public int getSoftScore() {
        return softScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public HardSoftScore toInitializedScore() {
        return initScore == 0 ? this : new HardSoftScore(0, hardScore, softScore);
    }

    @Override
    public HardSoftScore withInitScore(int newInitScore) {
        assertNoInitScore();
        return new HardSoftScore(newInitScore, hardScore, softScore);
    }

    @Override
    public boolean isFeasible() {
        return initScore >= 0 && hardScore >= 0;
    }

    @Override
    public HardSoftScore add(HardSoftScore addend) {
        return new HardSoftScore(
                initScore + addend.getInitScore(),
                hardScore + addend.getHardScore(),
                softScore + addend.getSoftScore());
    }

    @Override
    public HardSoftScore subtract(HardSoftScore subtrahend) {
        return new HardSoftScore(
                initScore - subtrahend.getInitScore(),
                hardScore - subtrahend.getHardScore(),
                softScore - subtrahend.getSoftScore());
    }

    @Override
    public HardSoftScore multiply(double multiplicand) {
        return new HardSoftScore(
                (int) Math.floor(initScore * multiplicand),
                (int) Math.floor(hardScore * multiplicand),
                (int) Math.floor(softScore * multiplicand));
    }

    @Override
    public HardSoftScore divide(double divisor) {
        return new HardSoftScore(
                (int) Math.floor(initScore / divisor),
                (int) Math.floor(hardScore / divisor),
                (int) Math.floor(softScore / divisor));
    }

    @Override
    public HardSoftScore power(double exponent) {
        return new HardSoftScore(
                (int) Math.floor(Math.pow(initScore, exponent)),
                (int) Math.floor(Math.pow(hardScore, exponent)),
                (int) Math.floor(Math.pow(softScore, exponent)));
    }

    @Override
    public HardSoftScore negate() {
        return new HardSoftScore(-initScore, -hardScore, -softScore);
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
        } else if (o instanceof HardSoftScore) {
            HardSoftScore other = (HardSoftScore) o;
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
                + hardScore) * 37
                + softScore;
    }

    @Override
    public int compareTo(HardSoftScore other) {
        // A direct implementation (instead of CompareToBuilder) to avoid dependencies
        if (initScore != other.getInitScore()) {
            return initScore < other.getInitScore() ? -1 : 1;
        } else if (hardScore != other.getHardScore()) {
            return hardScore < other.getHardScore() ? -1 : 1;
        } else {
            return Integer.compare(softScore, other.getSoftScore());
        }
    }

    @Override
    public String toShortString() {
        return buildShortString((n) -> ((Integer) n).intValue() != 0, HARD_LABEL, SOFT_LABEL);
    }

    @Override
    public String toString() {
        return getInitPrefix() + hardScore + HARD_LABEL + "/" + softScore + SOFT_LABEL;
    }

    @Override
    public boolean isCompatibleArithmeticArgument(Score otherScore) {
        return otherScore instanceof HardSoftScore;
    }

}
