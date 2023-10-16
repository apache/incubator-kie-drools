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
package org.kie.kogito.jobs.service.adapter;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.jobs.service.api.Schedule;
import org.kie.kogito.jobs.service.api.TemporalUnit;
import org.kie.kogito.jobs.service.api.schedule.timer.TimerSchedule;
import org.kie.kogito.timer.Trigger;
import org.kie.kogito.timer.impl.IntervalTrigger;
import org.kie.kogito.timer.impl.PointInTimeTrigger;
import org.kie.kogito.timer.impl.SimpleTimerTrigger;

import static org.assertj.core.api.Assertions.assertThat;

class JobDetailsAdapterTest {

    private static final OffsetDateTime TIMER_SCHEDULE_START_TIME = OffsetDateTime.parse("2023-03-06T14:00:00.000+01:00");

    @Test
    void triggerFromScheduleWithRepetitions() {
        TimerSchedule schedule = TimerSchedule.builder()
                .startTime(TIMER_SCHEDULE_START_TIME)
                .delay(5L)
                .delayUnit(TemporalUnit.SECONDS)
                .repeatCount(2)
                .build();
        Trigger trigger = JobDetailsAdapter.ScheduleAdapter.from(schedule);
        assertThat(trigger).isExactlyInstanceOf(SimpleTimerTrigger.class);
        SimpleTimerTrigger simpleTimerTrigger = (SimpleTimerTrigger) trigger;
        assertThat(simpleTimerTrigger.getRepeatCount()).isEqualTo(schedule.getRepeatCount());
        assertThat(simpleTimerTrigger.getPeriod()).isEqualTo(5);
        assertThat(simpleTimerTrigger.getPeriodUnit()).isEqualTo(ChronoUnit.SECONDS);
        assertThat(simpleTimerTrigger.getEndTime()).isNull();
        assertThat(simpleTimerTrigger.getZoneId()).isNotNull().hasToString("+01:00");

        // first execution
        assertThat(trigger.hasNextFireTime()).isNotNull();
        assertThat(trigger.nextFireTime()).isEqualTo(TIMER_SCHEDULE_START_TIME.toInstant());

        // second execution
        assertThat(trigger.hasNextFireTime()).isNotNull();
        assertThat(trigger.nextFireTime()).isEqualTo(TIMER_SCHEDULE_START_TIME.plus(5, ChronoUnit.SECONDS).toInstant());

        // third execution
        assertThat(trigger.hasNextFireTime()).isNotNull();
        assertThat(trigger.nextFireTime()).isEqualTo(TIMER_SCHEDULE_START_TIME.plus(10, ChronoUnit.SECONDS).toInstant());
        assertThat(simpleTimerTrigger.getCurrentRepeatCount()).isEqualTo(2);

        // no more executions are left.
        assertThat(trigger.hasNextFireTime()).isNull();
    }

    @Test
    void triggerFromScheduleWithNoRepetitions() {
        TimerSchedule schedule = TimerSchedule.builder()
                .startTime(TIMER_SCHEDULE_START_TIME)
                .repeatCount(0)
                .build();
        Trigger trigger = JobDetailsAdapter.ScheduleAdapter.from(schedule);
        assertThat(trigger).isExactlyInstanceOf(SimpleTimerTrigger.class);
        SimpleTimerTrigger simpleTimerTrigger = (SimpleTimerTrigger) trigger;
        assertThat(simpleTimerTrigger.hasNextFireTime()).isEqualTo(TIMER_SCHEDULE_START_TIME.toInstant());
        assertThat(simpleTimerTrigger.nextFireTime()).isEqualTo(TIMER_SCHEDULE_START_TIME.toInstant());
        assertThat(simpleTimerTrigger.getCurrentRepeatCount()).isZero();
        assertThat(simpleTimerTrigger.hasNextFireTime()).isNull();
    }

