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
public class DrlSimpleSetPredicateTreeTest {

    private static final String SOURCE_1 = "SimpleSetPredicateTreeGenerated.drl";
    private static final Logger logger = LoggerFactory.getLogger(DrlSimpleSetPredicateTreeTest.class);
    private static final String PACKAGE = "simplesetpredicatetreemodel";
    private static final String modelName = "SimpleSetPredicateTreeModel";
    // Expected scores
    private static final String CLASS_A = "classA";
    private static final String CLASS_B = "classB";
    private static final String CLASS_C = "classC";

    private static KieBase kbase;
    private final String INPUT1 = "INPUT1";
    private final String INPUT2 = "INPUT2";
    private final String INPUT3 = "INPUT3";
    private final String TARGET_FIELD = "RESULT";
    private Double input1;
    private Double input2;
    private Double input3;
    private String expectedResult;

    public DrlSimpleSetPredicateTreeTest(Double input1,
                                         Double input2,
                                         Double input3,
                                         String expectedResult) {
        this.input1 = input1;
        this.input2 = input2;
        this.input3 = input3;
        this.expectedResult = expectedResult;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0.0, 3.0, 0.0, CLASS_A},
                {0.0, 8.5, 0.0, CLASS_A},
                {-5.0, 0.0, 0.0, CLASS_B},
                {0.5, 0.0, 0.0, CLASS_B},
                {1.0, 0.0, 0.0, CLASS_B},
                {10.0, 0.0, 0.0, CLASS_B},
                {-10.0, 15.0, 15.0, CLASS_C},
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
        inputData.put(INPUT1, input1);
        inputData.put(INPUT2, input2);
        inputData.put(INPUT3, input3);
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
