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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.chrono.Chronology;

import static java.time.temporal.ChronoField.EPOCH_DAY;
import static java.time.temporal.ChronoField.NANO_OF_DAY;
import static java.time.temporal.ChronoField.OFFSET_SECONDS;

public final class TemporalQueries {

    private TemporalQueries() {
    }

    public static TemporalQuery<ZoneId> zoneId() {
        return TemporalQueries.ZONE_ID;
    }

    public static TemporalQuery<Chronology> chronology() {
        return TemporalQueries.CHRONO;
    }

    public static TemporalQuery<TemporalUnit> precision() {
        return TemporalQueries.PRECISION;
    }

    public static TemporalQuery<ZoneId> zone() {
        return TemporalQueries.ZONE;
    }

    public static TemporalQuery<ZoneOffset> offset() {
        return TemporalQueries.OFFSET;
    }

    public static TemporalQuery<LocalDate> localDate() {
        return TemporalQueries.LOCAL_DATE;
    }

    public static TemporalQuery<LocalTime> localTime() {
        return TemporalQueries.LOCAL_TIME;
    }

    static final TemporalQuery<ZoneId> ZONE_ID = (temporal) ->
            temporal.query(TemporalQueries.ZONE_ID);

    static final TemporalQuery<Chronology> CHRONO = (temporal) ->
            temporal.query(TemporalQueries.CHRONO);

    static final TemporalQuery<TemporalUnit> PRECISION = (temporal) ->
            temporal.query(TemporalQueries.PRECISION);

    static final TemporalQuery<ZoneOffset> OFFSET = (temporal) -> {
        if (temporal.isSupported(OFFSET_SECONDS)) {
            return ZoneOffset.ofTotalSeconds(temporal.get(OFFSET_SECONDS));
        }
        return null;
    };

    static final TemporalQuery<ZoneId> ZONE = (temporal) -> {
        ZoneId zone = temporal.query(ZONE_ID);
        return (zone != null ? zone : temporal.query(OFFSET));
    };

    static final TemporalQuery<LocalDate> LOCAL_DATE = (temporal) -> {
        if (temporal.isSupported(EPOCH_DAY)) {
            return LocalDate.ofEpochDay(temporal.getLong(EPOCH_DAY));
        }
        return null;
    };

    static final TemporalQuery<LocalTime> LOCAL_TIME = (temporal) -> {
        if (temporal.isSupported(NANO_OF_DAY)) {
            return LocalTime.ofNanoOfDay(temporal.getLong(NANO_OF_DAY));
        }
        return null;
    };
}
