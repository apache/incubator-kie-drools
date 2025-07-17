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
package org.kie.dmn.feel.util;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.lang.FEELProperty;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.feel.util.EvalHelper.getDefinedValue;

class EvalHelperTest {

    @Test
    void getGenericAccessor() throws NoSuchMethodException {
        Method expectedAccessor = TestPojo.class.getMethod("getAProperty");

        assertThat(EvalHelper.getGenericAccessor(TestPojo.class, "aProperty")).as("getGenericAccessor should work on Java bean accessors.").isEqualTo(expectedAccessor);

        assertThat(EvalHelper.getGenericAccessor(TestPojo.class, "feelPropertyIdentifier")).as("getGenericAccessor should work for methods annotated with '@FEELProperty'.").isEqualTo(expectedAccessor);
    }


    private static class TestPojo {
        @FEELProperty("feelPropertyIdentifier")
        public String getAProperty() {
            return null;
        }
    }

    @Test
    void testValueForLocalTime() {
        LocalTime localTime = LocalTime.of(1, 1, 1);
        EvalHelper.PropertyValueResult value = getDefinedValue(localTime, "value");
        Optional<Object> result = value.getValueResult().getRight();
        long secondsToAdd = ((BigDecimal) result.orElseThrow(
                () -> new AssertionError("Missing result for localTime: " + localTime))).longValue();
        LocalTime roundTripTime = LocalTime.of(0, 0, 0).plusSeconds(secondsToAdd);
        assertThat(localTime).isEqualTo(roundTripTime);
    }

    @Test
    void testValueForZonedDateTime() {
        ZonedDateTime zonedDateTime = ZonedDateTime.of(2025, 7, 8, 10, 0, 0, 0, ZoneId.of("Z"));
        EvalHelper.PropertyValueResult value = getDefinedValue(zonedDateTime, "value");
        Optional<Object> result = value.getValueResult().getRight();
        long secondsToAdd = ((BigDecimal) result.orElseThrow(
                () -> new AssertionError("Missing result for zonedDateTime: " + zonedDateTime))).longValue();
        ZonedDateTime roundTrip = ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneId.of("Z")).plusSeconds(secondsToAdd);
        assertThat(roundTrip).isEqualTo(zonedDateTime);
    }

    @Test
    void testValueForDate() {
        LocalDate localDate = LocalDate.of(2025, 7, 3);
        EvalHelper.PropertyValueResult value = getDefinedValue(localDate, "value");
        Optional<Object> result = value.getValueResult().getRight();
        long secondsToAdd = ((BigDecimal) result.orElseThrow(
                () -> new AssertionError("Missing result for duration: " + localDate))).longValue();
        LocalDate roundTrip = LocalDate.from(ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneId.of("Z")).plusSeconds(secondsToAdd));
        assertThat(roundTrip).isEqualTo(localDate);
    }

    @Test
    void testValueForDuration() {
        Duration duration = Duration.of(1, ChronoUnit.DAYS).plusHours(1);
        EvalHelper.PropertyValueResult value = getDefinedValue(duration, "value");
        Optional<Object> result = value.getValueResult().getRight();
        long secondsToAdd = ((BigDecimal) result.orElseThrow(
                () -> new AssertionError("Missing result for duration: " + duration))).longValue();
        Duration roundTrip = Duration.of(0, ChronoUnit.HOURS).plusSeconds(secondsToAdd);
        assertThat(roundTrip).isEqualTo(duration);
    }

    @Test
    void testValueForDurationYears() {
        ChronoPeriod period  = Period.parse("P2Y1M");
        EvalHelper.PropertyValueResult value = getDefinedValue(period, "value");
        Optional<Object> result = value.getValueResult().getRight();
        long durationToAdd = ((BigDecimal) result.orElseThrow(
                () -> new AssertionError("Missing result for period: " + period))).longValue();
        Period roundTrip = Period.ofYears(0).plusMonths(durationToAdd);
        assertThat(roundTrip.normalized()).isEqualTo(period);
    }

}
