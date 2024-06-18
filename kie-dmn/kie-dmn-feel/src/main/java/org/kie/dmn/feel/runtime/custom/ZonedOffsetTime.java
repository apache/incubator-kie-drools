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
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalQueries;
import java.time.temporal.TemporalQuery;
import java.time.temporal.TemporalUnit;
import java.util.Objects;

/**
 * This class is meant as sort-of <b>decorator</b> over <code>OffsetTime</code>, that is a final class.
 * It is used to provide both offset and zoneid information, replacing the <code>Parsed</code> instance that would be returned otherwise by
 * {@link  org.kie.dmn.feel.runtime.functions.TimeFunction#invoke(String)}
 */
public final class ZonedOffsetTime
        implements Temporal,
                   TemporalAdjuster,
                   Comparable<ZonedOffsetTime>,
                   Serializable {

    private final OffsetTime offset;
    private final ZoneId zoneId;

    public static ZonedOffsetTime of(LocalTime localTime, ZoneId zoneId) {
        return new ZonedOffsetTime(localTime, zoneId);
    }

    public OffsetTime getOffset() {
        return offset;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    public String getTimezone() {
        return zoneId.getId();
    }



    private ZonedOffsetTime(LocalTime localTime, ZoneId zoneId) {
        ZoneOffset offset = zoneId.getRules().getOffset(LocalDateTime.now());
        this.offset = OffsetTime.of(localTime, offset);
        this.zoneId = zoneId;
    }

    @Override
    public int compareTo(ZonedOffsetTime o) {
        return offset.compareTo(offset);
    }

    @Override
    public boolean isSupported(TemporalUnit unit) {
        return offset.isSupported(unit);
    }

    @Override
    public Temporal with(TemporalField field, long newValue) {
        return offset.with(field, newValue);
    }

    @Override
    public Temporal plus(long amountToAdd, TemporalUnit unit) {
        return offset.plus(amountToAdd, unit);
    }

    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        return offset.until(endExclusive, unit);
    }

    @Override
    public boolean isSupported(TemporalField field) {
        return offset.isSupported(field);
    }

    @Override
    public long getLong(TemporalField field) {
        return offset.getLong(field);
    }

    @Override
    public Temporal adjustInto(Temporal temporal) {
        return offset.adjustInto(temporal);
    }

    @Override
    public <R> R query(TemporalQuery<R> query) {
        if (query == TemporalQueries.zoneId() || query == TemporalQueries.zone()) {
            return (R) zoneId;
        } else {
            return offset.query(query);
        }
    }

    @Override
    public String toString() {
        return offset.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ZonedOffsetTime that)) {
            return false;
        }
        return Objects.equals(offset, that.offset) && Objects.equals(zoneId, that.zoneId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offset, zoneId);
    }
}
