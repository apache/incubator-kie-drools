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

import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.drools.scenariosimulation.backend.expression.BaseExpressionOperator.EQUALS;
import static org.drools.scenariosimulation.backend.expression.BaseExpressionOperator.LIST_OF_CONDITION;
import static org.drools.scenariosimulation.backend.expression.BaseExpressionOperator.LIST_OF_VALUES;
import static org.drools.scenariosimulation.backend.expression.BaseExpressionOperator.NOT_EQUALS;
import static org.drools.scenariosimulation.backend.expression.BaseExpressionOperator.RANGE;
import static org.drools.scenariosimulation.backend.expression.BaseExpressionOperator.values;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class BaseExpressionOperatorTest {

    private static final ClassLoader classLoader = BaseExpressionOperatorTest.class.getClassLoader();

    @Test
    public void evaluateLiteralExpression() {

        Arrays.stream(values())
                .filter(e -> !EQUALS.equals(e))
                .forEach(operator -> {
                    Assertions.assertThatThrownBy(
                            () -> operator.evaluateLiteralExpression(String.class.getCanonicalName(), " Test ", classLoader))
                            .isInstanceOf(IllegalStateException.class)
                            .hasMessageEndingWith(" operator cannot be used in a GIVEN clause");
                });

        assertEquals("Test", EQUALS.evaluateLiteralExpression(String.class.getCanonicalName(), "= Test", classLoader));
        assertEquals("", EQUALS.evaluateLiteralExpression(String.class.getCanonicalName(), "= ", classLoader));
        assertNull(EQUALS.evaluateLiteralExpression(String.class.getCanonicalName(), "null", classLoader));
        assertNull(EQUALS.evaluateLiteralExpression(String.class.getCanonicalName(), "= null", classLoader));
        assertNull(EQUALS.evaluateLiteralExpression(String.class.getCanonicalName(), null, classLoader));
    }

    @Test
    public void findOperator() {
        String rawValue = "Test";
        assertEquals(EQUALS, BaseExpressionOperator.findOperator(rawValue));

        rawValue = " Test ";
        assertEquals(EQUALS, BaseExpressionOperator.findOperator(rawValue));

        rawValue = "= Test ";
        assertEquals(EQUALS, BaseExpressionOperator.findOperator(rawValue));

        rawValue = "!= Test ";
        assertEquals(NOT_EQUALS, BaseExpressionOperator.findOperator(rawValue));
    }

    @Test
    public void equalsTest() {
        MyTestClass test1 = new MyTestClass();
        MyTestClass test2 = new MyTestClass();
        MyComparableTestClass comparableTest1 = new MyComparableTestClass();

        // Tested via Objects.equals
        assertTrue(EQUALS.eval(test1, test1, test1.getClass(), classLoader));
        assertFalse(EQUALS.eval(test1, test2, test1.getClass(), classLoader));
        // Tested via Comparable.compareTo
        assertTrue(EQUALS.eval(comparableTest1, comparableTest1, comparableTest1.getClass(), classLoader));

        assertTrue(EQUALS.eval("1", 1, int.class, classLoader));

        assertTrue(EQUALS.eval(null, null, null, classLoader));
    }

    @Test
    public void notEqualsTest() {
        MyTestClass test1 = new MyTestClass();
        MyTestClass test2 = new MyTestClass();
        MyComparableTestClass comparableTest1 = new MyComparableTestClass();

        // Tested via Objects.equals
        assertFalse(NOT_EQUALS.eval(test1, test1, test1.getClass(), classLoader));
        assertTrue(NOT_EQUALS.eval(test1, test2, test1.getClass(), classLoader));
        // Tested via Comparable.compareTo
        assertFalse(NOT_EQUALS.eval(comparableTest1, comparableTest1, comparableTest1.getClass(), classLoader));

        assertTrue(NOT_EQUALS.eval("<> 1", 2, int.class, classLoader));

        assertFalse(NOT_EQUALS.eval(null, null, null, classLoader));

        // NOT_EQUALS can be composed
        assertTrue(NOT_EQUALS.eval("! <1", 2, int.class, classLoader));
        assertTrue(NOT_EQUALS.eval("! [1, 3]", 2, int.class, classLoader));
    }

    @Test
    public void rangeTest() {
        Object o = new Object();
        assertFalse(RANGE.eval(o, "", o.getClass(), classLoader));

        assertTrue(RANGE.eval(">2", 3, int.class, classLoader));
    }

    @Test
    public void listOfValuesTest() {
        Object o = new Object();
        assertFalse(LIST_OF_VALUES.eval(o, "", o.getClass(), classLoader));

        assertThatThrownBy(() -> LIST_OF_VALUES.eval("[ 2", "", String.class, classLoader))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Malformed expression: [ 2");

        assertTrue(LIST_OF_VALUES.eval("[ Test, Another Test]", "Another Test", String.class, classLoader));
    }

    @Test
    public void listOfConditionsTest() {
        Object o = new Object();
        assertFalse(LIST_OF_CONDITION.eval(o, "", o.getClass(), classLoader));

        assertTrue(LIST_OF_CONDITION.eval("=1; ![2, 3]; <10", 1, int.class, classLoader));
    }

    private class MyTestClass {

    }

    private class MyComparableTestClass implements Comparable<MyComparableTestClass> {

        @Override
        public int compareTo(MyComparableTestClass o) {
            return 0;
        }
    }
}