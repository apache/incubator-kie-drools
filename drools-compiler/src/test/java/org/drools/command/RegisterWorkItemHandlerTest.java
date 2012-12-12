package org.drools.command;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.drools.process.instance.WorkItem;
import org.drools.process.instance.impl.DefaultWorkItemManager;
import org.drools.process.instance.impl.WorkItemImpl;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.command.CommandFactory;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;
import org.kie.runtime.StatelessKnowledgeSession;
import org.kie.runtime.process.WorkItemHandler;
import org.kie.runtime.process.WorkItemManager;

public class RegisterWorkItemHandlerTest {
    
    @Test
    public void testRegisterWorkItemHandlerWithStatelessSession() {
        String str = 
                "package org.kie.workitem.test \n" +
                "import " + DefaultWorkItemManager.class.getCanonicalName() + "\n" +
                "import " + WorkItem.class.getCanonicalName() + "\n" +
                "import " + WorkItemImpl.class.getCanonicalName() + "\n" + 
                "rule r1 when \n" + 
                "then \n" +
                "  WorkItem wi = new WorkItemImpl(); \n" +
                "  wi.setName( \"wihandler\" ); \n" +
                "  DefaultWorkItemManager wim = ( DefaultWorkItemManager ) kcontext.getKieRuntime().getWorkItemManager(); \n" +
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
            
            public void executeWorkItem(org.kie.runtime.process.WorkItem workItem,
                                        WorkItemManager manager) {
                answer[0] = true;
            }
            
            public void abortWorkItem(org.kie.runtime.process.WorkItem workItem,
                                      WorkItemManager manager) {
                // TODO Auto-generated method stub
                
            }
        },  "wihandler" ) );
        
        assertTrue( answer[0] );
    }
}
