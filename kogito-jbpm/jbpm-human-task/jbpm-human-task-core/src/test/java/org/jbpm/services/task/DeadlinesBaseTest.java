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


import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.impl.EnvironmentFactory;
import org.jbpm.services.task.deadlines.NotificationListener;
import org.jbpm.services.task.deadlines.notifications.impl.MockNotificationListener;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.services.task.impl.util.HumanTaskHandlerHelper;
import org.jbpm.test.listener.task.CountDownTaskEventListener;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.junit.Test;
import org.kie.api.runtime.Environment;
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



public abstract class DeadlinesBaseTest extends HumanTaskServicesBaseTest {

    protected NotificationListener notificationListener;
    
    public void tearDown(){
        super.tearDown();
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

        InternalContent content =  (InternalContent) TaskModelProvider.getFactory().newContent();
        
        Map<String, String> params = fillMarshalSubjectAndBodyParams();
        ContentData marshalledObject = ContentMarshallerHelper.marshal(task, params, null);
        content.setContent(marshalledObject.getContent());
        taskService.addContent(taskId, content);
        long contentId = content.getId();
        
        content = (InternalContent) taskService.getContentById(contentId);
        Object unmarshallObject = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        checkContentSubjectAndBody(unmarshallObject);

        // emails should not be set yet
        assertThat(((MockNotificationListener)notificationListener).getEventsRecieved().size()).isEqualTo(0);
        
        countDownListener.waitTillCompleted();

        // 1 email with two recipients should now exist
        assertThat(((MockNotificationListener)notificationListener).getEventsRecieved().size()).isEqualTo(1);
        
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
        assertThat(unmarshallObject.toString()).isEqualTo("'singleobject'");

        // emails should not be set yet
        assertThat(((MockNotificationListener)notificationListener).getEventsRecieved().size()).isEqualTo(0);
        countDownListener.waitTillCompleted();

        // 1 email with two recipients should now exist
        assertThat(((MockNotificationListener)notificationListener).getEventsRecieved().size()).isEqualTo(1);

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
        assertThat(((MockNotificationListener)notificationListener).getEventsRecieved().size()).isEqualTo(0);

        countDownListener.waitTillCompleted();

        // no email should ne sent as task was completed before deadline was triggered
        assertThat(((MockNotificationListener)notificationListener).getEventsRecieved().size()).isEqualTo(0);
        task = (InternalTask) taskService.getTaskById(taskId);
        assertThat(task.getTaskData().getStatus()).isEqualTo(Status.Completed);
        assertThat(((InternalTask) task).getDeadlines().getStartDeadlines().size()).isEqualTo(0);
        assertThat(((InternalTask) task).getDeadlines().getEndDeadlines().size()).isEqualTo(0);
        
        
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
        assertThat(((MockNotificationListener)notificationListener).getEventsRecieved().size()).isEqualTo(0);
        
        countDownListener.waitTillCompleted();
        
        // no email should ne sent as task was completed before deadline was triggered
        assertThat(((MockNotificationListener)notificationListener).getEventsRecieved().size()).isEqualTo(0);
        task = (InternalTask) taskService.getTaskById(taskId);
        assertThat(task.getTaskData().getStatus()).isEqualTo(Status.Failed);
        assertThat(task.getDeadlines().getStartDeadlines().size()).isEqualTo(0);
        assertThat(task.getDeadlines().getEndDeadlines().size()).isEqualTo(0);
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
        assertThat(((MockNotificationListener)notificationListener).getEventsRecieved().size()).isEqualTo(0);
        
        countDownListener.waitTillCompleted();

        // no email should ne sent as task was completed before deadline was triggered
        assertThat(((MockNotificationListener)notificationListener).getEventsRecieved().size()).isEqualTo(0);
        task = (InternalTask) taskService.getTaskById(taskId);
        assertThat(task.getTaskData().getStatus()).isEqualTo(Status.Obsolete);
        assertThat(task.getDeadlines().getStartDeadlines().size()).isEqualTo(0);
        assertThat(task.getDeadlines().getEndDeadlines().size()).isEqualTo(0);
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
        assertThat(((MockNotificationListener)notificationListener).getEventsRecieved().size()).isEqualTo(0);

        countDownListener.waitTillCompleted();

        // no email should ne sent as task was completed before deadline was triggered
        assertThat(((MockNotificationListener)notificationListener).getEventsRecieved().size()).isEqualTo(0);
        task = (InternalTask) taskService.getTaskById(taskId);
        assertThat(task.getTaskData().getStatus()).isEqualTo(Status.Exited);
        assertThat(task.getDeadlines().getStartDeadlines().size()).isEqualTo(0);
        assertThat(task.getDeadlines().getEndDeadlines().size()).isEqualTo(0);
    }


