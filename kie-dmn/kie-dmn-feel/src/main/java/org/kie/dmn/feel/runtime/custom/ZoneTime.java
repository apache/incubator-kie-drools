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
package org.kie.dmn.feel.runtime.custom;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.time.temporal.ValueRange;
import java.util.Objects;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;

/**
 * This class is meant as sort-of <b>decorator</b> over <code>OffsetTime</code>, that is a final class.
 * It is used to provide both time and zoneid information, replacing the <code>Parsed</code> instance that would be
 * returned otherwise by
 * {@link  org.kie.dmn.feel.runtime.functions.TimeFunction#invoke(String)}
 */
public final class ZoneTime
        implements Temporal,
                   TemporalAdjuster,
                   Comparable<ZoneTime>,
                   Serializable {

    private final OffsetTime offsetTime;
    private final ZoneId zoneId;
    private final String stringRepresentation;
    private final boolean hasSeconds;

    public static final DateTimeFormatter ZONED_OFFSET_WITH_SECONDS;
    public static final DateTimeFormatter ZONED_OFFSET_WITHOUT_SECONDS;

    static {
        ZONED_OFFSET_WITHOUT_SECONDS = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendValue(HOUR_OF_DAY, 2)
                .appendLiteral(':')
                .appendValue(MINUTE_OF_HOUR, 2)
                .appendLiteral("@")
                .appendZoneRegionId()
                .toFormatter()
                .withResolverStyle(ResolverStyle.STRICT);

        ZONED_OFFSET_WITH_SECONDS = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendValue(HOUR_OF_DAY, 2)
                .appendLiteral(':')
                .appendValue(MINUTE_OF_HOUR, 2)
                .appendLiteral(':')
                .appendValue(SECOND_OF_MINUTE, 2)
                .optionalStart()
                .appendFraction(NANO_OF_SECOND, 0, 9, true)
                .optionalStart()
                .appendLiteral("@")
                .appendZoneRegionId()
                .optionalEnd()
                .toFormatter()
                .withResolverStyle(ResolverStyle.STRICT);
    }

    public static ZoneTime of(LocalTime localTime, ZoneId zoneId, boolean hasSeconds) {
        return new ZoneTime(localTime, zoneId, hasSeconds);
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    public String getTimezone() {
        return zoneId.getId();
    }

    private ZoneTime(LocalTime localTime, ZoneId zoneId, boolean hasSeconds) {
        ZoneOffset offset = zoneId.getRules().getOffset(LocalDateTime.now());
        this.offsetTime = OffsetTime.of(localTime, offset);
        this.zoneId = zoneId;
        this.hasSeconds = hasSeconds;
        this.stringRepresentation = String.format("%s@%s", localTime, zoneId);
    }

    // package default for testing purpose
    ZoneTime(OffsetTime offsetTime, ZoneId zoneId, boolean hasSeconds) {
        this.offsetTime = offsetTime;
        this.zoneId = zoneId;
        this.hasSeconds = hasSeconds;
        String offsetString = offsetTime.toString().replace(offsetTime.getOffset().toString(), "");
        this.stringRepresentation = String.format("%s@%s", offsetString, zoneId);
    }

    @Override
    public int compareTo(ZoneTime o) {
        return offsetTime.compareTo(o.offsetTime);
    }

    @Override
    public Temporal with(TemporalField field, long newValue) {
        return getNewZoneOffset(offsetTime.with(field, newValue));
    }

    @Override
    public Temporal with(TemporalAdjuster adjuster) {
        return getNewZoneOffset(offsetTime.with(adjuster));
    }

    @Override
    public Temporal plus(long amountToAdd, TemporalUnit unit) {
        return getNewZoneOffset(offsetTime.plus(amountToAdd, unit));
    }

    @Override
    public Temporal plus(TemporalAmount amount) {
        return getNewZoneOffset(offsetTime.plus(amount));
    }

    @Override
    public Temporal minus(long amountToSubtract, TemporalUnit unit) {
        return
                getNewZoneOffset(offsetTime.minus(amountToSubtract, unit));
    }

    @Override
    public Temporal minus(TemporalAmount amount) {
        return
                getNewZoneOffset(offsetTime.minus(amount));
    }

    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        return offsetTime.until(endExclusive, unit);
    }

    @Override
    public boolean isSupported(TemporalUnit unit) {
        return offsetTime.isSupported(unit);
    }

    @Override
    public boolean isSupported(TemporalField field) {
        return offsetTime.isSupported(field);
    }

    @Override
    public long getLong(TemporalField field) {
        return offsetTime.getLong(field);
    }

    @Override
    public Temporal adjustInto(Temporal temporal) {
        return offsetTime.adjustInto(temporal);
    }

    @Override
    public <R> R query(TemporalQuery<R> query) {
        if (query == TemporalQueries.zoneId() || query == TemporalQueries.zone()) {
            return (R) zoneId;
        } else if (query.toString().contains(DateTimeFormatterBuilder.class.getCanonicalName())) {
            return (R) zoneId;
        } else {
            return offsetTime.query(query);
        }
    }

    @Override
    public ValueRange range(TemporalField field) {
        return offsetTime.range(field);
    }

    @Override
    public int get(TemporalField field) {
        return offsetTime.get(field);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ZoneTime that)) {
            return false;
        }
        return Objects.equals(offsetTime, that.offsetTime) && Objects.equals(zoneId, that.zoneId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offsetTime, zoneId);
    }

    @Override
    public String toString() {
        return stringRepresentation;
    }

    public String format() {
        return hasSeconds ? ZONED_OFFSET_WITH_SECONDS.format(this) : ZONED_OFFSET_WITHOUT_SECONDS.format(this);
    }

    // Package access for testing purpose
    OffsetTime getOffsetTime() {
        return offsetTime;
    }

    // Package access for testing purpose
    ZoneTime getNewZoneOffset(OffsetTime offset) {
        return new ZoneTime(offset, zoneId, hasSeconds);
    }

}
