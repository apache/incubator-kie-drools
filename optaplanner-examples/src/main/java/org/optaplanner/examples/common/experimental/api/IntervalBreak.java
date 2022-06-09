package org.optaplanner.examples.common.experimental.api;

/**
 * An IntervalBreak is a gap between two consecutive interval clusters. For instance,
 * the list [(1,3),(2,4),(3,5),(7,8)] has a break of length 2 between 5 and 7.
 *
 * @param <Interval_> The type of value in the sequence
 * @param <Difference_> The type of difference between values in the sequence
 */
public interface IntervalBreak<Interval_, Point_ extends Comparable<Point_>, Difference_ extends Comparable<Difference_>> {
    /**
     * @return never null, the interval cluster leading directly into this
     */
    IntervalCluster<Interval_, Point_, Difference_> getPreviousIntervalCluster();

    /**
     * @return never null, the interval cluster immediately following this
     */
    IntervalCluster<Interval_, Point_, Difference_> getNextIntervalCluster();

    /**
     * Return the end of the sequence before this break. For the
     * break between 6 and 10, this will return 6.
     *
     * @return never null, the item this break is directly after
     */
    default Point_ getPreviousIntervalClusterEnd() {
        return getPreviousIntervalCluster().getEnd();
    };

    /**
     * Return the start of the sequence after this break. For the
     * break between 6 and 10, this will return 10.
     *
     * @return never null, the item this break is directly before
     */
    default Point_ getNextIntervalClusterStart() {
        return getNextIntervalCluster().getStart();
    }

    /**
     * Return the length of the break, which is the difference
     * between {@link #getNextIntervalClusterStart()} and {@link #getPreviousIntervalClusterEnd()}. For the
     * break between 6 and 10, this will return 4.
     *
     * @return never null, the length of this break
     */
    Difference_ getLength();
}
