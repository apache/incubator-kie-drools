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
package org.optaplanner.core.impl.domain.lookup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.optaplanner.core.api.domain.common.DomainAccessType;
import org.optaplanner.core.api.domain.lookup.LookUpStrategyType;

class LookUpStrategyImmutableTest {

    private LookUpManager lookUpManager;

    static Stream<Arguments> data() {
        return Stream.of(
                arguments(true, true),
                arguments((byte) 1, (byte) 1),
                arguments((short) 1, (short) 1),
                arguments(1, 1),
                arguments(1L, (long) 1),
                arguments(0.5f, 0.5f),
                arguments(0.1d, 0.1d),
                arguments(BigInteger.ONE, new BigInteger("1")),
                arguments(BigDecimal.ONE, new BigDecimal("1")),
                arguments('!', (char) 33),
                arguments("", ""),

                arguments(Instant.ofEpochMilli(12345L), Instant.ofEpochMilli(12345L)),
                arguments(LocalDateTime.of(1, 2, 3, 4, 5), LocalDateTime.of(1, 2, 3, 4, 5)),
                arguments(LocalTime.of(1, 2), LocalTime.of(1, 2)),
                arguments(LocalDate.of(1, 2, 3), LocalDate.of(1, 2, 3)),
                arguments(MonthDay.of(12, 31), MonthDay.of(12, 31)),
                arguments(DayOfWeek.MONDAY, DayOfWeek.MONDAY),
                arguments(Month.DECEMBER, Month.DECEMBER),
                arguments(YearMonth.of(1999, 12), YearMonth.of(1999, 12)),
                arguments(Year.of(1999), Year.of(1999)),
                arguments(OffsetDateTime.of(1, 2, 3, 4, 5, 6, 7, ZoneOffset.UTC),
                        OffsetDateTime.of(1, 2, 3, 4, 5, 6, 7, ZoneOffset.UTC)),
                arguments(OffsetTime.of(1, 2, 3, 4, ZoneOffset.UTC), OffsetTime.of(1, 2, 3, 4, ZoneOffset.UTC)),
                arguments(ZonedDateTime.of(1, 2, 3, 4, 5, 6, 7, ZoneOffset.UTC),
                        ZonedDateTime.of(1, 2, 3, 4, 5, 6, 7, ZoneOffset.UTC)),
                arguments(ZoneOffset.UTC, ZoneOffset.UTC),
                arguments(Duration.of(5, ChronoUnit.DAYS), Duration.of(5, ChronoUnit.DAYS)),
                arguments(Period.of(1, 2, 3), Period.of(1, 2, 3)));
    }

    @BeforeEach
    void setUpLookUpManager() {
        lookUpManager = new LookUpManager(
                new LookUpStrategyResolver(DomainAccessType.REFLECTION, LookUpStrategyType.PLANNING_ID_OR_NONE));
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("data")
    void addImmutable(Object internalObject) {
        lookUpManager.addWorkingObject(internalObject);
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("data")
    void removeImmutable(Object internalObject) {
        lookUpManager.removeWorkingObject(internalObject);
    }

    @ParameterizedTest(name = "{index}: {0}")
    @MethodSource("data")
    void lookUpImmutable(Object internalObject, Object externalObject) {
        assertThat(lookUpManager.lookUpWorkingObject(externalObject)).isEqualTo(internalObject);
    }

}
