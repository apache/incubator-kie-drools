/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.drools.compiler.kie.builder.impl.KieProject;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.io.Resource;
import org.kie.api.marshalling.KieMarshallers;
import org.kie.api.marshalling.Marshaller;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.KieHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class DMNUpdateTest extends BaseInterpretedVsCompiledTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNUpdateTest.class);

    public DMNUpdateTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    @Test
    public void testRemoveAndAddSomething() {
        final KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId = ks.newReleaseId("org.kie", "dmn-test", "1.0.0");
        final KieContainer kieContainer = KieHelper.getKieContainer(releaseId,
                                                                    ks.getResources().newClassPathResource("0001-input-data-string.dmn", this.getClass()));

        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));

        check0001_input_data_string(runtime);

        final ReleaseId v101 = ks.newReleaseId("org.kie", "dmn-test", "1.0.1");
        KieHelper.createAndDeployJar(ks,
                                     v101,
                                     ks.getResources().newClassPathResource("0001-input-data-string-itIT.dmn", this.getClass()));

        final Results updateResults = kieContainer.updateToVersion(v101);
        assertThat(updateResults.hasMessages(Level.ERROR), is(false));

        runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));

        final DMNContext dmnContext2 = runtime.newContext();
        dmnContext2.set("Full Name", "John Doe");

        final DMNResult evaluateAll2 = runtime.evaluateAll(runtime.getModels().get(0), dmnContext2);
        assertThat(evaluateAll2.getDecisionResultByName("Greeting Message").getResult(), is("Salve John Doe"));
    }

    @Test
    public void testReplace() {
        final KieServices ks = KieServices.Factory.get();

        ReleaseId releaseId = ks.newReleaseId("org.kie", "dmn-test", "1.0.0");
        final KieContainer kieContainer = KieHelper.getKieContainer(releaseId,
                                                                    ks.getResources().newClassPathResource("0001-input-data-string.dmn", this.getClass()));

        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));

        check0001_input_data_string(runtime);

        final ReleaseId v101 = ks.newReleaseId("org.kie", "dmn-test", "1.0.1");
        final Resource newClassPathResource = ks.getResources().newClassPathResource("0001-input-data-string-itIT.dmn", this.getClass());
        newClassPathResource.setTargetPath("0001-input-data-string.dmn");
        KieHelper.createAndDeployJar(ks,
                                     v101,
                                     newClassPathResource);

        final Results updateResults = kieContainer.updateToVersion(v101);
        assertThat(updateResults.hasMessages(Level.ERROR), is(false));

        runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));

        final DMNContext dmnContext2 = runtime.newContext();
        dmnContext2.set("Full Name", "John Doe");

        final DMNResult evaluateAll2 = runtime.evaluateAll(runtime.getModels().get(0), dmnContext2);
        assertThat(evaluateAll2.getDecisionResultByName("Greeting Message").getResult(), is("Salve John Doe"));
    }

    @Test
    public void testReplaceDisposeCreateReplace() {
        final KieServices ks = KieServices.Factory.get();

        final ReleaseId v100 = ks.newReleaseId("org.kie", "dmn-test", "1.0.0");
        KieContainer kieContainer = KieHelper.getKieContainer(v100,
                                                              ks.getResources().newClassPathResource("0001-input-data-string.dmn", this.getClass()));

        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));

        check0001_input_data_string(runtime);

        final ReleaseId v101 = ks.newReleaseId("org.kie", "dmn-test", "1.0.1");
        final Resource newClassPathResource = ks.getResources().newClassPathResource("0001-input-data-string-itIT.dmn", this.getClass());
        newClassPathResource.setTargetPath("0001-input-data-string.dmn");
        KieHelper.createAndDeployJar(ks,
                                     v101,
                                     newClassPathResource);

        Results updateResults = kieContainer.updateToVersion(v101);
        assertThat(updateResults.hasMessages(Level.ERROR), is(false));

        runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));

        final DMNContext dmnContext2 = runtime.newContext();
        dmnContext2.set("Full Name", "John Doe");

        final DMNResult evaluateAll2 = runtime.evaluateAll(runtime.getModels().get(0), dmnContext2);
        assertThat(evaluateAll2.getDecisionResultByName("Greeting Message").getResult(), is("Salve John Doe"));

        kieContainer.dispose();

        kieContainer = ks.newKieContainer(v100);
        runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));

        check0001_input_data_string(runtime);

        updateResults = kieContainer.updateToVersion(v101);
        assertThat(updateResults.hasMessages(Level.ERROR), is(false));

        runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));

        final DMNContext dmnContext4 = runtime.newContext();
        dmnContext4.set("Full Name", "John Doe");

        final DMNResult evaluateAll4 = runtime.evaluateAll(runtime.getModels().get(0), dmnContext4);
        assertThat(evaluateAll4.getDecisionResultByName("Greeting Message").getResult(), is("Salve John Doe"));
    }

    @Test
    public void testFromClonedKiePackage() {
        final KieServices ks = KieServices.Factory.get();

        final ReleaseId v100 = ks.newReleaseId("org.kie", "dmn-test", "1.0.0");
        KieHelper.createAndDeployJar(ks,
                                     v100,
                                     ks.getResources().newClassPathResource("0001-input-data-string.dmn", this.getClass()));

        KieContainer kieContainer = ks.newKieContainer(v100);

        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));

        kieContainer.dispose();

        kieContainer = ks.newKieContainer(v100);

        runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));
    }

    @Test
    public void testFromClonedKiePackageThenUpgrade() {
        final KieServices ks = KieServices.Factory.get();

        final ReleaseId v100 = ks.newReleaseId("org.kie", "dmn-test", "1.0.0");
        KieHelper.createAndDeployJar(ks,
                                     v100,
                                     ks.getResources().newClassPathResource("0001-input-data-string.dmn", this.getClass()));

        KieContainer kieContainer = ks.newKieContainer(v100);

        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));

        kieContainer.dispose();

        kieContainer = ks.newKieContainer(v100);

        runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));

        final ReleaseId v101 = ks.newReleaseId("org.kie", "dmn-test", "1.0.1");
        final Resource newClassPathResource = ks.getResources().newClassPathResource("0001-input-data-string-itIT.dmn", this.getClass());
        newClassPathResource.setTargetPath("0001-input-data-string.dmn");
        KieHelper.createAndDeployJar(ks,
                                     v101,
                                     newClassPathResource);

        final Results updateResults = kieContainer.updateToVersion(v101);
        assertThat(updateResults.hasMessages(Level.ERROR), is(false));

        runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));
    }

    @Test
    public void testKieMarshaller() throws Exception {
        final KieServices ks = KieServices.Factory.get();

        final ReleaseId v100 = ks.newReleaseId("org.kie", "dmn-test", "1.0.0");
        KieHelper.createAndDeployJar(ks,
                                     v100,
                                     ks.getResources().newClassPathResource("0001-input-data-string.dmn", this.getClass()));

        KieContainer kieContainer = ks.newKieContainer(v100);
        KieSession kieSession = kieContainer.newKieSession();
        KieBase kieBase = kieSession.getKieBase();
        DMNRuntime runtime = kieSession.getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));

        check0001_input_data_string(runtime);

        KieMarshallers kieMarshallers = ks.getMarshallers();
        Marshaller marshaller = kieMarshallers.newMarshaller(kieBase);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        marshaller.marshall(baos, kieSession);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        baos.close();
        kieSession = marshaller.unmarshall(bais);
        bais.close();

        runtime = kieSession.getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));

        check0001_input_data_string(runtime);
    }

    @Test
    public void test_as_kie_wb_common_services_backend_Builder() throws Exception {
        final KieServices ks = KieServices.Factory.get();

        final ReleaseId v100 = ks.newReleaseId("org.kie", "dmn-test", "1.0.0");
        final KieModule kieModule = KieHelper.createAndDeployJar(ks,
                                                                 v100,
                                                                 ks.getResources().newClassPathResource("0001-input-data-string.dmn", this.getClass()));

        final KieContainer kieContainer = ks.newKieContainer(v100);
        final KieSession kieSession = kieContainer.newKieSession();
        final DMNRuntime runtime = kieSession.getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));

        check0001_input_data_string(runtime);

        // the below is performed by the WB at: https://github.com/kiegroup/kie-wb-common/blob/master/kie-wb-common-services/kie-wb-common-services-backend/src/main/java/org/kie/workbench/common/services/backend/builder/core/Builder.java#L592
        final KieProject kieProject = new KieModuleKieProject((InternalKieModule) kieModule, null);
        final KieContainer kieContainer2 = new KieContainerImpl(kieProject, ks.getRepository(), v100);
        final KieSession kieSession2 = kieContainer2.newKieSession(); // exhibit the issue.
        final DMNRuntime runtime2 = kieSession2.getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime2);
        assertThat(runtime2.getModels(), hasSize(1));

        check0001_input_data_string(runtime2);
    }

    private void check0001_input_data_string(final DMNRuntime runtime) {
        final DMNContext dmnContext = runtime.newContext();
        dmnContext.set("Full Name", "John Doe");
        final DMNResult evaluateAll = runtime.evaluateAll(runtime.getModels().get(0), dmnContext);
        LOG.debug("{}", evaluateAll);
        assertThat(evaluateAll.getDecisionResultByName("Greeting Message").getResult(), is("Hello John Doe"));
    }

    @Test
    public void test_as_kie_wb_common_services_backend_Builder2() throws Exception {
        final KieServices ks = KieServices.Factory.get();

        final ReleaseId v100 = ks.newReleaseId("org.kie", "dmn-test", "1.0.0");
        final KieModule kieModule = KieHelper.createAndDeployJar(ks,
                                                                 v100,
                                                                 ks.getResources().newClassPathResource("v1_2/dmn-hotcold.dmn", this.getClass()));

        final KieContainer kieContainer = ks.newKieContainer(v100);
        final KieSession kieSession = kieContainer.newKieSession();
        final DMNRuntime runtime = kieSession.getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime);
        assertThat(runtime.getModels(), hasSize(1));

        checkDMNHotColdDMN12WithNSScattered(runtime);

        // the below is performed by the WB at: https://github.com/kiegroup/kie-wb-common/blob/master/kie-wb-common-services/kie-wb-common-services-backend/src/main/java/org/kie/workbench/common/services/backend/builder/core/Builder.java#L592
        final KieProject kieProject = new KieModuleKieProject((InternalKieModule) kieModule, null);
        final KieContainer kieContainer2 = new KieContainerImpl(kieProject, ks.getRepository(), v100);
        final KieSession kieSession2 = kieContainer2.newKieSession(); // exhibit the issue.
        final DMNRuntime runtime2 = kieSession2.getKieRuntime(DMNRuntime.class);
        Assert.assertNotNull(runtime2);
        assertThat(runtime2.getModels(), hasSize(1));

        checkDMNHotColdDMN12WithNSScattered(runtime2);
    }

    private void checkDMNHotColdDMN12WithNSScattered(final DMNRuntime runtime) {
        final DMNModel dmnModel = runtime.getModel("https://github.com/kiegroup/drools/kie-dmn/_41A586D4-CEE9-420F-9289-7E0249B2EA34", "dmn1");
        assertThat(dmnModel, notNullValue());
        assertThat(dmnModel.getMessages().toString(), dmnModel.hasErrors(), is(false));
        final DMNContext context = DMNFactory.newContext();
        context.set("temperature", 3);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.info("{}", dmnResult);
        assertThat(dmnResult.hasErrors(), is(false));
        assertThat(dmnResult.getContext().get("is it cold?"), is("hot"));
    }
}
