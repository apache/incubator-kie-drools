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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.dmg.pmml.regression.RegressionModel;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.api.iinterfaces.SerializableFunction;
import org.kie.pmml.compiler.commons.utils.CommonCodegenUtils;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.regression.compiler.dto.RegressionCompilationDTO;
import org.kie.pmml.models.regression.model.enums.REGRESSION_NORMALIZATION_METHOD;
import org.kie.pmml.models.regression.model.tuples.KiePMMLTableSourceCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.Constants.MISSING_DEFAULT_CONSTRUCTOR;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getExpressionForObject;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getTypedClassOrInterfaceTypeByTypeNames;
import static org.kie.pmml.compiler.commons.utils.CommonCodegenUtils.getTypedClassOrInterfaceTypeByTypes;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFullClassName;

public class KiePMMLRegressionTableClassificationFactory {

    public static final String KIE_PMML_REGRESSION_TABLE_CLASSIFICATION_TEMPLATE_JAVA =
            "KiePMMLRegressionTableClassificationTemplate.tmpl";
    public static final String KIE_PMML_REGRESSION_TABLE_CLASSIFICATION_TEMPLATE =
            "KiePMMLRegressionTableClassificationTemplate";

    public final static List<RegressionModel.NormalizationMethod> SUPPORTED_NORMALIZATION_METHODS =
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

    private static final Logger logger =
            LoggerFactory.getLogger(KiePMMLRegressionTableClassificationFactory.class.getName());
    private static final String MAIN_CLASS_NOT_FOUND = "Main class not found";

    private static AtomicInteger classArity = new AtomicInteger(0);

    private KiePMMLRegressionTableClassificationFactory() {
        // Avoid instantiation
    }

    public static Map<String, KiePMMLTableSourceCategory> getRegressionTables(final RegressionCompilationDTO compilationDTO) {

        logger.trace("getRegressionTables {}", compilationDTO.getRegressionTables());
        LinkedHashMap<String, KiePMMLTableSourceCategory> toReturn =
                KiePMMLRegressionTableRegressionFactory.getRegressionTables(compilationDTO);
        Map.Entry<String, String> regressionTableEntry = getRegressionTable(compilationDTO, toReturn);
        toReturn.put(regressionTableEntry.getKey(), new KiePMMLTableSourceCategory(regressionTableEntry.getValue(),
                                                                                   ""));
        return toReturn;
    }

