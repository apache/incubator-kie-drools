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

import org.jbpm.ruleflow.core.Metadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcessInstance;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;
import org.kie.kogito.process.expr.Expression;
import org.kie.kogito.process.expr.ExpressionHandlerFactory;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JqExpressionHandlerTest {

    private KogitoProcessContext context;

    @BeforeEach
    void setup() {
        context = Mockito.mock(KogitoProcessContext.class);
        KogitoWorkflowProcessInstance processInstance = Mockito.mock(KogitoWorkflowProcessInstance.class);
        Mockito.when(context.getProcessInstance()).thenReturn(processInstance);
        KogitoWorkflowProcess process = Mockito.mock(KogitoWorkflowProcess.class);
        Mockito.when(processInstance.getProcess()).thenReturn(process);
        Mockito.when(processInstance.getId()).thenReturn("1111-2222-3333");
        Mockito.when(process.getMetaData()).thenReturn(Collections.singletonMap(Metadata.CONSTANTS, NullNode.instance));
    }

    @Test
    void testStringExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".foo");
        assertTrue(parsedExpression.isValid());
        JsonNode node = new ObjectMapper().createObjectNode().put("foo", "javierito");
        assertEquals("javierito", parsedExpression.eval(node, String.class, context));
    }

    @Test
    void testBooleanExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".foo");
        assertTrue(parsedExpression.isValid());
        JsonNode node = new ObjectMapper().createObjectNode().put("foo", true);
        assertTrue(parsedExpression.eval(node, Boolean.class, context));
    }

    @Test
    void testNumericExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".number*.number");
        assertTrue(parsedExpression.isValid());
        JsonNode node = new ObjectMapper().createObjectNode().put("number", 2);
        assertEquals(4, parsedExpression.eval(node, JsonNode.class, context).asInt());
    }

    @Test
    void testNumericAssignment() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", "{\"result\" : .number*.number}");
        assertTrue(parsedExpression.isValid());
        JsonNode node = new ObjectMapper().createObjectNode().put("number", 2);
        assertEquals(4, parsedExpression.eval(node, JsonNode.class, context).get("result").asInt());
    }

    @Test
    void testJsonNodeExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".foo");
        assertTrue(parsedExpression.isValid());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.createObjectNode().set("foo", mapper.createObjectNode().put("name", "Javierito"));
        assertEquals("Javierito", parsedExpression.eval(node, ObjectNode.class, context).get("name").asText());
    }

    @Test
    void testMultiExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".foo,.main,.another");
        assertTrue(parsedExpression.isValid());
        JsonNode node = new ObjectMapper().createObjectNode().put("foo", "Javierito").put("main", "Pepito").put("another", "Fulanito");
        assertEquals("Javierito Pepito Fulanito", parsedExpression.eval(node, String.class, context));
    }

    @Test
    void testCollection() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".foo");
        assertTrue(parsedExpression.isValid());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.createObjectNode().set("foo", objectMapper.createArrayNode().add("pepe").add(false).add(3).add(objectMapper.createArrayNode().add(1.1).add(1.2).add(1.3)));
        assertEquals(Arrays.asList("pepe", false, 3, Arrays.asList(1.1, 1.2, 1.3)), parsedExpression.eval(node, Collection.class, context));
    }

    @Test
    void testCollectFromArrayJsonNode() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".foo | .[] | .bar");
        assertTrue(parsedExpression.isValid());
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        ArrayNode node = objectNode.putArray("foo");
        node.add(objectMapper.createObjectNode().put("bar", "value1"));
        node.add(objectMapper.createObjectNode().put("bar", "value2"));
        node.add(objectMapper.createObjectNode().put("bar", "value3"));
        JsonNode eval = parsedExpression.eval(objectNode, JsonNode.class, context);
        assertTrue(eval.isArray(), "Expected array as a result.");
        assertEquals(3, eval.size(), "Unexpected size of the array.");
        assertEquals("value1", eval.get(0).asText(), "Unexpected value in array at index 0.");
        assertEquals("value2", eval.get(1).asText(), "Unexpected value in array at index 0.");
        assertEquals("value3", eval.get(2).asText(), "Unexpected value in array at index 0.");

    }

    @Test
    void testCollectFromArrayCollection() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".foo | .[] | .bar");
        assertTrue(parsedExpression.isValid());
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        ArrayNode node = objectNode.putArray("foo");
        node.add(objectMapper.createObjectNode().put("bar", "value1"));
        node.add(objectMapper.createObjectNode().put("bar", "value2"));
        node.add(objectMapper.createObjectNode().put("bar", "value3"));
        assertEquals(Arrays.asList("value1", "value2", "value3"), parsedExpression.eval(objectNode, Collection.class, context), "Unexpected contents of the collected values.");

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
        assertThrows(IllegalArgumentException.class, () -> parsedExpression.eval(objectNode, String.class, context), "Exception expected for non-matched expression.");
    }

    @Test
    void testAssignSimpleObjectUnderGivenProperty() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".bar2");
        assertTrue(parsedExpression.isValid());
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode toBeInserted = objectMapper.createObjectNode();
        toBeInserted.put("bar", "value1");
        ObjectNode targetNode = objectMapper.createObjectNode();
        targetNode.put("bar2", "value2");
        parsedExpression.assign(targetNode, toBeInserted, context);
        assertFalse(targetNode.has("bar"), "Property 'bar' should not be in root.");
        assertTrue(targetNode.has("bar2"), "Property 'bar2' is missing in root.");
        assertTrue(targetNode.get("bar2").has("bar"), "Property 'bar2' should contain 'bar'.");
        assertEquals("value1", targetNode.get("bar2").get("bar").asText(), "Unexpected value under 'bar2'->'bar' property.");
    }

    @Test
    void testAssignArrayUnderGivenProperty() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".bar2");
        assertTrue(parsedExpression.isValid());
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode toBeInserted = objectMapper.createObjectNode();
        toBeInserted.putArray("bar").add("value1").add("value2");
        ObjectNode targetNode = objectMapper.createObjectNode();
        targetNode.put("bar2", "value2");
        parsedExpression.assign(targetNode, toBeInserted, context);
        assertFalse(targetNode.has("bar"), "Property 'bar' should not be in root.");
        assertTrue(targetNode.has("bar2"), "Property 'bar2' was removed.");
        assertTrue(targetNode.get("bar2").has("bar"), "Property 'bar' is not under 'bar2'.");
        assertTrue(targetNode.get("bar2").get("bar").isArray(), "Property 'bar' is not an array.");
        assertEquals(2, targetNode.get("bar2").get("bar").size(), "'bar' array has unexpected size");
        assertEquals("value1", targetNode.get("bar2").get("bar").get(0).asText(), "Unexpected value in 'bar' array at index 0.");
        assertEquals("value2", targetNode.get("bar2").get("bar").get(1).asText(), "Unexpected value in 'bar' array at index 1.");
    }

    @Test
    void testAssignCollectedFromArrayUnderRootAsFallback() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".foo | .[] | .bar");
        assertTrue(parsedExpression.isValid());
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode targetNode = objectMapper.createObjectNode();
        ArrayNode node = targetNode.putArray("foo");
        node.add(objectMapper.createObjectNode().set("bar", objectMapper.createObjectNode().put("property", 1)));
        node.add(objectMapper.createObjectNode().set("bar", objectMapper.createObjectNode().put("property", 2)));
        node.add(objectMapper.createObjectNode().set("bar", objectMapper.createObjectNode().put("property", 3)));
        ObjectNode toBeInserted = objectMapper.createObjectNode().put("property", 4);

        parsedExpression.assign(targetNode, toBeInserted, context);

        assertTrue(targetNode.has("bar"), "A new property 'bar' should have been added at root as a fallback.");
        assertTrue(targetNode.get("bar").isArray(), "Property 'bar' should contain array");
        assertEquals(4, targetNode.get("bar").size(), "'bar' array length mismatch.");
        assertEquals(1, targetNode.get("bar").get(0).get("property").asInt(), "Unexpected value in 'bar' array at index 0.");
        assertEquals(2, targetNode.get("bar").get(1).get("property").asInt(), "Unexpected value in 'bar' array at index 1.");
        assertEquals(3, targetNode.get("bar").get(2).get("property").asInt(), "Unexpected value in 'bar' array at index 2.");
        assertEquals(4, targetNode.get("bar").get(3).get("property").asInt(), "Unexpected value in 'bar' array at index 3.");
    }

    @Test
    void testAssignWithNonExistentNodePathExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".bar3");
        assertTrue(parsedExpression.isValid());
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode toBeInserted = objectMapper.createObjectNode();
        toBeInserted.put("bar", "value1");
        ObjectNode targetNode = objectMapper.createObjectNode();
        targetNode.put("bar2", "value2");
        parsedExpression.assign(targetNode, toBeInserted, context);
        assertFalse(targetNode.has("bar"), "Property 'bar' should not be in root.");
        assertTrue(targetNode.has("bar2"), "Property 'bar2' is missing in root.");
        assertTrue(targetNode.has("bar3"), "Property 'bar3' is missing in root.");
        assertTrue(targetNode.get("bar3").has("bar"), "Property 'bar3' should contain 'bar'.");
        assertEquals("value1", targetNode.get("bar3").get("bar").asText(), "Unexpected value under 'bar3'->'bar' property.");
    }

    @Test
    void testMagicWord() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", "$WORKFLOW.instanceId");
        assertTrue(parsedExpression.isValid());
        assertEquals(new TextNode("1111-2222-3333"), parsedExpression.eval(ObjectMapperFactory.get().createObjectNode(), JsonNode.class, context));
    }

}
