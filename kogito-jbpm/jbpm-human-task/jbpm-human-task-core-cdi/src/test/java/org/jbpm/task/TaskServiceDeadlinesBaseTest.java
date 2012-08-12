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
package org.jbpm.task;


import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.inject.Inject;


import org.jbpm.task.deadlines.NotificationListener;
import org.jbpm.task.deadlines.notifications.impl.MockNotificationListener;

import org.jbpm.task.impl.factories.TaskFactory;

import org.jbpm.task.utils.ContentMarshallerHelper;
import org.junit.After;


import static org.junit.Assert.*;
import org.junit.Test;



public abstract class TaskServiceDeadlinesBaseTest extends BaseTest {

    @Inject
    private NotificationListener notificationListener;
    
    
    @After
    public void tearDown(){
        super.tearDown();
        MockNotificationListener.reset();
    }
    
    
    @Test
    public void testDelayedEmailNotificationOnDeadline() throws Exception {

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());
        MockUserInfo userInfo = new MockUserInfo();
        userInfo.getEmails().put(new User("Tony Stark"), "tony@domain.com");
        userInfo.getEmails().put(new User("Darth Vader"), "darth@domain.com");

        userInfo.getLanguages().put(new User("Tony Stark"), "en-UK");
        userInfo.getLanguages().put(new User("Darth Vader"), "en-UK");
        taskService.setUserInfo(userInfo);

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        Task task = (Task) TaskFactory.evalTask(reader, vars);
        
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        Content content = new Content();
        
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(params, null);
        content.setContent(marshalledObject.getContent());
        taskService.addContent(taskId, content);
        long contentId = content.getId();
        
        content = taskService.getContentById(contentId);
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);

        // emails should not be set yet
        //assertEquals(0, getWiser().getMessages().size());
        assertEquals(0, MockNotificationListener.getEventsRecieved().size());
        Thread.sleep(100);

        // nor yet
        assertEquals(0, MockNotificationListener.getEventsRecieved().size());

        long time = 0;
        while (MockNotificationListener.getEventsRecieved().size() != 1 && time < 15000) {
            Thread.sleep(500);
            time += 500;
        }

        // 1 email with two recipients should now exist
        assertEquals(1, MockNotificationListener.getEventsRecieved().size());
        assertEquals(2, MockNotificationListener.getEventsRecieved().get(0).getNotification().getRecipients().size());
        //@TODO: validate events content
//        List<String> list = new ArrayList<String>(2);
//        list.add(getWiser().getMessages().get(0).getEnvelopeReceiver());
//        list.add(getWiser().getMessages().get(1).getEnvelopeReceiver());
//
//        assertTrue(list.contains("tony@domain.com"));
//        assertTrue(list.contains("darth@domain.com"));
//
//
//        MimeMessage msg = ((WiserMessage) getWiser().getMessages().get(0)).getMimeMessage();
//        assertEquals(myBody, msg.getContent());
//        assertEquals(mySubject, msg.getSubject());
//        assertEquals("from@domain.com", ((InternetAddress) msg.getFrom()[0]).getAddress());
//        assertEquals("replyTo@domain.com", ((InternetAddress) msg.getReplyTo()[0]).getAddress());
//        assertEquals("tony@domain.com", ((InternetAddress) msg.getRecipients(RecipientType.TO)[0]).getAddress());
//        assertEquals("darth@domain.com", ((InternetAddress) msg.getRecipients(RecipientType.TO)[1]).getAddress());
    }
    @Test
    public void testDelayedEmailNotificationOnDeadlineContentSingleObject() throws Exception {
        

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());
        MockUserInfo userInfo = new MockUserInfo();
        userInfo.getEmails().put(new User("Tony Stark"), "tony@domain.com");
        userInfo.getEmails().put(new User("Darth Vader"), "darth@domain.com");
        userInfo.getLanguages().put(new User("Tony Stark"), "en-UK");
        userInfo.getLanguages().put(new User("Darth Vader"), "en-UK");
        taskService.setUserInfo(userInfo);

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotificationContentSingleObject));
        Task task = (Task) TaskFactory.evalTask(reader, vars);
     
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        Content content = new Content();
        ContentData marshalledObject = ContentMarshallerHelper.marshal("'singleobject'", null);
        content.setContent(marshalledObject.getContent());
       
        taskService.addContent(taskId, content);
        long contentId = content.getId();
      
        content = taskService.getContentById(contentId);
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        assertEquals("'singleobject'", unmarshallObject.toString());

        // emails should not be set yet
        assertEquals(0, MockNotificationListener.getEventsRecieved().size());
        Thread.sleep(100);
        // nor yet
        assertEquals(0, MockNotificationListener.getEventsRecieved().size());
        long time = 0;
        while (MockNotificationListener.getEventsRecieved().size() != 1 && time < 15000) {
            Thread.sleep(500);
            time += 500;
        }

        // 1 email with two recipients should now exist
        assertEquals(1, MockNotificationListener.getEventsRecieved().size());
        
        //@TODO: validate events content
