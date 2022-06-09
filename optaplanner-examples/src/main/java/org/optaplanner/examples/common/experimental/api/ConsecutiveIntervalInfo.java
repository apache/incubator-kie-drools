package org.optaplanner.examples.common.experimental.api;

public interface ConsecutiveIntervalInfo<Interval_, Point_ extends Comparable<Point_>, Difference_ extends Comparable<Difference_>> {

    /**
     * @return never null, an iterable that iterates through the interval clusters
     *         contained in the collection in ascending order
     */
    Iterable<IntervalCluster<Interval_, Point_, Difference_>> getIntervalClusters();

    /**
     * @return never null, an iterable that iterates through the breaks contained in
     *         the collection in ascending order
     */
    Iterable<IntervalBreak<Interval_, Point_, Difference_>> getBreaks();
}
