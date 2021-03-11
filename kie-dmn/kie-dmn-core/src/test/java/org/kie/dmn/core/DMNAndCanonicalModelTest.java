/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core;

import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.KieHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.MatcherAssert.assertThat;

public class DMNAndCanonicalModelTest extends BaseInterpretedVsCompiledTestCanonicalKieModule {

    public static final Logger LOG = LoggerFactory.getLogger(DMNAndCanonicalModelTest.class);

    public DMNAndCanonicalModelTest(final boolean useExecModelCompiler, boolean canonicalKieModule) {
        super(useExecModelCompiler, canonicalKieModule);
    }

    @Test
    public void testDMNAndCanonicalModel() {
        final KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId = ks.newReleaseId("org.kie", "dmn-and-canonical", "1.0.0");
        final KieContainer kieContainer = KieHelper.getKieContainer(releaseId,
                                                                    wrapWithDroolsModelResource(ks,
                                                                                                releaseId,
                                                                                                ks.getResources().newClassPathResource("0001-input-data-string.dmn",
                                                                                                                                       this.getClass())));

        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));

        final DMNContext dmnContext = runtime.newContext();
        dmnContext.set("Full Name", "John Doe");
        final DMNResult evaluateAll = runtime.evaluateAll(runtime.getModels().get(0), dmnContext);
        LOG.debug("{}", evaluateAll);
        assertThat(evaluateAll.getDecisionResultByName("Greeting Message").getResult(), is("Hello John Doe"));
    }

    @Test
    public void testDTAndCanonicalModel() {
        final KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId = ks.newReleaseId("org.kie", "dmn-and-canonical", "1.0.0");
        final KieContainer kieContainer = KieHelper.getKieContainer(releaseId,
                                                                    wrapWithDroolsModelResource(ks,
                                                                                                releaseId,
                                                                                                ks.getResources().newClassPathResource("decisiontable-default-value.dmn",
                                                                                                                                       this.getClass())));

        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));

        final DMNContext dmnContext = DMNFactory.newContext();
        dmnContext.set("Age", 18);
        dmnContext.set("RiskCategory", "Medium");
        dmnContext.set("isAffordable", true);
        final DMNResult evaluateAll = runtime.evaluateAll(runtime.getModels().get(0), dmnContext);
        LOG.debug("{}", evaluateAll);
        assertThat(evaluateAll.getDecisionResultByName("Approval Status").getResult(), is("Approved"));
    }
}
