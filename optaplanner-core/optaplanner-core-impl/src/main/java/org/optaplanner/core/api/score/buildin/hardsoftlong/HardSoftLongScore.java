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

package org.optaplanner.core.api.score.buildin.hardsoftlong;

import static org.optaplanner.core.impl.score.ScoreUtil.HARD_LABEL;
import static org.optaplanner.core.impl.score.ScoreUtil.SOFT_LABEL;

import java.util.Objects;

import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.ScoreUtil;

/**
 * This {@link Score} is based on 2 levels of long constraints: hard and soft.
 * Hard constraints have priority over soft constraints.
 * Hard constraints determine feasibility.
 * <p>
 * This class is immutable.
 *
 * @see Score
 */
public final class HardSoftLongScore implements Score<HardSoftLongScore> {

    public static final HardSoftLongScore ZERO = new HardSoftLongScore(0, 0L, 0L);
    public static final HardSoftLongScore ONE_HARD = new HardSoftLongScore(0, 1L, 0L);
    public static final HardSoftLongScore ONE_SOFT = new HardSoftLongScore(0, 0L, 1L);

    public static HardSoftLongScore parseScore(String scoreString) {
        String[] scoreTokens = ScoreUtil.parseScoreTokens(HardSoftLongScore.class, scoreString, HARD_LABEL, SOFT_LABEL);
        int initScore = ScoreUtil.parseInitScore(HardSoftLongScore.class, scoreString, scoreTokens[0]);
        long hardScore = ScoreUtil.parseLevelAsLong(HardSoftLongScore.class, scoreString, scoreTokens[1]);
        long softScore = ScoreUtil.parseLevelAsLong(HardSoftLongScore.class, scoreString, scoreTokens[2]);
        return ofUninitialized(initScore, hardScore, softScore);
    }

    public static HardSoftLongScore ofUninitialized(int initScore, long hardScore, long softScore) {
        return new HardSoftLongScore(initScore, hardScore, softScore);
    }

    public static HardSoftLongScore of(long hardScore, long softScore) {
        return new HardSoftLongScore(0, hardScore, softScore);
    }

    public static HardSoftLongScore ofHard(long hardScore) {
        return of(hardScore, 0L);
    }

    public static HardSoftLongScore ofSoft(long softScore) {
        return of(0L, softScore);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final int initScore;
    private final long hardScore;
    private final long softScore;

    /**
     * Private default constructor for default marshalling/unmarshalling of unknown frameworks that use reflection.
     * Such integration is always inferior to the specialized integration modules, such as
     * optaplanner-persistence-jpa, optaplanner-persistence-jackson, optaplanner-persistence-jaxb, ...
     */
    @SuppressWarnings("unused")
    private HardSoftLongScore() {
        this(Integer.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE);
    }

    private HardSoftLongScore(int initScore, long hardScore, long softScore) {
        this.initScore = initScore;
        this.hardScore = hardScore;
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
    public long hardScore() {
        return hardScore;
    }

    /**
     * As defined by {@link #hardScore()}.
     *
     * @deprecated Use {@link #hardScore()} instead.
     */
    @Deprecated(forRemoval = true)
    public long getHardScore() {
        return hardScore;
    }

    /**
     * The total of the broken negative soft constraints and fulfilled positive soft constraints.
     * Their weight is included in the total.
     * The soft score is usually a negative number because most use cases only have negative constraints.
     * <p>
     * In a normal score comparison, the soft score is irrelevant if the 2 scores don't have the same hard score.
     *
     * @return higher is better, usually negative, 0 if no soft constraints are broken/fulfilled
     */
    public long softScore() {
        return softScore;
    }

    /**
     * As defined by {@link #softScore()}.
     *
     * @deprecated Use {@link #softScore()} instead.
     */
    @Deprecated(forRemoval = true)
    public long getSoftScore() {
        return softScore;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public HardSoftLongScore withInitScore(int newInitScore) {
        return new HardSoftLongScore(newInitScore, hardScore, softScore);
    }

    @Override
    public boolean isFeasible() {
        return initScore >= 0 && hardScore >= 0L;
    }

    @Override
    public HardSoftLongScore add(HardSoftLongScore addend) {
        return new HardSoftLongScore(
                initScore + addend.initScore(),
                hardScore + addend.hardScore(),
                softScore + addend.softScore());
    }

    @Override
    public HardSoftLongScore subtract(HardSoftLongScore subtrahend) {
        return new HardSoftLongScore(
                initScore - subtrahend.initScore(),
                hardScore - subtrahend.hardScore(),
                softScore - subtrahend.softScore());
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
    public HardSoftLongScore abs() {
        return new HardSoftLongScore(Math.abs(initScore), Math.abs(hardScore), Math.abs(softScore));
    }

    @Override
    public HardSoftLongScore zero() {
        return HardSoftLongScore.ZERO;
    }

    @Override
    public Number[] toLevelNumbers() {
        return new Number[] { hardScore, softScore };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof HardSoftLongScore) {
            HardSoftLongScore other = (HardSoftLongScore) o;
            return initScore == other.initScore()
                    && hardScore == other.hardScore()
                    && softScore == other.softScore();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(initScore, hardScore, softScore);
    }

    @Override
    public int compareTo(HardSoftLongScore other) {
        if (initScore != other.initScore()) {
            return Integer.compare(initScore, other.initScore());
        } else if (hardScore != other.hardScore()) {
            return Long.compare(hardScore, other.hardScore());
        } else {
            return Long.compare(softScore, other.softScore());
        }
    }

    @Override
    public String toShortString() {
        return ScoreUtil.buildShortString(this, n -> n.longValue() != 0L, HARD_LABEL, SOFT_LABEL);
    }

    @Override
    public String toString() {
        return ScoreUtil.getInitPrefix(initScore) + hardScore + HARD_LABEL + "/" + softScore + SOFT_LABEL;
    }

}
