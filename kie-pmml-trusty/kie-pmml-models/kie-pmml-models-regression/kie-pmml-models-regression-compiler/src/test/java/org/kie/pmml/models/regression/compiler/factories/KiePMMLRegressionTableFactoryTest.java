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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import org.assertj.core.data.Offset;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.MiningField;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.OpType;
import org.dmg.pmml.PMML;
import org.dmg.pmml.regression.CategoricalPredictor;
import org.dmg.pmml.regression.NumericPredictor;
import org.dmg.pmml.regression.PredictorTerm;
import org.dmg.pmml.regression.RegressionModel;
import org.dmg.pmml.regression.RegressionTable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.RESULT_FEATURE;
import org.kie.pmml.api.iinterfaces.SerializableFunction;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils;
import org.kie.pmml.compiler.commons.mocks.PMMLCompilationContextMock;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.regression.compiler.dto.RegressionCompilationDTO;
import org.kie.pmml.models.regression.model.KiePMMLRegressionTable;
import org.kie.pmml.models.regression.model.tuples.KiePMMLTableSourceCategory;

import static java.util.stream.Collectors.groupingBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getGeneratedClassName;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedVariableName;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilation;
import static org.kie.pmml.compiler.commons.testutils.CodegenTestUtils.commonValidateCompilationWithImports;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionTableFactory.GETKIEPMML_TABLE;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionTableFactory.KIE_PMML_REGRESSION_TABLE_TEMPLATE;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionTableFactory.KIE_PMML_REGRESSION_TABLE_TEMPLATE_JAVA;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionTableFactory.SUPPORTED_NORMALIZATION_METHODS;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionTableFactory.UNSUPPORTED_NORMALIZATION_METHODS;
import static org.drools.util.FileUtils.getFileContent;

public class KiePMMLRegressionTableFactoryTest extends AbstractKiePMMLRegressionTableRegressionFactoryTest {

    private static final String PACKAGE_NAME = "packagename";
    private static final String TEST_01_SOURCE = "KiePMMLRegressionTableFactoryTest_01.txt";
    private static final String TEST_02_SOURCE = "KiePMMLRegressionTableFactoryTest_02.txt";
    private static final String TEST_03_SOURCE = "KiePMMLRegressionTableFactoryTest_03.txt";
    private static final String TEST_04_SOURCE = "KiePMMLRegressionTableFactoryTest_04.txt";
    private static final String TEST_05_SOURCE = "KiePMMLRegressionTableFactoryTest_05.txt";
    private static final String TEST_06_SOURCE = "KiePMMLRegressionTableFactoryTest_06.txt";
    private static final String TEST_07_SOURCE = "KiePMMLRegressionTableFactoryTest_07.txt";

    private static CompilationUnit COMPILATION_UNIT;
    private static ClassOrInterfaceDeclaration MODEL_TEMPLATE;
    private static MethodDeclaration STATIC_GETTER_METHOD;

    @BeforeAll
    public static void setup() {
        COMPILATION_UNIT = getFromFileName(KIE_PMML_REGRESSION_TABLE_TEMPLATE_JAVA);
        MODEL_TEMPLATE = COMPILATION_UNIT.getClassByName(KIE_PMML_REGRESSION_TABLE_TEMPLATE).get();
        STATIC_GETTER_METHOD = MODEL_TEMPLATE.getMethodsByName(GETKIEPMML_TABLE).get(0);
    }

    @Test
    void getRegressionTables() {
        regressionTable = getRegressionTable(3.5, "professional");
        RegressionTable regressionTable2 = getRegressionTable(3.9, "hobby");
        RegressionModel regressionModel = new RegressionModel();
        regressionModel.setNormalizationMethod(RegressionModel.NormalizationMethod.CAUCHIT);
        regressionModel.addRegressionTables(regressionTable, regressionTable2);
        regressionModel.setModelName(getGeneratedClassName("RegressionModel"));
        String targetField = "targetField";
        DataField dataField = new DataField();
        dataField.setName(targetField);
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
                                                                       new PMMLCompilationContextMock(),
                                                                       "FILENAME");
        final RegressionCompilationDTO compilationDTO =
                RegressionCompilationDTO.fromCompilationDTORegressionTablesAndNormalizationMethod(source,
                                                                                                  regressionModel.getRegressionTables(),
                                                                                                  regressionModel.getNormalizationMethod());

