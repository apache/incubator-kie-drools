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

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.builder.conf.impl.ResourceConfigurationImpl;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.io.impl.InputStreamResource;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.model.KiePMMLModel;
import org.kie.pmml.api.model.enums.PMML_MODEL;
import org.kie.pmml.api.model.regression.KiePMMLRegressionModel;
import org.kie.pmml.assembler.service.PMMLAssemblerService;
import org.kie.pmml.runtime.core.executor.PMMLRuntimeImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.runtime.regression.executor.PMMLIsRegresssionModelExecutor.evaluateRegression;
import static org.kie.pmml.runtime.regression.executor.TestUtils.AGE_COEFF;
import static org.kie.pmml.runtime.regression.executor.TestUtils.CARPARK;
import static org.kie.pmml.runtime.regression.executor.TestUtils.CARPARK_COEFF;
import static org.kie.pmml.runtime.regression.executor.TestUtils.INTERCEPT;
import static org.kie.pmml.runtime.regression.executor.TestUtils.SALARY_COEFF;
import static org.kie.pmml.runtime.regression.executor.TestUtils.STREET;
import static org.kie.pmml.runtime.regression.executor.TestUtils.STREET_COEFF;
import static org.kie.pmml.runtime.regression.executor.TestUtils.TARGETFIELD_NAME;
import static org.kie.pmml.runtime.regression.executor.TestUtils.getKiePMMLRegressionTable;
import static org.kie.pmml.runtime.regression.executor.TestUtils.getPMMLRequestData;
import static org.kie.test.util.filesystem.FileUtils.getInputStream;

public class RoundtripPMMLIsRegresssionModelExecutorTest {

    private static final PMMLAssemblerService pmmlAssemblerService = new PMMLAssemblerService();
    private PMMLRuntimeImpl pmmlRuntime;

    private Resource firstSampleResource;

    @Before
    public void setUp() throws Exception {
        firstSampleResource = new InputStreamResource(getInputStream("LinearRegressionSample.xml"));
        final KnowledgeBaseImpl knowledgeBase = new KnowledgeBaseImpl("TESTING", new RuleBaseConfiguration());
        final KnowledgeBuilderImpl knowledgeBuilder = new KnowledgeBuilderImpl(knowledgeBase);
        pmmlAssemblerService.addResource(knowledgeBuilder, firstSampleResource, ResourceType.PMML, new ResourceConfigurationImpl());
        pmmlRuntime = new PMMLRuntimeImpl(knowledgeBase);
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
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("age", age);
        inputData.put("salary", salary);
        inputData.put("car_location", carLocation);
        final PMMLRequestData pmmlRequestData = getPMMLRequestData(inputData);
        PMML4Result retrieved = evaluateRegression(kiePMMLRegressionModel.getTargetFieldName(), getKiePMMLRegressionTable(), pmmlRequestData);
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