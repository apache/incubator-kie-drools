/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.jbpm.services.task.audit.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.jbpm.services.task.HumanTaskServicesBaseTest;
import org.jbpm.services.task.audit.commands.DeleteAuditEventsCommand;
import org.jbpm.services.task.audit.commands.DeleteBAMTaskSummariesCommand;
import org.jbpm.services.task.audit.commands.GetAuditEventsCommand;
import org.jbpm.services.task.audit.commands.GetBAMTaskSummariesCommand;
import org.jbpm.services.task.audit.impl.model.BAMTaskSummaryImpl;
import org.jbpm.services.task.audit.service.TaskAuditService;
import org.jbpm.services.task.impl.model.I18NTextImpl;
import org.jbpm.services.task.utils.TaskFluent;
import org.junit.Test;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.task.api.AuditTask;
import org.kie.internal.task.api.model.TaskEvent;

public abstract class TaskAuditBaseTest extends HumanTaskServicesBaseTest {

    @Inject
    protected TaskAuditService taskAuditService;

    @Test
    public void testComplete() {
        Task task = new TaskFluent().setName("This is my task name")
                .addPotentialGroup("Knights Templer")
                .setAdminUser("Administrator")
                .getTask();

        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        List<TaskSummary> allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        assertEquals(1, allGroupAuditTasks.size());
        assertTrue(allGroupAuditTasks.get(0).getStatusId().equals("Ready"));

        taskService.claim(taskId, "Darth Vader");

        allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        assertEquals(0, allGroupAuditTasks.size());

        allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", null, null, null);
        assertEquals(1, allGroupAuditTasks.size());
        assertTrue(allGroupAuditTasks.get(0).getStatusId().equals("Reserved"));

        taskService.release(taskId, "Darth Vader");

        allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        assertEquals(1, allGroupAuditTasks.size());
        assertTrue(allGroupAuditTasks.get(0).getStatusId().equals("Ready"));

        taskService.claim(taskId, "Darth Vader");

        allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        assertEquals(0, allGroupAuditTasks.size());

        allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", null, null, null);
        assertEquals(1, allGroupAuditTasks.size());
        assertTrue(allGroupAuditTasks.get(0).getStatusId().equals("Reserved"));

        // Go straight from Ready to Inprogress
        taskService.start(taskId, "Darth Vader");

        Task task1 = taskService.getTaskById(taskId);
        assertEquals(Status.InProgress, task1.getTaskData().getStatus());
        assertEquals("Darth Vader", task1.getTaskData().getActualOwner().getId());

        // Check is Complete
        taskService.complete(taskId, "Darth Vader", null);

        Task task2 = taskService.getTaskById(taskId);
        assertEquals(Status.Completed, task2.getTaskData().getStatus());
        assertEquals("Darth Vader", task2.getTaskData().getActualOwner().getId());

        List<TaskEvent> allTaskEvents = taskService.execute(new GetAuditEventsCommand(taskId, new QueryFilter(0, 0)));
        assertEquals(6, allTaskEvents.size());

        // test DeleteAuditEventsCommand
        int numFirstTaskEvents = allTaskEvents.size();
        task = new TaskFluent().setName("This is my task name 2")
                .addPotentialGroup("Knights Templer")
                .setAdminUser("Administrator")
                .getTask();
        taskService.addTask(task, new HashMap<String, Object>());
        long secondTaskId = task.getId();
        taskService.claim(secondTaskId, "Darth Vader");
        taskService.start(secondTaskId, "Darth Vader");
        taskService.complete(secondTaskId, "Darth Vader", null);

        allTaskEvents = taskService.execute(new GetAuditEventsCommand());
        int numTaskEvents = allTaskEvents.size();
        assertTrue("Expected more than " + numFirstTaskEvents + " events: " + numTaskEvents,
                numTaskEvents > numFirstTaskEvents);

        taskService.execute(new DeleteAuditEventsCommand(taskId));
        allTaskEvents = taskService.execute(new GetAuditEventsCommand());
        assertEquals(numTaskEvents - numFirstTaskEvents, allTaskEvents.size());

        taskService.execute(new DeleteAuditEventsCommand());
        allTaskEvents = taskService.execute(new GetAuditEventsCommand());
        assertEquals(0, allTaskEvents.size());

        // test get/delete BAM Task summaries commands
        List<BAMTaskSummaryImpl> bamTaskList = taskService.execute(new GetBAMTaskSummariesCommand());
        assertEquals("BAM Task Summary list size: ", 2, bamTaskList.size());

        taskService.execute(new DeleteBAMTaskSummariesCommand(taskId));
        bamTaskList = taskService.execute(new GetBAMTaskSummariesCommand());
        assertEquals("BAM Task Summary list size after delete (task id: " + taskId + ") : ", 1, bamTaskList.size());

        bamTaskList = taskService.execute(new GetBAMTaskSummariesCommand(secondTaskId));
        assertEquals("BAM Task Summary list size after delete (task id: " + taskId + ") : ", 1, bamTaskList.size());

        taskService.execute(new DeleteBAMTaskSummariesCommand());
        bamTaskList = taskService.execute(new GetBAMTaskSummariesCommand());
        assertEquals("BAM Task Summary list size after delete (task id: " + taskId + ") : ", 0, bamTaskList.size());

        List<AuditTask> allHistoryAuditTasks = taskAuditService.getAllAuditTasks(new QueryFilter(0, 0));
        assertEquals(2, allHistoryAuditTasks.size());
    }
    
