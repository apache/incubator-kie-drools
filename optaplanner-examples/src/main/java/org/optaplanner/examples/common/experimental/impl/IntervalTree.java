package org.optaplanner.examples.common.experimental.impl;

import java.util.Iterator;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class IntervalTree<Interval_, Point_ extends Comparable<Point_>, Difference_ extends Comparable<Difference_>> {

    private final Function<Interval_, Point_> startMapping;
    private final Function<Interval_, Point_> endMapping;
    private final TreeSet<IntervalSplitPoint<Interval_, Point_>> splitPointSet;
    private final ConsecutiveIntervalInfoImpl<Interval_, Point_, Difference_> consecutiveIntervalData;

    public IntervalTree(Function<Interval_, Point_> startMapping, Function<Interval_, Point_> endMapping,
            BiFunction<Point_, Point_, Difference_> differenceFunction) {
        this.startMapping = startMapping;
        this.endMapping = endMapping;
        this.splitPointSet = new TreeSet<>();
        this.consecutiveIntervalData = new ConsecutiveIntervalInfoImpl<>(splitPointSet, differenceFunction);
    }

    public Interval<Interval_, Point_> getInterval(Interval_ intervalValue) {
        return new Interval<>(intervalValue, startMapping, endMapping);
    }

    public boolean isEmpty() {
        return splitPointSet.isEmpty();
    }

    public boolean contains(Interval_ o) {
        if (null == o || splitPointSet.isEmpty()) {
            return false;
        }
        Interval<Interval_, Point_> interval = getInterval(o);
        IntervalSplitPoint<Interval_, Point_> floorStartSplitPoint =
                splitPointSet.floor(interval.getStartSplitPoint());
        if (floorStartSplitPoint == null) {
            return false;
        }
        return floorStartSplitPoint.containsIntervalStarting(interval);
    }

    public Iterator<Interval_> iterator() {
        return new IntervalTreeIterator<>(splitPointSet);
    }

    public boolean add(Interval<Interval_, Point_> interval) {
        IntervalSplitPoint<Interval_, Point_> startSplitPoint = interval.getStartSplitPoint();
        IntervalSplitPoint<Interval_, Point_> endSplitPoint = interval.getEndSplitPoint();
        boolean isChanged;

        IntervalSplitPoint<Interval_, Point_> flooredStartSplitPoint = splitPointSet.floor(startSplitPoint);
        if (flooredStartSplitPoint == null || !flooredStartSplitPoint.equals(startSplitPoint)) {
            splitPointSet.add(startSplitPoint);
            startSplitPoint.createCollections();
            isChanged = startSplitPoint.addIntervalStartingAtSplitPoint(interval);
        } else {
            isChanged = flooredStartSplitPoint.addIntervalStartingAtSplitPoint(interval);
        }

        IntervalSplitPoint<Interval_, Point_> ceilingEndSplitPoint = splitPointSet.ceiling(endSplitPoint);
        if (ceilingEndSplitPoint == null || !ceilingEndSplitPoint.equals(endSplitPoint)) {
            splitPointSet.add(endSplitPoint);
            endSplitPoint.createCollections();
            isChanged |= endSplitPoint.addIntervalEndingAtSplitPoint(interval);
        } else {
            isChanged |= ceilingEndSplitPoint.addIntervalEndingAtSplitPoint(interval);
        }

        if (isChanged) {
            consecutiveIntervalData.addInterval(interval);
        }
        return true;
    }

    public boolean remove(Interval<Interval_, Point_> interval) {
        IntervalSplitPoint<Interval_, Point_> startSplitPoint = interval.getStartSplitPoint();
        IntervalSplitPoint<Interval_, Point_> endSplitPoint = interval.getEndSplitPoint();

        IntervalSplitPoint<Interval_, Point_> flooredStartSplitPoint = splitPointSet.floor(startSplitPoint);
        if (flooredStartSplitPoint == null || !flooredStartSplitPoint.containsIntervalStarting(interval)) {
            return false;
        }

        flooredStartSplitPoint.removeIntervalStartingAtSplitPoint(interval);
        if (flooredStartSplitPoint.isEmpty()) {
            splitPointSet.remove(flooredStartSplitPoint);
        }

        IntervalSplitPoint<Interval_, Point_> ceilEndSplitPoint = splitPointSet.ceiling(endSplitPoint);
        // Not null since the start point contained the interval
        ceilEndSplitPoint.removeIntervalEndingAtSplitPoint(interval);
        if (ceilEndSplitPoint.isEmpty()) {
            splitPointSet.remove(ceilEndSplitPoint);
        }

        consecutiveIntervalData.removeInterval(interval);
        return true;
    }

    public ConsecutiveIntervalInfoImpl<Interval_, Point_, Difference_> getConsecutiveIntervalData() {
        return consecutiveIntervalData;
    }
}
