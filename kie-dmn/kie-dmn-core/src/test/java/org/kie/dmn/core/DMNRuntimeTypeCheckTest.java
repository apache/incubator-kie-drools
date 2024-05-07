/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.core;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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
import static org.assertj.core.api.Assertions.fail;

public class DMNRuntimeTypeCheckTest extends BaseInterpretedVsCompiledTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNRuntimeTypeCheckTest.class);

    private static final KieServices ks = KieServices.Factory.get();

    @ParameterizedTest
    @MethodSource("params")
    void defaultNoTypeCheck(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        // do NOT use the DMNRuntimeUtil as that enables typeSafe check override for runtime.
        final KieContainer kieContainer = KieHelper.getKieContainer(ks.newReleaseId("org.kie", "dmn-test-" + UUID.randomUUID(), "1.0"),
                                                                    ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        assertNoTypeCheck(runtime);
    }

    @ParameterizedTest
    @MethodSource("params")
    void askTypeCheckInKModule(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = getRuntimeWithTypeCheckOption("true", ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        assertPerformTypeCheck(runtime);
    }

    @ParameterizedTest
    @MethodSource("params")
    void askTypeCheckWithGlobalEnvVariable(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        System.setProperty(RuntimeTypeCheckOption.PROPERTY_NAME, "true");
        // do NOT use the DMNRuntimeUtil as that enables typeSafe check override for runtime.
        final KieContainer kieContainer = KieHelper.getKieContainer(ks.newReleaseId("org.kie", "dmn-test-" + UUID.randomUUID(), "1.0"),
                                                                    ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        assertPerformTypeCheck(runtime);
        System.clearProperty(RuntimeTypeCheckOption.PROPERTY_NAME);
    }

    @ParameterizedTest
    @MethodSource("params")
    void explicitDisableTypeCheckInKModule(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = getRuntimeWithTypeCheckOption("false", ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        assertNoTypeCheck(runtime);
    }

    @ParameterizedTest
    @MethodSource("params")
    void unreckonOptionTypeCheckInKModuleDefaultsToNoTypeCheck(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = getRuntimeWithTypeCheckOption("boh", ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        assertNoTypeCheck(runtime);
    }

    @ParameterizedTest
    @MethodSource("params")
    void emptyOptionTypeCheckInKModuleDefaultsToNoTypeCheck(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = getRuntimeWithTypeCheckOption("", ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        assertNoTypeCheck(runtime);
    }

    @ParameterizedTest
    @MethodSource("params")
    void defaultNoTypeCheckButOverrideRuntime(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        // do NOT use the DMNRuntimeUtil as that enables typeSafe check override for runtime.
        final KieContainer kieContainer = KieHelper.getKieContainer(ks.newReleaseId("org.kie", "dmn-test-" + UUID.randomUUID(), "1.0"),
                                                                    ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
        ((DMNRuntimeImpl) runtime).setOption(new RuntimeTypeCheckOption(true));
        assertPerformTypeCheck(runtime);
    }

    @ParameterizedTest
    @MethodSource("params")
    void explicitDisableTypeCheckInKModuleButOverrideRuntime(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final DMNRuntime runtime = getRuntimeWithTypeCheckOption("false", ks.getResources().newClassPathResource("forTypeCheckTest.dmn", this.getClass()));
        ((DMNRuntimeImpl) runtime).setOption(new RuntimeTypeCheckOption(true));
        assertPerformTypeCheck(runtime);
    }

    @ParameterizedTest
    @MethodSource("params")
    void askTypeCheckInKModuleButOverrideRuntime(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
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
        assertThat(results.hasMessages(org.kie.api.builder.Message.Level.ERROR)).as(results.getMessages().toString()).isFalse();

        final KieContainer kieContainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
        return kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);
    }

    private void assertNoTypeCheck(final DMNRuntime runtime) {
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_6d8af9a2-dcf4-4b9e-8d90-6ccddc8c1bbd", "forTypeCheckTest");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("a number", "ciao");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);

        assertThat(dmnResult.getMessages(DMNMessage.Severity.ERROR)
                            .stream()
                            .allMatch(m -> m.getSourceId().equals(dmnModel.getDecisionByName("hundred minus number").getId())))
        .as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isTrue();

        final DMNDecisionResult textPlusNumberDR = dmnResult.getDecisionResultByName("text plus number");
        assertThat(textPlusNumberDR.getEvaluationStatus()).isEqualTo(DecisionEvaluationStatus.SUCCEEDED);
        assertThat(textPlusNumberDR.getResult()).isEqualTo("The input number is: ciao");

        final DMNDecisionResult hundredMinusNumber = dmnResult.getDecisionResultByName("hundred minus number");
        assertThat(hundredMinusNumber.getEvaluationStatus()).isEqualTo(DecisionEvaluationStatus.SUCCEEDED);
        assertThat(hundredMinusNumber.getResult()).isNull();
    }

    private void assertPerformTypeCheck(final DMNRuntime runtime) {
        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_6d8af9a2-dcf4-4b9e-8d90-6ccddc8c1bbd", "forTypeCheckTest");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("a number", "ciao");

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);

        assertThat(dmnResult.getMessages(DMNMessage.Severity.ERROR)
                            .stream()
                            .allMatch(m -> m.getSourceId().equals(dmnModel.getDecisionByName("hundred minus number").getId())))
        .as("Should throw several errors, not only for 1 specific Decision: " + DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNDecisionResult textPlusNumberDR = dmnResult.getDecisionResultByName("text plus number");
        assertThat(textPlusNumberDR.getEvaluationStatus()).isEqualTo(DecisionEvaluationStatus.SKIPPED); // dependency failed type check

        final DMNDecisionResult hundredMinusNumber = dmnResult.getDecisionResultByName("hundred minus number");
        assertThat(hundredMinusNumber.getEvaluationStatus()).isEqualTo(DecisionEvaluationStatus.SKIPPED); // dependency failed type check
    }

    @ParameterizedTest
    @MethodSource("params")
    void misleadingNPEbyAPIusage(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
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
            assertThat(e.getMessage()).contains("'model'");
            /* java.lang.NullPointerException: Kie DMN API parameter 'model' cannot be null.
                at java.util.Objects.requireNonNull(Objects.java:290)
                at org.kie.dmn.core.impl.DMNRuntimeImpl.evaluateAll(DMNRuntimeImpl.java:123)
                at org.kie.dmn.core.DMNRuntimeTypeCheckTest.testMisleadingNPEbyAPIusage(DMNRuntimeTypeCheckTest.java:199)
             */
        }
    }

    @ParameterizedTest
    @MethodSource("params")
    void sqrtString(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
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

