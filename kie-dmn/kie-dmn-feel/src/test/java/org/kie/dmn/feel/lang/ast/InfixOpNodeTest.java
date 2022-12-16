/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.feel.lang.ast;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BinaryOperator;

import ch.obermuhlner.math.big.BigDecimalMath;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InfixOpNodeTest {

    private static List<BinaryOperator<BigDecimal>> MATH_OPERATORS;
    @Before
    public void setUp() throws Exception {
        MATH_OPERATORS = new ArrayList<>();
        MATH_OPERATORS.add((l, r) -> l.add(r, MathContext.DECIMAL128 ) );
        MATH_OPERATORS.add((l, r) -> l.subtract(r, MathContext.DECIMAL128 ) );
        MATH_OPERATORS.add((l, r) -> l.multiply(r, MathContext.DECIMAL128 ) );
        MATH_OPERATORS.add((l, r) -> l.divide(r, MathContext.DECIMAL128 ) );
        MATH_OPERATORS.add((l, r) -> BigDecimalMath.pow(l, r, MathContext.DECIMAL128));
    }

    @Test
    public void math_BothNumbers() {
        final Random rnd = new Random();
        MATH_OPERATORS.forEach(operator -> {
            BigDecimal left = BigDecimal.valueOf(rnd.nextDouble());
            BigDecimal right = BigDecimal.valueOf(rnd.nextDouble());
            BigDecimal expected = operator.apply(left, right);
            Object retrieved = InfixOpNode.math(left, right, null, operator);
            assertThat(retrieved).isNotNull()
                    .isInstanceOf(BigDecimal.class)
                    .isEqualTo(expected);
        });
    }

    @Test
    public void math_NumberAndString() {
        final Random rnd = new Random();
        MATH_OPERATORS.forEach(operator -> {
            BigDecimal left = BigDecimal.valueOf(rnd.nextDouble());
            String right = String.valueOf(rnd.nextDouble());
            Object retrieved = InfixOpNode.math(left, right, null, operator);
            assertThat(retrieved).isNull();
        });
    }
}