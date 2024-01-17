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
package org.drools.base.base;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.function.Function;

import org.drools.base.factmodel.traits.Thing;
import org.drools.base.factmodel.traits.Trait;
import org.drools.util.CoercionUtil;
import org.kie.api.prototype.Prototype;
import org.kie.api.runtime.rule.EventHandle;

public enum ValueType {

    NULL_TYPE( "null", null, SimpleValueType.NULL ),
    PCHAR_TYPE( "char", Character.TYPE, SimpleValueType.CHAR, CoercionUtil::coerceToCharacter ),
    PBYTE_TYPE( "byte", Byte.TYPE, SimpleValueType.INTEGER ),
    PSHORT_TYPE( "short", Short.TYPE, SimpleValueType.INTEGER, CoercionUtil::coerceToShort ),
    PINTEGER_TYPE( "int", Integer.TYPE, SimpleValueType.INTEGER, CoercionUtil::coerceToInteger ),
    PLONG_TYPE( "long", Long.TYPE, SimpleValueType.INTEGER, CoercionUtil::coerceToLong ),
    PFLOAT_TYPE( "float", Float.TYPE, SimpleValueType.DECIMAL, CoercionUtil::coerceToFloat ),
    PDOUBLE_TYPE ( "double", Double.TYPE, SimpleValueType.DECIMAL, CoercionUtil::coerceToDouble ),
    PBOOLEAN_TYPE( "boolean", Boolean.TYPE, SimpleValueType.BOOLEAN ),

    // wrapper types
    CHAR_TYPE( "Character", Character.class, SimpleValueType.CHAR, CoercionUtil::coerceToCharacter ),
    BYTE_TYPE( "Byte", Byte.class, SimpleValueType.INTEGER ),
    SHORT_TYPE( "Short", Short.class, SimpleValueType.INTEGER, CoercionUtil::coerceToShort ),
    INTEGER_TYPE( "Integer", Integer.class, SimpleValueType.INTEGER, CoercionUtil::coerceToInteger ),
    LONG_TYPE( "Long", Long.class, SimpleValueType.INTEGER, CoercionUtil::coerceToLong ),
    FLOAT_TYPE( "Float", Float.class, SimpleValueType.DECIMAL, CoercionUtil::coerceToFloat ),
    DOUBLE_TYPE( "Double", Double.class, SimpleValueType.DECIMAL, CoercionUtil::coerceToDouble ),
    BOOLEAN_TYPE ( "Boolean", Boolean.class, SimpleValueType.BOOLEAN ),

    NUMBER_TYPE( "Number", Number.class, SimpleValueType.NUMBER ),
    BIG_DECIMAL_TYPE( "BigDecimal", BigDecimal.class, SimpleValueType.NUMBER, CoercionUtil::coerceToBigDecimal ),
    BIG_INTEGER_TYPE( "BigInteger",BigInteger .class, SimpleValueType.NUMBER, CoercionUtil::coerceToBigInteger ),

    // other types    
    DATE_TYPE( "Date", Date.class, SimpleValueType.DATE ),
    LOCAL_DATE_TYPE( "LocalDate", LocalDate.class, SimpleValueType.DATE ),
    LOCAL_TIME_TYPE( "LocalTime", LocalDateTime.class, SimpleValueType.DATE ),

    ARRAY_TYPE( "Array", Object[].class, SimpleValueType.LIST ),
    STRING_TYPE( "String", String.class, SimpleValueType.STRING, CoercionUtil::coerceToString ),
    OBJECT_TYPE( "Object", Object.class, SimpleValueType.OBJECT ),
    PROTOTYPE_TYPE("Prototype", Prototype.class, SimpleValueType.UNKNOWN ),

    EVENT_TYPE("Event", EventHandle.class, SimpleValueType.OBJECT ),
    QUERY_TYPE("Query", DroolsQuery.class, SimpleValueType.OBJECT ),
    TRAIT_TYPE( "Trait", Thing.class, SimpleValueType.OBJECT ),
    CLASS_TYPE( "Class", Class.class, SimpleValueType.OBJECT );

    private final String name;
    private final Class<?> classType;
    private final int simpleType;
    private final Function<Object, ?> coerceFunction;

    ValueType(String name, Class<?> classType, int simpleType) {
        this(name , classType, simpleType, Function.identity());
    }

    ValueType(String name, Class<?> classType, int simpleType, Function<Object, ?> coerceFunction) {
        this.name = name;
        this.classType = classType;
        this.simpleType = simpleType;
        this.coerceFunction = coerceFunction;
    }

