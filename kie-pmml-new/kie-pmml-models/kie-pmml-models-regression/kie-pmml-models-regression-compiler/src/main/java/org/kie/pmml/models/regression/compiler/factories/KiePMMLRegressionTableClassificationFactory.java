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
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import org.dmg.pmml.regression.RegressionTable;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.models.regression.model.enums.REGRESSION_NORMALIZATION_METHOD;
import org.kie.pmml.models.regression.model.tuples.KiePMMLTableSourceCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionTableRegressionFactory.addMethod;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionTableRegressionFactory.populateGetTargetCategory;
import static org.kie.pmml.models.regression.model.enums.REGRESSION_NORMALIZATION_METHOD.CAUCHIT;
import static org.kie.pmml.models.regression.model.enums.REGRESSION_NORMALIZATION_METHOD.CLOGLOG;
import static org.kie.pmml.models.regression.model.enums.REGRESSION_NORMALIZATION_METHOD.LOGIT;
import static org.kie.pmml.models.regression.model.enums.REGRESSION_NORMALIZATION_METHOD.NONE;
import static org.kie.pmml.models.regression.model.enums.REGRESSION_NORMALIZATION_METHOD.PROBIT;
import static org.kie.pmml.models.regression.model.enums.REGRESSION_NORMALIZATION_METHOD.SIMPLEMAX;
import static org.kie.pmml.models.regression.model.enums.REGRESSION_NORMALIZATION_METHOD.SOFTMAX;

public class KiePMMLRegressionTableClassificationFactory {

