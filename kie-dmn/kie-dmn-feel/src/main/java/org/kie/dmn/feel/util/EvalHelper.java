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
package org.kie.dmn.feel.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.ZoneId;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQueries;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.kie.dmn.api.core.FEELPropertyAccessible;
import org.kie.dmn.feel.lang.FEELProperty;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.Range.RangeBoundary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvalHelper {
    public static final Logger LOG = LoggerFactory.getLogger( EvalHelper.class );

    private static final Map<AccessorCacheKey, Method> accessorCache = new ConcurrentHashMap<>();

    public static class PropertyValueResult implements FEELPropertyAccessible.AbstractPropertyValueResult {

        // This exception is used to signal an undefined property for notDefined(). This method may be many times when
        // evaluating a decision, so a single instance is being cached to avoid the cost of creating the stack trace
        // each time.
        private static final Exception undefinedPropertyException = new UnsupportedOperationException("Property was not defined.");

        private final boolean defined;
        private final Either<Exception, Object> valueResult;

        private PropertyValueResult(boolean isDefined, Either<Exception, Object> value) {
            this.defined = isDefined;
            this.valueResult = value;
        }

        public static PropertyValueResult notDefined() {
            return new PropertyValueResult(false, Either.ofLeft(undefinedPropertyException));
        }

        public static PropertyValueResult of(Either<Exception, Object> valueResult) {
            return new PropertyValueResult(true, valueResult);
        }

        public static PropertyValueResult ofValue(Object value) {
            return new PropertyValueResult(true, Either.ofRight(value));
        }

        public boolean isDefined() {
            return defined;
        }

        public Either<Exception, Object> getValueResult() {
            return valueResult;
        }

        @Override
        public Optional<Object> toOptional() {
            return valueResult.cata(l -> Optional.empty(), Optional::ofNullable);
        }

    }

    public static PropertyValueResult getDefinedValue(final Object current, final String property) {
        Object result;
        if ( current == null ) {
            return PropertyValueResult.notDefined();
        } else if ( current instanceof Map ) {
            result = ((Map) current).get(property);
            if (result == null) {
                // most cases "result" will be defined, so checking here only in case null was to signify missing key altogether.
                if (!((Map) current).containsKey(property)) {
                    return PropertyValueResult.notDefined();
                }
            }
        } else if (current instanceof ChronoPeriod) {
            switch ( property ) {
                case "years":
                    result = ((ChronoPeriod) current).get(ChronoUnit.YEARS);
                    break;
                case "months":
                    result = ((ChronoPeriod) current).get(ChronoUnit.MONTHS) % 12;
                    break;
                default:
                    return PropertyValueResult.notDefined();
            }
        } else if ( current instanceof Duration ) {
            switch ( property ) {
                case "days":
                    result = ((Duration) current).toDays();
                    break;
                case "hours":
                    result = ((Duration) current).toHours() % 24;
                    break;
                case "minutes":
                    result = ((Duration) current).toMinutes() % 60;
                    break;
                case "seconds":
                    result = ((Duration) current).getSeconds() % 60;
                    break;
                default:
                    return PropertyValueResult.notDefined();
            }
        } else if (current instanceof TemporalAccessor) {
            switch ( property ) {
                case "year":
                    result = ((TemporalAccessor) current).get(ChronoField.YEAR);
                    break;
                case "month":
                    result = ((TemporalAccessor) current).get(ChronoField.MONTH_OF_YEAR);
                    break;
                case "day":
                    result = ((TemporalAccessor) current).get(ChronoField.DAY_OF_MONTH);
                    break;
                case "hour":
                    result = ((TemporalAccessor) current).get(ChronoField.HOUR_OF_DAY);
                    break;
                case "minute":
                    result = ((TemporalAccessor) current).get(ChronoField.MINUTE_OF_HOUR);
                    break;
                case "second":
                    result = ((TemporalAccessor) current).get(ChronoField.SECOND_OF_MINUTE);
                    break;
                case "time offset":
                    if (((TemporalAccessor) current).isSupported(ChronoField.OFFSET_SECONDS)) {
                        result = Duration.ofSeconds(((TemporalAccessor) current).get(ChronoField.OFFSET_SECONDS));
                    } else {
                        result = null;
                    }
                    break;
                case "timezone":
                    ZoneId zoneId = ((TemporalAccessor) current).query(TemporalQueries.zoneId());
                    if (zoneId != null) {
                        result = TimeZone.getTimeZone(zoneId).getID();
                        break;
                    } else {
                        return PropertyValueResult.notDefined();
                    }
                case "weekday":
                    result = ((TemporalAccessor) current).get(ChronoField.DAY_OF_WEEK);
                    break;
                default:
                    return PropertyValueResult.notDefined();
            }
        } else if (current instanceof Range) {
            switch (property) {
                case "start included":
                    result = ((Range) current).getLowBoundary() == RangeBoundary.CLOSED ? Boolean.TRUE : Boolean.FALSE;
                    break;
                case "start":
                    result = ((Range) current).getLowEndPoint();
                    break;
                case "end":
                    result = ((Range) current).getHighEndPoint();
                    break;
                case "end included":
                    result = ((Range) current).getHighBoundary() == RangeBoundary.CLOSED ? Boolean.TRUE : Boolean.FALSE;
                    break;
                default:
                    return PropertyValueResult.notDefined();
            }
        } else {
            Method getter = getGenericAccessor( current.getClass(), property );
            if ( getter != null ) {
                try {
                    result = getter.invoke(current);
                    if (result instanceof Character) {
                        result = result.toString();
                    } else if ( result instanceof java.util.Date ) {
                        result = java.time.Instant.ofEpochMilli(((java.util.Date) result).getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    LOG.error("Exception", e);
                    return PropertyValueResult.of(Either.ofLeft(e));
                }
            } else {
                // WORST-CASE: if code reached here, means that "property" is not defined on the "current" object at all.
                return PropertyValueResult.notDefined();
            }
        }

        // before returning, coerce "result" into number.
        result = NumberEvalHelper.coerceNumber(result);

        return PropertyValueResult.ofValue(result);
    }

    /**
     * {@link #getDefinedValue(Object, String)} method instead.
     * @deprecated this method cannot distinguish null because: 1. property undefined for current, 2. an error, 3. a properly defined property value valorized to null.
     *
     */
    public static Object getValue(final Object current, final String property) {
        return getDefinedValue(current, property).getValueResult().getOrElse(null);
    }

    /**
     * FEEL annotated or else Java accessor.
     * @param clazz
     * @param field
     * @return
     */
    public static Method getGenericAccessor(Class<?> clazz, String field) {
        LOG.trace( "getGenericAccessor({}, {})", clazz, field );

        AccessorCacheKey accessorCacheKey =
                new AccessorCacheKey( clazz.getClassLoader(), clazz.getCanonicalName(), field );

        return accessorCache.computeIfAbsent(accessorCacheKey, key ->
        	Stream.of( clazz.getMethods() )
            .filter( m -> Optional.ofNullable( m.getAnnotation( FEELProperty.class ) )
                    .map( ann -> ann.value().equals( field ) )
                    .orElse( false )
            )
            .findFirst()
            .orElse( getAccessor( clazz, field ) ));
    }

    public static void clearGenericAccessorCache() {
        accessorCache.clear();
    }

    /**
     * JavaBean -spec compliant accessor.
     * @param clazz
     * @param field
     * @return
     */
    public static Method getAccessor(Class<?> clazz, String field) {
        LOG.trace( "getAccessor({}, {})", clazz, field );
        try {
            return clazz.getMethod( "get" + StringEvalHelper.ucFirst( field ) );
        } catch ( NoSuchMethodException e ) {
            try {
                return clazz.getMethod( field );
            } catch ( NoSuchMethodException e1 ) {
                try {
                    return clazz.getMethod( "is" + StringEvalHelper.ucFirst( field ) );
                } catch ( NoSuchMethodException e2 ) {
                    return null;
                }
            }
        }
    }

    /**
     * Inverse of {@link #getAccessor(Class, String)}
     */
    public static Optional<String> propertyFromAccessor(Method accessor) {
        if ( accessor.getParameterCount() != 0 || accessor.getReturnType().equals( Void.class ) ) {
            return Optional.empty();
        }
        String methodName = accessor.getName();
        if ( methodName.startsWith( "get" ) ) {
            return Optional.of( StringEvalHelper.lcFirst( methodName.substring( 3)));
        } else if ( methodName.startsWith( "is" ) ) {
            return Optional.of( StringEvalHelper.lcFirst( methodName.substring( 2)));
        } else {
            return Optional.of( StringEvalHelper.lcFirst( methodName ) );
        }
    }

    private static class AccessorCacheKey {
        private final ClassLoader classLoader;
        private final String className;
        private final String propertyName;

        public AccessorCacheKey(ClassLoader classLoader, String className, String propertyName) {
            this.classLoader = classLoader;
            this.className = className;
            this.propertyName = propertyName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            AccessorCacheKey that = (AccessorCacheKey) o;

            if (!Objects.equals(classLoader, that.classLoader)) {
                return false;
            }
            if (!Objects.equals(className, that.className)) {
                return false;
            }
            return Objects.equals(propertyName, that.propertyName);
        }

        @Override
        public int hashCode() {
            int result = classLoader != null ? classLoader.hashCode() : 0;
            result = 31 * result + (className != null ? className.hashCode() : 0);
            result = 31 * result + (propertyName != null ? propertyName.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "AccessorCacheKey{" +
                    "classLoader=" + classLoader +
                    ", className='" + className + '\'' +
                    ", propertyName='" + propertyName + '\'' +
                    '}';
        }
    }
}
