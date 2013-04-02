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
package org.jbpm.services.task;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jbpm.services.task.deadlines.NotificationListener;
import org.jbpm.services.task.deadlines.notifications.impl.MockNotificationListener;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.services.task.impl.model.ContentDataImpl;
import org.jbpm.services.task.impl.model.ContentImpl;
import org.jbpm.services.task.impl.model.OrganizationalEntityImpl;
import org.jbpm.services.task.impl.model.PeopleAssignmentsImpl;
import org.jbpm.services.task.impl.model.TaskImpl;
import org.jbpm.services.task.impl.model.UserImpl;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.junit.After;
import org.junit.Test;
import org.kie.internal.task.api.model.Content;
import org.kie.internal.task.api.model.OrganizationalEntity;
import org.kie.internal.task.api.model.PeopleAssignments;
import org.kie.internal.task.api.model.Status;
import org.kie.internal.task.api.model.Task;



public abstract class DeadlinesBaseTest extends HumanTaskServicesBaseTest {

    @Inject
    private NotificationListener notificationListener;
    
    @After
    public void tearDown(){
        super.tearDown();
        ((MockNotificationListener)notificationListener).reset();
    }
    
    
    @Test
    public void testDelayedEmailNotificationOnDeadline() throws Exception {
        
        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        Task task = (TaskImpl) TaskFactory.evalTask(reader, vars);
        
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        Content content = new ContentImpl();
        
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentDataImpl marshalledObject = ContentMarshallerHelper.marshal(params, null);
        content.setContent(marshalledObject.getContent());
        taskService.addContent(taskId, content);
        long contentId = content.getId();
        
        content = taskService.getContentById(contentId);
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);

        // emails should not be set yet
        //assertEquals(0, getWiser().getMessages().size());
        assertEquals(0, ((MockNotificationListener)notificationListener).getEventsRecieved().size());
        Thread.sleep(100);

        // nor yet
        assertEquals(0, ((MockNotificationListener)notificationListener).getEventsRecieved().size());

        long time = 0;
        while (((MockNotificationListener)notificationListener).getEventsRecieved().size() != 1 && time < 5000) {
            Thread.sleep(500);
            time += 500;
        }

