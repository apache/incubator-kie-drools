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
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.databind.node.TextNode;

import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.runtime.events.FEELEventBase;
import org.kie.dmn.feel.runtime.events.SyntaxErrorEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;

public class DMNFeelExpressionEvaluatorTest {
	
	private Condition<ExpressionEvaluatorResult> successful= new Condition<>(x -> x.isSuccessful(), "isSuccessful");
	private Condition<ExpressionEvaluatorResult> notSuccessful= new Condition<>(x -> !x.isSuccessful(), "isNotSuccessful");

    private DMNFeelExpressionEvaluator expressionEvaluator;

    @Before
    public void setUp() {
    	expressionEvaluator = new DMNFeelExpressionEvaluator(this.getClass().getClassLoader());
    }
    
    @Test
    public void evaluateUnaryExpression_simpleResult() {
        assertThat(expressionEvaluator.evaluateUnaryExpression("not( true )", false, boolean.class)).is(successful);
        
        assertThat(expressionEvaluator.evaluateUnaryExpression(">2, >5", BigDecimal.valueOf(6), BigDecimal.class)).is(successful);
        
        assertThat(expressionEvaluator.evaluateUnaryExpression("abs(-1)", BigDecimal.valueOf(1), BigDecimal.class)).is(successful);
        
        assertThat(expressionEvaluator.evaluateUnaryExpression("abs(-1)", BigDecimal.valueOf(-1), BigDecimal.class)).is(notSuccessful);
        
        assertThat(expressionEvaluator.evaluateUnaryExpression("max(1, ?) > 1", BigDecimal.valueOf(2), BigDecimal.class)).is(successful);
        
        assertThat(expressionEvaluator.evaluateUnaryExpression("max(1, ?) < 1", BigDecimal.valueOf(2), BigDecimal.class)).is(notSuccessful);
        
        assertThat(expressionEvaluator.evaluateUnaryExpression("? = 2", BigDecimal.valueOf(2), BigDecimal.class)).is(successful);
        
        assertThat(expressionEvaluator.evaluateUnaryExpression("? > 2", BigDecimal.valueOf(2), BigDecimal.class)).is(notSuccessful);
        
        assertThat(expressionEvaluator.evaluateUnaryExpression("? + 1 > ?", BigDecimal.valueOf(2), BigDecimal.class)).is(successful);
    }
    
    @Test
    public void evaluateUnaryExpression_structuredResult_map() {
        Map<String, BigDecimal> contextValue = Map.of("key_a", BigDecimal.valueOf(1));
        
        assertThat(expressionEvaluator.evaluateUnaryExpression("{key_a : 1}", contextValue, Map.class)).is(successful);
        assertThat(expressionEvaluator.evaluateUnaryExpression("{key_a : 2}", contextValue, Map.class)).is(notSuccessful);
    }
    
