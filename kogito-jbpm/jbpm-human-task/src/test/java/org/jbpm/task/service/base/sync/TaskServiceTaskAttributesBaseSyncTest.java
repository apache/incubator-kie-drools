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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.task.AccessType;
import org.jbpm.task.BaseTest;
import org.jbpm.task.Content;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.TaskService;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.FaultData;
import org.jbpm.task.service.TaskServer;

public abstract class TaskServiceTaskAttributesBaseSyncTest extends BaseTest {
    
    protected TaskServer server;
    protected TaskService client;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        client.disconnect();
    }

	public void testAddRemoveOutput() {
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { createdOn = now, activationTime = now,";
        str += "actualOwner = new User('Darth Vader')}),";
        str += "deadlines = new Deadlines(),";
        str += "delegation = new Delegation(),";
        str += "peopleAssignments = new PeopleAssignments(),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();
        
        ContentData outputData = new ContentData();
        outputData.setAccessType(AccessType.Inline);
        outputData.setContent("This is my output!!!!".getBytes());
        outputData.setType("text/plain");
        
       
        client.setOutput( taskId, "Darth Vader", outputData );
        
        
        
        
        Task task1 = client.getTask( taskId );
        // If we use a local implementation it will be the same Object
        //assertNotSame(task, task1);
        //assertFalse(  task.equals( task1) );
       
        long outputContentId = task1.getTaskData().getOutputContentId();
        assertNotSame(0, outputContentId);

        Content content = client.getContent(outputContentId);
        assertNotNull(content);
        assertEquals("This is my output!!!!", new String(content.getContent()));
        assertEquals("text/plain", task1.getTaskData().getOutputType());
        assertEquals(AccessType.Inline, task1.getTaskData().getOutputAccessType());
        assertEquals(outputContentId, content.getId());
        
        // Make the same as the returned tasks, so we can test equals
        task.getTaskData().setOutput( outputContentId, outputData );
        task.getTaskData().setStatus( Status.Created );
        assertEquals(task, task1);       

        //test delete output
        
        client.deleteOutput( taskId, "Darth Vader" );

        
        task1 = client.getTask( taskId );
        assertEquals(0, task1.getTaskData().getOutputContentId() );   
        assertNull( task1.getTaskData().getOutputAccessType() );
        assertNull( task1.getTaskData().getOutputType() );
    }
    
    public void testAddRemoveFault() throws Exception {
    	Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { createdOn = now, activationTime = now,";
        str += "actualOwner = new User('Darth Vader')}),";
        str += "deadlines = new Deadlines(),";
        str += "delegation = new Delegation(),";
        str += "peopleAssignments = new PeopleAssignments(),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();
        
        FaultData faultData = new FaultData();
        faultData.setAccessType(AccessType.Inline);
        faultData.setContent("This is my fault!!!!".getBytes());
        faultData.setFaultName("fault1");
        faultData.setType("text/plain");
        
        
        client.setFault( taskId, "Darth Vader", faultData );
        
        
        
        
        
        Task task1 = client.getTask( taskId );
        
//        assertNotSame(task, task1);
//        assertFalse(  task.equals( task1) );
       
        long faultContentId = task1.getTaskData().getFaultContentId();
        assertNotSame(0, faultContentId);

        
        
        
        Content content = client.getContent(faultContentId);
        assertNotNull(content);
        assertEquals("This is my fault!!!!", new String(content.getContent()));
        assertEquals("text/plain", task1.getTaskData().getFaultType());
        assertEquals("fault1", task1.getTaskData().getFaultName());
        assertEquals(AccessType.Inline, task1.getTaskData().getFaultAccessType());
        assertEquals(faultContentId, content.getId());
        
        // Make the same as the returned tasks, so we can test equals
        task.getTaskData().setOutput( faultContentId, faultData );
        task.getTaskData().setStatus( Status.Created );
        assertEquals(task, task1);       

        //test delete fault
        
        client.deleteFault( taskId, "Darth Vader" );

        task1 = client.getTask( taskId );
        assertEquals(0, task1.getTaskData().getFaultContentId() );   
        assertNull( task1.getTaskData().getFaultAccessType() );
        assertNull( task1.getTaskData().getFaultType() );
        assertNull( task1.getTaskData().getFaultName() );
    } 
    
    public void testSetPriority() throws Exception {
    	Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { createdOn = now, activationTime = now,";
        str += "actualOwner = new User('Darth Vader')}),";
        str += "deadlines = new Deadlines(),";
        str += "delegation = new Delegation(),";
        str += "peopleAssignments = new PeopleAssignments(),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();
        
        int newPriority = 33;
        
        
        client.setPriority(taskId, "Darth Vader", newPriority );
        
        
        
        
        Task task1 = client.getTask( taskId );
        //If we use the local implementation the object will be the same
//        assertNotSame(task, task1);
//        assertFalse(  task.equals( task1) );
       
        int newPriority1 = task1.getPriority();
        assertEquals(newPriority, newPriority1);

        // Make the same as the returned tasks, so we can test equals
        task.setPriority( newPriority );
        task.getTaskData().setStatus( Status.Created );
        assertEquals(task, task1);       
    }
}
