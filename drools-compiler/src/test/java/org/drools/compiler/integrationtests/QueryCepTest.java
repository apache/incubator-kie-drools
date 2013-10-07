package org.drools.compiler.integrationtests;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Message.Level;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.SessionEntryPoint;
import org.kie.internal.KnowledgeBaseFactory;

public class QueryCepTest {

    @Test
    public void AfterOperatorTest() {
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();

        kfs.write(KieServices.Factory.get().getResources()
                .newClassPathResource("query_cep.drl", QueryCepTest.class));

        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);

        kbuilder.buildAll();
        
        List<Message> res = kbuilder.getResults().getMessages(Level.ERROR);
        
        assertEquals(res.toString(), 0, res.size());
        
        KieBase kbase = KieServices.Factory.get()
                .newKieContainer(kbuilder.getKieModule().getReleaseId())
                .getKieBase();
        
        KieSessionConfiguration ksconfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconfig.setOption(ClockTypeOption.get("pseudo"));
        
        KieSession ksession = kbase.newKieSession(ksconfig, null);
        
        SessionPseudoClock clock = ksession.getSessionClock();
        
        SessionEntryPoint ePoint = ksession.getEntryPoint("EStream");
        SessionEntryPoint entryPoint = ksession.getEntryPoint("EventStream");
        
        ePoint.insert(new TestEvent(0, "zero", null));

        entryPoint.insert(new TestEvent(1, "one", null));
        
        clock.advanceTime( 10, TimeUnit.SECONDS );
        
        entryPoint.insert(new TestEvent(2, "two", null));
        
        clock.advanceTime( 10, TimeUnit.SECONDS );
        
        entryPoint.insert(new TestEvent(3, "three", null));
        
        QueryResults results = ksession.getQueryResults("EventsBeforeNineSeconds");
        
        assertEquals(1, results.size());
        
        /*
        results = ksession.getQueryResults("EventsBeforeNineteenSeconds");
        
        assertEquals(2, results.size());
        
        results = ksession.getQueryResults("EventsBeforeHundredSeconds");
        
        assertEquals(3, results.size());
        */
        
        ksession.dispose();
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
            return String.format("TestEvent[id=%s, name=%s, value=%s]", id, name, value);
        }
    }
}