    @Test
    public void evaluateUnaryExpression_structuredResult_list() {
        List<BigDecimal> contextListValue = List.of(BigDecimal.valueOf(23));
        
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("23").toString(), contextListValue, List.class)).is(successful);
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("2").toString(), contextListValue, List.class)).is(notSuccessful);
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("? = [23]").toString(), contextListValue, List.class)).is(successful);
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("? = [2]").toString(), contextListValue, List.class)).is(notSuccessful);
        
        List<BigDecimal> contextListValue2 = List.of(BigDecimal.valueOf(23), BigDecimal.valueOf(32));

        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode(" ? = [23, 32]").toString(), contextListValue2, List.class)).is(successful);
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("[23, 32]").toString(),
                contextListValue2,
                List.class)).as("Collection unary expression needs to start with ?").is(notSuccessful);
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode(" ? = [23, 32, 123]").toString(), contextListValue2, List.class)).is(notSuccessful);
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode(" ?[1] = 23").toString(), contextListValue2, List.class)).is(successful);
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode(" ?[1] = 32").toString(), contextListValue2, List.class)).is(notSuccessful);
    }
    
    @Test
    public void evaluateUnaryExpression_structuredResult_listOfMaps() {
        Map<String, Object> firstMap = Map.of("Price", new BigDecimal(2000), "Name", "PC");
        Map<String, Object> secondMap = Map.of("Price", new BigDecimal(3300), "Name", "CAR");
        List<Map<String, Object>> context = List.of(firstMap, secondMap);
        String firstParameter = "{Price: 2000,Name:\"PC\"}";
        String secondParameter = "{Price:3300, Name:\"CAR\"}";

        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("?=[" + firstParameter + ", " + secondParameter + "]").toString(), context, List.class)).is(successful);
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("?=[{Price: 2001,Name:\"PC\"}, {Price:3301,Name:\"CAR\"}]").toString(), context, List.class)).is(notSuccessful);
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("?=[{Price: 2000, Name:\"PCA\"}, {Price:3300,Name:\"CARE\"}]").toString(), context, List.class)).is(notSuccessful);
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("?=[{Pric: 2000, Name:\"PC\"}, {Price:3300,Names:\"CARE\"}]").toString(), context, List.class)).is(notSuccessful);
        /* Different order: Failure */
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("?=[" + secondParameter + ", " + firstParameter + "]").toString(), context, List.class)).is(notSuccessful);
        /* IN operator */
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode(firstParameter + " in ?").toString(), context, List.class)).is(successful);
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode(secondParameter + " in ?").toString(), context, List.class)).is(successful);
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("{Price: 2001,Name:\"PC\"} in ?").toString(), context, List.class)).is(notSuccessful);
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("{Price: 3300,Name:\"CARE\"} in ?").toString(), context, List.class)).is(notSuccessful);
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("(" + firstParameter + " in ?) and (" + secondParameter + " in ?)").toString(), context, List.class)).is(successful);
        assertThat(expressionEvaluator.evaluateUnaryExpression(new TextNode("(" + secondParameter + " in ?) and (" + firstParameter + " in ?)").toString(), context, List.class)).is(successful);
    }
    
    @Test
    public void evaluateUnaryExpression_exceptions() {
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
    public void evaluateLiteralExpression_arithmeticExpression() {
        assertThat(expressionEvaluator.evaluateLiteralExpression("2 + 3", BigDecimal.class.getCanonicalName(), null)).isEqualTo(BigDecimal.valueOf(5));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void evaluateLiteralExpression_listExpression() {
        List<BigDecimal> parsedValueListExpression = (List<BigDecimal>) expressionEvaluator.evaluateLiteralExpression(new TextNode("[10, 12]").toString(), List.class.getCanonicalName(), List.of());
        
        assertThat(parsedValueListExpression).hasSize(2).containsExactly(BigDecimal.valueOf(10), BigDecimal.valueOf(12));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void evaluateLiteralExpression_mapExpression() {
        assertThat(expressionEvaluator.evaluateLiteralExpression("2 + 3", BigDecimal.class.getCanonicalName(), null)).isEqualTo(BigDecimal.valueOf(5));
        
        Map<String, Object> parsedValue = (Map<String, Object>) expressionEvaluator.evaluateLiteralExpression("{key_a : 1}", Map.class.getCanonicalName(), List.of());
        
        assertThat(parsedValue).containsEntry("key_a", BigDecimal.valueOf(1));
    }
    
    @Test
    public void evaluateLiteralExpression_exceptions() {
        assertThatThrownBy(() -> expressionEvaluator
                .evaluateLiteralExpression("SPEED", String.class.getCanonicalName(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Error during evaluation:");

        assertThatThrownBy(() -> expressionEvaluator
                .evaluateLiteralExpression("\"SPEED", String.class.getCanonicalName(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Syntax error:");
    }
    
    @Test
    public void evaluateLiteralExpression_now() {
    // DROOLS-6337 today() and now() functions not evaluated correctly in Test Scenarios
        ZonedDateTime now = (ZonedDateTime) expressionEvaluator.evaluateLiteralExpression("now()", ZonedDateTime.class.getCanonicalName(), List.of()); 
        
        assertThat(now).isNotNull();
        assertThat(expressionEvaluator.evaluateUnaryExpression("now() > ?", now.minusDays(1), ZonedDateTime.class)).is(successful);
    }

    @Test
    public void evaluateLiteralExpression_today() {
    // DROOLS-6337 today() and now() functions not evaluated correctly in Test Scenarios
        LocalDate today = (LocalDate) expressionEvaluator.evaluateLiteralExpression("today()", LocalDate.class.getCanonicalName(), List.of());

        assertThat(today).isNotNull();
        assertThat(expressionEvaluator.evaluateUnaryExpression("today() > ?", today.minusDays(1), LocalDate.class)).is(successful);
    }


    @Test
    @SuppressWarnings("unchecked")
    public void convertResult_list() {
        String listJsonString = new TextNode("[ 1, 10 ]").toString();
        List<BigDecimal> result = (List<BigDecimal>) expressionEvaluator.convertResult(listJsonString, List.class.getCanonicalName(), List.of());
        
        assertThat(result).hasSize(2).containsExactly(BigDecimal.ONE, BigDecimal.TEN);
    }

    @Test
    public void convertResult_map() {
        String expressionCollectionJsonString = new TextNode("{ x : 5, y : 3 }").toString();
        Map<String, BigDecimal> result = (Map<String, BigDecimal>) expressionEvaluator.convertResult(expressionCollectionJsonString, Map.class.getCanonicalName(), List.of());
        
        assertThat(result).hasSize(2).containsEntry("x", BigDecimal.valueOf(5)).containsEntry("y", BigDecimal.valueOf(3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertResult_map_fail() {
        String expressionCollectionJsonString = new TextNode(": 5 y : 3 }").toString();
        expressionEvaluator.convertResult(expressionCollectionJsonString, Map.class.getCanonicalName(), List.of());
    }


    
    @SuppressWarnings("unchecked")
    @Test
    public void convertResult_listOfMaps() {
        String listOfMapsJsonString = new TextNode("[{age:10},{name:\"John\"}]").toString();
        List<Map<String, Object>> result =
                (List<Map<String, Object>>) expressionEvaluator.convertResult(listOfMapsJsonString,
                                                                              List.class.getCanonicalName(),
                                                                              List.of());
        
        assertThat(result).hasSize(2);
        assertThat(result.get(0)).containsOnly(entry("age", BigDecimal.TEN));
        assertThat(result.get(1)).containsOnly(entry("name", "John"));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void convertResult_listOfMaps_complexValues() {
        String listJsonString = "[{\"name\": \"\\\"John\\\"\"}, " +
                "{\"name\": \"\\\"John\\\"\", \"names\" : [{\"value\": \"\\\"Anna\\\"\"}, {\"value\": \"\\\"Mario\\\"\"}]}]";

        List<Map<String, Object>> parsedValue = (List<Map<String, Object>>) expressionEvaluator.convertResult(listJsonString, List.class.getCanonicalName(),
                                                                                                              List.of(Map.class.getCanonicalName()));

        assertThat(parsedValue).hasSize(2);
        assertThat(parsedValue.get(0)).hasSize(1).containsEntry("name", "John");
        assertThat(parsedValue.get(1)).hasSize(2).containsEntry("name", "John").containsEntry("names", List.of("Anna", "Mario"));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void convertResult_mapOfMaps() {
        String mapJsonString = "{\"first\": {\"name\": \"\\\"John\\\"\"}}";
        Map<String, Map<String, Object>> parsedMap = (Map<String, Map<String, Object>>) expressionEvaluator
                .convertResult(mapJsonString, Map.class.getCanonicalName(),
                               List.of(String.class.getCanonicalName(), Object.class.getCanonicalName()));

        assertThat(parsedMap).hasSize(1);
        assertThat(parsedMap.get("first")).containsEntry("name", "John");
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void convertResult_mapOfMapsWithListOfMaps() {
    	String mapJsonString = "{\"first\": {\"siblings\": [{\"name\" : \"\\\"John\\\"\"}]}}";
    	Map<String, Map<String, Object>> parsedMap = (Map<String, Map<String, Object>>) expressionEvaluator
                .convertResult(mapJsonString, Map.class.getCanonicalName(),
                               List.of(String.class.getCanonicalName(), Object.class.getCanonicalName()));

    	assertThat(parsedMap).hasSize(1);
        assertThat(((List<Map<String, Object>>) parsedMap.get("first").get("siblings")).get(0)).containsEntry("name", "John");
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void convertResult_mapOfMapsOfMaps() {
    	String mapJsonString = "{\"first\": {\"phones\": {\"number\" : \"1\"}}}";
    	Map<String, Map<String, Object>> parsedMap = (Map<String, Map<String, Object>>) expressionEvaluator
                .convertResult(mapJsonString, Map.class.getCanonicalName(),
                               List.of(String.class.getCanonicalName(), Object.class.getCanonicalName()));

        assertThat(parsedMap).hasSize(1);
        assertThat(((Map<String, Object>) parsedMap.get("first").get("phones")).get("number")).isEqualTo(BigDecimal.valueOf(1));
    }

    @Test
    public void convertResult_list_fail() {
        String expressionCollectionJsonString = new TextNode("[ 1 : 234").toString();
        assertThatIllegalArgumentException().isThrownBy(() -> expressionEvaluator.convertResult(expressionCollectionJsonString, List.class.getCanonicalName(), List.of()));
    }
    

    @Test
    public void fromObjectToExpression() {
        assertThat(expressionEvaluator.fromObjectToExpression("Test")).isEqualTo("\"Test\"");
        assertThat(expressionEvaluator.fromObjectToExpression(false)).isEqualTo("false");
        assertThat(expressionEvaluator.fromObjectToExpression(BigDecimal.valueOf(1))).isEqualTo("1");
        assertThat(expressionEvaluator.fromObjectToExpression(LocalDate.of(2019, 5, 13))).isEqualTo("date( \"2019-05-13\" )");
        assertThat(expressionEvaluator.fromObjectToExpression(null)).isEqualTo("null");
    }

    @Test
    public void listener_notError() {
        FEELEvent notError = new FEELEventBase(Severity.INFO, "info", null);
        AtomicReference<FEELEvent> error = new AtomicReference<>();
        FEEL feel = expressionEvaluator.newFeelEvaluator(error);

        applyEvents(List.of(notError), feel);

        assertThat(error.get()).isNull();
    }
    
    @Test
    public void listener_singleSyntaxError() {
        FEELEvent syntaxErrorEvent = new SyntaxErrorEvent(Severity.ERROR, "test", null, 0, 0, null);
        AtomicReference<FEELEvent> error = new AtomicReference<>();
        FEEL feel = expressionEvaluator.newFeelEvaluator(error);

        applyEvents(List.of(syntaxErrorEvent), feel);

        assertThat(error.get()).isEqualTo(syntaxErrorEvent);
    }   
    
    @Test
    public void listener_sintaxErrorAsFirst() {
        FEELEvent syntaxErrorEvent = new SyntaxErrorEvent(Severity.ERROR, "test", null, 0, 0, null);
        FEELEvent genericError = new FEELEventBase(Severity.ERROR, "error", null);
        AtomicReference<FEELEvent> error = new AtomicReference<>();
        FEEL feel = expressionEvaluator.newFeelEvaluator(error);

        applyEvents(List.of(syntaxErrorEvent, genericError), feel);

        assertThat(error.get()).isEqualTo(syntaxErrorEvent);
    }


    @Test
    public void listener_syntaxErrorAsSecond() {
        FEELEvent syntaxErrorEvent = new SyntaxErrorEvent(Severity.ERROR, "test", null, 0, 0, null);
        FEELEvent genericError = new FEELEventBase(Severity.ERROR, "error", null);
        AtomicReference<FEELEvent> error = new AtomicReference<>();
        FEEL feel = expressionEvaluator.newFeelEvaluator(error);

        applyEvents(List.of(genericError, syntaxErrorEvent), feel);

        assertThat(error.get()).isEqualTo(syntaxErrorEvent);
    }
    
    private void applyEvents(List<FEELEvent> events, FEEL feel) {
        for (FEELEvent event : events) {
            feel.getListeners().forEach(listener -> listener.onEvent(event));
        }
    }


    @Test
    public void verifyResult_listResult() {
        String expressionCollectionJsonString = new TextNode("10").toString();
        List<BigDecimal> contextValue = List.of(BigDecimal.valueOf(10));
        
        assertThat(expressionEvaluator.verifyResult(expressionCollectionJsonString, contextValue, List.class)).is(successful);
    }

    @Test
    public void verifyResult_mapResult() {
        String expressionCollectionJsonString = new TextNode("{key_a : 1}").toString();
        Map<String, BigDecimal> contextValue = Map.of("key_a", BigDecimal.valueOf(1));
        
        assertThat(expressionEvaluator.verifyResult(expressionCollectionJsonString, contextValue, Map.class)).is(successful);
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
    

}