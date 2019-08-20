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

package org.optaplanner.core.api.score.buildin.simplelong;

import org.optaplanner.core.api.score.AbstractScore;
import org.optaplanner.core.api.score.Score;

/**
 * This {@link Score} is based on 1 level of long constraints.
 * <p>
 * This class is immutable.
 * @see Score
 */
public final class SimpleLongScore extends AbstractScore<SimpleLongScore> {

    public static final SimpleLongScore ZERO = new SimpleLongScore(0, 0L);
    public static final SimpleLongScore ONE = new SimpleLongScore(0, 1L);

    public static SimpleLongScore parseScore(String scoreString) {
        String[] scoreTokens = parseScoreTokens(SimpleLongScore.class, scoreString, "");
        int initScore = parseInitScore(SimpleLongScore.class, scoreString, scoreTokens[0]);
        long score = parseLevelAsLong(SimpleLongScore.class, scoreString, scoreTokens[1]);
        return ofUninitialized(initScore, score);
    }

    public static SimpleLongScore ofUninitialized(int initScore, long score) {
        return new SimpleLongScore(initScore, score);
    }

    /**
     * @deprecated in favor of {@link #ofUninitialized(int, long)}
     */
    @Deprecated
    public static SimpleLongScore valueOfUninitialized(int initScore, long score) {
        return new SimpleLongScore(initScore, score);
    }

    public static SimpleLongScore of(long score) {
        return new SimpleLongScore(0, score);
    }

    /**
     * @deprecated in favor of {@link #of(long)}
     */
    @Deprecated
    public static SimpleLongScore valueOf(long score) {
        return new SimpleLongScore(0, score);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final long score;

    /**
     * Private default constructor for default marshalling/unmarshalling of unknown frameworks that use reflection.
     * Such integration is always inferior to the specialized integration modules, such as
     * optaplanner-persistence-jpa, optaplanner-persistence-xstream, optaplanner-persistence-jaxb, ...
     */
    @SuppressWarnings("unused")
    private SimpleLongScore() {
        super(Integer.MIN_VALUE);
        score = Long.MIN_VALUE;
    }

    private SimpleLongScore(int initScore, long score) {
        super(initScore);
        this.score = score;
    }

    /**
     * The total of the broken negative constraints and fulfilled positive constraints.
     * Their weight is included in the total.
     * The score is usually a negative number because most use cases only have negative constraints.
     * @return higher is better, usually negative, 0 if no constraints are broken/fulfilled
     */
    public long getScore() {
        return score;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public SimpleLongScore toInitializedScore() {
        return initScore == 0 ? this : new SimpleLongScore(0, score);
    }

    @Override
    public SimpleLongScore withInitScore(int newInitScore) {
        assertNoInitScore();
        return new SimpleLongScore(newInitScore, score);
    }

    @Override
    public SimpleLongScore add(SimpleLongScore addend) {
        return new SimpleLongScore(
                initScore + addend.getInitScore(),
                score + addend.getScore());
    }

    @Override
    public SimpleLongScore subtract(SimpleLongScore subtrahend) {
        return new SimpleLongScore(
                initScore - subtrahend.getInitScore(),
                score - subtrahend.getScore());
    }

    @Override
    public SimpleLongScore multiply(double multiplicand) {
        return new SimpleLongScore(
                (int) Math.floor(initScore * multiplicand),
                (long) Math.floor(score * multiplicand));
    }

    @Override
    public SimpleLongScore divide(double divisor) {
        return new SimpleLongScore(
                (int) Math.floor(initScore / divisor),
                (long) Math.floor(score / divisor));
    }

    @Override
    public SimpleLongScore power(double exponent) {
        return new SimpleLongScore(
                (int) Math.floor(Math.pow(initScore, exponent)),
                (long) Math.floor(Math.pow(score, exponent)));
    }

    @Override
    public SimpleLongScore negate() {
        return new SimpleLongScore(-initScore, -score);
    }

    @Override
    public Number[] toLevelNumbers() {
        return new Number[]{score};
    }

    @Override
    public boolean equals(Object o) {
        // A direct implementation (instead of EqualsBuilder) to avoid dependencies
        if (this == o) {
            return true;
        } else if (o instanceof SimpleLongScore) {
            SimpleLongScore other = (SimpleLongScore) o;
            return initScore == other.getInitScore()
                    && score == other.getScore();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        // A direct implementation (instead of HashCodeBuilder) to avoid dependencies
        return ((17 * 37)
                + initScore) * 37
                + Long.valueOf(score).hashCode();
    }

    @Override
    public int compareTo(SimpleLongScore other) {
        // A direct implementation (instead of CompareToBuilder) to avoid dependencies
        if (initScore != other.getInitScore()) {
            return initScore < other.getInitScore() ? -1 : 1;
        } else {
            return Long.compare(score, other.getScore());
        }
    }

    @Override
    public String toShortString() {
        return buildShortString((n) -> ((Long) n).longValue() != 0L, "");
    }

    @Override
    public String toString() {
        return getInitPrefix() + score;
    }

    @Override
    public boolean isCompatibleArithmeticArgument(Score otherScore) {
        return otherScore instanceof SimpleLongScore;
    }

}
