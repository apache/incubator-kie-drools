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

package org.optaplanner.examples.common.experimental;

import java.util.function.Function;

public class Interval<IntervalValue_, PointValue_ extends Comparable<PointValue_>> {
    final IntervalValue_ value;
    final IntervalSplitPoint<IntervalValue_, PointValue_> startSplitPoint;
    final IntervalSplitPoint<IntervalValue_, PointValue_> endSplitPoint;

    public Interval(IntervalValue_ value, Function<IntervalValue_, PointValue_> startMapping,
            Function<IntervalValue_, PointValue_> endMapping) {
        this.value = value;
        this.startSplitPoint = new IntervalSplitPoint<>(startMapping.apply(value));
        this.endSplitPoint = new IntervalSplitPoint<>(endMapping.apply(value));
    }

    public IntervalValue_ getValue() {
        return value;
    }

    public PointValue_ getStart() {
        return startSplitPoint.splitPoint;
    }

    public PointValue_ getEnd() {
        return endSplitPoint.splitPoint;
    }

    public IntervalSplitPoint<IntervalValue_, PointValue_> getStartSplitPoint() {
        return startSplitPoint;
    }

    public IntervalSplitPoint<IntervalValue_, PointValue_> getEndSplitPoint() {
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
