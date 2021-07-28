/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.pmml.api.enums.builtinfunctions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DateFunctionsTest {

    public final static List<DateFunctions> supportedDateFunctions;
    public final static List<DateFunctions> unsupportedDateFunctions;

    static {
        supportedDateFunctions = new ArrayList<>();
        supportedDateFunctions.add(DateFunctions.DATE_DAYS_SINCE_YEAR);
        supportedDateFunctions.add(DateFunctions.DATE_SECONDS_SINCE_YEAR);
        supportedDateFunctions.add(DateFunctions.DATE_SECONDS_SINCE_MIDNIGHT);

        unsupportedDateFunctions = new ArrayList<>();
    }

    @Test
    public void getDateDaysSinceYearCorrectInput() {
        Date inputDate = new GregorianCalendar(2003, Calendar.APRIL, 1).getTime();
        Object[] input1 = {inputDate, 1960};
        Object retrieved = DateFunctions.DATE_DAYS_SINCE_YEAR.getValue(input1);
        assertEquals(15796, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getDateDaysSinceYearWrongSizeInput() {
        final Object[] input = {34};
        DateFunctions.DATE_DAYS_SINCE_YEAR.getValue(input);
    }

    @Test
    public void getDateDaysSinceYearWrongTypeInput() {
        final Object[] input1 = {34, 1970};
        Date inputDate = new GregorianCalendar(2003, Calendar.APRIL, 1).getTime();
        final Object[] input2 = {inputDate, "1970"};
        List<Object[]> inputs = Arrays.asList(input1, input2);
        for (Object[] input : inputs) {
            try {
                DateFunctions.DATE_DAYS_SINCE_YEAR.getValue(input);
                fail("Expecting IllegalArgumentException");
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
            }
        }
    }

    @Test
    public void getDateSecondsSinceYearCorrectInput() {
        Date inputDate = new GregorianCalendar(1960, Calendar.JANUARY, 3, 3, 30, 3 ).getTime();
        Object[] input1 = {inputDate, 1960};
        Object retrieved = DateFunctions.DATE_SECONDS_SINCE_YEAR.getValue(input1);
        assertEquals(185403, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getDateSecondsSinceYearWrongSizeInput() {
        final Object[] input = {34};
        DateFunctions.DATE_SECONDS_SINCE_YEAR.getValue(input);
    }

    @Test
    public void getDateSecondsSinceYearWrongTypeInput() {
        final Object[] input1 = {34, 1970};
        Date inputDate = new GregorianCalendar(2003, Calendar.APRIL, 1).getTime();
        final Object[] input2 = {inputDate, "1970"};
        List<Object[]> inputs = Arrays.asList(input1, input2);
        for (Object[] input : inputs) {
            try {
                DateFunctions.DATE_SECONDS_SINCE_YEAR.getValue(input);
                fail("Expecting IllegalArgumentException");
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
            }
        }
    }

    @Test
    public void getDateSecondsSinceMidnightCorrectInput() {
        Date inputDate = new GregorianCalendar(1960, Calendar.JANUARY, 2, 0, 0, 1 ).getTime();
        Object[] input1 = {inputDate};
        Object retrieved = DateFunctions.DATE_SECONDS_SINCE_MIDNIGHT.getValue(input1);
        assertEquals(1, retrieved);
        inputDate = new GregorianCalendar(1960, Calendar.MARCH, 12, 0, 1, 0 ).getTime();
        Object[] input2 = {inputDate};
        retrieved = DateFunctions.DATE_SECONDS_SINCE_MIDNIGHT.getValue(input2);
        assertEquals(60, retrieved);
        inputDate = new GregorianCalendar(1960, Calendar.DECEMBER, 23, 5, 23, 30 ).getTime();
        Object[] input3 = {inputDate};
        retrieved = DateFunctions.DATE_SECONDS_SINCE_MIDNIGHT.getValue(input3);
        assertEquals(19410, retrieved);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getDateSecondsSinceMidnightWrongSizeInput() {
        Date inputDate1 = new GregorianCalendar(2003, Calendar.APRIL, 1).getTime();
        Date inputDate2 = new GregorianCalendar(2003, Calendar.APRIL, 1).getTime();
        final Object[] input = {inputDate1, inputDate2};
        DateFunctions.DATE_SECONDS_SINCE_MIDNIGHT.getValue(input);
    }

    @Test
    public void getDateSecondsSinceMidnightWrongTypeInput() {
        final Object[] input1 = {34};
        final Object[] input2 = {"1970"};
        List<Object[]> inputs = Arrays.asList(input1, input2);
        for (Object[] input : inputs) {
            try {
                DateFunctions.DATE_SECONDS_SINCE_MIDNIGHT.getValue(input);
                fail("Expecting IllegalArgumentException");
            } catch (Exception e) {
                assertTrue(e instanceof IllegalArgumentException);
            }
        }
    }

}