//        List<String> list = new ArrayList<String>(2);
//        list.add(getWiser().getMessages().get(0).getEnvelopeReceiver());
//        list.add(getWiser().getMessages().get(1).getEnvelopeReceiver());
//
//        assertTrue(list.contains("tony@domain.com"));
//        assertTrue(list.contains("darth@domain.com"));
//
//        MimeMessage msg = ((WiserMessage) getWiser().getMessages().get(0)).getMimeMessage();
//        assertEquals("'singleobject'", msg.getContent());
//        assertEquals("'singleobject'", msg.getSubject());
//        assertEquals("from@domain.com", ((InternetAddress) msg.getFrom()[0]).getAddress());
//        assertEquals("replyTo@domain.com", ((InternetAddress) msg.getReplyTo()[0]).getAddress());
//        assertEquals("tony@domain.com", ((InternetAddress) msg.getRecipients(RecipientType.TO)[0]).getAddress());
//        assertEquals("darth@domain.com", ((InternetAddress) msg.getRecipients(RecipientType.TO)[1]).getAddress());

    }
    @Test
    public void testDelayedEmailNotificationOnDeadlineTaskCompleted() throws Exception {


         Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());
        MockUserInfo userInfo = new MockUserInfo();
        userInfo.getEmails().put(new User("Tony Stark"), "tony@domain.com");
        userInfo.getEmails().put(new User("Darth Vader"), "darth@domain.com");

        userInfo.getLanguages().put(new User("Tony Stark"), "en-UK");
        userInfo.getLanguages().put(new User("Darth Vader"), "en-UK");
        taskService.setUserInfo(userInfo);

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        Task task = (Task) TaskFactory.evalTask(reader, vars, false);
        
        task.getTaskData().setSkipable(true);
        PeopleAssignments assignments = new PeopleAssignments();
        List<OrganizationalEntity> ba = new ArrayList<OrganizationalEntity>();
        ba.add(new User("Administrator"));
        assignments.setBusinessAdministrators(ba);
        
        List<OrganizationalEntity> po = new ArrayList<OrganizationalEntity>();
        po.add(new User("Administrator"));
        assignments.setPotentialOwners(po);
        
        task.setPeopleAssignments(assignments);
        
        TaskFactory.initializeTask(task);
        
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        Content content = new Content();
        
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(params, null);
        content.setContent(marshalledObject.getContent());
        taskService.addContent(taskId, content);
        long contentId = content.getId();
        
        content = taskService.getContentById(contentId);
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);
        
        taskService.start(taskId, "Administrator");
        taskService.complete(taskId, "Administrator", null);
        // emails should not be set yet
        assertEquals(0, MockNotificationListener.getEventsRecieved().size());
        Thread.sleep(100);

        // nor yet
        assertEquals(0, MockNotificationListener.getEventsRecieved().size());

        long time = 0;
        while (MockNotificationListener.getEventsRecieved().size() != 1 && time < 15000) {
            Thread.sleep(500);
            time += 500;
        }

        // no email should ne sent as task was completed before deadline was triggered
        assertEquals(0, MockNotificationListener.getEventsRecieved().size());
        task = taskService.getTaskById(taskId);
        assertEquals(Status.Completed, task.getTaskData().getStatus());
        assertEquals(0, task.getDeadlines().getStartDeadlines().size());
        assertEquals(0, task.getDeadlines().getEndDeadlines().size());
        
        
    }
    @Test
    public void testDelayedEmailNotificationOnDeadlineTaskFailed() throws Exception {
        

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());
        MockUserInfo userInfo = new MockUserInfo();
        userInfo.getEmails().put(new User("Tony Stark"), "tony@domain.com");
        userInfo.getEmails().put(new User("Darth Vader"), "darth@domain.com");

        userInfo.getLanguages().put(new User("Tony Stark"), "en-UK");
        userInfo.getLanguages().put(new User("Darth Vader"), "en-UK");
        taskService.setUserInfo(userInfo);

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        Task task = (Task) TaskFactory.evalTask(reader, vars, false);
        
        task.getTaskData().setSkipable(true);
        PeopleAssignments assignments = new PeopleAssignments();
        List<OrganizationalEntity> ba = new ArrayList<OrganizationalEntity>();
        ba.add(new User("Administrator"));
        assignments.setBusinessAdministrators(ba);
        
        List<OrganizationalEntity> po = new ArrayList<OrganizationalEntity>();
        po.add(new User("Administrator"));
        assignments.setPotentialOwners(po);
        
        task.setPeopleAssignments(assignments);
        
        TaskFactory.initializeTask(task);
        
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        Content content = new Content();
        
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(params, null);
        content.setContent(marshalledObject.getContent());
        taskService.addContent(taskId, content);
        long contentId = content.getId();
        
        content = taskService.getContentById(contentId);
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);
        
        taskService.start(taskId, "Administrator");
        taskService.fail(taskId, "Administrator", null);
        // emails should not be set yet
        assertEquals(0, MockNotificationListener.getEventsRecieved().size());
        Thread.sleep(100);

        // nor yet
        assertEquals(0, MockNotificationListener.getEventsRecieved().size());

        long time = 0;
        while (MockNotificationListener.getEventsRecieved().size() != 1 && time < 15000) {
            Thread.sleep(500);
            time += 500;
        }

        // no email should ne sent as task was completed before deadline was triggered
        assertEquals(0, MockNotificationListener.getEventsRecieved().size());
        task = taskService.getTaskById(taskId);
        assertEquals(Status.Failed, task.getTaskData().getStatus());
        assertEquals(0, task.getDeadlines().getStartDeadlines().size());
        assertEquals(0, task.getDeadlines().getEndDeadlines().size());
    }
    @Test
    public void testDelayedEmailNotificationOnDeadlineTaskSkipped() throws Exception {

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());
        MockUserInfo userInfo = new MockUserInfo();
        userInfo.getEmails().put(new User("Tony Stark"), "tony@domain.com");
        userInfo.getEmails().put(new User("Darth Vader"), "darth@domain.com");

        userInfo.getLanguages().put(new User("Tony Stark"), "en-UK");
        userInfo.getLanguages().put(new User("Darth Vader"), "en-UK");
        taskService.setUserInfo(userInfo);

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        Task task = (Task) TaskFactory.evalTask(reader, vars);
        
        task.getTaskData().setSkipable(true);
        PeopleAssignments assignments = new PeopleAssignments();
        List<OrganizationalEntity> ba = new ArrayList<OrganizationalEntity>();
        ba.add(new User("Administrator"));
        assignments.setBusinessAdministrators(ba);
        
        List<OrganizationalEntity> po = new ArrayList<OrganizationalEntity>();
        po.add(new User("Administrator"));
        assignments.setPotentialOwners(po);
        
        task.setPeopleAssignments(assignments);
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        Content content = new Content();
        
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(params, null);
        content.setContent(marshalledObject.getContent());
        taskService.addContent(taskId, content);
        long contentId = content.getId();
        
        content = taskService.getContentById(contentId);
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);
        
        taskService.skip(taskId, "Administrator");
        // emails should not be set yet
        assertEquals(0, MockNotificationListener.getEventsRecieved().size());
        Thread.sleep(100);

        // nor yet
        assertEquals(0, MockNotificationListener.getEventsRecieved().size());

        long time = 0;
        while (MockNotificationListener.getEventsRecieved().size() != 1 && time < 15000) {
            Thread.sleep(500);
            time += 500;
        }

        // no email should ne sent as task was completed before deadline was triggered
        assertEquals(0, MockNotificationListener.getEventsRecieved().size());
        task = taskService.getTaskById(taskId);
        assertEquals(Status.Obsolete, task.getTaskData().getStatus());
        assertEquals(0, task.getDeadlines().getStartDeadlines().size());
        assertEquals(0, task.getDeadlines().getEndDeadlines().size());
    }
    @Test    
    public void testDelayedEmailNotificationOnDeadlineTaskExited() throws Exception {

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());
        
        MockUserInfo userInfo = new MockUserInfo();
        userInfo.getEmails().put(new User("Tony Stark"), "tony@domain.com");
        userInfo.getEmails().put(new User("Darth Vader"), "darth@domain.com");

        userInfo.getLanguages().put(new User("Tony Stark"), "en-UK");
        userInfo.getLanguages().put(new User("Darth Vader"), "en-UK");
        taskService.setUserInfo(userInfo);

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        Task task = (Task) TaskFactory.evalTask(reader, vars);
        
        task.getTaskData().setSkipable(true);
        PeopleAssignments assignments = new PeopleAssignments();
        List<OrganizationalEntity> ba = new ArrayList<OrganizationalEntity>();
        ba.add(new User("Administrator"));
        assignments.setBusinessAdministrators(ba);
        
        List<OrganizationalEntity> po = new ArrayList<OrganizationalEntity>();
        po.add(new User("Administrator"));
        assignments.setPotentialOwners(po);
        
        task.setPeopleAssignments(assignments);
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        Content content = new Content();
        
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(params, null);
        content.setContent(marshalledObject.getContent());
        taskService.addContent(taskId, content);
        long contentId = content.getId();
        
        content = taskService.getContentById(contentId);
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);
        
        taskService.exit(taskId, "Administrator");
        // emails should not be set yet
        assertEquals(0, MockNotificationListener.getEventsRecieved().size());
        Thread.sleep(100);

        // nor yet
        assertEquals(0, MockNotificationListener.getEventsRecieved().size());

        long time = 0;
        while (MockNotificationListener.getEventsRecieved().size() != 1 && time < 15000) {
            Thread.sleep(500);
            time += 500;
        }

        // no email should ne sent as task was completed before deadline was triggered
        assertEquals(0, MockNotificationListener.getEventsRecieved().size());
        task = taskService.getTaskById(taskId);
        assertEquals(Status.Exited, task.getTaskData().getStatus());
        assertEquals(0, task.getDeadlines().getStartDeadlines().size());
        assertEquals(0, task.getDeadlines().getEndDeadlines().size());
    }


      @Test
    public void testDelayedReassignmentOnDeadline() throws Exception {


        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());
        MockUserInfo userInfo = new MockUserInfo();
        userInfo.getEmails().put(new User("Tony Stark"), "tony@domain.com");
        userInfo.getEmails().put(new User("Luke Cage"), "luke@domain.com");
        userInfo.getEmails().put(new User("Bobba Fet"), "luke@domain.com");
        userInfo.getEmails().put(new User("Jabba Hutt"), "luke@domain.com");

        userInfo.getLanguages().put(new User("Tony Stark"), "en-UK");
        userInfo.getLanguages().put(new User("Luke Cage"), "en-UK");
        userInfo.getLanguages().put(new User("Bobba Fet"), "en-UK");
        userInfo.getLanguages().put(new User("Jabba Hutt"), "en-UK");
        taskService.setUserInfo(userInfo);

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithReassignment));
        Task task = (Task) TaskFactory.evalTask(reader, vars);
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        // Shouldn't have re-assigned yet
        Thread.sleep(1000);
        
        
        task = taskService.getTaskById(taskId);
        List<OrganizationalEntity> potentialOwners = task.getPeopleAssignments().getPotentialOwners();
        List<String> ids = new ArrayList<String>(potentialOwners.size());
        for (OrganizationalEntity entity : potentialOwners) {
            ids.add(entity.getId());
        }
        assertTrue(ids.contains("Tony Stark"));
        assertTrue(ids.contains("Luke Cage"));

        // should have re-assigned by now
        long time = 0;
        while (MockNotificationListener.getEventsRecieved().size() != 1 && time < 15000) {
            Thread.sleep(500);
            time += 500;
        }
        
        task = taskService.getTaskById(taskId);
        assertEquals(Status.Ready, task.getTaskData().getStatus());
        potentialOwners = task.getPeopleAssignments().getPotentialOwners();

        ids = new ArrayList<String>(potentialOwners.size());
        for (OrganizationalEntity entity : potentialOwners) {
            ids.add(entity.getId());
        }
        assertTrue(ids.contains("Bobba Fet"));
        assertTrue(ids.contains("Jabba Hutt"));
    }

   
}
