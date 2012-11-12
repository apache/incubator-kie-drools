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
import java.util.Map;

import org.jbpm.task.AccessType;
import org.jbpm.task.AsyncTaskService;
import org.jbpm.task.BaseTestNoUserGroupSetup;
import org.jbpm.task.Content;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.FaultData;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.responsehandlers.BlockingAddTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetContentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.utils.ContentMarshallerHelper;

public abstract class TaskServiceTaskAttributesBaseUserGroupCallbackAsyncTest extends BaseTestNoUserGroupSetup {

    protected TaskServer server;
    protected AsyncTaskService client;

    protected void tearDown() throws Exception {
        client.disconnect();
        server.stop();
        super.tearDown();
    }
    
	public void testAddRemoveOutput() {
        Map<String, Object> vars = fillVariables();        
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { createdOn = now, activationTime = now,";
        str += "actualOwner = new User('Darth Vader')}),";
        str += "deadlines = new Deadlines(),";
        str += "delegation = new Delegation(),";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [ users['darth'] ]}),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone(3000);
        
        long taskId = addTaskResponseHandler.getTaskId();
        
        ContentData outputData = ContentMarshallerHelper.marshal("This is my output!!!!", null);
        
        BlockingTaskOperationResponseHandler setOutputResponseHandler = new BlockingTaskOperationResponseHandler();
        client.setOutput( taskId, "Darth Vader", outputData, setOutputResponseHandler );
        setOutputResponseHandler.waitTillDone(1000);
        assertFalse( setOutputResponseHandler.hasError() );
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertNotSame(task, task1);
        assertFalse(  task.equals( task1) );
       
        long outputContentId = task1.getTaskData().getOutputContentId();
        assertNotSame(0, outputContentId);

        BlockingGetContentResponseHandler getOutputResponseHandler = new BlockingGetContentResponseHandler();
        client.getContent(outputContentId, getOutputResponseHandler);
        assertNotNull(getOutputResponseHandler.getContent());
        Content content = getOutputResponseHandler.getContent();
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        assertEquals("This is my output!!!!", unmarshalledObject.toString());
        assertEquals("java.lang.String", task1.getTaskData().getOutputType());
        assertEquals(AccessType.Inline, task1.getTaskData().getOutputAccessType());
        assertEquals(outputContentId, content.getId());
        
        // Make the same as the returned tasks, so we can test equals
        task.getTaskData().setOutput( outputContentId, outputData );
        task.getTaskData().setStatus( Status.Created );
        assertEquals(task, task1);       

        //test delete output
        BlockingTaskOperationResponseHandler deleteOutputResponseHandler = new BlockingTaskOperationResponseHandler();
        client.deleteOutput( taskId, "Darth Vader", deleteOutputResponseHandler );
        deleteOutputResponseHandler.waitTillDone( 1000 );
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        task1 = getTaskResponseHandler.getTask(); 
        assertEquals(0, task1.getTaskData().getOutputContentId() );   
        assertNull( task1.getTaskData().getOutputAccessType() );
        assertNull( task1.getTaskData().getOutputType() );
    }
    
    public void testAddRemoveFault() throws Exception {
        Map<String, Object> vars = fillVariables();
        
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { createdOn = now, activationTime = now,";
        str += "actualOwner = new User('Darth Vader')}),";
        str += "deadlines = new Deadlines(),";
        str += "delegation = new Delegation(),";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [ users['darth'] ]}),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        addTaskResponseHandler.waitTillDone(3000);
        
        long taskId = addTaskResponseHandler.getTaskId();
        
        FaultData faultData = new FaultData();
        faultData.setAccessType(AccessType.Inline);
        faultData.setContent("This is my fault!!!!".getBytes());
        faultData.setFaultName("fault1");
        faultData.setType("text/plain");
        
        BlockingTaskOperationResponseHandler setFaultResponseHandler = new BlockingTaskOperationResponseHandler();
        client.setFault( taskId, "Darth Vader", faultData, setFaultResponseHandler );
        setFaultResponseHandler.waitTillDone(1000);
        assertFalse( setFaultResponseHandler.hasError() );
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertNotSame(task, task1);
        assertFalse(  task.equals( task1) );
       
        long faultContentId = task1.getTaskData().getFaultContentId();
        assertNotSame(0, faultContentId);

        BlockingGetContentResponseHandler getFaultResponseHandler = new BlockingGetContentResponseHandler();
        client.getContent(faultContentId, getFaultResponseHandler);
        assertNotNull(getFaultResponseHandler.getContent());
        Content content = getFaultResponseHandler.getContent();
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
        BlockingTaskOperationResponseHandler deleteFaultResponseHandler = new BlockingTaskOperationResponseHandler();
        client.deleteFault( taskId, "Darth Vader", deleteFaultResponseHandler );
        deleteFaultResponseHandler.waitTillDone( 1000 );
        
        getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        task1 = getTaskResponseHandler.getTask(); 
        assertEquals(0, task1.getTaskData().getFaultContentId() );   
        assertNull( task1.getTaskData().getFaultAccessType() );
        assertNull( task1.getTaskData().getFaultType() );
        assertNull( task1.getTaskData().getFaultName() );
    } 
// do we really need this test here??    
//    public void testSetPriority() throws Exception {
//        TaskServiceTaskAttributesBaseTest.testSetPriority(fillVariables(), client);
//    }
}
