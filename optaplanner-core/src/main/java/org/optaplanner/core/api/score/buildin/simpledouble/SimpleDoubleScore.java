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

package org.optaplanner.core.api.score.buildin.simpledouble;

import org.optaplanner.core.api.score.AbstractScore;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;

/**
 * This {@link Score} is based on 1 level of double constraints.
 * <p>
 * WARNING: NOT RECOMMENDED TO USE DUE TO ROUNDING ERRORS THAT CAUSE SCORE CORRUPTION.
 * For example, this prints false: <code>System.out.println((0.01 + 0.05) == (0.01 + 0.02 + 0.03));</code>
 * Use {@link SimpleBigDecimalScore} instead.
 * <p>
 * This class is immutable.
 * @see Score
 */
public final class SimpleDoubleScore extends AbstractScore<SimpleDoubleScore> {

    public static final SimpleDoubleScore ZERO = new SimpleDoubleScore(0, 0.0);
    public static final SimpleDoubleScore ONE = new SimpleDoubleScore(0, 1.0);

    public static SimpleDoubleScore parseScore(String scoreString) {
        String[] scoreTokens = parseScoreTokens(SimpleDoubleScore.class, scoreString, "");
        int initScore = parseInitScore(SimpleDoubleScore.class, scoreString, scoreTokens[0]);
        double score = parseLevelAsDouble(SimpleDoubleScore.class, scoreString, scoreTokens[1]);
        return ofUninitialized(initScore, score);
    }

    public static SimpleDoubleScore ofUninitialized(int initScore, double score) {
        return new SimpleDoubleScore(initScore, score);
    }

    /**
     * @deprecated in favor of {@link #ofUninitialized(int, double)}
     */
    @Deprecated
    public static SimpleDoubleScore valueOfUninitialized(int initScore, double score) {
        return new SimpleDoubleScore(initScore, score);
    }

    public static SimpleDoubleScore of(double score) {
        return new SimpleDoubleScore(0, score);
    }

    /**
     * @deprecated in favor of {@link #of(double)}
     */
    @Deprecated
    public static SimpleDoubleScore valueOf(double score) {
        return new SimpleDoubleScore(0, score);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final double score;

    /**
     * Private default constructor for default marshalling/unmarshalling of unknown frameworks that use reflection.
     * Such integration is always inferior to the specialized integration modules, such as
     * optaplanner-persistence-jpa, optaplanner-persistence-xstream, optaplanner-persistence-jaxb, ...
     */
    @SuppressWarnings("unused")
    private SimpleDoubleScore() {
        super(Integer.MIN_VALUE);
        score = Double.NaN;
    }

    private SimpleDoubleScore(int initScore, double score) {
        super(initScore);
        this.score = score;
    }

    /**
     * The total of the broken negative constraints and fulfilled positive constraints.
     * Their weight is included in the total.
     * The score is usually a negative number because most use cases only have negative constraints.
     * @return higher is better, usually negative, 0 if no constraints are broken/fulfilled
     */
    public double getScore() {
        return score;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public SimpleDoubleScore toInitializedScore() {
        return initScore == 0 ? this : new SimpleDoubleScore(0, score);
    }

    @Override
    public SimpleDoubleScore withInitScore(int newInitScore) {
        assertNoInitScore();
        return new SimpleDoubleScore(newInitScore, score);
    }

    @Override
    public SimpleDoubleScore add(SimpleDoubleScore addend) {
        return new SimpleDoubleScore(
                initScore + addend.getInitScore(),
                score + addend.getScore());
    }

    @Override
    public SimpleDoubleScore subtract(SimpleDoubleScore subtrahend) {
        return new SimpleDoubleScore(
                initScore - subtrahend.getInitScore(),
                score - subtrahend.getScore());
    }

    @Override
    public SimpleDoubleScore multiply(double multiplicand) {
        return new SimpleDoubleScore(
                (int) Math.floor(initScore * multiplicand),
                score * multiplicand);
    }

    @Override
    public SimpleDoubleScore divide(double divisor) {
        return new SimpleDoubleScore(
                (int) Math.floor(initScore / divisor),
                score / divisor);
    }

    @Override
    public SimpleDoubleScore power(double exponent) {
        return new SimpleDoubleScore(
                (int) Math.floor(Math.pow(initScore, exponent)),
                Math.pow(score, exponent));
    }

    @Override
    public SimpleDoubleScore negate() {
        return new SimpleDoubleScore(-initScore, -score);
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
        } else if (o instanceof SimpleDoubleScore) {
            SimpleDoubleScore other = (SimpleDoubleScore) o;
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
                + Double.valueOf(score).hashCode();
    }

    @Override
    public int compareTo(SimpleDoubleScore other) {
        // A direct implementation (instead of CompareToBuilder) to avoid dependencies
        if (initScore != other.getInitScore()) {
            return initScore < other.getInitScore() ? -1 : 1;
        } else {
            return Double.compare(score, other.getScore());
        }
    }

    @Override
    public String toShortString() {
        return buildShortString((n) -> ((Double) n).doubleValue() != 0.0, "");
    }

    @Override
    public String toString() {
        return getInitPrefix() + score;
    }

    @Override
    public boolean isCompatibleArithmeticArgument(Score otherScore) {
        return otherScore instanceof SimpleDoubleScore;
    }

}
