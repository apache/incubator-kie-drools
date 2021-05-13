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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.body.MethodDeclaration;
import org.dmg.pmml.DefineFunction;
import org.dmg.pmml.Expression;
import org.kie.pmml.api.exceptions.KiePMMLException;

import static org.kie.pmml.compiler.commons.utils.ExpressionFunctionUtils.getExpressionMethodDeclarationWithVariableParameters;

/**
 * Class meant to provide <i>helper</i> methods to retrieve <code>Function</code> code-generators
 * out of <code>DefineFunction</code>s
 */
public class DefineFunctionUtils {

    private DefineFunctionUtils() {
        // Avoid instantiation
    }

    static Map<String, MethodDeclaration> getDefineFunctionsMethodMap(final List<DefineFunction> defineFunctions) {
        Map<String, MethodDeclaration> toReturn = new HashMap<>();
        defineFunctions.forEach(defineFunction ->
                                        toReturn.put(defineFunction.getName(),
                                                     getDefineFunctionMethodDeclaration(defineFunction)));
        return toReturn;
    }

    static MethodDeclaration getDefineFunctionMethodDeclaration(final DefineFunction defineFunction) {
        final Expression expression = defineFunction.getExpression();
        if (expression != null) {
            return getExpressionMethodDeclarationWithVariableParameters(defineFunction.getName(), expression, defineFunction.getDataType(),
                                                                        defineFunction.getParameterFields());
        } else {
            throw new KiePMMLException("Define Function without Expression are not supported, yet");
        }
    }

}
