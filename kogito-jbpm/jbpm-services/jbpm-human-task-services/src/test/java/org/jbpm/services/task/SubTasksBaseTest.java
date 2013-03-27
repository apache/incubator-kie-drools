/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.services.task;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.HashMap;

import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.services.task.impl.model.TaskImpl;
import org.junit.Test;
import org.kie.internal.task.api.model.Status;
import org.kie.internal.task.api.model.Task;

/**
 *
 */
public abstract class SubTasksBaseTest extends HumanTaskServicesBaseTest{
    
    @Test
    public void noActionStrategy() throws Exception {

  
         // One potential owner, should go straight to state Reserved
        String parentTaskstr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        parentTaskstr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('salaboy')  ], }),";
        parentTaskstr += "names = [ new I18NText( 'en-UK', 'This is my task Parent name')] })";
        // By default the task will contain a SubTask SubTasksStrategy.NoAction

        TaskImpl parentTask = TaskFactory.evalTask(new StringReader(parentTaskstr));
        taskService.addTask(parentTask, new HashMap<String, Object>());

        long taskParentId = parentTask.getId();

        // Task should remain in Created state with no actual owner

        Task parentTask1 = taskService.getTaskById(taskParentId);
        assertEquals(parentTask1.getTaskData().getStatus(), Status.Reserved);
        
        taskService.start(taskParentId, "salaboy");
        
        String child1Taskstr = "(with (new Task()) { priority = 55,  taskData = (with( new TaskData()) { parentId= "+taskParentId+" } ), ";
        child1Taskstr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Darth Vader')  ], }),";
        child1Taskstr += "names = [ new I18NText( 'en-UK', 'This is my task Child 1 name')] })";

        TaskImpl child1Task = TaskFactory.evalTask(new StringReader(child1Taskstr));
        taskService.addTask(child1Task, new HashMap<String, Object>());

        long child1TaskId = child1Task.getId();

        //Test if the task is succesfully created
        
        assertEquals(1, taskService.getPendingSubTasksByParent(taskParentId));
        
        Task childTask1 = taskService.getTaskById(child1TaskId);
        
        assertEquals(taskParentId, childTask1.getTaskData().getParentId());
        
        taskService.start(child1TaskId, "Darth Vader");
        
        taskService.complete(child1TaskId, "Darth Vader", null);
        
        childTask1 = taskService.getTaskById(child1TaskId);
        
        assertEquals(Status.Completed, childTask1.getTaskData().getStatus());
        
        parentTask1 = taskService.getTaskById(taskParentId);
        
        
        assertEquals(Status.InProgress, parentTask1.getTaskData().getStatus() );
        
        assertEquals(0, taskService.getPendingSubTasksByParent(taskParentId));
        
        taskService.complete(taskParentId, "salaboy", null);
        
        parentTask1 = taskService.getTaskById(taskParentId);
        
        assertEquals(Status.Completed, parentTask1.getTaskData().getStatus() );
        
    }
    
    @Test
    public void onSubtaskCompletionAutoCompleteParentStrategy() throws Exception {

  
         // One potential owner, should go straight to state Reserved
        String parentTaskstr = "(with (new Task()) { subTaskStrategy = SubTasksStrategy.EndParentOnAllSubTasksEnd,  priority = 55, taskData = (with( new TaskData()) { } ), ";
        parentTaskstr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('salaboy')  ], }),";
        parentTaskstr += "names = [ new I18NText( 'en-UK', 'This is my task Parent name')] })";
        // By default the task will contain a SubTask SubTasksStrategy.NoAction

        TaskImpl parentTask = TaskFactory.evalTask(new StringReader(parentTaskstr));
        taskService.addTask(parentTask, new HashMap<String, Object>());

        long taskParentId = parentTask.getId();

        // Task should remain in Created state with no actual owner

        Task parentTask1 = taskService.getTaskById(taskParentId);
        assertEquals(parentTask1.getTaskData().getStatus(), Status.Reserved);
        
        taskService.start(taskParentId, "salaboy");
        
        String child1Taskstr = "(with (new Task()) { priority = 55,  taskData = (with( new TaskData()) { parentId= "+taskParentId+" } ), ";
        child1Taskstr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Darth Vader')  ], }),";
        child1Taskstr += "names = [ new I18NText( 'en-UK', 'This is my task Child 1 name')] })";

        TaskImpl child1Task = TaskFactory.evalTask(new StringReader(child1Taskstr));
        taskService.addTask(child1Task, new HashMap<String, Object>());

        long child1TaskId = child1Task.getId();

        //Test if the task is succesfully created
        
        assertEquals(1, taskService.getPendingSubTasksByParent(taskParentId));
        
        Task childTask1 = taskService.getTaskById(child1TaskId);
        
        assertEquals(taskParentId, childTask1.getTaskData().getParentId());
        
        taskService.start(child1TaskId, "Darth Vader");
        
        taskService.complete(child1TaskId, "Darth Vader", null);
        
        childTask1 = taskService.getTaskById(child1TaskId);
        
        assertEquals(Status.Completed, childTask1.getTaskData().getStatus());
        
        parentTask1 = taskService.getTaskById(taskParentId);
        assertEquals(0, taskService.getPendingSubTasksByParent(taskParentId));
        
        assertEquals(Status.Completed, parentTask1.getTaskData().getStatus() );
        
        
    }

    @Test
    public void onParentAbortCompleteAllSubTasksStrategy() throws Exception {

  
         // One potential owner, should go straight to state Reserved 
        // Notice skippable in task data
        String parentTaskstr = "(with (new Task()) { subTaskStrategy = SubTasksStrategy.SkipAllSubTasksOnParentSkip ,  priority = 55, "
                                + "taskData = (with( new TaskData()) { skipable = true } ), ";
        parentTaskstr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('salaboy')  ], }),";
        parentTaskstr += "names = [ new I18NText( 'en-UK', 'This is my task Parent name')] })";
        // By default the task will contain a SubTask SubTasksStrategy.NoAction

        Task parentTask = TaskFactory.evalTask(new StringReader(parentTaskstr));
        taskService.addTask(parentTask, new HashMap<String, Object>());

        long taskParentId = parentTask.getId();

        // Task should remain in Created state with no actual owner

        Task parentTask1 = taskService.getTaskById(taskParentId);
        assertEquals(parentTask1.getTaskData().getStatus(), Status.Reserved);
        
        taskService.start(taskParentId, "salaboy");
        
        String child1Taskstr = "(with (new Task()) { priority = 55,  taskData = (with( new TaskData()) { skipable = true, parentId= "+taskParentId+" } ), ";
        child1Taskstr += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Darth Vader')  ], }),";
        child1Taskstr += "names = [ new I18NText( 'en-UK', 'This is my task Child 1 name')] })";

        Task child1Task = TaskFactory.evalTask(new StringReader(child1Taskstr));
        taskService.addTask(child1Task, new HashMap<String, Object>());

        long child1TaskId = child1Task.getId();

        //Test if the task is succesfully created
        
        assertEquals(1, taskService.getPendingSubTasksByParent(taskParentId));
        
        Task childTask1 = taskService.getTaskById(child1TaskId);
        
        assertEquals(taskParentId, childTask1.getTaskData().getParentId());
        
        taskService.start(child1TaskId, "Darth Vader");
        
        
        
        taskService.skip(taskParentId, "salaboy");
        
        parentTask1 = taskService.getTaskById(taskParentId);
        assertEquals(0, taskService.getPendingSubTasksByParent(taskParentId));
        
        
        
        assertEquals(Status.Obsolete, parentTask1.getTaskData().getStatus() );
        
        childTask1 = taskService.getTaskById(child1TaskId);
        
        assertEquals(Status.Obsolete, childTask1.getTaskData().getStatus());
        
    }

}
