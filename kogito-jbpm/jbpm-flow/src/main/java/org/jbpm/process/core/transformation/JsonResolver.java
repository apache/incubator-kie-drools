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
package org.jbpm.process.core.transformation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

public class JsonResolver {

    private final ObjectMapper objectMapper;

    /**
     * @param objectMapper object mapper to be used when converting input items
     */
    public JsonResolver(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JsonResolver() {
        this(new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                .setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE));
    }

    /**
     * Receive an Input Map of items and returns a new Map containing all items from input but the values
     * with Jackson annotations are translated into an inner Map in a recursive way. This is useful for the
     * communication of 2 services based on json parameter values as inputs but already deserialized into java POJOs.
     * 
     * @param items Input items map
     * @return a new Map containing all items from input but with the resolved values to Json if applied
     */
    public Map<String, Object> resolveOnlyAnnotatedItems(Map<String, Object> items) {
        if (Objects.isNull(items)) {
            return null;
        }
        Map<String, Map> resolved = items.entrySet().stream()
                .filter(v -> Objects.nonNull(v.getValue()))
                .filter(v -> hasJacksonAnnotations(v.getValue().getClass()))
                .collect(Collectors.toMap(Map.Entry::getKey, v -> objectMapper.convertValue(v.getValue(), Map.class)));
        HashMap<String, Object> result = new HashMap<>(items);
        result.putAll(resolved);
        return result;
    }

    public Map<String, Object> resolveAll(Map<String, Object> items) {
        return Optional.ofNullable(items)
                .map(input -> objectMapper.convertValue(items, Map.class))
                .orElse(null);
    }

    private boolean hasJacksonAnnotations(AnnotatedElement element) {
        Annotation[] declaredAnnotations = element.getDeclaredAnnotations();
        Annotation[] declaringClassAnnotations =
                Optional.of(element).filter(Field.class::isInstance).map(Field.class::cast).map(Field::getType).map(Class::getDeclaredAnnotations).orElse(new Annotation[0]);
        if (Stream.of(declaredAnnotations, declaringClassAnnotations).flatMap(Stream::of)
                .anyMatch(a -> a.annotationType().getAnnotationsByType(JacksonAnnotation.class).length > 0)) {
            return true;
        }
        if (!(element instanceof Class) || ((Class<?>) element).isPrimitive()) {
            return false;
        }
        Class<?> clazz = (Class<?>) element;
        return Arrays.stream(clazz.getDeclaredFields()).anyMatch(f -> hasJacksonAnnotations(f));
    }
}
