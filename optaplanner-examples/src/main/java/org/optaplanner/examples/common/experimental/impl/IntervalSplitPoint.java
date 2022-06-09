package org.optaplanner.examples.common.experimental.impl;

import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.IntStream;

public class IntervalSplitPoint<Interval_, Point_ extends Comparable<Point_>>
        implements Comparable<IntervalSplitPoint<Interval_, Point_>> {
    final Point_ splitPoint;
    Map<Interval_, Integer> startIntervalToCountMap;
    Map<Interval_, Integer> endIntervalToCountMap;
    TreeSet<Interval<Interval_, Point_>> intervalsStartingAtSplitPointSet;
    TreeSet<Interval<Interval_, Point_>> intervalsEndingAtSplitPointSet;

    public IntervalSplitPoint(Point_ splitPoint) {
        this.splitPoint = splitPoint;
    }

    protected void createCollections() {
        startIntervalToCountMap = new IdentityHashMap<>();
        endIntervalToCountMap = new IdentityHashMap<>();
        intervalsStartingAtSplitPointSet = new TreeSet<>(
                Comparator.<Interval<Interval_, Point_>, Point_> comparing(Interval::getEnd)
                        .thenComparingInt(interval -> System.identityHashCode(interval.getValue())));
        intervalsEndingAtSplitPointSet = new TreeSet<>(
                Comparator.<Interval<Interval_, Point_>, Point_> comparing(Interval::getStart)
                        .thenComparingInt(interval -> System.identityHashCode(interval.getValue())));
    }

    public boolean addIntervalStartingAtSplitPoint(Interval<Interval_, Point_> interval) {
        startIntervalToCountMap.merge(interval.getValue(), 1, Integer::sum);
        return intervalsStartingAtSplitPointSet.add(interval);
    }

    public void removeIntervalStartingAtSplitPoint(Interval<Interval_, Point_> interval) {
        Integer newCount = startIntervalToCountMap.computeIfPresent(interval.getValue(), (key, count) -> {
            if (count > 1) {
                return count - 1;
            }
            return null;
        });
        if (null == newCount) {
            intervalsStartingAtSplitPointSet.remove(interval);
        }
    }

    public boolean addIntervalEndingAtSplitPoint(Interval<Interval_, Point_> interval) {
        endIntervalToCountMap.merge(interval.getValue(), 1, Integer::sum);
        return intervalsEndingAtSplitPointSet.add(interval);
    }

    public void removeIntervalEndingAtSplitPoint(Interval<Interval_, Point_> interval) {
        Integer newCount = endIntervalToCountMap.computeIfPresent(interval.getValue(), (key, count) -> {
            if (count > 1) {
                return count - 1;
            }
            return null;
        });
        if (null == newCount) {
            intervalsEndingAtSplitPointSet.remove(interval);
        }
    }

    public boolean containsIntervalStarting(Interval<Interval_, Point_> interval) {
        return intervalsStartingAtSplitPointSet.contains(interval);
    }

    public boolean containsIntervalEnding(Interval<Interval_, Point_> interval) {
        return intervalsEndingAtSplitPointSet.contains(interval);
    }

    public Iterator<Interval_> getValuesStartingFromSplitPointIterator() {
        return intervalsStartingAtSplitPointSet.stream()
                .flatMap(interval -> IntStream.range(0, startIntervalToCountMap.get(interval.getValue()))
                        .mapToObj(index -> interval.getValue()))
                .iterator();
    }

    public boolean isEmpty() {
        return intervalsStartingAtSplitPointSet.isEmpty() && intervalsEndingAtSplitPointSet.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        IntervalSplitPoint<?, ?> that = (IntervalSplitPoint<?, ?>) o;
        return splitPoint.equals(that.splitPoint);
    }

    public boolean isBefore(IntervalSplitPoint<Interval_, Point_> other) {
        return compareTo(other) < 0;
    }

    public boolean isAfter(IntervalSplitPoint<Interval_, Point_> other) {
        return compareTo(other) > 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(splitPoint);
    }

    @Override
    public int compareTo(IntervalSplitPoint<Interval_, Point_> other) {
        return splitPoint.compareTo(other.splitPoint);
    }

    @Override
    public String toString() {
        return splitPoint.toString();
    }
}
