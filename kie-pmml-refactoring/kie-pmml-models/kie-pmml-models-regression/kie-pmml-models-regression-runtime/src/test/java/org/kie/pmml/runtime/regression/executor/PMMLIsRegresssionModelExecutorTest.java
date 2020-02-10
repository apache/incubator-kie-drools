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

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.models.regression.api.model.KiePMMLRegressionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.runtime.regression.executor.PMMLIsRegresssionModelExecutor.evaluateRegression;
import static org.kie.pmml.runtime.regression.executor.TestUtils.AGE_COEFF;
import static org.kie.pmml.runtime.regression.executor.TestUtils.CARPARK;
import static org.kie.pmml.runtime.regression.executor.TestUtils.CARPARK_COEFF;
import static org.kie.pmml.runtime.regression.executor.TestUtils.INTERCEPT;
import static org.kie.pmml.runtime.regression.executor.TestUtils.MODEL_NAME;
import static org.kie.pmml.runtime.regression.executor.TestUtils.SALARY_COEFF;
import static org.kie.pmml.runtime.regression.executor.TestUtils.STREET;
import static org.kie.pmml.runtime.regression.executor.TestUtils.STREET_COEFF;
import static org.kie.pmml.runtime.regression.executor.TestUtils.TARGETFIELD_NAME;
import static org.kie.pmml.runtime.regression.executor.TestUtils.getKiePMMLRegressionModel;
import static org.kie.pmml.runtime.regression.executor.TestUtils.getKiePMMLRegressionTable;
import static org.kie.pmml.runtime.regression.executor.TestUtils.getPMMLRequestData;

public class PMMLIsRegresssionModelExecutorTest {

    private static final Logger logger = LoggerFactory.getLogger(PMMLIsRegresssionModelExecutorTest.class);

    private KiePMMLRegressionModel kiePMMLRegressionModel;

    @Before
    public void setUp() throws Exception {
        kiePMMLRegressionModel = getKiePMMLRegressionModel();
    }

    @Test
    public void evaluateRegressionWithModel() {
    }

    @Test
    public void evaluateRegressionWithTable() throws KiePMMLException {
        commonEvaluate(20, 1950, STREET);
        commonEvaluate(20, 1950, CARPARK);
        commonEvaluate(59, 3750, STREET);
        commonEvaluate(35, 1800, CARPARK);
    }

    private void commonEvaluate(int age, double salary, String carLocation) throws KiePMMLException {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("age", age);
        inputData.put("salary", salary);
        inputData.put("car_location", carLocation);
        final PMMLRequestData pmmlRequestData = getPMMLRequestData(MODEL_NAME, inputData);
        PMML4Result retrieved = evaluateRegression(kiePMMLRegressionModel.getTargetField(), getKiePMMLRegressionTable(), pmmlRequestData);
        assertNotNull(retrieved);
        logger.info(retrieved.toString());
        assertNotNull(retrieved.getResultVariables());
        assertTrue(retrieved.getResultVariables().containsKey(TARGETFIELD_NAME));
        double expected = INTERCEPT + AGE_COEFF * age + SALARY_COEFF * salary;
        if (CARPARK.equals(carLocation)) {
            expected += CARPARK_COEFF;
        } else if (STREET.equals(carLocation)) {
            expected += STREET_COEFF;
        }
        double retrievedDouble = (double)retrieved.getResultVariables().get(TARGETFIELD_NAME);
        assertEquals(expected, retrievedDouble, 0.00001);
        logger.info("Expected {} retrieved {}", expected, retrievedDouble);
    }
}