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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.node.TextNode;
import org.drools.scenariosimulation.backend.model.ListMapClass;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.assertj.core.api.Condition;

public class BaseExpressionEvaluatorTest {
	
	private Condition<ExpressionEvaluatorResult> successful= new Condition<>(x -> x.isSuccessful(), "isSuccessful");
	private Condition<ExpressionEvaluatorResult> notSuccessful= new Condition<>(x -> !x.isSuccessful(), "isNotSuccessful");

    private final static ClassLoader classLoader = BaseExpressionEvaluatorTest.class.getClassLoader();
    private AbstractExpressionEvaluator expressionEvaluator;
    
    @Before
    public void setUp() {
    	expressionEvaluator = new BaseExpressionEvaluator(classLoader);
    }

    @Test
    public void evaluateLiteralExpression() {
        assertThat(expressionEvaluator.evaluateLiteralExpression("", Object.class.getCanonicalName(), List.of())).isEqualTo("");

        assertThat(expressionEvaluator.evaluateLiteralExpression("SimpleString", String.class.getCanonicalName(), List.of())).isEqualTo("SimpleString");

        assertThat(expressionEvaluator.evaluateLiteralExpression("= SimpleString", String.class.getCanonicalName(), List.of())).isEqualTo("SimpleString");

        assertThat(expressionEvaluator.evaluateLiteralExpression(null, String.class.getCanonicalName(), List.of())).isNull();
    }

    @Test
    public void createObject() {
        assertThat(expressionEvaluator.createObject(String.class.getCanonicalName(), List.of())).isNotNull();
        assertThat(expressionEvaluator.createObject(Map.class.getCanonicalName(), List.of(String.class.getCanonicalName(), String.class.getCanonicalName()))).isInstanceOf(Map.class);

        assertThatThrownBy(() -> expressionEvaluator.createObject("com.invalid.class.Name", List.of())).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Impossible to instantiate com.invalid.class.Name");
    }

    @Test
    public void verifyNullTest() {
        assertThat(expressionEvaluator.verifyResult("[]", null, null)).is(successful);
        assertThat(expressionEvaluator.verifyResult("[{\"value\" : \"result\"}]", null, null)).is(notSuccessful);
    }

    @Test
    public void nullResultTest() {
        assertThat(expressionEvaluator.evaluateUnaryExpression("> 1", null, null)).is(notSuccessful);
        assertThat(expressionEvaluator.evaluateUnaryExpression("", null, null)).is(successful);
        assertThat(expressionEvaluator.evaluateUnaryExpression(null, null, null)).is(successful);

        assertThat(expressionEvaluator.evaluateUnaryExpression("{}", null, Map.class)).is(successful);
        assertThat(expressionEvaluator.evaluateUnaryExpression("[]", null, List.class)).is(successful);

        String mapOfListJson = "{\"key1\" : [{\"value\" : \"value1\"}, {\"value\" : \"value2\"}]}";
        assertThat(expressionEvaluator.evaluateUnaryExpression(mapOfListJson, Map.of(), Map.class)).is(notSuccessful);
    }
    
    public void convertResult_list() {
        String listJsonString = "[{\"value\" : \"10\"}, {\"value\" : \"12\"}]";

        List<Integer> result = (List<Integer>) expressionEvaluator.convertResult(listJsonString, List.class.getCanonicalName(), List.of(Integer.class.getCanonicalName()));

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(10, 12);
    }
    
    @Test
    public void convertResult_mapOfStringToString() {
        List<String> genericClasses = List.of(String.class.getCanonicalName(), String.class.getCanonicalName());
        String givenWorkbenchMapString = "{ \"Home\": { \"value\": \"123 Any Street\" } }";

        Map<String, String> parsedWorkbench = ((Map<String, String>) expressionEvaluator.convertResult(givenWorkbenchMapString, Map.class.getCanonicalName(), genericClasses));

        assertThat(parsedWorkbench).hasSize(1).containsEntry("Home", "123 Any Street");
    }
    
