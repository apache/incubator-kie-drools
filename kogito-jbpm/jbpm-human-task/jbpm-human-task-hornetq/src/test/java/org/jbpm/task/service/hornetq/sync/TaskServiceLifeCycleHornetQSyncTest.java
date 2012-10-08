/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jbpm.task.service.hornetq.sync;

import java.io.StringReader;
import java.util.Map;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.service.SyncTaskServiceWrapper;
import org.jbpm.task.service.base.sync.TaskServiceLifeCycleBaseSyncTest;
import org.jbpm.task.service.hornetq.AsyncHornetQTaskClient;
import org.jbpm.task.service.hornetq.HornetQTaskServer;

public class TaskServiceLifeCycleHornetQSyncTest extends TaskServiceLifeCycleBaseSyncTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        server = new HornetQTaskServer(taskService, 5445);
        System.out.println("Waiting for the HornetQTask Server to come up");
        try {
            startTaskServerThread(server, false);
        } catch (Exception e) {
            startTaskServerThread(server, true);
        }

        client = new SyncTaskServiceWrapper(new AsyncHornetQTaskClient());
        client.connect("127.0.0.1", 5445);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        client.disconnect();
        server.stop();
    }

    public void testStartTaskThatDoesntExists() {
        Map<String, Object> vars = fillVariables();

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
         str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) eval(new StringReader(str), vars);
        client.addTask(task, null);

        long taskId = task.getId();

        // Task should remain in Reserved state because it have an actual owner

        Task task1 = client.getTask(taskId);
        assertEquals(Status.Reserved, task1.getTaskData().getStatus());
        assertNotNull(task1.getTaskData().getActualOwner());
        try{
            client.start(3, "");
        }catch(Exception e){
              assertEquals("Command OperationRequest faild due to No Task with ID 3 was found!. Please contact task server administrator.", e.getMessage());
        }
        
        task1 = client.getTask(taskId);
        assertEquals(Status.Reserved, task1.getTaskData().getStatus());
        
        client.start(taskId, users.get( "bobba" ).getId() );
        
        
        task1 = client.getTask(taskId);
        
        assertEquals(Status.InProgress, task1.getTaskData().getStatus() );
        
    }
}
