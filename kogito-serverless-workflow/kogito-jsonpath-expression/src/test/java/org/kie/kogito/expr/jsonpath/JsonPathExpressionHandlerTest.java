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
package org.kie.kogito.expr.jsonpath;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.process.expr.Expression;
import org.kie.kogito.process.expr.ExpressionHandlerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonPathExpressionHandlerTest {

    private KogitoProcessContext context;

    @Test
    void testStringExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jsonpath", "$.foo");
        JsonNode node = new ObjectMapper().createObjectNode().put("foo", "javierito");
        assertEquals("javierito", parsedExpression.eval(node, String.class, context));
    }

    @Test
    void testBooleanExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jsonpath", "$.foo");
        JsonNode node = new ObjectMapper().createObjectNode().put("foo", true);
        assertTrue(parsedExpression.eval(node, Boolean.class, context));
    }

    @Test
    void testJsonNodeExpression() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jsonpath", "$.foo");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode compositeNode = mapper.createObjectNode().put("name", "Javierito");
        JsonNode node = mapper.createObjectNode().set("foo", compositeNode);
        assertEquals("Javierito", parsedExpression.eval(node, ObjectNode.class, context).get("name").asText());
    }

    @Test
    void testCollection() {
        Expression parsedExpression = ExpressionHandlerFactory.get("jsonpath", "$.foo");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.createObjectNode().set("foo", objectMapper.createArrayNode().add("pepe").add(false).add(3).add(objectMapper.createArrayNode().add(1.1).add(1.2).add(1.3)));
        assertEquals(Arrays.asList("pepe", false, 3, Arrays.asList(1.1, 1.2, 1.3)), parsedExpression.eval(node, Collection.class, context));
    }
}
