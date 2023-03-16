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
import org.drools.scenariosimulation.backend.model.ListMapClass;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.drools.scenariosimulation.api.utils.ConstantsHolder.VALUE;

public class BaseExpressionEvaluatorTest {

    private final static ClassLoader classLoader = BaseExpressionEvaluatorTest.class.getClassLoader();
    private final static AbstractExpressionEvaluator expressionEvaluator = new BaseExpressionEvaluator(classLoader);

    @Test
    public void evaluateLiteralExpression() {
        String raw = "";
        assertThat(expressionEvaluator.evaluateLiteralExpression(raw, Object.class.getCanonicalName(), Collections.emptyList())).isEqualTo(raw);

        raw = "SimpleString";
        assertThat(expressionEvaluator.evaluateLiteralExpression(raw, String.class.getCanonicalName(), Collections.emptyList())).isEqualTo(raw);

        raw = "= SimpleString";
        assertThat(expressionEvaluator.evaluateLiteralExpression(raw, String.class.getCanonicalName(), Collections.emptyList())).isEqualTo("SimpleString");

        assertThat(expressionEvaluator.evaluateLiteralExpression(null, String.class.getCanonicalName(), Collections.emptyList())).isNull();
    }

    @Test
    public void createObjectTest() {
        assertThat(expressionEvaluator.createObject(String.class.getCanonicalName(), Collections.emptyList())).isNotNull();
        assertThat(expressionEvaluator.createObject(Map.class.getCanonicalName(), Arrays.asList(String.class.getCanonicalName(), String.class.getCanonicalName())) instanceof Map).isTrue();

        assertThatThrownBy(() -> expressionEvaluator.createObject("com.invalid.class.Name", Collections.emptyList())).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Impossible to instantiate com.invalid.class.Name");
    }

    @Test
    public void verifyNullTest() {
        assertThat(expressionEvaluator.verifyResult("[]", null, null).isSuccessful()).isTrue();
        assertThat(expressionEvaluator.verifyResult("[{\"" + VALUE + "\" : \"result\"}]", null, null).isSuccessful()).isFalse();
    }

