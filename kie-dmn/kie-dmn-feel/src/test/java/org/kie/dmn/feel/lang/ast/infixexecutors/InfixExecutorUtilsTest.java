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
package org.kie.dmn.feel.lang.ast.infixexecutors;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

import ch.obermuhlner.math.big.BigDecimalMath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.lang.EvaluationContext;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.isAllowedMultiplicationBasedOnSpec;
import static org.kie.dmn.feel.lang.ast.infixexecutors.InfixExecutorUtils.math;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class InfixExecutorUtilsTest {

    private static List<BinaryOperator<BigDecimal>> MATH_OPERATORS;

    @BeforeEach
    void setUp() throws Exception {
        MATH_OPERATORS = new ArrayList<>();
        MATH_OPERATORS.add((l, r) -> l.add(r, MathContext.DECIMAL128));
        MATH_OPERATORS.add((l, r) -> l.subtract(r, MathContext.DECIMAL128));
        MATH_OPERATORS.add((l, r) -> l.multiply(r, MathContext.DECIMAL128));
        MATH_OPERATORS.add((l, r) -> l.divide(r, MathContext.DECIMAL128));
        MATH_OPERATORS.add((l, r) -> BigDecimalMath.pow(l, r, MathContext.DECIMAL128));
    }

    @Test
    void math_BothNumbers() {
        final Random rnd = new Random();
        MATH_OPERATORS.forEach(operator -> {
            BigDecimal left = BigDecimal.valueOf(rnd.nextDouble());
            BigDecimal right = BigDecimal.valueOf(rnd.nextDouble());
            BigDecimal expected = operator.apply(left, right);
            Object retrieved = math(left, right, null, operator);
            assertThat(retrieved).isNotNull()
                    .isInstanceOf(BigDecimal.class)
                    .isEqualTo(expected);
        });
    }

    @Test
    void math_NumberAndString() {
        final Random rnd = new Random();
        MATH_OPERATORS.forEach(operator -> {
            BigDecimal left = BigDecimal.valueOf(rnd.nextDouble());
            String right = String.valueOf(rnd.nextDouble());
            Object retrieved = math(left, right, null, operator);
            assertThat(retrieved).isNull();
        });
    }

    @Test
    void isAllowedMultiplicationBasedOnSpecTest() {
        EvaluationContext evaluationContext = mock(EvaluationContext.class);
        Object left = 23;
        Object right = 354.5;
        assertThat(isAllowedMultiplicationBasedOnSpec(left, right, evaluationContext)).isTrue();
        verify(evaluationContext, never()).notifyEvt(any());
        right = Duration.of(5, DAYS);
        assertThat(isAllowedMultiplicationBasedOnSpec(left, right, evaluationContext)).isTrue();
        verify(evaluationContext, never()).notifyEvt(any());
        left = Duration.of(5, DAYS);
        right = 354.5;
        assertThat(isAllowedMultiplicationBasedOnSpec(left, right, evaluationContext)).isTrue();
        verify(evaluationContext, never()).notifyEvt(any());
        left = Duration.of(5, DAYS);
        right = Duration.of(5, DAYS);
        assertThat(isAllowedMultiplicationBasedOnSpec(left, right, evaluationContext)).isFalse();
        verify(evaluationContext, times(1)).notifyEvt(any(Supplier.class));
    }

}