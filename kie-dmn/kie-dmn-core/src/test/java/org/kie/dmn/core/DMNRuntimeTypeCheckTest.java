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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DMNRuntimeTypeCheckTest extends BaseInterpretedVsCompiledTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNRuntimeTypeCheckTest.class);

    private static final KieServices ks = KieServices.Factory.get();

    public DMNRuntimeTypeCheckTest(final boolean useExecModelCompiler) {
        super(useExecModelCompiler);
    }

    @Test
    public void testDefaultNoTypeCheck() {
        // do NOT use the DMNRuntimeUtil as that enables typeSafe check override for runtime.
        final KieContainer kieContainer = KieHelper.getKieContainer(ks.newReleaseId("org.kie", "dmn-test-" + UUID.randomUUID(), "1.0"),
                                                                    ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        assertNoTypeCheck(runtime);
    }

    @Test
    public void testAskTypeCheckInKModule() {
        final DMNRuntime runtime = getRuntimeWithTypeCheckOption("true", ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        assertPerformTypeCheck(runtime);
    }

    @Test
    public void testAskTypeCheckWithGlobalEnvVariable() {
        System.setProperty(RuntimeTypeCheckOption.PROPERTY_NAME, "true");
        // do NOT use the DMNRuntimeUtil as that enables typeSafe check override for runtime.
        final KieContainer kieContainer = KieHelper.getKieContainer(ks.newReleaseId("org.kie", "dmn-test-" + UUID.randomUUID(), "1.0"),
                                                                    ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        assertPerformTypeCheck(runtime);
        System.clearProperty(RuntimeTypeCheckOption.PROPERTY_NAME);
    }

    @Test
    public void testExplicitDisableTypeCheckInKModule() {
        final DMNRuntime runtime = getRuntimeWithTypeCheckOption("false", ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        assertNoTypeCheck(runtime);
    }

    @Test
    public void testUnreckonOptionTypeCheckInKModuleDefaultsToNoTypeCheck() {
        final DMNRuntime runtime = getRuntimeWithTypeCheckOption("boh", ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        assertNoTypeCheck(runtime);
    }

    @Test
    public void testEmptyOptionTypeCheckInKModuleDefaultsToNoTypeCheck() {
        final DMNRuntime runtime = getRuntimeWithTypeCheckOption("", ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        assertNoTypeCheck(runtime);
    }

    @Test
    public void testDefaultNoTypeCheckButOverrideRuntime() {
        // do NOT use the DMNRuntimeUtil as that enables typeSafe check override for runtime.
        final KieContainer kieContainer = KieHelper.getKieContainer(ks.newReleaseId("org.kie", "dmn-test-" + UUID.randomUUID(), "1.0"),
                                                                    ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        ((DMNRuntimeImpl) runtime).setOption(new RuntimeTypeCheckOption(true));
        assertPerformTypeCheck(runtime);
    }

    @Test
    public void testExplicitDisableTypeCheckInKModuleButOverrideRuntime() {
        final DMNRuntime runtime = getRuntimeWithTypeCheckOption("false", ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        ((DMNRuntimeImpl) runtime).setOption(new RuntimeTypeCheckOption(true));
        assertPerformTypeCheck(runtime);
    }

    @Test
    public void testAskTypeCheckInKModuleButOverrideRuntime() {
        final DMNRuntime runtime = getRuntimeWithTypeCheckOption("true", ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        ((DMNRuntimeImpl) runtime).setOption(new RuntimeTypeCheckOption(false));
        assertPerformTypeCheck(runtime);
    }

    private DMNRuntime getRuntimeWithTypeCheckOption(final String typeCheckKModuleOption, final Resource... resources) {
        final KieFileSystem kfs = ks.newKieFileSystem();

        final KieModuleModel kmm = ks.newKieModuleModel();
        kmm.setConfigurationProperty(RuntimeTypeCheckOption.PROPERTY_NAME, typeCheckKModuleOption);
        kfs.writeKModuleXML(kmm.toXML());
        for (final Resource r : resources) {
            kfs.write(r);
        }

        final KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
        final Results results = kieBuilder.getResults();
        assertThat(results.getMessages().toString(), results.hasMessages(org.kie.api.builder.Message.Level.ERROR), is(false));

        final KieContainer kieContainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
        return kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
    }

    private void assertNoTypeCheck(final DMNRuntime runtime) {
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_6d8af9a2-dcf4-4b9e-8d90-6ccddc8c1bbd", "forTypeCheckTest");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("a number", "ciao");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);

        assertThat(DMNRuntimeUtil.formatMessages(dmnResult.getMessages()),
                   dmnResult.getMessages(DMNMessage.Severity.ERROR)
                            .stream()
                            .allMatch(m -> m.getSourceId().equals(dmnModel.getDecisionByName("hundred minus number").getId())),
                   is(true));

        final DMNDecisionResult textPlusNumberDR = dmnResult.getDecisionResultByName("text plus number");
        assertThat(textPlusNumberDR.getEvaluationStatus(), is(DecisionEvaluationStatus.SUCCEEDED));
        assertThat(textPlusNumberDR.getResult(), is("The input number is: ciao"));

        final DMNDecisionResult hundredMinusNumber = dmnResult.getDecisionResultByName("hundred minus number");
        assertThat(hundredMinusNumber.getEvaluationStatus(), is(DecisionEvaluationStatus.SUCCEEDED));
        assertThat(hundredMinusNumber.getResult(), nullValue());
    }

    private void assertPerformTypeCheck(final DMNRuntime runtime) {
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_6d8af9a2-dcf4-4b9e-8d90-6ccddc8c1bbd", "forTypeCheckTest");
        assertThat(dmnModel, notNullValue());
        assertThat(DMNRuntimeUtil.formatMessages(dmnModel.getMessages()), dmnModel.hasErrors(), is(false));

        final DMNContext context = DMNFactory.newContext();
        context.set("a number", "ciao");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);

        assertThat("Should throw several errors, not only for 1 specific Decision: " + DMNRuntimeUtil.formatMessages(dmnResult.getMessages()),
                   dmnResult.getMessages(DMNMessage.Severity.ERROR)
                            .stream()
                            .allMatch(m -> m.getSourceId().equals(dmnModel.getDecisionByName("hundred minus number").getId())),
                   is(false));

        final DMNDecisionResult textPlusNumberDR = dmnResult.getDecisionResultByName("text plus number");
        assertThat(textPlusNumberDR.getEvaluationStatus(), is(DecisionEvaluationStatus.SKIPPED)); // dependency failed type check

        final DMNDecisionResult hundredMinusNumber = dmnResult.getDecisionResultByName("hundred minus number");
        assertThat(hundredMinusNumber.getEvaluationStatus(), is(DecisionEvaluationStatus.SKIPPED)); // dependency failed type check
    }

    @Test
    public void testMisleadingNPEbyAPIusage() {
        // do NOT use the DMNRuntimeUtil as that enables typeSafe check override for runtime.
        final KieContainer kieContainer = KieHelper.getKieContainer(ks.newReleaseId("org.kie", "dmn-test-" + UUID.randomUUID(), "1.0"),
                                                                    ks.getResources().newClassPathResource("simple-item-def.dmn", this.getClass()));
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        final DMNModel dmnModel = runtime.getModel("<wrong>", "<wrong>");
        // please notice an end-user of the API might not having checked the result of the previous call is not a null.

        final DMNContext emptyContext = DMNFactory.newContext();
        try {
            final DMNResult dmnResult = runtime.evaluateAll(dmnModel, emptyContext);
            LOG.debug("{}", dmnResult);

            fail("");
        } catch (final Exception e) {
            assertTrue(e.getMessage().contains("'model'"));
            /* java.lang.NullPointerException: Kie DMN API parameter 'model' cannot be null.
                at java.util.Objects.requireNonNull(Objects.java:290)
                at org.kie.dmn.core.impl.DMNRuntimeImpl.evaluateAll(DMNRuntimeImpl.java:123)
                at org.kie.dmn.core.DMNRuntimeTypeCheckTest.testMisleadingNPEbyAPIusage(DMNRuntimeTypeCheckTest.java:199)
             */
        }
    }

    @Test
    public void testSqrtString() {
        // do NOT use the DMNRuntimeUtil as that enables typeSafe check override for runtime.
        final KieContainer kieContainer = KieHelper.getKieContainer(ks.newReleaseId("org.kie", "dmn-test-" + UUID.randomUUID(), "1.0"),
                                                                    ks.getResources().newClassPathResource("notypecheck/sqrtstring.dmn", this.getClass()));
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_476F07A1-F787-4079-9A68-EF1C6030A3EF", "sqrtstring");

        final DMNContext ctx = runtime.newContext();
        ctx.set("value", "47");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, ctx);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).isTrue();

        List<String> messages = dmnResult.getMessages().stream().map(DMNMessage::getText).collect(Collectors.toList());
        LOG.info("{}", messages);
        assertThat(messages.get(0)).contains("Error invoking function SQRT").contains("Unable to coerce parameter");
    }
}

