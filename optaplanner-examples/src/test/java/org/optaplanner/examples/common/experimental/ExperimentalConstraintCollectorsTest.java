package org.optaplanner.examples.common.experimental;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.examples.common.experimental.api.ConsecutiveInfo;
import org.optaplanner.examples.common.experimental.api.ConsecutiveIntervalInfo;
import org.optaplanner.examples.common.experimental.impl.ConsecutiveIntervalInfoImpl;
import org.optaplanner.examples.common.experimental.impl.ConsecutiveSetTree;
import org.optaplanner.examples.common.experimental.impl.IntervalTree;

class ExperimentalConstraintCollectorsTest {
    @Test
    void consecutive() {
        // Do a basic test w/o edge cases; edge cases are covered in
        // ConsecutiveSetTreeTest
        UniConstraintCollector<Integer, ?, ConsecutiveInfo<Integer, Integer>> collector =
                ExperimentalConstraintCollectors.consecutive(Integer::intValue);
        Object container = collector.supplier().get();
        // Add first value, sequence is [2]
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, consecutiveData(2));
        // Add second value, sequence is [1,2]
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, consecutiveData(1, 2));
        // Add third value, same as the second. Sequence is [{1,1},2}]
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, consecutiveData(1, 1, 2));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, consecutiveData(1, 2));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, consecutiveData(2));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, consecutiveData());
    }

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
    }

    @Test
    void consecutiveInterval() {
        // Do a basic test w/o edge cases; edge cases are covered in
        // ConsecutiveSetTreeTest
        UniConstraintCollector<Interval, ?, ConsecutiveIntervalInfo<Interval, Integer, Integer>> collector =
                ExperimentalConstraintCollectors.consecutiveIntervals(Interval::getStart, Interval::getEnd, (a, b) -> b - a);
        Object container = collector.supplier().get();
        // Add first value, sequence is [(1,3)]
        Interval firstValue = new Interval(1, 3);
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, consecutiveIntervalData(firstValue));
        // Add second value, sequence is [(1,3),(2,4)]
        Interval secondValue = new Interval(2, 4);
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, consecutiveIntervalData(firstValue, secondValue));
        // Add third value, same as the second. Sequence is [{1,1},2}]
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, consecutiveIntervalData(firstValue, secondValue, secondValue));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, consecutiveIntervalData(firstValue, secondValue));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, consecutiveIntervalData(firstValue));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, consecutiveIntervalData());
    }

    private ConsecutiveInfo<Integer, Integer> consecutiveData(Integer... data) {
        ConsecutiveSetTree<Integer, Integer, Integer> tree =
                new ConsecutiveSetTree<>((a, b) -> b - a, Integer::sum, 1, 0);
        asList(data).forEach(datum -> tree.add(datum, datum));
        return tree;
    }

    private ConsecutiveIntervalInfoImpl<Interval, Integer, Integer> consecutiveIntervalData(Interval... data) {
        IntervalTree<Interval, Integer, Integer> tree =
                new IntervalTree<>(Interval::getStart, Interval::getEnd, (a, b) -> b - a);
        asList(data).forEach(datum -> tree.add(tree.getInterval(datum)));
        return tree.getConsecutiveIntervalData();
    }

    private static <A, Container_, Result_> Runnable accumulate(
            UniConstraintCollector<A, Container_, Result_> collector, Object container, A value) {
        return collector.accumulator().apply((Container_) container, value);
    }

    private static <A, Container_, Result_> void assertResult(
            UniConstraintCollector<A, Container_, Result_> collector, Object container, Result_ expectedResult) {
        Result_ actualResult = collector.finisher().apply((Container_) container);
        assertThat(actualResult)
                .as("Collector (" + collector + ") did not produce expected result.")
                .usingRecursiveComparison()
                .ignoringFields("sourceTree", "indexFunction", "sequenceList", "startItemToSequence")
                .isEqualTo(expectedResult);
    }
}
