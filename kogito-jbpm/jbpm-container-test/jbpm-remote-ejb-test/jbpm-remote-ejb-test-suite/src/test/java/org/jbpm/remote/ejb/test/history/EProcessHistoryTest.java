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

package org.jbpm.remote.ejb.test.history;

import java.util.Collection;
import java.util.List;

import org.assertj.core.api.Assertions;

import org.jbpm.remote.ejb.test.ProcessDefinitions;
import org.jbpm.remote.ejb.test.RemoteEjbTest;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.junit.Test;
import org.kie.api.runtime.query.QueryContext;

public class EProcessHistoryTest extends RemoteEjbTest {

    @Test
    public void testAllProcessInstanceLogs() {
        QueryContext queryContext = new QueryContext(0, Integer.MAX_VALUE);
        List<ProcessInstanceDesc> logs = ejb.getProcessInstances(queryContext);

        int originalSize = logs.size();

        startProcess(ProcessDefinitions.SCRIPT_TASK, 4);
        startProcess(ProcessDefinitions.HUMAN_TASK, 3);

        logs = ejb.getProcessInstances(queryContext);
        Assertions.assertThat(logs).hasSize(originalSize + 4 + 3);
    }

    @Test
    public void testProcessInstanceLogsWithDefinitionId() {
        QueryContext queryContext = new QueryContext(0, Integer.MAX_VALUE);
        int originalSize = ejb.getProcessInstancesByProcessDefinition(ProcessDefinitions.HUMAN_TASK, queryContext).size();
        startProcess(ProcessDefinitions.SCRIPT_TASK, 3);
        startProcess(ProcessDefinitions.HUMAN_TASK, 5);

        Collection<ProcessInstanceDesc> logs = ejb.getProcessInstancesByProcessDefinition(ProcessDefinitions.HUMAN_TASK, queryContext);
        Assertions.assertThat(logs).hasSize(originalSize + 5);
    }

    @Test
    public void testProcessInstanceLogWithInstanceId() {
        startProcess(ProcessDefinitions.HUMAN_TASK);
        long pid2 = startProcess(ProcessDefinitions.SCRIPT_TASK);

        ProcessInstanceDesc log = ejb.getProcessInstanceById(pid2);
        Assertions.assertThat(log.getId()).isEqualTo(pid2);
        Assertions.assertThat(log.getProcessId()).isEqualTo(ProcessDefinitions.SCRIPT_TASK);
    }

    @Test
    public void testNodeInstanceLogs() {
        long pid = startProcess(ProcessDefinitions.SCRIPT_TASK);
        List<NodeInstanceDesc> logs = ejb.getProcessInstanceFullHistory(pid);
        Assertions.assertThat(logs.size()).isEqualTo(6);
        String[] expected = {"_A3185DDF-23A7-48B7-A2FE-7C0FE39F6691", "_3C8F4385-5348-479C-83EE-0C2DC2004F1A",
                "_DC07735C-FA99-414C-AEAC-9F4CE0CF24F9", "_DC07735C-FA99-414C-AEAC-9F4CE0CF24F9",
                "_3C8F4385-5348-479C-83EE-0C2DC2004F1A", "_A3185DDF-23A7-48B7-A2FE-7C0FE39F6691"};
        for (int i = 0; i < logs.size(); ++i) {
            NodeInstanceDesc node = logs.get(i);
            Assertions.assertThat(node.getNodeId()).isEqualTo(expected[i]);
        }
    }

}
