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

import java.math.BigDecimal;
import java.util.Map;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.internal.assembler.KieAssemblers;
import org.kie.api.internal.utils.ServiceRegistry;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.assembler.DMNAssemblerService;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.internal.builder.IncrementalResults;
import org.kie.internal.builder.InternalKieBuilder;
import org.kie.internal.services.KieAssemblersImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class DMNRuntimePMMLTest {

    public DMNRuntimePMMLTest() {
        super();
    }

    public static final Logger LOG = LoggerFactory.getLogger(DMNRuntimePMMLTest.class);

    @Test
    public void testBasic() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("KiePMMLScoreCard.dmn",
                                                                                       DMNRuntimePMMLTest.class,
                                                                                       "test_scorecard.pmml");
        runDMNModelInvokingPMML(runtime);
    }

    @Test
    public void testWithInputTypes() {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntimeWithAdditionalResources("KiePMMLScoreCard_wInputType.dmn",
                                                                                       DMNRuntimePMMLTest.class,
                                                                                       "test_scorecard.pmml");
        runDMNModelInvokingPMML(runtime);
    }

    static void runDMNModelInvokingPMML(final DMNRuntime runtime) {
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_ca466dbe-20b4-4e88-a43f-4ce3aff26e4f", "KiePMMLScoreCard");
        assertThat( dmnModel, notNullValue() );
        assertThat( DMNRuntimeUtil.formatMessages( dmnModel.getMessages() ), dmnModel.hasErrors(), is( false ) );

        final DMNContext emptyContext = DMNFactory.newContext();

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat((Map<String, Object>) result.get("my decision"), hasEntry("calculatedScore", new BigDecimal("41.345")));
        
        // additional import info.
        Map<String, DMNImportPMMLInfo> pmmlImportInfo = ((DMNModelImpl) dmnModel).getPmmlImportInfo();
        assertThat(pmmlImportInfo.keySet(), hasSize(1));
        DMNImportPMMLInfo p0 = pmmlImportInfo.values().iterator().next();
        assertThat(p0.getImportName(), is("iris"));
        assertThat(p0.getModels(), hasSize(1));
        DMNPMMLModelInfo m0 = p0.getModels().iterator().next();
        assertThat(m0.getName(), is("Sample Score"));
        assertThat(m0.getInputFields(), hasEntry(is("age"), anything()));
        assertThat(m0.getInputFields(), hasEntry(is("occupation"), anything()));
        assertThat(m0.getInputFields(), hasEntry(is("residenceState"), anything()));
        assertThat(m0.getInputFields(), hasEntry(is("validLicense"), anything()));
        assertThat(m0.getInputFields(), not(hasEntry(is("overallScore"), anything())));
        assertThat(m0.getInputFields(), not(hasEntry(is("calculatedScore"), anything())));
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

        kfs.write("src/main/resources/org/acme/test_scorecard.pmml", ks.getResources().newClassPathResource("test_scorecard.pmml", DMNRuntimePMMLTest.class));
        KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
        assertEquals(0, kieBuilder.getResults().getMessages(org.kie.api.builder.Message.Level.ERROR).size());

        kfs.write("src/main/resources/org/acme/KiePMMLScoreCard.dmn", ks.getResources().newClassPathResource("KiePMMLScoreCard.dmn", DMNRuntimePMMLTest.class));
        IncrementalResults addResults = ((InternalKieBuilder) kieBuilder).createFileSet("src/main/resources/org/acme/KiePMMLScoreCard.dmn").build();
        assertEquals(0, addResults.getAddedMessages().size());
        assertEquals(0, addResults.getRemovedMessages().size());

        KieRepository kr = ks.getRepository();
        KieContainer kieContainer = ks.newKieContainer(kr.getDefaultReleaseId());

        DMNRuntime dmnRuntime = KieRuntimeFactory.of(kieContainer.getKieBase()).get(DMNRuntime.class);

        runDMNModelInvokingPMML(dmnRuntime);
    }
}
