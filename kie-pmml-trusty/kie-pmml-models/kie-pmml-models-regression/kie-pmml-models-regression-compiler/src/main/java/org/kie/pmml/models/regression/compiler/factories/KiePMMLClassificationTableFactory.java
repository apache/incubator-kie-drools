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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.dmg.pmml.regression.RegressionModel;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.api.iinterfaces.SerializableFunction;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.regression.compiler.dto.RegressionCompilationDTO;
import org.kie.pmml.models.regression.model.KiePMMLClassificationTable;
import org.kie.pmml.models.regression.model.KiePMMLRegressionTable;
import org.kie.pmml.models.regression.model.enums.REGRESSION_NORMALIZATION_METHOD;
import org.kie.pmml.models.regression.model.tuples.KiePMMLTableSourceCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.Constants.MISSING_BODY_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_INITIALIZER_TEMPLATE;
import static org.kie.pmml.commons.Constants.MISSING_VARIABLE_IN_BODY;
import static org.kie.pmml.commons.Constants.TO_RETURN;
import static org.kie.pmml.commons.Constants.VARIABLE_NAME_TEMPLATE;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.createPopulatedLinkedHashMap;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getChainedMethodCallExprFrom;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForObject;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getTypedClassOrInterfaceTypeByTypeNames;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getTypedClassOrInterfaceTypeByTypes;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getVariableDeclarator;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.MAIN_CLASS_NOT_FOUND;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFullClassName;

public class KiePMMLClassificationTableFactory {

    private static final Logger logger =
            LoggerFactory.getLogger(KiePMMLClassificationTableFactory.class.getName());

    static final String KIE_PMML_CLASSIFICATION_TABLE_TEMPLATE_JAVA = "KiePMMLClassificationTableTemplate.tmpl";
    static final String KIE_PMML_CLASSIFICATION_TABLE_TEMPLATE = "KiePMMLClassificationTableTemplate";
    static final String GETKIEPMML_TABLE = "getKiePMMLTable";
    static final String CATEGORICAL_TABLE_MAP = "categoryTableMap";
    static final ClassOrInterfaceDeclaration CLASSIFICATION_TABLE_TEMPLATE;

