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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.DataType;
import org.dmg.pmml.MiningField;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.OpType;
import org.dmg.pmml.PMML;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.regression.CategoricalPredictor;
import org.dmg.pmml.regression.NumericPredictor;
import org.dmg.pmml.regression.PredictorTerm;
import org.dmg.pmml.regression.RegressionModel;
import org.dmg.pmml.regression.RegressionTable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.MINING_FUNCTION;
import org.kie.pmml.api.enums.OP_TYPE;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.iinterfaces.SerializableFunction;
import org.kie.pmml.compiler.api.dto.CommonCompilationDTO;
import org.kie.pmml.compiler.api.dto.CompilationDTO;
import org.kie.pmml.compiler.commons.mocks.PMMLCompilationContextMock;
import org.kie.pmml.compiler.commons.utils.JavaParserUtils;
import org.kie.pmml.models.regression.compiler.dto.RegressionCompilationDTO;
import org.kie.pmml.models.regression.model.AbstractKiePMMLTable;
import org.kie.pmml.models.regression.model.KiePMMLClassificationTable;
import org.kie.pmml.models.regression.model.KiePMMLRegressionModel;
import org.kie.pmml.models.regression.model.KiePMMLRegressionTable;
import org.kie.pmml.models.regression.model.enums.REGRESSION_NORMALIZATION_METHOD;
import org.kie.pmml.models.regression.model.tuples.KiePMMLTableSourceCategory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.Constants.GET_MODEL;
import static org.kie.pmml.commons.Constants.PACKAGE_NAME;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getCategoricalPredictor;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getDataDictionary;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getDataField;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getMiningField;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getMiningSchema;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getNumericPredictor;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getPredictorTerm;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRegressionModel;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRegressionTable;
import static org.kie.pmml.compiler.commons.utils.JavaParserUtils.getFromFileName;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionModelFactory.KIE_PMML_REGRESSION_MODEL_TEMPLATE;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionModelFactory.KIE_PMML_REGRESSION_MODEL_TEMPLATE_JAVA;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionTableFactory.GETKIEPMML_TABLE;
import static org.drools.util.FileUtils.getFileContent;

public class KiePMMLRegressionModelFactoryTest {

    private static CompilationUnit COMPILATION_UNIT;
    private static ClassOrInterfaceDeclaration MODEL_TEMPLATE;
    private static final String TEST_01_SOURCE = "KiePMMLRegressionModelFactoryTest_01.txt";

    private static final String modelName = "firstModel";
    private static final double tableIntercept = 3.5;
    private static final Object tableTargetCategory = "professional";
    private static List<RegressionTable> regressionTables;
    private static List<DataField> dataFields;
    private static List<MiningField> miningFields;
    private static MiningField targetMiningField;
    private static DataDictionary dataDictionary;
    private static TransformationDictionary transformationDictionary;
    private static MiningSchema miningSchema;
    private static RegressionModel regressionModel;
    private static PMML pmml;

