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

import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class EvalHelper {

    public static Type determineFeelType( Object value ) {
        if( value == null ) {
            return BuiltInType.NULL;
        } else if( value instanceof Number ) {
            return BuiltInType.NUMBER;
        } else if( value instanceof String ) {
            return BuiltInType.STRING;
        } else if( value instanceof List ) {
            return BuiltInType.LIST;
        } else if( value instanceof Boolean ) {
            return BuiltInType.BOOLEAN;
        } else if( value instanceof Map ) {
            return BuiltInType.CONTEXT;
        }
        return BuiltInType.UNKNOWN;
    }


    public static BigDecimal getBigDecimalOrNull(Object value) {
        if( ! (value instanceof Number ) ) {
            return null;
        }
        if( ! BigDecimal.class.isAssignableFrom( value.getClass() ) ) {
            if( value instanceof Long || value instanceof Integer || value instanceof Short || value instanceof Byte ||
                value instanceof AtomicLong || value instanceof AtomicInteger ) {
                value = BigDecimal.valueOf( ((Number) value).longValue() );
            } else if( value instanceof BigInteger ) {
                value = new BigDecimal( ((BigInteger) value).toString() );
            } else {
                value = BigDecimal.valueOf( ((Number)value).doubleValue() );
            }
        }
        return (BigDecimal) value;
    }

    public static Boolean getBooleanOrNull(Object value) {
        if( value == null || ! (value instanceof Boolean ) ) {
            return null;
        }
        return (Boolean) value;
    }

    public static String stripQuotes(String text) {
        if( text == null ) {
            return null;
        } else if( text.length() >= 2 && text.startsWith( "\"" ) && text.endsWith( "\"" ) ) {
            return text.substring( 1, text.length()-1 );
        }
        // not sure this is ever possible, but using some defensive code here
        return text;
    }
}
