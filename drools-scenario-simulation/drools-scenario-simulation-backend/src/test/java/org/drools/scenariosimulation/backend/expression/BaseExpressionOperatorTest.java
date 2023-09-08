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

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.drools.scenariosimulation.backend.expression.BaseExpressionOperator.EQUALS;
import static org.drools.scenariosimulation.backend.expression.BaseExpressionOperator.LIST_OF_CONDITION;
import static org.drools.scenariosimulation.backend.expression.BaseExpressionOperator.LIST_OF_VALUES;
import static org.drools.scenariosimulation.backend.expression.BaseExpressionOperator.NOT_EQUALS;
import static org.drools.scenariosimulation.backend.expression.BaseExpressionOperator.RANGE;
import static org.drools.scenariosimulation.backend.expression.BaseExpressionOperator.values;

public class BaseExpressionOperatorTest {

    private static final ClassLoader classLoader = BaseExpressionOperatorTest.class.getClassLoader();

    @Test
    public void evaluateLiteralExpression() {
        Arrays.stream(values())
                .filter(e -> !EQUALS.equals(e))
                .forEach(operator -> {
                    assertThatThrownBy(
                            () -> operator.evaluateLiteralExpression(String.class.getCanonicalName(), " Test ", classLoader))
                            .isInstanceOf(IllegalStateException.class)
                            .hasMessageEndingWith(" operator cannot be used in a GIVEN clause");
                });

        assertThat(EQUALS.evaluateLiteralExpression(String.class.getName(), "= Test", classLoader)).isEqualTo("Test");
        assertThat(EQUALS.evaluateLiteralExpression(String.class.getName(), "= ", classLoader)).isEqualTo("");
        assertThat(EQUALS.evaluateLiteralExpression(Integer.class.getName(), "= 1", classLoader)).isEqualTo(1);
        assertThat(EQUALS.evaluateLiteralExpression(Integer.class.getName(), "= 2", classLoader)).isNotEqualTo(1);
        assertThat(EQUALS.evaluateLiteralExpression(TestEnum.class.getName(), "= TEST_1", classLoader)).isEqualTo(TestEnum.TEST_1);
        assertThat(EQUALS.evaluateLiteralExpression(TestEnum.class.getName(), "= TEST_1", classLoader)).isNotEqualTo(TestEnum.TEST_2);
        assertThat(EQUALS.evaluateLiteralExpression(OuterClass.InnerEnum.class.getName(), "= INNER_1", classLoader)).isEqualTo(OuterClass.InnerEnum.INNER_1);
        assertThat(EQUALS.evaluateLiteralExpression(OuterClass.InnerEnum.class.getName(), "= INNER_1", classLoader)).isNotEqualTo(OuterClass.InnerEnum.INNER_2);
        assertThat(EQUALS.evaluateLiteralExpression(String.class.getName(), "null", classLoader)).isNull();
        assertThat(EQUALS.evaluateLiteralExpression(String.class.getName(), "= null", classLoader)).isNull();
        assertThat(EQUALS.evaluateLiteralExpression(String.class.getName(), null, classLoader)).isNull();
    }

    @Test
    public void findOperator() {
        assertThat(BaseExpressionOperator.findOperator("Test")).isEqualTo(EQUALS);
        assertThat(BaseExpressionOperator.findOperator(" Test ")).isEqualTo(EQUALS);
        assertThat(BaseExpressionOperator.findOperator("= Test ")).isEqualTo(EQUALS);
        assertThat(BaseExpressionOperator.findOperator("!= Test ")).isEqualTo(NOT_EQUALS);
    }

    @Test
    public void equalsTest() {
        String test1 = "2019-12-02";
        LocalDate test2 = LocalDate.of(2019, 12, 2);

        assertThat(EQUALS.eval(test1, test2, test2.getClass(), classLoader)).isTrue();
        assertThat(EQUALS.eval(test1, test2.plusDays(1), test2.getClass(), classLoader)).isFalse();

        assertThat(EQUALS.eval("1", 1, int.class, classLoader)).isTrue();

        assertThat(EQUALS.eval(null, null, null, classLoader)).isTrue();
    }

    @Test
    public void notEqualsTest() {
        String test1 = "2019-12-02";
        LocalDate test2 = LocalDate.of(2019, 12, 2);

        assertThat(NOT_EQUALS.eval(test1, test2, test2.getClass(), classLoader)).isFalse();
        assertThat(NOT_EQUALS.eval(test1, test2.plusDays(1), test2.getClass(), classLoader)).isTrue();

        assertThat(NOT_EQUALS.eval("<> 1", 2, int.class, classLoader)).isTrue();

        assertThat(NOT_EQUALS.eval(null, null, null, classLoader)).isFalse();

        // NOT_EQUALS can be composed
        assertThat(NOT_EQUALS.eval("! <1", 2, int.class, classLoader)).isTrue();
        assertThat(NOT_EQUALS.eval("! [1, 3]", 2, int.class, classLoader)).isTrue();
    }

    @Test
    public void rangeTest() {
        assertThat(RANGE.eval("", "test", String.class, classLoader)).isFalse();

        assertThat(RANGE.eval(">2", 3, int.class, classLoader)).isTrue();
    }

    @Test
    public void listOfValuesTest() {
        assertThat(LIST_OF_VALUES.eval("", "test", String.class, classLoader)).isFalse();

        assertThatThrownBy(() -> LIST_OF_VALUES.eval("[ 2", "", String.class, classLoader))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Malformed expression: [ 2");

        assertThat(LIST_OF_VALUES.eval("[ Test, Another Test]", "Another Test", String.class, classLoader)).isTrue();
    }

    @Test
    public void listOfConditionsTest() {
        assertThat(LIST_OF_CONDITION.eval("", "test", String.class, classLoader)).isFalse();

        assertThat(LIST_OF_CONDITION.eval("=1; ![2, 3]; <10", 1, int.class, classLoader)).isTrue();

        assertThat(LIST_OF_CONDITION.eval(null, 1, int.class, classLoader)).isFalse();
    }

    private enum TestEnum {
        TEST_1,
        TEST_2
    }

    private static class OuterClass {
        public enum InnerEnum {
            INNER_1,
            INNER_2
        }
    }
}