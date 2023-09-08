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

package org.drools.util;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.drools.util.MathUtils;

public class CoercionUtil {

    public static Character coerceToCharacter( Object value ) {
        if (value == null) {
            return null;
        }
        if (value instanceof Character) {
            return (Character)value;
        }
        if (value instanceof String && (( String ) value).length() == 1) {
            return (( String ) value).charAt( 0 );
        }
        if (value instanceof Number) {
            return (char) ((Number)value).intValue();
        }
        throw new RuntimeException("Unable to coerce " + value + " into a Character");
    }

    public static String coerceToString( Object value ) {
        if (value == null) {
            return null;
        }
        return value instanceof String ? (String)value : value.toString();
    }

    public static BigInteger coerceToBigInteger( Object value ) {
        if (value == null) {
            return null;
        }
        return MathUtils.getBigInteger( value );
    }

    public static BigDecimal coerceToBigDecimal( Object value ) {
        if (value == null) {
            return null;
        }
        return MathUtils.getBigDecimal( value );
    }

    public static Short coerceToShort( Object value ) {
        if (value == null) {
            return null;
        }
        if (value instanceof Short) {
            return (Short)value;
        }
        if (value instanceof Number) {
            return ((Number)value).shortValue();
        }
        if (value instanceof String) {
            return ((Number)Integer.parseInt( (( String ) value) )).shortValue();
        }
        throw new RuntimeException("Unable to coerce " + value + " into a Short");
    }

    public static Integer coerceToInteger( Object value ) {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer)value;
        }
        if (value instanceof Number) {
            return ((Number)value).intValue();
        }
        if (value instanceof String) {
            return Integer.parseInt( (( String ) value) );
        }
        throw new RuntimeException("Unable to coerce " + value + " into an Integer");
    }

    public static Long coerceToLong( Object value ) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long)value;
        }
        if (value instanceof Number) {
            return ((Number)value).longValue();
        }
        if (value instanceof String) {
            return Long.parseLong( (( String ) value) );
        }
        throw new RuntimeException("Unable to coerce " + value + " into an Integer");
    }

    public static Float coerceToFloat( Object value ) {
        if (value == null) {
            return null;
        }
        if (value instanceof Float) {
            return (Float)value;
        }
        if (value instanceof Number) {
            return ((Number)value).floatValue();
        }
        if (value instanceof String) {
            return Float.parseFloat( (( String ) value) );
        }
        throw new RuntimeException("Unable to coerce " + value + " into an Integer");
    }

    public static Double coerceToDouble( Object value ) {
        if (value == null) {
            return null;
        }
        if (value instanceof Double) {
            return (Double)value;
        }
        if (value instanceof Number) {
            return ((Number)value).doubleValue();
        }
        if (value instanceof String) {
            return Double.parseDouble( (( String ) value) );
        }
        throw new RuntimeException("Unable to coerce " + value + " into an Integer");
    }

    public static Number coerceToNumber(String value, Class<?> toClass) {
        Number ret = null;
        if (value != null) {
            if (toClass.equals(BigDecimal.class)) {
                ret = MathUtils.getBigDecimal(value);
            } else if (toClass.equals(BigInteger.class)) {
                ret = MathUtils.getBigInteger(value);
            } else if (toClass.equals(Double.class)) {
                ret = Double.valueOf(value);
            } else if (toClass.equals(Float.class)) {
                ret = Float.valueOf(value);
            } else if (toClass.equals(Long.class)) {
                ret = Long.valueOf(value);
            } else if (toClass.equals(Integer.class)) {
                ret = Integer.valueOf(value);
            } else if (toClass.equals(Short.class)) {
                ret = Short.valueOf(value);
            } else if (toClass.equals(Byte.class)) {
                ret = Byte.valueOf(value);
            } else {
                throw new RuntimeException("Unable to coerce [" + value + "] from class " + value.getClass() + " to class " + toClass);
            }
        }
        return ret;
    }

    public static Number coerceToNumber(Number value, Class<?> toClass) {
        Number ret = null;
        if (value != null) {
            if (toClass.equals(BigDecimal.class)) {
                ret = MathUtils.getBigDecimal(value);
            } else if (toClass.equals(BigInteger.class)) {
                ret = MathUtils.getBigInteger(value);
            } else if (toClass.equals(Double.class)) {
                ret = value.doubleValue();
            } else if (toClass.equals(Float.class)) {
                ret = value.floatValue();
            } else if (toClass.equals(Long.class)) {
                ret = value.longValue();
            } else if (toClass.equals(Integer.class)) {
                ret = value.intValue();
            } else if (toClass.equals(Short.class)) {
                ret = value.shortValue();
            } else if (toClass.equals(Byte.class)) {
                ret = value.byteValue();
            } else {
                throw new ClassCastException("Not possible to coerce [" + value + "] from class " + value.getClass() + " to class " + toClass);
            }
        }
        return ret;
    }
}
