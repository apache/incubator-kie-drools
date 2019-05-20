/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.scenariosimulation.backend.expression;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AbstractExpressionEvaluatorTest {

    JsonNodeFactory factory = JsonNodeFactory.instance;

    @Test
    public void convertList() {
        // Test simple list
        ArrayNode jsonNodes = new ArrayNode(factory);
        ObjectNode objectNode = new ObjectNode(factory);
        objectNode.put("value", "data");
        jsonNodes.add(objectNode);

        List<Object> objects = expressionEvaluatorMock.createAndFillList(jsonNodes,
                                                                         new ArrayList<>(),
                                                                         List.class.getCanonicalName(),
                                                                         Collections.singletonList(String.class.getCanonicalName()));
        assertEquals("data", objects.get(0));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void convertObject() {
        // single level
        ObjectNode objectNode = new ObjectNode(factory);
        objectNode.put("age", "1");

        Object result = expressionEvaluatorMock.createAndFillObject(objectNode,
                                                                    new HashMap<>(),
                                                                    Map.class.getCanonicalName(),
                                                                    Collections.singletonList(String.class.getCanonicalName()));

        assertTrue(result instanceof Map);
        Map<String, Object> resultMap = (Map<String, Object>) result;

        assertEquals("1", resultMap.get("age"));

        // nested object
        objectNode.removeAll();
        ObjectNode nestedObject = new ObjectNode(factory);
        objectNode.set("nested", nestedObject);
        nestedObject.put("field", "fieldValue");

        result = expressionEvaluatorMock.createAndFillObject(objectNode,
                                                             new HashMap<>(),
                                                             String.class.getCanonicalName(),
                                                             Collections.emptyList());

        assertTrue(result instanceof Map);
        resultMap = (Map<String, Object>) result;

        assertEquals(1, resultMap.size());
        Map<String, Object> nested = (Map<String, Object>) resultMap.get("nested");
        assertEquals(1, nested.size());
        assertEquals("fieldValue", nested.get("field"));

        // nested list
        objectNode.removeAll();
        ArrayNode jsonNodes = new ArrayNode(factory);
        objectNode.set("listField", jsonNodes);
        jsonNodes.add(nestedObject);

        result = expressionEvaluatorMock.createAndFillObject(objectNode,
                                                             new HashMap<>(),
                                                             String.class.getCanonicalName(),
                                                             Collections.emptyList());

        assertTrue(result instanceof Map);
        resultMap = (Map<String, Object>) result;

        assertEquals(1, resultMap.size());
        List<Map<String, Object>> nestedList = (List<Map<String, Object>>) resultMap.get("listField");
        assertEquals(1, nestedList.size());
        assertEquals("fieldValue", nestedList.get(0).get("field"));
    }

    AbstractExpressionEvaluator expressionEvaluatorMock = new AbstractExpressionEvaluator() {

        @Override
        public boolean evaluateUnaryExpression(Object rawExpression, Object resultValue, Class<?> resultClass) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object evaluateLiteralExpression(String className, List<String> genericClasses, Object raw) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String fromObjectToExpression(Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected Object extractFieldValue(Object result, String fieldName) {
            return result;
        }

        @Override
        protected boolean internalUnaryEvaluation(String rawExpression, Object resultValue, Class<?> resultClass, boolean skipEmptyString) {
            return true;
        }

        @Override
        protected Object internalLiteralEvaluation(String raw, String className) {
            return raw;
        }

        @Override
        protected Object createObject(String className, List<String> genericClasses) {
            return new HashMap<>();
        }

        @Override
        protected void setField(Object toReturn, String fieldName, Object fieldValue) {
            ((Map) toReturn).put(fieldName, fieldValue);
        }

        @Override
        protected Map.Entry<String, List<String>> getFieldClassNameAndGenerics(Object element, String fieldName, String className, List<String> genericClasses) {
            return new AbstractMap.SimpleEntry<>("", Collections.singletonList(""));
        }
    };
}