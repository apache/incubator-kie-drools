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
package org.drools.compiler.integrationtests;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.drools.testcoverage.common.util.TimeUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.time.SessionPseudoClock;

import static org.assertj.core.api.Assertions.assertThat;

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
        final String drl = "package org.drools.compiler.integrationtests\n" +
                     "\n" +
                     "import " + CepFireUntilHaltTimerTest.MetadataEvent.class.getCanonicalName() + ";\n" +
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

        final KieServices ks = KieServices.Factory.get();

        final KieModuleModel module = ks.newKieModuleModel();

        final KieBaseModel defaultBase = module.newKieBaseModel("defaultKBase")
                                         .setDefault(true)
                                         .addPackage("*")
                                         .setEventProcessingMode(EventProcessingOption.STREAM);
        defaultBase.newKieSessionModel("defaultKSession")
                   .setDefault(true)
                   .setClockType(ClockTypeOption.PSEUDO);

        final KieFileSystem kfs = ks.newKieFileSystem()
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
        final ExecutorService thread = Executors.newSingleThreadExecutor();
        final Future fireUntilHaltResult = thread.submit(() -> ksession.fireUntilHalt());
        try {

            final int ITEMS = 10;

            final Date eventTime = new Date(clock.getCurrentTime());
            for (int i = 0; i < ITEMS; i++) {
                ksession.insert(new MetadataEvent(eventTime, 0L));
            }

            // this triggers the rule on after all events had been inserted
            ksession.insert( "events_inserted" );

            // give time to fireUntilHalt to process the insertions
            TimeUtil.sleepMillis(500);

            for (int count=0; count < 40; count++) {
                clock.advanceTime( 1, TimeUnit.SECONDS);
            }
            TimeUtil.sleepMillis(500);

            assertThat(result.size() >= 2).as("The result does not contain at least 2 elements").isTrue();
            assertThat((long) result.get(0)).isEqualTo(ITEMS);
            assertThat((long) result.get(1)).isEqualTo(0);
        } finally {
            ksession.halt();
            // wait for the engine to finish and throw exception if any was thrown
            // in engine's thread
            fireUntilHaltResult.get(60000, TimeUnit.SECONDS);
            thread.shutdownNow();
        }
    }

    public static class MetadataEvent implements Serializable {

        private static final long serialVersionUID = 6827172457832354239L;
        private Date metadataTimestamp;
        private Long metadataDuration;
        private String name;

        public MetadataEvent() {
        }

        public MetadataEvent(final Date timestamp, final Long duration) {
            metadataTimestamp = timestamp;
            metadataDuration = duration;
        }

        public MetadataEvent(final String name, final Date timestamp, final Long duration) {
            this.name = name;
            metadataTimestamp = timestamp;
            metadataDuration = duration;
        }

        public Date getMetadataTimestamp() {
            return metadataTimestamp != null ? (Date) metadataTimestamp.clone() : null;
        }

        public void setMetadataTimestamp(final Date metadataTimestamp) {
            this.metadataTimestamp = metadataTimestamp != null
                                     ? (Date) metadataTimestamp.clone() : null;
        }

        public Long getMetadataDuration() {
            return metadataDuration;
        }

        public void setMetadataDuration(final Long metadataDuration) {
            this.metadataDuration = metadataDuration;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return String.format("MetadataEvent[name='%s' timestamp='%s', duration='%s']", name, metadataTimestamp, metadataDuration);
        }
    }
}
