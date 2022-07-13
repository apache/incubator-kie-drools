package org.optaplanner.examples.common.experimental.impl;

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.function.BiFunction;

import org.optaplanner.examples.common.experimental.api.IntervalCluster;

final class IntervalClusterImpl<Interval_, Point_ extends Comparable<Point_>, Difference_ extends Comparable<Difference_>>
        implements IntervalCluster<Interval_, Point_, Difference_> {

    private final NavigableSet<IntervalSplitPoint<Interval_, Point_>> splitPointSet;
    private final BiFunction<Point_, Point_, Difference_> differenceFunction;
    private IntervalSplitPoint<Interval_, Point_> startSplitPoint;
    private IntervalSplitPoint<Interval_, Point_> endSplitPoint;

    private int count;
    private boolean hasOverlap;

    IntervalClusterImpl(NavigableSet<IntervalSplitPoint<Interval_, Point_>> splitPointSet,
            BiFunction<Point_, Point_, Difference_> differenceFunction, IntervalSplitPoint<Interval_, Point_> start) {
        if (start == null) {
            throw new IllegalArgumentException("start (" + start + ") is null");
        }
        if (differenceFunction == null) {
            throw new IllegalArgumentException("differenceFunction (" + differenceFunction + ") is null");
        }
        this.splitPointSet = splitPointSet;
        this.startSplitPoint = start;
        this.endSplitPoint = start;
        this.differenceFunction = differenceFunction;
        int activeIntervals = 0;
        count = 0;
        boolean anyOverlap = false;
        IntervalSplitPoint<Interval_, Point_> current = start;
        do {
            count += current.intervalsStartingAtSplitPointSet.size();
            activeIntervals += current.intervalsStartingAtSplitPointSet.size() - current.intervalsEndingAtSplitPointSet.size();
            if (activeIntervals > 1) {
                anyOverlap = true;
            }
            current = splitPointSet.higher(current);
        } while (activeIntervals > 0 && current != null);
        hasOverlap = anyOverlap;

        if (current != null) {
            endSplitPoint = splitPointSet.lower(current);
        } else {
            endSplitPoint = splitPointSet.last();
        }
    }

    IntervalClusterImpl(NavigableSet<IntervalSplitPoint<Interval_, Point_>> splitPointSet,
            BiFunction<Point_, Point_, Difference_> differenceFunction, IntervalSplitPoint<Interval_, Point_> start,
            IntervalSplitPoint<Interval_, Point_> end, int count, boolean hasOverlap) {
        this.splitPointSet = splitPointSet;
        this.startSplitPoint = start;
        this.endSplitPoint = end;
        this.differenceFunction = differenceFunction;
        this.count = count;
        this.hasOverlap = hasOverlap;
    }

    IntervalSplitPoint<Interval_, Point_> getStartSplitPoint() {
        return startSplitPoint;
    }

    IntervalSplitPoint<Interval_, Point_> getEndSplitPoint() {
        return endSplitPoint;
    }

    void addInterval(Interval<Interval_, Point_> interval) {
        if (interval.getEndSplitPoint().compareTo(getStartSplitPoint()) > 0
                && interval.getStartSplitPoint().compareTo(getEndSplitPoint()) < 0) {
            hasOverlap = true;
        }
        if (interval.getStartSplitPoint().compareTo(startSplitPoint) < 0) {
            startSplitPoint = splitPointSet.floor(interval.getStartSplitPoint());
        }
        if (interval.getEndSplitPoint().compareTo(endSplitPoint) > 0) {
            endSplitPoint = splitPointSet.ceiling(interval.getEndSplitPoint());
        }
        count++;
    }

    Iterable<IntervalClusterImpl<Interval_, Point_, Difference_>> removeInterval(Interval<Interval_, Point_> interval) {
        return IntervalClusterIterator::new;
    }

    void mergeIntervalCluster(IntervalClusterImpl<Interval_, Point_, Difference_> laterIntervalCluster) {
        if (endSplitPoint.compareTo(laterIntervalCluster.startSplitPoint) > 0) {
            hasOverlap = true;
        }
        if (endSplitPoint.compareTo(laterIntervalCluster.endSplitPoint) < 0) {
            endSplitPoint = laterIntervalCluster.endSplitPoint;
        }
        count += laterIntervalCluster.count;
        hasOverlap |= laterIntervalCluster.hasOverlap;
    }

    @Override
    public Iterator<Interval_> iterator() {
        return new IntervalTreeIterator<>(splitPointSet.subSet(startSplitPoint, true, endSplitPoint, true));
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public boolean hasOverlap() {
        return hasOverlap;
    }

    @Override
    public Point_ getStart() {
        return startSplitPoint.splitPoint;
    }

    @Override
    public Point_ getEnd() {
        return endSplitPoint.splitPoint;
    }

    @Override
    public Difference_ getLength() {
        return differenceFunction.apply(startSplitPoint.splitPoint, endSplitPoint.splitPoint);
    }

    @Override
    public String toString() {
        return "IntervalCluster{" +
                "startSplitPoint=" + startSplitPoint +
                ", endSplitPoint=" + endSplitPoint +
                ", count=" + count +
                ", hasOverlap=" + hasOverlap +
                '}';
    }

    // TODO: Make this incremental by only checking between the interval's start and end points
    private final class IntervalClusterIterator
            implements Iterator<IntervalClusterImpl<Interval_, Point_, Difference_>> {

        private IntervalSplitPoint<Interval_, Point_> current = getStart(startSplitPoint);

        private IntervalSplitPoint<Interval_, Point_>
                getStart(IntervalSplitPoint<Interval_, Point_> start) {
            while (start != null && start.isEmpty()) {
                start = splitPointSet.higher(start);
            }
            return start;
        }

        @Override
        public boolean hasNext() {
            return current != null && current.compareTo(endSplitPoint) <= 0 && !splitPointSet.isEmpty();
        }

        @Override
        public IntervalClusterImpl<Interval_, Point_, Difference_> next() {
            IntervalSplitPoint<Interval_, Point_> start = current;
            IntervalSplitPoint<Interval_, Point_> end;
            int activeIntervals = 0;
            count = 0;
            boolean anyOverlap = false;
            do {
                count += current.intervalsStartingAtSplitPointSet.size();
                activeIntervals +=
                        current.intervalsStartingAtSplitPointSet.size() - current.intervalsEndingAtSplitPointSet.size();
                if (activeIntervals > 1) {
                    anyOverlap = true;
                }
                current = splitPointSet.higher(current);
            } while (activeIntervals > 0 && current != null);
            hasOverlap = anyOverlap;

            if (current != null) {
                end = splitPointSet.lower(current);
                current = getStart(current);
            } else {
                end = splitPointSet.last();
            }

            return new IntervalClusterImpl<>(splitPointSet, differenceFunction, start, end, count, hasOverlap);
        }
    }
}
