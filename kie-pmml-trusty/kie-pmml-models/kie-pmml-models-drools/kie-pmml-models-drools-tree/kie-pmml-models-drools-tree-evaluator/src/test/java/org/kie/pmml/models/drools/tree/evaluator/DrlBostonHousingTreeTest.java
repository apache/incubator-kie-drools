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
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.AgendaGroupPoppedEvent;
import org.kie.api.event.rule.AgendaGroupPushedEvent;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.event.rule.RuleFlowGroupActivatedEvent;
import org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent;
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
public class DrlBostonHousingTreeTest {

    private static final String SOURCE_1 = "BostonHousingTree.drl";
    private static final Logger logger = LoggerFactory.getLogger(DrlBostonHousingTreeTest.class);
    private static final String PACKAGE = "bostonhousingtreemodel";
    private static final String TARGET_FIELD = "medv";
    private static KieBase kbase;
    private double crim;
    private double zn;
    private double indus;
    private String chas;
    private double nox;
    private double rm;
    private double age;
    private double dis;
    private double rad;
    private double tax;
    private double ptratio;
    private double b;
    private double lstat;
    private double expectedResult;

    public DrlBostonHousingTreeTest(double crim, double zn, double indus, String chas, double nox, double rm,
                                    double age, double dis, double rad, double tax, double ptratio, double b,
                                    double lstat, double expectedResult) {
        this.crim = crim;
        this.zn = zn;
        this.indus = indus;
        this.chas = chas;
        this.nox = nox;
        this.rm = rm;
        this.age = age;
        this.dis = dis;
        this.rad = rad;
        this.tax = tax;
        this.ptratio = ptratio;
        this.b = b;
        this.lstat = lstat;
        this.expectedResult = expectedResult;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                // crim   zn indus  chas   nox    rm    age   dis   rad tax ptratio  b    lstat  expectedResult;
                {0.00632, 18, 2.31, "0", 0.538, 6.575, 65.2, 4.0900, 1, 296, 15.3, 396.90, 4.98, 27.4272727272727},
                {0.02729, 0, 7.07, "0", 0.469, 7.185, 61.1, 4.9671, 2, 242, 17.8, 392.83, 4.03, 33.7384615384615},
                {0.06905, 0, 2.18, "0", 0.458, 7.147, 54.2, 6.0622, 3, 222, 18.7, 396.90, 5.33, 33.7384615384615},
                {0.02985, 0, 2.18, "0", 0.458, 6.430, 58.7, 6.0622, 3, 222, 18.7, 394.12, 5.21, 21.6564766839378},
                {0.78420, 0.0, 8.14, "0", 0.538, 5.990, 81.7, 4.2579, 4, 307, 21.0, 386.75, 14.67, 17.1376237623762},
                {3.53501, 0, 19.58, "1", 0.871, 6.152, 82.6, 1.7455, 5, 403, 14.7, 88.01, 15.02, 17.1376237623762},
                {8.26725, 0, 18.1, "1", 0.668, 5.875, 89.6, 1.1296, 24, 666, 20.2, 347.88, 8.88, 38.0},
                {3.47428, 0, 18.1, "1", 0.718, 8.780, 82.9, 1.9047, 24, 666, 20.2, 354.55, 5.29, 45.0966666666667},
                {5.20177, 0, 18.1, "1", 0.770, 6.127, 83.4, 2.7227, 24, 666, 20.2, 395.43, 11.48, 21.6564766839378},
                {4.22239, 0, 18.1, "1", 0.770, 5.803, 89.0, 1.9047, 24, 666, 20.2, 353.04, 14.64, 17.1376237623762}
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
    public void testTree() {
        final Map<String, Object> inputData = new HashMap<>();
        inputData.put("CRIM", crim);
        inputData.put("ZN", zn);
        inputData.put("INDUS", indus);
        inputData.put("CHAS", chas);
        inputData.put("NOX", nox);
        inputData.put("RM", rm);
        inputData.put("AGE", age);
        inputData.put("DIS", dis);
        inputData.put("RAD", rad);
        inputData.put("TAX", tax);
        inputData.put("PTRATIO", ptratio);
        inputData.put("B", b);
        inputData.put("LSTAT", lstat);
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
        setupExecutionListener(kSession);
        kSession.fireAllRules();
        assertEquals(ResultCode.OK.getName(), pmml4Result.getResultCode());
        assertNotNull(pmml4Result.getResultVariables().get(TARGET_FIELD));
        assertEquals(expectedResult, pmml4Result.getResultVariables().get(TARGET_FIELD));
    }

    private void setupExecutionListener(final KieSession kSession) {
        final AgendaEventListener agendaEventListener = new AgendaEventListener() {

            public void matchCancelled(MatchCancelledEvent event) { }
            public void matchCreated(MatchCreatedEvent event) { }
            public void afterMatchFired(AfterMatchFiredEvent event) {
                logger.debug(event.toString());
            }
            public void agendaGroupPopped(AgendaGroupPoppedEvent event) { }
            public void agendaGroupPushed(AgendaGroupPushedEvent event) {
                logger.debug(event.toString());
            }
            public void beforeMatchFired(BeforeMatchFiredEvent event) {  }
            public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) {  }
            public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event) { }
            public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) { }
            public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event) { }
        };
        kSession.addEventListener(agendaEventListener);
    }
}
