/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.common.experimental;

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Objects;

public class IntervalCluster<IntervalValue_, PointValue_ extends Comparable<PointValue_>> implements Iterable<IntervalValue_> {
    IntervalSplitPoint<IntervalValue_, PointValue_> startSplitPoint;
    IntervalSplitPoint<IntervalValue_, PointValue_> endSplitPoint;

    int count;
    boolean hasOverlap;
    final NavigableSet<IntervalSplitPoint<IntervalValue_, PointValue_>> splitPointSet;

    public IntervalCluster(NavigableSet<IntervalSplitPoint<IntervalValue_, PointValue_>> splitPointSet,
            IntervalSplitPoint<IntervalValue_, PointValue_> start) {
        if (start == null) {
            throw new IllegalArgumentException("start (" + start + ") is null");
        }
        this.splitPointSet = splitPointSet;
        this.startSplitPoint = start;
        int activeIntervals = 0;
        count = 0;
        boolean anyOverlap = false;
        IntervalSplitPoint<IntervalValue_, PointValue_> current = start;
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

    public IntervalCluster(NavigableSet<IntervalSplitPoint<IntervalValue_, PointValue_>> splitPointSet,
            IntervalSplitPoint<IntervalValue_, PointValue_> start,
            IntervalSplitPoint<IntervalValue_, PointValue_> end, int count, boolean hasOverlap) {
        this.splitPointSet = splitPointSet;
        this.startSplitPoint = start;
        this.endSplitPoint = end;
        this.count = count;
        this.hasOverlap = hasOverlap;
    }

    public IntervalSplitPoint<IntervalValue_, PointValue_> getStartSplitPoint() {
        return startSplitPoint;
    }

    public IntervalSplitPoint<IntervalValue_, PointValue_> getEndSplitPoint() {
        return endSplitPoint;
    }

    public void addInterval(Interval<IntervalValue_, PointValue_> interval) {
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

    public Iterable<IntervalCluster<IntervalValue_, PointValue_>>
            removeInterval(Interval<IntervalValue_, PointValue_> interval) {
        // TODO: Make this incremental by only checking between the interval's
        //       start and end points
        return () -> new Iterator<IntervalCluster<IntervalValue_, PointValue_>>() {

            IntervalSplitPoint<IntervalValue_, PointValue_> current = startSplitPoint;

            @Override
            public boolean hasNext() {
                return current != null && current.compareTo(endSplitPoint) < 0 && !splitPointSet.isEmpty();
            }

            @Override
            public IntervalCluster<IntervalValue_, PointValue_> next() {
                IntervalSplitPoint<IntervalValue_, PointValue_> start = current;
                IntervalSplitPoint<IntervalValue_, PointValue_> end;
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
                } else {
                    end = splitPointSet.last();
                }
                return new IntervalCluster<>(splitPointSet, start, end, count, hasOverlap);
            }
        };
    }

    public void mergeIntervalCluster(IntervalCluster<IntervalValue_, PointValue_> laterIntervalCluster) {
        count += laterIntervalCluster.count;
        endSplitPoint = laterIntervalCluster.endSplitPoint;
        hasOverlap |= laterIntervalCluster.hasOverlap;
    }

    public Iterator<IntervalValue_> iterator() {
        return new IntervalTreeIterator<>(splitPointSet.subSet(startSplitPoint, true, endSplitPoint, true));
    }

    public int size() {
        return count;
    }

    public boolean hasOverlap() {
        return hasOverlap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        IntervalCluster<?, ?> that = (IntervalCluster<?, ?>) o;
        return startSplitPoint.equals(that.startSplitPoint) && endSplitPoint.equals(that.endSplitPoint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startSplitPoint, endSplitPoint);
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
}
