/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.persistence.jdbc;

import java.util.Collections;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.persistence.jdbc.JDBCProcessInstances;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnProcessInstance;
import org.kie.kogito.process.bpmn2.BpmnVariables;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JdbcProcessInstancesWithLockIT extends TestHelper {

    @ParameterizedTest
    @MethodSource("datasources")
    public void testUpdate(DataSource dataSource) {
        var factory = new TestProcessInstancesFactory(dataSource, true);
        BpmnProcess process = createProcess(factory, "BPMN2-UserTask.bpmn2");
        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")));
        processInstance.start();

        JDBCProcessInstances processInstances = (JDBCProcessInstances) process.instances();
        assertThat(processInstances.size()).isOne();
        Optional<?> foundOne = processInstances.findById(processInstance.id());
        BpmnProcessInstance instanceOne = (BpmnProcessInstance) foundOne.get();
        foundOne = processInstances.findById(processInstance.id());
        BpmnProcessInstance instanceTwo = (BpmnProcessInstance) foundOne.get();
        assertEquals(1L, instanceOne.version());
        assertEquals(1L, instanceTwo.version());
        instanceOne.updateVariables(BpmnVariables.create(Collections.singletonMap("s", "test")));
        try {
            BpmnVariables testvar = BpmnVariables.create(Collections.singletonMap("ss", "test"));
            instanceTwo.updateVariables(testvar);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("The document with ID: " + instanceOne.id() + " was updated or deleted by other request.");
        }
        foundOne = processInstances.findById(processInstance.id());
        instanceOne = (BpmnProcessInstance) foundOne.get();
        assertEquals(2L, instanceOne.version());

        processInstances.remove(processInstance.id());
        assertThat(processInstances.size()).isZero();
        assertThat(process.instances().values()).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("datasources")
    public void testRemove(DataSource dataSource) {
        var factory = new TestProcessInstancesFactory(dataSource, true);
        BpmnProcess process = createProcess(factory, "BPMN2-UserTask.bpmn2");
        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections.singletonMap("test", "test")));
        processInstance.start();

        JDBCProcessInstances processInstances = (JDBCProcessInstances) process.instances();
        assertThat(processInstances.size()).isOne();
        Optional<?> foundOne = processInstances.findById(processInstance.id());
        BpmnProcessInstance instanceOne = (BpmnProcessInstance) foundOne.get();
        foundOne = processInstances.findById(processInstance.id());
        BpmnProcessInstance instanceTwo = (BpmnProcessInstance) foundOne.get();
        assertEquals(1L, instanceOne.version());
        assertEquals(1L, instanceTwo.version());

        processInstances.remove(instanceOne.id());
        try {
            String id = instanceTwo.id();
            processInstances.remove(id);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("The document with ID: " + instanceOne.id() + " was deleted by other request.");
        }
    }
}
