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
package org.jbpm.task;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jbpm.task.exception.PermissionDeniedException;

import org.jbpm.task.impl.factories.TaskFactory;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.utils.ContentMarshallerHelper;
import org.junit.Test;
import static org.junit.Assert.*;

public abstract class LifeCycleBaseTest extends BaseTest {

    
    @Test
    public void testNewTaskWithNoPotentialOwners() {
        
        
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')  ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = TaskFactory.evalTask(new StringReader(str));

        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        // Task should be assigned to the single potential owner and state set to Reserved


        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.Reserved, task1.getTaskData().getStatus());
        assertEquals("Bobba Fet", task1.getTaskData().getActualOwner().getId());
    }

    @Test
    public void testNewTaskWithContent() {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        ContentData data = ContentMarshallerHelper.marshal("content", null);

        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, data);

        long taskId = task.getId();

        // Task should be assigned to the single potential owner and state set to Reserved


        Task task1 = taskService.getTaskById(taskId);
        assertEquals(AccessType.Inline, task1.getTaskData().getDocumentAccessType());
        assertEquals("java.lang.String", task1.getTaskData().getDocumentType());
        long contentId = task1.getTaskData().getDocumentContentId();
        assertTrue(contentId != -1);



        Content content = taskService.getContentById(contentId);
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        assertEquals("content", unmarshalledObject.toString());
    }

    public void testNewTaskWithMapContent() {
        
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        Map<String, Object> variablesMap = new HashMap<String, Object>();
        variablesMap.put("key1", "value1");
        variablesMap.put("key2", null);
        variablesMap.put("key3", "value3");
        ContentData data = ContentMarshallerHelper.marshal(variablesMap, null);
        
        Task task = ( Task )  TaskFactory.evalTask( new StringReader( str ));
        taskService.addTask( task, data );
        
        long taskId = task.getId();
        
        // Task should be assigned to the single potential owner and state set to Reserved
        
        
        Task task1 = taskService.getTaskById( taskId );
        assertEquals( AccessType.Inline, task1.getTaskData().getDocumentAccessType() );
        assertEquals( "java.util.HashMap", task1.getTaskData().getDocumentType() );
        long contentId = task1.getTaskData().getDocumentContentId();
        assertTrue( contentId != -1 ); 

        
        
        Content content = taskService.getContentById(contentId);
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        if(!(unmarshalledObject instanceof Map)){
            fail("The variables should be a Map");
        
        }
        Map<String, Object> unmarshalledvars = (Map<String, Object>)unmarshalledObject;
        
        assertEquals("value1",unmarshalledvars.get("key1") );
        assertNull(unmarshalledvars.get("key2") );
        assertEquals("value3",unmarshalledvars.get("key3") );
    }
    
    @Test
    public void testNewTaskWithLargeContent() {
        
        

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        String largeContent = "";
        for (int i = 0; i < 1000; i++) {
            largeContent += i + "xxxxxxxxx";
        }

        ContentData data = ContentMarshallerHelper.marshal(largeContent, null);

        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, data);

        long taskId = task.getId();

        // Task should be assigned to the single potential owner and state set to Reserved


        Task task1 = taskService.getTaskById(taskId);
        assertEquals(AccessType.Inline, task1.getTaskData().getDocumentAccessType());
        assertEquals("java.lang.String", task1.getTaskData().getDocumentType());
        long contentId = task1.getTaskData().getDocumentContentId();
        assertTrue(contentId != -1);

        Content content = taskService.getContentById(contentId);
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        assertEquals(largeContent, unmarshalledObject.toString());
    }

    @Test
    public void testClaimWithMultiplePotentialOwners() throws Exception {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'),new User('Darth Vader') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // A Task with multiple potential owners moves to "Ready" state until someone claims it.


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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
    public void testResumeFromReservedWithIncorrectUser() {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ] }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ] }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        // Check is Delegated
        taskService.delegate(taskId, "Darth Vader", "Tony Stark");




        Task task2 = taskService.getTaskById(taskId);
        assertTrue(task2.getPeopleAssignments().getPotentialOwners().contains(new User("Darth Vader")));
        assertTrue(task2.getPeopleAssignments().getPotentialOwners().contains(new User("Tony Stark")));
        assertEquals("Tony Stark", task2.getTaskData().getActualOwner().getId());
        // this was checking for ready, but it should be reserved.. it was an old bug
        assertEquals(Status.Reserved, task2.getTaskData().getStatus());
    }

    @Test
    public void testDelegateFromReserved() throws Exception {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        assertTrue(task2.getPeopleAssignments().getPotentialOwners().contains(new User("Darth Vader")));
        assertTrue(task2.getPeopleAssignments().getPotentialOwners().contains(new User("Tony Stark")));
        assertEquals("Tony Stark", task2.getTaskData().getActualOwner().getId());
        assertEquals(Status.Reserved, task2.getTaskData().getStatus());
    }

    @Test
    public void testDelegateFromReservedWithIncorrectUser() throws Exception {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        assertTrue(task2.getPeopleAssignments().getPotentialOwners().contains(new User("Darth Vader")));
        assertFalse(task2.getPeopleAssignments().getPotentialOwners().contains(new User("Tony Stark")));
        assertEquals("Darth Vader", task2.getTaskData().getActualOwner().getId());
        assertEquals(Status.Reserved, task2.getTaskData().getStatus());
    }

    public void testForwardFromReady() throws Exception {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Check is Forwarded

        taskService.forward(taskId, "Darth Vader", "Tony Stark");


        Task task2 = taskService.getTaskById(taskId);
        assertFalse(task2.getPeopleAssignments().getPotentialOwners().contains("Darth Vader"));
        assertTrue(task2.getPeopleAssignments().getPotentialOwners().contains("Tony Stark"));
        assertNull(task2.getTaskData().getActualOwner());
        assertEquals(Status.Ready, task2.getTaskData().getStatus());
    }

    @Test
    public void testForwardFromReserved() throws Exception {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        assertFalse(task2.getPeopleAssignments().getPotentialOwners().contains(new User("Darth Vader")));
        assertTrue(task2.getPeopleAssignments().getPotentialOwners().contains(new User("Tony Stark")));
        assertNull(task2.getTaskData().getActualOwner());
        assertEquals(Status.Ready, task2.getTaskData().getStatus());
    }

    @Test
    public void testForwardFromReservedWithIncorrectUser() throws Exception {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        assertNotNull("Should get permissed denied exception", denied);



        Task task2 = taskService.getTaskById(taskId);
        assertTrue(task2.getPeopleAssignments().getPotentialOwners().contains(new User("Darth Vader")));
        assertFalse(task2.getPeopleAssignments().getPotentialOwners().contains(new User("Tony Stark")));
        assertEquals("Darth Vader", task2.getTaskData().getActualOwner().getId());
        assertEquals(Status.Reserved, task2.getTaskData().getStatus());
    }

    @Test
    public void testComplete() {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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


        Task task2 = taskService.getTaskById(taskId);
        assertEquals(AccessType.Inline, task2.getTaskData().getOutputAccessType());
        assertEquals("java.util.HashMap", task2.getTaskData().getOutputType());
        long contentId = task2.getTaskData().getOutputContentId();
        assertTrue(contentId != -1);



        Content content = taskService.getContentById(contentId);
        Map<String, Object> unmarshalledObject = (Map<String, Object>) ContentMarshallerHelper.unmarshall(content.getContent(), null);
        assertEquals("content", unmarshalledObject.get("content"));
    }

    @Test
    public void testCompleteWithResults() {
        

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        assertEquals(AccessType.Inline, task2.getTaskData().getOutputAccessType());
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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Go straight from Ready to Inprogress
        taskService.start(taskId, "Darth Vader");

        taskService.getTaskById(taskId);
        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

//        FaultData data = new FaultData();
//        data.setAccessType(AccessType.Inline);
//        data.setType("type");
//        data.setFaultName("faultName");
//        data.setContent("content".getBytes());
        Map<String, Object> faultData = new HashMap<String, Object>();
        faultData.put("faultType", "type");
        faultData.put("faultName", "faultName");
        faultData.put("content", "content");

        taskService.fail(taskId, "Darth Vader", faultData);


        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.Failed, task2.getTaskData().getStatus());
        assertEquals(AccessType.Inline, task2.getTaskData().getFaultAccessType());
        assertEquals("type", task2.getTaskData().getFaultType());
        assertEquals("faultName", task2.getTaskData().getFaultName());
        long contentId = task2.getTaskData().getFaultContentId();
        assertTrue(contentId != -1);



        Content content = taskService.getContentById(contentId);
        Map<String, Object> unmarshalledContent = (Map<String, Object>) ContentMarshallerHelper.unmarshall(content.getContent(), null);
        assertEquals("content", unmarshalledContent.get("content"));
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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ],";
        str += "recipients = [new User('Bobba Fet') ] }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str), null, false);
        // We need to add the Admin if we don't initialize the task
        if (task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        // Do nominate and fail due to Ready status


        List<TaskSummary> myRecipientTasks = taskService.getTasksAssignedAsRecipient("Jabba Hutt", "en-UK");

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
        assertTrue(task1.getPeopleAssignments().getRecipients().contains(new User("Bobba Fet")));
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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [ new User('Darth Vader') ] ,";
        str += " potentialOwners = [ new User('Darth Vader'), new User('Bobba Fet') ] } ),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        Task task = (Task) TaskFactory.evalTask(new StringReader(str), null, false);
        // We need to add the Admin if we don't initialize the task
        if (task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();

        taskService.start(taskId, "Bobba Fet");

        try {
            List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>();
            potentialOwners.add(new User("Bobba Fet"));
            taskService.nominate(taskId, "Darth Vader", potentialOwners);

            fail("Shouldn't be successful");
        } catch (RuntimeException e) { //expected
//        	assertNotNull(nominateHandler.getError());
//        	assertNotNull(nominateHandler.getError().getMessage());
//        	assertTrue(nominateHandler.getError().getMessage().contains("Created"));
        }

        //shouldn't affect the assignments

        Task task1 = taskService.getTaskById(taskId);
        assertTrue(task1.getPeopleAssignments().getPotentialOwners().contains(new User("Darth Vader")));
        assertTrue(task1.getPeopleAssignments().getPotentialOwners().contains(new User("Bobba Fet")));
    }

    @Test
    public void testNominateWithIncorrectUser() {
        

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [ new User('Bobba Fet') ] } ),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();


        try {
            List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>(1);
            potentialOwners.add(new User("Jabba Hutt"));
            taskService.nominate(taskId, "Darth Vader", potentialOwners);

            fail("Shouldn't be successful");
        } catch (RuntimeException e) { //expected
//        	assertNotNull(nominateHandler.getError());
//        	assertNotNull(nominateHandler.getError().getMessage());
//        	assertTrue(nominateHandler.getError().getMessage().contains("Darth Vader"));
        }

        //shouldn't affect the assignments

        Task task1 = taskService.getTaskById(taskId);
        assertTrue(task1.getPeopleAssignments().getBusinessAdministrators().contains(new User("Bobba Fet")));
        assertEquals(task1.getTaskData().getStatus(), Status.Created);
    }

    @Test
    public void testNominateToUser() {
        

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [ new User('Darth Vader'), new User('Bobba Fet') ] } ),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();


        List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>(1);
        potentialOwners.add(new User("Jabba Hutt"));
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
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());

        long taskId = task.getId();


        List<OrganizationalEntity> potentialGroups = new ArrayList<OrganizationalEntity>();
        potentialGroups.add(new Group( "Knights Templer" ));
        taskService.nominate(taskId, "Darth Vader", potentialGroups);


        //shouldn't affect the assignments


        Task task1 = taskService.getTaskById(taskId);
        assertTrue(task1.getPeopleAssignments().getPotentialOwners().contains(new Group("Knights Templer")));
        assertEquals(task1.getTaskData().getStatus(), Status.Ready);
    }

    @Test
    public void testActivate() {
        

        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { ";
        str += "businessAdministrators = [ new User('Darth Vader') ] } ),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str), null, false);
        // We need to add the Admin if we don't initialize the task
        if (task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
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
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ], businessAdministrators = [ new User('admin')] }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) TaskFactory.evalTask(new StringReader(str));
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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('salaboy'), new User('Bobba Fet') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        // Create a local instance of the TaskService

        // Deploy the Task Definition to the Task Component
        taskService.addTask((Task) TaskFactory.evalTask(new StringReader(str)), new HashMap<String, Object>());

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
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('salaboy'), new User('Bobba Fet') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        // Deploy the Task Definition to the Task Component
        taskService.addTask((Task) TaskFactory.evalTask(new StringReader(str)), new HashMap<String, Object>());

        // we don't need to query for our task to see what we will claim, just claim the next one available for us

        taskService.claimNextAvailable("Bobba Fet", "en-UK");


        List<Status> status = new ArrayList<Status>();
        status.add(Status.Ready);
        List<TaskSummary> salaboyTasks = taskService.getTasksAssignedAsPotentialOwnerByStatus("salaboy", status, "en-UK");
        assertEquals(0, salaboyTasks.size());

    }
}
