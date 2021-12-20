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

import org.junit.jupiter.api.Test;
import org.kie.kogito.process.workitems.impl.expr.Expression;
import org.kie.kogito.process.workitems.impl.expr.ExpressionHandlerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JqExpressionHandlerTest {

    @Test
    void testStringExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".foo");
        JsonNode node = new ObjectMapper().createObjectNode().put("foo", "javierito");
        assertEquals("javierito", parsedExpression.eval(node, String.class));
    }

    @Test
    void testBooleanExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".foo");
        JsonNode node = new ObjectMapper().createObjectNode().put("foo", true);
        assertTrue(parsedExpression.eval(node, Boolean.class));
    }

    @Test
    void testNumericExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".number*.number");
        JsonNode node = new ObjectMapper().createObjectNode().put("number", 2);
        assertEquals(4, parsedExpression.eval(node, JsonNode.class).asInt());
    }

    @Test
    void testNumericAssignment() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", "{\"result\" : .number*.number}");
        JsonNode node = new ObjectMapper().createObjectNode().put("number", 2);
        assertEquals(4, parsedExpression.eval(node, JsonNode.class).get("result").asInt());
    }

    @Test
    void testJsonNodeExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".foo");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.createObjectNode().set("foo", mapper.createObjectNode().put("name", "Javierito"));
        assertEquals("Javierito", parsedExpression.eval(node, ObjectNode.class).get("name").asText());
    }

    @Test
    void testMultiExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".foo,.main,.another");
        JsonNode node = new ObjectMapper().createObjectNode().put("foo", "Javierito").put("main", "Pepito").put("another", "Fulanito");
        assertEquals("Javierito Pepito Fulanito", parsedExpression.eval(node, String.class));
    }

    @Test
    void testCollection() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jq", ".foo");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.createObjectNode().set("foo", objectMapper.createArrayNode().add("pepe").add(false).add(3).add(objectMapper.createArrayNode().add(1.1).add(1.2).add(1.3)));
        assertEquals(Arrays.asList("pepe", false, 3, Arrays.asList(1.1, 1.2, 1.3)), parsedExpression.eval(node, Collection.class));
    }

}
