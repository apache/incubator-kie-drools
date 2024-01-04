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
package org.drools.model.codegen.execmodel.generator;

import java.util.Collection;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.Type;
import org.kie.api.prototype.PrototypeFactInstance;

import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toClassOrInterfaceType;

public class BoxedParameters {

    private final RuleContext context;

    public BoxedParameters(RuleContext context) {
        this.context = context;
    }

    // Types in the executable model are promoted to boxed to type check the Java DSL.
    // We add such promoted types as _<PARAMETER_NAME> (with the underscore prefix)
    // and then we downcast to the original unboxed type in the body of the function (methodBody)
    public NodeList<Parameter> getBoxedParametersWithUnboxedAssignment(Collection<String> declarationUsedInRHS, BlockStmt methodBody) {
        NodeList<Parameter> parameters = NodeList.nodeList();
        for (String parameterName : declarationUsedInRHS) {
            TypedDeclarationSpec declaration = context.getTypedDeclarationByIdWithException(parameterName);
            Parameter boxedParameter = getTypedParameter(methodBody, parameterName, declaration);
            parameters.add(boxedParameter);
        }
        return parameters;
    }

    private static Parameter getTypedParameter(BlockStmt methodBody, String parameterName, TypedDeclarationSpec declaration) {
        Type boxedType = declaration.getBoxedType();
        if (!declaration.isBoxed()) {
            return new Parameter(boxedType, parameterName);
        }

        String boxedParameterName = "_" + parameterName;
        VariableDeclarator varDec = new VariableDeclarator(declaration.getRawType(), parameterName, new NameExpr(boxedParameterName));
        methodBody.addStatement(0, new VariableDeclarationExpr(varDec));
        return new Parameter(boxedType, boxedParameterName);
    }

    public NodeList<Parameter> getParametersForPrototype(Collection<String> declarationUsedInRHS, BlockStmt methodBody) {
        NodeList<Parameter> parameters = NodeList.nodeList();
        for (String parameterName : declarationUsedInRHS) {
            DeclarationSpec declaration = context.getDeclarationByIdWithException(parameterName);
            Parameter boxedParameter = declaration instanceof TypedDeclarationSpec tSpec ?
                    getTypedParameter(methodBody, parameterName, tSpec) :
                    new Parameter(toClassOrInterfaceType(PrototypeFactInstance.class), parameterName);
            parameters.add(boxedParameter);
        }
        return parameters;
    }
}
