/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.workitem.jabber;



import org.drools.core.process.instance.impl.DefaultWorkItemManager;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.process.WorkItemManager;

public class JabberWorkItemHandlerTest extends AbstractBaseTest {
    
    @Test
    @Ignore
    public void TODOtestSendJabberMessage() throws Exception {
        JabberWorkItemHandler handler = new JabberWorkItemHandler();
        WorkItemImpl workItem = new WorkItemImpl();
        //The contact in To must be added as a contact in the chat of gtalk
        workItem.setParameter( "To", "drools.demo@gmail.com" );
        workItem.setParameter( "Text", "Hello from Ruleflow WorkItem" );
        //workItem.setParameter( "Server", "talk.google.com" );
        //workItem.setParameter( "Port", "5222" );
        workItem.setParameter( "Service", "gmail.com" );
        workItem.setParameter( "User", "drools.demo" );
        workItem.setParameter( "Password", "pa$$word" );
        
        WorkItemManager manager = new DefaultWorkItemManager(null);
        handler.executeWorkItem( workItem, manager );

        //In a real case i must register the WorkItemHandler:
        // workingMemory.getWorkItemManager()
        //.registerWorkItemHandler("Notification", new NotificationWorkItemHandler());

        
    }    
}
