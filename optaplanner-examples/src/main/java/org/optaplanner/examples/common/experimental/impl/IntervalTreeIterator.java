package org.optaplanner.examples.common.experimental.impl;

import java.util.Iterator;

final class IntervalTreeIterator<Interval_, Point_ extends Comparable<Point_>> implements Iterator<Interval_> {

    private final Iterator<IntervalSplitPoint<Interval_, Point_>> splitPointSetIterator;
    private Iterator<Interval_> splitPointValueIterator;

    IntervalTreeIterator(Iterable<IntervalSplitPoint<Interval_, Point_>> splitPointSet) {
        this.splitPointSetIterator = splitPointSet.iterator();
        if (splitPointSetIterator.hasNext()) {
            splitPointValueIterator = splitPointSetIterator.next().getValuesStartingFromSplitPointIterator();
        }
    }

    @Override
    public boolean hasNext() {
        return splitPointValueIterator != null && splitPointValueIterator.hasNext();
    }

    @Override
    public Interval_ next() {
        Interval_ next = splitPointValueIterator.next();

        while (!splitPointValueIterator.hasNext() && splitPointSetIterator.hasNext()) {
            splitPointValueIterator = splitPointSetIterator.next().getValuesStartingFromSplitPointIterator();
        }

        if (!splitPointValueIterator.hasNext()) {
            splitPointValueIterator = null;
        }

        return next;
    }
}
