/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.jbpm.process.core.timer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.jbpm.process.core.timer.BusinessCalendarImpl.END_HOUR;
import static org.jbpm.process.core.timer.BusinessCalendarImpl.HOLIDAYS;
import static org.jbpm.process.core.timer.BusinessCalendarImpl.HOLIDAY_DATE_FORMAT;
import static org.jbpm.process.core.timer.BusinessCalendarImpl.START_HOUR;
import static org.jbpm.process.core.timer.BusinessCalendarImpl.TIMEZONE;
import static org.jbpm.process.core.timer.BusinessCalendarImpl.WEEKEND_DAYS;
import static org.jbpm.process.core.timer.CalendarBean.DEFAULT_HOLIDAY_DATE_FORMAT;
import static org.jbpm.process.core.timer.CalendarBean.DEFAULT_TIMEZONE;
import static org.jbpm.process.core.timer.CalendarBean.DEFAULT_WEEKENDS;
import static org.jbpm.process.core.timer.CalendarBean.DEFAULT_WEEKEND_DAYS;

class CalendarBeanTest {

    // Static validation methods
    @ParameterizedTest
    @MethodSource("getMissingPropertiesCalendar")
    void requiredPropertyValidation(Map<String, Object> propertyMap, List<String> errorMessages) {
        Properties calendarConfiguration = new Properties();
        calendarConfiguration.putAll(propertyMap);
        commonStaticMethodValidation(errorMessage -> CalendarBean.requiredPropertyValidation(errorMessage, calendarConfiguration), errorMessages);
    }

    @ParameterizedTest
    @MethodSource("getWronglyFormatPropertiesCalendar")
    void propertyFormatValidation(Map<String, Object> propertyMap, List<String> errorMessages) {
        Properties calendarConfiguration = new Properties();
        calendarConfiguration.putAll(propertyMap);
        commonStaticMethodValidation(errorMessage -> CalendarBean.propertyFormatValidation(errorMessage, calendarConfiguration), errorMessages);
    }

    @ParameterizedTest
    @MethodSource("getBusinessInvalidPropertiesCalendar")
    void businessValidation(Map<String, Object> propertyMap, List<String> errorMessages) {
        Properties calendarConfiguration = new Properties();
        calendarConfiguration.putAll(propertyMap);
        commonStaticMethodValidation(errorMessage -> CalendarBean.businessValidation(errorMessage, calendarConfiguration), errorMessages);
    }

    @ParameterizedTest
    @MethodSource("getPartialPropertiesCalendar")
    void missingDataPopulation(Map<String, Object> propertyMap, Map<String, Object> defaultValuesMap) {
        Properties calendarConfiguration = new Properties();
        calendarConfiguration.putAll(propertyMap);
        defaultValuesMap.keySet().forEach(key -> assertThat(calendarConfiguration.containsKey(key)).isFalse());
        CalendarBean.missingDataPopulation(calendarConfiguration);
        defaultValuesMap.forEach((key, value) -> {
            assertThat(calendarConfiguration.containsKey(key)).isTrue();
            assertThat(calendarConfiguration.getProperty(key)).isEqualTo(value);
        });
    }

