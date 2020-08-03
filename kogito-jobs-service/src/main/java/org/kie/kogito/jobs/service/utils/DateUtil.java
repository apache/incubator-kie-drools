/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.jobs.service.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

public class DateUtil {

    public static final ZoneId DEFAULT_ZONE = ZoneId.of("UTC");

    private DateUtil() {

    }

    public static ZonedDateTime now() {
        return ZonedDateTime.now(DEFAULT_ZONE).truncatedTo(ChronoUnit.MILLIS);
    }

    public static Instant zonedDateTimeToInstant(ZonedDateTime dateTime) {
        return Optional.ofNullable(dateTime)
                .map(t -> t.withZoneSameLocal(DEFAULT_ZONE))
                .map(t -> t.truncatedTo(ChronoUnit.MILLIS))
                .map(ZonedDateTime::toInstant)
                .orElse(null);
    }

    public static ZonedDateTime instantToZonedDateTime(Instant instant) {
        return Optional.ofNullable(instant)
                .map(i -> ZonedDateTime.ofInstant(i, DateUtil.DEFAULT_ZONE))
                .map(i -> i.truncatedTo(ChronoUnit.MILLIS))
                .orElse(null);
    }

    public static ZonedDateTime fromDate(Date date){
        return ZonedDateTime.ofInstant(date.toInstant(), DEFAULT_ZONE);
    }

    public static Date toDate(ZonedDateTime zonedDateTime){
        return new Date(zonedDateTime.toInstant().toEpochMilli());
    }
}
