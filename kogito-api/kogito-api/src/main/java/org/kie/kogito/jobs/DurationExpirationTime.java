/*
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
package org.kie.kogito.jobs;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Objects;

public class DurationExpirationTime implements ExpirationTime {

    private final ZonedDateTime expirationTime;
    private Long repeatInterval;
    private Integer repeatLimit;

    private DurationExpirationTime(ZonedDateTime expirationTime, Long repeatInterval, Integer repeatLimit) {
        this.expirationTime = Objects.requireNonNull(expirationTime);
        this.repeatInterval = repeatInterval;
        this.repeatLimit = repeatLimit;
    }

    @Override
    public ZonedDateTime get() {
        return expirationTime;
    }

    @Override
    public Long repeatInterval() {
        return repeatInterval;
    }

    @Override
    public Integer repeatLimit() {
        return repeatLimit;
    }

    public static DurationExpirationTime now() {
        return new DurationExpirationTime(ZonedDateTime.now(), null, 0);
    }

    public static DurationExpirationTime after(long delay) {
        return after(delay, ChronoUnit.MILLIS);
    }

    public static DurationExpirationTime after(long delay, TemporalUnit unit) {
        return new DurationExpirationTime(ZonedDateTime.now().plus(delay, unit), null, 0);
    }

    public static DurationExpirationTime repeat(long delay) {
        return repeat(delay, null, ChronoUnit.MILLIS);
    }

    public static DurationExpirationTime repeat(long delay, Long repeatInterval) {
        return repeat(delay, repeatInterval, ChronoUnit.MILLIS);
    }

    public static DurationExpirationTime repeat(long delay, Long repeatInterval, Integer repeatLimit) {
        return repeat(delay, repeatInterval, repeatLimit, ChronoUnit.MILLIS);
    }

    public static DurationExpirationTime repeat(long delay, Long repeatInterval, TemporalUnit unit) {
        return new DurationExpirationTime(ZonedDateTime.now().plus(delay, unit), repeatInterval, 0);
    }

    public static DurationExpirationTime repeat(long delay, Long repeatInterval, Integer limit, TemporalUnit unit) {
        return new DurationExpirationTime(ZonedDateTime.now().plus(delay, unit), repeatInterval, limit);
    }
}
