/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.services.task;


import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.test.listener.task.CountDownTaskEventListener;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.junit.Test;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.model.InternalContent;
import org.kie.internal.task.api.model.InternalOrganizationalEntity;
import org.kie.internal.task.api.model.InternalPeopleAssignments;
import org.kie.internal.task.api.model.InternalTask;
import org.kie.internal.task.api.model.InternalTaskData;
import org.kie.internal.utils.ChainedProperties;
import org.kie.internal.utils.ClassLoaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import static org.junit.Assert.*;


/**
 * All users are defined as part of userinfo.propeties file that is utilized by PropertyUserInfoImpl
 *
 */

public abstract class EmailDeadlinesBaseTest extends HumanTaskServicesBaseTest {
    private static final Logger logger = LoggerFactory.getLogger(EmailDeadlinesBaseTest.class);
    
    private Wiser wiser;

    public void setup() {
        final ChainedProperties props = ChainedProperties.getChainedProperties( "email.conf", ClassLoaderUtil.getClassLoader( null, getClass(), false ));

        wiser = new Wiser();
        wiser.setHostname(props.getProperty( "mail.smtp.host", "localhost" ));
        wiser.setPort(Integer.parseInt(props.getProperty("mail.smtp.port", "2345")));
        wiser.start();
        try {
        	Thread.sleep(1000);
        } catch (Throwable t) {
        	// Do nothing
        }
    }
    
    
    public void tearDown(){
        if (wiser != null) {
            wiser.stop();
            try {
            	Thread.sleep(1000);
            } catch (Throwable t) {
            	// Do nothing
            }
        }
        super.tearDown();
    }

    protected Wiser getWiser() {
        return this.wiser;
    }

    @Test(timeout=10000)    
    public void testDelayedEmailNotificationOnDeadline() throws Exception {  
        CountDownTaskEventListener countDownListener = new CountDownTaskEventListener(1, false, true);
        addCountDownListner(countDownListener);
        
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        Task task = (Task) TaskFactory.evalTask(reader, vars);
        
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        InternalContent content = (InternalContent) TaskModelProvider.getFactory().newContent();
        
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(task, params, null);
        content.setContent(marshalledObject.getContent());
        taskService.addContent(taskId, content);
        long contentId = content.getId();
        
        content = (InternalContent) taskService.getContentById(contentId);
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);

        // emails should not be set yet
        assertEquals(0, getWiser().getMessages().size());
        
        countDownListener.waitTillCompleted();
        
        for (WiserMessage msg : getWiser().getMessages()) {
            logger.info(msg.getEnvelopeReceiver());
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

        final Address[] recipients = msg.getRecipients(RecipientType.TO);
        assertNotNull(recipients);
        assertTrue(recipients.length == 2);
        list = new ArrayList<String>(2);
        list.add(((InternetAddress) recipients[0]).getAddress());
        list.add(((InternetAddress) recipients[1]).getAddress());
        assertTrue(list.contains("tony@domain.com"));
        assertTrue(list.contains("darth@domain.com"));
    }
    
