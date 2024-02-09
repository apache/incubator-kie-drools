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

import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mvel2.CompileException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.drools.scenariosimulation.api.utils.ConstantsHolder.ACTUAL_VALUE_IDENTIFIER;
import static org.drools.scenariosimulation.api.utils.ConstantsHolder.MVEL_ESCAPE_SYMBOL;

public class MVELExpressionEvaluatorTest {
	
	private Condition<ExpressionEvaluatorResult> successful= new Condition<>(x -> x.isSuccessful(), "isSuccessful");
	private Condition<ExpressionEvaluatorResult> notSuccessful= new Condition<>(x -> !x.isSuccessful(), "isSuccessful");

    private MVELExpressionEvaluator evaluator;

    @Before
    public void setUp() {
        evaluator = new MVELExpressionEvaluator(MVELExpressionEvaluatorTest.class.getClassLoader());
    }
    
    @Test
    public void evaluateUnaryExpression() {
        assertThat(evaluator.evaluateUnaryExpression(mvelExpression("java.util.Objects.equals(" + ACTUAL_VALUE_IDENTIFIER + ", \"Test\")"), "Test", String.class)).is(successful);
        assertThat(evaluator.evaluateUnaryExpression(mvelExpression("java.util.Objects.equals(" + ACTUAL_VALUE_IDENTIFIER + ", " + "\"Test\")"), "Test1", String.class)).is(notSuccessful);
        assertThat(evaluator.evaluateUnaryExpression(mvelExpression("1"), 1, Integer.class)).is(successful);
        assertThat(evaluator.evaluateUnaryExpression(mvelExpression("2"), 1, Integer.class)).is(notSuccessful);

        assertThat(evaluator.evaluateUnaryExpression(mvelExpression(""), null, String.class)).is(successful);
        assertThat(evaluator.evaluateUnaryExpression(mvelExpression(""), "", String.class)).is(notSuccessful);

        assertThat(evaluator.evaluateUnaryExpression(mvelExpression(null), null, String.class)).is(successful);
        assertThat(evaluator.evaluateUnaryExpression(mvelExpression(null), "null", String.class)).is(notSuccessful);

        assertThat(evaluator.evaluateUnaryExpression(mvelExpression("\"\""), "", String.class)).is(successful);
        assertThat(evaluator.evaluateUnaryExpression(mvelExpression(null), "", String.class)).is(notSuccessful);

        assertThat(evaluator.evaluateUnaryExpression(mvelExpression(ACTUAL_VALUE_IDENTIFIER + " == 123"), 123, Integer.class)).is(successful);
        assertThat(evaluator.evaluateUnaryExpression(mvelExpression(ACTUAL_VALUE_IDENTIFIER + " != 123"), 321, Integer.class)).is(successful);
        assertThat(evaluator.evaluateUnaryExpression(mvelExpression(ACTUAL_VALUE_IDENTIFIER + " == 123"), 321, Integer.class)).is(notSuccessful);
        assertThat(evaluator.evaluateUnaryExpression(mvelExpression(ACTUAL_VALUE_IDENTIFIER + " != 123"), 123, Integer.class)).is(notSuccessful);

        assertThatThrownBy(() -> evaluator.evaluateUnaryExpression(null, "", String.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Malformed MVEL expression");
    }

    @Test
    public void evaluateLiteralExpression_many() {
        assertThat(evaluateLiteralExpression("1", Integer.class)).isEqualTo(1);

        assertThat(evaluateLiteralExpression("\"Value\"", String.class)).isEqualTo("Value");

        assertThat(evaluateLiteralExpression("2 * 3", Integer.class)).isEqualTo(6);

        assertThat(evaluateLiteralExpression("-1 + (3 * 5)", Integer.class)).isEqualTo(14);

        assertThat(evaluateLiteralExpression("[\"Jim\"]", ArrayList.class)).isEqualTo(List.of("Jim"));

        assertThat(evaluateLiteralExpression("[]", ArrayList.class)).isEqualTo(List.of());

        assertThat(evaluateLiteralExpression("\"abc..\"[2]", Character.class)).isEqualTo('c');

        assertThat(evaluateLiteralExpression("1.234B", BigDecimal.class)).isEqualTo(new BigDecimal("1.234"));

        assertThat(evaluateLiteralExpression("1.234d", Double.class)).isEqualTo(Double.valueOf("1.234"));

        assertThat(evaluateLiteralExpression("\"Value\"", String.class)).isEqualTo("Value");

        assertThat(evaluateLiteralExpression("a = 1; b = 2; a+b;", Integer.class)).isEqualTo(3);

        assertThat(evaluateLiteralExpression("a = \"Te\"; b = \"st\"; a+b;", String.class)).isEqualTo("Test");

        assertThatThrownBy(() -> evaluateLiteralExpression("a = 1 b = 2 a+b;", Integer.class))
                .isInstanceOf(CompileException.class);

        assertThatThrownBy(() -> evaluateLiteralExpression("a = 1; a+b;", Integer.class))
                .isInstanceOf(CompileException.class);

        assertThat(evaluateLiteralExpression("a = \"Bob\";\n" +
                "test = new java.util.ArrayList();\n" +
                "test.add(a);\n" +
                "test.add(\"Michael\");\n" +
                "test;",
                ArrayList.class)).isEqualTo(List.of("Bob", "Michael"));

        Map<String, String> expectedMap = Map.of("Jim", "Person");

        assertThat(evaluateLiteralExpression("[\"Jim\" : \"Person\"]", HashMap.class)).isEqualTo(expectedMap);
        
        assertThat(evaluateLiteralExpression("a = \"Person\";\n" +
                "test = new java.util.HashMap();\n" +
                "test.put(\"Jim\", a);\n" +
                "test;",
                HashMap.class)).isEqualTo(expectedMap);
        
        assertThat(evaluateLiteralExpression("a = \"Person\";test = new java.util.HashMap();test.put(\"Jim\", a);test;", HashMap.class)).isEqualTo(expectedMap);
        
        assertThat(evaluateLiteralExpression("a = \"Person\";\n" +
                        "test = new java.util.HashMap();\n" +
                        "test.put(\"Jim\", a);\n" +
                        "test;\n" +
                        "test.clear();\n" +
                        "test;",
                HashMap.class)).isEqualTo(Map.of());

        assertThatThrownBy(() -> evaluateLiteralExpression("1+", String.class)).isInstanceOf(RuntimeException.class);

        assertThatThrownBy(() -> evaluateLiteralExpression("1", String.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Cannot assign a 'java.lang.Integer");
    }

    @Ignore("https://issues.redhat.com/browse/DROOLS-4649")
    @Test
    public void evaluateLiteralExpression_Array() {
        assertThat(evaluateLiteralExpression("{\"Jim\", \"Michael\"}", Object[].class)).isEqualTo(new String[]{"Jim", "Michael"});

        assertThat(evaluateLiteralExpression("{ }", Object[].class)).isEqualTo(new String[]{});
    }
    
    private Object evaluateLiteralExpression(String rawExpression, Class klass) {
    	return evaluator.evaluateLiteralExpression(mvelExpression(rawExpression), klass.getCanonicalName(), List.of());
    }

    @Test
    public void fromObjectToExpression() {
        assertThatThrownBy(() -> evaluator.fromObjectToExpression(null))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("The condition has not been satisfied");
    }

    @Test
    public void cleanExpression() {
        assertThat(evaluator.cleanExpression(MVEL_ESCAPE_SYMBOL + "test")).isEqualTo("test");
        assertThat(evaluator.cleanExpression(MVEL_ESCAPE_SYMBOL + " test")).isEqualTo(" test");
        assertThat(evaluator.cleanExpression(MVEL_ESCAPE_SYMBOL + " " + MVEL_ESCAPE_SYMBOL + " test")).isEqualTo(" " + MVEL_ESCAPE_SYMBOL + " test");
        assertThat(evaluator.cleanExpression(new TextNode(MVEL_ESCAPE_SYMBOL + "test").toString())).isEqualTo("test");
        assertThat(evaluator.cleanExpression(new TextNode(MVEL_ESCAPE_SYMBOL + " test").toString())).isEqualTo(" test");
        assertThat(evaluator.cleanExpression(new TextNode(MVEL_ESCAPE_SYMBOL + " " + MVEL_ESCAPE_SYMBOL + " test").toString())).isEqualTo(" " + MVEL_ESCAPE_SYMBOL + " test");

        assertThatThrownBy(() -> evaluator.cleanExpression("test"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Malformed MVEL expression");

        assertThatThrownBy(() -> evaluator.cleanExpression(new TextNode("test").toString()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Malformed MVEL expression");
    }

    private static String mvelExpression(final String expression) {
        return MVEL_ESCAPE_SYMBOL + " " + expression;
    }
}