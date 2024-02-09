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

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(Parameterized.class)
public class ParameterizedBaseExpressionEvaluatorTest {

    private final static ClassLoader classLoader = ParameterizedBaseExpressionEvaluatorTest.class.getClassLoader();
    private final static BaseExpressionEvaluator baseExpressionEvaluator = new BaseExpressionEvaluator(classLoader);

    @Parameterized.Parameters(name = "{index}: Expr \"{0} {1}\" should be true")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {ExpressionEvaluatorResult.ofSuccessful(), 1, "1", int.class},
                {ExpressionEvaluatorResult.ofSuccessful(), 2, "!= 1", int.class},
                {ExpressionEvaluatorResult.ofSuccessful(), -1, "- 1", int.class},
                {ExpressionEvaluatorResult.ofSuccessful(), -2, "< -  1", int.class},
                {ExpressionEvaluatorResult.ofSuccessful(), -2L, "< -  1", long.class},
                {ExpressionEvaluatorResult.ofSuccessful(), -2D, "< -  1", double.class},
                {ExpressionEvaluatorResult.ofSuccessful(), -2F, "< -  1", float.class},
                {ExpressionEvaluatorResult.ofSuccessful(), (short) -2, "< - 1", short.class},
                {ExpressionEvaluatorResult.ofSuccessful(), "String", "<> Test", String.class},
                {ExpressionEvaluatorResult.ofSuccessful(), "Test", "= Test", String.class},
                {ExpressionEvaluatorResult.ofSuccessful(), 1, "<2", int.class},
                {ExpressionEvaluatorResult.ofSuccessful(), 1, "<2; >0", int.class},
                {ExpressionEvaluatorResult.ofSuccessful(), 2, " <= 2 ", int.class},
                {ExpressionEvaluatorResult.ofSuccessful(), 2, " >= 2", int.class},
                {ExpressionEvaluatorResult.ofSuccessful(), 1, "[ 1, 2 ,3]", int.class},
                {ExpressionEvaluatorResult.ofSuccessful(), 2, "[ 1, 2 ,3]", int.class},
                {ExpressionEvaluatorResult.ofSuccessful(), "3", "[ 1, 2 ,3]", String.class},
                {ExpressionEvaluatorResult.ofSuccessful(), 4, "![ 1, 2 ,3]", int.class},
                {ExpressionEvaluatorResult.ofSuccessful(), 4, "! < 1", int.class},
                {ExpressionEvaluatorResult.ofSuccessful(), 1, "> -1", int.class},
                {ExpressionEvaluatorResult.ofSuccessful(), 10, "!= <10;!= >11", int.class},
                {ExpressionEvaluatorResult.ofSuccessful(), 10, "= 10; >9", int.class},
                {ExpressionEvaluatorResult.ofSuccessful(), null, null, Integer.class},
                {ExpressionEvaluatorResult.ofSuccessful(), null, "!1", Integer.class},
                {ExpressionEvaluatorResult.ofSuccessful(), 'b', "!a", Character.class},
                {ExpressionEvaluatorResult.ofSuccessful(), "0".getBytes()[0], "![47, 49, 50]", byte.class},
                {ExpressionEvaluatorResult.ofSuccessful(), "0".getBytes()[0], ">40; <60", byte.class},
                {ExpressionEvaluatorResult.ofSuccessful(), "0".getBytes()[0], ">30; <100", Byte.class},
                {ExpressionEvaluatorResult.ofSuccessful(), "0".getBytes()[0], "[48, 49, 50]", Byte.class},
                {ExpressionEvaluatorResult.ofSuccessful(), (short) 1, ">0", Short.class},
                {ExpressionEvaluatorResult.ofSuccessful(), null, "[ !false]", boolean.class},
                {ExpressionEvaluatorResult.ofSuccessful(), null, "[! false, ! true]", boolean.class},
                {ExpressionEvaluatorResult.ofSuccessful(), 10, "[> 1]", int.class},
                {ExpressionEvaluatorResult.ofSuccessful(), 10, "[< 1, > 1]", int.class},
                {ExpressionEvaluatorResult.ofSuccessful(), "", ";", String.class},
                {ExpressionEvaluatorResult.ofFailed(), null, ";", String.class},
                {ExpressionEvaluatorResult.ofFailed(), null, "=", String.class},
                {ExpressionEvaluatorResult.ofFailed(), null, "[]", String.class},
                {ExpressionEvaluatorResult.ofSuccessful(), Error.class, ";", boolean.class},
                {ExpressionEvaluatorResult.ofSuccessful(), Error.class, "[]", boolean.class},
                {ExpressionEvaluatorResult.ofSuccessful(), Error.class, "=", boolean.class},
                {ExpressionEvaluatorResult.ofSuccessful(), Error.class, "!= false; <> false, ! false", boolean.class},
                {ExpressionEvaluatorResult.ofSuccessful(), Error.class, "<> false, ! false", boolean.class},
                {ExpressionEvaluatorResult.ofSuccessful(), Error.class, "! tru", void.class},
                {ExpressionEvaluatorResult.ofSuccessful(), Error.class, "fals", void.class},
                {ExpressionEvaluatorResult.ofSuccessful(), Error.class, "!= fals", void.class},
                {ExpressionEvaluatorResult.ofSuccessful(), Error.class, "tru", void.class},
                {ExpressionEvaluatorResult.ofSuccessful(), Error.class, "<> fals", void.class},
                {ExpressionEvaluatorResult.ofSuccessful(), Error.class, "tru", void.class},
                {ExpressionEvaluatorResult.ofSuccessful(), Error.class, "!m= false", void.class},
                {ExpressionEvaluatorResult.ofSuccessful(), Error.class, ">> 3", void.class},
                {ExpressionEvaluatorResult.ofSuccessful(), Error.class, "< - 1 1", int.class}
        });
    }

    @Parameterized.Parameter(0)
    public ExpressionEvaluatorResult expectedResult;

    @Parameterized.Parameter(1)
    public Object resultValue;

    @Parameterized.Parameter(2)
    public String exprToTest;

    @Parameterized.Parameter(3)
    public Class<?> clazz;

    @Test
    public void evaluateUnaryExpression() {

        if (!(resultValue instanceof Class)) {
            assertThat(baseExpressionEvaluator.evaluateUnaryExpression(exprToTest, resultValue, clazz).isSuccessful()).isEqualTo(expectedResult.isSuccessful());
        } else {
            try {
                baseExpressionEvaluator.evaluateUnaryExpression(exprToTest, true, clazz);
                fail("Should have failed");
            } catch (Exception ignored) {
            }
        }
    }
}