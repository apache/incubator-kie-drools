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
import java.util.*;

import org.jbpm.task.*;
import org.jbpm.task.identity.DefaultUserGroupCallbackImpl;
import org.jbpm.task.identity.UserGroupCallbackManager;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.*;
import org.jbpm.task.service.responsehandlers.*;
import org.jbpm.task.utils.ContentMarshallerHelper;

public abstract class TaskServiceLifeCycleBaseAsyncTest extends BaseTest {

    private static final int DEFAULT_WAIT_TIME = 5000;

    protected TaskServer server;
    protected AsyncTaskService client;

    public void testNewTaskWithNoPotentialOwners() {
        runTestNewTaskWithNoPotentialOwners(client, users, groups);
    }
    
    protected void tearDown() throws Exception {
        client.disconnect();
        server.stop();
        super.tearDown();
    }
    
    public static void runTestNewTaskWithNoPotentialOwners(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) { 
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();
        
        // Task should remain in Created state with no actual owner
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals( task1.getTaskData().getStatus(), Status.Created );     
        assertNull( task1.getTaskData().getActualOwner() );        
    }

    public void testNewTaskWithSinglePotentialOwner() {
        runTestNewTaskWithSinglePotentialOwner(client, users, groups);
    }
    
    public static void runTestNewTaskWithSinglePotentialOwner(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) { 
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();
        
        // Task should be assigned to the single potential owner and state set to Reserved
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals( Status.Reserved, task1.getTaskData().getStatus() );     
        assertEquals( users.get( "bobba" ), task1.getTaskData().getActualOwner() );
    }
    
    public void testNewTaskWithContent() {
        runTestNewTaskWithContent(client, users, groups);
    }

    public static void runTestNewTaskWithContent(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        ContentData data = ContentMarshallerHelper.marshal("content", null);
        
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, data, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();
        
        // Task should be assigned to the single potential owner and state set to Reserved
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals( AccessType.Inline, task1.getTaskData().getDocumentAccessType() );
        assertEquals( "java.lang.String", task1.getTaskData().getDocumentType() );
        long contentId = task1.getTaskData().getDocumentContentId();
        assertTrue( contentId != -1 ); 

        BlockingGetContentResponseHandler getContentResponseHandler = new BlockingGetContentResponseHandler();
        client.getContent(contentId, getContentResponseHandler);
        Content content = getContentResponseHandler.getContent();
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        assertEquals("content", unmarshalledObject.toString());
    }
    
    public void testNewTaskWithLargeContent() {
        runTestNewTaskWithLargeContent(client, users, groups);
    }

    public static void runTestNewTaskWithLargeContent(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        String largeContent = "";
        for (int i = 0; i < 1000; i++) {
        	largeContent += i + "xxxxxxxxx";
        }
        ContentData data = ContentMarshallerHelper.marshal(largeContent, null);
        
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, data, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();
        
        // Task should be assigned to the single potential owner and state set to Reserved
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals( AccessType.Inline, task1.getTaskData().getDocumentAccessType() );
        assertEquals( "java.lang.String", task1.getTaskData().getDocumentType() );
        long contentId = task1.getTaskData().getDocumentContentId();
        assertTrue( contentId != -1 ); 

        BlockingGetContentResponseHandler getContentResponseHandler = new BlockingGetContentResponseHandler();
        client.getContent(contentId, getContentResponseHandler);
        Content content = getContentResponseHandler.getContent();
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        assertEquals(largeContent, unmarshalledObject.toString());
    }
    
    public void testClaimWithMultiplePotentialOwners() throws Exception {
        runTestClaimWithMultiplePotentialOwners(client, users, groups);
    }

