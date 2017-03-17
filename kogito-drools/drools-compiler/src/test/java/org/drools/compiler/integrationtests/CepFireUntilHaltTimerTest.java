/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.kie.api.time.SessionPseudoClock;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests proper timer firing using accumulate and fireUntilHalt() mode.
 * BZ-981270
 */
@Ignore
public class CepFireUntilHaltTimerTest {

    private KieSession ksession;
    private List<Long> result;
    private SessionPseudoClock clock;

    public void init() {
        String drl = "package org.drools.compiler.integrationtests\n" +
                     "\n" +
                     "import org.drools.compiler.integrationtests.CepFireUntilHaltTimerTest.MetadataEvent;\n" +
                     "import java.util.List;\n" +
                     "\n" +
                     "global List countResult;\n" +
                     "\n" +
                     "declare MetadataEvent\n" +
                     "    @role( event )\n" +
                     "    @timestamp( metadataTimestamp )\n" +
                     "    @duration( metadataDuration )\n" +
                     "    @expires (24h)\n" +
                     "end\n" +
                     "\n" +
                     "rule \"Number of metadata events in the last 10 seconds\"\n" +
                     "timer (int: 1s 10s)\n" +
                     "//timer (int: 0s 10s) // this works\n" +
                     "when\n" +
                     "    String( this == \"events_inserted\" )\n" +
                     "    $count: Number() from accumulate( $event: MetadataEvent() over window:time(10s),  count( $event ) )\n" +
                     "then\n" +
                     "    System.out.println(\"Events count: \" + $count);\n" +
                     "    countResult.add($count);\n" +
                     "end\n";

        KieServices ks = KieServices.Factory.get();

        KieModuleModel module = ks.newKieModuleModel();

        KieBaseModel defaultBase = module.newKieBaseModel("defaultKBase")
                                         .setDefault(true)
                                         .addPackage("*")
                                         .setEventProcessingMode(EventProcessingOption.STREAM);
        defaultBase.newKieSessionModel("defaultKSession")
                   .setDefault(true)
                   .setClockType(ClockTypeOption.get("pseudo"));

        KieFileSystem kfs = ks.newKieFileSystem()
                              .write("src/main/resources/r1.drl", drl);
        kfs.writeKModuleXML(module.toXML());
        ks.newKieBuilder(kfs).buildAll();

        ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId())
                     .newKieSession();

        result = new ArrayList<Long>();
        ksession.setGlobal("countResult", result);

        clock = ksession.getSessionClock();
        ksession.insert(clock);
    }

    public void cleanup() {
        ksession.dispose();
    }

    @Test
    public void testTwoRunsTimerAccumulateFireUntilHalt() throws Exception {
        init();
        performTest();
        cleanup();
        init();
        performTest();
        cleanup();
    }

    private void performTest() throws Exception {
        ExecutorService thread = Executors.newSingleThreadExecutor();
        final Future fireUntilHaltResult = thread.submit(new Runnable() {
            @Override
            public void run() {
                ksession.fireUntilHalt();
            }
        });

        try {

            final int ITEMS = 10;

            final Date eventTime = new Date(clock.getCurrentTime());
            for (int i = 0; i < ITEMS; i++) {
                ksession.insert(new MetadataEvent(eventTime, 0L));
            }

            // this triggers the rule on after all events had been inserted
            ksession.insert( "events_inserted" );

            // give time to fireUntilHalt to process the insertions
            TimerUtils.sleepMillis(500);

            for (int count=0; count < 40; count++) {
                clock.advanceTime( 1, TimeUnit.SECONDS);
            }
            TimerUtils.sleepMillis(500);

            assertTrue( "The result does not contain at least 2 elements", result.size() >= 2);
            assertEquals(ITEMS, (long) result.get(0));
            assertEquals(0, (long) result.get(1));
        } finally {
            ksession.halt();
            // wait for the engine to finish and throw exception if any was thrown
            // in engine's thread
            fireUntilHaltResult.get(60000, TimeUnit.SECONDS);
            thread.shutdown();
        }
    }

    public static class MetadataEvent implements Serializable {

        private static final long serialVersionUID = 6827172457832354239L;
        private Date metadataTimestamp;
        private Long metadataDuration;
        private String name;

        public MetadataEvent() {
        }

        public MetadataEvent(Date timestamp, Long duration) {
            metadataTimestamp = timestamp;
            metadataDuration = duration;
        }

        public MetadataEvent(String name, Date timestamp, Long duration) {
            this.name = name;
            metadataTimestamp = timestamp;
            metadataDuration = duration;
        }

        public Date getMetadataTimestamp() {
            return metadataTimestamp != null ? (Date) metadataTimestamp.clone() : null;
        }

        public void setMetadataTimestamp(Date metadataTimestamp) {
            this.metadataTimestamp = metadataTimestamp != null
                                     ? (Date) metadataTimestamp.clone() : null;
        }

        public Long getMetadataDuration() {
            return metadataDuration;
        }

        public void setMetadataDuration(Long metadataDuration) {
            this.metadataDuration = metadataDuration;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return String.format("MetadataEvent[name='%s' timestamp='%s', duration='%s']", name, metadataTimestamp, metadataDuration);
        }
    }

    /**
     * Utility class providing methods for coping with timing issues, such as
     * {@link java.lang.Thread#sleep(long, int)} inaccuracy, on certain OS.
     * <p/>
     * Inspired by http://stackoverflow.com/questions/824110/accurate-sleep-for-java-on-windows
     * and http://andy-malakov.blogspot.cz/2010/06/alternative-to-threadsleep.html.
     */
    public static class TimerUtils {

        private static final long SLEEP_PRECISION = Long.valueOf(System.getProperty("TIMER_SLEEP_PRECISION", "50000"));

        private static final long SPIN_YIELD_PRECISION = Long.valueOf(System.getProperty("TIMER_YIELD_PRECISION", "30000"));

        private TimerUtils() {
        }

        /**
         * Sleeps for specified amount of time in milliseconds.
         *
         * @param duration the amount of milliseconds to wait
         * @throws InterruptedException if the current thread gets interrupted
         */
        public static void sleepMillis(final long duration) throws InterruptedException {
            sleepNanos(TimeUnit.MILLISECONDS.toNanos(duration));
        }

        /**
         * Sleeps for specified amount of time in nanoseconds.
         *
         * @param nanoDuration the amount of nanoseconds to wait
         * @throws InterruptedException if the current thread gets interrupted
         */
        public static void sleepNanos(final long nanoDuration) throws InterruptedException {
            final long end = System.nanoTime() + nanoDuration;
            long timeLeft = nanoDuration;
            do {
                if (timeLeft > SLEEP_PRECISION) {
                    Thread.sleep(1);
                } else if (timeLeft > SPIN_YIELD_PRECISION) {
                    Thread.yield();
                }
                timeLeft = end - System.nanoTime();
            } while (timeLeft > 0);
        }
    }

}
