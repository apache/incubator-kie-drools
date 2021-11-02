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

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.UnknownType;
import org.dmg.pmml.regression.CategoricalPredictor;
import org.dmg.pmml.regression.NumericPredictor;
import org.dmg.pmml.regression.PredictorTerm;
import org.dmg.pmml.regression.RegressionModel;
import org.dmg.pmml.regression.RegressionTable;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.api.iinterfaces.SerializableFunction;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.regression.compiler.dto.RegressionCompilationDTO;
import org.kie.pmml.models.regression.model.tuples.KiePMMLTableSourceCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.groupingBy;
import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedVariableName;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.addMapPopulation;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.addMapPopulationExpressions;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.addMethod;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForObject;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getTypedClassOrInterfaceTypeByTypeNames;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFullClassName;

public class KiePMMLRegressionTableRegressionFactory {

    public static final String KIE_PMML_REGRESSION_TABLE_REGRESSION_TEMPLATE_JAVA =
            "KiePMMLRegressionTableRegressionTemplate.tmpl";
    public static final String KIE_PMML_REGRESSION_TABLE_REGRESSION_TEMPLATE =
            "KiePMMLRegressionTableRegressionTemplate";
    private static final Logger logger =
            LoggerFactory.getLogger(KiePMMLRegressionTableRegressionFactory.class.getName());
    static final String MAIN_CLASS_NOT_FOUND = "Main class not found";
    static final String KIE_PMML_EVALUATE_METHOD_TEMPLATE_JAVA = "KiePMMLEvaluateMethodTemplate.tmpl";
    static final String KIE_PMML_EVALUATE_METHOD_TEMPLATE = "KiePMMLEvaluateMethodTemplate";
    static final List<RegressionModel.NormalizationMethod> SUPPORTED_NORMALIZATION_METHODS =
            Arrays.asList(RegressionModel.NormalizationMethod.SOFTMAX,
                          RegressionModel.NormalizationMethod.LOGIT,
                          RegressionModel.NormalizationMethod.EXP,
                          RegressionModel.NormalizationMethod.PROBIT,
                          RegressionModel.NormalizationMethod.CLOGLOG,
                          RegressionModel.NormalizationMethod.CAUCHIT,
                          RegressionModel.NormalizationMethod.NONE);
    static final List<RegressionModel.NormalizationMethod> UNSUPPORTED_NORMALIZATION_METHODS =
            Arrays.asList(
                    RegressionModel.NormalizationMethod.SIMPLEMAX,
                    RegressionModel.NormalizationMethod.LOGLOG);
    private static final String COEFFICIENT = "coefficient";
    private static AtomicInteger classArity = new AtomicInteger(0);
    private static AtomicInteger predictorsArity = new AtomicInteger(0);
    private static CompilationUnit templateEvaluate;
    private static CompilationUnit cloneEvaluate;

    private KiePMMLRegressionTableRegressionFactory() {
        // Avoid instantiation
    }

    /**
     * @param compilationDTO
     * @return Explicitly returning a <code>LinkedHashMap</code> because insertion order matters
     */
    public static LinkedHashMap<String, KiePMMLTableSourceCategory> getRegressionTables(final RegressionCompilationDTO compilationDTO) {
        logger.trace("getRegressionTables {}", compilationDTO.getRegressionTables());
        LinkedHashMap<String, KiePMMLTableSourceCategory> toReturn = new LinkedHashMap<>();
        for (RegressionTable regressionTable : compilationDTO.getRegressionTables()) {
            final Map.Entry<String, String> regressionTableEntry = getRegressionTable(regressionTable,
                                                                                      compilationDTO);
            String targetCategory = regressionTable.getTargetCategory() != null ?
                    regressionTable.getTargetCategory().toString() : "";
            toReturn.put(regressionTableEntry.getKey(),
                         new KiePMMLTableSourceCategory(regressionTableEntry.getValue(), targetCategory));
        }
        return toReturn;
    }