    public static void runTestClaimWithMultiplePotentialOwners(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
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
        
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.claim( taskId, users.get( "darth" ).getId(), responseHandler );        
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task2 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Reserved, task2.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() );
    }

    public void testClaimWithGroupAssignee() throws Exception {
        runTestClaimWithGroupAssignee(client, users, groups);
    }

    public static void runTestClaimWithGroupAssignee(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
    	Properties userGroups = new Properties();
    	
    	userGroups.setProperty(users.get( "darth" ).getId(), "Knights Templer, Dummy Group");
    	UserGroupCallbackManager.getInstance().setCallback(new DefaultUserGroupCallbackImpl(userGroups));
    	
    	Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [groups['knightsTempler' ]], businessAdministrators = [ new User('Administrator') ], }),";                        
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
        
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();

        client.claim( taskId, users.get( "darth" ).getId(), responseHandler );        
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task2 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Reserved, task2.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() );
    }

    public void testStartFromReadyStateWithPotentialOwner() throws Exception {
        runTestStartFromReadyStateWithPotentialOwner(client, users, groups);
    }

    public static void runTestStartFromReadyStateWithPotentialOwner(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
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
        
        // Go straight from Ready to Inprogress
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.start( taskId, users.get( "darth" ).getId(), responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task2 = getTaskResponseHandler.getTask();
        assertEquals(  Status.InProgress, task2.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() );        
    }
    
    public void testStartFromReadyStateWithIncorrectPotentialOwner() {
        runTestStartFromReadyStateWithIncorrectPotentialOwner(client, users, groups);
    }

    public static void runTestStartFromReadyStateWithIncorrectPotentialOwner(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
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
        
        // State should not change as user isn't potential owner
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.start( taskId, users.get( "tony" ).getId(), responseHandler );

        PermissionDeniedException denied = null;
        try {
            responseHandler.waitTillDone( DEFAULT_WAIT_TIME );
        } catch(PermissionDeniedException e) {
            denied = e;
        }

        assertNotNull("Should get permissed denied exception", denied);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task2 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Ready, task2.getTaskData().getStatus() );
        assertNull( task2.getTaskData().getActualOwner() );        
    }    
    
    public void testStartFromReserved() throws Exception {
        runTestStartFromReserved(client, users, groups);
    }

    public static void runTestStartFromReserved(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();
        
        // Task should be assigned to the single potential owner and state set to Reserved
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals( Status.Reserved, task1.getTaskData().getStatus());     
        assertEquals( users.get( "bobba" ), task1.getTaskData().getActualOwner() );
        
        // Should change to InProgress
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.start( taskId, users.get( "bobba" ).getId(), responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task2 = getTaskResponseHandler.getTask();
        assertEquals( Status.InProgress, task2.getTaskData().getStatus() );     
        assertEquals( users.get( "bobba" ), task1.getTaskData().getActualOwner() );        
    }
    
    public void testStartFromReservedWithIncorrectUser() {
        runTestStartFromReservedWithIncorrectUser(client, users, groups);
    }

    public static void runTestStartFromReservedWithIncorrectUser(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();
        
        // Task should be assigned to the single potential owner and state set to Reserved
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals( Status.Reserved , task1.getTaskData().getStatus());     
        assertEquals( users.get( "bobba" ), task1.getTaskData().getActualOwner() );
        
        // Should change not change
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.start( taskId, users.get( "tony" ).getId(), responseHandler );

        PermissionDeniedException denied = null;
        try{
            responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        } catch (PermissionDeniedException e) {
            denied = e;
        }
        assertNotNull("Should get permissed denied exception", denied);

        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task2 = getTaskResponseHandler.getTask();
        assertEquals( Status.Reserved, task2.getTaskData().getStatus() );     
        assertEquals( users.get( "bobba" ), task1.getTaskData().getActualOwner() );        
    }
    
    public void testStop() {
        runTestStop(client, users, groups);
    }

    public static void runTestStop(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();             
        
        // Go straight from Ready to Inprogress
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.start( taskId, users.get( "darth" ).getId(), responseHandler );        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals(  Status.InProgress, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );        
        
        // Now Stop
        responseHandler = new BlockingTaskOperationResponseHandler();
        client.stop( taskId, users.get( "darth" ).getId(), responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task2 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Reserved, task2.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() );                
    }    
    
    public void testStopWithIncorrectUser() {
        runTestStopWithIncorrectUser(client, users, groups);
    }

    public static void runTestStopWithIncorrectUser(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();             
        
        // Go straight from Ready to Inprogress
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.start( taskId, users.get( "darth" ).getId(), responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals(  Status.InProgress, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );        
        
        // Should not stop
        responseHandler = new BlockingTaskOperationResponseHandler();
        client.stop( taskId, users.get( "bobba" ).getId(), responseHandler );
        PermissionDeniedException denied = null;
        try{
            responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        } catch(PermissionDeniedException e) {
            denied = e;
        }
        assertNotNull("Should get permissed denied exception", denied);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task2 = getTaskResponseHandler.getTask();
        assertEquals(  Status.InProgress, task2.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() );                
    }   
    
    public void testReleaseFromInprogress() throws Exception {
        runTestReleaseFromInprogress(client, users, groups);
    }

    public static void runTestReleaseFromInprogress(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();             
        
        // Go straight from Ready to Inprogress
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.start( taskId, users.get( "darth" ).getId(), responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals(  Status.InProgress, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Check is Released
        responseHandler = new BlockingTaskOperationResponseHandler();
        client.release( taskId, users.get( "darth" ).getId(), responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task2 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Ready, task2.getTaskData().getStatus() );
        assertNull( task2.getTaskData().getActualOwner() );                  
    }    
    
    public void testReleaseFromReserved() {
        runTestReleaseFromReserved(client, users, groups);
    }

    public static void runTestReleaseFromReserved(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();             
        
        // Go straight from Ready to Inprogress
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.claim( taskId, users.get( "darth" ).getId(), responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Reserved, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Check is Released
        responseHandler = new BlockingTaskOperationResponseHandler();
        client.release( taskId, users.get( "darth" ).getId(), responseHandler );  
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task2 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Ready, task2.getTaskData().getStatus() );
        assertNull( task2.getTaskData().getActualOwner() );                  
    }     
    
    public void testReleaseWithIncorrectUser() {
        runTestReleaseWithIncorrectUser(client, users, groups);
    }

    public static void runTestReleaseWithIncorrectUser(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();             
        
        // Go straight from Ready to Inprogress
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.claim( taskId, users.get( "darth" ).getId(), responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Reserved, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Check is not changed
        responseHandler = new BlockingTaskOperationResponseHandler();
        client.release( taskId, users.get( "bobba" ).getId(), responseHandler );
        PermissionDeniedException denied = null;
        try{
            responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        } catch(PermissionDeniedException e) {
            denied = e;
        }
        assertNotNull("Should get permissed denied exception", denied);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task2 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Reserved, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() ); 
    }

    public void testSuspendFromReady() {
        runTestSuspendFromReady(client, users, groups);
    }

    public static void runTestSuspendFromReady(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();             
        
        // Check is Ready
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Ready, task1.getTaskData().getStatus() );
        assertNull( task1.getTaskData().getActualOwner() );  
        
        // Check is Suspended
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.suspend( taskId, users.get( "darth" ).getId(), responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task2 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Suspended, task2.getTaskData().getStatus() );
        assertEquals( Status.Ready, task2.getTaskData().getPreviousStatus() );
        assertNull( task1.getTaskData().getActualOwner() );                  
    }
    
    
    public void testSuspendFromReserved() {
        runTestSuspendFromReserved(client, users, groups);
    }

    public static void runTestSuspendFromReserved(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();             
        
        // Check is Reserved
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.claim( taskId, users.get( "darth" ).getId(), responseHandler );   
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Reserved, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Check is Suspended
        responseHandler = new BlockingTaskOperationResponseHandler();
        client.suspend( taskId, users.get( "darth" ).getId(), responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task2 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Reserved, task2.getTaskData().getPreviousStatus() );
        assertEquals(  Status.Suspended, task2.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() );          
    }    
    
    public void testSuspendFromReservedWithIncorrectUser() {
        runtestSuspendFromReservedWithIncorrectUser(client, users, groups);
    }

    public static void runtestSuspendFromReservedWithIncorrectUser(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();             
        
        // Check is Reserved
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.claim( taskId, users.get( "darth" ).getId(), responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Reserved, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Check is not changed
        responseHandler = new BlockingTaskOperationResponseHandler();
        client.suspend( taskId, users.get( "bobba" ).getId(), responseHandler );
        PermissionDeniedException denied = null;
        try{
            responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        } catch(PermissionDeniedException e) {
            denied = e;
        }
        assertNotNull("Should get permissed denied exception", denied);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task2 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Reserved, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );      
    }    
    
    public void testResumeFromReady() {
        runTestResumeFromReady(client, users, groups);
    }

    public static void runTestResumeFromReady(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();             
        
        // Check is Ready
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Ready, task1.getTaskData().getStatus() );
        assertNull( task1.getTaskData().getActualOwner() );  
        
        // Check is Suspended
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.suspend( taskId, users.get( "darth" ).getId(), responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task2 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Suspended, task2.getTaskData().getStatus() );
        assertEquals( Status.Ready, task2.getTaskData().getPreviousStatus() );
        assertNull( task1.getTaskData().getActualOwner() );    
        
        // Check is Resumed
        responseHandler = new BlockingTaskOperationResponseHandler();
        client.resume( taskId, users.get( "darth" ).getId(), responseHandler );   
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task3 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Ready, task3.getTaskData().getStatus() );
        assertEquals( Status.Suspended, task3.getTaskData().getPreviousStatus() );
        assertNull( task3.getTaskData().getActualOwner() );         
    }
    
    public void testResumeFromReserved() {
        runTestResumeFromReserved(client, users, groups);
    }

    public static void runTestResumeFromReserved(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();             
        
        // Check is Reserved
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.claim( taskId, users.get( "darth" ).getId(), responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Reserved, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Check is suspended
        responseHandler = new BlockingTaskOperationResponseHandler();
        client.suspend( taskId, users.get( "darth" ).getId(), responseHandler );        
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task2 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Reserved, task2.getTaskData().getPreviousStatus() );
        assertEquals(  Status.Suspended, task2.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() ); 
        
        // Check is Resumed
        responseHandler = new BlockingTaskOperationResponseHandler();
        client.resume( taskId, users.get( "darth" ).getId(), responseHandler ); 
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task3 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Reserved, task3.getTaskData().getStatus() );
        assertEquals( Status.Suspended, task3.getTaskData().getPreviousStatus() );
        assertEquals( users.get( "darth" ), task3.getTaskData().getActualOwner() );           
    }    
    
    public void testResumeFromReservedWithIncorrectUser() {
        runTestResumeFromReservedWithIncorrectUser(client, users, groups);
    }

    public static void runTestResumeFromReservedWithIncorrectUser(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);    
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();             
        
        // Check is Reserved
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.claim( taskId, users.get( "darth" ).getId(), responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Reserved, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Check not changed
        responseHandler = new BlockingTaskOperationResponseHandler();
        client.suspend( taskId, users.get( "bobba" ).getId(), responseHandler );
        PermissionDeniedException denied = null;
        try{
            responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        } catch(PermissionDeniedException e) {
            denied = e;
        }
        assertNotNull("Should get permissed denied exception", denied);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task2 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Reserved, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );      
    }     
          
    public void testSkipFromReady() {
        runTestSkipFromReady(client, users, groups);
    }

    public static void runTestSkipFromReady(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = true} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ] }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();                     
        
        // Check is Complete
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.skip( taskId, users.get( "darth" ).getId(), responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Obsolete, task1.getTaskData().getStatus() );
        assertNull(  task1.getTaskData().getActualOwner() );                  
    }    
    
    
    public void testSkipFromReserved() {
        runTestSkipFromReserved(client, users, groups);
    }

    public static void runTestSkipFromReserved(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = true} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ] }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();             
        
        // Go straight from Ready 
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.claim( taskId, users.get( "darth" ).getId(), responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        // Check is Complete
        responseHandler = new BlockingTaskOperationResponseHandler();
        client.skip( taskId, users.get( "darth" ).getId(), responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Obsolete, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );                  
    }     
    
    public void testDelegateFromReady() throws Exception {
        runTestDelegateFromReady(client, users, groups);
    }

    public static void runTestDelegateFromReady(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );        
        long taskId = addTaskResponseHandler.getTaskId();                     
        
        // Check is Delegated
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.delegate( taskId, users.get( "darth" ).getId(), users.get( "tony" ).getId(), responseHandler );    
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );        
        Task task2 = getTaskResponseHandler.getTask();
        assertTrue( task2.getPeopleAssignments().getPotentialOwners().contains( users.get( "darth" ) ) );
        assertTrue( task2.getPeopleAssignments().getPotentialOwners().contains( users.get( "tony" ) ) );
        assertEquals( users.get( "tony" ), task2.getTaskData().getActualOwner() );
        assertEquals(  Status.Ready, task2.getTaskData().getStatus() );             
    }     
    
    public void testDelegateFromReserved() throws Exception {
        runTestDelegateFromReserved(client, users, groups);
    }

    public static void runTestDelegateFromReserved(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();             
        
        // Claim and Reserved
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.claim( taskId, users.get( "darth" ).getId(), responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Reserved, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Check is Delegated
        responseHandler = new BlockingTaskOperationResponseHandler();
        client.delegate( taskId, users.get( "darth" ).getId(), users.get( "tony" ).getId(), responseHandler );    
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );        
        Task task2 = getTaskResponseHandler.getTask();
        assertTrue( task2.getPeopleAssignments().getPotentialOwners().contains( users.get( "darth" ) ) );
        assertTrue( task2.getPeopleAssignments().getPotentialOwners().contains( users.get( "tony" ) ) );
        assertEquals( users.get( "tony" ), task2.getTaskData().getActualOwner() );
        assertEquals(  Status.Ready, task2.getTaskData().getStatus() );             
    }     
    
    public void testDelegateFromReservedWithIncorrectUser() throws Exception {
        runTestDelegateFromReservedWithIncorrectUser(client, users, groups);
    }

    public static void runTestDelegateFromReservedWithIncorrectUser(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );        
        long taskId = addTaskResponseHandler.getTaskId();             
        
        // Claim and Reserved
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.claim( taskId, users.get( "darth" ).getId(), responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Reserved, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Check was not delegated
        responseHandler = new BlockingTaskOperationResponseHandler();
        client.delegate( taskId, users.get( "bobba" ).getId(), users.get( "tony" ).getId(), responseHandler );    
        PermissionDeniedException denied = null;
        try{
            responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        } catch(PermissionDeniedException e) {
            denied = e;
        }
        assertNotNull("Should get permissed denied exception", denied);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );        
        Task task2 = getTaskResponseHandler.getTask();
        assertTrue( task2.getPeopleAssignments().getPotentialOwners().contains( users.get( "darth" ) ) );
        assertFalse( task2.getPeopleAssignments().getPotentialOwners().contains( users.get( "tony" ) ) );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() );
        assertEquals(  Status.Reserved, task2.getTaskData().getStatus() );             
    }  
    
    public void testForwardFromReady() throws Exception {
        runTestForwardFromReady(client, users, groups);
    }

    public static void runTestForwardFromReady(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();                     
        
        // Check is Forwarded
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.forward( taskId, users.get( "darth" ).getId(), users.get( "tony" ).getId(), responseHandler );    
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );        
        Task task2 = getTaskResponseHandler.getTask();
        assertFalse( task2.getPeopleAssignments().getPotentialOwners().contains( users.get( "darth" ) ) );
        assertTrue( task2.getPeopleAssignments().getPotentialOwners().contains( users.get( "tony" ) ) );
        assertNull( task2.getTaskData().getActualOwner() );
        assertEquals(  Status.Ready, task2.getTaskData().getStatus() );             
    }  
    
    public void testForwardFromReserved() throws Exception {
        runTestForwardFromReserved(client, users, groups);
    }

    public static void runTestForwardFromReserved(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();             
        
        // Claim and Reserved
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.claim( taskId, users.get( "darth" ).getId(), responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Reserved, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Check is Delegated
        responseHandler = new BlockingTaskOperationResponseHandler();
        client.forward( taskId, users.get( "darth" ).getId(), users.get( "tony" ).getId(), responseHandler );    
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );        
        Task task2 = getTaskResponseHandler.getTask();
        assertFalse( task2.getPeopleAssignments().getPotentialOwners().contains( users.get( "darth" ) ) );
        assertTrue( task2.getPeopleAssignments().getPotentialOwners().contains( users.get( "tony" ) ) );
        assertNull( task2.getTaskData().getActualOwner() );
        assertEquals(  Status.Ready, task2.getTaskData().getStatus() );             
    }     
    
    public void testForwardFromReservedWithIncorrectUser() throws Exception {
        runTestForwardFromReservedWithIncorrectUser(client, users, groups);
    }

    public static void runTestForwardFromReservedWithIncorrectUser(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
            
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();             
        
        // Claim and Reserved
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.claim( taskId, users.get( "darth" ).getId(), responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Reserved, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Check was not delegated
        responseHandler = new BlockingTaskOperationResponseHandler();
        client.forward( taskId, users.get( "bobba" ).getId(), users.get( "tony" ).getId(), responseHandler );    
        PermissionDeniedException denied = null;
        try{
            responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        } catch(PermissionDeniedException e) {
            denied = e;
        }
        assertNotNull("Should get permissed denied exception", denied);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );        
        Task task2 = getTaskResponseHandler.getTask();
        assertTrue( task2.getPeopleAssignments().getPotentialOwners().contains( users.get( "darth" ) ) );
        assertFalse( task2.getPeopleAssignments().getPotentialOwners().contains( users.get( "tony" ) ) );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() );
        assertEquals(  Status.Reserved, task2.getTaskData().getStatus() );             
    }      
    
    public void testComplete() {
        runTestComplete(client, users, groups);
    }

    public static void runTestComplete(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();             
        
        // Go straight from Ready to Inprogress
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.start( taskId, users.get( "darth" ).getId(), responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals(  Status.InProgress, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Check is Complete
        responseHandler = new BlockingTaskOperationResponseHandler();
        client.complete( taskId, users.get( "darth" ).getId(), null, responseHandler ); 
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task2 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Completed, task2.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() );                  
    }
        
    public void testCompleteWithIncorrectUser() {
        runTestCompleteWithIncorrectUser(client, users, groups);
    }

    public static void runTestCompleteWithIncorrectUser(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();             
        
        // Go straight from Ready to Inprogress
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.start( taskId, users.get( "darth" ).getId(), responseHandler );        
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals(  Status.InProgress, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Should not complete as wrong user
        responseHandler = new BlockingTaskOperationResponseHandler();
        client.complete( taskId, users.get( "bobba" ).getId(), null, responseHandler );  
        PermissionDeniedException denied = null;
        try{
            responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        } catch(PermissionDeniedException e) {
            denied = e;
        }
        assertNotNull("Should get permissed denied exception", denied);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task2 = getTaskResponseHandler.getTask();
        assertEquals(  Status.InProgress, task2.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() );                  
    }    

    public void testCompleteWithContent() {
        runTestCompleteWithContent(client, users, groups);
    }

    public static void runTestCompleteWithContent(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();             
        
        // Go straight from Ready to Inprogress
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.start( taskId, users.get( "darth" ).getId(), responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals(  Status.InProgress, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        ContentData data = ContentMarshallerHelper.marshal("content", null);
        responseHandler = new BlockingTaskOperationResponseHandler();
        client.complete( taskId, users.get( "darth" ).getId(), data, responseHandler ); 
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task2 = getTaskResponseHandler.getTask();
        assertEquals( AccessType.Inline, task2.getTaskData().getOutputAccessType() );
        assertEquals( "java.lang.String", task2.getTaskData().getOutputType() );
        long contentId = task2.getTaskData().getOutputContentId();
        assertTrue( contentId != -1 ); 
        
        BlockingGetContentResponseHandler getContentResponseHandler = new BlockingGetContentResponseHandler();
        client.getContent(contentId, getContentResponseHandler);
        Content content = getContentResponseHandler.getContent();
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        assertEquals("content", unmarshalledObject.toString());
    }
    
    public void testCompleteWithResults() {
        runTestFail(client, users, groups);
    }

    public static void runTestCompleteWithResults(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();             
        
        // Go straight from Ready to Inprogress
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.start( taskId, users.get( "darth" ).getId(), responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals(  Status.InProgress, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        responseHandler = new BlockingTaskOperationResponseHandler();
        client.completeWithResults( taskId, users.get( "darth" ).getId(), "content", responseHandler ); 
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task2 = getTaskResponseHandler.getTask();
        assertEquals( AccessType.Inline, task2.getTaskData().getOutputAccessType() );
        assertEquals( "java.lang.String", task2.getTaskData().getOutputType() );
        long contentId = task2.getTaskData().getOutputContentId();
        assertTrue( contentId != -1 ); 
        
        BlockingGetContentResponseHandler getContentResponseHandler = new BlockingGetContentResponseHandler();
        client.getContent(contentId, getContentResponseHandler);
        Content content = getContentResponseHandler.getContent();
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        assertEquals("content", unmarshalledObject.toString());
    }
    
    public void testFail() {
        runTestFail(client, users, groups);
    }

    public static void runTestFail(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();             
        
        // Go straight from Ready to Inprogress
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.start( taskId, users.get( "darth" ).getId(), responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals(  Status.InProgress, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Check is Failed
        responseHandler = new BlockingTaskOperationResponseHandler();
        client.fail( taskId, users.get( "darth" ).getId(), null, responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task2 = getTaskResponseHandler.getTask();
        assertEquals(  Status.Failed, task2.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() );                  
    }
    
    public void testFailWithIncorrectUser() {
        runTestFailWithIncorrectUser(client, users, groups);
    }

    public static void runTestFailWithIncorrectUser(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();             
        
        // Go straight from Ready to Inprogress
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.start( taskId, users.get( "darth" ).getId(), responseHandler );      
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals(  Status.InProgress, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Should not fail as wrong user
        responseHandler = new BlockingTaskOperationResponseHandler();
        client.fail( taskId, users.get( "bobba" ).getId(), null, responseHandler );
        PermissionDeniedException denied = null;
        try{
            responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        } catch(PermissionDeniedException e) {
            denied = e;
        }
        assertNotNull("Should get permissed denied exception", denied);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task2 = getTaskResponseHandler.getTask();
        assertEquals(  Status.InProgress, task2.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() );                  
    }    

    public void testFailWithContent() {
        runTestFailWithContent(client, users, groups);
    }

    public static void runTestFailWithContent(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();             
        
        // Go straight from Ready to Inprogress
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.start( taskId, users.get( "darth" ).getId(), responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals(  Status.InProgress, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        FaultData data = new FaultData();
        data.setAccessType(AccessType.Inline);
        data.setType("type");
        data.setFaultName("faultName");
        data.setContent("content".getBytes());
        responseHandler = new BlockingTaskOperationResponseHandler();
        client.fail( taskId, users.get( "darth" ).getId(), data, responseHandler );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task2 = getTaskResponseHandler.getTask();
        assertEquals( Status.Failed, task2.getTaskData().getStatus() );
        assertEquals( AccessType.Inline, task2.getTaskData().getFaultAccessType() );
        assertEquals( "type", task2.getTaskData().getFaultType() );
        assertEquals( "faultName", task2.getTaskData().getFaultName() );
        long contentId = task2.getTaskData().getFaultContentId();
        assertTrue( contentId != -1 ); 
        
        BlockingGetContentResponseHandler getContentResponseHandler = new BlockingGetContentResponseHandler();
        client.getContent(contentId, getContentResponseHandler);
        Content content = getContentResponseHandler.getContent();
        assertEquals("content", new String(content.getContent()));
    }
    
    public void testRegisterRemove() throws Exception {
        runTestRegisterRemove(client, users, groups);
    }

    public static void runTestRegisterRemove(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) throws Exception {
        Map<String, Object> vars = fillVariables(users, groups);
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba'], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();             
        
        BlockingTaskOperationResponseHandler opResponseHandler = new BlockingTaskOperationResponseHandler();
        client.register(taskId, users.get("bobba").getId(), opResponseHandler);
        opResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        Thread.sleep(500);
        
        BlockingGetTaskResponseHandler responseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, responseHandler);
        Task task1 = responseHandler.getTask();
        List<OrganizationalEntity> myRecipientTasks = task1.getPeopleAssignments().getRecipients();
        
        assertNotNull(myRecipientTasks);
        assertEquals(1, myRecipientTasks.size());
        assertTrue(task1.getPeopleAssignments().getRecipients().contains(users.get("bobba")));
        
        BlockingTaskOperationResponseHandler removeHandler = new BlockingTaskOperationResponseHandler();
        client.remove(taskId, users.get("bobba").getId(), removeHandler);
        removeHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        Thread.sleep(500);
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task2 = getTaskResponseHandler.getTask();
        assertFalse(task2.getPeopleAssignments().getRecipients().contains(users.get("bobba")));
    }
    
    public void testRemoveNotInRecipientList() {
        runTestRemoveNotInRecipientList(client, users, groups);
    }

    public static void runTestRemoveNotInRecipientList(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { status = Status.Ready } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ] ],";
        str += "recipients = [users['bobba'] ] }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();             
        
        // Do nominate and fail due to Ready status
        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsRecipient(users.get("jabba").getId(), "en-UK", responseHandler);
        List<TaskSummary> myRecipientTasks = responseHandler.getResults();
        
        assertNotNull(myRecipientTasks);
        assertEquals(0, myRecipientTasks.size());
        
        responseHandler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner(users.get("jabba").getId(), "en-UK", responseHandler);
        List<TaskSummary> myPotentialTasks = responseHandler.getResults();
        
        assertNotNull(myPotentialTasks);
        assertEquals(0, myPotentialTasks.size());
        
        BlockingTaskOperationResponseHandler removeHandler = new BlockingTaskOperationResponseHandler();
       	try {
       		client.remove(taskId, users.get("jabba").getId(), removeHandler);
       		removeHandler.waitTillDone(DEFAULT_WAIT_TIME);
       		fail("Shouldn't be successful");
        } catch (RuntimeException e) { //expected
        }
        
        //shouldn't affect the assignments
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertTrue(task1.getPeopleAssignments().getRecipients().contains(users.get("bobba")));
    }
    
    /**
     * Nominate an organization entity to process the task. If it is nominated to one person
     * then the new state of the task is Reserved. If it is nominated to several people then 
     * the new state of the task is Ready. This can only be performed when the task is in the 
     * state Created.
     */
    public void testNominateOnOtherThanCreated() {
        runTestNominateOnOtherThanCreated(client, users, groups);
    }

    public static void runTestNominateOnOtherThanCreated(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { status = Status.Ready } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [ users['darth'] ] ,";
        str += " potentialOwners = [ users['darth'], users['bobba'] ] } ),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();
        
        BlockingTaskOperationResponseHandler startResponseHandler = new BlockingTaskOperationResponseHandler();
        client.start( taskId, users.get("bobba").getId(), startResponseHandler);
        startResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        BlockingTaskOperationResponseHandler nominateHandler = new BlockingTaskOperationResponseHandler();
       	try {
       		List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>();
       		potentialOwners.add(users.get("bobba"));
       		client.nominate(taskId, users.get("darth").getId(), potentialOwners, nominateHandler);
       		nominateHandler.waitTillDone(DEFAULT_WAIT_TIME);
       		fail("Shouldn't be successful");
        } catch (RuntimeException e) { //expected
        	assertNotNull(nominateHandler.getError());
        	assertNotNull(nominateHandler.getError().getMessage());
            String somethingAboutCreated = "Created";
            String errorMessage = nominateHandler.getError().getMessage();
            assertTrue("Error message does not contain '" + somethingAboutCreated + "' : " + errorMessage, 
                    errorMessage.contains(somethingAboutCreated));
        }
        
        //shouldn't affect the assignments
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertTrue(task1.getPeopleAssignments().getPotentialOwners().contains(users.get("darth")));
        assertTrue(task1.getPeopleAssignments().getPotentialOwners().contains(users.get("bobba")));
    }
    
    public void testNominateWithIncorrectUser() {
        runTestNominateWithIncorrectUser(client, users, groups);
    }

    public static void runTestNominateWithIncorrectUser(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [ users['bobba'] ] } ),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();
        
        BlockingTaskOperationResponseHandler nominateHandler = new BlockingTaskOperationResponseHandler();
       	try {
       		List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>(1);
       		potentialOwners.add(users.get("jabba"));
       		client.nominate(taskId, users.get("darth").getId(), potentialOwners, nominateHandler);
       		nominateHandler.waitTillDone(DEFAULT_WAIT_TIME);
       		fail("Shouldn't be successful");
        } catch (RuntimeException e) { //expected
        	assertNotNull(nominateHandler.getError());
        	assertNotNull(nominateHandler.getError().getMessage());
        	assertTrue(nominateHandler.getError().getMessage().contains(users.get("darth").getId()));
        }
        
        //shouldn't affect the assignments
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertTrue(task1.getPeopleAssignments().getBusinessAdministrators().contains(users.get("bobba")));
        assertEquals(task1.getTaskData().getStatus(), Status.Created);
    }
    
    public void testNominateToUser() {
        runTestNominateToUser(client, users, groups);
    }

    public static void runTestNominateToUser(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [ users['darth'], users['bobba'] ] } ),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();
        
        BlockingTaskOperationResponseHandler nominateHandler = new BlockingTaskOperationResponseHandler();
        List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>(1);
        potentialOwners.add(users.get("jabba"));
   		client.nominate(taskId, users.get("darth").getId(), potentialOwners, nominateHandler);
   		nominateHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        //shouldn't affect the assignments
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals(task1.getTaskData().getActualOwner(), users.get("jabba"));
        assertEquals(task1.getTaskData().getStatus(), Status.Reserved);
    }
    
    public void testNominateToGroup() {
        runTestNominateToGroup(client, users, groups);
    }

    public static void runTestNominateToGroup(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [ users['darth'], users['bobba'] ] } ),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();
        
        BlockingTaskOperationResponseHandler nominateHandler = new BlockingTaskOperationResponseHandler();
        List<OrganizationalEntity> potentialGroups = new ArrayList<OrganizationalEntity>();
        potentialGroups.add(groups.get("knightsTempler"));
   		client.nominate(taskId, users.get("darth").getId(), potentialGroups, nominateHandler);
   		nominateHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        //shouldn't affect the assignments
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertTrue(task1.getPeopleAssignments().getPotentialOwners().contains(groups.get("knightsTempler")));
        assertEquals(task1.getTaskData().getStatus(), Status.Ready);
    }
    
    public void testActivate() {
        runTestActivate(client, users, groups);
    }

    public static void runTestActivate(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { ";
        str += "businessAdministrators = [ users['darth'] ] } ),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();

        BlockingTaskOperationResponseHandler activateResponseHandler = new BlockingTaskOperationResponseHandler();
        client.activate(taskId, users.get("darth").getId(), activateResponseHandler);
        activateResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);

        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, getTaskResponseHandler);
        Task task1 = getTaskResponseHandler.getTask();
        
        assertEquals(task1.getTaskData().getStatus(), Status.Ready);
        assertFalse(task1.equals(task));
    }
    
    public void testActivateWithIncorrectUser() {
        runTestActivateWithIncorrectUser(client, users, groups);
    }

    public static void runTestActivateWithIncorrectUser(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [ users['darth'], users['bobba'] ], ";
        str += "businessAdministrators = [ users['jabba'] ] } ),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();

        BlockingTaskOperationResponseHandler activateResponseHandler = new BlockingTaskOperationResponseHandler();
        try {
        	client.activate(taskId, users.get("darth").getId(), activateResponseHandler);
        	activateResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        	fail("Shouldn't have succeded");
    	} catch (RuntimeException e) {
        	assertNotNull(activateResponseHandler.getError());
        	assertNotNull(activateResponseHandler.getError().getMessage());
        	assertTrue(activateResponseHandler.getError().getMessage().toLowerCase().contains("status"));
        }

    }
    
    public void testActivateFromIncorrectStatus() {
        runTestActivateFromIncorrectStatus(client, users, groups);
    }

    public static void runTestActivateFromIncorrectStatus(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { status = Status.Ready } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [ users['darth'], users['bobba'] ], ";
        str += "businessAdministrators = [ users['jabba'] ] } ),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();

        BlockingTaskOperationResponseHandler activateResponseHandler = new BlockingTaskOperationResponseHandler();
        try {
        	client.activate(taskId, users.get("darth").getId(), activateResponseHandler);
        	activateResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        	fail("Shouldn't have succeded");
    	} catch (RuntimeException e) {
        	assertNotNull(activateResponseHandler.getError());
        	assertNotNull(activateResponseHandler.getError().getMessage());
        	assertTrue(activateResponseHandler.getError().getMessage().contains(users.get("darth").getId()));
        }
    }
    
    public void testExitFromReady() {
        runTestExitFromReady(client, users, groups);
    }

    public static void runTestExitFromReady(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = false} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], businessAdministrators = [ users['admin']] }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        BlockingGetTaskResponseHandler taskResponseHandler = new BlockingGetTaskResponseHandler();
        long taskId = addTaskResponseHandler.getTaskId();   
        client.getTask(taskId, taskResponseHandler);
        task = taskResponseHandler.getTask();
        assertEquals(Status.Ready, task.getTaskData().getStatus());
        
        BlockingTaskOperationResponseHandler activateResponseHandler = new BlockingTaskOperationResponseHandler();
        client.exit( taskId, users.get( "admin" ).getId(), activateResponseHandler);
        
        taskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, taskResponseHandler);
        task = taskResponseHandler.getTask();
        assertEquals(  Status.Exited, task.getTaskData().getStatus() );               
    }  
    
    public void testExitFromReserved() {
        runTestExitFromReserved(client, users, groups);
    }

    public static void runTestExitFromReserved(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = false} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba'] ], businessAdministrators = [ users['admin']] }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();
        BlockingGetTaskResponseHandler taskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, taskResponseHandler);
        task = taskResponseHandler.getTask();
        assertEquals(Status.Reserved, task.getTaskData().getStatus());
        
        BlockingTaskOperationResponseHandler activateResponseHandler = new BlockingTaskOperationResponseHandler();
        client.exit( taskId, users.get( "admin" ).getId(), activateResponseHandler);
        
        taskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, taskResponseHandler);
        task = taskResponseHandler.getTask();
        assertEquals(  Status.Exited, task.getTaskData().getStatus() );                
    }  
    
    public void testExitFromInProgress() {
        runTestExitFromInProgress(client, users, groups);
    }

    public static void runTestExitFromInProgress(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = false} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba'] ], businessAdministrators = [ users['admin']] }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();
        BlockingGetTaskResponseHandler taskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, taskResponseHandler);
        task = taskResponseHandler.getTask();
        assertEquals(Status.Reserved, task.getTaskData().getStatus());
        
        BlockingTaskOperationResponseHandler startResponseHandler = new BlockingTaskOperationResponseHandler();
        client.start(taskId, users.get("bobba").getId(), startResponseHandler);
        taskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, taskResponseHandler);
        task = taskResponseHandler.getTask();
        assertEquals(Status.InProgress, task.getTaskData().getStatus());
        
        BlockingTaskOperationResponseHandler activateResponseHandler = new BlockingTaskOperationResponseHandler();
        client.exit( taskId, users.get( "admin" ).getId(), activateResponseHandler);
        
        taskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, taskResponseHandler);
        task = taskResponseHandler.getTask();
        assertEquals(  Status.Exited, task.getTaskData().getStatus() );                
    }  

    public void testExitFromSuspended() {
        runTestExitFromSuspended(client, users, groups);
    }

    public static void runTestExitFromSuspended(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = false} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba'] ], businessAdministrators = [ users['admin']] }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();   
        BlockingGetTaskResponseHandler taskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, taskResponseHandler);
        task = taskResponseHandler.getTask();
        assertEquals(Status.Reserved, task.getTaskData().getStatus());
        
        BlockingTaskOperationResponseHandler suspendResponseHandler = new BlockingTaskOperationResponseHandler();
        client.suspend(taskId, users.get("bobba").getId(), suspendResponseHandler);
        
        taskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, taskResponseHandler);
        task = taskResponseHandler.getTask();
        assertEquals(Status.Suspended, task.getTaskData().getStatus());
        
        BlockingTaskOperationResponseHandler activateResponseHandler = new BlockingTaskOperationResponseHandler();
        client.exit( taskId, users.get( "admin" ).getId(), activateResponseHandler);
        
        taskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, taskResponseHandler);
        task = taskResponseHandler.getTask();
        assertEquals(  Status.Exited, task.getTaskData().getStatus() );                
    }
    
    public void testExitPermissionDenied() {
        runTestExitPermissionDenied(client, users, groups);
    }

    public static void runTestExitPermissionDenied(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map<String, Object> vars = fillVariables(users, groups);
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = false} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], businessAdministrators = [ users['admin']] }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();    
        BlockingGetTaskResponseHandler taskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, taskResponseHandler);
        task = taskResponseHandler.getTask();
        assertEquals(Status.Ready, task.getTaskData().getStatus());
        
        try {
            BlockingTaskOperationResponseHandler activateResponseHandler = new BlockingTaskOperationResponseHandler();
            client.exit( taskId, users.get( "darth" ).getId(), activateResponseHandler);
            activateResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
            fail("Non admin user can't exit a task");
        } catch (PermissionDeniedException e) {
           
        }
        taskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, taskResponseHandler);
        task = taskResponseHandler.getTask();
        assertEquals(  Status.Ready, task.getTaskData().getStatus() );               
    } 
    
    public void testExitNotAvailableToUsers() {
        runTestExitNotAvailableToUsers(client, users, groups);
    }

    public static void runTestExitNotAvailableToUsers(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map <String, Object> vars = fillVariables(users, groups);
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = false} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba']], businessAdministrators = [ users['admin']] }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();     
        BlockingGetTaskResponseHandler taskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, taskResponseHandler);
        task = taskResponseHandler.getTask();
        assertEquals(Status.Reserved, task.getTaskData().getStatus());
        
        BlockingTaskOperationResponseHandler activateResponseHandler = new BlockingTaskOperationResponseHandler();
        client.exit( taskId, users.get( "admin" ).getId(), activateResponseHandler);
        
        taskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, taskResponseHandler);
        task = taskResponseHandler.getTask();
        assertEquals(  Status.Exited, task.getTaskData().getStatus() );  
        
        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner(users.get( "bobba" ).getId(), "en-UK", responseHandler);
        List<TaskSummary> exitedTasks = responseHandler.getResults();
        assertEquals(0, exitedTasks.size());
        
    } 
    
    
    public void testClaimConflictAndRetry() {
        runTestClaimConflictAndRetry(client, users, groups);
    }

    public static void runTestClaimConflictAndRetry(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
        Map <String, Object> vars = fillVariables(users, groups); 
    
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = false} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['salaboy'], users['bobba']], businessAdministrators = [ users['admin']] }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        // Create a local instance of the TaskService

        // Deploy the Task Definition to the Task Component
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        client.addTask(( Task )  eval( new StringReader( str ), vars ), new ContentData(), addTaskResponseHandler);

        // Because the Task contains a direct assignment we can query it for its Potential Owner
        // Notice that we obtain a list of TaskSummary (a lightweight representation of a task)
        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner(users.get( "salaboy" ).getId(), "en-UK", responseHandler);
        List<TaskSummary> salaboyTasks = responseHandler.getResults();

        // We know that there is just one task available so we get the first one
        Long salaboyTaskId = salaboyTasks.get(0).getId();

        // In order to check the task status we need to get the real task
        // The task is in a Reserved status because it already have a well-defined Potential Owner
        BlockingGetTaskResponseHandler taskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(salaboyTaskId, taskResponseHandler);
        Task salaboyTask = taskResponseHandler.getTask();
        
        assertEquals(Status.Ready, salaboyTask.getTaskData().getStatus());

        // Because the Task contains a direct assignment we can query it for its Potential Owner
        // Notice that we obtain a list of TaskSummary (a lightweight representation of a task)
        responseHandler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner(users.get( "bobba" ).getId(), "en-UK", responseHandler);
        List<TaskSummary> bobbaTasks = responseHandler.getResults();

        // We know that there is just one task available so we get the first one
        Long bobbaTaskId = bobbaTasks.get(0).getId();
        assertEquals(bobbaTaskId, salaboyTaskId);
        // In order to check the task status we need to get the real task
        // The task is in a Reserved status because it already have a well-defined Potential Owner
        taskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(bobbaTaskId, taskResponseHandler);
        Task bobbaTask = taskResponseHandler.getTask();
        assertEquals(Status.Ready, bobbaTask.getTaskData().getStatus());

        BlockingTaskOperationResponseHandler claimResponseHandler = new BlockingTaskOperationResponseHandler();
        client.claim(bobbaTask.getId(), users.get( "bobba" ).getId(), claimResponseHandler);
        
        try{
            claimResponseHandler = new BlockingTaskOperationResponseHandler();
            client.claim(salaboyTask.getId(), users.get( "salaboy" ).getId(), claimResponseHandler);
        } catch(PermissionDeniedException ex){
            // The Task is gone.. salaboy needs to retry
            assertNotNull(ex);
        }
        
        
        
        



    }

    public void testClaimNextAvailable() {
        runTestClaimConflictAndRetry(client, users, groups);
    }

    public static void runTestClaimNextAvailable(AsyncTaskService client, Map<String, User> users, Map<String, Group> groups) {
    
        Map <String, Object> vars = fillVariables(users, groups);
        // Create a local instance of the TaskService
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = false} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['salaboy'], users['bobba']], businessAdministrators = [ users['admin']] }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        

        // Deploy the Task Definition to the Task Component
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        client.addTask(( Task )  eval( new StringReader( str ), vars ), new ContentData(), addTaskResponseHandler);

        // we don't need to query for our task to see what we will claim, just claim the next one available for us
        BlockingTaskOperationResponseHandler claimResponseHandler = new BlockingTaskOperationResponseHandler();
        client.claimNextAvailable(users.get( "bobba" ).getId(), "en-UK", claimResponseHandler);
        
        
        List<Status> status = new ArrayList<Status>();
        status.add(Status.Ready);
        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwnerByStatus(users.get( "salaboy" ).getId(),status,  "en-UK", responseHandler);
        List<TaskSummary> salaboyTasks = responseHandler.getResults();
        assertEquals(0, salaboyTasks.size());
        
        



    } 
    
}
