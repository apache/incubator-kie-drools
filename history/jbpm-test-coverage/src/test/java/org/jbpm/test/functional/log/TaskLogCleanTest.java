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

package org.jbpm.test.functional.log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.jbpm.services.task.audit.service.TaskJPAAuditService;
import org.jbpm.test.JbpmTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.AuditTask;
import org.kie.internal.task.api.TaskVariable;

import qa.tools.ikeeper.annotation.BZ;

/**
 * Tests for:
 * - AuditTaskInstanceLogDeleteBuilder
 * - TaskEventInstanceLogDeleteBuilder
 * - TaskVariabletInstanceLogDeleteBuilder
 * - TaskJPAAuditService.clear()
 */
public class TaskLogCleanTest extends JbpmTestCase {

    private static final String HUMAN_TASK =
            "org/jbpm/test/functional/common/HumanTask.bpmn2";
    private static final String HUMAN_TASK_ID =
            "org.jbpm.test.functional.common.HumanTask";

    private static final String INPUT_ASSOCIATION =
            "org/jbpm/test/functional/log/TaskLogClean-inputAssociation.bpmn2";
    private static final String INPUT_ASSOCIATION_ID =
            "org.jbpm.test.functional.log.TaskLogClean-inputAssociation";

    private static final String HUMAN_TASK_MULTIACTORS =
            "org/jbpm/test/functional/common/HumanTaskWithMultipleActors.bpmn2";
    private static final String HUMAN_TASK_MULTIACTORS_ID =
            "org.jbpm.test.functional.common.HumanTaskWithMultipleActors";


