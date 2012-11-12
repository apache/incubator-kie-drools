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

package org.jbpm.task.service.base.sync;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jbpm.task.AccessType;
import org.jbpm.task.BaseTest;
import org.jbpm.task.Content;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.TaskService;
import org.jbpm.task.identity.DefaultUserGroupCallbackImpl;
import org.jbpm.task.identity.UserGroupCallbackManager;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.FaultData;
import org.jbpm.task.service.PermissionDeniedException;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.utils.ContentMarshallerHelper;

public abstract class TaskServiceLifeCycleBaseSyncTest extends BaseTest {

    private static final int DEFAULT_WAIT_TIME = 5000;
    protected TaskServer server;
    protected TaskService client;


    protected void tearDown() throws Exception {
        if( client != null ) { 
            client.disconnect();
        }
        if( server != null ) { 
            server.stop();
        }
        super.tearDown();
    }

    
    public void testNewTaskWithNoPotentialOwners() {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();
        
        // Task should remain in Created state with no actual owner
        
        Task task1 = client.getTask( taskId );
        assertEquals( task1.getTaskData().getStatus(), Status.Created );     
        assertNull( task1.getTaskData().getActualOwner() );        
    }

    public void testNewTaskWithSinglePotentialOwner() {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        
        client.addTask( task, null );
        long taskId = task.getId();
        
        // Task should be assigned to the single potential owner and state set to Reserved
        
        
        Task task1 = client.getTask( taskId );
        assertEquals( Status.Reserved, task1.getTaskData().getStatus() );     
        assertEquals( users.get( "bobba" ), task1.getTaskData().getActualOwner() );
    }
    
    public void testNewTaskWithContent() {
        Map <String, Object> vars = fillVariables();
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        ContentData data = ContentMarshallerHelper.marshal("content", null);
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, data );
        
        long taskId = task.getId();
        
        // Task should be assigned to the single potential owner and state set to Reserved
        
        
        Task task1 = client.getTask( taskId );
        assertEquals( AccessType.Inline, task1.getTaskData().getDocumentAccessType() );
        assertEquals( "java.lang.String", task1.getTaskData().getDocumentType() );
        long contentId = task1.getTaskData().getDocumentContentId();
        assertTrue( contentId != -1 ); 

        
        
