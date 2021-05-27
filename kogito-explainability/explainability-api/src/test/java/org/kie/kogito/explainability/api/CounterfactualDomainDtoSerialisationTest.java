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
package org.kie.kogito.explainability.api;

import java.io.StringWriter;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CounterfactualDomainDtoSerialisationTest {

    private ObjectMapper mapper;
    private StringWriter writer;

    @BeforeEach
    public void setup() {
        this.mapper = new ObjectMapper();
        this.writer = new StringWriter();
    }

    @Test
    public void testCounterfactualSearchDomain_Range_RoundTrip() throws Exception {
        CounterfactualDomainRangeDto domainRangeDto = new CounterfactualDomainRangeDto(new IntNode(18), new IntNode(65));
        CounterfactualSearchDomainUnitDto searchDomainDto = new CounterfactualSearchDomainUnitDto("integer",
                Boolean.TRUE,
                domainRangeDto);

        mapper.writeValue(writer, searchDomainDto);
        String searchDomainJson = writer.toString();
        assertNotNull(searchDomainJson);

        CounterfactualSearchDomainDto roundTrippedSearchDomainDto = mapper.readValue(searchDomainJson, CounterfactualSearchDomainDto.class);
        assertTrue(roundTrippedSearchDomainDto instanceof CounterfactualSearchDomainUnitDto);
        CounterfactualSearchDomainUnitDto roundTrippedSearchDomainUnitDto = (CounterfactualSearchDomainUnitDto) roundTrippedSearchDomainDto;

        assertEquals(searchDomainDto.getKind(), roundTrippedSearchDomainUnitDto.getKind());
        assertEquals(searchDomainDto.getType(), roundTrippedSearchDomainUnitDto.getType());
        assertEquals(searchDomainDto.isFixed(), roundTrippedSearchDomainUnitDto.isFixed());
        assertTrue(roundTrippedSearchDomainUnitDto.getDomain() instanceof CounterfactualDomainRangeDto);

        CounterfactualDomainRangeDto roundTrippedDomainRangeDto = (CounterfactualDomainRangeDto) roundTrippedSearchDomainUnitDto.getDomain();
        assertEquals(domainRangeDto.getLowerBound(), roundTrippedDomainRangeDto.getLowerBound());
        assertEquals(domainRangeDto.getUpperBound(), roundTrippedDomainRangeDto.getUpperBound());
    }

    @Test
    public void testCounterfactualSearchDomain_Categorical_RoundTrip() throws Exception {
        CounterfactualDomainCategoricalDto domainCategoricalDto = new CounterfactualDomainCategoricalDto(List.of(new TextNode("A"), new TextNode("B")));
        CounterfactualSearchDomainUnitDto searchDomainDto = new CounterfactualSearchDomainUnitDto("integer",
                Boolean.TRUE,
                domainCategoricalDto);

        mapper.writeValue(writer, searchDomainDto);
        String searchDomainJson = writer.toString();
        assertNotNull(searchDomainJson);

        CounterfactualSearchDomainDto roundTrippedSearchDomainDto = mapper.readValue(searchDomainJson, CounterfactualSearchDomainDto.class);
        assertTrue(roundTrippedSearchDomainDto instanceof CounterfactualSearchDomainUnitDto);
        CounterfactualSearchDomainUnitDto roundTrippedSearchDomainUnitDto = (CounterfactualSearchDomainUnitDto) roundTrippedSearchDomainDto;

        assertEquals(searchDomainDto.getKind(), roundTrippedSearchDomainUnitDto.getKind());
        assertEquals(searchDomainDto.getType(), roundTrippedSearchDomainUnitDto.getType());
        assertEquals(searchDomainDto.isFixed(), roundTrippedSearchDomainUnitDto.isFixed());
        assertTrue(roundTrippedSearchDomainUnitDto.getDomain() instanceof CounterfactualDomainCategoricalDto);

        CounterfactualDomainCategoricalDto roundTrippedDomainCategoricalDto = (CounterfactualDomainCategoricalDto) roundTrippedSearchDomainUnitDto.getDomain();
        assertEquals(domainCategoricalDto.getCategories().size(), roundTrippedDomainCategoricalDto.getCategories().size());
        assertTrue(roundTrippedDomainCategoricalDto.getCategories().containsAll(domainCategoricalDto.getCategories()));
    }

}
