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
package org.kie.pmml.api.enums;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import org.kie.pmml.api.exceptions.KieDataFieldException;
import org.kie.pmml.api.exceptions.KieEnumException;
import org.kie.pmml.api.utils.ConverterTypeUtil;

public enum DATA_TYPE implements Named {

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
        return Arrays.stream(DATA_TYPE.values())
                .filter(value -> Objects.equals(name, value.name))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find DATA_TYPE with name: " + name));
    }

    public String getName() {
        return name;
    }

    public Class<?> getMappedClass() {
        return mappedClass;
    }

    /**
     * This method convert a <b>raw</b> object value to the correct type as defined
     * in the original <code>DataDictionary</code>.
     * Needed for example when an <b>unmarshalled</b> <code>Predicate</code> expose a field' value as <code>String</code>
     * while the value' type is defined as <code>double</code> in the <code>DataField</code>definition.
     * @param rawValue
     * @return
     */
    public Object getActualValue(Object rawValue) {
        if (rawValue == null) {
            return null;
        }
        if (mappedClass.isAssignableFrom(rawValue.getClass())) {
            // No cast/transformation needed
            return rawValue;
        }
        if (rawValue instanceof String) {
            String stringValue = (String) rawValue;
            try {
                switch (this) {
                    case STRING:
                        return stringValue;
                    case INTEGER:
                        return Integer.parseInt(stringValue);
                    case FLOAT:
                        return Float.parseFloat(stringValue);
                    case DOUBLE:
                        return Double.parseDouble(stringValue);
                    case BOOLEAN:
                        return Boolean.parseBoolean(stringValue);
                    case DATE:
                        return LocalDate.parse(stringValue);
                    case TIME:
                        return LocalTime.parse(stringValue);
                    case DATE_TIME:
                        return LocalDateTime.parse(stringValue);
                    case DATE_DAYS_SINCE_0:
                    case DATE_DAYS_SINCE_1960:
                    case DATE_DAYS_SINCE_1970:
                    case DATE_DAYS_SINCE_1980:
                    case TIME_SECONDS:
                    case DATE_TIME_SECONDS_SINCE_0:
                    case DATE_TIME_SECONDS_SINCE_1960:
                    case DATE_TIME_SECONDS_SINCE_1970:
                    case DATE_TIME_SECONDS_SINCE_1980:
                        return Long.parseLong(stringValue);
                    default:
                        throw new KieDataFieldException("Fail to convert " + rawValue + "[" + rawValue.getClass().getName() + "] to expected class " + mappedClass.getName());
                }
            } catch (Exception e) {
                throw new KieDataFieldException("Fail to convert " + rawValue + "[" + rawValue.getClass().getName() + "] to expected class " + mappedClass.getName(), e);
            }
        } else {
            return ConverterTypeUtil.convert(mappedClass, rawValue);
        }
    }
}
