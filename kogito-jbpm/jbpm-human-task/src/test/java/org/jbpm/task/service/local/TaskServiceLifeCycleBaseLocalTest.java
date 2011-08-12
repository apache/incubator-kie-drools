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

package org.jbpm.task.service.local;

import org.jbpm.task.service.*;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.task.AccessType;
import org.jbpm.task.BaseTest;
import org.jbpm.task.Content;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.responsehandlers.BlockingAddTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetContentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;

public abstract class TaskServiceLifeCycleBaseLocalTest extends BaseTest {

    private static final int DEFAULT_WAIT_TIME = 5000;

    protected SyncTaskService client;

    public void testNewTaskWithNoPotentialOwners() {
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        ContentData data = new ContentData();
        data.setAccessType(AccessType.Inline);
        data.setType("type");
        data.setContent("content".getBytes());
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, data );
        
        long taskId = task.getId();
        
        // Task should be assigned to the single potential owner and state set to Reserved
        
        
        Task task1 = client.getTask( taskId );
        assertEquals( AccessType.Inline, task1.getTaskData().getDocumentAccessType() );
        assertEquals( "type", task1.getTaskData().getDocumentType() );
        long contentId = task1.getTaskData().getDocumentContentId();
        assertTrue( contentId != -1 ); 

        
        
        Content content = client.getContent(contentId);
        assertEquals("content", new String(content.getContent()));
    }
    
    public void testNewTaskWithLargeContent() {
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        ContentData data = new ContentData();
        data.setAccessType(AccessType.Inline);
        data.setType("type");
        String largeContent = "";
        for (int i = 0; i < 1000; i++) {
        	largeContent += i + "xxxxxxxxx";
        }
        data.setContent(largeContent.getBytes());
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, data );
        
        long taskId = task.getId();
        
        // Task should be assigned to the single potential owner and state set to Reserved
        
        
        Task task1 = client.getTask( taskId );
        assertEquals( AccessType.Inline, task1.getTaskData().getDocumentAccessType() );
        assertEquals( "type", task1.getTaskData().getDocumentType() );
        long contentId = task1.getTaskData().getDocumentContentId();
        assertTrue( contentId != -1 ); 

        
        
        Content content = client.getContent(contentId);
        System.out.println(new String(content.getContent()));
        assertEquals(largeContent, new String(content.getContent()));
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [groups['knightsTempler' ]], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();
        
        // A Task with multiple potential owners moves to "Ready" state until someone claims it.
        
        
        Task task1 = client.getTask( taskId );
        assertEquals( Status.Ready , task1.getTaskData().getStatus() );     
        
        
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Dummy Group");
        groupIds.add("Knights Templer");
        client.claim( taskId, users.get( "darth" ).getId(), groupIds );        
        
        
        
        
        Task task2 = client.getTask( taskId );
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        client.addTask( task, null );
        
        long taskId = task.getId();             
        
        // Go straight from Ready to Inprogress
        
        client.start( taskId, users.get( "darth" ).getId() );
        
        
        Task task1 = client.getTask( taskId );
        assertEquals(  Status.InProgress, task1.getTaskData().getStatus() );
        assertEquals( users.get( "darth" ), task1.getTaskData().getActualOwner() );  
        
        ContentData data = new ContentData();
        data.setAccessType(AccessType.Inline);
        data.setType("type");
        data.setContent("content".getBytes());
        
        client.complete( taskId, users.get( "darth" ).getId(), data ); 

        
        Task task2 = client.getTask( taskId );
        assertEquals( AccessType.Inline, task2.getTaskData().getOutputAccessType() );
        assertEquals( "type", task2.getTaskData().getOutputType() );
        long contentId = task2.getTaskData().getOutputContentId();
        assertTrue( contentId != -1 ); 
        
        
        
        Content content = client.getContent(contentId);
        assertEquals("content", new String(content.getContent()));
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
        client.addTask( task, null );
        
        long taskId = task.getId();             
        
        // Go straight from Ready to Inprogress
        
        client.start( taskId, users.get( "darth" ).getId() );      
        
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();  
        
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
        client.addTask( task, null );
        
        long taskId = task.getId();             
        
        // Go straight from Ready to Inprogress
        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.start( taskId, users.get( "darth" ).getId() );
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
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
        responseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        
        
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
    //@TODO: FIX IT, looks like a persistence problem around persistent bags
//    public void testRegisterRemove() throws Exception {
//    	Map <String, Object> vars = new HashMap<String, Object>();     
//        vars.put( "users", users );
//        vars.put( "groups", groups );
//        vars.put( "now", new Date() );
//        
//        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
//        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba'], users['darth'] ], }),";                        
//        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
//
//        
//        Task task = ( Task )  eval( new StringReader( str ), vars );
//        client.addTask( task, null );
//        
//        long taskId = task.getId();             
//        
//       
//        client.register(taskId, users.get("bobba").getId());
//       
//        
//        Thread.sleep(500);
//        
//        
//        
//        Task task1 = client.getTask(taskId);
//        List<OrganizationalEntity> myRecipientTasks = task1.getPeopleAssignments().getRecipients();
//        
//        assertNotNull(myRecipientTasks);
//        assertEquals(1, myRecipientTasks.size());
//        assertTrue(task1.getPeopleAssignments().getRecipients().contains(users.get("bobba")));
//        
//        client.remove(taskId, users.get("bobba").getId());
//        
//        
//        Thread.sleep(500);
//        
//        
//        
//        Task task2 = client.getTask( taskId );
//        assertFalse(task2.getPeopleAssignments().getRecipients().contains(users.get("bobba")));
//    }
    
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
    	Map <String, Object> vars = new HashMap<String, Object>();
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
    	Map <String, Object> vars = new HashMap<String, Object>();
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
    	Map <String, Object> vars = new HashMap<String, Object>();
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
    	Map <String, Object> vars = new HashMap<String, Object>();
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
    	Map <String, Object> vars = new HashMap<String, Object>();
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
        //We are not using remoting here so the object is the same
        assertTrue(task1.equals(task));
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
    	Map <String, Object> vars = new HashMap<String, Object>();
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
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
}
