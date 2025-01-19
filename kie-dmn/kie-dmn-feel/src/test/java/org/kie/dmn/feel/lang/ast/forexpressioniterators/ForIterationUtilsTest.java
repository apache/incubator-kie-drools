/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.feel.lang.ast.forexpressioniterators;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.exceptions.EndpointOfRangeNotValidTypeException;
import org.kie.dmn.feel.exceptions.EndpointOfRangeOfDifferentTypeException;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.impl.FEELEventListenersManager;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.impl.RangeImpl;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.kie.dmn.feel.lang.ast.forexpressioniterators.ForIterationUtils.getForIterationBigDecimalRange;
import static org.kie.dmn.feel.lang.ast.forexpressioniterators.ForIterationUtils.getForIterationLocalDateRange;
import static org.kie.dmn.feel.util.EvaluationContextTestUtil.newEmptyEvaluationContext;
import static org.kie.dmn.feel.lang.ast.forexpressioniterators.ForIterationUtils.getForIteration;
import static org.kie.dmn.feel.lang.ast.forexpressioniterators.ForIterationUtils.validateValues;
import static org.kie.dmn.feel.lang.ast.forexpressioniterators.ForIterationUtils.valueMustBeValid;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ForIterationUtilsTest {

    private FEELEventListener listener;
    private EvaluationContext ctx;

    @BeforeEach
    void setup() {
        final FEELEventListenersManager mngr = new FEELEventListenersManager();
        listener = mock(FEELEventListener.class);
        mngr.addListener(listener);
        ctx = newEmptyEvaluationContext(mngr);
    }

    @Test
    void getForIterationValidTest() {
        ForIteration retrieved = getForIteration(ctx, "iteration", BigDecimal.valueOf(1), BigDecimal.valueOf(3));
        assertThat(retrieved).isNotNull();
        verify(listener, never()).onEvent(any(FEELEvent.class));
        retrieved = getForIteration(ctx, "iteration", LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 3));
        assertThat(retrieved).isNotNull();
        verify(listener, never()).onEvent(any(FEELEvent.class));
    }

    @Test
    void getForIterationNotValidTest() {
        try {
            getForIteration(ctx, "iteration", "NOT", "VALID");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(EndpointOfRangeNotValidTypeException.class);
            final ArgumentCaptor<FEELEvent> captor = ArgumentCaptor.forClass(FEELEvent.class);
            verify(listener, times(1)).onEvent(captor.capture());
            reset(listener);
        }
        try {
            getForIteration(ctx, "iteration", BigDecimal.valueOf(1), LocalDate.of(2021, 1, 1));
        } catch (Exception e) {
            assertThat(e).isInstanceOf(EndpointOfRangeOfDifferentTypeException.class);
            final ArgumentCaptor<FEELEvent> captor = ArgumentCaptor.forClass(FEELEvent.class);
            verify(listener, times(1)).onEvent(captor.capture());
            reset(listener);
        }
        try {
            getForIteration(ctx, "iteration", LocalDate.of(2021, 1, 1), BigDecimal.valueOf(1));
        } catch (Exception e) {
            assertThat(e).isInstanceOf(EndpointOfRangeOfDifferentTypeException.class);
            final ArgumentCaptor<FEELEvent> captor = ArgumentCaptor.forClass(FEELEvent.class);
            verify(listener, times(1)).onEvent(captor.capture());
            reset(listener);
        }
    }

    @Test
    void valueMustBeValidTrueTest() {
        valueMustBeValid(ctx, BigDecimal.valueOf(1));
        verify(listener, never()).onEvent(any(FEELEvent.class));
        valueMustBeValid(ctx, LocalDate.of(2021, 1, 3));
        verify(listener, never()).onEvent(any(FEELEvent.class));
    }

    @Test
    void valueMustBeValidFalseTest() {
        try {
            valueMustBeValid(ctx, "INVALID");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(EndpointOfRangeNotValidTypeException.class);
            final ArgumentCaptor<FEELEvent> captor = ArgumentCaptor.forClass(FEELEvent.class);
            verify(listener, times(1)).onEvent(captor.capture());
        }
    }

    @Test
    void validateValuesTrueTest() {
        validateValues(ctx, BigDecimal.valueOf(1), BigDecimal.valueOf(3));
        verify(listener, never()).onEvent(any(FEELEvent.class));
        validateValues(ctx, LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 3));
        verify(listener, never()).onEvent(any(FEELEvent.class));
    }

    @Test
    void validateValuesFalseTest() {
        try {
            validateValues(ctx, "INVALID", "INVALID");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(EndpointOfRangeNotValidTypeException.class);
            final ArgumentCaptor<FEELEvent> captor = ArgumentCaptor.forClass(FEELEvent.class);
            verify(listener, times(1)).onEvent(captor.capture());
            reset(listener);
        }
        try {
            validateValues(ctx, BigDecimal.valueOf(1), LocalDate.of(2021, 1, 1));
        } catch (Exception e) {
            assertThat(e).isInstanceOf(EndpointOfRangeOfDifferentTypeException.class);
            final ArgumentCaptor<FEELEvent> captor = ArgumentCaptor.forClass(FEELEvent.class);
            verify(listener, times(1)).onEvent(captor.capture());
            reset(listener);
        }
        try {
            validateValues(ctx, LocalDate.of(2021, 1, 1), BigDecimal.valueOf(1));
        } catch (Exception e) {
            assertThat(e).isInstanceOf(EndpointOfRangeOfDifferentTypeException.class);
            final ArgumentCaptor<FEELEvent> captor = ArgumentCaptor.forClass(FEELEvent.class);
            verify(listener, times(1)).onEvent(captor.capture());
            reset(listener);
        }
    }

    @Test
    void testGetForIterationRangeValues() {
        Range range = new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.TEN, BigDecimal.valueOf(20), Range.RangeBoundary.OPEN);
        EvaluationContext ctx = mock(EvaluationContext.class);

        ForIteration expectedResult = new ForIteration("rangeTest", null);
        ForIteration actualResult = getForIteration(range, "rangeTest", ctx);
        assertThat(actualResult).isNotNull();
        assertEquals(actualResult.toString(), expectedResult.toString());
    }

    @Test
    void testGetForIterationBigDecimalRangeOpenBoundary() {
        Range range = new RangeImpl(Range.RangeBoundary.OPEN, BigDecimal.TEN, BigDecimal.valueOf(20), Range.RangeBoundary.OPEN);

        ForIteration expectedResult = new ForIteration("BigDecimalRange", null);
        ForIteration actualResult = getForIterationBigDecimalRange(range, "BigDecimalRange", ctx);
        assertThat(actualResult).isNotNull();
        assertEquals(actualResult.toString(), expectedResult.toString());
    }

    @Test
    void testGetForIterationBigDecimalRangeClosedBoundary() {
        Range range = new RangeImpl(Range.RangeBoundary.CLOSED, BigDecimal.TEN, BigDecimal.valueOf(20), Range.RangeBoundary.CLOSED);

        ForIteration expectedResult = new ForIteration("BigDecimalRange", null);
        ForIteration actualResult = getForIterationBigDecimalRange(range, "BigDecimalRange", ctx);
        assertThat(actualResult).isNotNull();
        assertEquals(actualResult.toString(), expectedResult.toString());
    }

    @Test
    void testGetForIterationLocalDateRangeOpenBoundary() {
        Range range = new RangeImpl(Range.RangeBoundary.OPEN, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 7), Range.RangeBoundary.OPEN);

        ForIteration expectedResult = new ForIteration("dateRange", null);
        ForIteration actualResult = getForIterationLocalDateRange(range, "dateRange", ctx);
        assertThat(actualResult).isNotNull();
        assertEquals(actualResult.toString(), expectedResult.toString());
    }

    @Test
    void testGetForIterationLocalDateRangeClosedBoundary() {
        Range range = new RangeImpl(Range.RangeBoundary.CLOSED, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 7), Range.RangeBoundary.CLOSED);

        ForIteration expectedResult = new ForIteration("dateRange", null);
        ForIteration actualResult = getForIterationLocalDateRange(range, "dateRange", ctx);
        assertThat(actualResult).isNotNull();
        assertEquals(actualResult.toString(), expectedResult.toString());
    }
}
