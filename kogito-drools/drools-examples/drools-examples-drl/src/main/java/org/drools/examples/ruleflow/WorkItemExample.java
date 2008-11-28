package org.drools.examples.ruleflow;

import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeType;
import org.drools.io.ResourceFactory;
import org.drools.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.drools.process.instance.impl.demo.UIWorkItemHandler;
import org.drools.runtime.StatefulKnowledgeSession;

public class WorkItemExample {

    public static final void main(String[] args) {
        try {
            KnowledgeBase knowledgeBase = readRule();
            StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession();

            // logging all work items to sysout
            SystemOutWorkItemHandler handler = new SystemOutWorkItemHandler();
            ksession.getWorkItemManager().registerWorkItemHandler( "Email",
                                                                   handler );
            ksession.getWorkItemManager().registerWorkItemHandler( "Log",
                                                                   handler );

            // using a dialog to show all work items
            UIWorkItemHandler handler2 = new UIWorkItemHandler();
            //workingMemory.getWorkItemManager().registerWorkItemHandler("Email", handler2);
            //workingMemory.getWorkItemManager().registerWorkItemHandler("Log", handler2);
            //handler2.setVisible(true);

            ksession.startProcess( "com.sample.ruleflow" );
            ksession.fireAllRules();
        } catch ( Throwable t ) {
            t.printStackTrace();
        }
    }

    private static KnowledgeBase readRule() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();        
        kbuilder.add( ResourceFactory.newClassPathResource( "workitems.rf", WorkItemExample.class ),
                              KnowledgeType.DRF );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        return kbase;
    }

}
