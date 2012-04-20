package org.drools.command;

import static org.junit.Assert.*;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.impl.DefaultWorkItemManager;
import org.drools.process.instance.impl.WorkItemImpl;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.junit.Test;

public class RegisterWorkItemHandlerTest {
    
    @Test
    public void testRegisterWorkItemHandlerWithStatelessSession() {
        String str = 
                "package org.drools.workitem.test \n" +
                "import " + DefaultWorkItemManager.class.getCanonicalName() + "\n" +
                "import " + WorkItem.class.getCanonicalName() + "\n" +
                "import " + WorkItemImpl.class.getCanonicalName() + "\n" + 
                "rule r1 when \n" + 
                "then \n" +
                "  WorkItem wi = new WorkItemImpl(); \n" +
                "  wi.setName( \"wihandler\" ); \n" +
                "  DefaultWorkItemManager wim = ( DefaultWorkItemManager ) kcontext.getKnowledgeRuntime().getWorkItemManager(); \n" +
                "  wim.internalExecuteWorkItem(wi); \n" +
                "end \n";
     
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(  ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        
        final boolean[] answer = new boolean[] { false };
        StatelessKnowledgeSession ks = kbase.newStatelessKnowledgeSession();
        ks.execute( CommandFactory.newRegisterWorkItemHandlerCommand( new WorkItemHandler() {
            
            public void executeWorkItem(org.drools.runtime.process.WorkItem workItem,
                                        WorkItemManager manager) {
                answer[0] = true;
            }
            
            public void abortWorkItem(org.drools.runtime.process.WorkItem workItem,
                                      WorkItemManager manager) {
                // TODO Auto-generated method stub
                
            }
        },  "wihandler" ) );
        
        assertTrue( answer[0] );
    }
}
