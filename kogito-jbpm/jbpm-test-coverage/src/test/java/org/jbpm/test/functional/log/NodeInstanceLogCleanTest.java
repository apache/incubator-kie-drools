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
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.test.JbpmTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.audit.NodeInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;

/**
 * TODO: Add deleteLogsByDate, deleteLogsByDateRange (however it is a bit redundant).
 */
public class NodeInstanceLogCleanTest extends JbpmTestCase {

    private static final String HELLO_WORLD_PROCESS =
            "org/jbpm/test/functional/common/HelloWorldProcess1.bpmn";
    private static final String HELLO_WORLD_PROCESS_ID =
            "org.jbpm.test.functional.common.HelloWorldProcess1";

    private static final String HUMAN_TASK_LOCALE =
            "org/jbpm/test/functional/log/NodeInstanceLogClean-humanTaskLocale.bpmn2";
    private static final String HUMAN_TASK_LOCALE_ID =
            "org.jbpm.test.functional.log.NodeInstanceLogClean-humanTaskLocale";

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
    public void deleteLogsByNodeNameAndInstanceId() {
        KieSession kieSession = createKSession(HELLO_WORLD_PROCESS);

        List<ProcessInstance> instanceList = startProcess(kieSession, HELLO_WORLD_PROCESS_ID, 2);

        // Delete one node named "Hello world"
        int resultCount = auditService.nodeInstanceLogDelete()
                .nodeName("Hello world")
                .processInstanceId(instanceList.get(0).getId())
                .build()
                .execute();
        // There are two node types in the log (value NodeInstanceLog.TYPE_ENTER and NodeInstanceLog.TYPE_EXIT)
        Assertions.assertThat(resultCount).isEqualTo(2);

        // Now check that all other node records are still present
        List<NodeInstanceLog> nodeList = auditService.nodeInstanceLogQuery()
                .nodeName("Start", "End", "Hello world")
                .build()
                .getResultList();
        Assertions.assertThat(nodeList)
                .hasSize(10)
                .extracting("nodeName")
                .containsOnly("Start", "End", "Hello world");
    }

    @Test
    public void deleteLogsByNodeId() {
        KieSession kieSession = createKSession(HELLO_WORLD_PROCESS);

        startProcess(kieSession, HELLO_WORLD_PROCESS_ID, 2);

        // Delete one node named "Hello world"
        int resultCount = auditService.nodeInstanceLogDelete()
                // the 'End' node
                .nodeId("_3")
                .build()
                .execute();
        Assertions.assertThat(resultCount).isEqualTo(4);

        // Now check that all other node records are still present
        List<NodeInstanceLog> nodeList = auditService.nodeInstanceLogQuery()
                .nodeId("_1", "_2", "_3")
                .build()
                .getResultList();
        Assertions.assertThat(nodeList)
                .hasSize(8)
                .extracting("nodeName")
                .containsOnly("Start", "Hello world");
    }

    @Test
    public void deleteLogsByNodeInstanceId() {
        KieSession kieSession = null;
        List<ProcessInstance> processInstanceList = null;

        try {
            kieSession = createKSession(HUMAN_TASK_LOCALE);

            processInstanceList = startProcess(kieSession, HUMAN_TASK_LOCALE_ID, 1);

            // Let's see how the code will manage Japan characters.
            List<NodeInstanceLog> nodeInstanceList = auditService.nodeInstanceLogQuery()
                    .nodeName("空手")
                    .build()
                    .getResultList();
            // We are expecting only NodeInstanceLog.TYPE_ENTERED as execution will be paused on the human task
            Assertions.assertThat(nodeInstanceList).hasSize(1);

            // Delete node named "空手" based on it's id.
            int resultCount = auditService.nodeInstanceLogDelete()
                    .nodeInstanceId(nodeInstanceList.get(0).getNodeId())
                    .build()
                    .execute();
            Assertions.assertThat(resultCount).isEqualTo(0);
        } finally {
            if (processInstanceList != null) {
                abortProcess(kieSession, processInstanceList);
            }
            if (kieSession != null) {
                kieSession.dispose();
            }
        }
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
