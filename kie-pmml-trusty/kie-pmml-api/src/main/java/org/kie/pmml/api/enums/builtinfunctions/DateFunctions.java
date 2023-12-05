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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import org.kie.pmml.api.exceptions.KieEnumException;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.kie.pmml.api.enums.BUILTIN_FUNCTIONS.checkDate;
import static org.kie.pmml.api.enums.BUILTIN_FUNCTIONS.checkDates;
import static org.kie.pmml.api.enums.BUILTIN_FUNCTIONS.checkInteger;
import static org.kie.pmml.api.enums.BUILTIN_FUNCTIONS.checkLength;

public enum DateFunctions {

    DATE_DAYS_SINCE_YEAR("dateDaysSinceYear"),
    DATE_SECONDS_SINCE_YEAR("dateSecondsSinceYear"),
    DATE_SECONDS_SINCE_MIDNIGHT("dateSecondsSinceMidnight");

    private final String name;
    private static final Logger logger = LoggerFactory.getLogger(DateFunctions.class);
    private static final long MILLISECONDS_PER_DAY = 24 * 60 * 60 * 1000;


    DateFunctions(String name) {
        this.name = name;
    }

    public static boolean isDateFunctions(String name) {
        return Arrays.stream(DateFunctions.values())
                .anyMatch(value -> name.equals(value.name));
    }

    public static DateFunctions byName(String name) {
        return Arrays.stream(DateFunctions.values())
                .filter(value -> name.equals(value.name))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find DateFunctions with name: " + name));
    }

    public String getName() {
        return name;
    }

    public Object getValue(final Object[] inputData) {
        switch (this) {
            case DATE_DAYS_SINCE_YEAR:
                return dateDaysSinceYear(inputData);
            case DATE_SECONDS_SINCE_YEAR:
                return dateSecondsSinceYear(inputData);
            case DATE_SECONDS_SINCE_MIDNIGHT:
                return dateSecondsSinceMidnight(inputData);
            default:
                throw new KiePMMLException("Unmanaged DateFunctions " + this);
        }
    }

    private int dateDaysSinceYear(final Object[] inputData) {
        checkLength(inputData, 2);
        checkDate(inputData[0]);
        checkInteger(inputData[1]);

        LocalDateTime referredDate = LocalDateTime.of((int)inputData[1], 1, 1, 0, 0, 0);
        LocalDateTime referringDate = ((Date) inputData[0]).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        return (int) DAYS.between(referredDate, referringDate);
    }

    private int dateSecondsSinceYear(final Object[] inputData) {
        checkLength(inputData, 2);
        checkDate(inputData[0]);
        checkInteger(inputData[1]);
        Date yearDate = new GregorianCalendar((int)inputData[1], Calendar.JANUARY, 1).getTime();
        long diff = ((Date) inputData[0]).getTime() - yearDate.getTime();
        return (int) TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS);
    }

    private int dateSecondsSinceMidnight(final Object[] inputData) {
        checkDates(inputData, 1);
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime((Date) inputData[0]);
        gregorianCalendar.set(Calendar.HOUR, 0);
        gregorianCalendar.set(Calendar.MINUTE, 0);
        gregorianCalendar.set(Calendar.SECOND, 0);
        gregorianCalendar.set(Calendar.MILLISECOND, 0);
        Date midnight = gregorianCalendar.getTime();
        long diff = ((Date) inputData[0]).getTime() - midnight.getTime();
        return (int) TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS);
    }

}
