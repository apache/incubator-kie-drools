package org.drools.integrationtests;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.core.util.FileManager;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.DefaultAgendaEventListener;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Before;
import org.junit.Test;

/**
 * BZ-986479
 */
public class TimerAndTemporalOperatorTest {
    FileManager fileManager;
    KnowledgeBase kbase;

    @Before
    public void setUp() throws Exception {
        fileManager = new FileManager();
        fileManager.setUp();

        fileManager.write("rule1.drl", createRule(" timer(int:0 5s)\n"));

    }

    @Test
    public void testIncrementalKbaseChangesWithTemporalRules() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add(ResourceFactory.newFileResource(new File(fileManager.getRootDirectory(), "rule1.drl")),
                     ResourceType.DRL);

        KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption(EventProcessingOption.STREAM);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(config);
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        CountingEventListener listener = new CountingEventListener();
        ksession.addEventListener(listener);

        List<String> results = new ArrayList<String>();
        ksession.setGlobal("result", results);

        AT alarm;
        for (int ii = 0; ii < 2; ii++) {
            alarm = new AT();
            alarm.setTiempo(new Date());
            ksession.insert(alarm);

        }

        Now n;
        try {
            for (int i = 0; i < 20; i++) {
                System.out.println("--------------------------cycle" + i);

                n = new Now();
                n.setTiempo(new Date());
                ksession.insert(n);
                ksession.fireAllRules();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    // doesn't matter
                }
                listener.printCount();
                System.out.println("--------------------------count " + results.size());
            }
        } finally {
            ksession.dispose();
        }
    }

    public static class AT {
        static Date to;

        public static Date getTiempo() {
            return to;
        }

        public Date tiempo() {
            return to;
        }

        public void setTiempo(Date tiempo) {
            AT.to = tiempo;
        }
    }

    public static class Now {
        static Date to;

        public static Date getTiempo() {
            return to;
        }

        public Date tiempo() {
            return to;
        }

        public void setTiempo(Date tiempo) {
            AT.to = tiempo;
        }
    }

    private static class CountingEventListener extends DefaultAgendaEventListener {
        private int count = 0;

        @Override
        public void afterActivationFired(AfterActivationFiredEvent event) {
            count++;
        }

        public void printCount() {
            System.out.println("--------------------------count " + count);
        }
    }

    public String createRule(String rhsMessage) {
        StringBuilder sb = new StringBuilder();

        sb.append("package org.jboss.qa.brms.bre.regression\n");
        sb.append("import java.util.List;\n");
        sb.append("import org.drools.integrationtests.TimerAndTemporalOperatorTest.AT;\n");
        sb.append("import org.drools.integrationtests.TimerAndTemporalOperatorTest.Now;\n");

        sb.append("global List result\n");

        sb.append("declare AT\n");
        sb.append("  @role(event)\n");
        sb.append("  @expires(10000s)\n");
        sb.append("end\n");

        sb.append("declare Now\n");
        sb.append("  @role(event)\n");
        sb.append("  @expires(1s)\n");
        sb.append("end\n");

        sb.append("rule '12'\n");
        sb.append(rhsMessage);

        sb.append("  when\n");
        sb.append("    t: Now() \n");
        sb.append("    c: AT(this before[10s] t) \n");
        sb.append("  then\n");
        sb.append("result.add(\"<<<<<  < < <\"+c.getTiempo());\n");
        sb.append("end\n");

        return sb.toString();
    }

}
