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

package org.kie.dmn.typesafe;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.feel.lang.SimpleType;
import org.kie.dmn.feel.lang.types.BuiltInType;

public class DMNAllTypesIndex {

    private final List<DMNType> indexedTypes = new ArrayList<>();

    Map<IndexKey, DMNModelTypesIndex.IndexValue> mapNamespaceIndex = new HashMap<>();

    public DMNAllTypesIndex(DMNTypeSafePackageName.Factory packageName, DMNModel... allModels) {
        for (DMNModel m : allModels) {
            DMNModelTypesIndex indexFromModel = new DMNModelTypesIndex(m, packageName);
            mapNamespaceIndex.putAll(indexFromModel.getIndex());
            indexedTypes.addAll(indexFromModel.getTypesToGenerate());
        }
    }

    public List<DMNType> typesToGenerateByNS(String namespace) {
        return indexedTypes.stream().filter(t -> namespace.equals(t.getNamespace())).collect(Collectors.toList());
    }

    @Deprecated
    public Optional<DMNTypeSafePackageName> namespaceOfClass(String typeName) {
        return mapNamespaceIndex.entrySet().stream()
                                .filter(kv -> kv.getKey().getName().equals(typeName))
                                .findFirst()
                                .map(Entry::getValue)
                                .map(DMNModelTypesIndex.IndexValue::getPackageName);
    }

    @Deprecated
    public boolean isIndexedClass(String typeName) {
        return namespaceOfClass(typeName).isPresent();
    }

    public String asJava(DMNType dmnType) {
        Optional<DMNModelTypesIndex.IndexValue> ivLookup = Optional.ofNullable(mapNamespaceIndex.get(IndexKey.from(dmnType)));
        if (ivLookup.isPresent()) {
            String simpleName = DMNDeclaredType.asJavaSimpleName(dmnType);
            return ivLookup.get().getPackageName().appendPackage(simpleName);
        }
        if (DMNTypeUtils.isFEELBuiltInType(dmnType)) {
            return convertBuiltin(dmnType);
        }
        if (dmnType.getBaseType() == null) {
            throw new IllegalStateException();
        }
        String baseConverted = asJava(dmnType.getBaseType());
        if (dmnType.isCollection()) {
            return juCollection(baseConverted);
        }
        return baseConverted;
    }

    public static String juCollection(String base) {
        return String.format("java.util.Collection<%s>", base);
    }

    private String convertBuiltin(DMNType expectedFEELType) {
        BuiltInType builtin = DMNTypeUtils.getFEELBuiltInType(expectedFEELType);
        Class<?> convertedClass = Object.class;
        if (builtin == BuiltInType.DURATION) {
            convertedClass = convertDurationToJavaClass(expectedFEELType);
        } else {
            convertedClass = convertBuiltInToJavaClass(builtin);
        }
        return convertedClass.getCanonicalName();
    }

    private Class<?> convertBuiltInToJavaClass(BuiltInType builtin) {
        switch (builtin) {
            case UNKNOWN:
                return Object.class;
            case DATE:
                return LocalDate.class;
            case TIME:
                return LocalTime.class;
            case DATE_TIME:
                return LocalDateTime.class;
            case BOOLEAN:
                return Boolean.class;
            case NUMBER:
                return Number.class;
            case STRING:
                return String.class;
            case DURATION:
            default:
                throw new IllegalArgumentException();
        }
    }

    private static Class<?> convertDurationToJavaClass(DMNType expectedFEELType) {
        switch (expectedFEELType.getName()) {
            case SimpleType.YEARS_AND_MONTHS_DURATION:
            case "yearMonthDuration":
                return Period.class;
            case SimpleType.DAYS_AND_TIME_DURATION:
            case "dayTimeDuration":
                return Duration.class;
            default:
                throw new IllegalArgumentException();
        }
    }
}
