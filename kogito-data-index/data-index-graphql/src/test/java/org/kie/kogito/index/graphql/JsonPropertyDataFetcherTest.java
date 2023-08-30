/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.graphql;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import graphql.language.Field;
import graphql.schema.DataFetchingEnvironment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JsonPropertyDataFetcherTest {

    ObjectMapper objectMapper = new ObjectMapper();
    JsonPropertyDataFetcher jsonPropertyDataFetcher = new JsonPropertyDataFetcher();

    @Test
    void testBasicTypesDataFetcher() {
        JsonNode addressJson = createTestObjectNodeJson("city1", 8, true, null);
        assertThat(jsonPropertyDataFetcher.get(getMockEnv("city", addressJson))).isEqualTo("city1");
        assertThat(jsonPropertyDataFetcher.get(getMockEnv("amount", addressJson))).isEqualTo(8);
        assertThat(jsonPropertyDataFetcher.get(getMockEnv("approved", addressJson))).isEqualTo(true);
        assertThat(jsonPropertyDataFetcher.get(getMockEnv("zipCode", addressJson))).isNull();
    }

    @Test
    void testArraysTypesDataFetcher() {
        JsonNode arraysJson = getArraysNodeJson();
        assertThat(((ArrayNode) jsonPropertyDataFetcher.get(getMockEnv("addressArray", arraysJson))).size()).isEqualTo(2);
        List stringArray = ((List) jsonPropertyDataFetcher.get(getMockEnv("stringArray", arraysJson)));
        assertThat(stringArray).hasSize(2);
        assertThat(stringArray.get(0)).isEqualTo("st1");
        assertThat(stringArray.get(1)).isEqualTo("st2");
        List numberArray = ((List) jsonPropertyDataFetcher.get(getMockEnv("numberArray", arraysJson)));
        assertThat(numberArray).hasSize(2);
        assertThat(numberArray.get(0)).isEqualTo(8);
        assertThat(numberArray.get(1)).isEqualTo(98.6);
        List booleanArray = ((List) jsonPropertyDataFetcher.get(getMockEnv("booleanArray", arraysJson)));
        assertThat(booleanArray).hasSize(2);
        assertThat(booleanArray.get(0)).isEqualTo(true);
        assertThat(booleanArray.get(1)).isEqualTo(false);
    }

    private DataFetchingEnvironment getMockEnv(String fieldName, JsonNode jsonNode) {
        DataFetchingEnvironment env = mock(DataFetchingEnvironment.class);
        Field mockField = mock(Field.class);
        when(mockField.getName()).thenReturn(fieldName);
        when(env.getField()).thenReturn(mockField);
        when(env.getSource()).thenReturn(jsonNode);
        return env;
    }

    private JsonNode createTestObjectNodeJson(String city, Integer amount, Boolean approved, String zipCode) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("city", city);
        objectNode.put("amount", amount);
        objectNode.put("approved", approved);
        objectNode.put("zipCode", zipCode);
        return objectNode;
    }

    private JsonNode getArraysNodeJson() {
        // {"addressArray": [
        //      {"city":"city1",
        //       "amount":9,
        //       "approved":true,
        //       "zipCode":"zipCode1"
        //       },
        //       {"city":"city2",
        //       "amount":97,
        //       "approved":false,
        //       "zipCode":"zipCode2"}
        //       ],
        //   "stringArray":["st1","st2"],
        //   "numberArray":[8,98.6],
        //   "booleanArray":[true,false]
        //   }
        ObjectNode objectNode = objectMapper.createObjectNode();
        ArrayNode addressesArrayNode = objectMapper.createArrayNode();
        addressesArrayNode.add(createTestObjectNodeJson("city1", 9, true, "zipCode1"));
        addressesArrayNode.add(createTestObjectNodeJson("city2", 97, false, "zipCode2"));

        ArrayNode stringArrayNode = objectMapper.createArrayNode();
        stringArrayNode.add("st1");
        stringArrayNode.add("st2");

        ArrayNode numberArrayNode = objectMapper.createArrayNode();
        numberArrayNode.add(8);
        numberArrayNode.add(98.6);

        ArrayNode booleanArrayNode = objectMapper.createArrayNode();
        booleanArrayNode.add(true);
        booleanArrayNode.add(false);

        objectNode.putIfAbsent("addressArray", addressesArrayNode);
        objectNode.putIfAbsent("stringArray", stringArrayNode);
        objectNode.putIfAbsent("numberArray", numberArrayNode);
        objectNode.putIfAbsent("booleanArray", booleanArrayNode);
        return objectNode;
    }
}
