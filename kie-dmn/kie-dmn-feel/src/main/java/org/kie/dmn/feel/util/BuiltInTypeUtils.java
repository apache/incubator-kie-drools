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
package org.kie.dmn.feel.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.List;
import java.util.Map;

import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.custom.ZoneTime;

public class BuiltInTypeUtils {

    private BuiltInTypeUtils() {
        // Utility class, no instantiation allowed
    }

    public static Type determineTypeFromName(String name) {
        if (name == null) {
            return BuiltInType.UNKNOWN;
        }
        for (BuiltInType t : BuiltInType.values()) {
            for (String n : t.getNames()) {
                if (n.equals(name)) {
                    return t;
                }
            }
        }
        return BuiltInType.UNKNOWN;
    }

    public static Type determineTypeFromInstance(Object o) {
        if (o == null) {
            return BuiltInType.UNKNOWN;
        } else if (o instanceof Number) {
            return BuiltInType.NUMBER;
        } else if (o instanceof String) {
            return BuiltInType.STRING;
        } else if (o instanceof LocalDate) {
            return BuiltInType.DATE;
        } else if (o instanceof LocalTime || o instanceof OffsetTime || o instanceof ZoneTime) {
            return BuiltInType.TIME;
        } else if (o instanceof ZonedDateTime || o instanceof OffsetDateTime || o instanceof LocalDateTime) {
            return BuiltInType.DATE_TIME;
        } else if (o instanceof Duration || o instanceof ChronoPeriod) {
            return BuiltInType.DURATION;
        } else if (o instanceof Boolean) {
            return BuiltInType.BOOLEAN;
        } else if (o instanceof UnaryTest) {
            return BuiltInType.UNARY_TEST;
        } else if (o instanceof Range) {
            return BuiltInType.RANGE;
        } else if (o instanceof FEELFunction) {
            return BuiltInType.FUNCTION;
        } else if (o instanceof List) {
            return BuiltInType.LIST;
        } else if (o instanceof Map) {
            return BuiltInType.CONTEXT;
        } else if (o instanceof TemporalAccessor) {
            TemporalAccessor ta = (TemporalAccessor) o;
            if (!(ta instanceof Temporal) && ta.isSupported(ChronoField.HOUR_OF_DAY)
                    && ta.isSupported(ChronoField.MINUTE_OF_HOUR) && ta.isSupported(ChronoField.SECOND_OF_MINUTE)
                    && ta.query(TemporalQueries.zone()) != null) {
                return BuiltInType.TIME;
            }
        }
        return BuiltInType.UNKNOWN;
    }

    public static Type determineTypeFromClass(Class<?> clazz) {
        if (clazz == null) {
            return BuiltInType.UNKNOWN;
        } else if (Number.class.isAssignableFrom(clazz)) {
            return BuiltInType.NUMBER;
        } else if (String.class.isAssignableFrom(clazz)) {
            return BuiltInType.STRING;
        } else if (LocalDate.class.isAssignableFrom(clazz)) {
            return BuiltInType.DATE;
        } else if (LocalTime.class.isAssignableFrom(clazz) || OffsetTime.class.isAssignableFrom(clazz) || ZoneTime.class.isAssignableFrom(clazz)) {
            return BuiltInType.TIME;
        } else if (ZonedDateTime.class.isAssignableFrom(clazz) || OffsetDateTime.class.isAssignableFrom(clazz) || LocalDateTime.class.isAssignableFrom(clazz)) {
            return BuiltInType.DATE_TIME;
        } else if (Duration.class.isAssignableFrom(clazz) || ChronoPeriod.class.isAssignableFrom(clazz)) {
            return BuiltInType.DURATION;
        } else if (Boolean.class.isAssignableFrom(clazz)) {
            return BuiltInType.BOOLEAN;
        } else if (UnaryTest.class.isAssignableFrom(clazz)) {
            return BuiltInType.UNARY_TEST;
        } else if (Range.class.isAssignableFrom(clazz)) {
            return BuiltInType.RANGE;
        } else if (FEELFunction.class.isAssignableFrom(clazz)) {
            return BuiltInType.FUNCTION;
        } else if (List.class.isAssignableFrom(clazz)) {
            return BuiltInType.LIST;
        } else if (Map.class.isAssignableFrom(clazz)) {
            return BuiltInType.CONTEXT;
        } else if (TemporalAccessor.class.isAssignableFrom(clazz)) {
            TemporalAccessor ta = TemporalAccessor.class.cast(clazz);
            if (!(ta instanceof Temporal) && ta.isSupported(ChronoField.HOUR_OF_DAY)
                    && ta.isSupported(ChronoField.MINUTE_OF_HOUR) && ta.isSupported(ChronoField.SECOND_OF_MINUTE)
                    && ta.query(TemporalQueries.zone()) != null) {
                return BuiltInType.TIME;
            }
        }
        return BuiltInType.UNKNOWN;
    }

    public static boolean isInstanceOf(Object o, Type t) {
        if (o == null) {
            return false; // See FEEL specifications Table 49.
        }
        if (t == BuiltInType.UNKNOWN) {
            return true;
        }
        return determineTypeFromInstance(o) == t;
    }

    public static boolean isInstanceOf(Object o, String name) {
        return determineTypeFromInstance(o) == determineTypeFromName(name);
    }

}