    @Test
    public void testOnlyActiveTasks() {
        Task task = new TaskFluent().setName("This is my task name")
                .addPotentialUser("salaboy")

                .setAdminUser("Administrator")
                .getTask();
        taskService.addTask(task, new HashMap<String, Object>());

        List<TaskSummary> allActiveTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        assertEquals(1, allActiveTasks.size());
        assertTrue(allActiveTasks.get(0).getStatusId().equals("Reserved"));
        QueryFilter queryFilter = new QueryFilter(0, 0);
        Map<String, Object> params = new HashMap<String, Object>();
        List<String> statuses = new ArrayList<String>();
        statuses.add(Status.Reserved.toString());
        params.put("statuses", statuses);
        queryFilter.setParams(params);
        List<AuditTask> allActiveAuditTasksByUser = taskAuditService.getAllAuditTasksByStatus("salaboy",
                queryFilter);
        assertEquals(1, allActiveAuditTasksByUser.size());
        assertTrue(allActiveAuditTasksByUser.get(0).getStatus().equals("Reserved"));
        
        statuses = new ArrayList<String>();
        statuses.add(Status.Completed.toString());
        params.put("statuses", statuses);
        queryFilter.setParams(params);
        allActiveAuditTasksByUser = taskAuditService.getAllAuditTasksByStatus("salaboy",
                queryFilter);
        assertEquals(0, allActiveAuditTasksByUser.size());
        
    }

    @Test
    public void testGroupTasks() {
        Task task = new TaskFluent().setName("This is my task name")
                .addPotentialUser("salaboy")
                .addPotentialUser("krisv")
                .addPotentialGroup("Knights Templer")
                .setAdminUser("Administrator")
                .getTask();
        taskService.addTask(task, new HashMap<String, Object>());

        List<TaskSummary> allGroupTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        assertEquals(1, allGroupTasks.size());
        assertTrue(allGroupTasks.get(0).getStatusId().equals("Ready"));

        List<AuditTask> allGroupAuditTasksByUser = taskAuditService.getAllGroupAuditTasksByUser("salaboy",
                new QueryFilter(0, 0));
        assertEquals(1, allGroupAuditTasksByUser.size());
        assertTrue(allGroupAuditTasksByUser.get(0).getStatus().equals("Ready"));
    }

    @Test
    public void testAdminTasks() {
        Task task = new TaskFluent().setName("This is my task name")
                .setAdminUser("salaboy")
                .getTask();

        taskService.addTask(task, new HashMap<String, Object>());

        List<TaskSummary> allAdminTasks = taskService.getTasksAssignedAsBusinessAdministrator("salaboy", null);
        assertEquals(1, allAdminTasks.size());

        List<AuditTask> allAdminAuditTasksByUser = taskAuditService.getAllAdminAuditTasksByUser("salaboy",
                new QueryFilter(0, 0));
        assertEquals(1, allAdminAuditTasksByUser.size());
    }