    @BeforeAll
    public static void setup() {
        Random random = new Random();
        Set<String> fieldNames = new HashSet<>();
        regressionTables = IntStream.range(0, 3).mapToObj(i -> {
                                                              List<CategoricalPredictor> categoricalPredictors =
                                                                      new ArrayList<>();
                                                              List<NumericPredictor> numericPredictors =
                                                                      new ArrayList<>();
                                                              List<PredictorTerm> predictorTerms = new ArrayList<>();
                                                              IntStream.range(0, 3).forEach(j -> {
                                                                  String catFieldName = "CatPred-" + j;
                                                                  String numFieldName = "NumPred-" + j;
                                                                  categoricalPredictors.add(getCategoricalPredictor(catFieldName, random.nextDouble(), random.nextDouble()));
                                                                  numericPredictors.add(getNumericPredictor(numFieldName, random.nextInt(), random.nextDouble()));
                                                                  predictorTerms.add(getPredictorTerm("PredTerm-" + j
                                                                          , random.nextDouble(), Arrays.asList(catFieldName, numFieldName)));
                                                                  fieldNames.add(catFieldName);
                                                                  fieldNames.add(numFieldName);
                                                              });
                                                              return getRegressionTable(categoricalPredictors,
                                                                                        numericPredictors,
                                                                                        predictorTerms,
                                                                                        tableIntercept + random.nextDouble(), tableTargetCategory + "-" + i);
                                                          }
        ).collect(Collectors.toList());
        dataFields = new ArrayList<>();
        miningFields = new ArrayList<>();
        fieldNames.forEach(fieldName -> {
            dataFields.add(getDataField(fieldName, OpType.CATEGORICAL, DataType.STRING));
            miningFields.add(getMiningField(fieldName, MiningField.UsageType.ACTIVE));
        });
        targetMiningField = miningFields.get(0);
        targetMiningField.setUsageType(MiningField.UsageType.TARGET);
        dataDictionary = getDataDictionary(dataFields);
        transformationDictionary = new TransformationDictionary();
        miningSchema = getMiningSchema(miningFields);
        regressionModel = getRegressionModel(modelName, MiningFunction.REGRESSION, miningSchema, regressionTables);
        COMPILATION_UNIT = getFromFileName(KIE_PMML_REGRESSION_MODEL_TEMPLATE_JAVA);
        MODEL_TEMPLATE = COMPILATION_UNIT.getClassByName(KIE_PMML_REGRESSION_MODEL_TEMPLATE).get();
        pmml = new PMML();
        pmml.setDataDictionary(dataDictionary);
        pmml.setTransformationDictionary(transformationDictionary);
        pmml.addModels(regressionModel);
    }

