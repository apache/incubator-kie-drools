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

package org.jbpm.test.functional.jobexec;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.persistence.EntityManagerFactory;

import org.assertj.core.api.Assertions;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.services.task.audit.service.TaskJPAAuditService;
import org.jbpm.test.JbpmAsyncJobTestCase;
import org.jbpm.test.listener.CountDownAsyncJobListener;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.internal.executor.api.CommandContext;
import qa.tools.ikeeper.annotation.BZ;

/**
 * BZ-TODO: SingleRun - only accepts "true"/"false" but is boolean type, other boolean types accept true/false.
 *
 * Note: Boolean parameters are processed as "Boolean.parseBoolean((String) ctx.getData("attr"))" - This does
 * make sense as the command is intended to be used from the Web UI using forms.
 */
public class LogCleanupCommandTest extends JbpmAsyncJobTestCase {

    public static final String HELLO_WORLD = "org/jbpm/test/functional/common/HelloWorldProcess1.bpmn";
    public static final String HELLO_WORLD_ID = "org.jbpm.test.functional.common.HelloWorldProcess1";

    public static final String LOG_CLEANUP = "org/jbpm/test/functional/jobexec/LogCleanupCommand.bpmn2";
    public static final String LOG_CLEANUP_ID = "org.jbpm.test.functional.jobexec.LogCleanupCommand";

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    private static final String ANOTHER_PROCESS = "hi";

    private String dateFormat = DATE_FORMAT;

    private TaskJPAAuditService taskAuditService;
    private JPAAuditLogService auditLogService;
    
    private EntityManagerFactory emfErrors = null;

    // ------------------------ Test Methods ------------------------

    @Override
    public void setUp() throws Exception {
        super.setUp();
        taskAuditService = new TaskJPAAuditService(getEmf());
        taskAuditService.clear();
        auditLogService = new JPAAuditLogService(getEmf());
        auditLogService.clear();
        
        emfErrors = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.persistence.complete");
    }

    @Override
    public void tearDown() throws Exception {
        try {
            taskAuditService.clear();
            taskAuditService.dispose();
            auditLogService.clear();
            auditLogService.dispose();
            
            if (emfErrors != null) {
                emfErrors.close();
            }
        } finally {
            super.tearDown();
        }
    }

    @Test(timeout=10000)
    public void skipProcessLog() throws Exception {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(1);
        ((ExecutorServiceImpl) getExecutorService()).addAsyncJobListener(countDownListener);
        // Generate data
        KieSession kieSession = createKSession(HELLO_WORLD);
        startProcess(kieSession, HELLO_WORLD_ID, 1);

        // Verify presence of data
        Assertions.assertThat(getProcessLogSize(HELLO_WORLD_ID)).isEqualTo(1);
        Assertions.assertThat(getNodeInstanceLogSize(HELLO_WORLD_ID)).isPositive();

        // Schedule cleanup job
        scheduleLogCleanup(true, false, false, getTomorrow(), null, HELLO_WORLD_ID);
        countDownListener.waitTillCompleted();

        // Verify that the process log has not been touched
        Assertions.assertThat(getProcessLogSize(HELLO_WORLD_ID)).isEqualTo(1);
        Assertions.assertThat(getNodeInstanceLogSize(HELLO_WORLD_ID)).isPositive();
    }

    @Test(timeout=10000)
    public void skipTaskLog() throws Exception {
        KieSession kieSession = null;
        List<ProcessInstance> processInstanceList = null;

        try {
            CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(1);
            ((ExecutorServiceImpl) getExecutorService()).addAsyncJobListener(countDownListener);
            // Generate data
            kieSession = createKSession(LOG_CLEANUP);

            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("assigneeName", "krisv");
            processInstanceList = startProcess(kieSession, LOG_CLEANUP_ID, paramMap, 1);

            TaskService taskService = getRuntimeEngine().getTaskService();
            List<Long> taskIdList = taskService.getTasksByProcessInstanceId(processInstanceList.get(0).getId());
            taskService.start(taskIdList.get(0), "krisv");
            taskService.complete(taskIdList.get(0), "krisv", null);

            // Verify presence of data
            Assertions.assertThat(getProcessLogSize(LOG_CLEANUP_ID)).isEqualTo(1);
            Assertions.assertThat(getTaskLogSize(LOG_CLEANUP_ID)).isEqualTo(1);
            Assertions.assertThat(getNodeInstanceLogSize(LOG_CLEANUP_ID)).isPositive();
            Assertions.assertThat(getVariableLogSize(LOG_CLEANUP_ID)).isEqualTo(3);

            // Schedule cleanup job
            scheduleLogCleanup(false, true, false, getTomorrow(), null, LOG_CLEANUP_ID);
            countDownListener.waitTillCompleted();

            // Verify absence of data
            Assertions.assertThat(getProcessLogSize(LOG_CLEANUP_ID)).isZero();
            Assertions.assertThat(getTaskLogSize(LOG_CLEANUP_ID)).isEqualTo(1);
            Assertions.assertThat(getNodeInstanceLogSize(LOG_CLEANUP_ID)).isZero();
            Assertions.assertThat(getVariableLogSize(LOG_CLEANUP_ID)).isZero();
        } finally {
            if (processInstanceList != null) {
                abortProcess(kieSession, processInstanceList);
            }
        }
    }

    private void deleteAllLogsOlderThan(Date date, CountDownAsyncJobListener countDownListener) throws Exception {
        deleteAllLogsOlderThan(date, HELLO_WORLD_ID, HELLO_WORLD_ID, countDownListener);
    }

