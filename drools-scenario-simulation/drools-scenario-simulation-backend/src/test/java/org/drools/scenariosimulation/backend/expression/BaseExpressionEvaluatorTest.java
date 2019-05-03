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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.scenariosimulation.backend.model.ListMapClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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

    @SuppressWarnings("unchecked")
    @Test
    public void expressionTest() {
        BaseExpressionEvaluator expressionEvaluator = new BaseExpressionEvaluator(classLoader);

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
