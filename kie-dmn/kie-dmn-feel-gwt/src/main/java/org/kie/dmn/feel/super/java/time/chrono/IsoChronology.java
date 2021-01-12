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

import java.io.Serializable;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.AbstractChronology;
import java.time.chrono.Era;
import java.time.chrono.IsoEra;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.util.List;
import java.util.Map;

public final class IsoChronology extends AbstractChronology implements Serializable {

    public String getId() {
        return null;
    }

    public String getCalendarType() {
        return null;
    }

    public LocalDate date(final Era era, final int yearOfEra, final int month, final int dayOfMonth) {
        return null;
    }

    public LocalDate date(final int prolepticYear, final int month, final int dayOfMonth) {
        return null;
    }

    public LocalDate dateYearDay(final Era era, final int yearOfEra, final int dayOfYear) {
        return null;
    }

    public LocalDate dateYearDay(final int prolepticYear, final int dayOfYear) {
        return null;
    }

    public LocalDate dateEpochDay(final long epochDay) {
        return null;
    }

    public LocalDate date(final TemporalAccessor temporal) {
        return null;
    }

    public LocalDateTime localDateTime(final TemporalAccessor temporal) {
        return null;
    }

    public ZonedDateTime zonedDateTime(final TemporalAccessor temporal) {
        return null;
    }

    public ZonedDateTime zonedDateTime(final Instant instant, final ZoneId zone) {
        return null;
    }

    public LocalDate dateNow() {
        return null;
    }

    public LocalDate dateNow(final ZoneId zone) {
        return null;
    }

    public LocalDate dateNow(final Clock clock) {
        return null;
    }

    public boolean isLeapYear(final long prolepticYear) {
        return true;
    }

    public int prolepticYear(final Era era, final int yearOfEra) {
        return 0;
    }

    public IsoEra eraOf(final int eraValue) {
        return null;
    }

    public List<Era> eras() {
        return null;
    }

    public LocalDate resolveDate(final Map<TemporalField, Long> fieldValues, final ResolverStyle resolverStyle) {
        return null;
    }

    public ValueRange range(final ChronoField field) {
        return null;
    }

    public Period period(final int years, final int months, final int days) {
        return null;
    }
}
