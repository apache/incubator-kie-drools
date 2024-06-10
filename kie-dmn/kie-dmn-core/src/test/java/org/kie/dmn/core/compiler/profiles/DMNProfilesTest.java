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
package org.kie.dmn.core.compiler.profiles;

import java.math.BigDecimal;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.BaseInterpretedVsCompiledTest;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class DMNProfilesTest extends BaseInterpretedVsCompiledTest {

    public static final Logger LOG = LoggerFactory.getLogger(DMNProfilesTest.class);

    @ParameterizedTest
    @MethodSource("params")
    void just47(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final KieServices ks = KieServices.Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem();

        final KieModuleModel kmm = ks.newKieModuleModel();
        kmm.setConfigurationProperty("org.kie.dmn.profiles.Just47DMNProfile", Just47DMNProfile.class.getCanonicalName());
        kfs.writeKModuleXML(kmm.toXML());
        kfs.write(ks.getResources().newClassPathResource("just_47.dmn", this.getClass()));

        final KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
        final Results results = kieBuilder.getResults();
        assertThat(results.hasMessages(org.kie.api.builder.Message.Level.ERROR)).as(results.getMessages().toString()).isFalse();

        final KieContainer kieContainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);

        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_ae46e75f-efc5-48f8-8e82-4f48bf16afc0", "just 47");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();
        context.set("number", 123.123456d);

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("decision result")).isEqualTo(new BigDecimal(47));
    }

    @ParameterizedTest
    @MethodSource("params")
    void customModelCount(boolean useExecModelCompiler) {
        init(useExecModelCompiler);
        final KieServices ks = KieServices.Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem();

        final KieModuleModel kmm = ks.newKieModuleModel();
        kmm.setConfigurationProperty("org.kie.dmn.profiles.CustomModelCountDMNProfile", CustomModelCountDMNProfile.class.getCanonicalName());
        kfs.writeKModuleXML(kmm.toXML());
        kfs.write(ks.getResources().newClassPathResource("customModelCount.dmn", this.getClass()));

        final KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
        final Results results = kieBuilder.getResults();
        assertThat(results.hasMessages(org.kie.api.builder.Message.Level.ERROR)).as(results.getMessages().toString()).isFalse();

        final KieContainer kieContainer = ks.newKieContainer(ks.getRepository().getDefaultReleaseId());
        final DMNRuntime runtime = kieContainer.newKieSession().getKieRuntime(DMNRuntime.class);

        final DMNModel dmnModel = runtime.getModel("http://www.trisotech.com/definitions/_5d9e63ac-e73a-4b9e-8635-760f0c23c0ad", "Drawing 1");
        assertThat(dmnModel).isNotNull();
        assertThat(dmnModel.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnModel.getMessages())).isFalse();

        final DMNContext context = DMNFactory.newContext();

        final DMNResult dmnResult = runtime.evaluateAll(dmnModel, context);
        LOG.debug("{}", dmnResult);
        assertThat(dmnResult.hasErrors()).as(DMNRuntimeUtil.formatMessages(dmnResult.getMessages())).isFalse();

        final DMNContext result = dmnResult.getContext();
        assertThat(result.get("main decision")).isEqualTo(new BigDecimal(1));
    }
}
