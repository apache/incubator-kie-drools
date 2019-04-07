/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.services.task;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Fail;
import org.jbpm.services.task.events.DefaultTaskEventListener;
import org.jbpm.services.task.exception.PermissionDeniedException;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.services.task.impl.model.xml.JaxbContent;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.junit.Test;
import org.kie.api.task.TaskEvent;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Group;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.EventService;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.AccessType;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.model.FaultData;
import org.kie.internal.task.api.model.InternalComment;
import org.kie.internal.task.api.model.InternalI18NText;
import org.kie.internal.task.api.model.InternalOrganizationalEntity;
import org.kie.internal.task.api.model.InternalPeopleAssignments;
import org.kie.internal.task.api.model.InternalTask;
import org.kie.internal.task.api.model.InternalTaskData;

public abstract class LifeCycleBaseTest extends HumanTaskServicesBaseTest {

    @Test
    /*
    * Related to BZ-1105868 
    */
    public void testWithNoTaskAndEmptyLists(){
      
      List<TaskSummary> tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("nouser", new ArrayList<String>());
      assertTrue(tasksAssignedAsPotentialOwner.isEmpty());
      
      List<TaskSummary> tasksAssignedAsPotentialOwner2 = taskService.getTasksAssignedAsPotentialOwner("nouser", (List<String>)null);
      assertTrue(tasksAssignedAsPotentialOwner2.isEmpty());
      
      List<TaskSummary> tasksAssignedAsPotentialOwner3 = taskService.getTasksAssignedAsPotentialOwner("", (List<String>)null);
      assertTrue(tasksAssignedAsPotentialOwner3.isEmpty());
      
      List<TaskSummary> tasksAssignedAsPotentialOwner4 = taskService.getTasksAssignedAsPotentialOwner(null,(List<String>) null);
      assertTrue(tasksAssignedAsPotentialOwner4.isEmpty());
      
      List<TaskSummary> tasksAssignedAsPotentialOwner5 = taskService.getTasksAssignedAsPotentialOwner("salaboy", (List<String>)null);
      assertTrue(tasksAssignedAsPotentialOwner5.isEmpty());
      
      List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwnerByStatusByGroup("Bobba Fet", null, null);
      assertTrue(tasks.isEmpty());
      
      List<TaskSummary> tasks2 = taskService.getTasksAssignedAsPotentialOwnerByStatusByGroup("Bobba Fet", new ArrayList<String>(), null);
      assertTrue(tasks2.isEmpty());
      
      List<TaskSummary> tasks3 = taskService.getTasksAssignedAsPotentialOwnerByStatusByGroup("Bobba Fet", new ArrayList<String>(), new ArrayList<Status>());
      assertTrue(tasks3.isEmpty());
      
      List<TaskSummary> tasks4 = taskService.getTasksAssignedAsPotentialOwnerByStatusByGroup("admin", new ArrayList<String>(), new ArrayList<Status>());
      assertTrue(tasks4.isEmpty());
              
      
    }
  