    @Test
    public void testExitAfterClaim() {
        // One potential owner, should go straight to state Reserved
        Task task = new TaskFluent().setName("This is my task name 2")
                .addPotentialGroup("Knights Templer")
                .setAdminUser("Administrator")
                .getTask();
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        List<TaskSummary> allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        assertEquals(1, allGroupAuditTasks.size());
        assertTrue(allGroupAuditTasks.get(0).getStatusId().equals("Ready"));

        taskService.claim(taskId, "Darth Vader");

        allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        assertEquals(0, allGroupAuditTasks.size());

        allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", null, null, null);
        assertEquals(1, allGroupAuditTasks.size());
        assertTrue(allGroupAuditTasks.get(0).getStatusId().equals("Reserved"));

        taskService.exit(taskId, "Administrator");

        List<AuditTask> allHistoryAuditTasks = taskAuditService.getAllAuditTasks(new QueryFilter(0, 0));
        assertEquals(1, allHistoryAuditTasks.size());

        allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        assertEquals(0, allGroupAuditTasks.size());
    }

    @Test
    public void testExitBeforeClaim() {
        Task task = new TaskFluent().setName("This is my task name 2")
                .addPotentialGroup("Knights Templer")
                .setAdminUser("Administrator")
                .getTask();
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        List<TaskSummary> allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        assertEquals(1, allGroupAuditTasks.size());
        assertTrue(allGroupAuditTasks.get(0).getStatusId().equals("Ready"));

        taskService.exit(taskId, "Administrator");

        List<AuditTask> allHistoryAuditTasks = taskAuditService.getAllAuditTasks(new QueryFilter(0, 0));
        assertEquals(1, allHistoryAuditTasks.size());

        allGroupAuditTasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", null, null, null);
        assertEquals(0, allGroupAuditTasks.size());
    }

    private void testDescriptionUpdate(String oldDescription, String newDescription, boolean changeExpected) {
        Task task = new TaskFluent()
                .setDescription(oldDescription)
                .setAdminUser("Administrator")
                .getTask();
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        List<I18NText> descriptions = new ArrayList<I18NText>();
        descriptions.add(new I18NTextImpl("", newDescription));
        taskService.setDescriptions(taskId, descriptions);

        task = taskService.getTaskById(taskId);
        Assertions.assertThat(task.getDescription()).isEqualTo(newDescription);

        List<AuditTask> auditTasks = taskAuditService.getAllAuditTasks(new QueryFilter());
        Assertions.assertThat(auditTasks).hasSize(1);
        Assertions.assertThat(auditTasks.get(0).getDescription()).isEqualTo(newDescription);

        List<TaskEvent> taskEvents = taskAuditService.getAllTaskEvents(taskId, new QueryFilter());
        if (changeExpected) {
            Assertions.assertThat(taskEvents).hasSize(2);
            Assertions.assertThat(taskEvents.get(1).getMessage()).contains(String.valueOf(oldDescription),
                    String.valueOf(newDescription));
        } else {
            Assertions.assertThat(taskEvents).hasSize(1);
        }
    }

    @Test
    public void testDescriptionUpdateSame() {
        testDescriptionUpdate("description", "description", false);
    }

    @Test
    public void testDescriptionUpdateDifferent() {
        testDescriptionUpdate("old description", "new description", true);
    }

   
    @Test
    public void testDescriptionUpdateToNull() {
        testDescriptionUpdate("old description", null, true);
    }

    @Test
    public void testDescriptionUpdateToEmpty() {
        testDescriptionUpdate("old description", "", true);
    }

    @Test
    public void testDescriptionUpdateFromNull() {
        testDescriptionUpdate(null, "new description", true);
    }

    @Test
    public void testDescriptionUpdateFromEmpty() {
        testDescriptionUpdate("", "new description", true);
    }

    private void testNameUpdate(String oldName, String newName, boolean changeExpected) {
        Task task = new TaskFluent()
                .setName(oldName)
                .setAdminUser("Administrator")
                .getTask();
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        List<I18NText> taskNames = new ArrayList<I18NText>();
        taskNames.add(new I18NTextImpl("", newName));
        taskService.setTaskNames(taskId, taskNames);

        task = taskService.getTaskById(taskId);
        Assertions.assertThat(task.getName()).isEqualTo(newName);

        List<AuditTask> auditTasks = taskAuditService.getAllAuditTasks(new QueryFilter());
        Assertions.assertThat(auditTasks).hasSize(1);
        Assertions.assertThat(auditTasks.get(0).getName()).isEqualTo(newName);

        List<TaskEvent> taskEvents = taskAuditService.getAllTaskEvents(taskId, new QueryFilter());
        if (changeExpected) {
            Assertions.assertThat(taskEvents).hasSize(2);
            Assertions.assertThat(taskEvents.get(1).getMessage()).contains(String.valueOf(oldName),
                    String.valueOf(newName));
        } else {
            Assertions.assertThat(taskEvents).hasSize(1);
        }
    }

