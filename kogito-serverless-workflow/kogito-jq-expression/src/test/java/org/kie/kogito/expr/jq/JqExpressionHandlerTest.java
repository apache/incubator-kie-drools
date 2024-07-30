/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.expr.jq;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.process.expr.Expression;
import org.kie.kogito.process.expr.ExpressionHandlerFactory;
import org.kie.kogito.serverless.workflow.test.MockBuilder;
import org.kie.kogito.serverless.workflow.utils.ConfigResolverHolder;
import org.kie.kogito.serverless.workflow.utils.MapConfigResolver;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JqExpressionHandlerTest {

    @BeforeAll
    public static void setConfigResolver() {
        ConfigResolverHolder.setConfigResolver(new MapConfigResolver(Map.of("lettersonly", "secretlettersonly",
                "dot.secret", "secretdotsecret",
                "dash-secret", "secretdashsecret")));
    }

    @Test
    void testStringExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".propertyString");
        assertThat(parsedExpression.isValid()).isTrue();
        assertThat(parsedExpression.eval(getObjectNode(), String.class, getContext())).isEqualTo("string");
    }

    @Test
    void testBooleanExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".propertyBoolean");
        assertThat(parsedExpression.isValid()).isTrue();
        assertThat(parsedExpression.eval(getObjectNode(), Boolean.class, getContext())).isTrue();
    }

    @Test
    void testNumericExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".propertyNum*.propertyNum");
        assertThat(parsedExpression.isValid()).isTrue();
        assertThat(parsedExpression.eval(getObjectNode(), JsonNode.class, getContext()).asInt()).isEqualTo(144);
    }

    @Test
    void testNumericAssignment() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", "{\"result\" : .propertyNum*.propertyNum}");
        assertThat(parsedExpression.isValid()).isTrue();
        assertThat(parsedExpression.eval(getObjectNode(), JsonNode.class, getContext()).get("result").asInt()).isEqualTo(144);
    }

    @Test
    void testJsonNodeExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".nested");
        assertThat(parsedExpression.isValid()).isTrue();
        assertThat(parsedExpression.eval(getObjectNode(), ObjectNode.class, getContext()).get("property1").asText()).isEqualTo("value1");
    }

    @Test
    void testMultiExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".propertyString,.propertyNum,.propertyBoolean");
        assertThat(parsedExpression.isValid()).isTrue();
        assertThat(parsedExpression.eval(getObjectNode(), String.class, getContext())).isEqualTo("string 12 true");
    }

    @Test
    void testMultiExpressionAsCollection() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".propertyString,.propertyNum,.propertyBoolean");
        assertThat(parsedExpression.eval(getObjectNode(), Collection.class, getContext())).containsExactly("string", 12, true);
    }

    @Test
    void testCollection() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".arrayMixed");
        assertThat(parsedExpression.isValid()).isTrue();
        assertThat(parsedExpression.eval(getObjectNode(), Collection.class, getContext())).containsExactly("string1", 12, false, Arrays.asList(1.1, 1.2, 1.3));
    }

    @Test
    void testCollectFromArrayJsonNode() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".arrayOfObjects | .[] | .property1");
        assertThat(parsedExpression.isValid()).isTrue();
        JsonNode eval = parsedExpression.eval(getObjectNode(), JsonNode.class, getContext());
        assertThat(eval.isArray()).as("Expected array as a result.").isTrue();
        assertThat(eval).as("Unexpected size of the array.").hasSize(3);
        assertThat(eval.get(0).asText()).as("Unexpected value in array at index 0.").isEqualTo("p1-value1");
        assertThat(eval.get(1).asText()).as("Unexpected value in array at index 1.").isEqualTo("p1-value2");
        assertThat(eval.get(2).asText()).as("Unexpected value in array at index 2.").isEqualTo("p1-value3");

    }

    @Test
    void testCollectFromArrayCollection() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".arrayOfObjects | .[] | .property1");
        assertThat(parsedExpression.isValid()).isTrue();
        assertThat(parsedExpression.eval(getObjectNode(), Collection.class, getContext())).as("Unexpected contents of the collected values.").containsExactly("p1-value1", "p1-value2", "p1-value3");
    }

    @Test
    void testCollectFromArrayCollectionRecursive() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", "..|.property1?//empty");
        assertThat(parsedExpression.isValid()).isTrue();
        assertThat(parsedExpression.eval(getObjectNode(), Collection.class, getContext()))
                .isEqualTo(Arrays.asList("value1", "p1-value1", "p1-value2", "p1-value3", "accessible_value1", "accessible_value2", "accessible_value3"));
    }

    @Test
    void testNonValidExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".-");
        assertThat(parsedExpression.isValid()).as("Exception was not thrown for invalid expression.").isFalse();
    }

    @Test
    void testNonMatchingExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".foo | .bar");
        assertThat(parsedExpression.isValid()).isTrue();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.putArray("foo").add(objectMapper.createArrayNode().add(objectMapper.createObjectNode().put("bar", "1")).add(objectMapper.createObjectNode().put("bar", "2")));
        assertThrows(IllegalArgumentException.class, () -> parsedExpression.eval(objectNode, String.class, getContext()), "Exception expected for non-matched expression.");
    }

    @Test
    void testAssignSimpleObjectUnderGivenProperty() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".propertyString");
        assertThat(parsedExpression.isValid()).isTrue();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode toBeInserted = objectMapper.createObjectNode();
        toBeInserted.put("bar", "value1");
        ObjectNode targetNode = getObjectNode();
        parsedExpression.assign(targetNode, toBeInserted, getContext());
        assertThat(targetNode.has("bar")).as("Property 'bar' should not be in root.").isFalse();
        assertThat(targetNode.has("propertyString")).as("Property 'propertyString' is missing in root.").isTrue();
        assertThat(targetNode.get("propertyString").has("bar")).as("Property 'propertyString' should contain 'bar'.").isTrue();
        assertThat(targetNode.get("propertyString").get("bar").asText()).as("Unexpected value under 'propertyString'->'bar' property.").isEqualTo("value1");
    }

    @Test
    void testAssignComplexObjectUnderGivenProperty() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".propertyObject.nestedString");
        assertThat(parsedExpression.isValid()).isTrue();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode toBeInserted = objectMapper.createObjectNode();
        toBeInserted.put("bar", "value1");
        ObjectNode targetNode = getObjectNode();
        parsedExpression.assign(targetNode, toBeInserted, getContext());
        assertThat(targetNode.has("bar")).as("Property 'bar' should not be in root.").isFalse();
        assertThat(targetNode.has("propertyObject")).as("Property 'propertyObject' is missing in root.").isTrue();
        assertThat(targetNode.get("propertyObject").has("nestedString")).as("Property 'propertyObject' should contain 'nestedString'.").isTrue();
        assertThat(targetNode.get("propertyObject").get("nestedString").has("bar")).as("Property 'nestedString' should contain 'bar'.").isTrue();
        assertThat(targetNode.get("propertyObject").get("nestedString").get("bar").asText()).as("Unexpected value under 'propertyObject'->'bar' property.").isEqualTo("value1");
    }

    @Test
    void testAssignArrayUnderGivenProperty() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".propertyString");
        assertThat(parsedExpression.isValid()).isTrue();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode toBeInserted = objectMapper.createObjectNode();
        toBeInserted.putArray("bar").add("value1").add("value2");
        ObjectNode targetNode = getObjectNode();
        parsedExpression.assign(targetNode, toBeInserted, getContext());
        assertThat(targetNode.has("bar")).as("Property 'bar' should not be in root.").isFalse();
        assertThat(targetNode.has("propertyString")).as("Property 'propertyString' was removed.").isTrue();
        assertThat(targetNode.get("propertyString").has("bar")).as("Property 'bar' is not under 'propertyString'.").isTrue();
        assertThat(targetNode.get("propertyString").get("bar").isArray()).as("Property 'bar' is not an array.").isTrue();
        assertThat(targetNode.get("propertyString").get("bar")).as("'bar' array has unexpected size").hasSize(2);
        assertThat(targetNode.get("propertyString").get("bar").get(0).asText()).as("Unexpected value in 'bar' array at index 0.").isEqualTo("value1");
        assertThat(targetNode.get("propertyString").get("bar").get(1).asText()).as("Unexpected value in 'bar' array at index 1.").isEqualTo("value2");
    }

    @Test
    void testAssignCollectedFromArrayUnderRootAsFallback() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".arrayOfNestedObjects | .[] | .nested");
        assertThat(parsedExpression.isValid()).isTrue();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode targetNode = getObjectNode();
        ObjectNode toBeInserted = objectMapper.createObjectNode().put("propertyNum", 4);

        parsedExpression.assign(targetNode, toBeInserted, getContext());

        assertThat(targetNode.has("nested")).as("A new property 'nested' should have been added at root as a fallback.").isTrue();
        assertThat(targetNode.get("nested").isArray()).as("Property 'nested' should contain array").isTrue();
        assertThat(targetNode.get("nested")).as("'nested' array length mismatch.").hasSize(4);
        assertThat(targetNode.get("nested").get(0).get("propertyNum").asInt()).as("Unexpected value in 'nested' array at index 0.").isEqualTo(1);
        assertThat(targetNode.get("nested").get(1).get("propertyNum").asInt()).as("Unexpected value in 'nested' array at index 1.").isEqualTo(2);
        assertThat(targetNode.get("nested").get(2).get("propertyNum").asInt()).as("Unexpected value in 'nested' array at index 2.").isEqualTo(3);
        assertThat(targetNode.get("nested").get(3).get("propertyNum").asInt()).as("Unexpected value in 'nested' array at index 3.").isEqualTo(4);
    }

    @Test
    void testAssignWithNonExistentNodePathExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".property3");
        assertThat(parsedExpression.isValid()).isTrue();
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode toBeInserted = objectMapper.createObjectNode();
        toBeInserted.put("property1", "value1");
        ObjectNode targetNode = objectMapper.createObjectNode();
        targetNode.put("property2", "value2");
        parsedExpression.assign(targetNode, toBeInserted, getContext());
        assertThat(targetNode.has("property1")).as("Property 'property1' should not be in root.").isFalse();
        assertThat(targetNode.has("property2")).as("Property 'property2' is missing in root.").isTrue();
        assertThat(targetNode.has("property3")).as("Property 'property3' is missing in root.").isTrue();
        assertThat(targetNode.get("property3").has("property1")).as("Property 'property3' should contain 'property1'.").isTrue();
        assertThat(targetNode.get("property3").get("property1").asText()).as("Unexpected value under 'property3'->'property1' property.").isEqualTo("value1");
    }

    @Test
    void testMagicWord() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", "$WORKFLOW.instanceId");
        assertThat(parsedExpression.isValid()).isTrue();
        assertThat(parsedExpression.eval(ObjectMapperFactory.get().createObjectNode(), JsonNode.class, getContext())).isEqualTo(new TextNode("1111-2222-3333"));
    }

    @Test
    void testConstPropertyFromJsonAccessible() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".CONST.property1");
        assertThat(parsedExpression.isValid()).isTrue();
        assertThat(parsedExpression.eval(getObjectNode(), String.class, getContext())).isEqualTo("accessible_value1");
    }

    @Test
    void testSecretPropertyFromJsonAccessible() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".SECRET.property1");
        assertThat(parsedExpression.isValid()).isTrue();
        assertThat(parsedExpression.eval(getObjectNode(), String.class, getContext())).isEqualTo("accessible_value2");
    }

    @Test
    void testWorkflowPropertyFromJsonAccessible() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".WORKFLOW.property1");
        assertThat(parsedExpression.isValid()).isTrue();
        assertThat(parsedExpression.eval(getObjectNode(), String.class, getContext())).isEqualTo("accessible_value3");
    }

    @ParameterizedTest(name = "{index} \"{0}\" is resolved to \"{1}\"")
    @MethodSource("provideMagicWordExpressionsToTest")
    void testMagicWordsExpressions(String expression, String expectedResult, KogitoProcessContext context) {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", expression);
        assertThat(parsedExpression.isValid()).isTrue();
        assertThat(parsedExpression.eval(getObjectNode(), String.class, context)).isEqualTo(expectedResult);
    }

    @Test
    void testHardcodedStringIsValidOrNot() {
        assertThat(ExpressionHandlerFactory.get("jq", "kserve_payload = to_kserve(image)").isValid()).isFalse();
        assertThat(ExpressionHandlerFactory.get("jq", "length .variable").isValid()).isTrue();
    }

    private static Stream<Arguments> provideMagicWordExpressionsToTest() {
        return Stream.of(
                Arguments.of("$WORKFLOW.instanceId", "1111-2222-3333", getContext()),
                Arguments.of("\"WORKFLOW.instanceId\"", "WORKFLOW.instanceId", getContext()),
                Arguments.of("\"$WORKFLOW.instanceId\"", "$WORKFLOW.instanceId", getContext()),
                Arguments.of("\"$WORKFLOW.instanceId: \" + $WORKFLOW.instanceId", "$WORKFLOW.instanceId: 1111-2222-3333", getContext()),
                Arguments.of("$SECRET.none", "", getContext()),
                Arguments.of("\"$SECRET.none\"", "$SECRET.none", getContext()),
                Arguments.of("$SECRET.lettersonly", "secretlettersonly", getContext()),
                Arguments.of("$SECRET.dot.secret", "secretdotsecret", getContext()),
                Arguments.of("$SECRET.\"dot.secret\"", "secretdotsecret", getContext()),
                Arguments.of("$SECRET.\"dash-secret\"", "secretdashsecret", getContext()),
                Arguments.of("$CONST.someconstant", "value", getContext()),
                Arguments.of("$CONST.\"someconstant\"", "value", getContext()),
                Arguments.of("$CONST.some.constant", "", getContext()),
                Arguments.of("$CONST.\"some.constant\"", "value", getContext()),
                Arguments.of("$CONST.\"some-constant\"", "value", getContext()),
                Arguments.of("$CONST.injectedexpression", "$WORKFLOW.instanceId", getContext()),
                Arguments.of(".arrayOfObjects | .[] | select(.property1 == \"p1-value1\") | .property1", "p1-value1", getContext()));
    }

    private static KogitoProcessContext getContext() {
        return MockBuilder.kogitoProcessContext()
                .withProcessInstanceMock(p -> Mockito.when(p.getId()).thenReturn("1111-2222-3333"))
                .withConstants(Collections.singletonMap("someconstant", "value"))
                .withConstants(Collections.singletonMap("some.constant", "value"))
                .withConstants(Collections.singletonMap("some-constant", "value"))
                .withConstants(Collections.singletonMap("injectedexpression", "$WORKFLOW.instanceId")) // should not be resolved
                .build();
    }

    private static ObjectNode getObjectNode() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode
                .put("propertyString", "string")
                .put("propertyNum", 12)
                .put("propertyBoolean", true);
        objectNode.putObject("nested").put("property1", "value1");
        objectNode.putArray("arrayOfObjects")
                .add(objectMapper.createObjectNode().put("property1", "p1-value1"))
                .add(objectMapper.createObjectNode().put("property1", "p1-value2"))
                .add(objectMapper.createObjectNode().put("property1", "p1-value3"));
        objectNode.putArray("arrayOfNestedObjects")
                .add(objectMapper.createObjectNode().set("nested", objectMapper.createObjectNode().put("propertyNum", 1)))
                .add(objectMapper.createObjectNode().set("nested", objectMapper.createObjectNode().put("propertyNum", 2)))
                .add(objectMapper.createObjectNode().set("nested", objectMapper.createObjectNode().put("propertyNum", 3)));
        objectNode.putArray("arrayOfStrings")
                .add("string1")
                .add("string2")
                .add("string3");
        objectNode.putArray("arrayOfNums")
                .add(1)
                .add(2)
                .add(3);
        objectNode.putArray("arrayMixed")
                .add("string1")
                .add(12)
                .add(false)
                .add(objectMapper.createArrayNode().add(1.1).add(1.2).add(1.3));
        objectNode.putObject("CONST").put("property1", "accessible_value1");
        objectNode.putObject("SECRET").put("property1", "accessible_value2");
        objectNode.putObject("WORKFLOW").put("property1", "accessible_value3");
        return objectNode;
    }
}
