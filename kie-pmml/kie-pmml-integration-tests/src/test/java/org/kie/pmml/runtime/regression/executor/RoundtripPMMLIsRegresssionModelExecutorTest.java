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
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.model.KiePMMLModel;
import org.kie.pmml.api.model.enums.PMML_MODEL;
import org.kie.pmml.api.model.regression.KiePMMLRegressionModel;
import org.kie.pmml.runtime.api.executor.PMMLContext;
import org.kie.pmml.runtime.api.executor.PMMLRuntime;
import org.kie.pmml.runtime.core.PMMLContextImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.runtime.regression.executor.TestUtils.AGE_COEFF;
import static org.kie.pmml.runtime.regression.executor.TestUtils.CARPARK;
import static org.kie.pmml.runtime.regression.executor.TestUtils.CARPARK_COEFF;
import static org.kie.pmml.runtime.regression.executor.TestUtils.INTERCEPT;
import static org.kie.pmml.runtime.regression.executor.TestUtils.SALARY_COEFF;
import static org.kie.pmml.runtime.regression.executor.TestUtils.STREET;
import static org.kie.pmml.runtime.regression.executor.TestUtils.STREET_COEFF;
import static org.kie.pmml.runtime.regression.executor.TestUtils.TARGETFIELD_NAME;
import static org.kie.pmml.runtime.regression.executor.TestUtils.getPMMLRequestData;
import static org.kie.test.util.filesystem.FileUtils.getFile;

public class RoundtripPMMLIsRegresssionModelExecutorTest {

    private PMMLRuntime pmmlRuntime;

    private Resource firstSampleResource;

    @Before
    public void setUp() throws Exception {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write( ResourceFactory.newFileResource( getFile("LinearRegressionSample.xml") ).setResourceType( ResourceType.PMML ) );
        final KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
        kieBuilder.getKieModule().getReleaseId();
        Results res = kieBuilder.getResults();
        assertNotNull(res);
        assertTrue(res.getMessages(Message.Level.ERROR).isEmpty());
        KieBase kbase = ks.newKieContainer( kieBuilder.getKieModule().getReleaseId() ).getKieBase();
        KieSession session = kbase.newKieSession();
        pmmlRuntime = session.getKieRuntime(PMMLRuntime.class);
        assertNotNull(pmmlRuntime);
    }

    @Test
    public void evaluateRegressionWithTable() throws KiePMMLException {
        String modelName = "Sample for linear regression";
        commonEvaluate(20, 1950, STREET, modelName);
        commonEvaluate(20, 1950, CARPARK, modelName);
        commonEvaluate(59, 3750, STREET, modelName);
        commonEvaluate(35, 1800, CARPARK, modelName);
    }

    private void commonEvaluate(int age, double salary, String carLocation, String modelName) throws KiePMMLException {
        final Optional<KiePMMLModel> model = pmmlRuntime.getModel(modelName);
        assertTrue(model.isPresent());
        assertEquals(PMML_MODEL.REGRESSION_MODEL, model.get().getPmmlMODEL());
        assertTrue(model.get() instanceof KiePMMLRegressionModel);
        KiePMMLRegressionModel kiePMMLRegressionModel = (KiePMMLRegressionModel)model.get();
        assertEquals(1,kiePMMLRegressionModel.getRegressionTables().size());
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("age", age);
        inputData.put("salary", salary);
        inputData.put("car_location", carLocation);
        final PMMLRequestData pmmlRequestData = getPMMLRequestData(modelName, inputData);
        PMMLContext pmmlContext = new PMMLContextImpl(pmmlRequestData);
        PMML4Result retrieved = pmmlRuntime.evaluate(model.get(), pmmlContext);
        assertNotNull(retrieved);
        System.out.println(retrieved);
        assertNotNull(retrieved.getResultVariables());
        assertTrue(retrieved.getResultVariables().containsKey(TARGETFIELD_NAME));
        double expected = INTERCEPT + AGE_COEFF * age + SALARY_COEFF * salary;
        if (CARPARK.equals(carLocation)) {
            expected += CARPARK_COEFF;
        } else if (STREET.equals(carLocation)) {
            expected += STREET_COEFF;
        }
        double retrievedDouble = (double) retrieved.getResultVariables().get(TARGETFIELD_NAME);
        assertEquals(expected, retrievedDouble, 0.00001);
        System.out.println("Expected " + expected + " retrieved " + retrievedDouble);
    }
}