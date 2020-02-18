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

package org.kie.pmml.models.regression.evaluator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.pmml.evaluator.core.PMMLContextImpl;
import org.kie.pmml.models.regression.api.model.KiePMMLRegressionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.models.regression.evaluator.PMMLRegresssionModelEvaluator.evaluateRegression;

public class PMMLRegresssionModelEvaluatorTest {

    private static final Logger logger = LoggerFactory.getLogger(PMMLRegresssionModelEvaluatorTest.class);

    private KiePMMLRegressionModel kiePMMLRegressionModel;

    @Before
    public void setUp() throws Exception {
        kiePMMLRegressionModel = TestUtils.getKiePMMLRegressionModel();
    }

//    @Test(expected = KiePMMLInternalException.class)
//    public void evaluateRegressionWithModelNoTargetField() {
//        KiePMMLRegressionModel toTest = KiePMMLRegressionModel.builder(MODEL_NAME, _MINING_FUNCTION, Collections.singletonList(getKiePMMLRegressionTable()), _OP_TYPE)
//                .withAlgorithmName(ALGORITHM_NAME)
//                .withModelType(_MODEL_TYPE)
//                .withRegressionNormalizationMethod(_REGRESSION_NORMALIZATION_METHOD)
//                .withScorable(SCORABLE)
//                .build();
//        PMMLRequestData pmmlRequestData = getPMMLRequestData(MODEL_NAME, new HashMap<>());
//        evaluateRegression(toTest, new PMMLContextImpl(pmmlRequestData));
//    }

    @Test
    public void evaluateRegressionWithModel() {
        KiePMMLRegressionModel toTest = KiePMMLRegressionModel.builder(TestUtils.MODEL_NAME, TestUtils._MINING_FUNCTION, Collections.singletonList(TestUtils.getKiePMMLRegressionTable()), TestUtils._OP_TYPE)
                .withAlgorithmName(TestUtils.ALGORITHM_NAME)
                .withModelType(TestUtils._MODEL_TYPE)
                .withRegressionNormalizationMethod(TestUtils._REGRESSION_NORMALIZATION_METHOD)
                .withScorable(TestUtils.SCORABLE)
                .withTargetField(TestUtils.TARGETFIELD_NAME)
                .build();
        commonEvaluateWithModel(20, 1950, TestUtils.STREET, toTest);
        commonEvaluateWithModel(20, 1950, TestUtils.CARPARK, toTest);
        commonEvaluateWithModel(59, 3750, TestUtils.STREET, toTest);
        commonEvaluateWithModel(35, 1800, TestUtils.CARPARK, toTest);
    }

    @Test
    public void evaluateRegressionWithTable() {
        commonEvaluateWithTable(20, 1950, TestUtils.STREET);
        commonEvaluateWithTable(20, 1950, TestUtils.CARPARK);
        commonEvaluateWithTable(59, 3750, TestUtils.STREET);
        commonEvaluateWithTable(35, 1800, TestUtils.CARPARK);
    }

    private void commonEvaluateWithModel(int age, double salary, String carLocation, KiePMMLRegressionModel toTest) {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("age", age);
        inputData.put("salary", salary);
        inputData.put("car_location", carLocation);
        final PMMLRequestData pmmlRequestData = TestUtils.getPMMLRequestData(TestUtils.MODEL_NAME, inputData);
        PMML4Result retrieved = evaluateRegression(toTest, new PMMLContextImpl(pmmlRequestData));
        commonEvaluate(retrieved, age, salary, carLocation);
    }

    private void commonEvaluateWithTable(int age, double salary, String carLocation) {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("age", age);
        inputData.put("salary", salary);
        inputData.put("car_location", carLocation);
        final PMMLRequestData pmmlRequestData = TestUtils.getPMMLRequestData(TestUtils.MODEL_NAME, inputData);
        PMML4Result retrieved = evaluateRegression(kiePMMLRegressionModel.getTargetField(), kiePMMLRegressionModel.getRegressionNormalizationMethod(), TestUtils.getKiePMMLRegressionTable(), pmmlRequestData);
        commonEvaluate(retrieved, age, salary, carLocation);
    }

    private void commonEvaluate(PMML4Result toEvaluate, int age, double salary, String carLocation) {
        assertNotNull(toEvaluate);
        logger.debug(toEvaluate.toString());
        assertNotNull(toEvaluate.getResultVariables());
        assertTrue(toEvaluate.getResultVariables().containsKey(TestUtils.TARGETFIELD_NAME));
        double expected = TestUtils.INTERCEPT + TestUtils.AGE_COEFF * age + TestUtils.SALARY_COEFF * salary;
        if (TestUtils.CARPARK.equals(carLocation)) {
            expected += TestUtils.CARPARK_COEFF;
        } else if (TestUtils.STREET.equals(carLocation)) {
            expected += TestUtils.STREET_COEFF;
        }
        double retrievedDouble = (double) toEvaluate.getResultVariables().get(TestUtils.TARGETFIELD_NAME);
        assertEquals(expected, retrievedDouble, 0.00001);
        logger.debug("Expected {} retrieved {}", expected, retrievedDouble);
    }
}