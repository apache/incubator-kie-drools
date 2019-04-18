/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.dmn.validation.dtanalysis.verifier;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.dmn.feel.lang.ast.UnaryTestNode;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class OperatorResolverTest {

    private final boolean isNegated;
    private final UnaryTestNode.UnaryOperator operator;
    private final String expected;

    public OperatorResolverTest(final boolean isNegated,
                                final UnaryTestNode.UnaryOperator operator,
                                final String expected) {
        this.isNegated = isNegated;
        this.operator = operator;
        this.expected = expected;
    }

    @Test
    public void name() {
        assertEquals(OperatorResolver.validatorStringOperatorFromUTOperator(isNegated, operator), expected);
    }

    @Parameterized.Parameters
    public static Collection primeNumbers() {
        return Arrays.asList(new Object[][]{
                {false, UnaryTestNode.UnaryOperator.EQ, "=="},
                {false, UnaryTestNode.UnaryOperator.GT, ">"},
                {false, UnaryTestNode.UnaryOperator.GTE, ">="},
                {false, UnaryTestNode.UnaryOperator.LT, "<"},
                {false, UnaryTestNode.UnaryOperator.LTE, "<="},
                {false, UnaryTestNode.UnaryOperator.IN, null},
                {false, UnaryTestNode.UnaryOperator.NE, "!="},
                {false, UnaryTestNode.UnaryOperator.NOT, null},
                {true, UnaryTestNode.UnaryOperator.EQ, "!="},
                {true, UnaryTestNode.UnaryOperator.GT, "<="},
                {true, UnaryTestNode.UnaryOperator.GTE, "<"},
                {true, UnaryTestNode.UnaryOperator.LT, ">="},
                {true, UnaryTestNode.UnaryOperator.LTE, ">"},
                {true, UnaryTestNode.UnaryOperator.IN, null},
                {true, UnaryTestNode.UnaryOperator.NE, "=="},
                {true, UnaryTestNode.UnaryOperator.NOT, null}
        });
    }
}