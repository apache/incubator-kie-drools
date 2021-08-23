/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.node.TextNode;
import org.assertj.core.api.Assertions;
import org.drools.scenariosimulation.backend.model.ListMapClass;
import org.junit.Test;

import static org.drools.scenariosimulation.api.utils.ConstantsHolder.VALUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class BaseExpressionEvaluatorTest {

    private final static ClassLoader classLoader = BaseExpressionEvaluatorTest.class.getClassLoader();
    private final static AbstractExpressionEvaluator expressionEvaluator = new BaseExpressionEvaluator(classLoader);

    @Test
    public void evaluateLiteralExpression() {
        String raw = "";
        assertEquals(raw, expressionEvaluator.evaluateLiteralExpression(raw, Object.class.getCanonicalName(), Collections.emptyList()));

        raw = "SimpleString";
        assertEquals(raw, expressionEvaluator.evaluateLiteralExpression(raw, String.class.getCanonicalName(), Collections.emptyList()));

        raw = "= SimpleString";
        assertEquals("SimpleString", expressionEvaluator.evaluateLiteralExpression(raw, String.class.getCanonicalName(), Collections.emptyList()));

        assertNull(expressionEvaluator.evaluateLiteralExpression(null, String.class.getCanonicalName(), Collections.emptyList()));
    }

    @Test
    public void createObjectTest() {
        assertNotNull(expressionEvaluator.createObject(String.class.getCanonicalName(), Collections.emptyList()));
        assertTrue(expressionEvaluator.createObject(Map.class.getCanonicalName(), Arrays.asList(String.class.getCanonicalName(), String.class.getCanonicalName())) instanceof Map);

        Assertions.assertThatThrownBy(() -> expressionEvaluator.createObject("com.invalid.class.Name", Collections.emptyList())).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Impossible to instantiate com.invalid.class.Name");
    }

    @Test
    public void verifyNullTest() {
        assertTrue(expressionEvaluator.verifyResult("[]", null, null).isSuccessful());
        assertFalse(expressionEvaluator.verifyResult("[{\"" + VALUE + "\" : \"result\"}]", null, null).isSuccessful());
    }

    @Test
    public void nullResultTest() {
        assertFalse(expressionEvaluator.evaluateUnaryExpression("> 1", null, null).isSuccessful());
        assertTrue(expressionEvaluator.evaluateUnaryExpression("", null, null).isSuccessful());
        assertTrue(expressionEvaluator.evaluateUnaryExpression(null, null, null).isSuccessful());

        assertTrue(expressionEvaluator.evaluateUnaryExpression("{}", null, Map.class).isSuccessful());
        assertTrue(expressionEvaluator.evaluateUnaryExpression("[]", null, List.class).isSuccessful());

        String mapOfListJson = "{\"key1\" : [{\"" + VALUE + "\" : \"value1\"}, {\"" + VALUE + "\" : \"value2\"}]}";
        Map<String, List<String>> mapOfListToCheck = new HashMap<>();
        assertFalse(expressionEvaluator.evaluateUnaryExpression(mapOfListJson, mapOfListToCheck, Map.class).isSuccessful());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mapOfSimpleTypeTest() {

        String givenWorkbenchMapString = "{ \"Home\": { \"" + VALUE + "\": \"123 Any Street\" } }";
        List<String> genericClasses = new ArrayList<>();
        genericClasses.add(String.class.getCanonicalName());
        genericClasses.add(String.class.getCanonicalName());
        Map<String, String> parsedWorkbench = ((Map<String, String>) expressionEvaluator.convertResult(givenWorkbenchMapString, Map.class.getCanonicalName(), genericClasses));

        assertEquals(1, parsedWorkbench.size());
        assertNotNull(parsedWorkbench.get("Home"));
        assertEquals("123 Any Street", parsedWorkbench.get("Home"));

        String givenWorkbenchMapInteger = "{ \"Home\": { \"" + VALUE + "\": \"100\" } }";
        genericClasses.clear();
        genericClasses.add(String.class.getCanonicalName());
        genericClasses.add(Integer.class.getCanonicalName());
        Map<String, Integer> parsedIntegerFromMap = ((Map<String, Integer>) expressionEvaluator.convertResult(givenWorkbenchMapInteger, Map.class.getCanonicalName(), genericClasses));

        assertEquals(1, parsedIntegerFromMap.size());
        assertNotNull(parsedIntegerFromMap.get("Home"));
        assertEquals(100, parsedIntegerFromMap.get("Home").intValue());

        String expectWorkbenchMapInteger = "{ \"Home\": { \"" + VALUE + "\": \"> 100\" } }";
        genericClasses.clear();
        genericClasses.add(String.class.getCanonicalName());
        genericClasses.add(Integer.class.getCanonicalName());
        Map<String, Integer> resultToTest = new HashMap<>();
        resultToTest.put("Home", 120);
        assertTrue(expressionEvaluator.verifyResult(expectWorkbenchMapInteger, resultToTest, null).isSuccessful());
        resultToTest.put("Home", 20);
        assertFalse(expressionEvaluator.verifyResult(expectWorkbenchMapInteger, resultToTest, null).isSuccessful());

        String mapOfStringJson = "{\"key1\" : {\"" + VALUE + "\" : \"value1\"}, \"key2\" : {\"" + VALUE + "\" : \"value2\"}}";
        Map<String, String> mapStringStringToCheck = new HashMap<>();
        mapStringStringToCheck.put("key1", "value1");
        assertFalse(expressionEvaluator.evaluateUnaryExpression(mapOfStringJson, mapStringStringToCheck, Map.class).isSuccessful());
        mapStringStringToCheck.put("key2", "value2");
        assertTrue(expressionEvaluator.evaluateUnaryExpression(mapOfStringJson, mapStringStringToCheck, Map.class).isSuccessful());

        String mapOfStringJson1 = "{\"key1\" : {\"" + VALUE + "\" : \"\"}}";
        Map<String, String> mapStringStringToCheck1 = new HashMap<>();
        assertTrue(expressionEvaluator.evaluateUnaryExpression(mapOfStringJson1, mapStringStringToCheck1, Map.class).isSuccessful());
        mapStringStringToCheck1.put("key1", "value1");
        assertTrue(expressionEvaluator.evaluateUnaryExpression(mapOfStringJson1, mapStringStringToCheck1, Map.class).isSuccessful());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mapOfComplexTypeTest() {
        String mapJsonString = "{\"first\": {\"name\": \"John\"}}";
        Map<String, ListMapClass> parsedMap = (Map<String, ListMapClass>) expressionEvaluator.convertResult(mapJsonString, Map.class.getCanonicalName(),
                                                                                                            Collections.singletonList(ListMapClass.class.getCanonicalName()));

        assertEquals(1, parsedMap.size());
        assertEquals("John", parsedMap.get("first").getName());

        mapJsonString = "{\"first\": {\"siblings\": [{\"name\" : \"John\"}]}}";
        parsedMap = (Map<String, ListMapClass>) expressionEvaluator.convertResult(mapJsonString, Map.class.getCanonicalName(),
                                                                                  Collections.singletonList(ListMapClass.class.getCanonicalName()));
        assertEquals(1, parsedMap.size());
        assertEquals("John", parsedMap.get("first").getSiblings().get(0).getName());

        mapJsonString = "{\"first\": {\"phones\": {\"number\" : \"1\"}}}";
        parsedMap = (Map<String, ListMapClass>) expressionEvaluator.convertResult(mapJsonString, Map.class.getCanonicalName(),
                                                                                  Collections.singletonList(ListMapClass.class.getCanonicalName()));

        assertEquals(1, parsedMap.size());
        assertEquals((Integer) 1, parsedMap.get("first").getPhones().get("number"));

        mapJsonString = "{\"first\": {\"phones\": {\"number\" : \"> 1\"}}}";

        Map<String, ListMapClass> toCheck = new HashMap<>();
        ListMapClass element = new ListMapClass();
        Map<String, Integer> phones = new HashMap<>();
        phones.put("number", 10);
        element.setPhones(phones);
        toCheck.put("first", element);

        assertTrue(expressionEvaluator.verifyResult(mapJsonString, toCheck, null).isSuccessful());
        phones.put("number", -1);
        assertFalse(expressionEvaluator.verifyResult(mapJsonString, toCheck, null).isSuccessful());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void listOfComplexTypeTest() {

        String listJsonString = "[{\"name\": \"John\"}, " +
                "{\"name\": \"John\", \"names\" : [{\"" + VALUE + "\": \"Anna\"}, {\"" + VALUE + "\": \"Mario\"}]}]";

        List<ListMapClass> parsedValue = (List<ListMapClass>) expressionEvaluator.convertResult(listJsonString, List.class.getCanonicalName(),
                                                                                                Collections.singletonList(ListMapClass.class.getCanonicalName()));

        assertEquals(2, parsedValue.size());
        assertEquals(2, parsedValue.get(1).getNames().size());
        assertTrue(parsedValue.get(1).getNames().contains("Anna"));

        assertTrue(expressionEvaluator.verifyResult(listJsonString, parsedValue, null).isSuccessful());

        parsedValue.get(1).setNames(new ArrayList<>());
        assertFalse(expressionEvaluator.verifyResult(listJsonString, parsedValue, null).isSuccessful());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void listOfSimpleTypeTest() {
        String listJsonString = "[{\"" + VALUE + "\" : \"10\"}, {\"" + VALUE + "\" : \"12\"}]";

        List<Integer> result = (List<Integer>) expressionEvaluator.convertResult(listJsonString, List.class.getCanonicalName(), Collections.singletonList(Integer.class.getCanonicalName()));

        assertEquals(2, result.size());
        assertTrue(result.contains(10));

        listJsonString = "[{\"" + VALUE + "\" : \"> 10\"}]";
        List<Integer> toCheck = Collections.singletonList(13);

        assertTrue(expressionEvaluator.verifyResult(listJsonString, toCheck, null).isSuccessful());

        listJsonString = "[{\"" + VALUE + "\" : \"> 100\"}]";
        assertFalse(expressionEvaluator.verifyResult(listJsonString, toCheck, null).isSuccessful());

        listJsonString = "[{\"" + VALUE + "\" : \"\"}]";
        assertTrue(expressionEvaluator.verifyResult(listJsonString, toCheck, null).isSuccessful());
    }

    @Test(expected = IllegalArgumentException.class)
    public void expressionListVerifyResultTest() {
        String expressionCollectionJsonString = new TextNode("10").toString();
        List<BigDecimal> contextValue = Collections.singletonList(BigDecimal.valueOf(10));
        assertTrue(expressionEvaluator.verifyResult(expressionCollectionJsonString, contextValue, List.class).isSuccessful());
    }

    @Test(expected = IllegalArgumentException.class)
    public void expressionMapVerifyResultTest() {
        String expressionCollectionJsonString = new TextNode("{key_a : 1}").toString();
        Map<String, BigDecimal> contextValue = Collections.singletonMap("key_a", BigDecimal.valueOf(1));
        assertTrue(expressionEvaluator.verifyResult(expressionCollectionJsonString, contextValue, Map.class).isSuccessful());
    }
}
