/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.trusty.service.common.messaging;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.kie.kogito.explainability.api.CounterfactualSearchDomainCollectionDto;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainDto;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainStructureDto;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainUnitDto;
import org.kie.kogito.tracing.typedvalue.CollectionValue;
import org.kie.kogito.tracing.typedvalue.StructureValue;
import org.kie.kogito.tracing.typedvalue.TypedValue;
import org.kie.kogito.tracing.typedvalue.UnitValue;
import org.kie.kogito.trusty.storage.api.model.CounterfactualDomain;
import org.kie.kogito.trusty.storage.api.model.CounterfactualDomainCategorical;
import org.kie.kogito.trusty.storage.api.model.CounterfactualDomainRange;
import org.kie.kogito.trusty.storage.api.model.CounterfactualSearchDomain;
import org.kie.kogito.trusty.storage.api.model.TypedVariableWithValue;

public class MessagingUtils {

    private MessagingUtils() {
    }

    /*
     * ================================================
     * TrustyService DTOs to ExplainabilityService DTOs
     * ================================================
     */

    public static TypedValue modelToTracingTypedValue(TypedVariableWithValue value) {
        if (value == null) {
            return null;
        }
        switch (value.getKind()) {
            case UNIT:
                return new UnitValue(value.getTypeRef(), null, value.getValue());
            case COLLECTION:
                return new CollectionValue(value.getTypeRef(), modelToTracingTypedValueCollection(value.getComponents()));
            case STRUCTURE:
                return new StructureValue(value.getTypeRef(), modelToTracingTypedValueMap(value.getComponents()));
        }
        throw new IllegalStateException("Can't convert org.kie.kogito.trusty.storage.api.model.TypedVariable of kind " + value.getKind() + " to TypedValue");
    }

    public static Collection<TypedValue> modelToTracingTypedValueCollection(Collection<TypedVariableWithValue> input) {
        if (input == null) {
            return null;
        }
        return input.stream().map(MessagingUtils::modelToTracingTypedValue).collect(Collectors.toList());
    }

    public static Map<String, TypedValue> modelToTracingTypedValueMap(Collection<TypedVariableWithValue> input) {
        if (input == null) {
            return null;
        }
        return input.stream()
                .filter(m -> m.getName() != null)
                .collect(HashMap::new, (m, v) -> m.put(v.getName(), modelToTracingTypedValue(v)), HashMap::putAll);
    }

    public static CounterfactualSearchDomainDto modelToCounterfactualSearchDomainDto(CounterfactualSearchDomain value) {
        if (value == null) {
            return null;
        }
        switch (value.getKind()) {
            case UNIT:
                return new CounterfactualSearchDomainUnitDto(value.getTypeRef(), value.isFixed(), MessagingUtils.modelToCounterfactualDomain(value.getDomain()));
            case COLLECTION:
                return new CounterfactualSearchDomainCollectionDto(value.getTypeRef(), modelToCounterfactualSearchDomainDtoCollection(value.getComponents()));
            case STRUCTURE:
                return new CounterfactualSearchDomainStructureDto(value.getTypeRef(), modelToCounterfactualSearchDomainDtoMap(value.getComponents()));
        }
        throw new IllegalStateException("Can't convert CounterfactualSearchDomain of kind " + value.getKind() + " to CounterfactualSearchDomainDto");
    }

    private static org.kie.kogito.explainability.api.CounterfactualDomainDto modelToCounterfactualDomain(CounterfactualDomain domain) {
        switch (domain.getType()) {
            case CATEGORICAL:
                CounterfactualDomainCategorical categorical = (CounterfactualDomainCategorical) domain;
                return new org.kie.kogito.explainability.api.CounterfactualDomainCategoricalDto(categorical.getCategories());
            case RANGE:
                CounterfactualDomainRange range = (CounterfactualDomainRange) domain;
                return new org.kie.kogito.explainability.api.CounterfactualDomainRangeDto(range.getLowerBound(), range.getUpperBound());
        }
        throw new IllegalStateException("Can't convert CounterfactualDomain of type " + domain.getType() + " to org.kie.kogito.explainability.api.CounterfactualDomain");
    }

    private static Collection<CounterfactualSearchDomainDto> modelToCounterfactualSearchDomainDtoCollection(Collection<CounterfactualSearchDomain> searchDomains) {
        if (searchDomains == null) {
            return Collections.emptyList();
        }
        return searchDomains.stream().map(MessagingUtils::modelToCounterfactualSearchDomainDto).collect(Collectors.toList());
    }

    private static Map<String, CounterfactualSearchDomainDto> modelToCounterfactualSearchDomainDtoMap(Collection<CounterfactualSearchDomain> searchDomains) {
        if (searchDomains == null) {
            return Collections.emptyMap();
        }
        return searchDomains.stream()
                .filter(m -> m.getName() != null)
                .collect(HashMap::new, (m, v) -> m.put(v.getName(), modelToCounterfactualSearchDomainDto(v)), HashMap::putAll);
    }

    /*
     * ================================================
     * ExplainabilityService DTOs to TrustyService DTOs
     * ================================================
     */

    public static Collection<TypedVariableWithValue> tracingTypedValueToModel(Map<String, TypedValue> typedValues) {
        if (Objects.isNull(typedValues) || typedValues.isEmpty()) {
            return Collections.emptyList();
        }
        return typedValues
                .entrySet()
                .stream()
                .map(MessagingUtils::tracingTypedValueToModel)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }

    public static TypedVariableWithValue tracingTypedValueToModel(Map.Entry<String, TypedValue> entry) {
        if (Objects.isNull(entry)) {
            return null;
        }
        String key = entry.getKey();
        TypedValue value = entry.getValue();
        if (Objects.isNull(value)) {
            return null;
        }
        switch (value.getKind()) {
            case UNIT:
                return TypedVariableWithValue.buildUnit(key, value.getType(), value.toUnit().getValue());
            case COLLECTION:
                return TypedVariableWithValue.buildCollection(key, value.getType(), tracingTypedValueToModelCollection(value.toCollection()));
            case STRUCTURE:
                return TypedVariableWithValue.buildStructure(key, value.getType(), tracingTypedValueToModel(value.toStructure().getValue()));
        }
        throw new IllegalStateException("Can't convert TypedValue of Kind '" + value.getKind() + "' to TypedVariableWithValue");
    }

    private static Collection<TypedVariableWithValue> tracingTypedValueToModelCollection(CollectionValue input) {
        if (Objects.isNull(input)) {
            return Collections.emptyList();
        }
        return input.getValue().stream().map(v -> tracingTypedValueToModel(Map.entry("", v))).collect(Collectors.toList());
    }

}
