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
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class ParameterizedBaseExpressionEvaluatorTest {

    private final static ClassLoader classLoader = ParameterizedBaseExpressionEvaluatorTest.class.getClassLoader();
    private final static BaseExpressionEvaluator baseExpressionEvaluator = new BaseExpressionEvaluator(classLoader);

    @Parameterized.Parameters(name = "{index}: Expr \"{0} {1}\" should be true")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {true, 1, "1", int.class},
                {true, 2, "!= 1", int.class},
                {true, -1, "- 1", int.class},
                {true, -2, "< -  1", int.class},
                {true, -2L, "< -  1", long.class},
                {true, -2D, "< -  1", double.class},
                {true, -2F, "< -  1", float.class},
                {true, (short) -2, "< - 1", short.class},
                {true, "String", "<> Test", String.class},
                {true, "Test", "= Test", String.class},
                {true, 1, "<2", int.class},
                {true, 1, "<2; >0", int.class},
                {true, 2, " <= 2 ", int.class},
                {true, 2, " >= 2", int.class},
                {true, 1, "[ 1, 2 ,3]", int.class},
                {true, 2, "[ 1, 2 ,3]", int.class},
                {true, "3", "[ 1, 2 ,3]", String.class},
                {true, 4, "![ 1, 2 ,3]", int.class},
                {true, 4, "! < 1", int.class},
                {true, 1, "> -1", int.class},
                {true, 10, "!= <10;!= >11", int.class},
                {true, 10, "= 10; >9", int.class},
                {true, null, null, Integer.class},
                {true, null, "!1", Integer.class},
                {true, 'b', "!a", Character.class},
                {true, "0".getBytes()[0], "![47, 49, 50]", byte.class},
                {true, "0".getBytes()[0], ">40; <60", byte.class},
                {true, "0".getBytes()[0], ">30; <100", Byte.class},
                {true, "0".getBytes()[0], "[48, 49, 50]", Byte.class},
                {true, (short) 1, ">0", Short.class},
                {true, null, "[ !false]", boolean.class},
                {true, null, "[! false, ! true]", boolean.class},
                {true, 10, "[> 1]", int.class},
                {true, 10, "[< 1, > 1]", int.class},
                {true, "", ";", String.class},
                {false, null, ";", String.class},
                {false, null, "=", String.class},
                {false, null, "[]", String.class},
                {true, Error.class, ";", boolean.class},
                {true, Error.class, "[]", boolean.class},
                {true, Error.class, "=", boolean.class},
                {true, Error.class, "!= false; <> false, ! false", boolean.class},
                {true, Error.class, "<> false, ! false", boolean.class},
                {true, Error.class, "! tru", void.class},
                {true, Error.class, "fals", void.class},
                {true, Error.class, "!= fals", void.class},
                {true, Error.class, "tru", void.class},
                {true, Error.class, "<> fals", void.class},
                {true, Error.class, "tru", void.class},
                {true, Error.class, "!m= false", void.class},
                {true, Error.class, ">> 3", void.class},
                {true, Error.class, "< - 1 1", int.class}
        });
    }

    @Parameterized.Parameter(0)
    public Boolean expectedResult;

    @Parameterized.Parameter(1)
    public Object resultValue;

    @Parameterized.Parameter(2)
    public String exprToTest;

    @Parameterized.Parameter(3)
    public Class<?> clazz;

    @Test
    public void evaluateUnaryExpression() {

        if (!(resultValue instanceof Class)) {
            assertEquals(expectedResult, baseExpressionEvaluator.evaluateUnaryExpression(exprToTest, resultValue, clazz));
        } else {
            try {
                baseExpressionEvaluator.evaluateUnaryExpression((String) exprToTest, true, clazz);
                fail();
            } catch (Exception ignored) {
            }
        }
    }
}