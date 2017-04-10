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

package org.kie.dmn.feel.lang.types;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.SimpleType;
import org.kie.dmn.feel.lang.Symbol;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.functions.*;

import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public enum BuiltInType implements SimpleType {

    UNKNOWN("unknown"),
    NUMBER("number"),
    STRING("string"),
    DATE("date"),
    TIME("time"),
    DATE_TIME("date and time", "dateTime"),
    DURATION("duration", "days and time duration", "years and months duration", "dayTimeDuration", "yearMonthDuration"),
    BOOLEAN("boolean"),
    RANGE("range"),
    FUNCTION("function"),
    LIST("list"),
    CONTEXT("context"),
    UNARY_TEST("unary test");

    private final String[] names;
    private final BuiltInTypeSymbol symbol;

    BuiltInType(String... names) {
        this.names = names;
        this.symbol = new BuiltInTypeSymbol( names[0], this );
    }

    public String getName() {
        return names[0];
    }

    public String[] getNames() {
        return names;
    }

    public Object fromString(String value) {
        switch ( this ) {
            case NUMBER: return BuiltInFunctions.getFunction( NumberFunction.class).invoke( value, null, null ).cata(BuiltInType.justNull(), Function.identity());
            case STRING: return value;
            case DATE: return BuiltInFunctions.getFunction( DateFunction.class ).invoke( value ).cata(BuiltInType.justNull(), Function.identity());
            case TIME: return BuiltInFunctions.getFunction( TimeFunction.class ).invoke( value ).cata(BuiltInType.justNull(), Function.identity());
            case DATE_TIME: return BuiltInFunctions.getFunction( DateTimeFunction.class ).invoke( value ).cata(BuiltInType.justNull(), Function.identity());
            case DURATION: return BuiltInFunctions.getFunction( DurationFunction.class ).invoke( value ).cata(BuiltInType.justNull(), Function.identity());
            case BOOLEAN: return Boolean.parseBoolean( value );
            case RANGE:
            case FUNCTION:
            case LIST:
            case CONTEXT:
            case UNARY_TEST:
                return FEEL.newInstance().evaluate( value );
        }
        return null;
    }

    public String toString(Object value) {
        return BuiltInFunctions.getFunction( StringFunction.class ).invoke( value ).cata(BuiltInType.justNull(), Function.identity());
    }
    
    static <T> Function<FEELEvent, T> justNull() {
        // TODO we should add the EventListener here somehow?
        return t -> null;
    }

    public Symbol getSymbol() { return symbol; }

    @Override
    public String toString() {
        return "Type{ " +
               names[0] +
               " }";
    }

    public static Type determineTypeFromName( String name ) {
        if( name == null ) {
            return UNKNOWN;
        }
        for( BuiltInType t : BuiltInType.values() ) {
            for( String n : t.getNames() ) {
                if( n.equals( name ) ) {
                    return t;
                }
            }
        }
        return UNKNOWN;
    }

    public static Type determineTypeFromInstance( Object o ) {
        if( o == null ) {
            return UNKNOWN;
        } else if( o instanceof Number ) {
            return NUMBER;
        } else if( o instanceof String ) {
            return STRING;
        } else if( o instanceof LocalDate ) {
            return DATE;
        } else if( o instanceof LocalTime || o instanceof OffsetTime ) {
            return TIME;
        } else if( o instanceof ZonedDateTime || o instanceof OffsetDateTime || o instanceof LocalDateTime ) {
            return DATE_TIME;
        } else if( o instanceof Duration || o instanceof Period ) {
            return DURATION;
        } else if( o instanceof Boolean ) {
            return BOOLEAN;
        } else if( o instanceof UnaryTest ) {
            return UNARY_TEST;
        } else if( o instanceof Range ) {
            return RANGE;
        } else if( o instanceof FEELFunction ) {
            return FUNCTION;
        } else if( o instanceof List ) {
            return LIST;
        } else if( o instanceof Map ) {
            return CONTEXT;
        }
        return UNKNOWN;
    }

    public static boolean isInstanceOf( Object o, Type t ) {
        if ( t == UNKNOWN ) {
            return true;
        }
        return determineTypeFromInstance( o ) == t;
    }

    public static boolean isInstanceOf( Object o, String name ) {
        return determineTypeFromInstance( o ) == determineTypeFromName( name );
    }

    @Override
    public boolean isInstanceOf(Object o) {
        return isInstanceOf(o, this);
    }


}
