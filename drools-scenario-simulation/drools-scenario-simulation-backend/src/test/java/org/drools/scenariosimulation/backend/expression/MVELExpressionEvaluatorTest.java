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

import java.util.Collections;

import org.junit.Test;

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
        assertTrue(evaluator.evaluateUnaryExpression(MVEL_ESCAPE_SYMBOL + " java.util.Objects.equals(" + ACTUAL_VALUE_IDENTIFIER + ", \"Test\"" + ")", "Test", String.class));
        assertFalse(evaluator.evaluateUnaryExpression(MVEL_ESCAPE_SYMBOL + " java.util.Objects.equals(" + ACTUAL_VALUE_IDENTIFIER + ", \"Test\"" + ")", "Test1", String.class));
        assertTrue(evaluator.evaluateUnaryExpression(MVEL_ESCAPE_SYMBOL + " 1", 1, Integer.class));
        assertFalse(evaluator.evaluateUnaryExpression(MVEL_ESCAPE_SYMBOL + " 2", 1, Integer.class));

        assertThatThrownBy(() -> evaluator.evaluateUnaryExpression(new Object(), "", String.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Raw expression should be a String");

        assertThatThrownBy(() -> evaluator.evaluateUnaryExpression(null, "", String.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Raw expression should be a String");
    }

    @Test
    public void evaluateLiteralExpression() {
        assertEquals(1, evaluator.evaluateLiteralExpression(Integer.class.getCanonicalName(), Collections.emptyList(), MVEL_ESCAPE_SYMBOL + " 1"));

        assertEquals("Value", evaluator.evaluateLiteralExpression(String.class.getCanonicalName(), Collections.emptyList(), MVEL_ESCAPE_SYMBOL + " \"Value\""));

        assertThatThrownBy(() -> evaluator.evaluateLiteralExpression(String.class.getCanonicalName(), Collections.emptyList(), "1+"))
                .isInstanceOf(RuntimeException.class);

        assertThatThrownBy(() -> evaluator.evaluateLiteralExpression(String.class.getCanonicalName(), Collections.emptyList(), new Object()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageStartingWith("Raw expression should be a String");

        assertThatThrownBy(() -> evaluator.evaluateLiteralExpression(String.class.getCanonicalName(), Collections.emptyList(), MVEL_ESCAPE_SYMBOL + " 1"))
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
}