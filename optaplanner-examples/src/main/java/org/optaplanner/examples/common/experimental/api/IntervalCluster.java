package org.optaplanner.examples.common.experimental.api;

public interface IntervalCluster<Interval_, Point_ extends Comparable<Point_>, Difference_ extends Comparable<Difference_>>
        extends Iterable<Interval_> {
    int size();

    boolean hasOverlap();

    Difference_ getLength();

    Point_ getStart();

    Point_ getEnd();
}
