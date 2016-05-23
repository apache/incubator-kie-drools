/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.testcoverage.functional;

import org.assertj.core.api.Assertions;
import org.drools.compiler.kie.builder.impl.InternalKieContainer;
import org.drools.testcoverage.common.util.TestConstants;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;

import java.io.IOException;

/**
 * This class's purpose is to test obtaining default KieSessions and default KieBases from KieContainer. It tests
 * KieContainer's behaviour in other cases than simple one default KieBase with one default KieSession.
 */
public class KieContainerDefaultsTest {

    private static final String DRL = "package defaultKBase;\n rule testRule when then end\n";

    private static final ReleaseId RELEASE_ID = KieServices.Factory.get().newReleaseId(
            TestConstants.PACKAGE_TESTCOVERAGE,
            "kie-container-defaults-test",
            "1.0.0-SNAPSHOT");

    private KieServices kieServices;

    @Before
    public void initialize() throws IOException {
        kieServices = KieServices.Factory.get();
    }

    /**
     * This test checks if default KieBases behave as expected.
     */
    @Test
    public void testTwoKieBasesOneDefault() {
        final KieModuleModel kieModule = kieServices.newKieModuleModel();
        kieModule.newKieBaseModel("firstKBase").setDefault(true);
        kieModule.newKieBaseModel("secondKBase").setDefault(false);

        buildKieModule(kieModule);

        final KieContainer kieContainer = kieServices.newKieContainer(RELEASE_ID);

        final KieBase firstKBase = kieContainer.getKieBase("firstKBase");
        final KieBase secondKBase = kieContainer.getKieBase("secondKBase");

        Assertions.assertThat(kieContainer.getKieBase()).isEqualTo(firstKBase);
        Assertions.assertThat(kieContainer.getKieBase()).isNotEqualTo(secondKBase);
    }

    /**
     * This test checks how KieBases behave when all are explicitly set not to be default.
     */
    @Test(expected = RuntimeException.class)
    public void testTwoKieBasesNoneDefault() {
        final KieModuleModel kieModule = kieServices.newKieModuleModel();
        kieModule.newKieBaseModel("firstKBase").setDefault(false);
        kieModule.newKieBaseModel("secondKBase").setDefault(false);

        buildKieModule(kieModule);

        final KieContainer kieContainer = kieServices.newKieContainer(RELEASE_ID);

        kieContainer.getKieBase();
    }

    /**
     * This test checks if default KieSessions behave as expected.
     */
    @Test
    public void testTwoKieSessionsOneDefault() {
        final KieModuleModel kieModule = kieServices.newKieModuleModel();
        kieModule.newKieBaseModel("firstKBase").setDefault(true).newKieSessionModel("firstKSession").setDefault(true);
        kieModule.newKieBaseModel("secondKBase").setDefault(true).newKieSessionModel("secondKSession");

        buildKieModule(kieModule);

        final KieContainer kieContainer = kieServices.newKieContainer(RELEASE_ID);

        final KieSession firstKSession = kieContainer.newKieSession("firstKSession");
        final KieSession secondKSession = kieContainer.newKieSession("secondKSession");

        try {
            Assertions.assertThat(firstKSession).isEqualTo(((InternalKieContainer) kieContainer).getKieSession());
            Assertions.assertThat(secondKSession).isNotEqualTo(((InternalKieContainer) kieContainer).getKieSession());
        } finally {
            firstKSession.dispose();
            secondKSession.dispose();
        }
    }

    /**
     * This test checks how KieSessions behave when more than one is set as default.
     */
    @Test(expected = RuntimeException.class)
    public void testTwoKieSessionsBothDefault() {
        final KieModuleModel kieModule = kieServices.newKieModuleModel();
        kieModule.newKieBaseModel("firstKBase").setDefault(true).newKieSessionModel("firstKSession").setDefault(true);
        kieModule.newKieBaseModel("secondKBase").setDefault(true).newKieSessionModel("secondKSession").setDefault(true);

        buildKieModule(kieModule);

        final KieContainer kieContainer = kieServices.newKieContainer(RELEASE_ID);

        kieContainer.newKieSession();
    }

    /**
     * This test checks how KieSessions behave when all are explicitly set not to be default.
     */
    @Test(expected = RuntimeException.class)
    public void testTwoKieSessionsNoneDefault() {
        final KieModuleModel kieModule = kieServices.newKieModuleModel();
        kieModule.newKieBaseModel("firstKBase").setDefault(true).newKieSessionModel("firstKSession").setDefault(false);
        kieModule.newKieBaseModel("secondKBase").setDefault(true).newKieSessionModel("secondKSession").setDefault(false);

        buildKieModule(kieModule);

        final KieContainer kieContainer = kieServices.newKieContainer(RELEASE_ID);

        kieContainer.newKieSession();
    }

    /**
     * This test checks how KieSessions behave when default state isn't explicitly set.
     */
    @Test(expected = RuntimeException.class)
    public void testTwoKieSessionsDefaultNotSet() {
        final KieModuleModel kieModule = kieServices.newKieModuleModel();
        kieModule.newKieBaseModel("firstKBase").setDefault(true).newKieSessionModel("firstKSession");
        kieModule.newKieBaseModel("secondKBase").setDefault(false).newKieSessionModel("secondKSession");

        buildKieModule(kieModule);

        final KieContainer kieContainer = kieServices.newKieContainer(RELEASE_ID);

        kieContainer.newKieSession();
    }

