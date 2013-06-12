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
import org.kie.api.runtime.rule.SessionEntryPoint;
import org.kie.internal.KnowledgeBaseFactory;

public class WindowTest {

    private KieSession ksession;

    private SessionPseudoClock clock;

    @Before
    public void initialization() {
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();

        kfs.write(KieServices.Factory.get().getResources()
                .newClassPathResource("window_test.drl", WindowTest.class));

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
        SessionEntryPoint entryPoint = ksession.getEntryPoint("EventStream");
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
        SessionEntryPoint entryPoint = ksession.getEntryPoint("EventStream");
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
        SessionEntryPoint entryPoint = ksession.getEntryPoint("EventStream");
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
        SessionEntryPoint entryPoint = ksession.getEntryPoint("EventStream");
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
        SessionEntryPoint entryPoint = ksession.getEntryPoint("EventStream");
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
        SessionEntryPoint entryPoint = ksession.getEntryPoint("EventStream");
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
