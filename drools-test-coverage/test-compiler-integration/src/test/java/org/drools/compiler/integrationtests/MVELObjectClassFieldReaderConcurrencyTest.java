/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.drools.compiler.integrationtests.ConstraintConcurrencyTest.Album;
import org.drools.compiler.integrationtests.ConstraintConcurrencyTest.Bus;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.test.testcategory.TurtleTestCategory;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
@Category(TurtleTestCategory.class)
public class MVELObjectClassFieldReaderConcurrencyTest {

    private static int LOOP = 500;

    private static int THREADS = 32;
    private static int REQUESTS = 32;

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public MVELObjectClassFieldReaderConcurrencyTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    @Test(timeout = 300000)
    public void testMVELObjectClassFieldReaderConcurrency() {
        final String drl =
                "package com.example.reproducer\n" +
                           "import " + Bus.class.getCanonicalName() + ";\n" +
                           "dialect \"mvel\"\n" +
                           "rule R1\n" +
                           "    when\n" +
                           "        $s : String()\n" +
                           "        $bus1 : Bus( name.concat(karaoke.dvd[\"POWER PLANT\"].artist) == $s )\n" +
                           "    then\n" +
                           "end\n";

        List<Exception> exceptions = new ArrayList<>();

        KieBase kieBase = null;
        if (kieBaseTestConfiguration.isExecutableModel()) { // There's no such a thing as jitting, so we can create the KieBase once
            kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration, drl);
        }
        for (int i = 0; i < LOOP; i++) {
            if (!kieBaseTestConfiguration.isExecutableModel()) { // to reset MVELConstraint Jitting we need to create a new KieBase each time
                kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration, drl);
            }
            ExecutorService executor = Executors.newFixedThreadPool(THREADS);
            CountDownLatch latch = new CountDownLatch(THREADS);
            for (int j = 0; j < REQUESTS; j++) {
                KieBase finalKieBase = kieBase;
                executor.execute(new Runnable() {

                    public void run() {
                        KieSession kSession = finalKieBase.newKieSession();

                        Bus bus1 = new Bus("red", 30);
                        bus1.getKaraoke().getDvd().put("POWER PLANT", new Album("POWER PLANT", "GAMMA RAY"));
                        bus1.getKaraoke().getDvd().put("Somewhere Out In Space", new Album("Somewhere Out In Space", "GAMMA RAY"));
                        kSession.insert(bus1);
                        kSession.insert("Start");

                        try {
                            latch.countDown();
                            latch.await();
                        } catch (InterruptedException e) {
                            // ignore
                        }

                        try {
                            kSession.fireAllRules();
                        } catch (Exception e) {
                            if (exceptions.isEmpty()) {
                                e.printStackTrace();
                            }
                            exceptions.add(e);
                        } finally {
                            kSession.dispose();
                        }
                    }
                });
            }

            executor.shutdown();
            try {
                executor.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                // ignore
            }
        }

        assertEquals(0, exceptions.size());
    }
}
