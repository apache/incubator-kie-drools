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
package org.jbpm.services.task.audit.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;

import org.jbpm.services.task.HumanTaskServicesBaseTest;
import org.jbpm.services.task.audit.DeleteAuditEventsCommand;
import org.jbpm.services.task.audit.GetAuditEventsCommand;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.services.task.impl.model.BAMTaskSummaryImpl;
import org.jbpm.services.task.impl.model.command.DeleteBAMTaskSummariesCommand;
import org.jbpm.services.task.impl.model.command.GetBAMTaskSummariesCommand;
import org.junit.Test;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.model.TaskEvent;

public abstract class LifeCycleBaseTest extends HumanTaskServicesBaseTest {

    @Test
    public void testComplete() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Knights Templer' )],businessAdministrators = [ new User('Administrator') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        taskService.claim(taskId, "Darth Vader");    
        
        taskService.release(taskId, "Darth Vader");
        
        taskService.claim(taskId, "Darth Vader");    
        
        // Go straight from Ready to Inprogress
        taskService.start(taskId, "Darth Vader");

        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        // Check is Complete
        taskService.complete(taskId, "Darth Vader", null);

        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.Completed, task2.getTaskData().getStatus());
        assertEquals("Darth Vader", task2.getTaskData().getActualOwner().getId());

        List<TaskEvent> allTaskEvents = taskService.execute(new GetAuditEventsCommand(taskId));
        assertEquals(6, allTaskEvents.size());
     
        // test DeleteAuditEventsCommand        
        int numFirstTaskEvents = allTaskEvents.size();
        task = (Task) TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        long secondTaskId = task.getId();
        taskService.claim(secondTaskId, "Darth Vader");    
        taskService.start(secondTaskId, "Darth Vader");
        taskService.complete(secondTaskId, "Darth Vader", null);
       
        allTaskEvents = taskService.execute(new GetAuditEventsCommand());
        int numTaskEvents = allTaskEvents.size();
        assertTrue("Expected more than " + numFirstTaskEvents + " events: "+ numTaskEvents, numTaskEvents > numFirstTaskEvents);
        
        taskService.execute(new DeleteAuditEventsCommand(taskId));
        allTaskEvents = taskService.execute(new GetAuditEventsCommand());
        assertEquals(numTaskEvents - numFirstTaskEvents, allTaskEvents.size());
        
        taskService.execute(new DeleteAuditEventsCommand());
        allTaskEvents = taskService.execute(new GetAuditEventsCommand());
        assertEquals(0, allTaskEvents.size());
        
        // test get/delete BAM Task summaries commands
        List<BAMTaskSummaryImpl> bamTaskList = taskService.execute(new GetBAMTaskSummariesCommand());
        assertEquals( "BAM Task Summary list size: ", 2, bamTaskList.size());
        
        taskService.execute(new DeleteBAMTaskSummariesCommand(taskId));
        bamTaskList = taskService.execute(new GetBAMTaskSummariesCommand());
        assertEquals( "BAM Task Summary list size after delete (task id: " + taskId + ") : ", 1, bamTaskList.size());
        
        bamTaskList = taskService.execute(new GetBAMTaskSummariesCommand(secondTaskId));
        assertEquals( "BAM Task Summary list size after delete (task id: " + taskId + ") : ", 1, bamTaskList.size());
        
        taskService.execute(new DeleteBAMTaskSummariesCommand());
        bamTaskList = taskService.execute(new GetBAMTaskSummariesCommand());
        assertEquals( "BAM Task Summary list size after delete (task id: " + taskId + ") : ", 0, bamTaskList.size());
    }

   
}
