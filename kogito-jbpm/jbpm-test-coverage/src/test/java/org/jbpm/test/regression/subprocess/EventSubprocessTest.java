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

import org.assertj.core.api.Assertions;
import org.jbpm.bpmn2.handler.SignallingTaskHandlerDecorator;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.test.JbpmTestCase;
import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.WorkflowRuntimeException;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import qa.tools.ikeeper.annotation.BZ;

import static org.junit.Assert.*;

public class EventSubprocessTest extends JbpmTestCase {

    private static final String ERROR_CODE_EXCEPTION =
            "org/jbpm/test/regression/subprocess/EventSubprocess-errorCodeException.bpmn2";
    private static final String ERROR_CODE_EXCEPTION_ID =
            "org.jbpm.test.regression.subprocess.EventSubprocess-errorCodeException";

    @Test
    @BZ("1082111")
    public void testErrorCodeException() {
        KieSession ksession = createKSession(ERROR_CODE_EXCEPTION);

        ksession.getWorkItemManager().registerWorkItemHandler("Request Handler",
                new SignallingTaskHandlerDecorator(ExceptionOnPurposeHandler.class, "Error-90277"));
        ksession.getWorkItemManager().registerWorkItemHandler("Error Handler", new SystemOutWorkItemHandler());

        try {
            ProcessInstance processInstance = ksession.startProcess(ERROR_CODE_EXCEPTION_ID);
            assertProcessInstanceNotActive(processInstance.getId(), ksession);
            Assertions.assertThat(((WorkflowProcessInstance) processInstance).getOutcome()).isEqualTo("90277");
        } catch (WorkflowRuntimeException e) {
            fail("Error code exceptions in subprocess does not work.");
        }
    }

    public static class ExceptionOnPurposeHandler implements WorkItemHandler {

        @Override
        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
            throw new RuntimeException("Thrown on purpose");
        }

        @Override
        public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        }

    }

}
