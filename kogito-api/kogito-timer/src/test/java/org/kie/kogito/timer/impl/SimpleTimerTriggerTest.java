/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.timer.impl;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleTimerTriggerTest {

    public static final OffsetDateTime TRIGGER_START_TIME = OffsetDateTime.parse("2023-03-06T14:00:00.000+05:00");
    public static final OffsetDateTime TRIGGER_END_TIME = OffsetDateTime.parse("2023-03-06T19:00:00.000+05:00");
    public static final long PERIOD = 10;

    @ParameterizedTest
    @MethodSource("simpleTimerTriggerParams")
    void simpleTimerTrigger(OffsetDateTime startTimeAsOffsetDateTime, long period, ChronoUnit periodUnit,
            int repeatCount, OffsetDateTime endTimeAsOffsetDateTime, String offsetId, int expectedCurrentRepeatCount, OffsetDateTime[] expectedExecutions) {
        Date startTime = Date.from(startTimeAsOffsetDateTime.toInstant());
        Date endTime = endTimeAsOffsetDateTime != null ? Date.from(endTimeAsOffsetDateTime.toInstant()) : null;
        SimpleTimerTrigger trigger;
        if (endTime != null) {
            trigger = new SimpleTimerTrigger(startTime, period, periodUnit, repeatCount, endTime, offsetId);
        } else {
            trigger = new SimpleTimerTrigger(startTime, period, periodUnit, repeatCount, offsetId);
        }
        List<Date> nextFireTimes = new ArrayList<>();
        while (trigger.hasNextFireTime() != null) {
            nextFireTimes.add(trigger.nextFireTime());
        }
        assertThat(trigger.getCurrentRepeatCount()).isEqualTo(expectedCurrentRepeatCount);
        assertThat(nextFireTimes).hasSize(expectedExecutions.length);
        for (int i = 0; i < expectedExecutions.length; i++) {
            assertThat(nextFireTimes.get(i)).isEqualTo(expectedExecutions[i].toInstant());
            OffsetDateTime nextFireTimeAsOffsetDateTime = OffsetDateTime.ofInstant(nextFireTimes.get(i).toInstant(), ZoneOffset.of(trigger.getZoneId()));
            assertThat(nextFireTimeAsOffsetDateTime).isEqualTo(expectedExecutions[i]);
        }
        if (trigger.isIndefinitely()) {
            assertThat(trigger.getRepeatCount()).isEqualTo(-1);
        }
    }

    private static Stream<Arguments> simpleTimerTriggerParams() {
        return Stream.of(
                //Period in MILLIS
                Arguments.of(TRIGGER_START_TIME, PERIOD, ChronoUnit.MILLIS, 0, null, TRIGGER_START_TIME.getOffset().getId(), 0,
                        new OffsetDateTime[] {
                                TRIGGER_START_TIME }),

                Arguments.of(TRIGGER_START_TIME, PERIOD, ChronoUnit.MILLIS, 1, null, TRIGGER_START_TIME.getOffset().getId(), 1,
                        new OffsetDateTime[] {
                                TRIGGER_START_TIME,
                                TRIGGER_START_TIME.plus(PERIOD, ChronoUnit.MILLIS)
                        }),
                Arguments.of(TRIGGER_START_TIME, PERIOD, ChronoUnit.MILLIS, 2, null, TRIGGER_START_TIME.getOffset().getId(), 2,
                        new OffsetDateTime[] {
                                TRIGGER_START_TIME,
                                TRIGGER_START_TIME.plus(PERIOD, ChronoUnit.MILLIS),
                                TRIGGER_START_TIME.plus(PERIOD * 2, ChronoUnit.MILLIS)
                        }),
                Arguments.of(TRIGGER_START_TIME, PERIOD, ChronoUnit.MILLIS, 3, null, TRIGGER_START_TIME.getOffset().getId(), 3,
                        new OffsetDateTime[] {
                                TRIGGER_START_TIME,
                                TRIGGER_START_TIME.plus(PERIOD, ChronoUnit.MILLIS),
                                TRIGGER_START_TIME.plus(PERIOD * 2, ChronoUnit.MILLIS),
                                TRIGGER_START_TIME.plus(PERIOD * 3, ChronoUnit.MILLIS)
                        }),

                // Period in SECONDS.
                Arguments.of(TRIGGER_START_TIME, PERIOD, ChronoUnit.SECONDS, 0, null, TRIGGER_START_TIME.getOffset().getId(), 0,
                        new OffsetDateTime[] {
                                TRIGGER_START_TIME }),

                Arguments.of(TRIGGER_START_TIME, PERIOD, ChronoUnit.SECONDS, 1, null, TRIGGER_START_TIME.getOffset().getId(), 1,
                        new OffsetDateTime[] {
                                TRIGGER_START_TIME,
                                TRIGGER_START_TIME.plus(PERIOD, ChronoUnit.SECONDS)
                        }),
                Arguments.of(TRIGGER_START_TIME, PERIOD, ChronoUnit.SECONDS, 2, null, TRIGGER_START_TIME.getOffset().getId(), 2,
                        new OffsetDateTime[] {
                                TRIGGER_START_TIME,
                                TRIGGER_START_TIME.plus(PERIOD, ChronoUnit.SECONDS),
                                TRIGGER_START_TIME.plus(PERIOD * 2, ChronoUnit.SECONDS)
                        }),
                Arguments.of(TRIGGER_START_TIME, PERIOD, ChronoUnit.SECONDS, 3, null, TRIGGER_START_TIME.getOffset().getId(), 3,
                        new OffsetDateTime[] {
                                TRIGGER_START_TIME,
                                TRIGGER_START_TIME.plus(PERIOD, ChronoUnit.SECONDS),
                                TRIGGER_START_TIME.plus(PERIOD * 2, ChronoUnit.SECONDS),
                                TRIGGER_START_TIME.plus(PERIOD * 3, ChronoUnit.SECONDS)
                        }),

                // Period in MINUTES.
                Arguments.of(TRIGGER_START_TIME, PERIOD, ChronoUnit.MINUTES, 0, null, TRIGGER_START_TIME.getOffset().getId(), 0,
                        new OffsetDateTime[] {
                                TRIGGER_START_TIME }),

                Arguments.of(TRIGGER_START_TIME, PERIOD, ChronoUnit.MINUTES, 1, null, TRIGGER_START_TIME.getOffset().getId(), 1,
                        new OffsetDateTime[] {
                                TRIGGER_START_TIME,
                                TRIGGER_START_TIME.plus(PERIOD, ChronoUnit.MINUTES)
                        }),
                Arguments.of(TRIGGER_START_TIME, PERIOD, ChronoUnit.MINUTES, 2, null, TRIGGER_START_TIME.getOffset().getId(), 2,
                        new OffsetDateTime[] {
                                TRIGGER_START_TIME,
                                TRIGGER_START_TIME.plus(PERIOD, ChronoUnit.MINUTES),
                                TRIGGER_START_TIME.plus(PERIOD * 2, ChronoUnit.MINUTES)
                        }),
                Arguments.of(TRIGGER_START_TIME, PERIOD, ChronoUnit.MINUTES, 3, null, TRIGGER_START_TIME.getOffset().getId(), 3,
                        new OffsetDateTime[] {
                                TRIGGER_START_TIME,
                                TRIGGER_START_TIME.plus(PERIOD, ChronoUnit.MINUTES),
                                TRIGGER_START_TIME.plus(PERIOD * 2, ChronoUnit.MINUTES),
                                TRIGGER_START_TIME.plus(PERIOD * 3, ChronoUnit.MINUTES)
                        }),

                // repeatCount with endTime not reached, period in HOURS
                Arguments.of(TRIGGER_START_TIME, 1, ChronoUnit.HOURS, 3, TRIGGER_END_TIME, TRIGGER_START_TIME.getOffset().getId(), 3,
                        new OffsetDateTime[] {
                                TRIGGER_START_TIME,
                                TRIGGER_START_TIME.plus(1, ChronoUnit.HOURS),
                                TRIGGER_START_TIME.plus(2, ChronoUnit.HOURS),
                                TRIGGER_START_TIME.plus(3, ChronoUnit.HOURS)
                        }),

                // repeatCount with endTime, period in HOURS
                Arguments.of(TRIGGER_START_TIME, 1, ChronoUnit.HOURS, 8, TRIGGER_END_TIME, TRIGGER_START_TIME.getOffset().getId(), 5,
                        new OffsetDateTime[] {
                                TRIGGER_START_TIME,
                                TRIGGER_START_TIME.plus(1, ChronoUnit.HOURS),
                                TRIGGER_START_TIME.plus(2, ChronoUnit.HOURS),
                                TRIGGER_START_TIME.plus(3, ChronoUnit.HOURS),
                                TRIGGER_START_TIME.plus(4, ChronoUnit.HOURS),
                                TRIGGER_START_TIME.plus(5, ChronoUnit.HOURS)
                        }),

                // repeatCount indefinitely with endTime, period in HOURS
                Arguments.of(TRIGGER_START_TIME, 1, ChronoUnit.HOURS, -1, TRIGGER_END_TIME, TRIGGER_START_TIME.getOffset().getId(), -1,
                        new OffsetDateTime[] {
                                TRIGGER_START_TIME,
                                TRIGGER_START_TIME.plus(1, ChronoUnit.HOURS),
                                TRIGGER_START_TIME.plus(2, ChronoUnit.HOURS),
                                TRIGGER_START_TIME.plus(3, ChronoUnit.HOURS),
                                TRIGGER_START_TIME.plus(4, ChronoUnit.HOURS),
                                TRIGGER_START_TIME.plus(5, ChronoUnit.HOURS)
                        }));

    }
}
