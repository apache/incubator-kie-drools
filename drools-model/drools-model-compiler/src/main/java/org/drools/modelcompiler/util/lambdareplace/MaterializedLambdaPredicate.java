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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import org.drools.model.functions.PredicateInformation;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.createSimpleAnnotation;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.mvelcompiler.util.TypeUtils.toJPType;

public class MaterializedLambdaPredicate extends MaterializedLambda {

    private static final String CLASS_NAME_PREFIX = "LambdaPredicate";
    private final PredicateInformation predicateInformation;

    MaterializedLambdaPredicate(String packageName, String ruleClassName, PredicateInformation predicateInformation) {
        super(packageName, ruleClassName);
        this.predicateInformation = predicateInformation;
    }

    @Override
    String getPrefix() {
        return CLASS_NAME_PREFIX;
    }

    @Override
    void createMethodsDeclaration(EnumDeclaration classDeclaration) {
        createTestMethod(classDeclaration);
        if (!predicateInformation.isEmpty()) {
            createPredicateInformationMethod(classDeclaration);
        }
    }

    private void createTestMethod(EnumDeclaration classDeclaration) {
        MethodDeclaration methodDeclaration = classDeclaration.addMethod("test", Modifier.Keyword.PUBLIC);
        methodDeclaration.setThrownExceptions(NodeList.nodeList(toClassOrInterfaceType(java.lang.Exception.class)));
        methodDeclaration.addAnnotation(createSimpleAnnotation("Override"));
        methodDeclaration.setType(new PrimitiveType(PrimitiveType.Primitive.BOOLEAN));

        setMethodParameter(methodDeclaration);

        ExpressionStmt clone = (ExpressionStmt) lambdaExpr.getBody().clone();
        methodDeclaration.setBody(new BlockStmt(NodeList.nodeList(new ReturnStmt(clone.getExpression()))));
    }

    private void createPredicateInformationMethod(EnumDeclaration classDeclaration) {
        MethodDeclaration methodDeclaration = classDeclaration.addMethod("predicateInformation", Modifier.Keyword.PUBLIC);
        methodDeclaration.addAnnotation("Override");
        ClassOrInterfaceType predicateInformationType = (ClassOrInterfaceType) toJPType(PredicateInformation.class);
        methodDeclaration.setType(predicateInformationType);

        BlockStmt block = new BlockStmt();

        NameExpr infoExpr = new NameExpr("info");
        VariableDeclarationExpr infoVar = new VariableDeclarationExpr(toClassOrInterfaceType(PredicateInformation.class), "info");
        ObjectCreationExpr newPredicateInformation = new ObjectCreationExpr(null, predicateInformationType, NodeList.nodeList(new StringLiteralExpr().setString(predicateInformation.getStringConstraint())));
        AssignExpr infoAssign = new AssignExpr(infoVar, newPredicateInformation, AssignExpr.Operator.ASSIGN);
        block.addStatement(infoAssign);

        List<MethodCallExpr> addRuleNameMethods = new ArrayList<>();
        Map<String, Set<String>> ruleNameMap = predicateInformation.getRuleNameMap();
        ruleNameMap.forEach((ruleFileName, ruleNameList) -> {
            ruleNameList.forEach(ruleName -> {
                MethodCallExpr addRuleName = new MethodCallExpr(infoExpr, "addRuleName", NodeList.nodeList(new StringLiteralExpr(ruleName), new StringLiteralExpr(ruleFileName)));
                addRuleNameMethods.add(addRuleName);
            });
        });
        addRuleNameMethods.sort((mce1, mce2) -> mce1.toString().compareTo(mce2.toString()));
        addRuleNameMethods.forEach(block::addStatement);

        block.addStatement(new ReturnStmt(infoExpr));

        methodDeclaration.setBody(block);
    }

    @Override
    protected ClassOrInterfaceType functionType() {
        String type = "Predicate" + lambdaParameters.size();
        return toClassOrInterfaceType("org.drools.model.functions." + type);
    }
}
