/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.nurserostering.domain;

import java.util.Calendar;

public enum DayOfWeek {
    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday"),
    SUNDAY("Sunday");

    public static DayOfWeek valueOfCalendar(int calendarDayInWeek) {
        switch (calendarDayInWeek) {
            case Calendar.SUNDAY:
                return SUNDAY;
            case Calendar.MONDAY:
                return MONDAY;
            case Calendar.TUESDAY:
                return TUESDAY;
            case Calendar.WEDNESDAY:
                return WEDNESDAY;
            case Calendar.THURSDAY:
                return THURSDAY;
            case Calendar.FRIDAY:
                return FRIDAY;
            case Calendar.SATURDAY:
                return SATURDAY;
            default:
                throw new IllegalArgumentException("The calendarDayInWeek (" + calendarDayInWeek
                        + ") is not supported.");
        }
    }

    public static DayOfWeek valueOfCode(String code) {
        for (DayOfWeek dayOfWeek : values()) {
            if (code.equalsIgnoreCase(dayOfWeek.getCode())) {
                return dayOfWeek;
            }
        }
        return null;
    }

    private String code;

    private DayOfWeek(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public int getDistanceToNext(DayOfWeek other) {
        int distance = other.ordinal() - ordinal();
        if (distance < 0) {
            distance += 7;
        }
        return distance;
    }

    public DayOfWeek determineNextDayOfWeek() {
        switch (this) {
            case MONDAY:
                return TUESDAY;
            case TUESDAY:
                return WEDNESDAY;
            case WEDNESDAY:
                return THURSDAY;
            case THURSDAY:
                return FRIDAY;
            case FRIDAY:
                return SATURDAY;
            case SATURDAY:
                return SUNDAY;
            case SUNDAY:
                return MONDAY;
            default:
                throw new IllegalArgumentException("The dayOfWeek (" + this + ") is not supported.");
        }
    }

    public String getLabel() {
        return code.substring(0, 2);
    }

    public String toString() {
        return code.substring(0, 3);
    }

}
