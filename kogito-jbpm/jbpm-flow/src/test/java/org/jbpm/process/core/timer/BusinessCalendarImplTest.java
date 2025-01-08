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
package org.jbpm.process.core.timer;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jbpm.process.core.timer.BusinessCalendarImpl.END_HOUR;
import static org.jbpm.process.core.timer.BusinessCalendarImpl.HOLIDAYS;
import static org.jbpm.process.core.timer.BusinessCalendarImpl.HOLIDAY_DATE_FORMAT;
import static org.jbpm.process.core.timer.BusinessCalendarImpl.START_HOUR;
import static org.jbpm.process.core.timer.BusinessCalendarImpl.WEEKEND_DAYS;

class BusinessCalendarImplTest extends AbstractBaseTest {

    public void addLogger() {
        logger = LoggerFactory.getLogger(this.getClass());
    }

    @Test
    void instantiate() {
        BusinessCalendarImpl retrieved = BusinessCalendarImpl.builder().build();
        assertThat(retrieved).isNotNull();
        retrieved = BusinessCalendarImpl.builder()
                .withCalendarBean(CalendarBeanFactory.createCalendarBean())
                .build();
        assertThat(retrieved).isNotNull();

        Properties calendarConfiguration = new Properties();
        int startHour = 10;
        int endHour = 16;
        calendarConfiguration.put(START_HOUR, String.valueOf(startHour));
        calendarConfiguration.put(END_HOUR, String.valueOf(endHour));
        retrieved = BusinessCalendarImpl.builder()
                .withCalendarBean(new CalendarBean(calendarConfiguration))
                .build();
        assertThat(retrieved).isNotNull();
    }

    @Test
    void calculateBusinessTimeAsDateInsideDailyWorkingHourWithDelay() {
        int daysToSkip = 0; // since executionHourDelay falls before endHOurGap
        commonCalculateBusinessTimeAsDateAssertBetweenHours(-4, 4, 0, 3, daysToSkip, null, null);
    }

    @Test
    void calculateBusinessTimeAsDateInsideDailyWorkingHourWithoutDelay() {
        int daysToSkip = 0; // since executionHourDelay falls before endHOurGap
        commonCalculateBusinessTimeAsDateAssertBetweenHours(-4, 4, 0, 0, daysToSkip, null, null);
    }

    @Disabled("TO FIX https://github.com/apache/incubator-kie-issues/issues/1651")
    @Test
    void calculateBusinessTimeAsDateInsideNightlyWorkingHour() {
        int daysToSkip = 0; // since executionHourDelay falls before endHOurGap
        commonCalculateBusinessTimeAsDateAssertBetweenHours(4, -4, 0, 3, daysToSkip, null, null);
    }

    @Test
    void calculateBusinessTimeAsDateBeforeWorkingHourWithDelay() {
        int daysToSkip = 0; // since executionHourDelay falls before endHOurGap
        commonCalculateBusinessTimeAsDateAssertBetweenHours(2, 4, -1, 1, daysToSkip, null, null);
    }

