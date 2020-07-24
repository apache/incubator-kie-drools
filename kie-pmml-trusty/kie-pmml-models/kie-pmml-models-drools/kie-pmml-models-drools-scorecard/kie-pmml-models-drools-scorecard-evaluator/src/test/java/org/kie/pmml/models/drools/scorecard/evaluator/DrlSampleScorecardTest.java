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
package org.kie.pmml.models.drools.scorecard.evaluator;

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
import org.kie.pmml.commons.enums.ResultCode;
import org.kie.pmml.evaluator.api.exceptions.KiePMMLModelException;
import org.kie.pmml.models.drools.executor.KiePMMLStatusHolder;
import org.kie.test.util.filesystem.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class DrlSampleScorecardTest {

    private static final String SOURCE_1 = "ScorecardSample.drl";
    private static final Logger logger = LoggerFactory.getLogger(DrlSampleScorecardTest.class);
    private static final String PACKAGE = "sample_score";
    private static final String TARGET_FIELD = "Species";
    private static KieBase kbase;
    private double age;
    private String occupation;
    private String residenceState;
    private boolean validLicense;
    private double expectedResult;

    public DrlSampleScorecardTest(double age, String occupation, String residenceState,
                                  boolean validLicense, double expectedResult) {
        this.age = age;
        this.occupation = occupation;
        this.residenceState = residenceState;
        this.validLicense = validLicense;
        this.expectedResult = expectedResult;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {4.0, "SKYDIVER", "AP", true, -8.655},
                {4.0, "SKYDIVER", "AP", false, -10.655},
                {4.0, "SKYDIVER", "KN", true, 11.345},
                {4.0, "SKYDIVER", "KN", false, 9.345},
                {4.0, "SKYDIVER", "TN", true, 6.345000000000001},
                {4.0, "SKYDIVER", "TN", false, 4.345000000000001},
                {4.0, "ASTRONAUT", "AP", true, -8.655},
                {4.0, "ASTRONAUT", "AP", false, -10.655},
                {4.0, "ASTRONAUT", "KN", true, 11.345},
                {4.0, "ASTRONAUT", "KN", false, 9.345},
                {4.0, "ASTRONAUT", "TN", true, 6.345000000000001},
                {4.0, "ASTRONAUT", "TN", false, 4.345000000000001},
                {4.0, "PROGRAMMER", "AP", true, 6.345000000000001},
                {4.0, "PROGRAMMER", "AP", false, 4.345000000000001},
                {4.0, "PROGRAMMER", "KN", true, 26.345},
                {4.0, "PROGRAMMER", "KN", false, 24.345},
                {4.0, "PROGRAMMER", "TN", true, 21.345},
                {4.0, "PROGRAMMER", "TN", false, 19.345},
                {4.0, "TEACHER", "AP", true, 11.344999999999999},
                {4.0, "TEACHER", "AP", false, 9.344999999999999},
                {4.0, "TEACHER", "KN", true, 31.345},
                {4.0, "TEACHER", "KN", false, 29.345},
                {4.0, "TEACHER", "TN", true, 26.345},
                {4.0, "TEACHER", "TN", false, 24.345},
                {4.0, "INSTRUCTOR", "AP", true, 11.344999999999999},
                {4.0, "INSTRUCTOR", "AP", false, 9.344999999999999},
                {4.0, "INSTRUCTOR", "KN", true, 31.345},
                {4.0, "INSTRUCTOR", "KN", false, 29.345},
                {4.0, "INSTRUCTOR", "TN", true, 26.345},
                {4.0, "INSTRUCTOR", "TN", false, 24.345},
                {5.0, "SKYDIVER", "AP", true, -8.655},
                {5.0, "SKYDIVER", "AP", false, -10.655},
                {5.0, "SKYDIVER", "KN", true, 11.345},
                {5.0, "SKYDIVER", "KN", false, 9.345},
                {5.0, "SKYDIVER", "TN", true, 6.345000000000001},
                {5.0, "SKYDIVER", "TN", false, 4.345000000000001},
                {5.0, "ASTRONAUT", "AP", true, -8.655},
                {5.0, "ASTRONAUT", "AP", false, -10.655},
                {5.0, "ASTRONAUT", "KN", true, 11.345},
                {5.0, "ASTRONAUT", "KN", false, 9.345},
                {5.0, "ASTRONAUT", "TN", true, 6.345000000000001},
                {5.0, "ASTRONAUT", "TN", false, 4.345000000000001},
                {5.0, "PROGRAMMER", "AP", true, 6.345000000000001},
                {5.0, "PROGRAMMER", "AP", false, 4.345000000000001},
                {5.0, "PROGRAMMER", "KN", true, 26.345},
                {5.0, "PROGRAMMER", "KN", false, 24.345},
                {5.0, "PROGRAMMER", "TN", true, 21.345},
                {5.0, "PROGRAMMER", "TN", false, 19.345},
                {5.0, "TEACHER", "AP", true, 11.344999999999999},
                {5.0, "TEACHER", "AP", false, 9.344999999999999},
                {5.0, "TEACHER", "KN", true, 31.345},
                {5.0, "TEACHER", "KN", false, 29.345},
                {5.0, "TEACHER", "TN", true, 26.345},
                {5.0, "TEACHER", "TN", false, 24.345},
                {5.0, "INSTRUCTOR", "AP", true, 11.344999999999999},
                {5.0, "INSTRUCTOR", "AP", false, 9.344999999999999},
                {5.0, "INSTRUCTOR", "KN", true, 31.345},
                {5.0, "INSTRUCTOR", "KN", false, 29.345},
                {5.0, "INSTRUCTOR", "TN", true, 26.345},
                {5.0, "INSTRUCTOR", "TN", false, 24.345},
                {6.0, "SKYDIVER", "AP", true, 11.344999999999999},
                {6.0, "SKYDIVER", "AP", false, 9.344999999999999},
                {6.0, "SKYDIVER", "KN", true, 31.345},
                {6.0, "SKYDIVER", "KN", false, 29.345},
                {6.0, "SKYDIVER", "TN", true, 26.345},
                {6.0, "SKYDIVER", "TN", false, 24.345},
                {6.0, "ASTRONAUT", "AP", true, 11.344999999999999},
                {6.0, "ASTRONAUT", "AP", false, 9.344999999999999},
                {6.0, "ASTRONAUT", "KN", true, 31.345},
                {6.0, "ASTRONAUT", "KN", false, 29.345},
                {6.0, "ASTRONAUT", "TN", true, 26.345},
                {6.0, "ASTRONAUT", "TN", false, 24.345},
                {6.0, "PROGRAMMER", "AP", true, 26.345},
                {6.0, "PROGRAMMER", "AP", false, 24.345},
                {6.0, "PROGRAMMER", "KN", true, 46.345},
                {6.0, "PROGRAMMER", "KN", false, 44.345},
                {6.0, "PROGRAMMER", "TN", true, 41.345},
                {6.0, "PROGRAMMER", "TN", false, 39.345},
                {6.0, "TEACHER", "AP", true, 31.345},
                {6.0, "TEACHER", "AP", false, 29.345},
                {6.0, "TEACHER", "KN", true, 51.345},
                {6.0, "TEACHER", "KN", false, 49.345},
                {6.0, "TEACHER", "TN", true, 46.345},
                {6.0, "TEACHER", "TN", false, 44.345},
                {6.0, "INSTRUCTOR", "AP", true, 31.345},
                {6.0, "INSTRUCTOR", "AP", false, 29.345},
                {6.0, "INSTRUCTOR", "KN", true, 51.345},
                {6.0, "INSTRUCTOR", "KN", false, 49.345},
                {6.0, "INSTRUCTOR", "TN", true, 46.345},
                {6.0, "INSTRUCTOR", "TN", false, 44.345},
                {13.0, "SKYDIVER", "AP", true, 21.345},
                {13.0, "SKYDIVER", "AP", false, 19.345},
                {13.0, "SKYDIVER", "KN", true, 41.345},
                {13.0, "SKYDIVER", "KN", false, 39.345},
                {13.0, "SKYDIVER", "TN", true, 36.345},
                {13.0, "SKYDIVER", "TN", false, 34.345},
                {13.0, "ASTRONAUT", "AP", true, 21.345},
                {13.0, "ASTRONAUT", "AP", false, 19.345},
                {13.0, "ASTRONAUT", "KN", true, 41.345},
                {13.0, "ASTRONAUT", "KN", false, 39.345},
                {13.0, "ASTRONAUT", "TN", true, 36.345},
                {13.0, "ASTRONAUT", "TN", false, 34.345},
                {13.0, "PROGRAMMER", "AP", true, 36.345},
                {13.0, "PROGRAMMER", "AP", false, 34.345},
                {13.0, "PROGRAMMER", "KN", true, 56.345},
                {13.0, "PROGRAMMER", "KN", false, 54.345},
                {13.0, "PROGRAMMER", "TN", true, 51.345},
                {13.0, "PROGRAMMER", "TN", false, 49.345},
                {13.0, "TEACHER", "AP", true, 41.345},
                {13.0, "TEACHER", "AP", false, 39.345},
                {13.0, "TEACHER", "KN", true, 61.345},
                {13.0, "TEACHER", "KN", false, 59.345},
                {13.0, "TEACHER", "TN", true, 56.345},
                {13.0, "TEACHER", "TN", false, 54.345},
                {13.0, "INSTRUCTOR", "AP", true, 41.345},
                {13.0, "INSTRUCTOR", "AP", false, 39.345},
                {13.0, "INSTRUCTOR", "KN", true, 61.345},
                {13.0, "INSTRUCTOR", "KN", false, 59.345},
                {13.0, "INSTRUCTOR", "TN", true, 56.345},
                {13.0, "INSTRUCTOR", "TN", false, 54.345},
                {43.0, "SKYDIVER", "AP", true, 21.345},
                {43.0, "SKYDIVER", "AP", false, 19.345},
                {43.0, "SKYDIVER", "KN", true, 41.345},
                {43.0, "SKYDIVER", "KN", false, 39.345},
                {43.0, "SKYDIVER", "TN", true, 36.345},
                {43.0, "SKYDIVER", "TN", false, 34.345},
                {43.0, "ASTRONAUT", "AP", true, 21.345},
                {43.0, "ASTRONAUT", "AP", false, 19.345},
                {43.0, "ASTRONAUT", "KN", true, 41.345},
                {43.0, "ASTRONAUT", "KN", false, 39.345},
                {43.0, "ASTRONAUT", "TN", true, 36.345},
                {43.0, "ASTRONAUT", "TN", false, 34.345},
                {43.0, "PROGRAMMER", "AP", true, 36.345},
                {43.0, "PROGRAMMER", "AP", false, 34.345},
                {43.0, "PROGRAMMER", "KN", true, 56.345},
                {43.0, "PROGRAMMER", "KN", false, 54.345},
                {43.0, "PROGRAMMER", "TN", true, 51.345},
                {43.0, "PROGRAMMER", "TN", false, 49.345},
                {43.0, "TEACHER", "AP", true, 41.345},
                {43.0, "TEACHER", "AP", false, 39.345},
                {43.0, "TEACHER", "KN", true, 61.345},
                {43.0, "TEACHER", "KN", false, 59.345},
                {43.0, "TEACHER", "TN", true, 56.345},
                {43.0, "TEACHER", "TN", false, 54.345},
                {43.0, "INSTRUCTOR", "AP", true, 41.345},
                {43.0, "INSTRUCTOR", "AP", false, 39.345},
                {43.0, "INSTRUCTOR", "KN", true, 61.345},
                {43.0, "INSTRUCTOR", "KN", false, 59.345},
                {43.0, "INSTRUCTOR", "TN", true, 56.345},
                {43.0, "INSTRUCTOR", "TN", false, 54.345},
                {45.0, "SKYDIVER", "AP", true, 6.344999999999999},
                {45.0, "SKYDIVER", "AP", false, 4.344999999999999},
                {45.0, "SKYDIVER", "KN", true, 26.345},
                {45.0, "SKYDIVER", "KN", false, 24.345},
                {45.0, "SKYDIVER", "TN", true, 21.345},
                {45.0, "SKYDIVER", "TN", false, 19.345},
                {45.0, "ASTRONAUT", "AP", true, 6.344999999999999},
                {45.0, "ASTRONAUT", "AP", false, 4.344999999999999},
                {45.0, "ASTRONAUT", "KN", true, 26.345},
                {45.0, "ASTRONAUT", "KN", false, 24.345},
                {45.0, "ASTRONAUT", "TN", true, 21.345},
                {45.0, "ASTRONAUT", "TN", false, 19.345},
                {45.0, "PROGRAMMER", "AP", true, 21.345},
                {45.0, "PROGRAMMER", "AP", false, 19.345},
                {45.0, "PROGRAMMER", "KN", true, 41.345},
                {45.0, "PROGRAMMER", "KN", false, 39.345},
                {45.0, "PROGRAMMER", "TN", true, 36.345},
                {45.0, "PROGRAMMER", "TN", false, 34.345},
                {45.0, "TEACHER", "AP", true, 26.345},
                {45.0, "TEACHER", "AP", false, 24.345},
                {45.0, "TEACHER", "KN", true, 46.345},
                {45.0, "TEACHER", "KN", false, 44.345},
                {45.0, "TEACHER", "TN", true, 41.345},
                {45.0, "TEACHER", "TN", false, 39.345},
                {45.0, "INSTRUCTOR", "AP", true, 26.345},
                {45.0, "INSTRUCTOR", "AP", false, 24.345},
                {45.0, "INSTRUCTOR", "KN", true, 46.345},
                {45.0, "INSTRUCTOR", "KN", false, 44.345},
                {45.0, "INSTRUCTOR", "TN", true, 41.345},
                {45.0, "INSTRUCTOR", "TN", false, 39.345}
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
    public void testScorecardSample() {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("AGE", age);
        inputData.put("OCCUPATION", occupation);
        inputData.put("RESIDENCESTATE", residenceState);
        inputData.put("VALIDLICENSE", validLicense);
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
        executionParams.add(pmml4Result);
        for (Map.Entry<String, Object> entry : inputData.entrySet()) {
            try {
                FactType factType = kSession.getKieBase().getFactType(PACKAGE, entry.getKey());
                Object toAdd = factType.newInstance();
                factType.set(toAdd, "value", entry.getValue());
                executionParams.add(toAdd);
            } catch (Exception e) {
                throw new KiePMMLModelException(e.getMessage(), e);
            }
        }
        executionParams.forEach(kSession::insert);
        kSession.setGlobal("$pmml4Result", pmml4Result);
        kSession.fireAllRules();
        assertEquals(ResultCode.OK.getName(), pmml4Result.getResultCode());
        assertNotNull(pmml4Result.getResultVariables().get(TARGET_FIELD));
        assertEquals(expectedResult, pmml4Result.getResultVariables().get(TARGET_FIELD));
    }
}