    public static final String KIE_PMML_REGRESSION_TABLE_CLASSIFICATION_TEMPLATE_JAVA = "KiePMMLRegressionTableClassificationTemplate.tmpl";
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLRegressionTableClassificationFactory.class.getName());
    private static final String MAIN_CLASS_NOT_FOUND = "Main class not found";
    private static final String KIE_PMML_GET_PROBABILITY_MAP_METHOD_TEMPLATE_JAVA = "KiePMMLGetProbabilityMapMethodTemplate.tmpl";
    private static final String KIE_PMML_GET_PROBABILITY_MAP_METHOD_TEMPLATE = "KiePMMLGetProbabilityMapMethodTemplate";
    private static final String KIE_PMML_REGRESSION_TABLE_CLASSIFICATION_TEMPLATE = "KiePMMLRegressionTableClassificationTemplate";
    private static final Map<REGRESSION_NORMALIZATION_METHOD, String> NORM_METHOD_TEMPLATE_MAP = Stream.of(
            new AbstractMap.SimpleImmutableEntry<>(SOFTMAX, "getSOFTMAXProbabilityMap"),
            new AbstractMap.SimpleImmutableEntry<>(SIMPLEMAX, "getSIMPLEMAXProbabilityMap"),
            new AbstractMap.SimpleImmutableEntry<>(NONE, "getNONEProbabilityMap"),
            new AbstractMap.SimpleImmutableEntry<>(LOGIT, "getLOGITProbabilityMap"),
            new AbstractMap.SimpleImmutableEntry<>(PROBIT, "getPROBITProbabilityMap"),
            new AbstractMap.SimpleImmutableEntry<>(CLOGLOG, "getCLOGLOGProbabilityMap"),
            new AbstractMap.SimpleImmutableEntry<>(CAUCHIT, "getCAUCHITProbabilityMap"))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    private static AtomicInteger classArity = new AtomicInteger(0);
    private static CompilationUnit templateEvaluate;
    private static CompilationUnit cloneEvaluate;

    private KiePMMLRegressionTableClassificationFactory() {
        // Avoid instantiation
    }

    public static Map<String, KiePMMLTableSourceCategory> getRegressionTables(final List<RegressionTable> regressionTables, final REGRESSION_NORMALIZATION_METHOD regressionNormalizationMethod, final List<KiePMMLOutputField> outputFields, final String targetField) throws IOException {
        logger.debug("getRegressionTables {}", regressionTables);
        CompilationUnit templateCU = StaticJavaParser.parseResource(KIE_PMML_REGRESSION_TABLE_CLASSIFICATION_TEMPLATE_JAVA);
        Map<String, KiePMMLTableSourceCategory> toReturn = KiePMMLRegressionTableRegressionFactory.getRegressionTables(regressionTables, targetField);
        AbstractMap.Entry<String, String> regressionTableEntry = getRegressionTable(templateCU, toReturn, regressionNormalizationMethod, outputFields, targetField);
        toReturn.put(regressionTableEntry.getKey(), new KiePMMLTableSourceCategory(regressionTableEntry.getValue(), ""));
        return toReturn;
    }

    public static AbstractMap.Entry<String, String> getRegressionTable(final CompilationUnit templateCU, final Map<String, KiePMMLTableSourceCategory> regressionTablesMap, final REGRESSION_NORMALIZATION_METHOD regressionNormalizationMethod, final List<KiePMMLOutputField> outputFields, final String targetField) throws IOException {
        logger.debug("getRegressionTable {}", regressionTablesMap);
        CompilationUnit cloneCU = templateCU.clone();
        ClassOrInterfaceDeclaration tableTemplate = cloneCU.getClassByName(KIE_PMML_REGRESSION_TABLE_CLASSIFICATION_TEMPLATE)
                .orElseThrow(() -> new RuntimeException(MAIN_CLASS_NOT_FOUND));
        String className = "KiePMMLRegressionTableClassification" + classArity.addAndGet(1);
        tableTemplate.setName(className);
        populateGetProbabilityMapMethod(regressionNormalizationMethod, tableTemplate);
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
     * Add entries <b>category/AbstractKiePMMLRegressionTable</b> inside the constructor
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
                        value = new MethodCallExpr(new NameExpr("predictedEntry"), "getValue");
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
     * @param regressionNormalizationMethod
     * @param tableTemplate
     * @return
     */
    private static void populateGetProbabilityMapMethod(final REGRESSION_NORMALIZATION_METHOD regressionNormalizationMethod, final ClassOrInterfaceDeclaration tableTemplate) {
        try {
            String methodName = NORM_METHOD_TEMPLATE_MAP.get(regressionNormalizationMethod);
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

//    /**
//     * Add a <code>MethodDeclaration</code> to the class
//     * @param methodTemplate
//     * @param tableTemplate
//     * @param evaluateMethodName
//     * @return
//     */
//    private static void populateMethod(final MethodDeclaration methodTemplate, final ClassOrInterfaceDeclaration tableTemplate, final String evaluateMethodName) {
//        methodTemplate.getBody().ifPresent(body -> {
//            final MethodDeclaration toReturn = tableTemplate.addMethod(evaluateMethodName).setBody(body);
//            toReturn.setModifiers(methodTemplate.getModifiers());
//            methodTemplate.getParameters().forEach(toReturn::addParameter);
//            toReturn.setType(methodTemplate.getType());
//        });
//    }

//    /**
//     * Add a <code>MethodDeclaration</code> to the class
//     * @param tableTemplate
//     * @param targetCategory
//     * @return
//     */
//    private static void populateGetTargetCategory(final ClassOrInterfaceDeclaration tableTemplate, final Object targetCategory) {
//        MethodDeclaration methodDeclaration = tableTemplate.getMethodsByName("getTargetCategory").get(0);
//        methodDeclaration.getBody().ifPresent(body -> {
//            ReturnStmt returnStmt = new ReturnStmt();
//            if (targetCategory == null) {
//                returnStmt.setExpression(new NameExpr("null"));
//            } else if (targetCategory instanceof String) {
//                returnStmt.setExpression(new StringLiteralExpr((String) targetCategory));
//            } else {
//                returnStmt.setExpression(new NameExpr(targetCategory.toString()));
//            }
//            body.addStatement(returnStmt);
//        });
//    }
}