    /**
     * @param compilationDTO
     * @param regressionTablesMap Explicitly using a <code>LinkedHashMap</code> because insertion order matters
     * @return
     */
    public static Map.Entry<String, String> getRegressionTable(final RegressionCompilationDTO compilationDTO,
                                                               final LinkedHashMap<String,
                                                                       KiePMMLTableSourceCategory> regressionTablesMap) {
        logger.trace("getRegressionTable {}", regressionTablesMap);
        String className = "KiePMMLRegressionTableClassification" + classArity.addAndGet(1);
        CompilationUnit cloneCU = JavaParserUtils.getKiePMMLModelCompilationUnit(className,
                                                                                 compilationDTO.getPackageName(),
                                                                                 KIE_PMML_REGRESSION_TABLE_CLASSIFICATION_TEMPLATE_JAVA, KIE_PMML_REGRESSION_TABLE_CLASSIFICATION_TEMPLATE);
        ClassOrInterfaceDeclaration tableTemplate = cloneCU.getClassByName(className)
                .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND + ": " + className));
        boolean isBinary = compilationDTO.isBinary(regressionTablesMap.size());
        final ConstructorDeclaration constructorDeclaration =
                tableTemplate.getDefaultConstructor().orElseThrow(() -> new KiePMMLInternalException(String.format(MISSING_DEFAULT_CONSTRUCTOR, tableTemplate.getName())));
        setConstructor(compilationDTO, constructorDeclaration, tableTemplate.getName(), null,
                       compilationDTO.getModelNormalizationMethod(), isBinary);
        addMapPopulation(constructorDeclaration.getBody(), regressionTablesMap);
        return new AbstractMap.SimpleEntry<>(getFullClassName(cloneCU), cloneCU.toString());
    }

    /**
     * Set the values inside the constructor
     * @param compilationDTO
     * @param constructorDeclaration
     * @param generatedClassName
     * @param targetCategory
     * @param normalizationMethod
     * @param isBinary
     */
    static void setConstructor(final RegressionCompilationDTO compilationDTO,
                               final ConstructorDeclaration constructorDeclaration,
                               final SimpleName generatedClassName,
                               final Object targetCategory,
                               final RegressionModel.NormalizationMethod normalizationMethod,
                               final boolean isBinary) {
        constructorDeclaration.setName(generatedClassName);
        final BlockStmt body = constructorDeclaration.getBody();
        CommonCodegenUtils.setAssignExpressionValue(body, "targetField",
                                                    new StringLiteralExpr(compilationDTO.getTargetFieldName()));
        final REGRESSION_NORMALIZATION_METHOD regressionNormalizationMethod =
                compilationDTO.getDefaultREGRESSION_NORMALIZATION_METHOD();
        CommonCodegenUtils.setAssignExpressionValue(body, "regressionNormalizationMethod",
                                                    new NameExpr(regressionNormalizationMethod.getClass().getSimpleName() + "." + regressionNormalizationMethod.name()));
        final OP_TYPE opType = compilationDTO.getOP_TYPE();
        if (opType != null) {
            CommonCodegenUtils.setAssignExpressionValue(body, "opType",
                                                        new NameExpr(opType.getClass().getSimpleName() + "." + opType.name()));
        }
        final Expression targetCategoryExpression = getExpressionForObject(targetCategory);
        CommonCodegenUtils.setAssignExpressionValue(body, "targetCategory", targetCategoryExpression);
        CommonCodegenUtils.setAssignExpressionValue(body, "isBinary", getExpressionForObject(isBinary));
        final Expression probabilityMapFunctionExpression =
                createProbabilityMapFunctionExpression(normalizationMethod, isBinary);
        CommonCodegenUtils.setAssignExpressionValue(body, "probabilityMapFunction", probabilityMapFunctionExpression);
    }

    static Expression createProbabilityMapFunctionExpression(final RegressionModel.NormalizationMethod normalizationMethod,
                                                             final boolean isBinary) {
        if (UNSUPPORTED_NORMALIZATION_METHODS.contains(normalizationMethod)) {
            throw new KiePMMLInternalException(String.format("Unsupported NormalizationMethod %s",
                                                             normalizationMethod));
        } else {
            return createProbabilityMapFunctionSupportedExpression(normalizationMethod, isBinary);
        }
    }

    /**
     * Create <b>probabilityMapFunction</b> <code>MethodReferenceExpr</code>
     * @param normalizationMethod
     * @param isBinary
     * @return
     */
    static MethodReferenceExpr createProbabilityMapFunctionSupportedExpression(final RegressionModel.NormalizationMethod normalizationMethod,
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
        castExpr.setExpression(new ThisExpr());
        final MethodReferenceExpr toReturn = new MethodReferenceExpr();
        toReturn.setScope(castExpr);
        toReturn.setIdentifier(thisExpressionMethodName);
        return toReturn;
    }

    /**
     * Add entries <b>category/KiePMMLRegressionTable</b> inside the constructor
     * @param body
     * @param regressionTablesMap Explicitly using a <code>LinkedHashMap</code> because insertion order matters
     */
    static void addMapPopulation(final BlockStmt body,
                                 final LinkedHashMap<String, KiePMMLTableSourceCategory> regressionTablesMap) {
        regressionTablesMap.forEach((className, tableSourceCategory) -> {
            ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr();
            objectCreationExpr.setType(className);
            NodeList<Expression> expressions =
                    NodeList.nodeList(new StringLiteralExpr(tableSourceCategory.getCategory()), objectCreationExpr);
            body.addStatement(new MethodCallExpr(new NameExpr("categoryTableMap"), "put", expressions));
        });
    }
}