    @Test
    public void testNameUpdateSame() {
        testNameUpdate("name", "name", false);
    }

    @Test
    public void testNameUpdateDifferent() {
        testNameUpdate("old name", "new name", true);
    }

    
    @Test
    public void testNameUpdateToNull() {
        testNameUpdate("old name", null, true);
    }

    @Test
    public void testNameUpdateToEmpty() {
        testNameUpdate("old name", "", true);
    }

    @Test
    public void testNameUpdateFromNull() {
        testNameUpdate(null, "new name", true);
    }

    @Test
    public void testNameUpdateFromEmpty() {
        testNameUpdate("", "new name", true);
    }

    private void testPriorityUpdate(int oldPriority, int newPriority, boolean changeExpected) {
        Task task = new TaskFluent()
                .setPriority(oldPriority)
                .setAdminUser("Administrator")
                .getTask();
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        taskService.setPriority(taskId, newPriority);

        task = taskService.getTaskById(taskId);
        Assertions.assertThat(task.getPriority()).isEqualTo(newPriority);

        List<AuditTask> auditTasks = taskAuditService.getAllAuditTasks(new QueryFilter());
        Assertions.assertThat(auditTasks).hasSize(1);
        Assertions.assertThat(auditTasks.get(0).getPriority()).isEqualTo(newPriority);

        List<TaskEvent> taskEvents = taskAuditService.getAllTaskEvents(taskId, new QueryFilter());
        if (changeExpected) {
            Assertions.assertThat(taskEvents).hasSize(2);
            Assertions.assertThat(taskEvents.get(1).getMessage()).contains(String.valueOf(oldPriority),
                    String.valueOf(newPriority));
        } else {
            Assertions.assertThat(taskEvents).hasSize(1);
        }
    }

    @Test
    public void testPriorityUpdateSame() {
        testPriorityUpdate(0, 0, false);
    }

    @Test
    public void testPriorityUpdateDifferent() {
        testPriorityUpdate(0, 10, true);
    }

    private void testDueDateUpdate(Date oldDate, Date newDate, boolean changeExpected) {
        Task task = new TaskFluent()
                .setDueDate(oldDate)
                .setAdminUser("Administrator")
                .getTask();
        taskService.addTask(task, new HashMap<String, Object>());
        long taskId = task.getId();

        taskService.setExpirationDate(taskId, newDate);

        task = taskService.getTaskById(taskId);
        Assertions.assertThat(task.getTaskData().getExpirationTime()).isEqualTo(newDate);

        List<AuditTask> auditTasks = taskAuditService.getAllAuditTasks(new QueryFilter());
        Assertions.assertThat(auditTasks).hasSize(1);
        Assertions.assertThat(auditTasks.get(0).getDueDate()).isEqualTo(newDate);

        List<TaskEvent> taskEvents = taskAuditService.getAllTaskEvents(taskId, new QueryFilter());
        if (changeExpected) {
            Assertions.assertThat(taskEvents).hasSize(2);
            Assertions.assertThat(taskEvents.get(1).getMessage()).contains(String.valueOf(oldDate),
                    String.valueOf(newDate));
        } else {
            Assertions.assertThat(taskEvents).hasSize(1);
        }
    }

    private Timestamp getToday() {
        return new Timestamp(new Date().getTime());
    }

    private Timestamp getTomorrow() {
        Calendar c = Calendar.getInstance();
        c.setTime(getToday());
        c.add(Calendar.DATE, 1);
        return new Timestamp(c.getTimeInMillis());
    }

    @Test
    public void testDueDateUpdateSame() {
        testDueDateUpdate(getToday(), getToday(), false);
    }

    @Test
    public void testDueDateUpdateDifferent() {
        testDueDateUpdate(getToday(), getTomorrow(), true);
    }

    @Test
    public void testDueDateUpdateFromNull() {
        testDueDateUpdate(null, getTomorrow(), true);
    }

    
    @Test
    public void testDueDateUpdateToNull() {
        testDueDateUpdate(getToday(), null, true);
    }

}