    @Test
    void getPropertyAsInt() {
        Properties calendarConfiguration = new Properties();
        String propertyName = "propertyName";
        int originalValue = 1;
        String value = "" + originalValue;
        calendarConfiguration.put(propertyName, value);
        int retrieved = CalendarBean.getPropertyAsInt(propertyName, calendarConfiguration);
        assertThat(retrieved).isEqualTo(originalValue);
        value = "WRONG";
        calendarConfiguration.put(propertyName, value);
        String expectedMessage = "For input string: \"WRONG\"";
        assertThatThrownBy(() -> CalendarBean.getPropertyAsInt(propertyName, calendarConfiguration))
                .isInstanceOf(NumberFormatException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    void validateRequiredProperty() {
        Properties calendarConfiguration = new Properties();
        String propertyName = "propertyName";
        String value = "propertyValue";
        calendarConfiguration.put(propertyName, value);
        StringBuilder errorMessage = new StringBuilder();
        CalendarBean.validateRequiredProperty(propertyName, errorMessage, calendarConfiguration);
        assertThat(errorMessage).isEmpty();
        CalendarBean.validateRequiredProperty("missingProperty", errorMessage, calendarConfiguration);
        String[] retrievedErrors = errorMessage.toString().split("\n");
        assertThat(retrievedErrors).hasSize(1);
        assertThat(retrievedErrors).contains("Property missingProperty is required");
    }

    @Test
    void getFormattedDate() throws ParseException {
        Properties calendarConfiguration = new Properties();
        String dateFormat = "dd-MM-yyyy";
        String date = "27-11-2024";
        calendarConfiguration.put(HOLIDAY_DATE_FORMAT, dateFormat);
        Date retrieved = CalendarBean.getFormattedDate(date, calendarConfiguration);
        Date expected = CalendarBean.getSimpleDateFormat(dateFormat).parse(date);
        assertThat(retrieved).isEqualTo(expected);

    }

    @Test
    void getSimpleDateFormat() {
        SimpleDateFormat retrieved = CalendarBean.getSimpleDateFormat(DEFAULT_HOLIDAY_DATE_FORMAT);
        assertThat(retrieved).isNotNull();
        retrieved = CalendarBean.getSimpleDateFormat("dd-MM-yyyy");
        assertThat(retrieved).isNotNull();
        String wrong = "WRONG";
        String expectedMessage = "Illegal pattern character 'R'";
        assertThatThrownBy(() -> CalendarBean.getSimpleDateFormat(wrong))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }

    // Instance methods
    @ParameterizedTest
    @MethodSource("getMissingPropertiesCalendar")
    void requiredPropertyMissing(Map<String, Object> propertyMap, List<String> errorMessages) {
        Properties calendarConfiguration = new Properties();
        calendarConfiguration.putAll(propertyMap);
        commonIllegalArgumentAssertion(() -> new CalendarBean(calendarConfiguration), errorMessages);
    }

    @ParameterizedTest
    @MethodSource("getWronglyFormatPropertiesCalendar")
    void propertyWrongFormat(Map<String, Object> propertyMap, List<String> errorMessages) {
        Properties calendarConfiguration = new Properties();
        calendarConfiguration.putAll(propertyMap);
        commonIllegalArgumentAssertion(() -> new CalendarBean(calendarConfiguration), errorMessages);
    }

    @ParameterizedTest
    @MethodSource("getBusinessInvalidPropertiesCalendar")
    void businessInvalid(Map<String, Object> propertyMap, List<String> errorMessages) {
        Properties calendarConfiguration = new Properties();
        calendarConfiguration.putAll(propertyMap);
        commonIllegalArgumentAssertion(() -> new CalendarBean(calendarConfiguration), errorMessages);
    }

    @Test
    void instantiationFull() throws ParseException {
        Properties calendarConfiguration = new Properties();
        int startHour = 10;
        int endHour = 16;
        List<Integer> weekendDays = Arrays.asList(3, 4);
        String dateFormat = "dd-MM-yyyy";
        String timezone = "ACT";
        String holidays = "27-11-2024";
        calendarConfiguration.put(START_HOUR, String.valueOf(startHour));
        calendarConfiguration.put(END_HOUR, String.valueOf(endHour));
        calendarConfiguration.put(WEEKEND_DAYS, weekendDays.stream().map(String::valueOf).collect(Collectors.joining(",")));
        calendarConfiguration.put(HOLIDAY_DATE_FORMAT, dateFormat);
        calendarConfiguration.put(TIMEZONE, timezone);
        calendarConfiguration.put(HOLIDAYS, holidays);
        CalendarBean retrieved = new CalendarBean(calendarConfiguration);

        Date from = CalendarBean.getFormattedDate(holidays, calendarConfiguration);
        Date to = CalendarBean.getFormattedDate("28-11-2024", calendarConfiguration);
        assertThat(retrieved.getHolidays()).isEqualTo(List.of(new BusinessCalendarImpl.TimePeriod(from, to)));
        assertThat(retrieved.getWeekendDays()).isEqualTo(weekendDays);
        assertThat(retrieved.getDaysPerWeek()).isEqualTo(7 - weekendDays.size());
        assertThat(retrieved.getTimezone()).isEqualTo(timezone);
        assertThat(retrieved.getStartHour()).isEqualTo(startHour);
        assertThat(retrieved.getEndHour()).isEqualTo(endHour);
        assertThat(retrieved.getHoursInDay()).isEqualTo(endHour - startHour);
    }

    @Test
    void instantiationPartial() {
        Properties calendarConfiguration = new Properties();
        int startHour = 10;
        int endHour = 16;
        calendarConfiguration.put(START_HOUR, String.valueOf(startHour));
        calendarConfiguration.put(END_HOUR, String.valueOf(endHour));
        CalendarBean retrieved = new CalendarBean(calendarConfiguration);
        assertThat(retrieved.getHolidays()).isEqualTo(Collections.emptyList());
        assertThat(retrieved.getWeekendDays()).isEqualTo(DEFAULT_WEEKEND_DAYS);
        assertThat(retrieved.getDaysPerWeek()).isEqualTo(5);
        assertThat(retrieved.getTimezone()).isEqualTo(DEFAULT_TIMEZONE);
        assertThat(retrieved.getStartHour()).isEqualTo(startHour);
        assertThat(retrieved.getEndHour()).isEqualTo(endHour);
        assertThat(retrieved.getHoursInDay()).isEqualTo(endHour - startHour);
    }

    @ParameterizedTest
    @MethodSource("getMissingPropertiesCalendar")
    void missingProperties(Map<String, Object> propertyMap, List<String> errorMessages) {
        Properties calendarConfiguration = new Properties();
        calendarConfiguration.putAll(propertyMap);
        commonIllegalArgumentAssertion(() -> new CalendarBean(calendarConfiguration), errorMessages);
    }

    @ParameterizedTest
    @MethodSource("getInvalidPropertiesCalendar")
    public void invalidProperties(Map<String, Object> propertyMap, List<String> errorMessages) {
        Properties calendarConfiguration = new Properties();
        calendarConfiguration.putAll(propertyMap);
        commonIllegalArgumentAssertion(() -> new CalendarBean(calendarConfiguration), errorMessages);
    }

    // Let's avoid duplication
    private void commonStaticMethodValidation(Consumer<StringBuilder> executedMethod,
            List<String> errorMessages) {
        StringBuilder errors = new StringBuilder();
        assertThat(errors).isEmpty();
        executedMethod.accept(errors);
        assertThat(errors).isNotEmpty();
        String[] retrievedErrors = errors.toString().split("\n");
        assertThat(retrievedErrors).hasSize(errorMessages.size());
        errorMessages.forEach(msg -> assertThat(retrievedErrors).contains(msg));
    }

    private void commonIllegalArgumentAssertion(ThrowableAssert.ThrowingCallable executedMethod, List<String> errorMessages) {
        ThrowableAssert throwableAssert = (ThrowableAssert) assertThatThrownBy(executedMethod)
                .isInstanceOf(IllegalArgumentException.class);
        errorMessages.forEach(throwableAssert::hasMessageContaining);
    }

    private static Stream<Arguments> getMissingPropertiesCalendar() {
        return Stream.of(
                Arguments.of(Map.of(), List.of("Property " + START_HOUR + " is required", "Property " + END_HOUR + " is required")),
                Arguments.of(Map.of(START_HOUR, "9"), List.of("Property " + END_HOUR + " is required")),
                Arguments.of(Map.of(END_HOUR, "17"), List.of("Property " + START_HOUR + " is required")));
    }

    private static Stream<Arguments> getWronglyFormatPropertiesCalendar() {

        return Stream.of(
                Arguments.of(Map.of(START_HOUR, "9", END_HOUR, "25"), List.of(END_HOUR + " 25 outside expected boundaries (0-24)")),
                Arguments.of(Map.of(START_HOUR, "26", END_HOUR, "-2"), List.of(START_HOUR + " 26 outside expected boundaries (0-24)", END_HOUR + " -2 outside expected boundaries (0-24)")),
                Arguments.of(Map.of(START_HOUR, "10", END_HOUR, "4", WEEKEND_DAYS, "1,2,8,9"), List.of(WEEKEND_DAYS + " [8, 9] outside expected boundaries (0-7)")),
                Arguments.of(Map.of(START_HOUR, "10", END_HOUR, "4", WEEKEND_DAYS, "0,1,2"), List.of("0 (= no weekends) and other values provided in the given " + WEEKEND_DAYS + " 0,1,2")),
                Arguments.of(Map.of(START_HOUR, "10", END_HOUR, "4", WEEKEND_DAYS, "1,1,2"), List.of("There are repeated values in the given " + WEEKEND_DAYS + " 1,1,2")),
                Arguments.of(Map.of(START_HOUR, "", END_HOUR, ""), List.of(START_HOUR + " is not valid: For input string: \"\"", END_HOUR + " is not valid: For input string: \"\"")));
    }

    private static Stream<Arguments> getBusinessInvalidPropertiesCalendar() {

        return Stream.of(
                Arguments.of(Map.of(START_HOUR, "10", END_HOUR, "10"), List.of(START_HOUR + " 10 and " + END_HOUR + " 10 must be different")));
    }

    private static Stream<Arguments> getPartialPropertiesCalendar() {

        return Stream.of(
                Arguments.of(Map.of(START_HOUR, "10", END_HOUR, "4", HOLIDAY_DATE_FORMAT, "dd-mm-YYYY", TIMEZONE, "ACT"), Map.of(WEEKEND_DAYS, DEFAULT_WEEKENDS)),
                Arguments.of(Map.of(START_HOUR, "10", END_HOUR, "4", WEEKEND_DAYS, "5,6", TIMEZONE, "ACT"), Map.of(HOLIDAY_DATE_FORMAT, DEFAULT_HOLIDAY_DATE_FORMAT)),
                Arguments.of(Map.of(START_HOUR, "10", END_HOUR, "4", WEEKEND_DAYS, "5,6", HOLIDAY_DATE_FORMAT, "dd-mm-YYYY"), Map.of(TIMEZONE, DEFAULT_TIMEZONE)));
    }

    private static Stream<Arguments> getInvalidPropertiesCalendar() {

        return Stream.of(
                Arguments.of(Map.of(START_HOUR, "9", END_HOUR, "25"), List.of(END_HOUR + " 25 outside expected boundaries (0-24)")),
                Arguments.of(Map.of(START_HOUR, "26", END_HOUR, "-2"), List.of(START_HOUR + " 26 outside expected boundaries (0-24)", END_HOUR + " -2 outside expected boundaries (0-24)")),
                Arguments.of(Map.of(START_HOUR, "10", END_HOUR, "4", WEEKEND_DAYS, "1,2,8,9"), List.of(WEEKEND_DAYS + " [8, 9] outside expected boundaries (0-7)")),
                Arguments.of(Map.of(START_HOUR, "10", END_HOUR, "10"), List.of(START_HOUR + " 10 and " + END_HOUR + " 10 must be different")),
                Arguments.of(Map.of(START_HOUR, "10", END_HOUR, "4", WEEKEND_DAYS, "0,1,2"), List.of("0 (= no weekends) and other values provided in the given " + WEEKEND_DAYS + " 0,1,2")),
                Arguments.of(Map.of(START_HOUR, "10", END_HOUR, "4", WEEKEND_DAYS, "1,1,2"), List.of("There are repeated values in the given " + WEEKEND_DAYS + " 1,1,2")),
                Arguments.of(Map.of(START_HOUR, "", END_HOUR, ""), List.of(START_HOUR + " is not valid: For input string: \"\"", END_HOUR + " is not valid: For input string: \"\"")));
    }
}
