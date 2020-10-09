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

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.VoidType;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;

public class MaterializedLambdaConsequence extends MaterializedLambda {

    private final static String CLASS_NAME_PREFIX = "LambdaConsequence";
    private final List<BitMaskVariable> bitMaskVariables;

    MaterializedLambdaConsequence(String packageName, String ruleClassName, List<BitMaskVariable> bitMaskVariables) {
        super(packageName, ruleClassName);
        this.bitMaskVariables = bitMaskVariables;
    }

    @Override
    void createMethodDeclaration(EnumDeclaration classDeclaration) {
        MethodDeclaration methodDeclaration = classDeclaration.addMethod("execute", Modifier.Keyword.PUBLIC);
        methodDeclaration.setThrownExceptions(NodeList.nodeList(parseClassOrInterfaceType("java.lang.Exception")));
        methodDeclaration.addAnnotation("Override");
        methodDeclaration.setType(new VoidType());

        setMethodParameter(methodDeclaration);

        Statement body = lambdaExpr.getBody().clone();
        if (body.isExpressionStmt()) {
            BlockStmt clone = new BlockStmt(NodeList.nodeList(body));
            methodDeclaration.setBody(clone);
        } else if (body.isBlockStmt()) {
            BlockStmt clone = (BlockStmt) body.clone();
            methodDeclaration.setBody(clone);
        }
    }

    @Override
    String getPrefix() {
        return CLASS_NAME_PREFIX;
    }

    @Override
    protected EnumDeclaration create(CompilationUnit compilationUnit) {
        EnumDeclaration lambdaClass = super.create(compilationUnit);

        boolean hasDroolsParameter = lambdaParameters.stream().anyMatch(this::isDroolsParameter);
        if (hasDroolsParameter) {
            bitMaskVariables.forEach(vd -> vd.generateBitMaskField(lambdaClass));
        }
        return lambdaClass;
    }

    private boolean isDroolsParameter(LambdaParameter p) {
        String anObject = p.type.asString();
        return "org.drools.model.Drools".equals(anObject) || "Drools".equals(anObject);
    }

    @Override
    protected ClassOrInterfaceType functionType() {
        String type = "Block" + lambdaParameters.size();
        return parseClassOrInterfaceType("org.drools.model.functions." + type);
    }
}

