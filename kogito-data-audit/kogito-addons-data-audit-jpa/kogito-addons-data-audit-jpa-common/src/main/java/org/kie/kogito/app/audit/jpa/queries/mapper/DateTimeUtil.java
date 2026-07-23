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
package org.kie.kogito.app.audit.jpa.queries.mapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;

public final class DateTimeUtil {

    private DateTimeUtil() {
    }

    /**
     * Converts various date/time types to OffsetDateTime.
     * Hibernate 7 returns OffsetDateTime instead of Date for native queries,
     * so this utility handles multiple input types for compatibility.
     */
    public static OffsetDateTime toDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof OffsetDateTime) {
            return (OffsetDateTime) value;
        }
        if (value instanceof Date) {
            return OffsetDateTime.ofInstant(((Date) value).toInstant(), ZoneId.of("UTC"));
        }
        if (value instanceof Instant) {
            return OffsetDateTime.ofInstant((Instant) value, ZoneId.of("UTC"));
        }
        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).atZone(ZoneId.of("UTC")).toOffsetDateTime();
        }
        throw new IllegalArgumentException("Cannot convert " + value.getClass() + " to OffsetDateTime");
    }
}
