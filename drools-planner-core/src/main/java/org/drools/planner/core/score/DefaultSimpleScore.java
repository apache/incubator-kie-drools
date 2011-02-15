/**
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

package org.drools.planner.core.score;

/**
 * Default implementation of {@link SimpleScore}.
 * <p/>
 * This class is immutable.
 * @see SimpleScore
 * @author Geoffrey De Smet
 */
public final class DefaultSimpleScore extends AbstractScore<SimpleScore>
        implements SimpleScore {

    public static DefaultSimpleScore parseScore(String scoreString) {
        return valueOf(Integer.parseInt(scoreString));
    }

    public static DefaultSimpleScore valueOf(int score) {
        return new DefaultSimpleScore(score);
    }

    private final int score;

    public DefaultSimpleScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public SimpleScore add(SimpleScore augment) {
        return new DefaultSimpleScore(this.score + augment.getScore());
    }

    public SimpleScore subtract(SimpleScore subtrahend) {
        return new DefaultSimpleScore(this.score - subtrahend.getScore());
    }

    public SimpleScore multiply(double multiplicand) {
        return new DefaultSimpleScore((int) Math.floor(this.score * multiplicand));
    }

    public SimpleScore divide(double divisor) {
        return new DefaultSimpleScore((int) Math.floor(this.score / divisor));
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
