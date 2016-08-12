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

import org.kie.dmn.feel.lang.Symbol;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.runtime.FEELFunction;
import org.kie.dmn.feel.lang.runtime.UnaryTest;

import java.time.*;
import java.util.List;
import java.util.Map;

public enum BuiltInType implements Type {

    UNKNOWN("unknown"),
    NUMBER("number"),
    STRING("string"),
    DATE("date"),
    TIME("time"),
    DATE_TIME("date and time"),
    DURATION("duration"),
    BOOLEAN("boolean"),
    FUNCTION("function"),
    LIST("list"),
    CONTEXT("context"),
    UNARY_TEST("unary test");

    private final String name;
    private final BuiltInTypeSymbol symbol;

    BuiltInType(String name) {
        this.name = name;
        this.symbol = new BuiltInTypeSymbol( name, this );
    }

    public String getName() {
        return name;
    }

    public Symbol getSymbol() { return symbol; }

    @Override
    public String toString() {
        return "Type{ " +
               name +
               " }";
    }

    public static Type determineTypeFromName( String name ) {
        for( BuiltInType t : BuiltInType.values() ) {
            if( t.getName().equals( name ) ) {
                return t;
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
        return determineTypeFromInstance( o ) == t;
    }

    public static boolean isInstanceOf( Object o, String name ) {
        return determineTypeFromInstance( o ) == determineTypeFromName( name );
    }
}
