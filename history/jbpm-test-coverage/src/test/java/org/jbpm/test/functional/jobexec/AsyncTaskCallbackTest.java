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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.executor.impl.wih.AsyncWorkItemHandler;
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.audit.VariableInstanceLog;
import org.jbpm.test.JbpmAsyncJobTestCase;
import org.jbpm.test.listener.process.NodeLeftCountDownProcessEventListener;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemManager;

import static org.junit.Assert.*;

//
// BZ-1121396
// BZ-1121040
//
// Test outline:
//    Start a process which accepts a command class. This process also expects a signal 'continue' to
//    be sent. If not received then execution will not end.
//
//    The passed in command class does two things.
//    1) sets the _user property as proof it has been picked up and processed by the job executor
//    2) registers a user defined callback which sends the 'continue' signal.
//
//
public class AsyncTaskCallbackTest extends JbpmAsyncJobTestCase {

    public static final String ASYNC_EXECUTOR_CALLBACK = "org/jbpm/test/functional/jobexec/AsyncExecutorCallback.bpmn2";
    public static final String ASYNC_EXECUTOR_CALLBACK_ID = "org.jbpm.test.functional.jobexec.AsyncExecutorCallback";

    public static final String ASYNC_DATA_EXECUTOR = "org/jbpm/test/functional/jobexec/AsyncDataExecutor.bpmn2";
    public static final String ASYNC_DATA_EXECUTOR_ID = "org.jbpm.test.functional.jobexec.AsyncDataExecutor";

    public static final String CALLBACK_COMMAND = "org.jbpm.test.jobexec.UserCommandWithCallback";

    private JPAAuditLogService auditLogService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        auditLogService = new JPAAuditLogService(getEmf());
        auditLogService.clear();
    }

    @Override
    public void tearDown() throws Exception {
        try {
            auditLogService.clear();
            auditLogService.dispose();
        } finally {
            super.tearDown();
        }
    }

    @Test(timeout=30000)
    public void testTaskCallback() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Continue", 1);
        addProcessEventListener(countDownListener);
        KieSession ksession = createKSession(ASYNC_EXECUTOR_CALLBACK, ASYNC_DATA_EXECUTOR);
        WorkItemManager wim = ksession.getWorkItemManager();
        wim.registerWorkItemHandler("async", new AsyncWorkItemHandler(getExecutorService()));

        Map<String, Object> pm = new HashMap<String, Object>();
        pm.put("_command", CALLBACK_COMMAND);
        ProcessInstance pi = ksession.startProcess(ASYNC_EXECUTOR_CALLBACK_ID, pm);

        // Wait for the job to be picked up and processed. The job will send
        // the 'Continue' signal on OK or Fail. We expect OK. 
        countDownListener.waitTillCompleted();
        
        ProcessInstance processInstance = ksession.getProcessInstance(pi.getId());
        assertNull(processInstance);

        // Make sure the user registered callback was executed (a.k.a the "continue" signal was received by the process)
        assertNodeTriggered(pi.getId(), "Process async", "Continue");
        assertProcessInstanceCompleted(pi.getId());

        // Make sure the job was processed by the job executor (a.k.a the _user property was set)
        List<VariableInstanceLog> varLogList = auditLogService.findVariableInstances(pi.getId(), "_user");
        assertEquals(1, varLogList.size());
        VariableInstanceLog userVarLog = varLogList.get(0);
        assertEquals("[Name=john after command execution, age=25]", userVarLog.getValue());
    }

}