    @Test
    public void evaluateUnaryExpression_mapOfStringToString() {
        String stringToString = "{\"key1\" : {\"value\" : \"value1\"}, \"key2\" : {\"value\" : \"value2\"}}";

        assertThat(expressionEvaluator.evaluateUnaryExpression(stringToString, Map.of("key1", "value1"), Map.class)).is(notSuccessful);
        assertThat(expressionEvaluator.evaluateUnaryExpression(stringToString, Map.of("key1", "value1", "key2", "value2"), Map.class)).is(successful);
    }
    
    @Test
    public void evaluateUnaryExpression_mapOfStringToString_empty() {
        String mapOfStringJson1 = "{\"key1\" : {\"value\" : \"\"}}";

        assertThat(expressionEvaluator.evaluateUnaryExpression(mapOfStringJson1, Map.of(), Map.class)).is(successful);
        assertThat(expressionEvaluator.evaluateUnaryExpression(mapOfStringJson1, Map.of("key1", "value1"), Map.class)).is(successful);
    }

    
    @Test
    public void convertResult_mapOfStringToInteger() {
        List<String> genericClasses = List.of(String.class.getCanonicalName(), Integer.class.getCanonicalName());
        String givenWorkbenchMapInteger = "{ \"Home\": { \"value\": \"100\" } }";

        Map<String, Integer> parsedMap = ((Map<String, Integer>) expressionEvaluator.convertResult(givenWorkbenchMapInteger, Map.class.getCanonicalName(), genericClasses));

        assertThat(parsedMap).hasSize(1).containsEntry("Home", 100);
    }
    


