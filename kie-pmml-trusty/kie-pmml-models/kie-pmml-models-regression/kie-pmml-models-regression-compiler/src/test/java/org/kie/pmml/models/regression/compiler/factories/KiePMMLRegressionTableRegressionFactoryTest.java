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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.MiningField;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.OpType;
import org.dmg.pmml.PMML;
import org.dmg.pmml.regression.CategoricalPredictor;
import org.dmg.pmml.regression.NumericPredictor;
import org.dmg.pmml.regression.PredictorTerm;
import org.dmg.pmml.regression.RegressionModel;
import org.junit.Before;
import org.junit.Test;
import org.kie.pmml.api.enums.RESULT_FEATURE;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.exceptions.KiePMMLInternalException;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils;
import org.kie.pmml.compiler.commons.mocks.HasClassLoaderMock;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.regression.compiler.dto.RegressionCompilationDTO;
import org.kie.pmml.models.regression.model.tuples.KiePMMLTableSourceCategory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getGeneratedClassName;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonEvaluateConstructor;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionTableRegressionFactory.KIE_PMML_EVALUATE_METHOD_TEMPLATE;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionTableRegressionFactory.KIE_PMML_EVALUATE_METHOD_TEMPLATE_JAVA;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionTableRegressionFactory.KIE_PMML_REGRESSION_TABLE_REGRESSION_TEMPLATE;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionTableRegressionFactory.KIE_PMML_REGRESSION_TABLE_REGRESSION_TEMPLATE_JAVA;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionTableRegressionFactory.MAIN_CLASS_NOT_FOUND;
import static org.kie.test.util.filesystem.FileUtils.getFileContent;

public class KiePMMLRegressionTableRegressionFactoryTest extends AbstractKiePMMLRegressionTableRegressionFactoryTest {

    private static final String PACKAGE_NAME = "packagename";
    private static final String TEST_01_SOURCE = "KiePMMLRegressionTableRegressionFactoryTest_01.txt";
    private static final String TEST_02_SOURCE = "KiePMMLRegressionTableRegressionFactoryTest_02.txt";
    private static final String TEST_03_SOURCE = "KiePMMLRegressionTableRegressionFactoryTest_03.txt";
    private static final String TEST_04_SOURCE = "KiePMMLRegressionTableRegressionFactoryTest_04.txt";
    private static final String TEST_05_SOURCE = "KiePMMLRegressionTableRegressionFactoryTest_05.txt";
    private static final String TEST_06_SOURCE = "KiePMMLRegressionTableRegressionFactoryTest_06.txt";

    private final static List<RegressionModel.NormalizationMethod> SUPPORTED_NORMALIZATION_METHODS =
            Arrays.asList(RegressionModel.NormalizationMethod.SOFTMAX,
                          RegressionModel.NormalizationMethod.LOGIT,
                          RegressionModel.NormalizationMethod.EXP,
                          RegressionModel.NormalizationMethod.PROBIT,
                          RegressionModel.NormalizationMethod.CLOGLOG,
                          RegressionModel.NormalizationMethod.CAUCHIT,
                          RegressionModel.NormalizationMethod.NONE);
    private final static List<RegressionModel.NormalizationMethod> UNSUPPORTED_NORMALIZATION_METHODS =
            Arrays.asList(
                    RegressionModel.NormalizationMethod.SIMPLEMAX,
                    RegressionModel.NormalizationMethod.LOGLOG);
    private static CompilationUnit compilationUnit;
    private static ClassOrInterfaceDeclaration modelTemplate;

    @Before
    public void setup() {
        compilationUnit = getFromFileName(KIE_PMML_REGRESSION_TABLE_REGRESSION_TEMPLATE_JAVA);
        modelTemplate = compilationUnit.getClassByName(KIE_PMML_REGRESSION_TABLE_REGRESSION_TEMPLATE).get();
    }

