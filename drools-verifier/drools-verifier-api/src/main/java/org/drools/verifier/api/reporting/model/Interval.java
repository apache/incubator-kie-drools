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

package org.drools.verifier.api.reporting.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO add to Errai marhalled list
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

    private final List<Integer> originColumns = new ArrayList<Integer>(); // This is only usable in DMN validation

    private final Bound<?> lowerBound;
    private final Bound<?> upperBound;
    private final int rule;
    private final int col;

    public Interval(Range.RangeBoundary lowBoundary, Comparable<?> start, Comparable<?> end, Range.RangeBoundary highBoundary, int rule, int col) {
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
        return (lowerBound.getBoundaryType() == Range.RangeBoundary.OPEN ? "(" : "[") +
                " " + Bound.boundValueToString(lowerBound.getValue()) +
                " .. " + Bound.boundValueToString(upperBound.getValue()) +
                " " + (upperBound.getBoundaryType() == Range.RangeBoundary.OPEN ? ")" : "]");
    }

    public Bound<?> getLowerBound() {
        return lowerBound;
    }

    public Bound<?> getUpperBound() {
        return upperBound;
    }

    public int getRule() {
        return rule;
    }

    public int getCol() {
        return col;
    }

    public List<Integer> getOriginColumns() {
        return originColumns;
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
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Interval other = (Interval) obj;
        if (lowerBound == null) {
            if (other.lowerBound != null) {
                return false;
            }
        } else if (!lowerBound.equals(other.lowerBound)) {
            return false;
        }
        if (upperBound == null) {
            if (other.upperBound != null) {
                return false;
            }
        } else if (!upperBound.equals(other.upperBound)) {
            return false;
        }
        return true;
    }

    public boolean includes(Interval o) {
        return this.lowerBound.compareTo((Bound) o.lowerBound) <= 0 && this.upperBound.compareTo((Bound) o.upperBound) >= 0;
    }

    public boolean leftAdjOrOverlap(Interval o) {
        boolean thisLeftLower = this.lowerBound.compareTo((Bound) o.lowerBound) <= 0;
        boolean oRightHigher = o.upperBound.compareTo((Bound) this.upperBound) >= 0;
        boolean chained = BoundValueComparator.compareValueDispatchingToInf(o.lowerBound, this.upperBound) < 0;
        boolean adj = Bound.adOrOver(o.lowerBound, this.upperBound);
        return thisLeftLower && oRightHigher && (chained || adj);
    }

    public static Range.RangeBoundary invertBoundary(Range.RangeBoundary b) {
        if (b == Range.RangeBoundary.OPEN) {
            return Range.RangeBoundary.CLOSED;
        } else if (b == Range.RangeBoundary.CLOSED) {
            return Range.RangeBoundary.OPEN;
        } else {
            throw new IllegalStateException("invertBoundary for: " + b);
        }
    }

    public static List<Interval> invertOverDomain(Interval interval, Interval domain) {
        List<Interval> results = new ArrayList<>();
        if (!domain.lowerBound.equals(interval.lowerBound)) {
            Interval left = new Interval(domain.lowerBound.getBoundaryType(),
                                         domain.lowerBound.getValue(),
                                         interval.lowerBound.getValue(),
                                         invertBoundary(interval.lowerBound.getBoundaryType()),
                                         interval.rule,
                                         interval.col);
            results.add(left);
        }
        if (!domain.upperBound.equals(interval.upperBound)) {
            Interval right = new Interval(invertBoundary(interval.upperBound.getBoundaryType()),
                                          interval.upperBound.getValue(),
                                          domain.upperBound.getValue(),
                                          domain.upperBound.getBoundaryType(),
                                          interval.rule,
                                          interval.col);
            results.add(right);
        }
        return results;
    }

    public static List<Interval> flatten(List<Interval> intervals) {
        List<Interval> results = new ArrayList<>();
        List<Bound> bounds = intervals.stream().flatMap(i -> Stream.of(i.getLowerBound(), i.getUpperBound())).collect(Collectors.toList());
        Collections.sort(bounds);
        Deque<Bound> stack = new ArrayDeque<Bound>();
        Interval candidate = null;
        for (Bound cur : bounds) {
            if (stack.isEmpty() && !cur.isLowerBound()) {
                throw new RuntimeException("Inconsistent sort of bounds.");
            }
            if (cur.isLowerBound()) {
                if (candidate == null) {
                    stack.push(cur);
                } else {
                    if (Bound.adOrOver(candidate.upperBound, cur)) {
                        stack.push(candidate.lowerBound);
                        candidate = null;
                    } else {
                        results.add(candidate);
                        stack.push(cur);
                    }
                }
            } else if (cur.isUpperBound()) {
                Bound pop = stack.pop();
                if (stack.isEmpty()) {
                    candidate = Interval.newFromBounds(pop, cur);
                }
            } else {
                throw new RuntimeException("Inconsistent value for bounds.");
            }
        }
        if (candidate != null) {
            results.add(candidate);
        }
        return results;
    }
}
