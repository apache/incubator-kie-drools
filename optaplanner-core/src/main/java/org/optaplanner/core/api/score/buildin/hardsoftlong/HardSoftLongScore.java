/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.buildin.hardsoftlong;

import org.optaplanner.core.api.score.AbstractScore;
import org.optaplanner.core.api.score.FeasibilityScore;
import org.optaplanner.core.api.score.Score;

/**
 * This {@link Score} is based on 2 levels of long constraints: hard and soft.
 * Hard constraints have priority over soft constraints.
 * Hard constraints determine feasibility.
 * <p>
 * This class is immutable.
 * @see Score
 */
public final class HardSoftLongScore extends AbstractScore<HardSoftLongScore>
        implements FeasibilityScore<HardSoftLongScore> {

    public static final HardSoftLongScore ZERO = new HardSoftLongScore(0, 0L, 0L);
    public static final HardSoftLongScore ONE_HARD = new HardSoftLongScore(0, 1L, 0L);
    public static final HardSoftLongScore ONE_SOFT = new HardSoftLongScore(0, 0L, 1L);
    private static final String HARD_LABEL = "hard";
    private static final String SOFT_LABEL = "soft";

    public static HardSoftLongScore parseScore(String scoreString) {
        String[] scoreTokens = parseScoreTokens(HardSoftLongScore.class, scoreString, HARD_LABEL, SOFT_LABEL);
        int initScore = parseInitScore(HardSoftLongScore.class, scoreString, scoreTokens[0]);
        long hardScore = parseLevelAsLong(HardSoftLongScore.class, scoreString, scoreTokens[1]);
        long softScore = parseLevelAsLong(HardSoftLongScore.class, scoreString, scoreTokens[2]);
        return ofUninitialized(initScore, hardScore, softScore);
    }

    public static HardSoftLongScore ofUninitialized(int initScore, long hardScore, long softScore) {
        return new HardSoftLongScore(initScore, hardScore, softScore);
    }

    /**
     * @deprecated in favor of {@link #ofUninitialized(int, long, long)}
     */
    @Deprecated
    public static HardSoftLongScore valueOfUninitialized(int initScore, long hardScore, long softScore) {
        return new HardSoftLongScore(initScore, hardScore, softScore);
    }

    public static HardSoftLongScore of(long hardScore, long softScore) {
        return new HardSoftLongScore(0, hardScore, softScore);
    }

    /**
     * @deprecated in favor of {@link #of(long, long)}
     */
    @Deprecated
    public static HardSoftLongScore valueOf(long hardScore, long softScore) {
        return new HardSoftLongScore(0, hardScore, softScore);
    }

    public static HardSoftLongScore ofHard(long hardScore) {
        return new HardSoftLongScore(0, hardScore, 0L);
    }

    public static HardSoftLongScore ofSoft(long softScore) {
        return new HardSoftLongScore(0, 0L, softScore);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final long hardScore;
    private final long softScore;

    /**
     * Private default constructor for default marshalling/unmarshalling of unknown frameworks that use reflection.
     * Such integration is always inferior to the specialized integration modules, such as
     * optaplanner-persistence-jpa, optaplanner-persistence-xstream, optaplanner-persistence-jaxb, ...
     */
    @SuppressWarnings("unused")
    private HardSoftLongScore() {
        super(Integer.MIN_VALUE);
        hardScore = Long.MIN_VALUE;
        softScore = Long.MIN_VALUE;
    }

    private HardSoftLongScore(int initScore, long hardScore, long softScore) {
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
    public long getHardScore() {
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
    public long getSoftScore() {
        return softScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public HardSoftLongScore toInitializedScore() {
        return initScore == 0 ? this : new HardSoftLongScore(0, hardScore, softScore);
    }

    @Override
    public HardSoftLongScore withInitScore(int newInitScore) {
        assertNoInitScore();
        return new HardSoftLongScore(newInitScore, hardScore, softScore);
    }

    @Override
    public boolean isFeasible() {
        return initScore >= 0 && hardScore >= 0L;
    }

    @Override
    public HardSoftLongScore add(HardSoftLongScore addend) {
        return new HardSoftLongScore(
                initScore + addend.getInitScore(),
                hardScore + addend.getHardScore(),
                softScore + addend.getSoftScore());
    }

    @Override
    public HardSoftLongScore subtract(HardSoftLongScore subtrahend) {
        return new HardSoftLongScore(
                initScore - subtrahend.getInitScore(),
                hardScore - subtrahend.getHardScore(),
                softScore - subtrahend.getSoftScore());
    }

    @Override
    public HardSoftLongScore multiply(double multiplicand) {
        return new HardSoftLongScore(
                (int) Math.floor(initScore * multiplicand),
                (long) Math.floor(hardScore * multiplicand),
                (long) Math.floor(softScore * multiplicand));
    }

    @Override
    public HardSoftLongScore divide(double divisor) {
        return new HardSoftLongScore(
                (int) Math.floor(initScore / divisor),
                (long) Math.floor(hardScore / divisor),
                (long) Math.floor(softScore / divisor));
    }

    @Override
    public HardSoftLongScore power(double exponent) {
        return new HardSoftLongScore(
                (int) Math.floor(Math.pow(initScore, exponent)),
                (long) Math.floor(Math.pow(hardScore, exponent)),
                (long) Math.floor(Math.pow(softScore, exponent)));
    }

    @Override
    public HardSoftLongScore negate() {
        return new HardSoftLongScore(-initScore, -hardScore, -softScore);
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
        } else if (o instanceof HardSoftLongScore) {
            HardSoftLongScore other = (HardSoftLongScore) o;
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
                + Long.valueOf(hardScore).hashCode()) * 37
                + Long.valueOf(softScore).hashCode();
    }

    @Override
    public int compareTo(HardSoftLongScore other) {
        // A direct implementation (instead of CompareToBuilder) to avoid dependencies
        if (initScore != other.getInitScore()) {
            return initScore < other.getInitScore() ? -1 : 1;
        } else if (hardScore != other.getHardScore()) {
            return hardScore < other.getHardScore() ? -1 : 1;
        } else {
            return Long.compare(softScore, other.getSoftScore());
        }
    }

    @Override
    public String toShortString() {
        return buildShortString((n) -> ((Long) n).longValue() != 0L, HARD_LABEL, SOFT_LABEL);
    }

    @Override
    public String toString() {
        return getInitPrefix() + hardScore + HARD_LABEL + "/" + softScore + SOFT_LABEL;
    }

    @Override
    public boolean isCompatibleArithmeticArgument(Score otherScore) {
        return otherScore instanceof HardSoftLongScore;
    }

}
