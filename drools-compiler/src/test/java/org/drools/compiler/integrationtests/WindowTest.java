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

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.internal.KnowledgeBaseFactory;

public class WindowTest {

    private KieSession ksession;

    private SessionPseudoClock clock;

    private String drl =
            "package org.drools.compiler.integrationtests;\n" +
            "\n" +
            "import org.drools.compiler.integrationtests.WindowTest.TestEvent\n" +
            "\n" +
            "global java.util.List result\n" +
            "\n" +
            "declare TestEvent\n" +
            "    @role( event )\n" +
            "end\n" +
            "\n" +
            "declare window DeclaredTimeWindow\n" +
            "    TestEvent ( name == \"timeDec\" ) over window:time( 50ms ) from entry-point EventStream\n" +
            "end\n" +
            "\n" +
            "declare window DeclaredLengthWindow\n" +
            "    TestEvent ( name == \"lengthDec\" ) over window:length( 5 ) from entry-point EventStream\n" +
            "end\n" +
            "\n" +
            "query \"TestTimeWindow\"\n" +
            "    Number( $eventCount : longValue ) from\n" +
            "        accumulate (\n" +
            "            $event : TestEvent ( name == \"time\" ) over window:time( 300ms ) from entry-point EventStream,\n" +
            "            count($event)\n" +
            "        )\n" +
            "end\n" +
            "\n" +
            "query \"TestLengthWindow\"\n" +
            "    Number( $eventCount : longValue ) from\n" +
            "        accumulate (\n" +
            "            $event : TestEvent ( name == \"length\" ) over window:length( 10 ) from entry-point EventStream,\n" +
            "            count($event)\n" +
            "        )\n" +
            "end\n" +
            "\n" +
            "query \"TestDeclaredTimeWindow\"\n" +
            "    Number( $eventCount : longValue ) from\n" +
            "        accumulate ( \n" +
            "            $event : TestEvent () from window DeclaredTimeWindow,\n" +
            "            count( $event )\n" +
            "        )\n" +
            "end\n" +
            "\n" +
            "query \"TestDeclaredLengthWindow\"\n" +
            "    Number( $eventCount : longValue ) from\n" +
            "        accumulate ( \n" +
            "            $event : TestEvent () from window DeclaredLengthWindow,\n" +
            "            count( $event )\n" +
            "        )\n" +
            "end\n" +
            "\n" +
            "rule \"TestDeclaredTimeWindowRule\"\n" +
            "    when\n" +
            "        Number( $eventCount : longValue, longValue > 0 ) from \n" +
            "            accumulate ( \n" +
            "                $event : TestEvent () from window DeclaredTimeWindow,\n" +
            "                count( $event )\n" +
            "            )\n" +
            "    then\n" +
            "        result.add($eventCount);\n" +
            "end\n" +
            "\n" +
            "rule \"TestDeclaredLengthWindowRule\"\n" +
            "    when\n" +
            "        Number( $eventCount : longValue, longValue > 0 ) from \n" +
            "            accumulate ( \n" +
            "                $event : TestEvent () from window DeclaredLengthWindow,\n" +
            "                count( $event )\n" +
            "            )\n" +
            "    then\n" +
            "        result.add($eventCount);\n" +
            "end\n";

    @Before
    public void initialization() {
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();

        kfs.write("src/main/resources/kbase1/window_test.drl", drl);

        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);

        kbuilder.buildAll();

        List<Message> res = kbuilder.getResults().getMessages(Level.ERROR);

        assertEquals(res.toString(), 0, res.size());

        KieBaseConfiguration kbconf = KnowledgeBaseFactory
                .newKnowledgeBaseConfiguration();

        kbconf.setOption(EventProcessingOption.STREAM);

        KieBase kbase = KieServices.Factory.get()
                                   .newKieContainer(kbuilder.getKieModule().getReleaseId())
                                   .newKieBase(kbconf);

        KieSessionConfiguration ksconfig = KnowledgeBaseFactory
                .newKnowledgeSessionConfiguration();
        ksconfig.setOption(ClockTypeOption.get("pseudo"));

        ksession = kbase.newKieSession(ksconfig, null);

