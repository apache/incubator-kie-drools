/*
 * Copyright 2010 JBoss Inc
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
 * <p/>
 * This class is immutable.
 * @see Score
 */
public final class SimpleScore extends AbstractScore<SimpleScore> {

    public static SimpleScore parseScore(String scoreString) {
        return valueOf(Integer.parseInt(scoreString));
    }

    public static SimpleScore valueOf(int score) {
        return new SimpleScore(score);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final int score;

    private SimpleScore(int score) {
        this.score = score;
    }

    /**
     * The total of the broken negative constraints and fulfilled positive hard constraints.
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

    public SimpleScore add(SimpleScore augment) {
        return new SimpleScore(score + augment.getScore());
    }

    public SimpleScore subtract(SimpleScore subtrahend) {
        return new SimpleScore(score - subtrahend.getScore());
    }

    public SimpleScore multiply(double multiplicand) {
        return new SimpleScore((int) Math.floor(score * multiplicand));
    }

    public SimpleScore divide(double divisor) {
        return new SimpleScore((int) Math.floor(score / divisor));
    }

    public double[] toDoubleLevels() {
        return new double[]{score};
    }

    public boolean equals(Object o) {
        // A direct implementation (instead of EqualsBuilder) to avoid dependencies
        if (this == o) {
            return true;
        } else if (o instanceof SimpleScore) {
            SimpleScore other = (SimpleScore) o;
            return score == other.getScore();
        } else {
            return false;
        }
    }

    public int hashCode() {
        // A direct implementation (instead of HashCodeBuilder) to avoid dependencies
        return (17 * 37) + score;
    }

    public int compareTo(SimpleScore other) {
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
        return Integer.toString(score);
    }

}
