/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Category(TurtleTestCategory.class)
public class KieScannerMemoryTest {

    private static final Logger logger = LoggerFactory.getLogger(KieScannerMemoryTest.class);

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
        final KieModule kieModule = KieBaseUtil.getKieModuleAndBuildInstallModuleFromDrl(
                TestConstants.PACKAGE_FUNCTIONAL,
                KieBaseTestConfiguration.CLOUD_IDENTITY,
                "rule R when then end");

        final ReleaseId releaseId = kieModule.getReleaseId();
        repository.installArtifact(releaseId, (InternalKieModule) kieModule, MavenUtil.createPomXml(fileManager, releaseId));

        final KieContainer kieContainer = kieServices.newKieContainer(releaseId);
        final KieScanner kieScanner = kieServices.newKieScanner(kieContainer);

        kieScanner.start(20);
        try {
            measureMemoryFootprint(1000, 100, 6, 30);
        } finally {
            kieScanner.stop();
        }
    }

    private void measureMemoryFootprint(final int numberOfIterations, final int numberOfAveragedIterations,
            final int acceptedNumberOfMemoryRaises, final long waitEachIterationMillis) {
        long lastTimeInMillis = System.currentTimeMillis();

        final Runtime runtime = Runtime.getRuntime();

        int memoryRaiseCount = 0;

        long averageMemory = 0;
        final List<Long> averageMemoryFootprints = new ArrayList<Long>();

        for (int i = 1; i < numberOfIterations; i++) {
            waitForMillis(waitEachIterationMillis, lastTimeInMillis);
            lastTimeInMillis = System.currentTimeMillis();

            final long usedMemory = runtime.totalMemory() - runtime.freeMemory();
            averageMemory = averageMemory + usedMemory;
            if ((i % numberOfAveragedIterations) == 0) {
                averageMemory = averageMemory / numberOfAveragedIterations;
                if (averageMemoryFootprints.size() > 0) {
                    final long previousAverageMemory = averageMemoryFootprints.get(averageMemoryFootprints.size() - 1);
                    if (averageMemory > previousAverageMemory) {
                        memoryRaiseCount++;
                    } else {
                        memoryRaiseCount = 0;
                    }
                    assertFalse(
                            "Memory raised during " + (acceptedNumberOfMemoryRaises + 1)
                                    + " consecutive measurements, there is probably some memory leak! "
                                    + getMemoryMeasurementsString(averageMemoryFootprints),
                            memoryRaiseCount > acceptedNumberOfMemoryRaises);
                }
                logger.debug("Average memory: " + averageMemory);
                averageMemoryFootprints.add(averageMemory);
                averageMemory = 0;
            }
        }
    }

    private void waitForMillis(final long millis, final long startTimeMillis) {
        while ((System.currentTimeMillis() - startTimeMillis) < millis) {
            // do nothing - wait
        }
    }

    private String getMemoryMeasurementsString(final List<Long> memoryMeasurements) {
        final StringBuilder builder = new StringBuilder();
        builder.append("Measured used memory: ");
        for (int i = 1; i <= memoryMeasurements.size(); i++) {
            final Long measurement = memoryMeasurements.get(i - 1) / 1024 / 1024;
            builder.append(i + ": " + measurement + " MB; ");
        }
        return builder.toString();
    }
}
