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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DMNFeelExpressionEvaluatorTest {

    DMNFeelExpressionEvaluator expressionEvaluator = new DMNFeelExpressionEvaluator(this.getClass().getClassLoader());

    @Test
    public void evaluateUnaryExpression() {
        assertTrue(expressionEvaluator.evaluateUnaryExpression("not( true )", false, boolean.class));
        assertTrue(expressionEvaluator.evaluateUnaryExpression(">2, >5", BigDecimal.valueOf(6), BigDecimal.class));
        assertThatThrownBy(() -> expressionEvaluator.evaluateUnaryExpression(new Object(), null, Object.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Raw expression should be a string");

        assertThatThrownBy(() -> expressionEvaluator.evaluateUnaryExpression("! true", null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Impossible to parse the expression '! true'");
    }

    @Test
    public void evaluateLiteralExpression() {
        assertEquals(BigDecimal.valueOf(5), expressionEvaluator.evaluateLiteralExpression(BigDecimal.class.getCanonicalName(), null, "2 + 3"));
        Object nonStringObject = new Object();
        assertEquals(nonStringObject, expressionEvaluator.evaluateLiteralExpression("class", null, nonStringObject));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void expressionTest() {

        String listJsonString = "[{\"name\": \"\\\"John\\\"\"}, " +
                "{\"name\": \"\\\"John\\\"\", \"names\" : [{\"value\": \"\\\"Anna\\\"\"}, {\"value\": \"\\\"Mario\\\"\"}]}]";

        List<Map<String, Object>> parsedValue = (List<Map<String, Object>>) expressionEvaluator.convertResult(listJsonString, List.class.getCanonicalName(),
                                                                                                              Collections.singletonList(Map.class.getCanonicalName()));

        assertEquals(2, parsedValue.size());
        assertEquals(2, ((List<Object>) parsedValue.get(1).get("names")).size());
        assertTrue(((List<Object>) parsedValue.get(1).get("names")).contains("Anna"));

        String mapJsonString = "{\"first\": {\"name\": \"\\\"John\\\"\"}}";
        Map<String, Map<String, Object>> parsedMap = (Map<String, Map<String, Object>>) expressionEvaluator
                .convertResult(mapJsonString, Map.class.getCanonicalName(),
                               Arrays.asList(String.class.getCanonicalName(), Object.class.getCanonicalName()));

        assertEquals(1, parsedMap.size());
        assertEquals("John", parsedMap.get("first").get("name"));

        mapJsonString = "{\"first\": {\"siblings\": [{\"name\" : \"\\\"John\\\"\"}]}}";
        parsedMap = (Map<String, Map<String, Object>>) expressionEvaluator
                .convertResult(mapJsonString, Map.class.getCanonicalName(),
                               Arrays.asList(String.class.getCanonicalName(), Object.class.getCanonicalName()));
        assertEquals(1, parsedMap.size());
        assertEquals("John", ((List<Map<String, Object>>) parsedMap.get("first").get("siblings")).get(0).get("name"));

        mapJsonString = "{\"first\": {\"phones\": {\"number\" : \"1\"}}}";
        parsedMap = (Map<String, Map<String, Object>>) expressionEvaluator
                .convertResult(mapJsonString, Map.class.getCanonicalName(),
                               Arrays.asList(String.class.getCanonicalName(), Object.class.getCanonicalName()));

        assertEquals(1, parsedMap.size());
        assertEquals(BigDecimal.valueOf(1), ((Map<String, Object>) parsedMap.get("first").get("phones")).get("number"));
    }

    @Test
    public void fromObjectToExpressionTest() {
        assertEquals("\"Test\"", expressionEvaluator.fromObjectToExpression("Test"));
        assertEquals("false", expressionEvaluator.fromObjectToExpression(false));
        assertEquals("1", expressionEvaluator.fromObjectToExpression(BigDecimal.valueOf(1)));
        assertEquals("date( \"2019-05-13\" )", expressionEvaluator.fromObjectToExpression(LocalDate.of(2019, 5, 13)));
        assertEquals("null", expressionEvaluator.fromObjectToExpression(null));
    }
}