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

package org.jbpm.process.instance.impl.util;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

public class TypeTransformer {

    private ObjectMapper mapper;
    private ClassLoader classLoader;

    public TypeTransformer() {
        this(TypeTransformer.class.getClassLoader());
    }

    public TypeTransformer(ClassLoader classLoader) {
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .registerModule(new com.fasterxml.jackson.databind.module.SimpleModule()
                        .addSerializer(ComparablePeriod.class, new ComparablePeriodSerializer()))
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        this.classLoader = classLoader;
    }

    private Object transform(Object toMarshal, Class<?> targetClazz, ClassLoader currentClassLoader, String className) throws ClassNotFoundException, IOException {
        JavaParser parser = new JavaParser();
        ParseResult<Type> unit = parser.parseType(className);
        if (!unit.isSuccessful()) {
            return toMarshal;
        }
        ClassOrInterfaceType type = (ClassOrInterfaceType) unit.getResult().get();
        if (Collection.class.isAssignableFrom(targetClazz) && type.getTypeArguments().isPresent()) {
            // it is a generic so we try to read it.
            ClassOrInterfaceType argument = (ClassOrInterfaceType) type.getTypeArguments().get().get(0);
            Class<?> genericType = currentClassLoader.loadClass(toString(argument));
            JavaType targetGenericType = mapper.getTypeFactory().constructCollectionType(List.class, genericType);
            return mapper.convertValue(toMarshal, targetGenericType);
        }
        return mapper.convertValue(toMarshal, targetClazz);
    }

    public Object transform(Object toMarshal, Class<?> targetClass) throws IOException, ClassNotFoundException {
        return transform(toMarshal, targetClass, targetClass.getClassLoader(), targetClass.getName());
    }

    public Object transform(Object toMarshal, String className) throws ClassNotFoundException, IOException {
        return transform(toMarshal, classLoader.loadClass(className), classLoader, className);
    }

    private String toString(ClassOrInterfaceType type) {
        StringBuilder str = new StringBuilder();
        type.getScope().ifPresent(s -> str.append(s.asString()).append("."));
        str.append(type.getNameAsString());
        return str.toString();
    }
}