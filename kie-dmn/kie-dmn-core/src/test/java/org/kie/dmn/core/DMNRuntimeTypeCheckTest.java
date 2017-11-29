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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.UUID;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.compiler.RuntimeTypeCheckOption;
import org.kie.dmn.core.impl.DMNRuntimeImpl;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.core.util.KieHelper;

public class DMNRuntimeTypeCheckTest {

    private static final KieServices ks = KieServices.Factory.get();

    @Test
    public void testDefaultNoTypeCheck() {
        // do NOT use the DMNRuntimeUtil as that enables typeSafe check override for runtime.
        final KieContainer kieContainer = KieHelper.getKieContainer(ks.newReleaseId("org.kie", "dmn-test-" + UUID.randomUUID(), "1.0"),
                                                                    ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        assertNoTypeCheck(runtime);
    }

    @Test
    public void testAskTypeCheckInKModule() {
        DMNRuntime runtime = getRuntimeWithTypeCheckOption("true", ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        assertPerformTypeCheck(runtime);
    }

    @Test
    public void testExplicitDisableTypeCheckInKModule() {
        DMNRuntime runtime = getRuntimeWithTypeCheckOption("false", ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        assertNoTypeCheck(runtime);
    }

    @Test
    public void testUnreckonOptionTypeCheckInKModuleDefaultsToNoTypeCheck() {
        DMNRuntime runtime = getRuntimeWithTypeCheckOption("boh", ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        assertNoTypeCheck(runtime);
    }

    @Test
    public void testEmptyOptionTypeCheckInKModuleDefaultsToNoTypeCheck() {
        DMNRuntime runtime = getRuntimeWithTypeCheckOption("", ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        assertNoTypeCheck(runtime);
    }

    @Test
    public void testDefaultNoTypeCheckButOverrideRuntime() {
        // do NOT use the DMNRuntimeUtil as that enables typeSafe check override for runtime.
        final KieContainer kieContainer = KieHelper.getKieContainer(ks.newReleaseId("org.kie", "dmn-test-" + UUID.randomUUID(), "1.0"),
                                                                    ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        ((DMNRuntimeImpl) runtime).setOption(new RuntimeTypeCheckOption(true));
        assertPerformTypeCheck(runtime);
    }

    @Test
    public void testExplicitDisableTypeCheckInKModuleButOverrideRuntime() {
        DMNRuntime runtime = getRuntimeWithTypeCheckOption("false", ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        ((DMNRuntimeImpl) runtime).setOption(new RuntimeTypeCheckOption(true));
        assertPerformTypeCheck(runtime);
    }

    @Test
    public void testAskTypeCheckInKModuleButOverrideRuntime() {
        DMNRuntime runtime = getRuntimeWithTypeCheckOption("true", ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        ((DMNRuntimeImpl) runtime).setOption(new RuntimeTypeCheckOption(false));
        assertPerformTypeCheck(runtime);
    }

    private DMNRuntime getRuntimeWithTypeCheckOption(String typeCheckKModuleOption, Resource... resources) {
        final KieFileSystem kfs = ks.newKieFileSystem();

        KieModuleModel kmm = ks.newKieModuleModel();
        kmm.setConfigurationProperty(RuntimeTypeCheckOption.PROPERTY_NAME, typeCheckKModuleOption);
        kfs.writeKModuleXML(kmm.toXML());
        for (Resource r : resources) {
            kfs.write(r);
        }

        KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
        Results results = kieBuilder.getResults();
        assertThat(results.getMessages().toString(), results.hasMessages(org.kie.api.builder.Message.Level.ERROR), is(false));

        final KieContainer kieContainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);

        return runtime;
    }

    private void assertNoTypeCheck(DMNRuntime runtime) {
        DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_6d8af9a2-dcf4-4b9e-8d90-6ccddc8c1bbd", "forTypeCheckTest");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        DMNContext context = DMNFactory.newContext();
        context.set("a number", "ciao");

        DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);

        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()),
                   dmnResult.getMessages(DMNMessage.Severity.ERROR)
                            .stream()
                            .allMatch(m -> m.getSourceId().equals(dmnModel.getDecisionByName("hundred minus number").getId())),
                   is(true));

        DMNDecisionResult textPlusNumberDR = dmnResult.getDecisionResultByName("text plus number");
        assertThat(textPlusNumberDR.getEvaluationStatus(), is(DecisionEvaluationStatus.SUCCEEDED));
        assertThat(textPlusNumberDR.getResult(), is("The input number is: ciao"));

        DMNDecisionResult hundredMinusNumber = dmnResult.getDecisionResultByName("hundred minus number");
        assertThat(hundredMinusNumber.getEvaluationStatus(), is(DecisionEvaluationStatus.FAILED));
    }

    private void assertPerformTypeCheck(DMNRuntime runtime) {
        DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_6d8af9a2-dcf4-4b9e-8d90-6ccddc8c1bbd", "forTypeCheckTest");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        DMNContext context = DMNFactory.newContext();
        context.set("a number", "ciao");

        DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);

        assertThat("Should throw several errors, not only for 1 specific Decision: " + DMNRuntimeUtil.formatMessages(dmnResult.getMessages()),
                   dmnResult.getMessages(DMNMessage.Severity.ERROR)
                            .stream()
                            .allMatch(m -> m.getSourceId().equals(dmnModel.getDecisionByName("hundred minus number").getId())),
                   is(false));

        DMNDecisionResult textPlusNumberDR = dmnResult.getDecisionResultByName("text plus number");
        assertThat(textPlusNumberDR.getEvaluationStatus(), is(DecisionEvaluationStatus.SKIPPED)); // dependency failed type check

        DMNDecisionResult hundredMinusNumber = dmnResult.getDecisionResultByName("hundred minus number");
        assertThat(textPlusNumberDR.getEvaluationStatus(), is(DecisionEvaluationStatus.SKIPPED)); // dependency failed type check
    }
}

