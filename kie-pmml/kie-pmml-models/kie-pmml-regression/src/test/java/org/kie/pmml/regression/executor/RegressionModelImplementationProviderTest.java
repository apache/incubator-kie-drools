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

package org.kie.pmml.regression.executor;

import java.util.List;
import java.util.Optional;

import org.dmg.pmml.PMML;
import org.dmg.pmml.regression.RegressionModel;
import org.junit.Test;
import org.kie.pmml.api.model.KiePMMLModel;
import org.kie.pmml.api.model.enums.MINING_FUNCTION;
import org.kie.pmml.api.model.enums.OP_TYPE;
import org.kie.pmml.api.model.enums.PMML_MODEL;
import org.kie.pmml.api.model.regression.KiePMMLRegressionModel;
import org.kie.pmml.api.model.regression.KiePMMLRegressionTable;
import org.kie.pmml.api.model.regression.enums.REGRESSION_NORMALIZATION_METHOD;
import org.kie.pmml.api.model.regression.predictors.KiePMMLCategoricalPredictor;
import org.kie.pmml.api.model.regression.predictors.KiePMMLNumericPredictor;
import org.kie.pmml.models.core.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class RegressionModelImplementationProviderTest {

    private final static RegressionModelImplementationProvider PROVIDER = new RegressionModelImplementationProvider();

    @Test
    public void getPMMLModelType() {
        assertEquals(PMML_MODEL.REGRESSION_MODEL, PROVIDER.getPMMLModelType());
    }

    @Test
    public void getKiePMMLModel() throws Exception {
        final PMML pmml = TestUtils.loadFromFile("LinearRegressionSample.xml");
        assertNotNull(pmml);
        KiePMMLRegressionModel retrieved = PROVIDER.getKiePMMLModel(pmml.getDataDictionary(), (RegressionModel) pmml.getModels().get(0));
        assertNotNull(retrieved);
        assertEquals(MINING_FUNCTION.REGRESSION, retrieved.getMiningFunction());
        assertEquals("linearRegression", retrieved.getAlgorithmName());
        assertNull(retrieved.getModelType());
        assertEquals("number_of_claims", retrieved.getTargetFieldName());
        assertEquals(OP_TYPE.CONTINUOUS, retrieved.getTargetOpType());
        assertEquals(REGRESSION_NORMALIZATION_METHOD.NONE, retrieved.getRegressionNormalizationMethod());
        assertTrue(retrieved.isScorable());
        assertTrue(retrieved.isRegression());
        assertEquals(1, retrieved.getRegressionTables().size());
        commonVerifyKiePMMLRegressionTable(retrieved.getRegressionTables().get(0));
    }

    private void commonVerifyKiePMMLRegressionTable(KiePMMLRegressionTable retrieved) {
        assertNotNull(retrieved);
        assertEquals(132.37, retrieved.getIntercept());
        assertNull(retrieved.getTargetCategory());
        assertTrue(retrieved.getExtensions().isEmpty());
        assertEquals(2, retrieved.getNumericPredictors().size());
        assertEquals(2, retrieved.getCategoricalPredictors().size());
        commonVerifyNumericPredictors(retrieved, "age", 1, 7.1);
        commonVerifyNumericPredictors(retrieved, "salary", 1, 0.01);
        commonVerifyCategoricalPredictors(retrieved, "car_location");
    }

    private void commonVerifyNumericPredictors(KiePMMLRegressionTable retrieved, String name, int exponent, double coefficient) {
        final Optional<KiePMMLNumericPredictor> optionalPredictor = retrieved.getKiePMMLNumericPredictorByName(name);
        assertTrue(optionalPredictor.isPresent());
        commonVerifyNumericPredictor(optionalPredictor.get(), name, exponent, coefficient);
    }

    private void commonVerifyNumericPredictor(KiePMMLNumericPredictor retrieved, String name, int exponent, double coefficient) {
        assertEquals(name, retrieved.getName());
        assertEquals(exponent, retrieved.getExponent());
        assertEquals(coefficient, retrieved.getCoefficient());
    }

    private void commonVerifyCategoricalPredictors(KiePMMLRegressionTable retrieved, String name) {
        final Optional<List<KiePMMLCategoricalPredictor>> optionalPredictor = retrieved.getKiePMMLCategoricalPredictorsByName(name);
        assertTrue(optionalPredictor.isPresent());
        List<KiePMMLCategoricalPredictor> predictors = optionalPredictor.get();
        assertEquals(2, predictors.size());
        commonVerifyCategoricalPredictors(predictors, name, "carpark", 41.1);
        commonVerifyCategoricalPredictors(predictors, name, "street", 325.03);
    }

    private void commonVerifyCategoricalPredictors(List<KiePMMLCategoricalPredictor> predictors, String name, Object value, double coefficient) {
        final Optional<KiePMMLCategoricalPredictor> retrieved = predictors.stream().filter(predictor -> value.equals(predictor.getValue())).findFirst();
        assertTrue(retrieved.isPresent());
        commonVerifyCategoricalPredictor(retrieved.get(), name, value, coefficient);
    }

    private void commonVerifyCategoricalPredictor(KiePMMLCategoricalPredictor retrieved, String name, Object value, double coefficient) {
        assertEquals(name, retrieved.getName());
        assertEquals(value, retrieved.getValue());
        assertEquals(coefficient, retrieved.getCoefficient());
    }


}