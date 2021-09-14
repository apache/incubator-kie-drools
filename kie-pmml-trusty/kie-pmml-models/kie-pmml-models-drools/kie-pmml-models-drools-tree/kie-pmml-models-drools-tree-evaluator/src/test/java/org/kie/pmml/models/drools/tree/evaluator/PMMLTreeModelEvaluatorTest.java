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
package org.kie.pmml.models.drools.tree.evaluator;

import java.util.HashMap;
import java.util.Map;

import org.dmg.pmml.PMML;
import org.dmg.pmml.tree.TreeModel;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.modelcompiler.ExecutableModelProject;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.builder.ReleaseId;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.pmml.PMMLRequestData;
import org.kie.internal.utils.KieHelper;
import org.kie.pmml.api.enums.PMML_MODEL;
import org.kie.pmml.api.enums.ResultCode;
import org.kie.pmml.api.runtime.PMMLContext;
import org.kie.pmml.compiler.testutils.TestUtils;
import org.kie.pmml.evaluator.core.PMMLContextImpl;
import org.kie.pmml.evaluator.core.utils.PMMLRequestDataBuilder;
import org.kie.pmml.models.drools.tree.compiler.executor.TreeModelImplementationProvider;
import org.kie.pmml.models.drools.tree.evaluator.implementations.HasKnowledgeBuilderMock;
import org.kie.pmml.models.drools.tree.model.KiePMMLTreeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PMMLTreeModelEvaluatorTest {

    private static final String SOURCE_1 = "TreeSample.pmml";
    private static final String PACKAGE_NAME = "PACKAGE_NAME";
    private static final Logger logger = LoggerFactory.getLogger(PMMLTreeModelEvaluatorTest.class);
    private static final String modelName = "golfing";
    private static final ReleaseId RELEASE_ID = new ReleaseIdImpl("org", "test", "1.0.0");
    private static final TreeModelImplementationProvider provider = new TreeModelImplementationProvider();
    private static KiePMMLTreeModel kiePMMLModel;
    private static PMMLTreeModelEvaluator evaluator;
    private static KieBase kieBase;
    private final String SCORE = "SCORE";
    private final String WILL_PLAY = "will play";
    private final String NO_PLAY = "no play";
    private final String MAY_PLAY = "may play";
    private final String WHO_PLAY = "who play";
    private final String HUMIDITY = "humidity";
    private final String TEMPERATURE = "temperature";
    private final String OUTLOOK = "outlook";
    private final String SUNNY = "sunny";
    private final String WINDY = "windy";
    private final String OVERCAST = "overcast";
    private final String RAIN = "rain";
    private final String TARGET_FIELD = "whatIdo";

    @BeforeClass
    public static void setUp() throws Exception {
        evaluator = new PMMLTreeModelEvaluator();
        final PMML pmml = TestUtils.loadFromFile(SOURCE_1);
        assertNotNull(pmml);
        assertEquals(1, pmml.getModels().size());
        assertTrue(pmml.getModels().get(0) instanceof TreeModel);
        KnowledgeBuilderImpl knowledgeBuilder = new KnowledgeBuilderImpl();
        kiePMMLModel = provider.getKiePMMLModel(PACKAGE_NAME,
                                                pmml.getDataDictionary(),
                                                pmml.getTransformationDictionary(),
                                                (TreeModel) pmml.getModels().get(0),
                                                new HasKnowledgeBuilderMock(knowledgeBuilder));
        kieBase = new KieHelper()
                .addContent(knowledgeBuilder.getPackageDescrs(kiePMMLModel.getKModulePackageName()).get(0))
                .setReleaseId(RELEASE_ID)
                .build( ExecutableModelProject.class );
        assertNotNull(kieBase);
    }

    @Test
    public void getPMMLModelType() {
        assertEquals(PMML_MODEL.TREE_MODEL, evaluator.getPMMLModelType());
    }

    @Test
    public void evaluateNull() throws Exception {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put(OUTLOOK, SUNNY);
        commonEvaluate(modelName, inputData, null);
        inputData.clear();
        inputData.put(OUTLOOK, SUNNY);
        inputData.put(TEMPERATURE, 65.0);
        commonEvaluate(modelName, inputData, null);
        inputData.clear();
        inputData.put(OUTLOOK, OVERCAST);
        commonEvaluate(modelName, inputData, null);
        inputData.clear();
        inputData.put(OUTLOOK, RAIN);
        commonEvaluate(modelName, inputData, null);
        inputData.clear();
        inputData.put(OUTLOOK, OVERCAST);
        inputData.put(TEMPERATURE, 80.0);
        commonEvaluate(modelName, inputData, null);
    }

    @Test
    public void evaluateWillPlay() throws Exception {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put(OUTLOOK, SUNNY);
        inputData.put(TEMPERATURE, 65.0);
        inputData.put(HUMIDITY, 65.0);
        commonEvaluate(modelName, inputData, WILL_PLAY);
    }

    @Test
    public void evaluateNoPlay() throws Exception {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put(OUTLOOK, SUNNY);
        inputData.put(TEMPERATURE, 65.0);
        inputData.put(HUMIDITY, 95.0);
        commonEvaluate(modelName, inputData, NO_PLAY);
        inputData.clear();
        inputData.put(OUTLOOK, SUNNY);
        inputData.put(HUMIDITY, 95.0);
        inputData.put(TEMPERATURE, 95.0);
        commonEvaluate(modelName, inputData, NO_PLAY);
        inputData.clear();
        inputData.put(OUTLOOK, SUNNY);
        inputData.put(TEMPERATURE, 95.0);
        commonEvaluate(modelName, inputData, NO_PLAY);
        inputData.clear();
        inputData.put(OUTLOOK, SUNNY);
        inputData.put(TEMPERATURE, 45.0);
        commonEvaluate(modelName, inputData, NO_PLAY);
        inputData.clear();
        inputData.put(OUTLOOK, RAIN);
        inputData.put(HUMIDITY, 45.0);
        commonEvaluate(modelName, inputData, NO_PLAY);
    }

    @Test
    public void evaluateMayPlay() throws Exception {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put(OUTLOOK, OVERCAST);
        inputData.put(TEMPERATURE, 70.0);
        inputData.put(HUMIDITY, 60.0);
        inputData.put(WINDY, "false");
        commonEvaluate(modelName, inputData, MAY_PLAY);
    }

    @Test
    public void evaluateWhoPlay() throws Exception {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put(TEMPERATURE, 75.0);
        inputData.put(WINDY, "true");
        inputData.put(HUMIDITY, 75.0);
        commonEvaluate(modelName, inputData, WHO_PLAY);
        inputData.clear();
        inputData.put(WINDY, "false");
        inputData.put(TEMPERATURE, 65.0);
        inputData.put(HUMIDITY, 75.0);
        commonEvaluate(modelName, inputData, WHO_PLAY);
    }

    private void commonEvaluate(String modelName, Map<String, Object> inputData, String expectedScore) {
        final PMMLRequestData pmmlRequestData = getPMMLRequestData(modelName, inputData);
        PMMLContext pmmlContext = new PMMLContextImpl(pmmlRequestData);
        commonEvaluate(pmmlContext, expectedScore);
    }

    private void commonEvaluate(PMMLContext pmmlContext, String expectedScore) {
        PMML4Result retrieved = evaluator.evaluate(kieBase, kiePMMLModel, pmmlContext);
        assertNotNull(retrieved);
        logger.trace(retrieved.toString());
        assertEquals(TARGET_FIELD, retrieved.getResultObjectName());
        final Map<String, Object> resultVariables = retrieved.getResultVariables();
        assertNotNull(resultVariables);
        if (expectedScore != null) {
            assertEquals(ResultCode.OK.getName(), retrieved.getResultCode());
            assertFalse(resultVariables.isEmpty());
            assertTrue(resultVariables.containsKey(TARGET_FIELD));
            assertEquals(expectedScore, resultVariables.get(TARGET_FIELD));
        } else {
            assertEquals(ResultCode.FAIL.getName(), retrieved.getResultCode());
            assertFalse(resultVariables.containsKey(TARGET_FIELD));
        }
    }

    private PMMLRequestData getPMMLRequestData(String modelName, Map<String, Object> parameters) {
        String correlationId = "CORRELATION_ID";
        PMMLRequestDataBuilder pmmlRequestDataBuilder = new PMMLRequestDataBuilder(correlationId, modelName);
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            Object pValue = entry.getValue();
            Class class1 = pValue.getClass();
            pmmlRequestDataBuilder.addParameter(entry.getKey(), pValue, class1);
        }
        return pmmlRequestDataBuilder.build();
    }
}
