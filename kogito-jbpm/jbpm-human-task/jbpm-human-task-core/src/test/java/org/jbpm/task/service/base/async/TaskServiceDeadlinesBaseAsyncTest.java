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

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.drools.process.instance.impl.DefaultWorkItemManager;
import org.kie.runtime.process.WorkItemManager;
import org.jbpm.task.AsyncTaskService;
import org.jbpm.task.BaseTest;
import org.jbpm.task.Content;
import org.jbpm.task.MockUserInfo;
import org.jbpm.task.MvelFilePath;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.PeopleAssignments;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.User;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.DefaultEscalatedDeadlineHandler;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.responsehandlers.BlockingAddTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetContentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingSetContentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.utils.ContentMarshallerHelper;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

public abstract class TaskServiceDeadlinesBaseAsyncTest extends BaseTest {

    protected TaskServer server;
    protected AsyncTaskService client;
    private Properties conf;
    private Wiser wiser;

    private static String emailAddressTony = "tony@domain.com"; 
    private static String emailAddressDarth = "darth@domain.com"; 
    

    protected void tearDown() throws Exception {
        client.disconnect();
        server.stop();
        getWiser().stop();
        super.tearDown();
    }

    public void testDelayedEmailNotificationOnDeadline() throws Exception {
        Map<String, Object> vars = fillVariables();

        DefaultEscalatedDeadlineHandler notificationHandler = new DefaultEscalatedDeadlineHandler(getConf());
        WorkItemManager manager = new DefaultWorkItemManager(null);
        notificationHandler.setManager(manager);
        
        MockUserInfo userInfo = new MockUserInfo();
        userInfo.getEmails().put( users.get("tony"), emailAddressTony);
        userInfo.getEmails().put( users.get("darth"), emailAddressDarth );

        userInfo.getLanguages().put(users.get("tony"), "en-UK");
        userInfo.getLanguages().put(users.get("darth"), "en-UK");
        notificationHandler.setUserInfo(userInfo);

        taskService.setEscalatedDeadlineHandler(notificationHandler);

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        Task task = (Task) eval(reader, vars);

        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        client.addTask(task, null, addTaskResponseHandler);
        long taskId = addTaskResponseHandler.getTaskId();

        Content content = new Content();
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData condantData = ContentMarshallerHelper.marshal(params, null);
        
        content.setContent(condantData.getContent());
        BlockingSetContentResponseHandler setContentResponseHandler = new BlockingSetContentResponseHandler();
        client.setDocumentContent(taskId,content , setContentResponseHandler);
        long contentId = setContentResponseHandler.getContentId();
        BlockingGetContentResponseHandler getResponseHandler = new BlockingGetContentResponseHandler();
        client.getContent(contentId, getResponseHandler);
        content = getResponseHandler.getContent();
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshalledObject);

        // emails should not be set yet
        assertEquals(0, getWiser().getMessages().size());
        Thread.sleep(100);

        // nor yet
        assertEquals(0, getWiser().getMessages().size());

        long time = 0;
        while (getWiser().getMessages().size() != 2 && time < 15000) {
            Thread.sleep(500);
            time += 500;
        }

        // 1 email with two recipients should now exist
        assertEquals(2, getWiser().getMessages().size());

        List<String> list = new ArrayList<String>(2);
        list.add(getWiser().getMessages().get(0).getEnvelopeReceiver());
        list.add(getWiser().getMessages().get(1).getEnvelopeReceiver());

        assertTrue( list.contains(emailAddressTony));
        assertTrue( list.contains(emailAddressDarth));

