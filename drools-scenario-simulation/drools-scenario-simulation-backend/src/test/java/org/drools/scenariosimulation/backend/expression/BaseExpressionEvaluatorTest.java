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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.scenariosimulation.backend.model.ListMapClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BaseExpressionEvaluatorTest {

    private final static ClassLoader classLoader = BaseExpressionEvaluatorTest.class.getClassLoader();

    @Test
    public void evaluateLiteralExpression() {
        BaseExpressionEvaluator baseExpressionEvaluator = new BaseExpressionEvaluator(classLoader);

        Object raw = new Object();
        assertEquals(raw, baseExpressionEvaluator.evaluateLiteralExpression(Object.class.getCanonicalName(), Collections.emptyList(), raw));

        raw = "SimpleString";
        assertEquals(raw, baseExpressionEvaluator.evaluateLiteralExpression(String.class.getCanonicalName(), Collections.emptyList(), raw));

        raw = "= SimpleString";
        assertEquals("SimpleString", baseExpressionEvaluator.evaluateLiteralExpression(String.class.getCanonicalName(), Collections.emptyList(), raw));

        assertNull(baseExpressionEvaluator.evaluateLiteralExpression(String.class.getCanonicalName(), Collections.emptyList(), null));
    }

    @Test
    public void createSimpleTypeObject() {
        // Integer has no default constructor
        BaseExpressionEvaluator expressionEvaluator = new BaseExpressionEvaluator(classLoader);
        Object obj = expressionEvaluator.createObject(Integer.class.getCanonicalName(), Collections.emptyList());

        assertNull(obj);
    }

    @Test
    public void createNonSimpleTypeObject() {
        // String has a default constructor
        BaseExpressionEvaluator expressionEvaluator = new BaseExpressionEvaluator(classLoader);
        Object obj = expressionEvaluator.createObject(String.class.getCanonicalName(), Collections.emptyList());

        assertNotNull(obj);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createObjectWithInvalidClass() {
        String invalidClassName = "com.invalid.class.Name";

        BaseExpressionEvaluator expressionEvaluator = new BaseExpressionEvaluator(classLoader);
        Object obj = expressionEvaluator.createObject(invalidClassName, Collections.emptyList());

        // This should never be reached since the invalid class name will cause the 
        // expected exception
        fail();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void expressionTest() {
        BaseExpressionEvaluator expressionEvaluator = new BaseExpressionEvaluator(classLoader);

        String likeWorkbenchMapString = "{ \"Home\": { \"value\": \"123 Any Street\" } }";
        List<String> genericClasses = new ArrayList<>();
//        genericClasses.add(String.class.getCanonicalName());
        genericClasses.add(String.class.getCanonicalName());
        Map<String, String> parsedWorkbench = ((Map<String, String>) expressionEvaluator.convertResult(likeWorkbenchMapString, Map.class.getCanonicalName(), genericClasses));
        System.out.println(parsedWorkbench);

        assertEquals(1, parsedWorkbench.size());
        assertNotNull(parsedWorkbench.get("Home"));
        assertTrue(parsedWorkbench.get("Home").equals("123 Any Street"));

        String likeWorkbenchMapInteger = "{ \"Home\": { \"value\": \"100\" } }";
        genericClasses.clear();
//        genericClasses.add(String.class.getCanonicalName());
        genericClasses.add(Integer.class.getCanonicalName());
        Map<String, Integer> parsedIntegerFromMap = ((Map<String, Integer>) expressionEvaluator.convertResult(likeWorkbenchMapInteger, Map.class.getCanonicalName(), genericClasses));

        assertEquals(1, parsedIntegerFromMap.size());
        assertNotNull(parsedIntegerFromMap.get("Home"));
        assertEquals(100, parsedIntegerFromMap.get("Home").intValue());

        String listJsonString = "[{\"name\": \"John\"}, " +
                "{\"name\": \"John\", \"names\" : [{\"value\": \"Anna\"}, {\"value\": \"Mario\"}]}]";

        List<ListMapClass> parsedValue = (List<ListMapClass>) expressionEvaluator.convertResult(listJsonString, List.class.getCanonicalName(),
                                                                                                Collections.singletonList(ListMapClass.class.getCanonicalName()));

        assertEquals(2, parsedValue.size());
        assertEquals(2, parsedValue.get(1).getNames().size());
        assertTrue(parsedValue.get(1).getNames().contains("Anna"));

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
    }
}
