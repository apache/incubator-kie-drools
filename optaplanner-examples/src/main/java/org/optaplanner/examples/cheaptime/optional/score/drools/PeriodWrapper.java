/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.cheaptime.optional.score.drools;

import java.util.Comparator;
import java.util.Objects;

public class PeriodWrapper {

    private static final Comparator<PeriodWrapper> COMPARATOR = Comparator.comparingInt(PeriodWrapper::getPeriod);

    private int period;

    public PeriodWrapper(int period) {
        this.period = period;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PeriodWrapper other = (PeriodWrapper) o;
        return period == other.period;
    }

    @Override
    public int hashCode() {
        return Objects.hash(period);
    }

    public int compareTo(PeriodWrapper other) {
        return COMPARATOR.compare(this, other);
    }

    @Override
    public String toString() {
        return String.valueOf(period);
    }

}