        MimeMessage msg = (( WiserMessage  ) getWiser().getMessages().get( 0 )).getMimeMessage();
        assertEquals( myBody, msg.getContent() );
        assertEquals( mySubject, msg.getSubject() );
        assertEquals( "from@domain.com", ((InternetAddress)msg.getFrom()[0]).getAddress() );
        assertEquals( "replyTo@domain.com", ((InternetAddress)msg.getReplyTo()[0]).getAddress() );
        boolean tonyMatched = false;
        boolean darthMatched = false;
        for( int i = 0; i < msg.getRecipients(RecipientType.TO).length; ++i ) { 
            String emailAddress = ((InternetAddress)msg.getRecipients( RecipientType.TO )[i]).getAddress(); 
           if( "tony@domain.com".equals(emailAddress) ) { 
               tonyMatched = true;
    }
           else if( "darth@domain.com".equals(emailAddress) ) { 
              darthMatched = true; 
           }
        }
        assertTrue("Could not find tony in recipients list.", tonyMatched);
        assertTrue("Could not find darth in recipients list.", darthMatched);
    }

    public void testDelayedReassignmentOnDeadline() throws Exception {
        Map<String, Object> vars = fillVariables();

        DefaultEscalatedDeadlineHandler notificationHandler = new DefaultEscalatedDeadlineHandler(getConf());
        WorkItemManager manager = new DefaultWorkItemManager(null);
        notificationHandler.setManager(manager);

        MockUserInfo userInfo = new MockUserInfo();
        userInfo.getEmails().put(users.get("tony"), "tony@domain.com");
        userInfo.getEmails().put(users.get("luke"), "luke@domain.com");
        userInfo.getEmails().put(users.get("bobba"), "luke@domain.com");
        userInfo.getEmails().put(users.get("jabba"), "luke@domain.com");

        userInfo.getLanguages().put(users.get("tony"), "en-UK");
        userInfo.getLanguages().put(users.get("luke"), "en-UK");
        userInfo.getLanguages().put(users.get("bobba"), "en-UK");
        userInfo.getLanguages().put(users.get("jabba"), "en-UK");
        notificationHandler.setUserInfo(userInfo);

        taskService.setEscalatedDeadlineHandler(notificationHandler);

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithReassignment));
        Task task = ( Task )  eval( reader, vars );               

        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        client.addTask( task, null, addTaskResponseHandler );
        long taskId = addTaskResponseHandler.getTaskId();

        // Shouldn't have re-assigned yet
        Thread.sleep(1000);
        BlockingGetTaskResponseHandler getTaskHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, getTaskHandler);
        task = getTaskHandler.getTask();
        List<OrganizationalEntity> potentialOwners = task.getPeopleAssignments().getPotentialOwners();
        List<String> ids = new ArrayList<String>(potentialOwners.size());
        for (OrganizationalEntity entity : potentialOwners) {
            ids.add(entity.getId());
        }
        assertTrue(ids.contains(users.get("tony").getId()));
        assertTrue(ids.contains(users.get("luke").getId()));

        // should have re-assigned by now
        long time = 0;
        while (getWiser().getMessages().size() != 2 && time < 15000) {
            Thread.sleep(500);
            time += 500;
        }

        getTaskHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, getTaskHandler);
        task = getTaskHandler.getTask();
        assertEquals(Status.Ready, task.getTaskData().getStatus());
        potentialOwners = task.getPeopleAssignments().getPotentialOwners();
        System.out.println(potentialOwners);
        ids = new ArrayList<String>(potentialOwners.size());
        for (OrganizationalEntity entity : potentialOwners) {
            ids.add(entity.getId());
        }
        assertTrue(ids.contains(users.get("bobba").getId()));
        assertTrue(ids.contains(users.get("jabba").getId()));
    }
    
    public void testDelayedEmailNotificationOnDeadlineTaskCompleted() throws Exception {
        Map<String, Object> vars = fillVariables();

        DefaultEscalatedDeadlineHandler notificationHandler = new DefaultEscalatedDeadlineHandler(getConf());
        WorkItemManager manager = new DefaultWorkItemManager(null);
        notificationHandler.setManager(manager);

        MockUserInfo userInfo = new MockUserInfo();
        userInfo.getEmails().put(users.get("tony"), "tony@domain.com");
        userInfo.getEmails().put(users.get("darth"), "darth@domain.com");

        userInfo.getLanguages().put(users.get("tony"), "en-UK");
        userInfo.getLanguages().put(users.get("darth"), "en-UK");
        notificationHandler.setUserInfo(userInfo);

        taskService.setEscalatedDeadlineHandler(notificationHandler);

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        Task task = (Task) eval(reader, vars);
        
        task.getTaskData().setSkipable(true);
        PeopleAssignments assignments = new PeopleAssignments();
        List<OrganizationalEntity> ba = new ArrayList<OrganizationalEntity>();
        ba.add(new User("Administrator"));
        assignments.setBusinessAdministrators(ba);
        
        List<OrganizationalEntity> po = new ArrayList<OrganizationalEntity>();
        po.add(new User("Administrator"));
        assignments.setPotentialOwners(po);
        
        task.setPeopleAssignments(assignments);
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();            
        client.addTask( task, null, addTaskResponseHandler );
        long taskId = addTaskResponseHandler.getTaskId();

        Content content = new Content();
        
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(params, null);
        content.setContent(marshalledObject.getContent());
        
        BlockingSetContentResponseHandler setContentResponseHandler = new BlockingSetContentResponseHandler();
        client.setDocumentContent(taskId,content , setContentResponseHandler);
        long contentId = setContentResponseHandler.getContentId();
        BlockingGetContentResponseHandler getResponseHandler = new BlockingGetContentResponseHandler();
        client.getContent(contentId, getResponseHandler);
        content = getResponseHandler.getContent();
        
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshalledObject);
        BlockingTaskOperationResponseHandler startTaskResponseHandler = new BlockingTaskOperationResponseHandler();
        client.start(taskId, "Administrator", startTaskResponseHandler);
        startTaskResponseHandler.waitTillDone(2000);
        BlockingTaskOperationResponseHandler completeTaskResponseHandler = new BlockingTaskOperationResponseHandler();
        client.complete(taskId, "Administrator", null, completeTaskResponseHandler);
        completeTaskResponseHandler.waitTillDone(2000);
        // emails should not be set yet
        assertEquals(0, getWiser().getMessages().size());
        Thread.sleep(100);

        // nor yet
        assertEquals(0, getWiser().getMessages().size());

        long time = 0;
        while (getWiser().getMessages().size() != 2 && time < 15000) {
            Thread.sleep(500);
            time += 500;
        }

        // no email should ne sent as task was completed before deadline was triggered
        assertEquals(0, getWiser().getMessages().size());
        BlockingGetTaskResponseHandler getTaskHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, getTaskHandler);
        task = getTaskHandler.getTask();
        assertEquals(Status.Completed, task.getTaskData().getStatus());
        assertEquals(0, task.getDeadlines().getStartDeadlines().size());
        assertEquals(0, task.getDeadlines().getEndDeadlines().size());
        
        
    }
    
    public void testDelayedEmailNotificationOnDeadlineTaskFailed() throws Exception {
        Map<String, Object> vars = fillVariables();

        DefaultEscalatedDeadlineHandler notificationHandler = new DefaultEscalatedDeadlineHandler(getConf());
        WorkItemManager manager = new DefaultWorkItemManager(null);
        notificationHandler.setManager(manager);

        MockUserInfo userInfo = new MockUserInfo();
        userInfo.getEmails().put(users.get("tony"), "tony@domain.com");
        userInfo.getEmails().put(users.get("darth"), "darth@domain.com");

        userInfo.getLanguages().put(users.get("tony"), "en-UK");
        userInfo.getLanguages().put(users.get("darth"), "en-UK");
        notificationHandler.setUserInfo(userInfo);

        taskService.setEscalatedDeadlineHandler(notificationHandler);

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        Task task = (Task) eval(reader, vars);
        
        task.getTaskData().setSkipable(true);
        PeopleAssignments assignments = new PeopleAssignments();
        List<OrganizationalEntity> ba = new ArrayList<OrganizationalEntity>();
        ba.add(new User("Administrator"));
        assignments.setBusinessAdministrators(ba);
        
        List<OrganizationalEntity> po = new ArrayList<OrganizationalEntity>();
        po.add(new User("Administrator"));
        assignments.setPotentialOwners(po);
        
        task.setPeopleAssignments(assignments);
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();            
        client.addTask( task, null, addTaskResponseHandler );
        long taskId = addTaskResponseHandler.getTaskId();

        Content content = new Content();
        
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(params, null);
        content.setContent(marshalledObject.getContent());
        
        BlockingSetContentResponseHandler setContentResponseHandler = new BlockingSetContentResponseHandler();
        client.setDocumentContent(taskId,content , setContentResponseHandler);
        long contentId = setContentResponseHandler.getContentId();
        BlockingGetContentResponseHandler getResponseHandler = new BlockingGetContentResponseHandler();
        client.getContent(contentId, getResponseHandler);
        content = getResponseHandler.getContent();
        
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshalledObject);
        
        BlockingTaskOperationResponseHandler startTaskResponseHandler = new BlockingTaskOperationResponseHandler(); 
        client.start(taskId, "Administrator", startTaskResponseHandler);
        startTaskResponseHandler.waitTillDone(2000);
        BlockingTaskOperationResponseHandler failTaskResponseHandler = new BlockingTaskOperationResponseHandler();
        client.fail(taskId, "Administrator", null, failTaskResponseHandler);
        failTaskResponseHandler.waitTillDone(2000);
        // emails should not be set yet
        assertEquals(0, getWiser().getMessages().size());
        Thread.sleep(100);

        // nor yet
        assertEquals(0, getWiser().getMessages().size());

        long time = 0;
        while (getWiser().getMessages().size() != 2 && time < 15000) {
            Thread.sleep(500);
            time += 500;
        }

        // no email should ne sent as task was completed before deadline was triggered
        assertEquals(0, getWiser().getMessages().size());
        BlockingGetTaskResponseHandler getTaskHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, getTaskHandler);
        task = getTaskHandler.getTask();
        assertEquals(Status.Failed, task.getTaskData().getStatus());
        assertEquals(0, task.getDeadlines().getStartDeadlines().size());
        assertEquals(0, task.getDeadlines().getEndDeadlines().size());
    }

    public void testDelayedEmailNotificationOnDeadlineTaskSkipped() throws Exception {
        Map<String, Object> vars = fillVariables();

        DefaultEscalatedDeadlineHandler notificationHandler = new DefaultEscalatedDeadlineHandler(getConf());
        WorkItemManager manager = new DefaultWorkItemManager(null);
        notificationHandler.setManager(manager);

        MockUserInfo userInfo = new MockUserInfo();
        userInfo.getEmails().put(users.get("tony"), "tony@domain.com");
        userInfo.getEmails().put(users.get("darth"), "darth@domain.com");

        userInfo.getLanguages().put(users.get("tony"), "en-UK");
        userInfo.getLanguages().put(users.get("darth"), "en-UK");
        notificationHandler.setUserInfo(userInfo);

        taskService.setEscalatedDeadlineHandler(notificationHandler);

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        Task task = (Task) eval(reader, vars);
        
        task.getTaskData().setSkipable(true);
        PeopleAssignments assignments = new PeopleAssignments();
        List<OrganizationalEntity> ba = new ArrayList<OrganizationalEntity>();
        ba.add(new User("Administrator"));
        assignments.setBusinessAdministrators(ba);
        
        List<OrganizationalEntity> po = new ArrayList<OrganizationalEntity>();
        po.add(new User("Administrator"));
        assignments.setPotentialOwners(po);
        
        task.setPeopleAssignments(assignments);
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();            
        client.addTask( task, null, addTaskResponseHandler );
        long taskId = addTaskResponseHandler.getTaskId();

        Content content = new Content();
        
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(params, null);
        content.setContent(marshalledObject.getContent());

        BlockingSetContentResponseHandler setContentResponseHandler = new BlockingSetContentResponseHandler();
        client.setDocumentContent(taskId,content , setContentResponseHandler);
        long contentId = setContentResponseHandler.getContentId();
        BlockingGetContentResponseHandler getResponseHandler = new BlockingGetContentResponseHandler();
        client.getContent(contentId, getResponseHandler);
        content = getResponseHandler.getContent();
        
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshalledObject);
        
        BlockingTaskOperationResponseHandler skipTaskResponseHandler = new BlockingTaskOperationResponseHandler(); 
        client.skip(taskId, "Administrator", skipTaskResponseHandler);
        skipTaskResponseHandler.waitTillDone(2000);
        // emails should not be set yet
        assertEquals(0, getWiser().getMessages().size());
        Thread.sleep(100);

        // nor yet
        assertEquals(0, getWiser().getMessages().size());

        long time = 0;
        while (getWiser().getMessages().size() != 2 && time < 15000) {
            Thread.sleep(500);
            time += 500;
        }

        // no email should ne sent as task was completed before deadline was triggered
        assertEquals(0, getWiser().getMessages().size());
        BlockingGetTaskResponseHandler getTaskHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, getTaskHandler);
        task = getTaskHandler.getTask();
        assertEquals(Status.Obsolete, task.getTaskData().getStatus());
        assertEquals(0, task.getDeadlines().getStartDeadlines().size());
        assertEquals(0, task.getDeadlines().getEndDeadlines().size());
    }
         
    public void testDelayedEmailNotificationOnDeadlineTaskExited() throws Exception {
        Map vars = new HashMap();
        vars.put("users", users);
        vars.put("groups", groups);
        vars.put("now", new Date());

        DefaultEscalatedDeadlineHandler notificationHandler = new DefaultEscalatedDeadlineHandler(getConf());
        WorkItemManager manager = new DefaultWorkItemManager(null);
        notificationHandler.setManager(manager);

        MockUserInfo userInfo = new MockUserInfo();
        userInfo.getEmails().put(users.get("tony"), "tony@domain.com");
        userInfo.getEmails().put(users.get("darth"), "darth@domain.com");

        userInfo.getLanguages().put(users.get("tony"), "en-UK");
        userInfo.getLanguages().put(users.get("darth"), "en-UK");
        notificationHandler.setUserInfo(userInfo);

        taskService.setEscalatedDeadlineHandler(notificationHandler);

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        Task task = (Task) eval(reader, vars);
        
        task.getTaskData().setSkipable(true);
        PeopleAssignments assignments = new PeopleAssignments();
        List<OrganizationalEntity> ba = new ArrayList<OrganizationalEntity>();
        ba.add(new User("Administrator"));
        assignments.setBusinessAdministrators(ba);
        
        List<OrganizationalEntity> po = new ArrayList<OrganizationalEntity>();
        po.add(new User("Administrator"));
        assignments.setPotentialOwners(po);
        
        task.setPeopleAssignments(assignments);
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();            
        client.addTask( task, null, addTaskResponseHandler );
        long taskId = addTaskResponseHandler.getTaskId();

        Content content = new Content();
        
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(params, null);
        content.setContent(marshalledObject.getContent());

        BlockingSetContentResponseHandler setContentResponseHandler = new BlockingSetContentResponseHandler();
        client.setDocumentContent(taskId,content , setContentResponseHandler);
        long contentId = setContentResponseHandler.getContentId();
        BlockingGetContentResponseHandler getResponseHandler = new BlockingGetContentResponseHandler();
        client.getContent(contentId, getResponseHandler);
        content = getResponseHandler.getContent();
        
        Object unmarshalledObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshalledObject);
        
        BlockingTaskOperationResponseHandler exitTaskResponseHandler = new BlockingTaskOperationResponseHandler(); 
        client.exit(taskId, "Administrator", exitTaskResponseHandler);
        exitTaskResponseHandler.waitTillDone(2000);
        // emails should not be set yet
        assertEquals(0, getWiser().getMessages().size());
        Thread.sleep(100);

        // nor yet
        assertEquals(0, getWiser().getMessages().size());

        long time = 0;
        while (getWiser().getMessages().size() != 2 && time < 15000) {
            Thread.sleep(500);
            time += 500;
        }

        // no email should ne sent as task was completed before deadline was triggered
        assertEquals(0, getWiser().getMessages().size());
        BlockingGetTaskResponseHandler getTaskHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, getTaskHandler);
        task = getTaskHandler.getTask();
        assertEquals(Status.Exited, task.getTaskData().getStatus());
        assertEquals(0, task.getDeadlines().getStartDeadlines().size());
        assertEquals(0, task.getDeadlines().getEndDeadlines().size());
    }

//	public void setClient(TaskClient client) {
//		this.client = client;
//	}
//
//	public TaskClient getClient() {
//		return client;
//	}
    public void setConf(Properties conf) {
        this.conf = conf;
    }

    public Properties getConf() {
        return conf;
    }

    public void setWiser(Wiser wiser) {
        this.wiser = wiser;
    }

    public Wiser getWiser() {
        return wiser;
    }
}