    private void deleteAllLogsOlderThan(Date date, String runProcess, String cleanProcess, CountDownAsyncJobListener countDownListener) throws Exception {
        // Generate data
        KieSession kieSession = createKSession(HELLO_WORLD);
        startProcess(kieSession, runProcess, 1);

        // Verify presence of data
        Assertions.assertThat(getProcessLogSize(runProcess)).isPositive();
        Assertions.assertThat(getNodeInstanceLogSize(runProcess)).isPositive();

        // Set to NOW if date was not provided
        if (date == null) {
            date = new Date();
        }

        // Schedule cleanup job
        scheduleLogCleanup(false, false, false, date, null, cleanProcess);
        countDownListener.waitTillCompleted();
    }

    @Test(timeout=10000)
    public void deleteAllLogsOlderThanYesterday() throws Exception {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(1);
        ((ExecutorServiceImpl) getExecutorService()).addAsyncJobListener(countDownListener);
        deleteAllLogsOlderThan(getYesterday(), countDownListener);

        Assertions.assertThat(getProcessLogSize(HELLO_WORLD_ID)).isPositive();
        Assertions.assertThat(getNodeInstanceLogSize(HELLO_WORLD_ID)).isPositive();
    }

    @Test(timeout=10000)
    @BZ("1190881")
    public void deleteAllLogsOlderThanNow() throws Exception {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(1);
        ((ExecutorServiceImpl) getExecutorService()).addAsyncJobListener(countDownListener);
        deleteAllLogsOlderThan(null, countDownListener);

        Assertions.assertThat(getProcessLogSize(HELLO_WORLD_ID)).isZero();
        Assertions.assertThat(getNodeInstanceLogSize(HELLO_WORLD_ID)).isZero();
    }

    @Test(timeout=10000)
    public void deleteAllLogsOlderThanTomorrow() throws Exception {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(1);
        ((ExecutorServiceImpl) getExecutorService()).addAsyncJobListener(countDownListener);
        deleteAllLogsOlderThan(getTomorrow(), countDownListener);

        Assertions.assertThat(getProcessLogSize(HELLO_WORLD_ID)).isZero();
        Assertions.assertThat(getNodeInstanceLogSize(HELLO_WORLD_ID)).isZero();
    }

    @Test(timeout=10000)
    public void deleteAllLogsOlderThanTomorrowDifferentProcess() throws Exception {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(1);
        ((ExecutorServiceImpl) getExecutorService()).addAsyncJobListener(countDownListener);
        deleteAllLogsOlderThan(getTomorrow(), HELLO_WORLD_ID, ANOTHER_PROCESS, countDownListener);

        Assertions.assertThat(getProcessLogSize(HELLO_WORLD_ID)).isPositive();
        Assertions.assertThat(getNodeInstanceLogSize(HELLO_WORLD_ID)).isPositive();
    }

    @Test(timeout=10000)
    @BZ("1190881")
    public void deleteAllLogsOlderThanPeriod() throws Exception {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(1);
        ((ExecutorServiceImpl) getExecutorService()).addAsyncJobListener(countDownListener);
        // Generate data
        KieSession kieSession = createKSession(HELLO_WORLD);
        startProcess(kieSession, HELLO_WORLD_ID, 3);

        // Advance time 1+ second forward
        Thread.sleep(1010);

        // Verify presence of data
        Assertions.assertThat(getProcessLogSize(HELLO_WORLD_ID)).isEqualTo(3);
        Assertions.assertThat(getNodeInstanceLogSize(HELLO_WORLD_ID)).isPositive();

        // Schedule cleanup job
        scheduleLogCleanup(false, false, false, null, "1s", HELLO_WORLD_ID);
        countDownListener.waitTillCompleted();

        // Verify absence of data
        Assertions.assertThat(getProcessLogSize(HELLO_WORLD_ID)).isZero();
        Assertions.assertThat(getNodeInstanceLogSize(HELLO_WORLD_ID)).isZero();
    }

    // ------------------------ Helper Methods ------------------------

    private int getProcessLogSize(String processId) {
        return auditLogService.processInstanceLogQuery()
                .processId(processId)
                .build()
                .getResultList()
                .size();
    }

    private int getTaskLogSize(String processId) {
        return taskAuditService.auditTaskQuery()
                .processId(processId)
                .build()
                .getResultList()
                .size();
    }

    private int getNodeInstanceLogSize(String processId) {
        return auditLogService.nodeInstanceLogQuery()
                .processId(processId)
                .build()
                .getResultList()
                .size();
    }

    private int getVariableLogSize(String processId) {
        return auditLogService.variableInstanceLogQuery()
                .processId(processId)
                .build()
                .getResultList()
                .size();
    }

    private void scheduleLogCleanup(boolean skipProcessLog, boolean skipTaskLog, boolean skipExecutorLog,
            Date olderThan, String olderThanDuration, String forProcess) {
        CommandContext commandContext = new CommandContext();
        commandContext.setData("EmfName", "org.jbpm.persistence.complete");
        commandContext.setData("SkipProcessLog", String.valueOf(skipProcessLog));
        commandContext.setData("SkipTaskLog", String.valueOf(skipTaskLog));
        commandContext.setData("SkipExecutorLog", String.valueOf(skipExecutorLog));
        commandContext.setData("SingleRun", "true");
        commandContext.setData("DateFormat", dateFormat);
        if (olderThan != null) {
            commandContext.setData("OlderThan", new SimpleDateFormat(dateFormat).format(olderThan));
        }
        if (olderThanDuration != null) {
            commandContext.setData("OlderThanPeriod", olderThanDuration);
        }
        commandContext.setData("ForProcess", forProcess);
        getExecutorService().scheduleRequest("org.jbpm.executor.commands.LogCleanupCommand", commandContext);
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

}
