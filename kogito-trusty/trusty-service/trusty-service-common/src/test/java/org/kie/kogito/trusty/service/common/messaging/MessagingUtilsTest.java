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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.api.CounterfactualDomainCategoricalDto;
import org.kie.kogito.explainability.api.CounterfactualDomainFixedDto;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
                null,
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
                null,
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
    void modelToCounterfactualSearchDomainDto_Unit_NullDomain_FAI_509() throws JsonProcessingException {
        //See https://issues.redhat.com/browse/FAI-509
        String request = "{\n" +
                "      \"kind\": \"UNIT\",\n" +
                "      \"name\": \"hasReferral\",\n" +
                "      \"typeRef\": \"boolean\",\n" +
                "      \"components\": null,\n" +
                "      \"value\": false,\n" +
                "      \"fixed\": false\n" +
                "    }";
        ObjectMapper mapper = new ObjectMapper();
        CounterfactualSearchDomain searchDomain = mapper.readValue(request, CounterfactualSearchDomain.class);

        CounterfactualSearchDomainDto searchDomainDto = MessagingUtils.modelToCounterfactualSearchDomainDto(searchDomain);
        assertNotNull(searchDomainDto);
        assertEquals(CounterfactualSearchDomainDto.Kind.UNIT, searchDomainDto.getKind());
        assertEquals("boolean", searchDomainDto.getType());
        assertTrue(searchDomainDto instanceof CounterfactualSearchDomainUnitDto);

        CounterfactualSearchDomainUnitDto unit = (CounterfactualSearchDomainUnitDto) searchDomainDto;
        assertTrue(unit.isUnit());
        assertFalse(unit.isCollection());
        assertFalse(unit.isStructure());
        assertFalse(unit.isFixed());
        assertNotNull(unit.getDomain());
        assertTrue(unit.getDomain() instanceof CounterfactualDomainFixedDto);
    }

    @Test
    void modelToCounterfactualSearchDomainDto_Collection() {
        CounterfactualSearchDomain searchDomain = new CounterfactualSearchDomain(TypedValue.Kind.COLLECTION,
                "weights",
                "collection",
                List.of(new CounterfactualSearchDomain(TypedValue.Kind.UNIT,
                        "weight",
                        "integer",
                        null,
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
                        null,
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

    @Test
    void modelToCounterfactualSearchDomainDto_Unit_nullDomain() {
        CounterfactualSearchDomain searchDomain = new CounterfactualSearchDomain(TypedValue.Kind.UNIT,
                "name",
                "string",
                null,
                Boolean.TRUE,
                null);

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
        assertTrue(unit.getDomain() instanceof CounterfactualDomainFixedDto);
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    void tracingTypedValueToModelWhenMapIsNull() {
        assertTrue(MessagingUtils.tracingTypedValueToModel((Map) null).isEmpty());
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    void tracingTypedValueToModelWhenMapEntryIsNull() {
        assertNull(MessagingUtils.tracingTypedValueToModel((Map.Entry) null));
    }

    @Test
    void tracingTypedValueToModelWhenMapEntryValueIsNull() {
        //Map.entry(k, v) does not accept null values so use the long route
        Map<String, TypedValue> map = new HashMap<>();
        map.put("key", null);
        Map.Entry<String, TypedValue> entry = map.entrySet().iterator().next();

        assertNull(MessagingUtils.tracingTypedValueToModel(entry));
    }

    @Test
    void modelToTracingTypedValueRoundTrip() {
        TypedValue typedValue = MessagingUtils.modelToTracingTypedValue(typedVariable);

        TypedVariableWithValue result = MessagingUtils.tracingTypedValueToModel(Map.entry("name", typedValue));
        assertNotNull(result);
        assertEquals(typedVariable.getName(), result.getName());
        assertEquals(typedVariable.getKind(), result.getKind());
        assertEquals(typedVariable.getTypeRef(), result.getTypeRef());
        assertEquals(typedVariable.getValue(), result.getValue());
        assertNull(result.getComponents());
    }

    @Test
    void modelToTracingTypedValueCollectionRoundTrip() {
        TypedVariableWithValue collectionItem1 = TypedVariableWithValue.buildUnit("item1", "string", new TextNode("sample1"));
        TypedVariableWithValue collectionItem2 = TypedVariableWithValue.buildUnit("item2", "number", new IntNode(123));

        TypedVariableWithValue typedVariableWithCollection = TypedVariableWithValue.buildCollection("collection",
                "object",
                List.of(collectionItem1, collectionItem2));

        TypedValue typedValue = MessagingUtils.modelToTracingTypedValue(typedVariableWithCollection);

        TypedVariableWithValue result = MessagingUtils.tracingTypedValueToModel(Map.entry("collection", typedValue));
        assertNotNull(result);
        assertEquals(typedVariableWithCollection.getName(), result.getName());
        assertEquals(typedVariableWithCollection.getKind(), result.getKind());
        assertEquals(typedVariableWithCollection.getTypeRef(), result.getTypeRef());
        assertEquals(typedVariableWithCollection.getValue(), result.getValue());
        assertNotNull(result.getComponents());
        assertEquals(2, result.getComponents().size());

        Iterator<TypedVariableWithValue> resultComponentIterator = result.getComponents().iterator();
        TypedVariableWithValue resultComponent1 = resultComponentIterator.next();
        assertNotNull(resultComponent1);
        //Names are not converted to TypedValue and hence are not available on the return for collections
        assertEquals("", resultComponent1.getName());
        assertEquals(collectionItem1.getKind(), resultComponent1.getKind());
        assertEquals(collectionItem1.getTypeRef(), resultComponent1.getTypeRef());
        assertEquals(collectionItem1.getValue(), resultComponent1.getValue());
        assertNull(resultComponent1.getComponents());

        TypedVariableWithValue resultComponent2 = resultComponentIterator.next();
        assertNotNull(resultComponent2);
        //Names are not converted to TypedValue and hence are not available on the return for collections
        assertEquals("", resultComponent2.getName());
        assertEquals(collectionItem2.getKind(), resultComponent2.getKind());
        assertEquals(collectionItem2.getTypeRef(), resultComponent2.getTypeRef());
        assertEquals(collectionItem2.getValue(), resultComponent2.getValue());
        assertNull(resultComponent2.getComponents());
    }

    @Test
    void modelToTracingTypedValueStructureRoundTrip() {
        TypedVariableWithValue structureItem1 = TypedVariableWithValue.buildUnit("child1", "string", new TextNode("sample1"));
        TypedVariableWithValue structureItem2 = TypedVariableWithValue.buildUnit("child2", "number", new IntNode(123));
        TypedVariableWithValue typedVariableWithStructure = TypedVariableWithValue.buildStructure("structure",
                "object",
                List.of(structureItem1, structureItem2));

        TypedValue typedValue = MessagingUtils.modelToTracingTypedValue(typedVariableWithStructure);

        TypedVariableWithValue result = MessagingUtils.tracingTypedValueToModel(Map.entry("structure", typedValue));
        assertNotNull(result);
        assertEquals(typedVariableWithStructure.getName(), result.getName());
        assertEquals(typedVariableWithStructure.getKind(), result.getKind());
        assertEquals(typedVariableWithStructure.getTypeRef(), result.getTypeRef());
        assertEquals(typedVariableWithStructure.getValue(), result.getValue());
        assertNotNull(result.getComponents());
        assertEquals(2, result.getComponents().size());

        //TypedValue stores children in a Map. The original order of the children is therefore not preserved on the return for structures
        Optional<TypedVariableWithValue> oResultComponent1 = result.getComponents().stream().filter(c -> c.getName().equals("child1")).findFirst();
        assertTrue(oResultComponent1.isPresent());
        TypedVariableWithValue resultComponent1 = oResultComponent1.get();
        assertEquals(structureItem1.getName(), resultComponent1.getName());
        assertEquals(structureItem1.getKind(), resultComponent1.getKind());
        assertEquals(structureItem1.getTypeRef(), resultComponent1.getTypeRef());
        assertEquals(structureItem1.getValue(), resultComponent1.getValue());
        assertNull(resultComponent1.getComponents());

        //TypedValue stores children in a Map. The original order of the children is therefore not preserved on the return for structures
        Optional<TypedVariableWithValue> oResultComponent2 = result.getComponents().stream().filter(c -> c.getName().equals("child2")).findFirst();
        assertTrue(oResultComponent2.isPresent());
        TypedVariableWithValue resultComponent2 = oResultComponent2.get();
        assertEquals(structureItem2.getName(), resultComponent2.getName());
        assertEquals(structureItem2.getKind(), resultComponent2.getKind());
        assertEquals(structureItem2.getTypeRef(), resultComponent2.getTypeRef());
        assertEquals(structureItem2.getValue(), resultComponent2.getValue());
        assertNull(resultComponent2.getComponents());
    }

    @Test
    void modelToTracingTypedValueStructureComplexRoundTrip() {
        TypedVariableWithValue structureItem1 = TypedVariableWithValue.buildUnit("child1", "string", new TextNode("sample1"));

        TypedVariableWithValue collectionItem1 = TypedVariableWithValue.buildUnit("item1", "string", new TextNode("sample1"));
        TypedVariableWithValue collectionItem2 = TypedVariableWithValue.buildUnit("item2", "number", new IntNode(123));

        TypedVariableWithValue typedVariableWithCollection = TypedVariableWithValue.buildCollection("collection",
                "object",
                List.of(collectionItem1, collectionItem2));

        TypedVariableWithValue structureItem2 = TypedVariableWithValue.buildStructure("child2", "number", List.of(typedVariableWithCollection));
        TypedVariableWithValue typedVariableWithStructure = TypedVariableWithValue.buildStructure("structure",
                "object",
                List.of(structureItem1, structureItem2));

        TypedValue typedValue = MessagingUtils.modelToTracingTypedValue(typedVariableWithStructure);

        TypedVariableWithValue result = MessagingUtils.tracingTypedValueToModel(Map.entry("structure", typedValue));
        assertNotNull(result);
        assertEquals(typedVariableWithStructure.getName(), result.getName());
        assertEquals(typedVariableWithStructure.getKind(), result.getKind());
        assertEquals(typedVariableWithStructure.getTypeRef(), result.getTypeRef());
        assertEquals(typedVariableWithStructure.getValue(), result.getValue());
        assertNotNull(result.getComponents());
        assertEquals(2, result.getComponents().size());

        //TypedValue stores children in a Map. The original order of the children is therefore not preserved on the return for structures
        Optional<TypedVariableWithValue> oResultComponent1 = result.getComponents().stream().filter(c -> c.getName().equals("child1")).findFirst();
        assertTrue(oResultComponent1.isPresent());
        TypedVariableWithValue resultComponent1 = oResultComponent1.get();
        assertEquals(structureItem1.getName(), resultComponent1.getName());
        assertEquals(structureItem1.getKind(), resultComponent1.getKind());
        assertEquals(structureItem1.getTypeRef(), resultComponent1.getTypeRef());
        assertEquals(structureItem1.getValue(), resultComponent1.getValue());
        assertNull(resultComponent1.getComponents());

        //TypedValue stores children in a Map. The original order of the children is therefore not preserved on the return for structures
        Optional<TypedVariableWithValue> oResultComponent2 = result.getComponents().stream().filter(c -> c.getName().equals("child2")).findFirst();
        assertTrue(oResultComponent2.isPresent());
        TypedVariableWithValue resultComponent2 = oResultComponent2.get();
        assertEquals(structureItem2.getName(), resultComponent2.getName());
        assertEquals(structureItem2.getKind(), resultComponent2.getKind());
        assertEquals(structureItem2.getTypeRef(), resultComponent2.getTypeRef());
        assertEquals(structureItem2.getValue(), resultComponent2.getValue());
        assertNotNull(resultComponent2.getComponents());
        assertEquals(1, resultComponent2.getComponents().size());

        TypedVariableWithValue resultComponent2Child1 = resultComponent2.getComponents().iterator().next();
        assertEquals(typedVariableWithCollection.getName(), resultComponent2Child1.getName());
        assertEquals(typedVariableWithCollection.getKind(), resultComponent2Child1.getKind());
        assertEquals(typedVariableWithCollection.getTypeRef(), resultComponent2Child1.getTypeRef());
        assertEquals(typedVariableWithCollection.getValue(), resultComponent2Child1.getValue());
        assertNotNull(resultComponent2Child1.getComponents());
        assertEquals(2, resultComponent2Child1.getComponents().size());

        Iterator<TypedVariableWithValue> resultComponent2Child1Iterator = resultComponent2Child1.getComponents().iterator();
        TypedVariableWithValue resultComponent2Child1Sibling1 = resultComponent2Child1Iterator.next();
        assertNotNull(resultComponent2Child1Sibling1);
        //Names are not converted to TypedValue and hence are not available on the return for collections
        assertEquals("", resultComponent2Child1Sibling1.getName());
        assertEquals(collectionItem1.getKind(), resultComponent2Child1Sibling1.getKind());
        assertEquals(collectionItem1.getTypeRef(), resultComponent2Child1Sibling1.getTypeRef());
        assertEquals(collectionItem1.getValue(), resultComponent2Child1Sibling1.getValue());
        assertNull(resultComponent2Child1Sibling1.getComponents());

        TypedVariableWithValue resultComponent2Child1Sibling2 = resultComponent2Child1Iterator.next();
        assertNotNull(resultComponent2Child1Sibling2);
        //Names are not converted to TypedValue and hence are not available on the return for collections
        assertEquals("", resultComponent2Child1Sibling2.getName());
        assertEquals(collectionItem2.getKind(), resultComponent2Child1Sibling2.getKind());
        assertEquals(collectionItem2.getTypeRef(), resultComponent2Child1Sibling2.getTypeRef());
        assertEquals(collectionItem2.getValue(), resultComponent2Child1Sibling2.getValue());
        assertNull(resultComponent2Child1Sibling2.getComponents());
    }

}
