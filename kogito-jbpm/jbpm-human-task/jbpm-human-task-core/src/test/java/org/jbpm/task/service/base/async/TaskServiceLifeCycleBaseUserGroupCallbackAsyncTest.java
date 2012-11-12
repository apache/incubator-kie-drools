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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jbpm.task.AccessType;
import org.jbpm.task.AsyncTaskService;
import org.jbpm.task.BaseTestNoUserGroupSetup;
import org.jbpm.task.Content;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.User;
import org.jbpm.task.identity.DefaultUserGroupCallbackImpl;
import org.jbpm.task.identity.UserGroupCallbackManager;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.FaultData;
import org.jbpm.task.service.PermissionDeniedException;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.responsehandlers.BlockingAddTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetContentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;
import org.jbpm.task.utils.ContentMarshallerHelper;

public abstract class TaskServiceLifeCycleBaseUserGroupCallbackAsyncTest extends BaseTestNoUserGroupSetup {
    private static final int DEFAULT_WAIT_TIME = 5000;

    protected TaskServer server;
    protected AsyncTaskService client;

    protected void tearDown() throws Exception {
        client.disconnect();
        server.stop();
        super.tearDown();
    }
    
    public void testNewTaskWithNoPotentialOwners() {
        Map<String, Object>  vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        } 
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
        
        long taskId = addTaskResponseHandler.getTaskId();
        
        // Task should remain in Created state with no actual owner
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals( task1.getTaskData().getStatus(), Status.Created );     
        assertNull( task1.getTaskData().getActualOwner() );        
    }

    public void testNewTaskWithSinglePotentialOwner() {
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        } 
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        ContentData data = ContentMarshallerHelper.marshal("content", null);
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        } 
        client.addTask( task, data, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        } 
        client.addTask( task, data, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        } 
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        Properties userGroups = new Properties();
    	
    	userGroups.setProperty(users.get( "darth" ).getId(), "Knights Templer, Dummy Group");
    	UserGroupCallbackManager.getInstance().setCallback(new DefaultUserGroupCallbackImpl(userGroups));
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [groups['knightsTempler' ]], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        } 
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        } 
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        } 
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        } 
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        } 
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        } 
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        } 
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        } 
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        } 
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = true} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ] }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = true} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ] }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );  
        addTaskResponseHandler.waitTillDone( 3000 );
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
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
        
    public void testFail() {
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
        long taskId = addTaskResponseHandler.getTaskId();             
        
        BlockingTaskOperationResponseHandler claimResponseHandler = new BlockingTaskOperationResponseHandler();
        client.claim( taskId, users.get( "darth" ).getId(), claimResponseHandler );        
        claimResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        // Go straight from Ready to Inprogress
        BlockingTaskOperationResponseHandler startResponseHandler = new BlockingTaskOperationResponseHandler();
        client.start( taskId, users.get( "darth" ).getId(), startResponseHandler );
        startResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
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
        BlockingTaskOperationResponseHandler failResponseHandler = new BlockingTaskOperationResponseHandler();
        client.fail( taskId, users.get( "darth" ).getId(), data, failResponseHandler );
        failResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
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
        Map <String, Object> vars = new HashMap<String, Object>();     
        vars.put( "users", users );
        vars.put( "groups", groups );
        vars.put( "now", new Date() );
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba'], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
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
        Map <String, Object> vars = new HashMap<String, Object>();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { status = Status.Ready } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ] ],";
        str += "recipients = [users['bobba'] ] }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
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
        Map <String, Object> vars = new HashMap<String, Object>();
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { status = Status.Ready } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [ users['darth'] ] ,";
        str += " potentialOwners = [ users['darth'], users['bobba'] ] } ),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        } 
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
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
            String errorMessage = null;
            if( nominateHandler.getError().getCause() != null ) { 
                errorMessage = nominateHandler.getError().getCause().getMessage();
            }
            else { 
                errorMessage = nominateHandler.getError().getMessage();
            }
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
        Map <String, Object> vars = new HashMap<String, Object>();
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [ users['bobba'] ] } ),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
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
        Map <String, Object> vars = new HashMap<String, Object>();
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [ users['darth'], users['bobba'] ] } ),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        } 
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
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
        Map <String, Object> vars = new HashMap<String, Object>();
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [ users['darth'], users['bobba'] ] } ),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
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
        Map <String, Object> vars = new HashMap<String, Object>();
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { ";
        str += "businessAdministrators = [ users['darth'] ] } ),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
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
        Map <String, Object> vars = new HashMap<String, Object>();
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [ users['darth'], users['bobba'] ], ";
        str += "businessAdministrators = [ users['jabba'] ] } ),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
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
        Map <String, Object> vars = new HashMap<String, Object>();
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { status = Status.Ready } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [ users['darth'], users['bobba'] ], ";
        str += "businessAdministrators = [ users['jabba'] ] } ),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        if(task.getPeopleAssignments() != null && task.getPeopleAssignments().getBusinessAdministrators() != null) {
            List<OrganizationalEntity> businessAdmins = new ArrayList<OrganizationalEntity>();
            businessAdmins.add(new User("Administrator"));
            businessAdmins.addAll(task.getPeopleAssignments().getBusinessAdministrators());
            task.getPeopleAssignments().setBusinessAdministrators(businessAdmins);
        }
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone( 3000 );
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
        Map <String, Object> vars = fillVariables();
        
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
        Map <String, Object> vars = fillVariables();
        
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
        Map <String, Object> vars = fillVariables();
        
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
        Map <String, Object> vars = fillVariables();
        
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
        Map <String, Object> vars = fillVariables();
        
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
        Map <String, Object> vars = fillVariables();
        
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
}
