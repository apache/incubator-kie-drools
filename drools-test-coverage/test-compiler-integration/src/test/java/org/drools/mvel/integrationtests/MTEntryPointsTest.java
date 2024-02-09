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
package org.drools.mvel.integrationtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.time.SessionPseudoClock;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests inserting events into KIE Session from multiple threads using one and
 * two entry points.
 *
 * BZ-967599
 */
@RunWith(Parameterized.class)
public class MTEntryPointsTest {

    private KieSession kieSession;

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public MTEntryPointsTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseStreamConfigurations(true);
    }

    @Before
    public void initSession() {
        String str = "package org.jboss.brms\n" +
                     "\n" +
                     "import org.drools.mvel.integrationtests.MTEntryPointsTest.MessageEvent\n" +
                     "\n" +
                     "declare MessageEvent\n" +
                     "    @role( event )\n" +
                     "end\n" +
                     "\n" +
                     "rule \"sum of last event from first entry point\"\n" +
                     "    when\n" +
                     "\t    accumulate (\n" +
                     "\t        MessageEvent ($value : value) over window:length(1) from entry-point \"FirstStream\",\n" +
                     "\t        $sum : sum($value)\n" +
                     "\t    )\n" +
                     "    then\n" +
                     "end\n" +
                     "\n" +
                     "rule \"sum of last event from both entry points\"\n" +
                     "    when\n" +
                     "        accumulate (\n" +
                     "            MessageEvent ($value1 : value) over window:length(1) from entry-point \"FirstStream\",\n" +
                     "            $thirdSum1 : sum($value1)\n" +
                     "        )\n" +
                     "        accumulate (\n" +
                     "            MessageEvent ($value2 : value) over window:length(1) from entry-point \"SecondStream\",\n" +
                     "            $thirdSum2 : sum($value2)\n" +
                     "        )\n" +
                     "    then\n" +
                     "end\n" +
                     "\n";

        KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", str );

        KieModuleModel kmoduleModel = ks.newKieModuleModel();
        kmoduleModel.newKieBaseModel("defaultKieBase")
                    .addPackage("*")
                    .setEventProcessingMode(EventProcessingOption.STREAM)
                    .newKieSessionModel("defaultKieSession")
                    .setDefault(true)
                    .setClockType(ClockTypeOption.PSEUDO);
        kfs.writeKModuleXML(kmoduleModel.toXML());

        final KieBuilder builder = KieUtil.getKieBuilderFromKieFileSystem(kieBaseTestConfiguration, kfs, false);

        assertThat(builder.getResults().getMessages().size()).isEqualTo(0);
        ks.getRepository().addKieModule(builder.getKieModule());

        this.kieSession = ks.newKieContainer(ks.getRepository()
                                               .getDefaultReleaseId()).newKieSession();
    }

    @After
    public void cleanup() {
        if (this.kieSession != null) {
            this.kieSession.dispose();
        }
    }

    /**
     * Inserts events using multiple threads into one EntryPoint. The insert
     * operation is synchronized on corresponding SessionEntryPoint instance.
     */
    @Test
    public void testOneEntryPoint() throws Exception {
        final EntryPoint firstThreadEntryPoint = kieSession.getEntryPoint("FirstStream");

        final int numInsertersInEachEntryPoint = 10;
        final ExecutorService executorService = Executors.newFixedThreadPool(numInsertersInEachEntryPoint);
        try {
            final List<Future<?>> futures = new ArrayList<>();

            for (int i = 0; i < numInsertersInEachEntryPoint; i++) {
                // future for exception watching
                final Future<?> futureForFirstThread = executorService.submit(
                        new TestInserter(kieSession, firstThreadEntryPoint));
                futures.add(futureForFirstThread);
            }

            for (final Future<?> f : futures) {
                f.get(30, TimeUnit.SECONDS);
            }
        } finally {
            executorService.shutdownNow();
        }
    }

    /**
     * Inserts events using multiple threads into two EntryPoints. The insert
     * operation is synchronized on corresponding SessionEntryPoint instance.
     */
    @Test
    public void testTwoEntryPoints() throws Exception {

        final EntryPoint firstThreadEntryPoint = kieSession.getEntryPoint("FirstStream");
        final EntryPoint secondThreadEntryPoint = kieSession.getEntryPoint("SecondStream");

        final int numInsertersInEachEntryPoint = 10;
        final int numThreadPoolCapacity = numInsertersInEachEntryPoint * 2;

        final ExecutorService executorService = Executors.newFixedThreadPool(numThreadPoolCapacity);
        try {
            final List<Future<?>> futures = new ArrayList<>();

            for (int i = 0; i < numInsertersInEachEntryPoint; i++) {
                // working only with first stream, future for exception watching
                final Future<?> futureForFirstThread = executorService.submit(new TestInserter(kieSession,
                                                                                         firstThreadEntryPoint));
                futures.add(futureForFirstThread);

                // working only with second stream, future for exception watching
                final Future<?> futureForSecondThread = executorService.submit(new TestInserter(kieSession,
                                                                                          secondThreadEntryPoint));
                futures.add(futureForSecondThread);
            }

            for (final Future<?> f : futures) {
                f.get(30, TimeUnit.SECONDS);
            }
        } finally {
            executorService.shutdownNow();
        }
    }

    /**
     * Inserts 10 test events into specified EntryPoint and advances pseudo-clock
     * time by a fixed amount.
     *
     * Insert operation is synchronized on given SessionEntryPoint instance.
     */
    public static class TestInserter implements Runnable {

        private final EntryPoint entryPoint;
        private final KieSession kieSession;

        public TestInserter(final KieSession kieSession, final EntryPoint entryPoint) {
            this.kieSession = kieSession;
            this.entryPoint = entryPoint;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                synchronized (entryPoint) {
                    entryPoint.insert(new MessageEvent(i));
                }
                advanceTime(100);
            }
        }

        private void advanceTime(long millis) {
            SessionPseudoClock pseudoClock = kieSession.getSessionClock();
            pseudoClock.advanceTime(millis, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Immutable event used in the test.
     */
    public static class MessageEvent {
        private int value;

        public MessageEvent(final int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }
}