    public Object coerce(Object value) {
        return coerceFunction.apply( value );
    }

    public String toString() {
        return "ValueType = '" + this.name + "'";
    }

    public String getName() {
        return this.name;
    }

    public Class<?> getClassType() {
        return this.classType;
    }

    public boolean isBoolean() {
        return ((this.classType == Boolean.class) || (this.classType == Boolean.TYPE));
    }

    public boolean isNumber() {
        return ( this.simpleType == SimpleValueType.INTEGER ||
                this.simpleType == SimpleValueType.DECIMAL ||
                this.simpleType == SimpleValueType.CHAR ||
                this.simpleType == SimpleValueType.NUMBER ) ;
    }

    public boolean isIntegerNumber() {
        return this.simpleType == SimpleValueType.INTEGER;
    }

    public boolean isDecimalNumber() {
        return this.simpleType == SimpleValueType.DECIMAL;
    }

    public boolean isChar() {
        return this.simpleType == SimpleValueType.CHAR;
    }

    public boolean isDate() {
        return this.simpleType == SimpleValueType.DATE;
    }

    public boolean isEvent() {
        return this.classType == EventHandle.class;
    }

    public static ValueType determineValueType(final Class<?> clazz) {
        if ( clazz == null ) {
            return ValueType.NULL_TYPE;
        }

        // primitives
        if ( clazz == Prototype.class ) {
            return ValueType.PROTOTYPE_TYPE;
        } else if ( clazz == DroolsQuery.class ) {
            return ValueType.QUERY_TYPE;
        } else if ( clazz == Character.TYPE ) {
            return ValueType.PCHAR_TYPE;
        } else if ( clazz == Byte.TYPE ) {
            return ValueType.PBYTE_TYPE;
        } else if ( clazz == Short.TYPE ) {
            return ValueType.PSHORT_TYPE;
        } else if ( clazz == Integer.TYPE ) {
            return ValueType.PINTEGER_TYPE;
        } else if ( clazz == Long.TYPE ) {
            return ValueType.PLONG_TYPE;
        } else if ( clazz == Float.TYPE ) {
            return ValueType.PFLOAT_TYPE;
        } else if ( clazz == Double.TYPE ) {
            return ValueType.PDOUBLE_TYPE;
        } else if ( clazz == Boolean.TYPE ) {
            return ValueType.PBOOLEAN_TYPE;
        }

        // Number Wrappers
        if ( clazz == Character.class ) {
            return ValueType.CHAR_TYPE;
        } else if ( clazz == Byte.class ) {
            return ValueType.BYTE_TYPE;
        } else if ( clazz == Short.class ) {
            return ValueType.SHORT_TYPE;
        } else if ( clazz == Integer.class ) {
            return ValueType.INTEGER_TYPE;
        } else if ( clazz == Long.class ) {
            return ValueType.LONG_TYPE;
        } else if ( clazz == Float.class ) {
            return ValueType.FLOAT_TYPE;
        } else if ( clazz == Double.class ) {
            return ValueType.DOUBLE_TYPE;
        } else if ( clazz == Boolean.class ) {
            return ValueType.BOOLEAN_TYPE;
        }  else if ( clazz == BigDecimal.class ) {
            return ValueType.BIG_DECIMAL_TYPE;
        } else if ( clazz == BigInteger.class ) {
            return ValueType.BIG_INTEGER_TYPE;
        } else if ( Number.class.isAssignableFrom( clazz ) ) {
            return ValueType.NUMBER_TYPE;
        }


        // Other Object types
        if ( Date.class.isAssignableFrom( clazz ) ) {
            return ValueType.DATE_TYPE;
        } else if ( clazz == LocalDate.class ) {
            return ValueType.LOCAL_DATE_TYPE;
        } else if ( clazz == LocalDateTime.class ) {
            return ValueType.LOCAL_TIME_TYPE;
        } else if ( clazz.isArray() ) {
            return ValueType.ARRAY_TYPE;
        } else if ( clazz == String.class ) {
            return ValueType.STRING_TYPE;
        } else if ( clazz == EventHandle.class ) {
            return ValueType.EVENT_TYPE;
        } else if ( clazz == Class.class ) {
            return ValueType.CLASS_TYPE;
        }
        else if ( Thing.class.isAssignableFrom( clazz ) || clazz.isAnnotationPresent( Trait.class ) ) {
            return ValueType.TRAIT_TYPE;
        } else {
            return ValueType.OBJECT_TYPE;
        }
    }
}

