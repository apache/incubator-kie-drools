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

import java.util.List;
import java.util.Map;
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
import org.kie.kogito.trusty.storage.api.model.CounterfactualSearchDomainCollectionValue;
import org.kie.kogito.trusty.storage.api.model.CounterfactualSearchDomainStructureValue;
import org.kie.kogito.trusty.storage.api.model.CounterfactualSearchDomainUnitValue;
import org.kie.kogito.trusty.storage.api.model.CounterfactualSearchDomainValue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessagingUtilsTest {

    TypedValue typedVariable = new UnitValue("string", "string", new TextNode("sample"));

    @Test
    void modelToCounterfactualSearchDomainDto_null() {
        assertNull(MessagingUtils.modelToCounterfactualSearchDomainDto(null));
    }

    @Test
    void modelToCounterfactualSearchDomainDto_Unit_Categorical() {
        CounterfactualSearchDomainValue searchDomain = new CounterfactualSearchDomainUnitValue("string",
                "string",
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
        CounterfactualSearchDomainValue searchDomain = new CounterfactualSearchDomainUnitValue("integer",
                "integer",
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
                "      \"type\": \"boolean\",\n" +
                "      \"baseType\": \"boolean\",\n" +
                "      \"value\": false,\n" +
                "      \"fixed\": false\n" +
                "    }";
        ObjectMapper mapper = new ObjectMapper();
        CounterfactualSearchDomainValue searchDomain = mapper.readValue(request, CounterfactualSearchDomainValue.class);

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
        CounterfactualSearchDomainValue searchDomain = new CounterfactualSearchDomainCollectionValue("collection",
                List.of(new CounterfactualSearchDomainUnitValue("integer",
                        "integer",
                        Boolean.TRUE,
                        new CounterfactualDomainRange(new IntNode(10), new IntNode(20)))));

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
        CounterfactualSearchDomainValue searchDomain = new CounterfactualSearchDomainStructureValue("Employee",
                Map.of("salary",
                        new CounterfactualSearchDomainUnitValue("integer",
                                "integer",
                                Boolean.TRUE,
                                new CounterfactualDomainRange(new IntNode(1000), new IntNode(2000)))));

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
        CounterfactualSearchDomainValue searchDomain = new CounterfactualSearchDomainUnitValue("string",
                "string",
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

}
