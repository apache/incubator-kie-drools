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

import java.io.StringReader;
import java.util.Map;

import org.jbpm.eventmessaging.EventKey;
import org.jbpm.eventmessaging.Payload;
import org.jbpm.task.*;
import org.jbpm.task.event.TaskEventKey;
import org.jbpm.task.event.entity.TaskClaimedEvent;
import org.jbpm.task.event.entity.TaskUserEvent;
import org.jbpm.task.service.Operation;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.responsehandlers.*;

public abstract class TaskServiceEventMessagingBaseUserGroupCallbackAsyncTest extends BaseTestNoUserGroupSetup {

    protected TaskServer server;
    protected AsyncTaskService client;

    protected void tearDown() throws Exception {
        client.disconnect();
        server.stop();
        super.tearDown();
    }    
    
    public void testClaimEvent() throws Exception {      
        Map<String, Object> vars = fillVariables(users, groups);

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], businessAdministrators = [ users['darth'] ] }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();
        
        // A Task with multiple potential owners moves to "Ready" state until someone claims it.
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals( Status.Ready , task1.getTaskData().getStatus() );         
        
        EventKey key = new TaskEventKey(TaskClaimedEvent.class, taskId );           
        BlockingEventResponseHandler handler = new BlockingEventResponseHandler(); 
        client.registerForEvent( key, true, handler );
        Thread.sleep( 3000 );
        
        taskSession.taskOperation( Operation.Claim, taskId, users.get( "darth" ).getId(), null, null, null );          
        handler.waitTillDone( 5000 );
        
        Payload payload = handler.getPayload();
        TaskUserEvent event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );        
    }

}
