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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Period;
import java.util.Comparator;
import java.util.function.Function;

import org.junit.Test;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;

import static org.junit.Assert.assertEquals;

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
        BiConstraintCollector<Integer, Integer, ?, Long> collector = ConstraintCollectors.countLongBi();
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
        TriConstraintCollector<Integer, Integer, Integer, ?, Long> collector = ConstraintCollectors.countLongTri();
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
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Integer> collector =
                ConstraintCollectors.countQuad();
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
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Long> collector =
                ConstraintCollectors.countLongQuad();
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
        UniConstraintCollector<Integer, ?, Integer> collector = ConstraintCollectors.countDistinct(Function.identity());
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
        BiConstraintCollector<Integer, Integer, ?, Integer> collector =
                ConstraintCollectors.countDistinct((a, b) -> a + b);
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
        BiConstraintCollector<Integer, Integer, ?, Long> collector =
                ConstraintCollectors.countDistinctLong((a, b) -> a + b);
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
        TriConstraintCollector<Integer, Integer, Integer, ?, Integer> collector =
                ConstraintCollectors.countDistinct((a, b, c) -> a + b + c);
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
        TriConstraintCollector<Integer, Integer, Integer, ?, Long> collector =
                ConstraintCollectors.countDistinctLong((a, b, c) -> a + b + c);
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
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Integer> collector =
                ConstraintCollectors.countDistinct((a, b, c, d) -> a + b + c + d);
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
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Long> collector =
                ConstraintCollectors.countDistinctLong((a, b, c, d) -> a + b + c + d);
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
        assertResult(collector, container, firstValue);
        // Add second value, we have two now.
        int secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container,firstValue + secondValue);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, firstValue + 2 * secondValue);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, firstValue + secondValue);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, firstValue);
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
        assertResult(collector, container, firstValue);
        // Add second value, we have two now.
        long secondValue = 1;
        Runnable secondRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, firstValue + secondValue);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValue);
        assertResult(collector, container, firstValue + 2 * secondValue);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, firstValue + secondValue);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, firstValue);
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
        assertResult(collector, container, firstValue);
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
        assertResult(collector, container, firstValue);
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
        assertResult(collector, container, firstValue);
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
        assertResult(collector, container, firstValue);
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
        assertResult(collector, container, firstValueA + firstValueB);
        // Add second value, we have two now.
        int secondValueA = 4;
        int secondValueB = 5;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container,firstValueA + firstValueB + secondValueA + secondValueB);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, firstValueA + firstValueB + 2 * (secondValueA + secondValueB));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, firstValueA + firstValueB + secondValueA + secondValueB);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, firstValueA + firstValueB);
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
        assertResult(collector, container, (long)firstValueA + firstValueB);
        // Add second value, we have two now.
        int secondValueA = 4;
        int secondValueB = 5;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container,(long)firstValueA + firstValueB + secondValueA + secondValueB);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB);
        assertResult(collector, container, (long)firstValueA + firstValueB + 2 * (secondValueA + secondValueB));
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, (long)firstValueA + firstValueB + secondValueA + secondValueB);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, (long)firstValueA + firstValueB);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0L);
    }

    @Test
    public void sumBiBigDecimal() {
        BiConstraintCollector<Integer, Integer, ?, BigDecimal> collector =
                ConstraintCollectors.sumBigDecimal((a, b) -> BigDecimal.valueOf(a + b));
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
        BiConstraintCollector<Integer, Integer, ?, BigInteger> collector =
                ConstraintCollectors.sumBigInteger((a, b) -> BigInteger.valueOf(a + b));
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
        BiConstraintCollector<Integer, Integer, ?, Duration> collector =
                ConstraintCollectors.sumDuration((a, b) -> Duration.ofSeconds(a + b));
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
        BiConstraintCollector<Integer, Integer, ?, Period> collector =
                ConstraintCollectors.sumPeriod((a, b) -> Period.ofDays(a + b));
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
        TriConstraintCollector<Integer, Integer, Integer, ?, Integer> collector =
                ConstraintCollectors.sum((a, b, c) -> a + b + c);
        Object container = collector.supplier().get();
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        int firstValueC = 1;
        int firstSum = firstValueA + firstValueB + firstValueC;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC);
        assertResult(collector, container, firstSum);
        // Add second value, we have two now.
        int secondValueA = 4;
        int secondValueB = 5;
        int secondValueC = 1;
        int secondSum = secondValueA + secondValueB + secondValueC;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container,firstSum + secondSum);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, firstSum + 2 * secondSum);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, firstSum + secondSum);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, firstSum);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0);
    }

    @Test
    public void sumTriLong() {
        TriConstraintCollector<Integer, Integer, Integer, ?, Long> collector =
                ConstraintCollectors.sumLong((a, b, c) -> a + b + c);
        Object container = collector.supplier().get();
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        int firstValueC = 1;
        int firstSum = firstValueA + firstValueB + firstValueC;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC);
        assertResult(collector, container, (long) firstSum);
        // Add second value, we have two now.
        int secondValueA = 4;
        int secondValueB = 5;
        int secondValueC = 1;
        int secondSum = secondValueA + secondValueB + secondValueC;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container,(long) firstSum + secondSum);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC);
        assertResult(collector, container, (long) firstSum + 2 * secondSum);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, (long) firstSum + secondSum);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, (long) firstSum);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0L);
    }

    @Test
    public void sumTriBigDecimal() {
        TriConstraintCollector<Integer, Integer, Integer, ?, BigDecimal> collector =
                ConstraintCollectors.sumBigDecimal((a, b, c) -> BigDecimal.valueOf(a + b + c));
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
        TriConstraintCollector<Integer, Integer, Integer, ?, BigInteger> collector =
                ConstraintCollectors.sumBigInteger((a, b, c) -> BigInteger.valueOf(a + b + c));
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
        TriConstraintCollector<Integer, Integer, Integer, ?, Duration> collector =
                ConstraintCollectors.sumDuration((a, b, c) -> Duration.ofSeconds(a + b + c));
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
        TriConstraintCollector<Integer, Integer, Integer, ?, Period> collector =
                ConstraintCollectors.sumPeriod((a, b, c) -> Period.ofDays(a + b + c));
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
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Integer> collector =
                ConstraintCollectors.sum((a, b, c, d) -> a + b + c + d);
        Object container = collector.supplier().get();
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        int firstValueC = 1;
        int firstValueD = 4;
        int firstSum = firstValueA + firstValueB + firstValueC + firstValueD;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC, firstValueD);
        assertResult(collector, container, firstSum);
        // Add second value, we have two now.
        int secondValueA = 4;
        int secondValueB = 5;
        int secondValueC = 1;
        int secondValueD = 2;
        int secondSum = secondValueA + secondValueB + secondValueC + secondValueD;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC,
                secondValueD);
        assertResult(collector, container,firstSum + secondSum);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC,
                secondValueD);
        assertResult(collector, container, firstSum + 2 * secondSum);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, firstSum + secondSum);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, firstSum);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0);
    }

    @Test
    public void sumQuadLong() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Long> collector =
                ConstraintCollectors.sumLong((a, b, c, d) -> a + b + c + d);
        Object container = collector.supplier().get();
        // Add first value, we have one now.
        int firstValueA = 2;
        int firstValueB = 3;
        int firstValueC = 1;
        int firstValueD = 4;
        int firstSum = firstValueA + firstValueB + firstValueC + firstValueD;
        Runnable firstRetractor = accumulate(collector, container, firstValueA, firstValueB, firstValueC, firstValueD);
        assertResult(collector, container, (long) firstSum);
        // Add second value, we have two now.
        int secondValueA = 4;
        int secondValueB = 5;
        int secondValueC = 1;
        int secondValueD = 2;
        int secondSum = secondValueA + secondValueB + secondValueC + secondValueD;
        Runnable secondRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC,
                secondValueD);
        assertResult(collector, container,(long) firstSum + secondSum);
        // Add third value, same as the second. We now have three values, two of which are the same.
        Runnable thirdRetractor = accumulate(collector, container, secondValueA, secondValueB, secondValueC,
                secondValueD);
        assertResult(collector, container, (long) firstSum + 2 * secondSum);
        // Retract one instance of the second value; we only have two values now.
        secondRetractor.run();
        assertResult(collector, container, (long) firstSum + secondSum);
        // Retract final instance of the second value; we only have one value now.
        thirdRetractor.run();
        assertResult(collector, container, (long) firstSum);
        // Retract last value; there are no values now.
        firstRetractor.run();
        assertResult(collector, container, 0L);
    }

    @Test
    public void sumQuadBigDecimal() {
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, BigDecimal> collector =
                ConstraintCollectors.sumBigDecimal((a, b, c, d) -> BigDecimal.valueOf(a + b + c + d));
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
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, BigInteger> collector =
                ConstraintCollectors.sumBigInteger((a, b, c, d) -> BigInteger.valueOf(a + b + c + d));
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
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Duration> collector =
                ConstraintCollectors.sumDuration((a, b, c,d ) -> Duration.ofSeconds(a + b + c + d));
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
        QuadConstraintCollector<Integer, Integer, Integer, Integer, ?, Period> collector =
                ConstraintCollectors.sumPeriod((a, b, c, d) -> Period.ofDays(a + b + c+ d));
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
        UniConstraintCollector<Integer, ?, Integer> collector = ConstraintCollectors.min();
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
        UniConstraintCollector<Class, ?, Class> collector =
                ConstraintCollectors.min(Comparator.comparing(Class::getCanonicalName));
        Object container = collector.supplier().get();
        // add first value, which becomes the min
        Class firstValue = ConstraintCollectorsTest.class;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, firstValue);
        // add second value, lesser than the first, becomes the new min
        Class secondValue = ConstraintCollectors.class;
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

    // ************************************************************************
    // max
    // ************************************************************************

    @Test
    public void maxComparable() {
        UniConstraintCollector<Integer, ?, Integer> collector = ConstraintCollectors.max();
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
        UniConstraintCollector<Class, ?, Class> collector =
                ConstraintCollectors.max(Comparator.comparing(Class::getCanonicalName));
        Object container = collector.supplier().get();
        // add first value, which becomes the max
        Class firstValue = ConstraintCollectorsTest.class;
        Runnable firstRetractor = accumulate(collector, container, firstValue);
        assertResult(collector, container, firstValue);
        // add second value, lesser than the first, result does not change
        Class secondValue = ConstraintCollectors.class;
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
        assertEquals("Collector (" + collector + ") did not produce expected result.", expectedResult, actualResult);
    }

    private static <A, B, C> void assertResult(TriConstraintCollector<A, A, A, B, C> collector, Object container,
            C expectedResult) {
        C actualResult = collector.finisher().apply((B) container);
        assertEquals("Collector (" + collector + ") did not produce expected result.", expectedResult, actualResult);
    }

    private static <A, B, C> void assertResult(BiConstraintCollector<A, A, B, C> collector, Object container,
            C expectedResult) {
        C actualResult = collector.finisher().apply((B) container);
        assertEquals("Collector (" + collector + ") did not produce expected result.", expectedResult, actualResult);
    }

    private static <A, B, C> void assertResult(UniConstraintCollector<A, B, C> collector, Object container,
            C expectedResult) {
        C actualResult = collector.finisher().apply((B) container);
        assertEquals("Collector (" + collector + ") did not produce expected result.", expectedResult, actualResult);
    }

}
