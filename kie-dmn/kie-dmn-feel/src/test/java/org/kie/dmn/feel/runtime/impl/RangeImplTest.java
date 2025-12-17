/*
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
package org.kie.dmn.feel.runtime.impl;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.feel.runtime.Range;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.feel.runtime.Range.RangeBoundary.CLOSED;
import static org.kie.dmn.feel.runtime.Range.RangeBoundary.OPEN;

class RangeImplTest {

    @Test
    void getLowBoundary() {
        final Range.RangeBoundary lowBoundary = CLOSED;
        final RangeImpl rangeImpl = new RangeImpl(lowBoundary, 10, 15, OPEN);
        assertThat(rangeImpl.getLowBoundary()).isEqualTo(lowBoundary);
    }

    @Test
    void getLowEndPoint() {
        final Integer lowEndPoint = 1;
        final RangeImpl rangeImpl = new RangeImpl(OPEN, lowEndPoint, 15, CLOSED);
        assertThat(rangeImpl.getLowEndPoint()).isEqualTo(lowEndPoint);
    }

    @Test
    void getHighEndPoint() {
        final Integer highEndPoint = 15;
        final RangeImpl rangeImpl = new RangeImpl(OPEN, 1, highEndPoint, CLOSED);
        assertThat(rangeImpl.getHighEndPoint()).isEqualTo(highEndPoint);
    }

    @Test
    void getHighBoundary() {
        final Range.RangeBoundary highBoundary = CLOSED;
        final RangeImpl rangeImpl = new RangeImpl(OPEN, 10, 15, highBoundary);
        assertThat(rangeImpl.getHighBoundary()).isEqualTo(highBoundary);
    }

    @Test
    void getStartForBigDecimalRangeOpenBoundary() {
        RangeImpl rangeImpl = new RangeImpl(OPEN, BigDecimal.TEN, BigDecimal.valueOf(20), OPEN);

        Comparable expectedResult = BigDecimal.valueOf(11);
        Comparable actualResult = rangeImpl.getStart();
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getStartForBigDecimalRangeClosedBoundary() {
        RangeImpl rangeImpl = new RangeImpl(CLOSED, BigDecimal.TEN, BigDecimal.valueOf(20), OPEN);

        Comparable expectedResult = BigDecimal.TEN;
        Comparable actualResult = rangeImpl.getStart();
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getEndForBigDecimalRangeOpenBoundary() {
        RangeImpl rangeImpl = new RangeImpl(OPEN, BigDecimal.TEN, BigDecimal.valueOf(20), OPEN);

        Comparable expectedResult = BigDecimal.valueOf(19);
        Comparable actualResult = rangeImpl.getEnd();
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getEndForBigDecimalRangeClosedBoundary() {
        RangeImpl rangeImpl = new RangeImpl(CLOSED, BigDecimal.TEN, BigDecimal.valueOf(20), CLOSED);

        Comparable expectedResult = BigDecimal.valueOf(20);
        Comparable actualResult = rangeImpl.getEnd();
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getStartForLocalDateRangeOpenBoundary() {
        RangeImpl rangeImpl = new RangeImpl(OPEN, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 7), OPEN);

        Comparable expectedResult = LocalDate.of(2025, 1, 2);
        Comparable actualResult = rangeImpl.getStart();
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getStartForLocalDateRangeClosedBoundary() {
        RangeImpl rangeImpl = new RangeImpl(CLOSED, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 7), OPEN);

        Comparable expectedResult = LocalDate.of(2025, 1, 1);
        Comparable actualResult = rangeImpl.getStart();
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getEndForLocalDateRangeOpenBoundary() {
        RangeImpl rangeImpl = new RangeImpl(OPEN, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 7), OPEN);

        Comparable expectedResult = LocalDate.of(2025, 1, 6);
        Comparable actualResult = rangeImpl.getEnd();
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getEndForLocalDateRangeClosedBoundary() {
        RangeImpl rangeImpl = new RangeImpl(CLOSED, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 7), CLOSED);

        Comparable expectedResult = LocalDate.of(2025, 1, 7);
        Comparable actualResult = rangeImpl.getEnd();
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getStartForStringRangeClosedBoundary() {
        RangeImpl rangeImpl = new RangeImpl(CLOSED, "a", "z", OPEN);

        Comparable expectedResult = "a";
        Comparable actualResult = rangeImpl.getStart();
        assertThat(actualResult).isEqualTo(expectedResult);

    }

    @Test
    void getEndForStringRangeOpenBoundary() {
        RangeImpl rangeImpl = new RangeImpl(CLOSED, "a", "z", OPEN);

        Comparable expectedResult = "z";
        Comparable actualResult = rangeImpl.getEnd();
        assertThat(actualResult).isEqualTo(expectedResult);

    }

    @Test
    void getStartForDurationRangeOpenBoundary() {
        RangeImpl rangeImpl = new RangeImpl(OPEN, Duration.parse("P2DT20H14M"), Duration.parse("P3DT20H14M"), CLOSED);

        Comparable expectedResult = Duration.parse("P2DT20H14M");
        Comparable actualResult = rangeImpl.getStart();
        assertThat(actualResult).isEqualTo(expectedResult);

    }

    @Test
    void getEndForDurationRangeClosedBoundary() {
        RangeImpl rangeImpl = new RangeImpl(CLOSED, Duration.parse("P2DT20H14M"), Duration.parse("P3DT20H14M"), CLOSED);

        Comparable expectedResult = Duration.parse("P3DT20H14M");
        Comparable actualResult = rangeImpl.getEnd();
        assertThat(actualResult).isEqualTo(expectedResult);

    }

    @ParameterizedTest
    @MethodSource("includesData")
    void includes(IncludesCase c) {
        RangeImpl range = new RangeImpl(
                c.lowBoundary(),
                c.lowEndPoint(),
                c.highEndPoint(),
                c.highBoundary());

        Boolean actual = range.includes(null, c.value());
        assertThat(actual).isEqualTo(c.expected());
    }

    @ParameterizedTest
    @MethodSource("isWithUndefinedData")
    void isWithUndefined(IncludesCase c) {
        RangeImpl range = new RangeImpl(
                c.lowBoundary(),
                c.lowEndPoint(),
                c.highEndPoint(),
                c.highBoundary());

        assertThat(range.isWithUndefined())
                .isEqualTo(c.expected());
    }

    @ParameterizedTest
    @MethodSource("equalsData")
    void testEquals(IncludesCase c) {
        RangeImpl base = new RangeImpl(OPEN, 10, 15, OPEN);
        RangeImpl other;
        // Special case: default constructor
        if (c.lowBoundary() == null && c.highBoundary() == null) {
            other = new RangeImpl();
            assertThat(other).isEqualTo(other);
            return;
        }
        other = new RangeImpl(
                c.lowBoundary(),
                c.lowEndPoint(),
                c.highEndPoint(),
                c.highBoundary());

        if (Boolean.TRUE.equals(c.expected())) {
            assertThat(other).isEqualTo(base);
        } else {
            assertThat(other).isNotEqualTo(base);
        }
    }

    @ParameterizedTest
    @MethodSource("hashCodeData")
    void testHashCode(IncludesCase c) {
        RangeImpl base = new RangeImpl(OPEN, 10, 15, OPEN);

        RangeImpl other = new RangeImpl(
                c.lowBoundary(),
                c.lowEndPoint(),
                c.highEndPoint(),
                c.highBoundary());

        if (Boolean.TRUE.equals(c.expected())) {
            assertThat(other.hashCode()).isEqualTo(base.hashCode());
        } else {
            assertThat(other).doesNotHaveSameHashCodeAs(base);
        }
    }

    private static Stream<IncludesCase> includesData() {
        return Stream.of(
                // (10,15)
                new IncludesCase(OPEN, 10, 15, OPEN, -15, false),
                new IncludesCase(OPEN, 10, 15, OPEN, 5, false),
                new IncludesCase(OPEN, 10, 15, OPEN, 10, false),
                new IncludesCase(OPEN, 10, 15, OPEN, 12, true),
                new IncludesCase(OPEN, 10, 15, OPEN, 15, false),
                new IncludesCase(OPEN, 10, 15, OPEN, 156, false),

                // [10,15)
                new IncludesCase(CLOSED, 10, 15, OPEN, 10, true),
                new IncludesCase(CLOSED, 10, 15, OPEN, 12, true),
                new IncludesCase(CLOSED, 10, 15, OPEN, 15, false),

                // (10,15]
                new IncludesCase(OPEN, 10, 15, CLOSED, 10, false),
                new IncludesCase(OPEN, 10, 15, CLOSED, 12, true),
                new IncludesCase(OPEN, 10, 15, CLOSED, 15, true),

                // [10,15]
                new IncludesCase(CLOSED, 10, 15, CLOSED, 10, true),
                new IncludesCase(CLOSED, 10, 15, CLOSED, 12, true),
                new IncludesCase(CLOSED, 10, 15, CLOSED, 15, true),

                // UndefinedValueComparable cases
                new IncludesCase(CLOSED, new UndefinedValueComparable(), 15, CLOSED, -1456, true),
                new IncludesCase(CLOSED, new UndefinedValueComparable(), 15, CLOSED, 20, false),
                new IncludesCase(CLOSED, new UndefinedValueComparable(), 15, CLOSED, null, null),

                new IncludesCase(CLOSED, 15, new UndefinedValueComparable(), CLOSED, -1456, false),
                new IncludesCase(CLOSED, 15, new UndefinedValueComparable(), CLOSED, 20, true),
                new IncludesCase(CLOSED, 15, new UndefinedValueComparable(), CLOSED, null, null),

                new IncludesCase(CLOSED, null, new UndefinedValueComparable(), CLOSED, -1456, null),
                new IncludesCase(CLOSED, null, new UndefinedValueComparable(), CLOSED, 20, null),
                new IncludesCase(CLOSED, null, new UndefinedValueComparable(), CLOSED, null, null),

                new IncludesCase(CLOSED, new UndefinedValueComparable(), null, CLOSED, -1456, null),
                new IncludesCase(CLOSED, new UndefinedValueComparable(), null, CLOSED, 20, null),
                new IncludesCase(CLOSED, new UndefinedValueComparable(), null, CLOSED, null, null),

                // Boolean ranges
                new IncludesCase(CLOSED, false, false, CLOSED, true, false),
                new IncludesCase(CLOSED, false, false, CLOSED, false, true),

                new IncludesCase(CLOSED, false, false, OPEN, true, false),
                new IncludesCase(CLOSED, false, false, OPEN, false, false),

                new IncludesCase(OPEN, false, false, CLOSED, true, false),
                new IncludesCase(OPEN, false, false, CLOSED, false, false),

                new IncludesCase(OPEN, false, false, OPEN, true, false),
                new IncludesCase(OPEN, false, false, OPEN, false, false),

                new IncludesCase(CLOSED, false, new UndefinedValueComparable(), CLOSED, true, true),
                new IncludesCase(CLOSED, false, new UndefinedValueComparable(), CLOSED, false, true),

                new IncludesCase(OPEN, false, new UndefinedValueComparable(), CLOSED, true, true),
                new IncludesCase(OPEN, false, new UndefinedValueComparable(), CLOSED, false, false),

                new IncludesCase(CLOSED, new UndefinedValueComparable(), false, CLOSED, true, false),
                new IncludesCase(CLOSED, new UndefinedValueComparable(), false, CLOSED, false, true),

                new IncludesCase(CLOSED, new UndefinedValueComparable(), false, OPEN, true, false),
                new IncludesCase(CLOSED, new UndefinedValueComparable(), false, OPEN, false, false));
    }

    private static Stream<IncludesCase> isWithUndefinedData() {
        return Stream.of(
                new IncludesCase(CLOSED, null, null, OPEN, null, false),
                new IncludesCase(CLOSED, 10, null, OPEN, null, false),
                new IncludesCase(CLOSED, null, 10, OPEN, null, false),
                new IncludesCase(CLOSED, null, new UndefinedValueComparable(), OPEN, null, true),
                new IncludesCase(CLOSED, new UndefinedValueComparable(), null, OPEN, null, true),
                new IncludesCase(CLOSED, 10, new UndefinedValueComparable(), OPEN, null, true),
                new IncludesCase(CLOSED, new UndefinedValueComparable(), 10, OPEN, null, true));
    }

    private static Stream<IncludesCase> equalsData() {
        return Stream.of(
                new IncludesCase(OPEN, 10, 15, OPEN, null, true),
                new IncludesCase(OPEN, 10, 15, OPEN, null, true),
                new IncludesCase(OPEN, 10, 15, CLOSED, null, false),
                new IncludesCase(CLOSED, 10, 15, OPEN, null, false),
                new IncludesCase(CLOSED, 10, 15, CLOSED, null, false),
                new IncludesCase(CLOSED, 12, 15, CLOSED, null, false),
                new IncludesCase(CLOSED, 12, 17, CLOSED, null, false),
                new IncludesCase(null, null, null, null, null, true));
    }

    private static Stream<IncludesCase> hashCodeData() {
        return Stream.of(
                new IncludesCase(OPEN, 10, 15, OPEN, null, true),
                new IncludesCase(OPEN, 10, 15, OPEN, null, true),
                new IncludesCase(OPEN, 10, 15, CLOSED, null, false),
                new IncludesCase(CLOSED, 10, 15, OPEN, null, false),
                new IncludesCase(CLOSED, 10, 15, CLOSED, null, false),
                new IncludesCase(CLOSED, 12, 15, CLOSED, null, false),
                new IncludesCase(CLOSED, 12, 17, CLOSED, null, false));
    }

    private record IncludesCase(
            Range.RangeBoundary lowBoundary,
            Comparable lowEndPoint,
            Comparable highEndPoint,
            Range.RangeBoundary highBoundary,
            Object value,
            Boolean expected) {
    }
}