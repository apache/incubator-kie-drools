/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.util.lambdareplace;

import java.util.List;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static com.github.javaparser.StaticJavaParser.parseType;
import static com.github.javaparser.ast.NodeList.nodeList;

public class MaterializedLambdaExtractor extends MaterializedLambda {

    private final static String CLASS_NAME_PREFIX = "LambdaExtractor";
    private String returnType;

    MaterializedLambdaExtractor(String packageName, String ruleClassName, String returnType) {
        super(packageName, ruleClassName);
        this.returnType = returnType;
    }

    @Override
    void createMethodsDeclaration(EnumDeclaration classDeclaration) {
        MethodDeclaration methodDeclaration = classDeclaration.addMethod("apply", Modifier.Keyword.PUBLIC);
        methodDeclaration.addAnnotation("Override");
        methodDeclaration.setType(returnTypeJP());

        setMethodParameter(methodDeclaration);

        Statement clonedBody = lambdaExpr.getBody().clone();
        if (clonedBody.isExpressionStmt()) {
            methodDeclaration.setBody(new BlockStmt(nodeList(new ReturnStmt(clonedBody
                                                                                    .asExpressionStmt()
                                                                                    .getExpression()))));
        } else if (clonedBody.isBlockStmt()) {
            methodDeclaration.setBody(clonedBody.asBlockStmt());
        }
    }

    private Type returnTypeJP() {
        return parseType(returnType);
    }

    @Override
    protected NodeList<ClassOrInterfaceType> createImplementedTypes() {
        ClassOrInterfaceType functionType = functionType();

        List<Type> typeArguments = lambdaParametersToType();
        NodeList<Type> implementedGenericType = nodeList(typeArguments);
        implementedGenericType.add(returnTypeJP());
        functionType.setTypeArguments(implementedGenericType);
        return nodeList(functionType, lambdaExtractorType());
    }

    @Override
    String getPrefix() {
        return CLASS_NAME_PREFIX;
    }

    @Override
    protected ClassOrInterfaceType functionType() {
        String type = "Function" + lambdaParameters.size();
        return parseClassOrInterfaceType("org.drools.model.functions." + type);
    }
}
