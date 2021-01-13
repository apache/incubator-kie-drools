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
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;

public final class OffsetTime
        implements Temporal,
                   TemporalAdjuster,
                   Comparable<OffsetTime>,
                   Serializable {

    public static final OffsetTime MIN = LocalTime.MIN.atOffset(ZoneOffset.MAX);

    public static final OffsetTime MAX = LocalTime.MAX.atOffset(ZoneOffset.MIN);

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

    public OffsetTime withOffsetSameLocal(final ZoneOffset offset) {
        return null;
    }

    public OffsetTime withOffsetSameInstant(final ZoneOffset offset) {
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

    public OffsetTime with(final TemporalAdjuster adjuster) {
        return null;
    }

    public OffsetTime with(final TemporalField field, final long newValue) {
        return null;
    }

    public OffsetTime withHour(final int hour) {
        return null;
    }

    public OffsetTime withMinute(final int minute) {
        return null;
    }

    public OffsetTime withSecond(final int second) {
        return null;
    }

    public OffsetTime withNano(final int nanoOfSecond) {
        return null;
    }

    public OffsetTime truncatedTo(final TemporalUnit unit) {
        return null;
    }

    public OffsetTime plus(final TemporalAmount amountToAdd) {
        return null;
    }

    public OffsetTime plus(final long amountToAdd, final TemporalUnit unit) {
        return null;
    }

    public OffsetTime plusHours(final long hours) {
        return null;
    }

    public OffsetTime plusMinutes(final long minutes) {
        return null;
    }

    public OffsetTime plusSeconds(final long seconds) {
        return null;
    }

    public OffsetTime plusNanos(final long nanos) {
        return null;
    }

    public OffsetTime minus(final TemporalAmount amountToSubtract) {
        return null;
    }

    public OffsetTime minus(final long amountToSubtract, final TemporalUnit unit) {
        return null;
    }

    public OffsetTime minusHours(final long hours) {
        return null;
    }

    public OffsetTime minusMinutes(final long minutes) {
        return null;
    }

    public OffsetTime minusSeconds(final long seconds) {
        return null;
    }

    public OffsetTime minusNanos(final long nanos) {
        return null;
    }

    public <R> R query(final TemporalQuery<R> query) {
        return null;
    }

    public Temporal adjustInto(final Temporal temporal) {
        return null;
    }

    public long until(final Temporal endExclusive, final TemporalUnit unit) {
        return 0L;
    }

    public String format(final DateTimeFormatter formatter) {
        return null;
    }

    public OffsetDateTime atDate(final LocalDate date) {
        return null;
    }

    public int compareTo(final OffsetTime other) {
        return 0;
    }

    public boolean isAfter(final OffsetTime other) {
        return true;
    }

    public boolean isBefore(final OffsetTime other) {
        return true;
    }

    public boolean isEqual(final OffsetTime other) {
        return true;
    }

    public boolean equals(final OffsetTime obj) {
        return true;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return null;
    }

    public static OffsetTime now() {
        return null;
    }

    public static OffsetTime now(ZoneId zone) {
        return null;
    }

    public static OffsetTime now(Clock clock) {
        return null;
    }

    public static OffsetTime of(LocalTime time, ZoneOffset offset) {
        return null;
    }

    public static OffsetTime of(int hour, int minute, int second, int nanoOfSecond, ZoneOffset offset) {
        return null;
    }

    public static OffsetTime ofInstant(Instant instant, ZoneId zone) {
        return null;
    }

    public static OffsetTime from(TemporalAccessor temporal) {
        return null;
    }

    public static OffsetTime parse(CharSequence text) {
        return null;
    }

    public static OffsetTime parse(CharSequence text, DateTimeFormatter formatter) {
        return null;
    }
}
