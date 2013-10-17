package org.drools.compiler.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.drools.core.time.SessionPseudoClock;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.internal.io.ResourceFactory;

public class QueryCepTest {
    
    private KieSession ksession;
    
    private SessionPseudoClock clock;
    
    private EntryPoint firstEntryPoint, secondEntryPoint;

    @Before
    public void prepare() {
        
        final KieServices ks = KieServices.Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        KieModuleModel kmodule = ks.newKieModuleModel();

        KieBaseModel baseModel = kmodule.newKieBaseModel("defaultKBase")
                .setDefault(true)
                .setEventProcessingMode(EventProcessingOption.STREAM);
        baseModel.newKieSessionModel("defaultKSession")
                .setDefault(true)
                .setClockType(ClockTypeOption.get("pseudo"));

        kfs.writeKModuleXML(kmodule.toXML());
        kfs.write("src/main/resources/defaultKBase/resource1.drl",
                  ResourceFactory.newClassPathResource("query-cep.drl", this.getClass()));

        assertTrue(ks.newKieBuilder(kfs).buildAll().getResults().getMessages().isEmpty());
        ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();
        
        firstEntryPoint = ksession.getEntryPoint("FirstStream");
        secondEntryPoint = ksession.getEntryPoint("SecondStream"); 
        clock = ksession.getSessionClock();
    }

    @Ignore
    @Test
    public void noResultTest() {
        
        QueryResults results = ksession.getQueryResults("EventsFromStream");
        
        assertEquals(0, results.size());
    }
    
    @Ignore
    @Test
    public void withResultTest() {
        
        secondEntryPoint.insert(new TestEvent("minusOne"));

        clock.advanceTime(5, TimeUnit.SECONDS);

        firstEntryPoint.insert(new TestEvent("zero"));

        secondEntryPoint.insert(new TestEvent("one"));

        clock.advanceTime(10, TimeUnit.SECONDS);

        secondEntryPoint.insert(new TestEvent("two"));

        clock.advanceTime(10, TimeUnit.SECONDS);

        secondEntryPoint.insert(new TestEvent("three"));
        
        QueryResults results = ksession.getQueryResults("ZeroToNineteenSeconds");
        
        assertEquals(2, results.size());
    }
    
    @After
    public void cleanup() {
        
        if (ksession != null) {
            ksession.dispose();
        }
    }
    
    public static class TestEvent {
        
        private final String name;
        
        public TestEvent(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }        
        
        @Override
        public String toString() {
            return "TestEvent[" + name + "]";
        }
    }
}
