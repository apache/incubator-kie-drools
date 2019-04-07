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
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.test.JbpmTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.audit.query.ProcessInstanceLogDeleteBuilder;
import org.kie.internal.runtime.manager.audit.query.ProcessInstanceLogQueryBuilder;
import qa.tools.ikeeper.annotation.BZ;

/**
 * TODO:
 * - try end dates.
 * - try initiator
 */
public class ProcessInstanceLogCleanTest extends JbpmTestCase {

    private static final String HELLO_WORLD_PROCESS =
            "org/jbpm/test/functional/common/HelloWorldProcess1.bpmn";
    private static final String HELLO_WORLD_PROCESS_ID =
            "org.jbpm.test.functional.common.HelloWorldProcess1";

    private static final String HELLO_WORLD_PROCESS2 =
            "org/jbpm/test/functional/common/HelloWorldProcess2.bpmn";
    private static final String HELLO_WORLD_PROCESS2_ID =
            "org.jbpm.test.functional.common.HelloWorldProcess2";

    private static final String PARENT_PROCESS_INFO =
            "org/jbpm/test/functional/common/ParentProcessInfo.bpmn2";
    private static final String PARENT_PROCESS_INFO_ID =
            "org.jbpm.test.functional.common.ParentProcessInfo";

    private static final String PARENT_PROCESS_CALLER =
            "org/jbpm/test/functional/common/ParentProcessInfo-Caller.bpmn2";
    private static final String PARENT_PROCESS_CALLER_ID =
            "org.jbpm.test.functional.common.ParentProcessInfo-Caller";

    private static final String HUMAN_TASK =
            "org/jbpm/test/functional/common/HumanTask.bpmn2";
    private static final String HUMAN_TASK_ID =
            "org.jbpm.test.functional.common.HumanTask";

    private static final String HELLO_WORLD_P1NAME = "HelloWorldProcess1";
    private static final String HELLO_WORLD_P2NAME = "HelloWorldProcess2";

