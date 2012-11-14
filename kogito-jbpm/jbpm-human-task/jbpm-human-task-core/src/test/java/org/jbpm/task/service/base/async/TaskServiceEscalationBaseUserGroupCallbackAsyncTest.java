/**
 * Copyright 2010 JBoss Inc
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

package org.jbpm.task.service.base.async;

import org.jbpm.task.MvelFilePath;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.SystemEventListenerFactory;
import org.jbpm.task.*;
import org.jbpm.task.service.MockEscalatedDeadlineHandler;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.responsehandlers.BlockingAddTaskResponseHandler;

public abstract class TaskServiceEscalationBaseUserGroupCallbackAsyncTest extends BaseTestNoUserGroupSetup {

    protected TaskServer server;
    protected AsyncTaskService client;


    protected void tearDown() throws Exception {
        client.disconnect();
        server.stop();
        super.tearDown();
    }

    
    public void testUnescalatedDeadlines() throws Exception {
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put( "users", users );
        vars.put( "groups", groups );

        MockEscalatedDeadlineHandler handler = new MockEscalatedDeadlineHandler();
        taskService.setEscalatedDeadlineHandler( handler );  
        
        //Reader reader;
        Reader reader = new InputStreamReader( TaskServiceEscalationBaseUserGroupCallbackAsyncTest.class.getResourceAsStream( MvelFilePath.UnescalatedDeadlines ) );
        List<Task> tasks = (List<Task>) eval( reader,
                                              vars );
        long now = ((Date)vars.get( "now" )).getTime();
        
        for ( Task task : tasks ) {  
            BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();  
            if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
                List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
                businessAdmins.add(new User("Administrator"));
                businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
                task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
            }
            client.addTask( task, null, addTaskResponseHandler ); 
            addTaskResponseHandler.waitTillDone( 1000 );
        }

        testDeadlines(now, handler); 
    }
    
    public void testUnescalatedDeadlinesOnStartup() throws Exception {
        Map vars = new HashMap();
        vars.put( "users", users );
        vars.put( "groups", groups );

        //Reader reader;
        Reader reader = new InputStreamReader( TaskServiceEscalationBaseUserGroupCallbackAsyncTest.class.getResourceAsStream( MvelFilePath.UnescalatedDeadlines ) );
        List<Task> tasks = (List<Task>) eval( reader,
                                              vars );
        long now = ((Date)vars.get( "now" )).getTime();
        
        for ( Task task : tasks ) {
            BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();  
            if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
                List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
                businessAdmins.add(new User("Administrator"));
                businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
                task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
            }
            client.addTask( task, null, addTaskResponseHandler ); 
            addTaskResponseHandler.waitTillDone( 3000 );
        }
        
        // now create a new service, to see if it initiates from the DB correctly
        MockEscalatedDeadlineHandler handler = new MockEscalatedDeadlineHandler();
        new TaskService(emf, SystemEventListenerFactory.getSystemEventListener(), handler);      
        
        testDeadlines(now, handler); 

    }
    
}
