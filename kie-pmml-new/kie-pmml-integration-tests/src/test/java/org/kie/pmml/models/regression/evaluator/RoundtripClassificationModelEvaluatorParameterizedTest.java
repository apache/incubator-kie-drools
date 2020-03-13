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
import java.util.HashMap;
import java.util.Map;

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
public class RoundtripClassificationModelEvaluatorParameterizedTest {

    private static final Logger logger = LoggerFactory.getLogger(RoundtripClassificationModelEvaluatorParameterizedTest.class);

    private static final String SOURCE = "test_regression_clax.pmml";

    private static final double COMPARISON_DELTA = 0.00001;

    private double fld1;
    private double fld2;
    private String fld3;

    private PMMLRuntime pmmlRuntime;

    private String releaseId;

    public RoundtripClassificationModelEvaluatorParameterizedTest(double fld1, double fld2, String fld3) {
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
                return -3.0; // Coefficient for the "x" CategoricalPredictor; see source file test_regression_clax.pmml
            case "y":
                return 3.0; // Coefficient for the "y" CategoricalPredictor; see source file test_regression_clax.pmml
            default:
                return 0;
        }
    }

    @Test
    public void evaluateClassification() {
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
        PMML4Result retrieved = pmmlRuntime.evaluate(model, pmmlContext, releaseId);
        assertNotNull(retrieved);
        assertEquals("OK", retrieved.getResultCode());

        Map<String, Double> expectedProbabilities = categoryProbabilities(fld1, fld2, fld3);
        String maxCategory = null;
        double maxValue = Double.MIN_VALUE;
        for (String key : expectedProbabilities.keySet()) {
            double value = expectedProbabilities.get(key);
            if (value > maxValue) {
                maxCategory = key;
                maxValue = value;
            }
        }
        String expected = "cat" + maxCategory;

        assertEquals("fld4", model.getTargetField());
        assertEquals("fld4", retrieved.getResultObjectName());
        assertTrue(retrieved.getResultVariables().containsKey("fld4"));
        assertEquals(expected, retrieved.getResultVariables().get("fld4"));

        assertNotNull(retrieved.getResultVariables().get("RegOut"));
        assertNotNull(retrieved.getResultVariables().get("RegProb"));
        assertNotNull(retrieved.getResultVariables().get("RegProbA"));

        String regOut = (String) retrieved.getResultVariables().get("RegOut");
        double regProb = (double) retrieved.getResultVariables().get("RegProb");
        double regProbA = (double) retrieved.getResultVariables().get("RegProbA");
        assertEquals(expected, regOut);
        assertEquals(maxValue, regProb, COMPARISON_DELTA);
        assertEquals(expectedProbabilities.get("A"), regProbA, COMPARISON_DELTA);
    }

    private Map<String, Double> categoryProbabilities(double fld1, double fld2, String fld3) {
        final Map<String, RegressionInterface> regressionTables = new HashMap<>();
        regressionTables.put("A", (f1, f2, f3) -> 0.1 + fld1 + fld2 + fld3Coefficient(fld3));
        regressionTables.put("B", (f1, f2, f3) -> 0.2 + 2 * fld1 + 2 * fld2 + fld3Coefficient(fld3));
        regressionTables.put("C", (f1, f2, f3) -> 0.3 + 3 * fld1 + 3 * fld2 + fld3Coefficient(fld3));
        regressionTables.put("D", (f1, f2, f3) -> 5.0); // Intercept for the RegressionTable with targetCategory="catD"

        final Map<String, Double> regressionTablesValues = new HashMap<>();
        double sum = 0;
        for (String item : regressionTables.keySet()) {
            double value = regressionTables.get(item).apply(fld1, fld2, fld3);
            value = Math.exp(value);
            regressionTablesValues.put(item, value);
            sum += value;
        }

        for (String item : regressionTables.keySet()) {
            regressionTablesValues.put(item, regressionTablesValues.get(item) / sum);
        }

        return regressionTablesValues;
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

    @FunctionalInterface
    private interface RegressionInterface {

        Double apply(double fld1, double fld2, String fld3);
    }
}