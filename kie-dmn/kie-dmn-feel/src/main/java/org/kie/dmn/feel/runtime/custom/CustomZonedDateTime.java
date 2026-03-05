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
package org.kie.dmn.feel.runtime.custom;

import org.kie.dmn.feel.runtime.functions.DateAndTimeFunction;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.util.Objects;

/**
 * This class is meant as sort-of <b>decorator</b> over <code>ZonedDateTime</code>, that is a final class.
 * It is used to provide a string representation that preserves seconds even when they are zero,
 */
public final class CustomZonedDateTime
        implements Temporal, ChronoZonedDateTime<LocalDate>, Serializable {

    private final ZonedDateTime zonedDateTime;
    private final String stringRepresentation;

    private CustomZonedDateTime(ZonedDateTime zonedDateTime) {
        this.zonedDateTime = zonedDateTime;
        ZoneId zone = zonedDateTime.getZone();
        if (zone instanceof ZoneOffset) {
            // For ZoneOffset, use ISO format (e.g., 2021-01-01T10:10:10+11:00)
            this.stringRepresentation = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } else {
            // For ZoneRegion, use REGION_DATETIME_FORMATTER which properly handles extended years
            this.stringRepresentation = zonedDateTime.format(DateAndTimeFunction.REGION_DATETIME_FORMATTER);
        }
    }

    public static CustomZonedDateTime of(LocalDate date, LocalTime time, ZoneId zone) {
        return new CustomZonedDateTime(ZonedDateTime.of(date, time, zone));
    }

    public static CustomZonedDateTime from(TemporalAccessor temporal) {
        return new CustomZonedDateTime(ZonedDateTime.from(temporal));
    }

    public static CustomZonedDateTime of(int coercedYear, int coercedMonth, int coercedDay, int coercedHour, int coercedMinute, int coercedSecond, int i, ZoneId zoneId) {
        return new CustomZonedDateTime(ZonedDateTime.of(coercedYear, coercedMonth, coercedDay, coercedHour, coercedMinute, coercedSecond, i, zoneId));
    }

    @Override
    public ChronoLocalDateTime<LocalDate> toLocalDateTime() {
        return zonedDateTime.toLocalDateTime();
    }

    @Override
    public ZoneOffset getOffset() {
        return zonedDateTime.getOffset();
    }

    @Override
    public ZoneId getZone() {
        return zonedDateTime.getZone();
    }

    @Override
    public ChronoZonedDateTime<LocalDate> withEarlierOffsetAtOverlap() {
        return new CustomZonedDateTime(zonedDateTime.withEarlierOffsetAtOverlap());
    }

    @Override
    public ChronoZonedDateTime<LocalDate> withLaterOffsetAtOverlap() {
        return new CustomZonedDateTime(zonedDateTime.withLaterOffsetAtOverlap());
    }

    @Override
    public ChronoZonedDateTime<LocalDate> withZoneSameLocal(ZoneId zone) {
        return new CustomZonedDateTime(zonedDateTime.withZoneSameLocal(zone));
    }

    @Override
    public ChronoZonedDateTime<LocalDate> withZoneSameInstant(ZoneId zone) {
        return new CustomZonedDateTime(zonedDateTime.withZoneSameInstant(zone));
    }

    @Override
    public ChronoZonedDateTime<LocalDate> with(TemporalField field, long newValue) {
        return new CustomZonedDateTime(zonedDateTime.with(field, newValue));
    }

    @Override
    public ChronoZonedDateTime<LocalDate> plus(long amountToAdd, TemporalUnit unit) {
        return new CustomZonedDateTime(zonedDateTime.plus(amountToAdd, unit));
    }

    @Override
    public ChronoZonedDateTime<LocalDate> plus(TemporalAmount amount) {
        return new CustomZonedDateTime(zonedDateTime.plus(amount));
    }

    @Override
    public ChronoZonedDateTime<LocalDate> minus(TemporalAmount amount) {
        return new CustomZonedDateTime(zonedDateTime.minus(amount));
    }

    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        return zonedDateTime.until(endExclusive, unit);
    }

    @Override
    public boolean isSupported(TemporalField field) {
        return zonedDateTime.isSupported(field);
    }

    @Override
    public long getLong(TemporalField field) {
        return zonedDateTime.getLong(field);
    }

    @Override
    public boolean isSupported(TemporalUnit unit) {
        return zonedDateTime.isSupported(unit);
    }

    @Override
    public ChronoZonedDateTime<LocalDate> minus(long amountToSubtract, TemporalUnit unit) {
        return new CustomZonedDateTime(zonedDateTime.minus(amountToSubtract, unit));
    }

    public static CustomZonedDateTime parse(CharSequence text) {
        return new CustomZonedDateTime(ZonedDateTime.parse(text));
    }

    @Override
    public int compareTo(ChronoZonedDateTime<?> other) {
        if (other instanceof CustomZonedDateTime) {
            return zonedDateTime.compareTo(((CustomZonedDateTime) other).zonedDateTime);
        }
        return zonedDateTime.compareTo(other);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof CustomZonedDateTime that) {
            return Objects.equals(zonedDateTime, that.zonedDateTime);
        }
        if (o instanceof ZonedDateTime other) {
            return Objects.equals(zonedDateTime, other);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(zonedDateTime);
    }

    @Override
    public String toString() {
        return stringRepresentation;
    }

    public ZonedDateTime getZonedDateTime() {
        return zonedDateTime;
    }

}