    @Test
    public void getRegressionTableTest() {
        regressionTable = getRegressionTable(3.5, "professional");
        RegressionModel regressionModel = new RegressionModel();
        regressionModel.setNormalizationMethod(RegressionModel.NormalizationMethod.CAUCHIT);
        regressionModel.addRegressionTables(regressionTable);
        regressionModel.setModelName(getGeneratedClassName("RegressionModel"));
        String targetField = "targetField";
        DataField dataField = new DataField();
        dataField.setName(FieldName.create(targetField));
        dataField.setOpType(OpType.CATEGORICAL);
        DataDictionary dataDictionary = new DataDictionary();
        dataDictionary.addDataFields(dataField);
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

        Map<String, KiePMMLTableSourceCategory> retrieved =
                KiePMMLRegressionTableRegressionFactory.getRegressionTables(compilationDTO);
        assertNotNull(retrieved);
        retrieved.values().forEach(kiePMMLTableSourceCategory -> commonValidateKiePMMLRegressionTable(kiePMMLTableSourceCategory.getSource()));
    }

    @Test
    public void setConstructor() {
        regressionTable = getRegressionTable(3.5, "professional");
        ConstructorDeclaration constructorDeclaration = modelTemplate.getDefaultConstructor().get();
        SimpleName tableName = new SimpleName("TableName");
        String targetField = "targetField";
        KiePMMLRegressionTableRegressionFactory.setConstructor(regressionTable,
                                                               constructorDeclaration,
                                                               tableName,
                                                               targetField);
        Map<Integer, Expression> superInvocationExpressionsMap = new HashMap<>();
        Map<String, Expression> assignExpressionMap = new HashMap<>();
        assignExpressionMap.put("targetField", new StringLiteralExpr(targetField));
        assignExpressionMap.put("intercept", new DoubleLiteralExpr(String.valueOf(3.5)));
        assertTrue(commonEvaluateConstructor(constructorDeclaration, tableName.asString(),
                                             superInvocationExpressionsMap, assignExpressionMap));
    }

    @Test
    public void addNumericPredictorWithExponent() throws IOException {
        String predictorName = "predictorName";
        int exponent = 2;
        double coefficient = 1.23;
        NumericPredictor numericPredictor = PMMLModelTestUtils.getNumericPredictor(predictorName, exponent,
                                                                                   coefficient);
        int arity = 3;
        ClassOrInterfaceDeclaration tableTemplate = new ClassOrInterfaceDeclaration();
        MethodDeclaration retrieved = KiePMMLRegressionTableRegressionFactory.addNumericPredictor(numericPredictor,
                                                                                                  tableTemplate, arity);
        BlockStmt body = retrieved.getBody().get();
        String text = getFileContent(TEST_01_SOURCE);
        BlockStmt expected = JavaParserUtils.parseBlock(String.format(text, coefficient, exponent));
        assertTrue(JavaParserUtils.equalsNode(expected, body));
    }

    @Test
    public void addNumericPredictors() {
        final List<NumericPredictor> numericPredictors = IntStream.range(0, 3).mapToObj(index -> {
            String predictorName = "predictorName-" + index;
            double coefficient = 1.23 * index;
            return PMMLModelTestUtils.getNumericPredictor(predictorName, index, coefficient);
        }).collect(Collectors.toList());
        ClassOrInterfaceDeclaration tableTemplate = new ClassOrInterfaceDeclaration();
        Map<String, MethodDeclaration> retrieved =
                KiePMMLRegressionTableRegressionFactory.addNumericPredictors(numericPredictors, tableTemplate);
        assertEquals(numericPredictors.size(), retrieved.size());
    }