    @Test
    void calculateBusinessTimeAsDateBeforeWorkingHourWithDelayFineGrained() {
        // lets pretend 2024-11-28 10:48:33 is the current time
        Calendar testingCalendar = Calendar.getInstance();
        testingCalendar.set(Calendar.YEAR, 2024);
        testingCalendar.set(Calendar.MONTH, Calendar.NOVEMBER);
        testingCalendar.set(Calendar.DAY_OF_MONTH, 28);
        testingCalendar.set(Calendar.HOUR_OF_DAY, 10);
        testingCalendar.set(Calendar.MINUTE, 48);
        testingCalendar.set(Calendar.SECOND, 33);

        int startHour = 14;
        Properties config = new Properties();
        config.setProperty(BusinessCalendarImpl.START_HOUR, String.valueOf(startHour));
        config.setProperty(BusinessCalendarImpl.END_HOUR, "18");
        config.setProperty(WEEKEND_DAYS, "0");

        String delay = "10m";
        BusinessCalendarImpl businessCal = BusinessCalendarImpl.builder().withCalendarBean(new CalendarBean(config))
                .withTestingCalendar(testingCalendar)
                .build();
        Date retrieved = businessCal.calculateBusinessTimeAsDate(delay);
        String expectedDate = "2024-11-28 14:10:00";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String retrievedTime = sdf.format(retrieved);
        assertThat(retrievedTime).isEqualTo(expectedDate);

        delay = "10s";
        retrieved = businessCal.calculateBusinessTimeAsDate(delay);
        expectedDate = "2024-11-28 14:00:10";
        retrievedTime = sdf.format(retrieved);
        assertThat(retrievedTime).isEqualTo(expectedDate);

        delay = "10m 10s";
        retrieved = businessCal.calculateBusinessTimeAsDate(delay);
        expectedDate = "2024-11-28 14:10:10";
        retrievedTime = sdf.format(retrieved);
        assertThat(retrievedTime).isEqualTo(expectedDate);
    }

    @Test
    void calculateBusinessTimeAsDateBeforeWorkingHourWithoutDelay() {
        int daysToSkip = 0; // since executionHourDelay falls before endHOurGap
        commonCalculateBusinessTimeAsDateAssertBetweenHours(-1, 4, -2, 1, daysToSkip, null, null);
    }

    @Test
    void calculateBusinessTimeAsDateAfterWorkingHour() {
        int daysToSkip = 1; // because the executionHourDelay is bigger to endHOurGap, so it goes to next day;
        commonCalculateBusinessTimeAsDateAssertAtStartHour(-1, 2, 3, 3, daysToSkip, null, null);
        commonCalculateBusinessTimeAsDateAssertAtStartHour(0, 6, 1, 5, daysToSkip, null, null);
    }

    @Test
    void calculateBusinessTimeAsDateWhenTodayAndTomorrowAreHolidays() {
        String holidayDateFormat = "yyyy-MM-dd";
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern(holidayDateFormat);
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        String holidays = sdf.format(today) + "," + sdf.format(tomorrow);
        int daysToSkip = 2; // because both today and tomorrow are holiday
        // endHOurGap and executionHourDelay are not relevant in this context
        commonCalculateBusinessTimeAsDateAssertBetweenHours(-4, 4, 0, 3, daysToSkip, holidayDateFormat, holidays);
        commonCalculateBusinessTimeAsDateAssertBetweenHours(-4, 4, 5, 3, daysToSkip, holidayDateFormat, holidays);
    }

    @Test
    void calculateBusinessTimeAsDateWhenNextDayIsHoliday() {
        String holidayDateFormat = "yyyy-MM-dd";
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern(holidayDateFormat);
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String holidays = sdf.format(tomorrow);
        // 1 because the executionHourDelay is equal to endHOurGap, so it goes to next day;
        // 1 because next day is holiday
        int daysToSkip = 2;

        commonCalculateBusinessTimeAsDateAssertBetweenHours(-4, 4, 0, 4, daysToSkip, holidayDateFormat, holidays);
        daysToSkip = 0; // since executionHourDelay falls before endHOurGap
        commonCalculateBusinessTimeAsDateAssertBetweenHours(-4, 4, 0, 3, daysToSkip, holidayDateFormat, holidays);
    }

    @Test
    void rollCalendarToDailyWorkingHour() {
        int startHour = 14;
        int endHour = 16;
        Calendar toRoll = Calendar.getInstance();
        int currentHour = 8;
        toRoll.set(Calendar.HOUR_OF_DAY, currentHour);
        int dayOfYear = toRoll.get(Calendar.DAY_OF_YEAR);
        BusinessCalendarImpl.rollCalendarToDailyWorkingHour(toRoll, startHour, endHour);
        assertThat(toRoll.get(Calendar.HOUR_OF_DAY)).isEqualTo(startHour);
        assertThat(toRoll.get(Calendar.DAY_OF_YEAR)).isEqualTo(dayOfYear);

        toRoll = Calendar.getInstance();
        currentHour = 19;
        toRoll.set(Calendar.HOUR_OF_DAY, currentHour);
        dayOfYear = toRoll.get(Calendar.DAY_OF_YEAR);
        BusinessCalendarImpl.rollCalendarToDailyWorkingHour(toRoll, startHour, endHour);
        assertThat(toRoll.get(Calendar.HOUR_OF_DAY)).isEqualTo(startHour);
        assertThat(toRoll.get(Calendar.DAY_OF_YEAR)).isEqualTo(dayOfYear + 1);
    }