    @Test(timeout=10000)     
    public void testDelayedEmailNotificationOnDeadlineContentSingleObject() throws Exception {
        CountDownTaskEventListener countDownListener = new CountDownTaskEventListener(1, false, true);
        addCountDownListner(countDownListener);
        
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotificationContentSingleObject));
        Task task = (Task) TaskFactory.evalTask(reader, vars);
     
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        InternalContent content = (InternalContent) TaskModelProvider.getFactory().newContent();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(task, "'singleobject'", null);
        content.setContent(marshalledObject.getContent());
       
        taskService.addContent(taskId, content);
        long contentId = content.getId();
      
        content = (InternalContent) taskService.getContentById(contentId);
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        assertEquals("'singleobject'", unmarshallObject.toString());

        // emails should not be set yet
        assertEquals(0, getWiser().getMessages().size());
        
        countDownListener.waitTillCompleted();

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

        final Address[] recipients = msg.getRecipients(RecipientType.TO);
        assertNotNull(recipients);
        assertTrue(recipients.length == 2);
        list = new ArrayList<String>(2);
        list.add(((InternetAddress) recipients[0]).getAddress());
        list.add(((InternetAddress) recipients[1]).getAddress());
        assertTrue(list.contains("tony@domain.com"));
        assertTrue(list.contains("darth@domain.com"));
    }

    @Test(timeout=10000)     
    public void testDelayedEmailNotificationOnDeadlineTaskCompleted() throws Exception {
        CountDownTaskEventListener countDownListener = new CountDownTaskEventListener(1, false, true);
        addCountDownListner(countDownListener);
        
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        InternalTask task = (InternalTask) TaskFactory.evalTask(reader, vars);
        
        ((InternalTaskData) task.getTaskData()).setSkipable(true);
        InternalPeopleAssignments assignments = (InternalPeopleAssignments) TaskModelProvider.getFactory().newPeopleAssignments();
        List<OrganizationalEntity> ba = new ArrayList<OrganizationalEntity>();
        User user = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) user).setId("Administrator");
        ba.add(user);
        assignments.setBusinessAdministrators(ba);
        
        List<OrganizationalEntity> po = new ArrayList<OrganizationalEntity>();
        User user2 = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) user2).setId("Administrator");        
        po.add(user2);
        assignments.setPotentialOwners(po);
        
        task.setPeopleAssignments(assignments);
        
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        InternalContent content = (InternalContent) TaskModelProvider.getFactory().newContent();
        
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(task, params, null);
        content.setContent(marshalledObject.getContent());
        taskService.addContent(taskId, content);
        long contentId = content.getId();
        
        content = (InternalContent) taskService.getContentById(contentId);
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);
        
        taskService.start(taskId, "Administrator");
        taskService.complete(taskId, "Administrator", null);
        // emails should not be set yet
        assertEquals(0, getWiser().getMessages().size());
        countDownListener.waitTillCompleted();

        // no email should ne sent as task was completed before deadline was triggered
        assertEquals(0, getWiser().getMessages().size());
        task = (InternalTask) taskService.getTaskById(taskId);
        assertEquals(Status.Completed, task.getTaskData().getStatus());
        assertEquals(0, task.getDeadlines().getStartDeadlines().size());
        assertEquals(0, task.getDeadlines().getEndDeadlines().size());       
    }

    @Test(timeout=10000)     
    public void testDelayedEmailNotificationOnDeadlineTaskFailed() throws Exception {
        CountDownTaskEventListener countDownListener = new CountDownTaskEventListener(1, false, true);
        addCountDownListner(countDownListener);
        
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        InternalTask task = (InternalTask) TaskFactory.evalTask(reader, vars);
        
        ((InternalTaskData) task.getTaskData()).setSkipable(true);
        InternalPeopleAssignments assignments = (InternalPeopleAssignments) TaskModelProvider.getFactory().newPeopleAssignments();
        List<OrganizationalEntity> ba = new ArrayList<OrganizationalEntity>();
        User user = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) user).setId("Administrator");
        ba.add(user);
        assignments.setBusinessAdministrators(ba);
        
        List<OrganizationalEntity> po = new ArrayList<OrganizationalEntity>();
        User user2 = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) user2).setId("Administrator");        
        po.add(user2);
        assignments.setPotentialOwners(po);
        
        task.setPeopleAssignments(assignments);
        
        
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        InternalContent content = (InternalContent) TaskModelProvider.getFactory().newContent();
        
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(task, params, null);
        content.setContent(marshalledObject.getContent());
        taskService.addContent(taskId, content);
        long contentId = content.getId();
        
        content = (InternalContent) taskService.getContentById(contentId);
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);
        
        taskService.start(taskId, "Administrator");
        taskService.fail(taskId, "Administrator", null);
        // emails should not be set yet
        assertEquals(0, getWiser().getMessages().size());
        
        countDownListener.waitTillCompleted();

        // no email should ne sent as task was completed before deadline was triggered
        assertEquals(0, getWiser().getMessages().size());
        task = (InternalTask) taskService.getTaskById(taskId);
        assertEquals(Status.Failed, task.getTaskData().getStatus());
        assertEquals(0, task.getDeadlines().getStartDeadlines().size());
        assertEquals(0, task.getDeadlines().getEndDeadlines().size());
    }

    @Test(timeout=10000)     
    public void testDelayedEmailNotificationOnDeadlineTaskSkipped() throws Exception {
        CountDownTaskEventListener countDownListener = new CountDownTaskEventListener(1, false, true);
        addCountDownListner(countDownListener);
        
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        InternalTask task = (InternalTask) TaskFactory.evalTask(reader, vars);
        
        ((InternalTaskData) task.getTaskData()).setSkipable(true);
        InternalPeopleAssignments assignments = (InternalPeopleAssignments) TaskModelProvider.getFactory().newPeopleAssignments();
        List<OrganizationalEntity> ba = new ArrayList<OrganizationalEntity>();
        User user = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) user).setId("Administrator");
        ba.add(user);
        assignments.setBusinessAdministrators(ba);
        
        List<OrganizationalEntity> po = new ArrayList<OrganizationalEntity>();
        User user2 = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) user2).setId("Administrator");        
        po.add(user2);
        assignments.setPotentialOwners(po);
        
        task.setPeopleAssignments(assignments);
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        InternalContent content = (InternalContent) TaskModelProvider.getFactory().newContent();
        
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(task, params, null);
        content.setContent(marshalledObject.getContent());
        taskService.addContent(taskId, content);
        long contentId = content.getId();
        
        content = (InternalContent) taskService.getContentById(contentId);
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);
        
        taskService.skip(taskId, "Administrator");
        // emails should not be set yet
        assertEquals(0, getWiser().getMessages().size());
        
        countDownListener.waitTillCompleted();

        // no email should ne sent as task was completed before deadline was triggered
        assertEquals(0, getWiser().getMessages().size());
        task = (InternalTask) taskService.getTaskById(taskId);
        assertEquals(Status.Obsolete, task.getTaskData().getStatus());
        assertEquals(0, task.getDeadlines().getStartDeadlines().size());
        assertEquals(0, task.getDeadlines().getEndDeadlines().size());
    }

    @Test(timeout=10000)         
    public void testDelayedEmailNotificationOnDeadlineTaskExited() throws Exception {
        CountDownTaskEventListener countDownListener = new CountDownTaskEventListener(1, false, true);
        addCountDownListner(countDownListener);
        
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        InternalTask task = (InternalTask) TaskFactory.evalTask(reader, vars);
        
        ((InternalTaskData) task.getTaskData()).setSkipable(true);
        InternalPeopleAssignments assignments = (InternalPeopleAssignments) TaskModelProvider.getFactory().newPeopleAssignments();
        List<OrganizationalEntity> ba = new ArrayList<OrganizationalEntity>();
        User user = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) user).setId("Administrator");
        ba.add(user);
        assignments.setBusinessAdministrators(ba);
        
        List<OrganizationalEntity> po = new ArrayList<OrganizationalEntity>();
        User user2 = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) user2).setId("Administrator");        
        po.add(user2);
        assignments.setPotentialOwners(po);
        
        task.setPeopleAssignments(assignments);
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        InternalContent content = (InternalContent) TaskModelProvider.getFactory().newContent();
        
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(task, params, null);
        content.setContent(marshalledObject.getContent());
        taskService.addContent(taskId, content);
        long contentId = content.getId();
        
        content = (InternalContent) taskService.getContentById(contentId);
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);
        
        taskService.exit(taskId, "Administrator");
        // emails should not be set yet
        assertEquals(0, getWiser().getMessages().size());
        
        countDownListener.waitTillCompleted();

        // no email should ne sent as task was completed before deadline was triggered
        assertEquals(0, getWiser().getMessages().size());
        task = (InternalTask) taskService.getTaskById(taskId);
        assertEquals(Status.Exited, task.getTaskData().getStatus());
        assertEquals(0, task.getDeadlines().getStartDeadlines().size());
        assertEquals(0, task.getDeadlines().getEndDeadlines().size());
    }

    @Test(timeout=10000)     
    public void testDelayedReassignmentOnDeadline() throws Exception {
        CountDownTaskEventListener countDownListener = new CountDownTaskEventListener(1, true, false);
        addCountDownListner(countDownListener);
        

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithReassignment));
        Task task = (Task) TaskFactory.evalTask(reader, vars);
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        task = taskService.getTaskById(taskId);
        List<OrganizationalEntity> potentialOwners = (List<OrganizationalEntity>) task.getPeopleAssignments().getPotentialOwners();
        List<String> ids = new ArrayList<String>(potentialOwners.size());
        for (OrganizationalEntity entity : potentialOwners) {
            ids.add(entity.getId());
        }
        assertTrue(ids.contains("Tony Stark"));
        assertTrue(ids.contains("Luke Cage"));

        // should have re-assigned by now
        countDownListener.waitTillCompleted();
        
        task = taskService.getTaskById(taskId);
        assertEquals(Status.Ready, task.getTaskData().getStatus());
        potentialOwners = (List<OrganizationalEntity>) task.getPeopleAssignments().getPotentialOwners();

        ids = new ArrayList<String>(potentialOwners.size());
        for (OrganizationalEntity entity : potentialOwners) {
            ids.add(entity.getId());
        }
        assertTrue(ids.contains("Bobba Fet"));
        assertTrue(ids.contains("Jabba Hutt"));
    }

    @Test(timeout=10000)     
    public void testDelayedEmailNotificationStartDeadlineStatusDoesNotMatch() throws Exception {
        CountDownTaskEventListener countDownListener = new CountDownTaskEventListener(2, false, true);
        addCountDownListner(countDownListener);
        
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        InternalTask task = (InternalTask) TaskFactory.evalTask(reader, vars);
          
        ((InternalTaskData) task.getTaskData()).setSkipable(true);
        InternalPeopleAssignments assignments = (InternalPeopleAssignments) TaskModelProvider.getFactory().newPeopleAssignments();
        List<OrganizationalEntity> ba = new ArrayList<OrganizationalEntity>();
        User user = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) user).setId("Administrator");
        ba.add(user);
        assignments.setBusinessAdministrators(ba);
          
        List<OrganizationalEntity> po = new ArrayList<OrganizationalEntity>();
        User user2 = TaskModelProvider.getFactory().newUser();
        ((InternalOrganizationalEntity) user2).setId("Administrator");        
        po.add(user2);
        assignments.setPotentialOwners(po);
        
        task.setPeopleAssignments(assignments);
        
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();
        InternalContent content = (InternalContent) TaskModelProvider.getFactory().newContent();
        
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(task, params, null);
        content.setContent(marshalledObject.getContent());
        taskService.addContent(taskId, content);
        long contentId = content.getId();
        
        content = (InternalContent) taskService.getContentById(contentId);
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);
        
        taskService.start(taskId, "Administrator");
        
        // emails should not be set yet
        assertEquals(0, getWiser().getMessages().size());
        // since we don't want the notification to be invoked we need to rely on timeout
        countDownListener.waitTillCompleted(3000);
        
        // no email should ne sent as task was completed before deadline was triggered
        assertEquals(0, getWiser().getMessages().size());
        task = (InternalTask) taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task.getTaskData().getStatus());
        assertEquals(0, task.getDeadlines().getStartDeadlines().size());
        assertEquals(0, task.getDeadlines().getEndDeadlines().size());
    }
}
