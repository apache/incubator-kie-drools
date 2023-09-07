/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.core.api.score.buildin.hardmediumsoft;

import static org.optaplanner.core.impl.score.ScoreUtil.HARD_LABEL;
import static org.optaplanner.core.impl.score.ScoreUtil.MEDIUM_LABEL;
import static org.optaplanner.core.impl.score.ScoreUtil.SOFT_LABEL;

import java.util.Objects;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.ScoreUtil;

/**
 * This {@link Score} is based on 3 levels of int constraints: hard, medium and soft.
 * Hard constraints have priority over medium constraints.
 * Medium constraints have priority over soft constraints.
 * Hard constraints determine feasibility.
 * <p>
 * This class is immutable.
 *
 * @see Score
 */
public final class HardMediumSoftScore implements Score<HardMediumSoftScore> {

    public static final HardMediumSoftScore ZERO = new HardMediumSoftScore(0, 0, 0, 0);
    public static final HardMediumSoftScore ONE_HARD = new HardMediumSoftScore(0, 1, 0, 0);
    public static final HardMediumSoftScore ONE_MEDIUM = new HardMediumSoftScore(0, 0, 1, 0);
    public static final HardMediumSoftScore ONE_SOFT = new HardMediumSoftScore(0, 0, 0, 1);

    public static HardMediumSoftScore parseScore(String scoreString) {
        String[] scoreTokens = ScoreUtil.parseScoreTokens(HardMediumSoftScore.class, scoreString,
                HARD_LABEL, MEDIUM_LABEL, SOFT_LABEL);
        int initScore = ScoreUtil.parseInitScore(HardMediumSoftScore.class, scoreString, scoreTokens[0]);
        int hardScore = ScoreUtil.parseLevelAsInt(HardMediumSoftScore.class, scoreString, scoreTokens[1]);
        int mediumScore = ScoreUtil.parseLevelAsInt(HardMediumSoftScore.class, scoreString, scoreTokens[2]);
        int softScore = ScoreUtil.parseLevelAsInt(HardMediumSoftScore.class, scoreString, scoreTokens[3]);
        return ofUninitialized(initScore, hardScore, mediumScore, softScore);
    }

    public static HardMediumSoftScore ofUninitialized(int initScore, int hardScore, int mediumScore, int softScore) {
        return new HardMediumSoftScore(initScore, hardScore, mediumScore, softScore);
    }

    public static HardMediumSoftScore of(int hardScore, int mediumScore, int softScore) {
        return new HardMediumSoftScore(0, hardScore, mediumScore, softScore);
    }

    public static HardMediumSoftScore ofHard(int hardScore) {
        return of(hardScore, 0, 0);
    }

    public static HardMediumSoftScore ofMedium(int mediumScore) {
        return of(0, mediumScore, 0);
    }

