package org.optaplanner.examples.common.experimental.impl;

import org.optaplanner.examples.common.experimental.api.IntervalBreak;
import org.optaplanner.examples.common.experimental.api.IntervalCluster;

final class IntervalBreakImpl<Interval_, Point_ extends Comparable<Point_>, Difference_ extends Comparable<Difference_>>
        implements IntervalBreak<Interval_, Point_, Difference_> {
    private IntervalCluster<Interval_, Point_, Difference_> previousCluster;
    private IntervalCluster<Interval_, Point_, Difference_> nextCluster;
    private Difference_ length;

    IntervalBreakImpl(IntervalCluster<Interval_, Point_, Difference_> previousCluster,
            IntervalCluster<Interval_, Point_, Difference_> nextCluster, Difference_ length) {
        this.previousCluster = previousCluster;
        this.nextCluster = nextCluster;
        this.length = length;
    }

    @Override
    public IntervalCluster<Interval_, Point_, Difference_> getPreviousIntervalCluster() {
        return previousCluster;
    }

    @Override
    public IntervalCluster<Interval_, Point_, Difference_> getNextIntervalCluster() {
        return nextCluster;
    }

    @Override
    public Difference_ getLength() {
        return length;
    }

    void setPreviousCluster(IntervalCluster<Interval_, Point_, Difference_> previousCluster) {
        this.previousCluster = previousCluster;
    }

    void setNextCluster(IntervalCluster<Interval_, Point_, Difference_> nextCluster) {
        this.nextCluster = nextCluster;
    }

    void setLength(Difference_ length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "IntervalBreak{" +
                "previousCluster=" + previousCluster +
                ", nextCluster=" + nextCluster +
                ", length=" + length +
                '}';
    }
}
