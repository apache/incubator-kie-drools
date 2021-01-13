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

import java.time.Duration;

public enum ChronoUnit implements TemporalUnit {

    NANOS("Nanos", null),
    MICROS("Micros", null),
    MILLIS("Millis", null),
    SECONDS("Seconds", null),
    MINUTES("Minutes", null),
    HOURS("Hours", null),
    HALF_DAYS("HalfDays", null),
    DAYS("Days", null),
    WEEKS("Weeks", null),
    MONTHS("Months", null),
    YEARS("Years", null),
    DECADES("Decades", null),
    CENTURIES("Centuries", null),
    MILLENNIA("Millennia", null),
    ERAS("Eras", null),
    FOREVER("Forever", null);

    private ChronoUnit(String name, Duration estimatedDuration) {
    }

    public Duration getDuration() {
        return null;
    }

    public boolean isDurationEstimated() {
        return true;
    }

    public boolean isDateBased() {
        return true;
    }

    public boolean isTimeBased() {
        return true;
    }

    public boolean isSupportedBy(Temporal temporal) {
        return true;
    }

    public <R extends Temporal> R addTo(R temporal, long amount) {
        return null;
    }

    public long between(Temporal temporal1Inclusive, Temporal temporal2Exclusive) {
        return 0;
    }

    public String toString() {
        return "";
    }
}