    static {
        CompilationUnit cloneCU = JavaParserUtils.getFromFileName(KIE_PMML_CLASSIFICATION_TABLE_TEMPLATE_JAVA);
        CLASSIFICATION_TABLE_TEMPLATE = cloneCU.getClassByName(KIE_PMML_CLASSIFICATION_TABLE_TEMPLATE)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + KIE_PMML_CLASSIFICATION_TABLE_TEMPLATE));
        CLASSIFICATION_TABLE_TEMPLATE.getMethodsByName(GETKIEPMML_TABLE).get(0).clone();
    }

    public static final List<RegressionModel.NormalizationMethod> SUPPORTED_NORMALIZATION_METHODS =
            Arrays.asList(RegressionModel.NormalizationMethod.SOFTMAX,
                          RegressionModel.NormalizationMethod.SIMPLEMAX,
                          RegressionModel.NormalizationMethod.NONE,
                          RegressionModel.NormalizationMethod.LOGIT,
                          RegressionModel.NormalizationMethod.PROBIT,
                          RegressionModel.NormalizationMethod.CLOGLOG,
                          RegressionModel.NormalizationMethod.CAUCHIT);
    public static final List<RegressionModel.NormalizationMethod> UNSUPPORTED_NORMALIZATION_METHODS =
            Arrays.asList(RegressionModel.NormalizationMethod.EXP,
                          RegressionModel.NormalizationMethod.LOGLOG);
    private static AtomicInteger classArity = new AtomicInteger(0);

    private KiePMMLClassificationTableFactory() {
        // Avoid instantiation
    }

    //  KiePMMLClassificationTable instantiation


    public static KiePMMLClassificationTable getClassificationTable(final RegressionCompilationDTO compilationDTO) {
        logger.trace("getClassificationTable {}", compilationDTO);
        final LinkedHashMap<String, KiePMMLRegressionTable> categoryTableMap =
                KiePMMLRegressionTableFactory.getRegressionTables(compilationDTO);
        boolean isBinary = compilationDTO.isBinary(categoryTableMap.size());
        final SerializableFunction<LinkedHashMap<String, Double>,
                LinkedHashMap<String, Double>> probabilityMapFunction = getProbabilityMapFunction(compilationDTO.getModelNormalizationMethod(), isBinary);

        return KiePMMLClassificationTable.builder(UUID.randomUUID().toString(), Collections.emptyList())
                .withRegressionNormalizationMethod(compilationDTO.getDefaultREGRESSION_NORMALIZATION_METHOD())
                .withOpType(compilationDTO.getOP_TYPE())
                .withCategoryTableMap(categoryTableMap)
                .withProbabilityMapFunction(probabilityMapFunction)
                .withIsBinary(isBinary)
                .withTargetField(compilationDTO.getTargetFieldName())
                .build();
    }

    // Source code generation

    public static Map<String, KiePMMLTableSourceCategory> getClassificationTableBuilders(final RegressionCompilationDTO compilationDTO) {

        logger.trace("getRegressionTables {}", compilationDTO.getRegressionTables());
        LinkedHashMap<String, KiePMMLTableSourceCategory> toReturn =
                KiePMMLRegressionTableFactory.getRegressionTableBuilders(compilationDTO);
        Map.Entry<String, String> regressionTableEntry = getClassificationTableBuilder(compilationDTO, toReturn);
        toReturn.put(regressionTableEntry.getKey(), new KiePMMLTableSourceCategory(regressionTableEntry.getValue(),
                                                                                   ""));
        return toReturn;
    }

    public static Map.Entry<String, String> getClassificationTableBuilder(final RegressionCompilationDTO compilationDTO,
                                                                          final LinkedHashMap<String,
                                                                                  KiePMMLTableSourceCategory> regressionTablesMap) {
        logger.trace("getRegressionTableBuilder {}", regressionTablesMap);
        String className = "KiePMMLClassificationTable" + classArity.addAndGet(1);
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className,
                                                                                 compilationDTO.getPackageName(),
                                                                                 KIE_PMML_CLASSIFICATION_TABLE_TEMPLATE_JAVA, KIE_PMML_CLASSIFICATION_TABLE_TEMPLATE);
        ClassOrInterfaceDeclaration tableTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        final MethodDeclaration staticGetterMethod =
                tableTemplate.getMethodsByName(GETKIEPMML_TABLE).get(0);
        setStaticGetter(compilationDTO, regressionTablesMap, staticGetterMethod, className.toLowerCase());
        return new AbstractMap.SimpleEntry<>(getFullClassName(cloneCU), cloneCU.toString());
    }

    // not-public KiePMMLClassificationTable instantiation

    static SerializableFunction<LinkedHashMap<String, Double>,
            LinkedHashMap<String, Double>> getProbabilityMapFunction(final RegressionModel.NormalizationMethod normalizationMethod,
                                                                     final boolean isBinary) {
        if (UNSUPPORTED_NORMALIZATION_METHODS.contains(normalizationMethod)) {
            throw new KiePMMLInternalException(String.format("Unsupported NormalizationMethod %s",
                                                             normalizationMethod));
        } else {
            return getProbabilityMapFunctionSupported(normalizationMethod, isBinary);
        }
    }

    /**
     * Create <b>probabilityMapFunction</b> <code>SerializableFunction</code>
     * @param normalizationMethod
     * @param isBinary
     * @return
     */
    static SerializableFunction<LinkedHashMap<String, Double>,
            LinkedHashMap<String, Double>> getProbabilityMapFunctionSupported(final RegressionModel.NormalizationMethod normalizationMethod,
                                                                              final boolean isBinary) {
        switch (normalizationMethod) {
            case SOFTMAX:
                return KiePMMLClassificationTable::getSOFTMAXProbabilityMap;
            case SIMPLEMAX:
                return KiePMMLClassificationTable::getSIMPLEMAXProbabilityMap;
            case NONE:
                return isBinary ? KiePMMLClassificationTable::getNONEBinaryProbabilityMap : KiePMMLClassificationTable::getNONEProbabilityMap;
            case LOGIT:
                return KiePMMLClassificationTable::getLOGITProbabilityMap;
            case PROBIT:
                return KiePMMLClassificationTable::getPROBITProbabilityMap;
            case CLOGLOG:
                return KiePMMLClassificationTable::getCLOGLOGProbabilityMap;
            case CAUCHIT:
                return KiePMMLClassificationTable::getCAUCHITProbabilityMap;
            default:
                throw new KiePMMLException("Unexpected NormalizationMethod " + normalizationMethod);
        }
    }

    // not-public code-generation

    static void setStaticGetter(final RegressionCompilationDTO compilationDTO,
                                final LinkedHashMap<String,
                                        KiePMMLTableSourceCategory> regressionTablesMap,
                                final MethodDeclaration staticGetterMethod,
                                final String variableName) {
        final BlockStmt classificationTableBody =
                staticGetterMethod.getBody().orElseThrow(() -> new KiePMMLException(String.format(MISSING_BODY_TEMPLATE, staticGetterMethod)));
        final VariableDeclarator variableDeclarator =
                getVariableDeclarator(classificationTableBody, TO_RETURN).orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_IN_BODY, TO_RETURN, classificationTableBody)));
        final BlockStmt newBody = new BlockStmt();
        final Map<String, Expression> regressionTableCategoriesMap = new LinkedHashMap<>();
        regressionTablesMap.forEach((className, tableSourceCategory) -> {
            MethodCallExpr methodCallExpr = new MethodCallExpr();
            methodCallExpr.setScope(new NameExpr(className));
            methodCallExpr.setName(KiePMMLRegressionTableFactory.GETKIEPMML_TABLE);
            regressionTableCategoriesMap.put(tableSourceCategory.getCategory(), methodCallExpr);
        });
        // populate map
        String categoryTableMapName = String.format(VARIABLE_NAME_TEMPLATE, CATEGORICAL_TABLE_MAP, variableName);
        createPopulatedLinkedHashMap(newBody, categoryTableMapName, Arrays.asList(String.class.getSimpleName(),
                                                                                  KiePMMLRegressionTable.class.getName()),
                                     regressionTableCategoriesMap);
        final MethodCallExpr initializer = variableDeclarator.getInitializer()
                .orElseThrow(() -> new KiePMMLException(String.format(MISSING_VARIABLE_INITIALIZER_TEMPLATE,
                                                                      TO_RETURN, classificationTableBody)))
                .asMethodCallExpr();
        final MethodCallExpr builder = getChainedMethodCallExprFrom("builder", initializer);
        builder.setArgument(0, new StringLiteralExpr(variableName));
        final REGRESSION_NORMALIZATION_METHOD regressionNormalizationMethod =
                compilationDTO.getDefaultREGRESSION_NORMALIZATION_METHOD();
        getChainedMethodCallExprFrom("withRegressionNormalizationMethod", initializer).setArgument(0,
                                                                                                   new NameExpr(regressionNormalizationMethod.getClass().getSimpleName() + "." + regressionNormalizationMethod.name()));

        OP_TYPE opType = compilationDTO.getOP_TYPE();
        getChainedMethodCallExprFrom("withOpType", initializer).setArgument(0,
                                                                            new NameExpr(opType.getClass().getSimpleName() + "." + opType.name()));
        getChainedMethodCallExprFrom("withCategoryTableMap", initializer).setArgument(0,
                                                                                      new NameExpr(categoryTableMapName));

        boolean isBinary = compilationDTO.isBinary(regressionTablesMap.size());
        final Expression probabilityMapFunctionExpression =
                getProbabilityMapFunctionExpression(compilationDTO.getModelNormalizationMethod(), isBinary);

        getChainedMethodCallExprFrom("withProbabilityMapFunction", initializer).setArgument(0,
                                                                                            probabilityMapFunctionExpression);
        getChainedMethodCallExprFrom("withIsBinary", initializer).setArgument(0, getExpressionForObject(isBinary));

        getChainedMethodCallExprFrom("withTargetField", initializer).setArgument(0,
                                                                                 getExpressionForObject(compilationDTO.getTargetFieldName()));
        getChainedMethodCallExprFrom("withTargetCategory", initializer).setArgument(0,
                                                                                    getExpressionForObject(null));
        classificationTableBody.getStatements().forEach(newBody::addStatement);
        staticGetterMethod.setBody(newBody);
    }

    static Expression getProbabilityMapFunctionExpression(final RegressionModel.NormalizationMethod normalizationMethod,
                                                          final boolean isBinary) {
        if (UNSUPPORTED_NORMALIZATION_METHODS.contains(normalizationMethod)) {
            throw new KiePMMLInternalException(String.format("Unsupported NormalizationMethod %s",
                                                             normalizationMethod));
        } else {
            return getProbabilityMapFunctionSupportedExpression(normalizationMethod, isBinary);
        }
    }

    /**
     * Create <b>probabilityMapFunction</b> <code>MethodReferenceExpr</code>
     *
     * @param normalizationMethod
     * @param isBinary
     * @return
     */
    static MethodReferenceExpr getProbabilityMapFunctionSupportedExpression(final RegressionModel.NormalizationMethod normalizationMethod,
                                                                            final boolean isBinary) {
        String normalizationName = normalizationMethod.name();
        if (RegressionModel.NormalizationMethod.NONE.equals(normalizationMethod) && isBinary) {
            normalizationName += "Binary";
        }
        final String thisExpressionMethodName = String.format("get%sProbabilityMap", normalizationName);
        final CastExpr castExpr = new CastExpr();
        final String stringClassName = String.class.getSimpleName();
        final String doubleClassName = Double.class.getSimpleName();
        final ClassOrInterfaceType linkedHashMapReferenceType =
                getTypedClassOrInterfaceTypeByTypeNames(LinkedHashMap.class.getCanonicalName(),
                                                        Arrays.asList(stringClassName, doubleClassName));
        final ClassOrInterfaceType consumerType =
                getTypedClassOrInterfaceTypeByTypes(SerializableFunction.class.getCanonicalName(),
                                                    Arrays.asList(linkedHashMapReferenceType,
                                                                  linkedHashMapReferenceType));
        castExpr.setType(consumerType);
        castExpr.setExpression("KiePMMLClassificationTable");
        final MethodReferenceExpr toReturn = new MethodReferenceExpr();
        toReturn.setScope(castExpr);
        toReturn.setIdentifier(thisExpressionMethodName);
        return toReturn;
    }
}
