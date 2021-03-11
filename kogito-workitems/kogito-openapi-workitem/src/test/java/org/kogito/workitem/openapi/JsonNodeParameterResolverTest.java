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
package org.kogito.workitem.openapi;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonNodeParameterResolverTest {

    final ObjectMapper mapper = new ObjectMapper();

    @Test
    void verifyMultipleBranchesNode() throws JsonProcessingException {
        final JsonNode inputModel = mapper.readTree("{ \"fahrenheit\": \"32\", \"subtractValue\": \"3\" }");
        final String parameterDefinition = "{\n" +
                "   \"SubtractionOperation\":{\n" +
                "      \"leftElement\":\"$.fahrenheit\",\n" +
                "      \"rightElement\":\"$.subtractValue\"\n" +
                "   }\n" +
                "}";
        final JsonNodeParameterResolver resolver = new JsonNodeParameterResolver(parameterDefinition);
        final JsonNode processedNode = resolver.apply(inputModel);
        assertThat(processedNode.get("SubtractionOperation").get("leftElement").asInt(), is(32));
        assertThat(processedNode.get("SubtractionOperation").get("rightElement").asInt(), is(3));
    }

    @Test
    void verifyArrayNode() throws JsonProcessingException {
        final JsonNode inputModel = mapper.readTree("{ \"fahrenheit\": \"32\", \"subtractValue\": \"3\" }");
        final String parameterDefinition = "[\n" +
                "   {\n" +
                "      \"leftElement\":\"$.fahrenheit\"\n" +
                "   },\n" +
                "   {\n" +
                "      \"rightElement\":\"$.subtractValue\"\n" +
                "   }\n" +
                "]";
        final JsonNodeParameterResolver resolver = new JsonNodeParameterResolver(parameterDefinition);
        final JsonNode processedNode = resolver.apply(inputModel);
        assertTrue(processedNode.isArray());
        assertThat(processedNode.findValue("leftElement").asInt(), equalTo(32));
        assertThat(processedNode.findValue("rightElement").asInt(), equalTo(3));
    }

    @Test
    void verifyValueNode() throws JsonProcessingException {
        final JsonNode inputModel = mapper.readTree("{ \"fahrenheit\": \"32\", \"subtractValue\": \"3\" }");
        final String parameterDefinition = "\"$.fahrenheit\"";
        final JsonNodeParameterResolver resolver = new JsonNodeParameterResolver(parameterDefinition);
        final JsonNode processedNode = resolver.apply(inputModel);
        assertTrue(processedNode.isValueNode());
        assertThat(processedNode.asInt(), equalTo(32));
    }

    @Test
    void verifyArrayValueNode() throws JsonProcessingException {
        final JsonNode inputModel = mapper.readTree("{ \"fahrenheit\": \"32\", \"subtractValue\": \"3\" }");
        final String parameterDefinition = "[\"$.fahrenheit\", \"$.subtractValue\"]";
        final JsonNodeParameterResolver resolver = new JsonNodeParameterResolver(parameterDefinition);
        final JsonNode processedNode = resolver.apply(inputModel);
        assertTrue(processedNode.isArray());
        assertThat(processedNode.get(0).asInt(), equalTo(32));
        assertThat(processedNode.get(1).asInt(), equalTo(3));
    }

    @Test
    void verifyParameterAsJsonPath() throws JsonProcessingException {
        final JsonNode inputModel = mapper.readTree("{ \"fahrenheit\": \"32\", \"subtractValue\": \"3\" }");
        final String parameterDefinition = "$.fahrenheit";
        final JsonNodeParameterResolver resolver = new JsonNodeParameterResolver(parameterDefinition);
        final JsonNode processedNode = resolver.apply(inputModel);
        assertTrue(processedNode.isValueNode());
        assertThat(processedNode.asInt(), equalTo(32));
    }
}