        Map<String, KiePMMLRegressionTable> retrieved =
                KiePMMLRegressionTableFactory.getRegressionTables(compilationDTO);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSameSizeAs(regressionModel.getRegressionTables());
        regressionModel.getRegressionTables().forEach(regrTabl -> {
            assertThat(retrieved).containsKey(regrTabl.getTargetCategory().toString());
            commonEvaluateRegressionTable(retrieved.get(regrTabl.getTargetCategory().toString()), regrTabl);
        });
    }

    @Test
    void getRegressionTable() {
        regressionTable = getRegressionTable(3.5, "professional");
        RegressionModel regressionModel = new RegressionModel();
        regressionModel.setNormalizationMethod(RegressionModel.NormalizationMethod.CAUCHIT);
        regressionModel.addRegressionTables(regressionTable);
        regressionModel.setModelName(getGeneratedClassName("RegressionModel"));
        String targetField = "targetField";
        DataField dataField = new DataField();
        dataField.setName(targetField);
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
                                                                       new PMMLCompilationContextMock(),
                                                                       "FILENAME");
        final RegressionCompilationDTO compilationDTO =
                RegressionCompilationDTO.fromCompilationDTORegressionTablesAndNormalizationMethod(source,
                                                                                                  new ArrayList<>(),
                                                                                                  regressionModel.getNormalizationMethod());
        KiePMMLRegressionTable retrieved = KiePMMLRegressionTableFactory.getRegressionTable(regressionTable,
                                                                                            compilationDTO);
        assertThat(retrieved).isNotNull();
        commonEvaluateRegressionTable(retrieved, regressionTable);
    }

    @Test
    void getRegressionTableBuilders() {
        regressionTable = getRegressionTable(3.5, "professional");
        RegressionModel regressionModel = new RegressionModel();
        regressionModel.setNormalizationMethod(RegressionModel.NormalizationMethod.CAUCHIT);
        regressionModel.addRegressionTables(regressionTable);
        regressionModel.setModelName(getGeneratedClassName("RegressionModel"));
        String targetField = "targetField";
        DataField dataField = new DataField();
        dataField.setName(targetField);
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
                                                                       new PMMLCompilationContextMock(),
                                                                       "FILENAME");
        final RegressionCompilationDTO compilationDTO =
                RegressionCompilationDTO.fromCompilationDTORegressionTablesAndNormalizationMethod(source,
                                                                                                  new ArrayList<>(),
                                                                                                  regressionModel.getNormalizationMethod());

        Map<String, KiePMMLTableSourceCategory> retrieved =
                KiePMMLRegressionTableFactory.getRegressionTableBuilders(compilationDTO);
        assertThat(retrieved).isNotNull();
        retrieved.values().forEach(kiePMMLTableSourceCategory -> commonValidateKiePMMLRegressionTable(kiePMMLTableSourceCategory.getSource()));
    }

    @Test
    void getRegressionTableBuilder() {
        regressionTable = getRegressionTable(3.5, "professional");
        RegressionModel regressionModel = new RegressionModel();
        regressionModel.setNormalizationMethod(RegressionModel.NormalizationMethod.CAUCHIT);
        regressionModel.addRegressionTables(regressionTable);
        regressionModel.setModelName(getGeneratedClassName("RegressionModel"));
        String targetField = "targetField";
        DataField dataField = new DataField();
        dataField.setName(targetField);
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
                                                                       new PMMLCompilationContextMock(),
                                                                       "FILENAME");
        final RegressionCompilationDTO compilationDTO =
                RegressionCompilationDTO.fromCompilationDTORegressionTablesAndNormalizationMethod(source,
                                                                                                  new ArrayList<>(),
                                                                                                  regressionModel.getNormalizationMethod());
        Map.Entry<String, String> retrieved = KiePMMLRegressionTableFactory.getRegressionTableBuilder(regressionTable
                , compilationDTO);
        assertThat(retrieved).isNotNull();
        Map<String, String> sources = new HashMap<>();
        sources.put(retrieved.getKey(), retrieved.getValue());
        commonValidateCompilation(sources);
    }

    @Test
    void getNumericPredictorsMap() {
        final List<NumericPredictor> numericPredictors = IntStream.range(0, 3).mapToObj(index -> {
            String predictorName = "predictorName-" + index;
            double coefficient = 1.23 * index;
            return PMMLModelTestUtils.getNumericPredictor(predictorName, index, coefficient);
        }).collect(Collectors.toList());
        Map<String, SerializableFunction<Double, Double>> retrieved =
                KiePMMLRegressionTableFactory.getNumericPredictorsMap(numericPredictors);
        assertThat(retrieved).hasSameSizeAs(numericPredictors);
    }

    @Test
    void getNumericPredictorEntryWithExponent() {
        String predictorName = "predictorName";
        int exponent = 2;
        double coefficient = 1.23;
        NumericPredictor numericPredictor = PMMLModelTestUtils.getNumericPredictor(predictorName, exponent,
                coefficient);
        SerializableFunction<Double, Double> retrieved =
                KiePMMLRegressionTableFactory.getNumericPredictorEntry(numericPredictor);
        assertThat(retrieved).isNotNull();
    }

    @Test
    void getNumericPredictorEntryWithoutExponent() {
        String predictorName = "predictorName";
        int exponent = 1;
        double coefficient = 1.23;
        NumericPredictor numericPredictor = PMMLModelTestUtils.getNumericPredictor(predictorName, exponent,
                coefficient);
        SerializableFunction<Double, Double> retrieved =
                KiePMMLRegressionTableFactory.getNumericPredictorEntry(numericPredictor);
        assertThat(retrieved).isNotNull();
    }

    @Test
    void getCategoricalPredictorsMap() {
        final List<CategoricalPredictor> categoricalPredictors = IntStream.range(0, 3).mapToObj(index ->
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
        Map<String, SerializableFunction<String, Double>> retrieved =
                KiePMMLRegressionTableFactory.getCategoricalPredictorsMap(categoricalPredictors);
        final Map<String, List<CategoricalPredictor>> groupedCollectors = categoricalPredictors.stream()
                .collect(groupingBy(categoricalPredictor ->categoricalPredictor.getField()));
        assertThat(retrieved).hasSameSizeAs(groupedCollectors);
        groupedCollectors.keySet().forEach(predictName -> assertThat(retrieved).containsKey(predictName));
    }

    @Test
    void getGroupedCategoricalPredictorMap() {
        final List<CategoricalPredictor> categoricalPredictors = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String predictorName = "predictorName-" + i;
            double coefficient = 1.23 * i;
            categoricalPredictors.add(PMMLModelTestUtils.getCategoricalPredictor(predictorName, i, coefficient));
        }
        Map<String, Double> retrieved =
                KiePMMLRegressionTableFactory.getGroupedCategoricalPredictorMap(categoricalPredictors);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSameSizeAs(categoricalPredictors);
        categoricalPredictors.forEach(categoricalPredictor ->
        {
            String key = categoricalPredictor.getValue().toString();
            assertThat(retrieved).containsKey(key);
            assertThat(retrieved.get(key)).isCloseTo(categoricalPredictor.getCoefficient().doubleValue(), Offset.offset(0.0));
        });
    }

    @Test
    void getPredictorTermsMap() {
        final List<PredictorTerm> predictorTerms = IntStream.range(0, 3).mapToObj(index -> {
            String predictorName = "predictorName-" + index;
            double coefficient = 1.23 * index;
            String fieldRef = "fieldRef-" + index;
            return PMMLModelTestUtils.getPredictorTerm(predictorName, coefficient,
                    Collections.singletonList(fieldRef));
        }).collect(Collectors.toList());
        Map<String, SerializableFunction<Map<String, Object>, Double>> retrieved =
                KiePMMLRegressionTableFactory.getPredictorTermsMap(predictorTerms);
        assertThat(retrieved).hasSameSizeAs(predictorTerms);
        IntStream.range(0, predictorTerms.size()).forEach(index -> {
            PredictorTerm predictorTerm = predictorTerms.get(index);
            assertThat(retrieved).containsKey(predictorTerm.getName());
        });
    }

    @Test
    void getPredictorTermSerializableFunction() {
        String predictorName = "predictorName";
        double coefficient = 23.12;
        String fieldRef = "fieldRef";
        PredictorTerm predictorTerm = PMMLModelTestUtils.getPredictorTerm(predictorName, coefficient,
                Collections.singletonList(fieldRef));
        SerializableFunction<Map<String, Object>, Double> retrieved =
                KiePMMLRegressionTableFactory.getPredictorTermSerializableFunction(predictorTerm);
        assertThat(retrieved).isNotNull();
    }

    @Test
    void getResultUpdaterUnsupportedFunction() {
        UNSUPPORTED_NORMALIZATION_METHODS.forEach(normalizationMethod ->
                assertThat(KiePMMLRegressionTableFactory.getResultUpdaterFunction(normalizationMethod)).isNull());
    }

    @Test
    void getResultUpdaterSupportedFunction() {
        SUPPORTED_NORMALIZATION_METHODS.forEach(normalizationMethod ->
                assertThat(KiePMMLRegressionTableFactory.getResultUpdaterFunction(normalizationMethod)).isNotNull());
    }

    @Test
    void setStaticGetter() throws IOException {
        regressionTable = getRegressionTable(3.5, "professional");
        RegressionModel regressionModel = new RegressionModel();
        regressionModel.setNormalizationMethod(RegressionModel.NormalizationMethod.CAUCHIT);
        regressionModel.addRegressionTables(regressionTable);
        regressionModel.setModelName(getGeneratedClassName("RegressionModel"));
        String targetField = "targetField";
        DataField dataField = new DataField();
        dataField.setName(targetField);
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
        String variableName = "variableName";
        final CommonCompilationDTO<RegressionModel> source =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       regressionModel,
                                                                       new PMMLCompilationContextMock(),
                                                                       "FILENAME");
        final RegressionCompilationDTO compilationDTO =
                RegressionCompilationDTO.fromCompilationDTORegressionTablesAndNormalizationMethod(source,
                                                                                                  new ArrayList<>(),
                                                                                                  regressionModel.getNormalizationMethod());

        final MethodDeclaration staticGetterMethod = STATIC_GETTER_METHOD.clone();
        KiePMMLRegressionTableFactory.setStaticGetter(regressionTable,
                compilationDTO,
                staticGetterMethod,
                variableName);
        String text = getFileContent(TEST_06_SOURCE);
        MethodDeclaration expected = JavaParserUtils.parseMethod(text);
        assertThat(staticGetterMethod.toString()).isEqualTo(expected.toString());
        assertThat(JavaParserUtils.equalsNode(expected, staticGetterMethod)).isTrue();
        List<Class<?>> imports = Arrays.asList(AtomicReference.class,
                Collections.class,
                Arrays.class,
                List.class,
                Map.class,
                KiePMMLRegressionTable.class,
                SerializableFunction.class);
        commonValidateCompilationWithImports(staticGetterMethod, imports);
    }

    @Test
    void getResultUpdaterExpressionWithSupportedMethods() {
        SUPPORTED_NORMALIZATION_METHODS.forEach(normalizationMethod -> {
            Expression retrieved =
                    KiePMMLRegressionTableFactory.getResultUpdaterExpression(normalizationMethod);
            try {
                String text = getFileContent(TEST_03_SOURCE);
                Expression expected = JavaParserUtils.parseExpression(String.format(text,
                        normalizationMethod.name()));
                assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
            } catch (IOException e) {
                fail(e.getMessage());
            }
        });
    }

    @Test
    void getResultUpdaterExpression() {
        UNSUPPORTED_NORMALIZATION_METHODS.forEach(normalizationMethod -> {
            Expression retrieved =
                    KiePMMLRegressionTableFactory.getResultUpdaterExpression(normalizationMethod);
            assertThat(retrieved).isInstanceOf(NullLiteralExpr.class);
        });
    }

    @Test
    void getResultUpdaterSupportedExpression() throws IOException {
        MethodReferenceExpr retrieved =
                KiePMMLRegressionTableFactory.getResultUpdaterSupportedExpression(RegressionModel.NormalizationMethod.CAUCHIT);
        String text = getFileContent(TEST_03_SOURCE);
        Expression expected = JavaParserUtils.parseExpression(String.format(text,
                RegressionModel.NormalizationMethod.CAUCHIT.name()));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
    }

    @Test
    void getNumericPredictorsExpressions() {
        final List<NumericPredictor> numericPredictors = IntStream.range(0, 3).mapToObj(index -> {
            String predictorName = "predictorName-" + index;
            double coefficient = 1.23 * index;
            return PMMLModelTestUtils.getNumericPredictor(predictorName, index, coefficient);
        }).collect(Collectors.toList());
        Map<String, Expression> retrieved =
                KiePMMLRegressionTableFactory.getNumericPredictorsExpressions(numericPredictors);
        assertThat(retrieved).hasSameSizeAs(numericPredictors);
    }

    @Test
    void getNumericPredictorExpressionWithExponent() throws IOException {
        String predictorName = "predictorName";
        int exponent = 2;
        double coefficient = 1.23;
        NumericPredictor numericPredictor = PMMLModelTestUtils.getNumericPredictor(predictorName, exponent,
                coefficient);
        CastExpr retrieved = KiePMMLRegressionTableFactory.getNumericPredictorExpression(numericPredictor);
        String text = getFileContent(TEST_01_SOURCE);
        Expression expected = JavaParserUtils.parseExpression(String.format(text, coefficient, exponent));
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    void getNumericPredictorExpressionWithoutExponent() throws IOException {
        String predictorName = "predictorName";
        int exponent = 1;
        double coefficient = 1.23;
        NumericPredictor numericPredictor = PMMLModelTestUtils.getNumericPredictor(predictorName, exponent,
                coefficient);
        CastExpr retrieved = KiePMMLRegressionTableFactory.getNumericPredictorExpression(numericPredictor);
        String text = getFileContent(TEST_02_SOURCE);
        Expression expected = JavaParserUtils.parseExpression(String.format(text, coefficient));
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    void getCategoricalPredictorsExpressions() {
        final List<CategoricalPredictor> categoricalPredictors = IntStream.range(0, 3).mapToObj(index ->
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
        final BlockStmt body = new BlockStmt();
        Map<String, Expression> retrieved =
                KiePMMLRegressionTableFactory.getCategoricalPredictorsExpressions(categoricalPredictors,
                        body,
                        "variableName");
        assertThat(retrieved).hasSize(3);
        final Map<String, List<CategoricalPredictor>> groupedCollectors = categoricalPredictors.stream()
                .collect(groupingBy(categoricalPredictor ->categoricalPredictor.getField()));

        groupedCollectors.values().forEach(categoricalPredictors12 -> commonEvaluateCategoryPredictors(body,
                categoricalPredictors12, "variableName"));
    }

    @Test
    void populateWithGroupedCategoricalPredictorMap() throws IOException {
        final List<CategoricalPredictor> categoricalPredictors = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String predictorName = "predictorName-" + i;
            double coefficient = 1.23 * i;
            categoricalPredictors.add(PMMLModelTestUtils.getCategoricalPredictor(predictorName, i, coefficient));
        }
        final BlockStmt toPopulate = new BlockStmt();
        final String categoricalPredictorMapName = "categoricalPredictorMapName";
        KiePMMLRegressionTableFactory.populateWithGroupedCategoricalPredictorMap(categoricalPredictors,
                toPopulate,
                categoricalPredictorMapName);
        String text = getFileContent(TEST_04_SOURCE);
        BlockStmt expected = JavaParserUtils.parseBlock(String.format(text,
                categoricalPredictorMapName,
                categoricalPredictors.get(0).getValue(),
                categoricalPredictors.get(0).getCoefficient(),
                categoricalPredictors.get(1).getValue(),
                categoricalPredictors.get(1).getCoefficient(),
                categoricalPredictors.get(2).getValue(),
                categoricalPredictors.get(2).getCoefficient()));
        assertThat(JavaParserUtils.equalsNode(expected, toPopulate)).isTrue();
    }

    @Test
    void getCategoricalPredictorExpression() throws IOException {
        final String categoricalPredictorMapName = "categoricalPredictorMapName";
        CastExpr retrieved =
                KiePMMLRegressionTableFactory.getCategoricalPredictorExpression(categoricalPredictorMapName);
        String text = getFileContent(TEST_05_SOURCE);
        Expression expected = JavaParserUtils.parseExpression(String.format(text, categoricalPredictorMapName));
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    void getPredictorTermFunctions() {
        final List<PredictorTerm> predictorTerms = IntStream.range(0, 3).mapToObj(index -> {
            String predictorName = "predictorName-" + index;
            double coefficient = 1.23 * index;
            String fieldRef = "fieldRef-" + index;
            return PMMLModelTestUtils.getPredictorTerm(predictorName, coefficient,
                    Collections.singletonList(fieldRef));
        }).collect(Collectors.toList());
        Map<String, Expression> retrieved =
                KiePMMLRegressionTableFactory.getPredictorTermFunctions(predictorTerms);
        assertThat(retrieved).hasSameSizeAs(predictorTerms);
        IntStream.range(0, predictorTerms.size()).forEach(index -> {
            PredictorTerm predictorTerm = predictorTerms.get(index);
            assertThat(retrieved).containsKey(predictorTerm.getName());
        });
    }

    @Test
    void getPredictorTermFunction() throws IOException {
        String predictorName = "predictorName";
        double coefficient = 23.12;
        String fieldRef = "fieldRef";
        PredictorTerm predictorTerm = PMMLModelTestUtils.getPredictorTerm(predictorName, coefficient,
                Collections.singletonList(fieldRef));
        LambdaExpr retrieved = KiePMMLRegressionTableFactory.getPredictorTermFunction(predictorTerm);

        String text = getFileContent(TEST_07_SOURCE);
        Expression expected = JavaParserUtils.parseExpression(String.format(text, fieldRef, coefficient));
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
    }

    private void commonEvaluateRegressionTable(KiePMMLRegressionTable retrieved, RegressionTable source) {
        Map<String, SerializableFunction<Double, Double>> numericFunctionMap = retrieved.getNumericFunctionMap();
        assertThat(numericFunctionMap).hasSameSizeAs(source.getNumericPredictors());
        source.getNumericPredictors().forEach(numericPredictor -> assertThat(numericFunctionMap).containsKey(numericPredictor.getField()));
        Map<String, SerializableFunction<String, Double>> categoricalFunctionMap =
                retrieved.getCategoricalFunctionMap();
        Map<String, List<CategoricalPredictor>> groupedCollectors = categoricalPredictors.stream()
                .collect(groupingBy(categoricalPredictor ->categoricalPredictor.getField()));
        assertThat(categoricalFunctionMap).hasSameSizeAs(groupedCollectors);
        groupedCollectors.keySet().forEach(categorical -> assertThat(categoricalFunctionMap).containsKey(categorical));
        Map<String, SerializableFunction<Map<String, Object>, Double>> predictorTermsFunctionMap =
                retrieved.getPredictorTermsFunctionMap();
        assertThat(predictorTermsFunctionMap).hasSameSizeAs(source.getPredictorTerms());
        source.getPredictorTerms().forEach(predictorTerm -> assertThat(predictorTermsFunctionMap).containsKey(predictorTerm.getName()));
    }

    private void commonEvaluateCategoryPredictors(final BlockStmt toVerify,
                                                  final List<CategoricalPredictor> categoricalPredictors,
                                                  final String variableName) {
        for (int i = 0; i < categoricalPredictors.size(); i++) {
            CategoricalPredictor categoricalPredictor = categoricalPredictors.get(i);
            String expectedVariableName =
                    getSanitizedVariableName(String.format("%sMap", variableName)) + "_" + i;
            assertThat(toVerify.getStatements()
                               .stream()
                               .anyMatch(statement -> {
                                   String expected = String.format(
                                           "%s.put(\"%s\", %s);",
                                           expectedVariableName,
                                           categoricalPredictor.getValue(),
                                           categoricalPredictor.getCoefficient());
                                   return statement instanceof ExpressionStmt &&
                                           ((ExpressionStmt) statement).getExpression() instanceof MethodCallExpr &&
                                           statement.toString().equals(expected);
                               })).isTrue();
        }
    }

    private KiePMMLOutputField getOutputField(String name, RESULT_FEATURE resultFeature, String targetField) {
        return KiePMMLOutputField.builder(name, Collections.emptyList())
                .withResultFeature(resultFeature)
                .withTargetField(targetField)
                .build();
    }
}