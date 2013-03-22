/*
 * Copyright 2011 JBoss Inc
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

/**
 * This {@link Score} is based on 1 level of double constraints.
 * <p/>
 * This class is immutable.
 * @see Score
 */
public final class SimpleDoubleScore extends AbstractScore<SimpleDoubleScore> {

    public static SimpleDoubleScore parseScore(String scoreString) {
        return valueOf(Double.parseDouble(scoreString));
    }

    public static SimpleDoubleScore valueOf(double score) {
        return new SimpleDoubleScore(score);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final double score;

    private SimpleDoubleScore(double score) {
        this.score = score;
    }

    /**
     * The total of the broken negative constraints and fulfilled positive hard constraints.
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

    public SimpleDoubleScore add(SimpleDoubleScore augment) {
        return new SimpleDoubleScore(score + augment.getScore());
    }

    public SimpleDoubleScore subtract(SimpleDoubleScore subtrahend) {
        return new SimpleDoubleScore(score - subtrahend.getScore());
    }

    public SimpleDoubleScore multiply(double multiplicand) {
        return new SimpleDoubleScore(score * multiplicand);
    }

    public SimpleDoubleScore divide(double divisor) {
        return new SimpleDoubleScore(score / divisor);
    }

    public double[] toDoubleLevels() {
        return new double[]{score};
    }

    public boolean equals(Object o) {
        // A direct implementation (instead of EqualsBuilder) to avoid dependencies
        if (this == o) {
            return true;
        } else if (o instanceof SimpleDoubleScore) {
            SimpleDoubleScore other = (SimpleDoubleScore) o;
            return score == other.getScore();
        } else {
            return false;
        }
    }

    public int hashCode() {
        // A direct implementation (instead of HashCodeBuilder) to avoid dependencies
        return (17 * 37) + Double.valueOf(score).hashCode();
    }

    public int compareTo(SimpleDoubleScore other) {
        // A direct implementation (instead of CompareToBuilder) to avoid dependencies
        if (score < other.getScore()) {
            return -1;
        } else if (score > other.getScore()) {
            return 1;
        } else {
            return 0;
        }
    }

    public String toString() {
        return Double.toString(score);
    }

}
