/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Collections.emptySortedMap;
import static java.util.Collections.emptySortedSet;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.compose;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.countLongBi;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.countLongQuad;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.countLongTri;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.max;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.min;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.asMap;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.asSet;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.asSortedMap;
import static org.optaplanner.core.impl.testdata.util.PlannerTestUtils.asSortedSet;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Period;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.util.Pair;
import org.optaplanner.core.impl.util.Quadruple;

class ConstraintCollectorsTest {

    // ************************************************************************
    // count
    // ************************************************************************

    @Test
    void count() {
        UniConstraintCollector<Integer, ?, Integer> collector = ConstraintCollectors.count();
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, 0);
        // Add first value, we have one now.
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, 1);
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, 2);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, 3);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, 2);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 1);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0);
    }

    @Test
    void countLong() {
        UniConstraintCollector<Long, ?, Long> collector = ConstraintCollectors.countLong();
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, 0L);
        // Add first value, we have one now.
        long firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, 1L);
        // Add second value, we have two now.
        long secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, 2L);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, 3L);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, 2L);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 1L);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0L);
    }

    @Test
    void countBi() {
        BiConstraintCollector<Integer, Integer, ?, Integer> collector = ConstraintCollectors.countBi();
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, 0);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 1;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB);
        assertResult(collector, container, 1);
        // Add second value, we have two now.
        int secondValueA = 1;
        int secondValueB = 2;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, 2);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, 3);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, 2);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 1);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0);
    }

    @Test
    void countBiLong() {
        BiConstraintCollector<Integer, Integer, ?, Long> collector = countLongBi();
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, 0L);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 1;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB);
        assertResult(collector, container, 1L);
        // Add second value, we have two now.
        int secondValueA = 1;
        int secondValueB = 2;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, 2L);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, 3L);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, 2L);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 1L);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0L);
    }

    @Test
    void countTri() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Integer> collector = ConstraintCollectors.countTri();
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, 0);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 1;
        int firstValueC = 3;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC);
        assertResult(collector, container, 1);
        // Add second value, we have two now.
        int secondValueA = 1;
        int secondValueB = 2;
        int secondValueC = 3;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, 2);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, 3);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, 2);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 1);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0);
    }

    @Test
    void countTriLong() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Long> collector = countLongTri();
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, 0L);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 1;
        int firstValueC = 3;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC);
        assertResult(collector, container, 1L);
        // Add second value, we have two now.
        int secondValueA = 1;
        int secondValueB = 2;
        int secondValueC = 3;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, 2L);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, 3L);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, 2L);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 1L);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0L);
    }

    @Test
    void countQuad() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Integer> collector = ConstraintCollectors.countQuad();
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, 0);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 1;
        int firstValueC = 3;
        int firstValueD = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC, firstValueD);
        assertResult(collector, container, 1);
        // Add second value, we have two now.
        int secondValueA = 1;
        int secondValueB = 2;
        int secondValueC = 3;
        int secondValueD = 4;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC,
                secondValueD);
        assertResult(collector, container, 2);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC,
                secondValueD);
        assertResult(collector, container, 3);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, 2);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 1);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0);
    }

    @Test
    void countQuadLong() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Long> collector = countLongQuad();
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, 0L);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 1;
        int firstValueC = 3;
        int firstValueD = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC, firstValueD);
        assertResult(collector, container, 1L);
        // Add second value, we have two now.
        int secondValueA = 1;
        int secondValueB = 2;
        int secondValueC = 3;
        int secondValueD = 4;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC,
                secondValueD);
        assertResult(collector, container, 2L);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC,
                secondValueD);
        assertResult(collector, container, 3L);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, 2L);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 1L);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0L);
    }

    // ************************************************************************
    // countDistinct
    // ************************************************************************

    @Test
    void countDistinct() {
        UniConstraintCollector<Integer, ?, Integer> collector = ConstraintCollectors.countDistinct();
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, 0);
        // Add first value, we have one now.
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, 1);
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, 2);
        // Add third value, same as the second. We now have two distinct values.
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, 2);
        // Retract one instance of the second value; we still only have two distinct values.
        secondRetractor.run();
        assertResult(collector, container, 2);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 1);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0);
    }

    @Test
    void countDistinctLong() {
        UniConstraintCollector<Long, ?, Long> collector = ConstraintCollectors.countDistinctLong(Function.identity());
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, 0L);
        // Add first value, we have one now.
        long firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, 1L);
        // Add second value, we have two now.
        long secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, 2L);
        // Add third value, same as the second. We still have two distinct values.
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, 2L);
        // Retract one instance of the second value. We still have two distinct values.
        secondRetractor.run();
        assertResult(collector, container, 2L);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 1L);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0L);
    }

    @Test
    void countDistinctBi() {
        BiConstraintCollector<Integer, Integer, ?, Integer> collector = ConstraintCollectors.countDistinct(Integer::sum);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, 0);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB);
        assertResult(collector, container, 1);
        // Add second value, we have two now.
        int secondValueA = 1;
        int secondValueB = 2;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, 2);
        // Add third value, same as the second. We now have two distinct values.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, 2);
        // Retract one instance of the second value; we still only have two distinct values.
        secondRetractor.run();
        assertResult(collector, container, 2);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 1);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0);
    }

    @Test
    void countDistinctBiLong() {
        BiConstraintCollector<Integer, Integer, ?, Long> collector = ConstraintCollectors.countDistinctLong(Integer::sum);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, 0L);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB);
        assertResult(collector, container, 1L);
        // Add second value, we have two now.
        int secondValueA = 1;
        int secondValueB = 2;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, 2L);
        // Add third value, same as the second. We now have two distinct values.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, 2L);
        // Retract one instance of the second value; we still only have two distinct values.
        secondRetractor.run();
        assertResult(collector, container, 2L);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 1L);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0L);
    }

    @Test
    void countDistinctTri() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Integer> collector = ConstraintCollectors
                .countDistinct((a, b, c) -> a + b + c);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, 0);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        int firstValueC = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC);
        assertResult(collector, container, 1);
        // Add second value, we have two now.
        int secondValueA = 1;
        int secondValueB = 2;
        int secondValueC = 3;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, 2);
        // Add third value, same as the second. We now have two distinct values.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, 2);
        // Retract one instance of the second value; we still only have two distinct values.
        secondRetractor.run();
        assertResult(collector, container, 2);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 1);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0);
    }

    @Test
    void countDistinctTriLong() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Long> collector = ConstraintCollectors
                .countDistinctLong((a, b, c) -> a + b + c);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, 0L);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        int firstValueC = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC);
        assertResult(collector, container, 1L);
        // Add second value, we have two now.
        int secondValueA = 1;
        int secondValueB = 2;
        int secondValueC = 3;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, 2L);
        // Add third value, same as the second. We now have two distinct values.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, 2L);
        // Retract one instance of the second value; we still only have two distinct values.
        secondRetractor.run();
        assertResult(collector, container, 2L);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 1L);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0L);
    }

    @Test
    void countDistinctQuad() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Integer> collector = ConstraintCollectors
                .countDistinct((a, b, c, d) -> a + b + c + d);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, 0);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        int firstValueC = 4;
        int firstValueD = 5;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC, firstValueD);
        assertResult(collector, container, 1);
        // Add second value, we have two now.
        int secondValueA = 1;
        int secondValueB = 2;
        int secondValueC = 3;
        int secondValueD = 4;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC,
                secondValueD);
        assertResult(collector, container, 2);
        // Add third value, same as the second. We now have two distinct values.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC,
                secondValueD);
        assertResult(collector, container, 2);
        // Retract one instance of the second value; we still only have two distinct values.
        secondRetractor.run();
        assertResult(collector, container, 2);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 1);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0);
    }

    @Test
    void countDistinctQuadLong() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Long> collector = ConstraintCollectors
                .countDistinctLong((a, b, c, d) -> a + b + c + d);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, 0L);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        int firstValueC = 4;
        int firstValueD = 5;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC, firstValueD);
        assertResult(collector, container, 1L);
        // Add second value, we have two now.
        int secondValueA = 1;
        int secondValueB = 2;
        int secondValueC = 3;
        int secondValueD = 4;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC,
                secondValueD);
        assertResult(collector, container, 2L);
        // Add third value, same as the second. We now have two distinct values.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC,
                secondValueD);
        assertResult(collector, container, 2L);
        // Retract one instance of the second value; we still only have two distinct values.
        secondRetractor.run();
        assertResult(collector, container, 2L);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 1L);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0L);
    }

    // ************************************************************************
    // sum
    // ************************************************************************

    @Test
    void sum() {
        UniConstraintCollector<Integer, ?, Integer> collector = ConstraintCollectors.sum(i -> i);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, 0);
        // Add first value, we have one now.
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, 2);
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, 3);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, 4);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, 3);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 2);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0);
    }

    @Test
    void sumLong() {
        UniConstraintCollector<Long, ?, Long> collector = ConstraintCollectors.sumLong(l -> l);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, 0L);
        // Add first value, we have one now.
        long firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, 2L);
        // Add second value, we have two now.
        long secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, 3L);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, 4L);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, 3L);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 2L);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0L);
    }

    @Test
    void sumBigDecimal() {
        UniConstraintCollector<BigDecimal, ?, BigDecimal> collector = ConstraintCollectors.sumBigDecimal(l -> l);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, BigDecimal.ZERO);
        // Add first value, we have one now.
        BigDecimal firstValue = BigDecimal.ONE;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, BigDecimal.ONE);
        // Add second value, we have two now.
        BigDecimal secondValue = BigDecimal.TEN;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, BigDecimal.valueOf(11));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, BigDecimal.valueOf(21));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, BigDecimal.valueOf(11));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, BigDecimal.ONE);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, BigDecimal.ZERO);
    }

    @Test
    void sumBigInteger() {
        UniConstraintCollector<BigInteger, ?, BigInteger> collector = ConstraintCollectors.sumBigInteger(l -> l);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, BigInteger.ZERO);
        // Add first value, we have one now.
        BigInteger firstValue = BigInteger.ONE;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, BigInteger.ONE);
        // Add second value, we have two now.
        BigInteger secondValue = BigInteger.TEN;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, BigInteger.valueOf(11));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, BigInteger.valueOf(21));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, BigInteger.valueOf(11));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, BigInteger.ONE);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, BigInteger.ZERO);
    }

    @Test
    void sumDuration() {
        UniConstraintCollector<Duration, ?, Duration> collector = ConstraintCollectors.sumDuration(l -> l);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, Duration.ZERO);
        // Add first value, we have one now.
        Duration firstValue = Duration.ofSeconds(1);
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, firstValue);
        // Add second value, we have two now.
        Duration secondValue = Duration.ofMinutes(1);
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, Duration.ofSeconds(61));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, Duration.ofSeconds(121));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, Duration.ofSeconds(61));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, firstValue);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, Duration.ZERO);
    }

    @Test
    void sumPeriod() {
        UniConstraintCollector<Period, ?, Period> collector = ConstraintCollectors.sumPeriod(l -> l);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, Period.ZERO);
        // Add first value, we have one now.
        Period firstValue = Period.ofDays(1);
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, firstValue);
        // Add second value, we have two now.
        Period secondValue = Period.ofDays(2);
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, Period.ofDays(3));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, Period.ofDays(5));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, Period.ofDays(3));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, firstValue);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, Period.ZERO);
    }

    @Test
    void sumBi() {
        BiConstraintCollector<Integer, Integer, ?, Integer> collector = ConstraintCollectors.sum(Integer::sum);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, 0);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB);
        assertResult(collector, container, 5);
        // Add second value, we have two now.
        int secondValueA = 4;
        int secondValueB = 5;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, 14);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, 23);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, 14);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 5);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0);
    }

    @Test
    void sumBiLong() {
        BiConstraintCollector<Integer, Integer, ?, Long> collector = ConstraintCollectors.sumLong(Integer::sum);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, 0L);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB);
        assertResult(collector, container, 5L);
        // Add second value, we have two now.
        int secondValueA = 4;
        int secondValueB = 5;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, 14L);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, 23L);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, 14L);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 5L);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0L);
    }

    @Test
    void sumBiBigDecimal() {
        BiConstraintCollector<Integer, Integer, ?, BigDecimal> collector = ConstraintCollectors
                .sumBigDecimal((a, b) -> BigDecimal.valueOf(a + b));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, BigDecimal.ZERO);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB);
        assertResult(collector, container, BigDecimal.valueOf(5));
        // Add second value, we have two now.
        int secondValueA = 4;
        int secondValueB = 5;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, BigDecimal.valueOf(14));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, BigDecimal.valueOf(23));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, BigDecimal.valueOf(14));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, BigDecimal.valueOf(5));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, BigDecimal.ZERO);
    }

    @Test
    void sumBiBigInteger() {
        BiConstraintCollector<Integer, Integer, ?, BigInteger> collector = ConstraintCollectors
                .sumBigInteger((a, b) -> BigInteger.valueOf(a + b));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, BigInteger.ZERO);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB);
        assertResult(collector, container, BigInteger.valueOf(5));
        // Add second value, we have two now.
        int secondValueA = 4;
        int secondValueB = 5;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, BigInteger.valueOf(14));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, BigInteger.valueOf(23));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, BigInteger.valueOf(14));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, BigInteger.valueOf(5));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, BigInteger.ZERO);
    }

    @Test
    void sumBiDuration() {
        BiConstraintCollector<Integer, Integer, ?, Duration> collector = ConstraintCollectors
                .sumDuration((a, b) -> Duration.ofSeconds(a + b));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, Duration.ZERO);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB);
        assertResult(collector, container, Duration.ofSeconds(5));
        // Add second value, we have two now.
        int secondValueA = 4;
        int secondValueB = 5;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, Duration.ofSeconds(14));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, Duration.ofSeconds(23));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, Duration.ofSeconds(14));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, Duration.ofSeconds(5));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, Duration.ZERO);
    }

    @Test
    void sumBiPeriod() {
        BiConstraintCollector<Integer, Integer, ?, Period> collector = ConstraintCollectors
                .sumPeriod((a, b) -> Period.ofDays(a + b));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, Period.ZERO);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB);
        assertResult(collector, container, Period.ofDays(5));
        // Add second value, we have two now.
        int secondValueA = 4;
        int secondValueB = 5;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, Period.ofDays(14));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, Period.ofDays(23));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, Period.ofDays(14));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, Period.ofDays(5));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, Period.ZERO);
    }

    @Test
    void sumTri() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Integer> collector = ConstraintCollectors
                .sum((a, b, c) -> a + b + c);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, 0);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        int firstValueC = 1;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC);
        assertResult(collector, container, 6);
        // Add second value, we have two now.
        int secondValueA = 4;
        int secondValueB = 5;
        int secondValueC = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, 16);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, 26);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, 16);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 6);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0);
    }

    @Test
    void sumTriLong() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Long> collector = ConstraintCollectors
                .sumLong((a, b, c) -> a + b + c);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, 0L);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        int firstValueC = 1;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC);
        assertResult(collector, container, 6L);
        // Add second value, we have two now.
        int secondValueA = 4;
        int secondValueB = 5;
        int secondValueC = 1;
        int secondSum = secondValueA + secondValueB + secondValueC;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, 16L);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, 26L);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, 16L);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 6L);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0L);
    }

    @Test
    void sumTriBigDecimal() {
        TriConstraintCollector<Integer, Integer, Integer, ?, BigDecimal> collector = ConstraintCollectors
                .sumBigDecimal((a, b, c) -> BigDecimal.valueOf(a + b + c));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, BigDecimal.ZERO);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        int firstValueC = 1;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC);
        assertResult(collector, container, BigDecimal.valueOf(6));
        // Add second value, we have two now.
        int secondValueA = 4;
        int secondValueB = 5;
        int secondValueC = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, BigDecimal.valueOf(16));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, BigDecimal.valueOf(26));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, BigDecimal.valueOf(16));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, BigDecimal.valueOf(6));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, BigDecimal.ZERO);
    }

    @Test
    void sumTriBigInteger() {
        TriConstraintCollector<Integer, Integer, Integer, ?, BigInteger> collector = ConstraintCollectors
                .sumBigInteger((a, b, c) -> BigInteger.valueOf(a + b + c));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, BigInteger.ZERO);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        int firstValueC = 1;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC);
        assertResult(collector, container, BigInteger.valueOf(6));
        // Add second value, we have two now.
        int secondValueA = 4;
        int secondValueB = 5;
        int secondValueC = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, BigInteger.valueOf(16));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, BigInteger.valueOf(26));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, BigInteger.valueOf(16));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, BigInteger.valueOf(6));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, BigInteger.ZERO);
    }

    @Test
    void sumTriDuration() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Duration> collector = ConstraintCollectors
                .sumDuration((a, b, c) -> Duration.ofSeconds(a + b + c));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, Duration.ZERO);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        int firstValueC = 1;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC);
        assertResult(collector, container, Duration.ofSeconds(6));
        // Add second value, we have two now.
        int secondValueA = 4;
        int secondValueB = 5;
        int secondValueC = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, Duration.ofSeconds(16));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, Duration.ofSeconds(26));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, Duration.ofSeconds(16));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, Duration.ofSeconds(6));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, Duration.ZERO);
    }

    @Test
    void sumTriPeriod() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Period> collector = ConstraintCollectors
                .sumPeriod((a, b, c) -> Period.ofDays(a + b + c));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, Period.ZERO);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        int firstValueC = 1;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC);
        assertResult(collector, container, Period.ofDays(6));
        // Add second value, we have two now.
        int secondValueA = 4;
        int secondValueB = 5;
        int secondValueC = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, Period.ofDays(16));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, Period.ofDays(26));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, Period.ofDays(16));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, Period.ofDays(6));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, Period.ZERO);
    }

    @Test
    void sumQuad() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Integer> collector = ConstraintCollectors
                .sum((a, b, c, d) -> a + b + c + d);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, 0);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        int firstValueC = 1;
        int firstValueD = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC, firstValueD);
        assertResult(collector, container, 10);
        // Add second value, we have two now.
        int secondValueA = 4;
        int secondValueB = 5;
        int secondValueC = 1;
        int secondValueD = 2;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC,
                secondValueD);
        assertResult(collector, container, 22);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC,
                secondValueD);
        assertResult(collector, container, 34);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, 22);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 10);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0);
    }

    @Test
    void sumQuadLong() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Long> collector = ConstraintCollectors
                .sumLong((a, b, c, d) -> a + b + c + d);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, 0L);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        int firstValueC = 1;
        int firstValueD = 4;
        int firstSum = firstValueA + firstValueB + firstValueC + firstValueD;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC, firstValueD);
        assertResult(collector, container, 10L);
        // Add second value, we have two now.
        int secondValueA = 4;
        int secondValueB = 5;
        int secondValueC = 1;
        int secondValueD = 2;
        int secondSum = secondValueA + secondValueB + secondValueC + secondValueD;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC,
                secondValueD);
        assertResult(collector, container, 22L);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC,
                secondValueD);
        assertResult(collector, container, 34L);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, 22L);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 10L);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0L);
    }

    @Test
    void sumQuadBigDecimal() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, BigDecimal> collector = ConstraintCollectors
                .sumBigDecimal((a, b, c, d) -> BigDecimal.valueOf(a + b + c + d));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, BigDecimal.ZERO);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        int firstValueC = 1;
        int firstValueD = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC, firstValueD);
        assertResult(collector, container, BigDecimal.valueOf(10));
        // Add second value, we have two now.
        int secondValueA = 4;
        int secondValueB = 5;
        int secondValueC = 1;
        int secondValueD = 2;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC,
                secondValueD);
        assertResult(collector, container, BigDecimal.valueOf(22));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC,
                secondValueD);
        assertResult(collector, container, BigDecimal.valueOf(34));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, BigDecimal.valueOf(22));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, BigDecimal.valueOf(10));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, BigDecimal.ZERO);
    }

    @Test
    void sumQuadBigInteger() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, BigInteger> collector = ConstraintCollectors
                .sumBigInteger((a, b, c, d) -> BigInteger.valueOf(a + b + c + d));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, BigInteger.ZERO);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        int firstValueC = 1;
        int firstValueD = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC, firstValueD);
        assertResult(collector, container, BigInteger.valueOf(10));
        // Add second value, we have two now.
        int secondValueA = 4;
        int secondValueB = 5;
        int secondValueC = 1;
        int secondValueD = 2;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC,
                secondValueD);
        assertResult(collector, container, BigInteger.valueOf(22));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC,
                secondValueD);
        assertResult(collector, container, BigInteger.valueOf(34));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, BigInteger.valueOf(22));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, BigInteger.valueOf(10));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, BigInteger.ZERO);
    }

    @Test
    void sumQuadDuration() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Duration> collector = ConstraintCollectors
                .sumDuration((a, b, c, d) -> Duration.ofSeconds(a + b + c + d));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, Duration.ZERO);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        int firstValueC = 1;
        int firstValueD = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC, firstValueD);
        assertResult(collector, container, Duration.ofSeconds(10));
        // Add second value, we have two now.
        int secondValueA = 4;
        int secondValueB = 5;
        int secondValueC = 1;
        int secondValueD = 2;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC,
                secondValueD);
        assertResult(collector, container, Duration.ofSeconds(22));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC,
                secondValueD);
        assertResult(collector, container, Duration.ofSeconds(34));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, Duration.ofSeconds(22));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, Duration.ofSeconds(10));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, Duration.ZERO);
    }

    @Test
    void sumQuadPeriod() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Period> collector = ConstraintCollectors
                .sumPeriod((a, b, c, d) -> Period.ofDays(a + b + c + d));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, Period.ZERO);
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        int firstValueC = 1;
        int firstValueD = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC, firstValueD);
        assertResult(collector, container, Period.ofDays(10));
        // Add second value, we have two now.
        int secondValueA = 4;
        int secondValueB = 5;
        int secondValueC = 1;
        int secondValueD = 2;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC,
                secondValueD);
        assertResult(collector, container, Period.ofDays(22));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC,
                secondValueD);
        assertResult(collector, container, Period.ofDays(34));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, Period.ofDays(22));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, Period.ofDays(10));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, Period.ZERO);
    }

    // ************************************************************************
    // min
    // ************************************************************************

    @Test
    void minComparable() {
        UniConstraintCollector<Integer, ?, Integer> collector = min();
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // add first value, which becomes the min
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, firstValue);
        // add second value, lesser than the first, becomes the new min
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, secondValue);
        // add third value, same as the second, result does not change
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, secondValue);
        // retract one instance of the second value; second value is still the min value, nothing should change
        secondRetractor.run();
        assertResult(collector, container, secondValue);
        // retract final instance of the second value; first value is now the min value
        thirdRetractor.run();
        assertResult(collector, container, firstValue);
        // retract last value; there are no values now
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void minNotComparable() {
        UniConstraintCollector<Object, ?, Object> collector = min(Comparator.comparing(o -> (String) o));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // add first value, which becomes the min
        String firstValue = "b";
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, firstValue);
        // add second value, lesser than the first, becomes the new min
        String secondValue = "a";
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, secondValue);
        // add third value, same as the second, result does not change
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, secondValue);
        // retract one instance of the second value; second value is still the min value, nothing should change
        secondRetractor.run();
        assertResult(collector, container, secondValue);
        // retract final instance of the second value; first value is now the min value
        thirdRetractor.run();
        assertResult(collector, container, firstValue);
        // retract last value; there are no values now
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void minComparableBi() {
        BiConstraintCollector<Integer, Integer, ?, Integer> collector = min(
                (BiFunction<Integer, Integer, Integer>) Integer::sum);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // add first value, which becomes the min
        int firstValueA = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, 0);
        assertResult(collector, container, 2);
        // add second value, lesser than the first, becomes the new min
        int secondValueA = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, 0);
        assertResult(collector, container, 1);
        // add third value, same as the second, result does not change
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, 0);
        assertResult(collector, container, 1);
        // retract one instance of the second value; second value is still the min value, nothing should change
        secondRetractor.run();
        assertResult(collector, container, 1);
        // retract final instance of the second value; first value is now the min value
        thirdRetractor.run();
        assertResult(collector, container, 2);
        // retract last value; there are no values now
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void minNotComparableBi() {
        BiConstraintCollector<String, String, ?, String> collector = min((a, b) -> a, Comparator.comparing(o -> o));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // add first value, which becomes the min
        String firstValue = "b";
        Runnable firstRetractor = accumulate(collector, container, firstValue, null);
        assertResult(collector, container, firstValue);
        // add second value, lesser than the first, becomes the new min
        String secondValue = "a";
        Runnable secondRetractor = accumulate(collector, container, secondValue, null);
        assertResult(collector, container, secondValue);
        // add third value, same as the second, result does not change
        Runnable thirdRetractor = accumulate(collector, container, secondValue, null);
        assertResult(collector, container, secondValue);
        // retract one instance of the second value; second value is still the min value, nothing should change
        secondRetractor.run();
        assertResult(collector, container, secondValue);
        // retract final instance of the second value; first value is now the min value
        thirdRetractor.run();
        assertResult(collector, container, firstValue);
        // retract last value; there are no values now
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void minComparableTri() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Integer> collector = min((a, b, c) -> a + b + c);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // add first value, which becomes the min
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0);
        assertResult(collector, container, 2);
        // add second value, lesser than the first, becomes the new min
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, 1);
        // add third value, same as the second, result does not change
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, 1);
        // retract one instance of the second value; second value is still the min value, nothing should change
        secondRetractor.run();
        assertResult(collector, container, 1);
        // retract final instance of the second value; first value is now the min value
        thirdRetractor.run();
        assertResult(collector, container, 2);
        // retract last value; there are no values now
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void minNotComparableTri() {
        TriConstraintCollector<String, String, String, ?, String> collector = min((a, b, c) -> a,
                Comparator.comparing(o -> o));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // add first value, which becomes the min
        String firstValue = "b";
        Runnable firstRetractor = accumulate(collector, container, firstValue, null, null);
        assertResult(collector, container, firstValue);
        // add second value, lesser than the first, becomes the new min
        String secondValue = "a";
        Runnable secondRetractor = accumulate(collector, container, secondValue, null, null);
        assertResult(collector, container, secondValue);
        // add third value, same as the second, result does not change
        Runnable thirdRetractor = accumulate(collector, container, secondValue, null, null);
        assertResult(collector, container, secondValue);
        // retract one instance of the second value; second value is still the min value, nothing should change
        secondRetractor.run();
        assertResult(collector, container, secondValue);
        // retract final instance of the second value; first value is now the min value
        thirdRetractor.run();
        assertResult(collector, container, firstValue);
        // retract last value; there are no values now
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void minComparableQuad() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Integer> collector = min((a, b, c, d) -> a + b + c + d);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // add first value, which becomes the min
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0, 0);
        assertResult(collector, container, 2);
        // add second value, lesser than the first, becomes the new min
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, 1);
        // add third value, same as the second, result does not change
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, 1);
        // retract one instance of the second value; second value is still the min value, nothing should change
        secondRetractor.run();
        assertResult(collector, container, 1);
        // retract final instance of the second value; first value is now the min value
        thirdRetractor.run();
        assertResult(collector, container, 2);
        // retract last value; there are no values now
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void minNotComparableQuad() {
        QuadConstraintCollector<String, String, String, String, ?, String> collector = min((a, b, c, d) -> a,
                Comparator.comparing(o -> o));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // add first value, which becomes the min
        String firstValue = "b";
        Runnable firstRetractor = accumulate(collector, container, firstValue, null, null, null);
        assertResult(collector, container, firstValue);
        // add second value, lesser than the first, becomes the new min
        String secondValue = "a";
        Runnable secondRetractor = accumulate(collector, container, secondValue, null, null, null);
        assertResult(collector, container, secondValue);
        // add third value, same as the second, result does not change
        Runnable thirdRetractor = accumulate(collector, container, secondValue, null, null, null);
        assertResult(collector, container, secondValue);
        // retract one instance of the second value; second value is still the min value, nothing should change
        secondRetractor.run();
        assertResult(collector, container, secondValue);
        // retract final instance of the second value; first value is now the min value
        thirdRetractor.run();
        assertResult(collector, container, firstValue);
        // retract last value; there are no values now
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    // ************************************************************************
    // max
    // ************************************************************************

    @Test
    void maxComparable() {
        UniConstraintCollector<Integer, ?, Integer> collector = max();
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // add first value, which becomes the max
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, firstValue);
        // add second value, lesser than the first, result does not change
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, firstValue);
        // add third value, same as the first, result does not change
        Runnable thirdRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, firstValue);
        // retract one instance of the first value; first value is still the max value, nothing should change
        firstRetractor.run();
        assertResult(collector, container, firstValue);
        // retract final instance of the first value; second value is now the max value
        thirdRetractor.run();
        assertResult(collector, container, secondValue);
        // retract last value; there are no values now
        secondRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void maxNotComparable() {
        UniConstraintCollector<String, ?, String> collector = max(Comparator.comparing(o -> o));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // add first value, which becomes the max
        String firstValue = "b";
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, firstValue);
        // add second value, lesser than the first, result does not change
        String secondValue = "a";
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, firstValue);
        // add third value, same as the first, result does not change
        Runnable thirdRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, firstValue);
        // retract one instance of the first value; first value is still the max value, nothing should change
        firstRetractor.run();
        assertResult(collector, container, firstValue);
        // retract final instance of the first value; second value is now the max value
        thirdRetractor.run();
        assertResult(collector, container, secondValue);
        // retract last value; there are no values now
        secondRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void maxComparableBi() {
        BiConstraintCollector<Integer, Integer, ?, Integer> collector = max(
                (BiFunction<Integer, Integer, Integer>) Integer::sum);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // add first value, which becomes the max
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0);
        assertResult(collector, container, 2);
        // add second value, lesser than the first, result does not change
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0);
        assertResult(collector, container, 2);
        // add third value, same as the first, result does not change
        Runnable thirdRetractor = accumulate(collector, container, firstValue, 0);
        assertResult(collector, container, 2);
        // retract one instance of the first value; first value is still the max value, nothing should change
        firstRetractor.run();
        assertResult(collector, container, 2);
        // retract final instance of the first value; second value is now the max value
        thirdRetractor.run();
        assertResult(collector, container, 1);
        // retract last value; there are no values now
        secondRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void maxNotComparableBi() {
        BiConstraintCollector<String, String, ?, String> collector = max((a, b) -> a, Comparator.comparing(o -> o));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // add first value, which becomes the max
        String firstValue = "b";
        Runnable firstRetractor = accumulate(collector, container, firstValue, null);
        assertResult(collector, container, firstValue);
        // add second value, lesser than the first, result does not change
        String secondValue = "a";
        Runnable secondRetractor = accumulate(collector, container, secondValue, null);
        assertResult(collector, container, firstValue);
        // add third value, same as the first, result does not change
        Runnable thirdRetractor = accumulate(collector, container, firstValue, null);
        assertResult(collector, container, firstValue);
        // retract one instance of the first value; first value is still the max value, nothing should change
        firstRetractor.run();
        assertResult(collector, container, firstValue);
        // retract final instance of the first value; second value is now the max value
        thirdRetractor.run();
        assertResult(collector, container, secondValue);
        // retract last value; there are no values now
        secondRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void maxComparableTri() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Integer> collector = max((a, b, c) -> a + b + c);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // add first value, which becomes the max
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0);
        assertResult(collector, container, 2);
        // add second value, lesser than the first, result does not change
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, 2);
        // add third value, same as the first, result does not change
        Runnable thirdRetractor = accumulate(collector, container, firstValue, 0, 0);
        assertResult(collector, container, 2);
        // retract one instance of the first value; first value is still the max value, nothing should change
        firstRetractor.run();
        assertResult(collector, container, 2);
        // retract final instance of the first value; second value is now the max value
        thirdRetractor.run();
        assertResult(collector, container, 1);
        // retract last value; there are no values now
        secondRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void maxNotComparableTri() {
        TriConstraintCollector<String, String, String, ?, String> collector = max((a, b, c) -> a,
                Comparator.comparing(o -> o));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // add first value, which becomes the max
        String firstValue = "b";
        Runnable firstRetractor = accumulate(collector, container, firstValue, null, null);
        assertResult(collector, container, firstValue);
        // add second value, lesser than the first, result does not change
        String secondValue = "a";
        Runnable secondRetractor = accumulate(collector, container, secondValue, null, null);
        assertResult(collector, container, firstValue);
        // add third value, same as the first, result does not change
        Runnable thirdRetractor = accumulate(collector, container, firstValue, null, null);
        assertResult(collector, container, firstValue);
        // retract one instance of the first value; first value is still the max value, nothing should change
        firstRetractor.run();
        assertResult(collector, container, firstValue);
        // retract final instance of the first value; second value is now the max value
        thirdRetractor.run();
        assertResult(collector, container, secondValue);
        // retract last value; there are no values now
        secondRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void maxComparableQuad() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Integer> collector = max((a, b, c, d) -> a + b + c + d);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // add first value, which becomes the max
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0, 0);
        assertResult(collector, container, 2);
        // add second value, lesser than the first, result does not change
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, 2);
        // add third value, same as the first, result does not change
        Runnable thirdRetractor = accumulate(collector, container, firstValue, 0, 0, 0);
        assertResult(collector, container, 2);
        // retract one instance of the first value; first value is still the max value, nothing should change
        firstRetractor.run();
        assertResult(collector, container, 2);
        // retract final instance of the first value; second value is now the max value
        thirdRetractor.run();
        assertResult(collector, container, 1);
        // retract last value; there are no values now
        secondRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void maxNotComparableQuad() {
        QuadConstraintCollector<String, String, String, String, ?, String> collector = max((a, b, c, d) -> a,
                Comparator.comparing(o -> o));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // add first value, which becomes the max
        String firstValue = "b";
        Runnable firstRetractor = accumulate(collector, container, firstValue, null, null, null);
        assertResult(collector, container, firstValue);
        // add second value, lesser than the first, result does not change
        String secondValue = "a";
        Runnable secondRetractor = accumulate(collector, container, secondValue, null, null, null);
        assertResult(collector, container, firstValue);
        // add third value, same as the first, result does not change
        Runnable thirdRetractor = accumulate(collector, container, firstValue, null, null, null);
        assertResult(collector, container, firstValue);
        // retract one instance of the first value; first value is still the max value, nothing should change
        firstRetractor.run();
        assertResult(collector, container, firstValue);
        // retract final instance of the first value; second value is now the max value
        thirdRetractor.run();
        assertResult(collector, container, secondValue);
        // retract last value; there are no values now
        secondRetractor.run();
        assertResult(collector, container, null);
    }

    // ************************************************************************
    // average
    // ************************************************************************

    @Test
    void average() {
        UniConstraintCollector<Integer, ?, Double> collector = ConstraintCollectors.average(i -> i);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // Add first value, we have one now.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, 4.0D);
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, 2.5D);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, 2.0D);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, 2.5D);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 4.0);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void averageLong() {
        UniConstraintCollector<Integer, ?, Double> collector = ConstraintCollectors.averageLong(i -> i);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // Add first value, we have one now.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, 4.0D);
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, 2.5D);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, 2.0D);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, 2.5D);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 4.0);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void averageBigDecimal() {
        UniConstraintCollector<Integer, ?, BigDecimal> collector =
                ConstraintCollectors.averageBigDecimal(i -> BigDecimal.valueOf(i));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // Add first value, we have one now.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, BigDecimal.valueOf(4));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, BigDecimal.valueOf(2)); // Scale = 0.
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, BigDecimal.valueOf(2));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, BigDecimal.valueOf(2)); // Scale = 0.
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, BigDecimal.valueOf(4));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void averageBigInteger() {
        UniConstraintCollector<Integer, ?, BigDecimal> collector =
                ConstraintCollectors.averageBigInteger(i -> BigInteger.valueOf(i));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // Add first value, we have one now.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, BigDecimal.valueOf(4));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, BigDecimal.valueOf(2)); // Scale = 0.
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, BigDecimal.valueOf(2));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, BigDecimal.valueOf(2)); // Scale = 0.
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, BigDecimal.valueOf(4));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void averageDuration() {
        UniConstraintCollector<Integer, ?, Duration> collector =
                ConstraintCollectors.averageDuration(i -> Duration.ofSeconds(i));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // Add first value, we have one now.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, Duration.ofSeconds(4));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, Duration.ofMillis(2500));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, Duration.ofSeconds(2));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, Duration.ofMillis(2500));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, Duration.ofSeconds(4));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void averageBi() {
        BiConstraintCollector<Integer, Integer, ?, Double> collector = ConstraintCollectors.average((i, i2) -> i + i2);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // Add first value, we have one now.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0);
        assertResult(collector, container, 4.0D);
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0);
        assertResult(collector, container, 2.5D);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0);
        assertResult(collector, container, 2.0D);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, 2.5D);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 4.0);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void averageBiLong() {
        BiConstraintCollector<Integer, Integer, ?, Double> collector =
                ConstraintCollectors.averageLong((i, i2) -> i + i2);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // Add first value, we have one now.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0);
        assertResult(collector, container, 4.0D);
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0);
        assertResult(collector, container, 2.5D);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0);
        assertResult(collector, container, 2.0D);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, 2.5D);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 4.0);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void averageBiBigDecimal() {
        BiConstraintCollector<Integer, Integer, ?, BigDecimal> collector =
                ConstraintCollectors.averageBigDecimal((i, i2) -> BigDecimal.valueOf(i + i2));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // Add first value, we have one now.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0);
        assertResult(collector, container, BigDecimal.valueOf(4));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0);
        assertResult(collector, container, BigDecimal.valueOf(2)); // Scale = 0.
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0);
        assertResult(collector, container, BigDecimal.valueOf(2));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, BigDecimal.valueOf(2)); // Scale = 0.
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, BigDecimal.valueOf(4));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void averageBiBigInteger() {
        BiConstraintCollector<Integer, Integer, ?, BigDecimal> collector =
                ConstraintCollectors.averageBigInteger((i, i2) -> BigInteger.valueOf(i + i2));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // Add first value, we have one now.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0);
        assertResult(collector, container, BigDecimal.valueOf(4));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0);
        assertResult(collector, container, BigDecimal.valueOf(2)); // Scale = 0.
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0);
        assertResult(collector, container, BigDecimal.valueOf(2));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, BigDecimal.valueOf(2)); // Scale = 0.
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, BigDecimal.valueOf(4));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void averageBiDuration() {
        BiConstraintCollector<Integer, Integer, ?, Duration> collector =
                ConstraintCollectors.averageDuration((i, i2) -> Duration.ofSeconds(i + i2));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // Add first value, we have one now.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0);
        assertResult(collector, container, Duration.ofSeconds(4));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0);
        assertResult(collector, container, Duration.ofMillis(2500));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0);
        assertResult(collector, container, Duration.ofSeconds(2));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, Duration.ofMillis(2500));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, Duration.ofSeconds(4));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void averageTri() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Double> collector =
                ConstraintCollectors.average((i, i2, i3) -> i + i2 + i3);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // Add first value, we have one now.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0);
        assertResult(collector, container, 4.0D);
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, 2.5D);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, 2.0D);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, 2.5D);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 4.0);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void averageTriLong() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Double> collector =
                ConstraintCollectors.averageLong((i, i2, i3) -> i + i2 + i3);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // Add first value, we have one now.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0);
        assertResult(collector, container, 4.0D);
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, 2.5D);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, 2.0D);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, 2.5D);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 4.0);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void averageTriBigDecimal() {
        TriConstraintCollector<Integer, Integer, Integer, ?, BigDecimal> collector =
                ConstraintCollectors.averageBigDecimal((i, i2, i3) -> BigDecimal.valueOf(i + i2 + i3));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // Add first value, we have one now.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0);
        assertResult(collector, container, BigDecimal.valueOf(4));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, BigDecimal.valueOf(2)); // Scale = 0.
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, BigDecimal.valueOf(2));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, BigDecimal.valueOf(2)); // Scale = 0.
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, BigDecimal.valueOf(4));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void averageTriBigInteger() {
        TriConstraintCollector<Integer, Integer, Integer, ?, BigDecimal> collector =
                ConstraintCollectors.averageBigInteger((i, i2, i3) -> BigInteger.valueOf(i + i2 + i3));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // Add first value, we have one now.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0);
        assertResult(collector, container, BigDecimal.valueOf(4));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, BigDecimal.valueOf(2)); // Scale = 0.
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, BigDecimal.valueOf(2));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, BigDecimal.valueOf(2)); // Scale = 0.
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, BigDecimal.valueOf(4));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void averageTriDuration() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Duration> collector =
                ConstraintCollectors.averageDuration((i, i2, i3) -> Duration.ofSeconds(i + i2 + i3));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // Add first value, we have one now.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0);
        assertResult(collector, container, Duration.ofSeconds(4));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, Duration.ofMillis(2500));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, Duration.ofSeconds(2));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, Duration.ofMillis(2500));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, Duration.ofSeconds(4));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void averageQuad() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Double> collector =
                ConstraintCollectors.average((i, i2, i3, i4) -> i + i2 + i3 + i4);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // Add first value, we have one now.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0, 0);
        assertResult(collector, container, 4.0D);
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, 2.5D);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, 2.0D);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, 2.5D);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 4.0);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void averageQuadLong() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Double> collector =
                ConstraintCollectors.averageLong((i, i2, i3, i4) -> i + i2 + i3 + i4);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // Add first value, we have one now.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0, 0);
        assertResult(collector, container, 4.0D);
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, 2.5D);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, 2.0D);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, 2.5D);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, 4.0);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void averageQuadBigDecimal() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, BigDecimal> collector =
                ConstraintCollectors.averageBigDecimal((i, i2, i3, i4) -> BigDecimal.valueOf(i + i2 + i3 + i4));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // Add first value, we have one now.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0, 0);
        assertResult(collector, container, BigDecimal.valueOf(4));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, BigDecimal.valueOf(2)); // Scale = 0.
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, BigDecimal.valueOf(2));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, BigDecimal.valueOf(2)); // Scale = 0.
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, BigDecimal.valueOf(4));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void averageQuadBigInteger() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, BigDecimal> collector =
                ConstraintCollectors.averageBigInteger((i, i2, i3, i4) -> BigInteger.valueOf(i + i2 + i3 + i4));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // Add first value, we have one now.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0, 0);
        assertResult(collector, container, BigDecimal.valueOf(4));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, BigDecimal.valueOf(2)); // Scale = 0.
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, BigDecimal.valueOf(2));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, BigDecimal.valueOf(2)); // Scale = 0.
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, BigDecimal.valueOf(4));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void averageQuadDuration() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Duration> collector =
                ConstraintCollectors.averageDuration((i, i2, i3, i4) -> Duration.ofSeconds(i + i2 + i3 + i4));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // Add first value, we have one now.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0, 0);
        assertResult(collector, container, Duration.ofSeconds(4));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, Duration.ofMillis(2500));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, Duration.ofSeconds(2));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, Duration.ofMillis(2500));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, Duration.ofSeconds(4));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, null);
    }

    // ************************************************************************
    // toCollection
    // ************************************************************************

    @Test
    void toSet() {
        UniConstraintCollector<Integer, ?, Set<Integer>> collector = ConstraintCollectors.toSet();
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptySet());
        // Add first value, we have one now.
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, singleton(firstValue));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, asSet(firstValue, secondValue));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, asSet(firstValue, secondValue));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asSet(firstValue, secondValue));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, singleton(firstValue));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptySet());
    }

    @Test
    void toSortedSet() {
        UniConstraintCollector<Integer, ?, SortedSet<Integer>> collector = ConstraintCollectors.toSortedSet();
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptySortedSet());
        // Add first value, we have one now.
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, asSortedSet(firstValue));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, asSortedSet(firstValue, secondValue));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, asSortedSet(firstValue, secondValue));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asSortedSet(firstValue, secondValue));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, asSortedSet(firstValue));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptySortedSet());
    }

    @Test
    void toList() {
        UniConstraintCollector<Integer, ?, List<Integer>> collector = ConstraintCollectors.toList();
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptyList());
        // Add first value, we have one now.
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, singletonList(firstValue));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, asList(firstValue, secondValue));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, asList(firstValue, secondValue, secondValue));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asList(firstValue, secondValue));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, singletonList(firstValue));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptyList());
    }

    @Test
    void toSetBi() {
        BiConstraintCollector<Integer, Integer, ?, Set<Integer>> collector = ConstraintCollectors.toSet(Integer::sum);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptySet());
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 1;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB);
        assertResult(collector, container, singleton(3));
        // Add second value, we have two now.
        int secondValueA = 3;
        int secondValueB = 4;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, asSet(3, 7));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, asSet(3, 7));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asSet(3, 7));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, singleton(3));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptySet());
    }

    @Test
    void toSortedSetBi() {
        BiConstraintCollector<Integer, Integer, ?, SortedSet<Integer>> collector =
                ConstraintCollectors.toSortedSet((BiFunction<Integer, Integer, Integer>) Integer::sum);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptySortedSet());
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 1;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB);
        assertResult(collector, container, asSortedSet(3));
        // Add second value, we have two now.
        int secondValueA = 3;
        int secondValueB = 4;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, asSortedSet(3, 7));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, asSortedSet(3, 7));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asSortedSet(3, 7));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, asSortedSet(3));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptySortedSet());
    }

    @Test
    void toListBi() {
        BiConstraintCollector<Integer, Integer, ?, List<Integer>> collector = ConstraintCollectors.toList(Integer::sum);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptyList());
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 1;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB);
        assertResult(collector, container, singletonList(3));
        // Add second value, we have two now.
        int secondValueA = 3;
        int secondValueB = 4;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, asList(3, 7));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, asList(3, 7, 7));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asList(3, 7));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, singletonList(3));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptyList());
    }

    @Test
    void toSetTri() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Set<Integer>> collector = ConstraintCollectors
                .toSet((a, b, c) -> a + b + c);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptySet());
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 1;
        int firstValueC = 3;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC);
        assertResult(collector, container, singleton(6));
        // Add second value, we have two now.
        int secondValueA = 3;
        int secondValueB = 4;
        int secondValueC = 2;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, asSet(6, 9));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, asSet(6, 9));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asSet(6, 9));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, singleton(6));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptySet());
    }

    @Test
    void toSortedSetTri() {
        TriConstraintCollector<Integer, Integer, Integer, ?, SortedSet<Integer>> collector = ConstraintCollectors
                .toSortedSet((a, b, c) -> a + b + c);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptySortedSet());
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 1;
        int firstValueC = 3;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC);
        assertResult(collector, container, asSortedSet(6));
        // Add second value, we have two now.
        int secondValueA = 3;
        int secondValueB = 4;
        int secondValueC = 2;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, asSortedSet(6, 9));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, asSortedSet(6, 9));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asSortedSet(6, 9));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, asSortedSet(6));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptySortedSet());
    }

    @Test
    void toListTri() {
        TriConstraintCollector<Integer, Integer, Integer, ?, List<Integer>> collector = ConstraintCollectors
                .toList((a, b, c) -> a + b + c);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptyList());
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 1;
        int firstValueC = 3;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC);
        assertResult(collector, container, singletonList(6));
        // Add second value, we have two now.
        int secondValueA = 3;
        int secondValueB = 4;
        int secondValueC = 2;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, asList(6, 9));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, asList(6, 9, 9));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asList(6, 9));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, singletonList(6));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptyList());
    }

    @Test
    void toSetQuad() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Set<Integer>> collector = ConstraintCollectors
                .toSet((a, b, c, d) -> a + b + c + d);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptySet());
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 1;
        int firstValueC = 3;
        int firstValueD = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC, firstValueD);
        assertResult(collector, container, singleton(10));
        // Add second value, we have two now.
        int secondValueA = 3;
        int secondValueB = 4;
        int secondValueC = 2;
        int secondValueD = 5;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC, secondValueD);
        assertResult(collector, container, asSet(10, 14));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC, secondValueD);
        assertResult(collector, container, asSet(10, 14));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asSet(10, 14));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, singleton(10));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptySet());
    }

    @Test
    void toSortedSetQuad() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, SortedSet<Integer>> collector = ConstraintCollectors
                .toSortedSet((a, b, c, d) -> a + b + c + d);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptySortedSet());
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 1;
        int firstValueC = 3;
        int firstValueD = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC, firstValueD);
        assertResult(collector, container, asSortedSet(10));
        // Add second value, we have two now.
        int secondValueA = 3;
        int secondValueB = 4;
        int secondValueC = 2;
        int secondValueD = 5;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC, secondValueD);
        assertResult(collector, container, asSortedSet(10, 14));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC, secondValueD);
        assertResult(collector, container, asSortedSet(10, 14));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asSortedSet(10, 14));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, asSortedSet(10));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptySortedSet());
    }

    @Test
    void toListQuad() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, List<Integer>> collector = ConstraintCollectors
                .toList((a, b, c, d) -> a + b + c + d);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptyList());
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 1;
        int firstValueC = 3;
        int firstValueD = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC, firstValueD);
        assertResult(collector, container, singletonList(10));
        // Add second value, we have two now.
        int secondValueA = 3;
        int secondValueB = 4;
        int secondValueC = 2;
        int secondValueD = 5;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC, secondValueD);
        assertResult(collector, container, asList(10, 14));
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC, secondValueD);
        assertResult(collector, container, asList(10, 14, 14));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asList(10, 14));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, singletonList(10));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptyList());
    }

    @Test
    void toMap() {
        UniConstraintCollector<Integer, ?, Map<Integer, Set<Integer>>> collector = ConstraintCollectors
                .toMap(Function.identity(), Function.identity());
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptyMap());
        // Add first value, we have one now.
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, asMap(2, singleton(2)));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, asMap(2, singleton(2), 1, singleton(1)));
        // Add third value, same as the second. We now have three values, two of which map to the same key.
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, asMap(2, singleton(2), 1, singleton(1)));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asMap(2, singleton(2), 1, singleton(1)));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, asMap(2, singleton(2)));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptyMap());
    }

    @Test
    void toMapMerged() {
        UniConstraintCollector<Integer, ?, Map<Integer, Integer>> collector = ConstraintCollectors.toMap(Function.identity(),
                Function.identity(), Integer::sum);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptyMap());
        // Add first value, we have one now.
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, asMap(2, 2));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, asMap(2, 2, 1, 1));
        // Add third value, same as the second. We now have three values, two of which map to the same key.
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, asMap(2, 2, 1, 1));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asMap(2, 2, 1, 1));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, asMap(2, 2));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptyMap());
    }

    @Test
    void toMapBi() {
        BiConstraintCollector<Integer, Integer, ?, Map<Integer, Set<Integer>>> collector = ConstraintCollectors
                .toMap(Integer::sum, Integer::sum);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptyMap());
        // Add first value, we have one now.
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0);
        assertResult(collector, container, asMap(2, singleton(2)));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0);
        assertResult(collector, container, asMap(2, singleton(2), 1, singleton(1)));
        // Add third value, same as the second. We now have three values, two of which map to the same key.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0);
        assertResult(collector, container, asMap(2, singleton(2), 1, singleton(1)));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asMap(2, singleton(2), 1, singleton(1)));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, asMap(2, singleton(2)));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptyMap());
    }

    @Test
    void toMapBiDuplicates() { // PLANNER-2271
        BiConstraintCollector<String, Integer, ?, Map<String, Set<Integer>>> collector =
                ConstraintCollectors.toMap((a, b) -> a, (a, b) -> b);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptyMap());
        // Add first value, we have one now.
        String firstValue = "A";
        Runnable firstRetractor = accumulate(collector, container, firstValue, 1);
        assertResult(collector, container, asMap("A", singleton(1)));
        // Add second value, we have two now.
        String secondValue = "B";
        Runnable secondRetractor = accumulate(collector, container, secondValue, 1);
        assertResult(collector, container, asMap("A", singleton(1), "B", singleton(1)));
        // Add third value, different from the second. We now have three values, two of which map to the same key.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 2);
        assertResult(collector, container, asMap("A", singleton(1), "B", asSet(1, 2)));
        // Retract one instance from the second key; we only have one there now, but still two keys.
        secondRetractor.run();
        assertResult(collector, container, asMap("A", singleton(1), "B", singleton(2)));
        // Retract final instance of the second value; we only have one key now.
        thirdRetractor.run();
        assertResult(collector, container, asMap("A", singleton(1)));
        // Retract last value; there are no keys now.
        firstRetractor.run();
        assertResult(collector, container, emptyMap());
    }

    @Test
    void toMapBiMerged() {
        BiConstraintCollector<Integer, Integer, ?, Map<Integer, Integer>> collector = ConstraintCollectors.toMap(Integer::sum,
                Integer::sum, Integer::sum);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptyMap());
        // Add first value, we have one now.
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0);
        assertResult(collector, container, asMap(2, 2));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0);
        assertResult(collector, container, asMap(2, 2, 1, 1));
        // Add third value, same as the second. We now have three values, two of which map to the same key.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0);
        assertResult(collector, container, asMap(2, 2, 1, 1));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asMap(2, 2, 1, 1));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, asMap(2, 2));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptyMap());
    }

    @Test
    void toMapTri() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Map<Integer, Set<Integer>>> collector = ConstraintCollectors
                .toMap((a, b, c) -> a + b + c, (a, b, c) -> a + b + c);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptyMap());
        // Add first value, we have one now.
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0);
        assertResult(collector, container, asMap(2, singleton(2)));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, asMap(2, singleton(2), 1, singleton(1)));
        // Add third value, same as the second. We now have three values, two of which map to the same key.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, asMap(2, singleton(2), 1, singleton(1)));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asMap(2, singleton(2), 1, singleton(1)));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, asMap(2, singleton(2)));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptyMap());
    }

    @Test
    void toMapTriMerged() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Map<Integer, Integer>> collector = ConstraintCollectors
                .toMap((a, b, c) -> a + b + c, (a, b, c) -> a + b + c, Integer::sum);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptyMap());
        // Add first value, we have one now.
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0);
        assertResult(collector, container, asMap(2, 2));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, asMap(2, 2, 1, 1));
        // Add third value, same as the second. We now have three values, two of which map to the same key.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, asMap(2, 2, 1, 1));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asMap(2, 2, 1, 1));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, asMap(2, 2));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptyMap());
    }

    @Test
    void toMapQuad() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Map<Integer, Set<Integer>>> collector =
                ConstraintCollectors.toMap((a, b, c, d) -> a + b + c + d, (a, b, c, d) -> a + b + c + d);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptyMap());
        // Add first value, we have one now.
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0, 0);
        assertResult(collector, container, asMap(2, singleton(2)));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, asMap(2, singleton(2), 1, singleton(1)));
        // Add third value, same as the second. We now have three values, two of which map to the same key.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, asMap(2, singleton(2), 1, singleton(1)));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asMap(2, singleton(2), 1, singleton(1)));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, asMap(2, singleton(2)));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptyMap());
    }

    @Test
    void toMapQuadMerged() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Map<Integer, Integer>> collector = ConstraintCollectors
                .toMap((a, b, c, d) -> a + b + c + d, (a, b, c, d) -> a + b + c + d, Integer::sum);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptyMap());
        // Add first value, we have one now.
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0, 0);
        assertResult(collector, container, asMap(2, 2));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, asMap(2, 2, 1, 1));
        // Add third value, same as the second. We now have three values, two of which map to the same key.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, asMap(2, 2, 1, 1));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asMap(2, 2, 1, 1));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, asMap(2, 2));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptyMap());
    }

    @Test
    void toSortedMap() {
        UniConstraintCollector<Integer, ?, SortedMap<Integer, Set<Integer>>> collector = ConstraintCollectors
                .toSortedMap(a -> a, Function.identity());
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptySortedMap());
        // Add first value, we have one now.
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, asSortedMap(2, singleton(2)));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, asSortedMap(2, singleton(2), 1, singleton(1)));
        // Add third value, same as the second. We now have three values, two of which map to the same key.
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, asSortedMap(2, singleton(2), 1, singleton(1)));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asSortedMap(2, singleton(2), 1, singleton(1)));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, asSortedMap(2, singleton(2)));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptySortedMap());
    }

    @Test
    void toSortedMapMerged() {
        UniConstraintCollector<Integer, ?, SortedMap<Integer, Integer>> collector = ConstraintCollectors.toSortedMap(a -> a,
                Function.identity(), Integer::sum);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptySortedMap());
        // Add first value, we have one now.
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, asSortedMap(2, 2));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, asSortedMap(2, 2, 1, 1));
        // Add third value, same as the second. We now have three values, two of which map to the same key.
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, asSortedMap(2, 2, 1, 1));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asSortedMap(2, 2, 1, 1));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, asSortedMap(2, 2));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptySortedMap());
    }

    @Test
    void toSortedMapBi() {
        BiConstraintCollector<Integer, Integer, ?, SortedMap<Integer, Set<Integer>>> collector = ConstraintCollectors
                .toSortedMap(Integer::sum, Integer::sum);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptySortedMap());
        // Add first value, we have one now.
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0);
        assertResult(collector, container, asSortedMap(2, singleton(2)));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0);
        assertResult(collector, container, asSortedMap(2, singleton(2), 1, singleton(1)));
        // Add third value, same as the second. We now have three values, two of which map to the same key.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0);
        assertResult(collector, container, asSortedMap(2, singleton(2), 1, singleton(1)));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asSortedMap(2, singleton(2), 1, singleton(1)));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, asSortedMap(2, singleton(2)));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptySortedMap());
    }

    @Test
    void toSortedMapBiMerged() {
        BiConstraintCollector<Integer, Integer, ?, SortedMap<Integer, Integer>> collector = ConstraintCollectors
                .toSortedMap(Integer::sum, Integer::sum, Integer::sum);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptySortedMap());
        // Add first value, we have one now.
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0);
        assertResult(collector, container, asSortedMap(2, 2));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0);
        assertResult(collector, container, asSortedMap(2, 2, 1, 1));
        // Add third value, same as the second. We now have three values, two of which map to the same key.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0);
        assertResult(collector, container, asSortedMap(2, 2, 1, 1));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asSortedMap(2, 2, 1, 1));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, asSortedMap(2, 2));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptySortedMap());
    }

    @Test
    void toSortedMapTri() {
        TriConstraintCollector<Integer, Integer, Integer, ?, SortedMap<Integer, Set<Integer>>> collector = ConstraintCollectors
                .toSortedMap((a, b, c) -> a + b + c, (a, b, c) -> a + b + c);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptySortedMap());
        // Add first value, we have one now.
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0);
        assertResult(collector, container, asSortedMap(2, singleton(2)));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, asSortedMap(2, singleton(2), 1, singleton(1)));
        // Add third value, same as the second. We now have three values, two of which map to the same key.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, asSortedMap(2, singleton(2), 1, singleton(1)));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asSortedMap(2, singleton(2), 1, singleton(1)));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, asSortedMap(2, singleton(2)));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptySortedMap());
    }

    @Test
    void toSortedMapTriMerged() {
        TriConstraintCollector<Integer, Integer, Integer, ?, SortedMap<Integer, Integer>> collector = ConstraintCollectors
                .toSortedMap((a, b, c) -> a + b + c, (a, b, c) -> a + b + c, Integer::sum);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptySortedMap());
        // Add first value, we have one now.
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0);
        assertResult(collector, container, asSortedMap(2, 2));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, asSortedMap(2, 2, 1, 1));
        // Add third value, same as the second. We now have three values, two of which map to the same key.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, asSortedMap(2, 2, 1, 1));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asSortedMap(2, 2, 1, 1));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, asSortedMap(2, 2));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptySortedMap());
    }

    @Test
    void toSortedMapQuad() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, SortedMap<Integer, Set<Integer>>> collector =
                ConstraintCollectors.toSortedMap((a, b, c, d) -> a + b + c + d, (a, b, c, d) -> a + b + c + d);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptySortedMap());
        // Add first value, we have one now.
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0, 0);
        assertResult(collector, container, asSortedMap(2, singleton(2)));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, asSortedMap(2, singleton(2), 1, singleton(1)));
        // Add third value, same as the second. We now have three values, two of which map to the same key.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, asSortedMap(2, singleton(2), 1, singleton(1)));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asSortedMap(2, singleton(2), 1, singleton(1)));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, asSortedMap(2, singleton(2)));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptySortedMap());
    }

    @Test
    void toSortedMapQuadMerged() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, SortedMap<Integer, Integer>> collector =
                ConstraintCollectors.toSortedMap((a, b, c, d) -> a + b + c + d, (a, b, c, d) -> a + b + c + d, Integer::sum);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, emptySortedMap());
        // Add first value, we have one now.
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0, 0);
        assertResult(collector, container, asSortedMap(2, 2));
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, asSortedMap(2, 2, 1, 1));
        // Add third value, same as the second. We now have three values, two of which map to the same key.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, asSortedMap(2, 2, 1, 1));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, asSortedMap(2, 2, 1, 1));
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, asSortedMap(2, 2));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, emptySortedMap());
    }

    @Test
    void conditionally() {
        UniConstraintCollector<Integer, Object, Integer> collector = ConstraintCollectors.conditionally(
                (Integer i) -> i > 1,
                min());
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // Add first value, we have one now.
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, 2);
        // Add second value; it is skipped even though it is lesser than the first value.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, 2);
        // Add third value, same as the first. We now have two values, both of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, 2);
        // Retract one instance of the first value; we only have one value now.
        firstRetractor.run();
        assertResult(collector, container, 2);
        // Retract the skipped value.
        secondRetractor.run();
        assertResult(collector, container, 2);
        // Retract last value; there are no values now.
        thirdRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void conditionallyBi() {
        BiConstraintCollector<Integer, Integer, Object, Integer> collector = ConstraintCollectors.conditionally(
                (i, i2) -> i < 2,
                max(Integer::sum, Integer::compareTo));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // Add first value, we have one now.
        int firstValue = 1;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0);
        assertResult(collector, container, 1);
        // Add second value; it is skipped even though it is greater than the first value.
        int secondValue = 2;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0);
        assertResult(collector, container, 1);
        // Add third value, same as the first. We now have two values, both of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, firstValue, 0);
        assertResult(collector, container, 1);
        // Retract one instance of the first value; we only have one value now.
        firstRetractor.run();
        assertResult(collector, container, 1);
        // Retract the skipped value.
        secondRetractor.run();
        assertResult(collector, container, 1);
        // Retract last value; there are no values now.
        thirdRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void conditionallyTri() {
        TriConstraintCollector<Integer, Integer, Integer, Object, Integer> collector =
                ConstraintCollectors.conditionally(
                        (i, i2, i3) -> i < 2,
                        max((i, i2, i3) -> i + i2 + i3, Integer::compareTo));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // Add first value, we have one now.
        int firstValue = 1;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0);
        assertResult(collector, container, 1);
        // Add second value; it is skipped even though it is greater than the first value.
        int secondValue = 2;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, 1);
        // Add third value, same as the first. We now have two values, both of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, firstValue, 0, 0);
        assertResult(collector, container, 1);
        // Retract one instance of the first value; we only have one value now.
        firstRetractor.run();
        assertResult(collector, container, 1);
        // Retract the skipped value.
        secondRetractor.run();
        assertResult(collector, container, 1);
        // Retract last value; there are no values now.
        thirdRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void conditionallyQuad() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, Object, Integer> collector =
                ConstraintCollectors.conditionally(
                        (i, i2, i3, i4) -> i < 2,
                        max((i, i2, i3, i4) -> i + i2 + i3 + i4, Integer::compareTo));
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, null);
        // Add first value, we have one now.
        int firstValue = 1;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0, 0);
        assertResult(collector, container, 1);
        // Add second value; it is skipped even though it is greater than the first value.
        int secondValue = 2;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, 1);
        // Add third value, same as the first. We now have two values, both of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, firstValue, 0, 0, 0);
        assertResult(collector, container, 1);
        // Retract one instance of the first value; we only have one value now.
        firstRetractor.run();
        assertResult(collector, container, 1);
        // Retract the skipped value.
        secondRetractor.run();
        assertResult(collector, container, 1);
        // Retract last value; there are no values now.
        thirdRetractor.run();
        assertResult(collector, container, null);
    }

    @Test
    void compose2() {
        UniConstraintCollector<Integer, ?, Pair<Integer, Integer>> collector =
                compose(min(i -> i), max(i -> i),
                        (BiFunction<Integer, Integer, Pair<Integer, Integer>>) Pair::of);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, Pair.of(null, null));
        // Add first value.
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, Pair.of(2, 2));
        // Add second value, lesser than the first.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, Pair.of(1, 2));
        // Add third value, same as the second, result does not change.
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, Pair.of(1, 2));
        // Retract one instance of the second value; nothing should change.
        secondRetractor.run();
        assertResult(collector, container, Pair.of(1, 2));
        // Retract final instance of the second value.
        thirdRetractor.run();
        assertResult(collector, container, Pair.of(2, 2));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, Pair.of(null, null));
    }

    @Test
    void compose3() {
        UniConstraintCollector<Integer, ?, Triple<Integer, Integer, Double>> collector =
                compose(min(i -> i), max(i -> i), ConstraintCollectors.average(i -> i),
                        (TriFunction<Integer, Integer, Double, Triple<Integer, Integer, Double>>) Triple::of);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, Triple.of(null, null, null));
        // Add first value.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, Triple.of(4, 4, 4D));
        // Add second value, lesser than the first.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, Triple.of(1, 4, 2.5D));
        // Add third value, same as the second.
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, Triple.of(1, 4, 2D));
        // Retract one instance of the second value.
        secondRetractor.run();
        assertResult(collector, container, Triple.of(1, 4, 2.5D));
        // Retract final instance of the second value.
        thirdRetractor.run();
        assertResult(collector, container, Triple.of(4, 4, 4D));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, Triple.of(null, null, null));
    }

    @Test
    void compose4() {
        UniConstraintCollector<Integer, ?, Quadruple<Integer, Integer, Integer, Double>> collector =
                compose(ConstraintCollectors.count(), min(i -> i), max(i -> i),
                        ConstraintCollectors.average(i -> i),
                        (QuadFunction<Integer, Integer, Integer, Double, Quadruple<Integer, Integer, Integer, Double>>) Quadruple::of);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, Quadruple.of(0, null, null, null));
        // Add first value.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, Quadruple.of(1, 4, 4, 4D));
        // Add second value, lesser than the first.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, Quadruple.of(2, 1, 4, 2.5D));
        // Add third value, same as the second.
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, Quadruple.of(3, 1, 4, 2D));
        // Retract one instance of the second value.
        secondRetractor.run();
        assertResult(collector, container, Quadruple.of(2, 1, 4, 2.5D));
        // Retract final instance of the second value.
        thirdRetractor.run();
        assertResult(collector, container, Quadruple.of(1, 4, 4, 4D));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, Quadruple.of(0, null, null, null));
    }

    @Test
    void compose2Bi() {
        BiConstraintCollector<Integer, Integer, ?, Pair<Integer, Integer>> collector =
                compose(min(Integer::sum, Integer::compareTo),
                        max(Integer::sum, Integer::compareTo),
                        Pair::of);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, Pair.of(null, null));
        // Add first value.
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0);
        assertResult(collector, container, Pair.of(2, 2));
        // Add second value, lesser than the first.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0);
        assertResult(collector, container, Pair.of(1, 2));
        // Add third value, same as the second, result does not change.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0);
        assertResult(collector, container, Pair.of(1, 2));
        // Retract one instance of the second value; nothing should change.
        secondRetractor.run();
        assertResult(collector, container, Pair.of(1, 2));
        // Retract final instance of the second value.
        thirdRetractor.run();
        assertResult(collector, container, Pair.of(2, 2));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, Pair.of(null, null));
    }

    @Test
    void compose3Bi() {
        BiConstraintCollector<Integer, Integer, ?, Triple<Integer, Integer, Double>> collector =
                compose(min(Integer::sum, Integer::compareTo),
                        max(Integer::sum, Integer::compareTo),
                        ConstraintCollectors.average(Integer::sum),
                        Triple::of);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, Triple.of(null, null, null));
        // Add first value.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0);
        assertResult(collector, container, Triple.of(4, 4, 4D));
        // Add second value, lesser than the first.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0);
        assertResult(collector, container, Triple.of(1, 4, 2.5D));
        // Add third value, same as the second.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0);
        assertResult(collector, container, Triple.of(1, 4, 2D));
        // Retract one instance of the second value.
        secondRetractor.run();
        assertResult(collector, container, Triple.of(1, 4, 2.5D));
        // Retract final instance of the second value.
        thirdRetractor.run();
        assertResult(collector, container, Triple.of(4, 4, 4D));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, Triple.of(null, null, null));
    }

    @Test
    void compose4Bi() {
        BiConstraintCollector<Integer, Integer, ?, Quadruple<Integer, Integer, Integer, Double>> collector =
                compose(ConstraintCollectors.countBi(),
                        min(Integer::sum, Integer::compareTo),
                        max(Integer::sum, Integer::compareTo),
                        ConstraintCollectors.average(Integer::sum),
                        Quadruple::of);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, Quadruple.of(0, null, null, null));
        // Add first value.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0);
        assertResult(collector, container, Quadruple.of(1, 4, 4, 4D));
        // Add second value, lesser than the first.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0);
        assertResult(collector, container, Quadruple.of(2, 1, 4, 2.5D));
        // Add third value, same as the second.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0);
        assertResult(collector, container, Quadruple.of(3, 1, 4, 2D));
        // Retract one instance of the second value.
        secondRetractor.run();
        assertResult(collector, container, Quadruple.of(2, 1, 4, 2.5D));
        // Retract final instance of the second value.
        thirdRetractor.run();
        assertResult(collector, container, Quadruple.of(1, 4, 4, 4D));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, Quadruple.of(0, null, null, null));
    }

    @Test
    void compose2Tri() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Pair<Integer, Integer>> collector =
                compose(min((i, i2, i3) -> i + i2 + i3, Integer::compareTo),
                        max((i, i2, i3) -> i + i2 + i3, Integer::compareTo),
                        Pair::of);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, Pair.of(null, null));
        // Add first value.
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0);
        assertResult(collector, container, Pair.of(2, 2));
        // Add second value, lesser than the first.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, Pair.of(1, 2));
        // Add third value, same as the second, result does not change.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, Pair.of(1, 2));
        // Retract one instance of the second value; nothing should change.
        secondRetractor.run();
        assertResult(collector, container, Pair.of(1, 2));
        // Retract final instance of the second value.
        thirdRetractor.run();
        assertResult(collector, container, Pair.of(2, 2));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, Pair.of(null, null));
    }

    @Test
    void compose3Tri() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Triple<Integer, Integer, Double>> collector =
                compose(min((i, i2, i3) -> i + i2 + i3, Integer::compareTo),
                        max((i, i2, i3) -> i + i2 + i3, Integer::compareTo),
                        ConstraintCollectors.average((i, i2, i3) -> i + i2 + i3),
                        Triple::of);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, Triple.of(null, null, null));
        // Add first value.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0);
        assertResult(collector, container, Triple.of(4, 4, 4D));
        // Add second value, lesser than the first.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, Triple.of(1, 4, 2.5D));
        // Add third value, same as the second.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, Triple.of(1, 4, 2D));
        // Retract one instance of the second value.
        secondRetractor.run();
        assertResult(collector, container, Triple.of(1, 4, 2.5D));
        // Retract final instance of the second value.
        thirdRetractor.run();
        assertResult(collector, container, Triple.of(4, 4, 4D));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, Triple.of(null, null, null));
    }

    @Test
    void compose4Tri() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Quadruple<Integer, Integer, Integer, Double>> collector =
                compose(ConstraintCollectors.countTri(),
                        min((i, i2, i3) -> i + i2 + i3, Integer::compareTo),
                        max((i, i2, i3) -> i + i2 + i3, Integer::compareTo),
                        ConstraintCollectors.average((i, i2, i3) -> i + i2 + i3),
                        Quadruple::of);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, Quadruple.of(0, null, null, null));
        // Add first value.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0);
        assertResult(collector, container, Quadruple.of(1, 4, 4, 4D));
        // Add second value, lesser than the first.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, Quadruple.of(2, 1, 4, 2.5D));
        // Add third value, same as the second.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0, 0);
        assertResult(collector, container, Quadruple.of(3, 1, 4, 2D));
        // Retract one instance of the second value.
        secondRetractor.run();
        assertResult(collector, container, Quadruple.of(2, 1, 4, 2.5D));
        // Retract final instance of the second value.
        thirdRetractor.run();
        assertResult(collector, container, Quadruple.of(1, 4, 4, 4D));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, Quadruple.of(0, null, null, null));
    }

    @Test
    void compose2Quad() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Pair<Integer, Integer>> collector =
                compose(min((i, i2, i3, i4) -> i + i2 + i3 + i4, Integer::compareTo),
                        max((i, i2, i3, i4) -> i + i2 + i3 + i4, Integer::compareTo),
                        Pair::of);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, Pair.of(null, null));
        // Add first value.
        int firstValue = 2;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0, 0);
        assertResult(collector, container, Pair.of(2, 2));
        // Add second value, lesser than the first.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, Pair.of(1, 2));
        // Add third value, same as the second, result does not change.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, Pair.of(1, 2));
        // Retract one instance of the second value; nothing should change.
        secondRetractor.run();
        assertResult(collector, container, Pair.of(1, 2));
        // Retract final instance of the second value.
        thirdRetractor.run();
        assertResult(collector, container, Pair.of(2, 2));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, Pair.of(null, null));
    }

    @Test
    void compose3Quad() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Triple<Integer, Integer, Double>> collector =
                compose(min((i, i2, i3, i4) -> i + i2 + i3 + i4, Integer::compareTo),
                        max((i, i2, i3, i4) -> i + i2 + i3 + i4, Integer::compareTo),
                        ConstraintCollectors.average((i, i2, i3, i4) -> i + i2 + i3 + i4),
                        Triple::of);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, Triple.of(null, null, null));
        // Add first value.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0, 0);
        assertResult(collector, container, Triple.of(4, 4, 4D));
        // Add second value, lesser than the first.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, Triple.of(1, 4, 2.5D));
        // Add third value, same as the second.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, Triple.of(1, 4, 2D));
        // Retract one instance of the second value.
        secondRetractor.run();
        assertResult(collector, container, Triple.of(1, 4, 2.5D));
        // Retract final instance of the second value.
        thirdRetractor.run();
        assertResult(collector, container, Triple.of(4, 4, 4D));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, Triple.of(null, null, null));
    }

    @Test
    void compose4Quad() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Quadruple<Integer, Integer, Integer, Double>> collector =
                compose(ConstraintCollectors.countQuad(),
                        min((i, i2, i3, i4) -> i + i2 + i3 + i4, Integer::compareTo),
                        max((i, i2, i3, i4) -> i + i2 + i3 + i4, Integer::compareTo),
                        ConstraintCollectors.average((i, i2, i3, i4) -> i + i2 + i3 + i4),
                        Quadruple::of);
        Object container = collector.supplier().get();

        // Default state.
        assertResult(collector, container, Quadruple.of(0, null, null, null));
        // Add first value.
        int firstValue = 4;
        Runnable firstRetractor = accumulate(collector, container, firstValue, 0, 0, 0);
        assertResult(collector, container, Quadruple.of(1, 4, 4, 4D));
        // Add second value, lesser than the first.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, Quadruple.of(2, 1, 4, 2.5D));
        // Add third value, same as the second.
        Runnable thirdRetractor = accumulate(collector, container, secondValue, 0, 0, 0);
        assertResult(collector, container, Quadruple.of(3, 1, 4, 2D));
        // Retract one instance of the second value.
        secondRetractor.run();
        assertResult(collector, container, Quadruple.of(2, 1, 4, 2.5D));
        // Retract final instance of the second value.
        thirdRetractor.run();
        assertResult(collector, container, Quadruple.of(1, 4, 4, 4D));
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, Quadruple.of(0, null, null, null));
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
                .isEqualTo(expectedResult);
    }

}
