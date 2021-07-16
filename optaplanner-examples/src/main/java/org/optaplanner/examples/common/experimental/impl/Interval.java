/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.common.experimental.impl;

import java.util.function.Function;

public class Interval<Interval_, Point_ extends Comparable<Point_>> {
    final Interval_ value;
    final IntervalSplitPoint<Interval_, Point_> startSplitPoint;
    final IntervalSplitPoint<Interval_, Point_> endSplitPoint;

    public Interval(Interval_ value, Function<Interval_, Point_> startMapping,
            Function<Interval_, Point_> endMapping) {
        this.value = value;
        Point_ start = startMapping.apply(value);
        Point_ end = endMapping.apply(value);
        this.startSplitPoint = new IntervalSplitPoint<>(start);
        if (start == end) {
            this.endSplitPoint = this.startSplitPoint;
        } else {
            this.endSplitPoint = new IntervalSplitPoint<>(end);
        }
    }

    public Interval_ getValue() {
        return value;
    }

    public Point_ getStart() {
        return startSplitPoint.splitPoint;
    }

    public Point_ getEnd() {
        return endSplitPoint.splitPoint;
    }

    public IntervalSplitPoint<Interval_, Point_> getStartSplitPoint() {
        return startSplitPoint;
    }

    public IntervalSplitPoint<Interval_, Point_> getEndSplitPoint() {
        return endSplitPoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Interval<?, ?> that = (Interval<?, ?>) o;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(value);
    }

    @Override
    public String toString() {
        return "Interval{" +
                "value=" + value +
                ", start=" + getStart() +
                ", end=" + getEnd() +
                '}';
    }
}
