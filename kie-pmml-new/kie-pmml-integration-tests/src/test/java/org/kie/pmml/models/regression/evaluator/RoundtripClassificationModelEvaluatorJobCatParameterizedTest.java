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
public class RoundtripClassificationModelEvaluatorJobCatParameterizedTest {

    private static final Logger logger = LoggerFactory.getLogger(RoundtripClassificationModelEvaluatorJobCatParameterizedTest.class);

    private static final String SOURCE = "JobCat.pmml";

    private double age;
    private double work;
    private String sex;
    private String minority;
    private String expected;

    private PMMLRuntime pmmlRuntime;

    private String releaseId;

    public RoundtripClassificationModelEvaluatorJobCatParameterizedTest(double age, double work, String sex, String minority, String expected) {
        this.age = age;
        this.work = work;
        this.sex = sex;
        this.minority = minority;
        this.expected = expected;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {27.0, 3.5, "0", "0", "professional"},
                {64.0, 27.4, "0", "0", "clerical"},
                {53.0, 12.6, "1", "1", "clerical"},
                {14.0, 0.5, "1", "0", "professional"},
                {51.0, 20.0, "0", "1", "clerical"},
        });
    }

    @Test
    public void evaluateClassification() {
        commonSetup(SOURCE);
        String modelName = "Sample for logistic regression";
        PMMLRequestData pmmlRequestData = new PMMLRequestData("123", modelName);
        pmmlRequestData.addRequestParam("age", age);
        pmmlRequestData.addRequestParam("work", work);
        pmmlRequestData.addRequestParam("sex", sex);
        pmmlRequestData.addRequestParam("minority", minority);
        PMMLContext pmmlContext = new PMMLContextImpl(pmmlRequestData);
        final KiePMMLModel model = pmmlRuntime.getModel(pmmlContext.getRequestData().getModelName()).orElseThrow(() -> new KiePMMLException("Failed to retrieve the model"));
        assertEquals(PMML_MODEL.REGRESSION_MODEL, model.getPmmlMODEL());
        assertTrue(model instanceof KiePMMLRegressionModel);
        PMML4Result retrieved = pmmlRuntime.evaluate(model, pmmlContext, releaseId);
        assertNotNull(retrieved);
        assertEquals("OK", retrieved.getResultCode());
        assertEquals("jobcat", model.getTargetField());
        assertEquals("jobcat", retrieved.getResultObjectName());
        assertTrue(retrieved.getResultVariables().containsKey("jobcat"));
        assertEquals(expected, retrieved.getResultVariables().get("jobcat"));
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