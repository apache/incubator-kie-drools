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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static java.util.Collections.emptyList;
import static org.drools.scenariosimulation.api.utils.ConstantsHolder.VALUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AbstractExpressionEvaluatorTest {

    private static final JsonNodeFactory factory = JsonNodeFactory.instance;
    private static final AbstractExpressionEvaluator expressionEvaluatorLocal = new AbstractExpressionEvaluator() {

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

        @SuppressWarnings("unchecked")
        @Override
        protected void setField(Object toReturn, String fieldName, Object fieldValue) {
            ((Map) toReturn).put(fieldName, fieldValue);
        }

        @Override
        protected Map.Entry<String, List<String>> getFieldClassNameAndGenerics(Object element, String fieldName, String className, List<String> genericClasses) {
            return new AbstractMap.SimpleEntry<>("", Collections.singletonList(""));
        }
    };

    @Test
    public void evaluateLiteralExpression() {
        assertNull(expressionEvaluatorLocal.evaluateLiteralExpression(null, String.class.getCanonicalName(), null));
        assertNull(expressionEvaluatorLocal.evaluateLiteralExpression(null, List.class.getCanonicalName(), null));
        assertNull(expressionEvaluatorLocal.evaluateLiteralExpression(null, Map.class.getCanonicalName(), null));
    }

    @Test
    public void evaluateUnaryExpression() {
        assertTrue(expressionEvaluatorLocal.evaluateUnaryExpression(null, null, String.class).isSuccessful());
        assertTrue(expressionEvaluatorLocal.evaluateUnaryExpression(null, null, Map.class).isSuccessful());
        assertTrue(expressionEvaluatorLocal.evaluateUnaryExpression(null, null, List.class).isSuccessful());
    }

    @Test
    public void convertList() {
        // Test simple list
        ArrayNode jsonNodes = new ArrayNode(factory);
        ObjectNode objectNode = new ObjectNode(factory);
        objectNode.put(VALUE, "data");
        jsonNodes.add(objectNode);

        List<Object> objects = expressionEvaluatorLocal.createAndFillList(jsonNodes,
                                                                          new ArrayList<>(),
                                                                          List.class.getCanonicalName(),
                                                                          Collections.singletonList(String.class.getCanonicalName()));
        assertEquals("data", objects.get(0));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void convertObject() {
        // Test simple list
        ObjectNode objectNode = new ObjectNode(factory);
        ObjectNode simpleValue =  new ObjectNode(factory);
        ObjectNode simpleValue2 =  new ObjectNode(factory);
        simpleValue.put(VALUE, "Polissena");
        simpleValue2.put(VALUE, "Antonia");
        objectNode.put("key1", simpleValue);
        objectNode.put("key2", simpleValue2);

        Object result  = expressionEvaluatorLocal.createAndFillObject(objectNode,
                                                                      new HashMap<>(),
                                                                      Map.class.getCanonicalName(),
                                                                      Collections.singletonList(String.class.getCanonicalName()));
        assertTrue(result instanceof Map);
        Map<String, Object> resultMap = (Map<String, Object>) result;
        assertEquals(2, resultMap.size());
        assertEquals("Polissena", resultMap.get("key1"));
        assertEquals("Antonia", resultMap.get("key2"));

        // single level
        objectNode.removeAll();
        objectNode.put("age", "1");
        objectNode.put("name", "FS");
        result = expressionEvaluatorLocal.createAndFillObject(objectNode,
                                                              new HashMap<>(),
                                                              Map.class.getCanonicalName(),
                                                              Collections.singletonList(String.class.getCanonicalName()));

        assertTrue(result instanceof Map);
        resultMap = (Map<String, Object>) result;

        assertEquals("1", resultMap.get("age"));
        assertEquals("FS", resultMap.get("name"));
        assertEquals(2, resultMap.size());

        // nested object
        objectNode.removeAll();
        ObjectNode nestedObject = new ObjectNode(factory);
        objectNode.set("nested", nestedObject);
        nestedObject.put("field", "fieldValue");

        result = expressionEvaluatorLocal.createAndFillObject(objectNode,
                                                              new HashMap<>(),
                                                              String.class.getCanonicalName(),
                                                              emptyList());

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

        result = expressionEvaluatorLocal.createAndFillObject(objectNode,
                                                              new HashMap<>(),
                                                              String.class.getCanonicalName(),
                                                              emptyList());

        assertTrue(result instanceof Map);
        resultMap = (Map<String, Object>) result;

        assertEquals(1, resultMap.size());
        List<Map<String, Object>> nestedList = (List<Map<String, Object>>) resultMap.get("listField");
        assertEquals(1, nestedList.size());
        assertEquals("fieldValue", nestedList.get(0).get("field"));
    }

    @Test
    public void isSimpleTypeNode() {
        assertFalse(expressionEvaluatorLocal.isSimpleTypeNode(new ArrayNode(factory)));

        ObjectNode jsonNode = new ObjectNode(factory);

        jsonNode.set(VALUE, new TextNode("test"));
        assertTrue(expressionEvaluatorLocal.isSimpleTypeNode(jsonNode));

        jsonNode.set("otherField", new TextNode("testValue"));

        assertFalse(expressionEvaluatorLocal.isSimpleTypeNode(jsonNode));
    }

    @Test
    public void getSimpleTypeNodeTextValue() {
        Assertions.assertThatThrownBy(() -> expressionEvaluatorLocal.getSimpleTypeNodeTextValue(new ArrayNode(factory)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter does not contains a simple type");

        ObjectNode jsonNode = new ObjectNode(factory);

        jsonNode.set(VALUE, new TextNode("testValue"));
        assertEquals("testValue", expressionEvaluatorLocal.getSimpleTypeNodeTextValue(jsonNode));

        jsonNode.set(VALUE, new IntNode(10));
        assertNull(expressionEvaluatorLocal.getSimpleTypeNodeTextValue(jsonNode));
    }

    @Test
    public void isNodeEmpty() {
        ObjectNode objectNode = new ObjectNode(factory);
        assertTrue(expressionEvaluatorLocal.isNodeEmpty(objectNode));
        objectNode.set("empty array", new ArrayNode(factory));
        assertTrue(expressionEvaluatorLocal.isNodeEmpty(objectNode));
        objectNode.set("key", new TextNode(VALUE));
        assertFalse(expressionEvaluatorLocal.isNodeEmpty(objectNode));

        ArrayNode arrayNode = new ArrayNode(factory);
        assertTrue(expressionEvaluatorLocal.isNodeEmpty(arrayNode));
        arrayNode.add(new TextNode(VALUE));
        assertFalse(expressionEvaluatorLocal.isNodeEmpty(arrayNode));

        assertTrue(expressionEvaluatorLocal.isNodeEmpty(new TextNode("")));
        assertTrue(expressionEvaluatorLocal.isNodeEmpty(new TextNode(null)));
        assertFalse(expressionEvaluatorLocal.isNodeEmpty(new TextNode(VALUE)));
    }

    @Test
    public void isListEmpty() {
        ArrayNode json = new ArrayNode(factory);
        assertTrue(expressionEvaluatorLocal.isListEmpty(json));
        ObjectNode nestedNode = new ObjectNode(factory);
        json.add(nestedNode);
        assertTrue(expressionEvaluatorLocal.isListEmpty(json));
        nestedNode.set("emptyField", new TextNode(""));
        assertTrue(expressionEvaluatorLocal.isListEmpty(json));
        nestedNode.set("notEmptyField", new TextNode("text"));
        assertFalse(expressionEvaluatorLocal.isListEmpty(json));
    }

    @Test
    public void isObjectEmpty() {
        ObjectNode json = new ObjectNode(factory);
        assertTrue(expressionEvaluatorLocal.isObjectEmpty(json));
        ObjectNode nestedNode = new ObjectNode(factory);
        json.set("emptyField", nestedNode);
        assertTrue(expressionEvaluatorLocal.isObjectEmpty(json));
        nestedNode.set("notEmptyField", new TextNode("text"));
        assertFalse(expressionEvaluatorLocal.isObjectEmpty(json));
    }

    @Test
    public void isEmptyText() {
        assertTrue(expressionEvaluatorLocal.isEmptyText(new TextNode("")));
        assertFalse(expressionEvaluatorLocal.isEmptyText(new TextNode(VALUE)));
        assertTrue(expressionEvaluatorLocal.isEmptyText(new ObjectNode(factory)));
    }

    @Test
    public void isStructuredInput() {
        assertTrue(expressionEvaluatorLocal.isStructuredInput(List.class.getCanonicalName()));
        assertTrue(expressionEvaluatorLocal.isStructuredInput(ArrayList.class.getCanonicalName()));
        assertTrue(expressionEvaluatorLocal.isStructuredInput(LinkedList.class.getCanonicalName()));
        assertTrue(expressionEvaluatorLocal.isStructuredInput(Map.class.getCanonicalName()));
        assertTrue(expressionEvaluatorLocal.isStructuredInput(HashMap.class.getCanonicalName()));
        assertTrue(expressionEvaluatorLocal.isStructuredInput(LinkedHashMap.class.getCanonicalName()));
        assertFalse(expressionEvaluatorLocal.isStructuredInput(Set.class.getCanonicalName()));
        assertFalse(expressionEvaluatorLocal.isStructuredInput(Integer.class.getCanonicalName()));
        assertFalse(expressionEvaluatorLocal.isStructuredInput(String.class.getCanonicalName()));
    }
}