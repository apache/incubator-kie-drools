package org.drools.examples.ruleflow;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

public class WorkItemExample {

    public static final void main(String[] args) {
        try {
            KnowledgeBase knowledgeBase = readRule();
            StatefulKnowledgeSession ksession = knowledgeBase.newStatefulKnowledgeSession();

            // logging all work items to System.out
            WorkItemHandler handler = new WorkItemHandler() {
				public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
					System.out.println("Executing work item " + workItem);
			        manager.completeWorkItem(workItem.getId(), null);
				}
            	
				public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
					// Do nothing
				}
            };
            ksession.getWorkItemManager().registerWorkItemHandler( "Email", handler );
            ksession.getWorkItemManager().registerWorkItemHandler( "Log", handler );

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
