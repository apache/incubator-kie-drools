/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.pmml;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.Message;
import org.kie.api.internal.assembler.KieAssemblers;
import org.kie.api.internal.utils.ServiceRegistry;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.assembler.DMNAssemblerService;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;
import org.kie.internal.builder.KieBuilderSet;
import org.kie.internal.services.KieAssemblersImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class DMNRuntimeKiePMMLTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNRuntimeKiePMMLTest.class);

    public DMNRuntimeKiePMMLTest() {
        super();
    }

    @Test
    public void testNoPMMLmodelName() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("KiePMMLScoreCard_NOPMMLmodelName.dmn",
                                                                                       DMNRuntimeKiePMMLTest.class,
                                                                                       "test_scorecard.pmml");
        runDMNModelInvokingPMML(runtime);
    }

    /**
     * test to use same building steps of BC/WB
     */
    @Test
    public void testSteppedCompilation() {
        final KieAssemblersImpl assemblers = (KieAssemblersImpl) ServiceRegistry.getInstance().get(KieAssemblers.class);
        assemblers.accept(new DMNAssemblerService());

        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem();

        kfs.write("src/main/resources/org/acme/test_scorecard.pmml", ks.getResources().newClassPathResource("test_scorecard.pmml", DMNRuntimeKiePMMLTest.class));
        KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
        assertEquals(0, kieBuilder.getResults().getMessages(org.kie.api.builder.Message.Level.ERROR).size());

        kfs.write("src/main/resources/org/acme/KiePMMLScoreCard.dmn", ks.getResources().newClassPathResource("KiePMMLScoreCard_NOPMMLmodelName.dmn", DMNRuntimeKiePMMLTest.class));
        KieBuilderSet kieBuilderSet = ((InternalKieBuilder) kieBuilder).createFileSet( Message.Level.WARNING, "src/main/resources/org/acme/KiePMMLScoreCard.dmn" );
        IncrementalResults addResults = kieBuilderSet.build();
        LOG.debug("getAddedMessages: {}", addResults.getAddedMessages());
        assertFalse(addResults.getAddedMessages().isEmpty());
        LOG.debug("getRemovedMessages: {}", addResults.getRemovedMessages());
        assertTrue(addResults.getRemovedMessages().isEmpty());

        KieRepository kr = ks.getRepository();
        KieContainer kieContainer = ks.newKieContainer(kr.getDefaultReleaseId());

        DMNRuntime dmnRuntime = KieRuntimeFactory.of(kieContainer.getKieBase()).get(DMNRuntime.class);

        runDMNModelInvokingPMML(dmnRuntime);

        kfs.write("src/main/resources/org/acme/KiePMMLScoreCard.dmn", ks.getResources().newClassPathResource("KiePMMLScoreCard.dmn", DMNRuntimeKiePMMLTest.class));
        addResults = kieBuilderSet.build();

        assertTrue(addResults.getAddedMessages().isEmpty());
        assertFalse(addResults.getRemovedMessages().isEmpty());

        kr = ks.getRepository();
        kieContainer = ks.newKieContainer(kr.getDefaultReleaseId());

        dmnRuntime = KieRuntimeFactory.of(kieContainer.getKieBase()).get(DMNRuntime.class);

        DMNRuntimePMMLTest.runDMNModelInvokingPMML(dmnRuntime);
    }

    private void runDMNModelInvokingPMML(DMNRuntime runtime) {
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_ca466dbe-20b4-4e88-a43f-4ce3aff26e4f", "KiePMMLScoreCard");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));
        // BKM for PMML invocation, with empty `model`, but PMML document contains at least 1 model name
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.getMessages(DMNMessage.Severity.WARN).size() > 0, is(true));

        final DMNContext emptyContext = DMNFactory.newContext();
        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        LOG.debug("{}", dmnResult);
        // As the kie-PMML runtime does not support runtime initialization without PMML `model`, will cause error, expected.
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(true));
    }
}