    private KieSession kieSession;
    private List<ProcessInstance> processInstanceList;
    private TaskJPAAuditService taskAuditService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        taskAuditService = new TaskJPAAuditService(getEmf());
        taskAuditService.clear();
    }

    @Override
    public void tearDown() throws Exception {
        try {
            taskAuditService.clear();
            taskAuditService.dispose();
            abortProcess(kieSession, processInstanceList);
        } finally {
            super.tearDown();
        }
    }

    @Test
    public void testDeleteLogsByProcessId() {
        kieSession = createKSession(HUMAN_TASK);

        processInstanceList = startProcess(kieSession, HUMAN_TASK_ID, 2);
        abortProcess(kieSession, processInstanceList);

        int deletedLogs = taskAuditService.auditTaskDelete()
                .processId(HUMAN_TASK_ID)
                .build()
                .execute();
        Assertions.assertThat(deletedLogs).isEqualTo(2);
        Assertions.assertThat(getAllAuditTaskLogs()).hasSize(0);
    }

    @Test
    public void testDeleteLogsByProcessName() {
        kieSession = createKSession(HUMAN_TASK);

        processInstanceList = startProcess(kieSession, HUMAN_TASK_ID, 3);
        abortProcess(kieSession, processInstanceList);
        
        int resultCount = taskAuditService.auditTaskDelete()
                .processInstanceId(processInstanceList.get(0).getId(), processInstanceList.get(1).getId())
                .build()
                .execute();
        Assertions.assertThat(resultCount).isEqualTo(2);

        TaskService taskService = getRuntimeEngine().getTaskService();
        List<Long> taskIdList = taskService.getTasksByProcessInstanceId(processInstanceList.get(2).getId());
        Assertions.assertThat(taskIdList).hasSize(1);
    }

    @Test
    @BZ("1188702")
    public void testDeleteLogsByDate() {
        kieSession = createKSession(HUMAN_TASK);

        processInstanceList = startProcess(kieSession, HUMAN_TASK_ID, 4);
        abortProcess(kieSession, processInstanceList);
        TaskService taskService = getRuntimeEngine().getTaskService();

        // Delete the last two task logs
        for (int i = processInstanceList.size() - 1; i > 0; i--) {
            List<Long> taskIdList = taskService.getTasksByProcessInstanceId(processInstanceList.get(i).getId());
            Assertions.assertThat(taskIdList).hasSize(1);

            Task task = taskService.getTaskById(taskIdList.get(0));
            Assertions.assertThat(task).isNotNull();

            int resultCount = taskAuditService.auditTaskDelete()
                    .date(task.getTaskData().getCreatedOn())
                    .build()
                    .execute();
            Assertions.assertThat(resultCount).isEqualTo(1);
        }
    }

    @Test
    @BZ("1193017")
    public void testDeleteLogsByDateRange() throws InterruptedException {
        processInstanceList = new ArrayList<ProcessInstance>();
        kieSession = createKSession(HUMAN_TASK, INPUT_ASSOCIATION);

        Date date1 = new Date();
        processInstanceList.addAll(startProcess(kieSession, INPUT_ASSOCIATION_ID, 2));
        processInstanceList.addAll(startProcess(kieSession, HUMAN_TASK_ID, 1));
        Date date2 = new Date();
        Thread.sleep(1000);
        processInstanceList.addAll(startProcess(kieSession, INPUT_ASSOCIATION_ID, 1));
        processInstanceList.addAll(startProcess(kieSession, HUMAN_TASK_ID, 1));
        
        abortProcess(kieSession, processInstanceList);

        // Delete tasks created from date1 to date2.
        int resultCount = deleteAuditTaskInstanceLogs(date1, date2);
        Assertions.assertThat(resultCount).isEqualTo(3);
        Assertions.assertThat(getAllAuditTaskLogs()).hasSize(2);
    }

    private void testDeleteLogsByDateRange(Date startDate, Date endDate, boolean remove) {
        processInstanceList = new ArrayList<ProcessInstance>();
        kieSession = createKSession(HUMAN_TASK);

        processInstanceList.addAll(startProcess(kieSession, HUMAN_TASK_ID, 5));
        abortProcess(kieSession, processInstanceList);

        // Delete tasks created from date1 to date2.
        int resultCount = deleteAuditTaskInstanceLogs(startDate, endDate);
        Assertions.assertThat(resultCount).isEqualTo(remove ? 5 : 0);
        Assertions.assertThat(getAllAuditTaskLogs()).hasSize(remove ? 0 : 5);
    }

    @Test
    public void testDeleteLogsByDateRangeEndingYesterday() {
        testDeleteLogsByDateRange(getYesterday(), getYesterday(), false);
    }

    @Test
    public void testDeleteLogsByDateRangeIncludingToday() {
        testDeleteLogsByDateRange(getYesterday(), getTomorrow(), true);
    }

    @Test
    public void testDeleteLogsByDateRangeStartingTomorrow() {
        testDeleteLogsByDateRange(getTomorrow(), getTomorrow(), false);
    }

    @Test
    public void testDeleteTaskEventByDate() {
        kieSession = createKSession(HUMAN_TASK_MULTIACTORS);

        Date startDate = new Date();
        processInstanceList = startProcess(kieSession, HUMAN_TASK_MULTIACTORS_ID, 1);

        // Get the task
        TaskService taskService = getRuntimeEngine().getTaskService();
        Task task = taskService.getTaskById(
                taskService.getTasksByProcessInstanceId(
                        processInstanceList.get(0).getId()).get(0));
        Assertions.assertThat(task).isNotNull();
        Assertions.assertThat(task.getTaskData().getStatus()).isEqualTo(Status.Ready);

        // Perform 2 operation on the task
        taskService.claim(task.getId(), "krisv");
        taskService.start(task.getId(), "krisv");
        taskService.complete(task.getId(), "krisv", null);

        // Remove the instance from the running list as it has ended already.
        processInstanceList.clear();

        // Delete all task operation between dates
        int resultCount = taskAuditService.taskEventInstanceLogDelete()
                .dateRangeStart(startDate)
                .dateRangeEnd(new Date())
                .build()
                .execute();
        // Changes are as follows (see https://docs.jboss.org/jbpm/v6.1/userguide/jBPMTaskService.html#jBPMTaskLifecycle):
        // 1) Created -> Ready (automatic change because there are multiple actors)
        // 2) Ready -> Reserved (by claim)
        // 3) Reserved -> In Progress (by start)
        // 4) In Progress -> Completed (by complete)
        Assertions.assertThat(resultCount).isEqualTo(4);
    }
    
    @Test
    public void testDeleteTaskVariablesByDateActiveProcess() {
        kieSession = createKSession(INPUT_ASSOCIATION);

        Date startDate = new Date();
        processInstanceList = startProcess(kieSession, INPUT_ASSOCIATION_ID, 1);

        // Get the task
        TaskService taskService = getRuntimeEngine().getTaskService();
        Task task = taskService.getTaskById(
                taskService.getTasksByProcessInstanceId(
                        processInstanceList.get(0).getId()).get(0));
        Assertions.assertThat(task).isNotNull();
        Assertions.assertThat(task.getTaskData().getStatus()).isEqualTo(Status.Reserved);
        
        List<TaskVariable> vars = taskAuditService.taskVariableQuery()
                .build()
                .getResultList();
        Assertions.assertThat(vars).hasSize(1);

        // Delete all task operation between dates
        int resultCount = taskAuditService.taskVariableInstanceLogDelete()
                .dateRangeStart(startDate)
                .dateRangeEnd(new Date())
                .build()
                .execute();
        Assertions.assertThat(resultCount).isEqualTo(0);
        
        vars = taskAuditService.taskVariableQuery()
                .build()
                .getResultList();
        Assertions.assertThat(vars).hasSize(1);
    }
    
    @Test
    public void testDeleteTaskVariablesByDate() {
        kieSession = createKSession(HUMAN_TASK_MULTIACTORS);

        Date startDate = new Date();
        processInstanceList = startProcess(kieSession, HUMAN_TASK_MULTIACTORS_ID, 1);

        // Get the task
        TaskService taskService = getRuntimeEngine().getTaskService();
        Task task = taskService.getTaskById(
                taskService.getTasksByProcessInstanceId(
                        processInstanceList.get(0).getId()).get(0));
        Assertions.assertThat(task).isNotNull();
        Assertions.assertThat(task.getTaskData().getStatus()).isEqualTo(Status.Ready);
        
        List<TaskVariable> vars = taskAuditService.taskVariableQuery()
                .build()
                .getResultList();
        Assertions.assertThat(vars).hasSize(0);

        // Perform 2 operation on the task
        taskService.claim(task.getId(), "krisv");
        taskService.start(task.getId(), "krisv");
        
        Map<String, Object> results = new HashMap<>();
        results.put("test", "testvalue");
        taskService.complete(task.getId(), "krisv", results);
        
        vars = taskAuditService.taskVariableQuery()
                .build()
                .getResultList();
        Assertions.assertThat(vars).hasSize(1);

        // Remove the instance from the running list as it has ended already.
        processInstanceList.clear();

        // Delete all task operation between dates
        int resultCount = taskAuditService.taskVariableInstanceLogDelete()
                .dateRangeStart(startDate)
                .dateRangeEnd(new Date())
                .build()
                .execute();
        Assertions.assertThat(resultCount).isEqualTo(1);
    }

    @Test
    @BZ("1192912")
    public void testClearLogs() {
        kieSession = createKSession(HUMAN_TASK);
        processInstanceList = startProcess(kieSession, HUMAN_TASK_ID, 2);
        
        taskAuditService.clear();
        Assertions.assertThat(getAllAuditTaskLogs()).hasSize(processInstanceList.size());
        
        abortProcess(kieSession, processInstanceList);
        
        taskAuditService.clear();
        Assertions.assertThat(getAllAuditTaskLogs()).hasSize(0);
    }

    private int deleteAuditTaskInstanceLogs(Date startDate, Date endDate) {
        return taskAuditService.auditTaskDelete()
                .dateRangeStart(startDate)
                .dateRangeEnd(endDate)
                .build()
                .execute();
    }

    private List<AuditTask> getAllAuditTaskLogs() {
        return taskAuditService.auditTaskQuery()
                .build()
                .getResultList();
    }

    private Date getTomorrow() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 1);
        return c.getTime();
    }

    private Date getYesterday() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, -1);
        return c.getTime();
    }

    private void abortProcess(KieSession kieSession, List<ProcessInstance> processInstanceList) {
        for (ProcessInstance processInstance : processInstanceList) {
            abortProcess(kieSession, processInstance.getId());
        }
    }

    private void abortProcess(KieSession kieSession, long pid) {
        ProcessInstance processInstance = kieSession.getProcessInstance(pid);
        if (processInstance != null && processInstance.getState() == ProcessInstance.STATE_ACTIVE) {
            kieSession.abortProcessInstance(pid);
        }
    }

    private List<ProcessInstance> startProcess(KieSession kieSession, String processId, int count) {
        List<ProcessInstance> piList = new ArrayList<ProcessInstance>();
        for (int i = 0; i < count; i++) {
            ProcessInstance pi = kieSession.startProcess(processId);
            if (pi != null) {
                piList.add(pi);
            }
        }
        return piList;
    }

}
