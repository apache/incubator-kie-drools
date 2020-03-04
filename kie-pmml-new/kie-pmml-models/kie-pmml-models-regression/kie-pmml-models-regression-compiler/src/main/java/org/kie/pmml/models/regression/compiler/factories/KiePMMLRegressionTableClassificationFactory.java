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
package org.kie.pmml.models.regression.compiler.factories;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.dmg.pmml.regression.RegressionModel;
import org.dmg.pmml.regression.RegressionTable;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.models.regression.model.tuples.KiePMMLTableSourceCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionTableRegressionFactory.addMethod;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionTableRegressionFactory.populateGetTargetCategory;

public class KiePMMLRegressionTableClassificationFactory {

    public static final String KIE_PMML_REGRESSION_TABLE_CLASSIFICATION_TEMPLATE_JAVA = "KiePMMLRegressionTableClassificationTemplate.tmpl";
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLRegressionTableClassificationFactory.class.getName());
    private static final String MAIN_CLASS_NOT_FOUND = "Main class not found";
    private static final String KIE_PMML_GET_PROBABILITY_MAP_METHOD_TEMPLATE_JAVA = "KiePMMLGetProbabilityMapMethodTemplate.tmpl";
    private static final String KIE_PMML_GET_PROBABILITY_MAP_METHOD_TEMPLATE = "KiePMMLGetProbabilityMapMethodTemplate";
    private static final String KIE_PMML_REGRESSION_TABLE_CLASSIFICATION_TEMPLATE = "KiePMMLRegressionTableClassificationTemplate";
    private static AtomicInteger classArity = new AtomicInteger(0);
    private static CompilationUnit templateEvaluate;
    private static CompilationUnit cloneEvaluate;

    private KiePMMLRegressionTableClassificationFactory() {
        // Avoid instantiation
    }

    public static Map<String, KiePMMLTableSourceCategory> getRegressionTables(final List<RegressionTable> regressionTables, final RegressionModel.NormalizationMethod normalizationMethod, final List<KiePMMLOutputField> outputFields, final String targetField) throws IOException {
        logger.debug("getRegressionTables {}", regressionTables);
        CompilationUnit templateCU = StaticJavaParser.parseResource(KIE_PMML_REGRESSION_TABLE_CLASSIFICATION_TEMPLATE_JAVA);
        Map<String, KiePMMLTableSourceCategory> toReturn = KiePMMLRegressionTableRegressionFactory.getRegressionTables(regressionTables, RegressionModel.NormalizationMethod.NONE, targetField);
        AbstractMap.Entry<String, String> regressionTableEntry = getRegressionTable(templateCU, toReturn, normalizationMethod, outputFields, targetField);
        toReturn.put(regressionTableEntry.getKey(), new KiePMMLTableSourceCategory(regressionTableEntry.getValue(), ""));
        return toReturn;
    }

    public static AbstractMap.Entry<String, String> getRegressionTable(final CompilationUnit templateCU, final Map<String, KiePMMLTableSourceCategory> regressionTablesMap, final RegressionModel.NormalizationMethod normalizationMethod, final List<KiePMMLOutputField> outputFields, final String targetField) throws IOException {
        logger.debug("getRegressionTable {}", regressionTablesMap);
        CompilationUnit cloneCU = templateCU.clone();
        ClassOrInterfaceDeclaration tableTemplate = cloneCU.getClassByName(KIE_PMML_REGRESSION_TABLE_CLASSIFICATION_TEMPLATE)
                .orElseThrow(() -> new RuntimeException(MAIN_CLASS_NOT_FOUND));
        String className = "KiePMMLRegressionTableClassification" + classArity.addAndGet(1);
        tableTemplate.setName(className);
        populateGetProbabilityMapMethod(normalizationMethod, tableTemplate);
        populateOutputFieldsMap(tableTemplate, outputFields);
        tableTemplate.getDefaultConstructor().ifPresent(constructorDeclaration -> {
            setConstructor(constructorDeclaration, tableTemplate.getName(), targetField);
            addMapPopulation(constructorDeclaration.getBody(), regressionTablesMap);
        });
        populateGetTargetCategory(tableTemplate, null);
        return new AbstractMap.SimpleEntry<>(className, cloneCU.toString());
    }

    /**
     * Set the <b>targetField</b> values inside the constructor
     * @param constructorDeclaration
     * @param generatedClassName
     * @param targetField
     */
    private static void setConstructor(final ConstructorDeclaration constructorDeclaration, final SimpleName generatedClassName, final String targetField) {
        constructorDeclaration.setName(generatedClassName);
        final BlockStmt body = constructorDeclaration.getBody();
        final List<AssignExpr> assignExprs = body.findAll(AssignExpr.class);
        assignExprs.stream().filter(assignExpr -> assignExpr.getTarget().asNameExpr().getNameAsString().equals("targetField"))
                .forEach(assignExpr -> assignExpr.setValue(new StringLiteralExpr(targetField)));
    }

    /**
     * Add entries <b>category/KiePMMLRegressionTable</b> inside the constructor
     * @param body
     * @param regressionTablesMap
     */
    private static void addMapPopulation(final BlockStmt body, final Map<String, KiePMMLTableSourceCategory> regressionTablesMap) {
        regressionTablesMap.forEach((className, tableSourceCategory) -> {
            ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
            objectCreationExpr.setType(className);
            NodeList<Expression> expressions = NodeList.nodeList(new StringLiteralExpr(tableSourceCategory.getCategory()), objectCreationExpr);
            body.addStatement(new MethodCallExpr(new NameExpr("categoryTableMap"), "put", expressions));
        });
    }

    /**
     * Add entries <b>output field/output value</b> inside <b>populateOutputFieldsMap</b> method
     * @param tableTemplate
     * @param outputFields
     */
    private static void populateOutputFieldsMap(final ClassOrInterfaceDeclaration tableTemplate, final List<KiePMMLOutputField> outputFields) {
        final MethodDeclaration methodDeclaration = tableTemplate.getMethodsByName("populateOutputFieldsMap").get(0);
        methodDeclaration.getBody().ifPresent(body -> populateOutputFieldsMap(body, outputFields));
    }

    /**
     * Add entries <b>output field/output value</b> inside <b>populateOutputFieldsMap</b> method
     * @param body
     * @param outputFields
     */
    private static void populateOutputFieldsMap(final BlockStmt body, final List<KiePMMLOutputField> outputFields) {
        outputFields.forEach(outputField -> {
            StringLiteralExpr key = new StringLiteralExpr(outputField.getName());
            Expression value = null;
            switch (outputField.getResultFeature()) {
                case PREDICTED_VALUE:
                    value = new MethodCallExpr(new NameExpr("predictedEntry"), "getKey");
                    break;
                case PROBABILITY:
                    if (!outputField.getTargetField().isPresent()) {
                        NodeList<Expression> expressions = NodeList.nodeList(new StringLiteralExpr(outputField.getValue().toString()));
                        value = new MethodCallExpr(new NameExpr("probabilityMap"), "get", expressions);
                    } else {

                        NodeList<Expression> expressions = NodeList.nodeList(new StringLiteralExpr(outputField.getTargetField().get()));
                        value = new MethodCallExpr(new NameExpr("probabilityMap"), "get", expressions);
                    }
                    break;
                default:
                    // All other possibilities not analyzed, yet
            }
            if (value != null) {
                NodeList<Expression> expressions = NodeList.nodeList(key, value);
                body.addStatement(new MethodCallExpr(new NameExpr("outputFieldsMap"), "put", expressions));
            }
        });
    }

    /**
     * Add the  <b>getProbabilityMapMethod</b>s <code>MethodDeclaration</code> to the class
     * @param normalizationMethod
     * @param tableTemplate
     * @return
     */
    private static void populateGetProbabilityMapMethod(final RegressionModel.NormalizationMethod normalizationMethod, final ClassOrInterfaceDeclaration tableTemplate) {
        try {
            String methodName = String.format("get%sProbabilityMap", normalizationMethod.name());
            templateEvaluate = StaticJavaParser.parseResource(KIE_PMML_GET_PROBABILITY_MAP_METHOD_TEMPLATE_JAVA);
            cloneEvaluate = templateEvaluate.clone();
            ClassOrInterfaceDeclaration evaluateTemplateClass = cloneEvaluate.getClassByName(KIE_PMML_GET_PROBABILITY_MAP_METHOD_TEMPLATE)
                    .orElseThrow(() -> new RuntimeException(MAIN_CLASS_NOT_FOUND));
            final MethodDeclaration toReturn = evaluateTemplateClass.getMethodsByName(methodName).get(0);
            addMethod(toReturn, tableTemplate, "getProbabilityMap");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