    @Test
    public void addNumericPredictorWithoutExponent() throws IOException {
        String predictorName = "predictorName";
        int exponent = 1;
        double coefficient = 1.23;
        NumericPredictor numericPredictor = PMMLModelTestUtils.getNumericPredictor(predictorName, exponent,
                                                                                   coefficient);
        int arity = 3;
        ClassOrInterfaceDeclaration tableTemplate = new ClassOrInterfaceDeclaration();
        MethodDeclaration retrieved = KiePMMLRegressionTableRegressionFactory.addNumericPredictor(numericPredictor,
                                                                                                  tableTemplate, arity);
        BlockStmt body = retrieved.getBody().get();
        String text = getFileContent(TEST_02_SOURCE);
        BlockStmt expected = JavaParserUtils.parseBlock(String.format(text, coefficient));
        assertTrue(JavaParserUtils.equalsNode(expected, body));
    }

    @Test
    public void getNumericPredictorWithExponentTemplate() throws IOException {
        String predictorName = "predictorName";
        int exponent = 2;
        double coefficient = 1.23;
        NumericPredictor numericPredictor = PMMLModelTestUtils.getNumericPredictor(predictorName, exponent,
                                                                                   coefficient);
        CompilationUnit templateEvaluate = getFromFileName(KIE_PMML_EVALUATE_METHOD_TEMPLATE_JAVA);
        CompilationUnit cloneEvaluate = templateEvaluate.clone();
        ClassOrInterfaceDeclaration evaluateTemplateClass =
                cloneEvaluate.getClassByName(KIE_PMML_EVALUATE_METHOD_TEMPLATE)
                        .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND));
        MethodDeclaration retrieved =
                KiePMMLRegressionTableRegressionFactory.getNumericPredictorWithExponentTemplate(numericPredictor,
                                                                                                evaluateTemplateClass);
        BlockStmt body = retrieved.getBody().get();
        String text = getFileContent(TEST_03_SOURCE);
        BlockStmt expected = JavaParserUtils.parseBlock(String.format(text, coefficient, exponent));
        assertTrue(JavaParserUtils.equalsNode(expected, body));
    }

    @Test
    public void getNumericPredictorWithoutExponentTemplate() throws IOException {
        String predictorName = "predictorName";
        int exponent = 2;
        double coefficient = 1.23;
        NumericPredictor numericPredictor = PMMLModelTestUtils.getNumericPredictor(predictorName, exponent,
                                                                                   coefficient);
        CompilationUnit templateEvaluate = getFromFileName(KIE_PMML_EVALUATE_METHOD_TEMPLATE_JAVA);
        CompilationUnit cloneEvaluate = templateEvaluate.clone();
        ClassOrInterfaceDeclaration evaluateTemplateClass =
                cloneEvaluate.getClassByName(KIE_PMML_EVALUATE_METHOD_TEMPLATE)
                        .orElseThrow(() -> new KiePMMLException(MAIN_CLASS_NOT_FOUND));
        MethodDeclaration retrieved =
                KiePMMLRegressionTableRegressionFactory.getNumericPredictorWithoutExponentTemplate(numericPredictor,
                                                                                                   evaluateTemplateClass);
        BlockStmt body = retrieved.getBody().get();
        String text = getFileContent(TEST_04_SOURCE);
        BlockStmt expected = JavaParserUtils.parseBlock(String.format(text, coefficient));
        assertTrue(JavaParserUtils.equalsNode(expected, body));
    }

    @Test
    public void addCategoricalPredictors() {
        List<CategoricalPredictor> categoricalPredictors = IntStream.range(0, 3).mapToObj(index ->
                                                                                                  IntStream.range(0,
                                                                                                                  3).mapToObj(i -> {
                                                                                                              String predictorName = "predictorName-" + index;
                                                                                                              double coefficient = 1.23 * i;
                                                                                                              return PMMLModelTestUtils.getCategoricalPredictor(predictorName, i, coefficient);
                                                                                                          })
                                                                                                          .collect(Collectors.toList())).reduce((categoricalPredictors1, categoricalPredictors2) -> {
            List<CategoricalPredictor> toReturn = new ArrayList<>();
            toReturn.addAll(categoricalPredictors1);
            toReturn.addAll(categoricalPredictors2);
            return toReturn;
        }).get();
        ClassOrInterfaceDeclaration tableTemplate = new ClassOrInterfaceDeclaration();
        Map<String, MethodDeclaration> retrieved =
                KiePMMLRegressionTableRegressionFactory.addCategoricalPredictors(categoricalPredictors, tableTemplate);
        assertEquals(3, retrieved.size());
    }

    @Test
    public void addGroupedCategoricalPredictor() throws IOException {
        final List<CategoricalPredictor> categoricalPredictors = IntStream.range(0, 3).mapToObj(index -> {
            String predictorName = "predictorName-" + index;
            double coefficient = 1.23 * index;
            return PMMLModelTestUtils.getCategoricalPredictor(predictorName, index, coefficient);
        }).collect(Collectors.toList());
        ClassOrInterfaceDeclaration tableTemplate = new ClassOrInterfaceDeclaration();
        int arity = 3;
        MethodDeclaration retrieved =
                KiePMMLRegressionTableRegressionFactory.addGroupedCategoricalPredictor(categoricalPredictors,
                                                                                       tableTemplate, arity);
        String expectedName = String.format("evaluateCategoricalPredictor%d", arity);
        assertEquals(expectedName, retrieved.getNameAsString());
        BlockStmt body = retrieved.getBody().get();
        String text = getFileContent(TEST_05_SOURCE);
        BlockStmt expected = JavaParserUtils.parseBlock(String.format(text,
                                                                      categoricalPredictors.get(0).getValue(),
                                                                      categoricalPredictors.get(0).getCoefficient(),
                                                                      categoricalPredictors.get(1).getValue(),
                                                                      categoricalPredictors.get(1).getCoefficient(),
                                                                      categoricalPredictors.get(2).getValue(),
                                                                      categoricalPredictors.get(2).getCoefficient()
        ));
        assertTrue(JavaParserUtils.equalsNode(expected, body));
    }

    @Test
    public void addPredictorTerms() {
        final List<PredictorTerm> predictorTerms = IntStream.range(0, 3).mapToObj(index -> {
            String predictorName = "predictorName-" + index;
            double coefficient = 1.23 * index;
            String fieldRef = "fieldRef-" + index;
            return PMMLModelTestUtils.getPredictorTerm(predictorName, coefficient,
                                                       Collections.singletonList(fieldRef));
        }).collect(Collectors.toList());
        ClassOrInterfaceDeclaration tableTemplate = new ClassOrInterfaceDeclaration();
        Map<String, MethodDeclaration> retrieved =
                KiePMMLRegressionTableRegressionFactory.addPredictorTerms(predictorTerms, tableTemplate);
        assertEquals(predictorTerms.size(), retrieved.size());
        IntStream.range(0, predictorTerms.size()).forEach(index -> {
            PredictorTerm predictorTerm = predictorTerms.get(index);
            assertTrue(retrieved.containsKey(predictorTerm.getName().getValue()));
            MethodDeclaration methodDeclaration = retrieved.get(predictorTerm.getName().getValue());
            String expected = String.format("evaluatePredictorTerm%d", index + 1);
            assertEquals(expected, methodDeclaration.getNameAsString());
        });
    }

    @Test
    public void addPredictorTerm() throws IOException {
        String predictorName = "predictorName";
        double coefficient = 23.12;
        String fieldRef = "fieldRef";
        int arity = 3;
        PredictorTerm predictorTerm = PMMLModelTestUtils.getPredictorTerm(predictorName, coefficient,
                                                                          Collections.singletonList(fieldRef));
        ClassOrInterfaceDeclaration tableTemplate = new ClassOrInterfaceDeclaration();
        MethodDeclaration retrieved = KiePMMLRegressionTableRegressionFactory.addPredictorTerm(predictorTerm,
                                                                                               tableTemplate, arity);
        String expectedName = String.format("evaluatePredictorTerm%d", arity);
        assertEquals(expectedName, retrieved.getNameAsString());
        BlockStmt body = retrieved.getBody().get();
        String text = getFileContent(TEST_06_SOURCE);
        BlockStmt expected = JavaParserUtils.parseBlock(String.format(text, fieldRef, coefficient));
        assertTrue(JavaParserUtils.equalsNode(expected, body));
    }

    @Test
    public void populateGetTargetCategoryTargetCategoryNull() {
        KiePMMLRegressionTableRegressionFactory.populateGetTargetCategory(modelTemplate, null);
        final Expression expectedExpression = new NameExpr("null");
        commonEvaluateGetTargetCategory(modelTemplate, expectedExpression);
    }

    @Test
    public void populateGetTargetCategoryTargetCategoryString() {
        KiePMMLRegressionTableRegressionFactory.populateGetTargetCategory(modelTemplate, "CATEGORY");
        final Expression expectedExpression = new StringLiteralExpr("CATEGORY");
        commonEvaluateGetTargetCategory(modelTemplate, expectedExpression);
    }

    @Test
    public void populateGetTargetCategoryTargetCategoryNoString() {
        KiePMMLRegressionTableRegressionFactory.populateGetTargetCategory(modelTemplate, 435);
        final Expression expectedExpression = new NameExpr("435");
        commonEvaluateGetTargetCategory(modelTemplate, expectedExpression);
    }

    @Test
    public void populateUpdateResultSupported() {
        SUPPORTED_NORMALIZATION_METHODS.forEach(normalizationMethod -> {
            KiePMMLRegressionTableRegressionFactory.populateUpdateResult(modelTemplate, normalizationMethod);
            MethodDeclaration methodDeclaration =
                    modelTemplate.getMethodsByName("updateResult").get(0);
            BlockStmt body = methodDeclaration.getBody().get();
            assertNotNull(body.getStatements());
            if (normalizationMethod.equals(RegressionModel.NormalizationMethod.NONE)) {
                assertTrue(body.getStatements().isEmpty());
            } else {
                assertFalse(body.getStatements().isEmpty());
            }
        });
    }

    @Test
    public void populateUpdateResultUnsupported() {
        UNSUPPORTED_NORMALIZATION_METHODS.forEach(normalizationMethod -> {
            try {
                KiePMMLRegressionTableRegressionFactory.populateUpdateResult(modelTemplate, normalizationMethod);
                fail("Expecting KiePMMLInternalException with normalizationMethod " + normalizationMethod);
            } catch (Exception e) {
                assertTrue(e instanceof KiePMMLInternalException);
            }
        });
    }

    @Test
    public void populateOutputFieldsMap() {
        final List<KiePMMLOutputField> outputFields = new ArrayList<>();
        KiePMMLOutputField predictedOutputField = getOutputField("KOF-TARGET", RESULT_FEATURE.PREDICTED_VALUE,
                                                                 "TARGET");
        outputFields.add(predictedOutputField);
        final List<KiePMMLOutputField> probabilityOutputFields = IntStream.range(0, 2)
                .mapToObj(index -> getOutputField("KOF-PROB-" + index, RESULT_FEATURE.PROBABILITY, "PROB-" + index))
                .collect(Collectors.toList());
        outputFields.addAll(probabilityOutputFields);
    }

    private void commonEvaluateGetTargetCategory(final ClassOrInterfaceDeclaration tableTemplate,
                                                 final Expression expectedExpression) {
        final MethodDeclaration methodDeclaration = tableTemplate.getMethodsByName("getTargetCategory").get(0);
        final BlockStmt body = methodDeclaration.getBody().get();
        final NodeList<Statement> retrieved = body.getStatements();
        assertEquals(1, retrieved.size());
        assertTrue(retrieved.get(0) instanceof ReturnStmt);
        ReturnStmt returnStmt = (ReturnStmt) retrieved.get(0);
        assertEquals(expectedExpression, returnStmt.getExpression().get());
    }

    private KiePMMLOutputField getOutputField(String name, RESULT_FEATURE resultFeature, String targetField) {
        return KiePMMLOutputField.builder(name, Collections.emptyList())
                .withResultFeature(resultFeature)
                .withTargetField(targetField)
                .build();
    }
}