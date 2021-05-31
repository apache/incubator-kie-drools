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

import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.ObjectUtils;

public class ConsecutiveIntervalData<IntervalValue_, PointValue_ extends Comparable<PointValue_>> {
    private final NavigableMap<IntervalSplitPoint<IntervalValue_, PointValue_>, IntervalCluster<IntervalValue_, PointValue_>> clusterStartSplitPointToCluster;
    private final NavigableSet<IntervalSplitPoint<IntervalValue_, PointValue_>> splitPointSet;
    private final Iterable<IntervalCluster<IntervalValue_, PointValue_>> valueList;

    public ConsecutiveIntervalData(TreeSet<IntervalSplitPoint<IntervalValue_, PointValue_>> splitPointSet) {
        clusterStartSplitPointToCluster = new TreeMap<>();
        valueList = new MapValuesIterable<>(clusterStartSplitPointToCluster);
        this.splitPointSet = splitPointSet;
    }

    protected void addInterval(Interval<IntervalValue_, PointValue_> interval) {
        NavigableMap<IntervalSplitPoint<IntervalValue_, PointValue_>, IntervalCluster<IntervalValue_, PointValue_>> intersectedIntervalClusterMap =
                clusterStartSplitPointToCluster.subMap(
                        ObjectUtils.defaultIfNull(clusterStartSplitPointToCluster.floorKey(interval.getStartSplitPoint()),
                                interval.getStartSplitPoint()),
                        true, interval.getEndSplitPoint(), true);

        // Case: the interval cluster before this interval does not intersect this interval
        if (!intersectedIntervalClusterMap.isEmpty()
                && intersectedIntervalClusterMap.get(intersectedIntervalClusterMap.firstKey()).getEndSplitPoint()
                        .compareTo(interval.getStartSplitPoint()) < 0) {
            intersectedIntervalClusterMap = intersectedIntervalClusterMap.subMap(intersectedIntervalClusterMap.firstKey(),
                    false, intersectedIntervalClusterMap.lastKey(), true);
        }

        if (intersectedIntervalClusterMap.isEmpty()) {
            IntervalSplitPoint<IntervalValue_, PointValue_> start = splitPointSet.floor(interval.getStartSplitPoint());
            clusterStartSplitPointToCluster.put(start, new IntervalCluster<>(splitPointSet, start));
            return;
        }
        IntervalCluster<IntervalValue_, PointValue_> intervalCluster =
                intersectedIntervalClusterMap.get(intersectedIntervalClusterMap.firstKey());
        IntervalSplitPoint<IntervalValue_, PointValue_> oldStart = intervalCluster.getStartSplitPoint();
        intervalCluster.addInterval(interval);
        intersectedIntervalClusterMap.tailMap(intersectedIntervalClusterMap.firstKey(), false).values()
                .forEach(intervalCluster::mergeIntervalCluster);
        intersectedIntervalClusterMap.tailMap(intersectedIntervalClusterMap.firstKey(), false).clear();
        if (oldStart.compareTo(intervalCluster.getStartSplitPoint()) > 0) {
            clusterStartSplitPointToCluster.remove(oldStart);
            clusterStartSplitPointToCluster.put(intervalCluster.getStartSplitPoint(), intervalCluster);
        }
    }

    protected void removalInterval(Interval<IntervalValue_, PointValue_> interval) {
        Map.Entry<IntervalSplitPoint<IntervalValue_, PointValue_>, IntervalCluster<IntervalValue_, PointValue_>> intervalClusterEntry =
                clusterStartSplitPointToCluster.floorEntry(interval.getStartSplitPoint());
        IntervalCluster<IntervalValue_, PointValue_> intervalCluster = intervalClusterEntry.getValue();
        clusterStartSplitPointToCluster.remove(intervalClusterEntry.getKey());
        for (IntervalCluster<IntervalValue_, PointValue_> newIntervalCluster : intervalCluster.removeInterval(interval)) {
            clusterStartSplitPointToCluster.put(newIntervalCluster.getStartSplitPoint(), newIntervalCluster);
        }
    }

    public Iterable<IntervalCluster<IntervalValue_, PointValue_>> getIntervalClusters() {
        return valueList;
    }

    @Override
    public String toString() {
        return "ConsecutiveIntervalData{" +
                "valueList=" + valueList +
                '}';
    }
}
