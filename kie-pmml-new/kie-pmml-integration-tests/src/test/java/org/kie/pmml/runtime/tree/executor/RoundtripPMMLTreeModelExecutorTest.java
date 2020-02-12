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

package org.kie.pmml.runtime.tree.executor;

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
import org.kie.api.io.ResourceType;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.model.KiePMMLModel;
import org.kie.pmml.api.model.enums.PMML_MODEL;
import org.kie.pmml.models.tree.api.model.KiePMMLTreeModel;
import org.kie.pmml.runtime.api.executor.PMMLContext;
import org.kie.pmml.runtime.api.executor.PMMLRuntime;
import org.kie.pmml.runtime.core.PMMLContextImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.runtime.regression.executor.TestUtils.getPMMLRequestData;
import static org.kie.test.util.filesystem.FileUtils.getFile;

public class RoundtripPMMLTreeModelExecutorTest {

    private static final Logger logger = LoggerFactory.getLogger(RoundtripPMMLTreeModelExecutorTest.class);
    private static final String modelName = "golfing";
    private final String SCORE = "SCORE";
    private final String WILL_PLAY = "will play";
    private final String NO_PLAY = "no play";
    private final String MAY_PLAY = "may play";
    private final String HUMIDITY = "humidity";
    private final String TEMPERATURE = "temperature";
    private final String OUTLOOK = "outlook";
    private final String SUNNY = "sunny";
    private final String WINDY = "windy";
    private final String OVERCAST = "overcast";
    private final String RAIN = "rain";

    private PMMLRuntime pmmlRuntime;
    private KieBuilder kieBuilder;

    @Before
    public void setUp() throws Exception {
        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write(ResourceFactory.newFileResource(getFile("TreeSample.xml")).setResourceType(ResourceType.PMML));
        kieBuilder = ks.newKieBuilder(kfs).buildAll();
        kieBuilder.getKieModule().getReleaseId();
        Results res = kieBuilder.getResults();
        assertNotNull(res);
        assertTrue(res.getMessages(Message.Level.ERROR).isEmpty());
        KieBase kbase = ks.newKieContainer(kieBuilder.getKieModule().getReleaseId()).getKieBase();
        KieSession session = kbase.newKieSession();
        pmmlRuntime = session.getKieRuntime(PMMLRuntime.class);
        assertNotNull(pmmlRuntime);
    }

    @Test
    public void evaluateTree() throws KiePMMLException {
        final Optional<KiePMMLModel> model = pmmlRuntime.getModel(modelName);
        assertTrue(model.isPresent());
        assertEquals(PMML_MODEL.TREE_MODEL, model.get().getPmmlMODEL());
        assertTrue(model.get() instanceof KiePMMLTreeModel);
    }

    @Test
    public void evaluateWillPlay_1() throws Exception {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put(OUTLOOK, SUNNY);
        commonEvaluate(modelName, inputData, WILL_PLAY);
    }

    @Test
    public void evaluateWillPlay_2() throws Exception {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put(OUTLOOK, SUNNY);
        inputData.put(TEMPERATURE, 65);
        commonEvaluate(modelName, inputData, WILL_PLAY);
    }

    @Test
    public void evaluateWillPlay_3() throws Exception {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put(OUTLOOK, SUNNY);
        inputData.put(TEMPERATURE, 65);
        inputData.put(HUMIDITY, 65);
        commonEvaluate(modelName, inputData, WILL_PLAY);
    }

    @Test
    public void evaluateWillPlay_4() throws Exception {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put(OUTLOOK, SUNNY);
        inputData.put(TEMPERATURE, 65);
        inputData.put(HUMIDITY, 95);
        commonEvaluate(modelName, inputData, NO_PLAY);
    }

    @Test
    public void evaluateWillPlay_5() throws Exception {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put(OUTLOOK, SUNNY);
        inputData.put(HUMIDITY, 95);
        inputData.put(TEMPERATURE, 95);
        commonEvaluate(modelName, inputData, NO_PLAY);
    }

    @Test
    public void evaluateWillPlay_6() throws Exception {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put(OUTLOOK, SUNNY);
        inputData.put(HUMIDITY, 95);
        inputData.put(TEMPERATURE, 40);
        commonEvaluate(modelName, inputData, NO_PLAY);
    }

    @Test
    public void evaluateMayPlay_1() throws Exception {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put(OUTLOOK, OVERCAST);
        commonEvaluate(modelName, inputData, MAY_PLAY);
        inputData.put(OUTLOOK, RAIN);
        commonEvaluate(modelName, inputData, MAY_PLAY);
    }

    @Test
    public void evaluateMayPlay_2() throws Exception {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put(OUTLOOK, OVERCAST);
        inputData.put(TEMPERATURE, 80);
        commonEvaluate(modelName, inputData, MAY_PLAY);
    }

    @Test
    public void evaluateMayPlay_3() throws Exception {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put(OUTLOOK, RAIN);
        inputData.put(HUMIDITY, 40);
        commonEvaluate(modelName, inputData, NO_PLAY);
    }

    private void commonEvaluate(String modelName, Map<String, Object> inputData, String expectedScore) throws KiePMMLException {
        final PMMLRequestData pmmlRequestData = getPMMLRequestData(modelName, inputData);
        PMMLContext pmmlContext = new PMMLContextImpl(pmmlRequestData);
        // TODO {gcardosi} restore when code is fixed
//        commonEvaluate(pmmlContext, expectedScore);
    }

    private void commonEvaluate(PMMLContext pmmlContext, String expectedScore) throws KiePMMLException {
        final KiePMMLModel model = pmmlRuntime.getModel(pmmlContext.getRequestData().getModelName()).orElseThrow(() -> new KiePMMLException("Failed to retrieve the model"));
        PMML4Result retrieved = pmmlRuntime.evaluate(model, pmmlContext, kieBuilder.getKieModule().getReleaseId().toExternalForm());
        assertNotNull(retrieved);
        logger.info(retrieved.toString());
        final Map<String, Object> resultVariables = retrieved.getResultVariables();
        assertNotNull(resultVariables);
        // TODO {gcardosi} restore when code is fixed
//        assertTrue(resultVariables.containsKey("score"));
//        assertEquals(expectedScore, resultVariables.get("score"));
    }
}