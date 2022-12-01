package org.optaplanner.examples.common.experimental;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiConsumer;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.examples.common.experimental.api.ConsecutiveInfo;
import org.optaplanner.examples.common.experimental.impl.ConsecutiveIntervalInfoImpl;
import org.optaplanner.examples.common.experimental.impl.ConsecutiveSetTree;
import org.optaplanner.examples.common.experimental.impl.IntervalTree;

class ExperimentalConstraintCollectorsTest {
    @Test
    void consecutive() {
        // Do a basic test w/o edge cases; edge cases are covered in ConsecutiveSetTreeTest
        var collector = ExperimentalConstraintCollectors.consecutive(Integer::intValue);
        var container = collector.supplier().get();
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
        // Do a basic test w/o edge cases; edge cases are covered in ConsecutiveSetTreeTest
        var collector =
                ExperimentalConstraintCollectors.consecutiveIntervals(Interval::getStart, Interval::getEnd, (a, b) -> b - a);
        var container = collector.supplier().get();
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
        return Arrays.stream(data).collect(
                () -> new ConsecutiveSetTree<Integer, Integer, Integer>((a, b) -> b - a, Integer::sum, 1, 0),
                (tree, datum) -> tree.add(datum, datum),
                mergingNotSupported());
    }

    private ConsecutiveIntervalInfoImpl<Interval, Integer, Integer> consecutiveIntervalData(Interval... data) {
        return Arrays.stream(data).collect(
                () -> new IntervalTree<>(Interval::getStart, Interval::getEnd, (a, b) -> b - a),
                (tree, datum) -> tree.add(tree.getInterval(datum)),
                mergingNotSupported()).getConsecutiveIntervalData();
    }

    private static <T> BiConsumer<T, T> mergingNotSupported() {
        return (a, b) -> {
            throw new UnsupportedOperationException();
        };
    }

    private static <A, Container_> Runnable accumulate(
            UniConstraintCollector<A, Container_, ?> collector, Container_ container, A value) {
        return collector.accumulator().apply(container, value);
    }

    private static <A, Container_, Result_> void assertResult(
            UniConstraintCollector<A, Container_, Result_> collector, Container_ container, Result_ expectedResult) {
        Result_ actualResult = collector.finisher().apply(container);
        assertThat(actualResult)
                .as("Collector (" + collector + ") did not produce expected result.")
                .usingRecursiveComparison()
                .ignoringFields("sourceTree", "indexFunction", "sequenceList", "startItemToSequence")
                .isEqualTo(expectedResult);
    }
}