    @Test
    void getKiePMMLRegressionModelClasses() throws IOException, IllegalAccessException, InstantiationException {
        final CompilationDTO<RegressionModel> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       regressionModel,
                                                                       new PMMLCompilationContextMock(),
                                                                       "FILENAME");
        KiePMMLRegressionModel retrieved =
                KiePMMLRegressionModelFactory.getKiePMMLRegressionModelClasses(RegressionCompilationDTO.fromCompilationDTO(compilationDTO));
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getName()).isEqualTo(regressionModel.getModelName());
        assertThat(retrieved.getMiningFunction()).isEqualTo(MINING_FUNCTION.byName(regressionModel.getMiningFunction().value()));
        assertThat(retrieved.getTargetField()).isEqualTo(miningFields.get(0).getName());
        final AbstractKiePMMLTable regressionTable = retrieved.getRegressionTable();
        assertThat(regressionTable).isNotNull();
        assertThat(regressionTable).isInstanceOf(KiePMMLClassificationTable.class);
        evaluateCategoricalRegressionTable((KiePMMLClassificationTable) regressionTable);
    }

    @Test
    void getKiePMMLRegressionModelSourcesMap() throws IOException {
        final CommonCompilationDTO<RegressionModel> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       regressionModel,
                                                                       new PMMLCompilationContextMock(),
                                                                       "FILENAME");
        Map<String, String> retrieved =
                KiePMMLRegressionModelFactory.getKiePMMLRegressionModelSourcesMap(RegressionCompilationDTO.fromCompilationDTO(compilationDTO));
        assertThat(retrieved).isNotNull();
        int expectedSize = regressionTables.size()
                + 2; // One for classification and one for the whole model
        assertThat(retrieved).hasSize(expectedSize);
    }

    @Test
    void getRegressionTablesMap() {
        final CompilationDTO<RegressionModel> compilationDTO =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       regressionModel,
                                                                       new PMMLCompilationContextMock(),
                                                                       "FILENAME");
        Map<String, KiePMMLTableSourceCategory> retrieved = KiePMMLRegressionModelFactory
                .getRegressionTablesMap(RegressionCompilationDTO.fromCompilationDTO(compilationDTO));
        int expectedSize = regressionTables.size() + 1; // One for classification
        assertThat(retrieved).hasSize(expectedSize);
        final Collection<KiePMMLTableSourceCategory> values = retrieved.values();
        regressionTables.forEach(regressionTable ->
                assertThat(values.stream().anyMatch(kiePMMLTableSourceCategory -> kiePMMLTableSourceCategory.getCategory().equals(regressionTable.getTargetCategory()))).isTrue());
    }

    @Test
    void setStaticGetter() throws IOException {
        String nestedTable = "NestedTable";
        MINING_FUNCTION miningFunction = MINING_FUNCTION.byName(regressionModel.getMiningFunction().value());
        final ClassOrInterfaceDeclaration modelTemplate = MODEL_TEMPLATE.clone();
        final CommonCompilationDTO<RegressionModel> source =
                CommonCompilationDTO.fromGeneratedPackageNameAndFields(PACKAGE_NAME,
                                                                       pmml,
                                                                       regressionModel,
                                                                       new PMMLCompilationContextMock(),
                                                                       "fileName");
        final RegressionCompilationDTO compilationDTO =
                RegressionCompilationDTO.fromCompilationDTORegressionTablesAndNormalizationMethod(source,
                                                                                                  new ArrayList<>(),
                                                                                                  regressionModel.getNormalizationMethod());
        KiePMMLRegressionModelFactory.setStaticGetter(compilationDTO,
                modelTemplate,
                nestedTable);
        Map<Integer, Expression> superInvocationExpressionsMap = new HashMap<>();
        superInvocationExpressionsMap.put(0, new NameExpr(String.format("\"%s\"", regressionModel.getModelName())));
        Map<String, Expression> assignExpressionMap = new HashMap<>();
        assignExpressionMap.put("targetField", new StringLiteralExpr(targetMiningField.getName()));
        assignExpressionMap.put("miningFunction",
                new NameExpr(miningFunction.getClass().getName() + "." + miningFunction.name()));
        assignExpressionMap.put("pmmlMODEL",
                new NameExpr(PMML_MODEL.class.getName() + "." + PMML_MODEL.REGRESSION_MODEL.name()));
        MethodCallExpr methodCallExpr = new MethodCallExpr();
        methodCallExpr.setScope(new NameExpr(nestedTable));
        methodCallExpr.setName(GETKIEPMML_TABLE);
        assignExpressionMap.put("regressionTable", methodCallExpr);
        MethodDeclaration retrieved = modelTemplate.getMethodsByName(GET_MODEL).get(0);
        String text = getFileContent(TEST_01_SOURCE);
        MethodDeclaration expected = JavaParserUtils.parseMethod(text);
        assertThat(expected.toString()).isEqualTo(retrieved.toString());
        assertThat(JavaParserUtils.equalsNode(expected, retrieved)).isTrue();
    }

    private void evaluateCategoricalRegressionTable(KiePMMLClassificationTable regressionTable) {
        assertThat(regressionTable.getRegressionNormalizationMethod()).isEqualTo(REGRESSION_NORMALIZATION_METHOD.byName(regressionModel.getNormalizationMethod().value()));
        assertThat(regressionTable.getOpType()).isEqualTo(OP_TYPE.CATEGORICAL);
        final Map<String, KiePMMLRegressionTable> categoryTableMap = regressionTable.getCategoryTableMap();
        for (RegressionTable originalRegressionTable : regressionTables) {
            assertThat(categoryTableMap).containsKey(originalRegressionTable.getTargetCategory().toString());
            evaluateRegressionTable(categoryTableMap.get(originalRegressionTable.getTargetCategory().toString()),
                                    originalRegressionTable);
        }
    }

    private void evaluateRegressionTable(KiePMMLRegressionTable regressionTable,
                                         RegressionTable originalRegressionTable) {
        assertThat(regressionTable.getIntercept()).isEqualTo(originalRegressionTable.getIntercept());
        final Map<String, SerializableFunction<Double, Double>> numericFunctionMap =
                regressionTable.getNumericFunctionMap();
        for (NumericPredictor numericPredictor : originalRegressionTable.getNumericPredictors()) {
            assertThat(numericFunctionMap).containsKey(numericPredictor.getField());
        }
        final Map<String, SerializableFunction<String, Double>> categoricalFunctionMap =
                regressionTable.getCategoricalFunctionMap();
        for (CategoricalPredictor categoricalPredictor : originalRegressionTable.getCategoricalPredictors()) {
		assertThat(categoricalFunctionMap).containsKey(categoricalPredictor.getField());
        }
        final Map<String, SerializableFunction<Map<String, Object>, Double>> predictorTermsFunctionMap =
                regressionTable.getPredictorTermsFunctionMap();
        for (PredictorTerm predictorTerm : originalRegressionTable.getPredictorTerms()) {
		assertThat(predictorTermsFunctionMap).containsKey(predictorTerm.getName());
        }
    }
}