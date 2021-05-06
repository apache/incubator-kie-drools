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

package org.kie.pmml.compiler.commons.utils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.github.javaparser.ast.body.MethodDeclaration;
import org.dmg.pmml.DefineFunction;
import org.dmg.pmml.Expression;
import org.junit.Test;
import org.kie.pmml.api.exceptions.KiePMMLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtilsTest.supportedExpressionSupplier;
import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtilsTest.unsupportedExpressionSupplier;

public class DefineFunctionUtilsTest {

    private static final Function<Supplier<Expression>, DefineFunction> defineFunctionCreator = supplier -> {
        Expression expression = supplier.get();
        DefineFunction defineFunction = new DefineFunction();
        defineFunction.setName("DEFINE_FUNCTION_" + expression.getClass().getSimpleName());
        defineFunction.setExpression(expression);
        return defineFunction;
    };

    @Test(expected = KiePMMLException.class)
    public void getDefineFunctionsMethodMapUnsupportedExpression() {
        List<DefineFunction> defineFunctions = unsupportedExpressionSupplier.stream().map(defineFunctionCreator).collect(Collectors.toList());
        DefineFunctionUtils.getDefineFunctionsMethodMap(defineFunctions);
    }

    @Test
    public void getDefineFunctionsMethodMapSupportedExpression() {
        List<DefineFunction> defineFunctions = supportedExpressionSupplier.stream().map(defineFunctionCreator).collect(Collectors.toList());
        Map<String, MethodDeclaration> retrieved = DefineFunctionUtils.getDefineFunctionsMethodMap(defineFunctions);
        assertEquals(defineFunctions.size(), retrieved.size());
    }

    @Test
    public void getDefineFunctionMethodDeclarationUnsupportedExpression() {
        for (Supplier<Expression> supplier : unsupportedExpressionSupplier) {
            DefineFunction defineFunction = defineFunctionCreator.apply(supplier);
            try {
                DefineFunctionUtils.getDefineFunctionMethodDeclaration(defineFunction);
                fail(String.format("Expecting KiePMMLException for %s", defineFunction));
            } catch (Exception e) {
                assertEquals(KiePMMLException.class, e.getClass());
            }
        }
    }

    @Test
    public void getDefineFunctionMethodDeclarationSupportedExpression() {
        for (Supplier<Expression> supplier : supportedExpressionSupplier) {
            DefineFunction defineFunction = defineFunctionCreator.apply(supplier);
            try {
                DefineFunctionUtils.getDefineFunctionMethodDeclaration(defineFunction);
            } catch (Exception e) {
                fail(String.format("Unexpected %s for %s", e, defineFunction.getExpression().getClass()));
            }
        }
    }

    @Test(expected = KiePMMLException.class)
    public void getDefineFunctionMethodDeclarationWithoutExpression() {
        DefineFunctionUtils.getDefineFunctionMethodDeclaration(new DefineFunction());
    }


}