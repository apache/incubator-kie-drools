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

import org.kie.kogito.explainability.api.CounterfactualDomainCategoricalDto;
import org.kie.kogito.explainability.api.CounterfactualDomainDto;
import org.kie.kogito.explainability.api.CounterfactualDomainFixedDto;
import org.kie.kogito.explainability.api.CounterfactualDomainRangeDto;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainCollectionDto;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainDto;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainStructureDto;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainUnitDto;
import org.kie.kogito.trusty.storage.api.model.CounterfactualDomain;
import org.kie.kogito.trusty.storage.api.model.CounterfactualDomainCategorical;
import org.kie.kogito.trusty.storage.api.model.CounterfactualDomainFixed;
import org.kie.kogito.trusty.storage.api.model.CounterfactualDomainRange;
import org.kie.kogito.trusty.storage.api.model.CounterfactualSearchDomainCollectionValue;
import org.kie.kogito.trusty.storage.api.model.CounterfactualSearchDomainStructureValue;
import org.kie.kogito.trusty.storage.api.model.CounterfactualSearchDomainUnitValue;
import org.kie.kogito.trusty.storage.api.model.CounterfactualSearchDomainValue;

public class MessagingUtils {

    private MessagingUtils() {
    }

    /*
     * ================================================
     * TrustyService DTOs to ExplainabilityService DTOs
     * ================================================
     */

    public static CounterfactualSearchDomainDto modelToCounterfactualSearchDomainDto(CounterfactualSearchDomainValue value) {
        if (Objects.isNull(value)) {
            return null;
        }
        switch (value.getKind()) {
            case UNIT:
                CounterfactualSearchDomainUnitValue unit = value.toUnit();
                return new CounterfactualSearchDomainUnitDto(unit.getType(), unit.isFixed(), MessagingUtils.modelToCounterfactualDomain(unit.getDomain()));
            case COLLECTION:
                CounterfactualSearchDomainCollectionValue collection = value.toCollection();
                return new CounterfactualSearchDomainCollectionDto(collection.getType(), modelToCounterfactualSearchDomainDtoCollection(collection.getValue()));
            case STRUCTURE:
                CounterfactualSearchDomainStructureValue structure = value.toStructure();
                return new CounterfactualSearchDomainStructureDto(structure.getType(), modelToCounterfactualSearchDomainDtoMap(structure.getValue()));
        }
        throw new IllegalStateException("Can't convert CounterfactualSearchDomain of kind " + value.getKind() + " to CounterfactualSearchDomainDto");
    }

    private static CounterfactualDomainDto modelToCounterfactualDomain(CounterfactualDomain domain) {
        if (Objects.isNull(domain)) {
            return new CounterfactualDomainFixedDto();
        } else if (domain instanceof CounterfactualDomainFixed) {
            return new CounterfactualDomainFixedDto();
        } else if (domain instanceof CounterfactualDomainCategorical) {
            CounterfactualDomainCategorical categorical = (CounterfactualDomainCategorical) domain;
            return new CounterfactualDomainCategoricalDto(categorical.getCategories());
        } else if (domain instanceof CounterfactualDomainRange) {
            CounterfactualDomainRange range = (CounterfactualDomainRange) domain;
            return new CounterfactualDomainRangeDto(range.getLowerBound(), range.getUpperBound());
        }
        throw new IllegalStateException("Can't convert CounterfactualDomain of type '" + domain.getClass().getName() + "' to org.kie.kogito.explainability.api.CounterfactualDomain");
    }

    private static Collection<CounterfactualSearchDomainDto> modelToCounterfactualSearchDomainDtoCollection(Collection<CounterfactualSearchDomainValue> searchDomains) {
        if (Objects.isNull(searchDomains)) {
            return Collections.emptyList();
        }
        return searchDomains.stream().map(MessagingUtils::modelToCounterfactualSearchDomainDto).collect(Collectors.toList());
    }

    private static Map<String, CounterfactualSearchDomainDto> modelToCounterfactualSearchDomainDtoMap(Map<String, CounterfactualSearchDomainValue> searchDomains) {
        if (Objects.isNull(searchDomains)) {
            return Collections.emptyMap();
        }
        return searchDomains.entrySet().stream()
                .filter(m -> m.getKey() != null)
                .collect(HashMap::new, (m, v) -> m.put(v.getKey(), modelToCounterfactualSearchDomainDto(v.getValue())), HashMap::putAll);
    }
}
