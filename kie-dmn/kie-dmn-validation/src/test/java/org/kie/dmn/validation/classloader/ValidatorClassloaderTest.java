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
package org.kie.dmn.validation.classloader;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.assembler.DMNAssemblerService;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.validation.AbstractValidatorTest;
import org.kie.dmn.validation.DMNValidator;
import org.kie.dmn.validation.DMNValidatorFactory;
import org.kie.dmn.validation.ValidatorUtil;
import org.kie.internal.utils.ChainedProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_COMPILATION;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_MODEL;
import static org.kie.dmn.validation.DMNValidator.Validation.VALIDATE_SCHEMA;

class ValidatorClassloaderTest extends AbstractValidatorTest {

    private static final Logger LOG = LoggerFactory.getLogger(ValidatorClassloaderTest.class);

    @Test
    void test() {
        String JAVA_SOURCE = "package com.acme.functions;\n" +
                             "public class Dummy {\n" +
                             "    public static String hello() {\n" +
                             "        return \"Hello World\";\n" +
                             "    }\n" +
                             "}";
        final KieServices ks = KieServices.Factory.get();
        final ReleaseId kjarReleaseId = ks.newReleaseId("org.kie.dmn.validation", "testValidatorClassloaderTest", UUID.randomUUID().toString());
        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write("src/main/java/com/acme/functions/Dummy.java", JAVA_SOURCE);
        kfs.write(ks.getResources().newClassPathResource("DummyInvocation.dmn", this.getClass()));
        kfs.generateAndWritePomXML(kjarReleaseId);

        final KieBuilder kieBuilder = ks.newKieBuilder(kfs).buildAll();
        assertThat(kieBuilder.getResults().getMessages()).as(kieBuilder.getResults().getMessages().toString()).isEmpty();

        final KieContainer container = ks.newKieContainer(kjarReleaseId);
        final DMNRuntime runtime = KieRuntimeFactory.of(container.getKieBase()).get(DMNRuntime.class);
        final DMNModel dmnModel = runtime.getModel("https://kiegroup.org/dmn/_69EA2E1A-F706-4CFB-8026-9E41397F6301", "DummyInvocation");

        DMNResult evaluateAll = runtime.evaluateAll(dmnModel, runtime.newContext());
        LOG.debug("{}", evaluateAll);
        assertThat(evaluateAll.getDecisionResultByName("Decision-1").getResult()).isEqualTo("Hello World");

        final ClassLoader kieProjectCL = container.getClassLoader();

        List<DMNProfile> defaultDMNProfiles = DMNAssemblerService.getDefaultDMNProfiles(ChainedProperties.getChainedProperties(kieProjectCL));
        final DMNValidator validatorWithCustomCL = DMNValidatorFactory.newValidator(kieProjectCL, defaultDMNProfiles);
        List<DMNMessage> validate = validatorWithCustomCL.validateUsing(VALIDATE_SCHEMA,
                                                                        VALIDATE_MODEL,
                                                                        VALIDATE_COMPILATION)
                                                         .theseModels(getReader("DummyInvocation.dmn"));
        assertThat(validate).as(ValidatorUtil.formatMessages(validate)).hasSize(0);
    }
}
