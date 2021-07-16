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

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.examples.common.experimental.api.ConsecutiveInfo;
import org.optaplanner.examples.common.experimental.api.ConsecutiveIntervalInfo;
import org.optaplanner.examples.common.experimental.impl.ConsecutiveIntervalInfoImpl;
import org.optaplanner.examples.common.experimental.impl.ConsecutiveSetTree;
import org.optaplanner.examples.common.experimental.impl.IntervalTree;

public class ExperimentalConstraintCollectorsTest {
    @Test
    public void consecutive() {
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
    public void consecutiveInterval() {
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
                new ConsecutiveSetTree<>(Integer::intValue, (a, b) -> b - a, Integer::sum, 1, 0);
        asList(data).forEach(tree::add);
        return tree.getConsecutiveData();
    }

    private ConsecutiveIntervalInfoImpl<Interval, Integer, Integer> consecutiveIntervalData(Interval... data) {
        IntervalTree<Interval, Integer, Integer> tree =
                new IntervalTree<>(Interval::getStart, Interval::getEnd, (a, b) -> b - a);
        asList(data).forEach(tree::add);
        return tree.getConsecutiveIntervalData();
    }

    private static <A, B, C, Container_, Result_> Runnable accumulate(
            TriConstraintCollector<A, B, C, Container_, Result_> collector, Object container, A valueA, B valueB,
            C valueC) {
        return collector.accumulator().apply((Container_) container, valueA, valueB, valueC);
    }

    private static <A, B, C, D, Container_, Result_> Runnable accumulate(
            QuadConstraintCollector<A, B, C, D, Container_, Result_> collector, Object container, A valueA, B valueB,
            C valueC, D valueD) {
        return collector.accumulator().apply((Container_) container, valueA, valueB, valueC, valueD);
    }

    private static <A, B, Container_, Result_> Runnable accumulate(
            BiConstraintCollector<A, B, Container_, Result_> collector, Object container, A valueA, B valueB) {
        return collector.accumulator().apply((Container_) container, valueA, valueB);
    }

    private static <A, Container_, Result_> Runnable accumulate(
            UniConstraintCollector<A, Container_, Result_> collector, Object container, A value) {
        return collector.accumulator().apply((Container_) container, value);
    }

    private static <A, B, C, D, Container_, Result_> void assertResult(
            QuadConstraintCollector<A, B, C, D, Container_, Result_> collector, Object container,
            Result_ expectedResult) {
        Result_ actualResult = collector.finisher().apply((Container_) container);
        assertThat(actualResult)
                .as("Collector (" + collector + ") did not produce expected result.")
                .isEqualTo(expectedResult);
    }

    private static <A, B, C, Container_, Result_> void assertResult(
            TriConstraintCollector<A, B, C, Container_, Result_> collector, Object container, Result_ expectedResult) {
        Result_ actualResult = collector.finisher().apply((Container_) container);
        assertThat(actualResult)
                .as("Collector (" + collector + ") did not produce expected result.")
                .isEqualTo(expectedResult);
    }

    private static <A, B, Container_, Result_> void assertResult(
            BiConstraintCollector<A, B, Container_, Result_> collector, Object container, Result_ expectedResult) {
        Result_ actualResult = collector.finisher().apply((Container_) container);
        assertThat(actualResult)
                .as("Collector (" + collector + ") did not produce expected result.")
                .isEqualTo(expectedResult);
    }

    private static <A, Container_, Result_> void assertResult(
            UniConstraintCollector<A, Container_, Result_> collector, Object container, Result_ expectedResult) {
        Result_ actualResult = collector.finisher().apply((Container_) container);
        assertThat(actualResult)
                .as("Collector (" + collector + ") did not produce expected result.")
                .usingRecursiveComparison()
                .ignoringFields("sourceTree")
                .isEqualTo(expectedResult);
    }
}
