package org.optaplanner.examples.common.experimental.impl;

import java.util.function.Function;

public final class Interval<Interval_, Point_ extends Comparable<Point_>> {
    private final Interval_ value;
    private final IntervalSplitPoint<Interval_, Point_> startSplitPoint;
    private final IntervalSplitPoint<Interval_, Point_> endSplitPoint;

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
