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

package org.kie.pmml.runtime.regression.executor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.models.regression.api.model.KiePMMLRegressionModel;
import org.kie.pmml.runtime.api.exceptions.KiePMMLModelException;
import org.kie.pmml.runtime.core.PMMLContextImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.runtime.regression.executor.PMMLRegresssionModelEvaluator.evaluateRegression;
import static org.kie.pmml.runtime.regression.executor.TestUtils.AGE_COEFF;
import static org.kie.pmml.runtime.regression.executor.TestUtils.ALGORITHM_NAME;
import static org.kie.pmml.runtime.regression.executor.TestUtils.CARPARK;
import static org.kie.pmml.runtime.regression.executor.TestUtils.CARPARK_COEFF;
import static org.kie.pmml.runtime.regression.executor.TestUtils.INTERCEPT;
import static org.kie.pmml.runtime.regression.executor.TestUtils.MODEL_NAME;
import static org.kie.pmml.runtime.regression.executor.TestUtils.REGRESSION_TABLES;
import static org.kie.pmml.runtime.regression.executor.TestUtils.SALARY_COEFF;
import static org.kie.pmml.runtime.regression.executor.TestUtils.SCORABLE;
import static org.kie.pmml.runtime.regression.executor.TestUtils.STREET;
import static org.kie.pmml.runtime.regression.executor.TestUtils.STREET_COEFF;
import static org.kie.pmml.runtime.regression.executor.TestUtils.TARGETFIELD_NAME;
import static org.kie.pmml.runtime.regression.executor.TestUtils._MINING_FUNCTION;
import static org.kie.pmml.runtime.regression.executor.TestUtils._MODEL_TYPE;
import static org.kie.pmml.runtime.regression.executor.TestUtils._OP_TYPE;
import static org.kie.pmml.runtime.regression.executor.TestUtils._REGRESSION_NORMALIZATION_METHOD;
import static org.kie.pmml.runtime.regression.executor.TestUtils.getKiePMMLRegressionModel;
import static org.kie.pmml.runtime.regression.executor.TestUtils.getKiePMMLRegressionTable;
import static org.kie.pmml.runtime.regression.executor.TestUtils.getPMMLRequestData;

public class PMMLRegresssionModelEvaluatorTest {

    private static final Logger logger = LoggerFactory.getLogger(PMMLRegresssionModelEvaluatorTest.class);

    private KiePMMLRegressionModel kiePMMLRegressionModel;

    @Before
    public void setUp() throws Exception {
        kiePMMLRegressionModel = getKiePMMLRegressionModel();
    }

    @Test(expected = KiePMMLException.class)
    public void evaluateRegressionWithModelNoTargetField() throws KiePMMLException {
        KiePMMLRegressionModel toTest = KiePMMLRegressionModel.builder(MODEL_NAME, _MINING_FUNCTION)
                .withAlgorithmName(ALGORITHM_NAME)
                .withModelType(_MODEL_TYPE)
                .withRegressionNormalizationMethod(_REGRESSION_NORMALIZATION_METHOD)
                .withScorable(SCORABLE)
                .withTargetOpType(_OP_TYPE)
                .withRegressionTables(REGRESSION_TABLES)
                .build();
        PMMLRequestData pmmlRequestData = getPMMLRequestData(MODEL_NAME, new HashMap<>());
        evaluateRegression(toTest, new PMMLContextImpl(pmmlRequestData));
    }

    @Test(expected = KiePMMLModelException.class)
    public void evaluateRegressionWithModelNoTables() throws KiePMMLException {
        KiePMMLRegressionModel toTest = KiePMMLRegressionModel.builder(MODEL_NAME, _MINING_FUNCTION)
                .withAlgorithmName(ALGORITHM_NAME)
                .withModelType(_MODEL_TYPE)
                .withRegressionNormalizationMethod(_REGRESSION_NORMALIZATION_METHOD)
                .withScorable(SCORABLE)
                .withTargetOpType(_OP_TYPE)
                .withTargetField(TARGETFIELD_NAME)
                .build();
        PMMLRequestData pmmlRequestData = getPMMLRequestData(MODEL_NAME, new HashMap<>());
        evaluateRegression(toTest, new PMMLContextImpl(pmmlRequestData));
    }

    @Test
    public void evaluateRegressionWithModel() throws KiePMMLException {
        KiePMMLRegressionModel toTest = KiePMMLRegressionModel.builder(MODEL_NAME, _MINING_FUNCTION)
                .withAlgorithmName(ALGORITHM_NAME)
                .withModelType(_MODEL_TYPE)
                .withRegressionNormalizationMethod(_REGRESSION_NORMALIZATION_METHOD)
                .withRegressionTables(Collections.singletonList(getKiePMMLRegressionTable()))
                .withScorable(SCORABLE)
                .withTargetOpType(_OP_TYPE)
                .withTargetField(TARGETFIELD_NAME)
                .build();
        commonEvaluateWithModel(20, 1950, STREET, toTest);
        commonEvaluateWithModel(20, 1950, CARPARK, toTest);
        commonEvaluateWithModel(59, 3750, STREET, toTest);
        commonEvaluateWithModel(35, 1800, CARPARK, toTest);
    }

    @Test
    public void evaluateRegressionWithTable() throws KiePMMLException {
        commonEvaluateWithTable(20, 1950, STREET);
        commonEvaluateWithTable(20, 1950, CARPARK);
        commonEvaluateWithTable(59, 3750, STREET);
        commonEvaluateWithTable(35, 1800, CARPARK);
    }

    private void commonEvaluateWithModel(int age, double salary, String carLocation, KiePMMLRegressionModel toTest) throws KiePMMLException {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("age", age);
        inputData.put("salary", salary);
        inputData.put("car_location", carLocation);
        final PMMLRequestData pmmlRequestData = getPMMLRequestData(MODEL_NAME, inputData);
        PMML4Result retrieved = evaluateRegression(toTest, new PMMLContextImpl(pmmlRequestData));
        commonEvaluate(retrieved, age,  salary,  carLocation);
    }

    private void commonEvaluateWithTable(int age, double salary, String carLocation) throws KiePMMLException {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("age", age);
        inputData.put("salary", salary);
        inputData.put("car_location", carLocation);
        final PMMLRequestData pmmlRequestData = getPMMLRequestData(MODEL_NAME, inputData);
        PMML4Result retrieved = evaluateRegression(kiePMMLRegressionModel.getTargetField(), kiePMMLRegressionModel.getRegressionNormalizationMethod(), getKiePMMLRegressionTable(), pmmlRequestData);
        commonEvaluate(retrieved, age,  salary,  carLocation);
    }

    private void commonEvaluate(PMML4Result toEvaluate, int age, double salary, String carLocation) {
        assertNotNull(toEvaluate);
        logger.info(toEvaluate.toString());
        assertNotNull(toEvaluate.getResultVariables());
        assertTrue(toEvaluate.getResultVariables().containsKey(TARGETFIELD_NAME));
        double expected = INTERCEPT + AGE_COEFF * age + SALARY_COEFF * salary;
        if (CARPARK.equals(carLocation)) {
            expected += CARPARK_COEFF;
        } else if (STREET.equals(carLocation)) {
            expected += STREET_COEFF;
        }
        double retrievedDouble = (double)toEvaluate.getResultVariables().get(TARGETFIELD_NAME);
        assertEquals(expected, retrievedDouble, 0.00001);
        logger.info("Expected {} retrieved {}", expected, retrievedDouble);
    }
}