        // 1 email with two recipients should now exist
        assertEquals(1, ((MockNotificationListener)notificationListener).getEventsRecieved().size());
        assertEquals(2, ((MockNotificationListener)notificationListener).getEventsRecieved().get(0).getNotification().getRecipients().size());
        
    }
    @Test
    public void testDelayedEmailNotificationOnDeadlineContentSingleObject() throws Exception {
        

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotificationContentSingleObject));
        Task task = (TaskImpl) TaskFactory.evalTask(reader, vars);
     
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        Content content = new ContentImpl();
        ContentDataImpl marshalledObject = ContentMarshallerHelper.marshal("'singleobject'", null);
        content.setContent(marshalledObject.getContent());
       
        taskService.addContent(taskId, content);
        long contentId = content.getId();
      
        content = taskService.getContentById(contentId);
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        assertEquals("'singleobject'", unmarshallObject.toString());

        // emails should not be set yet
        assertEquals(0, ((MockNotificationListener)notificationListener).getEventsRecieved().size());
        Thread.sleep(100);
        // nor yet
        assertEquals(0, ((MockNotificationListener)notificationListener).getEventsRecieved().size());
        long time = 0;
        while (((MockNotificationListener)notificationListener).getEventsRecieved().size() != 1 && time < 5000) {
            Thread.sleep(500);
            time += 500;
        }

        // 1 email with two recipients should now exist
        assertEquals(1, ((MockNotificationListener)notificationListener).getEventsRecieved().size());
        

    }
    @Test
    public void testDelayedEmailNotificationOnDeadlineTaskCompleted() throws Exception {


         Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        Task task = (TaskImpl) TaskFactory.evalTask(reader, vars);
        
        task.getTaskData().setSkipable(true);
        PeopleAssignments assignments = new PeopleAssignmentsImpl();
        List<OrganizationalEntity> ba = new ArrayList<OrganizationalEntity>();
        ba.add(new UserImpl("Administrator"));
        assignments.setBusinessAdministrators(ba);
        
        List<OrganizationalEntity> po = new ArrayList<OrganizationalEntity>();
        po.add(new UserImpl("Administrator"));
        assignments.setPotentialOwners(po);
        
        task.setPeopleAssignments(assignments);
        
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        Content content = new ContentImpl();
        
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentDataImpl marshalledObject = ContentMarshallerHelper.marshal(params, null);
        content.setContent(marshalledObject.getContent());
        taskService.addContent(taskId, content);
        long contentId = content.getId();
        
        content = taskService.getContentById(contentId);
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);
        
        taskService.start(taskId, "Administrator");
        taskService.complete(taskId, "Administrator", null);
        // emails should not be set yet
        assertEquals(0, ((MockNotificationListener)notificationListener).getEventsRecieved().size());
        Thread.sleep(100);

        // nor yet
        assertEquals(0, ((MockNotificationListener)notificationListener).getEventsRecieved().size());

        long time = 0;
        while (((MockNotificationListener)notificationListener).getEventsRecieved().size() != 1 && time < 5000) {
            Thread.sleep(500);
            time += 500;
        }

        // no email should ne sent as task was completed before deadline was triggered
        assertEquals(0, ((MockNotificationListener)notificationListener).getEventsRecieved().size());
        task = taskService.getTaskById(taskId);
        assertEquals(Status.Completed, task.getTaskData().getStatus());
        assertEquals(0, task.getDeadlines().getStartDeadlines().size());
        assertEquals(0, task.getDeadlines().getEndDeadlines().size());
        
        
    }
    @Test
    public void testDelayedEmailNotificationOnDeadlineTaskFailed() throws Exception {
        

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        Task task = (TaskImpl) TaskFactory.evalTask(reader, vars);
        
        task.getTaskData().setSkipable(true);
        PeopleAssignments assignments = new PeopleAssignmentsImpl();
        List<OrganizationalEntity> ba = new ArrayList<OrganizationalEntity>();
        ba.add(new UserImpl("Administrator"));
        assignments.setBusinessAdministrators(ba);
        
        List<OrganizationalEntity> po = new ArrayList<OrganizationalEntity>();
        po.add(new UserImpl("Administrator"));
        assignments.setPotentialOwners(po);
        
        task.setPeopleAssignments(assignments);
        
        
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        Content content = new ContentImpl();
        
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentDataImpl marshalledObject = ContentMarshallerHelper.marshal(params, null);
        content.setContent(marshalledObject.getContent());
        taskService.addContent(taskId, content);
        long contentId = content.getId();
        
        content = taskService.getContentById(contentId);
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);
        
        taskService.start(taskId, "Administrator");
        taskService.fail(taskId, "Administrator", null);
        // emails should not be set yet
        assertEquals(0, ((MockNotificationListener)notificationListener).getEventsRecieved().size());
        Thread.sleep(100);

        // nor yet
        assertEquals(0, ((MockNotificationListener)notificationListener).getEventsRecieved().size());

        long time = 0;
        while (((MockNotificationListener)notificationListener).getEventsRecieved().size() != 1 && time < 5000) {
            Thread.sleep(500);
            time += 500;
        }

        // no email should ne sent as task was completed before deadline was triggered
        assertEquals(0, ((MockNotificationListener)notificationListener).getEventsRecieved().size());
        task = taskService.getTaskById(taskId);
        assertEquals(Status.Failed, task.getTaskData().getStatus());
        assertEquals(0, task.getDeadlines().getStartDeadlines().size());
        assertEquals(0, task.getDeadlines().getEndDeadlines().size());
    }
    @Test
    public void testDelayedEmailNotificationOnDeadlineTaskSkipped() throws Exception {

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        Task task = (TaskImpl) TaskFactory.evalTask(reader, vars);
        
        task.getTaskData().setSkipable(true);
        PeopleAssignments assignments = new PeopleAssignmentsImpl();
        List<OrganizationalEntity> ba = new ArrayList<OrganizationalEntity>();
        ba.add(new UserImpl("Administrator"));
        assignments.setBusinessAdministrators(ba);
        
        List<OrganizationalEntity> po = new ArrayList<OrganizationalEntity>();
        po.add(new UserImpl("Administrator"));
        assignments.setPotentialOwners(po);
        
        task.setPeopleAssignments(assignments);
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        Content content = new ContentImpl();
        
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentDataImpl marshalledObject = ContentMarshallerHelper.marshal(params, null);
        content.setContent(marshalledObject.getContent());
        taskService.addContent(taskId, content);
        long contentId = content.getId();
        
        content = taskService.getContentById(contentId);
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);
        
        taskService.skip(taskId, "Administrator");
        // emails should not be set yet
        assertEquals(0, ((MockNotificationListener)notificationListener).getEventsRecieved().size());
        Thread.sleep(100);

        // nor yet
        assertEquals(0, ((MockNotificationListener)notificationListener).getEventsRecieved().size());

        long time = 0;
        while (((MockNotificationListener)notificationListener).getEventsRecieved().size() != 1 && time < 5000) {
            Thread.sleep(500);
            time += 500;
        }

        // no email should ne sent as task was completed before deadline was triggered
        assertEquals(0, ((MockNotificationListener)notificationListener).getEventsRecieved().size());
        task = taskService.getTaskById(taskId);
        assertEquals(Status.Obsolete, task.getTaskData().getStatus());
        assertEquals(0, task.getDeadlines().getStartDeadlines().size());
        assertEquals(0, task.getDeadlines().getEndDeadlines().size());
    }
    @Test    
    public void testDelayedEmailNotificationOnDeadlineTaskExited() throws Exception {

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
        Task task = (TaskImpl) TaskFactory.evalTask(reader, vars);
        
        task.getTaskData().setSkipable(true);
        PeopleAssignments assignments = new PeopleAssignmentsImpl();
        List<OrganizationalEntity> ba = new ArrayList<OrganizationalEntity>();
        ba.add(new UserImpl("Administrator"));
        assignments.setBusinessAdministrators(ba);
        
        List<OrganizationalEntity> po = new ArrayList<OrganizationalEntity>();
        po.add(new UserImpl("Administrator"));
        assignments.setPotentialOwners(po);
        
        task.setPeopleAssignments(assignments);
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        Content content = new ContentImpl();
        
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentDataImpl marshalledObject = ContentMarshallerHelper.marshal(params, null);
        content.setContent(marshalledObject.getContent());
        taskService.addContent(taskId, content);
        long contentId = content.getId();
        
        content = taskService.getContentById(contentId);
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);
        
        taskService.exit(taskId, "Administrator");
        // emails should not be set yet
        assertEquals(0, ((MockNotificationListener)notificationListener).getEventsRecieved().size());
        Thread.sleep(100);

        // nor yet
        assertEquals(0, ((MockNotificationListener)notificationListener).getEventsRecieved().size());

        long time = 0;
        while (((MockNotificationListener)notificationListener).getEventsRecieved().size() != 1 && time < 5000) {
            Thread.sleep(500);
            time += 500;
        }

        // no email should ne sent as task was completed before deadline was triggered
        assertEquals(0, ((MockNotificationListener)notificationListener).getEventsRecieved().size());
        task = taskService.getTaskById(taskId);
        assertEquals(Status.Exited, task.getTaskData().getStatus());
        assertEquals(0, task.getDeadlines().getStartDeadlines().size());
        assertEquals(0, task.getDeadlines().getEndDeadlines().size());
    }


      @Test
    public void testDelayedReassignmentOnDeadline() throws Exception {


        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithReassignment));
        Task task = (TaskImpl) TaskFactory.evalTask(reader, vars);
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        // Shouldn't have re-assigned yet
        Thread.sleep(1000);
        
        
        task = taskService.getTaskById(taskId);
        List<OrganizationalEntity> potentialOwners = (List<OrganizationalEntity>) task.getPeopleAssignments().getPotentialOwners();
        List<String> ids = new ArrayList<String>(potentialOwners.size());
        for (OrganizationalEntity entity : potentialOwners) {
            ids.add(entity.getId());
        }
        assertTrue(ids.contains("Tony Stark"));
        assertTrue(ids.contains("Luke Cage"));

        // should have re-assigned by now
        long time = 0;
        while (((MockNotificationListener)notificationListener).getEventsRecieved().size() != 1 && time < 5000) {
            Thread.sleep(500);
            time += 500;
        }
        
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

   
}
