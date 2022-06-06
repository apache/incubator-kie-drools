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

import org.drools.util.io.ClassPathResource;
import org.junit.jupiter.api.Test;
import org.kie.kogito.auth.IdentityProviders;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.persistence.jdbc.JDBCProcessInstances;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnProcessInstance;
import org.kie.kogito.process.bpmn2.BpmnVariables;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_ACTIVE;
import static org.kie.kogito.internal.process.runtime.KogitoProcessInstance.STATE_COMPLETED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

abstract class AbstractProcessInstancesIT {

    public static final String TEST_ID = "02ac3854-46ee-42b7-8b63-5186c9889d96";
    public static SecurityPolicy securityPolicy = SecurityPolicy.of(IdentityProviders.of("john"));

    public static BpmnProcess createProcess(TestProcessInstancesFactory factory, String fileName) {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource(fileName)).get(0);
        process.setProcessInstancesFactory(factory);
        process.configure();
        process.instances().values(ProcessInstanceReadMode.MUTABLE).forEach(p -> p.abort());
        return process;
    }

    public static BpmnProcess configure(boolean lock) {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-UserTask-Script.bpmn2")).get(0);
        process.setProcessInstancesFactory(new TestProcessInstancesFactory(null, lock));
        process.configure();
        return process;
    }

    abstract DataSource getDataSource();

    @Test
    void testBasicTaskFlow() {
        var factory = new TestProcessInstancesFactory(getDataSource(), false);
        BpmnProcess process = createProcess(factory, "BPMN2-UserTask.bpmn2");
        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections
                .singletonMap("test", "test")));
        processInstance.start();

        assertThat(processInstance.status()).isEqualTo(STATE_ACTIVE);
        assertThat(processInstance.description()).isEqualTo("User Task");

        JDBCProcessInstances processInstances = (JDBCProcessInstances) process.instances();
        assertThat(processInstances.size()).isOne();
        assertThat(processInstances.exists(processInstance.id())).isTrue();
        verify(processInstances).create(any(), any());

        String testVar = (String) processInstance.variables().get("test");
        assertThat(testVar).isEqualTo("test");

        assertThat(processInstance.description()).isEqualTo("User Task");

        assertThat(process.instances().values().iterator().next().workItems(securityPolicy)).hasSize(1);

        WorkItem workItem = processInstance.workItems(securityPolicy).get(0);
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameters()).containsEntry("ActorId", "john");
        processInstance.completeWorkItem(workItem.getId(), null, securityPolicy);
        assertThat(processInstance.status()).isEqualTo(STATE_COMPLETED);

        processInstances = (JDBCProcessInstances) process.instances();
        verify(processInstances, times(2)).remove(processInstance.id());
        assertThat(processInstances.size()).isZero();
        assertThat(process.instances().values()).isEmpty();
    }

    @Test
    void testMultipleProcesses() {
        var factory = new TestProcessInstancesFactory(getDataSource(), false);
        BpmnProcess utProcess = createProcess(factory, "BPMN2-UserTask.bpmn2");
        ProcessInstance<BpmnVariables> utProcessInstance = utProcess.createInstance(BpmnVariables.create());
        utProcessInstance.start();

        BpmnProcess scriptProcess = createProcess(factory, "BPMN2-UserTask-Script.bpmn2");
        ProcessInstance<BpmnVariables> scriptProcessInstance = scriptProcess.createInstance(BpmnVariables.create());
        scriptProcessInstance.start();

        //Try to remove process instance from another process id
        ((JDBCProcessInstances) utProcess.instances()).remove(scriptProcessInstance.id());
        ((JDBCProcessInstances) scriptProcess.instances()).remove(utProcessInstance.id());

        assertThat(utProcess.instances().size()).isOne();
        assertThat(utProcess.instances().values()).hasSize(1);
        assertThat(utProcess.instances().findById(utProcessInstance.id())).isPresent();
        assertThat(utProcess.instances().findById(scriptProcessInstance.id())).isEmpty();

        assertThat(scriptProcess.instances().size()).isOne();
        assertThat(scriptProcess.instances().values()).hasSize(1);
        assertThat(scriptProcess.instances().findById(scriptProcessInstance.id())).isPresent();
        assertThat(scriptProcess.instances().findById(utProcessInstance.id())).isEmpty();

        ((JDBCProcessInstances) utProcess.instances()).remove(utProcessInstance.id());
        assertThat(utProcess.instances().size()).isZero();
        assertThat(utProcess.instances().values()).isEmpty();
        assertThat(utProcess.instances().findById(utProcessInstance.id())).isEmpty();
        assertThat(utProcess.instances().findById(scriptProcessInstance.id())).isEmpty();

        ((JDBCProcessInstances) scriptProcess.instances()).remove(scriptProcessInstance.id());
        assertThat(scriptProcess.instances().size()).isZero();
        assertThat(scriptProcess.instances().values()).isEmpty();
        assertThat(scriptProcess.instances().findById(scriptProcessInstance.id())).isEmpty();
        assertThat(scriptProcess.instances().findById(utProcessInstance.id())).isEmpty();
    }

    @Test
    void testBasicFlow() {
        var factory = new TestProcessInstancesFactory(getDataSource(), false);
        BpmnProcess process = createProcess(factory, "BPMN2-UserTask.bpmn2");
        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(Collections
                .singletonMap("test",
                        "test")));
        processInstance.start();

        JDBCProcessInstances processInstances = (JDBCProcessInstances) process.instances();
        assertThat(processInstances.size()).isOne();
        Optional<?> foundOne = processInstances.findById(processInstance.id());
        BpmnProcessInstance instanceOne = (BpmnProcessInstance) foundOne.get();
        processInstances.update(processInstance.id(), instanceOne);

        assertThat(processInstances.size()).isOne();
        assertThat(processInstances.exists(TEST_ID)).isFalse();
        Optional<?> foundTwo = processInstances.findById(TEST_ID);
        assertThat(foundTwo).isEmpty();

        processInstances.remove(processInstance.id());
        assertThat(processInstances.size()).isZero();
        assertThat(process.instances().values()).isEmpty();
    }

    @Test
    void testException() {
        BpmnProcess process = configure(false);
        JDBCProcessInstances processInstances = (JDBCProcessInstances) process.instances();
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> processInstances.findById(TEST_ID));
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> processInstances.remove(TEST_ID));
    }

    @Test
    public void testUpdate() {
        var factory = new TestProcessInstancesFactory(getDataSource(), true);
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
            fail("Updating process should have failed");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isEqualTo("Process instance with id '" + instanceOne.id() + "' updated or deleted by other request");
        }
        foundOne = processInstances.findById(processInstance.id());
        instanceOne = (BpmnProcessInstance) foundOne.get();
        assertEquals(2L, instanceOne.version());

        processInstances.remove(processInstance.id());
        assertThat(processInstances.size()).isZero();
        assertThat(process.instances().values()).isEmpty();
    }

    @Test
    public void testRemove() {
        var factory = new TestProcessInstancesFactory(getDataSource(), true);
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
            assertThat(e.getMessage()).isEqualTo("Process instance with id: " + instanceOne.id() + " was deleted by other request.");
        }
    }
}
