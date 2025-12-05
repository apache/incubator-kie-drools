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

import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.Range;

import static org.assertj.core.api.Assertions.assertThat;

class RangeImplTest {

    @Test
    void isWithUndefined() {
        RangeImpl rangeImpl = new RangeImpl(Range.RangeBoundary.CLOSED, null, null, Range.RangeBoundary.OPEN);
        assertThat(rangeImpl.isWithUndefined()).isFalse();
        rangeImpl = new RangeImpl(Range.RangeBoundary.CLOSED, 10, null, Range.RangeBoundary.OPEN);
        assertThat(rangeImpl.isWithUndefined()).isFalse();
        rangeImpl = new RangeImpl(Range.RangeBoundary.CLOSED, null, 10, Range.RangeBoundary.OPEN);
        assertThat(rangeImpl.isWithUndefined()).isFalse();
        rangeImpl = new RangeImpl(Range.RangeBoundary.CLOSED, null, new UndefinedValueComparable(), Range.RangeBoundary.OPEN);
        assertThat(rangeImpl.isWithUndefined()).isTrue();
        rangeImpl = new RangeImpl(Range.RangeBoundary.CLOSED, new UndefinedValueComparable(), null, Range.RangeBoundary.OPEN);
        assertThat(rangeImpl.isWithUndefined()).isTrue();
        rangeImpl = new RangeImpl(Range.RangeBoundary.CLOSED, 10, new UndefinedValueComparable(), Range.RangeBoundary.OPEN);
        assertThat(rangeImpl.isWithUndefined()).isTrue();
        rangeImpl = new RangeImpl(Range.RangeBoundary.CLOSED, new UndefinedValueComparable(), 10, Range.RangeBoundary.OPEN);
        assertThat(rangeImpl.isWithUndefined()).isTrue();
    }

    @Test
    void getLowBoundary() {
        final Range.RangeBoundary lowBoundary = Range.RangeBoundary.CLOSED;
        final RangeImpl rangeImpl = new RangeImpl(lowBoundary, 10, 15, Range.RangeBoundary.OPEN);
        assertThat(rangeImpl.getLowBoundary()).isEqualTo(lowBoundary);
    }

    @Test
    void getLowEndPoint() {
        final Integer lowEndPoint = 1;
        final RangeImpl rangeImpl = new RangeImpl(Range.RangeBoundary.OPEN, lowEndPoint, 15, Range.RangeBoundary.CLOSED);
        assertThat(rangeImpl.getLowEndPoint()).isEqualTo(lowEndPoint);
    }

    @Test
    void getHighEndPoint() {
        final Integer highEndPoint = 15;
        final RangeImpl rangeImpl = new RangeImpl(Range.RangeBoundary.OPEN, 1, highEndPoint, Range.RangeBoundary.CLOSED);
        assertThat(rangeImpl.getHighEndPoint()).isEqualTo(highEndPoint);
    }

    @Test
    void getHighBoundary() {
        final Range.RangeBoundary highBoundary = Range.RangeBoundary.CLOSED;
        final RangeImpl rangeImpl = new RangeImpl(Range.RangeBoundary.OPEN, 10, 15, highBoundary);
        assertThat(rangeImpl.getHighBoundary()).isEqualTo(highBoundary);
    }