    @SuppressWarnings("unchecked")
    @Test
    public void convertResult_listMapToString() {
        String mapJsonString = "{\"first\": {\"name\": \"John\"}}";

        Map<String, ListMapClass> parsedMap = (Map<String, ListMapClass>) expressionEvaluator.convertResult(mapJsonString, Map.class.getCanonicalName(),
                                                                                                            List.of(ListMapClass.class.getCanonicalName()));

        assertThat(parsedMap).hasSize(1);
        assertThat(parsedMap.get("first").getName()).isEqualTo("John");
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void convertResult_listMapToList() {
        String mapJsonString = "{\"first\": {\"siblings\": [{\"name\" : \"John\"}]}}";
        
        Map<String, ListMapClass> parsedMap = (Map<String, ListMapClass>) expressionEvaluator.convertResult(mapJsonString, Map.class.getCanonicalName(),
                                                                                  List.of(ListMapClass.class.getCanonicalName()));
        assertThat(parsedMap).hasSize(1);
        assertThat(parsedMap.get("first").getSiblings().get(0).getName()).isEqualTo("John");
    }
    
    
    @SuppressWarnings("unchecked")
    @Test
    public void convertResult_listMapToListMap() {
        String mapJsonString = "{\"first\": {\"phones\": {\"number\" : \"1\"}}}";

        Map<String, ListMapClass> parsedMap = (Map<String, ListMapClass>) expressionEvaluator.convertResult(mapJsonString, Map.class.getCanonicalName(),
                                                                                  List.of(ListMapClass.class.getCanonicalName()));

        assertThat(parsedMap).hasSize(1);
        assertThat(parsedMap.get("first").getPhones().get("number")).isEqualTo((Integer) 1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void convertResult_listOfComplexTypes() {

        String listJsonString = "[{\"name\": \"John\"}, " +
                "{\"name\": \"John\", \"names\" : [{\"value\": \"Anna\"}, {\"value\": \"Mario\"}]}]";

        List<ListMapClass> parsedValue = (List<ListMapClass>) expressionEvaluator.convertResult(listJsonString, List.class.getCanonicalName(),
                                                                                                List.of(ListMapClass.class.getCanonicalName()));

        assertThat(parsedValue).hasSize(2);
        assertThat(parsedValue.get(1).getNames()).hasSize(2).containsExactly("Anna", "Mario");
    }
    
    @Test
    public void verifyResult_mapOfMapsofMaps_successful() {
        String mapJsonString = "{\"first\": {\"phones\": {\"number\" : \"> 1\"}}}";

        Map<String, ListMapClass> toCheck = new HashMap<>();
        ListMapClass element = new ListMapClass();
        Map<String, Integer> phones = new HashMap<>();
        phones.put("number", 10);
        element.setPhones(phones);
        toCheck.put("first", element);

        assertThat(expressionEvaluator.verifyResult(mapJsonString, toCheck, null)).is(successful);
    }

    @Test
    public void verifyResult_mapOfMapsofMaps_notSuccessful() {
        String mapJsonString = "{\"first\": {\"phones\": {\"number\" : \"> 1\"}}}";

        Map<String, ListMapClass> toCheck = new HashMap<>();
        ListMapClass element = new ListMapClass();
        Map<String, Integer> phones = new HashMap<>();
        phones.put("number", 10);
        element.setPhones(phones);
        phones.put("number", -1);

        assertThat(expressionEvaluator.verifyResult(mapJsonString, toCheck, null)).is(notSuccessful);
    }


    @SuppressWarnings("unchecked")
    @Test
    public void verifyResult_listOfComplexTypeTest_success() {

        String listJsonString = "[{\"name\": \"John\"}, " +
                "{\"name\": \"John\", \"names\" : [{\"value\": \"Anna\"}, {\"value\": \"Mario\"}]}]";

        List<ListMapClass> parsedValue = (List<ListMapClass>) expressionEvaluator.convertResult(listJsonString, List.class.getCanonicalName(),
                                                                                                List.of(ListMapClass.class.getCanonicalName()));

        assertThat(expressionEvaluator.verifyResult(listJsonString, parsedValue, null)).is(successful);
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void verifyResult_listOfComplexTypeTest_failure() {
        String listJsonString = "[{\"name\": \"John\"}, " +
                "{\"name\": \"John\", \"names\" : [{\"value\": \"Anna\"}, {\"value\": \"Mario\"}]}]";

        List<ListMapClass> parsedValue = (List<ListMapClass>) expressionEvaluator.convertResult(listJsonString, List.class.getCanonicalName(),
                                                                                                List.of(ListMapClass.class.getCanonicalName()));
        parsedValue.get(1).setNames(new ArrayList<>());

        assertThat(expressionEvaluator.verifyResult(listJsonString, parsedValue, null)).is(notSuccessful);
    }
    
    @Test
    public void verifyResult_mapOfStringToInteger() {
        String expectWorkbenchMapInteger = "{ \"Home\": { \"value\": \"> 100\" } }";

        assertThat(expressionEvaluator.verifyResult(expectWorkbenchMapInteger, Map.of("Home", 120), null)).is(successful);
        assertThat(expressionEvaluator.verifyResult(expectWorkbenchMapInteger, Map.of("Home", 10), null)).is(notSuccessful);
    }    
    
    @Test
    public void listOfSimpleTypeTest() {
        assertThat(expressionEvaluator.verifyResult("[{\"value\" : \"> 10\"}]", List.of(13), null)).is(successful);

        assertThat(expressionEvaluator.verifyResult("[{\"value\" : \"> 100\"}]", List.of(13), null)).is(notSuccessful);

        assertThat(expressionEvaluator.verifyResult("[{\"value\" : \"\"}]", List.of(13), null)).is(successful);
    }

    @Test
    public void expressionListVerifyResultTest() {
        String expressionCollectionJsonString = new TextNode("10").toString();
        List<BigDecimal> contextValue = List.of(BigDecimal.valueOf(10));
        
        assertThatIllegalArgumentException().isThrownBy(() -> expressionEvaluator.verifyResult(expressionCollectionJsonString, contextValue, List.class));
    }

    @Test
    public void expressionMapVerifyResultTest() {
        String expressionCollectionJsonString = new TextNode("{key_a : 1}").toString();
        Map<String, BigDecimal> contextValue = Map.of("key_a", BigDecimal.valueOf(1));
        
        assertThatIllegalArgumentException().isThrownBy(() -> expressionEvaluator.verifyResult(expressionCollectionJsonString, contextValue, Map.class));
    }
}
