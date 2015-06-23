/*
 * Copyright 2015 JBoss Inc
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
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.drools.core.time.SessionPseudoClock;
import org.junit.After;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.EntryPoint;

/**
 * Tests inserting events into KIE Session from multiple threads using one and
 * two entry points.
 *
 * BZ-967599
 */
public class MTEntryPointsTest {

    private KieSession kieSession;

    @Before
    public void initSession() {
        String str = "package org.jboss.brms\n" +
                     "\n" +
                     "import org.drools.compiler.integrationtests.MTEntryPointsTest.MessageEvent\n" +
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
                    .setClockType(ClockTypeOption.get("pseudo"));
        kfs.writeKModuleXML(kmoduleModel.toXML());

        KieBuilder builder = ks.newKieBuilder(kfs).buildAll();
        assertEquals(0, builder.getResults().getMessages().size());
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

        int numInsertersInEachEntryPoint = 10;
        int numThreadPoolCapacity = numInsertersInEachEntryPoint;

        ExecutorService executorService = Executors.newFixedThreadPool(numThreadPoolCapacity);
        List<Future<?>> futures = new ArrayList<Future<?>>();

        for (int i = 0; i < numInsertersInEachEntryPoint; i++) {
            // future for exception watching
            Future<?> futureForFirstThread = executorService.submit(
                    new TestInserter(kieSession, firstThreadEntryPoint));
            futures.add(futureForFirstThread);
        }

        try {
            for (Future<?> f : futures) {
                f.get(30, TimeUnit.SECONDS);
            }
        } catch (ExecutionException ex) {
            throw ex;
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

        int numInsertersInEachEntryPoint = 10;
        int numThreadPoolCapacity = numInsertersInEachEntryPoint * 2;

        ExecutorService executorService = Executors.newFixedThreadPool(numThreadPoolCapacity);
        List<Future<?>> futures = new ArrayList<Future<?>>();

        for (int i = 0; i < numInsertersInEachEntryPoint; i++) {
            // working only with first stream, future for exception watching
            Future<?> futureForFirstThread = executorService.submit(new TestInserter(kieSession,
                                                                                     firstThreadEntryPoint));
            futures.add(futureForFirstThread);

            // working only with second stream, future for exception watching
            Future<?> futureForSecondThread = executorService.submit(new TestInserter(kieSession,
                                                                                      secondThreadEntryPoint));
            futures.add(futureForSecondThread);
        }

        try {
            for (Future<?> f : futures) {
                f.get(30, TimeUnit.SECONDS);
            }
        } catch (ExecutionException ex) {
            throw ex;
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