    @Test
    void includes() {
        RangeImpl rangeImpl = new RangeImpl(Range.RangeBoundary.OPEN, 10, 15, Range.RangeBoundary.OPEN);
        EvaluationContext ctx = null;
        assertThat(rangeImpl.includes(null, -15)).isFalse();
        assertThat(rangeImpl.includes(null, 5)).isFalse();
        assertThat(rangeImpl.includes(null, 10)).isFalse();
        assertThat(rangeImpl.includes(null, 12)).isTrue();
        assertThat(rangeImpl.includes(null, 15)).isFalse();
        assertThat(rangeImpl.includes(null, 156)).isFalse();

        rangeImpl = new RangeImpl(Range.RangeBoundary.CLOSED, 10, 15, Range.RangeBoundary.OPEN);
        assertThat(rangeImpl.includes(null, 10)).isTrue();
        assertThat(rangeImpl.includes(null, 12)).isTrue();
        assertThat(rangeImpl.includes(null, 15)).isFalse();

        rangeImpl = new RangeImpl(Range.RangeBoundary.OPEN, 10, 15, Range.RangeBoundary.CLOSED);
        assertThat(rangeImpl.includes(null, 10)).isFalse();
        assertThat(rangeImpl.includes(null, 12)).isTrue();
        assertThat(rangeImpl.includes(null, 15)).isTrue();

        rangeImpl = new RangeImpl(Range.RangeBoundary.CLOSED, 10, 15, Range.RangeBoundary.CLOSED);
        assertThat(rangeImpl.includes(null, 10)).isTrue();
        assertThat(rangeImpl.includes(null, 12)).isTrue();
        assertThat(rangeImpl.includes(null, 15)).isTrue();

        rangeImpl = new RangeImpl(Range.RangeBoundary.CLOSED, new UndefinedValueComparable(), 15, Range.RangeBoundary.CLOSED);
        assertThat(rangeImpl.includes(null, -1456)).isTrue();
        assertThat(rangeImpl.includes(null, 20)).isFalse();
        assertThat(rangeImpl.includes(null, null)).isNull();

        rangeImpl = new RangeImpl(Range.RangeBoundary.CLOSED, 15, new UndefinedValueComparable(), Range.RangeBoundary.CLOSED);
        assertThat(rangeImpl.includes(null, -1456)).isFalse();
        assertThat(rangeImpl.includes(null, 20)).isTrue();
        assertThat(rangeImpl.includes(null, null)).isNull();

        rangeImpl = new RangeImpl(Range.RangeBoundary.CLOSED, null, new UndefinedValueComparable(), Range.RangeBoundary.CLOSED);
        assertThat(rangeImpl.includes(null, -1456)).isNull();
        assertThat(rangeImpl.includes(null, 20)).isNull();
        assertThat(rangeImpl.includes(null, null)).isNull();

        rangeImpl = new RangeImpl(Range.RangeBoundary.CLOSED, new UndefinedValueComparable(), null, Range.RangeBoundary.CLOSED);
        assertThat(rangeImpl.includes(null, -1456)).isNull();
        assertThat(rangeImpl.includes(null, 20)).isNull();
        assertThat(rangeImpl.includes(null, null)).isNull();
    }

    @Test
    void equals() {
        RangeImpl rangeImpl = new RangeImpl(Range.RangeBoundary.OPEN, 10, 15, Range.RangeBoundary.OPEN);
        assertThat(rangeImpl).isEqualTo(rangeImpl);

        RangeImpl rangeImpl2 = new RangeImpl(Range.RangeBoundary.OPEN, 10, 15, Range.RangeBoundary.OPEN);
        assertThat(rangeImpl2).isEqualTo(rangeImpl);

        rangeImpl2 = new RangeImpl(Range.RangeBoundary.OPEN, 10, 15, Range.RangeBoundary.CLOSED);
        assertThat(rangeImpl2).isNotEqualTo(rangeImpl);
        rangeImpl2 = new RangeImpl(Range.RangeBoundary.CLOSED, 10, 15, Range.RangeBoundary.OPEN);
        assertThat(rangeImpl2).isNotEqualTo(rangeImpl);
        rangeImpl2 = new RangeImpl(Range.RangeBoundary.CLOSED, 10, 15, Range.RangeBoundary.CLOSED);
        assertThat(rangeImpl2).isNotEqualTo(rangeImpl);
        rangeImpl2 = new RangeImpl(Range.RangeBoundary.CLOSED, 12, 15, Range.RangeBoundary.CLOSED);
        assertThat(rangeImpl2).isNotEqualTo(rangeImpl);
        rangeImpl2 = new RangeImpl(Range.RangeBoundary.CLOSED, 12, 17, Range.RangeBoundary.CLOSED);
        assertThat(rangeImpl2).isNotEqualTo(rangeImpl);

        rangeImpl = new RangeImpl();
        assertThat(rangeImpl).isEqualTo(rangeImpl);
    }

    @Test
    void hashCodeTest() {
        final RangeImpl rangeImpl = new RangeImpl(Range.RangeBoundary.OPEN, 10, 15, Range.RangeBoundary.OPEN);
        assertThat(rangeImpl.hashCode()).isEqualTo(rangeImpl.hashCode());

        RangeImpl rangeImpl2 = new RangeImpl(Range.RangeBoundary.OPEN, 10, 15, Range.RangeBoundary.OPEN);
        assertThat(rangeImpl2.hashCode()).isEqualTo(rangeImpl.hashCode());

        rangeImpl2 = new RangeImpl(Range.RangeBoundary.OPEN, 10, 15, Range.RangeBoundary.CLOSED);
        assertThat(rangeImpl2).doesNotHaveSameHashCodeAs(rangeImpl);
        rangeImpl2 = new RangeImpl(Range.RangeBoundary.CLOSED, 10, 15, Range.RangeBoundary.OPEN);
        assertThat(rangeImpl2).doesNotHaveSameHashCodeAs(rangeImpl);
        rangeImpl2 = new RangeImpl(Range.RangeBoundary.CLOSED, 10, 15, Range.RangeBoundary.CLOSED);
        assertThat(rangeImpl2).doesNotHaveSameHashCodeAs(rangeImpl);
        rangeImpl2 = new RangeImpl(Range.RangeBoundary.CLOSED, 12, 15, Range.RangeBoundary.CLOSED);
        assertThat(rangeImpl2).doesNotHaveSameHashCodeAs(rangeImpl);
        rangeImpl2 = new RangeImpl(Range.RangeBoundary.CLOSED, 12, 17, Range.RangeBoundary.CLOSED);
        assertThat(rangeImpl2).doesNotHaveSameHashCodeAs(rangeImpl);
    }

