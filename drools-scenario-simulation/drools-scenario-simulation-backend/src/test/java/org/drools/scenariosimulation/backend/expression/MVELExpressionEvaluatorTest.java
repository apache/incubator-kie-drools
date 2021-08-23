/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Ignore;
import org.junit.Test;
import org.mvel2.CompileException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.drools.scenariosimulation.api.utils.ConstantsHolder.ACTUAL_VALUE_IDENTIFIER;
import static org.drools.scenariosimulation.api.utils.ConstantsHolder.MVEL_ESCAPE_SYMBOL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MVELExpressionEvaluatorTest {

    MVELExpressionEvaluator evaluator = new MVELExpressionEvaluator(MVELExpressionEvaluatorTest.class.getClassLoader());

    @Test
    public void evaluateUnaryExpression() {
        assertTrue(evaluator.evaluateUnaryExpression(mvelExpression("java.util.Objects.equals(" + ACTUAL_VALUE_IDENTIFIER + ", \"Test\")"), "Test", String.class).isSuccessful());
        assertFalse(evaluator.evaluateUnaryExpression(mvelExpression("java.util.Objects.equals(" + ACTUAL_VALUE_IDENTIFIER + ", " + "\"Test\")"), "Test1", String.class).isSuccessful());
        assertTrue(evaluator.evaluateUnaryExpression(mvelExpression("1"), 1, Integer.class).isSuccessful());
        assertFalse(evaluator.evaluateUnaryExpression(mvelExpression("2"), 1, Integer.class).isSuccessful());

        assertTrue(evaluator.evaluateUnaryExpression(mvelExpression(""), null, String.class).isSuccessful());
        assertFalse(evaluator.evaluateUnaryExpression(mvelExpression(""), "", String.class).isSuccessful());

        assertTrue(evaluator.evaluateUnaryExpression(mvelExpression(null), null, String.class).isSuccessful());
        assertFalse(evaluator.evaluateUnaryExpression(mvelExpression(null), "null", String.class).isSuccessful());

        assertTrue(evaluator.evaluateUnaryExpression(mvelExpression("\"\""), "", String.class).isSuccessful());
        assertFalse(evaluator.evaluateUnaryExpression(mvelExpression(null), "", String.class).isSuccessful());

        assertTrue(evaluator.evaluateUnaryExpression(mvelExpression(ACTUAL_VALUE_IDENTIFIER + " == 123"), 123, Integer.class).isSuccessful());
        assertTrue(evaluator.evaluateUnaryExpression(mvelExpression(ACTUAL_VALUE_IDENTIFIER + " != 123"), 321, Integer.class).isSuccessful());
        assertFalse(evaluator.evaluateUnaryExpression(mvelExpression(ACTUAL_VALUE_IDENTIFIER + " == 123"), 321, Integer.class).isSuccessful());
        assertFalse(evaluator.evaluateUnaryExpression(mvelExpression(ACTUAL_VALUE_IDENTIFIER + " != 123"), 123, Integer.class).isSuccessful());

        assertThatThrownBy(() -> evaluator.evaluateUnaryExpression(null, "", String.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Malformed MVEL expression");
    }

    @Test
    public void evaluateLiteralExpression() {
        assertEquals(1, evaluator.evaluateLiteralExpression(mvelExpression("1"), Integer.class.getCanonicalName(),
                                                            Collections.emptyList()
        ));

        assertEquals("Value", evaluator.evaluateLiteralExpression(mvelExpression("\"Value\""), String.class.getCanonicalName(),
                                                                  Collections.emptyList()
        ));

        assertEquals(6, evaluator.evaluateLiteralExpression(mvelExpression("2 * 3"), Integer.class.getCanonicalName(),
                                                            Collections.emptyList()
        ));

        assertEquals(14, evaluator.evaluateLiteralExpression(mvelExpression("-1 + (3 * 5)"), Integer.class.getCanonicalName(),
                                                             Collections.emptyList()
        ));

        assertEquals(Arrays.asList("Jim"), evaluator.evaluateLiteralExpression(mvelExpression("[\"Jim\"]"), ArrayList.class.getCanonicalName(),
                                                                               Collections.emptyList()
        ));

        assertEquals(Collections.emptyList(), evaluator.evaluateLiteralExpression(mvelExpression("[]"), ArrayList.class.getCanonicalName(),
                                                                                  Collections.emptyList()
        ));

        assertThat(evaluator.evaluateLiteralExpression(mvelExpression("\"abc..\"[2]"), Character.class.getCanonicalName(),
                                                       Collections.emptyList()
        ))
                .isEqualTo('c');

        assertThat(evaluator.evaluateLiteralExpression(mvelExpression("1.234B"), BigDecimal.class.getCanonicalName(),
                                                       Collections.emptyList()
        ))
                .isEqualTo(new BigDecimal("1.234"));

        assertThat(evaluator.evaluateLiteralExpression(mvelExpression("1.234d"), Double.class.getCanonicalName(),
                                                       Collections.emptyList()
        ))
                .isEqualTo(Double.valueOf("1.234"));

        assertEquals("Value", evaluator.evaluateLiteralExpression("# \"Value\"", String.class.getCanonicalName(), Collections.emptyList()));

        assertEquals(3, evaluator.evaluateLiteralExpression("# a = 1; b = 2; a+b;", Integer.class.getCanonicalName(), Collections.emptyList()));

        assertEquals("Test", evaluator.evaluateLiteralExpression("# a = \"Te\"; b = \"st\"; a+b;", String.class.getCanonicalName(), Collections.emptyList()));

        assertThatThrownBy(() -> evaluator.evaluateLiteralExpression("# a = 1 b = 2 a+b;", Integer.class.getCanonicalName(), Collections.emptyList()))
                .isInstanceOf(CompileException.class);

        assertThatThrownBy(() -> evaluator.evaluateLiteralExpression("# a = 1; a+b;", Integer.class.getCanonicalName(), Collections.emptyList()))
                .isInstanceOf(CompileException.class);

        assertEquals(Arrays.asList("Bob", "Michael"), evaluator.evaluateLiteralExpression(mvelExpression("a = \"Bob\";\n" +
                                                                                                                 "test = new java.util.ArrayList();\n" +
                                                                                                                 "test.add(a);\n" +
                                                                                                                 "test.add(\"Michael\");\n" +
                                                                                                                 "test;"),
                                                                                          ArrayList.class.getCanonicalName(),
                                                                                          Collections.emptyList()
        ));

        HashMap<String, String> expectedMap = new HashMap<String, String>() {{
            put("Jim", "Person");
        }};

        assertEquals(expectedMap, evaluator.evaluateLiteralExpression(mvelExpression("[\"Jim\" : \"Person\"]"),
                                                                      HashMap.class.getCanonicalName(),
                                                                      Collections.emptyList()
        ));
        assertEquals(expectedMap, evaluator.evaluateLiteralExpression(mvelExpression("a = \"Person\";\n" +
                                                                                             "test = new java.util.HashMap();\n" +
                                                                                             "test.put(\"Jim\", a);\n" +
                                                                                             "test;"),
                                                                      HashMap.class.getCanonicalName(),
                                                                      Collections.emptyList()
        ));
        assertEquals(expectedMap,
                     evaluator.evaluateLiteralExpression(
                             mvelExpression("a = \"Person\";test = new java.util.HashMap();test.put(\"Jim\", a);test;"),
                             HashMap.class.getCanonicalName(),
                             Collections.emptyList()
                     ));
        assertEquals(Collections.emptyMap(),
                     evaluator.evaluateLiteralExpression(
                             mvelExpression("a = \"Person\";\n" +
                                                    "test = new java.util.HashMap();\n" +
                                                    "test.put(\"Jim\", a);\n" +
                                                    "test;\n" +
                                                    "test.clear();\n" +
                                                    "test;"),
                             HashMap.class.getCanonicalName(),
                             Collections.emptyList()
                     ));

        assertThatThrownBy(() -> evaluator.evaluateLiteralExpression("1+", String.class.getCanonicalName(), Collections.emptyList()))
                .isInstanceOf(RuntimeException.class);

        assertThatThrownBy(() -> evaluator.evaluateLiteralExpression(mvelExpression("1"), String.class.getCanonicalName(),
                                                                     Collections.emptyList()
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Cannot assign a 'java.lang.Integer");
    }

    @Ignore("https://issues.redhat.com/browse/DROOLS-4649")
    @Test
    public void evaluateLiteralExpression_Array() {
        assertThat(evaluator.evaluateLiteralExpression(mvelExpression("{\"Jim\", \"Michael\"}"), Object[].class.getCanonicalName(),
                                                       Collections.emptyList()
        ))
                .isEqualTo(new String[]{"Jim", "Michael"});

        assertThat(evaluator.evaluateLiteralExpression(mvelExpression("{ }"), Object[].class.getCanonicalName(),
                                                       Collections.emptyList()
        ))
                .isEqualTo(new String[]{});
    }

    @Test
    public void fromObjectToExpression() {
        assertThatThrownBy(() -> evaluator.fromObjectToExpression(null))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("The condition has not been satisfied");
    }

    @Test
    public void cleanExpression() {
        assertEquals("test", evaluator.cleanExpression(MVEL_ESCAPE_SYMBOL + "test"));
        assertEquals(" test", evaluator.cleanExpression(MVEL_ESCAPE_SYMBOL + " test"));
        assertEquals(" " + MVEL_ESCAPE_SYMBOL + " test", evaluator.cleanExpression(MVEL_ESCAPE_SYMBOL + " " + MVEL_ESCAPE_SYMBOL + " test"));
        assertEquals("test", evaluator.cleanExpression(new TextNode(MVEL_ESCAPE_SYMBOL + "test").toString()));
        assertEquals(" test", evaluator.cleanExpression(new TextNode(MVEL_ESCAPE_SYMBOL + " test").toString()));
        assertEquals(" " + MVEL_ESCAPE_SYMBOL + " test", evaluator.cleanExpression(new TextNode(MVEL_ESCAPE_SYMBOL + " " + MVEL_ESCAPE_SYMBOL + " test").toString()));

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