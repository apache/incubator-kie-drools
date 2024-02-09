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
package org.drools.model.codegen.execmodel.generator.declaredtype;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.drl.ast.descr.AnnotationDescr;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.AnnotationDefinition;
import org.drools.model.codegen.execmodel.generator.declaredtype.api.TypeResolver;
import org.kie.api.definition.type.Duration;
import org.kie.api.definition.type.Expires;
import org.kie.api.definition.type.Key;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Timestamp;

import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.joining;
import static org.drools.model.codegen.execmodel.generator.declaredtype.POJOGenerator.quote;

public class DescrAnnotationDefinition implements AnnotationDefinition {

    static final String VALUE = "value";

    private static final Map<String, Class<?>> annotationMapping = new HashMap<>();

    static {
        annotationMapping.put("role", Role.class);
        annotationMapping.put("duration", Duration.class);
        annotationMapping.put("expires", Expires.class);
        annotationMapping.put("timestamp", Timestamp.class);
        annotationMapping.put("key", Key.class);
        annotationMapping.put("position", Position.class);
    }

    private String name;
    private Map<String, String> values;

    public DescrAnnotationDefinition(String name, Map<String, String> values) {
        this.name = name;
        this.values = values;
    }

    public DescrAnnotationDefinition(String name, String singleValue) {
        this(name, singletonMap(VALUE, singleValue));
    }

    public DescrAnnotationDefinition(String name) {
        this(name, Collections.emptyMap());
    }

    public static DescrAnnotationDefinition fromDescr(TypeResolver typeResolver, AnnotationDescr ann) {
        Optional<Class<?>> optAnnotationClass = Optional.ofNullable(annotationMapping.get(ann.getName().toLowerCase()));

        optAnnotationClass = optAnnotationClass.isPresent() ?
                optAnnotationClass :
                typeResolver.resolveType(ann.getFullyQualifiedName() != null ? ann.getFullyQualifiedName() : ann.getName());

        return optAnnotationClass.map(annotationClass -> {
            Map<String, String> values = transformedAnnotationValues(annotationClass, ann.getValueMap());
            String name = annotationClass.getCanonicalName();

            return new DescrAnnotationDefinition(name, values);
        }).orElseThrow(() -> new UnkownAnnotationClassException(ann.getName()));
    }

    private static Map<String, String> transformedAnnotationValues(Class<?> annotationClass,
                                                                   Map<String, Object> annotationValues) {

        checkNonExistingKeys(annotationClass, annotationValues);

        return annotationValues.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                                          e -> parseValue(annotationClass, e.getKey(), e.getValue())));
    }

    private static void checkNonExistingKeys(Class<?> annotationClass, Map<String, Object> annotationValues) {
        List<String> allNonExistingKeys = annotationValues.keySet().stream()
                .map(o -> getNonExistingValue(annotationClass, o))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        if (!allNonExistingKeys.isEmpty()) {
            throw new UnknownKeysInAnnotation(allNonExistingKeys);
        }
    }

    private static String parseValue(Class<?> annotationClass, String valueName, Object valueObject) {
        final String parsedValue = parseAnnotationValue(valueObject);
        if (annotationClass.equals(Role.class)) {
            return Role.Type.class.getCanonicalName() + "." + parsedValue.toUpperCase();
        } else if (annotationClass.equals(Expires.class)) {
            if (VALUE.equals(valueName)) {
                return quote(parsedValue);
            } else if ("policy".equals(valueName)) {
                return org.kie.api.definition.type.Expires.Policy.class.getCanonicalName() + "." + parsedValue.toUpperCase();
            } else {
                throw new UnsupportedOperationException("Unrecognized annotation value for Expires: " + valueName);
            }
        } else if (needsQuoting(annotationClass, valueName)) {
            return quote(parsedValue);
        }
        return parsedValue;
    }

    private static boolean needsQuoting(Class<?> annotationClass, String valueName) {
        return annotationClass.equals(Duration.class)
                || annotationClass.equals(Timestamp.class)
                && valueName.equals(VALUE);
    }

    // This returns an Optional.of if the value doesn't exist.
    private static Optional<String> getNonExistingValue(Class<?> annotationClass, String valueName) {
        try {
            annotationClass.getMethod(valueName);
            return Optional.empty();
        } catch (NoSuchMethodException e) {
            return Optional.of(valueName);
        }
    }

    private static String parseAnnotationValue(Object value) {
        if (value instanceof Class<?>) {
            return ((Class<?>) value).getName() + ".class";
        }
        if (value.getClass().isArray()) {
            String valueString = Stream.of((Object[]) value)
                    .map(Object::toString)
                    .collect(joining(",", "{", "}"));

            return valueString
                    .replace('[', '{')
                    .replace(']', '}');
        }
        return value.toString();
    }

    public static AnnotationDefinition createPositionAnnotation(int position) {
        return new DescrAnnotationDefinition(Position.class.getName(),
                                             singletonMap(VALUE, String.valueOf(position)));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, String> getValueMap() {
        return values;
    }

    @Override
    public AnnotationDefinition addValue(String key, String value) {
        values.put(key, value);
        return this;
    }

    @Override
    public boolean shouldAddAnnotation() {
        return !name.equals("serialVersionUID");
    }

    public boolean isKey() {
        return isDroolsAnnotation(Key.class);
    }

    public boolean isPosition() {
        return isDroolsAnnotation(Position.class);
    }

    public boolean isClassLevelAnnotation() {
        return isDroolsAnnotation(Duration.class) ||
                isDroolsAnnotation(Expires.class) ||
                isDroolsAnnotation(Timestamp.class);
    }

    private boolean isDroolsAnnotation(Class<?> key) {
        return name.equals(key.getName());
    }

    @Override
    public String toString() {
        return "DescrAnnotationDefinition{" +
                "name='" + name + '\'' +
                ", values=" + values +
                '}';
    }
}
