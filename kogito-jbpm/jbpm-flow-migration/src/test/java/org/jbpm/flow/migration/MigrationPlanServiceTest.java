/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jbpm.flow.migration;

import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.workflow.instance.impl.ExtendedNodeInstanceImpl;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jbpm.ruleflow.core.WorkflowElementIdentifierFactory.fromExternalFormat;

public class MigrationPlanServiceTest {

    @Test
    public void testMigrationProcessInstance() {
        MigrationPlanService service = new MigrationPlanService();

        WorkflowProcessInstanceImpl processImpl = new RuleFlowProcessInstance();
        processImpl.setProcess(new DummyProcess("process_A", "1"));
        service.migrateProcessElement(processImpl);

        assertThat(processImpl)
                .hasFieldOrPropertyWithValue("processId", "process_B")
                .hasFieldOrPropertyWithValue("processVersion", "2");

    }

    @Test
    public void testMigrationProcessNonExisting() {
        MigrationPlanService service = new MigrationPlanService();
        WorkflowProcessInstanceImpl processImpl = new RuleFlowProcessInstance();
        processImpl.setProcess(new DummyProcess("process_C", "1"));
        service.migrateProcessElement(processImpl);

        assertThat(processImpl)
                .hasFieldOrPropertyWithValue("processId", "process_C")
                .hasFieldOrPropertyWithValue("processVersion", "1");
    }

    @Test
    public void testMigrationProcessNotRightVersion() {
        MigrationPlanService service = new MigrationPlanService();
        WorkflowProcessInstanceImpl processImpl = new RuleFlowProcessInstance();
        processImpl.setProcess(new DummyProcess("process_A", "3"));
        service.migrateProcessElement(processImpl);

        assertThat(processImpl)
                .hasFieldOrPropertyWithValue("processId", "process_A")
                .hasFieldOrPropertyWithValue("processVersion", "3");
    }

    @Test
    public void testMigrationNode() {
        MigrationPlanService service = new MigrationPlanService();
        WorkflowProcessInstanceImpl processImpl = new RuleFlowProcessInstance();
        processImpl.setProcess(new DummyProcess("process_A", "1"));
        ExtendedNodeInstanceImpl nodeInstanceImpl = new ExtendedNodeInstanceImpl() {
        };
        nodeInstanceImpl.setProcessInstance(processImpl);
        nodeInstanceImpl.setNodeId(fromExternalFormat("node_1"));
        service.migrateNodeElement(nodeInstanceImpl);

        assertThat(nodeInstanceImpl)
                .hasFieldOrPropertyWithValue("nodeId", fromExternalFormat("node_2"));
    }

    @Test
    public void testMigrationNodeNextItem() {
        MigrationPlanService service = new MigrationPlanService();
        WorkflowProcessInstanceImpl processImpl = new RuleFlowProcessInstance();
        processImpl.setProcess(new DummyProcess("process_A", "1"));
        ExtendedNodeInstanceImpl nodeInstanceImpl = new ExtendedNodeInstanceImpl() {
        };
        nodeInstanceImpl.setProcessInstance(processImpl);
        nodeInstanceImpl.setNodeId(fromExternalFormat("node_2"));
        service.migrateNodeElement(nodeInstanceImpl);

        assertThat(nodeInstanceImpl)
                .hasFieldOrPropertyWithValue("nodeId", fromExternalFormat("node_3"));
    }

    @Test
    public void testMigrationNodeNonExistent() {
        MigrationPlanService service = new MigrationPlanService();
        WorkflowProcessInstanceImpl processImpl = new RuleFlowProcessInstance();
        processImpl.setProcess(new DummyProcess("process_A", "1"));
        ExtendedNodeInstanceImpl nodeInstanceImpl = new ExtendedNodeInstanceImpl() {
        };
        nodeInstanceImpl.setProcessInstance(processImpl);
        nodeInstanceImpl.setNodeId(fromExternalFormat("node_3"));
        service.migrateNodeElement(nodeInstanceImpl);

        assertThat(nodeInstanceImpl)
                .hasFieldOrPropertyWithValue("nodeId", fromExternalFormat("node_3"));
    }

    @Test
    public void testMigrationNodeNotMigratedWrongProcess() {
        MigrationPlanService service = new MigrationPlanService();
        WorkflowProcessInstanceImpl processImpl = new RuleFlowProcessInstance();
        processImpl.setProcess(new DummyProcess("process_C", "1"));
        ExtendedNodeInstanceImpl nodeInstanceImpl = new ExtendedNodeInstanceImpl() {
        };
        nodeInstanceImpl.setProcessInstance(processImpl);
        nodeInstanceImpl.setNodeId(fromExternalFormat("node_3"));
        service.migrateNodeElement(nodeInstanceImpl);

        assertThat(nodeInstanceImpl)
                .hasFieldOrPropertyWithValue("nodeId", fromExternalFormat("node_3"));
    }
}
