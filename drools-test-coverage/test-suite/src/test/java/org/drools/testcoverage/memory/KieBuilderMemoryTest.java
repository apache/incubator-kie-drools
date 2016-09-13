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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.drools.testcoverage.common.util.TestConstants;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.internal.utils.KieHelper;

public class KieBuilderMemoryTest extends AbstractMemoryTest {

    private static final long TEST_DURATION_MS = 10_000L;

    private ExecutorService executor;

    @Before
    public void setupExecutor() {
        executor = Executors.newSingleThreadExecutor();
    }

    @After
    public void shutdownExecutor() throws InterruptedException, ExecutionException {
        executor.shutdown();
        if (!executor.awaitTermination(10, TimeUnit.MILLISECONDS)) {
            executor.shutdownNow();
        }
    }

    @Test
    public void testKieBuilderMemoryFootprint() {
        final long endTime = System.currentTimeMillis() + TEST_DURATION_MS;
        executor.submit(() -> {
            while (System.currentTimeMillis() < endTime) {
                try {
                    createKieBase();
                } catch (Exception ex) {
                    logger.info(ex.getMessage());
                    ex.printStackTrace();
                    break;
                }
            }
        });
        final long waitEachIterationMillis = 10;
        measureMemoryFootprintInTime(TEST_DURATION_MS / waitEachIterationMillis, 100, 6, waitEachIterationMillis);
    }

    private void createKieBase() {
        final String drl = "package " + this.getClass().getPackage().getName() + ";" +
                " rule R1 \n" +
                " when \n" +
                "     String() \n" +
                " then \n" +
                " end ";

        final KieResources kieResources = KieServices.Factory.get().getResources();
        final Resource drlResource = kieResources.newByteArrayResource(drl.getBytes(), "UTF-8");
        drlResource.setTargetPath(TestConstants.DRL_TEST_TARGET_PATH);

        final KieHelper kieHelper = new KieHelper();
        kieHelper.addResource(drlResource);
        kieHelper.build();
    }

}
