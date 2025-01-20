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
package org.kie.dmn.feel.runtime;

import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.impl.RangeImpl;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class RangeTest {

    @Test
    void getStartForBigDecimalRangeOpenBoundary() {
        Range range = new RangeImpl(Range.RangeBoundary.OPEN, BigDecimal.TEN, BigDecimal.valueOf(20), Range.RangeBoundary.OPEN);

        Comparable expectedResult = BigDecimal.valueOf(11);
        Comparable actualResult = Range.getStart(range);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getStartForBigDecimalRangeClosedBoundary() {
        Range range = new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.TEN, BigDecimal.valueOf(20), Range.RangeBoundary.OPEN);

        Comparable expectedResult = BigDecimal.TEN;
        Comparable actualResult = Range.getStart(range);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getEndForBigDecimalRangeOpenBoundary() {
        Range range = new RangeImpl(Range.RangeBoundary.OPEN, BigDecimal.TEN, BigDecimal.valueOf(20), Range.RangeBoundary.OPEN);

        Comparable expectedResult = BigDecimal.valueOf(19);
        Comparable actualResult = Range.getEnd(range);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getEndForBigDecimalRangeClosedBoundary() {
        Range range = new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.TEN, BigDecimal.valueOf(20), Range.RangeBoundary.CLOSED);

        Comparable expectedResult = BigDecimal.valueOf(20);
        Comparable actualResult = Range.getEnd(range);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getStartForLocalDateRangeOpenBoundary() {
        Range range = new RangeImpl(Range.RangeBoundary.OPEN, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 7), Range.RangeBoundary.OPEN);

        Comparable expectedResult = LocalDate.of(2025, 1, 2);
        Comparable actualResult = Range.getStart(range);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getStartForLocalDateRangeClosedBoundary() {
        Range range = new RangeImpl(Range.RangeBoundary.CLOSED, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 7), Range.RangeBoundary.OPEN);

        Comparable expectedResult = LocalDate.of(2025, 1, 1);
        Comparable actualResult = Range.getStart(range);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getEndForLocalDateRangeOpenBoundary() {
        Range range = new RangeImpl(Range.RangeBoundary.OPEN, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 7), Range.RangeBoundary.OPEN);

        Comparable expectedResult = LocalDate.of(2025, 1, 6);
        Comparable actualResult = Range.getEnd(range);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getEndForLocalDateRangeClosedBoundary() {
        Range range = new RangeImpl(Range.RangeBoundary.CLOSED, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 7), Range.RangeBoundary.CLOSED);

        Comparable expectedResult = LocalDate.of(2025, 1, 7);
        Comparable actualResult = Range.getEnd(range);
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void getStartForStringRangeClosedBoundary() {
        Range range = new RangeImpl(Range.RangeBoundary.CLOSED, "a", "z", Range.RangeBoundary.OPEN);

        Comparable expectedResult = "a";
        Comparable actualResult = Range.getStart(range);
        assertThat(actualResult).isEqualTo(expectedResult);

    }

    @Test
    void getEndForStringRangeOpenBoundary() {
        Range range = new RangeImpl(Range.RangeBoundary.CLOSED, "a", "z", Range.RangeBoundary.OPEN);

        Comparable expectedResult = "z";
        Comparable actualResult = Range.getEnd(range);
        assertThat(actualResult).isEqualTo(expectedResult);

    }

    @Test
    void getStartForDurationRangeOpenBoundary() {
        Range range =  new RangeImpl(Range.RangeBoundary.OPEN, Duration.parse("P2DT20H14M"), Duration.parse("P3DT20H14M"), Range.RangeBoundary.CLOSED);

        Comparable expectedResult = Duration.parse("P2DT20H14M");
        Comparable actualResult = Range.getStart(range);
        assertThat(actualResult).isEqualTo(expectedResult);

    }

    @Test
    void getEndForDurationRangeClosedBoundary() {
        Range range =  new RangeImpl(Range.RangeBoundary.CLOSED, Duration.parse("P2DT20H14M"), Duration.parse("P3DT20H14M"), Range.RangeBoundary.CLOSED);

        Comparable expectedResult = Duration.parse("P3DT20H14M");
        Comparable actualResult = Range.getEnd(range);
        assertThat(actualResult).isEqualTo(expectedResult);

    }
}
