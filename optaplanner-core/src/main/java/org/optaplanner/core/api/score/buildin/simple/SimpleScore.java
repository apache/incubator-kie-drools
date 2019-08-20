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

package org.optaplanner.core.api.score.buildin.simple;

import org.optaplanner.core.api.score.AbstractScore;
import org.optaplanner.core.api.score.Score;

/**
 * This {@link Score} is based on 1 level of int constraints.
 * <p>
 * This class is immutable.
 * @see Score
 */
public final class SimpleScore extends AbstractScore<SimpleScore> {

    public static final SimpleScore ZERO = new SimpleScore(0, 0);
    public static final SimpleScore ONE = new SimpleScore(0, 1);

    public static SimpleScore parseScore(String scoreString) {
        String[] scoreTokens = parseScoreTokens(SimpleScore.class, scoreString, "");
        int initScore = parseInitScore(SimpleScore.class, scoreString, scoreTokens[0]);
        int score = parseLevelAsInt(SimpleScore.class, scoreString, scoreTokens[1]);
        return ofUninitialized(initScore, score);
    }

    public static SimpleScore ofUninitialized(int initScore, int score) {
        return new SimpleScore(initScore, score);
    }

    /**
     * @deprecated in favor of {@link #ofUninitialized(int, int)}
     */
    @Deprecated
    public static SimpleScore valueOfUninitialized(int initScore, int score) {
        return new SimpleScore(initScore, score);
    }

    public static SimpleScore of(int score) {
        return new SimpleScore(0, score);
    }

    /**
     * @deprecated in favor of {@link #of(int)}
     */
    @Deprecated
    public static SimpleScore valueOf(int score) {
        return new SimpleScore(0, score);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final int score;

    /**
     * Private default constructor for default marshalling/unmarshalling of unknown frameworks that use reflection.
     * Such integration is always inferior to the specialized integration modules, such as
     * optaplanner-persistence-jpa, optaplanner-persistence-xstream, optaplanner-persistence-jaxb, ...
     */
    @SuppressWarnings("unused")
    private SimpleScore() {
        super(Integer.MIN_VALUE);
        score = Integer.MIN_VALUE;
    }

    private SimpleScore(int initScore, int score) {
        super(initScore);
        this.score = score;
    }

    /**
     * The total of the broken negative constraints and fulfilled positive constraints.
     * Their weight is included in the total.
     * The score is usually a negative number because most use cases only have negative constraints.
     * @return higher is better, usually negative, 0 if no constraints are broken/fulfilled
     */
    public int getScore() {
        return score;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public SimpleScore toInitializedScore() {
        return initScore == 0 ? this : new SimpleScore(0, score);
    }

    @Override
    public SimpleScore withInitScore(int newInitScore) {
        assertNoInitScore();
        return new SimpleScore(newInitScore, score);
    }

    @Override
    public SimpleScore add(SimpleScore addend) {
        return new SimpleScore(
                initScore + addend.getInitScore(),
                score + addend.getScore());
    }

    @Override
    public SimpleScore subtract(SimpleScore subtrahend) {
        return new SimpleScore(
                initScore - subtrahend.getInitScore(),
                score - subtrahend.getScore());
    }

    @Override
    public SimpleScore multiply(double multiplicand) {
        return new SimpleScore(
                (int) Math.floor(initScore * multiplicand),
                (int) Math.floor(score * multiplicand));
    }

    @Override
    public SimpleScore divide(double divisor) {
        return new SimpleScore(
                (int) Math.floor(initScore / divisor),
                (int) Math.floor(score / divisor));
    }

    @Override
    public SimpleScore power(double exponent) {
        return new SimpleScore(
                (int) Math.floor(Math.pow(initScore, exponent)),
                (int) Math.floor(Math.pow(score, exponent)));
    }

    @Override
    public SimpleScore negate() {
        return new SimpleScore(-initScore, -score);
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
        } else if (o instanceof SimpleScore) {
            SimpleScore other = (SimpleScore) o;
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
                + score;
    }

    @Override
    public int compareTo(SimpleScore other) {
        // A direct implementation (instead of CompareToBuilder) to avoid dependencies
        if (initScore != other.getInitScore()) {
            return initScore < other.getInitScore() ? -1 : 1;
        } else {
            return Integer.compare(score, other.getScore());
        }
    }

    @Override
    public String toShortString() {
        return buildShortString((n) -> ((Integer) n).intValue() != 0, "");
    }

    @Override
    public String toString() {
        return getInitPrefix() + score;
    }

    @Override
    public boolean isCompatibleArithmeticArgument(Score otherScore) {
        return otherScore instanceof SimpleScore;
    }

}