    public static Map.Entry<String, String> getRegressionTable(final RegressionTable regressionTable,
                                                               final RegressionCompilationDTO compilationDTO) {
        logger.trace("getRegressionTable {}", regressionTable);
        String className = "KiePMMLRegressionTableRegression" + classArity.addAndGet(1);
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className,
                                                                                 compilationDTO.getPackageName(),
                                                                                 KIE_PMML_REGRESSION_TABLE_REGRESSION_TEMPLATE_JAVA, KIE_PMML_REGRESSION_TABLE_REGRESSION_TEMPLATE);
        ClassOrInterfaceDeclaration tableTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        final ConstructorDeclaration constructorDeclaration =
                tableTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, tableTemplate.getName())));
        setConstructor(regressionTable,
                       constructorDeclaration,
                       tableTemplate.getName(),
                       compilationDTO.getTargetFieldName(),
                       regressionTable.getTargetCategory(),
                       compilationDTO.getDefaultNormalizationMethod());
        final Map<String, Expression> numericPredictorsMap =
                createNumericPredictorsExpressions(regressionTable.getNumericPredictors());
        final Map<String, MethodDeclaration> predictorTermsMap =
                addPredictorTerms(regressionTable.getPredictorTerms(), tableTemplate);
        final BlockStmt body = constructorDeclaration.getBody();
        final Map<String, Expression> categoricalPredictorsMap =
                createCategoricalPredictorsExpressions(regressionTable.getCategoricalPredictors(), body);
        addMapPopulationExpressions(numericPredictorsMap, body, "numericFunctionMap");
        addMapPopulationExpressions(categoricalPredictorsMap, body, "categoricalFunctionMap");
        addMapPopulation(predictorTermsMap, body, "predictorTermsFunctionMap");
        return new AbstractMap.SimpleEntry<>(getFullClassName(cloneCU), cloneCU.toString());
    }

    /**
     * Set the <b>intercept</b> and <b>targetField</b> values inside the constructor
     * @param regressionTable
     * @param constructorDeclaration
     * @param tableName
     * @param targetField
     * @param targetCategory
     * @param normalizationMethod
     */
    static void setConstructor(final RegressionTable regressionTable,
                               final ConstructorDeclaration constructorDeclaration,
                               final SimpleName tableName,
                               final String targetField,
                               final Object targetCategory,
                               final RegressionModel.NormalizationMethod normalizationMethod) {
        constructorDeclaration.setName(tableName);
        final BlockStmt body = constructorDeclaration.getBody();
        CommonCodegenUtils.setAssignExpressionValue(body, "intercept",
                                                    new DoubleLiteralExpr(String.valueOf(regressionTable.getIntercept().doubleValue())));
        CommonCodegenUtils.setAssignExpressionValue(body, "targetField", new StringLiteralExpr(targetField));
        final Expression targetCategoryExpression = getExpressionForObject(targetCategory);
        CommonCodegenUtils.setAssignExpressionValue(body, "targetCategory", targetCategoryExpression);
        final Expression resultUpdaterExpression = createResultUpdaterExpression(normalizationMethod);
        CommonCodegenUtils.setAssignExpressionValue(body, "resultUpdater", resultUpdaterExpression);
    }

    static Expression createResultUpdaterExpression(final RegressionModel.NormalizationMethod normalizationMethod) {
        if (UNSUPPORTED_NORMALIZATION_METHODS.contains(normalizationMethod)) {
            return new NullLiteralExpr();
        } else {
            return createResultUpdaterSupportedExpression(normalizationMethod);
        }
    }

    /**
     * Create a <b>resultUpdater</b> <code>CastExpr</code>
     * @param normalizationMethod
     * @return
     */
    static MethodReferenceExpr createResultUpdaterSupportedExpression(final RegressionModel.NormalizationMethod normalizationMethod) {
        final String thisExpressionMethodName = String.format("update%sResult", normalizationMethod.name());
        final CastExpr castExpr = new CastExpr();
        final String doubleClassName = Double.class.getSimpleName();
        final ClassOrInterfaceType consumerType =
                getTypedClassOrInterfaceTypeByTypeNames(SerializableFunction.class.getCanonicalName(),
                                                        Arrays.asList(doubleClassName, doubleClassName));
        castExpr.setType(consumerType);
        castExpr.setExpression(new ThisExpr());
        final MethodReferenceExpr toReturn = new MethodReferenceExpr();
        toReturn.setScope(castExpr);
        toReturn.setIdentifier(thisExpressionMethodName);
        return toReturn;
    }

    /**
     * Create <b>NumericPredictor</b>s <code>CastExpr</code>es
     * @param numericPredictors
     * @return
     */
    static Map<String, Expression> createNumericPredictorsExpressions(final List<NumericPredictor> numericPredictors) {
        return numericPredictors.stream()
                .collect(Collectors.toMap(numericPredictor -> numericPredictor.getName().getValue(),
                                          KiePMMLRegressionTableRegressionFactory::createNumericPredictorExpression));
    }

    /**
     * Create a <b>NumericPredictor</b> <code>CastExpr</code>
     * @param numericPredictor
     * @return
     */
    static CastExpr createNumericPredictorExpression(final NumericPredictor numericPredictor) {
        boolean withExponent = !Objects.equals(1, numericPredictor.getExponent());
        final String lambdaExpressionMethodName = withExponent ? "evaluateNumericWithExponent" :
                "evaluateNumericWithoutExponent";
        final String parameterName = "input";
        final MethodCallExpr lambdaMethodCallExpr = new MethodCallExpr();
        lambdaMethodCallExpr.setName(lambdaExpressionMethodName);
        final NodeList<Expression> arguments = new NodeList<>();
        arguments.add(0, new NameExpr(parameterName));
        arguments.add(1, getExpressionForObject(numericPredictor.getCoefficient().doubleValue()));
        if (withExponent) {
            arguments.add(2, getExpressionForObject(numericPredictor.getExponent().doubleValue()));
        }
        lambdaMethodCallExpr.setArguments(arguments);
        final ExpressionStmt lambdaExpressionStmt = new ExpressionStmt(lambdaMethodCallExpr);
        final LambdaExpr lambdaExpr = new LambdaExpr();
        final Parameter lambdaParameter = new Parameter(new UnknownType(), parameterName);
        lambdaExpr.setParameters(NodeList.nodeList(lambdaParameter));
        lambdaExpr.setBody(lambdaExpressionStmt);
        final String doubleClassName = Double.class.getSimpleName();
        final ClassOrInterfaceType serializableFunctionType =
                getTypedClassOrInterfaceTypeByTypeNames(SerializableFunction.class.getCanonicalName(),
                                                        Arrays.asList(doubleClassName, doubleClassName));
        final CastExpr toReturn = new CastExpr();
        toReturn.setType(serializableFunctionType);
        toReturn.setExpression(lambdaExpr);
        return toReturn;
    }

    /**
     * Create the <b>CategoricalPredictor</b>s lambda <code>Expression</code>s map
     * @param categoricalPredictors
     * @param body
     * @return
     */
    static Map<String, Expression> createCategoricalPredictorsExpressions(final List<CategoricalPredictor> categoricalPredictors, final BlockStmt body) {
        final Map<String, List<CategoricalPredictor>> groupedCollectors = categoricalPredictors.stream()
                .collect(groupingBy(categoricalPredictor -> categoricalPredictor.getField().getValue()));
        return groupedCollectors.entrySet().stream()
                .map(entry -> {
                    final String categoricalPredictorMapName = getSanitizedVariableName(String.format("%sMap",
                                                                                                      entry.getKey()));
                    populateWithGroupedCategoricalPredictorMap(entry.getValue(), body, categoricalPredictorMapName);
                    return new AbstractMap.SimpleEntry<>(entry.getKey(),
                                                         createCategoricalPredictorExpression(categoricalPredictorMapName));
                })
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey,
                                          AbstractMap.SimpleEntry::getValue));
    }

    /**
     * Populate the given <b>body</b> with the creation of a <code>Map</code> for the given <b>categoricalPredictors</b>
     * @param categoricalPredictors
     * @param toPopulate
     * @param categoricalPredictorMapName
     * @return
     */
    static void populateWithGroupedCategoricalPredictorMap(final List<CategoricalPredictor> categoricalPredictors,
                                                           final BlockStmt toPopulate,
                                                           final String categoricalPredictorMapName) {

        final VariableDeclarator categoricalMapDeclarator =
                new VariableDeclarator(getTypedClassOrInterfaceTypeByTypeNames(Map.class.getName(),
                                                                               Arrays.asList(String.class.getSimpleName(),
                                                                                             Double.class.getSimpleName())),
                                       categoricalPredictorMapName);
        final ObjectCreationExpr categoricalMapInitializer = new ObjectCreationExpr();
        categoricalMapInitializer.setType(getTypedClassOrInterfaceTypeByTypeNames(HashMap.class.getName(),
                                                                                  Arrays.asList(String.class.getSimpleName(),
                                                                                                Double.class.getSimpleName())));
        categoricalMapDeclarator.setInitializer(categoricalMapInitializer);
        final VariableDeclarationExpr categoricalMapDeclarationExpr =
                new VariableDeclarationExpr(categoricalMapDeclarator);
        toPopulate.addStatement(categoricalMapDeclarationExpr);
        final Map<String, Expression> mapExpressions = new LinkedHashMap<>();
        categoricalPredictors.forEach(categoricalPredictor -> mapExpressions.put(categoricalPredictor.getValue().toString(),
                                                                                 getExpressionForObject(categoricalPredictor.getCoefficient().doubleValue())));
        addMapPopulationExpressions(mapExpressions, toPopulate, categoricalPredictorMapName);
    }

    /**
     * Create <b>CategoricalPredictor</b> <code>CastExpr</code> to the class
     * @param categoricalPredictorMapName
     * @return
     */
    static CastExpr createCategoricalPredictorExpression(final String categoricalPredictorMapName) {
        final String lambdaExpressionMethodName = "evaluateCategoricalPredictor";
        final String parameterName = "input";
        final MethodCallExpr lambdaMethodCallExpr = new MethodCallExpr();
        lambdaMethodCallExpr.setName(lambdaExpressionMethodName);
        final NodeList<Expression> arguments = new NodeList<>();
        arguments.add(0, new NameExpr(parameterName));
        arguments.add(1, new NameExpr(categoricalPredictorMapName));
        lambdaMethodCallExpr.setArguments(arguments);
        final ExpressionStmt lambdaExpressionStmt = new ExpressionStmt(lambdaMethodCallExpr);
        final LambdaExpr lambdaExpr = new LambdaExpr();
        final Parameter lambdaParameter = new Parameter(new UnknownType(), parameterName);
        lambdaExpr.setParameters(NodeList.nodeList(lambdaParameter));
        lambdaExpr.setBody(lambdaExpressionStmt);
        final ClassOrInterfaceType serializableFunctionType =
                getTypedClassOrInterfaceTypeByTypeNames(SerializableFunction.class.getCanonicalName(),
                                                        Arrays.asList(String.class.getSimpleName(),
                                                                      Double.class.getSimpleName()));
        final CastExpr toReturn = new CastExpr();
        toReturn.setType(serializableFunctionType);
        toReturn.setExpression(lambdaExpr);
        return toReturn;
    }

    /**
     * Add <b>PredictorTerm</b>s <code>MethodDeclaration</code> to the class
     * @param predictorTerms
     * @param tableTemplate
     * @return
     */
    static Map<String, MethodDeclaration> addPredictorTerms(final List<PredictorTerm> predictorTerms,
                                                            final ClassOrInterfaceDeclaration tableTemplate) {
        predictorsArity.set(0);
        return predictorTerms.stream()
                .map(predictorTerm -> {
                    int arity = predictorsArity.addAndGet(1);
                    return new AbstractMap.SimpleEntry<>(predictorTerm.getName() != null ?
                                                                 predictorTerm.getName().getValue() :
                                                                 "predictorTerm" + arity,
                                                         addPredictorTerm(predictorTerm, tableTemplate, arity));
                })
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey,
                                          AbstractMap.SimpleEntry::getValue));
    }

    /**
     * Add a <b>PredictorTerm</b> <code>MethodDeclaration</code> to the class
     * @param predictorTerm
     * @param tableTemplate
     * @param predictorArity
     * @return
     */
    static MethodDeclaration addPredictorTerm(final PredictorTerm predictorTerm,
                                              final ClassOrInterfaceDeclaration tableTemplate,
                                              int predictorArity) {
        try {
            templateEvaluate = getFromFileName(KIE_PMML_EVALUATE_METHOD_TEMPLATE_JAVA);
            cloneEvaluate = templateEvaluate.clone();
            ClassOrInterfaceDeclaration evaluateTemplateClass =
                    cloneEvaluate.getClassByName(KIE_PMML_EVALUATE_METHOD_TEMPLATE)
                            .orElseThrow(() -> new RuntimeException(MAIN_CLASS_NOT_FOUND));
            MethodDeclaration methodTemplate = evaluateTemplateClass.getMethodsByName("evaluatePredictor").get(0);
            final BlockStmt body =
                    methodTemplate.getBody().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_BODY_TEMPLATE, methodTemplate.getName())));
            VariableDeclarator variableDeclarator = getVariableDeclarator(body, "fieldRefs")
                    .orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_VARIABLE_IN_BODY,
                                                                                  "fieldRefs", body)));
            final List<Expression> nodeList = predictorTerm.getFieldRefs().stream()
                    .map(fieldRef -> new StringLiteralExpr(fieldRef.getField().getValue()))
                    .collect(Collectors.toList());
            NodeList<Expression> expressions = NodeList.nodeList(nodeList);
            MethodCallExpr methodCallExpr = new MethodCallExpr(new NameExpr("Arrays"), "asList", expressions);
            variableDeclarator.setInitializer(methodCallExpr);
            variableDeclarator = getVariableDeclarator(body, COEFFICIENT)
                    .orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_VARIABLE_IN_BODY,
                                                                                  COEFFICIENT, body)));
            variableDeclarator.setInitializer(String.valueOf(predictorTerm.getCoefficient().doubleValue()));
            return addMethod(methodTemplate, tableTemplate, "evaluatePredictorTerm" + predictorArity);
        } catch (Exception e) {
            throw new KiePMMLInternalException(String.format("Failed to add PredictorTerm %s", predictorTerm), e);
        }
    }
}
