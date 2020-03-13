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

package org.kie.pmml.models.regression.compiler.executor;

import org.dmg.pmml.PMML;
import org.dmg.pmml.regression.RegressionModel;
import org.junit.Test;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.compiler.testutils.TestUtils;
import org.kie.pmml.models.regression.model.KiePMMLRegressionModel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//import org.kie.pmml.models.regression.model.KiePMMLRegressionTable;

public class RegressionModelImplementationProviderTest {

    private final static RegressionModelImplementationProvider PROVIDER = new RegressionModelImplementationProvider();
    private final static String RELEASE_ID = "org.drools:kie-pmml-models-testing:1.0";
    private static final String SOURCE_1 = "LinearRegressionSample.xml";
    private static final String SOURCE_2 = "test_regression.pmml";
    private static final String SOURCE_3 = "test_regression_clax.pmml";

    @Test
    public void getPMMLModelType() {
        assertEquals(PMML_MODEL.REGRESSION_MODEL, PROVIDER.getPMMLModelType());
    }

    @Test
    public void getKiePMMLModel() throws Exception {
        final PMML pmml = TestUtils.loadFromFile(SOURCE_1);
        assertNotNull(pmml);
        assertEquals(1, pmml.getModels().size());
        assertTrue(pmml.getModels().get(0) instanceof RegressionModel);
        commonVerifyKiePMMLRegressionModel(PROVIDER.getKiePMMLModel(pmml.getDataDictionary(), (RegressionModel) pmml.getModels().get(0), RELEASE_ID));
    }

    @Test
    public void validateSource2() throws Exception {
        commonValidateSource(SOURCE_2);
    }

    @Test
    public void validateSource3() throws Exception {
        commonValidateSource(SOURCE_3);
    }

    private void commonValidateSource(String sourceFile) throws Exception {
        final PMML pmml = TestUtils.loadFromFile(sourceFile);
        assertNotNull(pmml);
        assertEquals(1, pmml.getModels().size());
        assertTrue(pmml.getModels().get(0) instanceof RegressionModel);
        PROVIDER.validate(pmml.getDataDictionary(), (RegressionModel) pmml.getModels().get(0));
    }

    private void commonVerifyKiePMMLRegressionModel(KiePMMLRegressionModel retrieved) {
//        assertNotNull(retrieved);
//        assertEquals(MINING_FUNCTION.REGRESSION, retrieved.getMiningFunction());
//        assertTrue(retrieved.getAlgorithmName().isPresent());
//        assertEquals("linearRegression", retrieved.getAlgorithmName().get());
//        assertFalse(retrieved.getModelType().isPresent());
//        assertEquals("number_of_claims", retrieved.getTargetField());
//        assertEquals(OP_TYPE.CONTINUOUS, retrieved.getTargetOpType());
//        assertEquals(REGRESSION_NORMALIZATION_METHOD.NONE, retrieved.getRegressionNormalizationMethod());
//        assertTrue(retrieved.isScorable());
//        assertTrue(retrieved.isRegression());
//        assertEquals(1, retrieved.getRegressionTables().size());
//        commonVerifyKiePMMLRegressionTable(retrieved.getRegressionTables().get(0));
    }

//    private void commonVerifyKiePMMLRegressionTable(KiePMMLRegressionTable retrieved) {
//        assertNotNull(retrieved);
//        assertEquals(132.37, retrieved.getIntercept());
//        assertFalse(retrieved.getTargetCategory().isPresent());
//        assertTrue(retrieved.getNumericPredictors().isPresent());
//        assertEquals(2, retrieved.getNumericPredictors().get().size());
//        assertTrue(retrieved.getCategoricalPredictors().isPresent());
//        assertEquals(2, retrieved.getCategoricalPredictors().get().size());
//        commonVerifyNumericPredictors(retrieved, "age", 1, 7.1);
//        commonVerifyNumericPredictors(retrieved, "salary", 1, 0.01);
//        commonVerifyCategoricalPredictors(retrieved, "car_location");
//    }

//    private void commonVerifyNumericPredictors(KiePMMLRegressionTable retrieved, String name, int exponent, double coefficient) {
//        final Optional<KiePMMLNumericPredictor> optionalPredictor = retrieved.getKiePMMLNumericPredictorByName(name);
//        assertTrue(optionalPredictor.isPresent());
//        commonVerifyNumericPredictor(optionalPredictor.get(), name, exponent, coefficient);
//    }

//    private void commonVerifyNumericPredictor(KiePMMLNumericPredictor retrieved, String name, int exponent, double coefficient) {
//        assertEquals(name, retrieved.getName());
//        assertEquals(exponent, retrieved.getExponent());
//        assertEquals(coefficient, retrieved.getCoefficient());
//    }

//    private void commonVerifyCategoricalPredictors(KiePMMLRegressionTable retrieved, String name) {
//        List<KiePMMLCategoricalPredictor> predictors = new ArrayList<>();
//        retrieved.getKiePMMLCategoricalPredictorByNameAndValue(name, "carpark").ifPresent(predictors::add);
//        retrieved.getKiePMMLCategoricalPredictorByNameAndValue(name, "street").ifPresent(predictors::add);
//        assertEquals(2, predictors.size());
//        commonVerifyCategoricalPredictors(predictors, name, "carpark", 41.1);
//        commonVerifyCategoricalPredictors(predictors, name, "street", 325.03);
//    }

//    private void commonVerifyCategoricalPredictors(List<KiePMMLCategoricalPredictor> predictors, String name, Object value, double coefficient) {
//        final Optional<KiePMMLCategoricalPredictor> retrieved = predictors.stream().filter(predictor -> value.equals(predictor.getValue())).findFirst();
//        assertTrue(retrieved.isPresent());
//        commonVerifyCategoricalPredictor(retrieved.get(), name, value, coefficient);
//    }
//
//    private void commonVerifyCategoricalPredictor(KiePMMLCategoricalPredictor retrieved, String name, Object value, double coefficient) {
//        assertEquals(name, retrieved.getName());
//        assertEquals(value, retrieved.getValue());
//        assertEquals(coefficient, retrieved.getCoefficient());
//    }
}