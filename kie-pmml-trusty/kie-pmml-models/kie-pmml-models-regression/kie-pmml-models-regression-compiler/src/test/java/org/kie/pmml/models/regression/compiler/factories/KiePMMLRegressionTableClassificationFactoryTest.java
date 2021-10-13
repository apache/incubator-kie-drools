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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.MiningField;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.OpType;
import org.dmg.pmml.Output;
import org.dmg.pmml.OutputField;
import org.dmg.pmml.PMML;
import org.dmg.pmml.ResultFeature;
import org.dmg.pmml.regression.RegressionModel;
import org.dmg.pmml.regression.RegressionTable;
import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.compiler.commons.mocks.HasClassLoaderMock;
import org.kie.pmml.models.regression.compiler.dto.RegressionCompilationDTO;
import org.kie.pmml.models.regression.model.enums.REGRESSION_NORMALIZATION_METHOD;
import org.kie.pmml.models.regression.model.tuples.KiePMMLTableSourceCategory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.pmml.commons.Constants.PACKAGE_NAME;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getGeneratedClassName;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonEvaluateConstructor;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionTableClassificationFactory.KIE_PMML_REGRESSION_TABLE_CLASSIFICATION_TEMPLATE;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionTableClassificationFactory.KIE_PMML_REGRESSION_TABLE_CLASSIFICATION_TEMPLATE_JAVA;

public class KiePMMLRegressionTableClassificationFactoryTest extends AbstractKiePMMLRegressionTableRegressionFactoryTest {

    private final static List<RegressionModel.NormalizationMethod> SUPPORTED_NORMALIZATION_METHODS =
            Arrays.asList(RegressionModel.NormalizationMethod.SOFTMAX,
                          RegressionModel.NormalizationMethod.SIMPLEMAX,
                          RegressionModel.NormalizationMethod.NONE,
                          RegressionModel.NormalizationMethod.LOGIT,
                          RegressionModel.NormalizationMethod.PROBIT,
                          RegressionModel.NormalizationMethod.CLOGLOG,
                          RegressionModel.NormalizationMethod.CAUCHIT);
    private final static List<RegressionModel.NormalizationMethod> UNSUPPORTED_NORMALIZATION_METHODS =
            Arrays.asList(RegressionModel.NormalizationMethod.EXP,
                          RegressionModel.NormalizationMethod.LOGLOG);
    private CompilationUnit compilationUnit;
    private ClassOrInterfaceDeclaration modelTemplate;

    @Before
    public void setup() {
        compilationUnit = getFromFileName(KIE_PMML_REGRESSION_TABLE_CLASSIFICATION_TEMPLATE_JAVA);
        modelTemplate = compilationUnit.getClassByName(KIE_PMML_REGRESSION_TABLE_CLASSIFICATION_TEMPLATE).get();
    }

