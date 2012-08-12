///*
// * Copyright 2012 JBoss by Red Hat.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package org.jbpm.task;
//
//import java.util.List;
//import org.drools.process.instance.impl.WorkItemImpl;
//import org.jbpm.task.query.TaskSummary;
//import org.junit.After;
//import org.junit.AfterClass;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import static org.junit.Assert.*;
//
///**
// *
// */
//public class TaskServiceSubTasksBaseTest {
//    
//    public TaskServiceSubTasksBaseTest() {
//    }
//    
//    @BeforeClass
//    public static void setUpClass() {
//    }
//    
//    @AfterClass
//    public static void tearDownClass() {
//    }
//    
//    @Before
//    public void setUp() {
//    }
//    
//    @After
//    public void tearDown() {
//    }
//    
//    @Test
//    public void testOnAllSubTasksEndParentEndStrategy() throws Exception {
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
//        workItem.setParameter("SubTaskStrategies", "OnAllSubTasksEndParentEnd");
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
//   
//        //Start the parent task
//        getClient().start(task.getId(), "Darth Vader");
//        
//        //Check if the parent task is InProgress
//        
//        
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
//        // Complete the child task 1
//        getClient().complete(subTask1.getId(), "Darth Vader", null);
//        
//        // Complete the child task 2
//        getClient().complete(subTask2.getId(), "Darth Vader", null);
//       
//        //Check if the child task 1 is Completed
//
//       
//        
//        subTask1 = getClient().getTask(subTask1.getId());
//        assertEquals(Status.Completed, subTask1.getTaskData().getStatus());
//        assertEquals(users.get("darth"), subTask1.getTaskData().getActualOwner());
//
//        //Check if the child task 2 is Completed
//        subTask2 = getClient().getTask(subTask2.getId());
//        assertEquals(Status.Completed, subTask2.getTaskData().getStatus());
//        assertEquals(users.get("darth"), subTask2.getTaskData().getActualOwner());
//
//        // Check is the parent task is Complete
//        parentTask = getClient().getTask(parentTask.getId());
//        assertEquals(Status.Completed, parentTask.getTaskData().getStatus());
//        assertEquals(users.get("darth"), parentTask.getTaskData().getActualOwner());
//
//        assertTrue(manager.waitTillCompleted(MANAGER_COMPLETION_WAIT_TIME));
//    }
//
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
//}
