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
package org.kie.kogito.expr.jq;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
import org.kie.kogito.serverless.workflow.utils.ConfigResolver;
import org.kie.kogito.serverless.workflow.utils.ConfigResolverHolder;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JqExpressionHandlerTest {

    @BeforeAll
    public static void setConfigResolver() {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("lettersonly", "secretlettersonly");
        configMap.put("dot.secret", "secretdotsecret");
        configMap.put("dash-secret", "secretdashsecret");

        ConfigResolverHolder.setConfigResolver(new ConfigResolver() {
            @Override
            public <T> Optional<T> getConfigProperty(String name, Class<T> clazz) {
                return Optional.ofNullable((T) configMap.get(name));
            }
        });
    }

    @Test
    void testStringExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".propertyString");
        assertTrue(parsedExpression.isValid());
        assertEquals("string", parsedExpression.eval(getObjectNode(), String.class, getContext()));
    }

    @Test
    void testBooleanExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".propertyBoolean");
        assertTrue(parsedExpression.isValid());
        assertTrue(parsedExpression.eval(getObjectNode(), Boolean.class, getContext()));
    }

    @Test
    void testNumericExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".propertyNum*.propertyNum");
        assertTrue(parsedExpression.isValid());
        assertEquals(144, parsedExpression.eval(getObjectNode(), JsonNode.class, getContext()).asInt());
    }

    @Test
    void testNumericAssignment() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", "{\"result\" : .propertyNum*.propertyNum}");
        assertTrue(parsedExpression.isValid());
        assertEquals(144, parsedExpression.eval(getObjectNode(), JsonNode.class, getContext()).get("result").asInt());
    }

    @Test
    void testJsonNodeExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".nested");
        assertTrue(parsedExpression.isValid());
        assertEquals("value1", parsedExpression.eval(getObjectNode(), ObjectNode.class, getContext()).get("property1").asText());
    }

    @Test
    void testMultiExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".propertyString,.propertyNum,.propertyBoolean");
        assertTrue(parsedExpression.isValid());
        assertEquals("string 12 true", parsedExpression.eval(getObjectNode(), String.class, getContext()));
    }

    @Test
    void testMultiExpressionAsCollection() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".propertyString,.propertyNum,.propertyBoolean");
        assertEquals(Arrays.asList("string", 12, true), parsedExpression.eval(getObjectNode(), Collection.class, getContext()));
    }

    @Test
    void testCollection() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".arrayMixed");
        assertTrue(parsedExpression.isValid());
        assertEquals(Arrays.asList("string1", 12, false, Arrays.asList(1.1, 1.2, 1.3)), parsedExpression.eval(getObjectNode(), Collection.class, getContext()));
    }

    @Test
    void testCollectFromArrayJsonNode() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".arrayOfObjects | .[] | .property1");
        assertTrue(parsedExpression.isValid());
        JsonNode eval = parsedExpression.eval(getObjectNode(), JsonNode.class, getContext());
        assertTrue(eval.isArray(), "Expected array as a result.");
        assertEquals(3, eval.size(), "Unexpected size of the array.");
        assertEquals("p1-value1", eval.get(0).asText(), "Unexpected value in array at index 0.");
        assertEquals("p1-value2", eval.get(1).asText(), "Unexpected value in array at index 1.");
        assertEquals("p1-value3", eval.get(2).asText(), "Unexpected value in array at index 2.");

    }

    @Test
    void testCollectFromArrayCollection() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".arrayOfObjects | .[] | .property1");
        assertTrue(parsedExpression.isValid());
        assertEquals(Arrays.asList("p1-value1", "p1-value2", "p1-value3"), parsedExpression.eval(getObjectNode(), Collection.class, getContext()), "Unexpected contents of the collected values.");
    }

    @Test
    void testCollectFromArrayCollectionRecursive() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", "..|.property1?//empty");
        assertTrue(parsedExpression.isValid());
        assertEquals(Arrays.asList("value1", "p1-value1", "p1-value2", "p1-value3", "accessible_value1", "accessible_value2", "accessible_value3"),
                parsedExpression.eval(getObjectNode(), Collection.class, getContext()));
    }

    @Test
    void testNonValidExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".-");
        assertEquals(false, parsedExpression.isValid(), "Exception was not thrown for invalid expression.");
    }

    @Test
    void testNonMatchingExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".foo | .bar");
        assertTrue(parsedExpression.isValid());
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.putArray("foo").add(objectMapper.createArrayNode().add(objectMapper.createObjectNode().put("bar", "1")).add(objectMapper.createObjectNode().put("bar", "2")));
        assertThrows(IllegalArgumentException.class, () -> parsedExpression.eval(objectNode, String.class, getContext()), "Exception expected for non-matched expression.");
    }

    @Test
    void testAssignSimpleObjectUnderGivenProperty() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".propertyString");
        assertTrue(parsedExpression.isValid());
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode toBeInserted = objectMapper.createObjectNode();
        toBeInserted.put("bar", "value1");
        ObjectNode targetNode = getObjectNode();
        parsedExpression.assign(targetNode, toBeInserted, getContext());
        assertFalse(targetNode.has("bar"), "Property 'bar' should not be in root.");
        assertTrue(targetNode.has("propertyString"), "Property 'propertyString' is missing in root.");
        assertTrue(targetNode.get("propertyString").has("bar"), "Property 'propertyString' should contain 'bar'.");
        assertEquals("value1", targetNode.get("propertyString").get("bar").asText(), "Unexpected value under 'propertyString'->'bar' property.");
    }

    @Test
    void testAssignArrayUnderGivenProperty() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".propertyString");
        assertTrue(parsedExpression.isValid());
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode toBeInserted = objectMapper.createObjectNode();
        toBeInserted.putArray("bar").add("value1").add("value2");
        ObjectNode targetNode = getObjectNode();
        parsedExpression.assign(targetNode, toBeInserted, getContext());
        assertFalse(targetNode.has("bar"), "Property 'bar' should not be in root.");
        assertTrue(targetNode.has("propertyString"), "Property 'propertyString' was removed.");
        assertTrue(targetNode.get("propertyString").has("bar"), "Property 'bar' is not under 'propertyString'.");
        assertTrue(targetNode.get("propertyString").get("bar").isArray(), "Property 'bar' is not an array.");
        assertEquals(2, targetNode.get("propertyString").get("bar").size(), "'bar' array has unexpected size");
        assertEquals("value1", targetNode.get("propertyString").get("bar").get(0).asText(), "Unexpected value in 'bar' array at index 0.");
        assertEquals("value2", targetNode.get("propertyString").get("bar").get(1).asText(), "Unexpected value in 'bar' array at index 1.");
    }

    @Test
    void testAssignCollectedFromArrayUnderRootAsFallback() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".arrayOfNestedObjects | .[] | .nested");
        assertTrue(parsedExpression.isValid());
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode targetNode = getObjectNode();
        ObjectNode toBeInserted = objectMapper.createObjectNode().put("propertyNum", 4);

        parsedExpression.assign(targetNode, toBeInserted, getContext());

        assertTrue(targetNode.has("nested"), "A new property 'nested' should have been added at root as a fallback.");
        assertTrue(targetNode.get("nested").isArray(), "Property 'nested' should contain array");
        assertEquals(4, targetNode.get("nested").size(), "'nested' array length mismatch.");
        assertEquals(1, targetNode.get("nested").get(0).get("propertyNum").asInt(), "Unexpected value in 'nested' array at index 0.");
        assertEquals(2, targetNode.get("nested").get(1).get("propertyNum").asInt(), "Unexpected value in 'nested' array at index 1.");
        assertEquals(3, targetNode.get("nested").get(2).get("propertyNum").asInt(), "Unexpected value in 'nested' array at index 2.");
        assertEquals(4, targetNode.get("nested").get(3).get("propertyNum").asInt(), "Unexpected value in 'nested' array at index 3.");
    }

    @Test
    void testAssignWithNonExistentNodePathExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".property3");
        assertTrue(parsedExpression.isValid());
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode toBeInserted = objectMapper.createObjectNode();
        toBeInserted.put("property1", "value1");
        ObjectNode targetNode = objectMapper.createObjectNode();
        targetNode.put("property2", "value2");
        parsedExpression.assign(targetNode, toBeInserted, getContext());
        assertFalse(targetNode.has("property1"), "Property 'property1' should not be in root.");
        assertTrue(targetNode.has("property2"), "Property 'property2' is missing in root.");
        assertTrue(targetNode.has("property3"), "Property 'property3' is missing in root.");
        assertTrue(targetNode.get("property3").has("property1"), "Property 'property3' should contain 'property1'.");
        assertEquals("value1", targetNode.get("property3").get("property1").asText(), "Unexpected value under 'property3'->'property1' property.");
    }

    @Test
    void testMagicWord() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", "$WORKFLOW.instanceId");
        assertTrue(parsedExpression.isValid());
        assertEquals(new TextNode("1111-2222-3333"), parsedExpression.eval(ObjectMapperFactory.get().createObjectNode(), JsonNode.class, getContext()));
    }

    @Test
    void testConstPropertyFromJsonAccessible() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".CONST.property1");
        assertTrue(parsedExpression.isValid());
        assertEquals("accessible_value1", parsedExpression.eval(getObjectNode(), String.class, getContext()));
    }

    @Test
    void testSecretPropertyFromJsonAccessible() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".SECRET.property1");
        assertTrue(parsedExpression.isValid());
        assertEquals("accessible_value2", parsedExpression.eval(getObjectNode(), String.class, getContext()));
    }

    @Test
    void testWorkflowPropertyFromJsonAccessible() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".WORKFLOW.property1");
        assertTrue(parsedExpression.isValid());
        assertEquals("accessible_value3", parsedExpression.eval(getObjectNode(), String.class, getContext()));
    }

    @ParameterizedTest(name = "{index} \"{0}\" is resolved to \"{1}\"")
    @MethodSource("provideMagicWordExpressionsToTest")
    void testMagicWordsExpressions(String expression, String expectedResult, KogitoProcessContext context) {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", expression);
        assertTrue(parsedExpression.isValid());
        assertEquals(expectedResult, parsedExpression.eval(getObjectNode(), String.class, context));
    }

    private static Stream<Arguments> provideMagicWordExpressionsToTest() {
        return Stream.of(
                Arguments.of("$WORKFLOW.instanceId", "1111-2222-3333", getContext()),
                Arguments.of("\"WORKFLOW.instanceId\"", "WORKFLOW.instanceId", getContext()),
                Arguments.of("\"$WORKFLOW.instanceId\"", "$WORKFLOW.instanceId", getContext()),
                Arguments.of("\"$WORKFLOW.instanceId: \" + $WORKFLOW.instanceId", "$WORKFLOW.instanceId: 1111-2222-3333", getContext()),
                Arguments.of("$SECRET.none", "null", getContext()),
                Arguments.of("\"$SECRET.none\"", "$SECRET.none", getContext()),
                Arguments.of("$SECRET.lettersonly", "secretlettersonly", getContext()),
                Arguments.of("$SECRET.dot.secret", "null", getContext()),
                Arguments.of("$SECRET.\"dot.secret\"", "secretdotsecret", getContext()),
                Arguments.of("$SECRET.\"dash-secret\"", "secretdashsecret", getContext()),
                Arguments.of("$CONST.someconstant", "value", getContext()),
                Arguments.of("$CONST.\"someconstant\"", "value", getContext()),
                Arguments.of("$CONST.some.constant", "null", getContext()),
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
