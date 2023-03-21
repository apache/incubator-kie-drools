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
package org.kie.kogito.codegen.process.persistence.proto;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;

import org.drools.codegen.common.GeneratedFile;
import org.drools.codegen.common.GeneratedFileType;

import com.fasterxml.jackson.databind.JsonNode;

import static java.lang.String.format;

public interface ProtoGenerator {

    GeneratedFileType PROTO_TYPE = GeneratedFileType.of("PROTO", GeneratedFileType.Category.STATIC_HTTP_RESOURCE);
    String INDEX_COMMENT = "@Field(index = Index.YES, store = Store.YES) @SortableField";
    String KOGITO_JAVA_CLASS_OPTION = "kogito_java_class";
    String KOGITO_SERIALIZABLE = "kogito.Serializable";
    String ARRAY = "Array";
    String COLLECTION = "Collection";

    Proto protoOfDataClasses(String packageName, String... headers);

    Collection<GeneratedFile> generateProtoFiles();

    default String applicabilityByType(String type) {
        if (type.equals(COLLECTION) || type.equals(ARRAY)) {
            return "repeated";
        }

        return "optional";
    }

    default String protoType(String type) {
        if (String.class.getCanonicalName().equals(type) || String.class.getSimpleName().equalsIgnoreCase(type)) {
            return "string";
        } else if (Integer.class.getCanonicalName().equals(type) || "int".equalsIgnoreCase(type)) {
            return "int32";
        } else if (Long.class.getCanonicalName().equals(type) || "long".equalsIgnoreCase(type)) {
            return "int64";
        } else if (Double.class.getCanonicalName().equals(type) || "double".equalsIgnoreCase(type)) {
            return "double";
        } else if (Float.class.getCanonicalName().equals(type) || "float".equalsIgnoreCase(type)) {
            return "float";
        } else if (Boolean.class.getCanonicalName().equals(type) || "boolean".equalsIgnoreCase(type)) {
            return "bool";
        } else if (Date.class.getCanonicalName().equals(type) || "date".equalsIgnoreCase(type)) {
            return "kogito.Date";
        } else if (byte[].class.getCanonicalName().equals(type) || "[B".equalsIgnoreCase(type)) {
            return "bytes";
        } else if (Instant.class.getCanonicalName().equals(type)) {
            return "kogito.Instant";
        } else if (JsonNode.class.getCanonicalName().equals(type)) {
            return KOGITO_SERIALIZABLE;
        } else if (type.startsWith("java.lang") || type.startsWith("java.util") || type.startsWith("java.time") || type.startsWith("java.math")) {
            try {
                Class<?> cls = Class.forName(type);
                if (cls.isInterface()) {
                    return null;
                }
                boolean assignable = Serializable.class.isAssignableFrom(cls);
                if (assignable) {
                    return KOGITO_SERIALIZABLE;
                } else {
                    throw new IllegalArgumentException(format("Java type %s is no supported by Kogito persistence, please consider using a class that extends java.io.Serializable", type));
                }
            } catch (ClassNotFoundException e) {
                return null;
            }
        } else {
            try {
                Class<?> cls = Class.forName(type);
                if (cls.isEnum() || containsValidConstructor(cls)) {
                    return null;
                } else if (Serializable.class.isAssignableFrom(cls)) {
                    return KOGITO_SERIALIZABLE;
                } else {
                    throw new IllegalArgumentException(
                            format("Custom type %s is no supported by Kogito persistence, please consider using a class that extends java.io.Serializable and contains a no arg constructor", type));
                }
            } catch (ClassNotFoundException e) {
                return null;
            }
        }
    }

    private boolean containsValidConstructor(Class<?> cls) {
        for (Constructor c : cls.getConstructors()) {
            if (c.getParameterCount() == 0) {
                return true;
            }
        }
        return false;
    }

    interface Builder<E, T extends ProtoGenerator> {

        Builder<E, T> withDataClasses(Collection<E> dataClasses);

        T build(Collection<E> modelClasses);
    }
}