    @Test
    public void testNewTaskWithNoPotentialOwners() {

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [ new User('Administrator') ],}),";
        str += "name =  'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Task should remain in Created state with no actual owner

        Task task1 = taskService.getTaskById(taskId);
        assertEquals(task1.getTaskData().getStatus(), Status.Created);
        assertNull(task1.getTaskData().getActualOwner());
    }

    @Test
    public void testNewTaskWithSinglePotentialOwner() {
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')  ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));

        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        // Task should be assigned to the single potential owner and state set to Reserved
        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task1.getTaskData().getStatus());
        String potOwner = "Bobba Fet"; 
        assertEquals(potOwner, task1.getTaskData().getActualOwner().getId());
        
        taskService.getTasksAssignedAsPotentialOwner(potOwner, "en-UK");
    }
    
    

    @Test
    public void testNewTaskWithContent() {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";

        ContentData data = ContentMarshallerHelper.marshal(null, "content", null);

        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, data);

        long taskId = task.getId();

        // Task should be assigned to the single potential owner and state set to Reserved


        Task task1 = taskService.getTaskById(taskId);
        assertEquals(AccessType.Inline, ((InternalTaskData) task1.getTaskData()).getDocumentAccessType());
        assertEquals("java.lang.String", task1.getTaskData().getDocumentType());
        long contentId = task1.getTaskData().getDocumentContentId();
        assertTrue(contentId != -1);

        Content content = taskService.getContentById(contentId);
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        assertEquals("content", unmarshalledObject.toString());
        xmlRoundTripContent(content);
    }
    
    @Test
    public void testNewTaskWithMapContent() {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ],businessAdministrators = [ new User('Administrator') ], }),";                        
        str += "name =  'This is my task name' })";
            
        Map<String, Object> variablesMap = new HashMap<String, Object>();
        variablesMap.put("key1", "value1");
        variablesMap.put("key2", null);
        variablesMap.put("key3", "value3");
        ContentData data = ContentMarshallerHelper.marshal(null, variablesMap, null);
        
        Task task = TaskFactory.evalTask( new StringReader( str ));
        taskService.addTask( task, data );
        
        long taskId = task.getId();
        
        // Task should be assigned to the single potential owner and state set to Reserved
        Task task1 = taskService.getTaskById( taskId );
        assertEquals( AccessType.Inline, ((InternalTaskData) task1.getTaskData()).getDocumentAccessType() );
        assertEquals( "java.util.HashMap", task1.getTaskData().getDocumentType() );
        long contentId = task1.getTaskData().getDocumentContentId();
        assertTrue( contentId != -1 ); 
       
        // content
        Content content = taskService.getContentById(contentId);
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        if(!(unmarshalledObject instanceof Map)){
            fail("The variables should be a Map");
        }
        Map<String, Object> unmarshalledvars = (Map<String, Object>)unmarshalledObject;
        JaxbContent jaxbContent = xmlRoundTripContent(content);
        assertNotNull( "Jaxb Content map not filled", jaxbContent.getContentMap());
        
        assertEquals("value1",unmarshalledvars.get("key1") );
        assertNull(unmarshalledvars.get("key2") );
        assertEquals("value3",unmarshalledvars.get("key3") );
    }
    
    /*
     * This test shows how to work with a task and save severeal intermediate steps of the content that the 
     * task is handling. 
     * The input parameters for this task are: (key1,value1) (key3,value3). 
     * 
     * (key2, null) is a variable that is input/output, this means that is a variable that comes defined, but it value can be changed
     * by the user
     * 
     * The expected outputs for the task are: (key2, value2), (key4, value4) (key5, value5) (key6, value6)
     */
    @Test
    public void testNewTaskWithMapContentAndOutput() {
        
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ],businessAdministrators = [ new User('Administrator') ], }),";                        
        str += "name =  'This is my task name' })";
            
        Map<String, Object> variablesMap = new HashMap<String, Object>();
        variablesMap.put("key1", "value1");
        variablesMap.put("key2", null);
        variablesMap.put("key3", "value3");
        ContentData data = ContentMarshallerHelper.marshal(null, variablesMap, null);
        
        Task task = TaskFactory.evalTask( new StringReader( str ));
        taskService.addTask( task, data );
        
        long taskId = task.getId();
        
        // Task should be assigned to the single potential owner and state set to Reserved
        
        
        Task task1 = taskService.getTaskById( taskId );
        assertEquals( AccessType.Inline, ((InternalTaskData) task1.getTaskData()).getDocumentAccessType() );
        assertEquals( "java.util.HashMap", task1.getTaskData().getDocumentType() );
        long contentId = task1.getTaskData().getDocumentContentId();
        assertTrue( contentId != -1 ); 

        
        
        Content content = taskService.getContentById(contentId);
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        if(!(unmarshalledObject instanceof Map)){
            fail("The variables should be a Map");
        }
        xmlRoundTripContent(content);
        
        Map<String, Object> unmarshalledvars = (Map<String, Object>) unmarshalledObject;
        
        assertEquals("value1",unmarshalledvars.get("key1") );
        assertNull(unmarshalledvars.get("key2") );
        assertEquals("value3",unmarshalledvars.get("key3") );
        
        taskService.start(taskId,"Bobba Fet" );
        
        task1 = taskService.getTaskById( taskId );
        assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        // Once the task has being started the user decide to start working on it. 
        
        
        Map<String, Object> intermediateOutputContentMap = new HashMap<String, Object>();
        
        intermediateOutputContentMap.put("key2", "value2");
        intermediateOutputContentMap.put("key4", "value4");
        
        
        taskService.addContent(taskId, intermediateOutputContentMap);
        
        Map<String, Object> finalOutputContentMap = new HashMap<String, Object>();
         finalOutputContentMap.put("key5", "value5");
        finalOutputContentMap.put("key6", "value6");
        
        
        taskService.complete(taskId,"Bobba Fet", finalOutputContentMap);
        
        task1 = taskService.getTaskById( taskId );
        assertEquals(Status.Completed, task1.getTaskData().getStatus());
        long outputContentId = task1.getTaskData().getOutputContentId();
        Content contentById = taskService.getContentById(outputContentId);
        
        unmarshalledObject = ContentMarshallerHelper.unmarshall(contentById.getContent(), null);
        assertNotNull(unmarshalledObject);
        if(!(unmarshalledObject instanceof Map)){
            fail("The variables should be a Map");
        
        }
        assertTrue(((Map<String, Object>)unmarshalledObject).containsKey("key2"));
        assertTrue(((Map<String, Object>)unmarshalledObject).containsKey("key4"));
        assertTrue(((Map<String, Object>)unmarshalledObject).containsKey("key5"));
        assertTrue(((Map<String, Object>)unmarshalledObject).containsKey("key6"));
        xmlRoundTripContent(contentById);
    }
    
    @Test
    public void testNewTaskWithLargeContent() {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";

        String largeContent = "";
        for (int i = 0; i < 1000; i++) {
            largeContent += i + "xxxxxxxxx";
        }

        ContentData data = ContentMarshallerHelper.marshal(null, largeContent, null);

        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, data);

        long taskId = task.getId();

        // Task should be assigned to the single potential owner and state set to Reserved


        Task task1 = taskService.getTaskById(taskId);
        assertEquals(AccessType.Inline, ((InternalTaskData) task1.getTaskData()).getDocumentAccessType());
        assertEquals("java.lang.String", task1.getTaskData().getDocumentType());
        long contentId = task1.getTaskData().getDocumentContentId();
        assertTrue(contentId != -1);

        Content content = taskService.getContentById(contentId);
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        assertEquals(largeContent, unmarshalledObject.toString());
        xmlRoundTripContent(content);
    }

    @Test
    public void testClaimWithMultiplePotentialOwners() throws Exception {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'),new User('Darth Vader') ], businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // A Task with multiple potential owners moves to "Ready" state until someone claims it.


        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Ready, task1.getTaskData().getStatus());


        taskService.claim(taskId, "Darth Vader");

        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task2.getTaskData().getStatus());
        assertEquals("Darth Vader", task2.getTaskData().getActualOwner().getId());
    }

    @Test
    public void testClaimWithGroupAssignee() throws Exception {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Knights Templer' )], businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // A Task with multiple potential owners moves to "Ready" state until someone claims it.


        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Ready, task1.getTaskData().getStatus());

        taskService.claim(taskId, "Darth Vader");

        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task2.getTaskData().getStatus());
        assertEquals("Darth Vader", task2.getTaskData().getActualOwner().getId());
    }
    

    @Test
    public void testStartFromReadyStateWithPotentialOwner() throws Exception {

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], businessAdministrators = [ new User('Administrator') ],}),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // A Task with multiple potential owners moves to "Ready" state until someone claims it.


        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Ready, task1.getTaskData().getStatus());

        // Go straight from Ready to Inprogress
        taskService.start(taskId, "Darth Vader");

        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task2.getTaskData().getStatus());
        assertEquals("Darth Vader", task2.getTaskData().getActualOwner().getId());
    }

    @Test
    public void testStartFromReadyStateWithIncorrectPotentialOwner() {

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], businessAdministrators = [ new User('Administrator') ],}),";
        str += "name = 'This is my task name'})";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();
        // A Task with multiple potential owners moves to "Ready" state until someone claims it.
        List<TaskSummary> tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", "en-UK");
        assertEquals(1, tasksAssignedAsPotentialOwner.size());
        
        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Ready, task1.getTaskData().getStatus());

        // State should not change as user isn't potential owner


        PermissionDeniedException denied = null;
        try {
            taskService.start(taskId, "Tony Stark");
        } catch (PermissionDeniedException e) {
            denied = e;
        }

        assertNotNull("Should get permissed denied exception", denied);

        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.Ready, task2.getTaskData().getStatus());
        assertNull(task2.getTaskData().getActualOwner());
    }

    @Test
    public void testStartFromReserved() throws Exception {

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ], businessAdministrators = [ new User('Administrator') ],}),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Task should be assigned to the single potential owner and state set to Reserved


        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task1.getTaskData().getStatus());
        assertEquals("Bobba Fet", task1.getTaskData().getActualOwner().getId());

        // Should change to InProgress

        taskService.start(taskId, "Bobba Fet");




        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task2.getTaskData().getStatus());
        assertEquals("Bobba Fet", task1.getTaskData().getActualOwner().getId());
    }

    @Test
    public void testStartFromReservedWithIncorrectUser() {

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ], businessAdministrators = [ new User('Administrator') ],}),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Task should be assigned to the single potential owner and state set to Reserved

        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task1.getTaskData().getStatus());
        assertEquals("Bobba Fet", task1.getTaskData().getActualOwner().getId());

        // Should change not change



        PermissionDeniedException denied = null;
        try {
            taskService.start(taskId, "Tony Stark");
        } catch (PermissionDeniedException e) {
            denied = e;
        }
        assertNotNull("Should get permissed denied exception", denied);

        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task2.getTaskData().getStatus());
        assertEquals("Bobba Fet", task1.getTaskData().getActualOwner().getId());
    }

    @Test
    public void testStop() {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Go straight from Ready to Inprogress
        taskService.start(taskId, "Darth Vader");

        taskService.getTaskById(taskId);
        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        // Now Stop

        taskService.stop(taskId, "Darth Vader");


        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task2.getTaskData().getStatus());
        assertEquals("Darth Vader", task2.getTaskData().getActualOwner().getId());
    }

    @Test
    public void testStopWithIncorrectUser() {

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Go straight from Ready to Inprogress

        taskService.start(taskId, "Darth Vader");




        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        // Should not stop


        PermissionDeniedException denied = null;
        try {
            taskService.stop(taskId, "Bobba Fet");
        } catch (PermissionDeniedException e) {
            denied = e;
        }
        assertNotNull("Should get permissed denied exception", denied);



        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task2.getTaskData().getStatus());
        assertEquals("Darth Vader", task2.getTaskData().getActualOwner().getId());
    }

    @Test
    public void testReleaseFromInprogress() throws Exception {


        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Go straight from Ready to Inprogress

        taskService.start(taskId, "Darth Vader");



        taskService.getTaskById(taskId);
        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        // Check is Released

        taskService.release(taskId, "Darth Vader");




        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.Ready, task2.getTaskData().getStatus());
        assertNull(task2.getTaskData().getActualOwner());
    }

    public void testReleaseFromReserved() {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name'})";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Go straight from Ready to Inprogress

        taskService.claim(taskId, "Darth Vader");




        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        // Check is Released

        taskService.release(taskId, "Darth Vader");

        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.Ready, task2.getTaskData().getStatus());
        assertNull(task2.getTaskData().getActualOwner());
    }

    @Test
    public void testReleaseWithIncorrectUser() {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Go straight from Ready to Inprogress

        taskService.claim(taskId, "Darth Vader");




        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        // Check is not changed


        PermissionDeniedException denied = null;
        try {
            taskService.release(taskId, "Bobba Fet");
        } catch (PermissionDeniedException e) {
            denied = e;
        }
        assertNotNull("Should get permissed denied exception", denied);



        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task2.getTaskData().getStatus());
        assertEquals("Darth Vader", task2.getTaskData().getActualOwner().getId());
    }

    @Test
    public void testSuspendFromReady() {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name'})";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Check is Ready

        taskService.getTaskById(taskId);
        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Ready, task1.getTaskData().getStatus());
        assertNull(task1.getTaskData().getActualOwner());

        // Check is Suspended

        taskService.suspend(taskId, "Darth Vader");




        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.Suspended, task2.getTaskData().getStatus());
        assertEquals(Status.Ready, task2.getTaskData().getPreviousStatus());
        assertNull(task1.getTaskData().getActualOwner());
    }

    @Test
    public void testSuspendFromReserved() {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Check is Reserved

        taskService.claim(taskId, "Darth Vader");

        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        // Check is Suspended

        taskService.suspend(taskId, "Darth Vader");

        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task2.getTaskData().getPreviousStatus());
        assertEquals(Status.Suspended, task2.getTaskData().getStatus());
        assertEquals("Darth Vader", task2.getTaskData().getActualOwner().getId());
    }

    @Test
    public void testSuspendFromReservedWithIncorrectUser() {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Check is Reserved
        taskService.claim(taskId, "Darth Vader");


        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        // Check is not changed


        PermissionDeniedException denied = null;
        try {
            taskService.suspend(taskId, "Bobba Fet");
        } catch (PermissionDeniedException e) {
            denied = e;
        }
        assertNotNull("Should get permissed denied exception", denied);



        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task2.getTaskData().getStatus());
        assertEquals("Darth Vader", task2.getTaskData().getActualOwner().getId());
    }

    @Test // FIX
    public void testResumeFromReady() {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Check is Ready


        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Ready, task1.getTaskData().getStatus());
        assertNull(task1.getTaskData().getActualOwner());

        // Check is Suspended

        taskService.suspend(taskId, "Darth Vader");

        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.Suspended, task2.getTaskData().getStatus());
        assertEquals(Status.Ready, task2.getTaskData().getPreviousStatus());
        assertNull(task1.getTaskData().getActualOwner());

        // Check is Resumed

        taskService.resume(taskId, "Darth Vader");

        Task task3 = taskService.getTaskById(taskId);
        assertEquals(Status.Ready, task3.getTaskData().getStatus());
        assertEquals(Status.Suspended, task3.getTaskData().getPreviousStatus());
        assertNull(task3.getTaskData().getActualOwner());
    }

    @Test
    public void testResumeFromReserved() {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Check is Reserved

        taskService.claim(taskId, "Darth Vader");


        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        // Check is suspended

        taskService.suspend(taskId, "Darth Vader");




        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task2.getTaskData().getPreviousStatus());
        assertEquals(Status.Suspended, task2.getTaskData().getStatus());
        assertEquals("Darth Vader", task2.getTaskData().getActualOwner().getId());

        // Check is Resumed

        taskService.resume(taskId, "Darth Vader");

        Task task3 = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task3.getTaskData().getStatus());
        assertEquals(Status.Suspended, task3.getTaskData().getPreviousStatus());
        assertEquals("Darth Vader", task3.getTaskData().getActualOwner().getId());
    }
    
    @Test
    public void testResumeFromCompleted() {       
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";

        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();
        taskService.claim(taskId, "Darth Vader");

        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        taskService.start(taskId, "Darth Vader");

        task1 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        taskService.complete(taskId, "Darth Vader", null);

        task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Completed, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());
       
        assertThatExceptionOfType(PermissionDeniedException.class).isThrownBy(() -> { 
            taskService.resume(taskId, "Darth Vader"); })
        .withMessageContaining("was unable to execute operation 'Resume' on task id");                

        task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Completed, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());
    }

    @Test
    public void testResumeFromReservedWithIncorrectUser() {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Check is Reserved

        taskService.claim(taskId, "Darth Vader");


        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        // Check not changed

        PermissionDeniedException denied = null;
        try {
            taskService.suspend(taskId, "Bobba Fet");
        } catch (PermissionDeniedException e) {
            denied = e;
        }
        assertNotNull("Should get permissed denied exception", denied);

        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task2.getTaskData().getStatus());
        assertEquals("Darth Vader", task2.getTaskData().getActualOwner().getId());
    }

    @Test
    public void testSkipFromReady() {
        

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = true} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Check is Complete

        taskService.skip(taskId, "Darth Vader");

        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Obsolete, task1.getTaskData().getStatus());
        assertNull(task1.getTaskData().getActualOwner());
    }

    @Test
    public void testSkipFromReserved() {
        

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = true} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Go straight from Ready 

        taskService.claim(taskId, "Darth Vader");


        // Check is Complete

        taskService.skip(taskId, "Darth Vader");

        taskService.getTaskById(taskId);
        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Obsolete, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());
    }

    @Test
    public void testDelegateFromReady() throws Exception {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        // Check is Delegated
        taskService.delegate(taskId, "Darth Vader", "Tony Stark");




        Task task2 = taskService.getTaskById(taskId);
        User user = createUser("Darth Vader");
        assertTrue(task2.getPeopleAssignments().getPotentialOwners().contains(user));
        user = createUser("Tony Stark");
        assertTrue(task2.getPeopleAssignments().getPotentialOwners().contains(user));
        assertEquals("Tony Stark", task2.getTaskData().getActualOwner().getId());
        // this was checking for ready, but it should be reserved.. it was an old bug
        assertEquals(Status.Reserved, task2.getTaskData().getStatus());
    }

    @Test
    public void testDelegateFromReserved() throws Exception {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Claim and Reserved

        taskService.claim(taskId, "Darth Vader");


        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        // Check is Delegated

        taskService.delegate(taskId, "Darth Vader", "Tony Stark");




        Task task2 = taskService.getTaskById(taskId);
        User user = createUser("Darth Vader");
        assertTrue(task2.getPeopleAssignments().getPotentialOwners().contains(user));
        user = createUser("Tony Stark");
        assertTrue(task2.getPeopleAssignments().getPotentialOwners().contains(user));
        assertEquals("Tony Stark", task2.getTaskData().getActualOwner().getId());
        assertEquals(Status.Reserved, task2.getTaskData().getStatus());
    }

    @Test
    public void testDelegateFromReservedWithIncorrectUser() throws Exception {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        // Claim and Reserved

        taskService.claim(taskId, "Darth Vader");

        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        // Check was not delegated


        PermissionDeniedException denied = null;
        try {
            taskService.delegate(taskId, "Bobba Fet", "Tony Stark");
        } catch (PermissionDeniedException e) {
            denied = e;
        }
        assertNotNull("Should get permissed denied exception", denied);

        Task task2 = taskService.getTaskById(taskId);
        User user = createUser("Darth Vader");
        assertTrue(task2.getPeopleAssignments().getPotentialOwners().contains(user));
        user = createUser("Tony Stark");
        assertFalse(task2.getPeopleAssignments().getPotentialOwners().contains(user));
        assertEquals("Darth Vader", task2.getTaskData().getActualOwner().getId());
        assertEquals(Status.Reserved, task2.getTaskData().getStatus());
    }

    @Test
    public void testForwardFromReady() throws Exception {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Check is Forwarded

        taskService.forward(taskId, "Darth Vader", "Tony Stark");

        Task task2 = taskService.getTaskById(taskId);
        assertFalse(task2.getPeopleAssignments().getPotentialOwners().contains(createUser("Darth Vader")));
        assertTrue(task2.getPeopleAssignments().getPotentialOwners().contains(createUser("Tony Stark")));
        assertNull(task2.getTaskData().getActualOwner());
        assertEquals(Status.Ready, task2.getTaskData().getStatus());
    }

    @Test
    public void testForwardFromReserved() throws Exception {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Claim and Reserved

        taskService.claim(taskId, "Darth Vader");




        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        // Check is Delegated

        taskService.forward(taskId, "Darth Vader", "Tony Stark");


        Task task2 = taskService.getTaskById(taskId);
        User user = createUser("Darth Vader");
        assertFalse(task2.getPeopleAssignments().getPotentialOwners().contains(user));
        user = createUser("Tony Stark");
        assertTrue(task2.getPeopleAssignments().getPotentialOwners().contains(user));
        assertNull(task2.getTaskData().getActualOwner());
        assertEquals(Status.Ready, task2.getTaskData().getStatus());
    }

    @Test
    public void testForwardReadyWithBusinessAdministrator() throws Exception {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Ready
        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Ready, task1.getTaskData().getStatus());

        // Check is Delegated
        taskService.forward(taskId, "Administrator", "Tony Stark");


        Task task2 = taskService.getTaskById(taskId);
        User user = createUser("Darth Vader");
        assertTrue(task2.getPeopleAssignments().getPotentialOwners().contains(user));
        user = createUser("Tony Stark");
        assertTrue(task2.getPeopleAssignments().getPotentialOwners().contains(user));
        assertNull(task2.getTaskData().getActualOwner());
        assertEquals(Status.Ready, task2.getTaskData().getStatus());
    }

    @Test
    public void testForwardReservedWithBusinessAdministrator() throws Exception {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Claim and Reserved
        taskService.claim(taskId, "Darth Vader");

        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        // Check is Delegated
        taskService.forward(taskId, "Administrator", "Tony Stark");


        Task task2 = taskService.getTaskById(taskId);
        User user = createUser("Darth Vader");
        assertTrue(task2.getPeopleAssignments().getPotentialOwners().contains(user));
        user = createUser("Tony Stark");
        assertTrue(task2.getPeopleAssignments().getPotentialOwners().contains(user));
        assertNull(task2.getTaskData().getActualOwner());
        assertEquals(Status.Ready, task2.getTaskData().getStatus());
    }

    @Test
    public void testForwardGroupWithBusinessAdministrator() throws Exception {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [ new Group('Knights Templer') ], businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        final Group group = createGroup("Knights Templer");
        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Ready, task1.getTaskData().getStatus());
        System.out.println("task1.getPeopleAssignments().getPotentialOwners() = " + task1.getPeopleAssignments().getPotentialOwners());
        assertTrue(task1.getPeopleAssignments().getPotentialOwners().contains(group));

        // Check is Delegated
        try {
            taskService.forward(taskId, "Administrator", "Tony Stark");
            fail("Forward task from Group to a User should fail");
        } catch (PermissionDeniedException e) {}

        Task task2 = taskService.getTaskById(taskId);
        assertTrue(task2.getPeopleAssignments().getPotentialOwners().contains(group));
        assertNull(task2.getTaskData().getActualOwner());
        assertEquals(Status.Ready, task2.getTaskData().getStatus());
    }

    @Test
    public void testForwardFromReservedWithIncorrectUser() throws Exception {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Claim and Reserved

        taskService.claim(taskId, "Darth Vader");




        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        // Check was not delegated


        PermissionDeniedException denied = null;
        try {
            taskService.forward(taskId, "Bobba Fet", "Tony Stark");
        } catch (PermissionDeniedException e) {
            denied = e;
        }
        assertNotNull("Should get permission denied exception", denied);



        Task task2 = taskService.getTaskById(taskId);
        User user = createUser("Darth Vader");
        assertTrue(task2.getPeopleAssignments().getPotentialOwners().contains(user));
        user = createUser("Tony Stark");
        assertFalse(task2.getPeopleAssignments().getPotentialOwners().contains(user));
        assertEquals("Darth Vader", task2.getTaskData().getActualOwner().getId());
        assertEquals(Status.Reserved, task2.getTaskData().getStatus());
    }
    
    @Test
    public void testForwardFromReadyToGroup() throws Exception {
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Check is Forwarded
        IllegalArgumentException failed = null;
        try {
            taskService.forward(taskId, "Darth Vader", "Knights Templer");
        } catch (IllegalArgumentException e) {
            failed = e;
        }
        assertNotNull("Should get permissed denied exception", failed);
    }

    @Test
    public void testComplete() {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

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
    }

    @Test
    public void testCompleteWithIncorrectUser() {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Go straight from Ready to Inprogress

        taskService.start(taskId, "Darth Vader");


        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        // Should not complete as wrong user


        PermissionDeniedException denied = null;
        try {
            taskService.complete(taskId, "Bobba Fet", null);
        } catch (PermissionDeniedException e) {
            denied = e;
        }
        assertNotNull("Should get permissed denied exception", denied);



        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task2.getTaskData().getStatus());
        assertEquals("Darth Vader", task2.getTaskData().getActualOwner().getId());
    }

    @Test
    public void testCompleteWithContent() {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Go straight from Ready to Inprogress

        taskService.start(taskId, "Darth Vader");


        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        //ContentData data = ContentMarshallerHelper.marshal("content", null);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("content", "content");
        taskService.complete(taskId, "Darth Vader", params);

        List<Content> allContent = taskService.getAllContentByTaskId(taskId);
        assertNotNull(allContent);
        assertEquals(3, allContent.size());
        // only input(0) and output(1) is present
        assertNotNull(allContent.get(0));
        assertNotNull(allContent.get(1));
        assertNull(allContent.get(2));

        Task task2 = taskService.getTaskById(taskId);
        assertEquals(AccessType.Inline, ((InternalTaskData) task2.getTaskData()).getOutputAccessType());
        assertEquals("java.util.HashMap", task2.getTaskData().getOutputType());
        long contentId = task2.getTaskData().getOutputContentId();
        assertTrue(contentId != -1);



        Content content = taskService.getContentById(contentId);
        Map<String, Object> unmarshalledObject = (Map<String, Object>) ContentMarshallerHelper.unmarshall(content.getContent(), null);
        assertEquals("content", unmarshalledObject.get("content"));
        
        // update content
        params.put("content", "updated content");
	    taskService.setOutput(taskId, "Darth Vader", params);
	    
	    task = taskService.getTaskById(taskId);
	    contentId = task.getTaskData().getOutputContentId();
	    
	    content = taskService.getContentById(contentId);
	    String updated = new String(content.getContent());
	    unmarshalledObject = (Map<String, Object>) ContentMarshallerHelper.unmarshall(content.getContent(), null);
        assertEquals("updated content", unmarshalledObject.get("content"));
        
        taskService.deleteOutput(taskId, "Darth Vader");
        content = taskService.getContentById(contentId);
        assertNull(content);
    }

    @Test
    public void testCompleteWithResults() {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], businessAdministrators = [ new User('Administrator') ],}),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Go straight from Ready to Inprogress

        taskService.start(taskId, "Darth Vader");


        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());


        Map<String, Object> params = new HashMap<String, Object>();
        params.put("content", "content");
        taskService.complete(taskId, "Darth Vader", params);


        Task task2 = taskService.getTaskById(taskId);
        assertEquals(AccessType.Inline, ((InternalTaskData) task2.getTaskData()).getOutputAccessType());
        assertEquals("java.util.HashMap", task2.getTaskData().getOutputType());
        long contentId = task2.getTaskData().getOutputContentId();
        assertTrue(contentId != -1);



        Content content = taskService.getContentById(contentId);
        Map<String, Object> unmarshalledObject = (Map<String, Object>) ContentMarshallerHelper.unmarshall(content.getContent(), null);
        assertEquals("content", unmarshalledObject.get("content"));
    }

    @Test
    public void testFail() {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Go straight from Ready to Inprogress

        taskService.start(taskId, "Darth Vader");




        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        // Check is Failed

        taskService.fail(taskId, "Darth Vader", null);

        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.Failed, task2.getTaskData().getStatus());
        assertEquals("Darth Vader", task2.getTaskData().getActualOwner().getId());
    }

    @Test
    public void testFailWithIncorrectUser() {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], businessAdministrators = [ new User('Administrator') ],}),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Go straight from Ready to Inprogress

        taskService.start(taskId, "Darth Vader");




        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        // Should not fail as wrong user


        PermissionDeniedException denied = null;
        try {
            taskService.fail(taskId, "Bobba Fet", null);
        } catch (PermissionDeniedException e) {
            denied = e;
        }
        assertNotNull("Should get permissed denied exception", denied);



        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task2.getTaskData().getStatus());
        assertEquals("Darth Vader", task2.getTaskData().getActualOwner().getId());
    }

    @Test
    public void testFailWithContent() {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], businessAdministrators = [ new User('Administrator') ],}),";
        str += "name =  'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Go straight from Ready to Inprogress
        taskService.start(taskId, "Darth Vader");

        taskService.getTaskById(taskId);
        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        Map<String, Object> faultData = new HashMap<String, Object>();
        faultData.put("faultType", "type");
        faultData.put("faultName", "faultName");
        faultData.put("content", "content");

        taskService.fail(taskId, "Darth Vader", faultData);
        
        List<Content> allContent = taskService.getAllContentByTaskId(taskId);
        assertNotNull(allContent);
        assertEquals(3, allContent.size());
        // only input(0) and fault(2) is present
        assertNotNull(allContent.get(0));
        assertNull(allContent.get(1));
        assertNotNull(allContent.get(2));

        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.Failed, task2.getTaskData().getStatus());
        assertEquals(AccessType.Inline, ((InternalTaskData) task2.getTaskData()).getFaultAccessType());
        assertEquals("type", task2.getTaskData().getFaultType());
        assertEquals("faultName", task2.getTaskData().getFaultName());
        long contentId = task2.getTaskData().getFaultContentId();
        assertTrue(contentId != -1);



        Content content = taskService.getContentById(contentId);
        Map<String, Object> unmarshalledContent = (Map<String, Object>) ContentMarshallerHelper.unmarshall(content.getContent(), null);
        assertEquals("content", unmarshalledContent.get("content"));
        xmlRoundTripContent(content);
        
        // update fault
	    FaultData data = TaskModelProvider.getFactory().newFaultData();
	    data.setAccessType(AccessType.Inline);
	    data.setType("type");
	    data.setFaultName("faultName");
	    data.setContent("updated content".getBytes());
	    
	    taskService.setFault(taskId, "Darth Vader", data);
	    
	    task = taskService.getTaskById(taskId);
	    contentId = task.getTaskData().getFaultContentId();
	    
	    content = taskService.getContentById(contentId);
	    String updated = new String(content.getContent());
	    assertEquals("updated content", updated);
        
	    // delete fault
        taskService.deleteFault(taskId, "Darth Vader");
        content = taskService.getContentById(contentId);
        assertNull(content);
    }
