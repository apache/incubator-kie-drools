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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.api.CounterfactualDomainCategoricalDto;
import org.kie.kogito.explainability.api.CounterfactualDomainRangeDto;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainCollectionDto;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainDto;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainStructureDto;
import org.kie.kogito.explainability.api.CounterfactualSearchDomainUnitDto;
import org.kie.kogito.tracing.typedvalue.TypedValue;
import org.kie.kogito.tracing.typedvalue.UnitValue;
import org.kie.kogito.trusty.storage.api.model.CounterfactualDomainCategorical;
import org.kie.kogito.trusty.storage.api.model.CounterfactualDomainRange;
import org.kie.kogito.trusty.storage.api.model.CounterfactualSearchDomain;
import org.kie.kogito.trusty.storage.api.model.TypedVariableWithValue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessagingUtilsTest {

    TypedVariableWithValue typedVariable = new TypedVariableWithValue(TypedValue.Kind.UNIT, "name", "string", new TextNode("sample"), emptyList());

    @Test
    void modelToTracingTypedValue() {
        assertNull(MessagingUtils.modelToTracingTypedValue(null));

        TypedValue typedValue = MessagingUtils.modelToTracingTypedValue(typedVariable);
        assertNotNull(typedValue);
        assertTrue(typedValue instanceof UnitValue);
    }

    @Test
    void modelToTracingTypedValueCollection() {
        assertNull(MessagingUtils.modelToTracingTypedValueCollection(null));

        Collection<TypedValue> typedValues = MessagingUtils.modelToTracingTypedValueCollection(singletonList(typedVariable));
        assertNotNull(typedValues);
        assertEquals(1, typedValues.size());
        assertTrue(typedValues.iterator().next() instanceof UnitValue);
    }

    @Test
    void modelToTracingTypedValueMap() {
        assertNull(MessagingUtils.modelToTracingTypedValueMap(null));

        Map<String, TypedValue> valueMap = MessagingUtils.modelToTracingTypedValueMap(singletonList(typedVariable));
        assertNotNull(valueMap);
        assertEquals(1, valueMap.size());
        assertTrue(valueMap.containsKey("name"));
        assertTrue(valueMap.get("name") instanceof UnitValue);
    }

    @Test
    void modelToCounterfactualSearchDomainDto_null() {
        assertNull(MessagingUtils.modelToCounterfactualSearchDomainDto(null));
    }

    @Test
    void modelToCounterfactualSearchDomainDto_Unit_Categorical() {
        CounterfactualSearchDomain searchDomain = new CounterfactualSearchDomain(TypedValue.Kind.UNIT,
                "name",
                "string",
                emptyList(),
                Boolean.TRUE,
                new CounterfactualDomainCategorical(asList(new TextNode("A"), new TextNode("B"))));

        CounterfactualSearchDomainDto searchDomainDto = MessagingUtils.modelToCounterfactualSearchDomainDto(searchDomain);
        assertNotNull(searchDomainDto);
        assertEquals(CounterfactualSearchDomainDto.Kind.UNIT, searchDomainDto.getKind());
        assertEquals("string", searchDomainDto.getType());
        assertTrue(searchDomainDto instanceof CounterfactualSearchDomainUnitDto);

        CounterfactualSearchDomainUnitDto unit = (CounterfactualSearchDomainUnitDto) searchDomainDto;
        assertTrue(unit.isUnit());
        assertFalse(unit.isCollection());
        assertFalse(unit.isStructure());
        assertTrue(unit.isFixed());
        assertNotNull(unit.getDomain());
        assertTrue(unit.getDomain() instanceof CounterfactualDomainCategoricalDto);

        CounterfactualDomainCategoricalDto categorical = (CounterfactualDomainCategoricalDto) unit.getDomain();
        assertEquals(2, categorical.getCategories().size());
        assertTrue(categorical.getCategories().stream().map(JsonNode::asText).collect(Collectors.toList()).contains("A"));
        assertTrue(categorical.getCategories().stream().map(JsonNode::asText).collect(Collectors.toList()).contains("B"));
    }

    @Test
    void modelToCounterfactualSearchDomainDto_Unit_Range() {
        CounterfactualSearchDomain searchDomain = new CounterfactualSearchDomain(TypedValue.Kind.UNIT,
                "age",
                "integer",
                emptyList(),
                Boolean.TRUE,
                new CounterfactualDomainRange(new IntNode(18), new IntNode(65)));

        CounterfactualSearchDomainDto searchDomainDto = MessagingUtils.modelToCounterfactualSearchDomainDto(searchDomain);
        assertNotNull(searchDomainDto);
        assertEquals(CounterfactualSearchDomainDto.Kind.UNIT, searchDomainDto.getKind());
        assertEquals("integer", searchDomainDto.getType());
        assertTrue(searchDomainDto instanceof CounterfactualSearchDomainUnitDto);

        CounterfactualSearchDomainUnitDto unit = (CounterfactualSearchDomainUnitDto) searchDomainDto;
        assertTrue(unit.isUnit());
        assertFalse(unit.isCollection());
        assertFalse(unit.isStructure());
        assertTrue(unit.isFixed());
        assertNotNull(unit.getDomain());
        assertTrue(unit.getDomain() instanceof CounterfactualDomainRangeDto);

        CounterfactualDomainRangeDto range = (CounterfactualDomainRangeDto) unit.getDomain();
        assertEquals(18, range.getLowerBound().asInt());
        assertEquals(65, range.getUpperBound().asInt());
    }

    @Test
    void modelToCounterfactualSearchDomainDto_Collection() {
        CounterfactualSearchDomain searchDomain = new CounterfactualSearchDomain(TypedValue.Kind.COLLECTION,
                "weights",
                "collection",
                List.of(new CounterfactualSearchDomain(TypedValue.Kind.UNIT,
                        "weight",
                        "integer",
                        Collections.emptyList(),
                        Boolean.TRUE,
                        new CounterfactualDomainRange(new IntNode(10), new IntNode(20)))),
                Boolean.TRUE,
                new CounterfactualDomainRange(new IntNode(18), new IntNode(65)));

        CounterfactualSearchDomainDto searchDomainDto = MessagingUtils.modelToCounterfactualSearchDomainDto(searchDomain);
        assertNotNull(searchDomainDto);
        assertEquals(CounterfactualSearchDomainDto.Kind.COLLECTION, searchDomainDto.getKind());
        assertEquals("collection", searchDomainDto.getType());
        assertTrue(searchDomainDto instanceof CounterfactualSearchDomainCollectionDto);

        CounterfactualSearchDomainCollectionDto collection = (CounterfactualSearchDomainCollectionDto) searchDomainDto;
        assertFalse(collection.isUnit());
        assertTrue(collection.isCollection());
        assertFalse(collection.isStructure());

        assertEquals(1, collection.getValue().size());
        CounterfactualSearchDomainDto item0 = collection.getValue().iterator().next();
        assertTrue(item0 instanceof CounterfactualSearchDomainUnitDto);

        CounterfactualSearchDomainUnitDto unit = (CounterfactualSearchDomainUnitDto) item0;
        assertTrue(unit.isFixed());
        assertNotNull(unit.getDomain());
        assertTrue(unit.getDomain() instanceof CounterfactualDomainRangeDto);

        CounterfactualDomainRangeDto range = (CounterfactualDomainRangeDto) unit.getDomain();
        assertEquals(10, range.getLowerBound().asInt());
        assertEquals(20, range.getUpperBound().asInt());
    }

    @Test
    void modelToCounterfactualSearchDomainDto_Structure() {
        CounterfactualSearchDomain searchDomain = new CounterfactualSearchDomain(TypedValue.Kind.STRUCTURE,
                "employee",
                "Employee",
                List.of(new CounterfactualSearchDomain(TypedValue.Kind.UNIT,
                        "salary",
                        "integer",
                        Collections.emptyList(),
                        Boolean.TRUE,
                        new CounterfactualDomainRange(new IntNode(1000), new IntNode(2000)))),
                Boolean.TRUE,
                new CounterfactualDomainRange(new IntNode(18), new IntNode(65)));

        CounterfactualSearchDomainDto searchDomainDto = MessagingUtils.modelToCounterfactualSearchDomainDto(searchDomain);
        assertNotNull(searchDomainDto);
        assertEquals(CounterfactualSearchDomainDto.Kind.STRUCTURE, searchDomainDto.getKind());
        assertEquals("Employee", searchDomainDto.getType());
        assertTrue(searchDomainDto instanceof CounterfactualSearchDomainStructureDto);

        CounterfactualSearchDomainStructureDto structure = (CounterfactualSearchDomainStructureDto) searchDomainDto;
        assertFalse(structure.isUnit());
        assertFalse(structure.isCollection());
        assertTrue(structure.isStructure());

        assertEquals(1, structure.getValue().size());
        assertTrue(structure.getValue().containsKey("salary"));
        CounterfactualSearchDomainDto salary = structure.getValue().get("salary");
        assertTrue(salary instanceof CounterfactualSearchDomainUnitDto);

        CounterfactualSearchDomainUnitDto unit = (CounterfactualSearchDomainUnitDto) salary;
        assertTrue(unit.isFixed());
        assertNotNull(unit.getDomain());
        assertTrue(unit.getDomain() instanceof CounterfactualDomainRangeDto);

        CounterfactualDomainRangeDto range = (CounterfactualDomainRangeDto) unit.getDomain();
        assertEquals(1000, range.getLowerBound().asInt());
        assertEquals(2000, range.getUpperBound().asInt());
    }
}
