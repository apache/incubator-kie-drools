/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public final class DateTimeUtils {

    private DateTimeUtils() {
    }

    public static String formatDateTime(Date time) {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(time.toInstant().atZone(ZoneOffset.UTC));
    }

    public static String formatOffsetDateTime(OffsetDateTime time) {
        return time.truncatedTo(ChronoUnit.MILLIS).atZoneSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public static String formatZonedDateTime(ZonedDateTime time) {
        return time.truncatedTo(ChronoUnit.MILLIS).withZoneSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public static ZonedDateTime toZonedDateTime(Date date) {
        return date == null ? null : ZonedDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
    }

    public static ZonedDateTime toZonedDateTime(OffsetDateTime date) {
        return date == null ? null : date.atZoneSameInstant(ZoneOffset.UTC);
    }

    public static ZonedDateTime parseZonedDateTime(String s) {
        try {
            return ZonedDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (DateTimeParseException exception) {
            return LocalDateTime.parse(s, DateTimeFormatter.ISO_DATE_TIME).atZone(ZoneOffset.UTC);
        }
    }
}
