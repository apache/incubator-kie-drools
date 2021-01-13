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
package java.time.temporal;

import java.io.Serializable;

import java.time.DayOfWeek;

public final class WeekFields implements Serializable {

    public DayOfWeek getFirstDayOfWeek() {
        return null;
    }

    public int getMinimalDaysInFirstWeek() {
        return 0;
    }

    public TemporalField dayOfWeek() {
        return null;
    }

    public TemporalField weekOfMonth() {
        return null;
    }

    public TemporalField weekOfYear() {
        return null;
    }

    public TemporalField weekOfWeekBasedYear() {
        return null;
    }

    public TemporalField weekBasedYear() {
        return null;
    }

    public boolean equals(final WeekFields object) {
        return true;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return null;
    }
}
