package org.jbpm.integrationtests;

import java.util.ArrayList;
import java.util.List;

import org.kie.KnowledgeBase;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.io.ResourceFactory;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;
import org.jbpm.JbpmTestCase;

public class ExecutionFlowControlTest extends JbpmTestCase {

    public void testRuleFlowUpgrade() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        // Set the system property so that automatic conversion can happen
        System.setProperty( "drools.ruleflow.port", "true" );

        kbuilder.add( ResourceFactory.newClassPathResource("ruleflow.drl", ExecutionFlowControlTest.class), ResourceType.DRL);
        kbuilder.add( ResourceFactory.newClassPathResource("ruleflow40.rfm", ExecutionFlowControlTest.class), ResourceType.DRF);
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        final List list = new ArrayList();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        assertEquals(0, list.size());
        final ProcessInstance processInstance = ksession.startProcess("0");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        ksession.fireAllRules();
        assertEquals( 4,
                      list.size() );
        assertEquals( "Rule1",
                      list.get( 0 ) );
        assertEquals( "Rule3",
                      list.get( 1 ) );
        assertEquals( "Rule2",
                      list.get( 2 ) );
        assertEquals( "Rule4",
                      list.get( 3 ) );
        assertEquals( ProcessInstance.STATE_COMPLETED,
                      processInstance.getState() );
        // Reset the system property so that automatic conversion should not happen
        System.setProperty("drools.ruleflow.port", "false");
    }

}
