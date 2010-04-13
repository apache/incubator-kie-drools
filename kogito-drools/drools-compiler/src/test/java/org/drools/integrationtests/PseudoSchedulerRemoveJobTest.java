package org.drools.integrationtests;


import java.io.Serializable;
import java.io.StringReader;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.io.ResourceFactory;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.runtime.rule.FactHandle;
import org.drools.time.impl.PseudoClockScheduler;

/**
 * @author mproch
 * 
 */
public class PseudoSchedulerRemoveJobTest extends TestCase {

    KnowledgeBase knowledgeBase;

    StatefulKnowledgeSession ksession;

    PseudoClockScheduler pseudoClock;

    public void setUp() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
                .newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newReaderResource(new StringReader(str)),
                ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            throw new RuntimeException(kbuilder.getErrors().toString());
        }
        KnowledgeBaseConfiguration config = KnowledgeBaseFactory
                .newKnowledgeBaseConfiguration();
        config.setOption(EventProcessingOption.STREAM);

        KnowledgeSessionConfiguration sessionConfig = KnowledgeBaseFactory
                .newKnowledgeSessionConfiguration();
        sessionConfig.setOption(ClockTypeOption.get("pseudo"));
        knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase(config);
        knowledgeBase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        ksession = knowledgeBase.newStatefulKnowledgeSession(sessionConfig,
                KnowledgeBaseFactory.newEnvironment());
        pseudoClock = ksession.getSessionClock();
    }

    public void testRule() throws Exception {
        FactHandle h = ksession.insert(new A());
        ksession.retract(h);
    }

    public static class A implements Serializable {
    }

    String str = "import org.drools.integrationtests.PseudoSchedulerRemoveJobTest.A\n" + 
            "declare A\n" + 
            "    @role( event )\n" + 
            "end\n" + 
            "rule A\n" + 
            "when\n" + 
            "   $a : A()\n" + 
            "   not A(this after [1s,10s] $a)\n" + 
            "then\n" + 
            "end";

}

