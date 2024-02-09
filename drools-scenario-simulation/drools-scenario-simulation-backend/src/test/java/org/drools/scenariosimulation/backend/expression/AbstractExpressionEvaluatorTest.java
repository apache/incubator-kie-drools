/**
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
package org.drools.scenariosimulation.backend.expression;

import java.util.ArrayList;
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

import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.drools.scenariosimulation.api.utils.ConstantsHolder.VALUE;

public class AbstractExpressionEvaluatorTest {

    private static final JsonNodeFactory factory = JsonNodeFactory.instance;
    private AbstractExpressionEvaluator expressionEvaluator;

	private Condition<ExpressionEvaluatorResult> successful= new Condition<>(x -> x.isSuccessful(), "isSuccessful");
	private Condition<ExpressionEvaluatorResult> notSuccessful= new Condition<>(x -> !x.isSuccessful(), "isSuccessful");

    @Before
    public void setUp() {
        expressionEvaluator = new FakeExpressionEvaluator();
    }
    
    @Test
    public void evaluateLiteralExpression() {
        assertThat(expressionEvaluator.evaluateLiteralExpression(null, String.class.getCanonicalName(), null)).isNull();
        assertThat(expressionEvaluator.evaluateLiteralExpression(null, List.class.getCanonicalName(), null)).isNull();
        assertThat(expressionEvaluator.evaluateLiteralExpression(null, Map.class.getCanonicalName(), null)).isNull();
    }

    @Test
    public void evaluateUnaryExpression() {
        assertThat(expressionEvaluator.evaluateUnaryExpression(null, null, String.class)).is(successful);
        assertThat(expressionEvaluator.evaluateUnaryExpression(null, null, Map.class)).is(successful);
        assertThat(expressionEvaluator.evaluateUnaryExpression(null, null, List.class)).is(successful);
    }

    @Test
    public void convertList() {
        ArrayNode jsonNodes = new ArrayNode(factory);
        ObjectNode objectNode = new ObjectNode(factory);
        objectNode.put(VALUE, "data");
        jsonNodes.add(objectNode);

        List<Object> objects = expressionEvaluator.createAndFillList(jsonNodes, new ArrayList<>(), List.class.getCanonicalName(), List.of(String.class.getCanonicalName()));
        
        assertThat(objects).containsExactly("data");
    }

    @Test
    public void convertObject_simpleList() {
        ObjectNode objectNode = new ObjectNode(factory);
        objectNode.put("key1", "Polissena");
        objectNode.put("key2", "Antonia");

        Object result  = expressionEvaluator.createAndFillObject(objectNode, new HashMap<>(), Map.class.getCanonicalName(), List.of(String.class.getCanonicalName()));
        
        assertThat(result).isInstanceOf(Map.class);
        
        Map<String, Object> resultMap = (Map<String, Object>) result;        
        assertThat(resultMap).hasSize(2).containsEntry("key1", "Polissena").containsEntry("key2", "Antonia");
    }

    
    @Test
    public void convertObject_singleLevel() {
        ObjectNode objectNode = new ObjectNode(factory);
        objectNode.put("age", "1");
        objectNode.put("name", "FS");

        Object result = expressionEvaluator.createAndFillObject(objectNode, new HashMap<>(), Map.class.getCanonicalName(), List.of(String.class.getCanonicalName()));

        assertThat(result).isInstanceOf(Map.class);
        
        Map<String, Object> resultMap = (Map<String, Object>) result;
        assertThat(resultMap).hasSize(2).containsEntry("age",  "1").containsEntry("name", "FS");
    }
    
    @Test
    public void convertObject_nestedObject() {
        ObjectNode objectNode = new ObjectNode(factory);
        ObjectNode nestedObject = new ObjectNode(factory);
        objectNode.set("nested", nestedObject);
        nestedObject.put("field", "fieldValue");

        Object result = expressionEvaluator.createAndFillObject(objectNode, new HashMap<>(), String.class.getCanonicalName(), List.of());

        assertThat(result).isInstanceOf(Map.class);
        
        Map<String, Object>resultMap = (Map<String, Object>) result;
        assertThat(resultMap).hasSize(1);
        
        Map<String, Object> nested = (Map<String, Object>) resultMap.get("nested");
        assertThat(nested).hasSize(1).containsEntry("field", "fieldValue");
    }
    
    @Test
    public void convertObject_nestedList() {
        ObjectNode objectNode = new ObjectNode(factory);
        ArrayNode jsonNodes = new ArrayNode(factory);
        objectNode.set("listField", jsonNodes);
        ObjectNode nestedObject = new ObjectNode(factory);
        nestedObject.put("field", "fieldValue");
        jsonNodes.add(nestedObject);

        Object result = expressionEvaluator.createAndFillObject(objectNode, new HashMap<>(), String.class.getCanonicalName(), List.of());

        assertThat(result).isInstanceOf(Map.class);
        
        Map<String, Object> resultMap = (Map<String, Object>) result;
        assertThat(resultMap).hasSize(1);

        List<Map<String, Object>> nestedList = (List<Map<String, Object>>) resultMap.get("listField");
        assertThat(nestedList).hasSize(1);
        assertThat(nestedList.get(0)).containsEntry("field", "fieldValue");
    }

    @Test
    public void isSimpleTypeNode_emptyNode() {
        assertThat(expressionEvaluator.isSimpleTypeNode(new ArrayNode(factory))).isFalse();
    }
    
    @Test
    public void isSimpleTypeNode_nodeWithValueField() {
        ObjectNode jsonNode = new ObjectNode(factory);
        jsonNode.set(VALUE, new TextNode("test"));

        assertThat(expressionEvaluator.isSimpleTypeNode(jsonNode)).isTrue();
    }

    @Test
    public void isSimpleTypeNode_nodeWithValueFieldAndOtherField() {
        ObjectNode jsonNode = new ObjectNode(factory);
        jsonNode.set(VALUE, new TextNode("test"));

        assertThat(expressionEvaluator.isSimpleTypeNode(jsonNode)).isTrue();
    }

    @Test
    public void isSimpleTypeNode_nodeWithOtherField() {
        ObjectNode jsonNode = new ObjectNode(factory);
        jsonNode.set("otherField", new TextNode("testValue"));
        jsonNode.set(VALUE, new TextNode("test"));

        assertThat(expressionEvaluator.isSimpleTypeNode(jsonNode)).isFalse();
    }
    
    @Test
    public void getSimpleTypeNodeTextValue_noSimpleTypeCausesException() {
        assertThatThrownBy(() -> expressionEvaluator.getSimpleTypeNodeTextValue(new ArrayNode(factory)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter does not contains a simple type");
    }

    @Test
    public void getSimpleTypeNodeTextValue_textNode() {
        ObjectNode jsonNode = new ObjectNode(factory);
        jsonNode.set(VALUE, new TextNode("testValue"));

        assertThat(expressionEvaluator.getSimpleTypeNodeTextValue(jsonNode)).isEqualTo("testValue");
    }
    
    @Test
    public void getSimpleTypeNodeTextValue_intNode() {
        ObjectNode jsonNode = new ObjectNode(factory);
        jsonNode.set(VALUE, new IntNode(10));

        assertThat(expressionEvaluator.getSimpleTypeNodeTextValue(jsonNode)).isNull();
    }
    
    @Test
    public void isNodeEmpty_objectNode() {
        ObjectNode objectNode = new ObjectNode(factory);
        
        assertThat(expressionEvaluator.isNodeEmpty(objectNode)).isTrue();
    }

    @Test
    public void isNodeEmpty_arrayNode() {
        ArrayNode arrayNode = new ArrayNode(factory);

        assertThat(expressionEvaluator.isNodeEmpty(arrayNode)).isTrue();
    }

    @Test
    public void isNodeEmpty_objectNodeWithArrayNode() {
        ObjectNode objectNode = new ObjectNode(factory);
        objectNode.set("empty array", new ArrayNode(factory));

        assertThat(expressionEvaluator.isNodeEmpty(objectNode)).isTrue();
    }

    @Test
    public void isNodeEmpty_objectNodeWithTextNode() {
        ObjectNode objectNode = new ObjectNode(factory);
        objectNode.set("key", new TextNode(VALUE));

        assertThat(expressionEvaluator.isNodeEmpty(objectNode)).isFalse();
    }
    

    @Test
    public void isNodeEmpty_arrayNodeWithTextNode() {
        ArrayNode arrayNode = new ArrayNode(factory);
        arrayNode.add(new TextNode(VALUE));
        
        assertThat(expressionEvaluator.isNodeEmpty(arrayNode)).isFalse();
    }
    
    @Test
    public void isNodeEmpty_textNode() {
        assertThat(expressionEvaluator.isNodeEmpty(new TextNode(""))).isTrue();
        assertThat(expressionEvaluator.isNodeEmpty(new TextNode(null))).isTrue();
        assertThat(expressionEvaluator.isNodeEmpty(new TextNode(VALUE))).isFalse();
    }
    
    
    @Test
    public void isListEmpty_noNode() {
        ArrayNode json = new ArrayNode(factory);
        
        assertThat(expressionEvaluator.isListEmpty(json)).isTrue();
    }

    @Test
    public void isListEmpty_emptyNode() {
        ArrayNode json = new ArrayNode(factory);
        ObjectNode nestedNode = new ObjectNode(factory);
        json.add(nestedNode);
        
        assertThat(expressionEvaluator.isListEmpty(json)).isTrue();
    }

    
    @Test
    public void isListEmpty_nodeWithEmptyField() {
        ArrayNode json = new ArrayNode(factory);
        ObjectNode nestedNode = new ObjectNode(factory);
        json.add(nestedNode);
        nestedNode.set("emptyField", new TextNode(""));
        
        assertThat(expressionEvaluator.isListEmpty(json)).isTrue();
    }
    
    @Test
    public void isListEmpty_nodeWithNonEmptyField() {
        ArrayNode json = new ArrayNode(factory);
        ObjectNode nestedNode = new ObjectNode(factory);
        json.add(nestedNode);
        nestedNode.set("notEmptyField", new TextNode("text"));
        
        assertThat(expressionEvaluator.isListEmpty(json)).isFalse();
    }
    

    @Test
    public void isObjectEmpty_nodeWithNoField() {
        ObjectNode json = new ObjectNode(factory);

        assertThat(expressionEvaluator.isObjectEmpty(json)).isTrue();
    }
    
    @Test
    public void isObjectEmpty_nodeWithEmptyField() {
        ObjectNode json = new ObjectNode(factory);
        ObjectNode nestedNode = new ObjectNode(factory);
        json.set("emptyField", nestedNode);

        assertThat(expressionEvaluator.isObjectEmpty(json)).isTrue();
    }
    
    @Test
    public void isObjectEmpty_nodeWithNonEmptyField() {
        ObjectNode json = new ObjectNode(factory);
        ObjectNode nestedNode = new ObjectNode(factory);
        json.set("emptyField", nestedNode);
        nestedNode.set("notEmptyField", new TextNode("text"));

        assertThat(expressionEvaluator.isObjectEmpty(json)).isFalse();
    }


    @Test
    public void isEmptyText() {
        assertThat(expressionEvaluator.isEmptyText(new TextNode(""))).isTrue();
        assertThat(expressionEvaluator.isEmptyText(new TextNode(VALUE))).isFalse();
        assertThat(expressionEvaluator.isEmptyText(new ObjectNode(factory))).isTrue();
    }

    @Test
    public void isStructuredInput() {
        assertThat(expressionEvaluator.isStructuredInput(List.class.getCanonicalName())).isTrue();
        assertThat(expressionEvaluator.isStructuredInput(ArrayList.class.getCanonicalName())).isTrue();
        assertThat(expressionEvaluator.isStructuredInput(LinkedList.class.getCanonicalName())).isTrue();
        assertThat(expressionEvaluator.isStructuredInput(Map.class.getCanonicalName())).isTrue();
        assertThat(expressionEvaluator.isStructuredInput(HashMap.class.getCanonicalName())).isTrue();
        assertThat(expressionEvaluator.isStructuredInput(LinkedHashMap.class.getCanonicalName())).isTrue();
        assertThat(expressionEvaluator.isStructuredInput(Set.class.getCanonicalName())).isFalse();
        assertThat(expressionEvaluator.isStructuredInput(Integer.class.getCanonicalName())).isFalse();
        assertThat(expressionEvaluator.isStructuredInput(String.class.getCanonicalName())).isFalse();
    }
}