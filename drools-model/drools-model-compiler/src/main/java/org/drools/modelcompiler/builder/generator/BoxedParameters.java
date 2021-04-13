/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.generator;

import java.util.Collection;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.Type;

public class BoxedParameters {

    RuleContext context;

    public BoxedParameters(RuleContext context) {
        this.context = context;
    }

    // Types in the executable model are promoted to boxed to type check the Java DSL.
    // We add such promoted types as _<PARAMETER_NAME> (with the underscore prefix)
    // and then we downcast to the original unboxed type in the body of the function (methodBody)
    public NodeList<Parameter> getBoxedParametersWithUnboxedAssignment(Collection<String> declarationUsedInRHS,
                                                                       BlockStmt methodBody) {

        NodeList<Parameter> parameters = NodeList.nodeList();

        for (String parameterName : declarationUsedInRHS) {
            DeclarationSpec declaration = context.getDeclarationByIdWithException(parameterName);

            Parameter boxedParameter;
            Type boxedType = declaration.getBoxedType();

            if (declaration.isBoxed()) {
                String boxedParameterName = "_" + parameterName;
                boxedParameter = new Parameter(boxedType, boxedParameterName);
                Expression unboxedTypeDowncast = new VariableDeclarationExpr(new VariableDeclarator(declaration.getRawType(),
                                                                                                    parameterName,
                                                                                                    new NameExpr(boxedParameterName)));
                methodBody.addStatement(0, unboxedTypeDowncast);
            } else {
                boxedParameter = new Parameter(boxedType, parameterName);
            }
            parameters.add(boxedParameter);
        }
        return parameters;
    }
}
