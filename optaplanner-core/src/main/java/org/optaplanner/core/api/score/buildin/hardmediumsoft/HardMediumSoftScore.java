/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.AbstractScore;
import org.optaplanner.core.api.score.FeasibilityScore;
import org.optaplanner.core.api.score.Score;

/**
 * This {@link Score} is based on 3 levels of int constraints: hard, medium and soft.
 * Hard constraints have priority over medium constraints.
 * Medium constraints have priority over soft constraints.
 * Hard constraints determine feasibility.
 * <p>
 * This class is immutable.
 * @see Score
 */
public final class HardMediumSoftScore extends AbstractScore<HardMediumSoftScore>
        implements FeasibilityScore<HardMediumSoftScore> {

    public static final HardMediumSoftScore ZERO = new HardMediumSoftScore(0, 0, 0, 0);
    public static final HardMediumSoftScore ONE_HARD = new HardMediumSoftScore(0, 1, 0, 0);
    public static final HardMediumSoftScore ONE_MEDIUM = new HardMediumSoftScore(0, 0, 1, 0);
    public static final HardMediumSoftScore ONE_SOFT = new HardMediumSoftScore(0, 0, 0, 1);
    private static final String HARD_LABEL = "hard";
    private static final String MEDIUM_LABEL = "medium";
    private static final String SOFT_LABEL = "soft";

    public static HardMediumSoftScore parseScore(String scoreString) {
        String[] scoreTokens = parseScoreTokens(HardMediumSoftScore.class, scoreString,
                HARD_LABEL, MEDIUM_LABEL, SOFT_LABEL);
        int initScore = parseInitScore(HardMediumSoftScore.class, scoreString, scoreTokens[0]);
        int hardScore = parseLevelAsInt(HardMediumSoftScore.class, scoreString, scoreTokens[1]);
        int mediumScore = parseLevelAsInt(HardMediumSoftScore.class, scoreString, scoreTokens[2]);
        int softScore = parseLevelAsInt(HardMediumSoftScore.class, scoreString, scoreTokens[3]);
        return ofUninitialized(initScore, hardScore, mediumScore, softScore);
    }

    public static HardMediumSoftScore ofUninitialized(int initScore, int hardScore, int mediumScore, int softScore) {
        return new HardMediumSoftScore(initScore, hardScore, mediumScore, softScore);
    }

    /**
     * @deprecated in favor of {@link #ofUninitialized(int, int, int, int)}
     */
    @Deprecated
    public static HardMediumSoftScore valueOfUninitialized(int initScore, int hardScore, int mediumScore, int softScore) {
        return new HardMediumSoftScore(initScore, hardScore, mediumScore, softScore);
    }

    public static HardMediumSoftScore of(int hardScore, int mediumScore, int softScore) {
        return new HardMediumSoftScore(0, hardScore, mediumScore, softScore);
    }

    /**
     * @deprecated in favor of {@link #of(int, int, int)}
     */
    @Deprecated
    public static HardMediumSoftScore valueOf(int hardScore, int mediumScore, int softScore) {
        return new HardMediumSoftScore(0, hardScore, mediumScore, softScore);
    }

    public static HardMediumSoftScore ofHard(int hardScore) {
        return new HardMediumSoftScore(0, hardScore, 0, 0);
    }

    public static HardMediumSoftScore ofMedium(int mediumScore) {
        return new HardMediumSoftScore(0, 0, mediumScore, 0);
    }

    public static HardMediumSoftScore ofSoft(int softScore) {
        return new HardMediumSoftScore(0, 0, 0, softScore);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final int hardScore;
    private final int mediumScore;
    private final int softScore;

    /**
     * Private default constructor for default marshalling/unmarshalling of unknown frameworks that use reflection.
     * Such integration is always inferior to the specialized integration modules, such as
     * optaplanner-persistence-jpa, optaplanner-persistence-xstream, optaplanner-persistence-jaxb, ...
     */
    @SuppressWarnings("unused")
    private HardMediumSoftScore() {
        super(Integer.MIN_VALUE);
        hardScore = Integer.MIN_VALUE;
        mediumScore = Integer.MIN_VALUE;
        softScore = Integer.MIN_VALUE;
    }

    private HardMediumSoftScore(int initScore, int hardScore, int mediumScore, int softScore) {
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
    public int getHardScore() {
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
    public int getMediumScore() {
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
    public int getSoftScore() {
        return softScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public HardMediumSoftScore toInitializedScore() {
        return initScore == 0 ? this : new HardMediumSoftScore(0, hardScore, mediumScore, softScore);
    }

    @Override
    public HardMediumSoftScore withInitScore(int newInitScore) {
        assertNoInitScore();
        return new HardMediumSoftScore(newInitScore, hardScore, mediumScore, softScore);
    }

    /**
     * A {@link PlanningSolution} is feasible if it has no broken hard constraints.
     * @return true if the {@link #getHardScore()} is 0 or higher
     */
    @Override
    public boolean isFeasible() {
        return initScore >= 0 && hardScore >= 0;
    }

    @Override
    public HardMediumSoftScore add(HardMediumSoftScore addend) {
        return new HardMediumSoftScore(
                initScore + addend.getInitScore(),
                hardScore + addend.getHardScore(),
                mediumScore + addend.getMediumScore(),
                softScore + addend.getSoftScore());
    }

    @Override
    public HardMediumSoftScore subtract(HardMediumSoftScore subtrahend) {
        return new HardMediumSoftScore(
                initScore - subtrahend.getInitScore(),
                hardScore - subtrahend.getHardScore(),
                mediumScore - subtrahend.getMediumScore(),
                softScore - subtrahend.getSoftScore());
    }

    @Override
    public HardMediumSoftScore multiply(double multiplicand) {
        return new HardMediumSoftScore(
                (int) Math.floor(initScore * multiplicand),
                (int) Math.floor(hardScore * multiplicand),
                (int) Math.floor(mediumScore * multiplicand),
                (int) Math.floor(softScore * multiplicand));
    }

    @Override
    public HardMediumSoftScore divide(double divisor) {
        return new HardMediumSoftScore(
                (int) Math.floor(initScore / divisor),
                (int) Math.floor(hardScore / divisor),
                (int) Math.floor(mediumScore / divisor),
                (int) Math.floor(softScore / divisor));
    }

    @Override
    public HardMediumSoftScore power(double exponent) {
        return new HardMediumSoftScore(
                (int) Math.floor(Math.pow(initScore, exponent)),
                (int) Math.floor(Math.pow(hardScore, exponent)),
                (int) Math.floor(Math.pow(mediumScore, exponent)),
                (int) Math.floor(Math.pow(softScore, exponent)));
    }

    @Override
    public HardMediumSoftScore negate() {
        return new HardMediumSoftScore(-initScore, -hardScore, -mediumScore, -softScore);
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
        } else if (o instanceof HardMediumSoftScore) {
            HardMediumSoftScore other = (HardMediumSoftScore) o;
            return initScore == other.getInitScore()
                    && hardScore == other.getHardScore()
                    && mediumScore == other.getMediumScore()
                    && softScore == other.getSoftScore();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        // A direct implementation (instead of HashCodeBuilder) to avoid dependencies
        return ((((17 * 37)
                + initScore) * 37
                + hardScore) * 37
                + mediumScore) * 37
                + softScore;
    }

    @Override
    public int compareTo(HardMediumSoftScore other) {
        // A direct implementation (instead of CompareToBuilder) to avoid dependencies
        if (initScore != other.getInitScore()) {
            return initScore < other.getInitScore() ? -1 : 1;
        } else if (hardScore != other.getHardScore()) {
            return hardScore < other.getHardScore() ? -1 : 1;
        } else if (mediumScore != other.getMediumScore()) {
            return mediumScore < other.getMediumScore() ? -1 : 1;
        } else {
            return Integer.compare(softScore, other.getSoftScore());
        }
    }

    @Override
    public String toShortString() {
        return buildShortString((n) -> ((Integer) n).intValue() != 0, HARD_LABEL, MEDIUM_LABEL, SOFT_LABEL);
    }

    @Override
    public String toString() {
        return getInitPrefix() + hardScore + HARD_LABEL + "/" + mediumScore + MEDIUM_LABEL + "/" + softScore + SOFT_LABEL;
    }

    @Override
    public boolean isCompatibleArithmeticArgument(Score otherScore) {
        return otherScore instanceof HardMediumSoftScore;
    }

}
