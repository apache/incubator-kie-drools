package org.optaplanner.examples.common.experimental.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.optaplanner.examples.common.experimental.api.IntervalBreak;
import org.optaplanner.examples.common.experimental.api.IntervalCluster;

class IntervalTreeTest {
    private static class TestInterval {
        int start;
        int end;

        public TestInterval(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            TestInterval interval = (TestInterval) o;
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

    private IntervalTree<TestInterval, Integer, Integer> getIntegerIntervalTree() {
        return new IntervalTree<>(TestInterval::getStart, TestInterval::getEnd, (a, b) -> b - a);
    }

    @Test
    void testNonConsecutiveIntervals() {
        IntervalTree<TestInterval, Integer, Integer> tree = getIntegerIntervalTree();
        Interval<TestInterval, Integer> a = tree.getInterval(new TestInterval(0, 2));
        Interval<TestInterval, Integer> b = tree.getInterval(new TestInterval(3, 4));
        Interval<TestInterval, Integer> c = tree.getInterval(new TestInterval(5, 7));
        tree.add(a);
        tree.add(b);
        tree.add(c);

        IterableList<IntervalCluster<TestInterval, Integer, Integer>> clusterList =
                new IterableList<>(tree.getConsecutiveIntervalData().getIntervalClusters());
        assertThat(clusterList).hasSize(3);

        assertThat(clusterList.get(0)).containsExactly(new TestInterval(0, 2));
        assertThat(clusterList.get(0).hasOverlap()).isFalse();

        assertThat(clusterList.get(1)).containsExactly(new TestInterval(3, 4));
        assertThat(clusterList.get(1).hasOverlap()).isFalse();

        assertThat(clusterList.get(2)).containsExactly(new TestInterval(5, 7));
        assertThat(clusterList.get(2).hasOverlap()).isFalse();

        verifyBreaks(tree);
    }

    @Test
    void testConsecutiveIntervals() {
        IntervalTree<TestInterval, Integer, Integer> tree = getIntegerIntervalTree();
        Interval<TestInterval, Integer> a = tree.getInterval(new TestInterval(0, 2));
        Interval<TestInterval, Integer> b = tree.getInterval(new TestInterval(2, 4));
        Interval<TestInterval, Integer> c = tree.getInterval(new TestInterval(4, 7));
        tree.add(a);
        tree.add(b);
        tree.add(c);

        IterableList<IntervalCluster<TestInterval, Integer, Integer>> clusterList =
                new IterableList<>(tree.getConsecutiveIntervalData().getIntervalClusters());
        assertThat(clusterList).hasSize(1);

        assertThat(clusterList.get(0)).containsExactly(new TestInterval(0, 2), new TestInterval(2, 4), new TestInterval(4, 7));
        verifyBreaks(tree);
    }

    @Test
    void testDuplicateIntervals() {
        IntervalTree<TestInterval, Integer, Integer> tree = getIntegerIntervalTree();
        Interval<TestInterval, Integer> a = tree.getInterval(new TestInterval(0, 2));
        Interval<TestInterval, Integer> b = tree.getInterval(new TestInterval(4, 7));
        tree.add(a);
        tree.add(a);
        tree.add(b);

        IterableList<IntervalCluster<TestInterval, Integer, Integer>> clusterList =
                new IterableList<>(tree.getConsecutiveIntervalData().getIntervalClusters());
        assertThat(clusterList).hasSize(2);

        assertThat(clusterList.get(0)).containsExactly(a.getValue(), a.getValue());
        assertThat(clusterList.get(1)).containsExactly(b.getValue());
        verifyBreaks(tree);
    }

    @Test
    void testIntervalRemoval() {
        IntervalTree<TestInterval, Integer, Integer> tree = getIntegerIntervalTree();
        TestInterval removedInterval = new TestInterval(2, 4);
        Interval<TestInterval, Integer> a = tree.getInterval(new TestInterval(0, 2));
        Interval<TestInterval, Integer> b = tree.getInterval(removedInterval);
        Interval<TestInterval, Integer> c = tree.getInterval(new TestInterval(4, 7));
        tree.add(a);
        tree.add(b);
        tree.add(c);

        // Imitate changing planning variables
        removedInterval.setStart(10);
        removedInterval.setEnd(12);

        tree.remove(b);

        IterableList<IntervalCluster<TestInterval, Integer, Integer>> clusterList =
                new IterableList<>(tree.getConsecutiveIntervalData().getIntervalClusters());
        assertThat(clusterList).hasSize(2);

        assertThat(clusterList.get(0)).containsExactly(new TestInterval(0, 2));
        assertThat(clusterList.get(1)).containsExactly(new TestInterval(4, 7));
        verifyBreaks(tree);
    }

    @Test
    void testIntervalAddUpdatingOldBreak() {
        IntervalTree<TestInterval, Integer, Integer> tree = getIntegerIntervalTree();
        TestInterval beforeAll = new TestInterval(1, 2);
        TestInterval newStart = new TestInterval(3, 8);
        TestInterval oldStart = new TestInterval(4, 5);
        TestInterval betweenOldAndNewStart = new TestInterval(6, 7);
        TestInterval afterAll = new TestInterval(9, 10);

        tree.add(tree.getInterval(beforeAll));
        verifyBreaks(tree);

        tree.add(tree.getInterval(afterAll));
        verifyBreaks(tree);

        tree.add(tree.getInterval(oldStart));
        verifyBreaks(tree);

        tree.add(tree.getInterval(betweenOldAndNewStart));
        verifyBreaks(tree);

        tree.add(tree.getInterval(newStart));
        verifyBreaks(tree);
    }

    @Test
    void testOverlappingInterval() {
        IntervalTree<TestInterval, Integer, Integer> tree = getIntegerIntervalTree();
        Interval<TestInterval, Integer> a = tree.getInterval(new TestInterval(0, 2));
        TestInterval removedTestInterval1 = new TestInterval(1, 3);
        Interval<TestInterval, Integer> removedInterval1 = tree.getInterval(removedTestInterval1);
        Interval<TestInterval, Integer> c = tree.getInterval(new TestInterval(2, 4));

        Interval<TestInterval, Integer> d = tree.getInterval(new TestInterval(5, 6));

        Interval<TestInterval, Integer> e = tree.getInterval(new TestInterval(7, 9));
        TestInterval removedTestInterval2 = new TestInterval(7, 9);
        Interval<TestInterval, Integer> removedInterval2 = tree.getInterval(removedTestInterval2);

        tree.add(a);
        tree.add(removedInterval1);
        tree.add(c);
        tree.add(d);
        tree.add(e);
        tree.add(removedInterval2);

        IterableList<IntervalCluster<TestInterval, Integer, Integer>> clusterList =
                new IterableList<>(tree.getConsecutiveIntervalData().getIntervalClusters());
        assertThat(clusterList).hasSize(3);

        assertThat(clusterList.get(0)).containsExactly(a.getValue(), removedTestInterval1, c.getValue());
        assertThat(clusterList.get(0).hasOverlap()).isTrue();

        assertThat(clusterList.get(1)).containsExactly(d.getValue());
        assertThat(clusterList.get(1).hasOverlap()).isFalse();

        assertThat(clusterList.get(2)).containsExactly(e.getValue(), removedTestInterval2);
        assertThat(clusterList.get(2).hasOverlap()).isTrue();

        verifyBreaks(tree);

        // Simulate changing planning variables
        removedTestInterval1.setStart(0);
        removedTestInterval1.setEnd(10);

        tree.remove(removedInterval1);

        clusterList = new IterableList<>(tree.getConsecutiveIntervalData().getIntervalClusters());
        assertThat(clusterList).hasSize(3);

        assertThat(clusterList.get(0)).containsExactly(a.getValue(), c.getValue());
        assertThat(clusterList.get(0).hasOverlap()).isFalse();

        assertThat(clusterList.get(1)).containsExactly(d.getValue());
        assertThat(clusterList.get(1).hasOverlap()).isFalse();

        assertThat(clusterList.get(2)).containsExactly(e.getValue(), removedTestInterval2);
        assertThat(clusterList.get(2).hasOverlap()).isTrue();

        verifyBreaks(tree);

        // Simulate changing planning variables
        removedTestInterval2.setStart(2);
        removedTestInterval2.setEnd(4);

        tree.remove(removedInterval2);
        clusterList = new IterableList<>(tree.getConsecutiveIntervalData().getIntervalClusters());
        assertThat(clusterList).hasSize(3);

        assertThat(clusterList.get(0)).containsExactly(a.getValue(), c.getValue());
        assertThat(clusterList.get(0).hasOverlap()).isFalse();

        assertThat(clusterList.get(1)).containsExactly(d.getValue());
        assertThat(clusterList.get(1).hasOverlap()).isFalse();

        assertThat(clusterList.get(2)).containsExactly(e.getValue());
        assertThat(clusterList.get(2).hasOverlap()).isFalse();

        verifyBreaks(tree);
        Interval<TestInterval, Integer> g = tree.getInterval(new TestInterval(6, 7));
        tree.add(g);
        clusterList = new IterableList<>(tree.getConsecutiveIntervalData().getIntervalClusters());
        assertThat(clusterList).hasSize(2);

        assertThat(clusterList.get(0)).containsExactly(a.getValue(), c.getValue());
        assertThat(clusterList.get(0).hasOverlap()).isFalse();

        assertThat(clusterList.get(1)).containsExactly(d.getValue(), g.getValue(), e.getValue());
        assertThat(clusterList.get(1).hasOverlap()).isFalse();
    }

    public void verifyBreaks(IntervalTree<TestInterval, Integer, Integer> tree) {
        IterableList<IntervalCluster<TestInterval, Integer, Integer>> clusterList =
                new IterableList<>(tree.getConsecutiveIntervalData().getIntervalClusters());
        IterableList<IntervalBreak<TestInterval, Integer, Integer>> breakList =
                new IterableList<>(tree.getConsecutiveIntervalData().getBreaks());

        if (clusterList.size() == 0) {
            return;
        }
        assertThat(breakList).hasSize(clusterList.size() - 1);
        for (int i = 0; i < clusterList.size() - 1; i++) {
            assertThat(breakList.get(i).getPreviousIntervalCluster()).isSameAs(clusterList.get(i));
            assertThat(breakList.get(i).getNextIntervalCluster()).isSameAs(clusterList.get(i + 1));
            assertThat(breakList.get(i).getPreviousIntervalClusterEnd()).isEqualTo(clusterList.get(i).getEnd());
            assertThat(breakList.get(i).getNextIntervalClusterStart()).isEqualTo(clusterList.get(i + 1).getStart());
            assertThat(breakList.get(i).getLength()).isEqualTo(clusterList.get(i + 1).getStart() - clusterList.get(i).getEnd());
        }
    }

    private static int intervalBreakCompare(IntervalBreak<TestInterval, Integer, Integer> a,
            IntervalBreak<TestInterval, Integer, Integer> b) {
        if (a == b) {
            return 0;
        }
        if (a == null || b == null) {
            return (a == null) ? -1 : 1;
        }
        boolean out = intervalClusterCompare(a.getPreviousIntervalCluster(), b.getPreviousIntervalCluster()) == 0 &&
                intervalClusterCompare(a.getNextIntervalCluster(), b.getNextIntervalCluster()) == 0 &&
                Objects.equals(a.getLength(), b.getLength());

        if (out) {
            return 0;
        }
        return a.hashCode() - b.hashCode();
    }

    private static int intervalClusterCompare(IntervalCluster<TestInterval, Integer, Integer> a,
            IntervalCluster<TestInterval, Integer, Integer> b) {
        if (a == b) {
            return 0;
        }
        if (a == null || b == null) {
            return (a == null) ? -1 : 1;
        }

        if (!(a instanceof IntervalClusterImpl) || !(b instanceof IntervalClusterImpl)) {
            throw new IllegalArgumentException("Expected (" + a + ") and (" + b + ") to both be IntervalClusterImpl");
        }

        IntervalClusterImpl<TestInterval, Integer, Integer> first = (IntervalClusterImpl<TestInterval, Integer, Integer>) a;
        IntervalClusterImpl<TestInterval, Integer, Integer> second = (IntervalClusterImpl<TestInterval, Integer, Integer>) b;

        boolean out = first.getStartSplitPoint().compareTo(second.getStartSplitPoint()) == 0 &&
                first.getEndSplitPoint().compareTo(second.getEndSplitPoint()) == 0;
        if (out) {
            return 0;
        }
        return first.hashCode() - second.hashCode();
    }

    // Compare the mutable version with the recompute version
    @Test
    void testRandomIntervals() {
        Random random = new Random(1);

        for (int i = 0; i < 100; i++) {
            Map<TestInterval, Interval<TestInterval, Integer>> intervalToInstanceMap = new HashMap<>();
            TreeSet<IntervalSplitPoint<TestInterval, Integer>> splitPoints = new TreeSet<>();
            IntervalTree<TestInterval, Integer, Integer> tree =
                    new IntervalTree<>(TestInterval::getStart, TestInterval::getEnd, (a, b) -> b - a);
            for (int j = 0; j < 100; j++) {
                // Create a random interval
                String old = formatIntervalTree(tree);
                int from = random.nextInt(5);
                int to = from + random.nextInt(5);
                TestInterval data = new TestInterval(from, to);
                Interval<TestInterval, Integer> interval = intervalToInstanceMap.computeIfAbsent(data, tree::getInterval);
                Interval<TestInterval, Integer> treeInterval =
                        new Interval<>(data, TestInterval::getStart, TestInterval::getEnd);
                splitPoints.add(treeInterval.getStartSplitPoint());
                splitPoints.add(treeInterval.getEndSplitPoint());

                // Get the split points from the set (since those split points have collections)
                IntervalSplitPoint<TestInterval, Integer> startSplitPoint =
                        splitPoints.floor(treeInterval.getStartSplitPoint());
                IntervalSplitPoint<TestInterval, Integer> endSplitPoint = splitPoints.floor(treeInterval.getEndSplitPoint());

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
                IntervalSplitPoint<TestInterval, Integer> previous = null;
                IntervalSplitPoint<TestInterval, Integer> current = splitPoints.isEmpty() ? null : splitPoints.first();
                List<IntervalClusterImpl<TestInterval, Integer, Integer>> intervalClusterList = new ArrayList<>();
                List<IntervalBreakImpl<TestInterval, Integer, Integer>> breakList = new ArrayList<>();
                while (current != null) {
                    intervalClusterList.add(new IntervalClusterImpl<>(splitPoints, (a, b) -> a - b, current));
                    if (previous != null) {
                        IntervalClusterImpl<TestInterval, Integer, Integer> before =
                                intervalClusterList.get(intervalClusterList.size() - 2);
                        IntervalClusterImpl<TestInterval, Integer, Integer> after =
                                intervalClusterList.get(intervalClusterList.size() - 1);
                        breakList.add(new IntervalBreakImpl<>(before, after, after.getStart() - before.getEnd()));
                    }
                    previous = current;
                    current = splitPoints.higher(intervalClusterList.get(intervalClusterList.size() - 1).getEndSplitPoint());
                }

                // Verify the mutable version matches the recompute version
                verifyBreaks(tree);
                assertThat(tree.getConsecutiveIntervalData().getIntervalClusters())
                        .as(op + " interval " + interval + " to " + old)
                        .usingElementComparator(IntervalTreeTest::intervalClusterCompare)
                        .containsExactlyElementsOf(intervalClusterList);
                assertThat(tree.getConsecutiveIntervalData().getBreaks())
                        .as(op + " interval " + interval + " to " + old)
                        .usingElementComparator(IntervalTreeTest::intervalBreakCompare)
                        .containsExactlyElementsOf(breakList);
            }
        }
    }

    private String formatIntervalTree(IntervalTree<TestInterval, Integer, Integer> intervalTree) {
        List<List<TestInterval>> listOfIntervalClusters = new ArrayList<>();
        for (IntervalCluster<TestInterval, Integer, Integer> cluster : intervalTree.getConsecutiveIntervalData()
                .getIntervalClusters()) {
            List<TestInterval> intervalsInCluster = new ArrayList<>();
            for (TestInterval interval : cluster) {
                intervalsInCluster.add(interval);
            }
            listOfIntervalClusters.add(intervalsInCluster);
        }
        return listOfIntervalClusters.stream()
                .map(cluster -> cluster.stream().map(TestInterval::toString).collect(Collectors.joining(",", "[", "]")))
                .collect(Collectors.joining(";", "{", "}"));
    }

}