    private JPAAuditLogService auditService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        auditService = new JPAAuditLogService(getEmf());
        auditService.clear();
    }

    @Override
    public void tearDown() throws Exception {
        try {
            auditService.clear();
            auditService.dispose();
        } finally {
            super.tearDown();
        }
    }

    @Test
    public void deleteLogsByProcessName() {
        KieSession kieSession = createKSession(HELLO_WORLD_PROCESS);

        startProcess(kieSession, HELLO_WORLD_PROCESS_ID, 2);

        // Check that all records are created.
        List<ProcessInstanceLog> resultList = auditService.processInstanceLogQuery()
                .processId(HELLO_WORLD_PROCESS_ID)
                .processVersion("1.0")
                .build()
                .getResultList();
        Assertions.assertThat(resultList).hasSize(2);
        Assertions.assertThat(resultList)
                .extracting("processName")
                .containsExactly(HELLO_WORLD_P1NAME, HELLO_WORLD_P1NAME);

        // Perform delete
        int resultCount = auditService.processInstanceLogDelete()
                .processName(HELLO_WORLD_P1NAME)
                .build()
                .execute();
        Assertions.assertThat(resultCount).isEqualTo(2);

        // Check that all records are gone
        resultList = auditService.processInstanceLogQuery()
                .processId(HELLO_WORLD_PROCESS_ID)
                .processVersion("1.0")
                .build()
                .getResultList();
        Assertions.assertThat(resultList).hasSize(0);
    }

    @Test
    public void deleteLogsByProcessId() {
        KieSession kieSession = createKSession(HELLO_WORLD_PROCESS);

        startProcess(kieSession, HELLO_WORLD_PROCESS_ID, 3);

        // Check that all records are created.
        List<ProcessInstanceLog> resultList = auditService.processInstanceLogQuery()
                .processName(HELLO_WORLD_P1NAME)
                .processVersion("1.0")
                .build()
                .getResultList();
        Assertions.assertThat(resultList).hasSize(3);
        Assertions.assertThat(resultList)
                .extracting("processId")
                .containsExactly(HELLO_WORLD_PROCESS_ID, HELLO_WORLD_PROCESS_ID, HELLO_WORLD_PROCESS_ID);

        // Perform delete
        int resultCount = auditService.processInstanceLogDelete()
                .processId(HELLO_WORLD_PROCESS_ID)
                .build()
                .execute();
        Assertions.assertThat(resultCount).isEqualTo(3);

        // Check that all records are gone
        resultList = auditService.processInstanceLogQuery()
                .processName(HELLO_WORLD_P1NAME)
                .processVersion("1.0")
                .build()
                .getResultList();
        Assertions.assertThat(resultList).hasSize(0);
    }

    @Test
    public void deleteLogsByVersion() {
        KieSession kieSession = createKSession(HELLO_WORLD_PROCESS);
        startProcess(kieSession, HELLO_WORLD_PROCESS_ID, 7);
        disposeRuntimeManager();
        kieSession = createKSession(HELLO_WORLD_PROCESS2);
        startProcess(kieSession, HELLO_WORLD_PROCESS2_ID, 2);

        // Delete all logs of version 1.1
        ProcessInstanceLogDeleteBuilder deleteBuilder = auditService
                .processInstanceLogDelete()
                .processVersion("1.0");
        int deleteResult = deleteBuilder.build().execute();
        Assertions.assertThat(deleteResult).isEqualTo(7);

        // Make sure that the 1.1 version logs are gone
        List<ProcessInstanceLog> resultList = auditService
                .processInstanceLogQuery()
                .processVersion("1.0")
                .build()
                .getResultList();
        Assertions.assertThat(resultList).hasSize(0);

        // Now check that 1.0 version logs are present
        resultList = auditService
                .processInstanceLogQuery()
                .processVersion("1.1")
                .build()
                .getResultList();
        Assertions.assertThat(resultList).hasSize(2);
        Assertions.assertThat(resultList).extracting("processVersion").containsExactly("1.1", "1.1");

    }

    @Test
    public void deleteLogsWithStatusActive() {
        KieSession kieSession = null;
        List<ProcessInstance> instanceList1 = null;
        List<ProcessInstance> instanceList2 = null;

        try {
            kieSession = createKSession(HELLO_WORLD_PROCESS, HUMAN_TASK);
            instanceList1 = startProcess(kieSession, HELLO_WORLD_PROCESS_ID, 3);
            instanceList2 = startProcess(kieSession, HUMAN_TASK_ID, 5);

            ProcessInstanceLogDeleteBuilder deleteBuilder = auditService.processInstanceLogDelete().status(ProcessInstance.STATE_ACTIVE);
            int deleteResult = deleteBuilder.build().execute();
            Assertions.assertThat(deleteResult).isEqualTo(5);

            ProcessInstanceLogQueryBuilder queryBuilder = auditService.processInstanceLogQuery().status(ProcessInstance.STATE_COMPLETED);
            List<ProcessInstanceLog> queryList = queryBuilder.build().getResultList();

            Assertions.assertThat(queryList).hasSize(3);
            Assertions.assertThat(queryList).extracting("processId").containsExactly(HELLO_WORLD_PROCESS_ID, HELLO_WORLD_PROCESS_ID, HELLO_WORLD_PROCESS_ID);
            Assertions.assertThat(queryList).extracting("processVersion").containsExactly("1.0", "1.0", "1.0");
            Assertions.assertThat(queryList).extracting("status").containsExactly(ProcessInstance.STATE_COMPLETED, ProcessInstance.STATE_COMPLETED, ProcessInstance.STATE_COMPLETED);
        } finally {
            if (instanceList2 != null) {
                abortProcess(kieSession, instanceList2);
            }
            if (kieSession != null) {
                kieSession.dispose();
            }
        }
    }

    @Test
    @BZ("1188702")
    public void deleteLogsByDate() {
        Date testStartDate = new Date();

        KieSession kieSession = createKSession(HELLO_WORLD_PROCESS);

        startProcess(kieSession, HELLO_WORLD_PROCESS_ID, 4);

        List<ProcessInstanceLog> resultList = auditService.processInstanceLogQuery()
                .startDateRangeStart(testStartDate)
                .build()
                .getResultList();
        Assertions.assertThat(resultList)
                .hasSize(4)
                .extracting("processId")
                .containsExactly(HELLO_WORLD_PROCESS_ID, HELLO_WORLD_PROCESS_ID,
                        HELLO_WORLD_PROCESS_ID, HELLO_WORLD_PROCESS_ID);

        // Delete the last 3 logs in the list
        for (int i = resultList.size() - 1; i > 0; i--) {
            int resultCount = auditService.processInstanceLogDelete()
                    .startDate(resultList.get(i).getStart())
                    .build()
                    .execute();
            Assertions.assertThat(resultCount).isEqualTo(1);
        }

        // Attempt to delete with a date later than end of all the instances
        int resultCount = auditService.processInstanceLogDelete()
                .startDate(new Date())
                .build()
                .execute();
        Assertions.assertThat(resultCount).isEqualTo(0);

        // Check the last instance
        List<ProcessInstanceLog> resultList2 = auditService.processInstanceLogQuery()
                .startDateRangeStart(testStartDate)
                .build()
                .getResultList();
        Assertions.assertThat(resultList2).hasSize(1);
        Assertions.assertThat(resultList2.get(0)).isEqualTo(resultList.get(0));
    }

    @Test
    @BZ("1192498")
    public void deleteLogsByDateRange() throws InterruptedException {
        KieSession kieSession = createKSession(PARENT_PROCESS_CALLER, PARENT_PROCESS_INFO, HELLO_WORLD_PROCESS);

        Date date1 = new Date();
        startProcess(kieSession, PARENT_PROCESS_CALLER_ID, 4);
        Date date2 = new Date();
        Thread.sleep(1000);
        startProcess(kieSession, HELLO_WORLD_PROCESS_ID, 2);
        Date date3 = new Date();

        int beforeSize = getProcessInstanceLogSize(getYesterday(), getTomorrow());
        Assertions.assertThat(beforeSize).isEqualTo(10);

        int resultCount = auditService.processInstanceLogDelete()
                .startDateRangeStart(date1)
                .startDateRangeEnd(date2)
                .build()
                .execute();
        // 1 for ReussableSubprocess and 1 for ParentProcessInfo called by it
        Assertions.assertThat(resultCount).isEqualTo(8);

        List<ProcessInstanceLog> resultList = auditService.processInstanceLogQuery()
                .startDateRangeEnd(date3)
                .build()
                .getResultList();
        Assertions.assertThat(resultList)
                .hasSize(2)
                .extracting("processId").containsExactly(HELLO_WORLD_PROCESS_ID, HELLO_WORLD_PROCESS_ID);

        int afterSize = getProcessInstanceLogSize(date1, date3);
        Assertions.assertThat(afterSize).isEqualTo(2);
    }

    @Test
    @BZ("1192498")
    public void deleteLogsByDateRangeEndingYesterday() {
        deleteLogsByDateRange(getYesterday(), getYesterday(), false);
    }

    @Test
    @BZ("1192498")
    public void deleteLogsByDateRangeIncludingToday() {
        deleteLogsByDateRange(getYesterday(), getTomorrow(), true);
    }

    @Test
    @BZ("1192498")
    public void deleteLogsByDateRangeStartingTomorrow() {
        deleteLogsByDateRange(getTomorrow(), getTomorrow(), false);
    }

    private void deleteLogsByDateRange(Date startDate, Date endDate, boolean expectRemoval) {
        KieSession kieSession = createKSession(HELLO_WORLD_PROCESS);

        startProcess(kieSession, HELLO_WORLD_PROCESS_ID, 2);

        int beforeSize = getProcessInstanceLogSize(getYesterday(), getTomorrow());
        Assertions.assertThat(beforeSize).isEqualTo(2);

        int resultCount = auditService.processInstanceLogDelete()
                .startDateRangeStart(startDate)
                .startDateRangeEnd(endDate)
                .build()
                .execute();
        // 1 for ReussableSubprocess and 1 for ParentProcessInfo called by it
        Assertions.assertThat(resultCount).isEqualTo(expectRemoval ? 2 : 0);

        int afterSize = getProcessInstanceLogSize(getYesterday(), getTomorrow());
        Assertions.assertThat(afterSize).isEqualTo(expectRemoval ? 0 : 2);
    }


    private int getProcessInstanceLogSize(Date startDateRangeStart, Date startDateRangeEnd) {
        return auditService.processInstanceLogQuery()
                .startDateRangeStart(startDateRangeStart)
                .startDateRangeEnd(startDateRangeEnd)
                .build()
                .getResultList()
                .size();
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