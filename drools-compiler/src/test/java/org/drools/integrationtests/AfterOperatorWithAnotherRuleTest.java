package org.drools.integrationtests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.StockTick;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.io.ResourceFactory;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.time.impl.PseudoClockScheduler;
import org.junit.After;
import org.junit.Test;

public class AfterOperatorWithAnotherRuleTest {
    
    private StatefulKnowledgeSession ksession = null;
    
    @Test
    public void testAfterOperatorWithAnotherRule() throws Exception {

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("test_CEP_AfterOperatorWithAnotherRule.drl", getClass()), ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            if (kbuilder.getErrors().size() > 0) {
                for (KnowledgeBuilderError kerror : kbuilder.getErrors()) {
                    System.err.println(kerror);
                }
            }
        }
        KnowledgeBaseConfiguration config = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        config.setOption(EventProcessingOption.STREAM);
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(config);
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        KnowledgeSessionConfiguration conf = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        conf.setOption(ClockTypeOption.get("pseudo"));
        ksession = kbase.newStatefulKnowledgeSession(conf, null);

        final PseudoClockScheduler clock = (PseudoClockScheduler) ksession.getSessionClock();

        final List<StockTick> resultsAfter = new ArrayList<StockTick>();
        ksession.setGlobal("resultsAfter", resultsAfter);

        // inserting new StockTick with duration 30 at time 0 => rule
        // after[60,80] should fire when ACME starts at 90-110
        ksession.insert(new StockTick(1, "DROO", 0, 0, 30));

        clock.advanceTime(100, TimeUnit.MILLISECONDS);

        ksession.insert(new StockTick(2, "ACME", 0, 0, 20));

        ksession.fireAllRules();

        assertEquals(1, resultsAfter.size());   
    }
    
    @After
    public void disposeSession(){
        if (ksession != null){
            ksession.dispose(); 
        }      
    }
}