        clock = ksession.getSessionClock();
    }

    @After
    public void clean() {
        ksession.dispose();
    }

    @Test
    public void testTimeWindow() throws InterruptedException {
        EntryPoint entryPoint = ksession.getEntryPoint("EventStream");
        final long results[] = new long[] { 1, 2, 3, 3, 3 };
        TestEvent event;

        for (int i = 0; i < 5; i++) {
            event = new TestEvent(null, "time", null);
            entryPoint.insert(event);

            assertEquals(results[i], ksession.getQueryResults("TestTimeWindow")
                                             .iterator().next().get("$eventCount"));
            clock.advanceTime(100, TimeUnit.MILLISECONDS);
        }
    }

    @Test
    public void testLengthWindow() {
        EntryPoint entryPoint = ksession.getEntryPoint("EventStream");
        TestEvent event;

        for (int i = 1; i <= 20; i++) {
            event = new TestEvent(null, "length", null);
            entryPoint.insert(event);

            assertEquals((i < 10 ? i : 10),
                         ((Long) ksession.getQueryResults("TestLengthWindow")
                                         .iterator().next().get("$eventCount")).intValue());
        }
    }

    @Test
    public void testDeclaredTimeWindowInQuery() throws InterruptedException {
        final long results[] = new long[] { 1, 2, 3, 4, 5, 5, 5, 5, 5, 5 };
        EntryPoint entryPoint = ksession.getEntryPoint("EventStream");
        TestEvent event;

        for (int i = 0; i < 10; i++) {
            event = new TestEvent(null, "timeDec", null);
            entryPoint.insert(event);

            assertEquals(results[i],
                         ksession.getQueryResults("TestDeclaredTimeWindow")
                                 .iterator().next().get("$eventCount"));
            clock.advanceTime(10, TimeUnit.MILLISECONDS);
        }
    }

    @Test
    public void testDeclaredLengthWindowInQuery() {
        EntryPoint entryPoint = ksession.getEntryPoint("EventStream");
        TestEvent event;

        for (int i = 1; i <= 10; i++) {
            event = new TestEvent(null, "lengthDec", null);
            entryPoint.insert(event);
            assertEquals((i < 5 ? i : 5),
                         ((Long) ksession
                                 .getQueryResults("TestDeclaredLengthWindow")
                                 .iterator().next().get("$eventCount")).intValue());
        }
    }

    @Test
    public void testDeclaredTimeWindowInRule() throws InterruptedException {
        final long results[] = new long[] { 1, 2, 3, 4, 5, 5, 5, 5, 5, 5 };
        EntryPoint entryPoint = ksession.getEntryPoint("EventStream");
        List<Long> result = new ArrayList<Long>();
        ksession.setGlobal("result", result);
        TestEvent event;

        for (int i = 0; i < 10; i++) {
            event = new TestEvent(null, "timeDec", null);
            entryPoint.insert(event);
            ksession.fireAllRules();
            assertEquals(results[i], result.get(result.size() - 1).longValue());
            clock.advanceTime(10, TimeUnit.MILLISECONDS);
        }
    }

    @Test
    public void testDeclaredLengthWindowInRule() {
        EntryPoint entryPoint = ksession.getEntryPoint("EventStream");
        List<Long> result = new ArrayList<Long>();
        ksession.setGlobal("result", result);
        TestEvent event;

        for (int i = 1; i <= 10; i++) {
            event = new TestEvent(null, "lengthDec", null);
            entryPoint.insert(event);
            ksession.fireAllRules();
            assertEquals((i < 5 ? i : 5), result.get(result.size() - 1)
                                                .longValue());
        }
    }

    public class TestEvent implements Serializable {

        private static final long serialVersionUID = -6985691286327371275L;

        private final Integer id;
        private final String name;
        private Serializable value;

        public TestEvent(Integer id, String name, Serializable value) {
            this.id = id;
            this.name = name;
            this.value = value;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Serializable getValue() {
            return value;
        }

        public void setValue(Serializable value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("TestEvent[id=%s, name=%s, value=%s]", id,
                                 name, value);
        }
    }
}