    @Disabled("TO FIX https://github.com/apache/incubator-kie-issues/issues/1651")
    @Test
    void rollCalendarToNightlyWorkingHour() {
        int startHour = 20;
        int endHour = 4;
        Calendar toRoll = Calendar.getInstance();
        int currentHour = 21;
        toRoll.set(Calendar.HOUR_OF_DAY, currentHour);
        int dayOfYear = toRoll.get(Calendar.DAY_OF_YEAR);
        BusinessCalendarImpl.rollCalendarToNightlyWorkingHour(toRoll, startHour, endHour);
        assertThat(toRoll.get(Calendar.HOUR_OF_DAY)).isEqualTo(startHour);
        assertThat(toRoll.get(Calendar.DAY_OF_YEAR)).isEqualTo(dayOfYear);

        toRoll = Calendar.getInstance();
        currentHour = 3;
        toRoll.set(Calendar.HOUR_OF_DAY, currentHour);
        dayOfYear = toRoll.get(Calendar.DAY_OF_YEAR);
        BusinessCalendarImpl.rollCalendarToNightlyWorkingHour(toRoll, startHour, endHour);
        assertThat(toRoll.get(Calendar.HOUR_OF_DAY)).isEqualTo(startHour);
        assertThat(toRoll.get(Calendar.DAY_OF_YEAR)).isEqualTo(dayOfYear + 1);

    }

