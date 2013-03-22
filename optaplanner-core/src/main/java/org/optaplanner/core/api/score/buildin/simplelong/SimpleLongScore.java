/*
 * Copyright 2013 JBoss Inc
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
 * <p/>
 * This class is immutable.
 * @see Score
 */
public final class SimpleLongScore extends AbstractScore<SimpleLongScore> {

    public static SimpleLongScore parseScore(String scoreString) {
        return valueOf(Long.parseLong(scoreString));
    }

    public static SimpleLongScore valueOf(long score) {
        return new SimpleLongScore(score);
    }

    // ************************************************************************
    // Fields
    // ************************************************************************

    private final long score;

    private SimpleLongScore(long score) {
        this.score = score;
    }

    /**
     * The total of the broken negative constraints and fulfilled positive hard constraints.
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

    public SimpleLongScore add(SimpleLongScore augment) {
        return new SimpleLongScore(score + augment.getScore());
    }

    public SimpleLongScore subtract(SimpleLongScore subtrahend) {
        return new SimpleLongScore(score - subtrahend.getScore());
    }

    public SimpleLongScore multiply(double multiplicand) {
        return new SimpleLongScore((long) Math.floor(score * multiplicand));
    }

    public SimpleLongScore divide(double divisor) {
        return new SimpleLongScore((long) Math.floor(score / divisor));
    }

    public double[] toDoubleLevels() {
        return new double[]{score};
    }

    public boolean equals(Object o) {
        // A direct implementation (instead of EqualsBuilder) to avoid dependencies
        if (this == o) {
            return true;
        } else if (o instanceof SimpleLongScore) {
            SimpleLongScore other = (SimpleLongScore) o;
            return score == other.getScore();
        } else {
            return false;
        }
    }

    public int hashCode() {
        // A direct implementation (instead of HashCodeBuilder) to avoid dependencies
        return (17 * 37) + Long.valueOf(score).hashCode();
    }

    public int compareTo(SimpleLongScore other) {
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
        return Long.toString(score);
    }

}
