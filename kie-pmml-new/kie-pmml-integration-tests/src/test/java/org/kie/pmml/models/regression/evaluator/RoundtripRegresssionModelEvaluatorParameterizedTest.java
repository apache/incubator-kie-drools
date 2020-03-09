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

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
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
import static org.kie.test.util.filesystem.FileUtils.getFile;

@RunWith(Parameterized.class)
public class RoundtripRegresssionModelEvaluatorParameterizedTest {

    private static final Logger logger = LoggerFactory.getLogger(RoundtripRegresssionModelEvaluatorParameterizedTest.class);

    private static final String SOURCE = "test_regression.pmml";

    private static final double COMPARISON_DELTA = 0.00001;

    private double fld1;
    private double fld2;
    private String fld3;

    private PMMLRuntime pmmlRuntime;

    private String releaseId;

    public RoundtripRegresssionModelEvaluatorParameterizedTest(double fld1, double fld2, String fld3) {
        this.fld1 = fld1;
        this.fld2 = fld2;
        this.fld3 = fld3;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {1.0, 1.0, "x"},
                {0.9, 0.3, "x"},
                {12.0, 25.0, "x"},
                {0.2, 0.1, "x"},
                {5, 8, "y"},
        });
    }

    private static double fld3Coefficient(String fld3) {
        switch (fld3) {
            case "x":
                return -3.0; // Coefficient for the "x" CategoricalPredictor
            case "y":
                return 3.0; // Coefficient for the "y" CategoricalPredictor
            default:
                return 0;
        }
    }

    @Test
    public void evaluateRegression() {
        commonSetup(SOURCE);
        String modelName = "LinReg";
        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", modelName);
        pmmlRequestData.addRequestParam("fld1", fld1);
        pmmlRequestData.addRequestParam("fld2", fld2);
        pmmlRequestData.addRequestParam("fld3", fld3);
        PMMLContext pmmlContext = new PMMLContextImpl(pmmlRequestData);
        final KiePMMLModel model = pmmlRuntime.getModel(pmmlContext.getRequestData().getModelName()).orElseThrow(() -> new KiePMMLException("Failed to retrieve the model"));
        assertEquals(PMML_MODEL.REGRESSION_MODEL, model.getPmmlMODEL());
        assertTrue(model instanceof KiePMMLRegressionModel);
        assertEquals("fld4", model.getTargetField());
        PMML4Result retrieved = pmmlRuntime.evaluate(model, pmmlContext, releaseId);
        assertNotNull(retrieved);
        assertEquals("OK", retrieved.getResultCode());

        assertEquals(model.getTargetField(), retrieved.getResultObjectName());
        assertNotNull(retrieved.getResultVariables().get(model.getTargetField()));
        double retrievedDouble = (double) retrieved.getResultVariables().get(model.getTargetField());
        final double expectedValue = simpleRegressionResult(fld1, fld2, fld3);
        assertEquals(expectedValue, retrievedDouble, COMPARISON_DELTA);
    }

    private double simpleRegressionResult(double fld1, double fld2, String fld3) {
        double expectedFld1 = Math.pow(fld1, 2) * 5.0;
        double expectedFld2 = fld2 * 2.0;
        double expectedFldPredictor = 0.4 * fld1 * fld2;

        double result = 0.5 + expectedFld1 + expectedFld2 + fld3Coefficient(fld3) + expectedFldPredictor;
        result = 1.0 / (1.0 + Math.exp(-result));
        return result;
    }

    private void commonSetup(String fileName) {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write(ResourceFactory.newFileResource(getFile(fileName)).setResourceType(ResourceType.PMML));
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
}