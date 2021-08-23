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

package org.kie.dmn.core;

import java.math.BigDecimal;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.drools.compiler.kie.builder.impl.KieProject;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.core.util.KieHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.kie.dmn.core.util.DynamicTypeUtils.entry;
import static org.kie.dmn.core.util.DynamicTypeUtils.mapOf;

public class WBCommonServicesBackendTest extends BaseInterpretedVsCompiledTest {

    public static final Logger LOG = LoggerFactory.getLogger(WBCommonServicesBackendTest.class);

    public WBCommonServicesBackendTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    @Test
    public void testAsKieWbCommonServicesBackendBuilder() throws Exception {
        final KieServices ks = KieServices.Factory.get();

        final ReleaseId v100 = ks.newReleaseId("org.kie", "dmn-test", "1.0.0");
        final KieModule kieModule = KieHelper.createAndDeployJar(ks,
                v100,
                ks.getResources().newClassPathResource("wbcommonservicesbackend_app.dmn", this.getClass()),
                ks.getResources().newClassPathResource("wbcommonservicesbackend_route.dmn", this.getClass()));

        final KieContainer kieContainer = ks.newKieContainer(v100);
        final KieSession kieSession = kieContainer.newKieSession();
        final DMNRuntime runtime = kieSession.getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(2));

        checkApp(runtime);

        // the below is performed by the WB at: https://github.com/kiegroup/kie-wb-common/blob/9e6b6da145e61ac8f5a9f7c0259d44aa9d090a2b/kie-wb-common-services/kie-wb-common-services-backend/src/main/java/org/kie/workbench/common/services/backend/builder/core/Builder.java#L592-L620
        final KieProject kieProject = new KieModuleKieProject((InternalKieModule) kieModule, null);
        final KieContainer kieContainer2 = new KieContainerImpl(kieProject, ks.getRepository(), v100);
        final KieSession kieSession2 = kieContainer2.newKieSession(); // exhibit the issue.
        final DMNRuntime runtime2 = kieSession2.getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime2);
        assertThat(runtime2.getModels(), hasSize(2));

        checkApp(runtime2);
    }

    private void checkApp(DMNRuntime runtime) {
        DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_B2585232-1873-43C5-9D9D-BFD7A93BC51B", "app");
        DMNContext context = runtime.newContext();
        context.set("local", mapOf(entry("distance", new BigDecimal(10))));
        context.set("highway", mapOf(entry("distance", new BigDecimal(5))));

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()), dmnResult.hasErrors(), is(false));

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("the shortest distance"), is(new BigDecimal("5")));
    }
}