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

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.reteoo.builder.NodeFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;
import org.kie.api.pmml.PMML4Result;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.pmml.api.enums.ResultCode;
import org.kie.pmml.evaluator.api.exceptions.KiePMMLModelException;
import org.kie.pmml.models.drools.executor.KiePMMLStatusHolder;
import org.kie.test.util.filesystem.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class DrlTreeSampleTest {

    private static final String SOURCE_1 = "TreeGenerated.drl";
    private static final Logger logger = LoggerFactory.getLogger(DrlTreeSampleTest.class);
    private static final String PACKAGE = "golfing";
    private static final String modelName = "golfing";
    // Expected scores
    private static final String WILL_PLAY = "will play";
    private static final String NO_PLAY = "no play";
    private static final String MAY_PLAY = "may play";
    private static final String WHO_PLAY = "who play";
    // OUTLOOK Inputs
    private static final String SUNNY = "sunny";
    private static final String OVERCAST = "overcast";
    private static final String RAIN = "rain";
    // WINDY Inputs
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static KieBase kbase;
    private final String SCORE = "SCORE";
    private final String HUMIDITY = "HUMIDITY";
    private final String TEMPERATURE = "TEMPERATURE";
    private final String OUTLOOK = "OUTLOOK";
    private final String WINDY = "WINDY";
    private final String TARGET_FIELD = "whatIdo";
    private String outlook;
    private Double temperature;
    private Double humidity;
    private String windy;
    private String expectedResult;

    public DrlTreeSampleTest(String outlook, Double temperature, Double humidity,
                             String windy, String expectedResult) {
        this.outlook = outlook;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windy = windy;
        this.expectedResult = expectedResult;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                // null expected
                {SUNNY, null, null, null, null},
                {SUNNY, 65.0, null, null, null},
                {SUNNY, null, 45.0, null, null},
                {OVERCAST, null, null, null, null},
                {RAIN, null, null, null, null},
                {OVERCAST, 80.0, null, null, null},
                // will play expected
                {SUNNY, 65.0, 65.0, null, WILL_PLAY},
                // no play expected
                {SUNNY, 65.0, 95.0, null, NO_PLAY},
                {SUNNY, 95.0, 95.0, null, NO_PLAY},
                {SUNNY, 95.0, null, null, NO_PLAY},
                {SUNNY, 45.0, null, null, NO_PLAY},
                {SUNNY, 96.5, 45.0, null, NO_PLAY},
                // may play expected
                {OVERCAST, 70.0, 60.0, FALSE, MAY_PLAY},
                // who play expected
                {null, 75.0, 75.0, TRUE, WHO_PLAY},
                {null, 65.0, 75.0, FALSE, WHO_PLAY}
        });
    }

    @BeforeClass
    public static void setUp() throws Exception {
        File drlFile = FileUtils.getFile(SOURCE_1);
        String content = new String(Files.readAllBytes(drlFile.toPath()));
        kbase = loadKnowledgeBaseFromString(null, null, null, content);
    }

    private static KieBase loadKnowledgeBaseFromString(KnowledgeBuilderConfiguration config, KieBaseConfiguration kBaseConfig, NodeFactory nodeFactory, String... drlContentStrings) {
        KnowledgeBuilder kbuilder = config == null ? KnowledgeBuilderFactory.newKnowledgeBuilder() : KnowledgeBuilderFactory.newKnowledgeBuilder(config);
        for (String drlContentString : drlContentStrings) {
            kbuilder.add(ResourceFactory.newByteArrayResource(drlContentString
                                                                      .getBytes()), ResourceType.DRL);
        }

        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }
        if (kBaseConfig == null) {
            kBaseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        }
        InternalKnowledgeBase kbase = kBaseConfig == null ? KnowledgeBaseFactory.newKnowledgeBase() : KnowledgeBaseFactory.newKnowledgeBase(kBaseConfig);
        if (nodeFactory != null) {
            kbase.getConfiguration().getComponentFactory().setNodeFactoryProvider(nodeFactory);
        }
        kbase.addPackages(kbuilder.getKnowledgePackages());
        return kbase;
    }

    @Test
    public void testTreeSample() {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put(OUTLOOK, outlook);
        inputData.put(TEMPERATURE, temperature);
        inputData.put(HUMIDITY, humidity);
        inputData.put(WINDY, windy);
        commonExecute(inputData);
    }

    private void commonExecute(final Map<String, Object> inputData) {
        KieSession kSession = kbase.newKieSession();
        List<Object> executionParams = new ArrayList<>();
        KiePMMLStatusHolder statusHolder = new KiePMMLStatusHolder();
        executionParams.add(statusHolder);
        PMML4Result pmml4Result = new PMML4Result();
        pmml4Result.setResultCode(ResultCode.FAIL.getName());
        pmml4Result.setResultObjectName(TARGET_FIELD);
        for (Map.Entry<String, Object> entry : inputData.entrySet()) {
            if (entry.getValue() != null) {
                try {
                    FactType factType = kSession.getKieBase().getFactType(PACKAGE, entry.getKey());
                    Object toAdd = factType.newInstance();
                    factType.set(toAdd, "value", entry.getValue());
                    executionParams.add(toAdd);
                } catch (Exception e) {
                    throw new KiePMMLModelException(e.getMessage(), e);
                }
            }
        }
        executionParams.forEach(kSession::insert);
        kSession.setGlobal("$pmml4Result", pmml4Result);
        kSession.fireAllRules();
        if (expectedResult == null) {
            assertEquals(ResultCode.FAIL.getName(), pmml4Result.getResultCode());
        } else {
            assertEquals(ResultCode.OK.getName(), pmml4Result.getResultCode());
            assertNotNull(pmml4Result.getResultVariables().get(TARGET_FIELD));
            assertEquals(expectedResult, pmml4Result.getResultVariables().get(TARGET_FIELD));
        }
    }
}
