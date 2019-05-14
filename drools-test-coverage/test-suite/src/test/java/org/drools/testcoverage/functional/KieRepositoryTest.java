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
import org.drools.testcoverage.common.util.TestConstants;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;

import java.io.IOException;

/**
 * Tests correct behavior of KieRepository, such as adding/removing KieModules.
 */
public class KieRepositoryTest {

    private static final String TEST_ARTIFACT_ID = "kie-repo-test";
    private static final String TEST_ARTIFACT_VERSION_RELEASE = "1.0.0";
    private static final String TEST_ARTIFACT_VERSION_SNAPSHOT = "1.0.0-SNAPSHOT";

    private KieServices kieServices;

    private static final String DRL = "package defaultKBase;\n rule testRule when then end\n";

    @Before
    public void initialize() throws IOException {
        kieServices = KieServices.Factory.get();
    }

    /**
     * Tests adding a KieModule with a non-SNAPSHOT ReleaseId to KieRepository.
     */
    @Test
    public void testAddKieModuleRelease() {
        final ReleaseId releaseId = kieServices
                .newReleaseId(TestConstants.PACKAGE_TESTCOVERAGE, TEST_ARTIFACT_ID, TEST_ARTIFACT_VERSION_RELEASE);
        this.testKieModuleAddition(releaseId);
    }

    /**
     * Tests adding a KieModule with a SNAPSHOT ReleaseId to KieRepository.
     */
    @Test
    public void testAddKieModuleSnapshot() {
        final ReleaseId releaseId = kieServices
                .newReleaseId(TestConstants.PACKAGE_TESTCOVERAGE, TEST_ARTIFACT_ID, TEST_ARTIFACT_VERSION_SNAPSHOT);
        this.testKieModuleAddition(releaseId);
    }

    private void testKieModuleAddition(final ReleaseId releaseId) {
        final KieRepository kieRepository = kieServices.getRepository();

        this.createKieModule(releaseId);
        Assertions.assertThat(kieRepository.getKieModule(releaseId)).as("KieModule should be in KieRepository").isNotNull();
    }

    /**
     * Tests removing a KieModule with a non-SNAPSHOT ReleaseId from KieRepository.
     */
    @Test
    public void testRemoveKieModuleRelease() {
        final ReleaseId releaseId = kieServices
                .newReleaseId(TestConstants.PACKAGE_TESTCOVERAGE, TEST_ARTIFACT_ID, TEST_ARTIFACT_VERSION_RELEASE);
        this.testKieModuleRemoval(releaseId);
    }

    /**
     * Tests removing a KieModule with a SNAPSHOT ReleaseId from KieRepository.
     */
    @Test
    public void testRemoveKieModuleSnapshot() {
        final ReleaseId releaseId = kieServices
                .newReleaseId(TestConstants.PACKAGE_TESTCOVERAGE, TEST_ARTIFACT_ID, TEST_ARTIFACT_VERSION_SNAPSHOT);
        this.testKieModuleRemoval(releaseId);
    }

    private void testKieModuleRemoval(final ReleaseId releaseId) {
        final KieRepository kieRepository = kieServices.getRepository();

        this.createKieModule(releaseId);
        Assertions.assertThat(kieRepository.getKieModule(releaseId)).as("KieModule should be in KieRepository").isNotNull();


        kieRepository.removeKieModule(releaseId);
        Assertions.assertThat(kieRepository.getKieModule(releaseId)).as("KieModule should NOT be in KieRepository").isNull();
    }

    /**
     * Helper method creating simple KieModule with given ReleaseId.
     */
    private void createKieModule(final ReleaseId releaseId) {
        final KieModuleModel kmodule = kieServices.newKieModuleModel();
        kmodule.newKieBaseModel("defaultKBase").setDefault(true);

        final KieFileSystem kfs = kieServices.newKieFileSystem();
        kfs.generateAndWritePomXML(releaseId);
        kfs.writeKModuleXML(kmodule.toXML());

        kfs.write("src/main/resources/defaultKBase/test.drl",
                kieServices.getResources().newByteArrayResource(DRL.getBytes()));

        final KieBuilder kbuilder = kieServices.newKieBuilder(kfs);
        kbuilder.buildAll();
    }
}
