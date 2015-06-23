/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.command;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.drools.core.process.instance.WorkItem;
import org.drools.core.process.instance.impl.DefaultWorkItemManager;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.ResourceType;
import org.kie.internal.runtime.StatelessKnowledgeSession;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

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
            
            public void executeWorkItem(org.kie.api.runtime.process.WorkItem workItem,
                                        WorkItemManager manager) {
                answer[0] = true;
            }
            
            public void abortWorkItem(org.kie.api.runtime.process.WorkItem workItem,
                                      WorkItemManager manager) {
                // TODO Auto-generated method stub
                
            }
        },  "wihandler" ) );
        
        assertTrue( answer[0] );
    }
}
