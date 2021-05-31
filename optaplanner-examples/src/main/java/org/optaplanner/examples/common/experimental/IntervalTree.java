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
import java.util.TreeSet;
import java.util.function.Function;

public class IntervalTree<IntervalValue_, PointValue_ extends Comparable<PointValue_>> {
    final TreeSet<IntervalSplitPoint<IntervalValue_, PointValue_>> splitPointSet;
    final Function<IntervalValue_, PointValue_> startMapping;
    final Function<IntervalValue_, PointValue_> endMapping;
    final ConsecutiveIntervalData<IntervalValue_, PointValue_> consecutiveIntervalData;

    public IntervalTree(Function<IntervalValue_, PointValue_> startMapping,
            Function<IntervalValue_, PointValue_> endMapping) {
        this.startMapping = startMapping;
        this.endMapping = endMapping;
        splitPointSet = new TreeSet<>();
        consecutiveIntervalData = new ConsecutiveIntervalData<>(splitPointSet);
    }

    private Interval<IntervalValue_, PointValue_> getInterval(IntervalValue_ intervalValue) {
        return new Interval<>(intervalValue, startMapping, endMapping);
    }

    public boolean isEmpty() {
        return splitPointSet.isEmpty();
    }

    public boolean contains(IntervalValue_ o) {
        if (null == o || splitPointSet.isEmpty()) {
            return false;
        }
        Interval<IntervalValue_, PointValue_> interval = getInterval(o);
        IntervalSplitPoint<IntervalValue_, PointValue_> floorStartSplitPoint =
                splitPointSet.floor(interval.getStartSplitPoint());
        if (floorStartSplitPoint == null) {
            return false;
        }
        return floorStartSplitPoint.containsIntervalStarting(interval);
    }

    public Iterator<IntervalValue_> iterator() {
        return new IntervalTreeIterator<>(splitPointSet);
    }

    public boolean add(IntervalValue_ o) {
        Interval<IntervalValue_, PointValue_> interval = getInterval(o);
        IntervalSplitPoint<IntervalValue_, PointValue_> startSplitPoint = interval.getStartSplitPoint();
        IntervalSplitPoint<IntervalValue_, PointValue_> endSplitPoint = interval.getEndSplitPoint();
        boolean isChanged;

        IntervalSplitPoint<IntervalValue_, PointValue_> flooredStartSplitPoint = splitPointSet.floor(startSplitPoint);
        IntervalSplitPoint<IntervalValue_, PointValue_> ceilingEndSplitPoint = splitPointSet.ceiling(endSplitPoint);
        if (flooredStartSplitPoint == null || !flooredStartSplitPoint.equals(startSplitPoint)) {
            splitPointSet.add(startSplitPoint);
            startSplitPoint.createCollections();
            isChanged = startSplitPoint.addIntervalStartingAtSplitPoint(interval);
        } else {
            isChanged = flooredStartSplitPoint.addIntervalStartingAtSplitPoint(interval);
        }

        if (ceilingEndSplitPoint == null || !ceilingEndSplitPoint.equals(endSplitPoint)) {
            splitPointSet.add(endSplitPoint);
            endSplitPoint.createCollections();
            endSplitPoint.addIntervalEndingAtSplitPoint(interval);
        } else {
            ceilingEndSplitPoint.addIntervalEndingAtSplitPoint(interval);
        }

        if (isChanged) {
            consecutiveIntervalData.addInterval(interval);
        }
        return true;
    }

    public boolean remove(IntervalValue_ o) {
        if (null == o) {
            return false;
        }
        Interval<IntervalValue_, PointValue_> interval = getInterval(o);
        IntervalSplitPoint<IntervalValue_, PointValue_> startSplitPoint = interval.getStartSplitPoint();
        IntervalSplitPoint<IntervalValue_, PointValue_> endSplitPoint = interval.getEndSplitPoint();

        IntervalSplitPoint<IntervalValue_, PointValue_> flooredStartSplitPoint = splitPointSet.floor(startSplitPoint);
        if (flooredStartSplitPoint == null || !flooredStartSplitPoint.containsIntervalStarting(interval)) {
            return false;
        }

        flooredStartSplitPoint.removeIntervalStartingAtSplitPoint(interval);
        if (flooredStartSplitPoint.isEmpty()) {
            splitPointSet.remove(flooredStartSplitPoint);
        }

        IntervalSplitPoint<IntervalValue_, PointValue_> ceilEndSplitPoint = splitPointSet.ceiling(endSplitPoint);
        // Not null since the start point contained the interval
        ceilEndSplitPoint.removeIntervalEndingAtSplitPoint(interval);
        if (ceilEndSplitPoint.isEmpty()) {
            splitPointSet.remove(ceilEndSplitPoint);
        }

        consecutiveIntervalData.removalInterval(interval);
        return true;
    }

    public ConsecutiveIntervalData<IntervalValue_, PointValue_> getConsecutiveIntervalData() {
        return consecutiveIntervalData;
    }
}
