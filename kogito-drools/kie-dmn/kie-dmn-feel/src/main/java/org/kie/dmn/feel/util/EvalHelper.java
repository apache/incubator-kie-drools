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
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class EvalHelper {

    public static String normalizeVariableName(String name) {
        return name.replaceAll( "\\s+", " " );
    }

    public static BigDecimal getBigDecimalOrNull(Object value) {
        if ( !(value instanceof Number) ) {
            return null;
        }
        if ( !BigDecimal.class.isAssignableFrom( value.getClass() ) ) {
            if ( value instanceof Long || value instanceof Integer || value instanceof Short || value instanceof Byte ||
                 value instanceof AtomicLong || value instanceof AtomicInteger ) {
                value = new BigDecimal( ((Number) value).longValue(), MathContext.DECIMAL128 );
            } else if ( value instanceof BigInteger ) {
                value = new BigDecimal( ((BigInteger) value).toString(), MathContext.DECIMAL128 );
            } else {
                value = new BigDecimal( ((Number) value).doubleValue(), MathContext.DECIMAL128 );
            }
        }
        return (BigDecimal) value;
    }

    public static Boolean getBooleanOrNull(Object value) {
        if ( value == null || !(value instanceof Boolean) ) {
            return null;
        }
        return (Boolean) value;
    }

    public static String stripQuotes(String text) {
        if ( text == null ) {
            return null;
        } else if ( text.length() >= 2 && text.startsWith( "\"" ) && text.endsWith( "\"" ) ) {
            return text.substring( 1, text.length() - 1 );
        }
        // not sure this is ever possible, but using some defensive code here
        return text;
    }

    public static Object getValue(Object current, String property)
            throws IllegalAccessException, InvocationTargetException {
        if ( current == null ) {
            return null;
        } else if ( current instanceof Map ) {
            current = ((Map) current).get( property );
        } else {
            Method getter = getAccessor( current.getClass(), property );
            current = getter.invoke( current );
        }
        return current;
    }

    public static Method getAccessor(Class<?> clazz, String field) {
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

    public static String ucFirst(final String name) {
        return name.toUpperCase().charAt( 0 ) + name.substring( 1 );
    }

}