        Content content = client.getContent(contentId);
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        assertEquals("content", unmarshalledObject.toString());
    }
    
     public void testNewTaskWithMapContent() {
        Map <String, Object> vars = fillVariables();
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        Map<String, Object> variablesMap = new HashMap<String, Object>();
        variablesMap.put("key1", "value1");
        variablesMap.put("key2", null);
        variablesMap.put("key3", "value3");
        ContentData data = ContentMarshallerHelper.marshal(variablesMap, null);
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, data );
        
        long taskId = task.getId();
        
        // Task should be assigned to the single potential owner and state set to Reserved
        
        
        Task task1 = client.getTask( taskId );
        assertEquals( AccessType.Inline, task1.getTaskData().getDocumentAccessType() );
        assertEquals( "java.util.HashMap", task1.getTaskData().getDocumentType() );
        long contentId = task1.getTaskData().getDocumentContentId();
        assertTrue( contentId != -1 ); 

        
        
        Content content = client.getContent(contentId);
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        if(!(unmarshalledObject instanceof Map)){
            fail("The variables should be a Map");
        
        }
        Map<String, Object> unmarshalledvars = (Map<String, Object>)unmarshalledObject;
        
        assertEquals("value1",unmarshalledvars.get("key1") );
        assertNull(unmarshalledvars.get("key2") );
        assertEquals("value3",unmarshalledvars.get("key3") );
    }
    
    public void testNewTaskWithLargeContent() {
        Map <String, Object> vars = fillVariables();
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        String largeContent = "";
        for (int i = 0; i < 1000; i++) {
        	largeContent += i + "xxxxxxxxx";
        }
        
        ContentData data = ContentMarshallerHelper.marshal(largeContent, null);
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, data );
        
        long taskId = task.getId();
        
        // Task should be assigned to the single potential owner and state set to Reserved
        
        
        Task task1 = client.getTask( taskId );
        assertEquals( AccessType.Inline, task1.getTaskData().getDocumentAccessType() );
        assertEquals( "java.lang.String", task1.getTaskData().getDocumentType() );
        long contentId = task1.getTaskData().getDocumentContentId();
        assertTrue( contentId != -1 ); 
        
        Content content = client.getContent(contentId);
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        assertEquals(largeContent, unmarshalledObject.toString());
    }
    
    public void testClaimWithMultiplePotentialOwners() throws Exception {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();
        
        // A Task with multiple potential owners moves to "Ready" state until someone claims it.
        
        
        Task task1 = client.getTask( taskId );
        assertEquals( Status.Ready , task1.getTaskData().getStatus() );     
        
        
        client.claim( taskId, users.get( "darth" ).getId() );        
        
        
        
        
        Task task2 = client.getTask( taskId );
        assertEquals(  Status.Reserved, task2.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() );
    }

    public void testClaimWithGroupAssignee() throws Exception {
    	Properties userGroups = new Properties();
    	
    	userGroups.setProperty(users.get( "darth" ).getId(), "Knights Templer, Dummy Group");
    	UserGroupCallbackManager.getInstance().setCallback(new DefaultUserGroupCallbackImpl(userGroups));
    	
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [groups['knightsTempler' ]], businessAdministrators = [ new User('Administrator') ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();
        
        // A Task with multiple potential owners moves to "Ready" state until someone claims it.
        
        
        Task task1 = client.getTask( taskId );
        assertEquals( Status.Ready , task1.getTaskData().getStatus() );     

        client.claim( taskId, users.get( "darth" ).getId());        
        
        
        
        
        Task task2 = client.getTask( taskId );
        assertEquals(  Status.Reserved, task2.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() );
    }

    public void testStartFromReadyStateWithPotentialOwner() throws Exception {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();
        
        // A Task with multiple potential owners moves to "Ready" state until someone claims it.
        
        
        Task task1 = client.getTask( taskId );
        assertEquals( Status.Ready , task1.getTaskData().getStatus() );     
        
        // Go straight from Ready to Inprogress
        client.start( taskId, users.get( "darth" ).getId() );
        
        Task task2 = client.getTask( taskId );
        assertEquals(  Status.InProgress, task2.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() );        
    }
    
    public void testStartFromReadyStateWithIncorrectPotentialOwner() {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();
        
        // A Task with multiple potential owners moves to "Ready" state until someone claims it.
        
        
        Task task1 = client.getTask( taskId );
        assertEquals( Status.Ready , task1.getTaskData().getStatus() );     
        
        // State should not change as user isn't potential owner
        

        PermissionDeniedException denied = null;
        try {
            client.start( taskId, users.get( "tony" ).getId() );
        } catch(PermissionDeniedException e) {
            denied = e;
        }

        assertNotNull("Should get permissed denied exception", denied);

        Task task2 = client.getTask( taskId );
        assertEquals(  Status.Ready, task2.getTaskData().getStatus() );
        assertNull( task2.getTaskData().getActualOwner() );        
    }    
    
    public void testStartFromReserved() throws Exception {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();
        
        // Task should be assigned to the single potential owner and state set to Reserved
        
        
        Task task1 = client.getTask( taskId );
        assertEquals( Status.Reserved, task1.getTaskData().getStatus());     
        assertEquals( users.get( "bobba" ), task1.getTaskData().getActualOwner() );
        
        // Should change to InProgress
        
        client.start( taskId, users.get( "bobba" ).getId() );
        
        
        
        
        Task task2 = client.getTask( taskId );
        assertEquals( Status.InProgress, task2.getTaskData().getStatus() );     
        assertEquals( users.get( "bobba" ), task1.getTaskData().getActualOwner() );        
    }
    
    public void testStartFromReservedWithIncorrectUser() {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();
        
        // Task should be assigned to the single potential owner and state set to Reserved
        
        Task task1 = client.getTask( taskId );
        assertEquals( Status.Reserved , task1.getTaskData().getStatus());     
        assertEquals( users.get( "bobba" ), task1.getTaskData().getActualOwner() );
        
        // Should change not change
        
        

        PermissionDeniedException denied = null;
        try{
            client.start( taskId, users.get( "tony" ).getId() );
        } catch (PermissionDeniedException e) {
            denied = e;
        }
        assertNotNull("Should get permissed denied exception", denied);

        Task task2 = client.getTask( taskId );
        assertEquals( Status.Reserved, task2.getTaskData().getStatus() );     
        assertEquals( users.get( "bobba" ), task1.getTaskData().getActualOwner() );        
    }
    
    public void testStop() {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();             
        
        // Go straight from Ready to Inprogress
        client.start( taskId, users.get( "darth" ).getId());        
        
        client.getTask( taskId );
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.InProgress, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );        
        
        // Now Stop
        
        client.stop( taskId, users.get( "darth" ).getId() );
        
        
        Task task2 = client.getTask( taskId );
        assertEquals(  Status.Reserved, task2.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() );                
    }    
    
    public void testStopWithIncorrectUser() {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();             
        
        // Go straight from Ready to Inprogress
        
        client.start( taskId, users.get( "darth" ).getId() );
        
        
        
        
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.InProgress, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );        
        
        // Should not stop
        
        
        PermissionDeniedException denied = null;
        try{
            client.stop( taskId, users.get( "bobba" ).getId() );
        } catch(PermissionDeniedException e) {
            denied = e;
        }
        assertNotNull("Should get permissed denied exception", denied);
        
        
        
        Task task2 = client.getTask( taskId );
        assertEquals(  Status.InProgress, task2.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() );                
    }   
    
    public void testReleaseFromInprogress() throws Exception {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();             
        
        // Go straight from Ready to Inprogress
        
        client.start( taskId, users.get( "darth" ).getId() );
        
        
        
        client.getTask( taskId );
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.InProgress, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Check is Released
        
        client.release( taskId, users.get( "darth" ).getId() );
       
        
        
        
        Task task2 = client.getTask( taskId );
        assertEquals(  Status.Ready, task2.getTaskData().getStatus() );
        assertNull( task2.getTaskData().getActualOwner() );                  
    }    
    
    public void testReleaseFromReserved() {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();             
        
        // Go straight from Ready to Inprogress
        
        client.claim( taskId, users.get( "darth" ).getId() );
        
        
        
        
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.Reserved, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Check is Released
        
        client.release( taskId, users.get( "darth" ).getId() );  
        
        Task task2 = client.getTask( taskId );
        assertEquals(  Status.Ready, task2.getTaskData().getStatus() );
        assertNull( task2.getTaskData().getActualOwner() );                  
    }     
    
    public void testReleaseWithIncorrectUser() {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();             
        
        // Go straight from Ready to Inprogress
        
        client.claim( taskId, users.get( "darth" ).getId() );
        
        
        
        
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.Reserved, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Check is not changed
        
        
        PermissionDeniedException denied = null;
        try{
            client.release( taskId, users.get( "bobba" ).getId() );
        } catch(PermissionDeniedException e) {
            denied = e;
        }
        assertNotNull("Should get permissed denied exception", denied);
        
        
        
        Task task2 = client.getTask( taskId );
        assertEquals(  Status.Reserved, task2.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() ); 
    }

    public void testSuspendFromReady() {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();             
        
        // Check is Ready
        
        client.getTask( taskId );
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.Ready, task1.getTaskData().getStatus() );
        assertNull( task1.getTaskData().getActualOwner() );  
        
        // Check is Suspended
        
        client.suspend( taskId, users.get( "darth" ).getId() );
        
        
        
        
        Task task2 = client.getTask( taskId );
        assertEquals(  Status.Suspended, task2.getTaskData().getStatus() );
        assertEquals( Status.Ready, task2.getTaskData().getPreviousStatus() );
        assertNull( task1.getTaskData().getActualOwner() );                  
    }
    
    public void testSuspendFromReserved() {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();             
        
        // Check is Reserved
        
        client.claim( taskId, users.get( "darth" ).getId() );   
        
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.Reserved, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Check is Suspended
        
        client.suspend( taskId, users.get( "darth" ).getId() );
  
        Task task2 =  client.getTask( taskId );
        assertEquals(  Status.Reserved, task2.getTaskData().getPreviousStatus() );
        assertEquals(  Status.Suspended, task2.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() );          
    }    
    
    public void testSuspendFromReservedWithIncorrectUser() {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();             
        
        // Check is Reserved
        client.claim( taskId, users.get( "darth" ).getId() );
        

        Task task1 = client.getTask( taskId );
        assertEquals(  Status.Reserved, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Check is not changed
        
        
        PermissionDeniedException denied = null;
        try{
            client.suspend( taskId, users.get( "bobba" ).getId() );
        } catch(PermissionDeniedException e) {
            denied = e;
        }
        assertNotNull("Should get permissed denied exception", denied);
        
        
        
        Task task2 = client.getTask( taskId );
        assertEquals(  Status.Reserved, task2.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() );      
    }    
    
    public void testResumeFromReady() {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();             
        
        // Check is Ready
        
        
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.Ready, task1.getTaskData().getStatus() );
        assertNull( task1.getTaskData().getActualOwner() );  
        
        // Check is Suspended
        
        client.suspend( taskId, users.get( "darth" ).getId() );
        
        Task task2 = client.getTask( taskId );
        assertEquals(  Status.Suspended, task2.getTaskData().getStatus() );
        assertEquals( Status.Ready, task2.getTaskData().getPreviousStatus() );
        assertNull( task1.getTaskData().getActualOwner() );    
        
        // Check is Resumed
        
        client.resume( taskId, users.get( "darth" ).getId() );   
         
        Task task3 = client.getTask( taskId );
        assertEquals(  Status.Ready, task3.getTaskData().getStatus() );
        assertEquals( Status.Suspended, task3.getTaskData().getPreviousStatus() );
        assertNull( task3.getTaskData().getActualOwner() );         
    }
    
    public void testResumeFromReserved() {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();             
        
        // Check is Reserved
        
        client.claim( taskId, users.get( "darth" ).getId() );
        
        
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.Reserved, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Check is suspended
        
        client.suspend( taskId, users.get( "darth" ).getId() );        
        
        
        
        
        Task task2 = client.getTask( taskId );
        assertEquals(  Status.Reserved, task2.getTaskData().getPreviousStatus() );
        assertEquals(  Status.Suspended, task2.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() ); 
        
        // Check is Resumed
        
        client.resume( taskId, users.get( "darth" ).getId() ); 
  
        Task task3 = client.getTask( taskId );
        assertEquals(  Status.Reserved, task3.getTaskData().getStatus() );
        assertEquals( Status.Suspended, task3.getTaskData().getPreviousStatus() );
        assertEquals( users.get( "darth" ), task3.getTaskData().getActualOwner() );           
    }    
    
    public void testResumeFromReservedWithIncorrectUser() {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();             
        
        // Check is Reserved
        
        client.claim( taskId, users.get( "darth" ).getId() );
        
        
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.Reserved, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Check not changed
        
        PermissionDeniedException denied = null;
        try{
            client.suspend( taskId, users.get( "bobba" ).getId() );
        } catch(PermissionDeniedException e) {
            denied = e;
        }
        assertNotNull("Should get permissed denied exception", denied);
        
        Task task2 = client.getTask( taskId );
        assertEquals(  Status.Reserved, task2.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() );      
    }     
          
    public void testSkipFromReady() {
        Map <String, Object> vars = fillVariables();
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = true} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ] }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();                     
        
        // Check is Complete
        
        client.skip( taskId, users.get( "darth" ).getId() );
        
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.Obsolete, task1.getTaskData().getStatus() );
        assertNull(  task1.getTaskData().getActualOwner() );                  
    }    
    
    public void testSkipFromReserved() {
        Map <String, Object> vars = fillVariables();
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = true} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ] }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();             
        
        // Go straight from Ready 
       
        client.claim( taskId, users.get( "darth" ).getId() );
        
        
        // Check is Complete
        
        client.skip( taskId, users.get( "darth" ).getId() );
        
        client.getTask( taskId );
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.Obsolete, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );                  
    }     
    
    public void testDelegateFromReady() throws Exception {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );        
        long taskId = task.getId();                     
        
        // Check is Delegated
        client.delegate( taskId, users.get( "darth" ).getId(), users.get( "tony" ).getId() );    
        
        
        
        
        Task task2 = client.getTask( taskId );        
        assertTrue( task2.getPeopleAssignments().getPotentialOwners().contains( users.get( "darth" ) ) );
        assertTrue( task2.getPeopleAssignments().getPotentialOwners().contains( users.get( "tony" ) ) );
        assertEquals( users.get( "tony" ), task2.getTaskData().getActualOwner() );
        assertEquals(  Status.Ready, task2.getTaskData().getStatus() );             
    }     
    
    public void testDelegateFromReserved() throws Exception {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();             
        
        // Claim and Reserved
        
        client.claim( taskId, users.get( "darth" ).getId() );
        
        
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.Reserved, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Check is Delegated
        
        client.delegate( taskId, users.get( "darth" ).getId(), users.get( "tony" ).getId() );    
        
        
        
        
        Task task2 = client.getTask( taskId );        
        assertTrue( task2.getPeopleAssignments().getPotentialOwners().contains( users.get( "darth" ) ) );
        assertTrue( task2.getPeopleAssignments().getPotentialOwners().contains( users.get( "tony" ) ) );
        assertEquals( users.get( "tony" ), task2.getTaskData().getActualOwner() );
        assertEquals(  Status.Ready, task2.getTaskData().getStatus() );             
    }     
    
    public void testDelegateFromReservedWithIncorrectUser() throws Exception {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );        
        long taskId = task.getId();             
        
        // Claim and Reserved
        
        client.claim( taskId, users.get( "darth" ).getId() );
        
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.Reserved, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Check was not delegated
        
        
        PermissionDeniedException denied = null;
        try{
            client.delegate( taskId, users.get( "bobba" ).getId(), users.get( "tony" ).getId() );    
        } catch(PermissionDeniedException e) {
            denied = e;
        }
        assertNotNull("Should get permissed denied exception", denied);
        
        Task task2 = client.getTask( taskId );        
        assertTrue( task2.getPeopleAssignments().getPotentialOwners().contains( users.get( "darth" ) ) );
        assertFalse( task2.getPeopleAssignments().getPotentialOwners().contains( users.get( "tony" ) ) );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() );
        assertEquals(  Status.Reserved, task2.getTaskData().getStatus() );             
    }  
    
    public void testForwardFromReady() throws Exception {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();                     
        
        // Check is Forwarded
        
        client.forward( taskId, users.get( "darth" ).getId(), users.get( "tony" ).getId() );    

        
        Task task2 = client.getTask( taskId );        
        assertFalse( task2.getPeopleAssignments().getPotentialOwners().contains( users.get( "darth" ) ) );
        assertTrue( task2.getPeopleAssignments().getPotentialOwners().contains( users.get( "tony" ) ) );
        assertNull( task2.getTaskData().getActualOwner() );
        assertEquals(  Status.Ready, task2.getTaskData().getStatus() );             
    }  
    
    public void testForwardFromReserved() throws Exception {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();             
        
        // Claim and Reserved
        
        client.claim( taskId, users.get( "darth" ).getId() );
        
        
        
        
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.Reserved, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Check is Delegated
        
        client.forward( taskId, users.get( "darth" ).getId(), users.get( "tony" ).getId() );    
        
        
        
        
        Task task2 = client.getTask( taskId );        
        assertFalse( task2.getPeopleAssignments().getPotentialOwners().contains( users.get( "darth" ) ) );
        assertTrue( task2.getPeopleAssignments().getPotentialOwners().contains( users.get( "tony" ) ) );
        assertNull( task2.getTaskData().getActualOwner() );
        assertEquals(  Status.Ready, task2.getTaskData().getStatus() );             
    }     
    
    public void testForwardFromReservedWithIncorrectUser() throws Exception {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();             
        
        // Claim and Reserved
        
        client.claim( taskId, users.get( "darth" ).getId() );
        
        
        
        
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.Reserved, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Check was not delegated
        
        
        PermissionDeniedException denied = null;
        try{
            client.forward( taskId, users.get( "bobba" ).getId(), users.get( "tony" ).getId() );    
        } catch(PermissionDeniedException e) {
            denied = e;
        }
        assertNotNull("Should get permissed denied exception", denied);
        
        
        
        Task task2 = client.getTask( taskId );        
        assertTrue( task2.getPeopleAssignments().getPotentialOwners().contains( users.get( "darth" ) ) );
        assertFalse( task2.getPeopleAssignments().getPotentialOwners().contains( users.get( "tony" ) ) );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() );
        assertEquals(  Status.Reserved, task2.getTaskData().getStatus() );             
    }      
    
    public void testComplete() {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();             
        
        // Go straight from Ready to Inprogress
        
        client.start( taskId, users.get( "darth" ).getId() );
        
        
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.InProgress, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Check is Complete
        
        client.complete( taskId, users.get( "darth" ).getId(), null ); 
        
        
        
        
        Task task2 = client.getTask( taskId );
        assertEquals(  Status.Completed, task2.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() );                  
    }
        
    public void testCompleteWithIncorrectUser() {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();             
        
        // Go straight from Ready to Inprogress
        
        client.start( taskId, users.get( "darth" ).getId() );        
        
        
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.InProgress, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Should not complete as wrong user
        
        
        PermissionDeniedException denied = null;
        try{
            client.complete( taskId, users.get( "bobba" ).getId(), null );  
        } catch(PermissionDeniedException e) {
            denied = e;
        }
        assertNotNull("Should get permissed denied exception", denied);
        
        
        
        Task task2 = client.getTask( taskId );
        assertEquals(  Status.InProgress, task2.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() );                  
    }    

    public void testCompleteWithContent() {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            

        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();             
        
        // Go straight from Ready to Inprogress
        
        client.start( taskId, users.get( "darth" ).getId() );
        
        
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.InProgress, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        ContentData data = ContentMarshallerHelper.marshal("content", null);
        client.complete( taskId, users.get( "darth" ).getId(), data ); 

        
        Task task2 = client.getTask( taskId );
        assertEquals( AccessType.Inline, task2.getTaskData().getOutputAccessType() );
        assertEquals( "java.lang.String", task2.getTaskData().getOutputType() );
        long contentId = task2.getTaskData().getOutputContentId();
        assertTrue( contentId != -1 ); 
        
        
        
        Content content = client.getContent(contentId);
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        assertEquals("content", unmarshalledObject.toString());
    }
    
    public void testCompleteWithResults() {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            

        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();             
        
        // Go straight from Ready to Inprogress
        
        client.start( taskId, users.get( "darth" ).getId() );
        
        
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.InProgress, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        
        
        client.completeWithResults( taskId, users.get( "darth" ).getId(), "content" ); 

        
        Task task2 = client.getTask( taskId );
        assertEquals( AccessType.Inline, task2.getTaskData().getOutputAccessType() );
        assertEquals( "java.lang.String", task2.getTaskData().getOutputType() );
        long contentId = task2.getTaskData().getOutputContentId();
        assertTrue( contentId != -1 ); 
        
        
        
        Content content = client.getContent(contentId);
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        assertEquals("content", unmarshalledObject.toString());
    }
    
    public void testFail() {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
       
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();             
        
        // Go straight from Ready to Inprogress
        
        client.start( taskId, users.get( "darth" ).getId() );
        
        
        
        
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.InProgress, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Check is Failed
        
        client.fail( taskId, users.get( "darth" ).getId(), null );
          
        Task task2 = client.getTask( taskId );
        assertEquals(  Status.Failed, task2.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() );                  
    }
    
    public void testFailWithIncorrectUser() {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();             
        
        // Go straight from Ready to Inprogress
        
        client.start( taskId, users.get( "darth" ).getId() );      
        
        
        
        
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.InProgress, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        // Should not fail as wrong user
        
        
        PermissionDeniedException denied = null;
        try{
            client.fail( taskId, users.get( "bobba" ).getId(), null );
        } catch(PermissionDeniedException e) {
            denied = e;
        }
        assertNotNull("Should get permissed denied exception", denied);
        
       
        
        Task task2 = client.getTask( taskId );
        assertEquals(  Status.InProgress, task2.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task2.getTaskData().getActualOwner() );                  
    }    

    public void testFailWithContent() {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            

        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();             
        
        // Go straight from Ready to Inprogress
        client.start( taskId, users.get( "darth" ).getId() );
        
        client.getTask( taskId );
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.InProgress, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        FaultData data = new FaultData();
        data.setAccessType(AccessType.Inline);
        data.setType("type");
        data.setFaultName("faultName");
        data.setContent("content".getBytes());
        
        client.fail( taskId, users.get( "darth" ).getId(), data );
        
        
        Task task2 = client.getTask( taskId );
        assertEquals( Status.Failed, task2.getTaskData().getStatus() );
        assertEquals( AccessType.Inline, task2.getTaskData().getFaultAccessType() );
        assertEquals( "type", task2.getTaskData().getFaultType() );
        assertEquals( "faultName", task2.getTaskData().getFaultName() );
        long contentId = task2.getTaskData().getFaultContentId();
        assertTrue( contentId != -1 ); 
        
        
        
        Content content = client.getContent(contentId);
        assertEquals("content", new String(content.getContent()));
    }
    
    /**
     * The issue here has to do with the fact that hibernate uses lazy initialization. 
     * Actually, what's happening is that one of the collections retrieved isn't retrieved "for update", 
     * so that the proxy collection instance retrieved can't be updated. 
     * (The collection instance can't be updated because hibernate doesn't allowed that unless the collection 
     * has been retrieved "for update" -- which is actually logical.)
     * 
     * This, of course, only happens when using the LocalTaskService. Why? Because the LocalTaskService
     * "shares" a persistence context with the client. If I spent another half-hour, I could explain
     * why that causes this particular problem. 
     * Regardless,  I can't stress enough how much that complicates the situation here, and, especially, 
     * why that makes the LocalTaskService a significantly different variant of the TaskService
     * than the HornetQ, Mina or other transport medium based instances.  
     */
    public void FIXME_testRegisterRemove() throws Exception {
    	  Map <String, Object> vars = fillVariables();
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba'], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();               
       
        client.register(taskId, users.get("bobba").getId());

        
        Task task1 = client.getTask(taskId);
        List<OrganizationalEntity> myRecipientTasks = task1.getPeopleAssignments().getRecipients();
        
        assertNotNull(myRecipientTasks);
        assertEquals(1, myRecipientTasks.size());
        assertTrue(task1.getPeopleAssignments().getRecipients().contains(users.get("bobba")));
        
        client.remove(taskId, users.get("bobba").getId());
        
        Task task2 = client.getTask( taskId );
        assertFalse(task2.getPeopleAssignments().getRecipients().contains(users.get("bobba")));
    }
    
    public void testRemoveNotInRecipientList() {
        Map <String, Object> vars = fillVariables();
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { status = Status.Ready } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ] ],";
        str += "recipients = [users['bobba'] ] }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

       
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();             
        
        // Do nominate and fail due to Ready status
        
        
        List<TaskSummary> myRecipientTasks = client.getTasksAssignedAsRecipient(users.get("jabba").getId(), "en-UK");
        
        assertNotNull(myRecipientTasks);
        assertEquals(0, myRecipientTasks.size());
        
        
        
        List<TaskSummary> myPotentialTasks = client.getTasksAssignedAsPotentialOwner(users.get("jabba").getId(), "en-UK");
        
        assertNotNull(myPotentialTasks);
        assertEquals(0, myPotentialTasks.size());
        
        
       	try {
       		client.remove(taskId, users.get("jabba").getId());
       		fail("Shouldn't be successful");
        } catch (RuntimeException e) { //expected
        }
        
        //shouldn't affect the assignments
        
        
        Task task1 = client.getTask( taskId );
        assertTrue(task1.getPeopleAssignments().getRecipients().contains(users.get("bobba")));
    }
    
    /**
     * Nominate an organization entity to process the task. If it is nominated to one person
     * then the new state of the task is Reserved. If it is nominated to several people then 
     * the new state of the task is Ready. This can only be performed when the task is in the 
     * state Created.
     */
    public void testNominateOnOtherThanCreated() {
        Map <String, Object> vars = fillVariables();
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { status = Status.Ready } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [ users['darth'] ] ,";
        str += " potentialOwners = [ users['darth'], users['bobba'] ] } ),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();
        
        client.start( taskId, users.get("bobba").getId());
       
       	try {
       		List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>();
       		potentialOwners.add(users.get("bobba"));
       		client.nominate(taskId, users.get("darth").getId(), potentialOwners);
       		
       		fail("Shouldn't be successful");
        } catch (RuntimeException e) { //expected
//        	assertNotNull(nominateHandler.getError());
//        	assertNotNull(nominateHandler.getError().getMessage());
//        	assertTrue(nominateHandler.getError().getMessage().contains("Created"));
        }
        
        //shouldn't affect the assignments
        
        Task task1 = client.getTask( taskId );
        assertTrue(task1.getPeopleAssignments().getPotentialOwners().contains(users.get("darth")));
        assertTrue(task1.getPeopleAssignments().getPotentialOwners().contains(users.get("bobba")));
    }
    
    public void testNominateWithIncorrectUser() {
        Map <String, Object> vars = fillVariables();
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [ users['bobba'] ] } ),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();
        
        
       	try {
       		List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>(1);
       		potentialOwners.add(users.get("jabba"));
       		client.nominate(taskId, users.get("darth").getId(), potentialOwners);
       		
       		fail("Shouldn't be successful");
        } catch (RuntimeException e) { //expected
//        	assertNotNull(nominateHandler.getError());
//        	assertNotNull(nominateHandler.getError().getMessage());
//        	assertTrue(nominateHandler.getError().getMessage().contains(users.get("darth").getId()));
        }
        
        //shouldn't affect the assignments
        
        Task task1 = client.getTask( taskId );
        assertTrue(task1.getPeopleAssignments().getBusinessAdministrators().contains(users.get("bobba")));
        assertEquals(task1.getTaskData().getStatus(), Status.Created);
    }
    
    public void testNominateToUser() {
        Map <String, Object> vars = fillVariables();
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [ users['darth'], users['bobba'] ] } ),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();
        
        
        List<OrganizationalEntity> potentialOwners = new ArrayList<OrganizationalEntity>(1);
        potentialOwners.add(users.get("jabba"));
   		client.nominate(taskId, users.get("darth").getId(), potentialOwners);
   		
        
        //shouldn't affect the assignments
        
        
        Task task1 = client.getTask( taskId );
        assertEquals(task1.getTaskData().getActualOwner(), users.get("jabba"));
        assertEquals(task1.getTaskData().getStatus(), Status.Reserved);
    }
    
    public void testNominateToGroup() {
        Map <String, Object> vars = fillVariables();
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [ users['darth'], users['bobba'] ] } ),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();
        
        
        List<OrganizationalEntity> potentialGroups = new ArrayList<OrganizationalEntity>();
        potentialGroups.add(groups.get("knightsTempler"));
   		client.nominate(taskId, users.get("darth").getId(), potentialGroups);
   		
        
        //shouldn't affect the assignments
        
        
        Task task1 = client.getTask( taskId );
        assertTrue(task1.getPeopleAssignments().getPotentialOwners().contains(groups.get("knightsTempler")));
        assertEquals(task1.getTaskData().getStatus(), Status.Ready);
    }
    
    public void testActivate() {
        Map <String, Object> vars = fillVariables();
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { ";
        str += "businessAdministrators = [ users['darth'] ] } ),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

       
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();

       
        client.activate(taskId, users.get("darth").getId());
        
        Task task1 = client.getTask( taskId );
        
        assertEquals(task1.getTaskData().getStatus(), Status.Ready);
        //When we are not using remoting the object is the same
        //assertTrue(task1.equals(task));
        //When we use remoting this will be false
        //assertFalse(task1.equals(task));
    }
    
    public void testActivateWithIncorrectUser() {
        Map <String, Object> vars = fillVariables();
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [ users['darth'], users['bobba'] ], ";
        str += "businessAdministrators = [ users['jabba'] ] } ),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();

        
        try {
        	client.activate(taskId, users.get("darth").getId());
        	
        	fail("Shouldn't have succeded");
    	} catch (RuntimeException e) {
//        	assertNotNull(activateResponseHandler.getError());
//        	assertNotNull(activateResponseHandler.getError().getMessage());
//        	assertTrue(activateResponseHandler.getError().getMessage().toLowerCase().contains("status"));
        }

    }
    
    public void testActivateFromIncorrectStatus() {
    	Map <String, Object> vars = fillVariables();
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { status = Status.Ready } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [ users['darth'], users['bobba'] ], ";
        str += "businessAdministrators = [ users['jabba'] ] } ),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

       
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();

        
        try {
        	client.activate(taskId, users.get("darth").getId());
        	
        	fail("Shouldn't have succeded");
    	} catch (RuntimeException e) {
//        	assertNotNull(activateResponseHandler.getError());
//        	assertNotNull(activateResponseHandler.getError().getMessage());
//        	assertTrue(activateResponseHandler.getError().getMessage().contains(users.get("darth").getId()));
        }
    }
    
    public void testExitFromReady() {
        Map <String, Object> vars = fillVariables();
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = false} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], businessAdministrators = [ users['admin']] }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();    
        task = client.getTask(taskId);
        assertEquals(Status.Ready, task.getTaskData().getStatus());
        
        
        client.exit( taskId, users.get( "admin" ).getId() );
        
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.Exited, task1.getTaskData().getStatus() );               
    }  
    
    public void testExitFromReserved() {
        Map <String, Object> vars = fillVariables();
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = false} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba'] ], businessAdministrators = [ users['admin']] }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();  
        task = client.getTask(taskId);
        assertEquals(Status.Reserved, task.getTaskData().getStatus());
        
        
        client.exit( taskId, users.get( "admin" ).getId() );
        
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.Exited, task1.getTaskData().getStatus() );                
    }  
    
    public void testExitFromInProgress() {
        Map <String, Object> vars = fillVariables();
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = false} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba'] ], businessAdministrators = [ users['admin']] }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();   
        task = client.getTask(taskId);
        assertEquals(Status.Reserved, task.getTaskData().getStatus());
        
        client.start(taskId, users.get("bobba").getId());
        task = client.getTask(taskId);
        assertEquals(Status.InProgress, task.getTaskData().getStatus());
        
        client.exit( taskId, users.get( "admin" ).getId() );
        
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.Exited, task1.getTaskData().getStatus() );                
    }  

    public void testExitFromSuspended() {
        Map <String, Object> vars = fillVariables();
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = false} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba'] ], businessAdministrators = [ users['admin']] }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();      
        task = client.getTask(taskId);
        assertEquals(Status.Reserved, task.getTaskData().getStatus());
        
        client.suspend(taskId, users.get("bobba").getId());
        task = client.getTask(taskId);
        assertEquals(Status.Suspended, task.getTaskData().getStatus());
        
        client.exit( taskId, users.get( "admin" ).getId() );
        
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.Exited, task1.getTaskData().getStatus() );                
    }
    
    public void testExitPermissionDenied() {
        Map <String, Object> vars = fillVariables();
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = false} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], businessAdministrators = [ users['admin']] }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();      
        task = client.getTask(taskId);
        assertEquals(Status.Ready, task.getTaskData().getStatus());
        
        try {
            client.exit( taskId, users.get( "darth" ).getId() );
            fail("Non admin user can't exit a task");
        } catch (PermissionDeniedException e) {
           
        }
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.Ready, task1.getTaskData().getStatus() );               
    } 
    
    public void testExitNotAvailableToUsers() {
        Map <String, Object> vars = fillVariables();
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { skipable = false} ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba']], businessAdministrators = [ users['admin']] }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();      
        task = client.getTask(taskId);
        assertEquals(Status.Reserved, task.getTaskData().getStatus());
        
        
        client.exit( taskId, users.get( "admin" ).getId() );
        
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.Exited, task1.getTaskData().getStatus() );  
        
        List<TaskSummary> exitedTasks = client.getTasksAssignedAsPotentialOwner(users.get( "bobba" ).getId(), "en-UK");
        assertEquals(0, exitedTasks.size());
        
    }
    
     
    public void testClaimConflictAndRetry() {
        Map <String, Object> vars = fillVariables();
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['salaboy' ], users['bobba'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        // Create a local instance of the TaskService

        // Deploy the Task Definition to the Task Component
        client.addTask(( Task )  eval( new StringReader( str ), vars ), new ContentData());

        // Because the Task contains a direct assignment we can query it for its Potential Owner
        // Notice that we obtain a list of TaskSummary (a lightweight representation of a task)
        List<TaskSummary> salaboyTasks = client.getTasksAssignedAsPotentialOwner(users.get( "salaboy" ).getId(), "en-UK");

        // We know that there is just one task available so we get the first one
        Long salaboyTaskId = salaboyTasks.get(0).getId();

        // In order to check the task status we need to get the real task
        // The task is in a Reserved status because it already have a well-defined Potential Owner
        Task salaboyTask = client.getTask(salaboyTaskId);
        assertEquals(Status.Ready, salaboyTask.getTaskData().getStatus());

        // Because the Task contains a direct assignment we can query it for its Potential Owner
        // Notice that we obtain a list of TaskSummary (a lightweight representation of a task)
        List<TaskSummary> bobbaTasks = client.getTasksAssignedAsPotentialOwner(users.get( "bobba" ).getId(), "en-UK");

        // We know that there is just one task available so we get the first one
        Long bobbaTaskId = bobbaTasks.get(0).getId();
        assertEquals(bobbaTaskId, salaboyTaskId);
        // In order to check the task status we need to get the real task
        // The task is in a Reserved status because it already have a well-defined Potential Owner
        Task bobbaTask = client.getTask(bobbaTaskId);
        assertEquals(Status.Ready, bobbaTask.getTaskData().getStatus());

        
        client.claim(bobbaTask.getId(), users.get( "bobba" ).getId());
        
        try{
            client.claim(salaboyTask.getId(), users.get( "salaboy" ).getId());
        } catch(PermissionDeniedException ex){
            // The Task is gone.. salaboy needs to retry
            assertNotNull(ex);
        }
        
        
        
        



    }

    
    public void testClaimNextAvailable() {


        
       

        Map <String, Object> vars = fillVariables();
        // Create a local instance of the TaskService
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['salaboy' ], users['bobba'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            

        

        // Deploy the Task Definition to the Task Component
        client.addTask(( Task )  eval( new StringReader( str ), vars ), new ContentData());

        // we don't need to query for our task to see what we will claim, just claim the next one available for us
  
        client.claimNextAvailable(users.get( "bobba" ).getId(), "en-UK");
        
        
        List<Status> status = new ArrayList<Status>();
        status.add(Status.Ready);
        List<TaskSummary> salaboyTasks = client.getTasksAssignedAsPotentialOwnerByStatus(users.get( "salaboy" ).getId(),status,  "en-UK");
        assertEquals(0, salaboyTasks.size());
        
        



    } 
}
