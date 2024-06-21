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
package org.kie.dmn.feel.lang.types;

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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.SimpleType;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.marshaller.FEELStringMarshaller;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.custom.ZoneTime;

public enum BuiltInType implements SimpleType {

    UNKNOWN(SimpleType.ANY, "unknown", "any"), // updated FEEL lattice of types from DMN v1.2
    NUMBER(SimpleType.NUMBER),
    STRING(SimpleType.STRING),
    DATE(SimpleType.DATE),
    TIME(SimpleType.TIME),
    DATE_TIME(SimpleType.DATE_AND_TIME, "dateTime"),
    DURATION("duration", SimpleType.DAYS_AND_TIME_DURATION, SimpleType.YEARS_AND_MONTHS_DURATION, "dayTimeDuration", "yearMonthDuration"),
    BOOLEAN(SimpleType.BOOLEAN),
    RANGE("range"),
    FUNCTION(SimpleType.FUNCTION), // TODO be parametrized as FUNCTION<type>
    LIST(SimpleType.LIST),
    CONTEXT(SimpleType.CONTEXT),
    UNARY_TEST("unary test");

    private final String[] names;
    private final Collection<BuiltInTypeSymbol> symbols;

    BuiltInType(String... names) {
        this.names = names;
        this.symbols = Arrays.asList(names).stream().map(n -> new BuiltInTypeSymbol(n, this)).collect(Collectors.toList());
    }

    public String getName() {
        return names[0];
    }

    public String[] getNames() {
        return names;
    }

    public Object fromString(String value) {
        return FEELStringMarshaller.INSTANCE.unmarshall( this, value );
    }

    public String toString(Object value) {
        return FEELStringMarshaller.INSTANCE.marshall( value );
    }
    
    public static <T> Function<FEELEvent, T> justNull() {
        // TODO we should add the EventListener here somehow?
        return t -> null;
    }

    public Collection<BuiltInTypeSymbol> getSymbols() {
        return symbols;
    }

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
        } else if( o instanceof LocalTime || o instanceof OffsetTime || o instanceof ZoneTime) {
            return TIME;
        } else if( o instanceof ZonedDateTime || o instanceof OffsetDateTime || o instanceof LocalDateTime ) {
            return DATE_TIME;
        } else if (o instanceof Duration || o instanceof ChronoPeriod) {
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
        } else if (o instanceof TemporalAccessor) {
            // last, determine if it's a FEEL time with TZ
            TemporalAccessor ta = (TemporalAccessor) o;
            if (!(ta instanceof Temporal) && ta.isSupported(ChronoField.HOUR_OF_DAY) 
                    && ta.isSupported(ChronoField.MINUTE_OF_HOUR) && ta.isSupported(ChronoField.SECOND_OF_MINUTE) 
                    && ta.query(TemporalQueries.zone()) != null) {
                return TIME;
            }
        }
        return UNKNOWN;
    }

    public static boolean isInstanceOf( Object o, Type t ) {
        if ( o == null ) {
            return false; // See FEEL specifications Table 49.
        }
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

    @Override
    public boolean isAssignableValue(Object value) {
        if ( value == null ) {
            return true; // a null-value can be assigned to any type.
        }
        return isInstanceOf(value, this);
    }

    @Override
    public boolean conformsTo(Type t) {
        return t == UNKNOWN || this == t;
    }

}
