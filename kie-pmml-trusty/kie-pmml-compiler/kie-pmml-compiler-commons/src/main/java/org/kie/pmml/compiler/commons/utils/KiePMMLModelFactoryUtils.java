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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import org.dmg.pmml.LocalTransformations;
import org.dmg.pmml.TransformationDictionary;
import org.kie.pmml.commons.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.enums.RESULT_FEATURE;

import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.addMapPopulation;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.populateMethodDeclarations;
import static org.kie.pmml.compiler.commons.utils.DefineFunctionUtils.getDefineFunctionsMethodMap;
import static org.kie.pmml.compiler.commons.utils.DerivedFieldFunctionUtils.getDerivedFieldsMethodMap;

/**
 * Class to provide shared, helper methods to be invoked by model-specific
 * <b>factories</b> (e.g. KiePMMLTreeModelFactory, KiePMMLScorecardModelFactory, KiePMMLRegressionModelFactory)
 */
public class KiePMMLModelFactoryUtils {

    private KiePMMLModelFactoryUtils() {
        // Avoid instantiation
    }

    /**
     * Set the <b>name</b> parameter on <b>super</b> invocation
     * @param generatedClassName
     * @param constructorDeclaration
     * @param name
     */
    public static void setConstructorSuperNameInvocation(final String generatedClassName,
                                      final ConstructorDeclaration constructorDeclaration,
                                      final String name) {
        constructorDeclaration.setName(generatedClassName);
        final BlockStmt body = constructorDeclaration.getBody();
        body.getStatements().iterator().forEachRemaining(statement -> {
            if (statement instanceof ExplicitConstructorInvocationStmt) {
                ExplicitConstructorInvocationStmt superStatement = (ExplicitConstructorInvocationStmt) statement;
                NameExpr modelNameExpr = (NameExpr) superStatement.getArgument(0);
                modelNameExpr.setName(String.format("\"%s\"", name));
            }
        });
    }

    /**
     * Populate the <b>outputFields</b> <code>List&lt;KiePMMLOutputField&gt;</code>
     * @param body
     * @param outputFields
     */
    public static void addOutputFieldsPopulation(final BlockStmt body, final List<KiePMMLOutputField> outputFields) {
        for (KiePMMLOutputField outputField : outputFields) {
            NodeList<Expression> expressions = NodeList.nodeList(new StringLiteralExpr(outputField.getName()), new NameExpr("Collections.emptyList()"));
            MethodCallExpr builder = new MethodCallExpr(new NameExpr("KiePMMLOutputField"), "builder", expressions);
            if (outputField.getRank() != null) {
                expressions = NodeList.nodeList(new IntegerLiteralExpr(outputField.getRank()));
                builder = new MethodCallExpr(builder, "withRank", expressions);
            }
            if (outputField.getValue() != null) {
                expressions = NodeList.nodeList(new StringLiteralExpr(outputField.getValue().toString()));
                builder = new MethodCallExpr(builder, "withValue", expressions);
            }
            String targetField = outputField.getTargetField().orElse(null);
            if (targetField != null) {
                expressions = NodeList.nodeList(new StringLiteralExpr(targetField));
                builder = new MethodCallExpr(builder, "withTargetField", expressions);
            }
            if (outputField.getResultFeature() != null) {
                expressions = NodeList.nodeList(new NameExpr(RESULT_FEATURE.class.getName() + "." + outputField.getResultFeature().toString()));
                builder = new MethodCallExpr(builder, "withResultFeature", expressions);
            }
            Expression newOutputField = new MethodCallExpr(builder, "build");
            expressions = NodeList.nodeList(newOutputField);
            body.addStatement(new MethodCallExpr(new NameExpr("outputFields"), "add", expressions));
        }
    }

    /**
     * Add <b>common</b> and <b>local</b> local transformations management inside the given <code>ClassOrInterfaceDeclaration</code>
     * @param toPopulate
     * @param transformationDictionary
     * @param localTransformations
     */
    public static void addTransformationsInClassOrInterfaceDeclaration(final ClassOrInterfaceDeclaration toPopulate, final TransformationDictionary transformationDictionary, final LocalTransformations localTransformations) {
        final AtomicInteger arityCounter = new AtomicInteger(0);
        final Map<String, MethodDeclaration> commonDerivedFieldsMethodMap = (transformationDictionary != null && transformationDictionary.getDerivedFields() != null) ? getDerivedFieldsMethodMap(transformationDictionary.getDerivedFields(), arityCounter) : Collections.emptyMap();
        final Map<String, MethodDeclaration> localDerivedFieldsMethodMap = (localTransformations != null && localTransformations.getDerivedFields() != null) ? getDerivedFieldsMethodMap(localTransformations.getDerivedFields(), arityCounter) : Collections.emptyMap();
        final Map<String, MethodDeclaration> defineFunctionsMethodMap = (transformationDictionary != null && transformationDictionary.getDefineFunctions() != null) ? getDefineFunctionsMethodMap(transformationDictionary.getDefineFunctions()) : Collections.emptyMap();
        populateMethodDeclarations(toPopulate, commonDerivedFieldsMethodMap.values());
        populateMethodDeclarations(toPopulate, localDerivedFieldsMethodMap.values());
        populateMethodDeclarations(toPopulate, defineFunctionsMethodMap.values());
        final ConstructorDeclaration constructorDeclaration = toPopulate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, toPopulate.getName())));
        populateTransformationsInConstructor(constructorDeclaration, commonDerivedFieldsMethodMap, localDerivedFieldsMethodMap);
        //
    }

    /**
     * Populating the <b>commonTransformationsMap</b> and <b>localTransformationsMap</b> <code>Map&lt;String, Function&lt;List&lt;KiePMMLNameValue&gt;, Object&gt;&gt;</code>>s inside the constructor
     * @param constructorDeclaration
     * @param commonDerivedFieldsMethodMap
     * @param localDerivedFieldsMethodMap
     */
    static void populateTransformationsInConstructor(final ConstructorDeclaration constructorDeclaration, final Map<String, MethodDeclaration> commonDerivedFieldsMethodMap, final Map<String, MethodDeclaration> localDerivedFieldsMethodMap) {
        addMapPopulation(commonDerivedFieldsMethodMap, constructorDeclaration.getBody(), "commonTransformationsMap");
        addMapPopulation(localDerivedFieldsMethodMap, constructorDeclaration.getBody(), "localTransformationsMap");
    }
}
