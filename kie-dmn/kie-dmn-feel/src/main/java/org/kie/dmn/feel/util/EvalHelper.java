/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.time.Duration;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalQueries;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.FEELProperty;
import org.kie.dmn.feel.runtime.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvalHelper {
    public static final Logger LOG = LoggerFactory.getLogger( EvalHelper.class );
    private static final Pattern SPACES_PATTERN = Pattern.compile( "[\\s\u00A0]+" );

    private static final Map<String, Method> accessorCache = new HashMap<>();
    private static final Map<String, String> normalizationCache = new HashMap<>();

    public static String normalizeVariableName(String name) {
		if (normalizationCache.containsKey(name)){
			return normalizationCache.get(name);
		}
        String normalized = SPACES_PATTERN.matcher( name.trim() ).replaceAll( " " );

        normalizationCache.put(name, normalized);

        return normalized;
    }

    public static BigDecimal getBigDecimalOrNull(Object value) {
        if ( !(value instanceof Number
                || value instanceof String)
                || (value instanceof Double
                && (value.toString().equals("NaN") || value.toString().equals("Infinity") || value.toString().equals("-Infinity"))) ) {
            return null;
        }
        if ( !BigDecimal.class.isAssignableFrom( value.getClass() ) ) {
            if ( value instanceof Long || value instanceof Integer || value instanceof Short || value instanceof Byte ||
                 value instanceof AtomicLong || value instanceof AtomicInteger ) {
                value = new BigDecimal( ((Number) value).longValue(), MathContext.DECIMAL128 );
            } else if ( value instanceof BigInteger ) {
                value = new BigDecimal( (BigInteger) value, MathContext.DECIMAL128 );
            } else if ( value instanceof String ) {
                // we need to remove leading zeros to prevent octal conversion
                value = new BigDecimal( ((String) value).replaceFirst("^0+(?!$)", ""), MathContext.DECIMAL128 );
            } else {
                // doubleValue() sometimes produce rounding errors, so we need to use toString() instead
                // We also need to remove trailing zeros, if there are some so for 10d we get BigDecimal.valueOf(10)
                // instead of BigDecimal.valueOf(10.0).
                value = new BigDecimal( removeTrailingZeros(value.toString()), MathContext.DECIMAL128 );
            }
        }
        return (BigDecimal) value;
    }

    public static Object coerceNumber(Object value) {
        if ( value instanceof Number && !(value instanceof BigDecimal) ) {
            return getBigDecimalOrNull( value );
        } else {
            return value;
        }
    }

    public static Boolean getBooleanOrNull(Object value) {
        if ( value == null || !(value instanceof Boolean) ) {
            return null;
        }
        return (Boolean) value;
    }

    public static String unescapeString(String text) {
        if ( text == null ) {
            return null;
        }
        if ( text.length() >= 2 && text.startsWith( "\"" ) && text.endsWith( "\"" ) ) {
            // remove the quotes
            text = text.substring( 1, text.length() - 1 );
        }
        if ( text.indexOf( '\\' ) >= 0 ) {
            // might require un-escaping
            StringBuilder r = new StringBuilder();
            for ( int i = 0; i < text.length(); i++ ) {
                char c = text.charAt( i );
                if ( c == '\\' ) {
                    if ( text.length() > i + 1 ) {
                        i++;
                        char cn = text.charAt( i );
                        switch ( cn ) {
                            case 'b':
                                r.append( '\b' );
                                break;
                            case 't':
                                r.append( '\t' );
                                break;
                            case 'n':
                                r.append( '\n' );
                                break;
                            case 'f':
                                r.append( '\f' );
                                break;
                            case 'r':
                                r.append( '\r' );
                                break;
                            case '"':
                                r.append( '"' );
                                break;
                            case '\'':
                                r.append( '\'' );
                                break;
                            case '\\':
                                r.append( '\\' );
                                break;
                            case 'u': {
                                if ( text.length() >= i + 5 ) {
                                    // escape unicode
                                    String hex = text.substring( i + 1, i + 5 );
                                    char[] chars = Character.toChars( Integer.parseInt( hex, 16 ) );
                                    r.append( chars );
                                    i += 4;
                                } else {
                                    // not really unicode
                                    r.append( "\\" ).append( cn );
                                }
                                break;
                            }
                            default:
                                r.append( "\\" ).append( cn );
                        }
                    } else {
                        r.append( c );
                    }
                } else {
                    r.append( c );
                }
            }
            text = r.toString();
        }
        return text;
    }

    public static class PropertyValueResult {

        private final boolean defined;
        private final Either<Exception, Object> valueResult;

        private PropertyValueResult(boolean isDefined, Either<Exception, Object> value) {
            this.defined = isDefined;
            this.valueResult = value;
        }

        public static PropertyValueResult notDefined() {
            return new PropertyValueResult(false, Either.ofLeft(new UnsupportedOperationException("Property was not defined.")));
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
        } else if ( current instanceof Period ) {
            switch ( property ) {
                case "years":
                    result = ((Period) current).getYears();
                    break;
                case "months":
                    result = ((Period) current).getMonths() % 12;
                    break;
                case "days":
                    result = ((Period) current).getDays() % 30;
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
        } else if ( current instanceof Temporal ) {
            switch ( property ) {
                case "year":
                    result = ((Temporal) current).get(ChronoField.YEAR);
                    break;
                case "month":
                    result = ((Temporal) current).get(ChronoField.MONTH_OF_YEAR);
                    break;
                case "day":
                    result = ((Temporal) current).get(ChronoField.DAY_OF_MONTH);
                    break;
                case "hour":
                    result = ((Temporal) current).get(ChronoField.HOUR_OF_DAY);
                    break;
                case "minute":
                    result = ((Temporal) current).get(ChronoField.MINUTE_OF_HOUR);
                    break;
                case "second":
                    result = ((Temporal) current).get(ChronoField.SECOND_OF_MINUTE);
                    break;
                case "time offset":
                    result = Duration.ofSeconds(((Temporal) current).get(ChronoField.OFFSET_SECONDS));
                    break;
                case "timezone":
                    ZoneId zoneId = ((Temporal) current).query(TemporalQueries.zoneId());
                    if (zoneId != null) {
                        result = TimeZone.getTimeZone(zoneId).getDisplayName();
                        break;
                    } else {
                        return PropertyValueResult.notDefined();
                    }
                case "weekday":
                    result = ((Temporal) current).get(ChronoField.DAY_OF_WEEK);
                    break;
                default:
                    return PropertyValueResult.notDefined();
            }
        } else {
            Method getter = getGenericAccessor( current.getClass(), property );
            if ( getter != null ) {
                try {
                    result = getter.invoke(current);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                    return PropertyValueResult.of(Either.ofLeft(e));
                }
            } else {
                // WORST-CASE: if code reached here, means that "property" is not defined on the "current" object at all.
                return PropertyValueResult.notDefined();
            }
        }

        // before returning, coerce "result" into number.
        result = coerceNumber(result);

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

        String accessorQualifiedName = new StringBuilder(clazz.getCanonicalName())
			.append(".").append(field).toString();

        if (accessorCache.containsKey(accessorQualifiedName)) {
			return accessorCache.get(accessorQualifiedName);
        }

        Method method = Stream.of( clazz.getMethods() )
                .filter( m -> Optional.ofNullable( m.getAnnotation( FEELProperty.class ) )
                        .map( ann -> ann.value().equals( field ) )
                        .orElse( false )
                )
                .findFirst()
                .orElse( getAccessor( clazz, field ) );

        accessorCache.put(accessorQualifiedName, method);

        return method;
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
            return clazz.getMethod( "get" + ucFirst( field ) );
        } catch ( NoSuchMethodException e ) {
            try {
                return clazz.getMethod( field );
            } catch ( NoSuchMethodException e1 ) {
                try {
                    return clazz.getMethod( "is" + ucFirst( field ) );
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
            return Optional.of( lcFirst( methodName.substring( 3, methodName.length() ) ) );
        } else if ( methodName.startsWith( "is" ) ) {
            return Optional.of( lcFirst( methodName.substring( 2, methodName.length() ) ) );
        } else {
            return Optional.of( lcFirst( methodName ) );
        }
    }

    public static String ucFirst(final String name) {
        return name.toUpperCase().charAt( 0 ) + name.substring( 1 );
    }

    public static String lcFirst(final String name) {
        return name.toLowerCase().charAt( 0 ) + name.substring( 1 );
    }

    /**
     * Compares left and right operands using the given predicate and returns TRUE/FALSE accordingly
     *
     * @param left
     * @param right
     * @param ctx
     * @param op
     * @return
     */
    public static Boolean compare(Object left, Object right, EvaluationContext ctx, BiPredicate<Comparable, Comparable> op) {
        if ( left == null || right == null ) {
            return null;
        } else if ( (left instanceof Period && right instanceof Period ) ) {
            // periods have special compare semantics in FEEL as it ignores "days". Only months and years are compared
            Period lp = (Period) left;
            Period rp = (Period) right;
            Integer l = lp.getYears() * 12 + lp.getMonths();
            Integer r = rp.getYears() * 12 + rp.getMonths();
            return op.test( l, r );
        } else if ( (left instanceof String && right instanceof String) ||
                    (left instanceof Number && right instanceof Number) ||
                    (left instanceof Boolean && right instanceof Boolean) ||
                    (left instanceof Comparable && left.getClass().isAssignableFrom( right.getClass() )) ) {
            Comparable l = (Comparable) left;
            Comparable r = (Comparable) right;
            return op.test( l, r );
        }
        return null;
    }

    /**
     * Compares left and right for equality applying FEEL semantics to specific data types
     *
     * @param left
     * @param right
     * @param ctx
     * @return
     */
    public static Boolean isEqual(Object left, Object right, EvaluationContext ctx ) {
        if ( left == null || right == null ) {
            return left == right;
        }

        // spec defines that "a=[a]", i.e., singleton collections should be treated as the single element
        // and vice-versa
        if( left instanceof Collection && !(right instanceof Collection) && ((Collection)left).size() == 1 ) {
            left = ((Collection)left).toArray()[0];
        } else if( right instanceof Collection && !(left instanceof Collection) && ((Collection)right).size()==1 ) {
            right = ((Collection) right).toArray()[0];
        }

        if( left instanceof Range && right instanceof Range ) {
            return isEqual( (Range)left, (Range) right );
        } else if( left instanceof Iterable && right instanceof Iterable ) {
            return isEqual( (Iterable)left, (Iterable) right );
        } else if( left instanceof Map && right instanceof Map ) {
            return isEqual( (Map)left, (Map) right );
        }
        return compare( left, right, ctx, (l, r) -> l.compareTo( r ) == 0  );
    }

    private static Boolean isEqual(Range left, Range right) {
        return left.equals( right );
    }

    private static Boolean isEqual(Iterable left, Iterable right) {
        Iterator li = left.iterator();
        Iterator ri = right.iterator();
        while( li.hasNext() && ri.hasNext() ) {
            Object l = li.next();
            Object r = ri.next();
            if ( !isEqual( l, r ) ) return false;
        }
        return li.hasNext() == ri.hasNext();
    }

    private static Boolean isEqual(Map<?,?> left, Map<?,?> right) {
        if( left.size() != right.size() ) {
            return false;
        }
        for( Map.Entry le : left.entrySet() ) {
            Object l = le.getValue();
            Object r = right.get( le.getKey() );
            if ( !isEqual( l, r ) ) return false;
        }
        return true;
    }

    private static Boolean isEqual(Object l, Object r) {
        if( l instanceof Iterable && r instanceof Iterable && !isEqual( (Iterable) l, (Iterable) r ) ) {
            return false;
        } else if( l instanceof Map && r instanceof Map && !isEqual( (Map) l, (Map) r ) ) {
            return false;
        } else if( l != null && r != null && !l.equals( r ) ) {
            return false;
        } else if( ( l == null || r == null ) && l != r ) {
            return false;
        }
        return true;
    }

    private static String removeTrailingZeros(final String stringNumber) {
        final String stringWithoutZeros = stringNumber.replaceAll("0*$", "");
        if (Character.isDigit(stringWithoutZeros.charAt(stringWithoutZeros.length() - 1))) {
            return stringWithoutZeros;
        } else {
            return stringWithoutZeros.substring(0, stringWithoutZeros.length() - 1);
        }
    }
}
