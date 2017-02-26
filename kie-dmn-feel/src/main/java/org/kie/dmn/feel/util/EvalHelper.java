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

import org.kie.dmn.feel.lang.FEELProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.time.Duration;
import java.time.Period;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class EvalHelper {
    public static final Logger LOG = LoggerFactory.getLogger( EvalHelper.class );
    private static final Pattern SPACES_PATTERN = Pattern.compile( "\\s+" );

    public static String normalizeVariableName(String name) {
        return SPACES_PATTERN.matcher( name ).replaceAll( " " );
    }

    public static BigDecimal getBigDecimalOrNull(Object value) {
        if ( !(value instanceof Number || value instanceof String) ) {
            return null;
        }
        if ( !BigDecimal.class.isAssignableFrom( value.getClass() ) ) {
            if ( value instanceof Long || value instanceof Integer || value instanceof Short || value instanceof Byte ||
                 value instanceof AtomicLong || value instanceof AtomicInteger ) {
                value = new BigDecimal( ((Number) value).longValue(), MathContext.DECIMAL128 );
            } else if ( value instanceof BigInteger ) {
                value = new BigDecimal( ((BigInteger) value).toString(), MathContext.DECIMAL128 );
            } else if ( value instanceof String ) {
                // we need to remove leading zeros to prevent octal conversion
                value = new BigDecimal( ((String) value).replaceFirst("^0+(?!$)", ""), MathContext.DECIMAL128 );
            } else {
                value = new BigDecimal( ((Number) value).doubleValue(), MathContext.DECIMAL128 );
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
                        }
                    }
                } else {
                    r.append( c );
                }
            }
            text = r.toString();
        }
        return text;
    }

    public static Object getValue(Object current, String property)
            throws IllegalAccessException, InvocationTargetException {
        if ( current == null ) {
            return null;
        } else if ( current instanceof Map ) {
            current = ((Map) current).get( property );
        } else if ( current instanceof Period ) {
            switch ( property ) {
                case "years":
                    current = ((Period) current).getYears();
                    break;
                case "months":
                    current = ((Period) current).getMonths()%12;
                    break;
                case "days":
                    current = ((Period) current).getDays()%30;
                    break;
                default:
                    return null;
            }
        } else if ( current instanceof Duration ) {
            switch ( property ) {
                case "days":
                    current = ((Duration) current).toDays();
                    break;
                case "hours":
                    current = ((Duration) current).toHours()%24;
                    break;
                case "minutes":
                    current = ((Duration) current).toMinutes()%60;
                    break;
                case "seconds":
                    current = ((Duration) current).getSeconds()%60;
                    break;
                default:
                    return null;
            }
        } else if ( current instanceof Temporal ) {
            switch ( property ) {
                case "year":
                    current = ((Temporal) current).get( ChronoField.YEAR );
                    break;
                case "month":
                    current = ((Temporal) current).get( ChronoField.MONTH_OF_YEAR );
                    break;
                case "day":
                    current = ((Temporal) current).get( ChronoField.DAY_OF_MONTH );
                    break;
                case "hour":
                    current = ((Temporal) current).get( ChronoField.HOUR_OF_DAY );
                    break;
                case "minute":
                    current = ((Temporal) current).get( ChronoField.MINUTE_OF_HOUR );
                    break;
                case "second":
                    current = ((Temporal) current).get( ChronoField.SECOND_OF_MINUTE );
                    break;
                case "time offset":
                case "timezone":
                    current = Duration.ofSeconds( ((Temporal) current).get( ChronoField.OFFSET_SECONDS ) );
                    break;
                default:
                    return null;
            }
        } else {
            Method getter = getGenericAccessor( current.getClass(), property );
            if ( getter != null ) {
                current = getter.invoke( current );
            } else {
                return null;
            }
        }
        return coerceNumber( current );
    }

    /**
     * FEEL annotated or else Java accessor.
     * @param clazz
     * @param field
     * @return
     */
    public static Method getGenericAccessor(Class<?> clazz, String field) {
        LOG.trace( "getGenericAccessor({}, {})", clazz, field );
        return Stream.of( clazz.getMethods() )
                .filter( m -> Optional.ofNullable( m.getAnnotation( FEELProperty.class ) )
                        .map( ann -> ann.value().equals( field ) )
                        .orElse( false )
                )
                .findFirst()
                .orElse( getAccessor( clazz, field ) );
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

}
