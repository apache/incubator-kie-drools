/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.process.impl;

import java.util.Map;

import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.ProcessInstanceManager;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.junit.jupiter.api.Test;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.ProcessRuntime;
import org.kie.kogito.Model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AbstractProcessInstanceTest {

    @Test
    public void testCreteProcessInstance() {
        AbstractProcess process = mock(AbstractProcess.class);
        when(process.legacyProcess()).thenReturn(mock(Process.class));
        InternalProcessRuntime pr = mock(InternalProcessRuntime.class);
        WorkflowProcessInstanceImpl wpi = mock(WorkflowProcessInstanceImpl.class);
        when(pr.createProcessInstance(any(), any(), any())).thenReturn(wpi);
        ProcessInstanceManager pim = mock(ProcessInstanceManager.class);
        when(pr.getProcessInstanceManager()).thenReturn(pim);
        AbstractProcessInstance pi = new TestProcessInstance(process, new TestModel(), pr);

        assertThat(pi.status()).isEqualTo(ProcessInstance.STATE_PENDING);
        assertThat(pi.id()).isNull();
        assertThat(pi.businessKey()).isNull();

        verify(pim, never()).addProcessInstance(any(), any());
    }

    static class TestProcessInstance extends AbstractProcessInstance<TestModel> {

        public TestProcessInstance(AbstractProcess process, TestModel variables, ProcessRuntime rt) {
            super(process, variables, rt);
        }
    }

    static class TestModel implements Model {

        @Override
        public Map<String, Object> toMap() {
            return null;
        }

        @Override
        public void fromMap(Map<String, Object> params) {

        }
    }
}