//    
//    /**
//     * The issue here has to do with the fact that hibernate uses lazy initialization. 
//     * Actually, what's happening is that one of the collections retrieved isn't retrieved "for update", 
//     * so that the proxy collection instance retrieved can't be updated. 
//     * (The collection instance can't be updated because hibernate doesn't allowed that unless the collection 
//     * has been retrieved "for update" -- which is actually logical.)
//     * 
//     * This, of course, only happens when using the LocalTaskService. Why? Because the LocalTaskService
//     * "shares" a persistence context with the taskService. If I spent another half-hour, I could explain
//     * why that causes this particular problem. 
//     * Regardless,  I can't stress enough how much that complicates the situation here, and, especially, 
//     * why that makes the LocalTaskService a significantly different variant of the TaskService
//     * than the HornetQ, Mina or other transport medium based instances.  
//     */
//    public void FIXME_testRegisterRemove() throws Exception {
//    	  Map <String, Object> vars = fillVariables();
//        
//        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
//        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], }),";                        
//        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
//
//        
//        Task task = ( Task )  TaskFactory.eval( new StringReader( str ), vars );
//        taskService.addTask( task, null );
//        
//        long taskId = task.getId();               
//       
//        taskService.register(taskId, "Bobba Fet");
//
//        
//        Task task1 = taskService.getTaskById(taskId);
//        List<OrganizationalEntity> myRecipientTasks = task1.getPeopleAssignments().getRecipients();
//        
//        assertNotNull(myRecipientTasks);
//        assertEquals(1, myRecipientTasks.size());
//        assertTrue(task1.getPeopleAssignments().getRecipients().contains("Bobba Fet"));
//        
//        taskService.remove(taskId, "Bobba Fet");
//        
//        Task task2 = taskService.getTaskById( taskId );
//        assertFalse(task2.getPeopleAssignments().getRecipients().contains("Bobba Fet"));
//    }
//    

    @Test
    public void testRemoveNotInRecipientList() {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { status = Status.Ready } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ],businessAdministrators = [ new User('Administrator') ],";
        str += "recipients = [new User('Bobba Fet') ] }),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str), null);
        // We need to add the Admin if we don't initialize the task
        if (task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            User user = createUser("Administrator");
            businessAdmins.add(user);
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            ((InternalPeopleAssignments) task.getPeopleAssignments()).setBusinessAdministrators(businessAdmins);
        }
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Do nominate and fail due to Ready status


        List<TaskSummary> myRecipientTasks = taskService.getTasksAssignedAsRecipient("Jabba Hutt");

        assertNotNull(myRecipientTasks);
        assertEquals(0, myRecipientTasks.size());



        List<TaskSummary> myPotentialTasks = taskService.getTasksAssignedAsPotentialOwner("Jabba Hutt", "en-UK");

        assertNotNull(myPotentialTasks);
        assertEquals(0, myPotentialTasks.size());


        try {
            taskService.remove(taskId, "Jabba Hutt");
            fail("Shouldn't be successful");
        } catch (RuntimeException e) { //expected
        }

        //shouldn't affect the assignments


        Task task1 = taskService.getTaskById(taskId);
        User user = createUser("Bobba Fet");
        assertTrue(((InternalPeopleAssignments) task1.getPeopleAssignments()).getRecipients().contains(user));
    }

    /**
     * Nominate an organization entity to process the task. If it is nominated
     * to one person then the new state of the task is Reserved. If it is
     * nominated to several people then the new state of the task is Ready. This
     * can only be performed when the task is in the state Created.
     */
    @Test
    public void testNominateOnOtherThanCreated() {
        

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { status = Status.Ready } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [ new User('Administrator') ] ,";
        str += " potentialOwners = [ new User('Darth Vader'), new User('Bobba Fet') ] } ),";
        str += "name =  'This is my task name' })";

        Task task = TaskFactory.evalTask(new StringReader(str), null);

        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        taskService.start(taskId, "Bobba Fet");

        try {
            List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>();
            User user = createUser("Bobba Fet");
            potentialOwners.add(user);
            taskService.nominate(taskId, "Darth Vader", potentialOwners);

            fail("Shouldn't be successful");
        } catch (RuntimeException e) { //expected
//        	assertNotNull(nominateHandler.getError());
//        	assertNotNull(nominateHandler.getError().getMessage());
//        	assertTrue(nominateHandler.getError().getMessage().contains("Created"));
        }

        //shouldn't affect the assignments

        Task task1 = taskService.getTaskById(taskId);
        User user = createUser("Darth Vader");
        assertTrue(task1.getPeopleAssignments().getPotentialOwners().contains(user));
        user = createUser("Bobba Fet");
        assertTrue(task1.getPeopleAssignments().getPotentialOwners().contains(user));
    }

    @Test
    public void testNominateWithIncorrectUser() {
        

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [ new User('Bobba Fet') ] } ),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();


        try {
            List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>(1);
            User user = createUser("Jabba Hutt");
            potentialOwners.add(user);
            taskService.nominate(taskId, "Darth Vader", potentialOwners);

            fail("Shouldn't be successful");
        } catch (RuntimeException e) { //expected
//        	assertNotNull(nominateHandler.getError());
//        	assertNotNull(nominateHandler.getError().getMessage());
//        	assertTrue(nominateHandler.getError().getMessage().contains("Darth Vader"));
        }

        //shouldn't affect the assignments

        Task task1 = taskService.getTaskById(taskId);
        User user = createUser("Bobba Fet");
        assertTrue(task1.getPeopleAssignments().getBusinessAdministrators().contains(user));
        assertEquals(task1.getTaskData().getStatus(), Status.Created);
    }

    @Test
    public void testNominateToUser() {
        

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [ new User('Darth Vader'), new User('Bobba Fet') ] } ),";
        str += "name =  'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();


        List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>(1);
        User user = createUser("Jabba Hutt");
        potentialOwners.add(user);
        taskService.nominate(taskId, "Darth Vader", potentialOwners);


        //shouldn't affect the assignments


        Task task1 = taskService.getTaskById(taskId);
        assertEquals(task1.getTaskData().getActualOwner().getId(), "Jabba Hutt");
        assertEquals(task1.getTaskData().getStatus(), Status.Reserved);
    }

    @Test
    public void testNominateToGroup() {
        

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [ new User('Darth Vader'), new User('Bobba Fet') ] } ),";
        str += "name = 'This is my task name'})";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();


        List<OrganizationalEntity> potentialGroups = new ArrayList<OrganizationalEntity>();
        Group group = TaskModelProvider.getFactory().newGroup();
        ((InternalOrganizationalEntity) group).setId("Knights Templer");
        potentialGroups.add(group);
        taskService.nominate(taskId, "Darth Vader", potentialGroups);


        //shouldn't affect the assignments


        Task task1 = taskService.getTaskById(taskId);
        assertTrue(task1.getPeopleAssignments().getPotentialOwners().contains(group));
        assertEquals(task1.getTaskData().getStatus(), Status.Ready);
    }

    @Test
    public void testActivate() {
        

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { ";
        str += "businessAdministrators = [ new User('Darth Vader') ] } ),";
        str += "name =  'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();


        taskService.activate(taskId, "Darth Vader");

        Task task1 = taskService.getTaskById(taskId);

        assertEquals(task1.getTaskData().getStatus(), Status.Ready);
        //When we are not using remoting the object is the same
        //assertTrue(task1.equals(task));
        //When we use remoting this will be false
        //assertFalse(task1.equals(task));
    }

    @Test
    public void testActivateWithIncorrectUser() {
        

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [ new User('Darth Vader'), new User('Bobba Fet') ], ";
        str += "businessAdministrators = [ new User('Jabba Hutt') ] } ),";
        str += "name =  'This is my task name'})";

        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();


        try {
            taskService.activate(taskId, "Darth Vader");

            fail("Shouldn't have succeded");
        } catch (RuntimeException e) {
//        	assertNotNull(activateResponseHandler.getError());
//        	assertNotNull(activateResponseHandler.getError().getMessage());
//        	assertTrue(activateResponseHandler.getError().getMessage().toLowerCase().contains("status"));
        }

    }

    @Test
    public void testActivateFromIncorrectStatus() {
        

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { status = Status.Ready } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [ new User('Darth Vader'), new User('Bobba Fet') ], ";
        str += "businessAdministrators = [ new User('Jabba Hutt') ] } ),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str), null);
        // We need to add the Admin if we don't initialize the task
        if (task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            User user = createUser("Administrator");
            businessAdmins.add(user);
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            ((InternalPeopleAssignments) task.getPeopleAssignments()).setBusinessAdministrators(businessAdmins);
        }
        
        taskService.addTask(task, new HashMap<String, Object>());

        
        long taskId = task.getId();


        try {
            taskService.activate(taskId, "Darth Vader");

            fail("Shouldn't have succeded");
        } catch (RuntimeException e) {
//        	assertNotNull(activateResponseHandler.getError());
//        	assertNotNull(activateResponseHandler.getError().getMessage());
//        	assertTrue(activateResponseHandler.getError().getMessage().contains("Darth Vader"));
        }
    }

    @Test
    public void testExitFromReady() {
        

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = false} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], businessAdministrators = [ new User('Administrator')] }),";
        str += "name =  'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();
        task = taskService.getTaskById(taskId);
        assertEquals(Status.Ready, task.getTaskData().getStatus());


        taskService.exit(taskId, "Administrator");

        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Exited, task1.getTaskData().getStatus());
    }

    @Test
    public void testExitFromReserved() {
        

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = false} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ], businessAdministrators = [ new User('Administrator')] }),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();
        task = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task.getTaskData().getStatus());


        taskService.exit(taskId, "Administrator");

        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Exited, task1.getTaskData().getStatus());
    }

    @Test
    public void testExitFromInProgress() {
        

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = false} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ], businessAdministrators = [ new User('Administrator')] }),";
        str += "name =  'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();
        task = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task.getTaskData().getStatus());

        taskService.start(taskId, "Bobba Fet");
        task = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task.getTaskData().getStatus());

        taskService.exit(taskId, "Administrator");

        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Exited, task1.getTaskData().getStatus());
    }

    @Test
    public void testExitFromSuspended() {
        

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = false} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ], businessAdministrators = [ new User('Administrator')] }),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();
        task = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task.getTaskData().getStatus());

        taskService.suspend(taskId, "Bobba Fet");
        task = taskService.getTaskById(taskId);
        assertEquals(Status.Suspended, task.getTaskData().getStatus());

        taskService.exit(taskId, "Administrator");

        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Exited, task1.getTaskData().getStatus());
    }

    @Test
    public void testExitPermissionDenied() {
        

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = false} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], businessAdministrators = [ new User('Administrator')] }),";
        str += "name =  'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();
        task = taskService.getTaskById(taskId);
        assertEquals(Status.Ready, task.getTaskData().getStatus());

        try {
            taskService.exit(taskId, "Darth Vader");
            fail("Non admin user can't exit a task");
        } catch (PermissionDeniedException e) {
        }
        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Ready, task1.getTaskData().getStatus());
    }

    @Test
    public void testExitNotAvailableToUsers() {
        

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = false} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')], businessAdministrators = [ new User('Administrator')] }),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();
        task = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task.getTaskData().getStatus());


        taskService.exit(taskId, "Administrator");

        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Exited, task1.getTaskData().getStatus());

        List<TaskSummary> exitedTasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", "en-UK");
        assertEquals(0, exitedTasks.size());

    }

    @Test
    public void testClaimConflictAndRetry() {
        

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('salaboy'), new User('Bobba Fet') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";

        // Create a local instance of the TaskService

        // Deploy the Task Definition to the Task Component
        taskService.addTask(TaskFactory.evalTask(new StringReader(str)), new HashMap<String, Object>());

        // Because the Task contains a direct assignment we can query it for its Potential Owner
        // Notice that we obtain a list of TaskSummary (a lightweight representation of a task)
        List<TaskSummary> salaboyTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");

        // We know that there is just one task available so we get the first one
        Long salaboyTaskId = salaboyTasks.get(0).getId();

        // In order to check the task status we need to get the real task
        // The task is in a Reserved status because it already have a well-defined Potential Owner
        Task salaboyTask = taskService.getTaskById(salaboyTaskId);
        assertEquals(Status.Ready, salaboyTask.getTaskData().getStatus());

        // Because the Task contains a direct assignment we can query it for its Potential Owner
        // Notice that we obtain a list of TaskSummary (a lightweight representation of a task)
        List<TaskSummary> bobbaTasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", "en-UK");

        // We know that there is just one task available so we get the first one
        Long bobbaTaskId = bobbaTasks.get(0).getId();
        assertEquals(bobbaTaskId, salaboyTaskId);
        // In order to check the task status we need to get the real task
        // The task is in a Reserved status because it already have a well-defined Potential Owner
        Task bobbaTask = taskService.getTaskById(bobbaTaskId);
        assertEquals(Status.Ready, bobbaTask.getTaskData().getStatus());


        taskService.claim(bobbaTask.getId(), "Bobba Fet");

        try {
            taskService.claim(salaboyTask.getId(), "salaboy");
        } catch (PermissionDeniedException ex) {
            // The Task is gone.. salaboy needs to retry
            assertNotNull(ex);
        }

    }

    @Test
    public void testClaimNextAvailable() {
        
        // Create a local instance of the TaskService

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('salaboy'), new User('Bobba Fet') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";

        // Deploy the Task Definition to the Task Component
        taskService.addTask(TaskFactory.evalTask(new StringReader(str)), new HashMap<String, Object>());

        // we don't need to query for our task to see what we will claim, just claim the next one available for us

        taskService.claimNextAvailable("Bobba Fet", "en-UK");


        List<Status> status = new ArrayList<Status>();
        status.add(Status.Ready);
        List<TaskSummary> salaboyTasks = taskService.getTasksAssignedAsPotentialOwnerByStatus("salaboy", status, "en-UK");
        assertEquals(0, salaboyTasks.size());

    }
    
    @Test
    public void testCompleteWithRestrictedGroups() {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('analyst'), new Group('Crusaders') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();
        
        List<OrganizationalEntity> potOwners = task.getPeopleAssignments().getPotentialOwners();
        assertNotNull(potOwners);
        assertEquals(1, potOwners.size());
        assertEquals("Crusaders", potOwners.get(0).getId());

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
    }
    
    @Test
    public void testInvalidTask() {
    	try {
    		taskService.claim(-1, "Darth Vader");
    	} catch (PermissionDeniedException e) {
    		if ("Task '-1' not found".equals(e.getMessage())) {
    			return;
    		} else {
    			throw e;
    		}
    	}
    }
    
    @Test
    public void testCompleteWithComments() {       
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";

        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();
        
        List<Comment> comments = taskService.getAllCommentsByTaskId(taskId);
        assertNotNull(comments);
        assertEquals(0, comments.size());
        
        User user = createUser("Bobba Fet");
        
        Comment comment = TaskModelProvider.getFactory().newComment();
        ((InternalComment)comment).setAddedAt(new Date());
        ((InternalComment)comment).setAddedBy(user);
        ((InternalComment)comment).setText("Simple test comment");
        taskService.addComment(taskId, comment);
        
        comments = taskService.getAllCommentsByTaskId(taskId);
        assertNotNull(comments);
        assertEquals(1, comments.size());

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
    }
    
    @Test
    public void testNewTaskWithSingleInvalidPotentialOwner() {
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('invalid')  ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));

        taskService.addTask(task, new HashMap<String, Object>());
        try {
	        String potOwner = "invalid";             
	        taskService.getTasksAssignedAsPotentialOwner(potOwner, "en-UK");
	        fail("Should fail due to same id for group and user");
        } catch (RuntimeException e) {
        	assertTrue(e.getMessage().endsWith("please check that there is no group and user with same id"));
        }
    }

    @Test
    public void testLongDescription() {
        // BZ-1107473
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";

        Task task = TaskFactory.evalTask(new StringReader(str));

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("a");
        }
        String comment = sb.toString();

        // NOTE: AbstractHTWorkItemHandler stores "Comment" parameter as 'Description'
        List<I18NText> descriptions = new ArrayList<I18NText>();
        I18NText descText = TaskModelProvider.getFactory().newI18NText();
        ((InternalI18NText) descText).setLanguage("en-UK");
        ((InternalI18NText) descText).setText(comment);
        descriptions.add(descText);
        ((InternalTask)task).setDescriptions(descriptions);

        taskService.addTask(task, new HashMap<String, Object>()); // Fails if shortText is longer than 255

        long taskId = task.getId();

        Task resultTask = taskService.getTaskById(taskId);
        List<I18NText> resultDescriptions = resultTask.getDescriptions();

        InternalI18NText resultDescription = (InternalI18NText)resultDescriptions.get(0);

        assertEquals(1000, resultDescription.getText().length()); // This is text

        // 6.1.x no longer uses shortText in API and Taskorm.xml so no assert.
    }
    
    @Test
    public void testCompleteByActiveTasks() {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { activationTime = new Date(), processInstanceId = 123 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";

        Date beforeCreationTime = new Date(System.currentTimeMillis()-1000);
 
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();
        assertNotNull(task.getTaskData().getActivationTime());
        
        
        // Go straight from Ready to Inprogress@@@@@@ 2015-08-04 17:13:24.755
        taskService.start(taskId, "Darth Vader");
        
        List<TaskSummary> activeTasks = taskService.getActiveTasks();
        assertNotNull(activeTasks);
        assertEquals(1,  activeTasks.size());
        activeTasks = taskService.getActiveTasks(beforeCreationTime);
        assertNotNull(activeTasks);
        assertEquals(1,  activeTasks.size());


        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        // Check is Complete

        taskService.complete(taskId, "Darth Vader", null);

        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.Completed, task2.getTaskData().getStatus());
        assertEquals("Darth Vader", task2.getTaskData().getActualOwner().getId());
        
        List<TaskSummary> completedTasks = taskService.getCompletedTasks();
        assertNotNull(completedTasks);
        assertEquals(1,  completedTasks.size());
        
        completedTasks = taskService.getCompletedTasks(beforeCreationTime);
        assertNotNull(completedTasks);
        assertEquals(1,  completedTasks.size());
        
        completedTasks = taskService.getCompletedTasksByProcessId(123l);
        assertNotNull(completedTasks);
        assertEquals(1,  completedTasks.size());
        
        taskService.archiveTasks(completedTasks);
        
        List<TaskSummary> archiveddTasks = taskService.getArchivedTasks();
        assertNotNull(archiveddTasks);
        assertEquals(1,  archiveddTasks.size());
    }
    
    @Test
    public void testCompleteWithContentAndVarInputListener() {
        testCompleteWithContentAndVarListener(new DefaultTaskEventListener(){

            @Override
            public void beforeTaskStartedEvent(TaskEvent event) {
                assertNotNull(event.getTask().getTaskData().getTaskInputVariables());
                assertEquals(1, event.getTask().getTaskData().getTaskInputVariables().size());
                assertTrue(event.getTask().getTaskData().getTaskInputVariables().containsKey("input")); 
                assertNull(event.getTask().getTaskData().getTaskOutputVariables());
                
                event.getTaskContext().loadTaskVariables(event.getTask());
                
                assertNotNull(event.getTask().getTaskData().getTaskInputVariables());
                assertEquals(1, event.getTask().getTaskData().getTaskInputVariables().size());
                assertTrue(event.getTask().getTaskData().getTaskInputVariables().containsKey("input"));
                
                assertNull(event.getTask().getTaskData().getTaskOutputVariables());
            }

            @Override
            public void beforeTaskCompletedEvent(TaskEvent event) {
                assertNotNull(event.getTask().getTaskData().getTaskInputVariables());
                assertEquals(1, event.getTask().getTaskData().getTaskInputVariables().size());
                assertTrue(event.getTask().getTaskData().getTaskInputVariables().containsKey("input"));                
                assertNotNull(event.getTask().getTaskData().getTaskOutputVariables());
                assertEquals(1, event.getTask().getTaskData().getTaskOutputVariables().size());
                assertTrue(event.getTask().getTaskData().getTaskOutputVariables().containsKey("content"));
                
                event.getTaskContext().loadTaskVariables(event.getTask());
                
                assertNotNull(event.getTask().getTaskData().getTaskInputVariables());
                assertEquals(1, event.getTask().getTaskData().getTaskInputVariables().size());
                assertTrue(event.getTask().getTaskData().getTaskInputVariables().containsKey("input"));
                
                assertNotNull(event.getTask().getTaskData().getTaskOutputVariables());                
                assertEquals(1, event.getTask().getTaskData().getTaskOutputVariables().size());
                assertTrue(event.getTask().getTaskData().getTaskOutputVariables().containsKey("content"));
            }

            @Override
            public void beforeTaskAddedEvent(TaskEvent event) {
                assertNotNull(event.getTask().getTaskData().getTaskInputVariables());
                assertEquals(1, event.getTask().getTaskData().getTaskInputVariables().size());
                assertTrue(event.getTask().getTaskData().getTaskInputVariables().containsKey("input"));
                
                assertNull(event.getTask().getTaskData().getTaskOutputVariables());
                
                event.getTaskContext().loadTaskVariables(event.getTask());
                
                assertNotNull(event.getTask().getTaskData().getTaskInputVariables());
                assertEquals(1, event.getTask().getTaskData().getTaskInputVariables().size());
                assertTrue(event.getTask().getTaskData().getTaskInputVariables().containsKey("input"));
                
                assertNull(event.getTask().getTaskData().getTaskOutputVariables());
            }
            
        });

    }
    
    @Test
    public void testCompleteWithContentAndVarOutputListener() {
        
        testCompleteWithContentAndVarListener(new DefaultTaskEventListener(){

            @Override
            public void afterTaskStartedEvent(TaskEvent event) {
                
                assertNotNull(event.getTask().getTaskData().getTaskInputVariables());
                assertEquals(1, event.getTask().getTaskData().getTaskInputVariables().size());
                assertTrue(event.getTask().getTaskData().getTaskInputVariables().containsKey("input"));
                
                assertNull(event.getTask().getTaskData().getTaskOutputVariables());
            }

            @Override
            public void afterTaskCompletedEvent(TaskEvent event) {
                assertNotNull(event.getTask().getTaskData().getTaskInputVariables());
                assertEquals(1, event.getTask().getTaskData().getTaskInputVariables().size());
                assertTrue(event.getTask().getTaskData().getTaskInputVariables().containsKey("input"));             
                assertNotNull(event.getTask().getTaskData().getTaskOutputVariables());
                assertEquals(1, event.getTask().getTaskData().getTaskOutputVariables().size());
                assertTrue(event.getTask().getTaskData().getTaskOutputVariables().containsKey("content"));
                
                event.getTaskContext().loadTaskVariables(event.getTask());
                
                assertNotNull(event.getTask().getTaskData().getTaskInputVariables());
                assertEquals(1, event.getTask().getTaskData().getTaskInputVariables().size());
                assertTrue(event.getTask().getTaskData().getTaskInputVariables().containsKey("input"));
                
                assertNotNull(event.getTask().getTaskData().getTaskOutputVariables());                
                assertEquals(1, event.getTask().getTaskData().getTaskOutputVariables().size());
                assertTrue(event.getTask().getTaskData().getTaskOutputVariables().containsKey("content"));
            }

            @Override
            public void afterTaskAddedEvent(TaskEvent event) {
                assertNotNull(event.getTask().getTaskData().getTaskInputVariables());
                assertEquals(1, event.getTask().getTaskData().getTaskInputVariables().size());
                assertTrue(event.getTask().getTaskData().getTaskInputVariables().containsKey("input"));
                
                assertNull(event.getTask().getTaskData().getTaskOutputVariables());
                
                event.getTaskContext().loadTaskVariables(event.getTask());
                
                assertNotNull(event.getTask().getTaskData().getTaskInputVariables());
                assertEquals(1, event.getTask().getTaskData().getTaskInputVariables().size());
                assertTrue(event.getTask().getTaskData().getTaskInputVariables().containsKey("input"));
                
                assertNull(event.getTask().getTaskData().getTaskOutputVariables());
            }
            
        });

    }
    
    @Test
    public void testCompleteAlreadyCompleted() {
       
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";

        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        taskService.start(taskId, "Darth Vader");
        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        // Check is Complete
        taskService.complete(taskId, "Darth Vader", null);
        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.Completed, task2.getTaskData().getStatus());
        assertEquals("Darth Vader", task2.getTaskData().getActualOwner().getId());
        
        try {
            taskService.complete(taskId, "Darth Vader", null);
            Fail.fail("Task should already be completed and thus can't be completed again");
        } catch (PermissionDeniedException e) {
            // expected
        }
    }

    @Test
    public void testCompleteWithMergeOfResultsEmptyAtCompletion() {
        final Map<String, Object> outputsAfterCompletion = new HashMap<String, Object>(); 
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], businessAdministrators = [ new User('Administrator') ],}),";
        str += "name = 'This is my task name' })";
        
        ((EventService<TaskLifeCycleEventListener>)taskService).registerTaskEventListener(new DefaultTaskEventListener(){

            @Override
            public void afterTaskCompletedEvent(TaskEvent event) {
                outputsAfterCompletion.putAll(event.getTask().getTaskData().getTaskOutputVariables());
            }
            
        });

        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();
        taskService.start(taskId, "Darth Vader");

        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("response", "not sure");
        taskService.addContent(taskId, params);

        task1 = taskService.getTaskById(taskId);
        Map<String, Object> outputs = getTaskOutput(task1);
        assertEquals(1, outputs.size());
        assertEquals("not sure", outputs.get("response"));

        params.clear();        
        taskService.complete(taskId, "Darth Vader", params);

        task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Completed, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        outputs = getTaskOutput(task1);
        assertEquals(1, outputs.size());
        assertEquals("not sure", outputs.get("response"));
        
        // now let's check what was actually given to listeners
        assertEquals(1, outputsAfterCompletion.size());
        assertEquals("not sure", outputsAfterCompletion.get("response"));
    }
    
    @Test
    public void testCompleteWithMergeOfResultsOverrideAtCompletion() {
        final Map<String, Object> outputsAfterCompletion = new HashMap<String, Object>(); 
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], businessAdministrators = [ new User('Administrator') ],}),";
        str += "name = 'This is my task name' })";
        
        ((EventService<TaskLifeCycleEventListener>)taskService).registerTaskEventListener(new DefaultTaskEventListener(){

            @Override
            public void afterTaskCompletedEvent(TaskEvent event) {
                outputsAfterCompletion.putAll(event.getTask().getTaskData().getTaskOutputVariables());
            }
            
        });

        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();
        taskService.start(taskId, "Darth Vader");

        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("response", "not sure");
        taskService.addContent(taskId, params);

        task1 = taskService.getTaskById(taskId);
        Map<String, Object> outputs = getTaskOutput(task1);
        assertEquals(1, outputs.size());
        assertEquals("not sure", outputs.get("response"));

        params.clear(); 
        params.put("response", "let's do it");
        taskService.complete(taskId, "Darth Vader", params);

        task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Completed, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        outputs = getTaskOutput(task1);
        assertEquals(1, outputs.size());
        assertEquals("let's do it", outputs.get("response"));
        
        // now let's check what was actually given to listeners
        assertEquals(1, outputsAfterCompletion.size());
        assertEquals("let's do it", outputsAfterCompletion.get("response"));
    }
    
    @Test
    public void testCompleteWithMergeOfResultsOverrideAndAddAtCompletion() {
        final Map<String, Object> outputsAfterCompletion = new HashMap<String, Object>(); 
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], businessAdministrators = [ new User('Administrator') ],}),";
        str += "name = 'This is my task name' })";
        
        ((EventService<TaskLifeCycleEventListener>)taskService).registerTaskEventListener(new DefaultTaskEventListener(){

            @Override
            public void afterTaskCompletedEvent(TaskEvent event) {
                outputsAfterCompletion.putAll(event.getTask().getTaskData().getTaskOutputVariables());
            }
            
        });

        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();
        taskService.start(taskId, "Darth Vader");

        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("response", "not sure");
        taskService.addContent(taskId, params);

        task1 = taskService.getTaskById(taskId);
        Map<String, Object> outputs = getTaskOutput(task1);
        assertEquals(1, outputs.size());
        assertEquals("not sure", outputs.get("response"));

        params.clear(); 
        params.put("response", "let's do it");
        params.put("feedback", "ok");
        taskService.complete(taskId, "Darth Vader", params);

        task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Completed, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        outputs = getTaskOutput(task1);
        assertEquals(2, outputs.size());
        assertEquals("let's do it", outputs.get("response"));
        assertEquals("ok", outputs.get("feedback"));
        
        // now let's check what was actually given to listeners
        assertEquals(2, outputsAfterCompletion.size());
        assertEquals("let's do it", outputsAfterCompletion.get("response"));
        assertEquals("ok", outputsAfterCompletion.get("feedback"));
    }
    
    @Test
    public void testCompleteWithMergeOfResultsNoDataBeforeCompletion() {
        final Map<String, Object> outputsAfterCompletion = new HashMap<String, Object>(); 
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], businessAdministrators = [ new User('Administrator') ],}),";
        str += "name = 'This is my task name' })";
        
        ((EventService<TaskLifeCycleEventListener>)taskService).registerTaskEventListener(new DefaultTaskEventListener(){

            @Override
            public void afterTaskCompletedEvent(TaskEvent event) {
                outputsAfterCompletion.putAll(event.getTask().getTaskData().getTaskOutputVariables());
            }
            
        });

        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();
        taskService.start(taskId, "Darth Vader");

        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());
        
   
        task1 = taskService.getTaskById(taskId);
        Map<String, Object> outputs = getTaskOutput(task1);
        assertEquals(0, outputs.size());   

        Map<String, Object> params = new HashMap<String, Object>();            
        params.put("response", "let's do it");
        params.put("feedback", "ok");
        taskService.complete(taskId, "Darth Vader", params);

        task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Completed, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        outputs = getTaskOutput(task1);
        assertEquals(2, outputs.size());
        assertEquals("let's do it", outputs.get("response"));
        assertEquals("ok", outputs.get("feedback"));
        
        // now let's check what was actually given to listeners
        assertEquals(2, outputsAfterCompletion.size());
        assertEquals("let's do it", outputsAfterCompletion.get("response"));
        assertEquals("ok", outputsAfterCompletion.get("feedback"));
    }
    
    @Test
    public void testCompleteWithMergeOfResults() {
        final Map<String, Object> outputsAfterCompletion = new HashMap<String, Object>(); 
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], businessAdministrators = [ new User('Administrator') ],}),";
        str += "name = 'This is my task name' })";
        
        ((EventService<TaskLifeCycleEventListener>)taskService).registerTaskEventListener(new DefaultTaskEventListener(){

            @Override
            public void afterTaskCompletedEvent(TaskEvent event) {
                Map<String, Object> outs = event.getTask().getTaskData().getTaskOutputVariables();
                if (outs != null) {
                    outputsAfterCompletion.putAll(outs);
                }
            }
            
        });

        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();
        taskService.start(taskId, "Darth Vader");

        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());
        
   
        task1 = taskService.getTaskById(taskId);
        Map<String, Object> outputs = getTaskOutput(task1);
        assertEquals(0, outputs.size());   

        taskService.complete(taskId, "Darth Vader", null);

        task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Completed, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        outputs = getTaskOutput(task1);
        assertEquals(0, outputs.size());
        
        // now let's check what was actually given to listeners
        assertEquals(0, outputsAfterCompletion.size());        
    }
    
    @Test
    public void testDelegateFromReservedWithNotExistingTargetUser() throws Exception {
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";


        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        // Claim and Reserved

        taskService.claim(taskId, "Darth Vader");

        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        // Check was not delegated
        IllegalArgumentException failed = null;
        try {
            taskService.delegate(taskId, "Darth Vader", "not existing");
        } catch (IllegalArgumentException e) {
            failed = e;
        }
        assertNotNull("Should get permissed denied exception", failed);

        Task task2 = taskService.getTaskById(taskId);
        User user = createUser("Darth Vader");
        assertTrue(task2.getPeopleAssignments().getPotentialOwners().contains(user));
        user = createUser("Tony Stark");
        assertFalse(task2.getPeopleAssignments().getPotentialOwners().contains(user));
        assertEquals("Darth Vader", task2.getTaskData().getActualOwner().getId());
        assertEquals(Status.Reserved, task2.getTaskData().getStatus());
    }
    
    protected void testCompleteWithContentAndVarListener(TaskLifeCycleEventListener listener) {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";

        ((EventService<TaskLifeCycleEventListener>)taskService).registerTaskEventListener(listener);
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("input", "simple input");
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, params);
        long taskId = task.getId();
        
        // start task
        taskService.start(taskId, "Darth Vader");
        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        // complete task with output
        params = new HashMap<String, Object>();
        params.put("content", "content");
        taskService.complete(taskId, "Darth Vader", params);
    }
    
    private User createUser(String id) {
        return TaskModelProvider.getFactory().newUser(id);
    }
    
    private Group createGroup(String id) {
        return TaskModelProvider.getFactory().newGroup(id);
    }
    
    protected Map<String, Object> getTaskOutput(Task task) {
        long documentContentId = task.getTaskData().getOutputContentId();
        if (documentContentId > 0) {
            Content contentById = taskService.getContentById(documentContentId);
            if (contentById == null) {
                return new HashMap<String, Object>();
            }            
            
            Object unmarshall = ContentMarshallerHelper.unmarshall(contentById.getContent(), null);
            return (Map<String, Object>) unmarshall;
        }
        return new HashMap<String, Object>();
    }
}
