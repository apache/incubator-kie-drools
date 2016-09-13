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

package org.drools.testcoverage.memory;

import java.io.IOException;
import org.drools.compiler.TurtleTestCategory;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.core.util.FileManager;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.MavenUtil;
import org.drools.testcoverage.common.util.TestConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieScanner;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.scanner.MavenRepository;

@Category(TurtleTestCategory.class)
public class KieScannerMemoryTest extends AbstractMemoryTest {

    private FileManager fileManager;

    @Before
    public void setUp() throws Exception {
        this.fileManager = new FileManager();
        this.fileManager.setUp();
    }

    @After
    public void tearDown() throws Exception {
        this.fileManager.tearDown();
    }

    @Test
    public void testScannerMemoryFootprint() throws IOException {
        final KieServices kieServices = KieServices.Factory.get();

        final MavenRepository repository = MavenRepository.getMavenRepository();
        final KieModule kieModule = KieBaseUtil.buildAndInstallKieModuleIntoRepo(
                TestConstants.PACKAGE_FUNCTIONAL,
                KieBaseTestConfiguration.CLOUD_IDENTITY,
                "rule R when then end");

        final ReleaseId releaseId = kieModule.getReleaseId();
        repository.installArtifact(releaseId, (InternalKieModule) kieModule, MavenUtil.createPomXml(fileManager, releaseId));

        final KieContainer kieContainer = kieServices.newKieContainer(releaseId);
        final KieScanner kieScanner = kieServices.newKieScanner(kieContainer);

        kieScanner.start(20);
        try {
            measureMemoryFootprintInTime(1000, 100, 6, 30);
        } finally {
            kieScanner.stop();
        }
    }
}