    @Test
    void toScheduleFromIntervalTrigger() {
        Trigger trigger = new IntervalTrigger(0, Date.from(TIMER_SCHEDULE_START_TIME.toInstant()), null, 4, 0, 5000, null, null);
        Schedule schedule = JobDetailsAdapter.ScheduleAdapter.toSchedule(trigger);
        assertThat(schedule).isExactlyInstanceOf(TimerSchedule.class);
        TimerSchedule timerSchedule = (TimerSchedule) schedule;
        assertThat(timerSchedule.getStartTime()).isEqualTo(TIMER_SCHEDULE_START_TIME);
        assertThat(timerSchedule.getDelay()).isEqualTo(5000);
        assertThat(timerSchedule.getRepeatCount()).isEqualTo(3);
        assertThat(timerSchedule.getDelayUnit()).isEqualTo(TemporalUnit.MILLIS);
    }

    @Test
    void toScheduleFromPointInTimeTrigger() {
        Trigger trigger = new PointInTimeTrigger(Date.from(TIMER_SCHEDULE_START_TIME.toInstant()).getTime(), null, null);
        Schedule schedule = JobDetailsAdapter.ScheduleAdapter.toSchedule(trigger);
        assertThat(schedule).isExactlyInstanceOf(TimerSchedule.class);
        TimerSchedule timerSchedule = (TimerSchedule) schedule;
        assertThat(timerSchedule.getStartTime()).isEqualTo(TIMER_SCHEDULE_START_TIME);
        assertThat(timerSchedule.getRepeatCount()).isZero();
        assertThat(timerSchedule.getDelay()).isZero();
        assertThat(timerSchedule.getDelayUnit()).isEqualTo(TemporalUnit.MILLIS);
    }

    @Test
    void toScheduleFromSimpleTimerTrigger() {
        Trigger trigger = new SimpleTimerTrigger(Date.from(TIMER_SCHEDULE_START_TIME.toInstant()), 5, ChronoUnit.HOURS, 8, TIMER_SCHEDULE_START_TIME.getOffset().getId());
        Schedule schedule = JobDetailsAdapter.ScheduleAdapter.toSchedule(trigger);
        assertThat(schedule).isExactlyInstanceOf(TimerSchedule.class);
        TimerSchedule timerSchedule = (TimerSchedule) schedule;
        assertThat(timerSchedule.getStartTime()).isEqualTo(TIMER_SCHEDULE_START_TIME);
        assertThat(timerSchedule.getRepeatCount()).isEqualTo(8);
        assertThat(timerSchedule.getDelay()).isEqualTo(5);
        assertThat(timerSchedule.getDelayUnit()).isEqualTo(TemporalUnit.HOURS);
    }

    @ParameterizedTest
    @MethodSource("toChronoUnitParams")
    void toChronoUnit(TemporalUnit temporalUnit, ChronoUnit expectedChronoUnit) {
        assertThat(JobDetailsAdapter.TemporalUnitAdapter.toChronoUnit(temporalUnit)).isEqualTo(expectedChronoUnit);
    }

    private static Stream<Arguments> toChronoUnitParams() {
        return Stream.of(
                Arguments.of(TemporalUnit.MILLIS, ChronoUnit.MILLIS),
                Arguments.of(TemporalUnit.SECONDS, ChronoUnit.SECONDS),
                Arguments.of(TemporalUnit.MINUTES, ChronoUnit.MINUTES),
                Arguments.of(TemporalUnit.HOURS, ChronoUnit.HOURS),
                Arguments.of(TemporalUnit.DAYS, ChronoUnit.DAYS));
    }

    @ParameterizedTest
    @MethodSource("fromChronoUnitParams")
    void fromChronoUnit(ChronoUnit chronoUnit, TemporalUnit expectedTemporalUnit) {
        assertThat(JobDetailsAdapter.TemporalUnitAdapter.fromChronoUnit(chronoUnit)).isEqualTo(expectedTemporalUnit);
    }

    private static Stream<Arguments> fromChronoUnitParams() {
        return Stream.of(
                Arguments.of(ChronoUnit.MILLIS, TemporalUnit.MILLIS),
                Arguments.of(ChronoUnit.SECONDS, TemporalUnit.SECONDS),
                Arguments.of(ChronoUnit.MINUTES, TemporalUnit.MINUTES),
                Arguments.of(ChronoUnit.HOURS, TemporalUnit.HOURS),
                Arguments.of(ChronoUnit.DAYS, TemporalUnit.DAYS));
    }
}
