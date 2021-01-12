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

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;

public final class Instant
        implements Temporal,
                   TemporalAdjuster,
                   Comparable<Instant>,
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

    public long getEpochSecond() {
        return 0L;
    }

    public int getNano() {
        return 0;
    }

    public Instant with(final TemporalAdjuster adjuster) {
        return null;
    }

    public Instant with(final TemporalField field, final long newValue) {
        return null;
    }

    public Instant truncatedTo(final TemporalUnit unit) {
        return null;
    }

    public Instant plus(final TemporalAmount amountToAdd) {
        return null;
    }

    public Instant plus(final long amountToAdd, final TemporalUnit unit) {
        return null;
    }

    public Instant plusSeconds(final long secondsToAdd) {
        return null;
    }

    public Instant plusMillis(final long millisToAdd) {
        return null;
    }

    public Instant plusNanos(final long nanosToAdd) {
        return null;
    }

    public Instant minus(final TemporalAmount amountToSubtract) {
        return null;
    }

    public Instant minus(final long amountToSubtract, final TemporalUnit unit) {
        return null;
    }

    public Instant minusSeconds(final long secondsToSubtract) {
        return null;
    }

    public Instant minusMillis(final long millisToSubtract) {
        return null;
    }

    public Instant minusNanos(final long nanosToSubtract) {
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

    public OffsetDateTime atOffset(final ZoneOffset offset) {
        return null;
    }

    public ZonedDateTime atZone(final ZoneId zone) {
        return null;
    }

    public long toEpochMilli() {
        return 0L;
    }

    public int compareTo(final Instant otherInstant) {
        return 0;
    }

    public boolean isAfter(final Instant otherInstant) {
        return true;
    }

    public boolean isBefore(final Instant otherInstant) {
        return true;
    }

    public boolean equals(final Instant otherInstant) {
        return true;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return null;
    }
}
