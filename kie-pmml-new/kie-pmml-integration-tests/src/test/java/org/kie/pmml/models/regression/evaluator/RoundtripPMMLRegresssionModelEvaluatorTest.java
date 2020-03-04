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

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.model.enums.PMML_MODEL;
import org.kie.pmml.evaluator.api.executor.PMMLContext;
import org.kie.pmml.evaluator.api.executor.PMMLRuntime;
import org.kie.pmml.evaluator.core.PMMLContextImpl;
import org.kie.pmml.models.regression.model.KiePMMLRegressionModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.models.regression.evaluator.TestUtils.AGE_COEFF;
import static org.kie.pmml.models.regression.evaluator.TestUtils.CARPARK;
import static org.kie.pmml.models.regression.evaluator.TestUtils.CARPARK_COEFF;
import static org.kie.pmml.models.regression.evaluator.TestUtils.INTERCEPT;
import static org.kie.pmml.models.regression.evaluator.TestUtils.SALARY_COEFF;
import static org.kie.pmml.models.regression.evaluator.TestUtils.STREET;
import static org.kie.pmml.models.regression.evaluator.TestUtils.STREET_COEFF;
import static org.kie.pmml.models.regression.evaluator.TestUtils.TARGETFIELD_NAME;
import static org.kie.pmml.models.regression.evaluator.TestUtils.getPMMLRequestData;
import static org.kie.test.util.filesystem.FileUtils.getFile;

public class RoundtripPMMLRegresssionModelEvaluatorTest {

    private static final Logger logger = LoggerFactory.getLogger(RoundtripPMMLRegresssionModelEvaluatorTest.class);

    private static final String SOURCE = "LinearRegressionSample.pmml";

    private PMMLRuntime pmmlRuntime;

    private String releaseId;

    @Before
    public void setup() {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write(ResourceFactory.newFileResource(getFile(SOURCE)).setResourceType(ResourceType.PMML));
        final KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
        final ReleaseId relId = kieBuilder.getKieModule().getReleaseId();
        releaseId = relId.toExternalForm();
        Results res = kieBuilder.getResults();
        assertNotNull(res);
        assertTrue(res.getMessages(Message.Level.ERROR).isEmpty());
        KieBase kbase = ks.newKieContainer(relId).getKieBase();
        KieSession session = kbase.newKieSession();
        pmmlRuntime = session.getKieRuntime(PMMLRuntime.class);
        assertNotNull(pmmlRuntime);
    }

    @Test
    public void evaluateSimpleRegression() {
        String modelName = "Sample for linear regression";
        commonEvaluateSimpleRegression(20, 1950, STREET, modelName);
        commonEvaluateSimpleRegression(20, 1950, CARPARK, modelName);
        commonEvaluateSimpleRegression(59, 3750, STREET, modelName);
        commonEvaluateSimpleRegression(35, 1800, CARPARK, modelName);
    }

    private void commonEvaluateSimpleRegression(int age, double salary, String carLocation, String modelName) {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("age", age);
        inputData.put("salary", salary);
        inputData.put("car_location", carLocation);
        final PMMLRequestData pmmlRequestData = getPMMLRequestData(modelName, inputData);
        PMMLContext pmmlContext = new PMMLContextImpl(pmmlRequestData);
        double expected = INTERCEPT + AGE_COEFF * age + SALARY_COEFF * salary;
        if (CARPARK.equals(carLocation)) {
            expected += CARPARK_COEFF;
        } else if (STREET.equals(carLocation)) {
            expected += STREET_COEFF;
        }
        commonEvaluateSimpleRegression(pmmlContext, expected);
    }

    private void commonEvaluateSimpleRegression(PMMLContext pmmlContext, double expected) {
        final KiePMMLModel model = pmmlRuntime.getModel(pmmlContext.getRequestData().getModelName()).orElseThrow(() -> new KiePMMLException("Failed to retrieve the model"));
        assertEquals(PMML_MODEL.REGRESSION_MODEL, model.getPmmlMODEL());
        assertTrue(model instanceof KiePMMLRegressionModel);
        PMML4Result retrieved = pmmlRuntime.evaluate(model, pmmlContext, releaseId);
        assertNotNull(retrieved);
        logger.debug(retrieved.toString());
        assertNotNull(retrieved.getResultVariables());
        assertEquals(TARGETFIELD_NAME, retrieved.getResultObjectName());
        assertTrue(retrieved.getResultVariables().containsKey(TARGETFIELD_NAME));
        double retrievedDouble = (double) retrieved.getResultVariables().get(TARGETFIELD_NAME);
        assertEquals(expected, retrievedDouble, 0.00001);
    }
}