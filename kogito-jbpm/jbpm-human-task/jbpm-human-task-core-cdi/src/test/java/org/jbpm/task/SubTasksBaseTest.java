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
package org.jbpm.task;

import java.io.StringReader;
import java.util.HashMap;
import org.jbpm.task.impl.factories.TaskFactory;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public abstract class SubTasksBaseTest extends BaseTest{
    
    @Test
    public void noActionStrategy() throws Exception {

  
         // One potential owner, should go straight to state Reserved
        String parentTaskstr = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
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
        
        String child1Taskstr = "(with (new Task()) { priority = 55,  taskData = (with( new TaskData()) { parentId= "+taskParentId+" } ), ";
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
        String parentTaskstr = "(with (new Task()) { subTaskStrategy = SubTasksStrategy.EndAllSubTasksOnParentEnd,  priority = 55, taskData = (with( new TaskData()) { } ), ";
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
        
        String child1Taskstr = "(with (new Task()) { priority = 55,  taskData = (with( new TaskData()) { parentId= "+taskParentId+" } ), ";
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
        
        taskService.complete(child1TaskId, "Darth Vader", null);
        
        childTask1 = taskService.getTaskById(child1TaskId);
        
        assertEquals(Status.Completed, childTask1.getTaskData().getStatus());
        
        parentTask1 = taskService.getTaskById(taskParentId);
        assertEquals(0, taskService.getPendingSubTasksByParent(taskParentId));
        
        assertEquals(Status.Completed, parentTask1.getTaskData().getStatus() );
        
        
    }

//    @Test
//    public void testOnParentAbortAllSubTasksEndStrategy() throws Exception {
//
//        TestWorkItemManager manager = new TestWorkItemManager();
//        ksession.setWorkItemManager(manager);
//        //Create the parent task
//        WorkItemImpl workItem = new WorkItemImpl();
//        workItem.setName("Human Task");
//        workItem.setParameter("TaskName", "TaskNameParent");
//        workItem.setParameter("Comment", "CommentParent");
//        workItem.setParameter("Priority", "10");
//        workItem.setParameter("ActorId", "Darth Vader");
//        //Set the subtask policy
//        workItem.setParameter("SubTaskStrategies", "OnParentAbortAllSubTasksEnd");
//        getHandler().executeWorkItem(workItem, manager);
//
//
//        //Test if the task is succesfully created
//        
//        
//        List<TaskSummary> tasks = getClient().getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
//        assertEquals(1, tasks.size());
//        TaskSummary task = tasks.get(0);
//        assertEquals("TaskNameParent", task.getName());
//        assertEquals(10, task.getPriority());
//        assertEquals("CommentParent", task.getDescription());
//        assertEquals(Status.Reserved, task.getStatus());
//        assertEquals("Darth Vader", task.getActualOwner().getId());
//
//        //Create the child task
//        workItem = new WorkItemImpl();
//        workItem.setName("Human Task");
//        workItem.setParameter("TaskName", "TaskNameChild1");
//        workItem.setParameter("Comment", "CommentChild1");
//        workItem.setParameter("Priority", "10");
//        workItem.setParameter("ActorId", "Darth Vader");
//        workItem.setParameter("ParentId", task.getId());
//        getHandler().executeWorkItem(workItem, manager);
//
//        //Create the child task2
//        workItem = new WorkItemImpl();
//        workItem.setName("Human Task2");
//        workItem.setParameter("TaskName", "TaskNameChild2");
//        workItem.setParameter("Comment", "CommentChild2");
//        workItem.setParameter("Priority", "10");
//        workItem.setParameter("ActorId", "Darth Vader");
//        workItem.setParameter("ParentId", task.getId());
//        getHandler().executeWorkItem(workItem, manager);
//
//        //Start the parent task
//        getClient().start(task.getId(), "Darth Vader");
//
//        //Check if the parent task is InProgress
//        Task parentTask = getClient().getTask(task.getId());
//        assertEquals(Status.InProgress, parentTask.getTaskData().getStatus());
//        assertEquals(users.get("darth"), parentTask.getTaskData().getActualOwner());
//
//        //Get all the subtask created for the parent task based on the potential owner
//        
//        
//        List<TaskSummary> subTasks = getClient().getSubTasksAssignedAsPotentialOwner(parentTask.getId(), "Darth Vader", "en-UK");
//        assertEquals(2, subTasks.size());
//        TaskSummary subTaskSummary1 = subTasks.get(0);
//        TaskSummary subTaskSummary2 = subTasks.get(1);
//        assertNotNull(subTaskSummary1);
//        assertNotNull(subTaskSummary2);
//
//        //Starting the sub task 1
//        getClient().start(subTaskSummary1.getId(), "Darth Vader");
//        
//        //Starting the sub task 2
//        getClient().start(subTaskSummary2.getId(), "Darth Vader");
//        
//        //Check if the child task 1 is InProgress
//        
//        
//        Task subTask1 = getClient().getTask(subTaskSummary1.getId());
//        assertEquals(Status.InProgress, subTask1.getTaskData().getStatus());
//        assertEquals(users.get("darth"), subTask1.getTaskData().getActualOwner());
//
//        //Check if the child task 2 is InProgress
//        
//        
//        Task subTask2 = getClient().getTask(subTaskSummary2.getId());
//        assertEquals(Status.InProgress, subTask2.getTaskData().getStatus());
//        assertEquals(users.get("darth"), subTask2.getTaskData().getActualOwner());
//
//        // Complete the parent task
//        getClient().skip(parentTask.getId(), "Darth Vader");
//        
//        //Check if the child task 1 is Completed
//        
//        
//        subTask1 = getClient().getTask(subTaskSummary1.getId());
//        assertEquals(Status.Completed, subTask1.getTaskData().getStatus());
//        assertEquals(users.get("darth"), subTask1.getTaskData().getActualOwner());
//
//        //Check if the child task 2 is Completed
//        
//        
//        subTask2 = getClient().getTask(subTaskSummary2.getId());
//        assertEquals(Status.Completed, subTask2.getTaskData().getStatus());
//        assertEquals(users.get("darth"), subTask2.getTaskData().getActualOwner());
//
//        assertTrue(manager.waitTillCompleted(MANAGER_COMPLETION_WAIT_TIME));
//    }
}
