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


import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

import javax.mail.internet.*;
import javax.mail.internet.MimeMessage.RecipientType;

import org.drools.process.instance.impl.DefaultWorkItemManager;
import org.kie.runtime.process.WorkItemManager;
import org.jbpm.task.*;
import org.jbpm.task.TaskService;
import org.jbpm.task.service.*;
import org.jbpm.task.utils.ContentMarshallerHelper;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

public abstract class TaskServiceDeadlinesBaseSyncTest extends BaseTest {

    protected TaskServer server;
    protected TaskService client;
    private Properties conf;
    private Wiser wiser;

    protected void tearDown() throws Exception {
        if( client != null ) { 
            client.disconnect();
        }
        if( server != null ) { 
            server.stop();
        }
        if( wiser != null ) { 
            wiser.stop();
        }
        super.tearDown();
    }

    public void testDelayedEmailNotificationOnDeadline() throws Exception {
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
        
        client.addTask(task, null);
        long taskId = task.getId();

        Content content = new Content();
        
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(params, null);
        content.setContent(marshalledObject.getContent());
        client.setDocumentContent(taskId, content);
        long contentId = content.getId();
        
        content = client.getContent(contentId);
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);

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

        assertTrue(list.contains("tony@domain.com"));
        assertTrue(list.contains("darth@domain.com"));


        MimeMessage msg = ((WiserMessage) getWiser().getMessages().get(0)).getMimeMessage();
        assertEquals(myBody, msg.getContent());
        assertEquals(mySubject, msg.getSubject());
        assertEquals("from@domain.com", ((InternetAddress) msg.getFrom()[0]).getAddress());
        assertEquals("replyTo@domain.com", ((InternetAddress) msg.getReplyTo()[0]).getAddress());
        assertEquals("tony@domain.com", ((InternetAddress) msg.getRecipients(RecipientType.TO)[0]).getAddress());
        assertEquals("darth@domain.com", ((InternetAddress) msg.getRecipients(RecipientType.TO)[1]).getAddress());
    }
    
    public void testDelayedEmailNotificationOnDeadlineContentSingleObject() throws Exception {
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
        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotificationContentSingleObject));
        Task task = (Task) eval(reader, vars);
     
        client.addTask(task, null);
        long taskId = task.getId();

        Content content = new Content();
        ContentData marshalledObject = ContentMarshallerHelper.marshal("'singleobject'", null);
        content.setContent(marshalledObject.getContent());
       
        client.setDocumentContent(taskId, content);
        long contentId = content.getId();
      
        content = client.getContent(contentId);
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        assertEquals("'singleobject'", unmarshallObject.toString());

        // emails should not be set yet
        assertEquals(0, getWiser().getMessages().size());
        Thread.sleep(100);
        // nor yet
        assertEquals(0, getWiser().getMessages().size());
        long time = 0;
        while (getWiser().getMessages().size() != 2 && time < 15000) {
            Thread.sleep(500);
            time = 500;
        }

        // 1 email with two recipients should now exist
        assertEquals(2, getWiser().getMessages().size());
        List<String> list = new ArrayList<String>(2);
        list.add(getWiser().getMessages().get(0).getEnvelopeReceiver());
        list.add(getWiser().getMessages().get(1).getEnvelopeReceiver());

        assertTrue(list.contains("tony@domain.com"));
        assertTrue(list.contains("darth@domain.com"));

        MimeMessage msg = ((WiserMessage) getWiser().getMessages().get(0)).getMimeMessage();
        assertEquals("'singleobject'", msg.getContent());
        assertEquals("'singleobject'", msg.getSubject());
        assertEquals("from@domain.com", ((InternetAddress) msg.getFrom()[0]).getAddress());
        assertEquals("replyTo@domain.com", ((InternetAddress) msg.getReplyTo()[0]).getAddress());
        assertEquals("tony@domain.com", ((InternetAddress) msg.getRecipients(RecipientType.TO)[0]).getAddress());
        assertEquals("darth@domain.com", ((InternetAddress) msg.getRecipients(RecipientType.TO)[1]).getAddress());

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
        client.addTask(task, null);
        long taskId = task.getId();

        Content content = new Content();
        
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(params, null);
        content.setContent(marshalledObject.getContent());
        client.setDocumentContent(taskId, content);
        long contentId = content.getId();
        
        content = client.getContent(contentId);
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);
        
        client.start(taskId, "Administrator");
        client.complete(taskId, "Administrator", null);
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
        task = client.getTask(taskId);
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
        client.addTask(task, null);
        long taskId = task.getId();

        Content content = new Content();
        
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(params, null);
        content.setContent(marshalledObject.getContent());
        client.setDocumentContent(taskId, content);
        long contentId = content.getId();
        
        content = client.getContent(contentId);
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);
        
        client.start(taskId, "Administrator");
        client.fail(taskId, "Administrator", null);
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
        task = client.getTask(taskId);
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
        client.addTask(task, null);
        long taskId = task.getId();

        Content content = new Content();
        
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(params, null);
        content.setContent(marshalledObject.getContent());
        client.setDocumentContent(taskId, content);
        long contentId = content.getId();
        
        content = client.getContent(contentId);
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);
        
        client.skip(taskId, "Administrator");
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
        task = client.getTask(taskId);
        assertEquals(Status.Obsolete, task.getTaskData().getStatus());
        assertEquals(0, task.getDeadlines().getStartDeadlines().size());
        assertEquals(0, task.getDeadlines().getEndDeadlines().size());
    }
         
    public void testDelayedEmailNotificationOnDeadlineTaskExited() throws Exception {
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
        client.addTask(task, null);
        long taskId = task.getId();

        Content content = new Content();
        
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(params, null);
        content.setContent(marshalledObject.getContent());
        client.setDocumentContent(taskId, content);
        long contentId = content.getId();
        
        content = client.getContent(contentId);
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);
        
        client.exit(taskId, "Administrator");
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
        task = client.getTask(taskId);
        assertEquals(Status.Exited, task.getTaskData().getStatus());
        assertEquals(0, task.getDeadlines().getStartDeadlines().size());
        assertEquals(0, task.getDeadlines().getEndDeadlines().size());
    }
    
    //TODO: this test is not working for the local implementation and needs to be fixed  
    public void FIXtestDelayedReassignmentOnDeadline() throws Exception {
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
        Task task = (Task) eval(reader, vars);
        client.addTask(task, null);
        long taskId = task.getId();

        // Shouldn't have re-assigned yet
        Thread.sleep(1000);
        
        
        task = client.getTask(taskId);
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
        
        task = client.getTask(taskId);
        assertEquals(Status.Ready, task.getTaskData().getStatus());
        potentialOwners = task.getPeopleAssignments().getPotentialOwners();

        ids = new ArrayList<String>(potentialOwners.size());
        for (OrganizationalEntity entity : potentialOwners) {
            ids.add(entity.getId());
        }
        assertTrue(ids.contains(users.get("bobba").getId()));
        assertTrue(ids.contains(users.get("jabba").getId()));
    }

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
