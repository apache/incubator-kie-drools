/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.commons.model.enums;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import org.kie.pmml.commons.exceptions.KieEnumException;

/**
 * @see <a href=http://dmg.org/pmml/v4-4/DataDictionary.html#xsdType_DATATYPE>DATATYPE</a>
 */
public enum DATA_TYPE {

    STRING("string", String.class),
    INTEGER("integer", int.class),
    FLOAT("float", float.class),
    DOUBLE("double", double.class),
    BOOLEAN("boolean", boolean.class),
    DATE("date", Date.class),
    TIME("time", Date.class),
    DATE_TIME("dateTime", Date.class),
    DATE_DAYS_SINCE_0("dateDaysSince[0]", Long.class),
    DATE_DAYS_SINCE_1960("dateDaysSince[1960]", Long.class),
    DATE_DAYS_SINCE_1970("dateDaysSince[1970]", Long.class),
    DATE_DAYS_SINCE_1980("dateDaysSince[1980]", Long.class),
    TIME_SECONDS("timeSeconds", Long.class),
    DATE_TIME_SECONDS_SINCE_0("dateTimeSecondsSince[0]", Long.class),
    DATE_TIME_SECONDS_SINCE_1960("dateTimeSecondsSince[1960]", Long.class),
    DATE_TIME_SECONDS_SINCE_1970("dateTimeSecondsSince[1970]", Long.class),
    DATE_TIME_SECONDS_SINCE_1980("dateTimeSecondsSince[1980]", Long.class);

    private final String name;
    private final Class<?> mappedClass;

    DATA_TYPE(String v, Class<?> c) {
        name = v;
        mappedClass = c;
    }

    public static DATA_TYPE byName(String name) {
        return Arrays.stream(DATA_TYPE.values()).filter(value -> Objects.equals(name, value.name)).findFirst().orElseThrow(() -> new KieEnumException("Failed to find DATA_TYPE with name: " + name));
    }

    public String getName() {
        return name;
    }

    public Class<?> getMappedClass() {
        return mappedClass;
    }
}
