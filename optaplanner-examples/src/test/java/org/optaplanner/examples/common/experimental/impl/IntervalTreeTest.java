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

package org.optaplanner.examples.common.experimental.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.optaplanner.examples.common.experimental.api.IntervalBreak;
import org.optaplanner.examples.common.experimental.api.IntervalCluster;

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

    private IntervalTree<Interval, Integer, Integer> getIntegerIntervalTree() {
        return new IntervalTree<>(Interval::getStart, Interval::getEnd, (a, b) -> b - a);
    }

    @Test
    public void testNonConsecutiveIntervals() {
        IntervalTree<Interval, Integer, Integer> tree = getIntegerIntervalTree();
        tree.add(new Interval(0, 2));
        tree.add(new Interval(3, 4));
        tree.add(new Interval(5, 7));

        IterableList<IntervalCluster<Interval, Integer, Integer>> clusterList =
                new IterableList<>(tree.getConsecutiveIntervalData().getIntervalClusters());
        assertThat(clusterList).hasSize(3);

        assertThat(clusterList.get(0)).containsExactly(new Interval(0, 2));
        assertThat(clusterList.get(0).hasOverlap()).isFalse();

        assertThat(clusterList.get(1)).containsExactly(new Interval(3, 4));
        assertThat(clusterList.get(1).hasOverlap()).isFalse();

        assertThat(clusterList.get(2)).containsExactly(new Interval(5, 7));
        assertThat(clusterList.get(2).hasOverlap()).isFalse();

        verifyBreaks(tree);
    }

    @Test
    public void testConsecutiveIntervals() {
        IntervalTree<Interval, Integer, Integer> tree = getIntegerIntervalTree();
        tree.add(new Interval(0, 2));
        tree.add(new Interval(2, 4));
        tree.add(new Interval(4, 7));

        IterableList<IntervalCluster<Interval, Integer, Integer>> clusterList =
                new IterableList<>(tree.getConsecutiveIntervalData().getIntervalClusters());
        assertThat(clusterList).hasSize(1);

        assertThat(clusterList.get(0)).containsExactly(new Interval(0, 2), new Interval(2, 4), new Interval(4, 7));
        verifyBreaks(tree);
    }

    @Test
    public void testDuplicateIntervals() {
        IntervalTree<Interval, Integer, Integer> tree = getIntegerIntervalTree();
        Interval a = new Interval(0, 2);
        Interval b = new Interval(4, 7);
        tree.add(a);
        tree.add(a);
        tree.add(b);

        IterableList<IntervalCluster<Interval, Integer, Integer>> clusterList =
                new IterableList<>(tree.getConsecutiveIntervalData().getIntervalClusters());
        assertThat(clusterList).hasSize(2);

        assertThat(clusterList.get(0)).containsExactly(a, a);
        assertThat(clusterList.get(1)).containsExactly(b);
        verifyBreaks(tree);
    }

    @Test
    public void testIntervalRemoval() {
        IntervalTree<Interval, Integer, Integer> tree = getIntegerIntervalTree();
        Interval a = new Interval(0, 2);
        Interval b = new Interval(2, 4);
        Interval c = new Interval(4, 7);
        tree.add(a);
        tree.add(b);
        tree.add(c);

        tree.remove(b);

        IterableList<IntervalCluster<Interval, Integer, Integer>> clusterList =
                new IterableList<>(tree.getConsecutiveIntervalData().getIntervalClusters());
        assertThat(clusterList).hasSize(2);

        assertThat(clusterList.get(0)).containsExactly(new Interval(0, 2));
        assertThat(clusterList.get(1)).containsExactly(new Interval(4, 7));
        verifyBreaks(tree);
    }

    @Test
    public void testOverlappingInterval() {
        IntervalTree<Interval, Integer, Integer> tree = getIntegerIntervalTree();
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

        IterableList<IntervalCluster<Interval, Integer, Integer>> clusterList =
                new IterableList<>(tree.getConsecutiveIntervalData().getIntervalClusters());
        assertThat(clusterList).hasSize(3);

        assertThat(clusterList.get(0)).containsExactly(a, b, c);
        assertThat(clusterList.get(0).hasOverlap()).isTrue();

        assertThat(clusterList.get(1)).containsExactly(d);
        assertThat(clusterList.get(1).hasOverlap()).isFalse();

        assertThat(clusterList.get(2)).containsExactly(e, f);
        assertThat(clusterList.get(2).hasOverlap()).isTrue();

        verifyBreaks(tree);
        tree.remove(b);

        clusterList = new IterableList<>(tree.getConsecutiveIntervalData().getIntervalClusters());
        assertThat(clusterList).hasSize(3);

        assertThat(clusterList.get(0)).containsExactly(a, c);
        assertThat(clusterList.get(0).hasOverlap()).isFalse();

        assertThat(clusterList.get(1)).containsExactly(d);
        assertThat(clusterList.get(1).hasOverlap()).isFalse();

        assertThat(clusterList.get(2)).containsExactly(e, f);
        assertThat(clusterList.get(2).hasOverlap()).isTrue();

        verifyBreaks(tree);
        tree.remove(f);
        clusterList = new IterableList<>(tree.getConsecutiveIntervalData().getIntervalClusters());
        assertThat(clusterList).hasSize(3);

        assertThat(clusterList.get(0)).containsExactly(a, c);
        assertThat(clusterList.get(0).hasOverlap()).isFalse();

        assertThat(clusterList.get(1)).containsExactly(d);
        assertThat(clusterList.get(1).hasOverlap()).isFalse();

        assertThat(clusterList.get(2)).containsExactly(e);
        assertThat(clusterList.get(2).hasOverlap()).isFalse();

        verifyBreaks(tree);
        Interval g = new Interval(6, 7);
        tree.add(g);
        clusterList = new IterableList<>(tree.getConsecutiveIntervalData().getIntervalClusters());
        assertThat(clusterList).hasSize(2);

        assertThat(clusterList.get(0)).containsExactly(a, c);
        assertThat(clusterList.get(0).hasOverlap()).isFalse();

        assertThat(clusterList.get(1)).containsExactly(d, g, e);
        assertThat(clusterList.get(1).hasOverlap()).isFalse();
    }

    public void verifyBreaks(IntervalTree<Interval, Integer, Integer> tree) {
        IterableList<IntervalCluster<Interval, Integer, Integer>> clusterList =
                new IterableList<>(tree.getConsecutiveIntervalData().getIntervalClusters());
        IterableList<IntervalBreak<Interval, Integer, Integer>> breakList =
                new IterableList<>(tree.getConsecutiveIntervalData().getBreaks());

        assertThat(breakList).hasSize(clusterList.size() - 1);
        for (int i = 0; i < clusterList.size() - 1; i++) {
            assertThat(breakList.get(i).getPreviousIntervalCluster()).isSameAs(clusterList.get(i));
            assertThat(breakList.get(i).getNextIntervalCluster()).isSameAs(clusterList.get(i + 1));
            assertThat(breakList.get(i).getPreviousIntervalClusterEnd()).isEqualTo(clusterList.get(i).getEnd());
            assertThat(breakList.get(i).getNextIntervalClusterStart()).isEqualTo(clusterList.get(i + 1).getStart());
            assertThat(breakList.get(i).getLength()).isEqualTo(clusterList.get(i + 1).getStart() - clusterList.get(i).getEnd());
        }
    }

    // Compare the mutable version with the recompute version
    @Test
    public void testRandomIntervals() {
        Random random = new Random(1);

        for (int i = 0; i < 100; i++) {
            Map<Interval, Interval> intervalToInstanceMap = new HashMap<>();
            TreeSet<IntervalSplitPoint<Interval, Integer>> splitPoints = new TreeSet<>();
            IntervalTree<Interval, Integer, Integer> tree =
                    new IntervalTree<>(Interval::getStart, Interval::getEnd, (a, b) -> b - a);
            for (int j = 0; j < 100; j++) {
                // Create a random interval
                String old = formatIntervalTree(tree);
                int from = random.nextInt(5);
                int to = from + random.nextInt(5);
                Interval interval = intervalToInstanceMap.computeIfAbsent(new Interval(from, to), Function.identity());
                org.optaplanner.examples.common.experimental.impl.Interval<Interval, Integer> treeInterval =
                        new org.optaplanner.examples.common.experimental.impl.Interval<>(interval, Interval::getStart,
                                Interval::getEnd);
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
                String op;
                if (startSplitPoint.containsIntervalStarting(treeInterval) && random.nextBoolean()) {
                    op = "Remove";
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
                    op = "Add";
                    startSplitPoint.addIntervalStartingAtSplitPoint(treeInterval);
                    endSplitPoint.addIntervalEndingAtSplitPoint(treeInterval);
                    tree.add(interval);
                }

                // Recompute all interval clusters
                IntervalSplitPoint<Interval, Integer> previous = null;
                IntervalSplitPoint<Interval, Integer> current = splitPoints.isEmpty() ? null : splitPoints.first();
                List<IntervalClusterImpl<Interval, Integer, Integer>> intervalClusterList = new ArrayList<>();
                List<IntervalBreakImpl<Interval, Integer, Integer>> breakList = new ArrayList<>();
                while (current != null) {
                    intervalClusterList.add(new IntervalClusterImpl<>(splitPoints, (a, b) -> a - b, current));
                    if (previous != null) {
                        IntervalClusterImpl<Interval, Integer, Integer> before =
                                intervalClusterList.get(intervalClusterList.size() - 2);
                        IntervalClusterImpl<Interval, Integer, Integer> after =
                                intervalClusterList.get(intervalClusterList.size() - 1);
                        breakList.add(new IntervalBreakImpl<>(before, after, after.getStart() - before.getEnd()));
                    }
                    previous = current;
                    current = splitPoints.higher(intervalClusterList.get(intervalClusterList.size() - 1).getEndSplitPoint());
                }

                // Verify the mutable version matches the recompute version
                assertThat(tree.getConsecutiveIntervalData().getIntervalClusters())
                        .as(op + " interval " + interval + " to " + old).containsExactlyElementsOf(intervalClusterList);
                assertThat(tree.getConsecutiveIntervalData().getBreaks()).as(op + " interval " + interval + " to " + old)
                        .containsExactlyElementsOf(breakList);
            }
        }
    }

    private String formatIntervalTree(IntervalTree<Interval, Integer, Integer> intervalTree) {
        List<List<Interval>> listOfIntervalClusters = new ArrayList<>();
        for (IntervalCluster<Interval, Integer, Integer> cluster : intervalTree.getConsecutiveIntervalData()
                .getIntervalClusters()) {
            List<Interval> intervalsInCluster = new ArrayList<>();
            for (Interval interval : cluster) {
                intervalsInCluster.add(interval);
            }
            listOfIntervalClusters.add(intervalsInCluster);
        }
        return listOfIntervalClusters.stream()
                .map(cluster -> cluster.stream().map(Interval::toString).collect(Collectors.joining(",", "[", "]")))
                .collect(Collectors.joining(";", "{", "}"));
    }

}
