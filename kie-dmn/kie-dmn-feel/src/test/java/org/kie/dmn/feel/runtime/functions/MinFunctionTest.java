/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import java.time.Period;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

class MinFunctionTest {

    private MinFunction minFunction;

    @BeforeEach
    void setUp() {
        minFunction = new MinFunction();
    }

    @Test
    void invokeNullList() {
        FunctionTestUtil.assertResultError(minFunction.invoke((List) null), InvalidParametersEvent.class);
    }

    @Test
    void invokeEmptyList() {
        FunctionTestUtil.assertResultError(minFunction.invoke(Collections.emptyList()), InvalidParametersEvent.class);
    }

    @Test
    void invokeListWithHeterogenousTypes() {
        FunctionTestUtil.assertResultError(minFunction.invoke(Arrays.asList(1, "test", BigDecimal.valueOf(10.2))), InvalidParametersEvent.class);
    }

    @Test
    void invokeListOfIntegers() {
        FunctionTestUtil.assertResult(minFunction.invoke(Collections.singletonList(1)), 1);
        FunctionTestUtil.assertResult(minFunction.invoke(Arrays.asList(1, 2, 3)), 1);
        FunctionTestUtil.assertResult(minFunction.invoke(Arrays.asList(2, 1, 3)), 1);
        FunctionTestUtil.assertResult(minFunction.invoke(Arrays.asList(2, 3, 1)), 1);
    }

    @Test
    void invokeListOfStrings() {
        FunctionTestUtil.assertResult(minFunction.invoke(Collections.singletonList("a")), "a");
        FunctionTestUtil.assertResult(minFunction.invoke(Arrays.asList("a", "b", "c")), "a");
        FunctionTestUtil.assertResult(minFunction.invoke(Arrays.asList("b", "a", "c")), "a");
        FunctionTestUtil.assertResult(minFunction.invoke(Arrays.asList("b", "c", "a")), "a");
    }

    @Test
    void invokeListOfChronoPeriods() {
        final ChronoPeriod p1Period = Period.parse("P1Y");
        final ChronoPeriod p1Comparable = ComparablePeriod.parse("P1Y");
        final ChronoPeriod p2Period = Period.parse("P1M");
        final ChronoPeriod p2Comparable = ComparablePeriod.parse("P1M");
        Predicate<ChronoPeriod> assertion = i -> i.get(ChronoUnit.YEARS) == 0 && i.get(ChronoUnit.MONTHS) == 1;
        FunctionTestUtil.assertPredicateOnResult(minFunction.invoke(Collections.singletonList(p2Period)), ChronoPeriod.class, assertion);
        FunctionTestUtil.assertPredicateOnResult(minFunction.invoke(Collections.singletonList(p2Comparable)), ChronoPeriod.class, assertion);
        FunctionTestUtil.assertPredicateOnResult(minFunction.invoke(Arrays.asList(p1Period, p2Period)), ChronoPeriod.class, assertion);
        FunctionTestUtil.assertPredicateOnResult(minFunction.invoke(Arrays.asList(p1Comparable, p2Period)), ChronoPeriod.class, assertion);
        FunctionTestUtil.assertPredicateOnResult(minFunction.invoke(Arrays.asList(p1Period, p2Comparable)), ChronoPeriod.class, assertion);
        FunctionTestUtil.assertPredicateOnResult(minFunction.invoke(Arrays.asList(p1Comparable, p2Comparable)), ChronoPeriod.class, assertion);
    }

    @Test
    void invokeNullArray() {
        FunctionTestUtil.assertResultError(minFunction.invoke((Object[]) null), InvalidParametersEvent.class);
    }

    @Test
    void invokeEmptyArray() {
        FunctionTestUtil.assertResultError(minFunction.invoke(new Object[]{}), InvalidParametersEvent.class);
    }

    @Test
    void invokeArrayWithHeterogenousTypes() {
        FunctionTestUtil.assertResultError(minFunction.invoke(new Object[]{1, "test", BigDecimal.valueOf(10.2)}), InvalidParametersEvent.class);
    }

    @Test
    void invokeArrayOfIntegers() {
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{1}), 1);
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{1, 2, 3}), 1);
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{2, 1, 3}), 1);
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{2, 3, 1}), 1);
    }

    @Test
    void invokeArrayOfStrings() {
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{"a"}), "a");
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{"a", "b", "c"}), "a");
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{"b", "a", "c"}), "a");
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{"b", "c", "a"}), "a");
    }

    @Test
    void invokeArrayOfChronoPeriods() {
        final ChronoPeriod p1Period = Period.parse("P1Y");
        final ChronoPeriod p1Comparable = ComparablePeriod.parse("P1Y");
        final ChronoPeriod p2Period = Period.parse("P1M");
        final ChronoPeriod p2Comparable = ComparablePeriod.parse("P1M");
        Predicate<ChronoPeriod> assertion = i -> i.get(ChronoUnit.YEARS) == 0 && i.get(ChronoUnit.MONTHS) == 1;
        FunctionTestUtil.assertPredicateOnResult(minFunction.invoke(new Object[]{p2Period}), ChronoPeriod.class, assertion);
        FunctionTestUtil.assertPredicateOnResult(minFunction.invoke(new Object[]{p2Comparable}), ChronoPeriod.class, assertion);
        FunctionTestUtil.assertPredicateOnResult(minFunction.invoke(new Object[]{p1Period, p2Period}), ChronoPeriod.class, assertion);
        FunctionTestUtil.assertPredicateOnResult(minFunction.invoke(new Object[]{p1Comparable, p2Period}), ChronoPeriod.class, assertion);
        FunctionTestUtil.assertPredicateOnResult(minFunction.invoke(new Object[]{p1Period, p2Comparable}), ChronoPeriod.class, assertion);
        FunctionTestUtil.assertPredicateOnResult(minFunction.invoke(new Object[]{p1Comparable, p2Comparable}), ChronoPeriod.class, assertion);
    }
}