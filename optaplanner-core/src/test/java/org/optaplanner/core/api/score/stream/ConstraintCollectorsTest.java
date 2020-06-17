/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;

public class ConstraintCollectorsTest {

    // ************************************************************************
    // count
    // ************************************************************************

    @Test
    public void count() {
        UniConstraintCollector<Integer, ?, Integer> collector = ConstraintCollectors.count();
        Object container = collector.supplier().get();
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
    public void countLong() {
        UniConstraintCollector<Long, ?, Long> collector = ConstraintCollectors.countLong();
        Object container = collector.supplier().get();
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
    public void countBi() {
        BiConstraintCollector<Integer, Integer, ?, Integer> collector = ConstraintCollectors.countBi();
        Object container = collector.supplier().get();
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
    public void countBiLong() {
        BiConstraintCollector<Integer, Integer, ?, Long> collector = countLongBi();
        Object container = collector.supplier().get();
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
    public void countTri() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Integer> collector = ConstraintCollectors.countTri();
        Object container = collector.supplier().get();
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
    public void countTriLong() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Long> collector = countLongTri();
        Object container = collector.supplier().get();
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
    public void countQuad() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Integer> collector = ConstraintCollectors.countQuad();
        Object container = collector.supplier().get();
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
    public void countQuadLong() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Long> collector = countLongQuad();
        Object container = collector.supplier().get();
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
    public void countDistinct() {
        UniConstraintCollector<Integer, ?, Integer> collector = ConstraintCollectors.countDistinct();
        Object container = collector.supplier().get();
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
    public void countDistinctLong() {
        UniConstraintCollector<Long, ?, Long> collector = ConstraintCollectors.countDistinctLong(Function.identity());
        Object container = collector.supplier().get();
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
    public void countDistinctBi() {
        BiConstraintCollector<Integer, Integer, ?, Integer> collector = ConstraintCollectors.countDistinct((a, b) -> a + b);
        Object container = collector.supplier().get();
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
    public void countDistinctBiLong() {
        BiConstraintCollector<Integer, Integer, ?, Long> collector = ConstraintCollectors.countDistinctLong((a, b) -> a + b);
        Object container = collector.supplier().get();
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
    public void countDistinctTri() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Integer> collector = ConstraintCollectors
                .countDistinct((a, b, c) -> a + b + c);
        Object container = collector.supplier().get();
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
    public void countDistinctTriLong() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Long> collector = ConstraintCollectors
                .countDistinctLong((a, b, c) -> a + b + c);
        Object container = collector.supplier().get();
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
    public void countDistinctQuad() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Integer> collector = ConstraintCollectors
                .countDistinct((a, b, c, d) -> a + b + c + d);
        Object container = collector.supplier().get();
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
    public void countDistinctQuadLong() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Long> collector = ConstraintCollectors
                .countDistinctLong((a, b, c, d) -> a + b + c + d);
        Object container = collector.supplier().get();
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
    public void sum() {
        UniConstraintCollector<Integer, ?, Integer> collector = ConstraintCollectors.sum(i -> i);
        Object container = collector.supplier().get();
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
    public void sumLong() {
        UniConstraintCollector<Long, ?, Long> collector = ConstraintCollectors.sumLong(l -> l);
        Object container = collector.supplier().get();
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
    public void sumBigDecimal() {
        UniConstraintCollector<BigDecimal, ?, BigDecimal> collector = ConstraintCollectors.sumBigDecimal(l -> l);
        Object container = collector.supplier().get();
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
    public void sumBigInteger() {
        UniConstraintCollector<BigInteger, ?, BigInteger> collector = ConstraintCollectors.sumBigInteger(l -> l);
        Object container = collector.supplier().get();
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
    public void sumDuration() {
        UniConstraintCollector<Duration, ?, Duration> collector = ConstraintCollectors.sumDuration(l -> l);
        Object container = collector.supplier().get();
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
    public void sumPeriod() {
        UniConstraintCollector<Period, ?, Period> collector = ConstraintCollectors.sumPeriod(l -> l);
        Object container = collector.supplier().get();
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
    public void sumBi() {
        BiConstraintCollector<Integer, Integer, ?, Integer> collector = ConstraintCollectors.sum(Integer::sum);
        Object container = collector.supplier().get();
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
    public void sumBiLong() {
        BiConstraintCollector<Integer, Integer, ?, Long> collector = ConstraintCollectors.sumLong(Integer::sum);
        Object container = collector.supplier().get();
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
    public void sumBiBigDecimal() {
        BiConstraintCollector<Integer, Integer, ?, BigDecimal> collector = ConstraintCollectors
                .sumBigDecimal((a, b) -> BigDecimal.valueOf(a + b));
        Object container = collector.supplier().get();
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
    public void sumBiBigInteger() {
        BiConstraintCollector<Integer, Integer, ?, BigInteger> collector = ConstraintCollectors
                .sumBigInteger((a, b) -> BigInteger.valueOf(a + b));
        Object container = collector.supplier().get();
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
    public void sumBiDuration() {
        BiConstraintCollector<Integer, Integer, ?, Duration> collector = ConstraintCollectors
                .sumDuration((a, b) -> Duration.ofSeconds(a + b));
        Object container = collector.supplier().get();
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
    public void sumBiPeriod() {
        BiConstraintCollector<Integer, Integer, ?, Period> collector = ConstraintCollectors
                .sumPeriod((a, b) -> Period.ofDays(a + b));
        Object container = collector.supplier().get();
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
    public void sumTri() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Integer> collector = ConstraintCollectors
                .sum((a, b, c) -> a + b + c);
        Object container = collector.supplier().get();
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
    public void sumTriLong() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Long> collector = ConstraintCollectors
                .sumLong((a, b, c) -> a + b + c);
        Object container = collector.supplier().get();
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
    public void sumTriBigDecimal() {
        TriConstraintCollector<Integer, Integer, Integer, ?, BigDecimal> collector = ConstraintCollectors
                .sumBigDecimal((a, b, c) -> BigDecimal.valueOf(a + b + c));
        Object container = collector.supplier().get();
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
    public void sumTriBigInteger() {
        TriConstraintCollector<Integer, Integer, Integer, ?, BigInteger> collector = ConstraintCollectors
                .sumBigInteger((a, b, c) -> BigInteger.valueOf(a + b + c));
        Object container = collector.supplier().get();
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
    public void sumTriDuration() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Duration> collector = ConstraintCollectors
                .sumDuration((a, b, c) -> Duration.ofSeconds(a + b + c));
        Object container = collector.supplier().get();
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
    public void sumTriPeriod() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Period> collector = ConstraintCollectors
                .sumPeriod((a, b, c) -> Period.ofDays(a + b + c));
        Object container = collector.supplier().get();
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
    public void sumQuad() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Integer> collector = ConstraintCollectors
                .sum((a, b, c, d) -> a + b + c + d);
        Object container = collector.supplier().get();
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
    public void sumQuadLong() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Long> collector = ConstraintCollectors
                .sumLong((a, b, c, d) -> a + b + c + d);
        Object container = collector.supplier().get();
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
    public void sumQuadBigDecimal() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, BigDecimal> collector = ConstraintCollectors
                .sumBigDecimal((a, b, c, d) -> BigDecimal.valueOf(a + b + c + d));
        Object container = collector.supplier().get();
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
    public void sumQuadBigInteger() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, BigInteger> collector = ConstraintCollectors
                .sumBigInteger((a, b, c, d) -> BigInteger.valueOf(a + b + c + d));
        Object container = collector.supplier().get();
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
    public void sumQuadDuration() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Duration> collector = ConstraintCollectors
                .sumDuration((a, b, c, d) -> Duration.ofSeconds(a + b + c + d));
        Object container = collector.supplier().get();
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
    public void sumQuadPeriod() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Period> collector = ConstraintCollectors
                .sumPeriod((a, b, c, d) -> Period.ofDays(a + b + c + d));
        Object container = collector.supplier().get();
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
    public void minComparable() {
        UniConstraintCollector<Integer, ?, Integer> collector = min();
        Object container = collector.supplier().get();
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
    public void minNotComparable() {
        UniConstraintCollector<Object, ?, Object> collector = min(Comparator.comparing(o -> (String) o));
        Object container = collector.supplier().get();
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
    public void minComparableBi() {
        BiConstraintCollector<Integer, Integer, ?, Integer> collector = min(
                (BiFunction<Integer, Integer, Integer>) Integer::sum);
        Object container = collector.supplier().get();
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
    public void minNotComparableBi() {
        BiConstraintCollector<String, String, ?, String> collector = min((a, b) -> a, Comparator.comparing(o -> (String) o));
        Object container = collector.supplier().get();
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
    public void minComparableTri() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Integer> collector = min((a, b, c) -> a + b + c);
        Object container = collector.supplier().get();
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
    public void minNotComparableTri() {
        TriConstraintCollector<String, String, String, ?, String> collector = min((a, b, c) -> a,
                Comparator.comparing(o -> (String) o));
        Object container = collector.supplier().get();
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
    public void minComparableQuad() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Integer> collector = min((a, b, c, d) -> a + b + c + d);
        Object container = collector.supplier().get();
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
    public void minNotComparableQuad() {
        QuadConstraintCollector<String, String, String, String, ?, String> collector = min((a, b, c, d) -> a,
                Comparator.comparing(o -> (String) o));
        Object container = collector.supplier().get();
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
    public void maxComparable() {
        UniConstraintCollector<Integer, ?, Integer> collector = max();
        Object container = collector.supplier().get();
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
    public void maxNotComparable() {
        UniConstraintCollector<String, ?, String> collector = max(Comparator.comparing(o -> (String) o));
        Object container = collector.supplier().get();
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
    public void maxComparableBi() {
        BiConstraintCollector<Integer, Integer, ?, Integer> collector = max(
                (BiFunction<Integer, Integer, Integer>) Integer::sum);
        Object container = collector.supplier().get();
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
    public void maxNotComparableBi() {
        BiConstraintCollector<String, String, ?, String> collector = max((a, b) -> a, Comparator.comparing(o -> (String) o));
        Object container = collector.supplier().get();
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
    public void maxComparableTri() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Integer> collector = max((a, b, c) -> a + b + c);
        Object container = collector.supplier().get();
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
    public void maxNotComparableTri() {
        TriConstraintCollector<String, String, String, ?, String> collector = max((a, b, c) -> a,
                Comparator.comparing(o -> (String) o));
        Object container = collector.supplier().get();
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
    public void maxComparableQuad() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Integer> collector = max((a, b, c, d) -> a + b + c + d);
        Object container = collector.supplier().get();
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
    public void maxNotComparableQuad() {
        QuadConstraintCollector<String, String, String, String, ?, String> collector = max((a, b, c, d) -> a,
                Comparator.comparing(o -> (String) o));
        Object container = collector.supplier().get();
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
    // toCollection
    // ************************************************************************

    @Test
    public void toSet() {
        UniConstraintCollector<Integer, ?, Set<Integer>> collector = ConstraintCollectors.toSet();
        Object container = collector.supplier().get();
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
    public void toSortedSet() {
        UniConstraintCollector<Integer, ?, SortedSet<Integer>> collector = ConstraintCollectors.toSortedSet();
        Object container = collector.supplier().get();
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
    public void toList() {
        UniConstraintCollector<Integer, ?, List<Integer>> collector = ConstraintCollectors.toList();
        Object container = collector.supplier().get();
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
    public void toSetBi() {
        BiConstraintCollector<Integer, Integer, ?, Set<Integer>> collector = ConstraintCollectors.toSet(Integer::sum);
        Object container = collector.supplier().get();
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
    public void toSortedSetBi() {
        BiConstraintCollector<Integer, Integer, ?, SortedSet<Integer>> collector = ConstraintCollectors
                .toSortedSet(Integer::sum);
        Object container = collector.supplier().get();
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
    public void toListBi() {
        BiConstraintCollector<Integer, Integer, ?, List<Integer>> collector = ConstraintCollectors.toList(Integer::sum);
        Object container = collector.supplier().get();
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
    public void toSetTri() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Set<Integer>> collector = ConstraintCollectors
                .toSet((a, b, c) -> a + b + c);
        Object container = collector.supplier().get();
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
    public void toSortedSetTri() {
        TriConstraintCollector<Integer, Integer, Integer, ?, SortedSet<Integer>> collector = ConstraintCollectors
                .toSortedSet((a, b, c) -> a + b + c);
        Object container = collector.supplier().get();
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
    public void toListTri() {
        TriConstraintCollector<Integer, Integer, Integer, ?, List<Integer>> collector = ConstraintCollectors
                .toList((a, b, c) -> a + b + c);
        Object container = collector.supplier().get();
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
    public void toSetQuad() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Set<Integer>> collector = ConstraintCollectors
                .toSet((a, b, c, d) -> a + b + c + d);
        Object container = collector.supplier().get();
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
    public void toSortedSetQuad() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, SortedSet<Integer>> collector = ConstraintCollectors
                .toSortedSet((a, b, c, d) -> a + b + c + d);
        Object container = collector.supplier().get();
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
    public void toListQuad() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, List<Integer>> collector = ConstraintCollectors
                .toList((a, b, c, d) -> a + b + c + d);
        Object container = collector.supplier().get();
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
    public void toMap() {
        UniConstraintCollector<Integer, ?, Map<Integer, Set<Integer>>> collector = ConstraintCollectors
                .toMap(Function.identity(), Function.identity());
        Object container = collector.supplier().get();

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
    public void toMapMerged() {
        UniConstraintCollector<Integer, ?, Map<Integer, Integer>> collector = ConstraintCollectors.toMap(Function.identity(),
                Function.identity(), Integer::sum);
        Object container = collector.supplier().get();

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
    public void toBiMap() {
        BiConstraintCollector<Integer, Integer, ?, Map<Integer, Set<Integer>>> collector = ConstraintCollectors
                .toMap(Integer::sum, Integer::sum);
        Object container = collector.supplier().get();

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
    public void toMapBiMerged() {
        BiConstraintCollector<Integer, Integer, ?, Map<Integer, Integer>> collector = ConstraintCollectors.toMap(Integer::sum,
                Integer::sum, Integer::sum);
        Object container = collector.supplier().get();

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
    public void toMapTri() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Map<Integer, Set<Integer>>> collector = ConstraintCollectors
                .toMap((a, b, c) -> a + b + c, (a, b, c) -> a + b + c);
        Object container = collector.supplier().get();

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
    public void toMapTriMerged() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Map<Integer, Integer>> collector = ConstraintCollectors
                .toMap((a, b, c) -> a + b + c, (a, b, c) -> a + b + c, Integer::sum);
        Object container = collector.supplier().get();

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
    public void toMapQuad() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Map<Integer, Set<Integer>>> collector =
                ConstraintCollectors.toMap((a, b, c, d) -> a + b + c + d, (a, b, c, d) -> a + b + c + d);
        Object container = collector.supplier().get();

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
    public void toMapQuadMerged() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Map<Integer, Integer>> collector = ConstraintCollectors
                .toMap((a, b, c, d) -> a + b + c + d, (a, b, c, d) -> a + b + c + d, Integer::sum);
        Object container = collector.supplier().get();

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
    public void toSortedMap() {
        UniConstraintCollector<Integer, ?, SortedMap<Integer, Set<Integer>>> collector = ConstraintCollectors
                .toSortedMap(a -> a, Function.identity());
        Object container = collector.supplier().get();

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
    public void toSortedMapMerged() {
        UniConstraintCollector<Integer, ?, SortedMap<Integer, Integer>> collector = ConstraintCollectors.toSortedMap(a -> a,
                Function.identity(), Integer::sum);
        Object container = collector.supplier().get();

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
    public void toSortedMapBi() {
        BiConstraintCollector<Integer, Integer, ?, SortedMap<Integer, Set<Integer>>> collector = ConstraintCollectors
                .toSortedMap(Integer::sum, Integer::sum);
        Object container = collector.supplier().get();

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
    public void toSortedMapBiMerged() {
        BiConstraintCollector<Integer, Integer, ?, SortedMap<Integer, Integer>> collector = ConstraintCollectors
                .toSortedMap(Integer::sum, Integer::sum, Integer::sum);
        Object container = collector.supplier().get();

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
    public void toSortedMapTri() {
        TriConstraintCollector<Integer, Integer, Integer, ?, SortedMap<Integer, Set<Integer>>> collector = ConstraintCollectors
                .toSortedMap((a, b, c) -> a + b + c, (a, b, c) -> a + b + c);
        Object container = collector.supplier().get();

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
    public void toSortedMapTriMerged() {
        TriConstraintCollector<Integer, Integer, Integer, ?, SortedMap<Integer, Integer>> collector = ConstraintCollectors
                .toSortedMap((a, b, c) -> a + b + c, (a, b, c) -> a + b + c, Integer::sum);
        Object container = collector.supplier().get();

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
    public void toSortedMapQuad() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, SortedMap<Integer, Set<Integer>>> collector =
                ConstraintCollectors.toSortedMap((a, b, c, d) -> a + b + c + d, (a, b, c, d) -> a + b + c + d);
        Object container = collector.supplier().get();

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
    public void toSortedMapQuadMerged() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, SortedMap<Integer, Integer>> collector =
                ConstraintCollectors.toSortedMap((a, b, c, d) -> a + b + c + d, (a, b, c, d) -> a + b + c + d, Integer::sum);
        Object container = collector.supplier().get();

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

    private static <A, B, C, D> Runnable accumulate(QuadConstraintCollector<A, A, A, A, B, C> collector, Object container,
            A valueA, A valueB, A valueC, A valueD) {
        return collector.accumulator().apply((B) container, valueA, valueB, valueC, valueD);
    }

    private static <A, B, C> Runnable accumulate(TriConstraintCollector<A, A, A, B, C> collector, Object container,
            A valueA, A valueB, A valueC) {
        return collector.accumulator().apply((B) container, valueA, valueB, valueC);
    }

    private static <A, B, C> Runnable accumulate(BiConstraintCollector<A, A, B, C> collector, Object container,
            A valueA, A valueB) {
        return collector.accumulator().apply((B) container, valueA, valueB);
    }

    private static <A, B, C> Runnable accumulate(UniConstraintCollector<A, B, C> collector, Object container, A value) {
        return collector.accumulator().apply((B) container, value);
    }

    private static <A, B, C> void assertResult(QuadConstraintCollector<A, A, A, A, B, C> collector, Object container,
            C expectedResult) {
        C actualResult = collector.finisher().apply((B) container);
        assertThat(actualResult)
                .as("Collector (" + collector + ") did not produce expected result.")
                .isEqualTo(expectedResult);
    }

    private static <A, B, C> void assertResult(TriConstraintCollector<A, A, A, B, C> collector, Object container,
            C expectedResult) {
        C actualResult = collector.finisher().apply((B) container);
        assertThat(actualResult)
                .as("Collector (" + collector + ") did not produce expected result.")
                .isEqualTo(expectedResult);
    }

    private static <A, B, C> void assertResult(BiConstraintCollector<A, A, B, C> collector, Object container,
            C expectedResult) {
        C actualResult = collector.finisher().apply((B) container);
        assertThat(actualResult)
                .as("Collector (" + collector + ") did not produce expected result.")
                .isEqualTo(expectedResult);
    }

    private static <A, B, C> void assertResult(UniConstraintCollector<A, B, C> collector, Object container,
            C expectedResult) {
        C actualResult = collector.finisher().apply((B) container);
        assertThat(actualResult)
                .as("Collector (" + collector + ") did not produce expected result.")
                .isEqualTo(expectedResult);
    }

}
