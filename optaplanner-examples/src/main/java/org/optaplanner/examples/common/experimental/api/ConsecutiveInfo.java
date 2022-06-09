package org.optaplanner.examples.common.experimental.api;

/**
 * Contains info regarding the consecutive sequences and breaks
 * in a collection of points.
 *
 * @param <Value_> The type of value in the sequence
 * @param <Difference_> The type of difference between values in the sequence
 */
public interface ConsecutiveInfo<Value_, Difference_ extends Comparable<Difference_>> {
    /**
     * @return never null, an iterable that iterates through the sequences contained in
     *         the collection in ascending order
     */
    Iterable<Sequence<Value_, Difference_>> getConsecutiveSequences();

    /**
     * @return never null, an iterable that iterates through the breaks contained in
     *         the collection in ascending order
     */
    Iterable<Break<Value_, Difference_>> getBreaks();
}
