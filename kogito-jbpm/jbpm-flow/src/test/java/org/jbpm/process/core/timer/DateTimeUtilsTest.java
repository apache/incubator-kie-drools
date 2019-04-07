/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.core.timer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Test;
import org.slf4j.LoggerFactory;

public class DateTimeUtilsTest extends AbstractBaseTest {

    private static final long MINUTE_IN_MILLISECONDS = 60 * 1000L;
    private static final long FIFTY_NINE_SECONDS_IN_MILLISECONDS = 59 * 1000L;
    private static final long HOUR_IN_MILLISECONDS = 60 * 60 * 1000L;

    public void addLogger() {
        logger = LoggerFactory.getLogger(this.getClass());
    }

    @Test
    public void testParseDateTime() {
        OffsetDateTime hourAfterEpoch = OffsetDateTime.of(1970, 1, 1, 1, 0, 0, 0, ZoneOffset.UTC);

        long parsedMilliseconds = DateTimeUtils.parseDateTime(hourAfterEpoch.format(DateTimeFormatter.ISO_DATE_TIME));

        assertEquals(HOUR_IN_MILLISECONDS, parsedMilliseconds);
    }

    @Test
    public void testParseDuration() {
        long parsedMilliseconds = DateTimeUtils.parseDuration("1h");

        assertEquals(HOUR_IN_MILLISECONDS, parsedMilliseconds);
    }

    @Test
    public void testParseDurationPeriodFormat() {
        long parsedMilliseconds = DateTimeUtils.parseDuration("PT1H");

        assertEquals(HOUR_IN_MILLISECONDS, parsedMilliseconds);
    }

    @Test
    public void testParseDurationDefaultMilliseconds() {
        long parsedMilliseconds = DateTimeUtils.parseDuration(Long.toString(HOUR_IN_MILLISECONDS));

        assertEquals(HOUR_IN_MILLISECONDS, parsedMilliseconds);
    }

    @Test
    public void testParseDateAsDuration() {
        OffsetDateTime oneMinuteFromNow = OffsetDateTime.now().plusMinutes(1);

        long parsedMilliseconds = DateTimeUtils.parseDateAsDuration(oneMinuteFromNow.format(DateTimeFormatter.ISO_DATE_TIME));

        assertTrue("Parsed date as duration is bigger than " + MINUTE_IN_MILLISECONDS, parsedMilliseconds <= MINUTE_IN_MILLISECONDS);
        assertTrue("Parsed date as duration is too low! Expected value is between " + MINUTE_IN_MILLISECONDS + " and " + FIFTY_NINE_SECONDS_IN_MILLISECONDS + " but is " + parsedMilliseconds, parsedMilliseconds > FIFTY_NINE_SECONDS_IN_MILLISECONDS);
    }

    @Test
    public void testParseRepeatableStartEndDateTime() {
        OffsetDateTime oneMinuteFromNow = OffsetDateTime.now().plusMinutes(1);
        OffsetDateTime twoMinutesFromNow = oneMinuteFromNow.plusMinutes(1);
        String oneMinuteFromNowFormatted = oneMinuteFromNow.format(DateTimeFormatter.ISO_DATE_TIME);
        String twoMinutesFromNowFormatted = twoMinutesFromNow.format(DateTimeFormatter.ISO_DATE_TIME);
        String isoString = "R5/" + oneMinuteFromNowFormatted  + "/" + twoMinutesFromNowFormatted;

        long[] parsedRepeatable = DateTimeUtils.parseRepeatableDateTime(isoString);

        assertEquals(5L, parsedRepeatable[0]);
        assertTrue("Parsed delay is bigger than " + MINUTE_IN_MILLISECONDS, parsedRepeatable[1] <= MINUTE_IN_MILLISECONDS);
        assertTrue("Parsed delay is too low! Expected value is between " + MINUTE_IN_MILLISECONDS + " and " + FIFTY_NINE_SECONDS_IN_MILLISECONDS + " but is " + parsedRepeatable[1], parsedRepeatable[1] > FIFTY_NINE_SECONDS_IN_MILLISECONDS);
        assertEquals("Parsed period should be one minute in milliseconds but is " + parsedRepeatable[2], MINUTE_IN_MILLISECONDS, parsedRepeatable[2]);
    }

    @Test
    public void testParseRepeatableStartDateTimeAndPeriod() {
        OffsetDateTime oneMinuteFromNow = OffsetDateTime.now().plusMinutes(1);
        String oneMinuteFromNowFormatted = oneMinuteFromNow.format(DateTimeFormatter.ISO_DATE_TIME);
        String isoString = "R5/" + oneMinuteFromNowFormatted  + "/PT1M";

        long[] parsedRepeatable = DateTimeUtils.parseRepeatableDateTime(isoString);

        assertEquals(5L, parsedRepeatable[0]);
        assertTrue("Parsed delay is bigger than " + MINUTE_IN_MILLISECONDS, parsedRepeatable[1] <= MINUTE_IN_MILLISECONDS);
        assertTrue("Parsed delay is too low! Expected value is between " + MINUTE_IN_MILLISECONDS + " and " + FIFTY_NINE_SECONDS_IN_MILLISECONDS + " but is " + parsedRepeatable[1], parsedRepeatable[1] > FIFTY_NINE_SECONDS_IN_MILLISECONDS);
        assertEquals("Parsed period should be one minute in milliseconds but is " + parsedRepeatable[2], MINUTE_IN_MILLISECONDS, parsedRepeatable[2]);
    }

    @Test
    public void testParseRepeatablePeriodAndEndDateTime() {
        OffsetDateTime twoMinutesFromNow = OffsetDateTime.now().plusMinutes(2);
        String twoMinutesFromNowFormatted = twoMinutesFromNow.format(DateTimeFormatter.ISO_DATE_TIME);
        String isoString = "R5/PT1M/" + twoMinutesFromNowFormatted;

        long[] parsedRepeatable = DateTimeUtils.parseRepeatableDateTime(isoString);

        assertEquals(5L, parsedRepeatable[0]);
        assertTrue("Parsed delay is bigger than " + MINUTE_IN_MILLISECONDS, parsedRepeatable[1] <= MINUTE_IN_MILLISECONDS);
        assertTrue("Parsed delay is too low! Expected value is between " + MINUTE_IN_MILLISECONDS + " and " + FIFTY_NINE_SECONDS_IN_MILLISECONDS + " but is " + parsedRepeatable[1], parsedRepeatable[1] > FIFTY_NINE_SECONDS_IN_MILLISECONDS);
        assertEquals("Parsed period should be one minute in milliseconds but is " + parsedRepeatable[2], MINUTE_IN_MILLISECONDS, parsedRepeatable[2]);
    }

    @Test
    public void testParseRepeatablePeriodOnly() {
        String isoString = "R/PT1M";

        long[] parsedRepeatable = DateTimeUtils.parseRepeatableDateTime(isoString);

        assertEquals(-1L, parsedRepeatable[0]);
        // Default delay time is 1000ms
        assertEquals(1000L, parsedRepeatable[1]);
        assertEquals("Parsed period should be one minute in milliseconds but is " + parsedRepeatable[2], MINUTE_IN_MILLISECONDS, parsedRepeatable[2]);
    }
}