    @Test
    void rollCalendarAfterHolidaysWithoutYearRollover() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, 360);
        calendar.set(Calendar.YEAR, 2025);
        Instant currentInstant = calendar.toInstant();

        Instant startHolidayInstant = currentInstant.minus(2, DAYS);
        Instant endHolidayInstant = currentInstant.plus(4, DAYS);
        List<BusinessCalendarImpl.TimePeriod> holidays = Collections.singletonList(
                new BusinessCalendarImpl.TimePeriod(Date.from(startHolidayInstant), Date.from(endHolidayInstant)));
        List<Integer> weekendDays = Collections.emptyList();

        BusinessCalendarImpl.rollCalendarAfterHolidays(calendar, holidays, weekendDays, false);

        int expectedDayOfYear = 360 + 4 + 1; //last day of the year, as it is not leap year
        assertThat(calendar.get(Calendar.DAY_OF_YEAR)).isEqualTo(expectedDayOfYear);
    }

    @Test
    void rollCalendarAfterHolidaysWithYearRollover() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, 361);
        calendar.set(Calendar.YEAR, 2025);
        Instant currentInstant = calendar.toInstant();

        Instant startHolidayInstant = currentInstant.minus(2, DAYS);
        Instant endHolidayInstant = currentInstant.plus(4, DAYS);
        List<BusinessCalendarImpl.TimePeriod> holidays = Collections.singletonList(
                new BusinessCalendarImpl.TimePeriod(Date.from(startHolidayInstant), Date.from(endHolidayInstant)));
        List<Integer> weekendDays = Collections.emptyList();

        BusinessCalendarImpl.rollCalendarAfterHolidays(calendar, holidays, weekendDays, false);

        int expectedDayOfYear = 1; //since 2025 is not leap year
        assertThat(calendar.get(Calendar.DAY_OF_YEAR)).isEqualTo(expectedDayOfYear);
    }

    @Test
    void rollCalendarToNextWorkingDayIfCurrentDayIsNonWorking() {
        List<Integer> workingDays = IntStream.range(Calendar.MONDAY, Calendar.SATURDAY).boxed().toList();
        List<Integer> weekendDays = Arrays.asList(Calendar.SATURDAY, Calendar.SUNDAY);
        boolean resetTime = false;
        workingDays.forEach(workingDay -> {
            Calendar calendar = getCalendarAtExpectedWeekDay(workingDay);
            BusinessCalendarImpl.rollCalendarToNextWorkingDayIfCurrentDayIsNonWorking(calendar, weekendDays, resetTime);
            assertThat(calendar.get(Calendar.DAY_OF_WEEK)).isEqualTo(workingDay);
        });
        weekendDays.forEach(weekendDay -> {
            Calendar calendar = getCalendarAtExpectedWeekDay(weekendDay);
            BusinessCalendarImpl.rollCalendarToNextWorkingDayIfCurrentDayIsNonWorking(calendar, weekendDays, resetTime);
            assertThat(calendar.get(Calendar.DAY_OF_WEEK)).isEqualTo(Calendar.MONDAY);
        });
    }

    @Test
    void isWorkingDay() {
        List<Integer> workingDays = IntStream.range(Calendar.MONDAY, Calendar.SATURDAY).boxed().toList();
        List<Integer> weekendDays = Arrays.asList(Calendar.SATURDAY, Calendar.SUNDAY);
        workingDays.forEach(workingDay -> assertThat(BusinessCalendarImpl.isWorkingDay(weekendDays, workingDay)).isTrue());
        weekendDays.forEach(workingDay -> assertThat(BusinessCalendarImpl.isWorkingDay(weekendDays, workingDay)).isFalse());
    }

    private void commonCalculateBusinessTimeAsDateAssertBetweenHours(int startHourGap, int endHourGap, int testingCalendarHourGap, int executionHourDelay, int daysToSkip, String holidayDateFormat,
            String holidays) {
        BiFunction<Instant, Instant, Boolean> startBooleanCondition = (resultInstant, expectedStartTime) -> {
            logger.debug("Check if {} is after or equal to {} ", resultInstant, expectedStartTime);
            return !resultInstant.isBefore(expectedStartTime);
        };
        commonCalculateBusinessTimeAsDate(startHourGap,
                endHourGap,
                testingCalendarHourGap,
                executionHourDelay,
                daysToSkip,
                holidayDateFormat,
                holidays,
                startBooleanCondition);
    }

    private void commonCalculateBusinessTimeAsDateAssertAtStartHour(int startHourGap, int endHourGap, int testingCalendarHourGap, int executionHourDelay, int daysToSkip, String holidayDateFormat,
            String holidays) {
        BiFunction<Instant, Instant, Boolean> startBooleanCondition = (resultInstant, expectedStartTime) -> {
            logger.debug("Check if {} is equal to {} ", resultInstant, expectedStartTime);
            return resultInstant.getEpochSecond() == expectedStartTime.getEpochSecond();
        };
        commonCalculateBusinessTimeAsDate(startHourGap,
                endHourGap,
                testingCalendarHourGap,
                executionHourDelay,
                daysToSkip,
                holidayDateFormat,
                holidays,
                startBooleanCondition);
    }

    private void commonCalculateBusinessTimeAsDate(int startHourGap,
            int endHourGap, int testingCalendarHourGap,
            int executionHourDelay, int daysToSkip, String holidayDateFormat, String holidays,
            BiFunction<Instant, Instant, Boolean> startBooleanCondition) {
        logger.debug("startHourGap {}", startHourGap);
        logger.debug("endHourGap {}", endHourGap);
        logger.debug("testingCalendarHourGap {}", testingCalendarHourGap);
        logger.debug("executionHourDelay {}", executionHourDelay);
        logger.debug("numberOfHolidays {}", daysToSkip);
        logger.debug("holidayDateFormat {}", holidayDateFormat);
        logger.debug("holidays {}", holidays);

        // lets pretend 12.00 is the current time
        Calendar testingCalendar = Calendar.getInstance();
        testingCalendar.set(Calendar.HOUR_OF_DAY, 12);
        testingCalendar.set(Calendar.MINUTE, 0);
        testingCalendar.set(Calendar.SECOND, 0);
        logger.debug("testingCalendar {}", testingCalendar.getTime());
        Calendar startCalendar = (Calendar) testingCalendar.clone();
        startCalendar.add(Calendar.HOUR_OF_DAY, startHourGap);
        logger.debug("startCalendar {}", startCalendar.getTime());
        Calendar endCalendar = (Calendar) testingCalendar.clone();
        endCalendar.add(Calendar.HOUR_OF_DAY, endHourGap);
        logger.debug("endCalendar {}", endCalendar.getTime());

        int startHour = startCalendar.get(Calendar.HOUR_OF_DAY);
        int endHour = endCalendar.get(Calendar.HOUR_OF_DAY);

        // We need to reconciliate for daily/working hours and daily/nightly hours
        int hoursInDay = startHour < endHour ? endHour - startHour : 24 - (startHour - endHour);
        int daysToAdd = daysToSkip;
        logger.debug("daysToAdd (= numberOfHolidays) {}", daysToAdd);
        if (executionHourDelay >= hoursInDay) {
            daysToAdd += executionHourDelay / hoursInDay;
            logger.debug("daysToAdd += (hourDelay / hoursInDay) {}", daysToAdd);
        }
        if (daysToAdd > 0) {
            startCalendar.add(Calendar.DAY_OF_YEAR, daysToAdd);
            endCalendar.add(Calendar.DAY_OF_YEAR, daysToAdd);
            logger.debug("startCalendar (startCalendar + days to add) {}", startCalendar.getTime());
            logger.debug("endCalendar (endCalendar + days to add) {}", endCalendar.getTime());
        }

        Properties config = new Properties();
        config.setProperty(START_HOUR, String.valueOf(startHour));
        config.setProperty(END_HOUR, String.valueOf(endHour));
        config.setProperty(WEEKEND_DAYS, "0");
        if (holidayDateFormat != null) {
            config.setProperty(HOLIDAY_DATE_FORMAT, holidayDateFormat);
        }
        if (holidays != null) {
            config.setProperty(HOLIDAYS, holidays);
        }

        testingCalendar.add(Calendar.HOUR_OF_DAY, testingCalendarHourGap);
        logger.debug("testingCalendar after testingCalendarHourGap {}", testingCalendar.getTime());
        BusinessCalendarImpl businessCal = BusinessCalendarImpl.builder().withCalendarBean(new CalendarBean(config))
                .withTestingCalendar(testingCalendar)
                .build();
        Date retrieved = businessCal.calculateBusinessTimeAsDate(String.format("%sh", executionHourDelay));
        logger.debug("retrieved {}", retrieved);

        Date expectedStart = startCalendar.getTime();
        Date expectedEnd = endCalendar.getTime();

        Instant retrievedInstant = retrieved.toInstant();
        Instant expectedStartTime = expectedStart.toInstant();
        Instant expectedEndTime = expectedEnd.toInstant();

        logger.debug("retrievedInstant {}", retrievedInstant);
        logger.debug("expectedStartTime {}", expectedStartTime);
        logger.debug("expectedEndTime {}", expectedEndTime);

        assertThat(startBooleanCondition.apply(retrievedInstant, expectedStartTime)).isTrue();
        logger.debug("Check if {} is not after {} ", retrievedInstant, expectedEndTime);
        assertThat(retrievedInstant.isAfter(expectedEndTime)).isFalse();
    }

    private Calendar getCalendarAtExpectedWeekDay(int weekDay) {
        Calendar toReturn = Calendar.getInstance();
        while (toReturn.get(Calendar.DAY_OF_WEEK) != weekDay) {
            toReturn.add(Calendar.DAY_OF_YEAR, 1);
        }
        return toReturn;
    }

}
