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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.dmg.pmml.OpType;
import org.dmg.pmml.regression.RegressionModel;
import org.dmg.pmml.regression.RegressionTable;
import org.kie.pmml.commons.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.commons.model.enums.OP_TYPE;
import org.kie.pmml.models.regression.model.enums.REGRESSION_NORMALIZATION_METHOD;
import org.kie.pmml.models.regression.model.tuples.KiePMMLTableSourceCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionTableRegressionFactory.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionTableRegressionFactory.addMethod;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionTableRegressionFactory.populateGetTargetCategory;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;

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

    public static Map<String, KiePMMLTableSourceCategory> getRegressionTables(final List<RegressionTable> regressionTables, final RegressionModel.NormalizationMethod normalizationMethod, final OpType opType, final List<KiePMMLOutputField> outputFields, final String targetField, final String packageName) throws IOException {
        logger.trace("getRegressionTables {}", regressionTables);
        CompilationUnit templateCU = getFromFileName(KIE_PMML_REGRESSION_TABLE_CLASSIFICATION_TEMPLATE_JAVA);
        Map<String, KiePMMLTableSourceCategory> toReturn = KiePMMLRegressionTableRegressionFactory.getRegressionTables(regressionTables, RegressionModel.NormalizationMethod.NONE, targetField, packageName);
        Map.Entry<String, String> regressionTableEntry = getRegressionTable(templateCU, toReturn, normalizationMethod, opType, outputFields, targetField, packageName);
        toReturn.put(regressionTableEntry.getKey(), new KiePMMLTableSourceCategory(regressionTableEntry.getValue(), ""));
        return toReturn;
    }

    public static Map.Entry<String, String> getRegressionTable(final CompilationUnit templateCU, final Map<String, KiePMMLTableSourceCategory> regressionTablesMap, final RegressionModel.NormalizationMethod normalizationMethod, final OpType opType, final List<KiePMMLOutputField> outputFields, final String targetField, final String packageName) throws IOException {
        logger.trace("getRegressionTable {}", regressionTablesMap);
        CompilationUnit cloneCU = templateCU.clone();
        cloneCU.setPackageDeclaration(packageName);
        final REGRESSION_NORMALIZATION_METHOD regressionNormalizationMethod = REGRESSION_NORMALIZATION_METHOD.byName(normalizationMethod.value());
        final OP_TYPE op_type = OP_TYPE.byName(opType.value());
        ClassOrInterfaceDeclaration tableTemplate = cloneCU.getClassByName(KIE_PMML_REGRESSION_TABLE_CLASSIFICATION_TEMPLATE)
                .orElseThrow(() -> new RuntimeException(MAIN_CLASS_NOT_FOUND));
        String className = "KiePMMLRegressionTableClassification" + classArity.addAndGet(1);
        tableTemplate.setName(className);
        populateGetProbabilityMapMethod(normalizationMethod, tableTemplate);
        populateOutputFieldsMap(tableTemplate, outputFields);
        populateIsBinaryMethod(opType, regressionTablesMap.size(), tableTemplate);
        final ConstructorDeclaration constructorDeclaration = tableTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format("Missing default constructor in ClassOrInterfaceDeclaration %s ", tableTemplate.getName())));
        setConstructor(constructorDeclaration, tableTemplate.getName(), targetField, regressionNormalizationMethod, op_type);
        addMapPopulation(constructorDeclaration.getBody(), regressionTablesMap);
        populateGetTargetCategory(tableTemplate, null);
        return new AbstractMap.SimpleEntry<>(className, cloneCU.toString());
    }

    /**
     * Set the <b>targetField</b> values inside the constructor
     * @param constructorDeclaration
     * @param generatedClassName
     * @param targetField
     */
    private static void setConstructor(final ConstructorDeclaration constructorDeclaration, final SimpleName generatedClassName, final String targetField, final REGRESSION_NORMALIZATION_METHOD regressionNormalizationMethod, final OP_TYPE opType) {
        constructorDeclaration.setName(generatedClassName);
        final BlockStmt body = constructorDeclaration.getBody();
        final List<AssignExpr> assignExprs = body.findAll(AssignExpr.class);
        assignExprs.forEach(assignExpr -> {
            final String propertyName = assignExpr.getTarget().asNameExpr().getNameAsString();
            switch (propertyName) {
                case "targetField":
                    assignExpr.setValue(new StringLiteralExpr(targetField));
                    break;
                case "regressionNormalizationMethod":
                    assignExpr.setValue(new NameExpr(regressionNormalizationMethod.getClass().getSimpleName() + "." + regressionNormalizationMethod.name()));
                    break;
                case "opType":
                    assignExpr.setValue(new NameExpr(opType.getClass().getSimpleName() + "." + opType.name()));
                    break;
                default:
                    logger.warn("Unexpected property inside the constructor: {}", propertyName);
            }
        });
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
        final BlockStmt body = methodDeclaration.getBody().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_BODY_TEMPLATE, methodDeclaration.getName())));
        populateOutputFieldsMap(body, outputFields);
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
                    if (outputField.getValue() != null) {
                        NodeList<Expression> expressions = NodeList.nodeList(new StringLiteralExpr(outputField.getValue().toString()));
                        value = new MethodCallExpr(new NameExpr("probabilityMap"), "get", expressions);
                    } else if (outputField.getTargetField().isPresent()) {
                        NodeList<Expression> expressions = NodeList.nodeList(new StringLiteralExpr(outputField.getTargetField().get()));
                        value = new MethodCallExpr(new NameExpr("probabilityMap"), "get", expressions);
                    }
                    break;
                default:
                    // All other possibilities not managed, yet
                    throw new KiePMMLInternalException(String.format("%s not managed, yet!", outputField.getResultFeature()));
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
            templateEvaluate = getFromFileName(KIE_PMML_GET_PROBABILITY_MAP_METHOD_TEMPLATE_JAVA);
            cloneEvaluate = templateEvaluate.clone();
            ClassOrInterfaceDeclaration evaluateTemplateClass = cloneEvaluate.getClassByName(KIE_PMML_GET_PROBABILITY_MAP_METHOD_TEMPLATE)
                    .orElseThrow(() -> new RuntimeException(MAIN_CLASS_NOT_FOUND));
            final MethodDeclaration toReturn = evaluateTemplateClass.getMethodsByName(methodName).get(0);
            addMethod(toReturn, tableTemplate, "getProbabilityMap");
        } catch (Exception e) {
            throw new KiePMMLInternalException(e.getMessage());
        }
    }

    /**
     * Populate the <b>isBinary</b> <code>MethodDeclaration</code> of the class
     * @param opType
     * @param size
     * @param tableTemplate
     * @return
     */
    private static void populateIsBinaryMethod(final OpType opType, int size, final ClassOrInterfaceDeclaration tableTemplate) {
        try {
            final MethodDeclaration methodDeclaration = tableTemplate.getMethodsByName("isBinary").get(0);
            boolean toReturn = Objects.equals(OpType.CATEGORICAL, opType) && size == 2;
            BlockStmt blockStmt = new BlockStmt();
            blockStmt.addStatement(new ReturnStmt(new BooleanLiteralExpr(toReturn)));
            methodDeclaration.setBody(blockStmt);
        } catch (Exception e) {
            throw new KiePMMLInternalException(e.getMessage());
        }
    }
}