    public static HardMediumSoftScore ofSoft(int softScore) {
        return of(0, 0, softScore);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final int initScore;
    private final int hardScore;
    private final int mediumScore;
    private final int softScore;

    /**
     * Private default constructor for default marshalling/unmarshalling of unknown frameworks that use reflection.
     * Such integration is always inferior to the specialized integration modules, such as
     * optaplanner-persistence-jpa, optaplanner-persistence-jackson, optaplanner-persistence-jaxb, ...
     */
    @SuppressWarnings("unused")
    private HardMediumSoftScore() {
        this(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    private HardMediumSoftScore(int initScore, int hardScore, int mediumScore, int softScore) {
        this.initScore = initScore;
        this.hardScore = hardScore;
        this.mediumScore = mediumScore;
        this.softScore = softScore;
    }

    @Override
    public int initScore() {
        return initScore;
    }

    /**
     * The total of the broken negative hard constraints and fulfilled positive hard constraints.
     * Their weight is included in the total.
     * The hard score is usually a negative number because most use cases only have negative constraints.
     *
     * @return higher is better, usually negative, 0 if no hard constraints are broken/fulfilled
     */
    public int hardScore() {
        return hardScore;
    }

    /**
     * As defined by {@link #hardScore()}.
     *
     * @deprecated Use {@link #hardScore()} instead.
     */
    @Deprecated(forRemoval = true)
    public int getHardScore() {
        return hardScore;
    }

    /**
     * The total of the broken negative medium constraints and fulfilled positive medium constraints.
     * Their weight is included in the total.
     * The medium score is usually a negative number because most use cases only have negative constraints.
     * <p>
     * In a normal score comparison, the medium score is irrelevant if the 2 scores don't have the same hard score.
     *
     * @return higher is better, usually negative, 0 if no medium constraints are broken/fulfilled
     */
    public int mediumScore() {
        return mediumScore;
    }

    /**
     * As defined by {@link #mediumScore()}.
     *
     * @deprecated Use {@link #mediumScore()} instead.
     */
    @Deprecated(forRemoval = true)
    public int getMediumScore() {
        return mediumScore;
    }

    /**
     * The total of the broken negative soft constraints and fulfilled positive soft constraints.
     * Their weight is included in the total.
     * The soft score is usually a negative number because most use cases only have negative constraints.
     * <p>
     * In a normal score comparison, the soft score is irrelevant if the 2 scores don't have the same hard and medium score.
     *
     * @return higher is better, usually negative, 0 if no soft constraints are broken/fulfilled
     */
    public int softScore() {
        return softScore;
    }

    /**
     * As defined by {@link #softScore()}.
     *
     * @deprecated Use {@link #softScore()} instead.
     */
    @Deprecated(forRemoval = true)
    public int getSoftScore() {
        return softScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public HardMediumSoftScore withInitScore(int newInitScore) {
        return new HardMediumSoftScore(newInitScore, hardScore, mediumScore, softScore);
    }

    /**
     * A {@link PlanningSolution} is feasible if it has no broken hard constraints.
     *
     * @return true if the {@link #hardScore()} is 0 or higher
     */
    @Override
    public boolean isFeasible() {
        return initScore >= 0 && hardScore >= 0;
    }

    @Override
    public HardMediumSoftScore add(HardMediumSoftScore addend) {
        return new HardMediumSoftScore(
                initScore + addend.initScore(),
                hardScore + addend.hardScore(),
                mediumScore + addend.mediumScore(),
                softScore + addend.softScore());
    }

    @Override
    public HardMediumSoftScore subtract(HardMediumSoftScore subtrahend) {
        return new HardMediumSoftScore(
                initScore - subtrahend.initScore(),
                hardScore - subtrahend.hardScore(),
                mediumScore - subtrahend.mediumScore(),
                softScore - subtrahend.softScore());
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
    public HardMediumSoftScore abs() {
        return new HardMediumSoftScore(Math.abs(initScore), Math.abs(hardScore), Math.abs(mediumScore), Math.abs(softScore));
    }

    @Override
    public HardMediumSoftScore zero() {
        return HardMediumSoftScore.ZERO;
    }

    @Override
    public Number[] toLevelNumbers() {
        return new Number[] { hardScore, mediumScore, softScore };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof HardMediumSoftScore) {
            HardMediumSoftScore other = (HardMediumSoftScore) o;
            return initScore == other.initScore()
                    && hardScore == other.hardScore()
                    && mediumScore == other.mediumScore()
                    && softScore == other.softScore();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(initScore, hardScore, mediumScore, softScore);
    }

    @Override
    public int compareTo(HardMediumSoftScore other) {
        if (initScore != other.initScore()) {
            return Integer.compare(initScore, other.initScore());
        } else if (hardScore != other.hardScore()) {
            return Integer.compare(hardScore, other.hardScore());
        } else if (mediumScore != other.mediumScore()) {
            return Integer.compare(mediumScore, other.mediumScore());
        } else {
            return Integer.compare(softScore, other.softScore());
        }
    }

    @Override
    public String toShortString() {
        return ScoreUtil.buildShortString(this, n -> n.intValue() != 0, HARD_LABEL, MEDIUM_LABEL, SOFT_LABEL);
    }

    @Override
    public String toString() {
        return ScoreUtil.getInitPrefix(initScore) + hardScore + HARD_LABEL + "/" + mediumScore + MEDIUM_LABEL + "/" + softScore
                + SOFT_LABEL;
    }

}
