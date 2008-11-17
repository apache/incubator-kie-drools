package org.drools.examples.ruleflow;

import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeType;
import org.drools.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.drools.process.instance.impl.demo.UIWorkItemHandler;
import org.drools.runtime.StatefulKnowledgeSession;

public class WorkItemExample {

    public static final void main(String[] args) {
        try {
            KnowledgeBase knowledgeBase = readRule();
            StatefulKnowledgeSession workingMemory = knowledgeBase.newStatefulKnowledgeSession();

            // logging all work items to sysout
            SystemOutWorkItemHandler handler = new SystemOutWorkItemHandler();
            workingMemory.getWorkItemManager().registerWorkItemHandler( "Email",
                                                                        handler );
            workingMemory.getWorkItemManager().registerWorkItemHandler( "Log",
                                                                        handler );

            // using a dialog to show all work items
            UIWorkItemHandler handler2 = new UIWorkItemHandler();
            //workingMemory.getWorkItemManager().registerWorkItemHandler("Email", handler2);
            //workingMemory.getWorkItemManager().registerWorkItemHandler("Log", handler2);
            //handler2.setVisible(true);

            workingMemory.startProcess( "com.sample.ruleflow" );
            workingMemory.fireAllRules();
        } catch ( Throwable t ) {
            t.printStackTrace();
        }
    }

    private static KnowledgeBase readRule() throws Exception {
        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        Reader source = new InputStreamReader( WorkItemExample.class.getResourceAsStream( "/org/drools/examples/ruleflow/workitems.rf" ) );
        builder.addResource( source,
                             KnowledgeType.DRF );

        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages( builder.getKnowledgePackages() );

        return knowledgeBase;
    }

}
