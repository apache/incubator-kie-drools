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
import org.junit.Test;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.drools.scenariosimulation.api.utils.ConstantsHolder.VALUE;

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
        assertThat(expressionEvaluatorLocal.evaluateLiteralExpression(null, String.class.getCanonicalName(), null)).isNull();
        assertThat(expressionEvaluatorLocal.evaluateLiteralExpression(null, List.class.getCanonicalName(), null)).isNull();
        assertThat(expressionEvaluatorLocal.evaluateLiteralExpression(null, Map.class.getCanonicalName(), null)).isNull();
    }

    @Test
    public void evaluateUnaryExpression() {
        assertThat(expressionEvaluatorLocal.evaluateUnaryExpression(null, null, String.class).isSuccessful()).isTrue();
        assertThat(expressionEvaluatorLocal.evaluateUnaryExpression(null, null, Map.class).isSuccessful()).isTrue();
        assertThat(expressionEvaluatorLocal.evaluateUnaryExpression(null, null, List.class).isSuccessful()).isTrue();
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
        assertThat(objects.get(0)).isEqualTo("data");
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
        assertThat(result instanceof Map).isTrue();
        Map<String, Object> resultMap = (Map<String, Object>) result;
        assertThat(resultMap.size()).isEqualTo(2);
        assertThat(resultMap.get("key1")).isEqualTo("Polissena");
        assertThat(resultMap.get("key2")).isEqualTo("Antonia");

        // single level
        objectNode.removeAll();
        objectNode.put("age", "1");
        objectNode.put("name", "FS");
        result = expressionEvaluatorLocal.createAndFillObject(objectNode,
                                                              new HashMap<>(),
                                                              Map.class.getCanonicalName(),
                                                              Collections.singletonList(String.class.getCanonicalName()));

        assertThat(result instanceof Map).isTrue();
        resultMap = (Map<String, Object>) result;

        assertThat(resultMap.get("age")).isEqualTo("1");
        assertThat(resultMap.get("name")).isEqualTo("FS");
        assertThat(resultMap.size()).isEqualTo(2);

        // nested object
        objectNode.removeAll();
        ObjectNode nestedObject = new ObjectNode(factory);
        objectNode.set("nested", nestedObject);
        nestedObject.put("field", "fieldValue");

        result = expressionEvaluatorLocal.createAndFillObject(objectNode,
                                                              new HashMap<>(),
                                                              String.class.getCanonicalName(),
                                                              emptyList());

        assertThat(result instanceof Map).isTrue();
        resultMap = (Map<String, Object>) result;

        assertThat(resultMap.size()).isEqualTo(1);
        Map<String, Object> nested = (Map<String, Object>) resultMap.get("nested");
        assertThat(nested.size()).isEqualTo(1);
        assertThat(nested.get("field")).isEqualTo("fieldValue");

        // nested list
        objectNode.removeAll();
        ArrayNode jsonNodes = new ArrayNode(factory);
        objectNode.set("listField", jsonNodes);
        jsonNodes.add(nestedObject);

        result = expressionEvaluatorLocal.createAndFillObject(objectNode,
                                                              new HashMap<>(),
                                                              String.class.getCanonicalName(),
                                                              emptyList());

        assertThat(result instanceof Map).isTrue();
        resultMap = (Map<String, Object>) result;

        assertThat(resultMap.size()).isEqualTo(1);
        List<Map<String, Object>> nestedList = (List<Map<String, Object>>) resultMap.get("listField");
        assertThat(nestedList.size()).isEqualTo(1);
        assertThat(nestedList.get(0).get("field")).isEqualTo("fieldValue");
    }

    @Test
    public void isSimpleTypeNode() {
        assertThat(expressionEvaluatorLocal.isSimpleTypeNode(new ArrayNode(factory))).isFalse();

        ObjectNode jsonNode = new ObjectNode(factory);

        jsonNode.set(VALUE, new TextNode("test"));
        assertThat(expressionEvaluatorLocal.isSimpleTypeNode(jsonNode)).isTrue();

        jsonNode.set("otherField", new TextNode("testValue"));

        assertThat(expressionEvaluatorLocal.isSimpleTypeNode(jsonNode)).isFalse();
    }

    @Test
    public void getSimpleTypeNodeTextValue() {
        assertThatThrownBy(() -> expressionEvaluatorLocal.getSimpleTypeNodeTextValue(new ArrayNode(factory)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter does not contains a simple type");

        ObjectNode jsonNode = new ObjectNode(factory);

        jsonNode.set(VALUE, new TextNode("testValue"));
        assertThat(expressionEvaluatorLocal.getSimpleTypeNodeTextValue(jsonNode)).isEqualTo("testValue");

        jsonNode.set(VALUE, new IntNode(10));
        assertThat(expressionEvaluatorLocal.getSimpleTypeNodeTextValue(jsonNode)).isNull();
    }

    @Test
    public void isNodeEmpty() {
        ObjectNode objectNode = new ObjectNode(factory);
        assertThat(expressionEvaluatorLocal.isNodeEmpty(objectNode)).isTrue();
        objectNode.set("empty array", new ArrayNode(factory));
        assertThat(expressionEvaluatorLocal.isNodeEmpty(objectNode)).isTrue();
        objectNode.set("key", new TextNode(VALUE));
        assertThat(expressionEvaluatorLocal.isNodeEmpty(objectNode)).isFalse();

        ArrayNode arrayNode = new ArrayNode(factory);
        assertThat(expressionEvaluatorLocal.isNodeEmpty(arrayNode)).isTrue();
        arrayNode.add(new TextNode(VALUE));
        assertThat(expressionEvaluatorLocal.isNodeEmpty(arrayNode)).isFalse();

        assertThat(expressionEvaluatorLocal.isNodeEmpty(new TextNode(""))).isTrue();
        assertThat(expressionEvaluatorLocal.isNodeEmpty(new TextNode(null))).isTrue();
        assertThat(expressionEvaluatorLocal.isNodeEmpty(new TextNode(VALUE))).isFalse();
    }

    @Test
    public void isListEmpty() {
        ArrayNode json = new ArrayNode(factory);
        assertThat(expressionEvaluatorLocal.isListEmpty(json)).isTrue();
        ObjectNode nestedNode = new ObjectNode(factory);
        json.add(nestedNode);
        assertThat(expressionEvaluatorLocal.isListEmpty(json)).isTrue();
        nestedNode.set("emptyField", new TextNode(""));
        assertThat(expressionEvaluatorLocal.isListEmpty(json)).isTrue();
        nestedNode.set("notEmptyField", new TextNode("text"));
        assertThat(expressionEvaluatorLocal.isListEmpty(json)).isFalse();
    }

    @Test
    public void isObjectEmpty() {
        ObjectNode json = new ObjectNode(factory);
        assertThat(expressionEvaluatorLocal.isObjectEmpty(json)).isTrue();
        ObjectNode nestedNode = new ObjectNode(factory);
        json.set("emptyField", nestedNode);
        assertThat(expressionEvaluatorLocal.isObjectEmpty(json)).isTrue();
        nestedNode.set("notEmptyField", new TextNode("text"));
        assertThat(expressionEvaluatorLocal.isObjectEmpty(json)).isFalse();
    }

    @Test
    public void isEmptyText() {
        assertThat(expressionEvaluatorLocal.isEmptyText(new TextNode(""))).isTrue();
        assertThat(expressionEvaluatorLocal.isEmptyText(new TextNode(VALUE))).isFalse();
        assertThat(expressionEvaluatorLocal.isEmptyText(new ObjectNode(factory))).isTrue();
    }

    @Test
    public void isStructuredInput() {
        assertThat(expressionEvaluatorLocal.isStructuredInput(List.class.getCanonicalName())).isTrue();
        assertThat(expressionEvaluatorLocal.isStructuredInput(ArrayList.class.getCanonicalName())).isTrue();
        assertThat(expressionEvaluatorLocal.isStructuredInput(LinkedList.class.getCanonicalName())).isTrue();
        assertThat(expressionEvaluatorLocal.isStructuredInput(Map.class.getCanonicalName())).isTrue();
        assertThat(expressionEvaluatorLocal.isStructuredInput(HashMap.class.getCanonicalName())).isTrue();
        assertThat(expressionEvaluatorLocal.isStructuredInput(LinkedHashMap.class.getCanonicalName())).isTrue();
        assertThat(expressionEvaluatorLocal.isStructuredInput(Set.class.getCanonicalName())).isFalse();
        assertThat(expressionEvaluatorLocal.isStructuredInput(Integer.class.getCanonicalName())).isFalse();
        assertThat(expressionEvaluatorLocal.isStructuredInput(String.class.getCanonicalName())).isFalse();
    }
}