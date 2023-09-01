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
package org.kie.pmml.api.enums.builtinfunctions;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class DateFunctionsTest {

    public final static List<DateFunctions> supportedDateFunctions;
    public final static List<DateFunctions> unsupportedDateFunctions;
    private static final Logger logger = LoggerFactory.getLogger(DateFunctionsTest.class);

    static {
        supportedDateFunctions = new ArrayList<>();
        supportedDateFunctions.add(DateFunctions.DATE_DAYS_SINCE_YEAR);
        supportedDateFunctions.add(DateFunctions.DATE_SECONDS_SINCE_YEAR);
        supportedDateFunctions.add(DateFunctions.DATE_SECONDS_SINCE_MIDNIGHT);

        unsupportedDateFunctions = new ArrayList<>();
    }

    @Test
    void getDateDaysSinceYearCorrectInput() {
        LocalDateTime inputDateLocalDateTime = LocalDateTime.of(1960, 1, 1, 0, 0, 0);
        logger.debug("inputDateLocalDateTime {}", inputDateLocalDateTime);
        Date inputDate = java.util.Date.from(inputDateLocalDateTime.atZone(ZoneId.systemDefault())
                                                     .toInstant());
        logger.debug("Input date");
        logger.debug("{}", inputDate);
        logger.debug("{}", inputDate.getTime());
        Object[] input1 = {inputDate, 1960};
        Object retrieved = DateFunctions.DATE_DAYS_SINCE_YEAR.getValue(input1);
        assertThat(retrieved).isEqualTo(0);
        //--
        inputDateLocalDateTime = LocalDateTime.of(2003, 4, 1, 0, 0, 0);
        logger.debug("inputDateLocalDateTime {}", inputDateLocalDateTime);
        inputDate = java.util.Date.from(inputDateLocalDateTime.atZone(ZoneId.systemDefault())
                                                     .toInstant());
        logger.debug("Input date");
        logger.debug("{}", inputDate);
        logger.debug("{}", inputDate.getTime());
        Object[] input2 = {inputDate, 1960};
        retrieved = DateFunctions.DATE_DAYS_SINCE_YEAR.getValue(input2);
        assertThat(retrieved).isEqualTo(15796);
    }

    @Test
    void getDateDaysSinceYearWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {34};
            DateFunctions.DATE_DAYS_SINCE_YEAR.getValue(input);
        });
    }

    @Test
    void getDateDaysSinceYearWrongTypeInput() {
        final Object[] input1 = {34, 1970};
        Date inputDate = new GregorianCalendar(2003, Calendar.APRIL, 1).getTime();
        final Object[] input2 = {inputDate, "1970"};
        List<Object[]> inputs = Arrays.asList(input1, input2);
        for (Object[] input : inputs) {
            try {
                DateFunctions.DATE_DAYS_SINCE_YEAR.getValue(input);
                fail("Expecting IllegalArgumentException");
            } catch (Exception e) {
                assertThat(e).isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

    @Test
    void getDateSecondsSinceYearCorrectInput() {
        Date inputDate = new GregorianCalendar(1960, Calendar.JANUARY, 3, 3, 30, 3).getTime();
        Object[] input1 = {inputDate, 1960};
        Object retrieved = DateFunctions.DATE_SECONDS_SINCE_YEAR.getValue(input1);
        assertThat(retrieved).isEqualTo(185403);
    }

    @Test
    void getDateSecondsSinceYearWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            final Object[] input = {34};
            DateFunctions.DATE_SECONDS_SINCE_YEAR.getValue(input);
        });
    }

    @Test
    void getDateSecondsSinceYearWrongTypeInput() {
        final Object[] input1 = {34, 1970};
        Date inputDate = new GregorianCalendar(2003, Calendar.APRIL, 1).getTime();
        final Object[] input2 = {inputDate, "1970"};
        List<Object[]> inputs = Arrays.asList(input1, input2);
        for (Object[] input : inputs) {
            try {
                DateFunctions.DATE_SECONDS_SINCE_YEAR.getValue(input);
                fail("Expecting IllegalArgumentException");
            } catch (Exception e) {
                assertThat(e).isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

    @Test
    void getDateSecondsSinceMidnightCorrectInput() {
        Date inputDate = new GregorianCalendar(1960, Calendar.JANUARY, 2, 0, 0, 1).getTime();
        Object[] input1 = {inputDate};
        Object retrieved = DateFunctions.DATE_SECONDS_SINCE_MIDNIGHT.getValue(input1);
        assertThat(retrieved).isEqualTo(1);
        inputDate = new GregorianCalendar(1960, Calendar.MARCH, 12, 0, 1, 0).getTime();
        Object[] input2 = {inputDate};
        retrieved = DateFunctions.DATE_SECONDS_SINCE_MIDNIGHT.getValue(input2);
        assertThat(retrieved).isEqualTo(60);
        inputDate = new GregorianCalendar(1960, Calendar.DECEMBER, 23, 5, 23, 30).getTime();
        Object[] input3 = {inputDate};
        retrieved = DateFunctions.DATE_SECONDS_SINCE_MIDNIGHT.getValue(input3);
        assertThat(retrieved).isEqualTo(19410);
    }

    @Test
    void getDateSecondsSinceMidnightWrongSizeInput() {
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> {
            Date inputDate1 = new GregorianCalendar(2003, Calendar.APRIL, 1).getTime();
            Date inputDate2 = new GregorianCalendar(2003, Calendar.APRIL, 1).getTime();
            final Object[] input = {inputDate1, inputDate2};
            DateFunctions.DATE_SECONDS_SINCE_MIDNIGHT.getValue(input);
        });
    }

    @Test
    void getDateSecondsSinceMidnightWrongTypeInput() {
        final Object[] input1 = {34};
        final Object[] input2 = {"1970"};
        List<Object[]> inputs = Arrays.asList(input1, input2);
        for (Object[] input : inputs) {
            try {
                DateFunctions.DATE_SECONDS_SINCE_MIDNIGHT.getValue(input);
                fail("Expecting IllegalArgumentException");
            } catch (Exception e) {
                assertThat(e).isInstanceOf(IllegalArgumentException.class);
            }
        }
    }
}