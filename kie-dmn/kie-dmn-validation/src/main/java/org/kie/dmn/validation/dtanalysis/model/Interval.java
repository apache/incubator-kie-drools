/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.validation.dtanalysis.model;

import org.kie.dmn.feel.runtime.Range.RangeBoundary;

public class Interval {

    public static final Comparable<?> POS_INF = new Comparable<Object>() {

        @Override
        public int compareTo(Object o) {
            return o == this ? 0 : +1;
        }

        @Override
        public String toString() {
            return "+Inf";
        }
    };

    public static final Comparable<?> NEG_INF = new Comparable<Object>() {

        @Override
        public int compareTo(Object o) {
            return o == this ? 0 : -1;
        }

        @Override
        public String toString() {
            return "-Inf";
        }
    };

    private final Bound<?> lowerBound;
    private final Bound<?> upperBound;
    private final long rule;
    private final long col;

    public Interval(RangeBoundary lowBoundary, Comparable<?> start, Comparable<?> end, RangeBoundary highBoundary, long rule, long col) {
        this.lowerBound = new Bound(start, lowBoundary, this);
        this.upperBound = new Bound(end, highBoundary, this);
        this.rule = rule;
        this.col = col;
    }

    private Interval(Bound<?> lowerBound, Bound<?> upperBound) {
        this.lowerBound = new Bound(lowerBound.getValue(), lowerBound.getBoundaryType(), this);
        this.upperBound = new Bound(upperBound.getValue(), upperBound.getBoundaryType(), this);
        this.rule = 0;
        this.col = 0;
    }

    public static Interval newFromBounds(Bound<?> lowerBound, Bound<?> upperBound) {
        return new Interval(lowerBound, upperBound);
    }

    @Override
    public String toString() {
        return (lowerBound.getBoundaryType() == RangeBoundary.OPEN ? "(" : "[") +
               " " + Bound.boundValueToString(lowerBound.getValue()) +
               " .. " + Bound.boundValueToString(upperBound.getValue()) +
               " " + (upperBound.getBoundaryType() == RangeBoundary.OPEN ? ")" : "]");
    }

    public Bound<?> getLowerBound() {
        return lowerBound;
    }

    public Bound<?> getUpperBound() {
        return upperBound;
    }

    public long getRule() {
        return rule;
    }

    public long getCol() {
        return col;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lowerBound == null) ? 0 : lowerBound.hashCode());
        result = prime * result + ((upperBound == null) ? 0 : upperBound.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Interval other = (Interval) obj;
        if (lowerBound == null) {
            if (other.lowerBound != null)
                return false;
        } else if (!lowerBound.equals(other.lowerBound))
            return false;
        if (upperBound == null) {
            if (other.upperBound != null)
                return false;
        } else if (!upperBound.equals(other.upperBound))
            return false;
        return true;
    }

    public boolean includes(Interval o) {
        return this.lowerBound.compareTo((Bound) o.lowerBound) <= 0 && this.upperBound.compareTo((Bound) o.upperBound) >= 0;
    }
}
