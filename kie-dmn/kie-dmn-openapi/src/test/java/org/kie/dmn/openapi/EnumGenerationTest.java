/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.openapi;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.openapi.model.DMNOASResult;

import static org.assertj.core.api.Assertions.assertThat;

class EnumGenerationTest extends BaseDMNOASTest {

    private static final String DMN_FILE = "valid_models/DMNv1_5/ConstraintsChecks.dmn";
    private static JsonNode definitions;

    @BeforeAll
    static void setup() {
        DMNRuntime runtime = createRuntime(DMN_FILE, EnumGenerationTest.class);
        DMNOASResult result = DMNOASGeneratorFactory.generator(runtime.getModels()).build();

        DMNModel modelUnderTest = runtime.getModel("http://www.trisotech.com/definitions/_238bd96d-47cd-4746-831b" +
                                                           "-504f3e77b442",
                                                   "ConstraintsChecks");
        ObjectNode syntheticJSONSchema = synthesizeSchema(result, modelUnderTest);
        definitions = syntheticJSONSchema.get("definitions");
    }

    @Test
    void numberAllowedValues() {
        JsonNode node = definitions.get("numberAllowedValues");
        assertThat(node).isNotNull();
        ArrayNode arrayNode = (ArrayNode) node.get("enum");
        assertThat(arrayNode).isNotNull();
        List<BigDecimal> expected = Arrays.asList(BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.valueOf(2));
        arrayNode.iterator().forEachRemaining(e -> {
            assertThat(e).isInstanceOf(DecimalNode.class);
            assertThat(expected).contains(e.decimalValue());
        });
        assertThat(node.get("nullable")).isNotNull().isInstanceOf(BooleanNode.class);
        assertThat(node.get("nullable").asBoolean()).isFalse();
    }

    @Test
    void stringAllowedValues() {
        JsonNode node = definitions.get("stringAllowedValues");
        assertThat(node).isNotNull();
        ArrayNode arrayNode = (ArrayNode) node.get("enum");
        assertThat(arrayNode).isNotNull();
        List<String> expected = Arrays.asList("a", "b", "c");
        arrayNode.iterator().forEachRemaining(e -> {
            assertThat(e).isInstanceOf(TextNode.class);
            assertThat(expected).contains(e.textValue());
        });
        assertThat(node.get("nullable")).isNotNull().isInstanceOf(BooleanNode.class);
        assertThat(node.get("nullable").asBoolean()).isFalse();
    }

    @Test
    void stringNullableAllowedValues() {
        JsonNode node = definitions.get("stringNullableAllowedValues");
        assertThat(node).isNotNull();
        ArrayNode arrayNode = (ArrayNode) node.get("enum");
        assertThat(arrayNode).isNotNull();
        List<String> expected = Arrays.asList("a", "b", "c", null);
        arrayNode.iterator().forEachRemaining(e -> {
            assertThat(expected).contains(e.textValue());
        });
        assertThat(node.get("nullable")).isNotNull().isInstanceOf(BooleanNode.class);
        assertThat(node.get("nullable").asBoolean()).isTrue();
    }

    @Test
    void numberTypeConstraint() {
        JsonNode node = definitions.get("numberTypeConstraint");
        assertThat(node).isNotNull();
        ArrayNode arrayNode = (ArrayNode) node.get("enum");
        assertThat(arrayNode).isNotNull();
        List<BigDecimal> expected = Arrays.asList(BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.valueOf(2));
        arrayNode.iterator().forEachRemaining(e -> {
            assertThat(e).isInstanceOf(DecimalNode.class);
            assertThat(expected).contains(e.decimalValue());
        });
        assertThat(node.get("nullable")).isNotNull().isInstanceOf(BooleanNode.class);
        assertThat(node.get("nullable").asBoolean()).isFalse();
    }

    @Test
    void stringTypeConstraint() {
        JsonNode node = definitions.get("stringTypeConstraint");
        assertThat(node).isNotNull();
        ArrayNode arrayNode = (ArrayNode) node.get("enum");
        assertThat(arrayNode).isNotNull();
        List<String> expected = Arrays.asList("a", "b", "c");
        arrayNode.iterator().forEachRemaining(e -> {
            assertThat(e).isInstanceOf(TextNode.class);
            assertThat(expected).contains(e.textValue());
        });
        assertThat(node.get("nullable")).isNotNull().isInstanceOf(BooleanNode.class);
        assertThat(node.get("nullable").asBoolean()).isFalse();
    }

    @Test
    void stringNullableTypeConstraint() {
        JsonNode node = definitions.get("stringNullableTypeConstraint");
        assertThat(node).isNotNull();
        ArrayNode arrayNode = (ArrayNode) node.get("enum");
        assertThat(arrayNode).isNotNull();
        List<String> expected = Arrays.asList("a", "b", "c", null);
        arrayNode.iterator().forEachRemaining(e -> {
            assertThat(expected).contains(e.textValue());
        });
        assertThat(node.get("nullable")).isNotNull().isInstanceOf(BooleanNode.class);
        assertThat(node.get("nullable").asBoolean()).isTrue();
    }
}
