/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.process.instance.impl.humantask;

import java.time.Duration;
import java.time.Period;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.kogito.jobs.ExpirationTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class DeadlineHelperTest {

    @Test
    public void testRepetition() {
        Collection<DeadlineInfo<Map<String, Object>>> deadlines = DeadlineHelper.parseDeadlines(
                "[subject:5secs|body:NotCompleted repeated notification every 5secs]@[R/PT5S]");
        assertEquals(1, deadlines.size());
        DeadlineInfo<Map<String, Object>> deadlineInfo = deadlines.iterator().next();
        assertEquals("5secs", deadlineInfo.getNotification().get("subject"));
        assertEquals("NotCompleted repeated notification every 5secs", deadlineInfo.getNotification().get("body"));
        Collection<ScheduleInfo> scheduling = deadlineInfo.getScheduleInfo();
        assertEquals(1, scheduling.size());
        ScheduleInfo scheduleInfo = scheduling.iterator().next();
        assertEquals(Duration.ofSeconds(5), scheduleInfo.getDuration());
        assertEquals(-1, scheduleInfo.getNumRepetitions());
        assertNull(scheduleInfo.getEndDate());
        assertNull(scheduleInfo.getStartDate());

        ExpirationTime time = DeadlineHelper.getExpirationTime(scheduleInfo);
        assertEquals(5000L, time.repeatInterval());
        assertEquals(-1, time.repeatLimit());
        assertTrue(ZonedDateTime.now().plus(Duration.ofSeconds(5)).isAfter(time.get()));
    }

    @Test
    public void testRepetitionWithEndDate() {
        ZonedDateTime future = ZonedDateTime.now().plus(Duration.ofMinutes(2)).plus(Duration.ofSeconds(2));
        Collection<DeadlineInfo<Map<String, Object>>> deadlines = DeadlineHelper.parseDeadlines(
                "[subject:5secs]@[R/PT5S/" + future.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) + "]");
        assertEquals(1, deadlines.size());
        DeadlineInfo<Map<String, Object>> deadlineInfo = deadlines.iterator().next();
        assertEquals("5secs", deadlineInfo.getNotification().get("subject"));
        Collection<ScheduleInfo> scheduling = deadlineInfo.getScheduleInfo();
        assertEquals(1, scheduling.size());
        ScheduleInfo scheduleInfo = scheduling.iterator().next();
        assertEquals(Duration.ofSeconds(5), scheduleInfo.getDuration());
        assertEquals(-1, scheduleInfo.getNumRepetitions());
        assertEqualsDate(future, scheduleInfo.getEndDate());
        assertNull(scheduleInfo.getStartDate());
        ExpirationTime time = DeadlineHelper.getExpirationTime(scheduleInfo);
        assertEquals(5000L, time.repeatInterval());
        assertEquals(24, time.repeatLimit());
        assertTrue(ZonedDateTime.now().plus(Duration.ofSeconds(5)).isAfter(time.get()), "Date is " + time.get());
    }

    @Test
    public void testRepetitionWithEndDateCornerCase() {
        ZonedDateTime future = ZonedDateTime.now().plus(Duration.ofSeconds(5));
        Collection<DeadlineInfo<Map<String, Object>>> deadlines = DeadlineHelper.parseDeadlines(
                "[subject:5secs]@[R/PT5S/" + future.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) + "]");
        assertEquals(1, deadlines.size());
        DeadlineInfo<Map<String, Object>> deadlineInfo = deadlines.iterator().next();
        assertEquals("5secs", deadlineInfo.getNotification().get("subject"));
        Collection<ScheduleInfo> scheduling = deadlineInfo.getScheduleInfo();
        assertEquals(1, scheduling.size());
        ScheduleInfo scheduleInfo = scheduling.iterator().next();
        assertEquals(Duration.ofSeconds(5), scheduleInfo.getDuration());
        assertEquals(-1, scheduleInfo.getNumRepetitions());
        assertEqualsDate(future, scheduleInfo.getEndDate());
        assertNull(scheduleInfo.getStartDate());

        ExpirationTime time = DeadlineHelper.getExpirationTime(scheduleInfo);
        assertNull(time.repeatInterval());
        assertEquals(0, time.repeatLimit());
        assertTrue(ZonedDateTime.now().plus(Duration.ofSeconds(5)).isAfter(time.get()));
    }

    @Test
    public void testRepetitionWithEndDateAndLimit() {
        Collection<DeadlineInfo<Map<String, Object>>> deadlines = DeadlineHelper.parseDeadlines(
                "[subject:5secs]@[R2/PT5S/2021-03-18T18:55:01+01:00]");
        assertEquals(1, deadlines.size());
        DeadlineInfo<Map<String, Object>> deadlineInfo = deadlines.iterator().next();
        assertEquals("5secs", deadlineInfo.getNotification().get("subject"));
        Collection<ScheduleInfo> scheduling = deadlineInfo.getScheduleInfo();
        assertEquals(1, scheduling.size());
        ScheduleInfo scheduleInfo = scheduling.iterator().next();
        assertEquals(Duration.ofSeconds(5), scheduleInfo.getDuration());
        assertEquals(2, scheduleInfo.getNumRepetitions());
        assertEquals(ZonedDateTime.parse("2021-03-18T18:55:01+01:00"), scheduleInfo.getEndDate());
        assertNull(scheduleInfo.getStartDate());

        ExpirationTime time = DeadlineHelper.getExpirationTime(scheduleInfo);
        assertEquals(5000L, time.repeatInterval());
        assertEquals(2, time.repeatLimit());
        assertEqualsDate(ZonedDateTime.parse("2021-03-18T18:54:51+01:00"), time.get());
    }

    @Test
    public void testRepetitionStartEndDate() {
        Collection<DeadlineInfo<Map<String, Object>>> deadlines = DeadlineHelper.parseDeadlines(
                "[subject:5secs]@[R1/2021-03-18T18:55:01+01:00/2021-03-18T18:55:06+01:00]");
        assertEquals(1, deadlines.size());
        DeadlineInfo<Map<String, Object>> deadlineInfo = deadlines.iterator().next();
        assertEquals("5secs", deadlineInfo.getNotification().get("subject"));
        Collection<ScheduleInfo> scheduling = deadlineInfo.getScheduleInfo();
        assertEquals(1, scheduling.size());
        ScheduleInfo scheduleInfo = scheduling.iterator().next();
        assertEquals(1, scheduleInfo.getNumRepetitions());
        assertEquals(ZonedDateTime.parse("2021-03-18T18:55:01+01:00"), scheduleInfo.getStartDate());
        assertEquals(ZonedDateTime.parse("2021-03-18T18:55:06+01:00"), scheduleInfo.getEndDate());
        assertNull(scheduleInfo.getDuration());

        ExpirationTime time = DeadlineHelper.getExpirationTime(scheduleInfo);
        assertEquals(5000L, time.repeatInterval());
        assertEquals(1, time.repeatLimit());
        assertEqualsDate(ZonedDateTime.parse("2021-03-18T18:55:01+01:00"), time.get());
    }

    @Test
    public void testRepetitionWithStartDate() {
        Collection<DeadlineInfo<Map<String, Object>>> deadlines = DeadlineHelper.parseDeadlines(
                "[subject:5secs]@[R26/2021-03-18T18:55:01+01:00/PT2M]");
        assertEquals(1, deadlines.size());
        DeadlineInfo<Map<String, Object>> deadlineInfo = deadlines.iterator().next();
        assertEquals("5secs", deadlineInfo.getNotification().get("subject"));
        Collection<ScheduleInfo> scheduling = deadlineInfo.getScheduleInfo();
        assertEquals(1, scheduling.size());
        ScheduleInfo scheduleInfo = scheduling.iterator().next();
        assertEquals(Duration.ofMinutes(2), scheduleInfo.getDuration());
        assertEquals(26, scheduleInfo.getNumRepetitions());
        assertEquals(ZonedDateTime.parse("2021-03-18T18:55:01+01:00"), scheduleInfo.getStartDate());
        assertNull(scheduleInfo.getEndDate());

        ExpirationTime time = DeadlineHelper.getExpirationTime(scheduleInfo);
        assertEquals(Duration.ofMinutes(2).toMillis(), time.repeatInterval());
        assertEquals(26, time.repeatLimit());
        assertEqualsDate(ZonedDateTime.parse("2021-03-18T18:55:01+01:00"), time.get());
    }

    @Test
    public void testExactDate() {
        Collection<DeadlineInfo<Map<String, Object>>> deadlines = DeadlineHelper.parseDeadlines(
                "[subject:exact date]@[2021-03-18T18:55:01+01:00]");
        assertEquals(1, deadlines.size());
        DeadlineInfo<Map<String, Object>> deadlineInfo = deadlines.iterator().next();
        assertEquals("exact date", deadlineInfo.getNotification().get("subject"));
        Collection<ScheduleInfo> scheduling = deadlineInfo.getScheduleInfo();
        assertEquals(1, scheduling.size());
        ScheduleInfo scheduleInfo = scheduling.iterator().next();
        assertNull(scheduleInfo.getDuration());
        assertEquals(0, scheduleInfo.getNumRepetitions());
        assertEquals(ZonedDateTime.parse("2021-03-18T18:55:01+01:00"), scheduleInfo.getEndDate());
        assertNull(scheduleInfo.getStartDate());

        ExpirationTime time = DeadlineHelper.getExpirationTime(scheduleInfo);
        assertNull(time.repeatInterval());
        assertEquals(0, time.repeatLimit());
        assertEqualsDate(ZonedDateTime.parse("2021-03-18T18:55:01+01:00"), time.get());
    }

    @Test
    public void testLargeRepetition() {
        Collection<DeadlineInfo<Map<String, Object>>> deadlines = DeadlineHelper.parseDeadlines(
                "[subject:more than 1 year]@[R/P1Y3WT1H]");
        assertEquals(1, deadlines.size());
        DeadlineInfo<Map<String, Object>> deadlineInfo = deadlines.iterator().next();
        assertEquals("more than 1 year", deadlineInfo.getNotification().get("subject"));
        Collection<ScheduleInfo> scheduling = deadlineInfo.getScheduleInfo();
        assertEquals(1, scheduling.size());
        ScheduleInfo scheduleInfo = scheduling.iterator().next();
        assertEquals(DeadlineHelper.getDuration(Period.ofYears(1).plus(Period.ofWeeks(3)), Duration.ofHours(1)),
                scheduleInfo.getDuration());
        assertEquals(-1, scheduleInfo.getNumRepetitions());
        assertNull(scheduleInfo.getEndDate());
        assertNull(scheduleInfo.getStartDate());

        ExpirationTime time = DeadlineHelper.getExpirationTime(scheduleInfo);
        assertEquals(DeadlineHelper.getDuration(Period.ofYears(1).plus(Period.ofWeeks(3)), Duration.ofHours(1))
                .toMillis(),
                time
                        .repeatInterval());
        assertEquals(-1, time.repeatLimit());
    }

    @Test
    public void testMultipleDuration() {
        Collection<DeadlineInfo<Map<String, Object>>> deadlines = DeadlineHelper.parseDeadlines(
                "[subject:1 and 4 hour]@[PT1H,PT4H]");
        assertEquals(1, deadlines.size());
        DeadlineInfo<Map<String, Object>> deadlineInfo = deadlines.iterator().next();
        assertEquals("1 and 4 hour", deadlineInfo.getNotification().get("subject"));
        Collection<ScheduleInfo> scheduling = deadlineInfo.getScheduleInfo();
        assertEquals(2, scheduling.size());
        assertEquals(2, scheduling.stream().filter(s -> s.getDuration().equals(Duration.ofHours(1)) || s.getDuration()
                .equals(Duration.ofHours(4))).count());
    }

    @Test
    public void testMultipleNotification() {
        Collection<DeadlineInfo<Map<String, Object>>> deadlines = DeadlineHelper.parseDeadlines(
                "[subject:1 hour]@[PT1H]^[subject:4 hour]@[PT4H]");
        assertEquals(2, deadlines.size());

        for (DeadlineInfo<Map<String, Object>> deadline : deadlines) {
            if ("1 hour".equals(deadline.getNotification().get("subject"))) {
                assertEquals(Duration.ofHours(1), deadline.getScheduleInfo().iterator().next().getDuration());
            } else if ("4 hour".equals(deadline.getNotification().get("subject"))) {
                assertEquals(Duration.ofHours(4), deadline.getScheduleInfo().iterator().next().getDuration());
            } else {
                fail("Unexpected subject value");
            }
        }
    }

    @Test
    public void testReassignment() {
        Collection<DeadlineInfo<Reassignment>> reassigments = DeadlineHelper.parseReassignments(
                "[users:Pepe,Pepa|groups:Admin,Managers]@[1m]");
        assertEquals(1, reassigments.size());
        DeadlineInfo<Reassignment> reassignment = reassigments.iterator().next();
        assertEquals(new HashSet<>(Arrays.asList("Pepe", "Pepa")), reassignment.getNotification().getPotentialUsers());
        assertEquals(new HashSet<>(Arrays.asList("Admin", "Managers")), reassignment.getNotification()
                .getPotentialGroups());
        assertEquals(Duration.ofMinutes(1), reassignment.getScheduleInfo().iterator().next().getDuration());
    }

    private void assertEqualsDate(ZonedDateTime expectedDate, ZonedDateTime calculatedDate) {
        assertEquals(expectedDate.toInstant().getEpochSecond(), calculatedDate.toInstant().getEpochSecond());
    }
}
