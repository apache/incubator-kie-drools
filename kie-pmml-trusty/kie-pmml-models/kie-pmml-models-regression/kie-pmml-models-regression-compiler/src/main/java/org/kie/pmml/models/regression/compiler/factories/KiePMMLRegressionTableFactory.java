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
package org.kie.pmml.models.regression.compiler.factories;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
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
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.regression.compiler.dto.RegressionCompilationDTO;
import org.kie.pmml.models.regression.model.AbstractKiePMMLTable;
import org.kie.pmml.models.regression.model.KiePMMLRegressionTable;
import org.kie.pmml.models.regression.model.tuples.KiePMMLTableSourceCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.groupingBy;
import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.commons.Constants.TO_RETURN;
import static org.kie.pmml.commons.Constants.VARIABLE_NAME_TEMPLATE;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedVariableName;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.addMapPopulationExpressions;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.createPopulatedHashMap;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getChainedMethodCallExprFrom;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForObject;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getTypedClassOrInterfaceTypeByTypeNames;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFullClassName;

public class KiePMMLRegressionTableFactory {

    static final String KIE_PMML_REGRESSION_TABLE_TEMPLATE_JAVA = "KiePMMLRegressionTableTemplate.tmpl";
    static final String KIE_PMML_REGRESSION_TABLE_TEMPLATE = "KiePMMLRegressionTableTemplate";
    static final String GETKIEPMML_TABLE = "getKiePMMLTable";
    static final String NUMERIC_FUNCTION_MAP = "numericFunctionMap";
    static final String CATEGORICAL_FUNCTION_MAP = "categoricalFunctionMap";
    static final String PREDICTOR_TERM_FUNCTION_MAP = "predictorTermFunctionMap";
    static final ClassOrInterfaceDeclaration REGRESSION_TABLE_TEMPLATE;

    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_REGRESSION_TABLE_TEMPLATE_JAVA);
        REGRESSION_TABLE_TEMPLATE = cloneCU.getClassByName(KIE_PMML_REGRESSION_TABLE_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_REGRESSION_TABLE_TEMPLATE));
    }

    static final Logger logger =
            LoggerFactory.getLogger(KiePMMLRegressionTableFactory.class.getName());
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

    private KiePMMLRegressionTableFactory() {
        // Avoid instantiation
    }

    //  KiePMMLRegressionTable instantiation

    public static LinkedHashMap<String, KiePMMLRegressionTable> getRegressionTables(final RegressionCompilationDTO compilationDTO) {
        logger.trace("getRegressionTables {}", compilationDTO.getRegressionTables());
        LinkedHashMap<String, KiePMMLRegressionTable> toReturn = new LinkedHashMap<>();
        for (RegressionTable regressionTable : compilationDTO.getRegressionTables()) {
            final KiePMMLRegressionTable kiePMMLRegressionTable = getRegressionTable(regressionTable,
                                                                                     compilationDTO);
            String targetCategory = regressionTable.getTargetCategory() != null ?
                    regressionTable.getTargetCategory().toString() : "";
            toReturn.put(targetCategory, kiePMMLRegressionTable);
        }
        return toReturn;
    }

    public static KiePMMLRegressionTable getRegressionTable(final RegressionTable regressionTable,
                                                            final RegressionCompilationDTO compilationDTO) {
        logger.trace("getRegressionTable {}", regressionTable);

        final Map<String, SerializableFunction<Double, Double>> numericPredictorsMap =
                getNumericPredictorsMap(regressionTable.getNumericPredictors());
        final Map<String, SerializableFunction<String, Double>> categoricalPredictorsMap =
                getCategoricalPredictorsMap(regressionTable.getCategoricalPredictors());
        final Map<String, SerializableFunction<Map<String, Object>, Double>> predictorTermFunctionMap =
                getPredictorTermsMap(regressionTable.getPredictorTerms());
        final SerializableFunction<Double, Double> resultUpdater =
                getResultUpdaterFunction(compilationDTO.getDefaultNormalizationMethod());
        final Double intercept = regressionTable.getIntercept() != null ? regressionTable.getIntercept().doubleValue() : null;
        return KiePMMLRegressionTable.builder(UUID.randomUUID().toString(), Collections.emptyList())
                .withNumericFunctionMap(numericPredictorsMap)
                .withCategoricalFunctionMap(categoricalPredictorsMap)
                .withPredictorTermsFunctionMap(predictorTermFunctionMap)
                .withResultUpdater(resultUpdater)
                .withIntercept(intercept)
                .withTargetField(compilationDTO.getTargetFieldName())
                .withTargetCategory(regressionTable.getTargetCategory())
                .build();
    }

    // Source code generation

    public static LinkedHashMap<String, KiePMMLTableSourceCategory> getRegressionTableBuilders(final RegressionCompilationDTO compilationDTO) {
        logger.trace("getRegressionTables {}", compilationDTO.getRegressionTables());
        LinkedHashMap<String, KiePMMLTableSourceCategory> toReturn = new LinkedHashMap<>();
        for (RegressionTable regressionTable : compilationDTO.getRegressionTables()) {
            final Map.Entry<String, String> regressionTableEntry = getRegressionTableBuilder(regressionTable,
                                                                                             compilationDTO);
            String targetCategory = regressionTable.getTargetCategory() != null ?
                    regressionTable.getTargetCategory().toString() : "";
            toReturn.put(regressionTableEntry.getKey(),
                         new KiePMMLTableSourceCategory(regressionTableEntry.getValue(), targetCategory));
        }
        return toReturn;
    }

    public static Map.Entry<String, String> getRegressionTableBuilder(final RegressionTable regressionTable,
                                                                      final RegressionCompilationDTO compilationDTO) {
        logger.trace("getRegressionTableBuilder {}", regressionTable);
        String className = "KiePMMLRegressionTable" + classArity.addAndGet(1);
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className,
                                                                                 compilationDTO.getPackageName(),
                                                                                 KIE_PMML_REGRESSION_TABLE_TEMPLATE_JAVA, KIE_PMML_REGRESSION_TABLE_TEMPLATE);
        ClassOrInterfaceDeclaration tableTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        final MethodDeclaration staticGetterMethod =
                tableTemplate.getMethodsByName(GETKIEPMML_TABLE).get(0);
        setStaticGetter(regressionTable, compilationDTO, staticGetterMethod, className.toLowerCase());
        return new AbstractMap.SimpleEntry<>(getFullClassName(cloneCU), cloneCU.toString());
    }

    // not-public KiePMMLRegressionTable instantiation

    /**
     * Create <b>NumericPredictor</b>s <code>Map</code>es
     *
     * @param numericPredictors
     * @return
     */
    static Map<String, SerializableFunction<Double, Double>> getNumericPredictorsMap(final List<NumericPredictor> numericPredictors) {
        return numericPredictors.stream()
                .collect(Collectors.toMap(numericPredictor ->numericPredictor.getField(),
                                          KiePMMLRegressionTableFactory::getNumericPredictorEntry));
    }

    /**
     * Create a <b>NumericPredictor</b> <code>Entry</code>
     *
     * @param numericPredictor
     * @return
     */
    static SerializableFunction<Double, Double> getNumericPredictorEntry(final NumericPredictor numericPredictor) {
        boolean withExponent = !Objects.equals(1, numericPredictor.getExponent());
        if (withExponent) {
            return input -> KiePMMLRegressionTable.evaluateNumericWithExponent(input,
                                                                               numericPredictor.getCoefficient().doubleValue(), numericPredictor.getExponent().doubleValue());
        } else {
            return input -> KiePMMLRegressionTable.evaluateNumericWithoutExponent(input,
                                                                                  numericPredictor.getCoefficient().doubleValue());
        }
    }

    /**
     * Create the <b>CategoricalPredictor</b>s <code>Map</code>
     *
     * @param categoricalPredictors
     * @return
     */
    static Map<String, SerializableFunction<String, Double>> getCategoricalPredictorsMap(final List<CategoricalPredictor> categoricalPredictors) {
        final Map<String, List<CategoricalPredictor>> groupedCollectors = categoricalPredictors.stream()
                .collect(groupingBy(categoricalPredictor ->categoricalPredictor.getField()));
        return groupedCollectors.entrySet().stream()
                .map(entry -> {
                    Map<String, Double> groupedCategoricalPredictorMap =
                            getGroupedCategoricalPredictorMap(entry.getValue());
                    SerializableFunction<String, Double> function =
                            input -> KiePMMLRegressionTable.evaluateCategoricalPredictor(input,
                                                                                         groupedCategoricalPredictorMap);
                    return new AbstractMap.SimpleEntry<>(entry.getKey(),
                                                         function);
                })
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey,
                                          AbstractMap.SimpleEntry::getValue));
    }

    /**
     * Populate the <code>Map</code> for the given <b>categoricalPredictors</b>
     *
     * @param categoricalPredictors
     * @return
     */
    static Map<String, Double> getGroupedCategoricalPredictorMap(final List<CategoricalPredictor> categoricalPredictors) {
        final Map<String, Double> toReturn = new LinkedHashMap<>();
        for (CategoricalPredictor categoricalPredictor : categoricalPredictors) {
            toReturn.put(categoricalPredictor.getValue().toString(),
                         categoricalPredictor.getCoefficient().doubleValue());
        }
        return toReturn;
    }

    /**
     * Get the <code>Map</code> of <b>PredictorTerm</b>' <code>VariableDeclarationExpr</code>s
     *
     * @param predictorTerms
     * @return
     */
    static Map<String, SerializableFunction<Map<String, Object>, Double>> getPredictorTermsMap(final List<PredictorTerm> predictorTerms) {
        predictorsArity.set(0);
        return predictorTerms.stream()
                .map(predictorTerm -> {
                    int arity = predictorsArity.addAndGet(1);
                    String variableName = predictorTerm.getName() != null ?predictorTerm.getName() :
                            "predictorTermFunction" + arity;
                    return new AbstractMap.SimpleEntry<>(variableName,
                                                         getPredictorTermSerializableFunction(predictorTerm));
                })
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey,
                                          AbstractMap.SimpleEntry::getValue));
    }

    /**
     * Get the <b>PredictorTerm</b> <code>SerializableFunction</code>
     *
     * @param predictorTerm
     * @return
     */
    static SerializableFunction<Map<String, Object>, Double> getPredictorTermSerializableFunction(final PredictorTerm predictorTerm) {
        return resultMap -> {
            final AtomicReference<Double> result = new AtomicReference<>(1.0);
            final List<String> fieldRefs = predictorTerm.getFieldRefs().stream()
                    .map(fieldRef ->fieldRef.getField())
                    .collect(Collectors.toList());
            for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
                if (fieldRefs.contains(entry.getKey())) {
                    result.set(result.get() * (Double) entry.getValue());
                }
            }
            return result.get() * predictorTerm.getCoefficient().doubleValue();
        };
    }

    static SerializableFunction<Double, Double> getResultUpdaterFunction(final RegressionModel.NormalizationMethod normalizationMethod) {
        if (UNSUPPORTED_NORMALIZATION_METHODS.contains(normalizationMethod)) {
            return null;
        } else {
            return getResultUpdaterSupportedFunction(normalizationMethod);
        }
    }

    /**
     * Create a <b>resultUpdater</b> <code>CastExpr</code>
     *
     * @param normalizationMethod
     * @return
     */
    static SerializableFunction<Double, Double> getResultUpdaterSupportedFunction(final RegressionModel.NormalizationMethod normalizationMethod) {
        switch (normalizationMethod) {
            case SOFTMAX:
                return AbstractKiePMMLTable::updateSOFTMAXResult;
            case LOGIT:
                return AbstractKiePMMLTable::updateLOGITResult;
            case EXP:
                return AbstractKiePMMLTable::updateEXPResult;
            case PROBIT:
                return AbstractKiePMMLTable::updatePROBITResult;
            case CLOGLOG:
                return AbstractKiePMMLTable::updateCLOGLOGResult;
            case CAUCHIT:
                return AbstractKiePMMLTable::updateCAUCHITResult;
            case NONE:
                return AbstractKiePMMLTable::updateNONEResult;
            default:
                throw new KiePMMLException("Unexpected NormalizationMethod " + normalizationMethod);
        }
    }

    // not-public code-generation

    static void setStaticGetter(final RegressionTable regressionTable,
                                final RegressionCompilationDTO compilationDTO,
                                final MethodDeclaration staticGetterMethod,
                                final String variableName) {
        final BlockStmt regressionTableBody =
                staticGetterMethod.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, staticGetterMethod)));

        final BlockStmt newBody = new BlockStmt();
        // populate maps
        String numericFunctionMapName = String.format(VARIABLE_NAME_TEMPLATE, NUMERIC_FUNCTION_MAP, variableName);
        final Map<String, Expression> numericPredictorsMap =
                getNumericPredictorsExpressions(regressionTable.getNumericPredictors());
        createPopulatedHashMap(newBody, numericFunctionMapName, Arrays.asList(String.class.getSimpleName(),
                                                                              "SerializableFunction<Double, Double>"),
                               numericPredictorsMap);

        final Map<String, Expression> categoricalPredictorFunctionsMap =
                getCategoricalPredictorsExpressions(regressionTable.getCategoricalPredictors(), newBody, variableName);
        String categoricalFunctionMapName = String.format(VARIABLE_NAME_TEMPLATE, CATEGORICAL_FUNCTION_MAP,
                                                          variableName);
        createPopulatedHashMap(newBody, categoricalFunctionMapName, Arrays.asList(String.class.getSimpleName(),
                                                                                  "SerializableFunction<String, " +
                                                                                          "Double>")
                , categoricalPredictorFunctionsMap);
        String predictorTermsFunctionMapName = String.format(VARIABLE_NAME_TEMPLATE, PREDICTOR_TERM_FUNCTION_MAP,
                                                             variableName);
        final Map<String, Expression> predictorTermsMap =
                getPredictorTermFunctions(regressionTable.getPredictorTerms());
        createPopulatedHashMap(newBody, predictorTermsFunctionMapName, Arrays.asList(String.class.getSimpleName(),
                                                                                     "SerializableFunction<Map" +
                                                                                             "<String, " +
                                                                                             "Object>, Double>"),
                               predictorTermsMap);

        final VariableDeclarator variableDeclarator =
                getVariableDeclarator(regressionTableBody, TO_RETURN).orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, TO_RETURN, regressionTableBody)));
        final MethodCallExpr initializer = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE,
                                                                      TO_RETURN, regressionTableBody)))
                .asMethodCallExpr();
        final MethodCallExpr builder = getChainedMethodCallExprFrom("builder", initializer);
        builder.setArgument(0, new StringLiteralExpr(variableName));
        getChainedMethodCallExprFrom("withNumericFunctionMap", initializer).setArgument(0,
                                                                                        new NameExpr(numericFunctionMapName) {
                                                                                        });
        getChainedMethodCallExprFrom("withCategoricalFunctionMap", initializer).setArgument(0,
                                                                                            new NameExpr(categoricalFunctionMapName));
        getChainedMethodCallExprFrom("withPredictorTermsFunctionMap", initializer).setArgument(0,
                                                                                               new NameExpr(predictorTermsFunctionMapName));
        getChainedMethodCallExprFrom("withIntercept", initializer).setArgument(0,
                                                                               getExpressionForObject(regressionTable.getIntercept().doubleValue()));

        getChainedMethodCallExprFrom("withTargetField", initializer).setArgument(0,
                                                                                 getExpressionForObject(compilationDTO.getTargetFieldName()));
        getChainedMethodCallExprFrom("withTargetCategory", initializer).setArgument(0,
                                                                                    getExpressionForObject(regressionTable.getTargetCategory()));
        final Expression resultUpdaterExpression =
                getResultUpdaterExpression(compilationDTO.getDefaultNormalizationMethod());
        getChainedMethodCallExprFrom("withResultUpdater", initializer).setArgument(0, resultUpdaterExpression);
        regressionTableBody.getStatements().forEach(newBody::addStatement);
        staticGetterMethod.setBody(newBody);
    }

    static Expression getResultUpdaterExpression(final RegressionModel.NormalizationMethod normalizationMethod) {
        if (UNSUPPORTED_NORMALIZATION_METHODS.contains(normalizationMethod)) {
            return new NullLiteralExpr();
        } else {
            return getResultUpdaterSupportedExpression(normalizationMethod);
        }
    }

    /**
     * Create a <b>resultUpdater</b> <code>CastExpr</code>
     *
     * @param normalizationMethod
     * @return
     */
    static MethodReferenceExpr getResultUpdaterSupportedExpression(final RegressionModel.NormalizationMethod normalizationMethod) {
        final String thisExpressionMethodName = String.format("update%sResult", normalizationMethod.name());
        final CastExpr castExpr = new CastExpr();
        final String doubleClassName = Double.class.getSimpleName();
        final ClassOrInterfaceType consumerType =
                getTypedClassOrInterfaceTypeByTypeNames(SerializableFunction.class.getCanonicalName(),
                                                        Arrays.asList(doubleClassName, doubleClassName));
        castExpr.setType(consumerType);
        castExpr.setExpression(KiePMMLRegressionTable.class.getSimpleName());
        final MethodReferenceExpr toReturn = new MethodReferenceExpr();
        toReturn.setScope(castExpr);
        toReturn.setIdentifier(thisExpressionMethodName);
        return toReturn;
    }

    /**
     * Create <b>NumericPredictor</b>s <code>CastExpr</code>es
     *
     * @param numericPredictors
     * @return
     */
    static Map<String, Expression> getNumericPredictorsExpressions(final List<NumericPredictor> numericPredictors) {
        return numericPredictors.stream()
                .collect(Collectors.toMap(numericPredictor ->numericPredictor.getField(),
                                          KiePMMLRegressionTableFactory::getNumericPredictorExpression));
    }

    /**
     * Create a <b>NumericPredictor</b> <code>CastExpr</code>
     *
     * @param numericPredictor
     * @return
     */
    static CastExpr getNumericPredictorExpression(final NumericPredictor numericPredictor) {
        boolean withExponent = !Objects.equals(1, numericPredictor.getExponent());
        final String lambdaExpressionMethodName = withExponent ? "evaluateNumericWithExponent" :
                "evaluateNumericWithoutExponent";
        final String parameterName = "input";
        final MethodCallExpr lambdaMethodCallExpr = new MethodCallExpr();
        lambdaMethodCallExpr.setName(lambdaExpressionMethodName);
        lambdaMethodCallExpr.setScope(new NameExpr(KiePMMLRegressionTable.class.getSimpleName()));
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
     *
     * @param categoricalPredictors
     * @param body
     * @return
     */
    static Map<String, Expression> getCategoricalPredictorsExpressions(final List<CategoricalPredictor> categoricalPredictors, final BlockStmt body, final String variableName) {
        final Map<String, List<CategoricalPredictor>> groupedCollectors = categoricalPredictors.stream()
                .collect(groupingBy(categoricalPredictor ->categoricalPredictor.getField()));
        final String categoricalPredictorMapNameBase = getSanitizedVariableName(String.format("%sMap", variableName));
        final AtomicInteger counter = new AtomicInteger();
        return groupedCollectors.entrySet().stream()
                .map(entry -> {
                    final String categoricalPredictorMapName = String.format(VARIABLE_NAME_TEMPLATE,
                                                                             categoricalPredictorMapNameBase,
                                                                             counter.getAndIncrement());
                    populateWithGroupedCategoricalPredictorMap(entry.getValue(), body, categoricalPredictorMapName);
                    return new AbstractMap.SimpleEntry<>(entry.getKey(),
                                                         getCategoricalPredictorExpression(categoricalPredictorMapName));
                })
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey,
                                          AbstractMap.SimpleEntry::getValue));
    }

    /**
     * Populate the given <b>body</b> with the creation of a <code>Map</code> for the given <b>categoricalPredictors</b>
     *
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
     *
     * @param categoricalPredictorMapName
     * @return
     */
    static CastExpr getCategoricalPredictorExpression(final String categoricalPredictorMapName) {
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
        lambdaMethodCallExpr.setScope(new NameExpr(KiePMMLRegressionTable.class.getSimpleName()));
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
     * Get the <code>Map</code> of <b>PredictorTerm</b>' <code>VariableDeclarationExpr</code>s
     *
     * @param predictorTerms
     * @return
     */
    static Map<String, Expression> getPredictorTermFunctions(final List<PredictorTerm> predictorTerms) {
        predictorsArity.set(0);
        return predictorTerms.stream()
                .map(predictorTerm -> {
                    int arity = predictorsArity.addAndGet(1);
                    String variableName = predictorTerm.getName() != null ?predictorTerm.getName() :
                            "predictorTermFunction" + arity;
                    return new AbstractMap.SimpleEntry<>(variableName,
                                                         getPredictorTermFunction(predictorTerm));
                })
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey,
                                          AbstractMap.SimpleEntry::getValue));
    }

    /**
     * Get the <b>PredictorTerm</b> <code>VariableDeclarationExpr</code>
     *
     * @param predictorTerm
     * @return
     */
    static LambdaExpr getPredictorTermFunction(final PredictorTerm predictorTerm) {
        try {
            LambdaExpr toReturn = new LambdaExpr();
            toReturn.setParameters(NodeList.nodeList(new Parameter(new UnknownType(), "resultMap")));
            final BlockStmt body = getPredictorTermBody(predictorTerm);
            toReturn.setBody(body);

            return toReturn;
        } catch (Exception e) {
            throw new KiePMMLInternalException(String.format("Failed to get PredictorTermFunction for %s",
                                                             predictorTerm), e);
        }
    }

    /**
     * Add a <b>PredictorTerm</b> <code>MethodDeclaration</code> to the class
     *
     * @param predictorTerm
     * @return
     */
    static BlockStmt getPredictorTermBody(final PredictorTerm predictorTerm) {
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
                    .map(fieldRef -> new StringLiteralExpr(fieldRef.getField()))
                    .collect(Collectors.toList());
            NodeList<Expression> expressions = NodeList.nodeList(nodeList);
            MethodCallExpr methodCallExpr = new MethodCallExpr(new NameExpr("Arrays"), "asList", expressions);
            variableDeclarator.setInitializer(methodCallExpr);
            variableDeclarator = getVariableDeclarator(body, COEFFICIENT)
                    .orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_VARIABLE_IN_BODY,
                                                                                  COEFFICIENT, body)));
            variableDeclarator.setInitializer(String.valueOf(predictorTerm.getCoefficient().doubleValue()));
            return methodTemplate.getBody().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_BODY_TEMPLATE, methodTemplate.getName())));
        } catch (Exception e) {
            throw new KiePMMLInternalException(String.format("Failed to add PredictorTerm %s", predictorTerm), e);
        }
    }
}