    @Test
    public void nullResultTest() {
        assertThat(expressionEvaluator.evaluateUnaryExpression("> 1", null, null).isSuccessful()).isFalse();
        assertThat(expressionEvaluator.evaluateUnaryExpression("", null, null).isSuccessful()).isTrue();
        assertThat(expressionEvaluator.evaluateUnaryExpression(null, null, null).isSuccessful()).isTrue();

        assertThat(expressionEvaluator.evaluateUnaryExpression("{}", null, Map.class).isSuccessful()).isTrue();
        assertThat(expressionEvaluator.evaluateUnaryExpression("[]", null, List.class).isSuccessful()).isTrue();

        String mapOfListJson = "{\"key1\" : [{\"" + VALUE + "\" : \"value1\"}, {\"" + VALUE + "\" : \"value2\"}]}";
        Map<String, List<String>> mapOfListToCheck = new HashMap<>();
        assertThat(expressionEvaluator.evaluateUnaryExpression(mapOfListJson, mapOfListToCheck, Map.class).isSuccessful()).isFalse();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mapOfSimpleTypeTest() {

        String givenWorkbenchMapString = "{ \"Home\": { \"" + VALUE + "\": \"123 Any Street\" } }";
        List<String> genericClasses = new ArrayList<>();
        genericClasses.add(String.class.getCanonicalName());
        genericClasses.add(String.class.getCanonicalName());
        Map<String, String> parsedWorkbench = ((Map<String, String>) expressionEvaluator.convertResult(givenWorkbenchMapString, Map.class.getCanonicalName(), genericClasses));

        assertThat(parsedWorkbench.size()).isEqualTo(1);
        assertThat(parsedWorkbench.get("Home")).isNotNull();
        assertThat(parsedWorkbench.get("Home")).isEqualTo("123 Any Street");

        String givenWorkbenchMapInteger = "{ \"Home\": { \"" + VALUE + "\": \"100\" } }";
        genericClasses.clear();
        genericClasses.add(String.class.getCanonicalName());
        genericClasses.add(Integer.class.getCanonicalName());
        Map<String, Integer> parsedIntegerFromMap = ((Map<String, Integer>) expressionEvaluator.convertResult(givenWorkbenchMapInteger, Map.class.getCanonicalName(), genericClasses));

        assertThat(parsedIntegerFromMap.size()).isEqualTo(1);
        assertThat(parsedIntegerFromMap.get("Home")).isNotNull();
        assertThat(parsedIntegerFromMap.get("Home").intValue()).isEqualTo(100);

        String expectWorkbenchMapInteger = "{ \"Home\": { \"" + VALUE + "\": \"> 100\" } }";
        genericClasses.clear();
        genericClasses.add(String.class.getCanonicalName());
        genericClasses.add(Integer.class.getCanonicalName());
        Map<String, Integer> resultToTest = new HashMap<>();
        resultToTest.put("Home", 120);
        assertThat(expressionEvaluator.verifyResult(expectWorkbenchMapInteger, resultToTest, null).isSuccessful()).isTrue();
        resultToTest.put("Home", 20);
        assertThat(expressionEvaluator.verifyResult(expectWorkbenchMapInteger, resultToTest, null).isSuccessful()).isFalse();

        String mapOfStringJson = "{\"key1\" : {\"" + VALUE + "\" : \"value1\"}, \"key2\" : {\"" + VALUE + "\" : \"value2\"}}";
        Map<String, String> mapStringStringToCheck = new HashMap<>();
        mapStringStringToCheck.put("key1", "value1");
        assertThat(expressionEvaluator.evaluateUnaryExpression(mapOfStringJson, mapStringStringToCheck, Map.class).isSuccessful()).isFalse();
        mapStringStringToCheck.put("key2", "value2");
        assertThat(expressionEvaluator.evaluateUnaryExpression(mapOfStringJson, mapStringStringToCheck, Map.class).isSuccessful()).isTrue();

        String mapOfStringJson1 = "{\"key1\" : {\"" + VALUE + "\" : \"\"}}";
        Map<String, String> mapStringStringToCheck1 = new HashMap<>();
        assertThat(expressionEvaluator.evaluateUnaryExpression(mapOfStringJson1, mapStringStringToCheck1, Map.class).isSuccessful()).isTrue();
        mapStringStringToCheck1.put("key1", "value1");
        assertThat(expressionEvaluator.evaluateUnaryExpression(mapOfStringJson1, mapStringStringToCheck1, Map.class).isSuccessful()).isTrue();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void mapOfComplexTypeTest() {
        String mapJsonString = "{\"first\": {\"name\": \"John\"}}";
        Map<String, ListMapClass> parsedMap = (Map<String, ListMapClass>) expressionEvaluator.convertResult(mapJsonString, Map.class.getCanonicalName(),
                                                                                                            Collections.singletonList(ListMapClass.class.getCanonicalName()));

        assertThat(parsedMap.size()).isEqualTo(1);
        assertThat(parsedMap.get("first").getName()).isEqualTo("John");

        mapJsonString = "{\"first\": {\"siblings\": [{\"name\" : \"John\"}]}}";
        parsedMap = (Map<String, ListMapClass>) expressionEvaluator.convertResult(mapJsonString, Map.class.getCanonicalName(),
                                                                                  Collections.singletonList(ListMapClass.class.getCanonicalName()));
        assertThat(parsedMap.size()).isEqualTo(1);
        assertThat(parsedMap.get("first").getSiblings().get(0).getName()).isEqualTo("John");

        mapJsonString = "{\"first\": {\"phones\": {\"number\" : \"1\"}}}";
        parsedMap = (Map<String, ListMapClass>) expressionEvaluator.convertResult(mapJsonString, Map.class.getCanonicalName(),
                                                                                  Collections.singletonList(ListMapClass.class.getCanonicalName()));

        assertThat(parsedMap.size()).isEqualTo(1);
        assertThat(parsedMap.get("first").getPhones().get("number")).isEqualTo((Integer) 1);

        mapJsonString = "{\"first\": {\"phones\": {\"number\" : \"> 1\"}}}";

        Map<String, ListMapClass> toCheck = new HashMap<>();
        ListMapClass element = new ListMapClass();
        Map<String, Integer> phones = new HashMap<>();
        phones.put("number", 10);
        element.setPhones(phones);
        toCheck.put("first", element);

        assertThat(expressionEvaluator.verifyResult(mapJsonString, toCheck, null).isSuccessful()).isTrue();
        phones.put("number", -1);
        assertThat(expressionEvaluator.verifyResult(mapJsonString, toCheck, null).isSuccessful()).isFalse();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void listOfComplexTypeTest() {

        String listJsonString = "[{\"name\": \"John\"}, " +
                "{\"name\": \"John\", \"names\" : [{\"" + VALUE + "\": \"Anna\"}, {\"" + VALUE + "\": \"Mario\"}]}]";

        List<ListMapClass> parsedValue = (List<ListMapClass>) expressionEvaluator.convertResult(listJsonString, List.class.getCanonicalName(),
                                                                                                Collections.singletonList(ListMapClass.class.getCanonicalName()));

        assertThat(parsedValue.size()).isEqualTo(2);
        assertThat(parsedValue.get(1).getNames().size()).isEqualTo(2);
        assertThat(parsedValue.get(1).getNames().contains("Anna")).isTrue();

        assertThat(expressionEvaluator.verifyResult(listJsonString, parsedValue, null).isSuccessful()).isTrue();

        parsedValue.get(1).setNames(new ArrayList<>());
        assertThat(expressionEvaluator.verifyResult(listJsonString, parsedValue, null).isSuccessful()).isFalse();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void listOfSimpleTypeTest() {
        String listJsonString = "[{\"" + VALUE + "\" : \"10\"}, {\"" + VALUE + "\" : \"12\"}]";

        List<Integer> result = (List<Integer>) expressionEvaluator.convertResult(listJsonString, List.class.getCanonicalName(), Collections.singletonList(Integer.class.getCanonicalName()));

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.contains(10)).isTrue();

        listJsonString = "[{\"" + VALUE + "\" : \"> 10\"}]";
        List<Integer> toCheck = Collections.singletonList(13);

        assertThat(expressionEvaluator.verifyResult(listJsonString, toCheck, null).isSuccessful()).isTrue();

        listJsonString = "[{\"" + VALUE + "\" : \"> 100\"}]";
        assertThat(expressionEvaluator.verifyResult(listJsonString, toCheck, null).isSuccessful()).isFalse();

        listJsonString = "[{\"" + VALUE + "\" : \"\"}]";
        assertThat(expressionEvaluator.verifyResult(listJsonString, toCheck, null).isSuccessful()).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void expressionListVerifyResultTest() {
        String expressionCollectionJsonString = new TextNode("10").toString();
        List<BigDecimal> contextValue = Collections.singletonList(BigDecimal.valueOf(10));
        assertThat(expressionEvaluator.verifyResult(expressionCollectionJsonString, contextValue, List.class).isSuccessful()).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void expressionMapVerifyResultTest() {
        String expressionCollectionJsonString = new TextNode("{key_a : 1}").toString();
        Map<String, BigDecimal> contextValue = Collections.singletonMap("key_a", BigDecimal.valueOf(1));
        assertThat(expressionEvaluator.verifyResult(expressionCollectionJsonString, contextValue, Map.class).isSuccessful()).isTrue();
    }
}