    @Test
    void getStartForBigDecimalRangeOpenBoundary() {
        RangeImpl rangeImpl = new RangeImpl(Range.RangeBoundary.OPEN, BigDecimal.TEN, BigDecimal.valueOf(20), Range.RangeBoundary.OPEN);

        Comparable expectedResult = BigDecimal.valueOf(11);
        Comparable actualResult = rangeImpl.getStart();
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getStartForBigDecimalRangeClosedBoundary() {
        RangeImpl rangeImpl = new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.TEN, BigDecimal.valueOf(20), Range.RangeBoundary.OPEN);

        Comparable expectedResult = BigDecimal.TEN;
        Comparable actualResult = rangeImpl.getStart();
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getEndForBigDecimalRangeOpenBoundary() {
        RangeImpl rangeImpl = new RangeImpl(Range.RangeBoundary.OPEN, BigDecimal.TEN, BigDecimal.valueOf(20), Range.RangeBoundary.OPEN);

        Comparable expectedResult = BigDecimal.valueOf(19);
        Comparable actualResult = rangeImpl.getEnd();
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getEndForBigDecimalRangeClosedBoundary() {
        RangeImpl rangeImpl = new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.TEN, BigDecimal.valueOf(20), Range.RangeBoundary.CLOSED);

        Comparable expectedResult = BigDecimal.valueOf(20);
        Comparable actualResult = rangeImpl.getEnd();
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getStartForLocalDateRangeOpenBoundary() {
        RangeImpl rangeImpl = new RangeImpl(Range.RangeBoundary.OPEN, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 7), Range.RangeBoundary.OPEN);

        Comparable expectedResult = LocalDate.of(2025, 1, 2);
        Comparable actualResult = rangeImpl.getStart();
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getStartForLocalDateRangeClosedBoundary() {
        RangeImpl rangeImpl = new RangeImpl(Range.RangeBoundary.CLOSED, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 7), Range.RangeBoundary.OPEN);

        Comparable expectedResult = LocalDate.of(2025, 1, 1);
        Comparable actualResult = rangeImpl.getStart();
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getEndForLocalDateRangeOpenBoundary() {
        RangeImpl rangeImpl = new RangeImpl(Range.RangeBoundary.OPEN, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 7), Range.RangeBoundary.OPEN);

        Comparable expectedResult = LocalDate.of(2025, 1, 6);
        Comparable actualResult = rangeImpl.getEnd();
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getEndForLocalDateRangeClosedBoundary() {
        RangeImpl rangeImpl = new RangeImpl(Range.RangeBoundary.CLOSED, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 7), Range.RangeBoundary.CLOSED);

        Comparable expectedResult = LocalDate.of(2025, 1, 7);
        Comparable actualResult = rangeImpl.getEnd();
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getStartForStringRangeClosedBoundary() {
        RangeImpl rangeImpl = new RangeImpl(Range.RangeBoundary.CLOSED, "a", "z", Range.RangeBoundary.OPEN);

        Comparable expectedResult = "a";
        Comparable actualResult = rangeImpl.getStart();
        assertThat(actualResult).isEqualTo(expectedResult);

    }

    @Test
    void getEndForStringRangeOpenBoundary() {
        RangeImpl rangeImpl = new RangeImpl(Range.RangeBoundary.CLOSED, "a", "z", Range.RangeBoundary.OPEN);

        Comparable expectedResult = "z";
        Comparable actualResult = rangeImpl.getEnd();
        assertThat(actualResult).isEqualTo(expectedResult);

    }

    @Test
    void getStartForDurationRangeOpenBoundary() {
        RangeImpl rangeImpl = new RangeImpl(Range.RangeBoundary.OPEN, Duration.parse("P2DT20H14M"), Duration.parse("P3DT20H14M"), Range.RangeBoundary.CLOSED);

        Comparable expectedResult = Duration.parse("P2DT20H14M");
        Comparable actualResult = rangeImpl.getStart();
        assertThat(actualResult).isEqualTo(expectedResult);

    }

    @Test
    void getEndForDurationRangeClosedBoundary() {
        RangeImpl rangeImpl = new RangeImpl(Range.RangeBoundary.CLOSED, Duration.parse("P2DT20H14M"), Duration.parse("P3DT20H14M"), Range.RangeBoundary.CLOSED);

        Comparable expectedResult = Duration.parse("P3DT20H14M");
        Comparable actualResult = rangeImpl.getEnd();
        assertThat(actualResult).isEqualTo(expectedResult);

    }
}