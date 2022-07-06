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
import java.time.ZonedDateTime;
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

public class DMNFeelExpressionEvaluatorTest {

    DMNFeelExpressionEvaluator expressionEvaluator = new DMNFeelExpressionEvaluator(this.getClass().getClassLoader());

    @Test
    public void evaluateUnaryExpression() {
        assertThat(expressionEvaluator.evaluateUnaryExpression("not( true )", false, boolean.class).isSuccessful()).isTrue();
        assertThat(expressionEvaluator.evaluateUnaryExpression(">2, >5", BigDecimal.valueOf(6), BigDecimal.class).isSuccessful()).isTrue();
        assertThat(expressionEvaluator.evaluateUnaryExpression("abs(-1)", BigDecimal.valueOf(1), BigDecimal.class).isSuccessful()).isTrue();
        assertThat(expressionEvaluator.evaluateUnaryExpression("abs(-1)", BigDecimal.valueOf(-1), BigDecimal.class).isSuccessful()).isFalse();
        assertThat(expressionEvaluator.evaluateUnaryExpression("max(1, ?) > 1", BigDecimal.valueOf(2), BigDecimal.class).isSuccessful()).isTrue();
        assertThat(expressionEvaluator.evaluateUnaryExpression("max(1, ?) < 1", BigDecimal.valueOf(2), BigDecimal.class).isSuccessful()).isFalse();
        assertThat(expressionEvaluator.evaluateUnaryExpression("? = 2", BigDecimal.valueOf(2), BigDecimal.class).isSuccessful()).isTrue();
        assertThat(expressionEvaluator.evaluateUnaryExpression("? > 2", BigDecimal.valueOf(2), BigDecimal.class).isSuccessful()).isFalse();
        assertThat(expressionEvaluator.evaluateUnaryExpression("? + 1 > ?", BigDecimal.valueOf(2), BigDecimal.class).isSuccessful()).isTrue();
        Map<String, BigDecimal> contextValue = Collections.singletonMap("key_a", BigDecimal.valueOf(1));
        assertThat(expressionEvaluator.evaluateUnaryExpression("{key_a : 1}", contextValue, Map.class).isSuccessful()).isTrue();
        assertThat(expressionEvaluator.evaluateUnaryExpression("{key_a : 2}", contextValue, Map.class).isSuccessful()).isFalse();
        List<BigDecimal> contextListValue = Collections.singletonList(BigDecimal.valueOf(23));
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("23").toString(), contextListValue, List.class).isSuccessful()).isTrue();
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("2").toString(), contextListValue, List.class).isSuccessful()).isFalse();
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("? = [23]").toString(), contextListValue, List.class).isSuccessful()).isTrue();
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("? = [2]").toString(), contextListValue, List.class).isSuccessful()).isFalse();
        List<BigDecimal> contextListValue2 = Arrays.asList(BigDecimal.valueOf(23), BigDecimal.valueOf(32));
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode(" ? = [23, 32]").toString(), contextListValue2, List.class).isSuccessful()).isTrue();
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("[23, 32]").toString(),
                contextListValue2,
                List.class).isSuccessful()).as("Collection unary expression needs to start with ?").isFalse();
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode(" ? = [23, 32, 123]").toString(), contextListValue2, List.class).isSuccessful()).isFalse();
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode(" ?[1] = 23").toString(), contextListValue2, List.class).isSuccessful()).isTrue();
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode(" ?[1] = 32").toString(), contextListValue2, List.class).isSuccessful()).isFalse();

        Map<String, Object> firstMap = new HashMap<>();
        firstMap.put("Price", new BigDecimal(2000));
        firstMap.put("Name", "PC");
        Map<String, Object> secondMap = new HashMap<>();
        secondMap.put("Price", new BigDecimal(3300));
        secondMap.put("Name", "CAR");
        String firstParameter = "{Price: 2000,Name:\"PC\"}";
        String secondParameter = "{Price:3300, Name:\"CAR\"}";
        List<Map<String, Object>> context = Arrays.asList(firstMap, secondMap);
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("?=[" + firstParameter + ", " + secondParameter + "]").toString(), context, List.class).isSuccessful()).isTrue();
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("?=[{Price: 2001,Name:\"PC\"}, {Price:3301,Name:\"CAR\"}]").toString(), context, List.class).isSuccessful()).isFalse();
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("?=[{Price: 2000, Name:\"PCA\"}, {Price:3300,Name:\"CARE\"}]").toString(), context, List.class).isSuccessful()).isFalse();
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("?=[{Pric: 2000, Name:\"PC\"}, {Price:3300,Names:\"CARE\"}]").toString(), context, List.class).isSuccessful()).isFalse();
        /* Different order: Failure */
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("?=[" + secondParameter + ", " + firstParameter + "]").toString(), context, List.class).isSuccessful()).isFalse();
        /* IN operator */
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode(firstParameter + " in ?").toString(), context, List.class).isSuccessful()).isTrue();
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode(secondParameter + " in ?").toString(), context, List.class).isSuccessful()).isTrue();
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("{Price: 2001,Name:\"PC\"} in ?").toString(), context, List.class).isSuccessful()).isFalse();
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("{Price: 3300,Name:\"CARE\"} in ?").toString(), context, List.class).isSuccessful()).isFalse();
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("(" + firstParameter + " in ?) and (" + secondParameter + " in ?)").toString(), context, List.class).isSuccessful()).isTrue();
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("(" + secondParameter + " in ?) and (" + firstParameter + " in ?)").toString(), context, List.class).isSuccessful()).isTrue();

        assertThatThrownBy(() -> expressionEvaluator.evaluateUnaryExpression("variable", null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Error during evaluation:");

        assertThatThrownBy(() -> expressionEvaluator.evaluateUnaryExpression("! true", null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Syntax error:");

        assertThatThrownBy(() -> expressionEvaluator.evaluateUnaryExpression("? > 2", null, BigDecimal.class))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void evaluateLiteralExpression() {
        assertThat(expressionEvaluator.evaluateLiteralExpression("2 + 3", BigDecimal.class.getCanonicalName(), null)).isEqualTo(BigDecimal.valueOf(5));
        Map<String, Object> parsedValue = (Map<String, Object>) expressionEvaluator.evaluateLiteralExpression("{key_a : 1}", Map.class.getCanonicalName(), Collections.emptyList());
        assertThat(parsedValue.containsKey("key_a")).isTrue();
        assertThat(BigDecimal.valueOf(1)).isEqualTo(parsedValue.get("key_a"));
        List<BigDecimal> parsedValueListExpression = (List<BigDecimal>) expressionEvaluator.evaluateLiteralExpression(new TextNode("[10, 12]").toString(), List.class.getCanonicalName(), Collections.emptyList());
        assertThat(parsedValueListExpression.size()).isEqualTo(2);
        assertThat(parsedValueListExpression.get(0)).isEqualTo(BigDecimal.valueOf(10));
        assertThat(parsedValueListExpression.get(1)).isEqualTo(BigDecimal.valueOf(12));

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

        assertThat(parsedValue.size()).isEqualTo(2);
        assertThat(((List<Object>) parsedValue.get(1).get("names")).size()).isEqualTo(2);
        assertThat(((List<Object>) parsedValue.get(1).get("names")).contains("Anna")).isTrue();

        String mapJsonString = "{\"first\": {\"name\": \"\\\"John\\\"\"}}";
        Map<String, Map<String, Object>> parsedMap = (Map<String, Map<String, Object>>) expressionEvaluator
                .convertResult(mapJsonString, Map.class.getCanonicalName(),
                               Arrays.asList(String.class.getCanonicalName(), Object.class.getCanonicalName()));

        assertThat(parsedMap.size()).isEqualTo(1);
        assertThat(parsedMap.get("first").get("name")).isEqualTo("John");

        mapJsonString = "{\"first\": {\"siblings\": [{\"name\" : \"\\\"John\\\"\"}]}}";
        parsedMap = (Map<String, Map<String, Object>>) expressionEvaluator
                .convertResult(mapJsonString, Map.class.getCanonicalName(),
                               Arrays.asList(String.class.getCanonicalName(), Object.class.getCanonicalName()));
        assertThat(parsedMap.size()).isEqualTo(1);
        assertThat(((List<Map<String, Object>>) parsedMap.get("first").get("siblings")).get(0).get("name")).isEqualTo("John");

        mapJsonString = "{\"first\": {\"phones\": {\"number\" : \"1\"}}}";
        parsedMap = (Map<String, Map<String, Object>>) expressionEvaluator
                .convertResult(mapJsonString, Map.class.getCanonicalName(),
                               Arrays.asList(String.class.getCanonicalName(), Object.class.getCanonicalName()));

        assertThat(parsedMap.size()).isEqualTo(1);
        assertThat(((Map<String, Object>) parsedMap.get("first").get("phones")).get("number")).isEqualTo(BigDecimal.valueOf(1));
    }

    @Test
    public void fromObjectToExpressionTest() {
        assertThat(expressionEvaluator.fromObjectToExpression("Test")).isEqualTo("\"Test\"");
        assertThat(expressionEvaluator.fromObjectToExpression(false)).isEqualTo("false");
        assertThat(expressionEvaluator.fromObjectToExpression(BigDecimal.valueOf(1))).isEqualTo("1");
        assertThat(expressionEvaluator.fromObjectToExpression(LocalDate.of(2019, 5, 13))).isEqualTo("date( \"2019-05-13\" )");
        assertThat(expressionEvaluator.fromObjectToExpression(null)).isEqualTo("null");
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
        assertThat(error.get()).isEqualTo(syntaxErrorEvent);

        error.set(null);

        // Syntax error as second
        applyEvents(Arrays.asList(genericError, syntaxErrorEvent), feel);
        assertThat(error.get()).isEqualTo(syntaxErrorEvent);

        error.set(null);

        // Syntax error as first
        applyEvents(Arrays.asList(syntaxErrorEvent, genericError), feel);
        assertThat(error.get()).isEqualTo(syntaxErrorEvent);

        error.set(null);

        // Not error
        applyEvents(Collections.singletonList(notError), feel);
        assertThat(error.get()).isNull();
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
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0)).isEqualTo(BigDecimal.ONE);
        assertThat(result.get(1)).isEqualTo(BigDecimal.TEN);
    }

    @Test
    public void expressionObjectListTest() {
        String expressionCollectionJsonString = new TextNode("[{age:10},{name:\"John\"}]").toString();
        List<Map<String, Object>> result =
                (List<Map<String, Object>>) expressionEvaluator.convertResult(expressionCollectionJsonString,
                                                                              List.class.getCanonicalName(),
                                                                              Collections.EMPTY_LIST);
        assertThat(result.size()).isEqualTo(2);
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
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get("x")).isEqualTo(BigDecimal.valueOf(5));
        assertThat(result.get("y")).isEqualTo(BigDecimal.valueOf(3));
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
        assertThat(expressionEvaluator.verifyResult(expressionCollectionJsonString, contextValue, List.class).isSuccessful()).isTrue();
    }

    @Test
    public void expressionMapVerifyResultTest() {
        String expressionCollectionJsonString = new TextNode("{key_a : 1}").toString();
        Map<String, BigDecimal> contextValue = Collections.singletonMap("key_a", BigDecimal.valueOf(1));
        assertThat(expressionEvaluator.verifyResult(expressionCollectionJsonString, contextValue, Map.class).isSuccessful()).isTrue();
    }

    @Test
    public void isStructuredInput() {
        assertThat(expressionEvaluator.isStructuredInput(List.class.getCanonicalName())).isTrue();
        assertThat(expressionEvaluator.isStructuredInput(ArrayList.class.getCanonicalName())).isTrue();
        assertThat(expressionEvaluator.isStructuredInput(LinkedList.class.getCanonicalName())).isTrue();
        assertThat(expressionEvaluator.isStructuredInput(Map.class.getCanonicalName())).isFalse();
        assertThat(expressionEvaluator.isStructuredInput(HashMap.class.getCanonicalName())).isFalse();
        assertThat(expressionEvaluator.isStructuredInput(LinkedHashMap.class.getCanonicalName())).isFalse();
        assertThat(expressionEvaluator.isStructuredInput(Set.class.getCanonicalName())).isFalse();
        assertThat(expressionEvaluator.isStructuredInput(Integer.class.getCanonicalName())).isFalse();
        assertThat(expressionEvaluator.isStructuredInput(String.class.getCanonicalName())).isFalse();
    }
    
    @Test
    public void testUnaryTestUsingKieExtendedProfile() {
    // DROOLS-6337 today() and now() functions not evaluated correctly in Test Scenarios
        ZonedDateTime now = (ZonedDateTime) expressionEvaluator.evaluateLiteralExpression("now()", ZonedDateTime.class.getCanonicalName(), Collections.emptyList()); 
        LocalDate today = (LocalDate) expressionEvaluator.evaluateLiteralExpression("today()", LocalDate.class.getCanonicalName(), Collections.emptyList());
        assertThat(now).isNotNull();
        assertThat(today).isNotNull();
        assertThat(expressionEvaluator.evaluateUnaryExpression("now() > ?", now.minusDays(1), ZonedDateTime.class).isSuccessful()).isTrue();
        assertThat(expressionEvaluator.evaluateUnaryExpression("today() > ?", today.minusDays(1), LocalDate.class).isSuccessful()).isTrue();
    }
}