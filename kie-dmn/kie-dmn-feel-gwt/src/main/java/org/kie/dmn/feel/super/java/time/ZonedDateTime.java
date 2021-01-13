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
package java.time;

import java.io.Serializable;
import java.time.chrono.ChronoZonedDateTime;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;

public final class ZonedDateTime
        implements Temporal,
                   ChronoZonedDateTime<LocalDate>,
                   Serializable {

    public boolean isSupported(final TemporalField field) {
        return true;
    }

    public boolean isSupported(final TemporalUnit unit) {
        return true;
    }

    public ValueRange range(final TemporalField field) {
        return null;
    }

    public int get(final TemporalField field) {
        return 0;
    }

    public long getLong(final TemporalField field) {
        return 0L;
    }

    public ZoneOffset getOffset() {
        return null;
    }

    public ZonedDateTime withEarlierOffsetAtOverlap() {
        return null;
    }

    public ZonedDateTime withLaterOffsetAtOverlap() {
        return null;
    }

    public ZoneId getZone() {
        return null;
    }

    public ZonedDateTime withZoneSameLocal(final ZoneId zone) {
        return null;
    }

    public ZonedDateTime withZoneSameInstant(final ZoneId zone) {
        return null;
    }

    public ZonedDateTime withFixedOffsetZone() {
        return null;
    }

    public LocalDateTime toLocalDateTime() {
        return null;
    }

    public LocalDate toLocalDate() {
        return null;
    }

    public int getYear() {
        return 0;
    }

    public int getMonthValue() {
        return 0;
    }

    public Month getMonth() {
        return null;
    }

    public int getDayOfMonth() {
        return 0;
    }

    public int getDayOfYear() {
        return 0;
    }

    public DayOfWeek getDayOfWeek() {
        return null;
    }

    public LocalTime toLocalTime() {
        return null;
    }

    public int getHour() {
        return 0;
    }

    public int getMinute() {
        return 0;
    }

    public int getSecond() {
        return 0;
    }

    public int getNano() {
        return 0;
    }

    public ZonedDateTime with(final TemporalAdjuster adjuster) {
        return null;
    }

    public ZonedDateTime with(final TemporalField field, final long newValue) {
        return null;
    }

    public ZonedDateTime withYear(final int year) {
        return null;
    }

    public ZonedDateTime withMonth(final int month) {
        return null;
    }

    public ZonedDateTime withDayOfMonth(final int dayOfMonth) {
        return null;
    }

    public ZonedDateTime withDayOfYear(final int dayOfYear) {
        return null;
    }

    public ZonedDateTime withHour(final int hour) {
        return null;
    }

    public ZonedDateTime withMinute(final int minute) {
        return null;
    }

    public ZonedDateTime withSecond(final int second) {
        return null;
    }

    public ZonedDateTime withNano(final int nanoOfSecond) {
        return null;
    }

    public ZonedDateTime truncatedTo(final TemporalUnit unit) {
        return null;
    }

    public ZonedDateTime plus(final TemporalAmount amountToAdd) {
        return null;
    }

    public ZonedDateTime plus(final long amountToAdd, final TemporalUnit unit) {
        return null;
    }

    public ZonedDateTime plusYears(final long years) {
        return null;
    }

    public ZonedDateTime plusMonths(final long months) {
        return null;
    }

    public ZonedDateTime plusWeeks(final long weeks) {
        return null;
    }

    public ZonedDateTime plusDays(final long days) {
        return null;
    }

    public ZonedDateTime plusHours(final long hours) {
        return null;
    }

    public ZonedDateTime plusMinutes(final long minutes) {
        return null;
    }

    public ZonedDateTime plusSeconds(final long seconds) {
        return null;
    }

    public ZonedDateTime plusNanos(final long nanos) {
        return null;
    }

    public ZonedDateTime minus(final TemporalAmount amountToSubtract) {
        return null;
    }

    public ZonedDateTime minus(final long amountToSubtract, final TemporalUnit unit) {
        return null;
    }

    public ZonedDateTime minusYears(final long years) {
        return null;
    }

    public ZonedDateTime minusMonths(final long months) {
        return null;
    }

    public ZonedDateTime minusWeeks(final long weeks) {
        return null;
    }

    public ZonedDateTime minusDays(final long days) {
        return null;
    }

    public ZonedDateTime minusHours(final long hours) {
        return null;
    }

    public ZonedDateTime minusMinutes(final long minutes) {
        return null;
    }

    public ZonedDateTime minusSeconds(final long seconds) {
        return null;
    }

    public ZonedDateTime minusNanos(final long nanos) {
        return null;
    }

    public <R> R query(final TemporalQuery<R> query) {
        return null;
    }

    public long until(final Temporal endExclusive, final TemporalUnit unit) {
        return 0L;
    }

    public String format(final DateTimeFormatter formatter) {
        return null;
    }

    public OffsetDateTime toOffsetDateTime() {
        return null;
    }

    public boolean equals(final ZonedDateTime obj) {
        return true;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return null;
    }

    public Chronology getChronology() {
        return null;
    }

    public Instant toInstant() {
        return null;
    }

    public long toEpochSecond() {
        return 0;
    }

    public int compareTo(final ChronoZonedDateTime<?> other) {
        return 0;
    }

    public boolean isBefore(final ChronoZonedDateTime<?> other) {
        return false;
    }

    public boolean isAfter(final ChronoZonedDateTime<?> other) {
        return false;
    }

    public boolean isEqual(final ChronoZonedDateTime<?> other) {
        return false;
    }

    public static ZonedDateTime now() {
        return null;
    }

    public static ZonedDateTime now(ZoneId zone) {
        return null;
    }

    public static ZonedDateTime now(Clock clock) {
        return null;
    }

    public static ZonedDateTime of(LocalDate date, LocalTime time, ZoneId zone) {
        return null;
    }

    public static ZonedDateTime of(LocalDateTime localDateTime, ZoneId zone) {
        return null;
    }

    public static ZonedDateTime of(
            int year, int month, int dayOfMonth,
            int hour, int minute, int second, int nanoOfSecond, ZoneId zone) {
        return null;
    }

    public static ZonedDateTime ofLocal(LocalDateTime localDateTime, ZoneId zone, ZoneOffset preferredOffset) {
        return null;
    }

    public static ZonedDateTime ofInstant(Instant instant, ZoneId zone) {
        return null;
    }

    public static ZonedDateTime ofInstant(LocalDateTime localDateTime, ZoneOffset offset, ZoneId zone) {
        return null;
    }

    public static ZonedDateTime ofStrict(LocalDateTime localDateTime, ZoneOffset offset, ZoneId zone) {
        return null;
    }

    public static ZonedDateTime from(TemporalAccessor temporal) {
        return null;
    }

    public static ZonedDateTime parse(CharSequence text) {
        return null;
    }

    public static ZonedDateTime parse(CharSequence text, DateTimeFormatter formatter) {
        return null;
    }
}
