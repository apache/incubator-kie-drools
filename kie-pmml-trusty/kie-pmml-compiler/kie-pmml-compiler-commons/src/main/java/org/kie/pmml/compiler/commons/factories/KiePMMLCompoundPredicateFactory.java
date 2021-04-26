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
package org.kie.pmml.compiler.commons.factories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.Predicate;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getTypedClassOrInterfaceType;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;

public class KiePMMLCompoundPredicateFactory {

    static final String KIE_PMML_COMPOUND_PREDICATE_EVALUATE_METHOD_TEMPLATE_JAVA =
            "KiePMMLCompoundPredicateEvaluateMethodTemplate.tmpl";
    static final String KIE_PMML_COMPOUND_PREDICATE_EVALUATE_METHOD_TEMPLATE = "KiePMMLCompoundPredicateEvaluateMethodTemplate";

    public static final String EVALUATE_NESTED_PREDICATE = "evaluateNestedPredicate";
    public static final String NESTED_PREDICATE_FUNCTIONS = "functions";

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLCompoundPredicateFactory.class.getName());


    private KiePMMLCompoundPredicateFactory() {
    }

    public static BlockStmt getCompoundPredicateBody(final CompoundPredicate predicate,
                                                     final DataDictionary dataDictionary,
                                                     final List<MethodDeclaration> toPopulate,
                                                     final String rootNodeClassName,
                                                     final String nodeClassName,
                                                     final AtomicInteger counter) {
        final List<String> methodNames = new ArrayList<>();
        for (Predicate nestedPredicate : predicate.getPredicates()) {
            final BlockStmt nestedPredicateBody = KiePMMLPredicateFactory.getPredicateBody(nestedPredicate, dataDictionary, toPopulate, rootNodeClassName, nodeClassName, counter);
            final MethodDeclaration toAdd = new MethodDeclaration();
            toAdd.setModifiers(Modifier.Keyword.PRIVATE, Modifier.Keyword.STATIC);
            toAdd.setType("boolean");
            String nestedMethodName = EVALUATE_NESTED_PREDICATE +  nodeClassName + counter.addAndGet(1);
            toAdd.setName(new SimpleName(nestedMethodName));
            methodNames.add(nestedMethodName);
            Parameter parameter = new Parameter();
            parameter.setName(new SimpleName("stringObjectMap"));
            parameter.setType(getTypedClassOrInterfaceType(Map.class.getName(),
                                                           Arrays.asList("String", "Object")));
            toAdd.setParameters(NodeList.nodeList(parameter));
            toAdd.setBody(nestedPredicateBody);
            toPopulate.add(toAdd);
        }
        CompilationUnit templateEvaluate =
                getFromFileName(KIE_PMML_COMPOUND_PREDICATE_EVALUATE_METHOD_TEMPLATE_JAVA);
        CompilationUnit cloneEvaluate = templateEvaluate.clone();
        ClassOrInterfaceDeclaration evaluateTemplateClass =
                cloneEvaluate.getClassByName(KIE_PMML_COMPOUND_PREDICATE_EVALUATE_METHOD_TEMPLATE)
                        .orElseThrow(() -> new RuntimeException(MAIN_CLASS_NOT_FOUND));
        final BlockStmt toReturn = evaluateTemplateClass
                .getMethodsByName("evaluate").get(0)
                .getBody()
                .orElseThrow(() -> new KiePMMLInternalException("Failed to find body for \"evaluate\""));
        final NodeList<Expression> functionsExpressions = new NodeList<>();
        for (String nestedMethodName : methodNames) {
            MethodReferenceExpr toAdd = new MethodReferenceExpr();
            toAdd.setScope(new NameExpr(rootNodeClassName));
            toAdd.setIdentifier(nestedMethodName);
            functionsExpressions.add(toAdd);
        }
        MethodCallExpr valuesInit = new MethodCallExpr();
        valuesInit.setScope(new TypeExpr(parseClassOrInterfaceType(Arrays.class.getName())));
        valuesInit.setName("asList");
        valuesInit.setArguments(functionsExpressions);
        CommonCodegenUtils.setVariableDeclaratorValue(toReturn, NESTED_PREDICATE_FUNCTIONS, valuesInit);
        final Expression returnExpression = getReturnExpression(evaluateTemplateClass, predicate.getBooleanOperator());
        CommonCodegenUtils.setAssignExpressionValue(toReturn, "toReturn", returnExpression);
        return toReturn;
    }

    private static Expression getReturnExpression(final ClassOrInterfaceDeclaration evaluateTemplateClass, final CompoundPredicate.BooleanOperator booleanOperator) {
        String methodName = String.format("evaluate%s", booleanOperator.name());
        final BlockStmt blockStmt = evaluateTemplateClass
                .getMethodsByName(methodName).get(0)
                .getBody()
                .orElseThrow(() -> new KiePMMLInternalException("Failed to find body for " + methodName));
        final ReturnStmt returnStmt = blockStmt.stream()
                .filter(statement -> statement instanceof ReturnStmt)
                .findFirst()
                .map(ReturnStmt.class::cast)
                .orElseThrow(() -> new KiePMMLInternalException("Failed to find return statement for " + methodName));
        return returnStmt.getExpression().orElseThrow(() -> new KiePMMLInternalException("Failed to find return expression for " + methodName));
    }



}
