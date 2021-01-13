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

import java.io.IOException;
import java.io.Serializable;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.IsoChronology;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.List;

public final class Period
        implements ChronoPeriod,
                   Serializable {

    public static final Period ZERO = new Period(0, 0, 0);

    private Period(int years, int months, int days) {

    }

    public long get(final TemporalUnit unit) {
        return 0L;
    }

    public List<TemporalUnit> getUnits() {
        return null;
    }

    public IsoChronology getChronology() {
        return null;
    }

    public boolean isZero() {
        return true;
    }

    public boolean isNegative() {
        return true;
    }

    public int getYears() {
        return 0;
    }

    public int getMonths() {
        return 0;
    }

    public int getDays() {
        return 0;
    }

    public Period withYears(final int years) {
        return null;
    }

    public Period withMonths(final int months) {
        return null;
    }

    public Period withDays(final int days) {
        return null;
    }

    public Period plus(final TemporalAmount amountToAdd) {
        return null;
    }

    public Period plusYears(final long yearsToAdd) {
        return null;
    }

    public Period plusMonths(final long monthsToAdd) {
        return null;
    }

    public Period plusDays(final long daysToAdd) {
        return null;
    }

    public Period minus(final TemporalAmount amountToSubtract) {
        return null;
    }

    public Period minusYears(final long yearsToSubtract) {
        return null;
    }

    public Period minusMonths(final long monthsToSubtract) {
        return null;
    }

    public Period minusDays(final long daysToSubtract) {
        return null;
    }

    public Period multipliedBy(final int scalar) {
        return null;
    }

    public Period negated() {
        return null;
    }

    public Period normalized() {
        return null;
    }

    public long toTotalMonths() {
        return 0L;
    }

    public Temporal addTo(final Temporal temporal) {
        return null;
    }

    public Temporal subtractFrom(final Temporal temporal) {
        return null;
    }

    public boolean equals(final Period obj) {
        return true;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return null;
    }

    public static Period ofYears(int years) {
        return null;
    }

    public static Period ofMonths(int months) {
        return null;
    }

    public static Period ofWeeks(int weeks) {
        return null;
    }

    public static Period ofDays(int days) {
        return null;
    }

    public static Period of(int years, int months, int days) {
        return null;
    }

    public static Period from(TemporalAmount amount) {
        return null;
    }

    public static Period parse(CharSequence text) {
        return null;
    }

    public static Period between(LocalDate startDateInclusive, LocalDate endDateExclusive) {
        return null;
    }

    static Period readExternal(Object in) throws IOException {
        return null;
    }
}
