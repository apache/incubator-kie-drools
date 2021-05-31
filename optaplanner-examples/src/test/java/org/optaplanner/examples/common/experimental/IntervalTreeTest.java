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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TreeSet;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

public class IntervalTreeTest {
    private static class Interval {
        final int start;
        final int end;

        public Interval(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Interval interval = (Interval) o;
            return start == interval.start && end == interval.end;
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, end);
        }

        @Override
        public String toString() {
            return "(" + start + ", " + end + ")";
        }
    }

    private IntervalTree<Interval, Integer> getIntegerIntervalTree() {
        return new IntervalTree<>(Interval::getStart, Interval::getEnd);
    }

    @Test
    public void testNonConsecutiveIntervals() {
        IntervalTree<Interval, Integer> tree = getIntegerIntervalTree();
        tree.add(new Interval(0, 2));
        tree.add(new Interval(3, 4));
        tree.add(new Interval(5, 7));

        Iterable<IntervalCluster<Interval, Integer>> clusterList = tree.getConsecutiveIntervalData().getIntervalClusters();
        assertThat(clusterList).hasSize(3);
        Iterator<IntervalCluster<Interval, Integer>> iterator = clusterList.iterator();
        IntervalCluster<Interval, Integer> intervalCluster = iterator.next();

        assertThat(intervalCluster).containsExactly(new Interval(0, 2));
        assertThat(intervalCluster.hasOverlap()).isFalse();

        intervalCluster = iterator.next();
        assertThat(intervalCluster).containsExactly(new Interval(3, 4));
        assertThat(intervalCluster.hasOverlap()).isFalse();

        intervalCluster = iterator.next();
        assertThat(intervalCluster).containsExactly(new Interval(5, 7));
        assertThat(intervalCluster.hasOverlap()).isFalse();
    }

    @Test
    public void testConsecutiveIntervals() {
        IntervalTree<Interval, Integer> tree = getIntegerIntervalTree();
        tree.add(new Interval(0, 2));
        tree.add(new Interval(2, 4));
        tree.add(new Interval(4, 7));

        Iterable<IntervalCluster<Interval, Integer>> clusterList = tree.getConsecutiveIntervalData().getIntervalClusters();
        assertThat(clusterList).hasSize(1);
        Iterator<IntervalCluster<Interval, Integer>> iterator = clusterList.iterator();
        IntervalCluster<Interval, Integer> intervalCluster = iterator.next();

        assertThat(intervalCluster).containsExactly(new Interval(0, 2), new Interval(2, 4), new Interval(4, 7));
    }

    @Test
    public void testDuplicateIntervals() {
        IntervalTree<Interval, Integer> tree = getIntegerIntervalTree();
        Interval a = new Interval(0, 2);
        Interval b = new Interval(4, 7);
        tree.add(a);
        tree.add(a);
        tree.add(b);

        Iterable<IntervalCluster<Interval, Integer>> clusterList = tree.getConsecutiveIntervalData().getIntervalClusters();
        assertThat(clusterList).hasSize(2);
        Iterator<IntervalCluster<Interval, Integer>> iterator = clusterList.iterator();
        IntervalCluster<Interval, Integer> intervalCluster = iterator.next();

        assertThat(intervalCluster).containsExactly(a, a);

        intervalCluster = iterator.next();
        assertThat(intervalCluster).containsExactly(b);
    }

    @Test
    public void testIntervalRemoval() {
        IntervalTree<Interval, Integer> tree = getIntegerIntervalTree();
        Interval a = new Interval(0, 2);
        Interval b = new Interval(2, 4);
        Interval c = new Interval(4, 7);
        tree.add(a);
        tree.add(b);
        tree.add(c);

        tree.remove(b);

        Iterable<IntervalCluster<Interval, Integer>> clusterList = tree.getConsecutiveIntervalData().getIntervalClusters();
        assertThat(clusterList).hasSize(2);
        Iterator<IntervalCluster<Interval, Integer>> iterator = clusterList.iterator();
        IntervalCluster<Interval, Integer> intervalCluster = iterator.next();

        assertThat(intervalCluster).containsExactly(new Interval(0, 2));

        intervalCluster = iterator.next();
        assertThat(intervalCluster).containsExactly(new Interval(4, 7));
    }

    @Test
    public void testOverlappingInterval() {
        IntervalTree<Interval, Integer> tree = getIntegerIntervalTree();
        Interval a = new Interval(0, 2);
        Interval b = new Interval(1, 3);
        Interval c = new Interval(2, 4);

        Interval d = new Interval(5, 6);

        Interval e = new Interval(7, 9);
        Interval f = new Interval(7, 9);

        tree.add(a);
        tree.add(b);
        tree.add(c);
        tree.add(d);
        tree.add(e);
        tree.add(f);

        Iterable<IntervalCluster<Interval, Integer>> clusterList = tree.getConsecutiveIntervalData().getIntervalClusters();
        assertThat(clusterList).hasSize(3);
        Iterator<IntervalCluster<Interval, Integer>> iterator = clusterList.iterator();
        IntervalCluster<Interval, Integer> intervalCluster = iterator.next();

        assertThat(intervalCluster).containsExactly(a, b, c);
        assertThat(intervalCluster.hasOverlap()).isTrue();
        intervalCluster = iterator.next();

        assertThat(intervalCluster).containsExactly(d);
        assertThat(intervalCluster.hasOverlap()).isFalse();

        intervalCluster = iterator.next();
        assertThat(intervalCluster).containsExactly(e, f);
        assertThat(intervalCluster.hasOverlap()).isTrue();

        tree.remove(b);

        clusterList = tree.getConsecutiveIntervalData().getIntervalClusters();
        assertThat(clusterList).hasSize(3);
        iterator = clusterList.iterator();
        intervalCluster = iterator.next();

        assertThat(intervalCluster).containsExactly(a, c);
        assertThat(intervalCluster.hasOverlap()).isFalse();

        intervalCluster = iterator.next();
        assertThat(intervalCluster).containsExactly(d);
        assertThat(intervalCluster.hasOverlap()).isFalse();

        intervalCluster = iterator.next();
        assertThat(intervalCluster).containsExactly(e, f);
        assertThat(intervalCluster.hasOverlap()).isTrue();

        tree.remove(f);
        clusterList = tree.getConsecutiveIntervalData().getIntervalClusters();
        assertThat(clusterList).hasSize(3);
        iterator = clusterList.iterator();
        intervalCluster = iterator.next();

        assertThat(intervalCluster).containsExactly(a, c);
        assertThat(intervalCluster.hasOverlap()).isFalse();

        intervalCluster = iterator.next();
        assertThat(intervalCluster).containsExactly(d);
        assertThat(intervalCluster.hasOverlap()).isFalse();

        intervalCluster = iterator.next();
        assertThat(intervalCluster).containsExactly(e);
        assertThat(intervalCluster.hasOverlap()).isFalse();

        Interval g = new Interval(6, 7);
        tree.add(g);
        clusterList = tree.getConsecutiveIntervalData().getIntervalClusters();
        assertThat(clusterList).hasSize(2);
        iterator = clusterList.iterator();
        intervalCluster = iterator.next();

        assertThat(intervalCluster).containsExactly(a, c);
        assertThat(intervalCluster.hasOverlap()).isFalse();

        intervalCluster = iterator.next();
        assertThat(intervalCluster).containsExactly(d, g, e);
        assertThat(intervalCluster.hasOverlap()).isFalse();
    }

    // Compare the mutable version with the recompute version
    @Test
    public void testRandomIntervals() {
        Random random = new Random(1);
        Map<Interval, Interval> intervalToInstanceMap = new HashMap<>();
        TreeSet<IntervalSplitPoint<Interval, Integer>> splitPoints = new TreeSet<>();
        IntervalTree<Interval, Integer> tree = new IntervalTree<>(Interval::getStart, Interval::getEnd);
        for (int i = 0; i < 10000; i++) {
            // Create a random interval
            int from = random.nextInt(50);
            int to = from + random.nextInt(50);
            Interval interval = intervalToInstanceMap.computeIfAbsent(new Interval(from, to), Function.identity());
            org.optaplanner.examples.common.experimental.Interval<Interval, Integer> treeInterval =
                    new org.optaplanner.examples.common.experimental.Interval<>(interval, Interval::getStart, Interval::getEnd);
            splitPoints.add(treeInterval.getStartSplitPoint());
            splitPoints.add(treeInterval.getEndSplitPoint());

            // Get the split points from the set (since those split points have collections)
            IntervalSplitPoint<Interval, Integer> startSplitPoint = splitPoints.floor(treeInterval.getStartSplitPoint());
            IntervalSplitPoint<Interval, Integer> endSplitPoint = splitPoints.floor(treeInterval.getEndSplitPoint());

            // Create the collections if they do not exist
            if (startSplitPoint.startIntervalToCountMap == null) {
                startSplitPoint.createCollections();
            }
            if (endSplitPoint.endIntervalToCountMap == null) {
                endSplitPoint.createCollections();
            }

            // Either add or remove the interval
            if (startSplitPoint.containsIntervalStarting(treeInterval) && random.nextBoolean()) {
                startSplitPoint.removeIntervalStartingAtSplitPoint(treeInterval);
                endSplitPoint.removeIntervalEndingAtSplitPoint(treeInterval);
                if (startSplitPoint.isEmpty()) {
                    splitPoints.remove(startSplitPoint);
                }
                if (endSplitPoint.isEmpty()) {
                    splitPoints.remove(endSplitPoint);
                }
                tree.remove(interval);
            } else {
                startSplitPoint.addIntervalStartingAtSplitPoint(treeInterval);
                endSplitPoint.addIntervalEndingAtSplitPoint(treeInterval);
                tree.add(interval);
            }

            // Recompute all interval clusters
            IntervalSplitPoint<Interval, Integer> current = splitPoints.first();
            List<IntervalCluster<Interval, Integer>> intervalClusterList = new ArrayList<>();
            while (current != null) {
                intervalClusterList.add(new IntervalCluster<>(splitPoints, current));
                current = splitPoints.higher(intervalClusterList.get(intervalClusterList.size() - 1).getEndSplitPoint());
            }

            // Verify the mutable version matches the recompute version
            assertThat(tree.getConsecutiveIntervalData().getIntervalClusters()).containsExactlyElementsOf(intervalClusterList);
        }
    }

}