    @Test
    public void getRegressionTables() {
        RegressionTable regressionTableProf = getRegressionTable(3.5, "professional");
        RegressionTable regressionTableCler = getRegressionTable(27.4, "clerical");
        OutputField outputFieldCat = getOutputField("CAT-1", ResultFeature.PROBABILITY, "CatPred-1");
        OutputField outputFieldNum = getOutputField("NUM-1", ResultFeature.PROBABILITY, "NumPred-0");
        OutputField outputFieldPrev = getOutputField("PREV", ResultFeature.PREDICTED_VALUE, null);

        String targetField = "targetField";
        DataField dataField = new DataField();
        dataField.setName(FieldName.create(targetField));
        dataField.setOpType(OpType.CATEGORICAL);
        DataDictionary dataDictionary = new DataDictionary();
        dataDictionary.addDataFields(dataField);
        RegressionModel regressionModel = new RegressionModel();
        regressionModel.setNormalizationMethod(RegressionModel.NormalizationMethod.CAUCHIT);
        regressionModel.addRegressionTables(regressionTableProf, regressionTableCler);
        regressionModel.setModelName(getGeneratedClassName("RegressionModel"));
        Output output = new Output();
        output.addOutputFields(outputFieldCat, outputFieldNum, outputFieldPrev);
        regressionModel.setOutput(output);
        MiningField miningField = new MiningField();
        miningField.setUsageType(MiningField.UsageType.TARGET);
        miningField.setName(dataField.getName());
        MiningSchema miningSchema = new MiningSchema();
        miningSchema.addMiningFields(miningField);
        regressionModel.setMiningSchema(miningSchema);
        PMML pmml = new PMML();
        pmml.setDataDictionary(dataDictionary);
        pmml.addModels(regressionModel);
        final CommonCompilationDTO<RegressionModel> source =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       regressionModel,
                                                                       new HasClassLoaderMock());
        final RegressionCompilationDTO compilationDTO =
                RegressionCompilationDTO.fromCompilationDTORegressionTablesAndNormalizationMethod(source,
                                                                                                  regressionModel.getRegressionTables(),
                                                                                                  regressionModel.getNormalizationMethod());
        Map<String, KiePMMLTableSourceCategory> retrieved =
                KiePMMLRegressionTableClassificationFactory.getRegressionTables(compilationDTO);
        assertNotNull(retrieved);
        assertEquals(3, retrieved.size());
        retrieved.values().forEach(kiePMMLTableSourceCategory -> commonValidateKiePMMLRegressionTable(kiePMMLTableSourceCategory.getSource()));
    }

    @Test
    public void getRegressionTable() {
        OutputField outputFieldCat = getOutputField("CAT-1", ResultFeature.PROBABILITY, "CatPred-1");
        OutputField outputFieldNum = getOutputField("NUM-1", ResultFeature.PROBABILITY, "NumPred-0");
        OutputField outputFieldPrev = getOutputField("PREV", ResultFeature.PREDICTED_VALUE, null);

        String targetField = "targetField";
        DataField dataField = new DataField();
        dataField.setName(FieldName.create(targetField));
        dataField.setOpType(OpType.CATEGORICAL);
        DataDictionary dataDictionary = new DataDictionary();
        dataDictionary.addDataFields(dataField);
        RegressionModel regressionModel = new RegressionModel();
        regressionModel.setNormalizationMethod(RegressionModel.NormalizationMethod.CAUCHIT);
        regressionModel.setModelName(getGeneratedClassName("RegressionModel"));
        Output output = new Output();
        output.addOutputFields(outputFieldCat, outputFieldNum, outputFieldPrev);
        regressionModel.setOutput(output);
        MiningField miningField = new MiningField();
        miningField.setUsageType(MiningField.UsageType.TARGET);
        miningField.setName(dataField.getName());
        MiningSchema miningSchema = new MiningSchema();
        miningSchema.addMiningFields(miningField);
        regressionModel.setMiningSchema(miningSchema);
        PMML pmml = new PMML();
        pmml.setDataDictionary(dataDictionary);
        pmml.addModels(regressionModel);
        final CommonCompilationDTO<RegressionModel> source =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       regressionModel,
                                                                       new HasClassLoaderMock());
        final RegressionCompilationDTO compilationDTO =
                RegressionCompilationDTO.fromCompilationDTORegressionTablesAndNormalizationMethod(source,
                                                                                                  new ArrayList<>(),
                                                                                                  regressionModel.getNormalizationMethod());

        LinkedHashMap<String, KiePMMLTableSourceCategory> toReturn = new LinkedHashMap<>();
        Map.Entry<String, String> retrieved =
                KiePMMLRegressionTableClassificationFactory.getRegressionTable(compilationDTO,
                                                                               toReturn);
        assertNotNull(retrieved);
    }

    @Test
    public void setConstructor() {
        ConstructorDeclaration constructorDeclaration = modelTemplate.getDefaultConstructor().get();
        SimpleName generatedClassName = new SimpleName("GeneratedClassName");
        String targetField = "targetField";
        REGRESSION_NORMALIZATION_METHOD regressionNormalizationMethod = REGRESSION_NORMALIZATION_METHOD.CAUCHIT;
        OP_TYPE opType = OP_TYPE.CATEGORICAL;

        DataField dataField = new DataField();
        dataField.setName(FieldName.create(targetField));
        dataField.setOpType(OpType.CATEGORICAL);
        DataDictionary dataDictionary = new DataDictionary();
        dataDictionary.addDataFields(dataField);
        RegressionModel regressionModel = new RegressionModel();
        regressionModel.setNormalizationMethod(RegressionModel.NormalizationMethod.CAUCHIT);
        regressionModel.setModelName(getGeneratedClassName("RegressionModel"));
        MiningField miningField = new MiningField();
        miningField.setUsageType(MiningField.UsageType.TARGET);
        miningField.setName(dataField.getName());
        MiningSchema miningSchema = new MiningSchema();
        miningSchema.addMiningFields(miningField);
        regressionModel.setMiningSchema(miningSchema);
        PMML pmml = new PMML();
        pmml.setDataDictionary(dataDictionary);
        pmml.addModels(regressionModel);

        final CommonCompilationDTO<RegressionModel> source =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       regressionModel,
                                                                       new HasClassLoaderMock());
        final RegressionCompilationDTO compilationDTO =
                RegressionCompilationDTO.fromCompilationDTORegressionTablesAndNormalizationMethod(source,
                                                                                                  new ArrayList<>(),
                                                                                                  regressionModel.getNormalizationMethod());

        KiePMMLRegressionTableClassificationFactory.setConstructor(compilationDTO,
                                                                   constructorDeclaration,
                                                                   generatedClassName);
        Map<Integer, Expression> superInvocationExpressionsMap = new HashMap<>();
        Map<String, Expression> assignExpressionMap = new HashMap<>();
        assignExpressionMap.put("targetField", new StringLiteralExpr(targetField));
        assignExpressionMap.put("regressionNormalizationMethod",
                                new NameExpr(regressionNormalizationMethod.getClass().getSimpleName() + "." + regressionNormalizationMethod.name()));
        assignExpressionMap.put("opType", new NameExpr(opType.getClass().getSimpleName() + "." + opType.name()));
        assertTrue(commonEvaluateConstructor(constructorDeclaration, generatedClassName.asString(),
                                             superInvocationExpressionsMap, assignExpressionMap));
    }

    @Test
    public void addMapPopulation() {
        final BlockStmt body = new BlockStmt();
        final LinkedHashMap<String, KiePMMLTableSourceCategory> regressionTablesMap = new LinkedHashMap<>();
        IntStream.range(0, 3).forEach(index ->
                                              regressionTablesMap.put("KEY" + index,
                                                                      new KiePMMLTableSourceCategory("SOURCE-" + index, "CATEGORY-" + index)));
        KiePMMLRegressionTableClassificationFactory.addMapPopulation(body, regressionTablesMap);
        NodeList<Statement> retrieved = body.getStatements();
        assertEquals(regressionTablesMap.size(), retrieved.size());
        retrieved.forEach(statement -> {
            assertTrue(statement instanceof ExpressionStmt);
            assertTrue(((ExpressionStmt) statement).getExpression() instanceof MethodCallExpr);
            MethodCallExpr methodCallExpr = (MethodCallExpr) ((ExpressionStmt) statement).getExpression();
            assertEquals("categoryTableMap", methodCallExpr.getScope().get().asNameExpr().toString());
            assertEquals("put", methodCallExpr.getName().asString());
        });
        final List<MethodCallExpr> methodCallExprs = retrieved.stream()
                .map(statement -> (MethodCallExpr) ((ExpressionStmt) statement).getExpression())
                .collect(Collectors.toList());
        IntStream.range(0, 3).forEach(index -> {
            String key = "KEY" + index;
            KiePMMLTableSourceCategory kiePMMLTableSourceCategory = regressionTablesMap.get(key);
            MethodCallExpr methodCallExpr = methodCallExprs.get(index);
            StringLiteralExpr stringLiteralExpr = (StringLiteralExpr) methodCallExpr.getArguments().get(0);
            assertEquals(kiePMMLTableSourceCategory.getCategory(), stringLiteralExpr.getValue());
            ObjectCreationExpr objectCreationExpr = (ObjectCreationExpr) methodCallExpr.getArguments().get(1);
            assertEquals(key, objectCreationExpr.getTypeAsString());
        });
    }

    @Test
    public void populateGetProbabilityMapMethodSupported() {
        SUPPORTED_NORMALIZATION_METHODS.forEach(normalizationMethod -> {
            KiePMMLRegressionTableClassificationFactory.populateGetProbabilityMapMethod(normalizationMethod,
                                                                                        false,
                                                                                        modelTemplate);
            MethodDeclaration methodDeclaration =
                    modelTemplate.getMethodsByName("getProbabilityMap").get(0);
            BlockStmt body = methodDeclaration.getBody().get();
            assertNotNull(body.getStatements());
            assertFalse(body.getStatements().isEmpty());
        });
    }

    @Test
    public void populateGetProbabilityMapMethodUnsupported() {
        UNSUPPORTED_NORMALIZATION_METHODS.forEach(normalizationMethod -> {
            try {
                KiePMMLRegressionTableClassificationFactory.populateGetProbabilityMapMethod(normalizationMethod,
                                                                                            false,
                                                                                            modelTemplate);
                fail("Expecting KiePMMLInternalException with normalizationMethod " + normalizationMethod);
            } catch (Exception e) {
                assertTrue(e instanceof KiePMMLInternalException);
            }
        });
    }

    @Test
    public void populateIsBinaryMethod() {
        KiePMMLRegressionTableClassificationFactory.populateIsBinaryMethod(false, modelTemplate);
        commonEvaluateIsBinaryMethod(modelTemplate, false);
        KiePMMLRegressionTableClassificationFactory.populateIsBinaryMethod(true, modelTemplate);
        commonEvaluateIsBinaryMethod(modelTemplate, true);
    }

    private void commonEvaluateIsBinaryMethod(final ClassOrInterfaceDeclaration tableTemplate, final boolean expected) {
        final MethodDeclaration methodDeclaration = tableTemplate.getMethodsByName("isBinary").get(0);
        final BlockStmt body = methodDeclaration.getBody().get();
        NodeList<Statement> retrieved = body.getStatements();
        assertEquals(1, retrieved.size());
        assertTrue(retrieved.get(0) instanceof ReturnStmt);
        ReturnStmt returnStmt = (ReturnStmt) retrieved.get(0);
        assertTrue(returnStmt.getExpression().isPresent() && returnStmt.getExpression().get() instanceof BooleanLiteralExpr);
        BooleanLiteralExpr booleanLiteralExpr = (BooleanLiteralExpr) returnStmt.getExpression().get();
        assertEquals(expected, booleanLiteralExpr.getValue());
    }

    private OutputField getOutputField(String name, ResultFeature resultFeature, String targetField) {
        OutputField toReturn = new OutputField();
        toReturn.setName(FieldName.create(name));
        toReturn.setResultFeature(resultFeature);
        if (targetField != null) {
            toReturn.setTargetField(FieldName.create(targetField));
        }
        return toReturn;
    }
}