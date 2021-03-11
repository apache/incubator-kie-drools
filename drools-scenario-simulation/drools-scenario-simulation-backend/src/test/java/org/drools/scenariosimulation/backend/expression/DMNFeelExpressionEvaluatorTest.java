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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Test;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.runtime.events.FEELEventBase;
import org.kie.dmn.feel.runtime.events.SyntaxErrorEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DMNFeelExpressionEvaluatorTest {

    DMNFeelExpressionEvaluator expressionEvaluator = new DMNFeelExpressionEvaluator(this.getClass().getClassLoader());

    @Test
    public void evaluateUnaryExpression() {
        assertTrue(expressionEvaluator.evaluateUnaryExpression("not( true )", false, boolean.class).isSuccessful());
        assertTrue(expressionEvaluator.evaluateUnaryExpression(">2, >5", BigDecimal.valueOf(6), BigDecimal.class).isSuccessful());
        assertTrue(expressionEvaluator.evaluateUnaryExpression("abs(-1)", BigDecimal.valueOf(1), BigDecimal.class).isSuccessful());
        assertFalse(expressionEvaluator.evaluateUnaryExpression("abs(-1)", BigDecimal.valueOf(-1), BigDecimal.class).isSuccessful());
        assertTrue(expressionEvaluator.evaluateUnaryExpression("max(1, ?) > 1", BigDecimal.valueOf(2), BigDecimal.class).isSuccessful());
        assertFalse(expressionEvaluator.evaluateUnaryExpression("max(1, ?) < 1", BigDecimal.valueOf(2), BigDecimal.class).isSuccessful());
        assertTrue(expressionEvaluator.evaluateUnaryExpression("? = 2", BigDecimal.valueOf(2), BigDecimal.class).isSuccessful());
        assertFalse(expressionEvaluator.evaluateUnaryExpression("? > 2", BigDecimal.valueOf(2), BigDecimal.class).isSuccessful());
        assertTrue(expressionEvaluator.evaluateUnaryExpression("? + 1 > ?", BigDecimal.valueOf(2), BigDecimal.class).isSuccessful());
        Map<String, BigDecimal> contextValue = Collections.singletonMap("key_a", BigDecimal.valueOf(1));
        assertTrue(expressionEvaluator.evaluateUnaryExpression("{key_a : 1}", contextValue, Map.class).isSuccessful());
        assertFalse(expressionEvaluator.evaluateUnaryExpression("{key_a : 2}", contextValue, Map.class).isSuccessful());
        List<BigDecimal> contextListValue = Collections.singletonList(BigDecimal.valueOf(23));
        assertTrue(expressionEvaluator.evaluateUnaryExpression(new TextNode("23").toString(), contextListValue, List.class).isSuccessful());
        assertFalse(expressionEvaluator.evaluateUnaryExpression(new TextNode("2").toString(), contextListValue, List.class).isSuccessful());
        assertTrue(expressionEvaluator.evaluateUnaryExpression(new TextNode("? = [23]").toString(), contextListValue, List.class).isSuccessful());
        assertFalse(expressionEvaluator.evaluateUnaryExpression(new TextNode("? = [2]").toString(), contextListValue, List.class).isSuccessful());
        List<BigDecimal> contextListValue2 = Arrays.asList(BigDecimal.valueOf(23), BigDecimal.valueOf(32));
        assertTrue(expressionEvaluator.evaluateUnaryExpression(new TextNode(" ? = [23, 32]").toString(), contextListValue2, List.class).isSuccessful());
        assertFalse("Collection unary expression needs to start with ?",
                    expressionEvaluator.evaluateUnaryExpression(new TextNode("[23, 32]").toString(),
                                                                contextListValue2,
                                                                List.class).isSuccessful());
        assertFalse(expressionEvaluator.evaluateUnaryExpression(new TextNode(" ? = [23, 32, 123]").toString(), contextListValue2, List.class).isSuccessful());
        assertTrue(expressionEvaluator.evaluateUnaryExpression(new TextNode(" ?[1] = 23").toString(), contextListValue2, List.class).isSuccessful());
        assertFalse(expressionEvaluator.evaluateUnaryExpression(new TextNode(" ?[1] = 32").toString(), contextListValue2, List.class).isSuccessful());

        Map<String, Object> firstMap = new HashMap<>();
        firstMap.put("Price", new BigDecimal(2000));
        firstMap.put("Name", "PC");
        Map<String, Object> secondMap = new HashMap<>();
        secondMap.put("Price", new BigDecimal(3300));
        secondMap.put("Name", "CAR");
        String firstParameter = "{Price: 2000,Name:\"PC\"}";
        String secondParameter = "{Price:3300, Name:\"CAR\"}";
        List<Map<String, Object>> context = Arrays.asList(firstMap, secondMap);
        assertTrue(expressionEvaluator.evaluateUnaryExpression(new TextNode("?=[" + firstParameter + ", " + secondParameter + "]").toString(), context, List.class).isSuccessful());
        assertFalse(expressionEvaluator.evaluateUnaryExpression(new TextNode("?=[{Price: 2001,Name:\"PC\"}, {Price:3301,Name:\"CAR\"}]").toString(), context, List.class).isSuccessful());
        assertFalse(expressionEvaluator.evaluateUnaryExpression(new TextNode("?=[{Price: 2000, Name:\"PCA\"}, {Price:3300,Name:\"CARE\"}]").toString(), context, List.class).isSuccessful());
        assertFalse(expressionEvaluator.evaluateUnaryExpression(new TextNode("?=[{Pric: 2000, Name:\"PC\"}, {Price:3300,Names:\"CARE\"}]").toString(), context, List.class).isSuccessful());
        /* Different order: Failure */
        assertFalse(expressionEvaluator.evaluateUnaryExpression(new TextNode("?=[" + secondParameter + ", " + firstParameter + "]").toString(), context, List.class).isSuccessful());
        /* IN operator */
        assertTrue(expressionEvaluator.evaluateUnaryExpression(new TextNode(firstParameter + " in ?").toString(), context, List.class).isSuccessful());
        assertTrue(expressionEvaluator.evaluateUnaryExpression(new TextNode(secondParameter + " in ?").toString(), context, List.class).isSuccessful());
        assertFalse(expressionEvaluator.evaluateUnaryExpression(new TextNode("{Price: 2001,Name:\"PC\"} in ?").toString(), context, List.class).isSuccessful());
        assertFalse(expressionEvaluator.evaluateUnaryExpression(new TextNode("{Price: 3300,Name:\"CARE\"} in ?").toString(), context, List.class).isSuccessful());
        assertTrue(expressionEvaluator.evaluateUnaryExpression(new TextNode("(" + firstParameter + " in ?) and ("+ secondParameter +" in ?)").toString(), context, List.class).isSuccessful());
        assertTrue(expressionEvaluator.evaluateUnaryExpression(new TextNode("(" + secondParameter + " in ?) and ("+ firstParameter +" in ?)").toString(), context, List.class).isSuccessful());

        assertThatThrownBy(() -> expressionEvaluator.evaluateUnaryExpression("variable", null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Error during evaluation:");

        assertThatThrownBy(() -> expressionEvaluator.evaluateUnaryExpression("! true", null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Syntax error:");

        assertThatThrownBy(() -> expressionEvaluator.evaluateUnaryExpression("? > 2", null, BigDecimal.class))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void evaluateLiteralExpression() {
        assertEquals(BigDecimal.valueOf(5), expressionEvaluator.evaluateLiteralExpression("2 + 3", BigDecimal.class.getCanonicalName(), null));
        Map<String, Object> parsedValue = (Map<String, Object>) expressionEvaluator.evaluateLiteralExpression("{key_a : 1}", Map.class.getCanonicalName(), Collections.emptyList());
        assertTrue(parsedValue.containsKey("key_a"));
        assertEquals(parsedValue.get("key_a"), BigDecimal.valueOf(1));
        List<BigDecimal> parsedValueListExpression = (List<BigDecimal>) expressionEvaluator.evaluateLiteralExpression(new TextNode("[10, 12]").toString(), List.class.getCanonicalName(), Collections.emptyList());
        assertEquals(2, parsedValueListExpression.size());
        assertEquals(BigDecimal.valueOf(10), parsedValueListExpression.get(0));
        assertEquals(BigDecimal.valueOf(12), parsedValueListExpression.get(1));

        assertThatThrownBy(() -> expressionEvaluator
                .evaluateLiteralExpression("SPEED", String.class.getCanonicalName(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Error during evaluation:");

        assertThatThrownBy(() -> expressionEvaluator
                .evaluateLiteralExpression("\"SPEED", String.class.getCanonicalName(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Syntax error:");
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

    @Test
    public void listenerTest() {
        FEELEvent syntaxErrorEvent = new SyntaxErrorEvent(Severity.ERROR, "test", null, 0, 0, null);
        FEELEvent genericError = new FEELEventBase(Severity.ERROR, "error", null);
        FEELEvent notError = new FEELEventBase(Severity.INFO, "info", null);

        AtomicReference<FEELEvent> error = new AtomicReference<>();
        FEEL feel = expressionEvaluator.newFeelEvaluator(error);

        // Only a single error of type syntax
        applyEvents(Collections.singletonList(syntaxErrorEvent), feel);
        assertEquals(syntaxErrorEvent, error.get());

        error.set(null);

        // Syntax error as second
        applyEvents(Arrays.asList(genericError, syntaxErrorEvent), feel);
        assertEquals(syntaxErrorEvent, error.get());

        error.set(null);

        // Syntax error as first
        applyEvents(Arrays.asList(syntaxErrorEvent, genericError), feel);
        assertEquals(syntaxErrorEvent, error.get());

        error.set(null);

        // Not error
        applyEvents(Collections.singletonList(notError), feel);
        assertNull(error.get());
    }

    private void applyEvents(List<FEELEvent> events, FEEL feel) {
        for (FEELEvent event : events) {
            feel.getListeners().forEach(listener -> listener.onEvent(event));
        }
    }

    @Test
    public void expressionListTest() {
        String expressionCollectionJsonString = new TextNode("[ 1, 10 ]").toString();
        List<BigDecimal> result = (List<BigDecimal>) expressionEvaluator.convertResult(expressionCollectionJsonString, List.class.getCanonicalName(), Collections.EMPTY_LIST);
        assertEquals(2, result.size());
        assertEquals(BigDecimal.ONE, result.get(0));
        assertEquals(BigDecimal.TEN, result.get(1));
    }

    @Test
    public void expressionObjectListTest() {
        String expressionCollectionJsonString = new TextNode("[{age:10},{name:\"John\"}]").toString();
        List<Map<String, Object>> result =
                (List<Map<String, Object>>) expressionEvaluator.convertResult(expressionCollectionJsonString,
                                                                              List.class.getCanonicalName(),
                                                                              Collections.EMPTY_LIST);
        assertEquals(2, result.size());
        assertThat(result.get(0)).containsOnly(entry("age", BigDecimal.TEN));
        assertThat(result.get(1)).containsOnly(entry("name", "John"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void expressionListTest_Wrong() {
        String expressionCollectionJsonString = new TextNode("[ 1 : 234").toString();
        expressionEvaluator.convertResult(expressionCollectionJsonString, List.class.getCanonicalName(), Collections.EMPTY_LIST);
    }

    @Test
    public void expressionMapTest() {
        String expressionCollectionJsonString = new TextNode("{ x : 5, y : 3 }").toString();
        Map<String, BigDecimal> result = (Map<String, BigDecimal>) expressionEvaluator.convertResult(expressionCollectionJsonString, Map.class.getCanonicalName(), Collections.EMPTY_LIST);
        assertEquals(2, result.size());
        assertEquals(BigDecimal.valueOf(5), result.get("x"));
        assertEquals(BigDecimal.valueOf(3), result.get("y"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void expressionMapTest_Wrong() {
        String expressionCollectionJsonString = new TextNode(": 5 y : 3 }").toString();
        expressionEvaluator.convertResult(expressionCollectionJsonString, Map.class.getCanonicalName(), Collections.EMPTY_LIST);
    }

    @Test
    public void expressionListVerifyResultTest() {
        String expressionCollectionJsonString = new TextNode("10").toString();
        List<BigDecimal> contextValue = Collections.singletonList(BigDecimal.valueOf(10));
        assertTrue(expressionEvaluator.verifyResult(expressionCollectionJsonString, contextValue, List.class).isSuccessful());
    }

    @Test
    public void expressionMapVerifyResultTest() {
        String expressionCollectionJsonString = new TextNode("{key_a : 1}").toString();
        Map<String, BigDecimal> contextValue = Collections.singletonMap("key_a", BigDecimal.valueOf(1));
        assertTrue(expressionEvaluator.verifyResult(expressionCollectionJsonString, contextValue, Map.class).isSuccessful());
    }

    @Test
    public void isStructuredInput() {
        assertTrue(expressionEvaluator.isStructuredInput(List.class.getCanonicalName()));
        assertTrue(expressionEvaluator.isStructuredInput(ArrayList.class.getCanonicalName()));
        assertTrue(expressionEvaluator.isStructuredInput(LinkedList.class.getCanonicalName()));
        assertFalse(expressionEvaluator.isStructuredInput(Map.class.getCanonicalName()));
        assertFalse(expressionEvaluator.isStructuredInput(HashMap.class.getCanonicalName()));
        assertFalse(expressionEvaluator.isStructuredInput(LinkedHashMap.class.getCanonicalName()));
        assertFalse(expressionEvaluator.isStructuredInput(Set.class.getCanonicalName()));
        assertFalse(expressionEvaluator.isStructuredInput(Integer.class.getCanonicalName()));
        assertFalse(expressionEvaluator.isStructuredInput(String.class.getCanonicalName()));
    }
}