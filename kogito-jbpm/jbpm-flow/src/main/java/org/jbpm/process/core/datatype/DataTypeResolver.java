/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.process.core.datatype;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.jbpm.process.core.datatype.impl.type.BooleanDataType;
import org.jbpm.process.core.datatype.impl.type.EnumDataType;
import org.jbpm.process.core.datatype.impl.type.FloatDataType;
import org.jbpm.process.core.datatype.impl.type.IntegerDataType;
import org.jbpm.process.core.datatype.impl.type.ListDataType;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.process.core.datatype.impl.type.StringDataType;

public class DataTypeResolver {

    public static final DataType defaultDataType = new ObjectDataType("java.lang.Object");

    private static final Map<Class<?>, DataType> dataTypes = new HashMap<>();

    static {
        dataTypes.put(Boolean.class, new BooleanDataType());
        dataTypes.put(String.class, new StringDataType());
        dataTypes.put(Integer.class, new IntegerDataType());
        dataTypes.put(Float.class, new FloatDataType());
        dataTypes.put(Collection.class, new ListDataType());
        dataTypes.put(Enum.class, new EnumDataType());
    }

    private DataTypeResolver() {
    }

    public static DataType fromType(String type, ClassLoader cl) {
        return type == null ? defaultDataType : from(type, cl);
    }

    public static DataType fromObject(Object value) {
        return value == null ? defaultDataType : from(value.getClass()).orElse(buildObjectDataType(value.getClass().getCanonicalName()));
    }

    private static DataType from(String type, ClassLoader cl) {
        if (!type.contains(".")) {
            type = "java.lang." + type;
        }
        try {
            return from(cl.loadClass(type)).orElse(new ObjectDataType(type, cl));
        } catch (ClassNotFoundException ex) {
            return new ObjectDataType(type);
        }
    }

    private static Optional<DataType> from(Class<?> type) {
        for (Entry<Class<?>, DataType> entry : dataTypes.entrySet()) {
            if (entry.getKey().isAssignableFrom(type)) {
                return Optional.of(entry.getValue());
            }
        }
        return Optional.empty();
    }

    private static DataType buildObjectDataType(String type) {
        try {
            Class.forName(type);
            return new ObjectDataType(type);
        } catch (ClassNotFoundException ex) {
            return defaultDataType;
        }
    }
}
