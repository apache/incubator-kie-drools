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
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.List;

final class ChronoPeriodImpl
        implements ChronoPeriod,
                   Serializable {

    ChronoPeriodImpl(Chronology chrono, int years, int months, int days) {
    }

    private ChronoPeriodImpl validateAmount(TemporalAmount amount) {
        return null;
    }

    private long monthRange() {
        return 0L;
    }

    private void validateChrono(TemporalAccessor temporal) {
    }

    protected Object writeReplace() {
        return null;
    }

    public boolean equals(Object obj) {
        return true;
    }

    public boolean isNegative() {
        return true;
    }

    public boolean isZero() {
        return true;
    }

    public Chronology getChronology() {
        return null;
    }

    public ChronoPeriod minus(TemporalAmount amountToSubtract) {
        return null;
    }

    public ChronoPeriod multipliedBy(int scalar) {
        return null;
    }

    public ChronoPeriod normalized() {
        return null;
    }

    public ChronoPeriod plus(TemporalAmount amountToAdd) {
        return null;
    }

    public int hashCode() {
        return 0;
    }

    public List<TemporalUnit> getUnits() {
        return null;
    }

    public long get(TemporalUnit unit) {
        return 0L;
    }

    public String toString() {
        return null;
    }

    public Temporal addTo(Temporal temporal) {
        return null;
    }

    public Temporal subtractFrom(Temporal temporal) {
        return null;
    }
}