    @Test(timeout=10000)
    public void testDelayedReassignmentOnDeadline() throws Exception {
        CountDownTaskEventListener countDownListener = new CountDownTaskEventListener(1, true, false);
        addCountDownListner(countDownListener);

        Map<String, Object> vars = new HashMap<String, Object>();
        vars.put("now", new Date());

        Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithReassignment));
        Task task = (InternalTask) TaskFactory.evalTask(reader, vars);
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        task = taskService.getTaskById(taskId);
        List<OrganizationalEntity> potentialOwners = (List<OrganizationalEntity>) task.getPeopleAssignments().getPotentialOwners();
        List<String> ids = new ArrayList<String>(potentialOwners.size());
        for (OrganizationalEntity entity : potentialOwners) {
            ids.add(entity.getId());
        }
        assertThat(ids.contains("Tony Stark")).isTrue();
        assertThat(ids.contains("Luke Cage")).isTrue();

        // should have re-assigned by now
        countDownListener.waitTillCompleted();
        
        task = taskService.getTaskById(taskId);
        assertThat(task.getTaskData().getStatus()).isEqualTo(Status.Ready);
        potentialOwners = (List<OrganizationalEntity>) task.getPeopleAssignments().getPotentialOwners();

        ids = new ArrayList<String>(potentialOwners.size());
        for (OrganizationalEntity entity : potentialOwners) {
            ids.add(entity.getId());
        }
        assertThat(ids.contains("Bobba Fet")).isTrue();
        assertThat(ids.contains("Jabba Hutt")).isTrue();
    }

      @Test(timeout=12000)
      public void testDelayedEmailNotificationOnDeadlineTaskCompletedMultipleTasks() throws Exception {
          CountDownTaskEventListener countDownListener = new CountDownTaskEventListener(2, false, true);
          addCountDownListner(countDownListener);

          Map<String, Object> vars = new HashMap<String, Object>();
          vars.put("now", new Date());

          Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
          
          // create task 1
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
          
          
          // create task 2
          reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithNotification));
          InternalTask task2 = (InternalTask) TaskFactory.evalTask(reader, vars);          
          ((InternalTaskData) task2.getTaskData()).setSkipable(true);          
          task2.setPeopleAssignments(assignments);
          
          taskService.addTask(task, new HashMap<String, Object>());
          taskService.addTask(task2, new HashMap<String, Object>());
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

          Calendar cal = Calendar.getInstance();
          cal.add(Calendar.SECOND, 5);
          task.getDeadlines().getStartDeadlines().get(0).setDate(cal.getTime());
          task2.getDeadlines().getStartDeadlines().get(0).setDate(cal.getTime());

          taskService.start(taskId, "Administrator");
          taskService.complete(taskId, "Administrator", null);
          // emails should not be set yet
          assertThat(((MockNotificationListener)notificationListener).getEventsRecieved().size()).isEqualTo(0);
 
          countDownListener.waitTillCompleted();

          // no email should be sent as task was completed before deadline was triggered
          assertThat(((MockNotificationListener)notificationListener).getEventsRecieved().size()).isEqualTo(1);
          task = (InternalTask) taskService.getTaskById(taskId);
          assertThat(task.getTaskData().getStatus()).isEqualTo(Status.Completed);
          assertThat(((InternalTask) task).getDeadlines().getStartDeadlines().size()).isEqualTo(0);
          assertThat(((InternalTask) task).getDeadlines().getEndDeadlines().size()).isEqualTo(0);
          
          taskService.start(task2.getId(), "Administrator");
          taskService.complete(task2.getId(), "Administrator", null);
          
          task = (InternalTask) taskService.getTaskById(task2.getId());
          assertThat(task.getTaskData().getStatus()).isEqualTo(Status.Completed);
          assertThat(((InternalTask) task).getDeadlines().getStartDeadlines().size()).isEqualTo(0);
          assertThat(((InternalTask) task).getDeadlines().getEndDeadlines().size()).isEqualTo(0);
      }

      @Test(timeout = 15000)
      public void testTaskNotStartedReassign() throws Exception { 
          Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithMultipleReassignment));
          Map<String, Object> vars = new HashMap<String, Object>();
          vars.put("now", new Date());
          Task task = (InternalTask) TaskFactory.evalTask(reader, vars);
          Environment environment = EnvironmentFactory.newEnvironment();

          Map<String, Object> inputVars = new HashMap<String, Object>();
          inputVars.put("NotStartedReassign", "[users:Tony Stark,Bobba Fet,Jabba Hutt|groups:]@[2s]");
          ((InternalTask) task).setDeadlines(HumanTaskHandlerHelper.setDeadlines(inputVars, Collections.emptyList(), environment));

          taskService.addTask(task, inputVars);

          CountDownTaskEventListener countDownListener = new CountDownTaskEventListener(1, true, false);
          addCountDownListner(countDownListener);

          long taskId = task.getId();

          String []owners = new String[] {
             "Tony Stark", "Bobba Fet", "Jabba Hutt"
          };

          for(String owner : owners) {
              countDownListener.reset(1);

              taskService.claim(taskId, owner);
              task = taskService.getTaskById(taskId);
              assertThat(task.getTaskData().getActualOwner().getId()).isEqualTo(owner);

              countDownListener.waitTillCompleted();

              task = taskService.getTaskById(taskId);
              assertThat(task.getTaskData().getActualOwner()).as("Task was not reclaimed").isNull();
          }
          taskService.claim(taskId, "Bobba Fet");
          taskService.start(taskId, "Bobba Fet");
          taskService.complete(taskId, "Bobba Fet", Collections.<String, Object>emptyMap());
      }

      @Test(timeout = 15000)
      public void testTaskNotCompletedReassign() throws Exception { 
          Reader reader = new InputStreamReader(getClass().getResourceAsStream(MvelFilePath.DeadlineWithMultipleReassignment));
          Map<String, Object> vars = new HashMap<String, Object>();
          vars.put("now", new Date());
          InternalTask task = (InternalTask) TaskFactory.evalTask(reader, vars);

          Environment environment = EnvironmentFactory.newEnvironment();
          Map<String, Object> inputVars = new HashMap<String, Object>();
          inputVars.put("NotCompletedReassign", "[users:Tony Stark,Bobba Fet,Jabba Hutt|groups:]@[2s]");
          ((InternalTask) task).setDeadlines(HumanTaskHandlerHelper.setDeadlines(inputVars, Collections.emptyList(), environment));

          taskService.addTask(task, inputVars);

          CountDownTaskEventListener countDownListener = new CountDownTaskEventListener(1, true, false);
          addCountDownListner(countDownListener);

          long taskId = task.getId();

          String []owners = new String[] {
             "Tony Stark", "Bobba Fet", "Jabba Hutt"
          };

          for(String owner : owners) {
              countDownListener.reset(1);

              taskService.claim(taskId, owner);
              task = (InternalTask) taskService.getTaskById(taskId);
              assertThat(task.getTaskData().getActualOwner().getId()).isEqualTo(owner);

              taskService.start(taskId, owner);

              countDownListener.waitTillCompleted();

              task = (InternalTask) taskService.getTaskById(taskId);
              assertThat(task.getTaskData().getActualOwner()).as("Task was not reclaimed").isNull();
          }

          taskService.claim(taskId, "Bobba Fet");
          taskService.start(taskId, "Bobba Fet");
          taskService.complete(taskId, "Bobba Fet", Collections.<String, Object>emptyMap());
      }
}
