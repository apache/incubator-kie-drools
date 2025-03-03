/*
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

import java.util.Arrays;
import java.util.Collection;

import java.util.function.Function;
import java.util.stream.Collectors;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.lang.SimpleType;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.marshaller.FEELStringMarshaller;
import org.kie.dmn.feel.util.BuiltInTypeUtils;

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
        this.symbols = Arrays.stream(names).map(n -> new BuiltInTypeSymbol(n, this)).collect(Collectors.toList());
    }

    public String getName() {
        return names[0];
    }

    public String[] getNames() {
        return names;
    }

    public Object fromString(String value) {
        return FEELStringMarshaller.INSTANCE.unmarshall(this, value);
    }

    public String toString(Object value) {
        return FEELStringMarshaller.INSTANCE.marshall(value);
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

    @Override
    public boolean isInstanceOf(Object o) {
        return BuiltInTypeUtils.isInstanceOf(o, this);
    }

    @Override
    public boolean isAssignableValue(Object value) {
        // a null-value can be assigned to any type.
        return value == null || isInstanceOf(value);
    }

    @Override
    public boolean conformsTo(Type t) {
        return t == UNKNOWN || this == t;
    }

}
