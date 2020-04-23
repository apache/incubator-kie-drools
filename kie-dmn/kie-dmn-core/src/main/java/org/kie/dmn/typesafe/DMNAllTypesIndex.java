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

import org.drools.core.util.StringUtils;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.BaseDMNTypeImpl;
import org.kie.dmn.feel.codegen.feel11.CodegenStringUtil;
import org.kie.dmn.feel.lang.SimpleType;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;

public class DMNAllTypesIndex {

    private final List<DMNType> indexedTypes = new ArrayList<>();

    Map<DMNTypeKey, DMNModelTypesIndex.IndexValue> mapNamespaceIndex = new HashMap<>();

    public DMNAllTypesIndex(DMNTypeSafePackageName.Factory packageName, DMNModel... allModels) {
        for (DMNModel m : allModels) {
            DMNModelTypesIndex indexFromModel = new DMNModelTypesIndex(m, packageName);
            mapNamespaceIndex.putAll(indexFromModel.getIndex());
            allTypesToGenerate().addAll(indexFromModel.getTypesToGenerate());
        }
    }

    public List<DMNType> allTypesToGenerate() {
        return indexedTypes;
    }

    @Deprecated
    public Optional<DMNTypeSafePackageName> namespaceOfClass(String typeName) {
        return mapNamespaceIndex.entrySet().stream()
                                .filter(kv -> kv.getKey().name.equals(typeName))
                                .findFirst()
                                .map(Entry::getValue)
                                .map(DMNModelTypesIndex.IndexValue::getPackageName);
    }

    @Deprecated
    public boolean isIndexedClass(String typeName) {
        return namespaceOfClass(typeName).isPresent();
    }

    public String asJava(DMNType fieldDMNType) {
        String converted = converDMNTypeAsJava(fieldDMNType);
        return converted;
    }

    public String converDMNTypeAsJava(DMNType dmnType) {
        if (mapNamespaceIndex.containsKey(DMNTypeKey.from(dmnType))) {
            String simpleName = StringUtils.ucFirst(CodegenStringUtil.escapeIdentifier(dmnType.getName()));
            return mapNamespaceIndex.get(DMNTypeKey.from(dmnType)).getPackageName().appendPackage(simpleName);
        }
        if (dmnType.getNamespace().contains("FEEL")) { // TODO
            return convertBuiltin(dmnType);
        }
        if (dmnType.getBaseType() == null) {
            throw new IllegalStateException();
        }
        String baseConverted = converDMNTypeAsJava(dmnType.getBaseType());
        if (dmnType.isCollection()) {
            return String.format("java.util.Collection<%s>", baseConverted);
        }
        return baseConverted;
    }

    private String convertBuiltin(DMNType expectedFEELType) {
        Type feelType = ((BaseDMNTypeImpl) expectedFEELType).getFeelType();
        BuiltInType builtin;
        try {
            builtin = (BuiltInType) feelType;
        } catch (ClassCastException e) {
            throw new IllegalArgumentException();
        }
        Class<?> convertedClass = Object.class;
        switch (builtin) {
            case DATE:
                convertedClass = LocalDate.class;
                break;
            case TIME:
                convertedClass = LocalTime.class;
                break;
            case DATE_TIME:
                convertedClass = LocalDateTime.class;
                break;
            case DURATION:
                switch (expectedFEELType.getName()) {
                    case SimpleType.YEARS_AND_MONTHS_DURATION:
                    case "yearMonthDuration":
                        convertedClass = Period.class;
                        break;
                    case SimpleType.DAYS_AND_TIME_DURATION:
                    case "dayTimeDuration":
                        convertedClass = Duration.class;
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
                break;
            case BOOLEAN:
                convertedClass = Boolean.class;
                break;
            case NUMBER:
                convertedClass = Number.class;
                break;
            case STRING:
                convertedClass = String.class;
                break;
            default:
                convertedClass = Object.class;
        }
        return convertedClass.getCanonicalName();
    }
}
