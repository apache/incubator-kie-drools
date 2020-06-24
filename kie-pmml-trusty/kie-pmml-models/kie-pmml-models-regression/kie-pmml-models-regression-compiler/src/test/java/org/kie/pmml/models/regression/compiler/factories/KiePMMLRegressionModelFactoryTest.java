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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.dmg.pmml.DataDictionary;
import org.dmg.pmml.DataField;
import org.dmg.pmml.MiningField;
import org.dmg.pmml.MiningFunction;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.OpType;
import org.dmg.pmml.TransformationDictionary;
import org.dmg.pmml.regression.CategoricalPredictor;
import org.dmg.pmml.regression.NumericPredictor;
import org.dmg.pmml.regression.PredictorTerm;
import org.dmg.pmml.regression.RegressionModel;
import org.dmg.pmml.regression.RegressionTable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.pmml.commons.model.enums.MINING_FUNCTION;
import org.kie.pmml.commons.model.enums.OP_TYPE;
import org.kie.pmml.models.regression.model.KiePMMLRegressionClassificationTable;
import org.kie.pmml.models.regression.model.KiePMMLRegressionModel;
import org.kie.pmml.models.regression.model.KiePMMLRegressionTable;
import org.kie.pmml.models.regression.model.enums.REGRESSION_NORMALIZATION_METHOD;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getCategoricalPredictor;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getDataDictionary;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getDataField;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getMiningField;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getMiningSchema;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getNumericPredictor;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getPredictorTerm;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRegressionModel;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRegressionTable;
import static org.kie.pmml.models.regression.compiler.factories.KiePMMLRegressionModelFactory.getKiePMMLRegressionModelClasses;

@RunWith(Parameterized.class)
public class KiePMMLRegressionModelFactoryTest {

    private List<RegressionTable> regressionTables;
    private List<DataField> dataFields;
    private List<MiningField> miningFields;
    private MiningField targetMiningField;
    private DataDictionary dataDictionary;
    private TransformationDictionary transformationDictionary;
    private MiningSchema miningSchema;
    private RegressionModel regressionModel;

    public KiePMMLRegressionModelFactoryTest(String modelName, double tableIntercept, Object tableTargetCategory) {
        Random random = new Random();
        Set<String> fieldNames = new HashSet<>();
        regressionTables = IntStream.range(0, 3).mapToObj(i -> {
                                                              List<CategoricalPredictor> categoricalPredictors = new ArrayList<>();
                                                              List<NumericPredictor> numericPredictors = new ArrayList<>();
                                                              List<PredictorTerm> predictorTerms = new ArrayList<>();
                                                              IntStream.range(0, 3).forEach(j -> {
                                                                  String catFieldName = "CatPred-" + j;
                                                                  String numFieldName = "NumPred-" + j;
                                                                  categoricalPredictors.add(getCategoricalPredictor(catFieldName, random.nextDouble(), random.nextDouble()));
                                                                  numericPredictors.add(getNumericPredictor(numFieldName, random.nextInt(), random.nextDouble()));
                                                                  predictorTerms.add(getPredictorTerm("PredTerm-" + j, random.nextDouble(), Arrays.asList(catFieldName, numFieldName)));
                                                                  fieldNames.add(catFieldName);
                                                                  fieldNames.add(numFieldName);
                                                              });
                                                              return getRegressionTable(categoricalPredictors, numericPredictors, predictorTerms, tableIntercept + random.nextDouble(), tableTargetCategory + "-" + i);
                                                          }
        ).collect(Collectors.toList());
        dataFields = new ArrayList<>();
        miningFields = new ArrayList<>();
        fieldNames.forEach(fieldName -> {
            dataFields.add(getDataField(fieldName, OpType.CATEGORICAL));
            miningFields.add(getMiningField(fieldName, MiningField.UsageType.ACTIVE));
        });
        targetMiningField = miningFields.get(0);
        targetMiningField.setUsageType(MiningField.UsageType.TARGET);
        dataDictionary = getDataDictionary(dataFields);
        transformationDictionary = new TransformationDictionary();
        miningSchema = getMiningSchema(miningFields);
        regressionModel = getRegressionModel(modelName, MiningFunction.REGRESSION, miningSchema, regressionTables);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"firstModel", 3.5, "professional"},
                {"secondModel", 27.4, "clerical"}
        });
    }

    @Test
    public void getKiePMMLRegressionModelTest() throws IOException, IllegalAccessException, InstantiationException {
        KiePMMLRegressionModel retrieved = getKiePMMLRegressionModelClasses(dataDictionary, transformationDictionary, regressionModel);
        assertNotNull(retrieved);
        assertEquals(regressionModel.getModelName(), retrieved.getName());
        assertEquals(MINING_FUNCTION.byName(regressionModel.getMiningFunction().value()), retrieved.getMiningFunction());
        assertEquals(miningFields.get(0).getName().getValue(), retrieved.getTargetField());
        final KiePMMLRegressionTable regressionTable = retrieved.getRegressionTable();
        assertNotNull(regressionTable);
        assertTrue(regressionTable instanceof KiePMMLRegressionClassificationTable);
        evaluateCategoricalRegressionTable((KiePMMLRegressionClassificationTable) regressionTable);
    }

    private void evaluateCategoricalRegressionTable(KiePMMLRegressionClassificationTable regressionTable) {
        assertEquals(REGRESSION_NORMALIZATION_METHOD.byName(regressionModel.getNormalizationMethod().value()), regressionTable.getRegressionNormalizationMethod());
        assertEquals(OP_TYPE.CATEGORICAL, regressionTable.getOpType());
        final Map<String, KiePMMLRegressionTable> categoryTableMap = regressionTable.getCategoryTableMap();
        for (RegressionTable originalRegressionTable : regressionTables) {
            assertTrue(categoryTableMap.containsKey(originalRegressionTable.getTargetCategory().toString()));
            evaluateRegressionTable(categoryTableMap.get(originalRegressionTable.getTargetCategory().toString()), originalRegressionTable);
        }
    }

    private void evaluateRegressionTable(KiePMMLRegressionTable regressionTable, RegressionTable originalRegressionTable) {
        assertEquals(originalRegressionTable.getIntercept(), regressionTable.getIntercept());
        final Map<String, Function<Double, Double>> numericFunctionMap = regressionTable.getNumericFunctionMap();
        for (NumericPredictor numericPredictor : originalRegressionTable.getNumericPredictors()) {
            assertTrue(numericFunctionMap.containsKey(numericPredictor.getName().getValue()));
        }
        final Map<String, Function<Object, Double>> categoricalFunctionMap = regressionTable.getCategoricalFunctionMap();
        for (CategoricalPredictor categoricalPredictor : originalRegressionTable.getCategoricalPredictors()) {
            assertTrue(categoricalFunctionMap.containsKey(categoricalPredictor.getName().getValue()));
        }
        final Map<String, Function<Map<String, Object>, Double>> predictorTermsFunctionMap = regressionTable.getPredictorTermsFunctionMap();
        for (PredictorTerm predictorTerm : originalRegressionTable.getPredictorTerms()) {
            assertTrue(predictorTermsFunctionMap.containsKey(predictorTerm.getName().getValue()));
        }
    }
}