    /**
     * This test checks if default KieSessions behave as expected even if one of them is Stateless.
     */
    @Test
    public void testTwoKieSessionsOneStatelessDefault() {
        final KieModuleModel kieModule = kieServices.newKieModuleModel();
        kieModule.newKieBaseModel("firstKBase").setDefault(true).newKieSessionModel("firstKSession").setType(KieSessionModel.KieSessionType.STATELESS).setDefault(true);
        kieModule.newKieBaseModel("secondKBase").setDefault(true).newKieSessionModel("secondKSession").setDefault(false);

        buildKieModule(kieModule);

        final KieContainer kieContainer = kieServices.newKieContainer(RELEASE_ID);

        final StatelessKieSession firstKSession = kieContainer.newStatelessKieSession("firstKSession");
        final KieSession secondKSession = kieContainer.newKieSession("secondKSession");

        try {
            Assertions.assertThat(firstKSession).isEqualTo(((InternalKieContainer) kieContainer).getStatelessKieSession());
            Assertions.assertThat(secondKSession).isNotEqualTo(((InternalKieContainer) kieContainer).getStatelessKieSession());
        } finally {
            secondKSession.dispose();
        }
    }

    /**
     * This test checks if default StatelessKieSessions behave as expected.
     */
    @Test
    public void testTwoStatelessKieSessionsOneDefault() {
        final KieModuleModel kieModule = kieServices.newKieModuleModel();
        kieModule.newKieBaseModel("firstKBase").setDefault(true).newKieSessionModel("firstKSession").setType(KieSessionModel.KieSessionType.STATELESS).setDefault(true);
        kieModule.newKieBaseModel("secondKBase").setDefault(true).newKieSessionModel("secondKSession").setType(KieSessionModel.KieSessionType.STATELESS).setDefault(false);

        buildKieModule(kieModule);

        final KieContainer kieContainer = kieServices.newKieContainer(RELEASE_ID);

        final StatelessKieSession firstKSession = kieContainer.newStatelessKieSession("firstKSession");
        final StatelessKieSession secondKSession = kieContainer.newStatelessKieSession("secondKSession");

        Assertions.assertThat(firstKSession).isEqualTo(((InternalKieContainer) kieContainer).getStatelessKieSession());
        Assertions.assertThat(secondKSession).isNotEqualTo(((InternalKieContainer) kieContainer).getStatelessKieSession());
    }

    /**
     * This test checks how StatelessKieSessions behave when more than one is set as default.
     */
    @Test(expected = RuntimeException.class)
    public void testTwoStatelessKieSessionsBothDefault() {
        final KieModuleModel kieModule = kieServices.newKieModuleModel();
        kieModule.newKieBaseModel("firstKBase").setDefault(true).newKieSessionModel("firstKSession").setType(KieSessionModel.KieSessionType.STATELESS).setDefault(true);
        kieModule.newKieBaseModel("secondKBase").setDefault(true).newKieSessionModel("secondKSession").setType(KieSessionModel.KieSessionType.STATELESS).setDefault(true);

        buildKieModule(kieModule);

        final KieContainer kieContainer = kieServices.newKieContainer(RELEASE_ID);

        kieContainer.newStatelessKieSession();
    }

    /**
     * This test checks how StatelessKieSessions behave when all are explicitly set not to be default.
     */
    @Test(expected = RuntimeException.class)
    public void testTwoStatelessKieSessionsNoneDefault() {
        final KieModuleModel kieModule = kieServices.newKieModuleModel();
        kieModule.newKieBaseModel("firstKBase").setDefault(true).newKieSessionModel("firstKSession").setType(KieSessionModel.KieSessionType.STATELESS).setDefault(false);
        kieModule.newKieBaseModel("secondKBase").setDefault(true).newKieSessionModel("secondKSession").setType(KieSessionModel.KieSessionType.STATELESS).setDefault(false);

        buildKieModule(kieModule);

        final KieContainer kieContainer = kieServices.newKieContainer(RELEASE_ID);

        kieContainer.newStatelessKieSession();
    }

    /**
     * This test checks how StatelessKieSessions behave when default state isn't explicitly set.
     */
    @Test(expected = RuntimeException.class)
    public void testTwoStatelessKieSessionsDefaultNotSet() {
        final KieModuleModel kieModule = kieServices.newKieModuleModel();
        kieModule.newKieBaseModel("firstKBase").setDefault(true).newKieSessionModel("firstKSession").setType(KieSessionModel.KieSessionType.STATELESS);
        kieModule.newKieBaseModel("secondKBase").setDefault(true).newKieSessionModel("secondKSession").setType(KieSessionModel.KieSessionType.STATELESS);

        buildKieModule(kieModule);

        final KieContainer kieContainer = kieServices.newKieContainer(RELEASE_ID);

        kieContainer.newStatelessKieSession();
    }

    /**
     * This is a helper method that prevents code copying.
     *
     * @param kieModule KieModuleModel used in the particular test.
     */
    private void buildKieModule(KieModuleModel kieModule) {
        final KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.generateAndWritePomXML(RELEASE_ID);
        kieFileSystem.writeKModuleXML(kieModule.toXML());

        kieFileSystem.write("src/main/resources/defaultKBase/test.drl", kieServices.getResources().newByteArrayResource(DRL.getBytes()));

        final KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
        kieBuilder.buildAll();
    }
}