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

import org.junit.Test;

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
        assertTrue(evaluator.evaluateUnaryExpression(mvelExpression("java.util.Objects.equals(" + ACTUAL_VALUE_IDENTIFIER + ", \"Test\")"), "Test", String.class));
        assertFalse(evaluator.evaluateUnaryExpression(mvelExpression("java.util.Objects.equals(" + ACTUAL_VALUE_IDENTIFIER + ", " + "\"Test\")"), "Test1", String.class));
        assertTrue(evaluator.evaluateUnaryExpression(mvelExpression("1"), 1, Integer.class));
        assertFalse(evaluator.evaluateUnaryExpression(mvelExpression("2"), 1, Integer.class));

        assertTrue(evaluator.evaluateUnaryExpression(mvelExpression(ACTUAL_VALUE_IDENTIFIER + " == 123"), 123, Integer.class));
        assertTrue(evaluator.evaluateUnaryExpression(mvelExpression(ACTUAL_VALUE_IDENTIFIER + " != 123"), 321, Integer.class));
        assertFalse(evaluator.evaluateUnaryExpression(mvelExpression(ACTUAL_VALUE_IDENTIFIER + " == 123"), 321, Integer.class));
        assertFalse(evaluator.evaluateUnaryExpression(mvelExpression(ACTUAL_VALUE_IDENTIFIER + " != 123"), 123, Integer.class));

        assertThatThrownBy(() -> evaluator.evaluateUnaryExpression(new Object(), "", String.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Raw expression should be a String");

        assertThatThrownBy(() -> evaluator.evaluateUnaryExpression(null, "", String.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Raw expression should be a String");
    }

    @Test
    public void evaluateLiteralExpression() {
        assertEquals(1, evaluator.evaluateLiteralExpression(Integer.class.getCanonicalName(),
                                                            Collections.emptyList(),
                                                            mvelExpression("1")));

        assertEquals("Value", evaluator.evaluateLiteralExpression(String.class.getCanonicalName(),
                                                                  Collections.emptyList(),
                                                                  mvelExpression("\"Value\"")));

        assertEquals(6, evaluator.evaluateLiteralExpression(Integer.class.getCanonicalName(),
                                                            Collections.emptyList(),
                                                            mvelExpression("2 * 3")));

        assertEquals(14, evaluator.evaluateLiteralExpression(Integer.class.getCanonicalName(),
                                                             Collections.emptyList(),
                                                             mvelExpression("-1 + (3 * 5)")));

        assertEquals(Arrays.asList("Jim"), evaluator.evaluateLiteralExpression(ArrayList.class.getCanonicalName(),
                                                                               Collections.emptyList(),
                                                                               mvelExpression("[\"Jim\"]")));

        assertEquals(Collections.emptyList(), evaluator.evaluateLiteralExpression(ArrayList.class.getCanonicalName(),
                                                                                  Collections.emptyList(),
                                                                                  mvelExpression("[]")));

        assertThat(evaluator.evaluateLiteralExpression(Character.class.getCanonicalName(),
                                                       Collections.emptyList(),
                                                       mvelExpression("\"abc..\"[2]")))
                .isEqualTo('c');

        assertThat(evaluator.evaluateLiteralExpression(BigDecimal.class.getCanonicalName(),
                                                       Collections.emptyList(),
                                                       mvelExpression("1.234B")))
                .isEqualTo(new BigDecimal("1.234"));

        assertThat(evaluator.evaluateLiteralExpression(Double.class.getCanonicalName(),
                                                       Collections.emptyList(),
                                                       mvelExpression("1.234d")))
                .isEqualTo(Double.valueOf("1.234"));

        assertEquals("Value", evaluator.evaluateLiteralExpression(String.class.getCanonicalName(), Collections.emptyList(), "# \"Value\""));

        assertThatThrownBy(() -> evaluator.evaluateLiteralExpression(String.class.getCanonicalName(), Collections.emptyList(), "1+"))
                .isInstanceOf(RuntimeException.class);

        assertThatThrownBy(() -> evaluator.evaluateLiteralExpression(String.class.getCanonicalName(), Collections.emptyList(), new Object()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Raw expression should be a String");

        assertThatThrownBy(() -> evaluator.evaluateLiteralExpression(String.class.getCanonicalName(),
                                                                     Collections.emptyList(),
                                                                     mvelExpression("1")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Cannot assign a 'java.lang.Integer");
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

        assertThatThrownBy(() -> evaluator.cleanExpression("test"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Malformed MVEL expression");
    }

    private static String mvelExpression(final String expression) {
        return MVEL_ESCAPE_SYMBOL + " " + expression;
    }
}