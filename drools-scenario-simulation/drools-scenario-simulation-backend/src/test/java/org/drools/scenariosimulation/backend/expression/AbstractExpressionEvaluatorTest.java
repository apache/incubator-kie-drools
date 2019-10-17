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
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.drools.scenariosimulation.api.utils.ConstantsHolder.VALUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AbstractExpressionEvaluatorTest {

    JsonNodeFactory factory = JsonNodeFactory.instance;

    @Test
    public void convertList() {
        // Test simple list
        ArrayNode jsonNodes = new ArrayNode(factory);
        ObjectNode objectNode = new ObjectNode(factory);
        objectNode.put(VALUE, "data");
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

    @Test
    public void isSimpleTypeNode() {
        assertFalse(expressionEvaluatorMock.isSimpleTypeNode(new ArrayNode(factory)));

        ObjectNode jsonNode = new ObjectNode(factory);

        jsonNode.set(VALUE, new TextNode("test"));
        assertTrue(expressionEvaluatorMock.isSimpleTypeNode(jsonNode));

        jsonNode.set("otherField", new TextNode("testValue"));

        assertFalse(expressionEvaluatorMock.isSimpleTypeNode(jsonNode));
    }

    @Test
    public void getSimpleTypeNodeTextValue() {
        Assertions.assertThatThrownBy(() -> expressionEvaluatorMock.getSimpleTypeNodeTextValue(new ArrayNode(factory)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter does not contains a simple type");

        ObjectNode jsonNode = new ObjectNode(factory);

        jsonNode.set(VALUE, new TextNode("testValue"));
        assertEquals("testValue", expressionEvaluatorMock.getSimpleTypeNodeTextValue(jsonNode));

        jsonNode.set(VALUE, new IntNode(10));
        assertNull(expressionEvaluatorMock.getSimpleTypeNodeTextValue(jsonNode));
    }

    @Test
    public void isNodeEmpty() {
        ObjectNode objectNode = new ObjectNode(factory);
        assertTrue(expressionEvaluatorMock.isNodeEmpty(objectNode));
        objectNode.set("empty array", new ArrayNode(factory));
        assertTrue(expressionEvaluatorMock.isNodeEmpty(objectNode));
        objectNode.set("key", new TextNode(VALUE));
        assertFalse(expressionEvaluatorMock.isNodeEmpty(objectNode));

        ArrayNode arrayNode = new ArrayNode(factory);
        assertTrue(expressionEvaluatorMock.isNodeEmpty(arrayNode));
        arrayNode.add(new TextNode(VALUE));
        assertFalse(expressionEvaluatorMock.isNodeEmpty(arrayNode));

        assertTrue(expressionEvaluatorMock.isNodeEmpty(new TextNode("")));
        assertTrue(expressionEvaluatorMock.isNodeEmpty(new TextNode(null)));
        assertFalse(expressionEvaluatorMock.isNodeEmpty(new TextNode(VALUE)));
    }

    @Test
    public void isListEmpty() {
        ArrayNode json = new ArrayNode(factory);
        assertTrue(expressionEvaluatorMock.isListEmpty(json));
        ObjectNode nestedNode = new ObjectNode(factory);
        json.add(nestedNode);
        assertTrue(expressionEvaluatorMock.isListEmpty(json));
        nestedNode.set("emptyField", new TextNode(""));
        assertTrue(expressionEvaluatorMock.isListEmpty(json));
        nestedNode.set("notEmptyField", new TextNode("text"));
        assertFalse(expressionEvaluatorMock.isListEmpty(json));
    }

    @Test
    public void isObjectEmpty() {
        ObjectNode json = new ObjectNode(factory);
        assertTrue(expressionEvaluatorMock.isObjectEmpty(json));
        ObjectNode nestedNode = new ObjectNode(factory);
        json.set("emptyField", nestedNode);
        assertTrue(expressionEvaluatorMock.isObjectEmpty(json));
        nestedNode.set("notEmptyField", new TextNode("text"));
        assertFalse(expressionEvaluatorMock.isObjectEmpty(json));
    }

    @Test
    public void isEmptyText() {
        assertTrue(expressionEvaluatorMock.isEmptyText(new TextNode("")));
        assertFalse(expressionEvaluatorMock.isEmptyText(new TextNode(VALUE)));
        assertTrue(expressionEvaluatorMock.isEmptyText(new ObjectNode(factory)));
    }

    AbstractExpressionEvaluator expressionEvaluatorMock = new AbstractExpressionEvaluator() {

        @Override
        public boolean evaluateUnaryExpression(Object rawExpression, Object resultValue, Class<?> resultClass) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object evaluateLiteralExpression(String className, List<String> genericClasses, Object rawExpression) {
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