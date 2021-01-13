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
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.List;

public final class Duration
        implements TemporalAmount,
                   Comparable<Duration>,
                   Serializable {

    private Duration(long seconds, int nanos) {

    }

    public long get(final TemporalUnit unit) {
        return 0L;
    }

    public List<TemporalUnit> getUnits() {
        return null;
    }

    public boolean isZero() {
        return true;
    }

    public boolean isNegative() {
        return true;
    }

    public long getSeconds() {
        return 0L;
    }

    public int getNano() {
        return 0;
    }

    public Duration withSeconds(final long seconds) {
        return null;
    }

    public Duration withNanos(final int nanoOfSecond) {
        return null;
    }

    public Duration plus(final Duration duration) {
        return null;
    }

    public Duration plus(final long amountToAdd, final TemporalUnit unit) {
        return null;
    }

    public Duration plusDays(final long daysToAdd) {
        return null;
    }

    public Duration plusHours(final long hoursToAdd) {
        return null;
    }

    public Duration plusMinutes(final long minutesToAdd) {
        return null;
    }

    public Duration plusSeconds(final long secondsToAdd) {
        return null;
    }

    public Duration plusMillis(final long millisToAdd) {
        return null;
    }

    public Duration plusNanos(final long nanosToAdd) {
        return null;
    }

    public Duration minus(final Duration duration) {
        return null;
    }

    public Duration minus(final long amountToSubtract, final TemporalUnit unit) {
        return null;
    }

    public Duration minusDays(final long daysToSubtract) {
        return null;
    }

    public Duration minusHours(final long hoursToSubtract) {
        return null;
    }

    public Duration minusMinutes(final long minutesToSubtract) {
        return null;
    }

    public Duration minusSeconds(final long secondsToSubtract) {
        return null;
    }

    public Duration minusMillis(final long millisToSubtract) {
        return null;
    }

    public Duration minusNanos(final long nanosToSubtract) {
        return null;
    }

    public Duration multipliedBy(final long multiplicand) {
        return null;
    }

    public Duration dividedBy(final long divisor) {
        return null;
    }

    public Duration negated() {
        return null;
    }

    public Duration abs() {
        return null;
    }

    public Temporal addTo(final Temporal temporal) {
        return null;
    }

    public Temporal subtractFrom(final Temporal temporal) {
        return null;
    }

    public long toDays() {
        return 0L;
    }

    public long toHours() {
        return 0L;
    }

    public long toMinutes() {
        return 0L;
    }

    public long toMillis() {
        return 0L;
    }

    public long toNanos() {
        return 0L;
    }

    public int compareTo(final Duration otherDuration) {
        return 0;
    }

    public boolean equals(final Duration otherDuration) {
        return true;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return null;
    }

    public static final Duration ZERO = new Duration(0, 0);

    public static Duration ofDays(long days) {
        return null;
    }

    public static Duration ofHours(long hours) {
        return null;
    }

    public static Duration ofMinutes(long minutes) {
        return null;
    }

    public static Duration ofSeconds(long seconds) {
        return null;
    }

    public static Duration ofSeconds(long seconds, long nanoAdjustment) {
        return null;
    }

    public static Duration ofMillis(long millis) {
        return null;
    }

    public static Duration ofNanos(long nanos) {
        return null;
    }

    public static Duration of(long amount, TemporalUnit unit) {
        return null;
    }

    public static Duration from(TemporalAmount amount) {
        return null;
    }

    public static Duration parse(CharSequence text) {
        return null;
    }

    public static Duration between(Temporal startInclusive, Temporal endExclusive) {
        return null;
    }
}
