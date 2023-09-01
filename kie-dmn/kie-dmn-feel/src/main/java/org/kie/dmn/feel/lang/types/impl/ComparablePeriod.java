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
package org.kie.dmn.feel.lang.types.impl;

import java.io.Serializable;
import java.time.Period;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.Chronology;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.List;

import org.kie.dmn.feel.util.TypeUtil;

public class ComparablePeriod implements Comparable<ChronoPeriod>, ChronoPeriod, Serializable {
    private final int left;
    private final String toStringRep;
    private final Period period;

    public ComparablePeriod(Period value) {
        this.period = value;
        this.left = value.getYears() * 12 + value.getMonths();
        this.toStringRep = TypeUtil.formatPeriod(value, true);
    }

    public ComparablePeriod(ChronoPeriod value) {
        this.period = Period.from(value);
        this.left = (int) toTotalMonths(value);
        this.toStringRep = TypeUtil.formatPeriod(value, true);
    }

    public static ComparablePeriod parse(CharSequence text) {
        return new ComparablePeriod(Period.parse(text));
    }

    @Override
    public int compareTo(ChronoPeriod o) {
        int right = (int) (o.get(ChronoUnit.YEARS) * 12L + o.get(ChronoUnit.MONTHS));
        return left - right;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( !(o instanceof ComparablePeriod) ) return false;

        ComparablePeriod that = (ComparablePeriod) o;

        return left == that.left;
    }

    @Override
    public int hashCode() {
        return left;
    }

    @Override
    public String toString() {
        return toStringRep;
    }

    public Period asPeriod() {
        return period;
    }

    public static ComparablePeriod ofMonths(int months) {
        Period p = Period.ofMonths(months);
        return new ComparablePeriod(p);
    }

    public static long toTotalMonths(ChronoPeriod left) {
        return left.get(ChronoUnit.YEARS) * 12L + left.get(ChronoUnit.MONTHS);
    }

    @Override
    public long get(TemporalUnit unit) {
        return period.get(unit);
    }

    @Override
    public List<TemporalUnit> getUnits() {
        return period.getUnits();
    }

    @Override
    public Chronology getChronology() {
        return period.getChronology();
    }

    @Override
    public ChronoPeriod plus(TemporalAmount amountToAdd) {
        return new ComparablePeriod(period.plus(amountToAdd));
    }

    @Override
    public ChronoPeriod minus(TemporalAmount amountToSubtract) {
        return new ComparablePeriod(period.minus(amountToSubtract));
    }

    @Override
    public ChronoPeriod multipliedBy(int scalar) {
        return new ComparablePeriod(period.multipliedBy(scalar));
    }

    @Override
    public ChronoPeriod normalized() {
        return new ComparablePeriod(period.normalized());
    }

    @Override
    public Temporal addTo(Temporal temporal) {
        return period.addTo(temporal);
    }

    @Override
    public Temporal subtractFrom(Temporal temporal) {
        return period.subtractFrom(temporal);
    }

    public static ComparablePeriod of(int years, int months, int days) {
        Period p = Period.of(years, months, days);
        return new ComparablePeriod(p);
    }

}