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

package java.time.chrono;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.util.Comparator;

public interface ChronoLocalDate
        extends Temporal,
                TemporalAdjuster,
                Comparable<ChronoLocalDate> {

    static Comparator<ChronoLocalDate> timeLineOrder() {
        return null;
    }

    static ChronoLocalDate from(TemporalAccessor temporal) {
        return null;
    }

    Chronology getChronology();

    default Era getEra() {
        return null;
    }

    default boolean isLeapYear() {
        return true;
    }

    int lengthOfMonth();

    default int lengthOfYear() {
        return 0;
    }

    default boolean isSupported(TemporalField field) {
        return true;
    }

    default boolean isSupported(TemporalUnit unit) {
        return true;
    }

    default ChronoLocalDate with(TemporalAdjuster adjuster) {
        return null;
    }

    default ChronoLocalDate with(TemporalField field, long newValue) {
        return null;
    }

    default ChronoLocalDate plus(TemporalAmount amount) {
        return null;
    }

    default ChronoLocalDate plus(long amountToAdd, TemporalUnit unit) {
        return null;
    }

    default ChronoLocalDate minus(TemporalAmount amount) {
        return null;
    }

    default ChronoLocalDate minus(long amountToSubtract, TemporalUnit unit) {
        return null;
    }

    default <R> R query(TemporalQuery<R> query) {
        return null;
    }

    default Temporal adjustInto(Temporal temporal) {
        return null;
    }

    long until(Temporal endExclusive, TemporalUnit unit);

    ChronoPeriod until(ChronoLocalDate endDateExclusive);

    default String format(DateTimeFormatter formatter) {
        return null;
    }

    default ChronoLocalDateTime<?> atTime(LocalTime localTime) {
        return null;
    }

    default long toEpochDay() {
        return 0L;
    }

    default int compareTo(ChronoLocalDate other) {
        return 0;
    }

    default boolean isAfter(ChronoLocalDate other) {
        return true;
    }

    default boolean isBefore(ChronoLocalDate other) {
        return true;
    }

    default boolean isEqual(ChronoLocalDate other) {
        return true;
    }

    boolean equals(Object obj);

    int hashCode();

    String toString();
}
