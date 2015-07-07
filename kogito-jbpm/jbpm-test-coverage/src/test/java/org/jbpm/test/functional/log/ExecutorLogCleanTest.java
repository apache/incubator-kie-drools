/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.test.functional.log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.jbpm.executor.impl.jpa.ExecutorJPAAuditService;
import org.jbpm.executor.impl.wih.AsyncWorkItemHandler;
import org.jbpm.test.JbpmAsyncJobTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.executor.api.ErrorInfo;
import org.kie.internal.executor.api.STATUS;
import qa.tools.ikeeper.annotation.BZ;

public class ExecutorLogCleanTest extends JbpmAsyncJobTestCase {

    private static final String ASYNC_DATA_EXEC = "org/jbpm/test/functional/common/AsyncDataExecutor.bpmn2";
    private static final String ASYNC_DATA_EXEC_ID = "org.jbpm.test.functional.common.AsyncDataExecutor";

    private static final int EXECUTOR_RETRIES = 1;

    private ExecutorJPAAuditService auditService;

    public ExecutorLogCleanTest() {
        super(EXECUTOR_RETRIES);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        auditService = new ExecutorJPAAuditService(getEmf());
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
    public void deleteInfoLogsByStatus() throws Exception {
        KieSession kieSession = createKSession(ASYNC_DATA_EXEC);
        WorkItemManager wim = kieSession.getWorkItemManager();
        wim.registerWorkItemHandler("async", new AsyncWorkItemHandler(getExecutorService()));

        Map<String, Object> pm = new HashMap<String, Object>();
        pm.put("command", "org.jbpm.test.jobexec.UserCommand");
        ProcessInstance pi = kieSession.startProcess(ASYNC_DATA_EXEC_ID, pm);

        // Wait for the job to complete
        Thread.sleep(10 * 1000);

        // Assert completion of the job
        Assertions.assertThat(getExecutorService().getCompletedRequests()).hasSize(1);

        // Delete a record
        int resultCount = auditService.requestInfoLogDeleteBuilder()
                .status(STATUS.DONE)
                .build()
                .execute();
        Assertions.assertThat(resultCount).isEqualTo(1);

        // Assert remaining records
        Assertions.assertThat(getExecutorService().getCompletedRequests()).hasSize(0);
    }

    @Test
    @BZ("1188702")
    public void deleteErrorLogsByDate() throws Exception {
        KieSession ksession = createKSession(ASYNC_DATA_EXEC);
        WorkItemManager wim = ksession.getWorkItemManager();
        wim.registerWorkItemHandler("async", new AsyncWorkItemHandler(getExecutorService()));

        Map<String, Object> pm = new HashMap<String, Object>();
        pm.put("command", "org.jbpm.test.jobexec.UserFailingCommand");
        ProcessInstance pi = ksession.startProcess(ASYNC_DATA_EXEC_ID, pm);

        // Wait for the all retries to fail
        Thread.sleep(10 * 1000);

        // Assert comletion of the job
        List<ErrorInfo> errorList = getExecutorService().getAllErrors();
        Assertions.assertThat(errorList).hasSize(2);

        // Delete a record
        int resultCount = auditService.errorInfoLogDeleteBuilder()
                .date(errorList.get(0).getTime())
                .build()
                .execute();
        Assertions.assertThat(resultCount).isEqualTo(1);

        // Assert remaining records
        Assertions.assertThat(getExecutorService().getAllErrors()).hasSize(1);

        // Abort running process instance
        ksession.abortProcessInstance(pi.getId());
    }

}
