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

package org.jbpm.test.regression.subprocess;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.jbpm.test.JbpmTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import qa.tools.ikeeper.annotation.BZ;

public class ReusableSubprocessTest extends JbpmTestCase {

    private static final String WAIT_FOR_COMPLETION_FALSE_PARENT =
            "org/jbpm/test/regression/subprocess/ReusableSubprocess-waitForCompletionFalse-parent.bpmn2";
    private static final String WAIT_FOR_COMPLETION_FALSE_PARENT_ID =
            "org.jbpm.test.regression.subprocess.ReusableSubprocess-waitForCompletionFalse-parent";
    private static final String WAIT_FOR_COMPLETION_FALSE_SUBPROCESS =
            "org/jbpm/test/regression/subprocess/ReusableSubprocess-waitForCompletionFalse-subprocess.bpmn2";

    private static final String DEPENDENT_SUBPROCESS_ABORT_PARENT =
            "org/jbpm/test/regression/subprocess/ReusableSubprocess-dependentSubprocessAbort-parent.bpmn2";
    private static final String DEPENDENT_SUBPROCESS_ABORT_PARENT_ID =
            "org.jbpm.test.regression.subprocess.ReusableSubprocess-dependentSubprocessAbort-parent";
    private static final String DEPENDENT_SUBPROCESS_ABORT_SUBPROCESS =
            "org/jbpm/test/regression/subprocess/ReusableSubprocess-dependentSubprocessAbort-subprocess.bpmn2";
    private static final String DEPENDENT_SUBPROCESS_ABORT_SUBPROCESS_ID =
            "org.jbpm.test.regression.subprocess.ReusableSubprocess-dependentSubprocessAbort-subprocess";

    @Test
    @BZ("1194180")
    public void testWaitForCompletionFalse() throws Exception {
        createRuntimeManager(Strategy.PROCESS_INSTANCE, "BZ1194180-ppi-manager", WAIT_FOR_COMPLETION_FALSE_PARENT,
                WAIT_FOR_COMPLETION_FALSE_SUBPROCESS);

        KieSession ksession = getRuntimeEngine().getKieSession();
        Assertions.assertThat(ksession).isNotNull();
        ksession.startProcess(WAIT_FOR_COMPLETION_FALSE_PARENT_ID);
        Thread.sleep(3000);
    }

    @Test
    @BZ("1128597")
    public void testDependentSubprocessAbort() {
        RuntimeManager manager = createRuntimeManager(Strategy.PROCESS_INSTANCE, "myPpiManager",
                DEPENDENT_SUBPROCESS_ABORT_PARENT, DEPENDENT_SUBPROCESS_ABORT_SUBPROCESS);
        Assertions.assertThat(manager).isNotNull();

        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = getRuntimeEngine(ProcessInstanceIdContext.get());

        KieSession ksession = runtime.getKieSession();
        Assertions.assertThat(ksession).isNotNull();
        Assertions.assertThat(ksession.getId()).isEqualTo(2);

        ProcessInstance pi1 = ksession.startProcess(DEPENDENT_SUBPROCESS_ABORT_PARENT_ID);
        assertProcessInstanceActive(pi1.getId());

        // Aborting the parent process
        ksession.abortProcessInstance(pi1.getId());

        AuditService logService = getLogService();

        List<? extends ProcessInstanceLog> logs = logService.findActiveProcessInstances(DEPENDENT_SUBPROCESS_ABORT_PARENT_ID);
        Assertions.assertThat(logs).isNotNull();
        Assertions.assertThat(logs).hasSize(0);

        logs = logService.findActiveProcessInstances(DEPENDENT_SUBPROCESS_ABORT_SUBPROCESS_ID);
        Assertions.assertThat(logs).isNotNull();
        Assertions.assertThat(logs).hasSize(0);

        logs = logService.findProcessInstances(DEPENDENT_SUBPROCESS_ABORT_PARENT_ID);
        Assertions.assertThat(logs).isNotNull();
        Assertions.assertThat(logs).hasSize(1);
        assertProcessInstanceAborted(logs.get(0).getProcessInstanceId());

        logs = logService.findProcessInstances(DEPENDENT_SUBPROCESS_ABORT_SUBPROCESS_ID);
        Assertions.assertThat(logs).isNotNull();
        Assertions.assertThat(logs).hasSize(1);
        assertProcessInstanceAborted(logs.get(0).getProcessInstanceId());

        manager.close();
    }

}
